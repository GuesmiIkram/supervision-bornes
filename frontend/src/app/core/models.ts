export type Role = 'ADMIN' | 'TECHNICIEN';
export type StatutBorne = 'EN_SERVICE' | 'MAINTENANCE' | 'HORS_LIGNE';
export type NiveauAlarme = 'INFO' | 'AVERTISSEMENT' | 'CRITIQUE';

export interface LoginResponse {
  token: string;
  username: string;
  role: Role;
}

export interface Prise {
  id: number;
  numero: number;
  active: boolean;
}

export interface Borne {
  id: number;
  nom: string;
  localisation: string;
  statut: StatutBorne;
  anglePanneau: number;
  panneauEnSecurite: boolean;
  seuilTemperatureMax: number;
  seuilPuissanceMax: number;
  prises: Prise[];
}

export interface Mesure {
  id: number;
  borneId: number;
  horodatage: string;
  puissanceKw: number;
  productionSolaireKw: number;
  temperature: number;
}

export interface Alarme {
  id: number;
  borneId: number;
  borneNom: string;
  niveau: NiveauAlarme;
  message: string;
  horodatage: string;
  acquittee: boolean;
  acquitteePar: string | null;
  dateAcquittement: string | null;
}

export interface Recharge {
  id: number;
  borneNom: string;
  numeroPrise: number;
  debut: string;
  fin: string;
  energieKwh: number;
}

export interface EtatBorne {
  id: number;
  nom: string;
  statut: StatutBorne;
  puissanceKw: number | null;
  productionSolaireKw: number | null;
  temperature: number | null;
}

export interface Dashboard {
  nombreBornes: number;
  bornesEnService: number;
  bornesEnMaintenance: number;
  prisesActives: number;
  alarmesActives: number;
  energieDelivree24hKwh: number;
  bornes: EtatBorne[];
}

export interface Utilisateur {
  id: number;
  username: string;
  role: Role;
}
