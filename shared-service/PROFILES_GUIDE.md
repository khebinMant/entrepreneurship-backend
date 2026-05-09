# Spring Profiles Configuration Guide

## 📋 Available Profiles

This application uses Spring Boot profiles to manage different environments and configurations.

### Profile Overview

| Profile | Purpose | Storage | Database | Use When |
|---------|---------|---------|----------|----------|
| `local` | Daily development | Local files | Local PostgreSQL | Developing features |
| `local-cloud` | Cloud testing | Digital Ocean Spaces | Local PostgreSQL | Testing cloud integration |
| `prod` | Production | Digital Ocean Spaces | Remote PostgreSQL | Production deployment |

## 🎯 Profile: `local` (Default for Development)

### Configuration
```yaml
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:postgresql://localhost:5433/postgres
    username: postgres
    password: emprendia2026
  jpa:
    hibernate:
      ddl-auto: update  # Auto-creates tables

storage:
  type: local  # Uses LocalStorageService
  local:
    path: ./uploads
    base-url: http://localhost:8084/api/files

server:
  port: 8084
```

### How to Activate
```bash
# IntelliJ IDEA
Active profiles: local

# Command line
--spring.profiles.active=local

# Gradle
./gradlew bootRun --args='--spring.profiles.active=local'
```

### What Happens
- ✅ LocalStorageService activates
- ✅ Files stored in `./uploads/`
- ✅ Database tables auto-created
- ✅ No cloud credentials needed
- ✅ SQL queries shown in console

### Best For
- 🔨 Daily development
- 🐛 Debugging
- 🧪 Unit testing
- ⚡ Fast iteration

## ☁️ Profile: `local-cloud` (Test Cloud Locally)

### Configuration
```yaml
spring:
  profiles:
    active: local-cloud
  datasource:
    url: jdbc:postgresql://localhost:5433/postgres  # Still local!
    username: postgres
    password: emprendia2026
  jpa:
    hibernate:
      ddl-auto: update

storage:
  type: cloud  # Uses CloudStorageService
  cloud:
    endpoint: ${DO_SPACES_ENDPOINT}
    region: ${DO_SPACES_REGION:us-east-1}
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: ${DO_SPACES_BUCKET_NAME}

server:
  port: 8084
```

### Prerequisites
1. Digital Ocean Spaces account
2. Created bucket
3. API credentials

### Setup
```bash
# PowerShell
$env:DO_SPACES_ENDPOINT="https://nyc3.digitaloceanspaces.com"
$env:DO_SPACES_ACCESS_KEY="your-access-key"
$env:DO_SPACES_SECRET_KEY="your-secret-key"
$env:DO_SPACES_BUCKET_NAME="emprendia-dev"

# Run
./gradlew bootRun --args='--spring.profiles.active=local-cloud'
```

### What Happens
- ✅ CloudStorageService activates
- ✅ Files uploaded to Digital Ocean Spaces
- ✅ Database still local (easy debugging)
- ✅ Tables auto-created
- ✅ SQL queries shown in console

### Best For
- 🧪 Testing cloud storage integration
- 🔍 Verifying uploads work before production
- 🐛 Debugging cloud issues locally
- ✨ Demo with real cloud storage

## 🚀 Profile: `prod` (Production)

### Configuration
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DB_URL}  # Remote database
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # Only validates, doesn't create

storage:
  type: cloud
  cloud:
    endpoint: ${DO_SPACES_ENDPOINT}
    region: ${DO_SPACES_REGION:us-east-1}
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: ${DO_SPACES_BUCKET_NAME}

server:
  port: 8084
```

### Required Environment Variables
```bash
# Database
DB_URL=jdbc:postgresql://prod-host:5432/emprendia
DB_USERNAME=prod_user
DB_PASSWORD=prod_password

# Digital Ocean Spaces
DO_SPACES_ENDPOINT=https://nyc3.digitaloceanspaces.com
DO_SPACES_REGION=us-east-1
DO_SPACES_ACCESS_KEY=prod-access-key
DO_SPACES_SECRET_KEY=prod-secret-key
DO_SPACES_BUCKET_NAME=emprendia-prod

# Keycloak
KEYCLOAK_ISSUER_URI=https://auth.yourcompany.com/realms/emprendia
```

### How to Run
```bash
# On production server
export SPRING_PROFILES_ACTIVE=prod
# ... set other env vars ...
java -jar shared-service.jar
```

### What Happens
- ✅ CloudStorageService activates
- ✅ Files uploaded to production bucket
- ✅ Uses remote database
- ✅ Only validates schema (doesn't create tables)
- ❌ SQL queries hidden
- ✅ Production-ready configuration

### Best For
- 🌐 Production deployment
- 🔒 Secure environment
- 📊 Real user traffic

## 🔄 How Profile Switching Works

### The Flow

```
1. Application Starts
   ↓
2. Reads --spring.profiles.active=XXX
   ↓
3. Loads application.yml section for profile XXX
   ↓
4. Sets configuration values (including storage.type)
   ↓
5. Spring evaluates @ConditionalOnProperty annotations
   ↓
6. Creates appropriate beans
   ↓
7. Injects correct StorageService implementation
```

### Example: Switching from local to local-cloud

**Before (local profile)**:
```
storage.type = "local"
   ↓
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
   ↓
LocalStorageService bean created
   ↓
