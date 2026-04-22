import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardAnalytics } from '../models/analytics';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/admin/analytics';

  constructor(private http: HttpClient) { }

  getAdminAnalytics(): Observable<DashboardAnalytics> {
    return this.http.get<DashboardAnalytics>(this.apiUrl);
  }
}
