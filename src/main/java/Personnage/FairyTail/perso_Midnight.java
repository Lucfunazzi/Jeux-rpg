package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Midnight (Macbeth) — Elementaliste, rang A.
 * Ancien membre d'Oración Seis, Magie de la Réflexion (Reflector).
 */
public class perso_Midnight extends PersonnageBase {

    public perso_Midnight() {
        this.nom    = "Midnight";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.44;
        this.vie     = 440 * mult;
        this.attaque = 190 * mult;
        this.defense = 100 * mult;
        this.vitesse = 130 * mult;
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
        log.add("Midnight dévie la lumière autour de lui avec Reflector et se régénère !");
        double soin = this.getVieMax() * 0.06;
        this.recevoirSoin(soin, log);
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
        Combat.appliquerEffet(this, new BuffBlocage(0.10, 2), log);
        List<PersonnageBase> vivants = new ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) vivants.add(ennemi);
        }
        Collections.shuffle(vivants);
        int nbCibles = Math.min(2, vivants.size());
        for (int i = 0; i < nbCibles; i++) {
            Combat.appliquerEffet(this, vivants.get(i), new Aveuglement(0.30, 2), log);
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
        Combat.appliquerEffet(this, new BuffTauxEsquive(0.10, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Reflector — se regenre de 6% de ses pv max");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Miroir Illusoire — Inflige 130% ATK et réduit la précision de la cible de 20% pendant 2 tours. et augmente son blocage de 10% ainsi que inflige aveuglement de 30% a 2 adversaires aleatoires");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Chaos Réflecté — Inflige 95% ATK à tous les ennemis et augmente les dégâts qu'ils subissent de 15% pendant 2 tours. et augmente son esquive de 10%");
    }
}
