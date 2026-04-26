# AAD Project — 7-Day Sprint Workplan (2 Developers)

> **Deadline:** 7 days (April 23 – April 29)
> **Developer A:** Backend (Spring Boot) + Database
> **Developer B:** Frontend (Angular)
> **AI Chatbot:** OpenAI `gpt-4o-mini` — lowest priority, minimal viable version only if time permits

---

## Current State: ~35% Complete

| Area | Score | What Exists |
|------|-------|-------------|
| Database / ETL | ██████████ 100% | All 6 Kaggle datasets loaded into MySQL via `init.sql`. All core tables exist. ✅ **No rework needed.** |
| Backend APIs | ██████░░░░ 30% | JWT auth, Product CRUD, Order CRUD (partial), Admin analytics (6 endpoints). Missing: service layer, most entity CRUDs, corporate/individual endpoints, error handling, Swagger. |
| Frontend UI | ██████░░░░ 30% | Auth flow, 3 dashboards (admin wired, corporate/individual use mock data), home page, navbar. Missing: product browsing, cart, checkout, user/store management, real data for 2 dashboards. |
| Admin Features | ██████░░░░ 30% | Analytics dashboard with real data. Missing: user management, store management, category management. |
| Corporate Features | ███░░░░░░░ 15% | Dashboard shell with mock data. Missing: all actual features. |
| Individual Features | ██░░░░░░░░ 10% | Dashboard shell with mock data. Missing: product browsing, cart, checkout, order history, reviews. |
| AI Chatbot | █░░░░░░░░░ 5% | Empty stub on both ends. |

---

## What We're Cutting to Fit 7 Days

> [!WARNING]
> These are deprioritized — not in scope unless time allows after Day 5:

| Feature | Reason |
|---------|--------|
| Audit logs & activity monitoring | Nice-to-have, not core |
| PostgreSQL dual-database support | Configurable but not demo-critical |
| Customer segmentation & behavior analysis | Complex analytics, low demo impact |
| System configuration page | Not visible in demo |
| Refresh token rotation | JWT works fine for demo without it |
| CSV export for purchase history | Minor feature |
| Dynamic/configurable dashboard widgets | Over-engineering for deadline |

---

## Sprint Plan

### 📅 Day 1-2: Foundation & Core APIs

> [!IMPORTANT]
> Goal: Backend has all the endpoints the frontend needs. Frontend has the core shopping flow skeleton.

#### Developer A — Backend (Day 1-2)

| # | Task | Priority | Est. |
|---|------|----------|------|
| A1 | **Add Service Layer** — Create `UserService`, `ProductService`, `OrderService`, `StoreService`, `ReviewService`, `ShipmentService`, `CategoryService`. Refactor controllers to use services. | 🔴 High | 3h |
| A2 | **Complete Entity CRUDs** — Full REST endpoints for: Users (list/get/update/delete/suspend), Stores (list/get/create/update/open/close), Reviews (list/create/get by product), Shipments (list/get/by-order), Categories (list/create/update/delete) | 🔴 High | 4h |
| A3 | **Corporate Analytics Endpoint** — `GET /api/corporate/analytics` scoped to authenticated user's store. Returns: store revenue, order count, top products, low-stock count. | 🔴 High | 2h |
| A4 | **Individual Analytics Endpoint** — `GET /api/individual/analytics` scoped to authenticated user. Returns: total spent, order count, monthly spending, order status breakdown. | 🔴 High | 2h |
| A5 | **Global Exception Handler** — `@ControllerAdvice` with handlers for 400/401/403/404/500. Consistent JSON error response format. | 🟡 Med | 1h |
| A6 | **Product Search & Filter** — Add `?search=`, `?categoryId=`, `?sortBy=price&sortDir=asc` params to `GET /api/products`. | 🔴 High | 2h |
| A7 | **Add Database Indexes** — On `orders.user_id`, `orders.store_id`, `products.store_id`, `products.category_id`, `order_items.order_id`, `reviews.product_id`, `shipments.order_id`. | 🟡 Med | 30m |

#### Developer B — Frontend (Day 1-2)

| # | Task | Priority | Est. |
|---|------|----------|------|
| B1 | **Product Browsing Page** — `/products` route: grid of product cards loaded from `GET /api/products`. Search bar, category filter dropdown, sort selector, pagination. | 🔴 High | 4h |
| B2 | ✅ **Product Detail Page** — `/products/:id` route: product info, price, reviews list, "Add to Cart" button. | 🔴 High | 2h |
| B3 | ✅ **Shopping Cart** — `CartService` using Angular signals. Cart icon in navbar with item count badge. Cart page/drawer: item list, quantity +/-, remove, subtotal, "Proceed to Checkout". | 🔴 High | 3h |
| B4 | ✅ **Admin: User Management Tab** — New tab in admin dashboard: table of users with role badge, email. Actions: suspend (toggle), delete. Calls backend CRUD. | 🔴 High | 3h |
| B5 | **Admin: Store Management Tab** — New tab in admin dashboard: table of stores with status badge, owner. Actions: open/close store. | 🔴 High | 2h |
| B6 | **Lazy Loading** — Convert dashboard routes to `loadComponent: () => import(...)` for code splitting. | 🟢 Low | 30m |
| B7 | **Responsive CSS Pass** — Add media queries to all pages for tablet (768px) and mobile (480px) breakpoints. | 🟡 Med | 2h |

