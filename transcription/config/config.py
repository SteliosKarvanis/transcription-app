import os
from functools import lru_cache

from typing_extensions import Optional
from dotenv import load_dotenv

# Load environment variables from .env file.
# By default, it looks for .env in the current directory and its parents.
load_dotenv()


class Config:
    SECRET_KEY: str = os.environ["SECRET_KEY"]
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: Optional[int] = None
    USERS_FILE: str = os.getcwd() + "/users.json"


@lru_cache
def get_config() -> Config:
    """Get the configuration object."""
    return Config()


config: Config = get_config()
