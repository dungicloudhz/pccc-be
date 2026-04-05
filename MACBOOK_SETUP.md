# Hướng dẫn Setup API Từ MacBook (Frontend) Đến Windows Machine (Backend)

## 🎯 Tình Huống

```
Windows Machine (Máy A):
├── Backend Java Spring Boot
└── Chạy trên port 8081

MacBook (Máy B):
├── Frontend React/Vue
└── Chạy trên port 3000
```

---

## 📍 Bước 1: Xác định IP Address

### MacBook (Frontend)

**Cách 1: Dùng System Preferences**
1. **Apple Menu** → **System Preferences** → **Network**
2. Chọn **Wi-Fi** hoặc **Ethernet**
3. Xem **IP Address** (VD: `192.168.1.200`)

**Cách 2: Dùng Terminal**
```bash
# Hiển thị tất cả network interfaces
ifconfig

# Hoặc dùng lệnh này (đơn giản hơn)
ipconfig getifaddr en0  # Wi-Fi
ipconfig getifaddr en5  # Ethernet
```

**Output ví dụ:**
```
en0: flags=8863<UP,BROADCAST,RUNNING,SIMPLEX,MULTICAST> mtu 1500
    inet 192.168.1.200 netmask 0xffffff00 broadcast 192.168.1.255
```

→ **MacBook IP: `192.168.1.200`**

---

### Windows Machine (Backend)

**Terminal (Command Prompt):**
```bash
ipconfig
```

**Output ví dụ:**
```
Ethernet adapter Ethernet:
   IPv4 Address. . . . . . . . . : 192.168.1.100
```

→ **Windows IP: `192.168.1.100`**

---

## ✅ Bước 2: Test Network Connectivity

### Từ MacBook Terminal

```bash
# Ping Windows machine
ping -c 4 192.168.1.100

# Expected output:
# PING 192.168.1.100 (192.168.1.100): 56 data bytes
# 64 bytes from 192.168.1.100: icmp_seq=0 ttl=64 time=5.234 ms
```

Nếu không ping được:
- ✅ Check cùng Wi-Fi network
- ✅ Check Windows Firewall (cho phép ping)
- ✅ Thử dùng Ethernet cable

---

## 🚀 Bước 3: Backend Configuration (Windows Machine)

### 3.1 Check Server Listening

**Windows Command Prompt:**
```bash
netstat -ano | findstr :8081

# Output:
# TCP    0.0.0.0:8081         0.0.0.0:0    LISTENING    1234
# ✅ Có 0.0.0.0:8081 = Backend nghe tất cả interfaces
```

### 3.2 Add MacBook IP to CORS

File: `application.yml` (hoặc `application-dev.yml`)

```yaml
cors:
  allowed-origins: "http://192.168.1.200:3000,http://localhost:3000,http://127.0.0.1:3000"
  allow-credentials: true

server:
  port: 8081
  address: 0.0.0.0
```

**Hoặc** file: `src/main/java/com/cozyquoteforge/pccc/config/CorsConfig.java`

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://192.168.1.200:3000",  // MacBook
                    "http://localhost:3000",
                    "http://127.0.0.1:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

**Restart Backend:**
```bash
# Windows Command Prompt
mvn clean install
mvn spring-boot:run
```

---

## 🔌 Bước 4: Test From MacBook

### 4.1 Test Direct Connection (Terminal)

```bash
# Test if backend is accessible
curl http://192.168.1.100:8081

# Expected response: Empty page hoặc error page (OK)
# ✅ Nếu không response: Backend không listen hoặc Firewall chặn
```

### 4.2 Test API Endpoint

```bash
curl -X GET http://192.168.1.100:8081/api/v1/part-types \
  -H "Content-Type: application/json"

# Expected: [] (empty array) hoặc data
```

### 4.3 Test CORS Pre-flight

