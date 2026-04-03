# API Documentation - cURL Commands

Tài liệu này cung cấp các lệnh `curl` để kiểm tra tất cả các API endpoints của PCCC Backend.

**Base URL**: `http://localhost:8080`

---

## 1. Part Types API (Danh mục Hạng mục)

### 1.1 Lấy danh sách tất cả danh mục hạng mục

```bash
curl -X GET http://localhost:8080/api/v1/part-types \
  -H "Content-Type: application/json"
```

**Response `200 OK`**:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Phần chữa cháy",
    "createdAt": "2023-10-01T12:00:00",
    "updatedAt": "2023-10-01T12:00:00"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Phần báo cháy",
    "createdAt": "2023-10-02T10:30:00",
    "updatedAt": "2023-10-02T10:30:00"
  }
]
```

---

### 1.2 Lấy chi tiết một danh mục hạng mục

```bash
curl -X GET http://localhost:8080/api/v1/part-types/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json"
```

**Response `200 OK`**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Phần chữa cháy",
  "createdAt": "2023-10-01T12:00:00",
  "updatedAt": "2023-10-01T12:00:00"
}
```

---

### 1.3 Tạo mới một danh mục hạng mục

```bash
curl -X POST http://localhost:8080/api/v1/part-types \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vật liệu điện"
  }'
```

**Response `201 Created`**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "name": "Vật liệu điện",
  "createdAt": "2023-10-03T14:15:00",
  "updatedAt": "2023-10-03T14:15:00"
}
```

---

### 1.4 Cập nhật danh mục hạng mục

```bash
curl -X PUT http://localhost:8080/api/v1/part-types/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Phần chữa cháy (Cập nhật)"
  }'
```

**Response `200 OK`**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Phần chữa cháy (Cập nhật)",
  "createdAt": "2023-10-01T12:00:00",
  "updatedAt": "2023-10-03T15:30:00"
}
```

---

### 1.5 Xóa danh mục hạng mục

```bash
curl -X DELETE http://localhost:8080/api/v1/part-types/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json"
```

**Response `204 No Content`** (Không có body)

---

## 2. Products API (Vật tư, Thiết bị)

### 2.1 Lấy danh sách tất cả vật tư

```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json"
```

**Response `200 OK`**:
```json
[
  {
    "id": "p1",
    "name": "Đầu phun sprinkler",
    "unit": "Cái",
    "category": "Phần chữa cháy",
    "origin": "Trung Quốc",
    "code": "P1",
    "materialUnitPrice": 120000,
    "laborUnitPrice": 30000,
    "lossPercent": 5
  },
  {
    "id": "p2",
    "name": "Ống nước PVC",
    "unit": "Mét",
    "category": "Vật liệu xây dựng",
    "origin": "Việt Nam",
    "code": "P2",
    "materialUnitPrice": 50000,
    "laborUnitPrice": 10000,
    "lossPercent": 3
  }
]
```

---

### 2.2 Lấy chi tiết một vật tư

```bash
curl -X GET http://localhost:8080/api/v1/products/p1 \
  -H "Content-Type: application/json"
```

**Response `200 OK`**:
```json
{
  "id": "p1",
  "name": "Đầu phun sprinkler",
  "unit": "Cái",
  "category": "Phần chữa cháy",
  "origin": "Trung Quốc",
  "code": "P1",
  "materialUnitPrice": 120000,
  "laborUnitPrice": 30000,
  "lossPercent": 5
}
```

---

### 2.3 Tạo mới một vật tư

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "id": "p3",
    "name": "Bơm nước",
    "unit": "Cái",
    "category": "Thiết bị",
    "origin": "Nhật Bản",
    "code": "P3",
    "materialUnitPrice": 500000,
    "laborUnitPrice": 100000,
    "lossPercent": 2
  }'
