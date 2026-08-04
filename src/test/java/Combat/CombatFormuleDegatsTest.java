package Combat;

import Personnage.json.PersonnageData;
import Personnage.json.PersonnageJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verrouille Combat.calculerDegats() : degats bruts d'une attaque de base = 100% ATK.
 * La defense n'est plus soustraite ici : elle est appliquee une seule fois, en aval,
 * dans PersonnageBase.subirDegats() (comme pour les speciales/ultimes qui passent par
 * Combat.appliquerDegatsAvecLog). L'appliquer aussi ici l'aurait fait compter deux fois
 * sur l'attaque de base.
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
    void degatsBrutsEgauxALAttaqueQuandLaDefenseEstFaible() {
        PersonnageJson attaquant = creerPersonnage(100, 30);
        PersonnageJson cible     = creerPersonnage(0, 30);

        assertEquals(100.0, Combat.calculerDegats(attaquant, cible), 0.001);
    }

    @Test
    void degatsBrutsRestentEgauxALAttaqueQuandLaDefenseEstProche() {
        PersonnageJson attaquant = creerPersonnage(100, 99);
        PersonnageJson cible     = creerPersonnage(0, 99);

        // La defense de la cible n'intervient pas dans calculerDegats : elle sera
        // appliquee ensuite, une seule fois, dans subirDegats().
        assertEquals(100.0, Combat.calculerDegats(attaquant, cible), 0.001);
    }

    @Test
    void degatsBrutsRestentEgauxALAttaqueMemeQuandLaDefenseDepasseLAttaque() {
        PersonnageJson attaquant = creerPersonnage(100, 200);
        PersonnageJson cible     = creerPersonnage(0, 200);

        assertEquals(100.0, Combat.calculerDegats(attaquant, cible), 0.001);
    }
}
