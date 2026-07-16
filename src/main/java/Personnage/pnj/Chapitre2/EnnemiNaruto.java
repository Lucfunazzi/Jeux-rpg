package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnnemiNaruto extends PersonnageBase {

    public EnnemiNaruto() {
        this(20);
    }

    public EnnemiNaruto(int niveau) {
        this.nom    = "Naruto";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Tank";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 296.8 * mult * niv;
        this.attaque =  57.4 * mult * niv;
        this.defense =  67.3 * mult * niv;
        this.vitesse =  62.7 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.15;
        this.reduction_blocage = 0.30;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de kunai", "Sexy Jutsu", "Rasengan"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Naruto utilise Coup de kunai !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Naruto utilise Sexy Jutsu !");
        List<PersonnageBase> ennemisVivants = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant).collect(Collectors.toList());
        if (!ennemisVivants.isEmpty()) {
            PersonnageBase cibleConfuse = ennemisVivants.get((int)(Math.random() * ennemisVivants.size()));
            Combat.appliquerEffet(this, cibleConfuse, new Confusion(2), log);
        }
        Combat.appliquerEffet(this, new BuffBlocage(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Naruto utilise Rasengan !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        PersonnageBase ciblePrincipale = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (ciblePrincipale == null || ennemi.getAttaque() > ciblePrincipale.getAttaque())
                    ciblePrincipale = ennemi;
            }
        }
        if (ciblePrincipale != null) {
            double degats = (this.getAttaque() * 1.40) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ciblePrincipale, degats, log);
            Combat.appliquerEffet(this, ciblePrincipale, new Saignement(2, 0.10), log);
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.10, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de kunai — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Sexy Jutsu — confuse un ennemi aleatoire pendant 2 tours et gagne 10% de taux de blocage pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Rasengan — inflige 140% ATK a l'ennemi avec la plus haute ATK, lui applique Saignement (10% PV/tour) 2 tours, et reduit la defense de tous les ennemis de 10% pendant 2 tours.");
    }
}