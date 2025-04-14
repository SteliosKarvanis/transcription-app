import os
import uuid
from datetime import datetime
from enum import Enum

from pydantic import BaseModel, Field, root_validator
from typing_extensions import Optional

from app.config.config import config


class TaskStatus(str, Enum):
    CREATED = "CREATED"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"
    QUEUED = "QUEUED"


class Task(BaseModel):
    task_id: str = Field(alias="input_filename")
    user: str
    status: TaskStatus = TaskStatus.CREATED
    start_time: datetime = Field(default_factory=datetime.now)
    end_time: Optional[datetime] = None

    @root_validator(pre=True)
    def set_task_id(cls, values):
        task_id = values.get("task_id")
        if not task_id:
            values["task_id"] = str(uuid.uuid4().hex)
        return values

    @property
    def local_file_path(self) -> str:
        return f"{config.DATA_DIR}/{self.task_id}"

    @property
    def output_file_path(self) -> str:
        return f"{config.TRANSCRIPTIONS_DIR}/{self.task_id.rsplit('.', maxsplit=1)[-1]}.txt"

    def load_content(self, content: bytes) -> None:
        os.makedirs(config.DATA_DIR, exist_ok=True)
        with open(self.local_file_path, "wb") as f:
            f.write(content)

    def save_output(self, output: str) -> None:
        os.makedirs(config.TRANSCRIPTIONS_DIR, exist_ok=True)
        with open(self.output_file_path, "w") as f:
            f.write(output)

    def finish(self, output: str) -> None:
        self.end_time = datetime.now()
        self.status = TaskStatus.COMPLETED
        with open(self.output_file_path, "w") as f:
            f.write(output)

    def refresh(self) -> None:
        """Refresh the task status and check for timeout."""
        end_time = datetime.now()
        time_diff = end_time - self.start_time
        if time_diff.total_seconds() > config.TASK_TIMEOUT:
            self.status = TaskStatus.FAILED
            self.end_time = end_time
