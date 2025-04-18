from fastapi import Depends, HTTPException
from fastapi.background import BackgroundTasks
from fastapi.datastructures import UploadFile
from fastapi.routing import APIRouter
from typing_extensions import List

from app.auth.utils import get_current_user
from app.client.transcription import Transcriptor, get_transcriptor
from app.db.tasks import tasks
from app.models.transcription import Task, TaskPromise, TaskResult

router = APIRouter(prefix="/transcription", dependencies=[Depends(get_current_user)])


@router.post("/", response_model=TaskPromise)
async def transcript(
    background_tasks: BackgroundTasks,
    file: UploadFile,
    user: str = Depends(get_current_user),
    transcriptor: Transcriptor = Depends(get_transcriptor),
):
    new_task = Task.create_task(
        user=user, sender_file_path=file.filename, content=await file.read()  # type: ignore
    )
    # Check if the task already exists
    existent_task = tasks.get_task_by_id(new_task.task_id, user)
    if existent_task is not None:
        tasks.update_task_request(existent_task.task_id)
        return TaskPromise.from_task(existent_task)
    # Start task
    tasks.start_task(new_task)
    # Execute Async
    background_tasks.add_task(transcriptor, new_task)
    tasks.queue_task(new_task.task_id)
    return TaskPromise.from_task(new_task)


@router.get("/task/{task_id}", response_model=TaskResult)
async def get_transcription(
    task_id: str,
    user: str = Depends(get_current_user),
):
    task = tasks.get_task_by_id(task_id, user)
    if task is None:
        raise HTTPException(status_code=404, detail="Task not found")
    return TaskResult.from_task(task)


@router.get("/health")
async def health_check():
    return {"status": "healthy"}


@router.get("/tasks", response_model=List[Task])
async def get_all_tasks(
    user: str = Depends(get_current_user),
):
    return tasks.get_tasks_by_user(user)
