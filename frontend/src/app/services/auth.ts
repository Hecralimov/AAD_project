import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  role: string;
  email: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // null means not logged in.
  currentUserRole = signal<string | null>(null);
  
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {
    // Check localStorage on init
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    if (token && role) {
      this.currentUserRole.set(role);
    }
  }

  login(credentials: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
        this.currentUserRole.set(res.role);
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.currentUserRole.set(null);
  }

  hasRole(role: string): boolean {
    return this.currentUserRole() === role;
  }
  
  isLoggedIn(): boolean {
    return this.currentUserRole() !== null;
  }
}
