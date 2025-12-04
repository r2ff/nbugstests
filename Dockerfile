FROM gradle:8.9.0-jdk21

ARG TEST_PROFILE=api
ARG SERVER=http://localhost:4111
ARG UIBASEURL=http://localhost:3000

ENV TEST_PROFILE=${TEST_PROFILE}
ENV SERVER=${SERVER}
ENV UIBASEURL=${UIBASEURL}

WORKDIR /app

# 1. Копируем только те файлы, которые нужны для загрузки зависимостей.
# Они меняются редко, поэтому этот слой будет хорошо кэшироваться.
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

# Change ownership of the gradle wrapper directory (which was copied)
RUN chown -R gradle:gradle /app/gradle

# Даем права на выполнение
RUN chmod +x gradlew

# 2. Копируем исходный код. Этот слой будет пересобираться чаще.
COPY src ./src

# 3. Создаем папку для логов и передаем права пользователю gradle
RUN mkdir -p /app/logs && chown -R gradle:gradle /app/logs

# Grant ownership of the entire /app directory to the gradle user
RUN chown -R gradle:gradle /app

# 4. Переключаемся на пользователя без root-прав
USER gradle

# 5. Запускаем тесты.
CMD bash -c "./gradlew clean ${TEST_PROFILE} -Dhttps.protocols=TLSv1.2 --no-daemon 2>&1 | tee /app/logs/run.log"