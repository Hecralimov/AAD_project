import { Component, OnInit, ChangeDetectorRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CorporateProductService, CorporateProduct } from '../../services/corporate-product.service';
import { CategoryService, Category } from '../../services/category.service';
import { CorporateOrderService, CorporateOrder } from '../../services/corporate-order.service';

interface CorporateAnalytics {
  totalRevenue: number;
  totalOrders: number;
  lowStockCount: number;
}

@Component({
  selector: 'app-corporate-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './corporate-dashboard.html',
  styleUrl: './corporate-dashboard.css',
})
export class CorporateDashboard implements OnInit {
  currentTab = signal('dashboard');
  private tabRefresh = signal<{ tab: string; tick: number } | null>(null);
  kpis: any[] = [];
  analyticsLoading = false;
  analyticsError = '';
  startDate = '';
  endDate = '';

  // Products tab
  products: CorporateProduct[] = [];
  categories: Category[] = [];
  productsLoading = false;
  productError = '';
  productSuccess = '';
  isSavingProduct = false;
  isDeletingProduct = false;

  // Orders tab
  orders: CorporateOrder[] = [];
  orderDraftStatuses: Record<string, string> = {};
  ordersLoading = false;
  orderError = '';
  orderSuccess = '';
  updatingOrderId: string | null = null;

  // Modal state
  showProductModal = false;
  showDeleteModal = false;
  isEditMode = false;
  productForm: CorporateProduct = { name: '', sku: '', unitPrice: 0, stock: 0, categoryId: '' };
  editingProductId: string | null = null;
  deletingProduct: CorporateProduct | null = null;

