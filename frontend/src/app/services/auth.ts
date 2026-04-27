import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
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
  currentUserRole = signal<string | null>(null);
  isLoggedIn = computed(() => this.currentUserRole() !== null);

  private apiUrl = 'http://localhost:8080/api/auth';
  private router = inject(Router);

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (token && role) {
      console.log('Restoring session from localStorage:', { role });
      this.currentUserRole.set(role.toUpperCase());
    } else {
      console.log('No session found in localStorage');
    }
  }

  login(credentials: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((res) => {
        const normalizedRole = res.role.toUpperCase();
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', normalizedRole);
        this.currentUserRole.set(normalizedRole);
      })
    );
  }

  register(userData: { email: string; password: string; roleType: string; gender: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      tap((res) => {
        const normalizedRole = res.role.toUpperCase();
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', normalizedRole);
        this.currentUserRole.set(normalizedRole);
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.currentUserRole.set(null);
    this.router.navigate(['/login']);
  }

  hasRole(role: string): boolean {
    return this.currentUserRole() === role.toUpperCase();
  }
}