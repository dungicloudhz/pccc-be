# API: POST /api/v1/constructions/{id}/details

## Mô tả
API này dùng để lưu hoặc cập nhật chi tiết công trình dựa trên `id` của construction. Nếu `id` chưa tồn tại trong hệ thống, backend sẽ tạo mới một `Construction` và lưu chi tiết.

## Endpoint
- Method: `POST`
- URL: `/api/v1/constructions/{id}/details`

## Header
- `Content-Type: application/json`

## Path Parameter
- `id` (UUID): ID của construction cần lưu chi tiết.

## Request Body
```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "projectName": "Tên dự án",
  "materialPercent": 100,
  "laborPercent": 100,
  "workshops": [
    {
      "id": "22222222-2222-2222-2222-222222222222",
      "orderId": 1,
      "name": "Xưởng A"
    }
  ],
  "sections": [
    {
      "id": "33333333-3333-3333-3333-333333333333",
      "orderId": 1,
      "name": "Phần 1",
      "rows": [
        {
          "id": "44444444-4444-4444-4444-444444444444",
          "orderId": 1,
          "productId": 123,
          "code": "CODE123",
          "note": "Ghi chú",
          "totalCable": 10.5,
          "lossPercent": 2.5,
          "materialPrice": 150000,
          "laborPrice": 50000,
          "workshopValues": [
            {
              "workshopId": "22222222-2222-2222-2222-222222222222",
              "tenXuong": "Xưởng A",
              "value": 1000
            }
          ]
        }
      ],
      "id_sections": "44444444-4444-4444-4444-444444444444"
    }
  ]
}
```

### Giải thích các trường
- `projectName`: tên dự án / công trình.
- `materialPercent`: tỷ lệ vật tư.
- `laborPercent`: tỷ lệ nhân công.
- `workshops`: danh sách xưởng.
  - `orderId`: thứ tự hiển thị của xưởng.
  - `name`: tên xưởng.
- `sections`: danh sách phần.
  - `rows`: danh sách hàng trong phần.
  - `id_sections`: chuỗi ID các row, nối bằng dấu `,`.

## Response
```json
{
  "message": "Saved successfully",
  "status": 200
}
```

## Ví dụ curl
```bash
curl -X POST "http://localhost:8080/api/v1/constructions/create-details" \
  -H "Content-Type: application/json" \
  -d '{
    "projectName": "Dự án A",
    "materialPercent": 90,
    "laborPercent": 110,
    "workshops": [
      {"orderId": 1, "name": "Xưởng A"}
    ],
    "sections": [
      {
        "orderId": 1,
        "name": "Phần 1",
        "rows": [
          {
            "id": "44444444-4444-4444-4444-444444444444",
            "orderId": 1,
            "productId": 123,
            "code": "CODE123",
            "note": "Ghi chú",
            "totalCable": 10.5,
            "lossPercent": 2.5,
            "materialPrice": 150000,
            "laborPrice": 50000,
            "workshopValues": [
              {"workshopId": "22222222-2222-2222-2222-222222222222", "tenXuong": "Xưởng A", "value": 1000}
            ]
          }
        ],
        "id_sections": "44444444-4444-4444-4444-444444444444"
      }
    ]
  }'
```

## Ghi chú
- API này trả về thông báo thành công và mã 200, không trả về dữ liệu chi tiết công trình.
- Nếu `id` chưa tồn tại, server vẫn tạo mới construction và lưu thông tin.
