import asyncio

from typing_extensions import Callable, Coroutine, Any
from fastapi import HTTPException
from functools import wraps

import asyncio.timeouts

def timeout(seconds: int):
    """
    Decorator to apply a timeout to an async function.

    Args:
        seconds: The timeout duration in seconds.
    """
    def decorator(func: Callable[..., Coroutine[Any, Any, Any]]):
        @wraps(func)
        async def wrapper(*args, **kwargs):
            try:
                async with asyncio.timeouts.timeout(seconds):
                    return await func(*args, **kwargs)
            except asyncio.TimeoutError:
                raise HTTPException(status_code=408, detail="Request timeout")
        return wrapper
    return decorator