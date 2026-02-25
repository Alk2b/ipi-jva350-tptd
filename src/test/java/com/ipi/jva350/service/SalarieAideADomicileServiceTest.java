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
class SalarieAideADomicileServiceTest {

    @Autowired
    private SalarieAideADomicileService service;

    @Autowired
    private SalarieAideADomicileRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testAjouteConge_integration() throws Exception {
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

    @Test
    void testCalculeLimiteEntrepriseCongesPermis_integration() {
        // Given : données en base pour que partCongesPrisTotauxAnneeNMoins1 retourne une valeur réelle
        // proportion = (10+15)/(25+25) = 0.5
        SalarieAideADomicile salarie1 = new SalarieAideADomicile();
        salarie1.setNom("Salarie1");
        salarie1.setCongesPayesAcquisAnneeNMoins1(25);
        salarie1.setCongesPayesPrisAnneeNMoins1(10);
        repository.save(salarie1);

        SalarieAideADomicile salarie2 = new SalarieAideADomicile();
        salarie2.setNom("Salarie2");
        salarie2.setCongesPayesAcquisAnneeNMoins1(25);
        salarie2.setCongesPayesPrisAnneeNMoins1(15);
        repository.save(salarie2);

        // When
        long limite = service.calculeLimiteEntrepriseCongesPermis(
            LocalDate.of(2026, 1, 1),
            25.0,
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2026, 1, 5),
            LocalDate.of(2026, 1, 9)
        );

        // Then
        assertTrue(limite > 0, "La limite doit être positive");
    }

    @Test
    void testCalculeLimiteEntrepriseCongesPermis_seniorPlusQueJunior() {
        // Given
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Salarie");
        salarie.setCongesPayesAcquisAnneeNMoins1(25);
        salarie.setCongesPayesPrisAnneeNMoins1(10);
        repository.save(salarie);

        // When : junior (1 an) vs senior (10 ans)
        long limiteJunior = service.calculeLimiteEntrepriseCongesPermis(
            LocalDate.of(2026, 1, 1), 25.0,
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 9)
        );
        long limiteSenior = service.calculeLimiteEntrepriseCongesPermis(
            LocalDate.of(2026, 1, 1), 25.0,
            LocalDate.of(2016, 1, 1),
            LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 9)
        );

        // Then : le senior a une limite plus élevée
        assertTrue(limiteSenior > limiteJunior);
    }
}
