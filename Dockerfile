# Используем официальный образ JDK
FROM openjdk:17

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем артефакт jar-файл внутрь образа
COPY target/serving-web-content-0.0.1-SNAPSHOT.jar app.jar

# Экспортируем порт (если требуется)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java","-jar","/app/app.jar"]