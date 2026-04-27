import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                console.error('Oturum süresi doldu veya yetkisiz erişim.');
                alert('Oturumunuzun süresi doldu. Lütfen tekrar giriş yapın.');
                authService.logout();
            } else if (error.status === 403) {
                console.error('Yetkisiz işlem.');
                alert('Bu işlemi gerçekleştirmek için yetkiniz bulunmuyor.');
            } else if (error.status >= 500) {
                console.error('Sunucu hatası:', error.message);
                alert('Sunucu tarafında beklenmeyen bir hata oluştu. Lütfen daha sonra tekrar deneyin.');
            } else {
                console.error('İşlem başarısız:', error.message);
            }

            return throwError(() => error);
        })
    );
};