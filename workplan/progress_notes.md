# Progress Notes

## Task B3: Shopping Cart (Completed & Verified)

- Created `CartService` (`frontend/src/app/services/cart.service.ts`) utilizing Angular Signals (`cartItems`, `totalItems`, `subtotal`) and `localStorage` to persist cart data.
- Updated the `Navbar` component to dynamically show the number of items currently in the cart with a red badge over the cart icon.
- Created the `/cart` page component (`frontend/src/app/components/cart/cart.ts` and HTML/CSS) featuring:
  - List of cart items with images, names, and prices.
  - Quantity controls (+ / -) that automatically adjust the subtotal.
  - Remove button to delete items from the cart.
  - "Proceed to Checkout" placeholder link and dynamic total summary.
- Wired up the "Add to Cart" buttons inside the existing `Shop` component to correctly populate the `CartService`.
- Successfully ran a browser test to verify that the cart state stays synchronized with the navbar and that quantities update correctly.
- Checked off **B3** in `implementation_plan.md`.

## Task B4: Admin User Management Tab (Completed)

- Frontend changes:
  - Created `UserService` (`frontend/src/app/services/user.service.ts`) with methods to get, suspend, and delete users via the Spring Boot API.
  - Redesigned `AdminDashboard` to include tab navigation (`Dashboard` vs `Users` view).
  - Built out the `Users` tab with a responsive data table showing the user's avatar, email, role, and active/suspended status.
  - Added paginated user management controls above and below the users table, including quick navigation to the first and last pages.
  - Wired up action buttons for toggling "Suspend" and deleting a user row.
  - Fixed an Angular compilation issue (AOT compiler restriction) by explicitly importing the `UserService` into the component constructor.
- Backend changes:
  - Discovered that the `UserController` and `UserService` were missing the API endpoint for toggling the user's suspension status.
  - Updated `UserService.java` to cleanly toggle the `isActive` property of a user.
  - Added `PUT /api/users/{id}/suspend` endpoint to `UserController.java`.
  - Realigned frontend and backend data models to correctly map the boolean `active` status and `roleType` fields.
- Checked off **B4** in `implementation_plan.md`.

## Task B5: Admin Store Management Tab (Completed)

- Frontend changes:
  - Created `StoreService` (`frontend/src/app/services/store.service.ts`) for frontend CRUD operations on stores.
  - Modified `AdminDashboard` to include a **Stores** tab alongside the **Users** tab.
  - Implemented the store data table displaying Store ID, Name, Owner ID, and Status (OPEN/CLOSED/SUSPENDED).
  - Integrated the Open/Close Store action button to toggle a store's status.
- Backend changes:
  - Updated `StoreController.java` to include a `PUT /api/stores/{id}/status` endpoint for handling status toggling.
- Checked off **B5** in `implementation_plan.md`.

## Task B6: Lazy Loading (Completed)

- Refactored `frontend/src/app/app.routes.ts` to implement route-level code splitting using Angular's standalone `loadComponent`.
- Removed static imports for all non-essential initial components (Dashboards, Shop, Cart, Login, Register).
- Transformed route definitions from standard `component: X` to `loadComponent: () => import(...).then(m => m.X)`.
- Kept the `Home` component eagerly loaded for immediate first paint.
- Checked off **B6** in `implementation_plan.md`.

## Task B7: Responsive CSS Pass (Completed)

- Reviewed and updated the main CSS files across the application to ensure mobile and tablet responsiveness.
- `home.css`: Stacked the hero section and adjusted grid columns for product displays on smaller screens.
- `shop.css`: Adjusted the main layout grid to stack the sidebar above products on mobile devices, and changed the product grid to show fewer items per row on smaller screens.
- `admin-dashboard.css` applies to all dashboards: re-flowed the layout from a fixed sidebar to a full-width header on mobile and tablet. Stacked KPI cards and charts gracefully.
- `login.css` and `register.css`: Hid the left-side branding panel on mobile screens to allow the form to take up the full width, preventing cramped inputs. Stacked the two-column inputs in the register form.
- `navbar.css`: Updated the navbar to wrap nicely on mobile, centering navigation links below the logo and actions.
- Checked off **B7** in `implementation_plan.md`.

## Task B8: Checkout Page (Completed)

- Created `OrderService` (`frontend/src/app/services/order.service.ts`) with `checkout()`, `getMyOrders()`, and `getOrderTracking()` methods.
- Built `CheckoutComponent` (`frontend/src/app/components/checkout/`) with:
  - Shipping information form (full name, address, city, zip code).
  - Payment method selection (Credit Card, PayPal, Bank Transfer) with radio buttons.
  - Real-time order summary showing cart items, subtotal, shipping, and total.
  - Calls `POST /api/individual/checkout` to submit orders to backend.
  - Success confirmation screen with order ID display.
- Added `/checkout` route to `app.routes.ts` with lazy loading.
- Backend already had `CheckoutController` and `CheckoutService` fully implemented.

## Task B9: Individual Purchase History (Completed)

