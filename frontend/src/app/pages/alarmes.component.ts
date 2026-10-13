import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { messageErreur } from '../core/erreur';
import { Alarme } from '../core/models';

@Component({
  selector: 'app-alarmes',
  imports: [DatePipe, FormsModule],
  template: `
    <h1>Alarmes</h1>
    <div class="barre">
      <label class="case"><input type="checkbox" [(ngModel)]="seulementActives" (change)="charger()" /> Non acquittées uniquement</label>
      <button class="btn" (click)="charger()">Actualiser</button>
    </div>

    @if (erreur()) { <p class="erreur">{{ erreur() }}</p> }

    <div class="carte tableau-scroll">
      <table>
        <thead>
          <tr><th>Date</th><th>Borne</th><th>Niveau</th><th>Message</th><th>État</th></tr>
        </thead>
        <tbody>
          @for (a of alarmes(); track a.id) {
            <tr>
              <td>{{ a.horodatage | date: 'dd/MM HH:mm:ss' }}</td>
              <td>{{ a.borneNom }}</td>
              <td><span class="badge" [class]="'badge niveau-' + a.niveau">{{ a.niveau }}</span></td>
              <td>{{ a.message }}</td>
              <td>
                @if (a.acquittee) {
                  <span class="acq">✔ {{ a.acquitteePar }}</span>
                } @else {
                  <button class="btn btn-primaire" (click)="acquitter(a)">Acquitter</button>
                }
              </td>
            </tr>
          } @empty {
            <tr><td colspan="5" class="vide">Aucune alarme</td></tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: `
    .barre { display: flex; gap: 12px; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; }
    .case { display: flex; gap: 8px; align-items: center; }
    .acq { color: var(--vert); font-size: 13px; }
    .vide { text-align: center; color: var(--texte-2); }
  `,
})
export class AlarmesComponent implements OnInit {
  private api = inject(ApiService);

  alarmes = signal<Alarme[]>([]);
  erreur = signal('');
  seulementActives = true;

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.api.alarmes(this.seulementActives).subscribe({
      next: (a) => this.alarmes.set(a),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  acquitter(a: Alarme): void {
    this.api.acquitter(a.id).subscribe({
      next: () => this.charger(),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }
}
