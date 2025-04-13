import jwt
from fastapi import status
from fastapi.exceptions import HTTPException
from fastapi.params import Depends
from fastapi.security.oauth2 import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from jwt.exceptions import InvalidTokenError
from typing_extensions import Dict

from transcription.config.config import Config, get_config
from transcription.db.users import Users, get_users

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth")


def authenticate_user(users: Users, request: OAuth2PasswordRequestForm) -> bool:
    # Check if the user exists
    valid_password = users.get(request.username)
    if valid_password is None:
        return False
    # Check if the password is correct
    if not request.password == valid_password:
        return False
    return True


def create_access_token(data: Dict, config: Config) -> str:
    to_encode = data.copy()
    encoded_jwt = jwt.encode(to_encode, config.SECRET_KEY, algorithm=config.ALGORITHM)
    return encoded_jwt


async def get_current_user(token: str = Depends(oauth2_scheme), config: Config = Depends(get_config), users: Users = Depends(get_users)) -> str:  # type: ignore
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, config.SECRET_KEY, algorithms=[config.ALGORITHM])
        username = payload.get("sub")
        if username is None:
            raise credentials_exception
    except InvalidTokenError:
        raise credentials_exception
    if username not in users:
        raise credentials_exception
    return username
