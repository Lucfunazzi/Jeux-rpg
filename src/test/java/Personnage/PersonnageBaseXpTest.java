package Personnage;

import Personnage.json.PersonnageData;
import Personnage.json.PersonnageJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verrouille la courbe d'XP (experienceCumuleePourNiveau, palier de niveau, mur endgame niveau 59)
 * et l'effet de monterDeNiveau()/gagnerExperience() sur les stats. C'est la courbe dont
 * CourbeChapitres derive les totaux d'XP par chapitre : toute regression ici desynchronise
 * silencieusement les paliers de chapitre, comme les 3 bugs corriges avant cette suite de tests.
 */
class PersonnageBaseXpTest {

    private PersonnageJson creerPersonnage(double vie, double attaque, double defense, double vitesse) {
        PersonnageData data = new PersonnageData();
        data.nom = "TestPerso";
        data.type = "Mage";
        data.role = "DPS";
        data.rarete = "C"; // multiplicateur 1.00, pour isoler la courbe de niveau des tests de rarete
        data.stats = new PersonnageData.StatsData();
        data.stats.vie = vie;
        data.stats.attaque = attaque;
        data.stats.defense = defense;
        data.stats.vitesse = vitesse;
        return new PersonnageJson(data);
    }

    @Test
    void experienceCumuleePourNiveau_debut() {
        assertEquals(0L, PersonnageBase.experienceCumuleePourNiveau(1));
        assertEquals(300L, PersonnageBase.experienceCumuleePourNiveau(2));
        assertEquals(700L, PersonnageBase.experienceCumuleePourNiveau(3));
        assertEquals(1200L, PersonnageBase.experienceCumuleePourNiveau(4));
    }

    @Test
    void experienceCumuleePourNiveau_murEndgameNiveau59() {
        // Cout normal (+100/niveau) jusqu'au niveau 58, puis mur plat de 20000 XP/niveau a partir de 59.
        assertEquals(182700L, PersonnageBase.experienceCumuleePourNiveau(59));
        assertEquals(202700L, PersonnageBase.experienceCumuleePourNiveau(60));
        assertEquals(222700L, PersonnageBase.experienceCumuleePourNiveau(61));
    }

    @Test
    void monterDeNiveau_appliqueLesGainsDeStatsEtLePalierXp() {
        PersonnageJson p = creerPersonnage(200, 100, 50, 80);

        p.monterDeNiveau();

        assertEquals(2, p.getNiveau());
        assertEquals(400, p.getExperienceMax());
        assertEquals(210.0, p.getVieMax(), 0.001);
        assertEquals(210.0, p.getVie(), 0.001);
        assertEquals(105.0, p.getAttaque(), 0.001);
        assertEquals(52.5, p.getDefense(), 0.001);
        assertEquals(82.4, p.getVitesse(), 0.001);
    }

    @Test
    void gagnerExperience_peutFaireMonterPlusieursNiveauxEnUnSeulGain() {
        PersonnageJson p = creerPersonnage(200, 100, 50, 80);

        p.gagnerExperience(1000);

        // 1000 XP : 300 pour passer niveau 1->2, puis 400 pour 2->3, reste 300 (< palier niveau 3 = 500).
        assertEquals(3, p.getNiveau());
        assertEquals(300, p.getExperience());
        assertEquals(500, p.getExperienceMax());
    }
}
