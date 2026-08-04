package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Kawazu — mage observateur de l'examen de rang S (Chapitre 8, stage 7,
 * duel contre Gadjeel et Levy aux côtés de Yamaru).
 */
public class EnnemiKawazu extends PersonnageBase {

    public EnnemiKawazu() { this(96); }

    public EnnemiKawazu(int niveau) {
        this.nom    = "Kawazu";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.35;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 450.0 * mult * niv;
        this.attaque = 160.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

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
