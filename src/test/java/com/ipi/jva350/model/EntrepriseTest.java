package com.ipi.jva350.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EntrepriseTest {

    // TESTS TDD - ALLAN (estDansPlage)

    // test TDD Allan (AVANT DE VOIR LA MÉTHODE)
    // Ce test est écrit et se base sur la javaDoc,
    // Javadoc dit : "Calcule si une date donnée est dans une plage (intervalle) de date (inclusif)"
    // notes :
    // - Une date au milieu de la plage -> devrait retourner true
    // - Une date après la plage -> devrait retourner false
    // - Une date avant la plage -> devrait retourner false
    // Ce test va probablement échouer car la méthode n'existe pas encore ou lance une exception
    @ParameterizedTest(name = "{0} est dans la plage ?")
    @CsvSource({
        "2026-01-05, 2026-01-01, 2026-01-10, true",   // Date au milieu
        "2026-01-11, 2026-01-01, 2026-01-10, false",  // Date après
        "2025-12-31, 2026-01-01, 2026-01-10, false"   // Date avant
    })
    void testEstDansPlage_naif(String dateStr, String debutStr, String finStr, boolean attendu) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate debut = LocalDate.parse(debutStr);
        LocalDate fin = LocalDate.parse(finStr);
        boolean resultat = Entreprise.estDansPlage(date, debut, fin);
        assertEquals(attendu, resultat);
    }

    // test TDD Allan (APRES AVOIR VU LA MÉTHODE et la faire marcher)
    // Sans cette méthode, j'aurais sûrement codé la logique directement sans vérifier les cas limites
    // (ex: date == début/fin ou plage d'un seul jour).
    // Le TDD m'a forcé à anticiper ces pièges avant d'écrire le code,
    // ce qui évite le biais du "ça marche sur un test, donc c'est fini".
    // Maintenant que j'ai réfléchi à tout les cas j'écris les tests aux complet
    @ParameterizedTest(name = "{0} -> {3}")
    @CsvSource({
        "2026-01-05, 2026-01-01, 2026-01-10, true",   // Date au milieu de la plage
        "2026-01-01, 2026-01-01, 2026-01-10, true",   // EDGE CASE : Date = début (inclusif)
        "2026-01-10, 2026-01-01, 2026-01-10, true",   // EDGE CASE : Date = fin (inclusif)
        "2025-12-31, 2026-01-01, 2026-01-10, false",  // Date juste avant la plage
        "2026-01-11, 2026-01-01, 2026-01-10, false",  // Date juste après la plage
        "2026-01-05, 2026-01-05, 2026-01-05, true",   // EDGE CASE : Plage d'UN SEUL JOUR (debut = fin)
        "2026-01-04, 2026-01-05, 2026-01-05, false",  // Avant une plage d'un seul jour
        "2026-01-06, 2026-01-05, 2026-01-05, false"   // Après une plage d'un seul jour
    })
    void testEstDansPlage(String dateStr, String debutStr, String finStr, boolean attendu) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate debut = LocalDate.parse(debutStr);
        LocalDate fin = LocalDate.parse(finStr);
        boolean resultat = Entreprise.estDansPlage(date, debut, fin);
        assertEquals(attendu, resultat, "Pour date=" + dateStr + ", plage=[" + debutStr + ", " + finStr + "]");
    }

    // TESTS PARAMÉTRÉS - ANGELICA

    @ParameterizedTest(name = "{0} est un jour férié")
    @CsvSource({
        "2026-01-01", "2026-04-06", "2026-05-01", "2026-05-08",
        "2026-05-15", "2026-05-25", "2026-07-14", "2026-08-15",
        "2026-11-01", "2026-11-11", "2026-12-25"
    })
    void testEstJourFerie_joursFeries(LocalDate jour) {
        assertTrue(Entreprise.estJourFerie(jour));
    }

    // L'année de congés va du 1er juin au 31 mai. La fonction accumule des poids :
    // - Juillet & Août ont un poids 20 (mois de vacances d'été)
    // - Tous les autres mois ont un poids 8
    @ParameterizedTest(name = "{0} n'est pas un jour férié")
    @CsvSource({
        "2026-01-02", "2026-04-05", "2026-05-02",
        "2026-07-15", "2026-12-24", "2026-03-15"
    })
    void testEstJourFerie_joursOrdinaires(LocalDate jour) {
        assertFalse(Entreprise.estJourFerie(jour));
    }

    @ParameterizedTest(name = "mois {0} → proportion attendue {1}")
    @CsvSource({
        "2026-06-01, 0.0667",
        "2026-07-01, 0.2333",
        "2026-08-01, 0.4000",
        "2026-09-01, 0.4667",
        "2026-12-01, 0.6667",
        "2026-01-01, 0.7333",
        "2026-05-01, 1.0000"
    })
    void testProportionPondereeDuMois(LocalDate mois, double proportionAttendue) {
        assertEquals(proportionAttendue, Entreprise.proportionPondereeDuMois(mois), 0.0001);
    }

    @Test
    void testGetPremierJourAnneeDeConges_null() {
        assertNull(Entreprise.getPremierJourAnneeDeConges(null));
    }

    @ParameterizedTest(name = "{0} → premier jour année de congés = {1}")
    @CsvSource({
        "2026-06-01, 2026-06-01",
        "2026-07-15, 2026-06-01",
        "2026-12-31, 2026-06-01",
        "2026-01-01, 2025-06-01",
        "2026-05-31, 2025-06-01",
        "2026-05-01, 2025-06-01"
    })
    void testGetPremierJourAnneeDeConges(LocalDate input, LocalDate expected) {
        assertEquals(expected, Entreprise.getPremierJourAnneeDeConges(input));
    }
}
