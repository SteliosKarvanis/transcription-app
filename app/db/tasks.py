import os
from functools import lru_cache

from pydantic import BaseModel, Field
from typing_extensions import Dict, List, Optional

from app.models.transcription import Task, TaskPromise
from app.config.config import config

class Tasks(BaseModel):
    tasks: Dict[str, Task] = Field(default_factory=dict)

    @classmethod
    def from_files(cls):
        """Load tasks from files."""
        tasks = cls()
        # Load tasks from files
        for user in os.listdir(config.TASKS_DIR):
            folder = os.path.join(config.TASKS_DIR, user)
            for f in os.listdir(folder):
                if f.endswith(".json"):
                    task_path = os.path.join(folder, f)
                    # Load task from file
                    task = Task.parse_file(task_path)
                    # Add task to tasks
                    tasks.tasks[task.task_id] = task
        return tasks

    def get_task_by_id(
        self, task_id: str, user: Optional[str] = None
    ) -> Optional[Task]:
        task = self.tasks.get(task_id)
        # Get if the task exists
        if not task:
            return None
        # Check if the user is the owner of the task
        if user and task.user != user:
            return None
        self.tasks[task_id].refresh()
        return self.tasks[task_id]

    def get_tasks_by_user(self, user: str) -> List[TaskPromise]:
        tasks = [task for task in self.tasks.values() if task.user == user]
        for task in tasks:
            task.refresh()
        return sorted(
            [TaskPromise.from_task(task) for task in tasks],
            key=lambda x: x.last_requested_at,
            reverse=True,
        )

    def start_task(self, task: Task):
        # Add task to tasks
        self.tasks[task.task_id] = task

    def queue_task(self, task_id: str) -> None:
        self.tasks[task_id].queue()

    def process_task(self, task_id: str) -> None:
        self.tasks[task_id].process()

    def finish_task(self, task_id: str, output: str) -> None:
        self.tasks[task_id].finish(output)

    def update_task_request(self, task_id: str) -> None:
        self.tasks[task_id].update_request()


@lru_cache
def get_tasks() -> Tasks:
    """Get the tasks object."""
    return Tasks.from_files()


tasks: Tasks = get_tasks()
