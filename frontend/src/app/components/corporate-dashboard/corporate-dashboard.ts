import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { HttpClient } from '@angular/common/http';
import { DashboardAnalytics } from '../../models/analytics';

@Component({
  selector: 'app-corporate-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './corporate-dashboard.html',
  styleUrl: './corporate-dashboard.css',
})
export class CorporateDashboard implements OnInit {
  kpis: any[] = [];

  public revenueChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    datasets: [{ data: [1200, 1900, 1500, 2100, 2400, 3000, 2800], label: 'Daily Revenue', borderColor: '#4f46e5', fill: true }]
  };

  public revenueChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false
  };

  public inventoryChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Product A', 'Product B', 'Product C', 'Product D'],
    datasets: [{ data: [45, 12, 88, 34], label: 'Stock Level', backgroundColor: '#8b5cf6' }]
  };

  public inventoryChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false
  };

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchAnalytics();
  }

  fetchAnalytics() {
    this.http.get<DashboardAnalytics>('http://localhost:8080/api/corporate/analytics').subscribe({
      next: (data) => {
        this.kpis = [
          { title: 'Store Revenue', value: data.kpis.totalRevenue, icon: 'storefront' },
          { title: 'Orders Received', value: data.kpis.totalOrders, icon: 'receipt_long' },
          { title: 'Customer Rating', value: '4.8/5.0', icon: 'star' },
          { title: 'Low Stock Alerts', value: '3 Items', icon: 'warning' }
        ];
      },
      error: (err) => console.error('Failed to fetch corporate analytics', err)
    });
  }
}
