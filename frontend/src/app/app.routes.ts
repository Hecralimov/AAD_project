import { Routes, CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Home } from './components/home/home';
import { AuthService } from './services/auth';

const authGuard = (role: string): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    if (authService.hasRole(role)) {
      return true;
    }
    return router.parseUrl('/home');
  };
};

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: Home },
  { 
    path: 'products', 
    loadComponent: () => import('./components/shop/shop').then(m => m.Shop) 
  },
  { 
    path: 'products/:id', 
    loadComponent: () => import('./components/product-detail/product-detail').then(m => m.ProductDetailComponent) 
  },
  { 
    path: 'cart', 
    loadComponent: () => import('./components/cart/cart').then(m => m.CartComponent) 
  },
  { 
    path: 'checkout', 
    loadComponent: () => import('./components/checkout/checkout').then(m => m.CheckoutComponent) 
  },
  { 
    path: 'login', 
    loadComponent: () => import('./components/login/login').then(m => m.Login) 
  },
  { 
    path: 'register', 
    loadComponent: () => import('./components/register/register').then(m => m.Register) 
  },
  { 
    path: 'admin', 
    loadComponent: () => import('./components/admin-dashboard/admin-dashboard').then(m => m.AdminDashboard), 
    canActivate: [authGuard('Admin')] 
  },
  { 
    path: 'corporate', 
    loadComponent: () => import('./components/corporate-dashboard/corporate-dashboard').then(m => m.CorporateDashboard), 
    canActivate: [authGuard('Corporate')] 
  },
  { 
    path: 'individual', 
    loadComponent: () => import('./components/individual-dashboard/individual-dashboard').then(m => m.IndividualDashboard), 
    canActivate: [authGuard('Individual')] 
  },
  { path: '**', redirectTo: 'home' } // Catch-all route for unknown paths
];