```

**Response `201 Created`**:
```json
{
  "id": "p3",
  "name": "Bơm nước",
  "unit": "Cái",
  "category": "Thiết bị",
  "origin": "Nhật Bản",
  "code": "P3",
  "materialUnitPrice": 500000,
  "laborUnitPrice": 100000,
  "lossPercent": 2
}
```

---

### 2.4 Cập nhật vật tư

```bash
curl -X PUT http://localhost:8080/api/v1/products/p1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": "p1",
    "name": "Đầu phun sprinkler (v2)",
    "unit": "Cái",
    "category": "Phần chữa cháy",
    "origin": "Trung Quốc",
    "code": "P1_V2",
    "materialUnitPrice": 130000,
    "laborUnitPrice": 35000,
    "lossPercent": 5
  }'
```

**Response `200 OK`**:
```json
{
  "id": "p1",
  "name": "Đầu phun sprinkler (v2)",
  "unit": "Cái",
  "category": "Phần chữa cháy",
  "origin": "Trung Quốc",
  "code": "P1_V2",
  "materialUnitPrice": 130000,
  "laborUnitPrice": 35000,
  "lossPercent": 5
}
```

---

### 2.5 Xóa vật tư

```bash
curl -X DELETE http://localhost:8080/api/v1/products/p1 \
  -H "Content-Type: application/json"
```

**Response `204 No Content`** (Không có body)

---

## 3. Constructions API (Công trình)

### 3.1 Lấy danh sách tất cả công trình

```bash
curl -X GET http://localhost:8080/api/v1/constructions \
  -H "Content-Type: application/json"
```

**Response `200 OK`**:
```json
[
  {
    "id": "c1-uuid",
    "name": "Nhà thuốc A",
    "createdAt": "2023-10-01T10:00:00",
    "updatedAt": "2023-10-01T10:00:00"
  },
  {
    "id": "c2-uuid",
    "name": "Bệnh viện B",
    "createdAt": "2023-10-02T09:30:00",
    "updatedAt": "2023-10-02T09:30:00"
  }
]
```

---

### 3.2 Lấy chi tiết một công trình

```bash
curl -X GET http://localhost:8080/api/v1/constructions/c1-uuid \
  -H "Content-Type: application/json"
```

**Response `200 OK`**:
```json
{
  "id": "c1-uuid",
  "name": "Nhà thuốc A",
  "createdAt": "2023-10-01T10:00:00",
  "updatedAt": "2023-10-01T10:00:00"
}
```

---

### 3.3 Tạo mới một công trình

```bash
curl -X POST http://localhost:8080/api/v1/constructions \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dự án C"
  }'
```

**Response `201 Created`**:
```json
{
  "id": "c3-uuid",
  "name": "Dự án C",
  "createdAt": "2023-10-03T11:00:00",
  "updatedAt": "2023-10-03T11:00:00"
}
```

---

### 3.4 Cập nhật công trình

```bash
curl -X PUT http://localhost:8080/api/v1/constructions/c1-uuid \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nhà thuốc A (Đã cập nhật)"
  }'
```

**Response `200 OK`**:
```json
{
  "id": "c1-uuid",
  "name": "Nhà thuốc A (Đã cập nhật)",
  "createdAt": "2023-10-01T10:00:00",
  "updatedAt": "2023-10-03T12:30:00"
}
```

---

### 3.5 Xóa công trình

```bash
curl -X DELETE http://localhost:8080/api/v1/constructions/c1-uuid \
  -H "Content-Type: application/json"
```

**Response `204 No Content`** (Không có body)

---

## 4. Construction Details API (Chi tiết Dự toán Công trình)

**LƯU Ý**: Đây là API phức tạp nhất. Nó trả về toàn bộ cấu trúc chi tiết của công trình bao gồm:
- Workshops (Xưởng/Khu vực)
- Sections (Hạng mục)
- Rows (Dòng vật tư chi tiết)
- Workshop Values (Giá trị đo khối lượng cho từng workshop)

---

### 4.1 Lấy chi tiết toàn bộ cấu trúc công trình

```bash
curl -X GET http://localhost:8080/api/v1/constructions/c1-uuid/details \
  -H "Content-Type: application/json"
