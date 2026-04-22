import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map } from 'rxjs';
import { DashboardAnalytics } from '../models/analytics';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/admin/analytics';

  constructor(private http: HttpClient) { }

  getAdminAnalytics(): Observable<DashboardAnalytics> {
    return forkJoin({
      revenue: this.http.get<number>(`${this.apiUrl}/revenue/total`),
      categories: this.http.get<any[]>(`${this.apiUrl}/sales/categories`),
      users: this.http.get<any[]>(`${this.apiUrl}/users/distribution`),
      totalOrders: this.http.get<number>(`${this.apiUrl}/orders/total`),
      pendingShipments: this.http.get<number>(`${this.apiUrl}/shipments/pending`),
      monthlyRevenue: this.http.get<any[]>(`${this.apiUrl}/revenue/monthly`)
    }).pipe(
      map(res => {
        const totalUsers = res.users.reduce((sum, role) => sum + role.userCount, 0);

        return {
          kpis: {
            totalRevenue: '$' + (res.revenue || 0).toLocaleString(undefined, { maximumFractionDigits: 0 }),
            revenueTrend: 'N/A', 
            revenuePositive: true,
            totalOrders: res.totalOrders || 0,
            ordersTrend: 'N/A',
            ordersPositive: true,
            activeUsers: totalUsers,
            pendingShipments: res.pendingShipments || 0
          },
          categorySales: res.categories.map(c => ({
            categoryName: c.categoryName,
            count: c.totalSales
          })),
          monthlyRevenue: res.monthlyRevenue.map(m => ({
            month: m.month,
            amount: m.amount
          }))
        } as DashboardAnalytics;
      })
    );
  }
}