import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService, Product, PageResponse } from '../../services/product.service';
import { CategoryService, Category } from '../../services/category.service';
import { CartService } from '../../services/cart.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './shop.html',
  styleUrl: './shop.css'
})
export class Shop implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 12;
  isLoading = false;
  errorMessage = '';
  categoriesError = '';

  selectedCategoryId: string = '';
  searchQuery: string = '';
  sortOption: string = '';

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private cartService: CartService,
    private toastService: ToastService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  addToCart(product: Product, event: Event) {
    event.stopPropagation();
    this.cartService.addToCart(product);
    this.toastService.success(`${product.name} added to cart.`);
  }

  ngOnInit() {
    this.categoryService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
      },
      error: (err) => {
        console.error('Error fetching categories', err);
        this.categoriesError = 'Categories could not be loaded.';
      }
    });

    this.route.queryParams.subscribe(params => {
      this.selectedCategoryId = params['categoryId'] || '';
      this.searchQuery = params['search'] || '';
      this.sortOption = params['sort'] || '';
      this.currentPage = params['page'] ? parseInt(params['page'], 10) : 0;
      this.loadProducts();
    });
  }

  loadProducts() {
    this.isLoading = true;
    this.errorMessage = '';
    let sortBy = undefined;
    let sortDir = undefined;

    if (this.sortOption === 'priceAsc') {
      sortBy = 'unitPrice';
      sortDir = 'asc';
    } else if (this.sortOption === 'priceDesc') {
      sortBy = 'unitPrice';
      sortDir = 'desc';
    } else if (this.sortOption === 'latest') {
      sortBy = 'id'; 
      sortDir = 'desc';
    }

    this.productService.getProducts(
      this.currentPage,
      this.pageSize,
      this.selectedCategoryId || undefined,
      this.searchQuery || undefined,
      sortBy,
      sortDir
    ).subscribe({
      next: (response: PageResponse<Product>) => {
        this.products = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: (err) => {
        console.error('Error fetching products', err);
        this.products = [];
        this.totalPages = 0;
        this.totalElements = 0;
        this.errorMessage = 'Products could not be loaded. Please try again.';
        this.isLoading = false;
      }
    });
  }

  updateFilters() {
    const queryParams: any = {};
    if (this.selectedCategoryId) queryParams.categoryId = this.selectedCategoryId;
    if (this.searchQuery) queryParams.search = this.searchQuery;
    if (this.sortOption) queryParams.sort = this.sortOption;
    if (this.currentPage > 0) queryParams.page = this.currentPage;

    this.router.navigate(['/products'], { queryParams });
  }

  onSearch() {
    this.currentPage = 0;
    this.updateFilters();
  }

  onCategorySelect(categoryId: string) {
    this.selectedCategoryId = categoryId;
    this.currentPage = 0;
    this.updateFilters();
  }

  onSortChange(event: any) {
    this.sortOption = event.target.value;
    this.currentPage = 0;
    this.updateFilters();
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.updateFilters();
    }
  }

  getPages(): number[] {
    const pages = [];
    const maxVisible = 5;
    let start = Math.max(0, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible);
    
    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }
    
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    return pages;
  }
}
