# Demo1

A Spring Boot application built with **Java 18**, **Spring MVC**, **Spring Security**, **MongoDB**, and **Logback**.  
The project includes authentication, comments with AI moderation, weather-based greeting responses, and property/booking APIs similar to an Airbnb-style platform.

## Features

### Authentication
- User registration
- User login
- JWT-based authentication

### Comments
- Create, read, update, delete comments
- AI-powered sentiment moderation using Claude
- Negative comments are flagged for review
- AI-generated reply for user comments

### Greeting
- Authenticated greeting endpoint
- Uses Open-Meteo public weather API
- Returns a greeting message based on current weather conditions

### Properties
- Users can create, update, delete, and view their own properties
- Property data includes:
  - title
  - description
  - address
  - location
  - price
  - capacity details
  - amenities
- Designed for property ownership and management

### Bookings
- Users can book properties for a date range
- Booking overlap validation
- Guest and owner booking views
- Designed for future booking approval and calendar support

### Logging
- Logback configured for:
  - console logging
  - rolling file logging
- Application logs are stored in `logs/application.log`

---

## Tech Stack

- **Java 18**
- **Spring Boot**
- **Spring MVC**
- **Spring Security**
- **Spring Data MongoDB**
- **Spring Data JPA** (available for future use)
- **Logback**
- **Lombok**
- **JWT**
- **Claude AI API**
- **Open-Meteo API**

---

## Project Structure

- `controllers/` - REST controllers for authentication, comments, greeting, bookings, and properties
- `services/` - business logic
- `dto/` - request/response objects
- `entity/` - MongoDB document models
- `repository/` - database repositories
- `config/` - security and HTTP client configuration

---

## API Overview

### Authentication
- `POST /auth/register`
- `POST /auth/login`

### Greeting
- `GET /greeting`

### Comments
- `POST /comments`
- `GET /comments`
- `GET /comments/{id}`
- `GET /comments/user/{userId}`
- `PUT /comments/{id}`
- `DELETE /comments/{id}`

### Properties
- `POST /properties`
- `GET /properties/{id}`
- `GET /properties/me`
- `PUT /properties/{id}`
- `DELETE /properties/{id}`

### Bookings
- `POST /bookings/property/{propertyId}`
- `GET /bookings/me`
- `GET /bookings/property/{propertyId}`

---

## External Integrations

### Claude AI
Used for:
- comment sentiment analysis
- comment moderation
- AI-generated replies

Configuration example:
properties app.claude.api-key=YOUR_CLAUDE_API_KEY_HERE 
app.claude.base-url=[https://api.anthropic.com](https://api.anthropic.com) 
app.claude.sentiment-model=claude-haiku-4-5 
app.claude.reply-model=claude-sonnet-4-6

### Open-Meteo
Used by the greeting endpoint to generate a weather-based response.

Example endpoint:
text [https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m](https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m)


---

## Configuration

### Application properties
The application uses environment-based configuration where possible.

Common properties:
properties spring.application.name=demo1 spring.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/demo1}
spring.datasource.url={SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/demo1} spring.datasource.username={SPRING_DATASOURCE_USERNAME:demo1} spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:demo1} spring.datasource.driver-class-name=org.postgresql.Driver
app.jwt.secret=change-this-to-a-long-random-secret-key-at-least-32-chars app.jwt.expiration-ms=86400000
app.weather.latitude=52.52 app.weather.longitude=13.41


---

## Logging

Logs are configured in `src/main/resources/logback.xml` to write to:
- console
- rolling log file

Log file location:
text logs/application.log

---

## Running the Project

### Prerequisites
- Java 18
- Maven
- MongoDB running locally or via Docker
- Optional: PostgreSQL if you plan to use JPA features later

### Start the application
bash ./mvnw spring-boot:run

Or on Windows:
bash mvnw.cmd spring-boot:run

---

## Notes

- The project currently uses MongoDB for core persistence.
- Property and booking APIs are designed to support future marketplace-style enhancements.
- AI moderation and external API integrations are isolated in service classes for easier maintenance.

---

## Future Improvements
- property search and filters
- property image upload
- booking cancellation
- booking approval workflow
- payment integration
- review and rating system
- caching for external API calls
- stronger validation and error responses