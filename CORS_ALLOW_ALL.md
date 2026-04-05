# CORS Bypass - Cho Phép Tất Cả Origins

## ✅ Cấu hình mới

### CorsConfig.java
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")           // ✅ TẤT CẢ origins
                .allowedMethods("*")                   // ✅ TẤT CẢ methods
                .allowedHeaders("*")                   // ✅ TẤT CẢ headers
                .allowCredentials(false)               // ✅ Không cần credentials
                .maxAge(3600);
    }
}
```

---

## 🚀 Frontend có thể gọi từ bất kỳ đâu

### React/Vue/Angular (bất kỳ port nào)

```javascript
// ✅ localhost:3000
fetch('http://localhost:8081/api/v1/part-types')

// ✅ localhost:5173 (Vite)
fetch('http://localhost:8081/api/v1/part-types')

// ✅ MacBook 192.168.1.200:3000
fetch('http://192.168.1.100:8081/api/v1/part-types')

// ✅ Production https://myapp.com
fetch('http://192.168.1.100:8081/api/v1/part-types')

// ✅ Bất kỳ domain nào
fetch('[ANY_DOMAIN]/api/v1/part-types')
```

---

## 🔧 Configuration (3 File)

| File | Cấu hình |
|------|---------|
| **CorsConfig.java** | `allowedOriginPatterns("*")` + `allowCredentials(false)` |
| **application.yml** | `allow-credentials: false` |
| **application-dev.yml** | `allow-credentials: false` |

---

## 📋 So sánh Before/After

| Trước | Sau |
|-------|-----|
| `allowedOrigins("...")` Cần list cụ thể | `allowedOriginPatterns("*")` Tất cả |
| `allowCredentials(true)` Cần credentials | `allowCredentials(false)` Không cần |
| Chỉ một số domains gọi được | **Tất cả domains gọi được** ✅ |

---

## ✨ Ưu điểm

✅ **Frontend không bị CORS error**
✅ **Gọi từ localhost:3000, localhost:5173, ... đều OK**
✅ **Gọi từ MacBook, Windows, ... đều OK**
✅ **Thêm frontend mới không cần update CORS**

---

## ⚠️ Nhược điểm (Security)

⚠️ **Không bảo vệ CSRF attacks**
⚠️ **Bất kỳ website nào cũng gọi API được**
⚠️ **Chỉ nên dùng cho:**
- Development environment
- Public APIs (không có sensitive data)
- Mobile apps (không có CORS limitation)

---

## 🎯 Khi nào dùng Production

```yaml
# application.yml (Production)
cors:
  allowed-origins: "https://myapp.com,https://www.myapp.com,https://admin.myapp.com"
  allow-credentials: true  # Nếu cần JWT/Cookies
  max-age: 86400
```

---

## 🚀 Chạy ngay

```bash
# Windows Command Prompt
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Hoặc không cần -dev flag (dùng default application.yml)
mvn spring-boot:run
```

---

## 🧪 Test

### Terminal

```bash
# Bất kỳ port nào cũng OK
curl http://localhost:8081/api/v1/part-types
curl http://192.168.1.100:8081/api/v1/part-types
curl http://10.0.0.5:8081/api/v1/part-types
```

### Browser Console

```javascript
// ✅ Bất kỳ domain nào cũng gọi được
fetch('http://192.168.1.100:8081/api/v1/part-types')
  .then(r => r.json())
  .then(d => console.log(d));
```

### Frontend React/Vue

```javascript
// .env
VITE_API_URL=http://192.168.1.100:8081

// api.js
const API = import.meta.env.VITE_API_URL;

export const getPartTypes = async () => {
  const res = await fetch(`${API}/api/v1/part-types`);
  return res.json();
};
```

---

## 📊 CORS Response Headers

```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD
Access-Control-Allow-Headers: *
Access-Control-Max-Age: 3600
```

---

## ✅ Checklist

- [x] CorsConfig.java - `allowedOriginPatterns("*")`
- [x] CorsConfig.java - `allowCredentials(false)`
- [x] application.yml - `allow-credentials: false`
- [x] application-dev.yml - `allow-credentials: false`
- [ ] Chạy backend: `mvn spring-boot:run`
- [ ] Test: `curl http://localhost:8081/api/v1/part-types`
- [ ] Frontend không cần update CORS origin

---

## 🔗 Liên quan

- `CORS_CONFIG.md` - Hướng dẫn CORS chi tiết
- `MACBOOK_SETUP.md` - Setup MacBook + Windows
- `API_CURL.md` - API curl commands

---

**Version**: 2.0
**Status**: ✅ Allow All Origins
**Last Updated**: 2024
