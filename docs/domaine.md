# Modèle Métier

## Contexte

Le projet modélise la gestion des congés payés pour les **salariés aides à domicile**,
selon les règles de la convention collective du particulier employeur
([source](https://femme-de-menage.ooreka.fr/comprendre/conges-payes-femme-de-menage)).

## Règles métier clés

- L'**année de congés** va du **1er juin** au **31 mai** (pas l'année civile)
- Un salarié acquiert **2,5 jours de congés par mois** travaillé
- Pour avoir droit aux congés, il faut avoir travaillé **au moins 10 jours** sur l'année de référence
- Seuls les jours **ouvrables** (lundi–samedi, hors jours fériés) sont décomptés

## Classes principales

### `Entreprise`

Classe utilitaire (finale, non instanciable) contenant les règles de l'entreprise.

| Méthode | Description |
|---|---|
| `joursFeries(LocalDate)` | Retourne la liste des 11 jours fériés français pour une année donnée |
| `estJourFerie(LocalDate)` | Indique si une date est un jour férié |
| `proportionPondereeDuMois(LocalDate)` | Proportion cumulée de congés autorisée jusqu'à ce mois (poids x2 en juillet-août) |
| `getPremierJourAnneeDeConges(LocalDate)` | Retourne le 1er juin de l'année de congés correspondante |
| `estDansPlage(LocalDate, LocalDate, LocalDate)` | Vérifie si une date est dans un intervalle inclusif |

### `SalarieAideADomicile`

Entité JPA représentant un salarié.

| Champ | Description |
|---|---|
| `moisEnCours` | Mois courant pour le calcul de la paie |
| `moisDebutContrat` | Date d'embauche (utilisée pour le calcul de l'ancienneté) |
| `congesPayesAcquisAnneeNMoins1` | Congés acquis lors de l'année de référence précédente |
| `congesPayesPrisAnneeNMoins1` | Congés déjà pris sur ces acquis |

### `SalarieAideADomicileService`

Service Spring contenant la logique métier principale.

| Méthode | Description |
|---|---|
| `ajouteConge()` | Vérifie et enregistre une demande de congé |
| `calculeLimiteEntrepriseCongesPermis()` | Calcule le plafond de congés autorisés par l'entreprise |
| `clotureMois()` | Clôture le mois courant et met à jour les compteurs |