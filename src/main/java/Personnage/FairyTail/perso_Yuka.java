package Personnage.FairyTail;

import Combat.Combat;
import Effets.Purification;
import Effets.ReductionAttaque;
import Effets.ReductionDefense;
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
        this.role   = "Support";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 300 * mult;
        this.attaque = 100 * mult;
        this.defense =  80 * mult;
        this.vitesse =  90 * mult;
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
        log.add("Yuka projette une Ondulation (Hadô) qui neutralise la magie de " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("L'onde de Yuka entre en collision avec la magie de " + cible.getNom() + " — EXPLOSION !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yuka déploie ses Ondulations sur toute l'équipe ennemie — toutes les magies sont annulées !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                Purification.purifier(cible, 3, log);
                double degats = (this.getAttaque() * 0.65) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() { System.out.println("Ondulation — Hadô : 100% ATK, réduit ATK de 10%."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Onde Explosive : 130% ATK, réduit ATK de 20% et DEF de 15%."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Annulation Totale : Purge buffs, 65% ATK à tous (x rage), réduit ATK de 15%."); }
}