- Rewrote `IndividualDashboard` to include sidebar navigation with tabbed views.
- Added "My Orders" tab with a responsive data table showing Order ID, Date, Total, and Status.
- Implemented order tracking expansion - clicking "Track" reveals shipment warehouse, mode, and status inline.
- Wired to `GET /api/orders/my-orders` and `GET /api/orders/{orderId}/tracking` backend endpoints.
- Added styled status badges for DELIVERED, PENDING, SHIPPED, PROCESSING, and CANCELLED statuses.

## Task B10: Individual Dashboard Wiring (Completed)

- Replaced mock data in `IndividualDashboard` with real API call to `GET /api/individual/analytics`.
- KPI cards now display actual `totalSpent` and `orderCount` from `IndividualAnalyticsDTO`.
- Status doughnut chart is now dynamically populated from `statusDistribution` data.
- Added `ChangeDetectorRef` for proper zoneless change detection.

## Task B11: Corporate Dashboard Wiring (Completed)

- Replaced mock data in `CorporateDashboard` with real API call to `GET /api/corporate/analytics`.
- KPI cards now display actual `totalRevenue`, `orderCount`, and `lowStockCount` from `CorporateAnalyticsDTO`.
- Added `ChangeDetectorRef` for proper zoneless change detection.
- Checked off **B8-B11** in `implementation_plan.md`.

## April 27, 2026

### App-Wide Delayed Data Rendering and Profile Query Loop Fix

- User-visible bug:
  - Admin Users, Shop/All Products, Profile, Product Detail, and some dashboard sections could show empty or stale UI after data was fetched.
  - The data sometimes appeared only after another click, changing sections, or waiting several seconds.
  - Backend logs repeatedly showed `select ... from customer_profiles ... where user_id=?`, suggesting profile data was being touched too often.
- Root cause:
  - The frontend uses `provideZonelessChangeDetection()`.
  - Many components updated normal class fields inside HTTP `subscribe()` callbacks, such as `this.users = users`, `this.products = response.content`, or `this.profile = profile`.
  - In zoneless Angular, those async updates do not automatically repaint the template.
  - A previous global `changeDetectionInterceptor` tried to force `ApplicationRef.tick()` after every HTTP request, but that was too broad and could make unrelated requests trigger full-app repaint work.
  - On the backend, `User.profile` was a `@OneToOne` relation. One-to-one relations default to eager loading, so ordinary user loads could also trigger `customer_profiles` queries even when profile data was not needed.
- Frontend fix:
  - Removed the global `changeDetectionInterceptor` from the HTTP interceptor chain.
  - Added targeted `ChangeDetectorRef.detectChanges()` calls after async state updates in:
    - Admin dashboard user/store/category/comparison/analytics paths.
    - Corporate dashboard analytics/products/categories/orders paths.
    - Individual dashboard analytics/orders/tracking paths.
    - Shop product/category loading paths.
    - Product detail product/review/review-submit paths.
    - Profile load/update/password-change paths.
    - Checkout, login, and register async paths.
  - Kept zoneless mode, but made each affected component explicitly repaint after its own async work finishes.
- Backend fix:
  - Marked `User.profile` as `fetch = FetchType.LAZY`.
  - Added `@JsonIgnore` to `User.profile` so JSON serialization does not walk into the profile relation.
  - Changed `/api/profile` to return `CustomerProfileDTO` instead of returning a raw JPA `CustomerProfile` entity.
  - Expanded `CustomerProfileDTO` to include account-facing fields used by the frontend: `email`, `roleType`, and `active`.
  - Kept profile responses stable without exposing lazy entity graphs.
- Related admin user fix:
  - Updated `/api/users` to include the `active` field expected by the admin users table.
  - Fixed `UserService.suspendUser(id, suspend)` so `suspend = true` sets `isActive = false`, and `suspend = false` reactivates the user.
- Verification notes:
  - No frontend/backend/database services were started during this pass.
  - Static checks were run only; `git diff --check` passed.
  - Runtime confirmation still requires restarting/reloading the live frontend and backend so the source changes are actually active.

### B2 - Product Detail Page

- Added lazy route `/products/:id` in `frontend/src/app/app.routes.ts`.
- Added `ProductDetailComponent` with product image, SKU, description, price, stock, quantity controls, average rating, and add-to-cart flow.
- Extended `ProductService` with `getProductById(id)`.
- Added backend support for `GET /api/products/{id}` so the detail page can load a single product directly.

### B12 - Corporate Product Management

- Fixed `CorporateProductService.getProducts()` to read the backend paged response from `/api/corporate/operations/products` and expose the product list to the dashboard.
- Completed corporate-scoped backend CRUD for products:
  - `POST /api/corporate/operations/products`
  - `PUT /api/corporate/operations/products/{productId}`
  - `DELETE /api/corporate/operations/products/{productId}`
- Added store ownership checks for product update/delete so corporate users cannot modify products from another store.
- Improved the corporate Products tab with form validation, saving/deleting states, and inline success/error feedback.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### Corporate Auth and Dashboard Access Fix

