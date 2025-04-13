from typing_extensions import Dict
from fastapi import Depends, HTTPException, status
from fastapi.routing import APIRouter
from fastapi.security import OAuth2PasswordRequestForm

from transcription.auth.utils import authenticate_user, create_access_token
from transcription.config.config import Config, get_config
from transcription.db.users import Users, get_users

router = APIRouter(prefix="/auth")


@router.post("/")
async def login(
    form_data: OAuth2PasswordRequestForm = Depends(),
    config: Config = Depends(get_config),
    users: Users = Depends(get_users),
) -> Dict[str, str]:
    valid_user = authenticate_user(users, form_data)
    if not valid_user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    token = create_access_token(data={"sub": form_data.username}, config=config)
    return {"access_token": token, "token_type": "bearer"}