package Personnage.DragonBallZ;

import Combat.Combat;
import Effets.Etourdissement;
import Effets.ReductionDefense;
import Effets.Provocation;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Nappa extends PersonnageBase {

    public perso_Nappa() {
        this.nom = "Nappa";
        this.type = "Guerrier";
        this.role = "Tank";
        this.rarete = "B";
        this.niveau = 1;
        double multiplicateurRarete = 1.20;
        this.vie = 620 * multiplicateurRarete;
        this.attaque = 140 * multiplicateurRarete;
        this.defense = 130 * multiplicateurRarete;
        this.vitesse = 80 * multiplicateurRarete;
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.30;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.03;
        this.taux_blocage      = 0.15;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing ecrasant", "Souffle destructeur", "Massacre Saiyan"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa utilise Poing ecrasant !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // 25% de chance d'etourdir
        if (Math.random() < 0.25) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa utilise Souffle destructeur !");
        // AoE leger + reduction DEF
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.00;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
            }
        }
        // Nappa provoque apres l'attaque
        Combat.appliquerEffet(this, this, new Provocation(2, this), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa utilise Massacre Saiyan !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        // Frappe l'ennemi avec le plus de DEF (Nappa casse les tanks)
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getDefense))
                .orElse(null);
        if (cible == null) return;
        double degats = (this.getAttaque() * 1.80) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.30, 3), log);
        if (Math.random() < 0.40) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Poing ecrasant — Inflige 110% ATK a la cible. "
                + "25% de chance d'Etourdissement pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Souffle destructeur — Inflige 100% ATK a tous les ennemis. "
                + "Reduit leur DEF de 15% pendant 2 tours. "
                + "Nappa gagne Provocation pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Massacre Saiyan — Inflige 180% ATK a l'ennemi avec le plus de DEF. "
                + "Reduit sa DEF de 30% pendant 3 tours. "
                + "40% de chance d'Etourdissement. "
                + "Puissance augmentee par la Rage.");
    }
}