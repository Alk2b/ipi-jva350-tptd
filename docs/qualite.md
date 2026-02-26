# Rapport Qualité

## Analyse SonarCloud

Le projet est analysé automatiquement à chaque push sur `master` via GitHub Actions.

[Voir le rapport complet sur SonarCloud](https://sonarcloud.io/summary/new_code?id=Alk2b_ipi-jva350-tptd)

## Métriques

| Métrique | Badge |
|---|---|
| Quality Gate | ![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Alk2b_ipi-jva350-tptd&metric=alert_status) |
| Bugs | ![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Alk2b_ipi-jva350-tptd&metric=bugs) |
| Vulnerabilities | ![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Alk2b_ipi-jva350-tptd&metric=vulnerabilities) |
| Code Smells | ![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Alk2b_ipi-jva350-tptd&metric=code_smells) |
| Coverage | ![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Alk2b_ipi-jva350-tptd&metric=coverage) |
| Duplicated Lines | ![Duplicated Lines](https://sonarcloud.io/api/project_badges/measure?project=Alk2b_ipi-jva350-tptd&metric=duplicated_lines_density) |

## Corrections apportées

### Bugs corrigés dans `Entreprise.java`

- **`getPremierJourAnneeDeConges`** : `d.getMonthValue()` → `d.getYear()` dans la branche `true` du ternaire
- **`estJourFerie`** : suppression du dead code `bissextile` inutile, simplification en `joursFeries(jour).contains(jour)`

### Code Smells traités

- Suppression des modificateurs `public` inutiles dans les interfaces et classes de test
- Suppression des variables inutilisées
- Ajout de logging (`slf4j`) aux points clés du service

## Intégration continue

Le workflow GitHub Actions lance à chaque push :

1. Compilation Maven (`mvn package verify`)
2. Analyse SonarCloud avec envoi des métriques