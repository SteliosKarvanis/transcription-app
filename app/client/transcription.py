import os
from functools import lru_cache

import torch
from transformers.models.auto.modeling_auto import AutoModelForSpeechSeq2Seq
from transformers.models.auto.processing_auto import AutoProcessor
from transformers.pipelines import pipeline
from transformers.pipelines.base import Pipeline

from app.db.tasks import tasks
from app.models.transcription import Task, TaskStatus


class Transcriptor:
    _MODEL_ID = "openai/whisper-large-v3-turbo"
    _DEVICE = "cpu"

    def __init__(self):
        self._model = self._load_model()

    def _load_model(self) -> Pipeline:
        model = AutoModelForSpeechSeq2Seq.from_pretrained(
            self._MODEL_ID,
            torch_dtype=torch.float32,
            low_cpu_mem_usage=True,
            use_safetensors=True,
        )

        processor = AutoProcessor.from_pretrained(self._MODEL_ID)

        return pipeline(
            "automatic-speech-recognition",
            model=model,
            tokenizer=processor.tokenizer,
            feature_extractor=processor.feature_extractor,
            torch_dtype=torch.float32,
            device=self._DEVICE,
        )

    def __call__(self, task: Task) -> None:
        tasks.process_task(task.task_id)
        # Inference
        transcription = self._model(
            task.local_file_path,
            return_timestamps=True,
            generate_kwargs={"language": "portuguese"},
        )
        output: str = transcription["text"]  # type: ignore
        # Save the transcription to a file
        tasks.finish_task(task.task_id, output)


@lru_cache
def get_transcriptor() -> Transcriptor:
    return Transcriptor()


transcriptor = get_transcriptor()
