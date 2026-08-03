package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Brain — chef d'Oración Seis, rang S. Ancien membre du Conseil de la Magie,
 * maître de la manipulation mentale et du drain de magie. Apparaît aux stages 8 et 9
 * du Chapitre 6, avant de révéler sa véritable forme (Zero) au stage 10.
 */
public class EnnemiBrain extends PersonnageBase {

    public EnnemiBrain() { this(67); }

    public EnnemiBrain(int niveau) {
        this.nom    = "Brain";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "S";

        double mult = 1.65;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 560.0 * mult * niv;
        this.attaque = 180.0 * mult * niv;
        this.defense = 130.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Barrière de Particules Magiques", "Manipulation Mentale", "Drain Magique Absolu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain projette des Particules Magiques sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain envahit l'esprit de " + cible.getNom() + " — Manipulation Mentale !");
        double degats = this.getAttaque() * 1.20;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Confusion(2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain draine la magie de toute l'équipe ennemie !");
        double degatsTotaux = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.85;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                degatsTotaux += degats;
            }
        }
        double soin = degatsTotaux * 0.20;
        if (soin > 0) {
            this.recevoirSoin(soin, log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Barrière de Particules Magiques — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Manipulation Mentale — Inflige 120% ATK et confond la cible pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Drain Magique Absolu — Inflige 85% ATK à tous les ennemis et se soigne de 20% des dégâts infligés.");
    }
}
