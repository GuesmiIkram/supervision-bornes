import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { messageErreur } from '../core/erreur';
import { Borne } from '../core/models';

@Component({
  selector: 'app-bornes',
  imports: [FormsModule],
  template: `
    <h1>Bornes de recharge</h1>
    <p class="page-sous-titre">Commande du panneau solaire, des prises et du mode maintenance</p>

    @if (message()) { <p class="erreur">{{ message() }}</p> }

    @if (auth.estAdmin()) {
      <form class="carte ajout" (ngSubmit)="creer()">
        <b>Nouvelle borne</b>
        <input name="nom" placeholder="Nom (ex. BORNE-PARIS-01)" [(ngModel)]="nouvelle.nom" required />
        <input name="loc" placeholder="Localisation" [(ngModel)]="nouvelle.localisation" />
        <input name="prises" type="number" min="1" max="8" [(ngModel)]="nouvelle.nombrePrises" title="Nombre de prises" />
        <button class="btn btn-primaire" type="submit">Ajouter</button>
      </form>
    }

    <div class="grille">
      @for (b of bornes(); track b.id) {
        <article class="carte borne">
          <header>
            <div>
              <h2>{{ b.nom }}</h2>
              <p class="loc">{{ b.localisation }}</p>
            </div>
            <span class="badge" [class]="'badge statut-' + b.statut">{{ b.statut }}</span>
          </header>

          <section>
            <h3>Prises</h3>
            <div class="prises">
              @for (p of b.prises; track p.id) {
                <button class="btn prise" [class.active]="p.active" (click)="basculerPrise(p.id, !p.active)">
                  Prise {{ p.numero }} · {{ p.active ? 'active' : 'désactivée' }}
                </button>
              }
            </div>
          </section>

          <section>
            <h3>Panneau solaire</h3>
            @if (b.panneauEnSecurite) {
              <p class="securite">🛡️ En position de sécurité</p>
            } @else {
              <p>Inclinaison actuelle : <b>{{ b.anglePanneau }}°</b></p>
            }
            <div class="ligne">
              <input type="range" min="0" max="90" #angle [value]="b.anglePanneau" />
              <button class="btn" (click)="executer(api.commanderPanneau(b.id, +angle.value))">Orienter</button>
            </div>
            <button class="btn btn-danger" (click)="executer(api.panneauSecurite(b.id))">Mettre en sécurité</button>
          </section>

          @if (auth.estAdmin()) {
            <section>
              <h3>Seuils d'alarme</h3>
              <div class="ligne">
                <label>Temp. max (°C) <input type="number" #temp [value]="b.seuilTemperatureMax" /></label>
                <label>Puiss. max (kW) <input type="number" #puis [value]="b.seuilPuissanceMax" /></label>
                <button class="btn" (click)="executer(api.configurerSeuils(b.id, +temp.value, +puis.value))">Enregistrer</button>
              </div>
            </section>
          }

          <footer>
            @if (b.statut === 'MAINTENANCE') {
              <button class="btn btn-primaire" (click)="executer(api.maintenance(b.id, false))">Remettre en service</button>
            } @else {
              <button class="btn" (click)="executer(api.maintenance(b.id, true))">Passer en maintenance</button>
            }
            @if (auth.estAdmin()) {
              <button class="btn btn-danger" (click)="supprimer(b)">Supprimer</button>
            }
          </footer>
        </article>
      }
    </div>
  `,
  styles: `
    .ajout { display: flex; flex-wrap: wrap; gap: 10px; align-items: center; margin-bottom: 20px; }
    .ajout input { flex: 1 1 160px; }
    .ajout input[type=number] { flex: 0 0 80px; }
    .grille { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
    .borne { display: flex; flex-direction: column; gap: 14px; }
    header { display: flex; justify-content: space-between; align-items: flex-start; gap: 8px; }
    h2 { margin: 0; font-size: 17px; }
    h3 { margin: 0 0 8px; font-size: 13px; text-transform: uppercase; letter-spacing: .04em; color: var(--texte-2); }
    .loc { margin: 2px 0 0; color: var(--texte-2); font-size: 13px; }
    section p { margin: 0 0 8px; }
    .prises { display: flex; flex-wrap: wrap; gap: 8px; }
    .prise { opacity: .6; }
    .prise.active { opacity: 1; border-color: var(--vert); color: var(--vert); }
    .ligne { display: flex; flex-wrap: wrap; gap: 8px; align-items: end; margin-bottom: 8px; }
    .ligne input[type=range] { flex: 1; }
    .ligne label { display: flex; flex-direction: column; font-size: 12px; color: var(--texte-2); gap: 4px; }
    .ligne input[type=number] { width: 90px; }
    .securite { color: var(--orange); font-weight: 600; }
    footer { display: flex; gap: 8px; flex-wrap: wrap; margin-top: auto; }
  `,
})
export class BornesComponent implements OnInit {
  api = inject(ApiService);
  auth = inject(AuthService);

  bornes = signal<Borne[]>([]);
  message = signal('');
  nouvelle = { nom: '', localisation: '', nombrePrises: 2 };

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.api.bornes().subscribe({
      next: (b) => this.bornes.set(b),
      error: (e) => this.message.set(messageErreur(e)),
    });
  }

  /** Exécute une commande puis recharge la liste. */
  executer(action: Observable<unknown>): void {
    this.message.set('');
    action.subscribe({
      next: () => this.charger(),
      error: (e) => this.message.set(messageErreur(e)),
    });
  }

  basculerPrise(priseId: number, actif: boolean): void {
    this.executer(this.api.changerPrise(priseId, actif));
  }

  creer(): void {
    if (!this.nouvelle.nom.trim()) return;
    this.executer(this.api.creerBorne(this.nouvelle.nom.trim(), this.nouvelle.localisation, this.nouvelle.nombrePrises));
    this.nouvelle = { nom: '', localisation: '', nombrePrises: 2 };
  }

  supprimer(b: Borne): void {
    if (confirm(`Supprimer la borne ${b.nom} et tout son historique ?`)) {
      this.executer(this.api.supprimerBorne(b.id));
    }
  }
}
