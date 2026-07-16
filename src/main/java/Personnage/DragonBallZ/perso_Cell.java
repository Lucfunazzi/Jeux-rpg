package Personnage.DragonBallZ;

import Combat.Combat;
import Effets.Poison;
import Effets.ReductionAttaque;
import Effets.Bouclier;
import Effets.Regeneration;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Cell extends PersonnageBase {

    public perso_Cell() {
        this.nom = "Cell";
        this.type = "Guerrier";
        this.role = "Tank";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 680 * multiplicateurRarete;
        this.attaque = 190 * multiplicateurRarete;
        this.defense = 160 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.35;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        this.taux_blocage      = 0.20;
        this.reduction_blocage = 0.25;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Piqure cellulaire", "Absorption d'energie", "Imperfection parfaite"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell utilise Piqure cellulaire !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Cell injecte du poison (absorption cellulaire)
        Combat.appliquerEffet(this, cible, new Poison(2, 0.06), log);
        // Cell se regenere legerement en frappant
        Combat.appliquerEffet(this, this, new Regeneration(0.04, 1), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell utilise Absorption d'energie !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Reduit l'ATK de la cible (Cell absorbe sa puissance)
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.25, 3), log);
        // Cell se pose un bouclier avec l'energie absorbee
        Combat.appliquerEffet(this, this, new Bouclier(this.getVieMax() * 0.15), log);
        log.add("Cell absorbe l'energie de " + cible.getNom() + " et forme un bouclier !");
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell utilise Imperfection parfaite !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        // Poison et affaiblissement sur toute l'equipe ennemie
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerEffet(this, ennemi, new Poison(3, 0.08), log);
                Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.20, 3), log);
            }
        }
        // Frappe lourde sur la cible avec le plus d'ATK (Cell neutralise la menace)
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getAttaque))
                .orElse(null);
        if (cible != null) {
            double degats = (this.getAttaque() * 1.80) * multiplicateurRage;
            log.add("Cell concentre son absorption sur " + cible.getNom() + " !");
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        }
        // Regeneration massive sur Cell
        Combat.appliquerEffet(this, this, new Regeneration(0.10, 3), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Piqure cellulaire — Inflige 110% ATK a la cible. "
                + "Applique Poison (6% PV max/tour) pendant 2 tours. "
                + "Cell se regenere de 4% PV max pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Absorption d'energie — Inflige 140% ATK a la cible. "
                + "Reduit son ATK de 25% pendant 3 tours. "
                + "Cell gagne un bouclier de 15% de ses PV max pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Imperfection parfaite — Applique Poison (8% PV max/tour) et -20% ATK a tous les ennemis pendant 3 tours. "
                + "Inflige 180% ATK a l'ennemi avec la plus haute ATK. "
                + "Cell se regenere de 10% PV max pendant 3 tours. "
                + "Puissance augmentee par la Rage.");
    }
}