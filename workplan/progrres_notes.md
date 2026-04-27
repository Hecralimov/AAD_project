# Progress Notes

## April 27, 2026

### B19 - UI Polish (frontend-only)

- Added reusable `ToastService` and global `ToastContainerComponent`.
- Mounted toast container at the app root so frontend feedback can appear across pages.
- Replaced blocking HTTP interceptor alerts with non-blocking error toasts for 401, 403, and 500-level failures.
- Added shop page polish:
  - Add-to-cart success toast.
  - Product loading copy with spinner.
  - Product load error state with retry action.
  - Empty product result state with clear filters action.
  - Category load inline error.
  - Smoother product card transitions.
- Added individual dashboard polish:
  - Analytics loading and error states with retry.
  - Orders loading indicator and inline error retry.
  - Improved "No orders yet" empty state.
  - Shipment tracking button spinner and tracking failure toast.
  - Small table/card transition polish.
- No backend changes were made.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B18 - Profile Management Page (frontend-only)

- Added protected `/profile` route using the existing lazy standalone component pattern.
- Added `ProfileComponent` with account summary, editable email form, password change form, loading state, validation, and inline success/error feedback.
- Added `Profile` frontend model and `ProfileService` for profile fetch/update and password change calls.
- Updated auth state to persist and expose the signed-in email from login/register responses, with JWT email fallback for restored sessions.
- Added a navbar profile icon linking signed-in users to `/profile`.
- No backend changes were made. Frontend expects:
  - `GET /api/profile` returning `{ id?, email, roleType, active? }`
  - `PUT /api/profile` accepting `{ email }`
  - `PUT /api/profile/password` accepting `{ currentPassword, newPassword }`
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B17 - Corporate Date Range Picker (frontend-only)

- Added corporate dashboard date range controls on the dashboard tab.
- Added quick range buttons for 7, 30, and 90 days.
- Added custom `From` / `To` date inputs with apply and clear actions.
- Updated corporate analytics fetching to send `startDate` and `endDate` query parameters.
- Added date validation, loading state, and inline error feedback for analytics refresh.
- Added responsive styling for the date range panel.
- No backend changes were made. The frontend sends `startDate` / `endDate`; if runtime expects different names, backend or API contract should align with those params or document the required names.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B16 - Admin Cross-Store Comparison Chart (frontend-only)

- Added `StoreComparison` frontend model for store revenue, order count, and average rating.
- Added `AnalyticsService.getStoreComparison()` wired to the existing `GET /api/admin/analytics/store-comparison` endpoint.
- Added a Compare tab to the admin dashboard sidebar.
- Added grouped bar chart comparing store revenue and order volume with separate chart axes.
- Added summary cards for store count, top revenue store, and best rating.
- Added store ranking table with revenue, orders, and average rating for quick scanning.
- Added comparison-specific responsive styles and loading/empty/error states.
- No backend changes were made. The endpoint was observed in the existing backend; if it fails in runtime, the needed backend contract is `GET /api/admin/analytics/store-comparison` returning `storeId`, `storeName`, `totalRevenue`, `totalOrders`, and `averageRating`.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B15 - Admin Category Management Page (frontend-only)

- Added a Categories tab to the admin dashboard sidebar and header state.
- Added category table with category name, short ID, edit action, and delete action.
- Added add/edit category modal with required name validation and saving state.
- Added delete confirmation modal with deleting state and product reassignment warning text.
- Extended `CategoryService` with frontend methods for `POST /api/categories`, `PUT /api/categories/{id}`, and `DELETE /api/categories/{id}`.
- Added local admin dashboard table, badge, button, modal, loading, empty, and alert styles used by the category management page.
- No backend changes were made. Current backend code observed here only exposes `GET /api/categories`; for full B15 functionality, backend needs category create/update/delete endpoints at the paths above.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B14 - Review Submission UI (frontend-only continuation)

- Improved product detail review submission without touching backend code.
- Added customer-account gating so only logged-in `Individual` users can submit from the UI.
- Added clearer review validation for missing rating/comment and a 500-character review limit.
- Added character counter, review loading state, and success/error message styling.
- Made `ReviewService.getReviewsForProduct()` defensively filter reviews by `productId` on the frontend, so the UI stays product-scoped even if the backend returns a broader review list.
- Kept existing backend contract assumptions only:
  - `GET /api/reviews?productId={id}` should return reviews for the product, or at least a list containing `productId`.
  - `POST /api/reviews` should accept `{ productId, rating, comment }` and enforce purchase validation.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B13 - Corporate Order Management (committed in 27.04.2026 ~20:29)

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
