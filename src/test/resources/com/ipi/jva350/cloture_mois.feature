Feature: Clôture de mois d'un salarié aide à domicile
  Un salarié aide à domicile doit voir son mois clôturé avec mise à jour des jours travaillés et congés acquis

  Scenario: Clôture d'un mois avec 20 jours travaillés
    Given un salarié "Martin" avec 100 jours travaillés en année N et 10 congés acquis
    When je clôture le mois avec 20 jours travaillés
    Then le salarié devrait avoir 120 jours travaillés en année N
    And le salarié devrait avoir 12.5 congés acquis en année N
