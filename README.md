# Bank Cards Management REST API

## 🇷🇺 Русская версия | 🇬🇧 English Version

---

# 🇷🇺 Русская версия

Тестовый проект: система управления банковскими картами с JWT-авторизацией, шифрованием номеров карт и ролевым доступом (ADMIN / USER).

## 🔧 Технологический стек

* Java 21
* Spring Boot 3 (Web, Security, Data JPA, Validation)
* PostgreSQL
* Liquibase
* JWT (jjwt)
* Docker / Docker Compose
* Springdoc OpenAPI (Swagger UI)
* JUnit 5, Mockito

---

## 🚀 Как запустить проект

### 1. Запуск PostgreSQL через Docker Compose

```bash
docker-compose up -d
```

PostgreSQL:

* host: `localhost`
* port: `5432`
* db: `bankdb`
* user: `bankuser`
* pass: `bankpass`

### 2. Запуск приложения

```bash
mvn clean package
java -jar target/bank-rest.jar
```

или запустить класс:
`com.example.bankcards.BankCardsApplication`

➡ Приложение: `http://localhost:8080`

При старте создаётся админ:

* username: **admin@test**
* password: **password**

---

## 🔐 Аутентификация и Авторизация

Тип: **JWT (Bearer Token)**

### Логин:

`POST /api/auth/login`

```json
{
  "username": "admin@test",
  "password": "password"
}
```

Ответ содержит токен:

```http
Authorization: Bearer <token>
```

---

## 📌 Основные эндпоинты

### 👑 ADMIN (`/api/admin/**`)

* Создание карты
* Изменение статуса
* Удаление карты
* Получение всех карт
* История всех транзакций

### 👤 USER (`/api/user/**`)

* Список своих карт
* Переводы между картами
* Запрос блокировки карты
* История транзакций

---

## 🔒 Безопасность карт

* Номера хранятся **зашифрованными (AES/GCM)**
* Всегда маскируются:

```
**** **** **** 1234
```

---

## 📘 Swagger UI

* `http://localhost:8080/swagger-ui/index.html`
* `http://localhost:8080/v3/api-docs`

---

## 🧪 Тесты

* Успешный перевод
* Недостаточно средств

```bash
mvn test
```

---

# 🇬🇧 English Version

Test project: banking card management system with JWT authentication, encrypted card numbers, and role-based access (ADMIN / USER).

## 🔧 Tech Stack

* Java 21
* Spring Boot 3
* PostgreSQL
* Liquibase
* JWT
* Docker / Docker Compose
* Springdoc OpenAPI
* JUnit 5, Mockito

---

## 🚀 How to Run

### 1. Start PostgreSQL via Docker Compose

```bash
docker-compose up -d
```

Connection:

* host: `localhost`
* port: `5432`
* db: `bankdb`
* user: `bankuser`
* pass: `bankpass`

### 2. Run Application

```bash
mvn clean package
java -jar target/bank-rest.jar
```

Or run:
`com.example.bankcards.BankCardsApplication`

➡ App runs at: `http://localhost:8080`

Default admin user:

* username: **admin@test**
* password: **password**

---

## 🔐 Authentication & Authorization

Type: **JWT Bearer Token**

### Login:

`POST /api/auth/login`

```json
{
  "username": "admin@test",
  "password": "password"
}
```

Use token:

```http
Authorization: Bearer <token>
```

---

## 📌 Main Endpoints

### 👑 ADMIN (`/api/admin/**`)

* Create card
* Update card status
* Delete card
* Get all cards
* Full transaction history

### 👤 USER (`/api/user/**`)

* List own cards
* Transfer between cards
* Request card block
* Personal transaction history

---

## 🔒 Card Security

* Card numbers stored **encrypted (AES/GCM)**
* Always masked:

```
**** **** **** 1234
```

---

## 📘 Swagger UI

* `http://localhost:8080/swagger-ui/index.html`
* `http://localhost:8080/v3/api-docs`

---

## 🧪 Tests

* Successful transfer
* Insufficient funds

```bash
mvn test
```

---

## Notes

* Temporary README.*.md files removed from final build.
* Project focuses on correctness, security, and clean architecture for a test assignment.
