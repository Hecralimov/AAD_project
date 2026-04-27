# Frontend API Coverage Report

Source API list: `C:\Users\User\Downloads\Documents\All_APIs.txt`  
Frontend audited under: `frontend/src/app`  
Date: April 27, 2026

## Summary

The frontend is connected to the main demo flows: auth, product browsing/detail, checkout, individual order history/tracking, reviews, admin analytics, admin users/stores/categories, corporate analytics/products/orders, store comparison, and profile view/edit.

Not connected or incomplete areas are mostly broad CRUD endpoints that exist in Swagger but do not have frontend screens yet: generic shipment CRUD, generic order CRUD, generic product catalog create/update/delete, store create/update/delete, user create/update, review edit/delete, chat, and admin review sentiment analytics.

There are also frontend calls that are not present in the supplied API list:

- `PUT /api/profile/password` is used by the B18 change-password UI, but Swagger only lists `GET /api/profile` and `PUT /api/profile`.
- `POST /api/categories`, `PUT /api/categories/{id}`, and `DELETE /api/categories/{id}` are used by the admin category UI, but Swagger only lists `GET /api/categories`.

## Connected APIs

| API | Frontend usage | Feature |
| --- | --- | --- |
| `POST /api/auth/login` | `AuthService.login()` | Login page |
| `POST /api/auth/register` | `AuthService.register()` | Register page |
| `GET /api/products` | `ProductService.getProducts()` | Shop/product browsing |
| `GET /api/products/{id}` | `ProductService.getProductById()` | Product detail page |
| `GET /api/categories` | `CategoryService.getCategories()` | Shop filters, admin categories, corporate product forms |
| `POST /api/individual/checkout` | `OrderService.checkout()` | Checkout page |
| `GET /api/orders/my-orders` | `OrderService.getMyOrders()` | Individual dashboard purchase history |
| `GET /api/orders/{orderId}/tracking` | `OrderService.getOrderTracking()` | Individual order tracking |
| `GET /api/reviews` | `ReviewService.getReviewsForProduct()` | Product detail reviews list |
| `POST /api/reviews` | `ReviewService.submitReview()` | Product detail review submission |
| `GET /api/individual/analytics` | `IndividualDashboard.fetchAnalytics()` | Individual dashboard charts/KPIs |
| `GET /api/profile` | `ProfileService.getProfile()` | Profile page |
| `PUT /api/profile` | `ProfileService.updateProfile()` | Profile email update |
| `GET /api/users` | `UserService.getUsers()` | Admin user management |
| `PUT /api/users/{id}/suspend` | `UserService.suspendUser()` | Admin suspend/activate user |
| `DELETE /api/users/{id}` | `UserService.deleteUser()` | Admin delete user |
| `GET /api/stores` | `StoreService.getStores()` | Admin store management |
| `PUT /api/stores/{id}/status` | `StoreService.updateStoreStatus()` | Admin open/close store |
| `GET /api/admin/analytics/revenue/total` | `AnalyticsService.getAdminAnalytics()` | Admin dashboard KPI |
| `GET /api/admin/analytics/revenue/monthly` | `AnalyticsService.getAdminAnalytics()` | Admin revenue chart |
| `GET /api/admin/analytics/orders/total` | `AnalyticsService.getAdminAnalytics()` | Admin dashboard KPI |
| `GET /api/admin/analytics/shipments/pending` | `AnalyticsService.getAdminAnalytics()` | Admin dashboard KPI |
| `GET /api/admin/analytics/sales/categories` | `AnalyticsService.getAdminAnalytics()` | Admin category sales chart |
| `GET /api/admin/analytics/users/distribution` | `AnalyticsService.getAdminAnalytics()` | Admin active/users KPI calculation |
| `GET /api/admin/analytics/store-comparison` | `AnalyticsService.getStoreComparison()` | Admin store comparison tab |
| `GET /api/corporate/analytics` | `CorporateDashboard.fetchAnalytics()` | Corporate dashboard KPIs/date range |
| `GET /api/corporate/operations/products` | `CorporateProductService.getProducts()` | Corporate product management |
| `POST /api/corporate/operations/products` | `CorporateProductService.createProduct()` | Corporate add product |
| `PUT /api/corporate/operations/products/{productId}` | `CorporateProductService.updateProduct()` | Corporate edit product |
| `DELETE /api/corporate/operations/products/{productId}` | `CorporateProductService.deleteProduct()` | Corporate delete product |
| `GET /api/corporate/operations/orders` | `CorporateOrderService.getOrders()` | Corporate order management |
| `PUT /api/corporate/operations/orders/{orderId}/status` | `CorporateOrderService.updateOrderStatus()` | Corporate fulfillment/status update |

