import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  // Line chart for Revenue
  public revenueChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
    datasets: [
      {
        data: [45000, 52000, 48000, 61000, 59000, 75000, 84000],
        label: 'Monthly Revenue ($)',
        fill: true,
        tension: 0.4,
        borderColor: '#6366f1',
        backgroundColor: 'rgba(99, 102, 241, 0.2)'
      }
    ]
  };
  public revenueChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      y: { beginAtZero: true, grid: { color: '#e5e7eb' } },
      x: { grid: { display: false } }
    }
  };

  // Bar chart for Sales by Category
  public categoryChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Electronics', 'Clothing', 'Home', 'Beauty', 'Sports'],
    datasets: [
      { 
        data: [650, 450, 300, 250, 150], 
        label: 'Sales', 
        backgroundColor: '#8b5cf6', 
        borderRadius: 6 
      }
    ]
  };
  public categoryChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      y: { beginAtZero: true, grid: { color: '#e5e7eb' } },
      x: { grid: { display: false } }
    }
  };

  // KPIs
  kpis = [
    { title: 'Total Revenue', value: '$124,563', icon: 'payments', trend: '+14.5%', positive: true },
    { title: 'Total Orders', value: '8,452', icon: 'shopping_cart', trend: '+5.2%', positive: true },
    { title: 'Active Users', value: '1,245', icon: 'group', trend: '+2.1%', positive: true },
    { title: 'Pending Shipments', value: '342', icon: 'local_shipping', trend: '-1.4%', positive: false }
  ];
}
