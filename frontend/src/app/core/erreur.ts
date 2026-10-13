import { HttpErrorResponse } from '@angular/common/http';

/** Extrait un message lisible d'une erreur HTTP (format ProblemDetail renvoyé par Spring). */
export function messageErreur(erreur: unknown): string {
  if (erreur instanceof HttpErrorResponse) {
    if (erreur.status === 0) return 'Serveur injoignable. Le backend est-il démarré ?';
    if (erreur.status === 403) return "Action réservée à l'administrateur.";
    return erreur.error?.detail ?? `Erreur ${erreur.status}`;
  }
  return 'Erreur inattendue';
}
