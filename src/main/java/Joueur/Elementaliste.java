package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.List;

/**
 * Mage — magie de glace et de cristaux, inspiré de Gray Fullbuster.
 *
 * Spéciale de base : Lance de Glace         (100% ATK mono-cible Tank + 100% précision)
 * Ultime de base   : Bazooka de Glace       (120% ATK mono-cible Tank + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Épée de Glace Éternelle (frappe lourde + gel + -DEF)
 * Ultime  Arbre 2  : Ice Make — Démon de Glace (AoE 3 cibles + gel massif)
 */
public class Elementaliste implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Lance de Glace", "Bazooka de Glace"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lance de Glace !");
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
        log.add("Bazooka de Glace !");
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
        log.add("Épée de Glace Éternelle !");
        double degats = utilisateur.getAttaque() * 1.90;
        Combat.appliquerDegatsAvecLog(utilisateur, cible, degats, log);
        Combat.appliquerEffet(utilisateur, cible, new ReductionDefense(0.20, 3), log);
        if (Math.random() < 0.40) {
            Combat.appliquerEffet(utilisateur, cible, new Gel(1), log);
        }
    }

    // ── Ultime Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ice Make — Démon de Glace !");
        int touche = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && touche < 3) {
                double degats = utilisateur.getAttaque() * 1.60;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                Combat.appliquerEffet(utilisateur, ennemi, new ReductionDefense(0.15, 2), log);
                if (Math.random() < 0.45) {
                    Combat.appliquerEffet(utilisateur, ennemi, new Gel(1), log);
                }
                touche++;
            }
        }
        Combat.appliquerEffet(utilisateur, new BuffDefense(0.20, 2), log);
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lance de Glace — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Bazooka de Glace — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
    @Override public void descriptionCompetenceArbre() {
        
    }
    @Override public void descriptionCompetenceArbre2() {
        
    }
}