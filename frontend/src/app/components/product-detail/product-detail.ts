import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { Product, ProductService } from '../../services/product.service';
import { Review, ReviewService } from '../../services/review.service';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css'
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  reviews: Review[] = [];
  quantity = 1;
  selectedRating = 0;
  hoverRating = 0;
  reviewComment = '';
  isLoading = true;
  isSubmittingReview = false;
  errorMessage = '';
  cartMessage = '';
  reviewMessage = '';

  readonly stars = [1, 2, 3, 4, 5];

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private reviewService: ReviewService,
    private cartService: CartService,
    public authService: AuthService
  ) {}

  ngOnInit() {
    const productId = this.route.snapshot.paramMap.get('id');
    if (!productId) {
      this.errorMessage = 'Product not found.';
      this.isLoading = false;
      return;
    }

    this.loadProduct(productId);
    this.loadReviews(productId);
  }

  loadProduct(productId: string) {
    this.isLoading = true;
    this.productService.getProductById(productId).subscribe({
      next: (product) => {
        this.product = product;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'We could not load this product.';
        this.isLoading = false;
      }
    });
  }

  loadReviews(productId: string) {
    this.reviewService.getReviewsForProduct(productId).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
      },
      error: () => {
        this.reviews = [];
      }
    });
  }

  updateQuantity(change: number) {
    if (!this.product) return;
    const nextQuantity = this.quantity + change;
    this.quantity = Math.min(Math.max(nextQuantity, 1), this.product.stock || 1);
  }

  addToCart() {
    if (!this.product || this.product.stock <= 0) return;
    this.cartService.addToCart(this.product, this.quantity);
    this.cartMessage = `${this.quantity} item${this.quantity > 1 ? 's' : ''} added to cart.`;
    setTimeout(() => {
      this.cartMessage = '';
    }, 2500);
  }

  submitReview() {
    if (!this.product || this.selectedRating === 0 || !this.reviewComment.trim()) {
      this.reviewMessage = 'Choose a rating and write a short review.';
      return;
    }

    this.isSubmittingReview = true;
    this.reviewMessage = '';

    this.reviewService.submitReview({
      productId: this.product.id,
      rating: this.selectedRating,
      comment: this.reviewComment.trim()
    }).subscribe({
      next: (review) => {
        this.reviews = [review, ...this.reviews];
        this.selectedRating = 0;
        this.hoverRating = 0;
        this.reviewComment = '';
        this.reviewMessage = 'Thanks, your review was submitted.';
        this.isSubmittingReview = false;
      },
      error: (err) => {
        this.reviewMessage = err?.error?.message || 'Only customers who bought this product can review it.';
        this.isSubmittingReview = false;
      }
    });
  }

  getAverageRating(): number {
    if (this.reviews.length === 0) return 0;
    const total = this.reviews.reduce((sum, review) => sum + review.rating, 0);
    return Math.round((total / this.reviews.length) * 10) / 10;
  }
}
