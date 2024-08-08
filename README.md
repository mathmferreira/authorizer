# Transaction Authorizer

![Java](https://img.shields.io/badge/java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring_boot_3-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

## About the project 📋

This is an API for authorize credit card transactions. The project was built with the purpose of solve a technical challenge.

#### This API allows you to:

- View the benefits balance of an account
- Authorize transactions

## Built With 🔨

- Java 21
- Spring Boot 3
- Gradle
- Docker Compose
- Lombok
- JUnit 5
- RestAssured
- OpenAPI 3 (Swagger)

## Getting Started 🚀

### ⚙️ Prerequisites

- Java 21
- Gradle
- Docker

You can download Java 21 [here](https://adoptium.net/temurin/releases/).\
Follow the instructions in the official Gradle documentation for installation [here](https://gradle.org/install/).

The project uses **Docker Compose** for containerization of the database. You'll need to install docker compose in your local machine to run the database locally.
You can access the docker documentation for installation and configuration instructions [here](https://docs.docker.com/engine/install/).

### ‍✅️ Rules

#### Example Request

This is an example of request for authorize a transaction. (All the fields are required)

```json
{
  "account": "123",
  "totalAmount": 100.00,
  "mcc": 5411,
  "merchant": "Supermarket XYZ"
}
```

#### Transaction Authorization Rules
- If `mcc` is `"5411"` or `"5412"`, the `FOOD` balance will be used.
- If `mcc` is `"5811"` or `"5812"`, the `MEAL` balance will be used.
- For any other `mcc` values, the `CASH` balance will be used as fallback.
- If the benefit balance has insufficient amount then `CASH` balance will be used as fallback.
- Sometimes, the `mcc` are incorrect and a transaction must be processed taking into account the merchant's data as well. Register merchants using the `import.sql` file located at `src/main/resources/` to make sure that the name will be considered.

#### Default Merchants

```
Merchant Name             Type
-------------------------------
UBER EATS                 MEAL
PADARIA DO ZE             FOOD
RESTAURANTE DA MARIA      MEAL
SUPERMERCADOS BH          FOOD
```
* **Remember**: For more merchants just add them in the insert query on `import.sql`

#### Possible Responses
- `{ "code": "00" }` if the transaction is **approved**
- `{ "code": "51" }` if the transaction is **declined** due to insufficient funds
- `{ "code": "07" }` for any other issue preventing the transaction

### API Docs 📃

Once you have the application running, you can access the documentation of the API in the URL:

```
http://localhost:8080/swagger-ui/index.html
```
