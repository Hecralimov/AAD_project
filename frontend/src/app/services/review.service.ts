import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Review {
  id: string;
  userId: string;
  productId: string;
  rating: number;
  comment: string;
  sentiment?: string;
}

export interface ReviewRequest {
  productId: string;
  rating: number;
  comment: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  getReviewsForProduct(productId: string): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}?productId=${productId}`).pipe(
      map(reviews => reviews.filter(review => review.productId === productId))
    );
  }

  submitReview(request: ReviewRequest): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, request);
  }
}
