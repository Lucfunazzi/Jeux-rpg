package Combat;

import Effets.BuffAttaque;
import Equipement.Equipement;
import Personnage.json.PersonnageData;
import Personnage.json.PersonnageJson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test de combat bout-en-bout : equipement + buff + Combat.attaquer(), pour servir de filet
 * de securite avant de toucher au bloc de calcul de stats de PersonnageBase (getAttaque/
 * getDefense/...), qui reste volontairement non refactore tant que ce genre de couverture
 * n'existe pas. Esquive/blocage/critique sont neutralises (mis a 0) cote attaquant/cible pour
 * rendre le resultat deterministe malgre les tirages aleatoires de Combat.attaqueTouche/
 * estCritique/subirDegats. La defense n'est appliquee qu'une seule fois, dans subirDegats().
 */
class CombatEquipementBuffIntegrationTest {

    private PersonnageJson creerPersonnage(double attaque, double defense, double vie) {
        PersonnageData data = new PersonnageData();
        data.nom = "TestPerso";
        data.type = "Mage";
        data.role = "DPS";
        data.rarete = "C"; // multiplicateur 1.00
        data.stats = new PersonnageData.StatsData();
        data.stats.attaque = attaque;
        data.stats.defense = defense;
        data.stats.vie = vie;
        data.stats.tauxCritique = 0;   // jamais de critique
        data.stats.esquive = 0;        // jamais d'esquive
        data.stats.blocage = 0;        // jamais de blocage
        return new PersonnageJson(data);
    }

    private Equipement arme(double bonusATK) {
        return new Equipement("Arme de test", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.EPEE, bonusATK, 0, 0, 0);
    }

    @Test
    void lEquipementSeCumuleALAttaqueDeBase() {
        PersonnageJson attaquant = creerPersonnage(150, 0, 500);
        attaquant.equiper(arme(50));

        assertEquals(200.0, attaquant.getAttaque(), 0.001);
    }

    @Test
    void unBuffDAttaqueSeCumuleAvecLEquipement() {
        PersonnageJson attaquant = creerPersonnage(150, 0, 500);
        attaquant.equiper(arme(50));
        attaquant.ajouterEffet(new BuffAttaque(0.20, 3));

        // (150 * 1.20) + 50 = 230 : le buff (%) s'applique sur la base AVANT d'ajouter le bonus d'equipement.
        assertEquals(230.0, attaquant.getAttaque(), 0.001);
    }

    @Test
    void combatBoutEnBoutAvecEquipementEtBuffAppliqueLesDegatsAttendus() {
        PersonnageJson attaquant = creerPersonnage(150, 0, 500);
        attaquant.equiper(arme(50));
        attaquant.ajouterEffet(new BuffAttaque(0.20, 3));
        assertEquals(230.0, attaquant.getAttaque(), 0.001);

        PersonnageJson cible = creerPersonnage(0, 80, 1000);
        List<String> log = new ArrayList<>();

        boolean touche = Combat.attaquer(attaquant, cible, log);

        assertTrue(touche, "L'esquive est desactivee (0%), l'attaque doit toucher.");
        // calculerDegats = attaque = 230 (100% ATK, la defense n'est plus soustraite ici)
        // subirDegats applique la mitigation de defense une seule fois : 100/(100+80*0.5) = 100/140
        double degatsAttendus = 230.0 * (100.0 / 140.0);
        assertEquals(1000.0 - degatsAttendus, cible.getVie(), 0.001);
    }
}
