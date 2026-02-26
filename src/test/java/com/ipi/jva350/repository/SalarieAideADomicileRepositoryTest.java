package com.ipi.jva350.repository;

import com.ipi.jva350.model.SalarieAideADomicile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalarieAideADomicileRepositoryTest {

    @Autowired
    private SalarieAideADomicileRepository repository;

    @BeforeEach
    void setUp() {
        // Nettoie la base avant chaque test
        repository.deleteAll();
    }

    @Test
    void testFindByNom_retourneUnResultat() {
        // Given : Sauvegarde d'un salarié dans la base de données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        repository.save(salarie);

        // When : Recherche par nom
        SalarieAideADomicile salarieTrouve = repository.findByNom("Dupont");

        // Then : Vérification que le résultat n'est pas null
        assertNotNull(salarieTrouve);
    }

    @Test
    void testFindByNom_retourneLeBonNom() {
        // Given : Sauvegarde d'un salarié dans la base de données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        repository.save(salarie);

        // When : Recherche par nom
        SalarieAideADomicile salarieTrouve = repository.findByNom("Dupont");

        // Then : Vérification que le nom correspond
        assertEquals("Dupont", salarieTrouve.getNom());
    }

    @Test
    void testPartCongesPrisTotauxAnneeNMoins1_calculeCorrectement() {
        // Given : proportion attendue = (10 + 15) / (25 + 25) = 0.5
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
        Double proportion = repository.partCongesPrisTotauxAnneeNMoins1();

        // Then
        assertNotNull(proportion);
        assertEquals(0.5, proportion, 0.01);
    }

    @Test
    void testPartCongesPrisTotauxAnneeNMoins1_baseVide() {
        // Given : base vide (setUp a appelé deleteAll)

        // When
        Double proportion = repository.partCongesPrisTotauxAnneeNMoins1();

        // Then : aucune donnée = null
        assertNull(proportion);
    }
}