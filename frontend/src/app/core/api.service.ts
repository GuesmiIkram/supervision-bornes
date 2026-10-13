import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Alarme, Borne, Dashboard, Mesure, Prise, Recharge, Role, Utilisateur } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  // Tableau de bord
  dashboard(): Observable<Dashboard> {
    return this.http.get<Dashboard>('/api/dashboard');
  }

  // Bornes
  bornes(): Observable<Borne[]> {
    return this.http.get<Borne[]>('/api/bornes');
  }
  creerBorne(nom: string, localisation: string, nombrePrises: number): Observable<Borne> {
    return this.http.post<Borne>('/api/bornes', { nom, localisation, nombrePrises });
  }
  supprimerBorne(id: number): Observable<void> {
    return this.http.delete<void>(`/api/bornes/${id}`);
  }
  maintenance(id: number, actif: boolean): Observable<Borne> {
    return this.http.patch<Borne>(`/api/bornes/${id}/maintenance`, { actif });
  }
  commanderPanneau(id: number, angle: number): Observable<Borne> {
    return this.http.patch<Borne>(`/api/bornes/${id}/panneau`, { angle });
  }
  panneauSecurite(id: number): Observable<Borne> {
    return this.http.post<Borne>(`/api/bornes/${id}/panneau/securite`, {});
  }
  configurerSeuils(id: number, seuilTemperatureMax: number, seuilPuissanceMax: number): Observable<Borne> {
    return this.http.put<Borne>(`/api/bornes/${id}/seuils`, { seuilTemperatureMax, seuilPuissanceMax });
  }
  changerPrise(priseId: number, actif: boolean): Observable<Prise> {
    return this.http.patch<Prise>(`/api/bornes/prises/${priseId}`, { actif });
  }

  // Alarmes
  alarmes(actives: boolean): Observable<Alarme[]> {
    return this.http.get<Alarme[]>('/api/alarmes', { params: { actives } });
  }
  acquitter(id: number): Observable<Alarme> {
    return this.http.post<Alarme>(`/api/alarmes/${id}/acquitter`, {});
  }

  // Mesures
  mesures(borneId: number | null, debut: string, fin: string): Observable<Mesure[]> {
    return this.http.get<Mesure[]>('/api/mesures', { params: this.filtres(borneId, debut, fin) });
  }
  exporterCsv(borneId: number | null, debut: string, fin: string): Observable<Blob> {
    return this.http.get('/api/mesures/export', {
      params: this.filtres(borneId, debut, fin),
      responseType: 'blob',
    });
  }

  // Recharges
  recharges(): Observable<Recharge[]> {
    return this.http.get<Recharge[]>('/api/recharges');
  }

  // Utilisateurs (admin)
  utilisateurs(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>('/api/utilisateurs');
  }
  creerUtilisateur(username: string, password: string, role: Role): Observable<Utilisateur> {
    return this.http.post<Utilisateur>('/api/utilisateurs', { username, password, role });
  }
  supprimerUtilisateur(id: number): Observable<void> {
    return this.http.delete<void>(`/api/utilisateurs/${id}`);
  }

  private filtres(borneId: number | null, debut: string, fin: string): HttpParams {
    let params = new HttpParams();
    if (borneId) params = params.set('borneId', borneId);
    if (debut) params = params.set('debut', this.iso(debut));
    if (fin) params = params.set('fin', this.iso(fin));
    return params;
  }

  /** "2026-10-13T14:05" -> "2026-10-13T14:05:00" (format attendu par Spring). */
  private iso(valeur: string): string {
    return valeur.length === 16 ? `${valeur}:00` : valeur;
  }
}
