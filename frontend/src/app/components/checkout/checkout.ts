import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CartService, CartItem } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css'
})
export class CheckoutComponent implements OnInit {
  cartItems: CartItem[] = [];
  subtotal = 0;
  shipping = 9.99;

  // Form fields
  fullName = '';
  address = '';
  city = '';
  zipCode = '';
  paymentMethod = 'credit_card';

  isProcessing = false;
  orderPlaced = false;
  orderId = '';
  errorMessage = '';

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cartItems = this.cartService.items();
    this.subtotal = this.cartService.subtotal();

    if (this.cartItems.length === 0 && !this.orderPlaced) {
      this.router.navigate(['/cart']);
    }
  }

  get total() {
    return this.subtotal + this.shipping;
  }

  placeOrder() {
    if (!this.fullName || !this.address || !this.city || !this.zipCode) {
      this.errorMessage = 'Please fill in all shipping fields.';
      return;
    }

    this.isProcessing = true;
    this.errorMessage = '';

    // Group cart items by storeId; for now use first product's storeId
    const storeId = this.cartItems[0]?.product?.storeId || '';

    const request = {
      storeId,
      items: this.cartItems.map(item => ({
        productId: item.product.id,
        quantity: item.quantity
      }))
    };

    this.orderService.checkout(request).subscribe({
      next: (order) => {
        this.isProcessing = false;
        this.orderPlaced = true;
        this.orderId = order.id;
        this.cartService.clearCart();
      },
      error: (err) => {
        this.isProcessing = false;
        this.errorMessage = err.error?.message || err.error || 'Checkout failed. Please try again.';
        console.error('Checkout error', err);
      }
    });
  }
}
