package Combat;

import Personnage.json.PersonnageData;
import Personnage.json.PersonnageJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verrouille Combat.calculerDegats() : max(attaque * 0.10, attaque - defense).
 * La rarete est fixee a "C" (multiplicateur 1.00) pour que getAttaque()/getDefense()
 * refletent exactement les valeurs de stats saisies, sans interference d'un autre systeme.
 */
class CombatFormuleDegatsTest {

    private PersonnageJson creerPersonnage(double attaque, double defense) {
        PersonnageData data = new PersonnageData();
        data.nom = "TestPerso";
        data.type = "Mage";
        data.role = "DPS";
        data.rarete = "C";
        data.stats = new PersonnageData.StatsData();
        data.stats.attaque = attaque;
        data.stats.defense = defense;
        return new PersonnageJson(data);
    }

    @Test
    void degatsBrutsQuandAttaqueDepasseLargementLaDefense() {
        PersonnageJson attaquant = creerPersonnage(100, 30);
        PersonnageJson cible     = creerPersonnage(0, 30);

        assertEquals(70.0, Combat.calculerDegats(attaquant, cible), 0.001);
    }

    @Test
    void degatsPlancherA10PourCentDeLAttaqueQuandLaDefenseEstProche() {
        PersonnageJson attaquant = creerPersonnage(100, 99);
        PersonnageJson cible     = creerPersonnage(0, 99);

        // attaque - defense = 1, plus petit que le plancher de 10% de l'attaque (10) : le plancher s'applique.
        assertEquals(10.0, Combat.calculerDegats(attaquant, cible), 0.001);
    }

    @Test
    void degatsPlancherMemeQuandLaDefenseEgaleOuDepasseLAttaque() {
        PersonnageJson attaquant = creerPersonnage(100, 200);
        PersonnageJson cible     = creerPersonnage(0, 200);

        // attaque - defense est negatif : on ne descend jamais sous 10% de l'attaque (pas de degats negatifs).
        assertEquals(10.0, Combat.calculerDegats(attaquant, cible), 0.001);
    }
}
