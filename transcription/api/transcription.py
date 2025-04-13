from fastapi import Depends
from fastapi.datastructures import UploadFile
from fastapi.routing import APIRouter

from transcription.auth.utils import get_current_user
from transcription.client.transcription import Transcriptor, get_transcriptor

router = APIRouter(prefix="/transcription", dependencies=[Depends(get_current_user)])


@router.post("/")
async def transcript(
    audio_file: UploadFile, transcriptor: Transcriptor = Depends(get_transcriptor)
):
    try:
        contents = await audio_file.read()
        with open(f"received_{audio_file.filename}", "wb") as f:
            f.write(contents)
        transcription = transcriptor(f"received_{audio_file.filename}")
        return {"filename": audio_file.filename, "transcription": transcription}
    except Exception as e:
        return {"error": str(e)}
    finally:
        await audio_file.close()


@router.get("/health")
async def health_check():
    return {"status": "healthy"}
