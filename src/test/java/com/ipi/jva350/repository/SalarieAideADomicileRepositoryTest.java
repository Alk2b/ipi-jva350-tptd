package com.ipi.jva350.repository;

import com.ipi.jva350.model.SalarieAideADomicile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // Junit 4 : @RunWith(SpringRunner.class)
@SpringBootTest

class SalarieAideADomicileRepositoryTest {

    @Autowired
    private SalarieAideADomicileRepository repository;

    @Test
    void testFindByNom() {
        // Given : Sauvegarde d'un salarié dans la base de données
        SalarieAideADomicile salarie = new SalarieAideADomicile();
        salarie.setNom("Dupont");
        repository.save(salarie);

        // When : Recherche par nom
        SalarieAideADomicile salarieTrouve = repository.findByNom("Dupont");

        // Then : Vérification
        assertNotNull(salarieTrouve);
        assertEquals("Dupont", salarieTrouve.getNom());
    }
}