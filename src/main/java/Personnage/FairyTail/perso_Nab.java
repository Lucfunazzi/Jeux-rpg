package Personnage.FairyTail;
import Combat.Combat;
import Effets.BuffDefense;
import Effets.BuffBlocage;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Nab extends PersonnageBase {
    public perso_Nab() {
        this.nom = "Nab"; this.niveau = 1; this.type = "Guerrier";
        this.role = "Tank"; this.rarete = "C";
        double m = 1.00;
        this.vie=380*m; this.attaque=85*m; this.defense=95*m; this.vitesse=65*m;
        this.taux_critiques=0.05; this.degat_critiques=1.10; this.taux_precisions=100;
        this.taux_esquives=0.03; this.taux_blocage=0.18; this.reduction_blocage=0.20;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de poing","Posture de défense","Forteresse animale"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Nab balance un coup de poing solide !"); Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Nab se met en posture de défense et frappe !");
        double d = this.getAttaque()*0.80; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffBlocage(0.12, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Nab invoque sa forteresse animale et protège ses alliés !");
        for (PersonnageBase al : a) if (al.estVivant()) { Combat.appliquerEffet(this, al, new BuffDefense(0.10, 2), log); }
        double soin = this.getVieMax()*0.10; this.recevoirSoin(soin, log);
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Coup de poing — 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Posture de défense — 80% ATK, +15% DEF et +12% blocage 2 tours."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Forteresse animale — +10% DEF à tous, se soigne de 10% PV max."); }
}