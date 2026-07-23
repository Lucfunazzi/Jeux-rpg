package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Yuka Suzuki — Mage des Ondes, rang C.
 * Magie des Ondes (Ondulation / Hadô) : annule toutes les formes d'attaque magique.
 * Ses ondes neutralisent la magie adverse et peuvent causer des explosions.
 * Affrontement célèbre contre Natsu : Natsu l'a battu en punchant physiquement à travers le bouclier.
 */
public class EnnemiYuka extends PersonnageBase {

    public EnnemiYuka() { this(12); }

    public EnnemiYuka(int niveau) {
        this.nom    = "Yuka";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 230.0 * niv;
        this.attaque =  80.0 * niv;
        this.defense =  65.0 * niv;
        this.vitesse =  70.0 * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
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
