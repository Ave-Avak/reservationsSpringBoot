# 📋 PID Reservations — État d'avancement

> **Dernière mise à jour** : 16 mai 2026  
> **Stack** : Spring Boot 3.5.13 · Java 17 · MariaDB (XAMPP) · Thymeleaf · Flyway · Spring Security · Lombok · Bootstrap 5.3  
> **Repo perso** : Ave-Avak/reservationsSpringBoot

## 🎯 Vision globale

App de réservation de spectacles pour le PID école (EPFC/ICC).
6 profils métier prévus : Internaute, Membre, Affilié, Critique presse, Producteur, Administrateur.

## ✅ État au 16 mai 2026

### Chapitres terminés

| Ch. | Description | Statut |
|---|---|---|
| 1 | Démarrage projet | ✅ |
| 2 | CRUD Artist (Thymeleaf, validation) | ✅ |
| 3 | Notifications flash | ✅ |
| 4 | Auth Spring Security (login/register/reset/profil) | ✅ |
| 5 | Mapping JPA complet (12 tables) + Catalogue public + Réservations | ✅ |

### Fonctionnalités opérationnelles

- 🏠 Page d'accueil
- 🎭 Catalogue public `/shows` (4 spectacles bookables)
- 📋 Détail spectacle `/shows/{slug}` avec représentations à venir
- 🎬 Catalogue artistes `/artists` (CRUD admin)
- 🔐 Inscription, connexion, déconnexion, reset password via MailTrap
- 👤 Profil utilisateur (`/profile`) : édition, changement password, suppression
- 🎫 Page "Mes réservations" (`/profile/reservations`)
- 📝 Formulaire de réservation (`/reservations/new?representationId=X`)
- ❌ Annulation de réservation PENDING

## 🗂️ Architecture des fichiers

src/main/java/be/iccbxl/pid/reservations/
├── ReservationsApplication.java
├── config/
│   ├── SecurityConfig.java         # Règles auth + autorisation
│   └── ThymeleafConfig.java        # Layout dialect
├── controller/
│   ├── ArtistController.java       # CRUD artist
│   ├── AuthController.java         # /login
│   ├── HomeController.java         # /
│   ├── PasswordResetController.java
│   ├── ProfileController.java      # /profile, /profile/reservations
│   ├── RegistrationController.java # /register
│   ├── ReservationController.java  # /reservations/new, POST /reservations
│   └── ShowController.java         # /shows, /shows/{slug}
├── dto/
│   ├── PasswordChangeDto.java
│   ├── ReservationFormDto.java
│   ├── UserProfileDto.java
│   └── UserRegistrationDto.java
├── model/
│   ├── Artist.java                 # ManyToOne via ArtistType
│   ├── ArtistType.java             # Entité-association
│   ├── Locality.java               # @OneToMany locations
│   ├── Location.java               # ManyToOne Locality + OneToMany Shows/Reps
│   ├── PasswordResetToken.java
│   ├── Representation.java         # 2x ManyToOne (Show, Location)
│   ├── Reservation.java            # Entité-association User↔Representation
│   ├── ReservationStatus.java      # ENUM (PENDING, CONFIRMED, CANCELLED, REFUNDED)
│   ├── Role.java
│   ├── Show.java                   # ManyToOne Location + OneToMany Reps
│   ├── Type.java                   # OneToMany ArtistType
│   └── User.java                   # ManyToMany Role + OneToMany Reservation
├── repository/                     # 11 JpaRepository (Artist, User, Role, PasswordResetToken, Type, Locality, Location, Show, Representation, ArtistType, Reservation)
├── service/                        # Services métier correspondants
└── validation/
├── PasswordMatches.java
└── PasswordMatchesValidator.java

## 🗃️ Migrations Flyway appliquées

| Version | Description |
|---|---|
| V0 | dummy initial |
| V1.1 / V1.2 | artists + seeds |
| V2 | users + roles + users_roles |
| V3 | insert admin de test |
| V4 | password_reset_tokens |
| V5 | insert 3 rôles métier (AFFILIATE, PRESS, PRODUCER) |
| V6 | types (8 types pré-insérés) |
| V7 | localities (10 villes belges) |
| V8 | locations (5 lieux bruxellois) |
| V9 | shows (5 spectacles dont Huis Clos non bookable) |
| V10 | representations (15 dates programmées) |
| V11 | artist_type (table de jointure initiale) |
| V12 | refactor artist_type en entité + ajout artist_type_show |
| V13 | reservations |
| V14 | seed 3 réservations test pour admin |

## ⚙️ Conventions techniques importantes

### Entités JPA
- ✅ Utiliser `@Getter @Setter` (PAS `@Data`) pour éviter les boucles infinies avec relations bilatérales
- ✅ `equals()` / `hashCode()` basés sur `id` uniquement
- ✅ Setters JPA simples (`this.x = x`) — PAS de synchro inverse qui touche les collections lazy
- ✅ Toujours déclarer `@OneToMany(mappedBy=...)` côté inverse
- ✅ Méthodes `addX()` / `removeX()` sur les entités "owner" de OneToMany

### Repositories
- ✅ Toujours `@Query` avec `JOIN FETCH` quand un controller ou template accède aux relations
- ✅ Sinon → `LazyInitializationException` (bug n°1 en JPA)

### Types Java ↔ SQL
| SQL | Java |
|---|---|
| YEAR | java.time.Year |
| SMALLINT | Short |
| BIGINT | Long |
| INT | Integer |
| DATETIME | LocalDateTime |
| TINYINT(1) | Boolean |

### Slugify
```java
private static final Slugify SLUGIFY = Slugify.builder().build();
private static String generateSlug(String input) {
    return SLUGIFY.slugify(input);
}
```

