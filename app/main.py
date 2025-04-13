from fastapi import FastAPI

from app.api.auth import router as auth_router
from app.api.transcription import router as transcription_router

app = FastAPI()

routers = [
    auth_router,
    transcription_router,
]


@app.get("/")
async def root():
    return {"message": "Welcome to the FastAPI application!"}


for router in routers:
    app.include_router(router)
