docker login

docker build --no-cache -t saac04/frontend-https -f docker/frontend/Dockerfile .
docker push saac04/frontend-https

docker build --no-cache -t saac04/service1-backend -f docker/service1/Dockerfile .
docker push saac04/service1-backend

docker build --no-cache -t saac04/service2-result -f docker/service2/Dockerfile .
docker push saac04/service2-result

docker build --no-cache -t saac04/service3-events -f docker/service3/Dockerfile .
docker push saac04/service3-events
