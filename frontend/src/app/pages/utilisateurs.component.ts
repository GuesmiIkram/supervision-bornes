import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { messageErreur } from '../core/erreur';
import { Role, Utilisateur } from '../core/models';

@Component({
  selector: 'app-utilisateurs',
  imports: [FormsModule],
  template: `
    <h1>Utilisateurs</h1>
    <p class="page-sous-titre">Réservé aux administrateurs</p>

    <form class="carte ajout" (ngSubmit)="creer()">
      <input name="u" placeholder="Identifiant" [(ngModel)]="username" required />
      <input name="p" type="password" placeholder="Mot de passe (6 car. min.)" [(ngModel)]="password" required />
      <select name="r" [(ngModel)]="role">
        <option value="TECHNICIEN">Technicien</option>
        <option value="ADMIN">Administrateur</option>
      </select>
      <button class="btn btn-primaire" type="submit">Créer</button>
    </form>

    @if (erreur()) { <p class="erreur">{{ erreur() }}</p> }

    <div class="carte tableau-scroll">
      <table>
        <thead><tr><th>Identifiant</th><th>Rôle</th><th></th></tr></thead>
        <tbody>
          @for (u of utilisateurs(); track u.id) {
            <tr>
              <td>{{ u.username }}</td>
              <td><span class="badge">{{ u.role }}</span></td>
              <td class="droite"><button class="btn btn-danger" (click)="supprimer(u)">Supprimer</button></td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: `
    .ajout { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 16px; }
    .ajout input { flex: 1 1 160px; }
    .droite { text-align: right; }
  `,
})
export class UtilisateursComponent implements OnInit {
  private api = inject(ApiService);

  utilisateurs = signal<Utilisateur[]>([]);
  erreur = signal('');
  username = '';
  password = '';
  role: Role = 'TECHNICIEN';

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.api.utilisateurs().subscribe({
      next: (u) => this.utilisateurs.set(u),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  creer(): void {
    this.erreur.set('');
    this.api.creerUtilisateur(this.username, this.password, this.role).subscribe({
      next: () => { this.username = ''; this.password = ''; this.charger(); },
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }

  supprimer(u: Utilisateur): void {
    if (!confirm(`Supprimer ${u.username} ?`)) return;
    this.api.supprimerUtilisateur(u.id).subscribe({
      next: () => this.charger(),
      error: (e) => this.erreur.set(messageErreur(e)),
    });
  }
}
