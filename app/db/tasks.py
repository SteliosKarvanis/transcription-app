from functools import lru_cache

from pydantic import BaseModel
from typing_extensions import Dict, List, Optional

from app.models.transcription import Task, TaskStatus


class Tasks(BaseModel):
    tasks: Dict[str, Task] = {}

    def update_task(self, task: Task) -> None:
        self.tasks[task.task_id] = task

    def get_task(self, task_id: str, user: Optional[str] = None) -> Optional[Task]:
        task = self.tasks.get(task_id)
        print(f"Task {task_id} Found")
        print(self.tasks)
        print(task)
        # Get if the task exists
        if not task:
            return None
        # Check if the user is the owner of the task
        if user and task.user != user:
            return None
        task.refresh()
        self.update_task(task)
        return task

    def get_tasks(self, user: Optional[str] = None) -> List[Task]:
        tasks = [task for task in self.tasks.values() if not user or task.user == user]
        for task in tasks:
            task.refresh()
        return tasks

    def update_task_status(self, task_id: str, new_status: TaskStatus) -> bool:
        task = self.tasks.get(task_id)
        if task:
            task.status = new_status
            self.update_task(task)
        return task is not None

    def finish_task(self, task_id: str, output: str) -> bool:
        task = self.get_task(task_id)
        if task:
            task.finish(output)
            self.update_task(task)
        return task is not None


@lru_cache
def get_tasks() -> Tasks:
    """Get the tasks object."""
    return Tasks()


tasks: Tasks = get_tasks()
