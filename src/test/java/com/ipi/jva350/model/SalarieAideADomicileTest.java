package com.ipi.jva350.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;

public class SalarieAideADomicileTest {

    @Test
    public void testALegalementDroitADesCongesPayes_avecMoinsDe10Jours() {
        // Given : Mise en place de l'environnement du test et de ses données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setJoursTravaillesAnneeNMoins1(5);
        
        // When : Comportement à tester
        Boolean aDroit = salarie.aLegalementDroitADesCongesPayes();
        
        // Then : Comparaison du résultat avec celui attendu
        assertEquals(false, aDroit);
    }

    @Test
    public void testALegalementDroitADesCongesPayes_avecExactement10Jours() {
        // Given : Mise en place de l'environnement du test et de ses données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setJoursTravaillesAnneeNMoins1(10);
        
        // When : Comportement à tester
        Boolean aDroit = salarie.aLegalementDroitADesCongesPayes();
        
        // Then : Comparaison du résultat avec celui attendu (10 jours exactement ne suffit pas)
        assertEquals(true, aDroit);
    }

    @Test
    public void testALegalementDroitADesCongesPayes_avecPlusDe10Jours() {
        // Given : Mise en place de l'environnement du test et de ses données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setJoursTravaillesAnneeNMoins1(11);
        
        // When : Comportement à tester
        Boolean aDroit = salarie.aLegalementDroitADesCongesPayes();
        
        // Then : Comparaison du résultat avec celui attendu
        assertEquals(true, aDroit);
    }

    @ParameterizedTest
    @CsvSource({
        "2026-01-05, 2026-01-09, 6",  // Lundi au vendredi + samedi (tous jours sauf dimanche)
        "2026-01-05, 2026-01-11, 6",  // Lundi au dimanche - samedi inclus, dimanche exclu
        "2026-01-10, 2026-01-11, 0",  // Weekend - aucun jour décompté
        "2026-05-01, 2026-05-05, 3",  // Avec vendredi 1er mai férié - 3 jours (sam 2, lun 4, mar 5)
    })
    public void testCalculeJoursDeCongeDecomptesPourPlage(String dateDebutStr, String dateFinStr, int joursAttendus) {
        // Given : Mise en place de l'environnement du test et de ses données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        LocalDate dateDebut = LocalDate.parse(dateDebutStr);
        LocalDate dateFin = LocalDate.parse(dateFinStr);
        
        // When : Comportement à tester
        LinkedHashSet<LocalDate> joursDecomptes = salarie.calculeJoursDeCongeDecomptesPourPlage(dateDebut, dateFin);
        
        // Then : Comparaison du résultat avec celui attendu
        assertEquals(joursAttendus, joursDecomptes.size(), 
            "Pour la plage du " + dateDebutStr + " au " + dateFinStr);
    }
    
}