Files go to ./uploads/
```

**After (local-cloud profile)**:
```
storage.type = "cloud"
   ↓
@ConditionalOnProperty(name = "storage.type", havingValue = "cloud")
   ↓
CloudStorageService bean created
   ↓
Files go to Digital Ocean Spaces
```

**No code changes needed!** ✨

## 🛠️ IntelliJ IDEA Setup

### 1. Create Run Configuration for Each Profile

**Local Profile**:
1. Run → Edit Configurations
2. Add New → Spring Boot
3. Main class: `com.project.emprendia.shared.SharedServiceApplication`
4. Active profiles: `local`
5. Name: "SharedService - Local"

**Local-Cloud Profile**:
1. Add New → Spring Boot
2. Main class: `com.project.emprendia.shared.SharedServiceApplication`
3. Active profiles: `local-cloud`
4. Environment variables:
   ```
   DO_SPACES_ENDPOINT=https://nyc3.digitaloceanspaces.com;
   DO_SPACES_ACCESS_KEY=your-key;
   DO_SPACES_SECRET_KEY=your-secret;
   DO_SPACES_BUCKET_NAME=emprendia-dev
   ```
5. Name: "SharedService - Local-Cloud"

**Production Profile** (optional, for testing):
1. Add New → Spring Boot
2. Main class: `com.project.emprendia.shared.SharedServiceApplication`
3. Active profiles: `prod`
4. Environment variables: (set all prod variables)
5. Name: "SharedService - Prod"

### 2. Switch Between Profiles

Just select the desired configuration from the dropdown and click Run! 🎮

## 🧪 Testing Each Profile

### Test Local Profile
```bash
# 1. Start with local profile
./gradlew bootRun --args='--spring.profiles.active=local'

# 2. Upload a file
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@test.jpg" \
  -F "entityType=USER" \
  -F "entityId=1"

# 3. Verify file exists locally
ls ./uploads/users/1/

# 4. Check logs for:
# "🚀 LocalStorageService initialized"
```

### Test Local-Cloud Profile
```bash
# 1. Set environment variables
$env:DO_SPACES_ACCESS_KEY="your-key"
$env:DO_SPACES_SECRET_KEY="your-secret"
$env:DO_SPACES_BUCKET_NAME="emprendia-dev"

# 2. Start with local-cloud profile
./gradlew bootRun --args='--spring.profiles.active=local-cloud'

# 3. Upload a file
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@test.jpg" \
  -F "entityType=USER" \
  -F "entityId=1"

# 4. Check Digital Ocean Spaces for the file

# 5. Check logs for:
# "✅ Cloud storage (Digital Ocean Spaces) initialized successfully"
```

## 📊 Profile Decision Tree

```
┌─────────────────────────────────────────┐
│  Are you developing a new feature?      │
│                                         │
│  YES → Use 'local' profile              │
│  ✓ Fast, no setup needed                │
└──────────────┬──────────────────────────┘
               │ NO
               ↓
┌─────────────────────────────────────────┐
│  Do you need to test cloud storage?     │
│                                         │
│  YES → Use 'local-cloud' profile        │
│  ✓ Test cloud with local DB             │
└──────────────┬──────────────────────────┘
               │ NO
               ↓
┌─────────────────────────────────────────┐
│  Are you deploying to production?       │
│                                         │
│  YES → Use 'prod' profile               │
│  ✓ Production configuration              │
└─────────────────────────────────────────┘
```

## 🔍 Verifying Active Profile

### Method 1: Check Application Logs
Look for these messages on startup:

**Local profile**:
```
The following 1 profile is active: "local"
🚀 LocalStorageService initialized
📁 Local storage path: C:\...\uploads
```

**Local-cloud profile**:
```
The following 1 profile is active: "local-cloud"
🚀 Initializing CloudStorageService...
✅ Cloud storage (Digital Ocean Spaces) initialized successfully
```

**Prod profile**:
```
The following 1 profile is active: "prod"
🚀 Initializing CloudStorageService...
✅ Cloud storage (Digital Ocean Spaces) initialized successfully
```

### Method 2: Actuator Endpoint
```bash
# If actuator is enabled
curl http://localhost:8084/actuator/env | grep "activeProfiles"
```

## 💡 Best Practices

### ✅ DO
- Use `local` profile for daily development
- Test with `local-cloud` before pushing to main
- Keep credentials in environment variables
- Use different buckets for dev/prod (`emprendia-dev`, `emprendia-prod`)
- Document required environment variables
- Add `.env` to `.gitignore`

### ❌ DON'T
- Don't hardcode credentials in application.yml
- Don't use prod credentials in development
- Don't commit `.env` files
- Don't change storage type without changing profile
- Don't forget to set environment variables for cloud profiles

## 🚨 Common Issues

### Profile not activating
**Symptom**: Wrong storage type loading
**Solution**: Check spelling of profile name (case-sensitive)

### Environment variables not found
**Symptom**: Application fails to start with cloud profile
**Solution**: Verify environment variables are set in your shell

### Tables not created
**Symptom**: "missing table" error with prod profile
**Solution**: Prod uses `validate`, not `update`. Run migrations manually.

## 📚 Further Reading

- See `STORAGE_README.md` for complete storage documentation
- See `STORAGE_QUICK_REFERENCE.md` for quick tips
- See `.env.example` for environment variable template