```bash
curl -X OPTIONS http://192.168.1.100:8081/api/v1/part-types \
  -H "Origin: http://192.168.1.200:3000" \
  -H "Access-Control-Request-Method: GET" \
  -v

# Tìm trong output:
# < Access-Control-Allow-Origin: http://192.168.1.200:3000
# ✅ Nếu có = CORS OK
```

---

## 💻 Bước 5: Frontend Configuration (MacBook)

### 5.1 React + Fetch API

**File: `src/api/client.js`**
```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://192.168.1.100:8081';

export const fetchPartTypes = async () => {
  const response = await fetch(`${API_BASE_URL}/api/v1/part-types`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include', // important for CORS
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(`API Error: ${error.message || response.statusText}`);
  }

  return response.json();
};
```

**File: `.env.local`**
```
REACT_APP_API_URL=http://192.168.1.100:8081
```

### 5.2 React Hook Example

```javascript
import { useState, useEffect } from 'react';
import { fetchPartTypes } from './api/client';

function App() {
  const [data, setData] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchPartTypes()
      .then(data => setData(data))
      .catch(err => setError(err.message));
  }, []);

  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>Part Types</h1>
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  );
}

export default App;
```

### 5.3 Axios Configuration

**File: `src/api/axios.js`**
```javascript
import axios from 'axios';

const client = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://192.168.1.100:8081',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// Add response interceptor for error handling
client.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default client;
```

**Usage:**
```javascript
import client from './api/axios';

// GET
client.get('/api/v1/part-types');

// POST
client.post('/api/v1/part-types', { name: 'New Category' });

// PUT
client.put('/api/v1/part-types/id', { name: 'Updated' });

// DELETE
client.delete('/api/v1/part-types/id');
```

---

## ⚙️ Bước 6: Firewall Configuration

### Windows Firewall (Allow Port 8081)

**PowerShell (Run as Administrator):**
```powershell
# Add firewall rule
netsh advfirewall firewall add rule name="Allow Port 8081" dir=in action=allow protocol=tcp localport=8081 remoteip=192.168.1.200

# Verify
netsh advfirewall firewall show rule name="Allow Port 8081"
```

**Hoặc GUI:**
1. **Settings** → **Privacy & Security** → **Windows Defender Firewall**
2. **Allow an app through firewall**
3. Click **Allow another app**
4. Select Java/Spring Boot application
5. Check **Private** checkbox
6. Click **Add**

### macOS Firewall (Usually not needed)

macOS firewall bị disabled by default. Nếu enable:
1. **System Preferences** → **Security & Privacy** → **Firewall**
2. Click **Firewall Options**
3. Uncheck "Block all incoming connections"

---

## 🧪 Bước 7: Test from MacBook Browser

### Safari Console Test

```javascript
// Open Safari → Develop → Show JavaScript Console
fetch('http://192.168.1.100:8081/api/v1/part-types', {
  method: 'GET',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include'
})
.then(r => r.json())
.then(data => console.log('✅ Success:', data))
.catch(err => console.error('❌ Error:', err));
```

### Network Tab Debug

**Safari:**
1. **Develop** → **Show Web Inspector**
2. Tab **Network**
3. Refresh page
4. Look for API requests to `192.168.1.100:8081`
5. Check response headers for `Access-Control-Allow-Origin`

---

## 🔍 Troubleshooting

### Problem 1: "Cannot reach 192.168.1.100:8081"

```bash
# Check connectivity
ping 192.168.1.100

# Check DNS
nslookup 192.168.1.100

# Check port specific
nc -zv 192.168.1.100 8081
# Success: Connection to 192.168.1.100 port 8081 [tcp/*] succeeded!
```

**Solutions:**
- ✅ Verify Windows machine IP
- ✅ Check Wi-Fi both machines on same network
- ✅ Disable Windows Firewall temporarily to test
- ✅ Check backend is running: `netstat -ano | findstr :8081`

### Problem 2: "CORS error in Browser"

```
Access to fetch at 'http://192.168.1.100:8081/api/v1/part-types'
from origin 'http://192.168.1.200:3000' has been blocked by CORS policy
```

