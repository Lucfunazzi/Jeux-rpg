package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.BuffVitesse;
import Effets.ReductionAttaque;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiTemari extends PersonnageBase {

    public EnnemiTemari() {
        this(22);
    }

    public EnnemiTemari(int niveau) {
        this.nom    = "Temari";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.20;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 250.6 * mult * niv;
        this.attaque =  70.1 * mult * niv;
        this.defense =  32.3 * mult * niv;
        this.vitesse =  69.9 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lame du Vent Coupante", "Danse du Grand Eventail", "Invocation : Kamatari"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " donne un coup sec de son eventail et genere des lames de vent !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " cree un courant d'air ascendant pour propulser son equipe !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.25, 2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " utilise la Technique de la Tempete de Sable et du Vent !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.15;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.15, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lame du Vent Coupante — Inflige 100% ATK et reduit la Defense de la cible de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Danse du Grand Eventail — Augmente la Vitesse de tous les allies de 25% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Invocation : Kamatari — Inflige 115% ATK a tous les ennemis et reduit leur ATK de 15% pendant 2 tours.");
    }
}