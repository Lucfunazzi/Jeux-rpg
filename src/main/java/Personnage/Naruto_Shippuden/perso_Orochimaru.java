package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Orochimaru extends PersonnageBase {

    public perso_Orochimaru() {
        this.nom = "Orochimaru";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "S";

        double multiplicateurRarete = 1.50;
        this.vie      = 480 * multiplicateurRarete;
        this.attaque  = 175 * multiplicateurRarete;
        this.defense  = 110 * multiplicateurRarete;
        this.vitesse  = 125 * multiplicateurRarete;

        this.taux_critiques   = 0.15;
        this.degat_critiques  = 1.50;
        this.taux_precisions  = 100.00;
        this.taux_esquives    = 0.10;

        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{
            "Langue du Serpent",
            "Mille Serpents — Kusanagi",
            "Regeneration Maudite — Forme du Serpent"
        };
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Orochimaru projette sa langue de serpent et frappe la cible !");
    Combat.attaquer(this, cible, log);
    Combat.appliquerEffet(this, cible, new Poison(2, 0.06), log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Orochimaru invoque l'epee Kusanagi — une lame de chakra transperce la cible !");
    double degats = this.getAttaque() * 1.60;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Orochimaru libere ses Mille Serpents !");
    log.add("Une nuee de serpents de chakra envahit le champ de bataille !");
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Poison(3, 0.06), log);
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Langue du Serpent — Inflige 100% ATK a une cible et l'empoisonne (6% PV max / tour) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Mille Serpents — Kusanagi — Inflige 160% ATK a une cible et reduit sa Defense de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Regeneration Maudite — Forme du Serpent — Inflige 120% ATK a tous les ennemis et les empoisonne (6% PV max / tour) pendant 3 tours.");
    }
}