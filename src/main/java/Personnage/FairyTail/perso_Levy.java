package Personnage.FairyTail;
import Combat.Combat;
import Effets.BuffAttaque;
import Effets.BuffDefense;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Levy extends PersonnageBase {
    public perso_Levy() {
        this.nom = "Levy"; this.niveau = 1; this.type = "Mage";
        this.role = "Support"; this.rarete = "B";
        double m = 1.30;
        this.vie=390*m; this.attaque=130*m; this.defense=100*m; this.vitesse=105*m;
        this.taux_critiques=0.10; this.degat_critiques=1.20; this.taux_precisions=100;
        this.taux_esquives=0.08; this.taux_blocage=0.06; this.reduction_blocage=0.08;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Script : Fer","Script : Flamme","Script : Solidifier"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Levy écrit 'Fer' et une lame d'acier frappe "+cible.getNom()+" !");
        Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Levy inscrit 'Flamme' — un feu de script s'embrase sur "+cible.getNom()+" !");
        double d = this.getAttaque()*1.30; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        if (Math.random()<0.30) Combat.appliquerEffet(this, cible, new Silence(1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Levy inscrit 'Solidifier' — toute l'équipe est renforcée !");
        for (PersonnageBase al : a) if (al.estVivant()) {
            Combat.appliquerEffet(this, al, new BuffAttaque(0.12, 2), log);
            Combat.appliquerEffet(this, al, new BuffDefense(0.10, 2), log);
        }
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Script : Fer — 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Script : Flamme — 130% ATK, 30% silence 1 tour."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Script : Solidifier — toute l'équipe +12% ATK et +10% DEF 2 tours."); }
}