- Fixed corporate registration to normalize email/role, set `is_active = true`, reject duplicate emails, and create a default store for new corporate users.
- Updated user enabled checks so existing `is_active = NULL` users are not treated as disabled.
- Adjusted email lookup to avoid duplicate-email crashes and prefer the recent nullable test rows already present in the local database.
- Added `/api/corporate/analytics` as an alias for the existing corporate analytics controller path.
- Fixed the corporate dashboard KPI binding from `orderCount` to the backend DTO field `totalOrders`.
- Corporate analytics/products now create a fallback store if an existing corporate user has no store row yet.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

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

### B14 - Review Submission UI

- Added `ReviewService` for `GET /api/reviews?productId=...` and `POST /api/reviews`.
- Added star rating picker, review textarea, submit state, and user feedback on the product detail page.
- Added review list rendering with rating stars and empty state.
- Extended backend `GET /api/reviews` with optional `productId` filtering, reusing the existing `ReviewService.getReviewsForProduct`.
- Improved product detail review submission without touching backend code.
- Added customer-account gating so only logged-in `Individual` users can submit from the UI.
- Added clearer review validation for missing rating/comment and a 500-character review limit.
- Added character counter, review loading state, and success/error message styling.
- Made `ReviewService.getReviewsForProduct()` defensively filter reviews by `productId` on the frontend, so the UI stays product-scoped even if the backend returns a broader review list.
- Kept existing backend contract assumptions only:
  - `GET /api/reviews?productId={id}` should return reviews for the product, or at least a list containing `productId`.
  - `POST /api/reviews` should accept `{ productId, rating, comment }` and enforce purchase validation.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B15 - Admin Category Management Page

- Added a Categories tab to the admin dashboard sidebar and header state.
- Added category table with category name, short ID, edit action, and delete action.
- Added add/edit category modal with required name validation and saving state.
- Added delete confirmation modal with deleting state and product reassignment warning text.
- Extended `CategoryService` with frontend methods for `POST /api/categories`, `PUT /api/categories/{id}`, and `DELETE /api/categories/{id}`.
- Added local admin dashboard table, badge, button, modal, loading, empty, and alert styles used by the category management page.
- No backend changes were made. Current backend code observed here only exposes `GET /api/categories`; for full B15 functionality, backend needs category create/update/delete endpoints at the paths above.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B16 - Admin Cross-Store Comparison Chart

- Added `StoreComparison` frontend model for store revenue, order count, and average rating.
- Added `AnalyticsService.getStoreComparison()` wired to the existing `GET /api/admin/analytics/store-comparison` endpoint.
- Added a Compare tab to the admin dashboard sidebar.
- Added grouped bar chart comparing store revenue and order volume with separate chart axes.
- Added summary cards for store count, top revenue store, and best rating.
- Added store ranking table with revenue, orders, and average rating for quick scanning.
- Added comparison-specific responsive styles and loading/empty/error states.
- No backend changes were made. The endpoint was observed in the existing backend; if it fails in runtime, the needed backend contract is `GET /api/admin/analytics/store-comparison` returning `storeId`, `storeName`, `totalRevenue`, `totalOrders`, and `averageRating`.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B17 - Corporate Date Range Picker

- Added corporate dashboard date range controls on the dashboard tab.
- Added quick range buttons for 7, 30, and 90 days.
- Added custom `From` / `To` date inputs with apply and clear actions.
- Updated corporate analytics fetching to send `startDate` and `endDate` query parameters.
- Added date validation, loading state, and inline error feedback for analytics refresh.
- Added responsive styling for the date range panel.
- No backend changes were made. The frontend sends `startDate` / `endDate`; if runtime expects different names, backend or API contract should align with those params or document the required names.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### B18 - Profile Management Page

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

### B19 - UI Polish

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

### Frontend Click/Refresh Delay Fix

- Root cause found: app uses `provideZonelessChangeDetection()`, while many components update plain class fields inside HTTP `subscribe()` callbacks.
- In zoneless mode, those async updates do not automatically trigger a repaint, so data appears only after a later user click.
- Added `changeDetectionInterceptor` to schedule `ApplicationRef.tick()` after HTTP requests finish.
- Registered the interceptor with the existing HTTP interceptor chain.
- Updated shop filter navigation so clicking the same active filter/query can still reload products when Angular Router skips same-URL navigation.
- No backend changes were made.
- Per instruction, did not run frontend/backend/database servers and did not run final tests/builds.

### Developer Workflow Makefile

- Added root `Makefile` as a short command layer over `docker compose`.
- Used for quickly starting/stopping services, restarting the database/backend/frontend, rebuilding containers, following logs, and checking service status.
- Main shortcuts include `make up`, `make down`, `make restart`, `make rebuild`, `make logs`, and `make ps`.

### Verification

- `npm.cmd run build` passed for the frontend.
- `cmd /c mvnw.cmd -DskipTests package` passed for backend compile/package.
- `cmd /c mvnw.cmd test` did not complete because the Spring context test could not connect to the configured database (`Communications link failure`), after Java compilation succeeded.
