# 🚀 TaskFlow API — Spring Boot 4 / Spring Framework 7

API REST de gestion de **taches et projets**, construite comme demonstration
complete des nouveautes de **Spring Boot 4.0.7** (Java 25, Jakarta EE 11,
Jackson 3, JSpecify, versioning d'API natif, resilience, virtual threads).

---

## 📦 Stack technique

| Composant | Version / Detail |
|---|---|
| Java | **25** (baseline minimum 17) |
| Spring Boot | **4.0.7** |
| Spring Framework | 7 |
| Spring Security | 7 (JWT stateless) |
| Spring Data JPA / Hibernate | 7.x |
| Base de donnees | H2 (en memoire) |
| Mapping objet | MapStruct **+** mapping manuel (2 approches comparees) |
| Documentation API | springdoc-openapi / Swagger UI |
| Lombok | oui |
| Build | Maven |

---

## 🔑 Configuration du secret JWT (important pour un usage reel)

Par defaut, le projet utilise un secret JWT **de demonstration**, code en dur
dans `application.properties`, pour que le projet fonctionne immediatement en local
(`git clone` + run, sans configuration supplementaire).

⚠️ **Ne jamais utiliser ce secret par defaut en production.**

Pour definir ton propre secret, exporte une variable d'environnement
`JWT_SECRET` (une cle aleatoire encodee en Base64, 256 bits minimum) avant
de lancer l'application :

```bash
# Generer une cle aleatoire de 256 bits encodee en base64 (Linux/Mac)
openssl rand -base64 32

# Puis exporter la variable
export JWT_SECRET="<la-cle-generee>"

# Optionnel : duree de validite du token (en millisecondes)
export JWT_EXPIRATION_MS=86400000

mvn spring-boot:run
```

Sous Windows (PowerShell) :
```powershell
$env:JWT_SECRET="<la-cle-generee>"
mvn spring-boot:run
```

Dans IntelliJ, tu peux aussi definir `JWT_SECRET` dans
**Run/Debug Configurations > Environment variables**.

---

## ✅ Prerequis (a installer avant de commencer)

1. **Java 25 (JDK)** — tu as deja prevu de l'installer, parfait.
   Verifie avec :
   ```bash
   java -version
   ```
2. **Maven 3.9+** (ou utilise le wrapper `mvnw` si tu en ajoutes un).
3. **IntelliJ IDEA** (Community ou Ultimate) avec le plugin **Lombok** active
   (Settings > Plugins > Lombok) et **"Annotation Processing"** active
   (Settings > Build, Execution, Deployment > Compiler > Annotation Processors
   > cocher "Enable annotation processing").
   C'est indispensable pour que Lombok **et** MapStruct generent leur code.

---

## ▶️ Lancer le projet

### Avec Maven
```bash
mvn clean spring-boot:run
```

### Depuis IntelliJ
1. Ouvre le dossier `taskflow-api` comme projet Maven (IntelliJ detecte le `pom.xml`).
2. Attends la fin de l'indexation / telechargement des dependances.
3. Lance `TaskflowApiApplication.main()`.

L'application demarre sur **http://localhost:8080**.

---

## 🔎 Acces utiles

| Ressource | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Console H2 | http://localhost:8080/h2-console |

**Connexion H2 :**
- JDBC URL : `jdbc:h2:mem:taskflowdb`
- User : `sa`
- Password : *(vide)*

---

## 👤 Comptes de demonstration (crees automatiquement au demarrage)

| Email                | Mot de passe | Role |
|----------------------|--------------|---|
| admin@taskflow.dev   | Admin123!    | ROLE_ADMIN |
| ibrahim@taskflow.dev | Ibrahim123!  | ROLE_USER |

Un projet *"Refonte API TaskFlow"* avec 4 taches est cree pour Ibrahim.

---

## 🔐 Authentification

### 1. Inscription
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "fullName": "Mamadou Ndiaye",
  "email": "mamadou@example.com",
  "password": "MotDePasse123"
}
```

### 2. Connexion
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "ibrahim@taskflow.dev",
  "password": "Ibrahim123!"
}
```

