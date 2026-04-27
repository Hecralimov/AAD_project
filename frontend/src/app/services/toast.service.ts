import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastMessage {
  id: number;
  type: ToastType;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private nextId = 1;
  private readonly toastState = signal<ToastMessage[]>([]);

  readonly toasts = this.toastState.asReadonly();

  success(message: string) {
    this.show(message, 'success');
  }

  error(message: string) {
    this.show(message, 'error');
  }

  info(message: string) {
    this.show(message, 'info');
  }

  dismiss(id: number) {
    this.toastState.update((toasts) => toasts.filter((toast) => toast.id !== id));
  }

  private show(message: string, type: ToastType) {
    const toast: ToastMessage = {
      id: this.nextId++,
      type,
      message
    };

    this.toastState.update((toasts) => [...toasts, toast].slice(-4));
    window.setTimeout(() => this.dismiss(toast.id), 4200);
  }
}
