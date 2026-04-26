import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class CartComponent {
  constructor(public cartService: CartService) {}

  updateQuantity(productId: string, event: Event) {
    const input = event.target as HTMLInputElement;
    const value = parseInt(input.value, 10);
    if (!isNaN(value)) {
      this.cartService.updateQuantity(productId, value);
    }
  }

  incrementQuantity(productId: string, currentQuantity: number) {
    this.cartService.updateQuantity(productId, currentQuantity + 1);
  }

  decrementQuantity(productId: string, currentQuantity: number) {
    if (currentQuantity > 1) {
      this.cartService.updateQuantity(productId, currentQuantity - 1);
    } else {
      this.cartService.removeFromCart(productId);
    }
  }

  removeItem(productId: string) {
    this.cartService.removeFromCart(productId);
  }
}
