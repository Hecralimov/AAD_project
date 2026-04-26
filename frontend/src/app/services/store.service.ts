import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Store {
  id: string;
  name: string;
  ownerId: string;
  status: string; // "OPEN", "CLOSED", "SUSPENDED"
}

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  private apiUrl = 'http://localhost:8080/api/stores';

  constructor(private http: HttpClient) {}

  getStores(): Observable<Store[]> {
    return this.http.get<Store[]>(this.apiUrl);
  }

  updateStoreStatus(id: string, status: string): Observable<Store> {
    return this.http.put<Store>(`${this.apiUrl}/${id}/status`, { status });
  }
}
