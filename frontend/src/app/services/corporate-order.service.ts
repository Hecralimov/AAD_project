import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CorporateOrder {
  id: string;
  userId: string;
  storeId: string;
  status: string;
  grandTotal: number;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class CorporateOrderService {
  private apiUrl = 'http://localhost:8080/api/corporate/operations/orders';

  constructor(private http: HttpClient) {}

  getOrders(): Observable<CorporateOrder[]> {
    return this.http.get<CorporateOrder[]>(this.apiUrl);
  }

  updateOrderStatus(orderId: string, status: string): Observable<CorporateOrder> {
    return this.http.put<CorporateOrder>(`${this.apiUrl}/${orderId}/status`, { status });
  }
}
