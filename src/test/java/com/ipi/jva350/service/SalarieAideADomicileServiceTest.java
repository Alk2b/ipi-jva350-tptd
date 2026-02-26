package com.ipi.jva350.service;

import com.ipi.jva350.exception.SalarieException;
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

    // ========== Tests pour creerSalarieAideADomicile() ==========

    @Test
    void testCreerSalarieAideADomicile_avecNomExistant_doitLancerException() {
        // Given : un salarié existant en base
        SalarieAideADomicile salarieExistant = new SalarieAideADomicile();
        salarieExistant.setNom("Dupont");
        salarieExistant.setMoisEnCours(LocalDate.of(2026, 1, 1));
        salarieExistant.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarieExistant.setJoursTravaillesAnneeNMoins1(200);
        repository.save(salarieExistant);

        // When : tentative de création avec le même nom
        SalarieAideADomicile nouveauSalarie = new SalarieAideADomicile();
        nouveauSalarie.setNom("Dupont");

        // Then : une exception doit être levée (couvre logger.error)
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.creerSalarieAideADomicile(nouveauSalarie);
        });
        assertTrue(exception.getMessage().contains("Un salarié existe déjà avec le nom"));
    }

    @Test
    void testCreerSalarieAideADomicile_avecIdDejaPresent_doitLancerException() {
        // Given : un nouveau salarié avec un ID déjà renseigné
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setId(999L);
        salarie.setNom("Martin");

        // When/Then : une exception doit être levée (couvre logger.warn)
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.creerSalarieAideADomicile(salarie);
        });
        assertTrue(exception.getMessage().contains("L'id ne doit pas être fourni"));
    }

    // ========== Tests pour ajouteConge() - toutes les branches d'exception ==========

    @Test
    void testAjouteConge_sansDroitAuxConges_doitLancerException() {
        // Given : un salarié sans droit aux congés (moins de 10 jours travaillés)
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Stagiaire");
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2026, 1, 1));
        salarie.setJoursTravaillesAnneeNMoins1(5); // moins de 10 jours
        salarie.setCongesPayesAcquisAnneeNMoins1(0);
        repository.save(salarie);

        LocalDate dateDebut = LocalDate.of(2026, 1, 5);
        LocalDate dateFin = LocalDate.of(2026, 1, 9);

        // When/Then : une exception doit être levée
        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.ajouteConge(salarie, dateDebut, dateFin);
        });
        assertTrue(exception.getMessage().contains("N'a pas légalement droit à des congés payés"));
    }

    @Test
    void testAjouteConge_aucunJourDecompte_doitLancerException() {
        // Given : un salarié avec droits aux congés
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 6)); // lundi
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeNMoins1(200);
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0);
        repository.save(salarie);

        // When/Then : demande de congé sur un week-end uniquement (aucun jour décompté)
        LocalDate samedi = LocalDate.of(2026, 1, 3);
        LocalDate dimanche = LocalDate.of(2026, 1, 4);

        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.ajouteConge(salarie, samedi, dimanche);
        });
        assertTrue(exception.getMessage().contains("Pas besoin de congés"));
    }

    @Test
    void testAjouteConge_avantMoisEnCours_doitLancerException() {
        // Given : un salarié avec mois en cours = février
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        salarie.setMoisEnCours(LocalDate.of(2026, 2, 1)); // février
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeNMoins1(200);
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0);
        repository.save(salarie);

        // When/Then : demande de congé en janvier (avant mois en cours)
        LocalDate dateDebut = LocalDate.of(2026, 1, 5);
        LocalDate dateFin = LocalDate.of(2026, 1, 9);

        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.ajouteConge(salarie, dateDebut, dateFin);
        });
        assertTrue(exception.getMessage().contains("Pas possible de prendre de congé avant le mois en cours"));
    }

    @Test
    void testAjouteConge_dansPlusieursAnneesDeConges_doitLancerException() {
        // Given : un salarié avec mois en cours = avril 2026
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        salarie.setMoisEnCours(LocalDate.of(2026, 4, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeNMoins1(200);
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0);
        repository.save(salarie);

        // When/Then : demande de congé qui s'étend trop loin dans la nouvelle année de congés
        // L'année de congés va du 1er juin au 31 mai
        LocalDate dateDebut = LocalDate.of(2026, 4, 5);
        LocalDate dateFin = LocalDate.of(2026, 6, 10); // plus d'un jour dans la nouvelle année

        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.ajouteConge(salarie, dateDebut, dateFin);
        });
        assertTrue(exception.getMessage().contains("Pas possible de prendre de congé dans l'année de congés suivante"));
    }

    @Test
    void testAjouteConge_depasseCongesAcquis_doitLancerException() {
        // Given : un salarié avec seulement 5 jours de congés acquis
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeNMoins1(200);
        salarie.setCongesPayesAcquisAnneeNMoins1(5.0); // seulement 5 jours
        repository.save(salarie);

        // When/Then : demande de 10 jours de congés (plus que les 5 acquis)
        LocalDate dateDebut = LocalDate.of(2026, 1, 5); // lundi
        LocalDate dateFin = LocalDate.of(2026, 1, 16); // vendredi (10 jours ouvrés)

        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.ajouteConge(salarie, dateDebut, dateFin);
        });
        assertTrue(exception.getMessage().contains("dépassent les congés acquis en année N-1"));
    }

    @Test
    void testAjouteConge_depasseLimiteEntreprise_doitLancerException() {
        // Given : un salarié junior (0 an d'ancienneté) avec 25 jours de congés
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Junior");
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 1)); // début d'année
        salarie.setMoisDebutContrat(LocalDate.of(2026, 1, 1)); // 0 an d'ancienneté
        salarie.setJoursTravaillesAnneeNMoins1(200);
        salarie.setCongesPayesAcquisAnneeNMoins1(25.0);
        repository.save(salarie);

        // When/Then : demande de trop de congés trop tôt dans l'année (dépasse limite entreprise)
        LocalDate dateDebut = LocalDate.of(2026, 1, 5);
        LocalDate dateFin = LocalDate.of(2026, 1, 30); // environ 20 jours ouvrés

        SalarieException exception = assertThrows(SalarieException.class, () -> {
            service.ajouteConge(salarie, dateDebut, dateFin);
        });
        assertTrue(exception.getMessage().contains("dépassent la limite des règles de l'entreprise"));
    }

    // ========== Tests pour clotureMois() et clotureAnnee() ==========

    @Test
    void testClotureMois_incrementeCorrectement() throws Exception {
        // Given : un salarié avec valeurs initiales
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Martin");
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeN(100.0);
        salarie.setCongesPayesAcquisAnneeN(10.0);
        service.creerSalarieAideADomicile(salarie);

        // When : clôture du mois avec 20 jours travaillés
        service.clotureMois(salarie, 20.0);

        // Then : vérifications
        SalarieAideADomicile salarieRecupere = repository.findByNom("Martin");
        assertEquals(120.0, salarieRecupere.getJoursTravaillesAnneeN());
        assertEquals(12.5, salarieRecupere.getCongesPayesAcquisAnneeN());
        assertEquals(LocalDate.of(2026, 2, 1), salarieRecupere.getMoisEnCours());
    }

    @Test
    void testClotureAnnee_transfertCorrectementLesValeurs() throws Exception {
        // Given : un salarié en mai (dernier mois avant clôture d'année de congés)
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Senior");
        salarie.setMoisEnCours(LocalDate.of(2026, 5, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
        salarie.setJoursTravaillesAnneeN(200.0);
        salarie.setCongesPayesAcquisAnneeN(25.0);
        salarie.setJoursTravaillesAnneeNMoins1(180.0);
        salarie.setCongesPayesAcquisAnneeNMoins1(22.0);
        service.creerSalarieAideADomicile(salarie);

        // When : clôture du mois de mai (déclenche clôture d'année)
        service.clotureMois(salarie, 20.0);

        // Then : les valeurs N doivent passer en N-1, et N remis à zéro
        SalarieAideADomicile salarieRecupere = repository.findByNom("Senior");
        assertEquals(220.0, salarieRecupere.getJoursTravaillesAnneeNMoins1()); // 200 + 20
        assertEquals(27.5, salarieRecupere.getCongesPayesAcquisAnneeNMoins1()); // 25 + 2.5
        assertEquals(0.0, salarieRecupere.getJoursTravaillesAnneeN());
        assertEquals(0.0, salarieRecupere.getCongesPayesAcquisAnneeN());
        assertEquals(0.0, salarieRecupere.getCongesPayesPrisAnneeNMoins1());
        assertEquals(LocalDate.of(2026, 6, 1), salarieRecupere.getMoisEnCours());
    }
}
