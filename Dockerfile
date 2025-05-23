FROM app_base

WORKDIR /code
COPY ./users.json /code/users.json
COPY ./.env /code/.env
COPY ./app /code/app
COPY ./data /code/data
COPY ./transcriptions /code/transcriptions
COPY ./tasks_json /code/tasks_json

CMD ["fastapi", "run", "app/main.py"]