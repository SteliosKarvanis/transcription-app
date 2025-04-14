from fastapi import Depends, HTTPException
from fastapi.background import BackgroundTasks
from fastapi.datastructures import UploadFile
from fastapi.routing import APIRouter
from typing_extensions import List

from app.auth.utils import get_current_user
from app.client.transcription import Transcriptor, get_transcriptor
from app.db.tasks import tasks
from app.models.transcription import Task, TaskStatus

router = APIRouter(prefix="/transcription", dependencies=[Depends(get_current_user)])


@router.post("/", response_model=Task)
async def transcript(
    background_tasks: BackgroundTasks,
    audio_file: UploadFile,
    user: str = Depends(get_current_user),
    transcriptor: Transcriptor = Depends(get_transcriptor),
):
    filename = audio_file.filename or ""
    # Create new task
    task = Task(user=user, input_filename=filename)
    tasks.update_task(task)
    # Write content
    task.load_content(await audio_file.read())
    # Execute Async
    background_tasks.add_task(transcriptor, task.task_id)
    task.status = TaskStatus.QUEUED
    tasks.update_task(task)
    return task


@router.get("/task/{task_id}", response_model=Task)
async def get_transcription(
    task_id: str,
    user: str = Depends(get_current_user),
):
    task = tasks.get_task(task_id, user)
    if task is None:
        raise HTTPException(status_code=404, detail="Task not found")
    return task


@router.get("/health")
async def health_check():
    return {"status": "healthy"}


@router.get("/tasks", response_model=List[Task])
async def get_all_tasks(
    user: str = Depends(get_current_user),
):
    return tasks.get_tasks(user)
