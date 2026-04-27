import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface CorporateProduct {
  id?: string;
  name: string;
  sku: string;
  unitPrice: number;
  stock: number;
  categoryId: string;
  storeId?: string;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class CorporateProductService {
  private apiUrl = 'http://localhost:8080/api/corporate/operations/products';

  constructor(private http: HttpClient) { }

  getProducts(): Observable<CorporateProduct[]> {
    return this.http.get<PageResponse<CorporateProduct>>(this.apiUrl).pipe(
      map(response => response.content)
    );
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
