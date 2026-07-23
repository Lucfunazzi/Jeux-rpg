package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Yuka Suzuki — Elementaliste, rang C.
 * Magie des Ondes (Ondulation / Hadô) : annule toute forme d'attaque magique.
 * Ses ondes peuvent causer des explosions lorsqu'elles entrent en contact avec une magie adverse.
 * Lamia Scale.
 */
public class perso_Yuka extends PersonnageBase {

    public perso_Yuka() {
        this.nom    = "Yuka";
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 420 * mult;
        this.attaque = 100 * mult;
        this.defense =  90 * mult;
        this.vitesse =  90 * mult;
        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.15;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

   @Override
    public String[] getNomsAttaques() {
        return new String[]{"Ondulation — Hadô", "Onde Explosive", "Annulation Totale des Magies"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yuka projette une Ondulation sur " + cible.getNom() + " et le frappe !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yuka Lance Onde Explosive " + cible.getNom() + " — une explosion dévaste la cible !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(cible,this,new BuffTauxEsquive(0.15,2),log );
        
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yuka déploie une vague d'Ondulations qui annule toutes les magies ennemies !");
        Purification.purifier(this, 3, log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                
                double degats = this.getAttaque() * 0.65;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Ondulation — Hadô : Inflige 100%.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Onde Explosive : Inflige 130% ATK à une cible, Augmente son esquive de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Annulation Totale : Enleve tout ses débuffs, inflige 65% ATK à tous les ennemis.");
    }
}
