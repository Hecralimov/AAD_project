import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ToastMessage, ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.html',
  styleUrl: './toast-container.css'
})
export class ToastContainerComponent {
  constructor(public toastService: ToastService) {}

  iconFor(toast: ToastMessage): string {
    if (toast.type === 'success') {
      return 'check_circle';
    }
    if (toast.type === 'error') {
      return 'error_outline';
    }
    return 'info';
  }
}
