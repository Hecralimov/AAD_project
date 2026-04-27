import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        console.error('Session expired or unauthorized access.');
        toastService.error('Your session expired. Please sign in again.');
        authService.logout();
      } else if (error.status === 403) {
        console.error('Unauthorized operation.');
        toastService.error('You do not have permission to perform this action.');
      } else if (error.status >= 500) {
        console.error('Server error:', error.message);
        toastService.error('A server error occurred. Please try again later.');
      } else {
        console.error('Request failed:', error.message);
      }

      return throwError(() => error);
    })
  );
};
