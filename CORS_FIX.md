# Sửa lỗi CORS: "allowCredentials cannot be true with wildcard origins"

## 🔴 Vấn đề

Bạn gặp lỗi:
```
When allowCredentials is true, allowedOrigins cannot be '*'
```

**Nguyên nhân:** Spring Boot không cho phép dùng:
- ✅ `allowCredentials(true)` + `allowedOrigins("*")` = **LỖI**

---

## ✅ Giải pháp (Đã cập nhật)

### 3 File được sửa:

#### 1. **CorsConfig.java** ✅ FIXED

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
    private String allowedOrigins;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");

        registry.addMapping("/api/**")
                .allowedOrigins(origins)  // ✅ Specific origins, NOT "*"
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(allowCredentials)  // ✅ true = OK
                .maxAge(maxAge);
    }
}
```

#### 2. **application-dev.yml** ✅ UPDATED

```yaml
# CORS Configuration - For Development
cors:
  allowed-origins: "http://localhost:3000,http://127.0.0.1:3000,http://192.168.1.200:3000"
  allow-credentials: true
  max-age: 3600

server:
  port: 8081
  address: 0.0.0.0
```

#### 3. **application.yml** ✅ UPDATED

```yaml
# CORS Configuration - Default for Production
cors:
  allowed-origins: "https://myapp.com,https://www.myapp.com"
  allow-credentials: true
  max-age: 86400
```

---

## 🚀 Để Chạy Ngay

### Bước 1: Xác định IP của MacBook

```bash
# MacBook Terminal
ipconfig getifaddr en0
# Output: 192.168.1.200
```

### Bước 2: Cập nhật application-dev.yml

```yaml
cors:
  allowed-origins: "http://localhost:3000,http://127.0.0.1:3000,http://192.168.1.200:3000"
  # Thay 192.168.1.200 với IP thực tế của MacBook
```

### Bước 3: Chạy Backend (Dev mode)

```bash
# Windows Command Prompt
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Bước 4: Test từ MacBook Terminal

```bash
curl http://192.168.1.100:8081/api/v1/part-types
# Response: [] ✅ OK
```

### Bước 5: Frontend .env

```
REACT_APP_API_URL=http://192.168.1.100:8081
```

---

## 📊 Tóm tắt thay đổi

| Trước | Sau |
|-------|-----|
| `allowedOrigins("*")` | `allowedOrigins(origins)` |
| `allowCredentials(true)` | ✅ Cùng |
| Hardcoded | Dùng `@Value` từ .yml |
| Không support dev/prod khác | ✅ application-dev.yml + application.yml |

---

## 🔧 Các Scenarios khác nhau

### Scenario 1: Development (localhost + Network)

File: `application-dev.yml`

```yaml
cors:
  allowed-origins: "http://localhost:3000,http://127.0.0.1:3000,http://192.168.1.200:3000"
  allow-credentials: true
  max-age: 3600
```

**Run:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Scenario 2: Production (Specific domains)

File: `application.yml`

```yaml
cors:
  allowed-origins: "https://myapp.com,https://www.myapp.com,https://admin.myapp.com"
  allow-credentials: true
  max-age: 86400
```

**Run:**
```bash
mvn spring-boot:run
```

### Scenario 3: Disable Credentials (nếu không cần cookies/JWT)

```yaml
cors:
  allowed-origins: "*"  # ✅ Có thể dùng wildcard
  allow-credentials: false  # ✅ Phải false
  max-age: 3600
```

### Scenario 4: Environment Variables

```bash
# Run với custom CORS
java -jar app.jar \
  --cors.allowed-origins="http://custom.com:3000" \
  --cors.allow-credentials=true
```

---

## ⚠️ Lưu ý quan trọng

### ❌ SAI - Không thể dùng cùng lúc:
```java
.allowedOrigins("*")
.allowCredentials(true)
```

### ✅ ĐÚNG - Một trong hai:

**Option 1: Specific origins + credentials**
```java
.allowedOrigins("http://localhost:3000", "https://myapp.com")
.allowCredentials(true)
```

**Option 2: Wildcard + NO credentials**
```java
.allowedOrigins("*")
.allowCredentials(false)
```

---

## 🧪 Test sau khi Fix

### 1. Test từ Terminal (MacBook)

```bash
# Test connection
curl http://192.168.1.100:8081/api/v1/part-types

# Expected: [] (empty array)
```

### 2. Test CORS Headers

```bash
curl -i -X OPTIONS http://192.168.1.100:8081/api/v1/part-types \
  -H "Origin: http://192.168.1.200:3000" \
  -H "Access-Control-Request-Method: GET"

# Look for in response:
# access-control-allow-origin: http://192.168.1.200:3000 ✅
# access-control-allow-credentials: true ✅
```

### 3. Test từ Browser (MacBook Safari)

```javascript
// Safari → Develop → Show JavaScript Console
fetch('http://192.168.1.100:8081/api/v1/part-types', {
  method: 'GET',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include'
})
.then(r => r.json())
.then(d => console.log('✅ Success:', d))
.catch(e => console.error('❌ Error:', e));
```

---

## 📋 Checklist

- [ ] Fix CorsConfig.java (không dùng wildcard "*")
- [ ] Xác định IP MacBook: `192.168.1.200`
- [ ] Update application-dev.yml với MacBook IP
- [ ] Restart backend: `mvn spring-boot:run --spring.profiles.active=dev`
- [ ] Test: `curl http://192.168.1.100:8081/api/v1/part-types`
- [ ] Test CORS: `curl -X OPTIONS ...` check headers
- [ ] Update Frontend .env: `REACT_APP_API_URL=http://192.168.1.100:8081`
- [ ] Test từ browser console

---

## 🎯 Kết quả mong đợi

**Backend logs:**
```
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081
Initializing Spring DispatcherServlet 'dispatcherServlet'
```

**Frontend request headers:**
```
GET /api/v1/part-types HTTP/1.1
Host: 192.168.1.100:8081
Origin: http://192.168.1.200:3000
```

**Backend response headers:**
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://192.168.1.200:3000
Access-Control-Allow-Credentials: true
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS,PATCH
```

---

## 🔗 Liên quan

- **CORS_CONFIG.md** - Hướng dẫn CORS chi tiết
- **MACBOOK_SETUP.md** - Setup MacBook + Windows

---

**Version**: 1.0
**Status**: ✅ Fixed
**Last Updated**: 2024
