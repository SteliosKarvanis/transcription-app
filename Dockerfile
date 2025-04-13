FROM python:3.9

WORKDIR /code
COPY ./requirements.txt /code/requirements.txt
COPY ./users.json /code/users.json
COPY ./.env /code/.env
COPY ./app /code/app

RUN pip install -r /code/requirements.txt
RUN apt update && apt install ffmpeg -y

CMD ["fastapi", "run", "app/main.py"]