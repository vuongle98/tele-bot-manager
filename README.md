# Telegram Bot Manager

A comprehensive Telegram Bot Management system built with Spring Boot, featuring Clean Architecture, dynamic module loading, and AI integration.

## 🚀 Features

### Core Capabilities
- **Clean Architecture**: Strict separation of concerns with domain, application, infrastructure, and presentation layers
- **Dynamic Command Engine**: Runtime command registration and execution with strategy pattern
- **Dynamic Module Loader**: Runtime compilation and execution of custom Java botPlugins
- **AI Integration**: Google Gemini AI for intelligent bot interactions
- **Database-Driven Configuration**: All settings stored in PostgreSQL with versioning
- **REST API**: Comprehensive API with OpenAPI/Swagger documentation
- **Security**: OAuth2 JWT authentication and authorization
- **Caching**: Redis-based caching for improved performance
- **Monitoring**: Actuator endpoints for health checks and metrics

### Bot Management
- Create, update, activate/deactivate bots
- Webhook configuration and management
- Bot status tracking and history
- Multi-bot support with isolation

### Command System
- Dynamic command registration
- Multiple command types: AI, Plugin, Custom, Scheduled
- Command execution tracking and statistics
- Priority-based command handling

### Plugin System
- Runtime Java code compilation
- Plugin lifecycle management (compile, load, unload, reload)
- Plugin execution statistics
- Plugin aliases and metadata

### AI Integration
- Google Gemini AI integration
- AI-powered commands (summarize, generate, analyze, Q&A)
- Configurable AI parameters
- AI service health monitoring

## 🏗️ Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   REST APIs     │  │   Controllers    │  │   DTOs      │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Use Cases     │  │   Services       │  │   Handlers  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Entities      │  │   Repositories   │  │   Services  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Database      │  │   External APIs  │  │   Utils     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

#### Domain Layer
- **Entities**: Bot, Command, Configuration, Plugin, BotHistory, CommandExecution
- **Value Objects**: CommandRequest, CommandResponse
- **Repositories**: BotRepository, CommandRepository, ConfigurationRepository, PluginRepository
- **Services**: CommandHandler, PluginManager, AiService

#### Application Layer
- **Use Cases**: BotManagementUseCase, CommandManagementUseCase, PluginManagementUseCase
- **Services**: BotManagementService, CommandManagementService, PluginManagementService

#### Infrastructure Layer
- **Command Handlers**: AiCommandHandler, PluginCommandHandler, DefaultCommandHandler
- **Services**: GoogleAiService, DynamicPluginManager
- **Utils**: TelegramUtils, PluginUtils
- **Database**: PostgreSQL with Flyway migrations

#### Presentation Layer
- **Controllers**: BotController, CommandController, PluginController
- **DTOs**: Request/Response objects for API endpoints

## 🛠️ Technology Stack

### Backend
- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.2.0**: Framework for building production-ready applications
- **Spring Data JPA**: Data access layer with Hibernate
- **Spring Security**: Authentication and authorization
- **Spring Cache**: Caching abstraction with Redis
- **Spring Web**: REST API development

### Database
- **PostgreSQL**: Primary database for data persistence
- **Redis**: Caching and session storage
- **Flyway**: Database migration management

### AI & External Services
- **Google Cloud AI Platform**: AI capabilities with Gemini
- **Telegram Bot API**: Bot communication
- **Dynamic Module Loader**: Custom library for runtime compilation

### Documentation & Monitoring
- **OpenAPI/Swagger**: API documentation
- **Spring Actuator**: Health checks and metrics
- **Lombok**: Boilerplate code reduction
- **MapStruct**: Object mapping

## 📦 Installation & Setup

### Prerequisites
- Java 21+
- PostgreSQL 13+
- Redis 6+
- Maven 3.8+

### Environment Variables
```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/telebot
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Google AI Configuration
GOOGLE_AI_ENABLED=true
GOOGLE_AI_API_KEY=your_google_ai_api_key
GOOGLE_CLOUD_PROJECT_ID=your_project_id
GOOGLE_CLOUD_REGION=us-central1

# Security Configuration
JWT_ISSUER_URI=http://localhost:8080/realms/applications
JWT_JWK_SET_URI=http://localhost:8080/realms/applications/protocol/openid-connect/certs

# Server Configuration
SERVER_PORT=8082
LOG_LEVEL=INFO
```

### Database Setup
```sql
-- Create database
CREATE DATABASE telebot;

-- Create user (optional)
CREATE USER telebot_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE telebot TO telebot_user;
```

### Running the Application
```bash
# Clone the repository
git clone <repository-url>
cd TelegramBotManager

# Build the application
./gradlew build

# Run the application
./gradlew bootRun

# Or run with Maven
mvn spring-boot:run
```

## 🔧 Configuration