### Sécurité Spring
- `permitAll()` : `/`, `/login`, `/register`, `/forgot-password`, `/reset-password/**`, `/artists`, `/artists/{id}`, `/shows`, `/shows/**`, ressources statiques
- `hasRole("ADMIN")` : `/artists/new`, `/artists/{id}/edit`, `/artists/{id}/delete`
- `authenticated()` : `/profile/**`, `/reservations/**`
- CSRF activé (formulaires en POST avec token automatique Thymeleaf)
- BCryptPasswordEncoder pour les passwords

## 🔐 Comptes de test

| Login | Password | Rôles | Réservations |
|---|---|---|---|
| admin | password | ROLE_USER + ROLE_ADMIN | 3 (Ayiti, Le Petit Prince, Hamlet) |

## 🌐 Routes principales

### Publiques
- `GET /` — accueil
- `GET /shows` — catalogue
- `GET /shows/{slug}` — détail
- `GET /artists` — liste artistes
- `GET /artists/{id}` — détail artiste

### Auth
- `GET /login` · `POST /login`
- `GET /register` · `POST /register`
- `GET /forgot-password` · `POST /forgot-password`
- `GET /reset-password/{token}` · `POST /reset-password`
- `POST /logout`

### Membre (authentifié)
- `GET /profile` — profil
- `POST /profile/update`
- `POST /profile/password`
- `POST /profile/delete`
- `GET /profile/reservations` — mes réservations
- `POST /profile/reservations/{id}/cancel`
- `GET /reservations/new?representationId=X`
- `POST /reservations`

### Admin uniquement
- `GET /artists/new` · `POST /artists`
- `GET /artists/{id}/edit` · `POST /artists/{id}/edit`
- `POST /artists/{id}/delete`

## 🛑 Bugs courants déjà rencontrés et résolus

1. **Flyway checksum mismatch** suite à DevTools qui détecte un fichier de migration vide
   → Solution : `DELETE FROM flyway_schema_history WHERE version='X';` puis relance
   → Prévention : préparer le contenu complet AVANT premier Ctrl+S

2. **LazyInitializationException** sur entités relationnelles
   → Solution : `@Query` avec `JOIN FETCH` dans le repository

3. **Constructor Slugify() undefined** (lib v3 vs v2)
   → Solution : `Slugify.builder().build()` au lieu de `new Slugify()`

4. **Schema-validation : wrong column type**
   → Solution : aligner types Java sur types SQL (YEAR→Year, SMALLINT→Short)

5. **83 erreurs Lombok après création fichier vide**
   → Solution : remplir le fichier `.java` avec son contenu complet, le compilateur Java casse Lombok si un fichier source est invalide

## 🚀 Prochaines étapes (priorisées)

### Priorité 1 — Compléter l'app fonctionnellement
- [ ] **CRUD admin pour Shows** : créer/éditer/supprimer spectacles depuis l'UI
- [ ] **CRUD admin pour Locations** : gérer les lieux
- [ ] **CRUD admin pour Representations** : gérer les dates programmées
- [ ] **Données ArtistType + ArtistType_Show** : relier artistes aux spectacles

### Priorité 2 — Polish
- [ ] Page d'erreur 404 personnalisée
- [ ] Page d'erreur 500 personnalisée  
- [ ] Améliorer la page d'accueil (mise en avant des spectacles)
- [ ] Remplacer les `via.placeholder.com` cassés par de vrais posters
- [ ] Implémenter `@StrongPassword` validator (regex 8+ chars, maj/min/chiffre/spécial)
- [ ] Tâche `@Scheduled` pour nettoyer les tokens password reset expirés

### Priorité 3 — Phase suivante (Roadmap API)
- [ ] Module REST avec Spring HATEOAS
- [ ] Endpoints `/api/artists`, `/api/shows`, `/api/admin/artists` etc.
- [ ] Sécurité par rôle ROLE_AFFILIATE pour l'API
- [ ] Tests via Talend API Tester

## 📚 Documents de référence du projet

Tous les documents sont uploadés dans le Project Claude :
- `PID-ProjetReservations__3_.docx` — cahier des charges complet
- `Roadmap-SpringBoot3__2_.docx` — roadmap principale (Ch.1-4)
- `Roadmap-SpringBoot3_Mapping__1_.docx` — roadmap mapping (Ch.5)
- `Roadmap-SpringBoot3_API__1_.docx` — roadmap API REST
- Diagrammes : ClassDiagram, schémas BDD, use cases, contexte, navigation, Gantt

---

## 💬 Pour reprendre après une pause

Si tu reviens après plusieurs jours :
1. Lis ce fichier en entier pour te remettre dans le bain
2. Vérifie l'état avec `git log --oneline -20` pour voir les derniers commits
3. Lance `mvn spring-boot:run` et navigue sur http://localhost:8080
4. Connecte-toi avec `admin` / `password` pour tester l'admin
5. Choisis la prochaine tâche dans la section "Prochaines étapes"

## 🎓 Apprentissages clés acquis

- Mapping JPA bilatéral (OneToMany ↔ ManyToOne)
- ManyToMany avec et sans propriétés sur la relation
- Entité-association (pattern utilisé pour ArtistType et Reservation)
- LazyInitializationException et solution JOIN FETCH
- Cascade types (PERSIST, ALL, orphanRemoval)
- Bean Validation (NotNull, Min, Max, Email, Size, custom validator)
- Spring Security (auth + autorisation + CSRF)
- BCrypt pour les passwords
- Flyway migrations versionnées
- Thymeleaf avec layout dialect et fragments
- Conventions snake_case (SQL) ↔ camelCase (Java) via @Column