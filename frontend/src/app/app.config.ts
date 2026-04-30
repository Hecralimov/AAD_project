import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideZonelessChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { PlotlyService } from 'angular-plotly.js';
// @ts-ignore
import * as PlotlyJS from 'plotly.js-dist-min';

// In angular-plotly.js v20+, you must register the Plotly object via the PlotlyService
PlotlyService.setPlotly((PlotlyJS as any).default || PlotlyJS);

import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';
import { errorInterceptor } from './interceptors/error.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    provideCharts(withDefaultRegisterables())
  ],
};