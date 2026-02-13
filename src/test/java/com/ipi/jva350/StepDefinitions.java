package com.ipi.jva350;

import io.cucumber.java.en.*;
import com.ipi.jva350.model.SalarieAideADomicile;
import com.ipi.jva350.service.SalarieAideADomicileService;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IsItFriday {
    static String isItFriday(String today) {
        return "Nope";
    }
}

public class StepDefinitions {

    private String today;
    private String actualAnswer;
    private SalarieAideADomicile salarie;
    private SalarieAideADomicileService service = new SalarieAideADomicileService();

    @Given("today is Sunday")
    public void today_is_Sunday() {
        today = "Sunday";
    }

    @When("I ask whether it's Friday yet")
    public void i_ask_whether_it_s_Friday_yet() {
        actualAnswer = IsItFriday.isItFriday(today);
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
        assertEquals(joursAttendus, salarie.getJoursTravaillesAnneeN(), 0.01);
    }

    @Then("le salarié devrait avoir {double} congés acquis en année N")
    public void verifier_conges_acquis(Double congesAttendus) {
        assertEquals(congesAttendus, salarie.getCongesPayesAcquisAnneeN(), 0.01);
    }

}
