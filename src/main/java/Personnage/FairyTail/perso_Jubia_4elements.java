package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Jubia_4elements extends PersonnageBase {

    public perso_Jubia_4elements() {
        this.nom = "Jubia";
        this.type="Elementaliste";
        this.role = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 550 * multiplicateurRarete;
        this.attaque = 160 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.20;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing d'eau", "Déferlante", "Prison d'eau"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia utilise Poing d'eau sur " + cible.getNom());
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia utilise Déferlante !");
        // NOTE: "DSP" corrigé en "DPS"
        PersonnageBase cibleDPS = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("DPS"))
                .findFirst()
                .orElse(cible);

        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cibleDPS, degats, log);
        Combat.appliquerEffet(this, cibleDPS, new Ralentissement(2, 0.20), log);
        if (Math.random() < 0.30){
            Combat.appliquerEffet(this, cibleDPS, new Trempe(2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia utilise Prison d'eau sur les DPS !");
        for (PersonnageBase cibleDPS : equipeEnnemie) {
            if (cibleDPS.estVivant() && cibleDPS.getRole().equals("DPS")) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, cibleDPS, degats, log);
                Combat.appliquerEffet(this, cibleDPS, new ReductionVitesse(0.20, 2), log);
                Combat.appliquerEffet(this, cibleDPS, new Etourdissement(2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing d'eau — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Deferlante — cible le DPS ennemi prioritairement (fallback cible normale), "
                + "inflige 120% ATK, applique Ralentissement -20% VIT pendant 2 tours, "
                + "30% de chance de Trempe pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Prison d'eau — inflige 80% ATK a tous les DPS ennemis, "
                + "applique Reduction Vitesse -20% pendant 2 tours "
                + "et Etourdissement pendant 2 tours sur chacun.");
    }
}