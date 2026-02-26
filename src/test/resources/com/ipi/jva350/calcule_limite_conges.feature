Feature: Calcul de la limite de congés permis par l'entreprise
  La règle stipule qu'un salarié gagne 1 jour de limite de congés par année d'ancienneté, jusqu'à un maximum de 10.

  Scenario Outline: L'ancienneté augmente la limite de congés
    Given un salarié embauché il y a <annees> ans
    When on calcule la limite de congés avec 20 jours acquis
    Then le bonus d'ancienneté ajouté à la limite est de <bonus_attendu> jours

    Examples:
      | annees | bonus_attendu |
      | 0      | 0             |
      | 1      | 1             |
      | 5      | 5             |
      | 10     | 10            |
      | 15     | 10            |