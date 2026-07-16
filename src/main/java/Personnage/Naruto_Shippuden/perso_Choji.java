package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Choji extends PersonnageBase {

    public perso_Choji() {
        this.nom = "Choji";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Tank";
        this.rarete = "B";

        double multiplicateurRarete = 1.30;
        this.vie     = 750 * multiplicateurRarete;
        this.attaque = 120 * multiplicateurRarete;
        this.defense = 130 * multiplicateurRarete;
        this.vitesse =  80 * multiplicateurRarete;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 95.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.20;
        this.reduction_blocage = 0.30;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Poing Decuple", "Boulet Humain", "Decuplement Partiel"};
    }

    // ── Attaque de base — 100% ATK
    // Synergie : si la cible a ReductionVitesse (posée par Shikamaru) → +20% dégâts
    @Override
   public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log){
        log.add("Choji utilise Coup de Poing Decuple !");
        boolean cibleRalentie = cible.aEffet(ReductionVitesse.class);
        if (cibleRalentie) {
            log.add("[Synergie Equipe 10] Choji profite de l'immobilisation de Shikamaru ! +20% degats !");
            double degats = this.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        } else {
            Combat.attaquer(this, cible, log);
        }
    }

    // ── Spéciale — 120% ATK + ReductionDefense 15% 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Choji utilise Boulet Humain !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    // ── Ultime — BuffDefense 25% sur soi 2 tours
    // Synergie : si Shikamaru ou Ino vivants → gagne aussi un Bouclier de 15% PV max
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Choji utilise Decuplement Partiel !");
        Combat.appliquerEffet(this, new BuffDefense(0.25, 2), log);

        boolean synergieEquipe10 = false;
        for (PersonnageBase allie : equipeAlliee) {
            if ((allie.getNom().equals("Shikamaru") || allie.getNom().equals("Ino"))
                    && allie.estVivant()) {
                synergieEquipe10 = true;
                break;
            }
        }
        if (synergieEquipe10) {
            double bouclier = this.getVieMax() * 0.15;
            Combat.appliquerEffet(this, new Bouclier(bouclier), log);
            log.add("[Synergie Equipe 10] Choji gagne un bouclier de "
                    + String.format("%.0f", bouclier) + " PV !");
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de Poing Decuple — inflige 100% ATK a une cible. "
                + "[Synergie Equipe 10] Si la cible est ralentie par Shikamaru : +20% degats.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Boulet Humain — inflige 120% ATK a une cible et reduit sa DEF de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Decuplement Partiel — gagne +25% DEF pendant 2 tours. "
                + "[Synergie Equipe 10] Si Shikamaru ou Ino sont vivants : gagne aussi un bouclier de 15% PV max.");
    }
}