import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  featuredProducts = [
    { id: 1, name: 'Wireless Noise-Cancelling Headphones', price: 299.99, category: 'Electronics', image: '🎧', tag: 'Bestseller' },
    { id: 2, name: 'Minimalist Smartwatch', price: 199.50, category: 'Accessories', image: '⌚', tag: 'New' },
    { id: 3, name: 'Eco-Friendly Backpack', price: 89.00, category: 'Fashion', image: '🎒', tag: 'Trending' },
    { id: 4, name: 'Mechanical Keyboard', price: 149.99, category: 'Gaming', image: '⌨️', tag: 'Sale' }
  ];
}
