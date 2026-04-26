import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CorporateProduct {
  id?: string;
  name: string;
  sku: string;
  unitPrice: number;
  stock: number;
  categoryId: string;
  storeId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CorporateProductService {
  private apiUrl = 'http://localhost:8080/api/corporate/products';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<CorporateProduct[]> {
    return this.http.get<CorporateProduct[]>(this.apiUrl);
  }

  createProduct(product: CorporateProduct): Observable<CorporateProduct> {
    return this.http.post<CorporateProduct>(this.apiUrl, product);
  }

  updateProduct(id: string, product: CorporateProduct): Observable<CorporateProduct> {
    return this.http.put<CorporateProduct>(`${this.apiUrl}/${id}`, product);
  }

  deleteProduct(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
