# Image Storage Module

## Overview

This module provides a flexible and scalable image storage solution for the Emprendia platform. It supports both **local file storage** (for development) and **cloud storage** with Digital Ocean Spaces (for production).

## Architecture

### Storage Abstraction

The module uses a **Strategy Pattern** with the `StorageService` interface that has two implementations:

1. **LocalStorageService** - Stores files in the local file system (development)
2. **CloudStorageService** - Stores files in Digital Ocean Spaces (production)

The active implementation is determined by the `storage.type` configuration property.

```
┌─────────────────────────┐
│   StorageService        │ (Interface)
│  (Strategy Pattern)     │
└───────────┬─────────────┘
            │
     ┌──────┴──────┐
     │             │
┌────▼────┐  ┌────▼────┐
│ Local   │  │ Cloud   │
│ Storage │  │ Storage │
└─────────┘  └─────────┘
```

### Storage Structure

```
bucket/ or ./uploads/
├── users/
│   └── {userId}/
│       ├── profile.jpg          (Main profile picture)
│       └── gallery/
│           ├── img1.jpg
│           └── img2.jpg
├── entrepreneurships/
│   └── {entrepreneurshipId}/
│       ├── logo.jpg             (Main logo)
│       └── gallery/
│           ├── img1.jpg
│           └── img2.jpg
└── events/
    └── {eventId}/
        ├── cover.jpg            (Main cover image)
        └── gallery/
            ├── img1.jpg
            └── img2.jpg
```

## Configuration

### Local Storage (Development)

```yaml
storage:
  type: local
  local:
    path: ./uploads
    base-url: http://localhost:8084/api/files
```

### Cloud Storage (Production - Digital Ocean Spaces)

```yaml
storage:
  type: cloud
  cloud:
    endpoint: https://nyc3.digitaloceanspaces.com
    region: us-east-1
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: emprendia-bucket
    base-url: https://emprendia-bucket.nyc3.digitaloceanspaces.com
```

### Environment Variables

For production, set these environment variables:

```bash
DO_SPACES_ENDPOINT=https://nyc3.digitaloceanspaces.com
DO_SPACES_REGION=us-east-1
DO_SPACES_ACCESS_KEY=your-access-key
DO_SPACES_SECRET_KEY=your-secret-key
DO_SPACES_BUCKET_NAME=emprendia-bucket
```

## Entity Limits

Maximum number of images per entity:

| Entity Type | Limit |
|------------|-------|
| USER | 5 images |
| ENTREPRENEURSHIP | 10 images |
| EVENT | 20 images |

## File Restrictions

- **Max file size**: 5 MB
- **Allowed formats**: JPEG, JPG, PNG, WebP, GIF
- **Content types**: `image/jpeg`, `image/jpg`, `image/png`, `image/webp`, `image/gif`

## API Endpoints

### Upload Image

```http
POST /api/images/upload
Content-Type: multipart/form-data

Parameters:
- file: MultipartFile (required)
- entityType: USER | ENTREPRENEURSHIP | EVENT (required)
- entityId: Long (required)
- displayOrder: Integer (optional)
- altText: String (optional)
- description: String (optional)
- uploadedByUserId: Long (optional)

Response: ImageUploadResponse
{
  "imageUrl": "http://localhost:8084/api/files/users/1/gallery/20260505_123456_abc123.jpg",
  "fileName": "20260505_123456_abc123.jpg",
  "fileSizeKb": 245,
  "widthPx": 1920,
  "heightPx": 1080,
  "mimeType": "image/jpeg",
  "success": true,
  "message": "Image uploaded successfully"
}
```

### Get Images for Entity

```http
GET /api/images?entityType=USER&entityId=1

Response: List<ImageGalleryResponse>
[
  {
    "imageId": 1,
    "entityType": "USER",
    "entityId": 1,
    "imageUrl": "...",
    "fileName": "...",
    "displayOrder": 1,
    ...
  }
]
```

### Get Image by ID

```http
GET /api/images/{imageId}

Response: ImageGalleryResponse
```

### Update Image Metadata

```http
PUT /api/images/{imageId}
Content-Type: application/json

{
  "altText": "Updated alt text",
  "description": "Updated description",
  "displayOrder": 2
}

Response: ImageGalleryResponse
```

### Delete Image

```http
DELETE /api/images/{imageId}

Response: 204 No Content
```

### Delete All Images for Entity

