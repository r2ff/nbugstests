#!/bin/bash

# Немедленно выходить, если команда завершается с ошибкой
set -e

# Настройки
IMAGE_NAME=nbugs-tests
TAG=latest

# Проверка наличия переменных окружения
if [ -z "$DOCKERHUB_USERNAME" ]; then
  echo "Ошибка: Переменная окружения DOCKERHUB_USERNAME не установлена."
  exit 1
fi

if [ -z "$DOCKERHUB_TOKEN" ]; then
  echo "Ошибка: Переменная окружения DOCKERHUB_TOKEN не установлена."
  exit 1
fi

# Логин в Docker Hub с токеном
echo ">>> Логин в Docker Hub..."
echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin

# Тегирование образа
echo ">>> Тегирование образа..."
docker tag "$IMAGE_NAME" "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

# Отправка образа в Docker Hub
echo ">>> Отправка образа в Docker Hub..."
docker push "$DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"

echo ">>> Готово! Образ доступен как: docker pull $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"