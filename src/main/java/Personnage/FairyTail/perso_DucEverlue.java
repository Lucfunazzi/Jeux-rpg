package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Duc Everlue (Ebar) — Invocateur, rang C.
 * Magie de la Terre : Nage/Diver (nage dans le sol), Rebond de Terre.
 * Magie des Constellations : invoquait Virgo (aujourd'hui perdu sa clé).
 */
public class perso_DucEverlue extends PersonnageBase {

    public perso_DucEverlue() {
        this.nom    = "Duc Everlue";
        this.type   = "Invocateur";
        this.role   = "Tank";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 360 * mult;
        this.attaque =  70 * mult;
        this.defense = 100 * mult;
        this.vitesse =  60 * mult;
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
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, c, degats, log);
                Combat.appliquerEffet(this, c, new ReductionAttaque(0.10, 2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Le Duc Everlue ouvre la Porte de la Vierge et invoque Virgo sous sa forme colossale !");
        
       
              PersonnageBase cible = null;
    for (PersonnageBase p : equipeEnnemie) {
        if (p.estVivant() && p.getRole().equals("Tank")) {
            cible = p;
            break;
        }
    }
    if (cible == null) {
        for (PersonnageBase p : equipeEnnemie) {
            if (p.estVivant()) {
                cible = p;
                break;
            }
        }
    }

    if (cible == null) return;
    
     double degats = this.getAttaque() * 1.20;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Nage — Diver : Plonge dans le sol et surgit sous la cible, inflige 100% ATK, 20% d'étourdissement.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Rebond de Terre : Rebondit sur tous les ennemis (70% ATK chacun), réduit leur ATK de 10%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Invocation de Virgo : inflige 120% ATK à un seul ennemi + étourdissement.");
    }
}
