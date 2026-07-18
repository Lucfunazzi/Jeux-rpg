package Personnage.FairyTail;
import Combat.Combat;
import Effets.BuffAttaque;
import Effets.BuffDefense;
import Effets.Etourdissement;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_ElfmanBete extends PersonnageBase {
    public perso_ElfmanBete() {
        this.nom = "Elfman Bête"; this.niveau = 1; this.type = "Guerrier";
        this.role = "Tank"; this.rarete = "B";
        double m = 1.30;
        this.vie=520*m; this.attaque=145*m; this.defense=120*m; this.vitesse=85*m;
        this.taux_critiques=0.10; this.degat_critiques=1.20; this.taux_precisions=100;
        this.taux_esquives=0.04; this.taux_blocage=0.15; this.reduction_blocage=0.18;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Poing de la bête","Prise du colosse","Forme de bête totale"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Elfman Bête écrase "+cible.getNom()+" de son poing colossal !");
        Combat.attaquer(this, cible, log);
        if (Math.random()<0.15) Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Elfman saisit "+cible.getNom()+" dans une prise titanesque !");
        double d = this.getAttaque()*1.25; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Elfman libère sa forme de bête totale — puissance maximale !");
        Combat.appliquerEffet(this, new BuffAttaque(0.25, 3), log);
        Combat.appliquerEffet(this, new BuffDefense(0.20, 3), log);
        double soin = this.getVieMax()*0.12; this.recevoirSoin(soin, log);
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Poing de la bête — 100% ATK, 15% étourdissement 1 tour."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Prise du colosse — 125% ATK + étourdissement 1 tour."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Forme de bête totale — +25% ATK et +20% DEF 3 tours, se soigne 12% PV max."); }
}