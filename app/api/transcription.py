import os

from fastapi import Depends
from fastapi.datastructures import UploadFile
from fastapi.routing import APIRouter

from app.auth.utils import get_current_user
from app.client.transcription import Transcriptor, get_transcriptor

router = APIRouter(prefix="/transcription", dependencies=[Depends(get_current_user)])


@router.post("/")
async def transcript(
    audio_file: UploadFile, transcriptor: Transcriptor = Depends(get_transcriptor)
):
    try:
        contents = await audio_file.read()
        os.makedirs("input", exist_ok=True)
        local_file_path = f"input/received_{audio_file.filename}"
        with open(local_file_path, "wb") as f:
            f.write(contents)
        print("File Downloaded")
        transcription = transcriptor(local_file_path)
        print("Transcription Done")
        os.remove(local_file_path)
        return {"filename": audio_file.filename, "transcription": transcription}
    except Exception as e:
        return {"error": str(e)}
    finally:
        await audio_file.close()


@router.get("/health")
async def health_check():
    return {"status": "healthy"}
