# Guía Rápida de Integración - Sistema de Imágenes

## Para Desarrolladores: Cómo Integrar el Sistema de Imágenes

Esta guía explica cómo usar el sistema de imágenes desde otros microservicios.

---

## 📌 Casos de Uso por Servicio

### 1. USER-SERVICE

#### Agregar foto de perfil

**Flujo:**
1. Usuario sube imagen desde frontend
2. Frontend llama a `POST /api/images/upload` del shared-service
3. Se guardan metadatos en `image_gallery`
4. URL se puede referenciar en `app_user.profile_picture_url` (opcional)

**Ejemplo de código (si llamaras desde backend):**

```java
// En UserService o UserController
@Autowired
private RestTemplate restTemplate;

public String uploadProfilePicture(Long userId, MultipartFile file) {
    // Build multipart request
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", file.getResource());
    body.add("entityType", "USER");
    body.add("entityId", userId.toString());
    body.add("displayOrder", "0");
    body.add("altText", "User profile picture");
    body.add("uploadedByUserId", userId.toString());
    
    HttpEntity<MultiValueMap<String, Object>> requestEntity = 
        new HttpEntity<>(body, headers);
    
    String sharedServiceUrl = "http://localhost:8084/api/images/upload";
    ResponseEntity<ImageUploadResponse> response = restTemplate.postForEntity(
        sharedServiceUrl, 
        requestEntity, 
        ImageUploadResponse.class
    );
    
    return response.getBody().getImageUrl();
}
```

**Recomendación:** Desde el frontend, llamar directamente a la API REST de shared-service.

---

### 2. ENTREPRENEURSHIP-SERVICE

#### Subir logo de emprendimiento

**Opción A: Referencia directa en entidad**

```java
@Entity
@Table(name = "entrepreneurship")
public class Entrepreneurship {
    // ...
    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;  // Guardar URL devuelta por upload
}
```

**Opción B: Referencias separadas**

El modelo actual ya tiene `EntrepreneurshipGallery`, pero con el nuevo sistema, recomendamos:

1. Logo principal → `entrepreneurship.logo_url` (campo directo)
2. Galería de productos → `image_gallery` (entityType=ENTREPRENEURSHIP, displayOrder>0)

#### Subir productos a galería

```bash
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@product1.jpg" \
  -F "entityType=ENTREPRENEURSHIP" \
  -F "entityId=42" \
  -F "displayOrder=1" \
  -F "altText=Producto artesanal" \
  -F "description=Hecho a mano con materiales reciclados"
```

**Obtener galería completa:**

```bash
curl "http://localhost:8084/api/images?entityType=ENTREPRENEURSHIP&entityId=42"
```

**Respuesta:**
```json
[
  {
    "imageId": 15,
    "entityType": "ENTREPRENEURSHIP",
    "entityId": 42,
    "imageUrl": "http://localhost:8084/api/files/entrepreneurships/42/gallery/20260505_134523_abc123.jpg",
    "displayOrder": 1,
    "altText": "Producto artesanal",
    ...
  }
]
```

---

### 3. EVENT-SERVICE

#### Subir cover de evento

Similar a entrepreneurship, usar campo directo en entidad:

```java
@Entity
@Table(name = "event")
public class Event {
    // ...
    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;
}
```

#### Subir imágenes a galería de evento

```bash
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@event_moment.jpg" \
  -F "entityType=EVENT" \
  -F "entityId=8" \
  -F "displayOrder=1" \
  -F "altText=Opening ceremony"
```

---

## 🔗 Integración desde Frontend

### React/Angular/Vue Example

```javascript
async function uploadImage(file, entityType, entityId) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('entityType', entityType);
  formData.append('entityId', entityId);
  formData.append('displayOrder', '1');
  formData.append('altText', file.name);
  
  const response = await fetch('http://localhost:8084/api/images/upload', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  return result.imageUrl;
}

// Uso
const imageUrl = await uploadImage(fileInput.files[0], 'ENTREPRENEURSHIP', 42);
console.log('Image uploaded:', imageUrl);
```

### Obtener galería

```javascript
async function getGallery(entityType, entityId) {
  const response = await fetch(
    `http://localhost:8084/api/images?entityType=${entityType}&entityId=${entityId}`
  );
  const images = await response.json();
  return images;
}

// Uso
const gallery = await getGallery('EVENT', 8);
gallery.forEach(img => {
  console.log('Image:', img.imageUrl, 'Order:', img.displayOrder);
});
```

---

## 🎨 Frontend: Componente de Galería

### React Component Example

```jsx
import React, { useState, useEffect } from 'react';

