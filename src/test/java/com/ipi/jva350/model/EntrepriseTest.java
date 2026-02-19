package com.ipi.jva350.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class EntrepriseTest {

    // test TDD Allan (AVANT DE VOIR LA MÉTHODE)
    // Ce test est écrit et se base sur la javaDoc,
    // Javadoc dit : "Calcule si une date donnée est dans une plage (intervalle) de date (inclusif)"
    // 
    // notes :
    // - Une date au milieu de la plage -> devrait retourner true
    // - Une date après la plage -> devrait retourner false
    // - Une date avant la plage -> devrait retourner false
    //
    // Ce test va probablement échouer car la méthode n'existe pas encore ou lance une exception

    @ParameterizedTest
    @CsvSource({
        "2026-01-05, 2026-01-01, 2026-01-10, true",   // Date au milieu
        "2026-01-11, 2026-01-01, 2026-01-10, false",  // Date après
        "2025-12-31, 2026-01-01, 2026-01-10, false"   // Date avant
    })
    public void testEstDansPlage_naif(String dateStr, String debutStr, String finStr, boolean attendu) {
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


    // PHASE 3 : TESTS TDD COMPLETS
    // Maintenant que j'ai réfléchi à tout les cas j'écris les tests aux complet
    // (toujours AVANT l'implémentation finale !)

    @ParameterizedTest
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
    public void testEstDansPlage(String dateStr, String debutStr, String finStr, boolean attendu) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate debut = LocalDate.parse(debutStr);
        LocalDate fin = LocalDate.parse(finStr);
        
        boolean resultat = Entreprise.estDansPlage(date, debut, fin);
        
        assertEquals(attendu, resultat, 
            "Pour date=" + dateStr + ", plage=[" + debutStr + ", " + finStr + "]");
    }
}
