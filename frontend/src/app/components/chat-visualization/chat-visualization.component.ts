import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlotlyModule } from 'angular-plotly.js';

@Component({
  selector: 'app-chat-visualization',
  standalone: true,
  imports: [CommonModule, PlotlyModule],
  template: `
    <div class="visualization-container">
      <!-- Only render if data exists and there is no error -->
      @if (!error && data && data.length > 0) {
        <plotly-plot 
          [data]="data" 
          [layout]="layout"
          [useResizeHandler]="true"
          [style]="{position: 'relative', width: '100%', height: '250px'}">
        </plotly-plot>
      } @else if (error) {
        <div class="error-placeholder">
          <span class="icon">⚠️</span>
          <p>Visualization failed to load</p>
        </div>
      }
    </div>
  `,
  styles: [`
    .visualization-container {
      width: 100%;
      min-height: 150px;
      border-radius: 8px;
      overflow: hidden;
      margin-top: 12px;
      background-color: #1f2937;
      border: 1px solid #374151;
    }
    .error-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 150px;
      color: #f87171;
      font-size: 14px;
      background-color: #1f2937;
    }
    .error-placeholder .icon {
      font-size: 24px;
      margin-bottom: 8px;
    }
  `]
})
export class ChatVisualizationComponent implements OnInit {
  @Input() code!: string;

  data: any[] = [];
  layout: any = {};
  error: boolean = false;

  ngOnInit() {
    this.parseCode();
  }

  private parseCode() {
    try {
      let parsed: any;
      const cleanCode = this.code.trim();

      // The Visualization Agent returns clean Plotly JSON
      try {
        parsed = JSON.parse(cleanCode);
      } catch (e) {
        // Fallback for JS object literal formats
        const fn = new Function(`return ${cleanCode}`);
        parsed = fn();
      }

      if (parsed && (parsed.data || parsed.layout)) {
        this.data = parsed.data || [];
        this.layout = {
          ...parsed.layout,
          paper_bgcolor: 'rgba(0,0,0,0)', // Ensure transparent background for dashboard
          plot_bgcolor: 'rgba(0,0,0,0)',
          font: { color: '#e5e7eb' },
          margin: { l: 30, r: 10, t: 30, b: 30 },
          autosize: true
        };
      } else {
        this.error = true;
      }
    } catch (err) {
      console.error('Visualization parse error:', err);
      this.error = true;
    }
  }
}