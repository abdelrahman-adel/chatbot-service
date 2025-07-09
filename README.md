# Pidima Chatbot Service

A Spring Boot microservice for an AI-powered documentation assistant.

## Setup
1. **Prerequisites**:
    - Java 21
    - Maven
    - Docker
    - AWS account with RDS (PostgreSQL), ElastiCache (Redis), and Secrets Manager configured
    - LLM API key stored in AWS Secrets Manager
    - Cognito public key or JWKS endpoint for JWT validation

2. **Configuration**:
    - Update `application.yml` with RDS, Redis, and Secrets Manager details.
    - Set environment variables:
      - `RDS_HOSTNAME`
      - `RDS_USERNAME`
      - `RDS_PASSWORD`
      - `REDIS_HOST`
      - `REDIS_PORT`
      - `AWS_REGION`
      - `JWT_SECRET_NAME`
      - `LLM_SECRET_NAME`
      - `LLM_API_URL`

3. **Build**:
   ```bash
   mvn clean package