# Progress Notes

## April 27, 2026

### B13 - Corporate Order Management
- Added `CorporateOrderService` for the existing backend endpoints:
  - `GET /api/corporate/operations/orders`
  - `PUT /api/corporate/operations/orders/{orderId}/status`
- Added an Orders tab to the corporate dashboard sidebar.
- Added incoming orders table with order ID, customer ID, total, created date, current status, and status selector.
- Added status update action with disabled state while unchanged/updating, plus inline success/error feedback.
- Added empty/loading states and order status badge styling.
- No backend changes were made for B13. Existing endpoints were used as-is; if those endpoints fail, the needed backend work is to expose corporate-scoped order list and status update at the paths above.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### Corporate Auth and Dashboard Access Fix
- Fixed corporate registration to normalize email/role, set `is_active = true`, reject duplicate emails, and create a default store for new corporate users.
- Updated user enabled checks so existing `is_active = NULL` users are not treated as disabled.
- Adjusted email lookup to avoid duplicate-email crashes and prefer the recent nullable test rows already present in the local database.
- Added `/api/corporate/analytics` as an alias for the existing corporate analytics controller path.
- Fixed the corporate dashboard KPI binding from `orderCount` to the backend DTO field `totalOrders`.
- Corporate analytics/products now create a fallback store if an existing corporate user has no store row yet.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B12 - Corporate Product Management
- Fixed `CorporateProductService.getProducts()` to read the backend paged response from `/api/corporate/operations/products` and expose the product list to the dashboard.
- Completed corporate-scoped backend CRUD for products:
  - `POST /api/corporate/operations/products`
  - `PUT /api/corporate/operations/products/{productId}`
  - `DELETE /api/corporate/operations/products/{productId}`
- Added store ownership checks for product update/delete so corporate users cannot modify products from another store.
- Improved the corporate Products tab with form validation, saving/deleting states, and inline success/error feedback.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B2 - Product Detail Page
- Added lazy route `/products/:id` in `frontend/src/app/app.routes.ts`.
- Added `ProductDetailComponent` with product image, SKU, description, price, stock, quantity controls, average rating, and add-to-cart flow.
- Extended `ProductService` with `getProductById(id)`.
- Added backend support for `GET /api/products/{id}` so the detail page can load a single product directly.

### B14 - Review Submission UI
- Added `ReviewService` for `GET /api/reviews?productId=...` and `POST /api/reviews`.
- Added star rating picker, review textarea, submit state, and user feedback on the product detail page.
- Added review list rendering with rating stars and empty state.
- Extended backend `GET /api/reviews` with optional `productId` filtering, reusing the existing `ReviewService.getReviewsForProduct`.

### Verification
- `npm.cmd run build` passed for the frontend.
- `cmd /c mvnw.cmd -DskipTests package` passed for backend compile/package.
- `cmd /c mvnw.cmd test` did not complete because the Spring context test could not connect to the configured database (`Communications link failure`), after Java compilation succeeded.
