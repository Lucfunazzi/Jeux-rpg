package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.List;

/**
 * Chevalier — inspiré d'Erza Scarlet.
 *
 * Spéciale de base : Lance Électrique     (100% ATK mono-cible)
 * Ultime de base   : Lance anémisthe      (120% ATK mono-cible + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Lance de Feu         (frappe + réduction défense)
 * Spéciale Arbre 2 : Marteau vengeur      (frappe lourde + étourdissement)
 */
public class Chevalier implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Lance Électrique", "Lance des Ténèbres"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lance Électrique !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffPrecision(1.00, 2), log);
    }

    // ── Ultime de base ───────────────────────────────────────────────────
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lance anémisthe !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        if (cibleFinale != null) {
            double degats = utilisateur.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        }
        // +5% ATK aux DPS alliés
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
        log.add("Lance de Feu!");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new ReductionDefense(0.15,2), log);
    }

    // ── Spéciale Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Marteau vengeur !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new Etourdissement(2), log);
    }

    // ── Spéciale Arbre 3 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre3(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        // TODO
    }

    @Override public void descriptionCompetenceArbre3() {
        // TODO
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lance Électrique — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Lance anémisthe — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
   @Override public void descriptionCompetenceArbre() {
        System.out.println("Lance de Feu -- Inflige 120% ATK au Tank ennemi. Réduit la défense de la cible de 15% pendants 2 tours");
        
    }
    @Override public void descriptionCompetenceArbre2() {
        System.out.println("Marteau vengeur -- Inflige 150% ATK au Tank ennemi. Etourdit la cible pendant 2 tours");
    }
}