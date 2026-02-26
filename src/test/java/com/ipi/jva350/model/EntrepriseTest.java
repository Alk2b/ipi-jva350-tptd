package com.ipi.jva350.model;

import com.ipi.jva350.model.Entreprise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public final class EntrepriseTest {
    @ParameterizedTest(name = "{0} est un jour férié")
    @CsvSource({
            "2026-01-01",
            "2026-04-06",
            "2026-05-01",
            "2026-05-08",
            "2026-05-15",
            "2026-05-25",
            "2026-07-14",
            "2026-08-15",
            "2026-11-01",
            "2026-11-11",
            "2026-12-25"
    })
    void testEstJourFerie_joursFeries(LocalDate jour) {
        assertTrue(Entreprise.estJourFerie(jour));
    }
            //    L'année de congés va du 1er juin au 31 mai. La fonction accumule des poids :
            //            - Juillet & Août ont un poids 20 (mois de vacances d'été)
            //            - Tous les autres mois ont un poids 8
    @ParameterizedTest(name = "{0} n'est pas un jour férié")
    @CsvSource({
            "2026-01-02",
            "2026-04-05",
            "2026-05-02",
            "2026-07-15",
            "2026-12-24",
            "2026-03-15"
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