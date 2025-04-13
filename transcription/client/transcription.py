from functools import lru_cache
import torch
from transformers.models.auto.modeling_auto import AutoModelForSpeechSeq2Seq
from transformers.models.auto.processing_auto import AutoProcessor
from transformers.pipelines import pipeline
from transformers.pipelines.base import Pipeline


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
        model.to(self._DEVICE)

        processor = AutoProcessor.from_pretrained(self._MODEL_ID)

        return pipeline(
            "automatic-speech-recognition",
            model=model,
            tokenizer=processor.tokenizer,
            feature_extractor=processor.feature_extractor,
            torch_dtype=torch.float32,
            device=self._DEVICE,
        )

    def __call__(self, audio_file) -> str:
        transcription = self._model(
            audio_file,
            return_timestamps=True,
            generate_kwargs={"language": "portuguese"},
        )
        return transcription["text"]  # type: ignore


@lru_cache
def get_transcriptor() -> Transcriptor:
    return Transcriptor()
