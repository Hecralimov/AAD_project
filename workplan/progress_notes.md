# Recent Progress Notes

## Task B3: Shopping Cart (Completed & Verified)
- Created `CartService` (`frontend/src/app/services/cart.service.ts`) utilizing Angular Signals (`cartItems`, `totalItems`, `subtotal`) and `localStorage` to persist cart data.
- Updated the `Navbar` component to dynamically show the number of items currently in the cart with a red badge over the cart icon.
- Created the `/cart` page component (`frontend/src/app/components/cart/cart.ts` & HTML/CSS) featuring:
  - List of cart items with images, names, and prices.
  - Quantity controls (+ / -) that automatically adjust the subtotal.
  - Remove button to delete items from the cart.
  - "Proceed to Checkout" placeholder link and dynamic total summary.
- Wired up the "Add to Cart" buttons inside the existing `Shop` component to correctly populate the `CartService`.
- Successfully ran a browser test to verify that the cart state stays synchronized with the navbar and that quantities update perfectly.
- Checked off **B3** in `implementation_plan.md`.

## Task B4: Admin User Management Tab (Completed)
- **Frontend changes**: 
  - Created `UserService` (`frontend/src/app/services/user.service.ts`) with methods to get, suspend, and delete users via the Spring Boot API.
  - Redesigned `AdminDashboard` to include tab navigation (`Dashboard` vs `Users` view).
  - Built out the `Users` tab with a responsive data table showing the user's avatar, email, role, and active/suspended status.
  - Wired up action buttons for toggling "Suspend" and deleting a user row.
  - Fixed an Angular compilation issue (AOT compiler restriction) by explicitly importing the `UserService` into the component constructor.
- **Backend changes**:
  - Discovered that the `UserController` and `UserService` were missing the API endpoint for toggling the user's suspension status.
  - Updated `UserService.java` to cleanly toggle the `isActive` property of a user.
  - Added `PUT /api/users/{id}/suspend` endpoint to `UserController.java`.
  - Realigned frontend and backend data models to correctly map the boolean `active` status and `roleType` fields.
- Checked off **B4** in `implementation_plan.md`.

*Currently stopped at testing the B4 user flow (via registration and dashboard access) due to test cancellation, but the code implementation is completed.*

## Task B5: Admin Store Management Tab (Completed)
- **Frontend changes**:
  - Created `StoreService` (`frontend/src/app/services/store.service.ts`) for frontend CRUD operations on stores.
  - Modified `AdminDashboard` to include a **Stores** tab alongside the **Users** tab.
  - Implemented the store data table displaying Store ID, Name, Owner ID, and Status (OPEN/CLOSED/SUSPENDED).
  - Integrated the 'Open/Close Store' action button to toggle a store's status.
- **Backend changes**:
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
- **`home.css`**: Stacked the hero section and adjusted grid columns for product displays on smaller screens.
- **`shop.css`**: Adjusted the main layout grid to stack the sidebar above products on mobile devices, and changed the product grid to show fewer items per row on smaller screens.
- **`admin-dashboard.css`** (applies to all dashboards): Re-flowed the layout from a fixed sidebar to a full-width header on mobile and tablet. Stacked KPI cards and charts gracefully.
- **`login.css` & `register.css`**: Hid the left-side branding panel on mobile screens to allow the form to take up the full width, preventing cramped inputs. Stacked the two-column inputs in the register form.
- **`navbar.css`**: Updated the navbar to wrap nicely on mobile, centering navigation links below the logo and actions.
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
- Implemented order tracking expansion — clicking "Track" reveals shipment warehouse, mode, and status inline.
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
