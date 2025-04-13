import json
from functools import lru_cache

from typing_extensions import Dict

from app.config.config import config

Users = Dict[str, str]
import os


@lru_cache
def get_users() -> Users:
    """Get the users from the configuration file."""
    with open(config.USERS_FILE, "r") as file:
        return json.load(file)