## Partially Connected

| API area | Status |
| --- | --- |
| `GET /api/reviews` | Connected for product-scoped reviews using `?productId=...`; no general admin/all-reviews screen. |
| `GET /api/corporate/analytics` | Connected and sends `startDate` / `endDate` params; the API list does not document query parameter names. |
| `GET /api/orders/my-orders` | Connected; frontend supports optional `?status=`, but no visible status filter UI is currently present. |
| `POST /api/users` | Admin UI has an "Add User" button, but it is not wired to a modal/service call. |
| `GET /api/products` | Connected with page/category/search/sort query params; Swagger list only names the endpoint, not the supported params. |

## Not Connected

| API | Notes |
| --- | --- |
| `GET /api/users/{id}` | No user detail view. |
| `PUT /api/users/{id}` | No admin edit-user form. |
| `POST /api/users` | No wired admin create-user flow. |
| `GET /api/stores/{id}` | No store detail view. |
| `PUT /api/stores/{id}` | No admin edit-store form. |
| `DELETE /api/stores/{id}` | No admin delete-store action. |
| `POST /api/stores` | No admin create-store form. |
| `GET /api/shipments` | No shipment management page. |
| `GET /api/shipments/{id}` | No direct shipment detail page. |
| `POST /api/shipments` | No create-shipment UI. |
| `PUT /api/shipments/{id}` | No edit-shipment UI. |
| `DELETE /api/shipments/{id}` | No delete-shipment UI. |
| `GET /api/reviews/{id}` | Product detail uses list endpoint instead. |
| `PUT /api/reviews/{id}` | No edit-review UI. |
| `DELETE /api/reviews/{id}` | No delete-review UI. |
| `POST /api/products` | Generic product catalog create is not used; corporate product create uses corporate operations endpoint. |
| `PUT /api/products/{id}` | Generic product catalog update is not used; corporate product update uses corporate operations endpoint. |
| `DELETE /api/products/{id}` | Generic product catalog delete is not used; corporate product delete uses corporate operations endpoint. |
| `GET /api/orders` | No generic admin/all-orders screen. |
| `GET /api/orders/{id}` | No generic order detail view. |
| `POST /api/orders` | Checkout uses `/api/individual/checkout` instead. |
| `PUT /api/orders/{id}` | Corporate status updates use corporate operations endpoint. |
| `DELETE /api/orders/{id}` | No order delete/cancel action. |
| `POST /api/chat/ask` | Chat widget is still a stub and not wired to the API. |
| `GET /api/analytics/corporate` | Frontend uses `/api/corporate/analytics`; this alias is not used. |
| `GET /api/admin/analytics/reviews/sentiment` | No admin sentiment chart/card currently uses it. |

## Frontend Calls Missing From Supplied API List

| Frontend call | Current feature | Needed backend/API action |
| --- | --- | --- |
| `PUT /api/profile/password` | Profile page change password form | Add endpoint or update frontend to use the documented password-change contract. |
| `POST /api/categories` | Admin add category | Swagger/API list only exposes `GET /api/categories`; add endpoint or remove/disable create UI. |
| `PUT /api/categories/{id}` | Admin edit category | Add endpoint or remove/disable edit UI. |
| `DELETE /api/categories/{id}` | Admin delete category | Add endpoint or remove/disable delete UI. |

## Feature-Level Coverage

| Feature | Coverage |
| --- | --- |
| Auth | Connected |
| Product browsing | Connected |
| Product detail | Connected |
| Cart | Frontend-only local state |
| Checkout | Connected |
| Individual dashboard analytics | Connected |
| Individual purchase history | Connected |
| Individual shipment tracking | Connected through order tracking endpoint |
| Product reviews | Connected for list/create |
| Admin analytics dashboard | Mostly connected; sentiment endpoint unused |
| Admin user management | List/suspend/delete connected; create/edit/detail not connected |
| Admin store management | List/status connected; create/edit/delete/detail not connected |
| Admin category management | UI exists, but create/update/delete calls are missing from supplied API list |
| Admin store comparison | Connected |
| Corporate analytics | Connected |
| Corporate product management | Connected through corporate operations endpoints |
| Corporate order management | Connected through corporate operations endpoints |
| Profile management | View/edit connected; change password frontend call missing from supplied API list |
| Chat | Not connected |
