import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { HttpClient } from '@angular/common/http';
import { CorporateProductService, CorporateProduct } from '../../services/corporate-product.service';
import { CategoryService, Category } from '../../services/category.service';

interface CorporateAnalytics {
  totalRevenue: number;
  orderCount: number;
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
  currentTab = 'dashboard';
  kpis: any[] = [];

  // Products tab
  products: CorporateProduct[] = [];
  categories: Category[] = [];
  productsLoading = false;

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
    private categoryService: CategoryService
  ) {}

  ngOnInit() {
    this.fetchAnalytics();
    this.loadCategories();
  }

  setTab(tab: string) {
    this.currentTab = tab;
    if (tab === 'products') {
      this.loadProducts();
    }
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

  // --- Categories ---
  loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (cats) => this.categories = cats,
      error: (err) => console.error('Failed to load categories', err)
    });
  }

  getCategoryName(categoryId: string): string {
    const cat = this.categories.find(c => c.id === categoryId);
    return cat ? cat.name : categoryId?.substring(0, 8) || 'N/A';
  }

  // --- Product CRUD ---
  loadProducts() {
    this.productsLoading = true;
    this.corporateProductService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.productsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load products', err);
        this.productsLoading = false;
      }
    });
  }

  openAddModal() {
    this.isEditMode = false;
    this.editingProductId = null;
    this.productForm = { name: '', sku: '', unitPrice: 0, stock: 0, categoryId: '' };
    this.showProductModal = true;
  }

  openEditModal(product: CorporateProduct) {
    this.isEditMode = true;
    this.editingProductId = product.id!;
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
    if (this.isEditMode && this.editingProductId) {
      this.corporateProductService.updateProduct(this.editingProductId, this.productForm).subscribe({
        next: () => {
          this.showProductModal = false;
          this.loadProducts();
        },
        error: (err) => console.error('Failed to update product', err)
      });
    } else {
      this.corporateProductService.createProduct(this.productForm).subscribe({
        next: () => {
          this.showProductModal = false;
          this.loadProducts();
        },
        error: (err) => console.error('Failed to create product', err)
      });
    }
  }

  openDeleteModal(product: CorporateProduct) {
    this.deletingProduct = product;
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.deletingProduct = null;
  }

  confirmDelete() {
    if (this.deletingProduct?.id) {
      this.corporateProductService.deleteProduct(this.deletingProduct.id).subscribe({
        next: () => {
          this.showDeleteModal = false;
          this.deletingProduct = null;
          this.loadProducts();
        },
        error: (err) => console.error('Failed to delete product', err)
      });
    }
  }
}
