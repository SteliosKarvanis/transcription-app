# Description
App to transcribe an audio

# Set Up
### Set Authenticated Users

Add users in `users.json` at project root
> E.g. {"user": "password"}

### Set Secret Key
Generate the key with:
```
openssl rand -hex 32
```
> Add the output to `.env` file at project root:


# Run
```
docker-compose up --build
```
