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
  currentUserEmail = signal<string | null>(null);
  isLoggedIn = computed(() => this.currentUserRole() !== null);

  private apiUrl = 'http://localhost:8080/api/auth';
  private router = inject(Router);

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const email = localStorage.getItem('email') || this.getEmailFromToken(token);

    if (token && role) {
      console.log('Restoring session from localStorage:', { role });
      this.currentUserRole.set(role.toUpperCase());
      this.currentUserEmail.set(email);
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
        localStorage.setItem('email', res.email);
        this.currentUserRole.set(normalizedRole);
        this.currentUserEmail.set(res.email);
      })
    );
  }

  register(userData: { email: string; password: string; roleType: string; gender: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      tap((res) => {
        const normalizedRole = res.role.toUpperCase();
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', normalizedRole);
        localStorage.setItem('email', res.email);
        this.currentUserRole.set(normalizedRole);
        this.currentUserEmail.set(res.email);
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('email');
    this.currentUserRole.set(null);
    this.currentUserEmail.set(null);
    this.router.navigate(['/login']);
  }

  hasRole(role: string): boolean {
    return this.currentUserRole() === role.toUpperCase();
  }

  updateStoredEmail(email: string) {
    localStorage.setItem('email', email);
    this.currentUserEmail.set(email);
  }

  private getEmailFromToken(token: string | null): string | null {
    if (!token) {
      return null;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.email || payload.sub || null;
    } catch {
      return null;
    }
  }
}
