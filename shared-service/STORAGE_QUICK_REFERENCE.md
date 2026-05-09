# Storage Configuration - Quick Reference

## 🎯 TL;DR

```bash
# Development (use this 90% of the time)
--spring.profiles.active=local
→ Storage: Local folders (./uploads)
→ Database: Local PostgreSQL

# Test cloud storage locally
--spring.profiles.active=local-cloud
→ Storage: Digital Ocean Spaces
→ Database: Local PostgreSQL

# Production
--spring.profiles.active=prod
→ Storage: Digital Ocean Spaces
→ Database: Remote PostgreSQL
```

## 🧠 How It Works

### The Magic Behind Profile Switching

```java
// These annotations determine which storage gets activated
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
@ConditionalOnProperty(name = "storage.type", havingValue = "cloud")
```

### Decision Flow

```
Profile Active → Loads Config → Sets storage.type → Activates Matching Bean
```

**Example**:
```
local → storage.type: local → LocalStorageService ✅
local-cloud → storage.type: cloud → CloudStorageService ✅
prod → storage.type: cloud → CloudStorageService ✅
```

## 📋 Profile Comparison

| Aspect | `local` | `local-cloud` | `prod` |
|--------|---------|---------------|--------|
| **Storage Location** | `./uploads/` | Digital Ocean | Digital Ocean |
| **Storage Type** | `local` | `cloud` | `cloud` |
| **Database** | Local PostgreSQL | Local PostgreSQL | Remote PostgreSQL |
| **DB Host** | localhost:5433 | localhost:5433 | ${DB_URL} |
| **DDL Auto** | `update` | `update` | `validate` |
| **Show SQL** | ✅ Yes | ✅ Yes | ❌ No |
| **Requires DO Credentials** | ❌ No | ✅ Yes | ✅ Yes |
| **When to Use** | Daily dev | Test cloud | Production |

## 🔧 Configuration Files

### Local Profile
```yaml
spring:
  profiles:
    active: local
storage:
  type: local  # ← Key property!
  local:
    path: ./uploads
    base-url: http://localhost:8084/api/files
```

### Local-Cloud Profile
```yaml
spring:
  profiles:
    active: local-cloud
storage:
  type: cloud  # ← Changes to cloud!
  cloud:
    endpoint: ${DO_SPACES_ENDPOINT}
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: ${DO_SPACES_BUCKET_NAME}
```

## 🎮 How to Switch Profiles

### In IntelliJ IDEA
1. Edit Configurations
2. Active profiles: `local` or `local-cloud` or `prod`
3. (Optional) Environment variables: `DO_SPACES_ACCESS_KEY=xxx;...`

### Command Line
```bash
# Gradle
./gradlew bootRun --args='--spring.profiles.active=local'

# JAR
java -jar shared-service.jar --spring.profiles.active=local

# With environment variables (PowerShell)
$env:DO_SPACES_ACCESS_KEY="your-key"
java -jar shared-service.jar --spring.profiles.active=local-cloud
```

## ✅ Verification

### How to know which storage is active?

Check the startup logs:

**Local Storage Active**:
```
🚀 LocalStorageService initialized
📁 Local storage path: C:\...\uploads
🌐 Base URL: http://localhost:8084/api/files
```

**Cloud Storage Active**:
```
🚀 Initializing CloudStorageService...
✅ Cloud storage (Digital Ocean Spaces) initialized successfully
📦 Bucket: emprendia-dev, Endpoint: https://nyc3.digitaloceanspaces.com
```

## 🔑 Environment Variables

### For `local-cloud` and `prod` profiles:

```bash
# Required
DO_SPACES_ENDPOINT=https://nyc3.digitaloceanspaces.com
DO_SPACES_ACCESS_KEY=your-access-key
DO_SPACES_SECRET_KEY=your-secret-key
DO_SPACES_BUCKET_NAME=your-bucket-name

# Optional (has defaults)
DO_SPACES_REGION=us-east-1

# For prod only
DB_URL=jdbc:postgresql://host:5432/database
DB_USERNAME=user
DB_PASSWORD=password
KEYCLOAK_ISSUER_URI=https://auth.example.com/realms/emprendia
```

### Setting Environment Variables

