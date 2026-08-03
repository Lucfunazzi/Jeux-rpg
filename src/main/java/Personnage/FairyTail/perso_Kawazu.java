package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/** Kawazu — Elementaliste, rang B. Mage observateur de l'examen de rang S. */
public class perso_Kawazu extends PersonnageBase {

    public perso_Kawazu() {
        this.nom    = "Kawazu";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "B";
        this.niveau = 1;
        double mult = 1.30;
        this.vie     = 430 * mult;
        this.attaque = 150 * mult;
        this.defense =  95 * mult;
        this.vitesse = 110 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe Vive", "Onde de Choc", "Déluge"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kawazu frappe " + cible.getNom() + " d'une Frappe Vive !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kawazu projette une Onde de Choc sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.25;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche && Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Kawazu déchaîne un Déluge sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe Vive — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Onde de Choc — Inflige 125% ATK, 30% de chance d'étourdir la cible 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Déluge — Inflige 80% ATK à tous les ennemis.");
    }
}
