package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.Brulure;
import Effets.BuffAttaque;
import Effets.Poison;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Totomaru — Element 4 de Phantom Lord, Magie des Flammes Arc-en-Ciel (Rainbow Fire), rang B.
 * Chaque couleur de flamme a un effet différent :
 * - Feu Blanc : flammes basiques brûlantes
 * - Feu Bleu : feu GLACÉ (contre-intuitif !)
 * - Feu Orange : feu nauséabond pour contrecarrer les mangeurs de feu
 * - Feu Arc-en-ciel : combinaison des 3 précédentes, le plus destructeur
 * Pyrokinésie : contrôle les flammes adverses dans une zone limitée.
 */
public class EnnemiTotomaru extends PersonnageBase {

    public EnnemiTotomaru() { this(26); }

    public EnnemiTotomaru(int niveau) {
        this.nom    = "Totomaru";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.30;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        // PV releves (~x4) : boss titulaire du stage face a Natsu (invite ~1300 PV), sinon
        // combat expedie en un ou deux tours.
        this.vie     = 950.0 * mult * niv;
        this.attaque = 105.0 * mult * niv;
        this.defense =  65.0 * mult * niv;
        this.vitesse =  85.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flamme Blanche", "Feu Orange Nauséabond", "Flammes Arc-en-Ciel"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru projette une flamme blanche intense sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
     
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru génère un feu orange nauséabond — l'odeur est si répugnante que " + cible.getNom() + " ne peut plus se concentrer !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Poison(2, 0.06), log);
        
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru déchaîne les Flammes Arc-en-Ciel — la combinaison mortelle de toutes ses flammes dévaste l'équipe !");
        
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.00;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Brulure(2, 0.07), log);
                
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flamme Blanche — Inflige 100% ATK, brûle 2 tours (6% PV/tour).");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Feu Orange Nauséabond — Inflige 140% ATK, empoisonne 2 tours (6% PV/tour), silence 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Flammes Arc-en-Ciel — +20% ATK, inflige 100% ATK à tous, brûle 2 tours + empoisonne 2 tours.");
    }
}
