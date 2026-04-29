export interface Profile {
  id?: string;
  email: string;
  roleType: string;
  active?: boolean;
  fullName?: string;
  phoneNumber?: string;
  addressLine?: string;
  city?: string;
  country?: string;
}

export interface UpdateProfileRequest {
  email: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}
