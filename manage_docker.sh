#!/usr/bin/env bash  
# initiate docker container
# sudo docker run --name backend -p 5555:5432 -e POSTGRES_USER=backend -e POSTGRES_PASSWORD=backend -e POSTGRES_DB=tokens -d postgres:latest 

# start last-created container
# sudo docker start $(sudo docker ps -q -l)

# build docker image for deployment
# sudo docker build . -t marketing_pipeline_backend

# run built image. For example, on a server
# sudo docker run -p 8080:8080 -t marketing_pipeline_backend

# run with docker-compose.yml settings
sudo docker-compose up

# shutdown everything docker-compose up started
# sudo docker-compose down

# if there's a container conflict, remove the old container
# docker rm backend --force

# if you want to build a specific service from *.yml
# sudo docker-compose build <service_name>

# check that docker successfully built your container
# maybe $DB_HOST:8080?
# curl -i localhost:8080

# docker exposes 2 ports. The first is what the outside world talks to? Second what internal services talk to?

# to run tests within intellij, you must 'Edit Configurations..' and set the following environment variables:
# DB_HOST -> localhost (different)
# DB_USER -> backend (same as docker-compose.yml)
# DB_PORT -> 5555 (same as docker-compose.yml 1st db port)
# DB_NAME -> tokens (same as docker-compose.yml)
# DB_PASSWORD -> backend (same as docker-compose.yml)
