package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.DashboardAnalyticsDto;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.model.Store;
import com.ecommerce.analytics_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public DashboardAnalyticsDto getAdminAnalytics() {
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        Long totalOrders = orderRepository.getTotalOrders();
        Long activeUsers = orderRepository.getActiveUsersCount();
        Long pendingShipments = shipmentRepository.countPendingShipments();

        // Format revenue as currency
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedRevenue = formatter.format(totalRevenue);

        DashboardAnalyticsDto.KpiDto kpis = DashboardAnalyticsDto.KpiDto.builder()
                .totalRevenue(formattedRevenue)
                .totalOrders(totalOrders)
                .activeUsers(activeUsers)
                .pendingShipments(pendingShipments)
                .revenueTrend("+12.5%") // Mock trend for now
                .ordersTrend("+5.2%")   // Mock trend for now
                .revenuePositive(true)
                .ordersPositive(true)
                .build();

        return DashboardAnalyticsDto.builder()
                .kpis(kpis)
                .categorySales(orderItemRepository.getSalesByCategory())
                .monthlyRevenue(orderRepository.getMonthlyRevenue())
                .build();
    }

    public DashboardAnalyticsDto getIndividualAnalytics(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        String userId = user.getId();

        BigDecimal totalSpend = orderRepository.getTotalSpendByUserId(userId);
        if (totalSpend == null) totalSpend = BigDecimal.ZERO;

        Long totalOrders = orderRepository.getOrderCountByUserId(userId);

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        
        DashboardAnalyticsDto.KpiDto kpis = DashboardAnalyticsDto.KpiDto.builder()
                .totalRevenue(formatter.format(totalSpend)) 
                .totalOrders(totalOrders)
                .activeUsers(1L) 
                .pendingShipments(0L) 
                .revenueTrend("Personal Stats")
                .build();

        return DashboardAnalyticsDto.builder()
                .kpis(kpis)
                .build();
    }

    public DashboardAnalyticsDto getCorporateAnalytics(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Store store = storeRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Store not found for this corporate user"));

        BigDecimal totalRevenue = orderRepository.getTotalRevenueByStoreId(store.getId());
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        Long totalOrders = orderRepository.getOrderCountByStoreId(store.getId());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

        DashboardAnalyticsDto.KpiDto kpis = DashboardAnalyticsDto.KpiDto.builder()
                .totalRevenue(formatter.format(totalRevenue))
                .totalOrders(totalOrders)
                .revenueTrend("Store: " + store.getName())
                .build();

        return DashboardAnalyticsDto.builder()
                .kpis(kpis)
                .build();
    }
}
