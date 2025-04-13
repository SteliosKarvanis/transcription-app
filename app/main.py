from fastapi import FastAPI

from app.api.auth import router as auth_router
from app.api.transcription import router as transcription_router

app = FastAPI()

routers = [
    auth_router,
    transcription_router,
]

for router in routers:
    app.include_router(router)