```

**Response `200 OK`**:
```json
{
  "id": "c1-uuid",
  "projectName": "Nhà thuốc A",
  "materialPercent": 100,
  "laborPercent": 100,
  "workshops": [
    {
      "id": "w1-uuid",
      "name": "Xưởng 1"
    },
    {
      "id": "w2-uuid",
      "name": "Xưởng 2"
    }
  ],
  "sections": [
    {
      "id": "sec1-uuid",
      "name": "Phần chữa cháy",
      "rows": [
        {
          "id": "row1-uuid",
          "productId": "p1",
          "code": "P1",
          "note": "Ghi chú thêm",
          "totalCable": 10.5,
          "lossPercent": 5,
          "materialPrice": 120000,
          "laborPrice": 30000,
          "workshopValues": {
            "w1-uuid": 15,
            "w2-uuid": 20
          }
        },
        {
          "id": "row2-uuid",
          "productId": "p2",
          "code": "P2",
          "note": "Ống nước khu vực 1",
          "totalCable": null,
          "lossPercent": 3,
          "materialPrice": 50000,
          "laborPrice": 10000,
          "workshopValues": {
            "w1-uuid": 100,
            "w2-uuid": 150
          }
        }
      ]
    },
    {
      "id": "sec2-uuid",
      "name": "Phần báo cháy",
      "rows": [
        {
          "id": "row3-uuid",
          "productId": "p4",
          "code": "P4",
          "note": null,
          "totalCable": null,
          "lossPercent": 2,
          "materialPrice": 80000,
          "laborPrice": 20000,
          "workshopValues": {
            "w1-uuid": 5,
            "w2-uuid": 8
          }
        }
      ]
    }
  ]
}
```

---

### 4.2 Cập nhật chi tiết toàn bộ cấu trúc công trình (Lưu)

```bash
curl -X PUT http://localhost:8080/api/v1/constructions/c1-uuid/details \
  -H "Content-Type: application/json" \
  -d '{
    "projectName": "Nhà thuốc A (Sửa)",
    "materialPercent": 105,
    "laborPercent": 100,
    "workshops": [
      {
        "id": "w1-uuid",
        "name": "Xưởng 1"
      },
      {
        "id": "w2-uuid",
        "name": "Xưởng 2 (Cập nhật)"
      },
      {
        "id": null,
        "name": "Xưởng 3 (Mới)"
      }
    ],
    "sections": [
      {
        "id": "sec1-uuid",
        "name": "Phần chữa cháy (Cập nhật)",
        "rows": [
          {
            "id": "row1-uuid",
            "productId": "p1",
            "code": "P1",
            "note": "Ghi chú sửa",
            "totalCable": 12,
            "lossPercent": 5,
            "materialPrice": 125000,
            "laborPrice": 32000,
            "workshopValues": {
              "w1-uuid": 17,
              "w2-uuid": 22
            }
          },
          {
            "id": null,
            "productId": "p5",
            "code": "P5",
            "note": "Sản phẩm mới",
            "totalCable": null,
            "lossPercent": 4,
            "materialPrice": 60000,
            "laborPrice": 15000,
            "workshopValues": {
              "w1-uuid": 8,
              "w2-uuid": 12
            }
          }
        ]
      },
      {
        "id": null,
        "name": "Phần báo cháy (Mới)",
        "rows": [
          {
            "id": null,
            "productId": "p6",
            "code": "P6",
            "note": "Section mới",
            "totalCable": null,
            "lossPercent": 2,
            "materialPrice": 90000,
            "laborPrice": 25000,
            "workshopValues": {
              "w1-uuid": 3,
              "w2-uuid": 5
            }
          }
        ]
      }
    ]
  }'
```

**Response `200 OK`**:
```json
{
  "message": "Saved successfully",
  "status": 200
}
```

---

## 5. Error Responses

### 5.1 Not Found Error

**Request**:
```bash
curl -X GET http://localhost:8080/api/v1/part-types/non-existent-id \
  -H "Content-Type: application/json"