Reponse :
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": { "id": 2, "fullName": "Ibrahim Diao", "email": "ibrahim@taskflow.dev", "role": "ROLE_USER", ... }
}
```

### 3. Utiliser le token
Pour toutes les autres requetes, ajoute l'en-tete :
```
Authorization: Bearer <accessToken>
```

Dans Swagger UI : clique sur **"Authorize"** en haut a droite et colle le token
(sans le prefixe "Bearer ").

---

## 📚 Endpoints principaux

### Auth (publics)
| Methode | URL | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Creer un compte |
| POST | `/api/v1/auth/login` | Se connecter |

### Utilisateurs
| Methode | URL | Description |
|---|---|---|
| GET | `/api/v1/users` | Lister les utilisateurs (pour assigner des taches) |

### Projets
| Methode | URL | Description |
|---|---|---|
| POST | `/api/v1/projects` | Creer un projet |
| GET | `/api/v1/projects` | Lister mes projets (pagine) |
| GET | `/api/v1/projects/{id}` | Detail d'un projet (avec ses taches) |
| PUT | `/api/v1/projects/{id}` | Modifier un projet |
| DELETE | `/api/v1/projects/{id}` | Supprimer un projet |

### Taches
| Methode | URL | Description |
|---|---|---|
| POST | `/api/v1/tasks` | Creer une tache |
| GET | `/api/v1/tasks?projectId=1` | Lister les taches d'un projet (pagine) |
| GET | `/api/v1/tasks/me` | Mes taches assignees |
| GET | `/api/v1/tasks/{id}` | Detail d'une tache (v1, par defaut) |
| GET | `/api/v1/tasks/{id}` + header `X-API-Version: 2` | Detail d'une tache (v2) |
| PUT | `/api/v1/tasks/{id}` | Modifier une tache |
| PATCH | `/api/v1/tasks/{id}/status` | Changer uniquement le statut (kanban) |
| DELETE | `/api/v1/tasks/{id}` | Supprimer une tache |

---

## 🆕 Nouveautes Spring Boot 4 illustrees dans ce projet

### 1. Versioning d'API natif
Voir `ApiVersioningConfig` + `TaskController.findByIdV2`.
Strategie : en-tete HTTP `X-API-Version` (defaut = `1`).

```http
GET /api/v1/tasks/1
X-API-Version: 2
Authorization: Bearer ...
```

### 2. JSpecify (null-safety portee par l'ecosysteme)
Les champs optionnels des entites/DTOs (`description`, `dueDate`, `assignee`,
`jobTitle`...) sont annotes `@org.jspecify.annotations.Nullable`, ce qui rend
explicite — pour les outils ET pour les developpeurs Kotlin — quels champs
peuvent etre `null`.

### 3. Virtual Threads
Active via `spring.threads.virtual.enabled=true` dans `application.properties`.
Sur Java 21+/25, Tomcat traite chaque requete sur un thread virtuel,
ameliorant le debit pour les charges I/O (appels DB, etc.) sans changer
le code applicatif.

### 4. Jakarta EE 11 / Spring Security 7
- CSRF desactive explicitement pour une API stateless (`SecurityFilterChain`)
- Authentification stateless par JWT (`SessionCreationPolicy.STATELESS`)
- `DaoAuthenticationProvider` configure avec la nouvelle API
  (constructeur `(UserDetailsService)` + `setPasswordEncoder(...)`)

### 5. Modularisation des starters
Le projet n'utilise que les starters necessaires :
`spring-boot-starter-web`, `-data-jpa`, `-security`, `-validation`.

### 6. ⚠️ Piege JJWT vs Jackson 3 (a connaitre absolument)
La librairie `io.jsonwebtoken:jjwt` (v0.12.x) depend encore de **Jackson 2**,
ce qui entre en conflit avec **Jackson 3**, defaut de Spring Boot 4.
**Solution appliquee dans ce projet** : utiliser `jjwt-gson` au lieu de
`jjwt-jackson` comme module de (de)serialisation pour JJWT — cela elimine
toute dependance a Jackson cote JWT, sans impacter le reste de l'application
(qui utilise bien Jackson 3 pour les payloads REST).

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-gson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

---

## 🧩 Les deux approches de mapping (MapStruct vs Manuel)

Ce projet illustre **volontairement** les deux styles, sur des entites differentes,
pour pouvoir les comparer directement dans le code :

| Entite | Mapper utilise | Package |
|---|---|---|
| `User` | **MapStruct** (interface, genere a la compilation) | `mapper.mapstruct.UserMapStructMapper` |
| `Task` | **MapStruct** (avec `@Mapping` pour les champs imbriques) | `mapper.mapstruct.TaskMapStructMapper` |
| `Project` | **Manuel** (classe Java classique, conversions explicites) | `mapper.manual.ProjectManualMapper` |

Les equivalents existent aussi dans l'autre style pour comparaison directe :
- `mapper.manual.UserManualMapper` (equivalent manuel de `UserMapStructMapper`)
- `mapper.manual.TaskManualMapper` (equivalent manuel de `TaskMapStructMapper`)
- `mapper.mapstruct.ProjectMapStructMapper` (equivalent declaratif de `ProjectManualMapper`)

### Pourquoi MapStruct ?
- Le code de mapping est **genere a la compilation** (zero reflexion, tres performant)
- Moins de code repetitif (`boilerplate`) pour les mappings simples (memes noms de champs)
- Les erreurs de mapping (champ manquant, type incompatible) sont detectees **a la compilation**

### Pourquoi le mapping manuel ?
- Controle total, lisible pas a pas, ideal pour de la logique conditionnelle complexe
- Pas de generation de code -> debogage plus simple, pas de "classe magique" `...Impl`
- Aucune dependance/annotation processor supplementaire

---

## 🏗️ Architecture du projet

```
com.diao.taskflowapi
├── configs/              # Security, OpenAPI, JPA Auditing, API Versioning, DataSeeder
├── controllers/          # REST controllers (Auth, User, Project, Task)
├── dtos/
│   ├── requests/         # DTOs d'entree (records + validation Bean Validation)
│   └── responses/        # DTOs de sortie (records)
├── entities/              # Entites JPA (User, Project, Task, BaseEntity)
├── enums/               # Role, TaskStatus, TaskPriority
├── exceptions/           # Exceptions metier + GlobalExceptionHandler
├── mappers/
│   ├── autos/       # Mappers declaratifs (MapStruct)
│   └── manuals/           # Mappers manuels (classes Java classiques)
├── repositories/          # Spring Data JPA repositories
├── securities/            # JWT (service, filtre, UserDetails)
└── services/
    ├── *.java           # Interfaces (contrats metier)
    └── impls/             # Implementations
```

---

## 🔮 Prochaine etape

Cette base monolithique est volontairement simple et bien decoupee
(couches claires : controller -> service -> repository, DTOs separes
des entites, mappers isoles) afin de pouvoir **extraire facilement** des
modules vers une architecture microservices par la suite (ex: un service
"Users", un service "Projects/Tasks", une gateway, etc.).