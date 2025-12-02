#!/bin/bash

# Настройка
IMAGE_NAME=nbugs-tests
TEST_PROFILE=${1:-apiTest} # аргумент запуска
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP

# Собираем Docker образ
echo ">>> Сборка тестов запущена"
docker build -t $IMAGE_NAME .

# Создаем директории для выходных данных
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"
mkdir -p "$TEST_OUTPUT_DIR/gradle-reports"

# Запуск Docker контейнера
echo ">>> Тесты запущены с профилем: $TEST_PROFILE"
# Подключаемся к сети nbank-network, созданной Docker Compose
docker run --rm \
  --network=nbank-network \
  -v "$PWD/$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$PWD/$TEST_OUTPUT_DIR/results":/app/results \
  -v "$PWD/$TEST_OUTPUT_DIR/gradle-reports":/app/gradle-reports \
  -v "$PWD/$TEST_OUTPUT_DIR/report":/app/report \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e SERVER="http://192.168.31.155:4111" \
  -e UIBASEURL="http://192.168.31.155:3000" \
  $IMAGE_NAME



# Проверяем статус выполнения тестов
TEST_RESULT=$?

# Вывод итогов
echo ""
echo ">>> Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты тестов (JUnit XML): $TEST_OUTPUT_DIR/results"
echo "Отчеты Gradle: $TEST_OUTPUT_DIR/gradle-reports"
echo "HTML отчет: $TEST_OUTPUT_DIR/report"

# Показываем последние строки лога для быстрого просмотра
echo ""
echo "=== Последние строки лога ==="
tail -20 "$TEST_OUTPUT_DIR/logs/run.log"

# Показываем сводку результатов тестов
echo ""
echo "=== Сводка результатов ==="

# Ищем файлы с результатами тестов
TEST_RESULTS_FILES=$(find "$TEST_OUTPUT_DIR/results/" -name "*.xml" 2>/dev/null | head -5)

if [ -n "$TEST_RESULTS_FILES" ]; then
    for result_file in $TEST_RESULTS_FILES; do
        if [ -f "$result_file" ]; then
            # Извлекаем базовую информацию из XML файла результатов
            tests=$(grep -o 'tests="[0-9]*"' "$result_file" | grep -o '[0-9]*' | head -1 || echo "0")
            failures=$(grep -o 'failures="[0-9]*"' "$result_file" | grep -o '[0-9]*' | head -1 || echo "0")
            errors=$(grep -o 'errors="[0-9]*"' "$result_file" | grep -o '[0-9]*' | head -1 || echo "0")

            echo "Файл: $(basename "$result_file")"
            echo "  Тестов: ${tests:-0}, Ошибок: ${errors:-0}, Падений: ${failures:-0}"
        fi
    done

    # Создаем простой HTML файл со ссылками на отчеты
    HTML_SUMMARY="$TEST_OUTPUT_DIR/summary.html"
    cat > "$HTML_SUMMARY" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Test Results Summary - $TIMESTAMP</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        h1 { color: #333; }
        .success { color: green; }
        .failure { color: red; }
        .section { margin: 20px 0; padding: 15px; background: #f5f5f5; border-radius: 5px; }
        a { color: #0066cc; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>Test Results Summary</h1>
    <p><strong>Timestamp:</strong> $TIMESTAMP</p>
    <p><strong>Profile:</strong> $TEST_PROFILE</p>

    <div class="section">
        <h2>Quick Links</h2>
        <ul>
            <li><a href="logs/run.log">View Full Log</a></li>
            <li><a href="report/index.html">View HTML Report</a></li>
            <li><a href="gradle-reports/tests/test/index.html">View Gradle Test Report</a></li>
        </ul>
    </div>

    <div class="section">
        <h2>Test Results</h2>
        <p>Exit code: $TEST_RESULT (0 = success)</p>
    </div>
</body>
</html>
EOF

    echo ""
    echo "Сводка в HTML: $HTML_SUMMARY"
fi

# Возвращаем код завершения контейнера
exit $TEST_RESULT