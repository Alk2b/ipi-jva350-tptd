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
}