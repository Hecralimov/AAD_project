# AI Purchase Assistant Workplan

## Summary

Build a safe v1 assistant for NextGenCommerce that can answer user questions, analyze order/product context, and draft cart actions for user confirmation. Keep AI integration behind the Spring Boot backend using a mock provider first, so frontend/backend contracts, security, and environment setup are ready before connecting a real model.

Profit margin analysis is deferred for this phase because the current product schema has product sale price but no cost/COGS field.

## Key Changes

- Replace the dummy `/api/chat/ask` flow with an authenticated assistant API under `/api/assistant`.
- Add backend DTOs for:
  - `AssistantChatRequest`: message and optional conversation/session id.
  - `AssistantChatResponse`: assistant reply, suggested actions, and referenced data summary.
  - `AssistantAction`: action type, product id, quantity, label, and confidence.
- Add an `AssistantService` in Spring Boot that gathers allowed context from existing services:
  - individual user orders from `OrderService`.
  - product/catalog data from the existing product repositories or catalog service.
  - cart draft suggestions only, with no checkout execution.
- Add an `AiProvider` interface with a `MockAiProvider` implementation first.
- Add environment properties:
  - `assistant.provider=mock`
  - future placeholders such as `assistant.model`, `assistant.timeout-ms`, and `assistant.max-context-orders`.
- Secure assistant endpoints for logged-in users only, using the current JWT flow.
- Keep purchase safety strict: the assistant may suggest items and draft cart changes, but checkout remains manual through the existing checkout flow.
- Update the Angular chat widget from placeholder text to a real floating assistant panel available in the app shell.
- Add an `AssistantService` in Angular to call the backend and map suggested actions.
- Let users apply assistant draft cart actions through the existing `CartService`, then review cart/checkout normally.
- Add clear UI states: closed/open, loading, error, empty conversation, assistant response, and suggested cart action buttons.

## Public Interfaces

Backend endpoint:

```text
POST /api/assistant/chat
```

Requires JWT authentication.

Request shape:

```json
{
  "message": "Recommend something from my past orders",
  "conversationId": "optional-session-id"
}
```

Response shape:

```json
{
  "conversationId": "session-id",
  "answer": "Based on your recent orders, you may want to buy...",
  "suggestedActions": [
    {
      "type": "ADD_TO_CART_DRAFT",
      "productId": "product-id",
      "quantity": 1,
      "label": "Add to cart",
      "confidence": 0.82
    }
  ],
  "referencedData": {
    "ordersReviewed": 5,
    "productsReviewed": 12
  }
}
```

Frontend service methods:

```ts
sendMessage(message: string): Observable<AssistantChatResponse>
applySuggestedCartAction(action: AssistantAction): void
```

No real AI key is required for v1. The mock provider should return deterministic responses for demo scenarios like:

- "Recommend something from my past orders."
- "Help me buy this again."
- "What products should I reorder?"

## Implementation Steps

1. Backend foundation
   - Create assistant DTOs.
   - Create `AiProvider` and `MockAiProvider`.
   - Create `AssistantService`.
   - Create `AssistantController` under `/api/assistant`.
   - Add assistant configuration properties.
   - Update security rules so `/api/assistant/**` requires authentication.

2. Backend context gathering
   - Resolve current user from `Principal`.
   - Fetch recent individual orders for the current user.
   - Fetch order item/product details needed for recommendations.
   - Limit context size using `assistant.max-context-orders`.
   - Return only data the logged-in user is allowed to access.

3. Frontend service and models
   - Add assistant TypeScript interfaces.
   - Add Angular `AssistantService`.
   - Reuse existing auth interceptor for JWT.
   - Connect assistant suggested actions to `CartService`.

4. Frontend widget
   - Replace placeholder `chat-widget` content.
   - Add floating open/close launcher.
   - Add message list, input, submit button, loading state, and error state.
   - Render suggested cart action buttons below assistant replies.
   - Mount the widget globally in `app.html`.

5. Safety and UX
   - Do not let assistant call checkout.
   - When an action is applied, add item to cart and show clear confirmation.
   - Send the user to cart for review rather than placing an order.
   - Make unsupported questions return a helpful mock response.

## Test Plan

- Backend unit tests for `AssistantService`:
  - authenticated user context is loaded correctly.
  - mock provider returns valid response shape.
  - assistant cannot create orders or bypass checkout.
  - empty or invalid message returns a validation error.
- Backend controller tests:
  - unauthenticated requests are rejected.
  - authenticated requests are accepted.
  - response includes the expected action schema.
- Frontend tests:
  - widget opens and closes.
  - message submission shows loading and renders response.
  - suggested cart action adds items through `CartService`.
  - backend errors show a friendly error state.
- Manual acceptance:
  - log in as an Individual user.
  - ask assistant for a purchase recommendation.
  - apply a suggested cart item.
  - verify cart updates.
  - verify checkout still requires normal user confirmation.

## Assumptions

- V1 assistant scope is "recommend and draft cart", not automatic purchasing.
- AI implementation starts with a Spring Boot mock adapter, not a separate Python/LangGraph service.
- Profit margin analysis is deferred until product cost/COGS data exists.
- The assistant should be mounted globally near the existing app shell, alongside `app-navbar`, `router-outlet`, and `toast-container`.
