# Product API Documentation

## Overview

This document describes the API endpoint for creating multiple products in the PCCC Backend application.

## Authentication

The endpoint requires authentication via JWT token and specific roles.

## Endpoint

### Create Multiple Products

#### Endpoint

`POST /api/v1/products/bulk`

#### Authorization

Requires `ROLE_EDITOR` or `ROLE_ADMIN` authority.

#### Request

- **Content-Type**: `application/json`
- **Body**: Array of `ProductDto` objects

#### ProductDto Schema

```json
{
  "name": "string",
  "unit": "string",
  "category": "string",
  "origin": "string",
  "code": "string",
  "materialUnitPrice": "number (BigDecimal)",
  "laborUnitPrice": "number (BigDecimal)",
  "lossPercent": "number (BigDecimal)"
}
```

#### Response

- **Status Code**: `201 Created`
- **Content-Type**: `application/json`
- **Body**: Array of created `ProductDto` objects (including generated `id`)

#### Example Request

```json
[
  {
    "name": "Product 1",
    "unit": "kg",
    "category": "Category A",
    "origin": "Vietnam",
    "code": "P001",
    "materialUnitPrice": 100.00,
    "laborUnitPrice": 50.00,
    "lossPercent": 5.00
  },
  {
    "name": "Product 2",
    "unit": "m",
    "category": "Category B",
    "origin": "China",
    "code": "P002",
    "materialUnitPrice": 200.00,
    "laborUnitPrice": 75.00,
    "lossPercent": 3.00
  }
]
```

#### Example Response

```json
[
  {
    "id": 1,
    "name": "Product 1",
    "unit": "kg",
    "category": "Category A",
    "origin": "Vietnam",
    "code": "P001",
    "materialUnitPrice": 100.00,
    "laborUnitPrice": 50.00,
    "lossPercent": 5.00
  },
  {
    "id": 2,
    "name": "Product 2",
    "unit": "m",
    "category": "Category B",
    "origin": "China",
    "code": "P002",
    "materialUnitPrice": 200.00,
    "laborUnitPrice": 75.00,
    "lossPercent": 3.00
  }
]
```

## Error Responses

- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **400 Bad Request**: Invalid request data
- **500 Internal Server Error**: Server error