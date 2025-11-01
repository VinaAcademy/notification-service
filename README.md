# Notification Service 📧

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Java Version](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

## 📋 Overview

The **Notification Service** is a robust microservice designed to handle all notification and email communications within the VinaAcademy ecosystem. It provides comprehensive email delivery capabilities with support for multiple email providers, HTML-based templates, real-time WebSocket notifications, and Kafka-based event processing. This service is built to be scalable, reliable, and easy to integrate with other microservices.

**Key Use Cases:**
- Account verification and welcome emails
- Password reset notifications
- Payment success/failure confirmations
- Real-time notifications via WebSocket

## 🌟 Features

- ✉️ **Multi-Provider Email Support** - Gmail and other SMTP providers
- 🎨 **Thymeleaf HTML Templates** - Professional email templates with layouts and fragments
- 🔄 **Kafka Integration** - Asynchronous event-driven architecture
- 🔴 **Redis Caching** - High-performance data caching layer
- 🔐 **Security** - Integrated security client for authentication & authorization
- 🌐 **WebSocket Support** - Real-time bidirectional communication
- 📊 **PostgreSQL Persistence** - Reliable data storage
- 📚 **OpenAPI/Swagger Documentation** - Auto-generated API docs
- 🏗️ **Spring Cloud Discovery** - Service registration and discovery
- 🛡️ **Rate Limiting** - Daily email limits to prevent abuse

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.x |
| **ORM** | Spring Data JPA | Latest |
| **Database** | PostgreSQL | Latest |
| **Cache** | Redis | Latest |
| **Message Queue** | Kafka | Latest |
| **Templates** | Thymeleaf | Latest |
| **Email** | Spring Mail | Latest |
| **Real-time** | WebSocket | Spring Boot Starter |
| **Service Discovery** | Spring Cloud | 2024.0.1 |
| **API Documentation** | Springdoc OpenAPI | 2.8.5 |
| **Build Tool** | Maven | Latest |

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8.x** or higher
- **Docker** (for containerization)
- **PostgreSQL 12+** (for the database)
- **Redis 6.0+** (for caching)
- **Kafka 3.0+** (for message streaming)

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/VinaAcademy/notification-service.git
cd notification-service
```

### 2. Set Up Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Server Configuration
SERVER_PORT=8080
SPRING_APPLICATION_NAME=notification-service

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vinaacademy_email
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Redis Configuration
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:29092
SPRING_KAFKA_CONSUMER_GROUP_ID=notification-group

# Email Configuration
MAIL_DAILYIMIT=450
MAIL_ONLY_GMAIL=true
MAIL_ACTIVE_PROVIDER=gmail
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
GMAIL_USERNAME=your_email@gmail.com
GMAIL_PASSWORD=your_app_password

# Notifications
NOTIFICATIONS_EMAIL_ENABLED=true
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

#### Option A: Using Maven

```bash
mvn spring-boot:run
```

#### Option B: Using Docker

```bash
# Build the Docker image
docker build -t notification-service:1.0.0 .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/vinaacademy_email \
  -e SPRING_DATA_REDIS_HOST=redis \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
  notification-service:1.0.0
```

### 5. Access the Application

- **API Documentation (Swagger UI):** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health

## 💻 Usage & Examples

### Example 1: Sending a Verification Email

```bash
curl -X POST http://localhost:8080/api/notifications/email/verify \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user@example.com",
    "templateName": "verify-account",
    "variables": {
      "firstName": "John",
      "verificationLink": "https://vinaacademy.com/verify?token=xyz"
    }
  }'
```

### Example 2: Sending a Password Reset Email

```bash
curl -X POST http://localhost:8080/api/notifications/email/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user@example.com",
    "resetLink": "https://vinaacademy.com/reset?token=abc123"
  }'
```

### Example 3: Real-time Notification via WebSocket

```javascript
// Connect to WebSocket
const socket = new WebSocket('ws://localhost:8080/ws/notifications');

// Listen for incoming notifications
socket.onmessage = function(event) {
  console.log('Notification received:', event.data);
};

// Send a notification
socket.send(JSON.stringify({
  type: 'NOTIFICATION',
  message: 'Your payment has been processed successfully'
}));
```

## 📊 Architecture

The service follows a layered architecture:

```
├── Config           (Configuration classes for Kafka, Redis, WebSocket)
├── Controller       (REST API endpoints)
├── Service          (Business logic and processing)
├── Repository       (Data access layer with JPA)
├── Entity           (Database entity models)
├── DTO              (Data transfer objects)
├── Listener         (Event listeners)
├── MQ               (Message queue handlers)
├── Observer         (Observer pattern implementation)
├── Constants        (Application constants and email templates)
└── Interceptor      (HTTP interceptors)
```

## 🔧 Configuration

### Key Configuration Files

- **`application.yml`** - Main application configuration
- **`KafkaTopicConfig.java`** - Kafka topic definitions
- **`RedisConfig.java`** - Redis connection settings
- **`WebSocketConfig.java`** - WebSocket configuration
- **`MailProperties.java`** - Email provider settings

### Email Templates

Email templates are located in `src/main/resources/templates/email/`:

- `welcome.html` - Welcome email template
- `verify-account.html` - Account verification template
- `reset-password.html` - Password reset template
- `payment-success.html` - Payment confirmation template
- `payment-failed.html` - Payment failure template
- `notification.html` - General notification template

## 🧪 Testing

Run the test suite using:

```bash
mvn test
```

To run tests with code coverage:

```bash
mvn test jacoco:report
```

## 🔐 Security

- Service integrates with **Security Client** for authentication and authorization
- API endpoints are protected using Spring Security
- Email credentials are managed securely via environment variables
- Rate limiting is enforced to prevent abuse (default: 450 emails/day)

## 🚢 Deployment

### Prerequisites for Production

- Configure environment variables for production databases
- Set up proper SSL/TLS certificates
- Enable security features in Spring Security configuration
- Configure appropriate log levels

### Docker Compose (for local development)

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: vinaacademy_email
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    ports:
      - "6379:6379"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    ports:
      - "29092:29092"

  notification-service:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
      - kafka
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/vinaacademy_email
      SPRING_DATA_REDIS_HOST: redis
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:29092
```

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Contributors

- **Nguyen Huu Loc** - Primary Developer

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For issues, questions, or suggestions:

- Open an issue on [GitHub Issues](https://github.com/VinaAcademy/notification-service/issues)
- Contact the development team
- Check the [Documentation](./docs)

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/projects/spring-data-jpa)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Redis Documentation](https://redis.io/documentation)

---

**Last Updated:** November 2025  
**Version:** 1.0.0  
**Status:** Active Development