  // Charts
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
    private cdr: ChangeDetectorRef,
    private corporateProductService: CorporateProductService,
    private categoryService: CategoryService,
    private corporateOrderService: CorporateOrderService
  ) {}

  ngOnInit() {
    this.setQuickRange('30d');
    this.loadCategories();
    this.refreshTab('dashboard');
  }

  setTab(tab: string) {
    this.currentTab.set(tab);
    this.refreshTab(tab);
  }

  fetchAnalytics() {
    this.analyticsLoading = true;
    this.analyticsError = '';

    let params = new HttpParams();
    if (this.startDate) {
      params = params.set('startDate', this.startDate);
    }
    if (this.endDate) {
      params = params.set('endDate', this.endDate);
    }

    this.http.get<CorporateAnalytics>('http://localhost:8080/api/corporate/analytics', { params }).subscribe({
      next: (data) => {
        this.kpis = [
          { title: 'Store Revenue', value: '$' + (data.totalRevenue || 0).toLocaleString(), icon: 'storefront' },
          { title: 'Orders Received', value: data.totalOrders || 0, icon: 'receipt_long' },
          { title: 'Customer Rating', value: '4.8/5.0', icon: 'star' },
          { title: 'Low Stock Alerts', value: (data.lowStockCount || 0) + ' Items', icon: 'warning' }
        ];
        this.analyticsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch corporate analytics', err);
        this.analyticsError = 'Could not refresh analytics for this date range.';
        this.analyticsLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyDateRange() {
    if (this.startDate && this.endDate && this.startDate > this.endDate) {
      this.analyticsError = 'Start date must be before end date.';
      return;
    }

    this.fetchAnalytics();
  }

  clearDateRange() {
    this.startDate = '';
    this.endDate = '';
    this.fetchAnalytics();
  }

  setQuickRange(range: '7d' | '30d' | '90d') {
    const end = new Date();
    const start = new Date();
    const days = range === '7d' ? 7 : range === '30d' ? 30 : 90;
    start.setDate(end.getDate() - days);
    this.startDate = this.formatDateInput(start);
    this.endDate = this.formatDateInput(end);
  }

  private formatDateInput(date: Date): string {
    return date.toISOString().slice(0, 10);
  }

  private refreshTab(tab: string) {
    this.tabRefresh.update(current => ({
      tab,
      tick: (current?.tick ?? 0) + 1
    }));

    if (tab === 'dashboard') {
      this.fetchAnalytics();
    } else if (tab === 'products') {
      this.loadProducts();
    } else if (tab === 'orders') {
      this.loadOrders();
    }
  }

  // --- Categories ---
  loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load categories', err);
        this.cdr.detectChanges();
      }
    });
  }

  getCategoryName(categoryId: string): string {
    const cat = this.categories.find(c => c.id === categoryId);
    return cat ? cat.name : categoryId?.substring(0, 8) || 'N/A';
  }

  // --- Product CRUD ---
  loadProducts() {
    this.productsLoading = true;
    this.productError = '';
    this.corporateProductService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.productsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load products', err);
        this.productError = 'Could not load your products.';
        this.productsLoading = false;
      }
    });
  }

  openAddModal() {
    this.isEditMode = false;
    this.editingProductId = null;
    this.productError = '';
    this.productSuccess = '';
    this.productForm = { name: '', sku: '', unitPrice: 0, stock: 0, categoryId: '' };
    this.showProductModal = true;
  }

  openEditModal(product: CorporateProduct) {
    this.isEditMode = true;
    this.editingProductId = product.id!;
    this.productError = '';
    this.productSuccess = '';
    this.productForm = {
      name: product.name,
      sku: product.sku,
      unitPrice: product.unitPrice,
      stock: product.stock,
      categoryId: product.categoryId
    };
    this.showProductModal = true;
  }

  closeProductModal() {
    this.showProductModal = false;
  }

  saveProduct() {
    if (!this.isProductFormValid()) {
      this.productError = 'Complete product name, SKU, category, price, and stock before saving.';
      return;
    }

    this.isSavingProduct = true;
    this.productError = '';
    this.productSuccess = '';

    if (this.isEditMode && this.editingProductId) {
      this.corporateProductService.updateProduct(this.editingProductId, this.productForm).subscribe({
        next: () => {
        this.showProductModal = false;
        this.productSuccess = 'Product updated.';
        this.isSavingProduct = false;
        this.loadProducts();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to update product', err);
        this.productError = 'Could not update product.';
        this.isSavingProduct = false;
        this.cdr.detectChanges();
      }
    });
    } else {
      this.corporateProductService.createProduct(this.productForm).subscribe({
        next: () => {
        this.showProductModal = false;
        this.productSuccess = 'Product added.';
        this.isSavingProduct = false;
        this.loadProducts();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to create product', err);
        this.productError = 'Could not add product.';
        this.isSavingProduct = false;
        this.cdr.detectChanges();
      }
    });
    }
  }

  isProductFormValid(): boolean {
    return Boolean(
      this.productForm.name?.trim() &&
      this.productForm.sku?.trim() &&
      this.productForm.categoryId &&
      this.productForm.unitPrice >= 0 &&
      this.productForm.stock >= 0
    );
  }

  openDeleteModal(product: CorporateProduct) {
    this.deletingProduct = product;
    this.productError = '';
    this.productSuccess = '';
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.deletingProduct = null;
  }

  confirmDelete() {
    if (this.deletingProduct?.id) {
      this.isDeletingProduct = true;
      this.corporateProductService.deleteProduct(this.deletingProduct.id).subscribe({
        next: () => {
          this.showDeleteModal = false;
          this.deletingProduct = null;
          this.productSuccess = 'Product deleted.';
          this.isDeletingProduct = false;
          this.loadProducts();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to delete product', err);
          this.productError = 'Could not delete product.';
          this.isDeletingProduct = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  // --- Order Management ---
  loadOrders() {
    this.ordersLoading = true;
    this.orderError = '';
    this.corporateOrderService.getOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.orderDraftStatuses = orders.reduce((drafts, order) => {
          drafts[order.id] = order.status;
          return drafts;
        }, {} as Record<string, string>);
        this.ordersLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load corporate orders', err);
        this.orderError = 'Could not load incoming orders.';
        this.ordersLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  updateOrderStatus(order: CorporateOrder) {
    const nextStatus = this.orderDraftStatuses[order.id];
    if (!nextStatus || nextStatus === order.status) return;

    this.updatingOrderId = order.id;
    this.orderError = '';
    this.orderSuccess = '';

    this.corporateOrderService.updateOrderStatus(order.id, nextStatus).subscribe({
      next: (updatedOrder) => {
        this.orders = this.orders.map(existing =>
          existing.id === updatedOrder.id ? updatedOrder : existing
        );
        this.orderDraftStatuses[updatedOrder.id] = updatedOrder.status;
        this.orderSuccess = `Order ${this.formatShortId(updatedOrder.id)} marked ${updatedOrder.status}.`;
        this.updatingOrderId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to update order status', err);
        this.orderError = 'Could not update order status.';
        this.updatingOrderId = null;
        this.cdr.detectChanges();
      }
    });
  }

  formatShortId(id: string): string {
    return id ? id.substring(0, 8).toUpperCase() : 'N/A';
  }

  getStatusClass(status: string): string {
    const normalized = (status || '').toLowerCase();
    if (normalized.includes('deliver')) return 'badge-active';
    if (normalized.includes('ship')) return 'badge-info';
    if (normalized.includes('cancel')) return 'badge-suspended';
    return 'badge-warning';
  }
}
