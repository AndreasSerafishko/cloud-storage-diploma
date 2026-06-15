# Cloud Storage - Дипломный проект

REST-сервис для облачного хранения файлов с авторизацией.

## Технологии
- Java 21, Spring Boot 3.2
- Spring Security, JWT
- PostgreSQL, Hibernate
- Docker, Maven

## Запуск

docker-compose up -d db
mvn spring-boot:run

## API

### Логин
curl -X POST http://localhost:8080/login -H "Content-Type: application/json" -d '{"login":"user","password":"password"}'

### Список файлов
curl -X GET "http://localhost:8080/cloud/list?limit=3" -H "auth-token: YOUR_TOKEN"

### Загрузка файла
curl -X POST "http://localhost:8080/cloud/file" -H "auth-token: YOUR_TOKEN" -F "file=@test.txt"

### Удаление файла
curl -X DELETE "http://localhost:8080/cloud/file?filename=test.txt" -H "auth-token: YOUR_TOKEN"

### Логаут
curl -X POST http://localhost:8080/logout -H "auth-token: YOUR_TOKEN"

## Пользователи
- user / password
- test / test123
