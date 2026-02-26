package com.ipi.jva350.service;
import com.ipi.jva350.repository.SalarieAideADomicileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalarieAideADomicileServiceMockitoTest {
    @Mock
    private SalarieAideADomicileRepository salarieAideADomicileRepository;

    @InjectMocks
    private SalarieAideADomicileService service;

//    valeur concrète (ancienneté 0, congé début juin)
//
//    Calculer a la main pour vérifier :
//    proportionPonderee(Juin) = 8/120 = 0.0667
//    limiteConges = 0.0667 × 25 = 1.667
//    proportionMoisEnCours = (6-6)/12 = 0.0,  retard = 0.0 → bonus/malus = 0
//    distanceMois = 0 → marge = 0
//    ancienneté = 0 → bonus = 0
//    BigDecimal(1.667).round() = Math.round(1.667) = 2
//                                  → resultat attendu : 2
    @Test
    void testCalculeLimite_ancienneteNulle_retourneValeurAttendue() {
        when(salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1()).thenReturn(0.0);

        long result = service.calculeLimiteEntrepriseCongesPermis(
                LocalDate.of(2026, 6, 1),
                25.0,
                LocalDate.of(2026, 6, 1), // 0 an d'ancienneté
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 7)
        );

        assertEquals(2, result);
    }
    // senior > junior : meme parametres, seul moisDebutContrat change
    @Test
    void testCalculeLimite_seniorPlusHautQueJunior() {
        when(salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1()).thenReturn(0.0);

        long limiteJunior = service.calculeLimiteEntrepriseCongesPermis(
                LocalDate.of(2026, 6, 1), 25.0,
                LocalDate.of(2026, 6, 1), // 0 an
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 7)
        );
        long limiteSenior = service.calculeLimiteEntrepriseCongesPermis(
                LocalDate.of(2026, 6, 1), 25.0,
                LocalDate.of(2021, 6, 1), // 5 ans
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 7)
        );

        assertTrue(limiteSenior > limiteJunior);
    }
    // anciennete plafonnee à 10 ans : 10 ans = 20 ans (le Math.min(anciennete, 10) dans le code)
    @Test
    void testCalculeLimite_anciennetePlafonneeA10Ans() {
        when(salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1()).thenReturn(0.0);

        long limite10ans = service.calculeLimiteEntrepriseCongesPermis(
                LocalDate.of(2026, 6, 1), 25.0,
                LocalDate.of(2016, 6, 1), // 10 ans
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 7)
        );
        long limite20ans = service.calculeLimiteEntrepriseCongesPermis(
                LocalDate.of(2026, 6, 1), 25.0,
                LocalDate.of(2006, 6, 1), // 20 ans (bonus plafonné à 10)
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 7)
        );

        assertEquals(limite10ans, limite20ans);
    }
    // Conges en aout > conges en juin : les poids d'ete (20 vs 8) doivent se traduire en limite plus haute.
    @Test
    void testCalculeLimite_congeEnAout_plusHautQueCongeEnJuin() {
        when(salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1()).thenReturn(0.0);

        long limiteJuin = service.calculeLimiteEntrepriseCongesPermis(
                LocalDate.of(2026, 6, 1), 25.0,
                LocalDate.of(2021, 6, 1),
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 7)
        );
        long limiteAout = service.calculeLimiteEntrepriseCongesPermis(
                LocalDate.of(2026, 6, 1), 25.0,
                LocalDate.of(2021, 6, 1),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 14)
        );

        assertTrue(limiteAout > limiteJuin);
    }

    // partCongesPrisTotaux = null → NPE revéle : le repository peut retourner null si la base est vide (query SUM sur table vide). Le service ne gere pas ce cas →
    //  NullPointerException. documentation de ce bug par ce test.
    @Test
    void testCalculeLimite_partCongesNull_lanceNullPointerException() {
        when(salarieAideADomicileRepository.partCongesPrisTotauxAnneeNMoins1()).thenReturn(null);
        LocalDate moisEnCours = LocalDate.of(2026, 6, 1);
        double congesPayesAcquisAnneeNMoins1 = 25.0;
        LocalDate moisDebutContrat = LocalDate.of(2021, 6, 1);
        LocalDate premierJourDeConge = LocalDate.of(2026, 6, 1);
        LocalDate dernierJourDeConge = LocalDate.of(2026, 6, 7);

        assertThrows(NullPointerException.class, () ->
                service.calculeLimiteEntrepriseCongesPermis(
                        moisEnCours, congesPayesAcquisAnneeNMoins1,
                        moisDebutContrat,
                        premierJourDeConge, dernierJourDeConge
                )
        );
    }
}
