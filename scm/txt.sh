chmod +x mvnw

echo "STEP 1 : Build Spring Boot Project"
./mvnw clean install -DskipTests

echo "STEP 2 : Run Tests"
./mvnw test

echo "STEP 3 : SonarQube Analysis"
./mvnw clean verify sonar:sonar \
  -Dsonar.token=$SONAR_TOKEN \
  -Dsonar.organization=reshmamahadik0 \
  -Dsonar.projectKey=ReshmaMahadik0_github-action \
  -Dsonar.host.url=https://sonarcloud.io

echo "STEP 4 : Build Docker Image"
IMAGE_TAG=latest
docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG .


echo "STEP 5 : Push Image to ECR"
docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG


echo "STEP 6 : Deploy to EC2"

echo "$EC2_SSH_KEY" > key.pem
chmod 400 key.pem

#echo "EC2 HOST = $EC2_HOST"

ssh -o StrictHostKeyChecking=no -i key.pem ec2-user@$EC2_HOST << EOF

aws ecr get-login-password --region $AWS_REGION \
| docker login --username AWS --password-stdin $ECR_REGISTRY

#pull the latest image
docker pull $ECR_REGISTRY/$ECR_REPOSITORY:latest

#Stop old container(if running)
docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true

#Run new Container
docker run -d \
-p 80:8080 \
--name $CONTAINER_NAME \
$ECR_REGISTRY/$ECR_REPOSITORY:latest

EOF

echo "Deployment Completed"
