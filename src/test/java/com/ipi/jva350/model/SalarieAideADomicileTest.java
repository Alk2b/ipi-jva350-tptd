package com.ipi.jva350.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;
import com.ipi.jva350.service.SalarieAideADomicileService;
import com.ipi.jva350.exception.SalarieException;

@ExtendWith(MockitoExtension.class)
class SalarieAideADomicileTest {

    @Mock
    private SalarieAideADomicileRepository salarieAideADomicileRepository;

    @InjectMocks
    private SalarieAideADomicileService salarieAideADomicileService;

    @ParameterizedTest(name = "Test aLegalementDroitADesCongesPayes avec {0} jours travaillés, doit retourner {1}")
    @CsvSource({
            "5, false",
            "10, true",
            "11, true"
    })
    void testALegalementDroitADesCongesPayes(int joursTravailles, boolean expected) {
        // Given : Mise en place de l'environnement du test et de ses données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setJoursTravaillesAnneeNMoins1(joursTravailles);

        // When : Comportement à tester
        boolean aDroit = salarie.aLegalementDroitADesCongesPayes();

        // Then : Comparaison du résultat avec celui attendu
        assertEquals(expected, aDroit);
    }

    @ParameterizedTest
    @CsvSource({
        "2026-01-05, 2026-01-09, 6",  // Lundi au vendredi + samedi (tous jours sauf dimanche)
        "2026-01-05, 2026-01-11, 6",  // Lundi au dimanche - samedi inclus, dimanche exclu
        "2026-01-10, 2026-01-11, 0",  // Weekend - aucun jour décompté
        "2026-05-01, 2026-05-05, 3",  // Avec vendredi 1er mai férié - 3 jours (sam 2, lun 4, mar 5)
    })
    void testCalculeJoursDeCongeDecomptesPourPlage(String dateDebutStr, String dateFinStr, int joursAttendus) {
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
    /**
     * Tests avec mocks : Testez de manière mockée (sans dépendance à la base de données) la méthode SalarieAideADomicileService.ajouteConge().
     * Elle fait plusieurs choses et il y a donc plusieurs choses à tester.
     */
    @Test
    void testAjouteConge_avecCongesValides() throws Exception {
        // Given : Configuration du salarié et du mock
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setId(1L);
        salarie.setNom("Dupont");
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeNMoins1(200);
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0);
        salarie.setCongesPayesPrisAnneeNMoins1(0.0);

        LocalDate dateDebut = LocalDate.of(2026, 1, 5);
        LocalDate dateFin = LocalDate.of(2026, 1, 9);

        // Mock de la méthode partCongesPrisTotauxAnneeNMoins1 utilisée dans ajouteConge
        Mockito.when(salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1())
                .thenReturn(1.0);

        // When : Ajout du congé
        salarieAideADomicileService.ajouteConge(salarie, dateDebut, dateFin);

        // Then : Vérifications
        Mockito.verify(salarieAideADomicileRepository).save(salarie);
        
        // Vérifier que le nombre de jours de congé pris a été mis à jour
        assertTrue(salarie.getCongesPayesPris().size() > 0);
        assertEquals(6.0, salarie.getCongesPayesPrisAnneeNMoins1());
    }

    @Test
    void testAjouteConge_sansDroitAuxConges() {
        // Given : Salarié n'ayant pas droit aux congés
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setJoursTravaillesAnneeNMoins1(0); // Moins de 10 jours = pertinent
        
        LocalDate dateDebut = LocalDate.of(2026, 1, 5);
        LocalDate dateFin = LocalDate.of(2026, 1, 9);

        // When/Then : Exception attendue
        assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, dateDebut, dateFin);
        });
    }

    @Test
    void testAjouteConge_aucunJourDecompte() {
        // Given : Congé uniquement le dimanche (pas décompté)
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setJoursTravaillesAnneeNMoins1(200);
        
        LocalDate dateDebut = LocalDate.of(2026, 1, 11); // Dimanche
        LocalDate dateFin = LocalDate.of(2026, 1, 11);   // Dimanche

        // When/Then : Exception attendue
        assertThrows(SalarieException.class, () -> {
            salarieAideADomicileService.ajouteConge(salarie, dateDebut, dateFin);

        });
    }
}

