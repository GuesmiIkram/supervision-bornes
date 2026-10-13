# ⚡ Supervision de bornes de recharge

Application web full-stack pour superviser un parc de **bornes de recharge pour véhicules électriques équipées de panneaux solaires**.
Les techniciens suivent l'état des bornes en temps réel, pilotent le panneau solaire et les prises, et traitent les alarmes ; les administrateurs gèrent le parc, les seuils d'alarme et les comptes.

![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Angular](https://img.shields.io/badge/Angular_19-DD0031?style=flat&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)

---

## Fonctionnalités

| Fonction | Technicien | Administrateur |
|---|:---:|:---:|
| S'authentifier (JWT) | ✅ | ✅ |
| Consulter le tableau de bord (KPI, état des bornes, rafraîchi toutes les 10 s) | ✅ | ✅ |
| Activer / désactiver une prise | ✅ | ✅ |
| Commander l'inclinaison du panneau solaire | ✅ | ✅ |
| Mettre le panneau en position de sécurité | ✅ | ✅ |
| Activer / désactiver le mode maintenance | ✅ | ✅ |
| Consulter et acquitter les alarmes | ✅ | ✅ |
| Consulter l'historique des mesures + export CSV | ✅ | ✅ |
| Consulter les sessions de recharge | ✅ | ✅ |
| Gérer les bornes (ajout, suppression) | | ✅ |
| Configurer les seuils d'alarme | | ✅ |
| Gérer les utilisateurs | | ✅ |

Un **simulateur** intégré joue le rôle des bornes physiques : toutes les 10 secondes, chaque borne en service envoie une mesure (puissance, production solaire, température). Les mesures sont comparées aux seuils et génèrent automatiquement des alarmes (avertissement ou critique).

### Diagramme de cas d'utilisation

![Cas d'utilisation](docs/cas-utilisation.png)

---

## Architecture

```
┌──────────────┐   HTTP /api    ┌───────────────────────┐   JPA    ┌──────────────┐
│  Angular 19  │ ─────────────▶ │  Spring Boot 3 (REST) │ ───────▶ │  PostgreSQL  │
│  (Nginx)     │ ◀───────────── │  Spring Security/JWT  │          └──────────────┘
└──────────────┘     JSON       │  Simulateur @Scheduled│
                                └───────────────────────┘
```

**Backend** (`backend/`) — architecture en couches :
- `controller/` : endpoints REST
- `service/` : logique métier (dont `AnalyseurSeuils`, la règle de détection d'alarmes)
- `repository/` : accès aux données avec Spring Data JPA
- `model/` : entités JPA (Borne, Prise, Mesure, Alarme, Recharge, Utilisateur)
- `config/` : sécurité JWT, gestion centralisée des erreurs (RFC 7807), données de démo
- `simulation/` : génération périodique des mesures

**Frontend** (`frontend/`) — Angular 19 en composants standalone, signals, intercepteur HTTP pour le JWT, guards de routes par rôle.

---

## Lancer le projet

### Option 1 — Docker (recommandé)

```bash
docker compose up --build
```

- Application : http://localhost:4200
- API : http://localhost:8080

### Option 2 — En local (sans Docker)

Prérequis : Java 21, Maven, Node.js 20+.

```bash
# Terminal 1 : backend (base H2 en mémoire, aucune installation de base de données)
cd backend
mvn spring-boot:run

# Terminal 2 : frontend
cd frontend
npm install
npm start
```

Puis ouvrir http://localhost:4200.

### Comptes de démonstration

| Identifiant | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123` | Administrateur |
| `technicien` | `tech123` | Technicien |

---

## API REST (extrait)

| Méthode | Endpoint | Rôle |
|---|---|---|
| `POST` | `/api/auth/login` | public |
| `GET` | `/api/dashboard` | connecté |
| `GET` | `/api/bornes` | connecté |
| `POST` | `/api/bornes` | admin |
| `PUT` | `/api/bornes/{id}/seuils` | admin |
| `PATCH` | `/api/bornes/{id}/maintenance` | connecté |
| `PATCH` | `/api/bornes/{id}/panneau` | connecté |
| `POST` | `/api/bornes/{id}/panneau/securite` | connecté |
| `PATCH` | `/api/bornes/prises/{priseId}` | connecté |
| `GET` | `/api/alarmes?actives=true` | connecté |
| `POST` | `/api/alarmes/{id}/acquitter` | connecté |
| `GET` | `/api/mesures?borneId=&debut=&fin=` | connecté |
| `GET` | `/api/mesures/export` | connecté (CSV) |
| `GET` | `/api/recharges` | connecté |
| `GET/POST/DELETE` | `/api/utilisateurs` | admin |

Exemple :

```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/dashboard
```

---

## Tests

```bash
cd backend
mvn test
```

- Tests unitaires de la détection d'alarmes (`AnalyseurSeuilsTest`)
- Tests d'intégration de la sécurité : accès sans token, mauvais mot de passe, droits technicien / administrateur (`ApiSecuriteTest`)

---

## Pistes d'amélioration

- Communication temps réel avec les bornes via MQTT ou WebSocket
- Graphiques d'évolution des mesures (Chart.js)
- Règles de priorité entre alarmes
- Pipeline CI GitHub Actions (build + tests)

---

👩‍💻 Réalisé par **Ikram Guesmi** — [LinkedIn](https://www.linkedin.com/in/ikram-guesmi75)
