lint:
	black .
build_base:
	docker rmi app_base:latest
	docker build -t app_base -f Dockerfile.base .
build:
	docker rmi app:latest
	docker build -t app .
run:
	docker run -p 80:8000 --rm -d app
stop:
	docker ps --filter "ancestor=app:latest" --format "{{.ID}}" | xargs -r docker stop
debug:
	docker ps --filter "ancestor=app:latest" --format "{{.ID}}" | xargs -r docker logs -f