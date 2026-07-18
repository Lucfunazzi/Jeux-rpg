package Personnage.FairyTail;
import Combat.Combat;
import Effets.BuffAttaque;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.List;
public class perso_Warren extends PersonnageBase {
    public perso_Warren() {
        this.nom = "Warren"; this.niveau = 1; this.type = "Mage";
        this.role = "Support"; this.rarete = "C";
        double m = 1.00;
        this.vie=300*m; this.attaque=80*m; this.defense=75*m; this.vitesse=70*m;
        this.taux_critiques=0.06; this.degat_critiques=1.10; this.taux_precisions=100;
        this.taux_esquives=0.05; this.taux_blocage=0.10; this.reduction_blocage=0.12;
        this.degats_renvoi=0.80; initialiserVieMax();
    }
    @Override public String[] getNomsAttaques() {
        return new String[]{"Télékinésie","Onde psychique","Lien télépathique"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Warren utilise la télékinésie sur "+cible.getNom()+" !"); Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Warren perturbe l'esprit de "+cible.getNom()+" avec une onde psychique !");
        double d = this.getAttaque()*1.05; Combat.appliquerDegatsAvecLog(this, cible, d, log);
        if (Math.random()<0.35) Combat.appliquerEffet(this, cible, new Silence(1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Warren tisse un lien télépathique et synchronise l'équipe !");
        for (PersonnageBase al : a) if (al.estVivant()) Combat.appliquerEffet(this, al, new BuffAttaque(0.10, 2), log);
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Télékinésie — 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Onde psychique — 105% ATK, 35% silence 1 tour."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Lien télépathique — toute l'équipe +10% ATK 2 tours."); }
}