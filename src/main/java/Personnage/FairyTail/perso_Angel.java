package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Angel extends PersonnageBase {

    public perso_Angel() {
        this.nom    = "Angel";
        this.type="Invocateur";
        this.role   = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.44;
        this.vie     = 480 * multiplicateurRarete;
        this.attaque = 200 * multiplicateurRarete;
        this.defense = 90  * multiplicateurRarete;
        this.vitesse = 125 * multiplicateurRarete;
        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Epée de caelum", "Caelum", "Aries"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel utilise epée de caelum " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel invoque Caelum !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.50, log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new Etourdissement(2), log);
            log.add("  Le laser aveugle " + cible.getNom() + " !");
        }
        Combat.appliquerEffet(this,cible,new  ReductionDefense(0.20,2), log);

    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Angel invoque Aries !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (!ennemi.estVivant()) continue;
            double degats = this.getAttaque() * 1.30 * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.20, 2), log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Epée de caelum — Inflige 120% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Caelum — Inflige 150% ATK,Reduit la defense de la cible de 20% et à 30% de chance d'étourdir la cible 2 tours .");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Aries — Inflige 130% ATK (bonus selon la Rage) à tous les ennemis et réduit leur ATK de 20% pendant 2 tours.");
    }
}
