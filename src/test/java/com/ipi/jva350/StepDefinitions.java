package com.ipi.jva350;

import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.service.SalarieAideADomicileService;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(classes = Jva350Application.class)
public class StepDefinitions {

    private String actualAnswer;
    
    private SalarieAideADomicile salarie;
    
    @Autowired
    private SalarieAideADomicileService service;

    @Autowired
    private SalarieAideADomicileRepository repository;

    @Given("today is Sunday")
    public void today_is_Sunday() {
    }

    @When("I ask whether it's Friday yet")
    public void i_ask_whether_it_s_Friday_yet() {
        actualAnswer = "Nope";
    }

    @Then("I should be told {string}")
    public void i_should_be_told(String expectedAnswer) {
        assertEquals(expectedAnswer, actualAnswer);
    }

    @Given("un salarié {string} avec {int} jours travaillés en année N et {int} congés acquis")
    public void un_salarie_avec_jours_travailles(String nom, Integer joursTravailles, Integer congesAcquis) {
        salarie = new SalarieAideADomicile();
        salarie.setNom(nom);
        salarie.setJoursTravaillesAnneeN(joursTravailles);
        salarie.setCongesPayesAcquisAnneeN(congesAcquis);
        salarie.setMoisEnCours(LocalDate.of(2026, 1, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2020, 1, 1));
    }

    @When("je clôture le mois avec {int} jours travaillés")
    public void je_cloture_le_mois(Integer joursTravailles) throws Exception {
        service.clotureMois(salarie, joursTravailles); 
    }

    @Then("le salarié devrait avoir {int} jours travaillés en année N")
    public void verifier_jours_travailles(Integer joursAttendus) {
        assertEquals(joursAttendus.doubleValue(), salarie.getJoursTravaillesAnneeN(), 0.01);
    }

    @Then("le salarié devrait avoir {double} congés acquis en année N")
    public void verifier_conges_acquis(Double congesAttendus) {
        assertEquals(congesAttendus, salarie.getCongesPayesAcquisAnneeN(), 0.01);
    }

    private long limiteBase;
    private long limiteCalculee;

    @Given("un salarié embauché il y a {int} ans")
    public void un_salarie_embauche_il_y_a_ans(Integer anciennete) {
        repository.deleteAll();
        SalarieAideADomicile employeFictif = new SalarieAideADomicile();
        employeFictif.setNom("Employe Fictif");
        employeFictif.setCongesPayesAcquisAnneeNMoins1(25.0);
        employeFictif.setCongesPayesPrisAnneeNMoins1(10.0);
        repository.save(employeFictif);

        salarie = new SalarieAideADomicile();
        salarie.setMoisEnCours(LocalDate.of(2026, 7, 1));
        salarie.setMoisDebutContrat(LocalDate.of(2026 - anciennete, 7, 1));
    }

    @When("on calcule la limite de congés avec {int} jours acquis")
    public void on_calcule_la_limite_de_conges_avec_jours_acquis(Integer congesAcquis) {
        limiteBase = service.calculeLimiteEntrepriseCongesPermis(
            salarie.getMoisEnCours(), 
            congesAcquis.doubleValue(), 
            LocalDate.of(2026, 7, 1), 
            LocalDate.of(2026, 8, 1), 
            LocalDate.of(2026, 8, 15)
        );

        limiteCalculee = service.calculeLimiteEntrepriseCongesPermis(
            salarie.getMoisEnCours(), 
            congesAcquis.doubleValue(), 
            salarie.getMoisDebutContrat(), 
            LocalDate.of(2026, 8, 1), 
            LocalDate.of(2026, 8, 15)
        );
    }

    @Then("le bonus d'ancienneté ajouté à la limite est de {int} jours")
    public void le_bonus_d_anciennete_ajoute_a_la_limite_est_de_jours(Integer bonusAttendu) {
        long bonusReel = limiteCalculee - limiteBase;
        assertEquals(bonusAttendu.longValue(), bonusReel);
    }
}