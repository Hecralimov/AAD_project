import { Routes, CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { AdminDashboard } from './components/admin-dashboard/admin-dashboard';
import { CorporateDashboard } from './components/corporate-dashboard/corporate-dashboard';
import { IndividualDashboard } from './components/individual-dashboard/individual-dashboard';
import { Register } from './components/register/register';
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
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'admin', component: AdminDashboard, canActivate: [authGuard('Admin')] },
  { path: 'corporate', component: CorporateDashboard, canActivate: [authGuard('Corporate')] },
  { path: 'individual', component: IndividualDashboard, canActivate: [authGuard('Individual')] },
  { path: '**', redirectTo: 'home' } // Catch-all route for unknown paths
];
