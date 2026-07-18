package Personnage.FairyTail;
import Combat.Combat;
import Effets.BuffVitesse;
import Effets.Etourdissement;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Jet extends PersonnageBase {
    public perso_Jet() {
        this.nom = "Jet"; this.niveau = 1; this.type = "Guerrier";
        this.role = "DPS"; this.rarete = "C";
        double m = 1.00;
        this.vie=290*m; this.attaque=105*m; this.defense=60*m; this.vitesse=120*m;
        this.taux_critiques=0.18; this.degat_critiques=1.30; this.taux_precisions=100;
        this.taux_esquives=0.12; this.taux_blocage=0.03; this.reduction_blocage=0.05;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup éclair","Sprint fulgurant","Rafale de coups"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Jet frappe à la vitesse de l'éclair !"); Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Jet effectue un sprint fulgurant et percute "+cible.getNom()+" !");
        double d = this.getAttaque()*1.30; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        if (Math.random()<0.25) Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Jet enchaîne une rafale de coups sur tous les ennemis !");
        Combat.appliquerEffet(this, new BuffVitesse(0.20, 2), log);
        for (PersonnageBase c : e) if (c.estVivant()) Combat.appliquerDegatsAvecLog(this, c, this.getAttaque()*0.60, log);
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Coup éclair — 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Sprint fulgurant — 130% ATK, 25% étourdissement 1 tour."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Rafale de coups — +20% VIT 2 tours, 60% ATK à tous."); }
}