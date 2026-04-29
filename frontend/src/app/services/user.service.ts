import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: string;
  email: string;
  roleType: string;
  active: boolean;
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
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getUsers(page: number = 0, size: number = 10): Observable<PageResponse<User>> {
    return this.http.get<PageResponse<User>>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  suspendUser(id: string, suspend: boolean): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/suspend`, { suspended: suspend });
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
