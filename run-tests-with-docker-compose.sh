#!/bin/bash

# Определяем переменные
COMPOSE_FILE="infra/docker_compose/docker-compose-all.yml"
TEST_IMAGE_NAME="nbugs-tests"
NETWORK_NAME="nbank-network"

# Создаем уникальную директорию для вывода результатов на хосте
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
TEST_OUTPUT_DIR="./test-output/${TIMESTAMP}"
mkdir -p "${TEST_OUTPUT_DIR}/logs"
mkdir -p "${TEST_OUTPUT_DIR}/results"
mkdir -p "${TEST_OUTPUT_DIR}/report"

# Эта команда гарантирует, что окружение будет остановлено при выходе из скрипта (даже при ошибке)
trap 'echo; echo ">>> (4/4) Останавливаем тестовое окружение..."; docker compose -f $COMPOSE_FILE down --remove-orphans' EXIT

# 1. Сборка Docker-образа с тестами
echo ">>> (1/4) Сборка образа с тестами..."
docker build --no-cache -t $TEST_IMAGE_NAME .

# 2. Поднятие тестового окружения
echo ">>> (2/4) Поднимаем тестовое окружение..."
docker compose -f $COMPOSE_FILE up -d --remove-orphans --wait

echo ">>> Окружение готово."
echo ">>> Запускаем тесты..."

# 3. Запуск контейнера с тестами
docker run --rm \
  --network=$NETWORK_NAME \
  -v "${PWD}/${TEST_OUTPUT_DIR}/logs":/app/logs \
  -v "${PWD}/${TEST_OUTPUT_DIR}/results":/app/results \
  -v "${PWD}/${TEST_OUTPUT_DIR}/report":/app/report \
  -e TEST_PROFILE="allTests" \
  -e SERVER="http://backend:4111" \
  -e UIBASEURL="http://nginx:80" \
  -e UIREMOTE="http://selenoid:4444/wd/hub" \
  $TEST_IMAGE_NAME

# Выводим содержимое лог-файла из смонтированного тома
echo "=== Лог выполнения тестов ==="
if [ -f "${TEST_OUTPUT_DIR}/logs/run.log" ]; then
    cat "${TEST_OUTPUT_DIR}/logs/run.log"
else
    echo "Лог-файл не найден."
fi

# Определяем окончательный статус скрипта на основе лога Gradle
FINAL_SCRIPT_EXIT_CODE=1 # По умолчанию считаем, что сборка упала
if grep -q "BUILD SUCCESSFUL" "${TEST_OUTPUT_DIR}/logs/run.log"; then
  echo ">>> Тесты успешно выполнены (согласно логу Gradle)!"
  FINAL_SCRIPT_EXIT_CODE=0
elif grep -q "BUILD FAILED" "${TEST_OUTPUT_DIR}/logs/run.log"; then
  echo ">>> Тесты завершились с ошибками (согласно логу Gradle)."
  FINAL_SCRIPT_EXIT_CODE=1
else
  echo ">>> Не удалось определить статус выполнения тестов из лога."
  FINAL_SCRIPT_EXIT_CODE=1
fi

# Выходим с кодом выхода выполнения тестов
exit $FINAL_SCRIPT_EXIT_CODE
