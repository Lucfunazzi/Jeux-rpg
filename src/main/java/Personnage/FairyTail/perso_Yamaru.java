package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/** Yamaru — Elementaliste, rang B. Mage observateur de l'examen de rang S. */
public class perso_Yamaru extends PersonnageBase {

    public perso_Yamaru() {
        this.nom    = "Yamaru";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "B";
        this.niveau = 1;
        double mult = 1.52;
        this.vie     = 450 * mult;
        this.attaque = 150 * mult;
        this.defense = 110 * mult;
        this.vitesse = 121 * mult;
        this.taux_critiques    = 0.06;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe Terne", "Voile Protecteur", "Barrage"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamaru frappe " + cible.getNom() + " d'une Frappe Terne !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamaru érige un Voile Protecteur !");
        Combat.appliquerEffet(this, new BuffDefense(0.20, 2), log);
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamaru ouvre un Barrage sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.75;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe Terne — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Voile Protecteur — Augmente sa défense de 20% pendant 2 tours et inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Barrage — Inflige 75% ATK à tous les ennemis.");
    }
}
