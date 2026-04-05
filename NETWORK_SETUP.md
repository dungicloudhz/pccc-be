# Hướng dẫn Gọi API Từ 2 Máy Tính Khác Nhau (Cùng Network)

## 🎯 Vấn đề
Backend chạy trên **Máy A** (LAN), Frontend chạy trên **Máy B** (cùng LAN), nhưng frontend không gọi được API từ backend.

**Nguyên nhân chính:**
1. ✅ Backend chỉ listen trên `localhost` (127.0.0.1), không listen trên network interface
2. ✅ Frontend gọi sai URL (gọi `localhost:8081` thay vì IP của backend)
3. ✅ Firewall chặn port 8081
4. ✅ CORS cấu hình sai

---

## 📋 Bước 1: Xác định IP Addresses

### Máy A (Backend)

**Windows:**
```bash
ipconfig
```

Tìm dòng:`IPv4 Address: 192.168.x.x` hoặc `10.0.x.x`

**Linux/Mac:**
```bash
ifconfig
# hoặc
ip addr | grep "inet "
```

**Ví dụ:**
```
Máy A (Backend):   192.168.1.100  (port 8081)
Máy B (Frontend):  192.168.1.200  (port 3000)
```

---

## ✅ Bước 2: Cấu hình Backend (Đã làm)

### 2.1 Backend Listen trên Network (✅ Đã cấu hình)

File: `src/main/resources/application.yml`
```yaml
server:
  port: 8081
  address: 0.0.0.0  # ✅ Listen trên tất cả network interfaces
```

### 2.2 CORS Configuration (✅ Đã cấu hình)

File: `src/main/java/com/cozyquoteforge/pccc/config/CorsConfig.java`

Thêm IP của Máy B vào allowed origins:

```yaml
cors:
  allowed-origins: "http://192.168.1.100:3000,http://192.168.1.200:3000,http://localhost:3000"
  allow-credentials: true
```

**Hoặc** cập nhật file `application-dev.yml`:

```yaml
cors:
  allowed-origins: "http://192.168.1.200:3000,http://localhost:3000,http://127.0.0.1:3000"
  allow-credentials: true
```

---

## 🚀 Bước 3: Chạy Backend

### Option 1: Run normally

```bash
mvn clean install
mvn spring-boot:run
```

Backend sẽ chạy tại:
- **Từ Máy B**: `http://192.168.1.100:8081` ✅
- **Từ Máy A**: `http://localhost:8081` ✅

### Option 2: Run with specific profile

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Option 3: Chỉ định IP address cụ thể

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.address=0.0.0.0"
```

---

## 🔌 Bước 4: Test Connectivity

### 4.1 Test từ Máy B (Frontend Machine)

```bash
# Kiểm tra backend có accessible không
ping 192.168.1.100

# Hoặc test port 8081
curl http://192.168.1.100:8081

# Test API cụ thể
curl http://192.168.1.100:8081/api/v1/part-types
```

**Response mong đợi:**
```json
[]  # Empty array hoặc danh sách dữ liệu
```

Nếu có lỗi:
```
curl: (7) Failed to connect to 192.168.1.100 port 8081: Connection refused
```

→ Kiểm tra lại bước 2 (Server cấu hình) hoặc bước 5 (Firewall)

### 4.2 Test CORS Pre-flight

```bash
curl -X OPTIONS http://192.168.1.100:8081/api/v1/part-types \
  -H "Origin: http://192.168.1.200:3000" \
  -H "Access-Control-Request-Method: GET" \
  -v
```

Kiểm tra response headers có `Access-Control-Allow-Origin` không

---

## 🛡️ Bước 5: Cấu hình Firewall

### Windows Firewall

```bash
# Mở PowerShell as Administrator
netsh advfirewall firewall add rule name="Allow Port 8081" dir=in action=allow protocol=tcp localport=8081
```

Hoặc manual:
1. **Settings** → **Privacy & Security** → **Windows Defender Firewall**
2. **Allow an app through firewall**
3. Tìm Java/Spring Boot, tick "Private" + "Public"

### Linux Firewall (UFW)

```bash
sudo ufw allow 8081/tcp
sudo ufw reload
sudo ufw status
```

### macOS Firewall

System Preferences → Security & Privacy → Firewall Options → Allow incoming connections

---

## 💻 Bước 6: Cấu hình Frontend

### React Example

```javascript
// api.js
const API_BASE = process.env.REACT_APP_API_URL || 'http://192.168.1.100:8081';

