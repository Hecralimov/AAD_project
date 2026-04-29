import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';
  errorMessage = '';
  isLoading = false;
  showPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }


  onSubmit() {
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Please enter both email and password.';
      return;
    }

    this.isLoading = true;

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        console.log('Backend Response:', res);
        const role = res.role?.toUpperCase();
        if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (role === 'CORPORATE') {
          this.router.navigate(['/corporate']);
        } else {
          this.router.navigate(['/individual']);
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Login failed', err);
        this.errorMessage = 'Invalid email or password.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }
}
