import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { HttpClient } from '@angular/common/http';

interface CorporateAnalytics {
  totalRevenue: number;
  orderCount: number;
  lowStockCount: number;
}

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
    datasets: [{ 
      data: [0, 0, 0, 0, 0, 0, 0], 
      label: 'Daily Revenue', 
      borderColor: '#4f46e5', 
      backgroundColor: 'rgba(79, 70, 229, 0.1)',
      fill: true, 
      tension: 0.4 
    }]
  };

  public revenueChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } }
  };

  public inventoryChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Stock Level', backgroundColor: '#8b5cf6' }]
  };

  public inventoryChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } }
  };

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.fetchAnalytics();
  }

  fetchAnalytics() {
    this.http.get<CorporateAnalytics>('http://localhost:8080/api/corporate/analytics').subscribe({
      next: (data) => {
        this.kpis = [
          { title: 'Store Revenue', value: '$' + (data.totalRevenue || 0).toLocaleString(), icon: 'storefront' },
          { title: 'Orders Received', value: data.orderCount || 0, icon: 'receipt_long' },
          { title: 'Customer Rating', value: '4.8/5.0', icon: 'star' },
          { title: 'Low Stock Alerts', value: (data.lowStockCount || 0) + ' Items', icon: 'warning' }
        ];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to fetch corporate analytics', err)
    });
  }
}
