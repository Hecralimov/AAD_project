import { Component, OnInit, ViewChild, ChangeDetectorRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions, Chart } from 'chart.js';
import { AnalyticsService } from '../../services/analytics.service';
import { UserService } from '../../services/user.service';
import { StoreService } from '../../services/store.service';
import { CategoryService, Category } from '../../services/category.service';
import { DashboardAnalytics, StoreComparison } from '../../models/analytics';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  currentTab = signal('dashboard');
  private tabRefresh = signal({ tab: 'dashboard', tick: 0 });
  users: any[] = []; // From UserService
  usersLoading = false;
  usersError = '';
  usersPage = 0;
  usersPageSize = 10;
  usersTotalPages = 0;
  usersTotalElements = 0;
  readonly usersPageSizeOptions = [10, 25, 50];
  stores: any[] = []; // From StoreService
  storesLoading = false;
  storesError = '';
  categories: Category[] = [];
  categoriesLoading = false;
  categoryError = '';
  categorySuccess = '';
  showCategoryModal = false;
  showCategoryDeleteModal = false;
  isCategoryEditMode = false;
  isSavingCategory = false;
  isDeletingCategory = false;
  categoryForm = { name: '' };
  editingCategoryId: string | null = null;
  deletingCategory: Category | null = null;

  storeComparison: StoreComparison[] = [];
  comparisonLoading = false;
  comparisonError = '';

  // Chart'ları manuel güncellemek için referans alıyoruz
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  constructor(
    private analyticsService: AnalyticsService,
    private userService: UserService,
    private storeService: StoreService,
    private categoryService: CategoryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.refreshTab('dashboard');
  }

  setTab(tab: string) {
    this.currentTab.set(tab);
    this.refreshTab(tab);
  }

  getHeaderTitle(): string {
    const tab = this.currentTab();
    if (tab === 'dashboard') return 'Overview Dashboard';
    if (tab === 'users') return 'User Management';
    if (tab === 'stores') return 'Store Management';
    if (tab === 'categories') return 'Category Management';
    if (tab === 'comparison') return 'Store Comparison';
    return 'Overview Dashboard';
  }

  getHeaderSubtitle(): string {
    const tab = this.currentTab();
    if (tab === 'dashboard') return "Welcome back, here's what's happening today.";
    if (tab === 'users') return 'Manage system users and access levels.';
    if (tab === 'stores') return 'Manage marketplace stores and operations.';
    if (tab === 'categories') return 'Manage product categories shown across the shop.';
    if (tab === 'comparison') return 'Compare store revenue, order volume, and average rating.';
    return '';
  }

  loadStoreComparison() {
    this.comparisonLoading = true;
    this.comparisonError = '';
    this.analyticsService.getStoreComparison().subscribe({
      next: (comparison) => {
        this.storeComparison = comparison;
        this.updateStoreComparisonChart(comparison);
        this.comparisonLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch store comparison', err);
        this.comparisonError = 'Could not load store comparison data.';
        this.comparisonLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  updateStoreComparisonChart(comparison: StoreComparison[]) {
    this.storeComparisonChartData = {
      labels: comparison.map(store => store.storeName || store.storeId.substring(0, 8)),
      datasets: [
        {
          data: comparison.map(store => store.totalRevenue || 0),
          label: 'Revenue ($)',
          backgroundColor: '#4f46e5',
          borderRadius: 6,
          yAxisID: 'revenue'
        },
        {
          data: comparison.map(store => store.totalOrders || 0),
          label: 'Orders',
          backgroundColor: '#0ea5e9',
          borderRadius: 6,
          yAxisID: 'orders'
        }
      ]
    };
  }

  getTopStore(): StoreComparison | null {
    if (this.storeComparison.length === 0) return null;
    return [...this.storeComparison].sort((a, b) => (b.totalRevenue || 0) - (a.totalRevenue || 0))[0];
  }

  getBestRating(): number {
    if (this.storeComparison.length === 0) return 0;
    return Math.max(...this.storeComparison.map(store => store.averageRating || 0));
  }

  loadCategories() {
    this.categoriesLoading = true;
    this.categoryError = '';
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.categoriesLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch categories', err);
        this.categoryError = 'Could not load categories.';
        this.categoriesLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openAddCategoryModal() {
    this.isCategoryEditMode = false;
    this.editingCategoryId = null;
    this.categoryForm = { name: '' };
    this.categoryError = '';
    this.categorySuccess = '';
    this.showCategoryModal = true;
  }

  openEditCategoryModal(category: Category) {
    this.isCategoryEditMode = true;
    this.editingCategoryId = category.id;
    this.categoryForm = { name: category.name };
    this.categoryError = '';
    this.categorySuccess = '';
    this.showCategoryModal = true;
  }

  closeCategoryModal() {
    this.showCategoryModal = false;
  }

  saveCategory() {
    const name = this.categoryForm.name.trim();
    if (!name) {
      this.categoryError = 'Category name is required.';
      return;
    }

    this.isSavingCategory = true;
    this.categoryError = '';
    this.categorySuccess = '';

    const request = this.isCategoryEditMode && this.editingCategoryId
      ? this.categoryService.updateCategory(this.editingCategoryId, { name })
      : this.categoryService.createCategory({ name });

    request.subscribe({
      next: () => {
        this.showCategoryModal = false;
        this.categorySuccess = this.isCategoryEditMode ? 'Category updated.' : 'Category added.';
        this.isSavingCategory = false;
        this.loadCategories();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to save category', err);
        this.categoryError = 'Could not save category. Backend category create/update support may be missing.';
        this.isSavingCategory = false;
        this.cdr.detectChanges();
      }
    });
  }

  openDeleteCategoryModal(category: Category) {
    this.deletingCategory = category;
    this.categoryError = '';
    this.categorySuccess = '';
    this.showCategoryDeleteModal = true;
  }

  closeCategoryDeleteModal() {
    this.showCategoryDeleteModal = false;
    this.deletingCategory = null;
  }

  confirmDeleteCategory() {
    if (!this.deletingCategory?.id) return;

    this.isDeletingCategory = true;
    this.categoryService.deleteCategory(this.deletingCategory.id).subscribe({
      next: () => {
        this.showCategoryDeleteModal = false;
        this.deletingCategory = null;
        this.categorySuccess = 'Category deleted.';
        this.isDeletingCategory = false;
        this.loadCategories();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to delete category', err);
        this.categoryError = 'Could not delete category. Backend category delete support may be missing.';
        this.isDeletingCategory = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadStores() {
    this.storesLoading = true;
    this.storesError = '';
    this.storeService.getStores().subscribe({
      next: (stores) => {
        this.stores = stores;
        this.storesLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch stores', err);
        this.storesError = 'Could not load stores.';
        this.storesLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  toggleStoreStatus(store: any) {
    const newStatus = store.status === 'OPEN' ? 'CLOSED' : 'OPEN';
    this.storeService.updateStoreStatus(store.id, newStatus).subscribe({
      next: (updatedStore) => {
        store.status = updatedStore.status;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to update store status', err);
        this.cdr.detectChanges();
      }
    });
  }

  loadUsers(page = this.usersPage) {
    this.usersLoading = true;
    this.usersError = '';
    this.usersPage = Math.max(page, 0);

    this.userService.getUsers(this.usersPage, this.usersPageSize).subscribe({
      next: (response) => {
        this.users = response.content;
        this.usersPage = response.number;
        this.usersPageSize = response.size;
        this.usersTotalPages = response.totalPages;
        this.usersTotalElements = response.totalElements;
        this.usersLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch users', err);
        this.users = [];
        this.usersError = 'Could not load users.';
        this.usersLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  changeUsersPageSize(size: number | string) {
    this.usersPageSize = Number(size);
    this.loadUsers(0);
  }

  goToUsersPage(page: number) {
    if (this.usersLoading || page < 0 || page >= this.usersTotalPages || page === this.usersPage) return;
    this.loadUsers(page);
  }

  getUsersStart(): number {
    if (this.usersTotalElements === 0) return 0;
    return this.usersPage * this.usersPageSize + 1;
  }

  getUsersEnd(): number {
    return Math.min((this.usersPage + 1) * this.usersPageSize, this.usersTotalElements);
  }

  toggleSuspend(user: any) {
    const newStatus = user.active;
    this.userService.suspendUser(user.id, newStatus).subscribe({
      next: (updatedUser) => {
        user.active = updatedUser.active;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to suspend user', err);
        this.cdr.detectChanges();
      }
    });
  }

  deleteUser(id: string) {
    if(confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== id);
          this.usersTotalElements = Math.max(this.usersTotalElements - 1, 0);
          this.usersTotalPages = Math.ceil(this.usersTotalElements / this.usersPageSize);
          if (this.users.length === 0 && this.usersPage > 0) {
            this.loadUsers(this.usersPage - 1);
            return;
          }
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to delete user', err);
          this.cdr.detectChanges();
        }
      });
    }
  }

  fetchAnalytics() {
    this.analyticsService.getAdminAnalytics().subscribe({
      next: (data) => {
        this.updateDashboard(data);
      },
      error: (err) => {
        console.error('Failed to fetch analytics', err);
        this.cdr.detectChanges();
      }
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

  public storeComparisonChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: []
  };

  public storeComparisonChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        labels: { usePointStyle: true }
      }
    },
    scales: {
      revenue: {
        type: 'linear',
        position: 'left',
        beginAtZero: true,
        grid: { color: '#e5e7eb' },
        ticks: {
          callback: (value) => '$' + Number(value).toLocaleString()
        }
      },
      orders: {
        type: 'linear',
        position: 'right',
        beginAtZero: true,
        grid: { drawOnChartArea: false },
        ticks: {
          precision: 0
        }
      },
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

  private refreshTab(tab: string) {
    this.tabRefresh.update(current => ({ tab, tick: current.tick + 1 }));
    if (tab === 'dashboard') {
      this.fetchAnalytics();
    } else if (tab === 'users') {
      this.loadUsers();
    } else if (tab === 'stores') {
      this.loadStores();
    } else if (tab === 'categories') {
      this.loadCategories();
    } else if (tab === 'comparison') {
      this.loadStoreComparison();
    }
  }
}
