export interface Profile {
  id?: string;
  email: string;
  roleType: string;
  active?: boolean;
}

export interface UpdateProfileRequest {
  email: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}
