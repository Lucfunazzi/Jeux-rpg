package Personnage;

import Equipement.Equipement;
import Personnage.json.PersonnageData;
import Personnage.json.PersonnageJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verrouille l'asymetrie DEF vs ATK/VIT dans le calcul des stats de combat : ATK et VIT
 * beneficient du bonus de set en % (6 et 5 pieces de meme rarete) et du bonus de pierre, DEF
 * n'en beneficie d'aucun des deux (design du jeu, pas un oubli). getAttaque/getDefense/
 * getVitesse partagent maintenant un meme helper prive (statCombinee) ; ces tests garantissent
 * que ce partage n'a pas fait fuiter le bonus de set/pierre vers la DEF par erreur.
 */
class PersonnageBaseStatCombineeTest {

    private PersonnageJson creerPersonnage(double attaque, double defense, double vitesse) {
        PersonnageData data = new PersonnageData();
        data.nom = "TestPerso";
        data.type = "Mage";
        data.role = "DPS";
        data.rarete = "C";
        data.stats = new PersonnageData.StatsData();
        data.stats.attaque = attaque;
        data.stats.defense = defense;
        data.stats.vitesse = vitesse;
        return new PersonnageJson(data);
    }

    private Equipement piece(Equipement.Slot slot, Equipement.Rarete rarete, double def) {
        return new Equipement("Piece " + slot, slot, rarete, Equipement.TypeArme.AUCUN, 0, def, 0, 0);
    }

    @Test
    void leBonusDeSetEnPourcentNeSAppliquePasALaDefense() {
        PersonnageJson p = creerPersonnage(0, 100, 0);
        // 6/6 pieces de rarete A : declenche le bonus de set ATK/VIT (seuil 6), mais la DEF
        // n'a pas de bonus de set en % dans le design du jeu.
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            p.equiper(piece(slot, Equipement.Rarete.A, 10));
        }

        // DEF attendue : (100 * 1.0) + (6 * 10) = 160, sans multiplicateur de set.
        assertEquals(160.0, p.getDefense(), 0.001);
    }

    @Test
    void leBonusDeSetVitesseSAppliqueBienALaVitesse() {
        PersonnageJson p = creerPersonnage(0, 0, 100);
        for (Equipement.Slot slot : new Equipement.Slot[]{
                Equipement.Slot.ARME, Equipement.Slot.TORSE, Equipement.Slot.MAINS,
                Equipement.Slot.JAMBIERES, Equipement.Slot.BOTTES}) {
            p.equiper(new Equipement("Piece " + slot, slot, Equipement.Rarete.S,
                    Equipement.TypeArme.AUCUN, 0, 0, 0, 0));
        }

        // 5/5 pieces S : seuil de bonus de vitesse (SEUIL_VIT=5) atteint -> +5% (palier S).
        assertEquals(100.0 * 1.05, p.getVitesse(), 0.001);
    }
}
