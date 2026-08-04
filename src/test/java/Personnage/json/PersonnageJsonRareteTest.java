package Personnage.json;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verrouille le mapping rarete -> multiplicateur de stats (multiplicateurRarete, prive dans
 * PersonnageJson), verifie ici via l'API publique du constructeur plutot qu'en changeant sa
 * visibilite. Ce mapping (C=1.00, B=1.30, A=1.40, S=1.50, SS=1.75, UR=2.00) est aussi duplique
 * a la main dans ~30 fichiers perso_*.java : une regression ici n'empeche pas une incoherence
 * dans ces fichiers, mais garantit au moins que la source JSON reste correcte.
 */
class PersonnageJsonRareteTest {

    @ParameterizedTest
    @CsvSource({
        "C,  100.0",
        "B,  130.0",
        "A,  140.0",
        "S,  150.0",
        "SS, 175.0",
        "UR, 200.0",
        // Rarete inconnue : le mapping retombe sur le comportement par defaut (C, 1.00).
        "ZZ, 100.0",
    })
    void multiplicateurRareteAppliqueALAttaqueDeBase(String rarete, double attaqueAttendue) {
        PersonnageData data = new PersonnageData();
        data.nom = "TestPerso";
        data.type = "Mage";
        data.role = "DPS";
        data.rarete = rarete;
        data.stats = new PersonnageData.StatsData();
        data.stats.attaque = 100;

        PersonnageJson p = new PersonnageJson(data);

        assertEquals(attaqueAttendue, p.getAttaque(), 0.001);
    }
}
