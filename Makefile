lint:
	black .
build_base:
	docker build -t app_base -f Dockerfile.base .
build:
	docker build -t app .
run:
	docker run -p 80:8000 -d --rm app