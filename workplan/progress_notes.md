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
