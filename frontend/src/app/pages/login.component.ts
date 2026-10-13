import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { messageErreur } from '../core/erreur';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  template: `
    <div class="login">
      <form class="carte" (ngSubmit)="connexion()">
        <div class="logo">⚡</div>
        <h1>Supervision des bornes</h1>
        <p class="sous-titre">Connectez-vous pour accéder au tableau de bord</p>

        <label>Identifiant
          <input name="username" [(ngModel)]="username" required autocomplete="username" />
        </label>
        <label>Mot de passe
          <input name="password" type="password" [(ngModel)]="password" required autocomplete="current-password" />
        </label>

        @if (erreur()) {
          <p class="erreur">{{ erreur() }}</p>
        }
        <button class="btn btn-primaire" type="submit" [disabled]="chargement()">
          {{ chargement() ? 'Connexion…' : 'Se connecter' }}
        </button>
        <p class="aide">Comptes de démo : <b>admin / admin123</b> · <b>technicien / tech123</b></p>
      </form>
    </div>
  `,
  styles: `
    .login { min-height: 100vh; display: grid; place-items: center; padding: 16px; }
    form { width: 100%; max-width: 380px; display: flex; flex-direction: column; gap: 14px; }
    .logo { font-size: 32px; }
    h1 { margin: 0; font-size: 22px; }
    .sous-titre { margin: 0 0 8px; color: var(--texte-2); }
    label { display: flex; flex-direction: column; gap: 6px; font-size: 14px; color: var(--texte-2); }
    .aide { font-size: 13px; color: var(--texte-2); margin: 4px 0 0; }
  `,
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  erreur = signal('');
  chargement = signal(false);

  connexion(): void {
    this.chargement.set(true);
    this.erreur.set('');
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (e) => {
        this.erreur.set(messageErreur(e));
        this.chargement.set(false);
      },
    });
  }
}
