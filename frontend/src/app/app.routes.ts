import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/auth.guard';
import { AlarmesComponent } from './pages/alarmes.component';
import { BornesComponent } from './pages/bornes.component';
import { DashboardComponent } from './pages/dashboard.component';
import { LoginComponent } from './pages/login.component';
import { MesuresComponent } from './pages/mesures.component';
import { RechargesComponent } from './pages/recharges.component';
import { UtilisateursComponent } from './pages/utilisateurs.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'bornes', component: BornesComponent, canActivate: [authGuard] },
  { path: 'alarmes', component: AlarmesComponent, canActivate: [authGuard] },
  { path: 'mesures', component: MesuresComponent, canActivate: [authGuard] },
  { path: 'recharges', component: RechargesComponent, canActivate: [authGuard] },
  { path: 'utilisateurs', component: UtilisateursComponent, canActivate: [authGuard, adminGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' },
];
