import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { messageErreur } from '../core/erreur';
import { Borne, Mesure } from '../core/models';

@Component({
  selector: 'app-mesures',
  imports: [DatePipe, DecimalPipe, FormsModule],
  template: `
    <h1>Historique des mesures</h1>

    <form class="carte filtres" (ngSubmit)="rechercher()">
      <label>Borne
        <select name="borne" [(ngModel)]="borneId">
          <option [ngValue]="null">Toutes</option>
          @for (b of bornes(); track b.id) { <option [ngValue]="b.id">{{ b.nom }}</option> }
        </select>
      </label>
      <label>Du <input name="debut" type="datetime-local" [(ngModel)]="debut" /></label>
      <label>Au <input name="fin" type="datetime-local" [(ngModel)]="fin" /></label>
      <button class="btn btn-primaire" type="submit">Rechercher</button>
      <button class="btn" type="button" (click)="exporter()">Exporter en CSV</button>
    </form>

    @if (erreur()) { <p class="erreur">{{ erreur() }}</p> }
    <p class="page-sous-titre">{{ mesures().length }} mesure(s){{ mesures().length > 200 ? ' — affichage des 200 plus récentes' : '' }}</p>

    <div class="carte tableau-scroll">
      <table>
        <thead>
          <tr><th>Date</th><th>Borne</th><th>Puissance (kW)</th><th>Solaire (kW)</th><th>Température (°C)</th></tr>
        </thead>
        <tbody>
          @for (m of recentes(); track m.id) {
            <tr>
              <td>{{ m.horodatage | date: 'dd/MM HH:mm:ss' }}</td>
              <td>{{ nomBorne(m.borneId) }}</td>
              <td>{{ m.puissanceKw | number: '1.2-2' }}</td>
              <td>{{ m.productionSolaireKw | number: '1.2-2' }}</td>
              <td>{{ m.temperature | number: '1.1-1' }}</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: `
    .filtres { display: flex; flex-wrap: wrap; gap: 12px; align-items: end; margin-bottom: 16px; }
    .filtres label { display: flex; flex-direction: column; gap: 4px; font-size: 13px; color: var(--texte-2); }
  `,
})
export class MesuresComponent implements OnInit {
  private api = inject(ApiService);

  bornes = signal<Borne[]>([]);
  mesures = signal<Mesure[]>([]);
  erreur = signal('');

  borneId: number | null = null;
  debut = '';
  fin = '';

  ngOnInit(): void {
    this.api.bornes().subscribe((b) => this.bornes.set(b));
    this.rechercher();
  }

  recentes(): Mesure[] {
    return this.mesures().slice(-200).reverse();
  }

  nomBorne(id: number): string {
    return this.bornes().find((b) => b.id === id)?.nom ?? `#${id}`;
  }

  rechercher(): void {
    this.erreur.set('');
    this.api.mesures(this.borneId, this.debut, this.fin).subscribe({
      next: (m) => this.mesures.set(m),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  exporter(): void {
    this.api.exporterCsv(this.borneId, this.debut, this.fin).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const lien = document.createElement('a');
        lien.href = url;
        lien.download = 'mesures.csv';
        lien.click();
        URL.revokeObjectURL(url);
      },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }
}
