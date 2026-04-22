import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { AnalyticsService } from '../../services/analytics.service';
import { DashboardAnalytics } from '../../models/analytics';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-individual-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './individual-dashboard.html',
  styleUrl: './individual-dashboard.css',
})
export class IndividualDashboard implements OnInit {
  kpis: any[] = [];

  public spendingChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [{ data: [0, 0, 0, 0, 0, 0], label: 'Spending ($)', borderColor: '#10b981', backgroundColor: 'rgba(16, 185, 129, 0.1)', fill: true }]
  };

  public spendingChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } }
  };

  public statusChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Delivered', 'Pending', 'Cancelled'],
    datasets: [{ data: [10, 2, 1], backgroundColor: ['#10b981', '#f59e0b', '#ef4444'] }]
  };

  public statusChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false
  };

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchAnalytics();
  }

  fetchAnalytics() {
    this.http.get<DashboardAnalytics>('http://localhost:8080/api/individual/analytics').subscribe({
      next: (data) => {
        this.kpis = [
          { title: 'Total Spent', value: data.kpis.totalRevenue, icon: 'payments' },
          { title: 'Total Orders', value: data.kpis.totalOrders, icon: 'shopping_basket' },
          { title: 'Savings', value: '$420', icon: 'savings' },
          { title: 'Reward Points', value: '1,250', icon: 'stars' }
        ];
      },
      error: (err) => console.error('Failed to fetch individual analytics', err)
    });
  }
}
