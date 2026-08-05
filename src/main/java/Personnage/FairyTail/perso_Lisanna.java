package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Lisanna extends PersonnageBase {
    public perso_Lisanna() {
        this.nom = "Lisanna"; 
        this.niveau = 1;
        this.type="Elementaliste";
        this.role = "Support";
        this.rarete = "B";
        double m = 1.24;
        this.vie=420*m;
        this.attaque=145*m;
        this.defense=119*m;
        this.vitesse=162*m;
        this.taux_critiques=0.08;
        this.degat_critiques=1.15;
        this.taux_precisions=100;
        this.taux_esquives=0.06; 
        this.taux_blocage=0.10;
        this.reduction_blocage=0.12;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Griffe animale","Soin du coeur","Forme de colombe"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Lisanna attaque avec ses griffes animales !"); Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Lisanna prodigue un soin du coeur à ses alliés !");
        for (PersonnageBase al : a) if (al.estVivant()) {
            double soin = this.getAttaque()*0.80; al.recevoirSoin(soin, log);
        }
        Purification.purifierEquipe(a, 1, log);
        for (PersonnageBase al : a) {
            if (al.estVivant() && al.getNom().contains("Mirajane") && al.getVie() < al.getVieMax() * 0.5) {
                Combat.appliquerEffet(this, al, new Regeneration(0.08, 2), log);
                break;
            }
        }
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Lisanna prend sa forme de colombe et régénère l'équipe !");
        for (PersonnageBase al : a) if (al.estVivant()) Combat.appliquerEffet(this, al, new Regeneration(0.08,2), log);
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Griffe animale — 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Soin du coeur — soigne toute l'équipe de 80% ATK et purifie 1 effet negatif sur chacun."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Forme de colombe — régénère toute l'équipe 2 tours (8% PV max/tour)."); }
}