import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from './models';

const CLE_SESSION = 'supervision.session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private session = signal<LoginResponse | null>(this.lireSession());

  readonly utilisateur = computed(() => this.session()?.username ?? null);
  readonly estConnecte = computed(() => this.session() !== null);
  readonly estAdmin = computed(() => this.session()?.role === 'ADMIN');

  get token(): string | null {
    return this.session()?.token ?? null;
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(
      tap((reponse) => {
        this.session.set(reponse);
        try {
          sessionStorage.setItem(CLE_SESSION, JSON.stringify(reponse));
        } catch {
          /* stockage indisponible : la session reste en mémoire */
        }
      })
    );
  }

  logout(): void {
    this.session.set(null);
    try {
      sessionStorage.removeItem(CLE_SESSION);
    } catch {
      /* rien à faire */
    }
    this.router.navigate(['/login']);
  }

  private lireSession(): LoginResponse | null {
    try {
      const brut = sessionStorage.getItem(CLE_SESSION);
      return brut ? (JSON.parse(brut) as LoginResponse) : null;
    } catch {
      return null;
    }
  }
}