### Application Configuration
The application uses `application.yml` for configuration with environment variable overrides:

```yaml
spring:
  application:
    name: telegram-bot-manager
  
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/telebot}
    username: ${DATABASE_USERNAME:postgres}
    password: ${DATABASE_PASSWORD:password}
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  
  cache:
    type: redis
    redis:
      time-to-live: 600000

app:
  ai:
    google:
      enabled: ${GOOGLE_AI_ENABLED:true}
      api-key: ${GOOGLE_AI_API_KEY:}
      model-name: ${GOOGLE_AI_MODEL:gemini-pro}
      temperature: ${GOOGLE_AI_TEMPERATURE:0.7}
```

### Database Migrations
The application uses Flyway for database migrations. Migration files are located in `src/main/resources/db/migration/`:

- `V1__create_telegram_bots_table.sql`: Bot management tables
- `V2__create_bot_commands_table.sql`: Command definitions
- `V3__create_bot_history_table.sql`: Bot status history
- `V4__create_configurations_table.sql`: Dynamic configuration
- `V5__create_plugins_table.sql`: Plugin management
- `V6__create_command_executions_table.sql`: Command execution tracking

## 📚 API Documentation

### Base URL
```
http://localhost:8082/api/v1
```

### Authentication
All API endpoints require OAuth2 JWT authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Key Endpoints

#### Bot Management
- `POST /bots` - Create a new bot
- `GET /bots` - List all bots
- `GET /bots/{id}` - Get bot by ID
- `PUT /bots/{id}` - Update bot
- `POST /bots/{id}/activate` - Activate bot
- `POST /bots/{id}/deactivate` - Deactivate bot
- `DELETE /bots/{id}` - Delete bot

#### Command Management
- `POST /commands` - Create a new command
- `GET /commands` - List all commands
- `GET /commands/bot/{botId}` - Get commands for a bot
- `POST /commands/execute` - Execute a command
- `PUT /commands/{id}` - Update command
- `POST /commands/{id}/enable` - Enable command
- `POST /commands/{id}/disable` - Disable command

#### Plugin Management
- `POST /botPlugins` - Create a new botPlugin
- `GET /botPlugins` - List all botPlugins
- `POST /botPlugins/{id}/compile` - Compile botPlugin
- `POST /botPlugins/{id}/load` - Load botPlugin
- `POST /botPlugins/{id}/unload` - Unload botPlugin
- `POST /botPlugins/execute` - Execute botPlugin

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8082/swagger-ui.html
```

## 🤖 Bot Usage

### Basic Commands
- `/start` - Start the bot and show welcome message
- `/help` - Show available commands
- `/status` - Check bot status
- `/ping` - Test bot connectivity

### AI Commands
- `/ai <question>` - Ask AI a question
- `/summarize <text>` - Summarize text using AI
- `/generate <prompt>` - Generate content using AI
- `/analyze <text>` - Analyze text using AI

### Plugin Commands
- `/botPlugin <name>` - Execute a botPlugin
- `/custom <command>` - Execute custom command

## 🔌 Plugin Development

### Creating a Plugin
1. Use the API to create a new botPlugin with source code
2. The botPlugin will be compiled and loaded automatically
3. Execute the botPlugin using the botPlugin name

### Plugin Template
```java
package com.vuog.telebotmanager.botPlugins;

import java.util.Map;

public class MyPlugin {
    public String execute(String input, Map<String, Object> parameters) {
        // Your botPlugin logic here
        return "Plugin result: " + input;
    }
}
```

### Plugin Types
- **COMMAND_HANDLER**: Custom command processing
- **AI_PROCESSOR**: AI-powered text processing
- **DATA_TRANSFORMER**: Data transformation logic
- **SCHEDULER**: Scheduled task execution
- **WEBHOOK_HANDLER**: Webhook processing
- **CUSTOM**: Custom functionality

## 📊 Monitoring & Health Checks

### Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

### Health Checks
The application provides health checks for:
- Database connectivity
- Redis connectivity
- AI service availability
- Plugin system status

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/telegram-bot-manager-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Configuration
```yaml
spring:
  profiles:
    active: prod
  
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    com.vuog.telebotmanager: INFO
    org.springframework.security: WARN
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation
- Review the logs for error details

## 🔮 Roadmap

### Planned Features
- [ ] Webhook management UI
- [ ] Real-time bot monitoring dashboard
- [ ] Advanced AI model configuration
- [ ] Plugin marketplace
- [ ] Multi-tenant support
- [ ] Advanced scheduling capabilities
- [ ] Bot analytics and reporting
- [ ] Integration with external services

### Future Enhancements
- [ ] Kubernetes deployment support
- [ ] Advanced security features
- [ ] Performance optimizations
- [ ] Additional AI providers
- [ ] Plugin versioning system
- [ ] Advanced caching strategies