package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.List;

/**
 * Chasseur de Dragon — magie draconique de l'eau.
 *
 * Spéciale de base : Poings du Dragon d'Eau      (100% ATK mono-cible Tank + 100% précision)
 * Ultime de base   : Hurlement du Dragon d'Eau   (120% ATK mono-cible Tank + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Écailles du Dragon d'Eau    (frappe + absorption)
 * Ultime  Arbre 2  : Forme Dragon — Inondation Abyssale (AoE dévastateur)
 */
public class ChasseurDeDragon implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Poings du Dragon d'Eau", "Hurlement du Dragon d'Eau"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Poings du Dragon d'Eau !");
        PersonnageBase tank = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Tank"))
                .findFirst().orElse(cible);
        double degats = utilisateur.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(utilisateur, tank, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffPrecision(1.00, 2), log);
    }

    // ── Ultime de base ───────────────────────────────────────────────────
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hurlement du Dragon d'Eau !");
        PersonnageBase tank = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Tank"))
                .findFirst().orElse(
                    equipeEnnemie.stream().filter(PersonnageBase::estVivant).findFirst().orElse(null)
                );
        if (tank != null) {
            double degats = utilisateur.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(utilisateur, tank, degats, log);
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(utilisateur, allie, new BuffAttaque(0.05, 2), log);
            }
        }
    }

    // ── Spéciale Arbre 1 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Écailles du Dragon d'Eau !");
        double degats = utilisateur.getAttaque() * 1.70;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        double soin = degats * 0.20;
        utilisateur.recevoirSoin(soin, log);
        Combat.appliquerEffet(utilisateur, cible, new ReductionVitesse(0.25, 3), log);
    }

    // ── Ultime Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Forme Dragon — Inondation Abyssale !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = utilisateur.getAttaque() * 1.50;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                Combat.appliquerEffet(utilisateur, ennemi, new ReductionVitesse(0.20, 2), log);
                if (Math.random() < 0.40) {
                    Combat.appliquerEffet(utilisateur, ennemi, new Trempe(2), log);
                }
            }
        }
        Combat.appliquerEffet(utilisateur, new BuffAttaque(0.20, 2), log);
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Poings du Dragon d'Eau — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Hurlement du Dragon d'Eau — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
    @Override public void descriptionCompetenceArbre() {
        
    }
    @Override public void descriptionCompetenceArbre2() {
        
    }
}