```

**Response `404 Not Found`**:
```json
{
  "timestamp": "2023-10-03T15:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "PartType not found with id: non-existent-id",
  "path": "/api/v1/part-types/non-existent-id"
}
```

---

### 5.2 Invalid Request Error

**Request**:
```bash
curl -X POST http://localhost:8080/api/v1/part-types \
  -H "Content-Type: application/json" \
  -d '{
    "name": ""
  }'
```

**Response `400 Bad Request`** hoặc **500 Internal Server Error** tùy theo validation.

---

## 6. Batch Testing Scripts

### 6.1 Script bash kiểm tra toàn bộ API

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing Part Types API ==="
echo "1. Creating Part Type..."
PART_TYPE=$(curl -s -X POST $BASE_URL/api/v1/part-types \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Category"}' | jq -r '.id')
echo "Created Part Type ID: $PART_TYPE"

echo "2. Getting all Part Types..."
curl -s -X GET $BASE_URL/api/v1/part-types \
  -H "Content-Type: application/json" | jq '.'

echo ""
echo "=== Testing Products API ==="
echo "3. Creating Product..."
curl -s -X POST $BASE_URL/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "id":"prod1",
    "name":"Test Product",
    "unit":"Cái",
    "category":"Test",
    "origin":"Việt Nam",
    "code":"TP1",
    "materialUnitPrice":100000,
    "laborUnitPrice":20000,
    "lossPercent":5
  }' | jq '.'

echo "4. Getting all Products..."
curl -s -X GET $BASE_URL/api/v1/products \
  -H "Content-Type: application/json" | jq '.'

echo ""
echo "=== Testing Constructions API ==="
echo "5. Creating Construction..."
CONSTRUCTION=$(curl -s -X POST $BASE_URL/api/v1/constructions \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Construction"}' | jq -r '.id')
echo "Created Construction ID: $CONSTRUCTION"

echo "6. Getting all Constructions..."
curl -s -X GET $BASE_URL/api/v1/constructions \
  -H "Content-Type: application/json" | jq '.'

echo ""
echo "=== Done ==="
```

---

## 7. Tools để Test API

### 7.1 Postman
1. Import tất cả endpoints từ file này
2. Đặt variable `base_url` = `http://localhost:8080`
3. Chạy collection để kiểm tra

### 7.2 Insomnia
- Tương tự như Postman
- Thêm tất cả curl commands

### 7.3 VS Code REST Client Extension
1. Cài extension "REST Client"
2. Tạo file `requests.http`
3. Copy tất cả curl commands ở trên

---

## 8. Headers và Authentication

Hiện tại không có authentication. Tất cả requests chỉ cần:

```
Content-Type: application/json
```

Trong tương lai nếu thêm JWT:
```
Authorization: Bearer {token}
Content-Type: application/json
```

---

## 9. Notes quan trọng

- Thay `localhost` bằng IP/domain thực tế khi deploy
- Thay port `8080` nếu cấu hình khác trong `application.yml`
- UUID trong ví dụ chỉ mang tính chất minh họa, thực tế sẽ khác
- `jq` tool dùng để format JSON, có thể cài từ: https://stedolan.github.io/jq/

---

## 10. Examples sử dụng cURL Options

### Pretty Print JSON Response
```bash
curl -s http://localhost:8080/api/v1/part-types | jq '.'
```

### Save Response to File
```bash
curl -s http://localhost:8080/api/v1/part-types > response.json
```

### Verbose Output (kiểm tra headers)
```bash
curl -v http://localhost:8080/api/v1/part-types
```

### Measure Response Time
```bash
curl -w "Time taken: %{time_total}s\n" http://localhost:8080/api/v1/part-types
```

### Custom Headers
```bash
curl -X GET http://localhost:8080/api/v1/part-types \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

**Version**: 1.0
**Last Updated**: 2024
**Author**: PCCC Backend Team
