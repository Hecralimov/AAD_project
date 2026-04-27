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
    public CustomerProfile getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return profileRepository.findByUserId(user.getId())
                .orElse(CustomerProfile.builder().user(user).build()); // Return empty shell if none exists
    }

    @Transactional
    public CustomerProfile upsertProfile(String email, CustomerProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerProfile profile = profileRepository.findByUserId(user.getId())
                .orElse(CustomerProfile.builder().user(user).build());

        profile.setFullName(dto.getFullName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setAddressLine(dto.getAddressLine());
        profile.setCity(dto.getCity());
        profile.setCountry(dto.getCountry());

        return profileRepository.save(profile);
    }
}