**PowerShell**:
```powershell
$env:DO_SPACES_ACCESS_KEY="your-key"
$env:DO_SPACES_SECRET_KEY="your-secret"
$env:DO_SPACES_BUCKET_NAME="your-bucket"
```

**Bash/Linux**:
```bash
export DO_SPACES_ACCESS_KEY="your-key"
export DO_SPACES_SECRET_KEY="your-secret"
export DO_SPACES_BUCKET_NAME="your-bucket"
```

## 🎓 Important Concepts

### ❌ Common Misconception
> "I need to create a new implementation for each profile"

### ✅ Reality
> "Profiles just change configuration. Spring picks the right implementation automatically based on `storage.type`"

### The Pattern

```
                    StorageService (Interface)
                           │
                ┌──────────┴──────────┐
                │                     │
        LocalStorageService   CloudStorageService
        (storage.type=local)  (storage.type=cloud)
                │                     │
                │                     │
        Activated by:         Activated by:
        - local profile       - local-cloud profile
                              - prod profile
```

### Key Takeaway

**You have:**
- 3 profiles (local, local-cloud, prod)
- 2 implementations (LocalStorageService, CloudStorageService)

**Spring automatically maps them:**
- `local` profile → sets `storage.type=local` → activates `LocalStorageService`
- `local-cloud` profile → sets `storage.type=cloud` → activates `CloudStorageService`
- `prod` profile → sets `storage.type=cloud` → activates `CloudStorageService`

**No new classes needed!** 🎉

## 🚨 Common Errors

### Error: "missing table [image_gallery]"
**Cause**: Using `ddl-auto: validate` but table doesn't exist
**Fix**: Use `local` or `local-cloud` profile (both have `ddl-auto: update`)

### Error: "No qualifying bean of type 'StorageService'"
**Cause**: `storage.type` doesn't match any `@ConditionalOnProperty`
**Fix**: Ensure `storage.type` is either `local` or `cloud`

### Error: "The request signature we calculated does not match..."
**Cause**: Wrong Digital Ocean credentials
**Fix**: 
1. Check environment variables are set
2. Verify no extra spaces in credentials
3. Regenerate API keys if needed

## 🎯 Recommended Workflow

```
┌────────────────────────────────────────┐
│  DAILY DEVELOPMENT                     │
│  Profile: local                        │
│  No setup needed, just run!            │
└──────────────┬─────────────────────────┘
               │
               ↓
┌────────────────────────────────────────┐
│  FEATURE COMPLETE                      │
│  Profile: local-cloud                  │
│  Test cloud storage before deploy      │
└──────────────┬─────────────────────────┘
               │
               ↓
┌────────────────────────────────────────┐
│  DEPLOY TO PRODUCTION                  │
│  Profile: prod                         │
│  Set all env vars on server            │
└────────────────────────────────────────┘
```

## 📝 Testing Uploads

### With Local Storage (profile: local)
```bash
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@test.jpg" \
  -F "entityType=USER" \
  -F "entityId=1"

# Result:
{
  "imageUrl": "http://localhost:8084/api/files/users/1/20260508_143022_abc123.jpg",
  "success": true
}

# File location on disk: ./uploads/users/1/20260508_143022_abc123.jpg
```

### With Cloud Storage (profile: local-cloud or prod)
```bash
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@test.jpg" \
  -F "entityType=USER" \
  -F "entityId=1"

# Result:
{
  "imageUrl": "https://emprendia-dev.nyc3.digitaloceanspaces.com/users/1/20260508_143022_abc123.jpg",
  "success": true
}

# File location: Digital Ocean Spaces bucket
```

## 💡 Pro Tips

1. **Use `local` for 90% of development** - No cloud setup, fast iterations
2. **Switch to `local-cloud` before merging to main** - Verify cloud integration works
3. **Never commit credentials** - Use `.env` files (add to `.gitignore`)
4. **Check logs on startup** - Verify correct storage is activated
5. **Test both storage types** - Ensure your code works with both implementations

## 🔗 Related Files

- **Configuration**: `src/main/resources/application.yml`
- **Local Implementation**: `src/main/java/.../storage/impl/LocalStorageService.java`
- **Cloud Implementation**: `src/main/java/.../storage/impl/CloudStorageService.java`
- **Interface**: `src/main/java/.../storage/StorageService.java`
- **Environment Template**: `.env.example`
- **Full Documentation**: `STORAGE_README.md`

