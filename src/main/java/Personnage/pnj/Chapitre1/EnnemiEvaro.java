package Personnage.pnj.Chapitre1;

import Combat.Combat;
import Effets.BuffBlocage;
import Effets.BuffDefense;
import Effets.Etourdissement;
import Effets.Regeneration;
import Effets.ReductionAttaque;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Duc Everlue — Mage constellationniste et Mage de la Terre, rang C.
 * Politicien corrompu de l'arc Daybreak. Possédait Virgo (Clef de la Vierge).
 * Magie de la Terre : Nage (Diver) — plonge et nage dans le sol, Rebond de Terre.
 * Magie des Constellations : invoque Virgo (forme de gorille géant).
 */
public class EnnemiEvaro extends PersonnageBase {

    public EnnemiEvaro() { this(3); }

    public EnnemiEvaro(int niveau) {
        this.nom    = "Duc Everlue";
        this.niveau = niveau;
        this.type   = "Invocateur";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 280.0 * niv;
        this.attaque =  55.0 * niv;
        this.defense =  75.0 * niv;
        this.vitesse =  50.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.16;
        this.reduction_blocage = 0.18;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Nage — Diver", "Rebond de Terre", "Invocation de Virgo"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Le Duc Everlue plonge dans le sol et surgit sous " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Everlue se met en boule et rebondit frénétiquement sur " + cible.getNom() + " et ses alliés !");
        for (PersonnageBase c : equipeEnnemie) {
            if (c.estVivant()) {
                double degats = this.getAttaque() * 0.60;
                Combat.appliquerDegatsAvecLog(this, c, degats, log);
                
            }
        }
        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Le Duc Everlue ouvre la Porte de la Vierge et invoque Virgo sous sa forme colossale !");

        PersonnageBase cible = Combat.choisirCible(this, equipeEnnemie);
        if (cible == null) return;

     double degats = this.getAttaque() * 1.20;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Nage — Diver : Plonge dans le sol et surgit sous la cible, inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Rebond de Terre : Rebondit sur tous les ennemis (70% ATK chacun), Augmente son blocage de 15%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Invocation de Virgo : inflige 120% ATK à un seul ennemi + étourdit la cible pendant 1 tour.");
    }
}
