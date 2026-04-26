import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CheckoutRequest {
  storeId: string;
  items: { productId: string; quantity: number }[];
}

export interface OrderResponse {
  id: string;
  userId: string;
  storeId: string;
  status: string;
  grandTotal: number;
  createdAt: string;
}

export interface ShipmentResponse {
  id: string;
  orderId: string;
  warehouse: string;
  mode: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  checkout(request: CheckoutRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.apiUrl}/individual/checkout`, request);
  }

  getMyOrders(status?: string): Observable<OrderResponse[]> {
    const params = status ? `?status=${status}` : '';
    return this.http.get<OrderResponse[]>(`${this.apiUrl}/orders/my-orders${params}`);
  }

  getOrderTracking(orderId: string): Observable<ShipmentResponse> {
    return this.http.get<ShipmentResponse>(`${this.apiUrl}/orders/${orderId}/tracking`);
  }
}