export const fetchPartTypes = async () => {
  const response = await fetch(`${API_BASE}/api/v1/part-types`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include' // Gửi credentials (cookies, etc)
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return response.json();
};
```

**File `.env`:**
```
REACT_APP_API_URL=http://192.168.1.100:8081
```

### Axios Example

```javascript
// axiosInstance.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://192.168.1.100:8081',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true
});

export default instance;
```

### Test từ Browser Console

```javascript
fetch('http://192.168.1.100:8081/api/v1/part-types')
  .then(r => r.json())
  .then(data => console.log('Success:', data))
  .catch(err => console.error('Error:', err));
```

---

## 🔍 Troubleshooting

### Problem 1: "Connection refused"

```
curl: (7) Failed to connect to 192.168.1.100 port 8081
```

**Nguyên nhân:**
- Backend không chạy
- Port sai
- Firewall chặn

**Giải pháp:**
```bash
# Kiểm tra backend chạy không
netstat -an | grep 8081  # Windows
netstat -tuln | grep 8081  # Linux

# Kiểm tra backend logs
# Restart backend với: mvn spring-boot:run
```

### Problem 2: "CORS policy: No 'Access-Control-Allow-Origin' header"

```
Access to XMLHttpRequest at 'http://192.168.1.100:8081/...'
from origin 'http://192.168.1.200:3000' has been blocked by CORS policy
```

**Nguyên nhân:**
- IP của Frontend không trong `allowedOrigins`

**Giải pháp:**
```yaml
# application.yml hoặc application-dev.yml
cors:
  allowed-origins: "http://192.168.1.200:3000,http://localhost:3000"
```

Restart backend sau khi thay đổi.

### Problem 3: "Network is unreachable"

```bash
ping 192.168.1.100
# ping: connect: Network is unreachable
```

**Nguyên nhân:**
- 2 máy không cùng network
- Network cable bị ngắt
- IP sai

**Giải pháp:**
```bash
# Kiểm tra lại IP
ipconfig  # Windows
ifconfig  # Linux/Mac

# Ping đến gateway
ping 192.168.1.1

# Check network connectivity
arp -a  # Windows
arp  # Linux/Mac
```

### Problem 4: "Request timeout"

**Nguyên nhân:**
- Network chậm
- Firewall drop packets (không reject)
- Database không kết nối được

**Giải pháp:**
```bash
# Test với timeout ngắn
curl --max-time 5 http://192.168.1.100:8081/api/v1/part-types

# Check database connection
# Kiểm tra PostgreSQL accessible từ backend machine
psql -h localhost -U postgres -d pccc_db
```

---

## ✨ Checklist Khi Setup

- [ ] Backend chạy và listen trên `0.0.0.0:8081`
- [ ] Xác định IP của Máy A (Backend) - VD: `192.168.1.100`
- [ ] Xác định IP của Máy B (Frontend) - VD: `192.168.1.200`
- [ ] Cấu hình CORS với IP của Frontend
- [ ] Firewall allow port 8081
- [ ] Test `curl http://192.168.1.100:8081/api/v1/part-types` từ Máy B
- [ ] Frontend URL sử dụng `http://192.168.1.100:8081` (không phải localhost)
- [ ] Test CORS pre-flight request
- [ ] Check browser Network tab để xem request/response

---

## 🧪 Complete Test Script

Chạy từ Máy B:

```bash
#!/bin/bash

BACKEND_IP="192.168.1.100"
BACKEND_PORT="8081"
FRONTEND_URL="http://192.168.1.200:3000"

echo "=== Testing Backend Connectivity ==="

echo "1. Ping backend..."
ping -c 3 $BACKEND_IP

echo ""
echo "2. Test port $BACKEND_PORT..."
curl -v http://$BACKEND_IP:$BACKEND_PORT

echo ""
echo "3. Test API endpoint..."
curl -s http://$BACKEND_IP:$BACKEND_PORT/api/v1/part-types | jq '.'

echo ""
echo "4. Test CORS..."
curl -v -X OPTIONS http://$BACKEND_IP:$BACKEND_PORT/api/v1/part-types \
  -H "Origin: $FRONTEND_URL" \
  -H "Access-Control-Request-Method: GET"

echo ""
echo "=== Done ==="
```

---

## 📚 Tham khảo thêm

- **Network Issues**: https://www.digitalocean.com/community/tutorials/how-to-troubleshoot-connection-issues
- **CORS Guide**: Xem file `CORS_CONFIG.md`
- **Spring Boot Server Binding**: https://spring.io/guides/tutorials/rest/

---

**Version**: 1.0
**Created**: 2024
