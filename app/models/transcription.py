import os
from datetime import datetime
from enum import Enum

from pydantic import BaseModel, Field
from typing_extensions import Optional
import uuid
from app.config.config import config
import subprocess

class TaskStatus(str, Enum):
    CREATED = "CREATED"
    QUEUED = "QUEUED"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"


class Task(BaseModel):
    task_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    sender_file_path: str
    user: str
    status: TaskStatus = TaskStatus.CREATED
    last_requested_at: datetime = Field(default_factory=datetime.now)
    created_at: datetime = Field(default_factory=datetime.now)
    started_at: Optional[datetime] = None
    ended_at: Optional[datetime] = None

    @classmethod
    def create_task(cls, user: str, sender_file_path: str, content: bytes) -> "Task":
        task_id = sender_file_path.split("/")[-1].split(".")[0]
        task = cls(user=user, sender_file_path=sender_file_path, task_id=task_id)
        if not os.path.exists(task.output_file_path):
            # Write content
            task.save_content(content)
        return task

    @property
    def local_video_file_path(self) -> str:
        os.makedirs(config.TRANSCRIPTIONS_DIR, exist_ok=True)
        os.makedirs(f"{config.DATA_DIR}/{self.user}", exist_ok=True)
        return f"{config.DATA_DIR}/{self.user}/{self.task_id}.mp4"

    @property
    def local_audio_file_path(self) -> str:
        os.makedirs(config.TRANSCRIPTIONS_DIR, exist_ok=True)
        os.makedirs(f"{config.DATA_DIR}/{self.user}", exist_ok=True)
        return f"{config.DATA_DIR}/{self.user}/{self.task_id}.mp3"

    @property
    def output_file_path(self) -> str:
        os.makedirs(config.DATA_DIR, exist_ok=True)
        os.makedirs(f"{config.TRANSCRIPTIONS_DIR}/{self.user}", exist_ok=True)

        return f"{config.TRANSCRIPTIONS_DIR}/{self.user}/{self.task_id}.txt"

    @property
    def active(self) -> bool:
        return self.status in {
            TaskStatus.QUEUED,
            TaskStatus.IN_PROGRESS,
            TaskStatus.CREATED,
        }

    def save_content(self, content: bytes) -> None:
        with open(self.local_video_file_path, "wb") as f:
            f.write(content)
        print("Video Saved")
        process = subprocess.Popen(['ffmpeg', '-y', '-i', self.local_video_file_path, '-q:a', '0', '-map', 'a', self.local_audio_file_path])
        process.wait()
        print("Audio Saved")
        os.remove(self.local_video_file_path)
        print("Video Removed")

    def save_output(self, output: str) -> None:
        with open(self.output_file_path, "w") as f:
            f.write(output)

    def read_output(self) -> Optional[str]:
        if os.path.exists(self.output_file_path):
            with open(self.output_file_path, "r") as f:
                return f.read()
        return None

    def queue(self) -> None:
        if self.status == TaskStatus.CREATED:
            self.status = TaskStatus.QUEUED

    def process(self) -> None:
        self.status = TaskStatus.IN_PROGRESS
        self.started_at = datetime.now()

    def finish(self, output: str) -> None:
        self.ended_at = datetime.now()
        self.status = TaskStatus.COMPLETED
        with open(self.output_file_path, "w") as f:
            f.write(output)

    def refresh(self) -> None:
        """Refresh the task status and check for timeout."""
        end_time = datetime.now()
        time_diff = end_time - self.created_at
        if self.active and time_diff.total_seconds() > config.TASK_TIMEOUT:
            self.status = TaskStatus.FAILED
            self.ended_at = end_time

    def update_request(self) -> None:
        self.last_requested_at = datetime.now()

class TaskResult(BaseModel):
    task_id: str
    sender_file_path: str
    output: Optional[str] = None

    @classmethod
    def from_task(cls, task: Task) -> "TaskResult":
        output = task.read_output()
        return cls(
            task_id=task.task_id,
            sender_file_path=task.sender_file_path,
            output=output,  # Include the output attribute
        )

class TaskPromise(BaseModel):
    task_id: str
    sender_file_path: str
    status: TaskStatus
    last_requested_at: datetime

    @classmethod
    def from_task(cls, task: Task) -> "TaskPromise":
        return cls(
            task_id=task.task_id,
            sender_file_path=task.sender_file_path,
            status=task.status,
            last_requested_at=task.last_requested_at,
        )