---

### 📅 Day 3-4: E-Commerce Flow & Dashboard Wiring

> [!IMPORTANT]
> Goal: Complete the shopping flow end-to-end. Wire all 3 dashboards to real backend data.

#### Developer A — Backend (Day 3-4)

| # | Task | Priority | Est. |
|---|------|----------|------|
| A8 | **Checkout Endpoint** — `POST /api/orders/checkout` accepts `{items: [{productId, quantity}], paymentMethod}`. Creates Order + OrderItems, sets status "Pending", creates Shipment with status "PROCESSING". | 🔴 High | 3h |
| A9 | **My Orders Endpoint** — `GET /api/orders/my-orders` returns orders for the authenticated user. Support `?status=` filter. | 🔴 High | 1.5h |
| A10 | **Shipment by Order** — `GET /api/shipments/by-order/{orderId}` returns shipment tracking info. | 🔴 High | 1h |
| A11 | **Review Submission** — `POST /api/reviews` — validates user purchased the product, saves review with star rating + optional text. | 🟡 Med | 1.5h |
| A12 | **Corporate: Store Products** — `GET /api/corporate/products` returns products for the authenticated user's store. | 🔴 High | 1h |
| A13 | **Corporate: Store Orders** — `GET /api/corporate/orders` returns orders for the authenticated user's store with status filter. `PUT /api/corporate/orders/{id}/status` for fulfillment. | 🔴 High | 2h |
| A14 | **Swagger/OpenAPI Setup** — Add `springdoc-openapi-starter-webmvc-ui` dependency. Auto-generates docs at `/swagger-ui.html`. | 🟡 Med | 30m |

#### Developer B — Frontend (Day 3-4)

| # | Task | Priority | Est. |
|---|------|----------|------|
| B8 | **Checkout Page** — `/checkout` route: order summary from cart → shipping info form → payment method radio buttons → "Place Order" button. Call checkout API, show confirmation. | 🔴 High | 3h |
| B9 | **Individual: Purchase History** — `/individual/orders` or tab in dashboard: table of past orders with status badges, date, total. Click to see order detail + shipment status. | 🔴 High | 3h |
| B10 | **Individual: Wire Dashboard** — Replace mock data with calls to `GET /api/individual/analytics`. Spending chart + status doughnut from real data. | 🔴 High | 1.5h |
| B11 | **Corporate: Wire Dashboard** — Replace mock data with calls to `GET /api/corporate/analytics`. Revenue chart + inventory chart from real data. | 🔴 High | 1.5h |
| B12 | **Corporate: Product Management** — Tab in corporate dashboard: table of their products, add/edit/delete modals. Wire to backend CRUD. | 🔴 High | 3h |
| B13 | **Corporate: Order Management** — Tab in corporate dashboard: incoming orders table, status update actions (mark shipped/delivered). | 🔴 High | 2h |
| B14 | **Review Submission UI** — On product detail page: star rating picker + text input. List existing reviews below product. | 🟡 Med | 2h |

---

### 📅 Day 5-6: Polish, Admin Completion & Documentation

#### Developer A — Backend (Day 5-6)

| # | Task | Priority | Est. |
|---|------|----------|------|
| A15 | **Admin: Category CRUD** — Full CRUD endpoints for categories. | 🟡 Med | 1.5h |
| A16 | **Admin: Cross-Store Comparison** — `GET /api/admin/analytics/store-comparison` returns revenue, orders, avg rating per store. | 🟡 Med | 2h |
| A17 | **Corporate: Revenue by Date Range** — Add `?from=&to=` params to corporate analytics for date-filtered revenue data. | 🟡 Med | 1.5h |
| A18 | **Add `CustomerProfiles` entity** — Model + repo + basic endpoint. Link to Users table. | 🟡 Med | 1.5h |
| A19 | **Fix & Test all endpoints** — Integration testing, fix bugs found by Developer B, verify data flows. | 🔴 High | 3h |
| A20 | **ETL Documentation** — Write field mapping document showing how 6 Kaggle sources map to the DB schema. | 🟡 Med | 2h |

#### Developer B — Frontend (Day 5-6)

