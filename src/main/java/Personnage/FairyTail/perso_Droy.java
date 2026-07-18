package Personnage.FairyTail;
import Combat.Combat;
import Effets.Regeneration;
import Effets.Poison;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Droy extends PersonnageBase {
    public perso_Droy() {
        this.nom = "Droy"; this.niveau = 1; this.type = "Mage";
        this.role = "Support"; this.rarete = "C";
        double m = 1.00;
        this.vie=330*m; this.attaque=90*m; this.defense=80*m; this.vitesse=75*m;
        this.taux_critiques=0.07; this.degat_critiques=1.15; this.taux_precisions=100;
        this.taux_esquives=0.04; this.taux_blocage=0.08; this.reduction_blocage=0.10;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de vrilles","Pollen toxique","Floraison guerrière"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Droy utilise Coup de vrilles !"); Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Poison(1, 0.04), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Droy répand un pollen toxique sur "+cible.getNom()+" !");
        double d = this.getAttaque()*1.10; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        Combat.appliquerEffet(this, cible, new Poison(2, 0.06), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Droy déclenche une floraison guerrière — ses plantes soignent l'équipe !");
        for (PersonnageBase al : a) if (al.estVivant()) { Combat.appliquerEffet(this, al, new Regeneration(0.07,2), log); }
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Coup de vrilles — 100% ATK + poison 1 tour (4% PV)."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Pollen toxique — 110% ATK + poison 2 tours (6% PV)."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Floraison guerrière — régénère toute l'équipe 2 tours (7% PV)."); }
}