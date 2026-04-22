package com.ecommerce.analytics_backend.config;

import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordMigrationSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // fixedDelay: Bir önceki görev bittikten 5000ms (5sn) sonra tekrar çalışır.
    // Uygulama açık olduğu sürece arka planda sessizce veriyi eritir.
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void migrateInBackround() {
        // Sadece ilk 500 kirli kaydı getir
        List<User> dirtyUsers = userRepository.findUsersNeedingMigration();

        if (dirtyUsers.isEmpty()) {
            // Log kalabalığı yapmamak için burayı kapatabilirsin
            return; 
        }

        int batchSize = Math.min(dirtyUsers.size(), 500);
        List<User> batch = dirtyUsers.subList(0, batchSize);

        log.info("[BACKGROUND-ETL] Processing batch of {} users. Remaining: {}", batchSize, dirtyUsers.size());

        for (User user : batch) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        userRepository.saveAllAndFlush(batch);
        log.info("[BACKGROUND-ETL] Batch committed successfully.");
    }
}