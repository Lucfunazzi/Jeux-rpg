package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Eve Tearm — Elementaliste, rang B.
 * Membre des Trimens de Blue Pegasus. Magie de la Neige : recouvre le champ
 * de bataille de glace et de flocons tranchants. Allié dès l'arc Oración Seis.
 */
public class perso_Eve extends PersonnageBase {

    public perso_Eve() {
        this.nom    = "Eve";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.40;
        this.vie     = 440 * mult;
        this.attaque = 150 * mult;
        this.defense = 115 * mult;
        this.vitesse = 125 * mult;
        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.07;
        this.taux_blocage      = 0.07;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flocon Tranchant", "Blizzard", "Ice Bringer"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eve entaille " + cible.getNom() + " d'un Flocon Tranchant !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eve déchaîne un Blizzard sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.85;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
                }
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eve invoque Ice Bringer — le blizzard glacial fond sur les ennemis !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche && Math.random() < 0.30) {
                    Combat.appliquerEffet(this, ennemi, new Gel(2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flocon Tranchant — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Blizzard — Inflige 85% ATK à tous les ennemis et réduit leur vitesse de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ice Bringer — Inflige 110% ATK à tous les ennemis, 30% de chance de Gel pendant 2 tours.");
    }
}
