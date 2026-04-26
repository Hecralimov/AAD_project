import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { HttpClient } from '@angular/common/http';
import { OrderService, OrderResponse, ShipmentResponse } from '../../services/order.service';

interface IndividualAnalytics {
  totalSpent: number;
  orderCount: number;
  statusDistribution: { status: string; count: number }[];
}

@Component({
  selector: 'app-individual-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './individual-dashboard.html',
  styleUrl: './individual-dashboard.css',
})
export class IndividualDashboard implements OnInit {
  currentTab: string = 'dashboard';
  kpis: any[] = [];
  orders: OrderResponse[] = [];
  selectedTracking: ShipmentResponse | null = null;
  trackingOrderId: string | null = null;

  public spendingChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [{ data: [0, 0, 0, 0, 0, 0], label: 'Spending ($)', borderColor: '#10b981', backgroundColor: 'rgba(16, 185, 129, 0.1)', fill: true, tension: 0.4 }]
  };

  public spendingChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } }
  };

  public statusChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: ['#10b981', '#f59e0b', '#ef4444', '#6366f1', '#8b5cf6'] }]
  };

  public statusChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false
  };

  constructor(
    private http: HttpClient,
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.fetchAnalytics();
  }

  setTab(tab: string) {
    this.currentTab = tab;
    if (tab === 'orders') {
      this.loadOrders();
    }
  }

  fetchAnalytics() {
    this.http.get<IndividualAnalytics>('http://localhost:8080/api/individual/analytics').subscribe({
      next: (data) => {
        this.kpis = [
          { title: 'Total Spent', value: '$' + (data.totalSpent || 0).toLocaleString(), icon: 'payments' },
          { title: 'Total Orders', value: data.orderCount || 0, icon: 'shopping_basket' },
          { title: 'Savings', value: '$420', icon: 'savings' },
          { title: 'Reward Points', value: '1,250', icon: 'stars' }
        ];

        // Wire status doughnut chart
        if (data.statusDistribution && data.statusDistribution.length > 0) {
          this.statusChartData = {
            labels: data.statusDistribution.map((s: any) => s.status || s.STATUS),
            datasets: [{
              data: data.statusDistribution.map((s: any) => s.count || s.COUNT),
              backgroundColor: ['#10b981', '#f59e0b', '#ef4444', '#6366f1', '#8b5cf6']
            }]
          };
        }

        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to fetch individual analytics', err)
    });
  }

  loadOrders() {
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to fetch orders', err)
    });
  }

  trackOrder(orderId: string) {
    if (this.trackingOrderId === orderId) {
      this.trackingOrderId = null;
      this.selectedTracking = null;
      return;
    }
    this.trackingOrderId = orderId;
    this.orderService.getOrderTracking(orderId).subscribe({
      next: (tracking) => {
        this.selectedTracking = tracking;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch tracking', err);
        this.selectedTracking = null;
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'DELIVERED': return 'badge-delivered';
      case 'PENDING': return 'badge-pending';
      case 'SHIPPED': return 'badge-shipped';
      case 'PROCESSING': return 'badge-processing';
      case 'CANCELLED': return 'badge-cancelled';
      default: return '';
    }
  }
}
