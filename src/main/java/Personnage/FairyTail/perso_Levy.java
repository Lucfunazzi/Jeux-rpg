package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Levy extends PersonnageBase {
    public perso_Levy() {
        this.nom = "Levy"; 
        this.niveau = 1;
        this.type="Elementaliste";
        this.role = "Support";
        this.rarete = "B";
        double m = 1.30;
        this.vie=390*m; 
        this.attaque=130*m; 
        this.defense=100*m; this.vitesse=105*m;
        this.taux_critiques=0.10;
        this.degat_critiques=1.20; this.taux_precisions=100;
        this.taux_esquives=0.08;
        this.taux_blocage=0.06; this.reduction_blocage=0.08;
        this.degats_renvoi=0.80;
        initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Script : Fer","Script : Flamme","Script : Solidifier"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Levy écrit 'Fer' et une lame d'acier frappe " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Levy inscrit 'Flamme' — un feu de script s'embrase sur "+cible.getNom()+" !");
        double d = this.getAttaque()*1.20; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        if (Math.random()<0.30) Combat.appliquerEffet(this, cible, new Brulure(1,0.05), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Levy inscrit 'Solidifier' — toute l'équipe est renforcée !");
        for (PersonnageBase al : a) if (al.estVivant()) {
            Combat.appliquerEffet(this, al, new BuffAttaque(0.12, 2), log);
            Combat.appliquerEffet(this, al, new BuffDefense(0.10, 2), log);
        }
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Script : Fer — inflige 100% ATK a une cible."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Script : Flamme — inflige 120% ATK a une cible, 30% de chance de Silence pendant 1 tour."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Script : Solidifier — applique +12% ATK et +10% DEF pendant 2 tours a toute l'equipe alliee."); }
}