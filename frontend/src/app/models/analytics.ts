export interface DashboardAnalytics {
    kpis: KpiData;
    categorySales: CategorySales[];
    monthlyRevenue: MonthlyRevenue[];
}

export interface KpiData {
    totalRevenue: string;
    totalOrders: number;
    activeUsers: number;
    pendingShipments: number;
    revenueTrend: string;
    ordersTrend: string;
    revenuePositive: boolean;
    ordersPositive: boolean;
}

export interface CategorySales {
    categoryName: string;
    count: number;
}

export interface MonthlyRevenue {
    month: string;
    amount: number;
}
