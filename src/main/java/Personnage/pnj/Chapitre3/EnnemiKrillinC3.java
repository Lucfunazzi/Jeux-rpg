package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiKrillinC3 extends PersonnageBase {

    public EnnemiKrillinC3() { this(30); }

    public EnnemiKrillinC3(int niveau) {
        this.nom    = "Krillin";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 250.0 * mult * niv;
        this.attaque = 110.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.1;
        this.degat_critiques   = 1.2;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }
  @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de pieds", "Kienzan", "Super kienzan"};
    }

    
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Krillin attaque !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Krillin lance son Kienzan !");
    PersonnageBase cibleKienzan = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (cibleKienzan == null || ennemi.getDefense() > cibleKienzan.getDefense())
                cibleKienzan = ennemi;
        }
    }
    if (cibleKienzan == null) return;
    double degats = this.getAttaque() * 1.10;
    Combat.appliquerDegatsAvecLog(this, cibleKienzan, degats, log);
    Combat.appliquerEffet(this, cibleKienzan, new ReductionDefense(0.10, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Krillin utilise Super Kienzan !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.08), log);
        }
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
        }
    }
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de pieds — inflige 100% ATK à une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Kienzan — inflige 105% ATK à tous les DPS ennemis, "
                + "leur applique Saignement pendant 2 tours "
                + "et restaure 50 rage à Krillin.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Super Kienzan — inflige 110% ATK à tous les ennemis, "
                + " inflige saignement aux dps ennemies pendants 2 tours "
                + "et reduit leurs defense de 10% pendants 2 tours.");
    }

}
