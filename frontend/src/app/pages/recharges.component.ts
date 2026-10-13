import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ApiService } from '../core/api.service';
import { messageErreur } from '../core/erreur';
import { Recharge } from '../core/models';

@Component({
  selector: 'app-recharges',
  imports: [DatePipe, DecimalPipe],
  template: `
    <h1>Sessions de recharge</h1>
    <p class="page-sous-titre">{{ recharges().length }} session(s) · {{ total() | number: '1.0-1' }} kWh délivrés</p>
    @if (erreur()) { <p class="erreur">{{ erreur() }}</p> }

    <div class="carte tableau-scroll">
      <table>
        <thead><tr><th>Borne</th><th>Prise</th><th>Début</th><th>Fin</th><th>Énergie (kWh)</th></tr></thead>
        <tbody>
          @for (r of recharges(); track r.id) {
            <tr>
              <td>{{ r.borneNom }}</td>
              <td>{{ r.numeroPrise }}</td>
              <td>{{ r.debut | date: 'dd/MM/yyyy HH:mm' }}</td>
              <td>{{ r.fin | date: 'dd/MM/yyyy HH:mm' }}</td>
              <td>{{ r.energieKwh | number: '1.1-1' }}</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
})
export class RechargesComponent implements OnInit {
  private api = inject(ApiService);

  recharges = signal<Recharge[]>([]);
  erreur = signal('');

  ngOnInit(): void {
    this.api.recharges().subscribe({
      next: (r) => this.recharges.set(r),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  total(): number {
    return this.recharges().reduce((somme, r) => somme + r.energieKwh, 0);
  }
}
