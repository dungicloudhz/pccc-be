# Tài liệu thiết kế API - Cozy Quote Forge (Backend)

Tài liệu này mô tả các API và cấu trúc cơ sở dữ liệu (Database Schema) để phục vụ cho việc xây dựng Backend bằng Java Spring Boot và cơ sở dữ liệu PostgreSQL dựa trên các tính năng hiện tại của Frontend.

## 1. Tổng quan các tính năng
Hệ thống hiện tại (Frontend) bao gồm các chức năng chính xoay quanh việc lập dự toán, quản lý vật tư và quản lý công trình báo giá, bao gồm các Module cụ thể như sau:
1. **Quản lý Loại hạng mục / Loại vật tư (Part Types / Categories)**: Lưu trữ các nhóm danh mục như "Phần chữa cháy", "Phần báo cháy", "Vật liệu xây dựng",...
2. **Quản lý Vật tư, Thiết bị (Products)**: Thêm, sửa, xóa, danh sách các vật tư với các thuộc tính như tên, mã, hãng, giá vật tư, giá nhân công, tỉ lệ hao hụt,...
3. **Quản lý Danh sách Công trình (Constructions)**: Tạo mới, cập nhật, xóa, và liệt kê các dự án/công trình cần lập dự toán.
4. **Chi tiết Dự toán Công trình (Construction Detail)**: Lưu trữ chi tiết một bảng khối lượng vật tư công trình phức tạp, với cấu trúc bao gồm các hạng mục (sections), các xưởng (workshops - đại diện cho các khu vực/vị trí đo đếm), và các dòng vật tư chi tiết bên trong từng hạng mục.

---

## 2. Thông tin cấu trúc Database (PostgreSQL)

Dưới đây là thiết kế các bảng dữ liệu cho PostgreSQL để lưu trữ trạng thái của ứng dụng.

### Bảng `part_types` (Danh mục Hạng mục)
* `id` (UUID, Primary Key)
* `name` (VARCHAR, Not Null): Tên danh mục (Ví dụ: "Phần chữa cháy").
* `created_at` (TIMESTAMP)
* `updated_at` (TIMESTAMP)

### Bảng `products` (Vật tư, thiết bị)
* `id` (VARCHAR hoặc UUID, Primary Key)
* `name` (VARCHAR, Not Null): Tên vật tư
* `unit` (VARCHAR): Đơn vị tính (Cái, M, Lít...)
* `category_id` (UUID, Foreign Key hoặc VARCHAR nullable) / Hoặc sử dụng VARCHAR `category` theo thiết kế cũ.
* `origin` (VARCHAR): Xuất xứ
* `code` (VARCHAR): Mã vật tư
* `material_unit_price` (NUMERIC): Đơn giá vật tư gốc
* `labor_unit_price` (NUMERIC): Đơn giá nhân công gốc
* `loss_percent` (NUMERIC): % Hao hụt mặc định
* `created_at` (TIMESTAMP)

### Bảng `constructions` (Công trình)
* `id` (UUID, Primary Key)
* `name` (VARCHAR, Not Null): Tên dự án / nhà thuốc
* `material_percent` (NUMERIC): % điều chỉnh giá vật tư cho công trình này (Mặc định 100)
* `labor_percent` (NUMERIC): % điều chỉnh giá nhân công cho công trình này (Mặc định 100)
* `created_at` (TIMESTAMP)
* `updated_at` (TIMESTAMP)

### Bảng `construction_workshops` (Các xưởng / khu vực trong công trình)
* `id` (UUID, Primary Key)
* `construction_id` (UUID, Foreign Key -> `constructions.id`)
* `name` (VARCHAR): Tên xưởng (Ví dụ: "Xưởng 1")
* `display_order` (INT): Thứ tự hiển thị

### Bảng `construction_sections` (Các hạng mục trong bảng giá của công trình)
* `id` (UUID, Primary Key)
* `construction_id` (UUID, Foreign Key -> `constructions.id`)
* `name` (VARCHAR): Tên hạng mục (Ví dụ: "Phần chữa cháy")
* `display_order` (INT): Thứ tự hiển thị

### Bảng `construction_rows` (Chi tiết dòng vật tư trong hạng mục)
* `id` (UUID, Primary Key)
* `section_id` (UUID, Foreign Key -> `construction_sections.id`)
* `product_id` (VARCHAR/UUID, Nullable, Foreign Key -> `products.id`): ID Vật tư (hoặc có thể bỏ trống nếu user nhập tự do).
* `code` (VARCHAR): Mã (có thể overwrite từ product)
* `note` (TEXT): Ghi chú
* `total_cable` (NUMERIC, Nullable): Tổng dây (thông tin bổ sung)
* `loss_percent` (NUMERIC, Nullable): % hao hụt áp dụng cho dòng này
* `material_price` (NUMERIC, Nullable): Đơn giá vật tư áp dụng
* `labor_price` (NUMERIC, Nullable): Đơn giá nhân công áp dụng
* `display_order` (INT): Thứ tự dòng

### Bảng `construction_row_workshop_values` (Giá trị đo khối lượng cho từng xưởng trên mỗi dòng)
* `row_id` (UUID, Foreign Key -> `construction_rows.id`)
* `workshop_id` (UUID, Foreign Key -> `construction_workshops.id`)
* `value` (NUMERIC, Nullable): Số lượng khối lượng ghi nhận
* Primary Key chập: `(row_id, workshop_id)`

---

## 3. Danh sách API (Request và Response)

### 3.1. Danh mục hạng mục (Part Types)