function ImageGallery({ entityType, entityId }) {
  const [images, setImages] = useState([]);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    loadImages();
  }, [entityType, entityId]);

  async function loadImages() {
    const response = await fetch(
      `http://localhost:8084/api/images?entityType=${entityType}&entityId=${entityId}`
    );
    const data = await response.json();
    setImages(data);
  }

  async function handleUpload(event) {
    const file = event.target.files[0];
    if (!file) return;

    setUploading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('entityType', entityType);
      formData.append('entityId', entityId);
      formData.append('displayOrder', images.length + 1);

      const response = await fetch('http://localhost:8084/api/images/upload', {
        method: 'POST',
        body: formData
      });

      const result = await response.json();
      if (result.success) {
        loadImages(); // Reload gallery
      } else {
        alert('Error: ' + result.message);
      }
    } catch (error) {
      console.error('Upload failed:', error);
      alert('Upload failed');
    } finally {
      setUploading(false);
    }
  }

  async function handleDelete(imageId) {
    if (!confirm('Delete this image?')) return;

    await fetch(`http://localhost:8084/api/images/${imageId}`, {
      method: 'DELETE'
    });
    
    loadImages();
  }

  return (
    <div className="image-gallery">
      <h3>Gallery</h3>
      
      {/* Upload button */}
      <div>
        <input
          type="file"
          accept="image/*"
          onChange={handleUpload}
          disabled={uploading}
        />
        {uploading && <span>Uploading...</span>}
      </div>

      {/* Image grid */}
      <div className="image-grid">
        {images.map(img => (
          <div key={img.imageId} className="image-item">
            <img src={img.imageUrl} alt={img.altText} />
            <p>{img.altText}</p>
            <button onClick={() => handleDelete(img.imageId)}>Delete</button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ImageGallery;
```

**Uso:**
```jsx
<ImageGallery entityType="ENTREPRENEURSHIP" entityId={42} />
```

---

## 🔐 Seguridad y Autenticación

### Agregar JWT Token

Cuando implementes autenticación, las llamadas deben incluir el token:

```javascript
const token = localStorage.getItem('jwt_token');

const response = await fetch('http://localhost:8084/api/images/upload', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});
```

### Validación Backend

En el controller, extraer userId del JWT y validar:

```java
@PostMapping("/upload")
public ResponseEntity<ImageUploadResponse> uploadImage(
        @RequestParam("file") MultipartFile file,
        @RequestParam("entityType") EntityType entityType,
        @RequestParam("entityId") Long entityId,
        @AuthenticationPrincipal Jwt jwt) { // Inyectar JWT
    
    String keycloakId = jwt.getSubject();
    Long userId = userService.findByKeycloakId(keycloakId).getUserId();
    
    // Validar: usuario solo puede subir a sus propias entidades
    if (entityType == EntityType.USER && !entityId.equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Si es ENTREPRENEURSHIP, verificar que el usuario es el dueño
    if (entityType == EntityType.ENTREPRENEURSHIP) {
        Entrepreneurship entrepreneurship = entrepreneurshipService.findById(entityId);
        if (!entrepreneurship.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Continuar con upload...
}
```

---

## 📦 Dependencias Requeridas

Si otros servicios necesitan llamar al shared-service, agregar a `build.gradle`:

```gradle
dependencies {
    // Para llamadas HTTP REST
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Para multipart file handling
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

---

## 🌐 URLs por Ambiente

### Local
- API: `http://localhost:8084/api/images`
- Files: `http://localhost:8084/api/files`

### Dev
- API: `http://shared-service-dev:8084/api/images`
- Files (local): `http://shared-service-dev:8084/api/files`
- Files (cloud): `https://emprendia-bucket-dev.nyc3.digitaloceanspaces.com`

### Prod
- API: `https://api.emprendia.com/shared/images`
- Files: `https://emprendia-bucket.nyc3.cdn.digitaloceanspaces.com`

---

## 📋 Checklist de Integración

Para cada microservicio que use imágenes:

- [ ] Decidir qué imágenes usar (principal vs galería)
- [ ] Agregar campos `*_url` a entidades si necesario
- [ ] Implementar llamadas REST a shared-service desde frontend
- [ ] Agregar validaciones de permisos
- [ ] Manejar errores de upload
- [ ] Mostrar progress bar durante upload
- [ ] Implementar preview de imágenes
- [ ] Agregar validación de formato/tamaño en frontend
- [ ] Implementar delete con confirmación
- [ ] Agregar loading states
- [ ] Considerar lazy loading para galerías grandes

---

## 🐛 Troubleshooting Común

### Error: "File size exceeds maximum allowed"
- **Causa:** Archivo mayor a 5MB
- **Solución:** Comprimir imagen antes de subir

### Error: "Image limit reached"
- **Causa:** Se alcanzó el límite por tipo de entidad
- **Solución:** Eliminar imágenes existentes o contactar admin

### Error: "Invalid file type"
- **Causa:** Formato no permitido
- **Solución:** Usar JPEG, PNG, WebP o GIF

### Error: CORS en navegador
- **Causa:** Frontend en diferente origen
- **Solución:** Configurar CORS en Spring Boot:

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*");
            }
        };
    }
}
```

---

## 📞 Soporte

Para preguntas o issues:
- Email: kguachag@pichincha.com
- Documentación completa: Ver `STORAGE_README.md`
- Resumen implementación: Ver `IMPLEMENTATION_SUMMARY.md`

---

Generado: Mayo 5, 2026
