package Personnage.DragonBallZ;

import Combat.Combat;
import Effets.ReductionAttaque;
import Effets.ReductionDefense;
import Effets.Malediction;
import Effets.Fragilite;
import Effets.BuffAttaque;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Freezer extends PersonnageBase {

    public perso_Freezer() {
        this.nom = "Freezer";
        this.type = "Guerrier";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 570 * multiplicateurRarete;
        this.attaque = 220 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.45;
        this.taux_precisions = 110.00;
        this.taux_esquives = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rayon de la mort", "Supernova", "Annihilation totale"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer utilise Rayon de la mort !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Fragilise la cible (Freezer joue avec sa proie)
        Combat.appliquerEffet(this, cible, new Fragilite(2, 0.20), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer utilise Supernova !");
        double degats = this.getAttaque() * 1.70;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Double debuff offensif et defensif
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.25, 2), log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 2), log);
        // 35% de chance de Malediction
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Malediction(2,0.15), log);
        }
        // Freezer se buff
        Combat.appliquerEffet(this, this, new BuffAttaque(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer utilise Annihilation totale !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        // Debuff toute l'equipe ennemie d'abord
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerEffet(this, ennemi, new Fragilite(3, 0.25), log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.20, 3), log);
            }
        }
        // Puis frappe la cible la plus faible en PV (Freezer achevement)
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .min(java.util.Comparator.comparingDouble(PersonnageBase::getVie))
                .orElse(null);
        if (cible == null) return;
        double degats = (this.getAttaque() * 2.20) * multiplicateurRage;
        log.add("Freezer cible l'ennemi le plus affaibli !");
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Rayon de la mort — Inflige 120% ATK a la cible. "
                + "Applique Fragilite (+20% degats recus) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Supernova — Inflige 170% ATK a la cible. "
                + "Reduit son ATK et sa DEF de 25% pendant 2 tours. "
                + "35% de chance de Malediction pendant 2 tours. "
                + "Freezer gagne +15% ATK pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Annihilation totale — Applique Fragilite (+25%) et -20% DEF a tous les ennemis pendant 3 tours. "
                + "Inflige 220% ATK a l'ennemi avec le moins de PV. "
                + "Puissance augmentee par la Rage.");
    }
}