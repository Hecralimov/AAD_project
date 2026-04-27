import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { ProfileService } from '../../services/profile.service';
import { Profile } from '../../models/profile';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent implements OnInit {
  profile: Profile | null = null;
  email = '';
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  isLoading = true;
  isSavingProfile = false;
  isChangingPassword = false;
  profileError = '';
  profileSuccess = '';
  passwordError = '';
  passwordSuccess = '';
  showCurrentPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  constructor(public authService: AuthService, private profileService: ProfileService) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.isLoading = true;
    this.profileError = '';

    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.email = profile.email;
        this.authService.updateStoredEmail(profile.email);
        this.isLoading = false;
      },
      error: () => {
        const fallbackEmail = this.authService.currentUserEmail() || '';
        const fallbackRole = this.authService.currentUserRole() || 'USER';

        this.profile = {
          email: fallbackEmail,
          roleType: this.formatRole(fallbackRole)
        };
        this.email = fallbackEmail;
        this.profileError = 'Profile details could not be loaded from the server.';
        this.isLoading = false;
      }
    });
  }

  saveProfile() {
    this.profileError = '';
    this.profileSuccess = '';

    const normalizedEmail = this.email.trim();
    if (!this.isValidEmail(normalizedEmail)) {
      this.profileError = 'Please enter a valid email address.';
      return;
    }

    this.isSavingProfile = true;
    this.profileService.updateProfile({ email: normalizedEmail }).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.email = profile.email;
        this.authService.updateStoredEmail(profile.email);
        this.profileSuccess = 'Profile updated successfully.';
        this.isSavingProfile = false;
      },
      error: () => {
        this.profileError = 'Profile update failed. The backend profile endpoint may not be available yet.';
        this.isSavingProfile = false;
      }
    });
  }

  changePassword() {
    this.passwordError = '';
    this.passwordSuccess = '';

    if (!this.currentPassword || !this.newPassword || !this.confirmPassword) {
      this.passwordError = 'Please fill in all password fields.';
      return;
    }

    if (this.newPassword.length < 8) {
      this.passwordError = 'New password must be at least 8 characters.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.passwordError = 'New password and confirmation do not match.';
      return;
    }

    this.isChangingPassword = true;
    this.profileService.changePassword({
      currentPassword: this.currentPassword,
      newPassword: this.newPassword
    }).subscribe({
      next: () => {
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        this.passwordSuccess = 'Password changed successfully.';
        this.isChangingPassword = false;
      },
      error: () => {
        this.passwordError = 'Password change failed. Please check your current password or backend support.';
        this.isChangingPassword = false;
      }
    });
  }

  roleLabel(): string {
    return this.formatRole(this.profile?.roleType || this.authService.currentUserRole() || 'User');
  }

  dashboardPath(): string {
    return `/${(this.authService.currentUserRole() || 'individual').toLowerCase()}`;
  }

  private formatRole(role: string): string {
    const lowered = role.toLowerCase();
    return lowered.charAt(0).toUpperCase() + lowered.slice(1);
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
}
