package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.StoreComparisonProjection;
import com.ecommerce.analytics_backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final StoreRepository storeRepository;

    public List<StoreComparisonProjection> getStoreComparison() {
        return storeRepository.getStoreComparisonAnalytics();
    }
}