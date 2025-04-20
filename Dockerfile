# Используем официальный образ JDK
FROM openjdk:17-jre-slim

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем артефакт jar-файл внутрь образа
COPY target/serving-web-content.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java","-jar","/app/app.jar"]