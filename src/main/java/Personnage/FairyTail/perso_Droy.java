package Personnage.FairyTail;
import Combat.Combat;
import Effets.Regeneration;
import Effets.Etourdissement;
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
        return new String[]{"Boxing Plantes","Support de plantes","Chaînes de Plantes"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> a, List<PersonnageBase> e, List<String> log) {
        log.add("Droy utilise Boxing plantes sur " + cible.getNom() + " !");
        double degats = this.getAttaque()*1.00;
        Combat.appliquerDegatsAvecLog(this,cible,degats,log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Droy lance Support de plantes !");
        PersonnageBase cibleSoin = null;
        for (PersonnageBase allie : equipeAlliee){
            if (allie.estVivant()){
                if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                    cibleSoin = allie;
            }
        }
        if (cibleSoin == null) return;
        double soin = this.getAttaque() * 1.00;
        cibleSoin.recevoirSoin(soin, log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Droy déclenche Chaine de plantes !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie){
            if(ennemi.estVivant()){
                double degats = this.getAttaque()*0.50 * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this,ennemi,degats,log);
                if (Math.random() < 0.10){
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
    }
    @Override public void descriptionAttaqueBase() { System.out.println("Boxing Plantes — inflige 100% ATK a une cible."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Support de plantes — soigne l'allie ayant le moins de PV pour 100% ATK en soin."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Chaine de plantes — inflige 50% ATK a tous les ennemis, 10% de chance d'Etourdissement 1 tour sur chacun. Puissance augmentee par la Rage."); }
}