```http
DELETE /api/images?entityType=USER&entityId=1

Response: "Deleted 3 images"
```

### Reorder Images

```http
POST /api/images/reorder?entityType=USER&entityId=1
Content-Type: application/json

[1, 3, 2, 5, 4]

Response: 200 OK
```

### Count Images

```http
GET /api/images/count?entityType=USER&entityId=1

Response: 3
```

### Check if Can Add More Images

```http
GET /api/images/can-add-more?entityType=USER&entityId=1

Response: true
```

## Usage Examples

### Upload Profile Picture (User)

```bash
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@profile.jpg" \
  -F "entityType=USER" \
  -F "entityId=1" \
  -F "displayOrder=0" \
  -F "altText=User profile picture" \
  -F "uploadedByUserId=1"
```

### Upload to Gallery (Entrepreneurship)

```bash
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@product.jpg" \
  -F "entityType=ENTREPRENEURSHIP" \
  -F "entityId=5" \
  -F "displayOrder=1" \
  -F "altText=Handmade product" \
  -F "description=Our flagship product" \
  -F "uploadedByUserId=1"
```

### Get All Images

```bash
curl "http://localhost:8084/api/images?entityType=ENTREPRENEURSHIP&entityId=5"
```

## Database Schema

### image_gallery Table

```sql
CREATE TABLE image_gallery (
    image_id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    display_order INTEGER,
    alt_text VARCHAR(255),
    description TEXT,
    file_size_kb INTEGER,
    width_px INTEGER,
    height_px INTEGER,
    mime_type VARCHAR(50),
    uploaded_by_user_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_image_gallery_entity ON image_gallery(entity_type, entity_id);
CREATE INDEX idx_image_gallery_order ON image_gallery(entity_type, entity_id, display_order);
```

## Testing

### Local Development

1. Run the application with `local` profile:
   ```bash
   ./gradlew :shared-service:bootRun --args='--spring.profiles.active=local'
   ```

2. Upload files will be stored in `./uploads/` directory

3. Access files via: `http://localhost:8084/api/files/{path}`

### Production with Digital Ocean Spaces

1. Create a Space in Digital Ocean
2. Generate API keys (access key + secret key)
3. Set environment variables
4. Run with `prod` profile:
   ```bash
   export DO_SPACES_ACCESS_KEY=your-key
   export DO_SPACES_SECRET_KEY=your-secret
   ./gradlew :shared-service:bootRun --args='--spring.profiles.active=prod'
   ```

5. Files will be uploaded to Digital Ocean Spaces
6. Access files via: `https://your-bucket.region.digitaloceanspaces.com/{path}`

## Security Considerations

### Digital Ocean Spaces

- Images are uploaded with `PUBLIC_READ` ACL for public access
- You can configure CORS in your Digital Ocean Space settings
- Use CDN if needed for better performance

### Local Storage

- Files are served through Spring MVC controller
- Add authentication/authorization as needed
- Consider using Spring Security to restrict access

## File Naming

Files are automatically renamed with a unique pattern:

```
{timestamp}_{uuid}.{extension}
```

Example: `20260505_123456_a1b2c3d4.jpg`

This prevents naming conflicts and provides chronological ordering.

## Future Enhancements

- [ ] Image compression and optimization
- [ ] Thumbnail generation
- [ ] CDN integration
- [ ] Image transformation (resize, crop)
- [ ] Video support
- [ ] Batch upload
- [ ] Direct upload to cloud (pre-signed URLs)
- [ ] Image moderation/content filtering

## Troubleshooting

### Local Storage Issues

**Problem**: Files not being served
**Solution**: Check that `storage.local.path` directory exists and has write permissions

**Problem**: 404 when accessing files
**Solution**: Verify `storage.local.base-url` matches your server URL

### Cloud Storage Issues

**Problem**: Authentication error
**Solution**: Verify `DO_SPACES_ACCESS_KEY` and `DO_SPACES_SECRET_KEY` are correct

**Problem**: Bucket not found
**Solution**: Ensure bucket exists and `DO_SPACES_BUCKET_NAME` is correct

**Problem**: CORS errors in browser
**Solution**: Configure CORS in Digital Ocean Spaces settings

## References

- [Digital Ocean Spaces Documentation](https://docs.digitalocean.com/products/spaces/)
- [AWS S3 SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
- [Spring Boot File Upload](https://spring.io/guides/gs/uploading-files/)
