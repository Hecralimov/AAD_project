import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id: string;
  name: string;
  description: string;
  unitPrice: number;
  imageUrl: string;
  sku: string;
  categoryId: string;
  storeId: string;
  stock: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  getProductById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  getProducts(page: number = 0, size: number = 12, categoryId?: string, search?: string, sortBy?: string, sortDir?: string): Observable<PageResponse<Product>> {
    let url = `${this.apiUrl}?page=${page}&size=${size}`;
    if (categoryId) {
      url += `&categoryId=${categoryId}`;
    }
    if (search) {
      url += `&search=${encodeURIComponent(search)}`;
    }
    if (sortBy) {
      url += `&sortBy=${sortBy}`;
    }
    if (sortDir) {
      url += `&sortDir=${sortDir}`;
    }
    return this.http.get<PageResponse<Product>>(url);
  }
}
