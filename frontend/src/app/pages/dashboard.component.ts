import { DecimalPipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { ApiService } from '../core/api.service';
import { messageErreur } from '../core/erreur';
import { Dashboard } from '../core/models';

@Component({
  selector: 'app-dashboard',
  imports: [DecimalPipe],
  template: `
    <h1>Tableau de bord</h1>
    <p class="page-sous-titre">Actualisation automatique toutes les 10 secondes</p>

    @if (erreur()) { <p class="erreur">{{ erreur() }}</p> }

    @if (data(); as d) {
      <div class="kpis">
        <div class="carte kpi"><span>Bornes</span><b>{{ d.nombreBornes }}</b></div>
        <div class="carte kpi"><span>En service</span><b class="ok">{{ d.bornesEnService }}</b></div>
        <div class="carte kpi"><span>En maintenance</span><b class="warn">{{ d.bornesEnMaintenance }}</b></div>
        <div class="carte kpi"><span>Prises actives</span><b>{{ d.prisesActives }}</b></div>
        <div class="carte kpi"><span>Alarmes actives</span><b [class.ko]="d.alarmesActives > 0">{{ d.alarmesActives }}</b></div>
        <div class="carte kpi"><span>Énergie 24 h</span><b>{{ d.energieDelivree24hKwh | number: '1.0-1' }} <small>kWh</small></b></div>
      </div>

      <h2>État des bornes</h2>
      <div class="carte tableau-scroll">
        <table>
          <thead>
            <tr><th>Borne</th><th>Statut</th><th>Puissance</th><th>Production solaire</th><th>Température</th></tr>
          </thead>
          <tbody>
            @for (b of d.bornes; track b.id) {
              <tr>
                <td>{{ b.nom }}</td>
                <td><span class="badge" [class]="'badge statut-' + b.statut">{{ b.statut }}</span></td>
                <td>{{ b.puissanceKw === null ? '—' : (b.puissanceKw | number: '1.1-1') + ' kW' }}</td>
                <td>{{ b.productionSolaireKw === null ? '—' : (b.productionSolaireKw | number: '1.1-1') + ' kW' }}</td>
                <td>{{ b.temperature === null ? '—' : (b.temperature | number: '1.1-1') + ' °C' }}</td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    }
  `,
  styles: `
    .kpis { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 12px; margin-bottom: 24px; }
    .kpi { display: flex; flex-direction: column; gap: 6px; }
    .kpi span { color: var(--texte-2); font-size: 13px; }
    .kpi b { font-size: 26px; }
    .kpi small { font-size: 14px; color: var(--texte-2); }
    .ok { color: var(--vert); } .warn { color: var(--orange); } .ko { color: var(--rouge); }
  `,
})
export class DashboardComponent implements OnInit, OnDestroy {
  private api = inject(ApiService);
  private minuteur?: ReturnType<typeof setInterval>;

  data = signal<Dashboard | null>(null);
  erreur = signal('');

  ngOnInit(): void {
    this.charger();
    this.minuteur = setInterval(() => this.charger(), 10_000);
  }

  ngOnDestroy(): void {
    clearInterval(this.minuteur);
  }

  private charger(): void {
    this.api.dashboard().subscribe({
      next: (d) => { this.data.set(d); this.erreur.set(''); },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }
}
