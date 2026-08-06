package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Midnight (Macbeth) — Mage de la Réflexion d'Oración Seis, rang S.
 * Sa magie Reflector dévie les attaques et brouille la perception de ses adversaires.
 */
public class EnnemiMidnight extends PersonnageBase {

    public EnnemiMidnight() { this(65); }

    public EnnemiMidnight(int niveau) {
        this.nom    = "Midnight";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 460.0 * mult * niv;
        this.attaque = 195.0 * mult * niv;
        this.defense = 105.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.16;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Reflector", "Miroir Illusoire", "Chaos Réflecté"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Midnight dévie la lumière et frappe " + cible.getNom() + " avec Reflector !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Midnight brouille les sens de " + cible.getNom() + " avec un Miroir Illusoire !");
        double degats = this.getAttaque() * 1.30;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Aveuglement(0.20, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Midnight déchaîne un Chaos Réflecté sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.95;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Fragilite(2, 0.15), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Reflector — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Miroir Illusoire — Inflige 130% ATK à la cible et réduit sa précision de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Chaos Réflecté — Inflige 95% ATK à tous les ennemis et augmente les dégâts qu'ils subissent de 15% pendant 2 tours.");
    }
}
