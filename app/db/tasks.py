from functools import lru_cache

from pydantic import BaseModel
from typing_extensions import Dict, List, Optional

from app.models.transcription import Task, TaskPromise


class Tasks(BaseModel):
    _tasks: Dict[str, Task] = {}

    def get_task_by_id(
        self, task_id: str, user: Optional[str] = None
    ) -> Optional[Task]:
        task = self._tasks.get(task_id)
        # Get if the task exists
        if not task:
            return None
        # Check if the user is the owner of the task
        if user and task.user != user:
            return None
        self._tasks[task_id].refresh()
        return self._tasks[task_id]

    def get_tasks_by_user(self, user: str) -> List[TaskPromise]:
        tasks = [task for task in self._tasks.values() if task.user == user]
        for task in tasks:
            task.refresh()
        return sorted([TaskPromise.from_task(task) for task in tasks], key=lambda x: x.last_requested_at, reverse=True)

    def start_task(self, task: Task):
        # Add task to tasks
        self._tasks[task.task_id] = task

    def queue_task(self, task_id: str) -> None:
        self._tasks[task_id].queue()

    def process_task(self, task_id: str) -> None:
        self._tasks[task_id].process()

    def finish_task(self, task_id: str, output: str) -> None:
        self._tasks[task_id].finish(output)

    def update_task_request(self, task_id: str) -> None:
        self._tasks[task_id].update_request()

@lru_cache
def get_tasks() -> Tasks:
    """Get the tasks object."""
    return Tasks()


tasks: Tasks = get_tasks()
