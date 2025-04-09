# Spring Boot Bankito

A modern banking system with blockchain integration built on Spring Boot 3.4.0.

## Overview

Spring Boot Bankito is a comprehensive banking application that combines traditional banking features with blockchain technology. It provides a secure, scalable platform for managing accounts, transactions, and financial operations with the added transparency and security of blockchain integration.

## Key Features

- **Complete Banking System**: Customer management, accounts, transfers, transaction tracking
- **Double-Entry Accounting**: Ensures financial integrity through proper accounting principles
- **Blockchain Integration**: Optional layer for enhanced security and transparency
  - Supports Mock Blockchain (for development)
  - Cosmos SDK Testnet integration
- **Security**: JWT-based authentication and authorization
- **API-First Design**: OpenAPI specification with generated interfaces
- **Idempotency**: Prevents accidental duplicate operations
- **Comprehensive Audit Trail**: Records all financial operations

## Technologies

- **Spring Boot 3.4.0**
- **Spring Data JPA**: Database interaction
- **Spring Security + JWT**: Authentication and authorization
- **PostgreSQL**: Primary database
- **Cosmos SDK**: Blockchain integration
- **Mapstruct**: Object mapping
- **Lombok**: Reduces boilerplate code
- **Swagger/OpenAPI**: API documentation and client generation
- **Liquibase**: Database schema management

## Architecture

The system follows a layered architecture with specialized services:

- **Controllers**: Handle HTTP requests based on OpenAPI specification
- **Services**: Encapsulate business logic for accounts, customers, transfers, etc.
- **Repositories**: Database interaction via Spring Data JPA
- **Blockchain Integration**: Modular design supporting multiple blockchain implementations
- **Security**: JWT-based authentication with Spring Security

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL
- Docker (optional, for containerized deployment)
- Cosmos SDK (optional, for blockchain integration)

### Running the Application

1. **Database Setup**:
   ```bash
   docker compose up -d
   ```

2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   java -jar target/spring-boot-boilerplate.jar
   ```

4. **Access the API**:
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Base URL: http://localhost:8080/api/v1

### Configuration

- **Database**: Configure connection details in `application.yml`
- **JWT**: Customize token settings in `application.yml`
- **Blockchain**: Configure blockchain settings in `blockchain.properties`
- **Security**: Customize endpoint access in `SecurityConfiguration.java`

## API Documentation

The API is fully documented using OpenAPI/Swagger. Access the Swagger UI at http://localhost:8080/swagger-ui.html when the application is running.

## Blockchain Integration

The system integrates blockchain technology as an optional layer for recording financial operations:

- **Dual-Write Approach**: Operations are recorded both on the blockchain and in the traditional database
- **Modular Design**: Supports multiple blockchain implementations (currently Mock and Cosmos SDK)
- **Command Pattern**: Standardized commands for financial operations (Deposit, Withdraw, Transfer)

## Future Enhancements

- Complete Cosmos SDK transaction submission
- Event-driven architecture
- CQRS implementation
- Advanced monitoring and alerting
- Disaster recovery strategy

## License

Apache License 2.0