#### `GET /api/v1/part-types`
* Lấy danh sách các loại hạng mục.
* **Response `200 OK`**:
  ```json
  [
    {
      "id": "uuid",
      "name": "Phần chữa cháy",
      "createdAt": "2023-10-01T12:00:00Z",
      "updatedAt": "2023-10-01T12:00:00Z"
    }
  ]
  ```

#### `POST /api/v1/part-types`
* Tạo mới hạng mục.
* **Request**:
  ```json
  { "name": "Vật liệu điện" }
  ```
* **Response `201 Created`**: Object hạng mục vừa tạo kèm ID.

#### `PUT /api/v1/part-types/{id}`
* Cập nhật hạng mục.
* **Request**:
  ```json
  { "name": "Vật liệu điện (Cập nhật)" }
  ```
* **Response `200 OK`**.

#### `DELETE /api/v1/part-types/{id}`
* Xóa hạng mục (Response `204 No Content`).

---

### 3.2. Quản lý Vật tư, Thiết bị (Products)

#### `GET /api/v1/products`
* Lấy danh sách vật tư.
* **Response `200 OK`**:
  ```json
  [
    {
      "id": "p1",
      "name": "Đầu phun sprinkler",
      "category": "Phần chữa cháy",
      "origin": "Trung Quốc",
      "unit": "Cái",
      "code": "P1",
      "materialUnitPrice": 120000,
      "laborUnitPrice": 30000,
      "lossPercent": 5
    }
  ]
  ```

#### `POST /api/v1/products`
* Xây dựng API tạo mới sản phẩm (Tương tự format Request của Response GET).

#### `PUT /api/v1/products/{id}`
* Cập nhật thông tin vật tư.

#### `DELETE /api/v1/products/{id}`
* Xóa vật tư by ID.

---

### 3.3. Quản lý danh sách Công trình (Constructions)

#### `GET /api/v1/constructions`
* Màn hình danh sách công trình (hiển thị tóm tắt, không load chi tiết row/section).
* **Response `200 OK`**:
  ```json
  [
    {
      "id": "c1",
      "name": "Nhà thuốc A",
      "createdAt": "2023-10-01T10:00:00Z",
      "updatedAt": "2023-10-01T10:00:00Z"
    }
  ]
  ```

#### `POST /api/v1/constructions`
* Khởi tạo công trình mới
* **Request**:
  ```json
  { "name": "Dự án B" }
  ```

#### `PUT /api/v1/constructions/{id}` / `DELETE /api/v1/constructions/{id}`
* Đổi tên hoặc Xóa công trình.

---

### 3.4. API Chi tiết Dự toán Công trình (Construction Details)
Đây là API phức tạp nhất, nên gom nhóm vào một cấu trúc JSON JSON (Tree) để dễ dàng đồng bộ từ Frontend.

#### `GET /api/v1/constructions/{id}/details`
* Tải toàn bộ cấu trúc chi tiết để điền vào màn hình `ConstructionDetailPage`.
* **Response `200 OK`**:
  ```json
  {
    "id": "c1",
    "projectName": "Nhà thuốc",
    "materialPercent": 100,
    "laborPercent": 100,
    "workshops": [
      { "id": "w1", "name": "Xưởng 1" },
      { "id": "w2", "name": "Xưởng 2" }
    ],
    "sections": [
      {
        "id": "sec1",
        "name": "Phần chữa cháy",
        "rows": [
          {
            "id": "row1",
            "productId": "p1",
            "code": "P1",
            "note": "Ghi chú thêm",
            "totalCable": 10.5,
            "lossPercent": 5,
            "materialPrice": 120000,
            "laborPrice": 30000,
            "workshopValues": {
              "w1": 15,
              "w2": 20
            }
          }
        ]
      }
    ]
  }
  ```

#### `PUT /api/v1/constructions/{id}/details`
* API dùng để "Lưu" (Save) bảng dữ liệu. Nó sẽ cập nhật lại toàn bộ `percent`, `workshops`, `sections`, và `rows` của chi tiết bảng khối lượng. Tại backend, logic chuẩn nhất là có thể Drop & Re-insert các entities con (`sections`, `rows`, `workshops`) hoặc sync thông minh (Upsert).
* **Request**:
  Gửi vào JSON Body cấu trúc tương tự API `GET` để backend phân tích và lưu.
  ```json
  {
    "projectName": "Nhà thuốc (Đã sửa)",
    "materialPercent": 105,
    "laborPercent": 100,
    "workshops": [
       ...
    ],
    "sections": [
       ...
    ]
  }
  ```
* **Response `200 OK`**: 
  ```json
  {
    "message": "Saved successfully",
    "status": 200
  }
  ``` 

## 4. Ghi chú cho lập trình viên Backend (Java Spring Boot)
1. Hãy dùng JPA / Hibernate cùng với các cấu hình cascading như `CascadeType.ALL` (`orphanRemoval = true`) cho mối quan hệ từ `Construction` -> `ConstructionSection` -> `ConstructionRow` để dễ dàng update toàn bộ bảng khi gọi `PUT /details` mà không gặp rác dữ liệu.
2. Với `workshopValues` (đang map key-value từ React state) bạn có thể custom map thành List Entity `ConstructionRowWorkshopValue` trong payload hoặc dùng tính năng `@ElementCollection` của Hibernate map trực tiếp ra bảng.
3. Để hiệu năng tốt, bạn có thể thiết kế `@EntityGraph` khi GET chi tiết công trình tránh N+1 Query.
