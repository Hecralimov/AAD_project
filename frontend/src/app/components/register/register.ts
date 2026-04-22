import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  email = '';
  password = '';
  roleType = 'Individual';
  gender = 'Other';
  errorMessage = '';
  isLoading = false;
  showPassword = false;

  constructor(private authService: AuthService, private router: Router) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    this.isLoading = true;

    this.authService.register({
      email: this.email,
      password: this.password,
      roleType: this.roleType,
      gender: this.gender
    }).subscribe({
      next: (res) => {
        const role = res.role?.toUpperCase();
        if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (role === 'CORPORATE') {
          this.router.navigate(['/corporate']);
        } else {
          this.router.navigate(['/individual']);
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Registration failed', err);
        this.errorMessage = 'Registration failed. Email might already be in use.';
        this.isLoading = false;
      },
    });
  }
}
