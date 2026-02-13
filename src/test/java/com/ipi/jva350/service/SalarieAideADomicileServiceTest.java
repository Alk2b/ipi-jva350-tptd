package com.ipi.jva350.service;

import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SalarieAideADomicileServiceTest {

    @Autowired
    private SalarieAideADomicileService service;

    @Autowired
    private SalarieAideADomicileRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testAjouteConge_integration() throws Exception {
        // Given : Création d'un salarié en base
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeNMoins1(200);
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0);
        salarie.setCongesPayesPrisAnneeNMoins1(0.0);
        service.creerSalarieAideADomicile(salarie);

        LocalDate dateDebut = LocalDate.of(2026, 1, 5);
        LocalDate dateFin = LocalDate.of(2026, 1, 9);

        // When : Ajout du congé
        service.ajouteConge(salarie, dateDebut, dateFin);

        // Then : Vérifications
        SalarieAideADomicile salarieRecupere = repository.findByNom("Dupont");
        assertNotNull(salarieRecupere);
        assertTrue(salarieRecupere.getCongesPayesPris().size() > 0);
        assertEquals(6.0, salarieRecupere.getCongesPayesPrisAnneeNMoins1());
    }
}