**Solution:**
```yaml
# Check application.yml has MacBook IP
cors:
  allowed-origins: "http://192.168.1.200:3000"
```

Restart backend and clear browser cache.

### Problem 3: "Connection timeout"

```bash
# Test with timeout
timeout 5 curl http://192.168.1.100:8081/api/v1/part-types

# If timeout, check:
# 1. Backend running?
# 2. Network issue?
# 3. Try HTTP instead of HTTPS
```

### Problem 4: "macOS shows 'Cannot find server'"

**Possible causes:**
- DNS issue
- Network connection lost
- Firewall blocking

**Solutions:**
```bash
# Flush DNS cache (macOS)
sudo dscacheutil -flushcache
sudo killall -HUP mDNSResponder

# Restart Wi-Fi
networksetup -setairportpower en0 off
networksetup -setairportpower en0 on

# Or test with direct IP (not hostname)
curl http://192.168.1.100:8081
```

---

## 📋 Complete Setup Checklist

- [ ] **Step 1:** Get Windows IP: `192.168.1.100`
- [ ] **Step 2:** Get MacBook IP: `192.168.1.200`
- [ ] **Step 3:** Both on same Wi-Fi network
- [ ] **Step 4:** Ping from MacBook: `ping 192.168.1.100` ✅
- [ ] **Step 5:** Windows backend running on port 8081
- [ ] **Step 6:** Check `netstat -ano | findstr :8081` on Windows
- [ ] **Step 7:** Update CORS config with MacBook IP
- [ ] **Step 8:** Restart Windows backend
- [ ] **Step 9:** Open Windows Firewall, add port 8081
- [ ] **Step 10:** Test from MacBook: `curl http://192.168.1.100:8081/api/v1/part-types`
- [ ] **Step 11:** Update `.env` in React with `REACT_APP_API_URL=http://192.168.1.100:8081`
- [ ] **Step 12:** Test from browser console
- [ ] **Step 13:** Check Network tab in Safari DevTools
- [ ] **Step 14:** Run React app and test API calls

---

## 🛠️ MacBook Terminal Commands Quick Reference

```bash
# Get IP Address
ipconfig getifaddr en0

# Check network connectivity
ping -c 1 192.168.1.100

# Test port
nc -zv 192.168.1.100 8081

# Test API
curl http://192.168.1.100:8081/api/v1/part-types

# Pretty print JSON
curl http://192.168.1.100:8081/api/v1/part-types | json_pp

# Test CORS headers
curl -i -X OPTIONS http://192.168.1.100:8081/api/v1/part-types \
  -H "Origin: http://192.168.1.200:3000"

# DNS cache clear
sudo dscacheutil -flushcache
sudo killall -HUP mDNSResponder

# List network interfaces
ifconfig | grep inet
```

---

## 📚 macOS Networking Tips

1. **Switch between Wi-Fi and Ethernet:**
   - Use System Preferences → Network
   - IP may change, update in .env if needed

2. **Keep IP consistent:**
   - Set Static IP in Network settings
   - Or use hostname instead of IP

3. **VPN Issues:**
   - Some VPN software blocks LAN access
   - Try disabling VPN to test

4. **Host names alternative:**
   - Instead of IP, use hostname like `windows-machine.local`
   - May be easier to remember

---

## 🎯 Next Steps

1. **Run backend on Windows:**
   ```bash
   mvn spring-boot:run
   ```

2. **Run frontend on MacBook:**
   ```bash
   npm start  # React
   # or
   npm run dev  # Vite
   ```

3. **Test from browser:**
   - Open http://localhost:3000
   - Check browser Network tab
   - Verify API requests to 192.168.1.100:8081

4. **If any issues:**
   - Check all checklist items
   - Run troubleshooting commands
   - Report error messages

---

**Version**: 1.0
**Platform**: macOS + Windows
**Last Updated**: 2024