| # | Task | Priority | Est. |
|---|------|----------|------|
| B15 | **Admin: Category Management Page** — Table of categories with add/edit/delete. | 🟡 Med | 2h |
| B16 | **Admin: Cross-Store Comparison Chart** — Bar chart comparing stores by revenue and orders. | 🟡 Med | 2h |
| B17 | **Corporate: Date Range Picker** — Add date range selector to corporate dashboard, re-fetch data on change. | 🟡 Med | 1.5h |
| B18 | **Profile Management Page** — `/profile` route: view/edit email, change password. | 🟡 Med | 2h |
| B19 | **UI Polish** — Smooth transitions, loading spinners on API calls, empty states ("No orders yet"), error toasts. | 🔴 High | 3h |
| B20 | **Fix & Test all pages** — Cross-browser test, fix layout bugs, verify all flows end-to-end. | 🔴 High | 2h |

---

### 📅 Day 7: AI Chatbot (Minimal) + Presentation Prep

> [!NOTE]
> This is a **minimal viable chatbot**. The goal is to have something demo-able, not a full multi-agent system.

#### Developer A — Backend + Python (Day 7)

| # | Task | Priority | Est. |
|---|------|----------|------|
| A21 | **Python LangGraph Service** — Create `ai-chatbot/` directory. Simple FastAPI app with LangGraph state machine. Connect to MySQL directly for query execution. Use OpenAI `gpt-4o-mini`. | 🟡 Med | 3h |
| A22 | **Guardrails + SQL Agent** — Basic guardrails (greeting detection, scope check). SQL generation from natural language using schema context. | 🟡 Med | 2h |
| A23 | **Connect Spring Boot → Python** — Update `ChatController` to forward `/api/chat/ask` requests to the Python service via HTTP. Pass user role for scoping. | 🟡 Med | 1h |
| A24 | **Technical Report Draft** — Write architecture decisions, ER diagram description, API overview, chatbot architecture. Target 10 pages. | 🔴 High | 2h |

#### Developer B — Frontend (Day 7)

| # | Task | Priority | Est. |
|---|------|----------|------|
| B21 | **Chat UI** — Floating chat button (bottom-right) → expandable chat panel. Message list with user/bot bubbles, text input, send button. Wire to `/api/chat/ask`. | 🟡 Med | 3h |
| B22 | **Chat in all dashboards** — Include `<app-chat-widget>` in admin, corporate, and individual dashboards. Pass user role context. | 🟡 Med | 1h |
| B23 | **Final Visual Polish** — Ensure all pages look presentation-ready. Fix any remaining CSS issues. | 🔴 High | 2h |
| B24 | **Prepare Demo Script** — Write a 10-minute demo walkthrough: login as each role, show key features, demonstrate chatbot. | 🔴 High | 1h |

---

## Daily Summary View

| Day | Developer A (Backend) | Developer B (Frontend) |
|-----|----------------------|----------------------|
| **1** | Service layer + Entity CRUDs | Product browsing + detail page |
| **2** | Corporate/Individual analytics endpoints + search/filter | Shopping cart + Admin user/store management |
| **3** | Checkout + My Orders + Shipment tracking | Checkout page + Individual purchase history |
| **4** | Reviews + Corporate endpoints + Swagger | Wire dashboards + Corporate product/order management |
| **5** | Admin endpoints (categories, store comparison) | Admin category + store comparison UI + profile page |
| **6** | CustomerProfiles + testing + ETL docs | UI polish + testing + bug fixes |
| **7** | AI chatbot (Python) + technical report | Chat UI + final polish + demo script |

---

## Presentation Checklist (Day 7)

- [ ] Demo as **Admin**: login → dashboard (KPIs, charts) → manage users → manage stores → manage categories → cross-store comparison → AI chatbot
- [ ] Demo as **Corporate**: login → dashboard (store KPIs) → manage products → manage orders (fulfill) → sales analytics with date range → AI chatbot
- [ ] Demo as **Individual**: login → browse products → search/filter → add to cart → checkout → order history → order tracking → write review → spending dashboard → AI chatbot
- [ ] Show **Swagger UI** at `/swagger-ui.html`
- [ ] Explain architecture: Angular ↔ Spring Boot ↔ MySQL ↔ Python LangGraph
- [ ] Show ER diagram
- [ ] Technical report printed/ready

---

## Architecture At-A-Glance

```
┌──────────────┐     HTTP/REST      ┌──────────────────┐     JPA      ┌─────────┐
│   Angular    │ ◄──────────────► │   Spring Boot    │ ◄──────────► │  MySQL  │
│  (Port 4200) │                    │   (Port 8080)    │              │  (3306) │
└──────┬───────┘                    └────────┬─────────┘              └─────────┘
       │                                     │
       │         ┌───────────────┐           │  HTTP (forward)
       └────────►│  Chat Widget  │───────────┼──────────────────►┌──────────────┐
                 └───────────────┘           │                   │ Python/      │
                                             │                   │ LangGraph    │
                                             │                   │ + OpenAI     │
                                             └──────────────────►│ (Port 5000)  │
                                                                 └──────────────┘
```
