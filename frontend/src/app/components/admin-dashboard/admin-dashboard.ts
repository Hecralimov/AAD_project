import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions, Chart } from 'chart.js';
import { AnalyticsService } from '../../services/analytics.service';
import { UserService } from '../../services/user.service';
import { DashboardAnalytics } from '../../models/analytics';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  currentTab: string = 'dashboard';
  users: any[] = []; // From UserService

  // Chart'ları manuel güncellemek için referans alıyoruz
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  constructor(
    private analyticsService: AnalyticsService,
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.fetchAnalytics();
  }

  setTab(tab: string) {
    this.currentTab = tab;
    if (tab === 'users') {
      this.loadUsers();
    }
  }

  loadUsers() {
    this.userService.getUsers().subscribe({
      next: (users) => this.users = users,
      error: (err) => console.error('Failed to fetch users', err)
    });
  }

  toggleSuspend(user: any) {
    const newStatus = !user.isSuspended;
    this.userService.suspendUser(user.id, newStatus).subscribe({
      next: () => user.isSuspended = newStatus,
      error: (err) => console.error('Failed to suspend user', err)
    });
  }

  deleteUser(id: string) {
    if(confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => this.users = this.users.filter(u => u.id !== id),
        error: (err) => console.error('Failed to delete user', err)
      });
    }
  }

  fetchAnalytics() {
    this.analyticsService.getAdminAnalytics().subscribe({
      next: (data) => {
        this.updateDashboard(data);
      },
      error: (err) => console.error('Failed to fetch analytics', err)
    });
  }

  updateDashboard(data: DashboardAnalytics) {
    // 1. KPI'ları Gerçek Veriyle Güncelle
    this.kpis = [
      { title: 'Total Revenue', value: data.kpis.totalRevenue, icon: 'payments', trend: data.kpis.revenueTrend, positive: data.kpis.revenuePositive },
      { title: 'Total Orders', value: data.kpis.totalOrders.toString(), icon: 'shopping_cart', trend: data.kpis.ordersTrend, positive: data.kpis.ordersPositive },
      { title: 'Active Users', value: data.kpis.activeUsers.toString(), icon: 'group', trend: '+2.1%', positive: true },
      { title: 'Pending Shipments', value: data.kpis.pendingShipments.toString(), icon: 'local_shipping', trend: '-1.4%', positive: false }
    ];

    // 2. Kategori Grafiğini Gerçek Veriyle "YENİDEN" Oluştur (Referansı değiştirerek Angular'ı tetikliyoruz)
    this.categoryChartData = {
      labels: data.categorySales.map(s => s.categoryName),
      datasets: [
        { 
          data: data.categorySales.map(s => s.count), 
          label: 'Sales', 
          backgroundColor: '#8b5cf6', 
          borderRadius: 6 
        }
      ]
    };

    // 3. Revenue Grafiğini Gerçek Veriyle "YENİDEN" Oluştur
    this.revenueChartData = {
      labels: data.monthlyRevenue.map(r => r.month),
      datasets: [
        {
          data: data.monthlyRevenue.map(r => r.amount),
          label: 'Monthly Revenue ($)',
          fill: true,
          tension: 0.4,
          borderColor: '#6366f1',
          backgroundColor: 'rgba(99, 102, 241, 0.2)'
        }
      ]
    };

    // Değişiklikleri grafiğe zorla
    this.chart?.update();

    this.cdr.detectChanges();
    if (this.chart) {
      this.chart.update();
    }
  }

  // Başlangıçta boş grafik objeleri tanımlıyoruz (Mock dataları sildik)
  public revenueChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Monthly Revenue ($)' }]
  };
  
  public revenueChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      y: { beginAtZero: true, grid: { color: '#e5e7eb' } },
      x: { grid: { display: false } }
    }
  };

  public categoryChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Sales' }]
  };

  public categoryChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      y: { beginAtZero: true, grid: { color: '#e5e7eb' } },
      x: { grid: { display: false } }
    }
  };

  // Başlangıçta boş KPI'lar
  kpis = [
    { title: 'Total Revenue', value: 'Loading...', icon: 'payments', trend: '...', positive: true },
    { title: 'Total Orders', value: 'Loading...', icon: 'shopping_cart', trend: '...', positive: true },
    { title: 'Active Users', value: 'Loading...', icon: 'group', trend: '...', positive: true },
    { title: 'Pending Shipments', value: 'Loading...', icon: 'local_shipping', trend: '...', positive: false }
  ];
}