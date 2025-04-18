import os
from functools import lru_cache

from dotenv import load_dotenv
from typing_extensions import Optional

# Load environment variables from .env file.
# By default, it looks for .env in the current directory and its parents.
load_dotenv()


class Config:
    SECRET_KEY: str = os.environ["SECRET_KEY"]
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: Optional[int] = None
    USERS_FILE: str = os.getcwd() + "/users.json"
    DATA_DIR: str = os.getcwd() + "/data"
    TRANSCRIPTIONS_DIR: str = os.getcwd() + "/transcriptions"
    TASKS_DIR: str = os.getcwd() + "/tasks_json"
    TASK_TIMEOUT: int = 5 * 60  # 5 minutes


@lru_cache
def get_config() -> Config:
    """Get the configuration object."""
    return Config()


config: Config = get_config()
