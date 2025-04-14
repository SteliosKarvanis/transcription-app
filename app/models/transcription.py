import os
import uuid
from datetime import datetime
from enum import Enum

from pydantic import BaseModel, Field
from typing_extensions import Optional

from app.config.config import config


class TaskStatus(str, Enum):
    CREATED = "CREATED"
    QUEUED = "QUEUED"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"


class Task(BaseModel):
    task_id: str
    user: str
    status: TaskStatus = TaskStatus.CREATED
    created_at: datetime = Field(default_factory=datetime.now)
    started_at: Optional[datetime] = None
    ended_at: Optional[datetime] = None

    @classmethod
    def create_task(cls, user: str, filename: Optional[str], content: bytes) -> "Task":
        task_id = filename or str(uuid.uuid4().hex)
        task = cls(user=user, task_id=task_id)
        if not os.path.exists(task.output_file_path):
            # Write content
            task.save_content(content)
        return task

    @property
    def local_file_path(self) -> str:
        os.makedirs(config.TRANSCRIPTIONS_DIR, exist_ok=True)
        os.makedirs(f"{config.DATA_DIR}/{self.user}", exist_ok=True)
        return f"{config.DATA_DIR}/{self.user}/{self.task_id}"

    @property
    def output_file_path(self) -> str:
        os.makedirs(config.DATA_DIR, exist_ok=True)
        os.makedirs(f"{config.TRANSCRIPTIONS_DIR}/{self.user}", exist_ok=True)
        filename = self.task_id.rsplit("/", maxsplit=1)[-1].split(".", maxsplit=1)[0]
        return f"{config.TRANSCRIPTIONS_DIR}/{self.user}/{filename}.txt"

    @property
    def active(self) -> bool:
        return self.status in {
            TaskStatus.QUEUED,
            TaskStatus.IN_PROGRESS,
            TaskStatus.CREATED,
        }

    def save_content(self, content: bytes) -> None:
        with open(self.local_file_path, "wb") as f:
            f.write(content)

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


class TaskResponse(Task):
    output: Optional[str] = None

    @classmethod
    def from_task(cls, task: Task) -> "TaskResponse":
        output = task.read_output()
        return cls(
            task_id=task.task_id,
            user=task.user,
            status=task.status,
            created_at=task.created_at,
            started_at=task.started_at,
            ended_at=task.ended_at,
            output=output,
        )
