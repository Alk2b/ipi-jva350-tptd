# Stratégie de Tests

## Vue d'ensemble

Le projet applique une stratégie de tests en plusieurs couches,
du plus isolé au plus intégré.

```
Tests d'acceptation (Cucumber)
        ↑
Tests d'intégration (Spring Boot Test + H2)
        ↑
Tests Mockito (isolation totale, sans Spring)
        ↑
Tests unitaires paramétrés (JUnit 5)
```

## Tests unitaires — `EntrepriseTest`

Tests paramétrés avec `@ParameterizedTest` + `@CsvSource` pour les méthodes statiques d'`Entreprise`.

### `estJourFerie`

Vérifie les 11 jours fériés légaux français et des jours ordinaires.

Cas limite important : le **dimanche de Pâques** n'est PAS férié, seul le lundi l'est.

### `proportionPondereeDuMois`

Vérifie la proportion cumulée pour chaque mois de l'année de congés.

Logique : juillet et août ont un poids **20** (vacances d'été),
les autres mois ont un poids **8**. Total sur l'année = 120.

| Mois | Proportion |
|---|---|
| Juin (début) | 6,7% |
| Juillet | 23,3% |
| Août | 40% |
| Mai (fin) | 100% |

### `getPremierJourAnneeDeConges`

Vérifie le calcul du 1er jour de l'année de congés (toujours le 1er juin).

Cas limites :
- `null` en entrée → `null` en sortie
- Exactement le 1er juin → même année
- 31 mai → année précédente

**Bug corrigé** : la version originale utilisait `d.getMonthValue()` au lieu de `d.getYear()`,
retournant une date dans l'an 7 après J.-C. pour tout mois > 5.

## Tests Mockito — `SalarieAideADomicileServiceMockitoTest`

Test de `calculeLimiteEntrepriseCongesPermis()` **sans base de données**.

Le repository est mocké avec `@Mock` + `@InjectMocks` via `@ExtendWith(MockitoExtension.class)`.
Le seul appel base de données (`partCongesPrisTotauxAnneeNMoins1()`) est stubbé avec `when(...).thenReturn(...)`.

| Test | Ce qu'il vérifie |
|---|---|
| `ancienneteNulle` | Valeur concrète attendue (2 jours) avec 0 an d'ancienneté |
| `seniorPlusHautQueJunior` | Le bonus ancienneté augmente bien la limite |
| `anciennetePlafonneeA10Ans` | `Math.min(anciennete, 10)` — 10 ans = 20 ans |
| `congeEnAout_plusHautQueJuin` | Les poids d'été (20 vs 8) se traduisent en limite plus haute |
| `partCongesNull_lanceNPE` | Bug documenté : `null` du repository → `NullPointerException` |

## Tests d'intégration — `SalarieAideADomicileServiceTest`

Tests `@SpringBootTest` avec base H2 en mémoire.
Couvrent `ajouteConge()`, `clotureMois()`, `clotureAnnee()` et `creerSalarieAideADomicile()`.

## Tests d'acceptation — Cucumber

Features dans `src/test/resources/com/ipi/jva350/` :

- `cloture_mois.feature` — clôture mensuelle d'un salarié
- `calcule_limite_conges.feature` — calcul de la limite de congés par ancienneté