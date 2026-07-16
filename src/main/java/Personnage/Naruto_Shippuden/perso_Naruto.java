package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.List;
public class perso_Naruto extends PersonnageBase {

    public perso_Naruto() {
        this.nom = "Naruto";
        this.type = "Ninja";
        this.role = "Tank";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 700 * multiplicateurRarete;
        this.attaque = 130 * multiplicateurRarete;  // ATK relevée pour rang A
        this.defense = 150 * multiplicateurRarete;
        this.vitesse = 70 * multiplicateurRarete;
        this.taux_critiques = 0.08;
        this.degat_critiques = 1.15;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.15;
        this.reduction_blocage = 0.30;
        this.degats_renvoi = 0.80;
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
            .filter(PersonnageBase::estVivant)
            .collect(Collectors.toList());
    if (!ennemisVivants.isEmpty()) {
        PersonnageBase cibleConfuse = ennemisVivants.get(
                (int)(Math.random() * ennemisVivants.size()));
        Combat.appliquerEffet(this, cibleConfuse, new Confusion(2), log);
    }
    Combat.appliquerEffet(this, new BuffBlocage(0.10, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Naruto utilise Rasengan !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
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
        System.out.println("Sexy Jutsu — confuse un ennemi aleatoire pendant 2 tours "
                + "et gagne 10% de taux de blocage pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Rasengan — inflige 140% ATK a l'ennemi avec la plus haute attaque, "
                + "lui applique Saignement (10% PV/tour) pendant 2 tours. "
                + "Reduit la defense de tous les ennemis de 10% pendant 2 tours.");
    }
}