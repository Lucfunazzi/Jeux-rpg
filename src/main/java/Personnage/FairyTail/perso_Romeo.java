package Personnage.FairyTail;
import Combat.Combat;
import Effets.Brulure;
import Effets.BuffAttaque;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Romeo extends PersonnageBase {
    public perso_Romeo() {
        this.nom = "Romeo"; this.niveau = 1; this.type = "Mage";
        this.role = "DPS"; this.rarete = "C";
        double m = 1.00;
        this.vie=295*m; this.attaque=100*m; this.defense=60*m; this.vitesse=85*m;
        this.taux_critiques=0.14; this.degat_critiques=1.25; this.taux_precisions=100;
        this.taux_esquives=0.08; this.taux_blocage=0.03; this.reduction_blocage=0.05;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Flamme multicolore","Magie des couleurs","Inferno arc-en-ciel"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Romeo projette une flamme multicolore sur "+cible.getNom()+" !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Brulure(1, 0.04), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Romeo déchaîne sa magie des couleurs sur "+cible.getNom()+" !");
        double d = this.getAttaque()*1.20; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.06), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Romeo déclenche un Inferno arc-en-ciel sur tous les ennemis !");
        Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);
        for (PersonnageBase c : e) if (c.estVivant()) {
            double d = this.getAttaque()*0.75; Combat.appliquerDegatsAvecLog(this, c, d, log);
            Combat.appliquerEffet(this, c, new Brulure(1, 0.05), log);
        }
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Flamme multicolore — 100% ATK + brûlure 1 tour (4% PV)."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Magie des couleurs — 120% ATK + brûlure 2 tours (6% PV)."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Inferno arc-en-ciel — +15% ATK, 75% ATK à tous + brûlure 1 tour."); }
}