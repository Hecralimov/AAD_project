package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.CustomerProfileDTO;
import com.ecommerce.analytics_backend.model.CustomerProfile;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.CustomerProfileRepository;
import com.ecommerce.analytics_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final CustomerProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CustomerProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        return toDto(user, profile);
    }

    @Transactional
    public CustomerProfileDTO upsertProfile(String email, CustomerProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerProfile profile = profileRepository.findByUserId(user.getId())
                .orElse(CustomerProfile.builder().user(user).build());

        if (dto.getFullName() != null) {
            profile.setFullName(dto.getFullName());
        }
        if (dto.getPhoneNumber() != null) {
            profile.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAddressLine() != null) {
            profile.setAddressLine(dto.getAddressLine());
        }
        if (dto.getCity() != null) {
            profile.setCity(dto.getCity());
        }
        if (dto.getCountry() != null) {
            profile.setCountry(dto.getCountry());
        }

        return toDto(user, profileRepository.save(profile));
    }

    private CustomerProfileDTO toDto(User user, CustomerProfile profile) {
        CustomerProfileDTO dto = new CustomerProfileDTO();
        if (profile != null) {
            dto.setId(profile.getId());
            dto.setFullName(profile.getFullName());
            dto.setPhoneNumber(profile.getPhoneNumber());
            dto.setAddressLine(profile.getAddressLine());
            dto.setCity(profile.getCity());
            dto.setCountry(profile.getCountry());
        }
        dto.setEmail(user.getEmail());
        dto.setRoleType(user.getRoleType());
        dto.setActive(user.isEnabled());
        return dto;
    }
}
