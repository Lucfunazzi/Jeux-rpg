package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Max extends PersonnageBase {
    public perso_Max() {
        this.nom = "Max"; this.niveau = 1; this.type="Elementaliste";
        this.role = "Tank"; this.rarete = "C";
        double m = 1.00;
        this.vie=310*m; this.attaque=110*m; this.defense=65*m; this.vitesse=95*m;
        this.taux_critiques=0.12; this.degat_critiques=1.25; this.taux_precisions=100;
        this.taux_esquives=0.05; this.taux_blocage=0.03; this.reduction_blocage=0.05;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de balai magique","Tornade de sable","Tempête abrasive"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Max utilise Coup de balai magique !"); Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Max déchaîne une tornade de sable !");
        double d = this.getAttaque()*1.25; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Max soulève une tempête abrasive sur tous les ennemis !");
        Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);
        for (PersonnageBase c : e) if (c.estVivant()) Combat.appliquerDegatsAvecLog(this, c, this.getAttaque()*0.65, log);
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Coup de balai — 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Tornade de sable — 125% ATK, -10% DEF 1 tour."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Tempête abrasive — +15% ATK, 65% ATK à tous."); }
}