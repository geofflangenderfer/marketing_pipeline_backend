#!/usr/bin/env bash  
# initiate docker container
# sudo docker run --name backend -p 5555:5432 -e POSTGRES_USER=backend -e POSTGRES_PASSWORD=backend -e POSTGRES_DB=tokens -d postgres:latest 

# start last-created container
sudo docker start $(sudo docker ps -q -l)

