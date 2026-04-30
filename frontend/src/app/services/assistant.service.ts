import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AssistantChatRequest, AssistantChatResponse, AssistantAction } from '../models/assistant.model';
import { CartService } from './cart.service';
import { Product } from './product.service';

@Injectable({
  providedIn: 'root'
})
export class AssistantService {
  private http = inject(HttpClient);
  private cartService = inject(CartService);
  private apiUrl = 'http://localhost:8080/api/assistant';

  sendMessage(message: string, conversationId?: string): Observable<AssistantChatResponse> {
    const request: AssistantChatRequest = { message, conversationId };
    return this.http.post<AssistantChatResponse>(`${this.apiUrl}/chat`, request);
  }

  applySuggestedCartAction(action: AssistantAction, referencedProducts: Product[]): boolean {
    if (action.type !== 'ADD_TO_CART_DRAFT') {
      return false;
    }

    const product = referencedProducts.find(p => String(p.id) === String(action.productId));

    if (product) {
      console.log('Found matching product:', product);

      if (product.id && product.unitPrice !== undefined) {
        try {
          this.cartService.addToCart(product, action.quantity);
          console.log('Successfully added to cart!');
          return true;
        } catch (error) {
          console.error('CartService threw an error:', error);
          return false;
        }
      } else {
        console.error('AI Error: Product object is missing required fields (e.g., price)', product);
        return false;
      }

    } else {
      console.error('AI Error: Could not find product with ID', action.productId, 'in the provided references.');
      return false;
    }
  }
}
