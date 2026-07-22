package Effets;
import Personnage.PersonnageBase;
import java.util.List;
public class Aveuglement implements Effet {
    private int toursRestants;
    private double pourcentage;
    public Aveuglement(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }
    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est aveugle ! -" + (pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }
    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println("L'aveuglement de " + cible.getNom() + " se dissipe !");
        }
    }
    @Override
    public boolean estTermine() { return toursRestants <= 0; }
    @Override
    public String getNom() { return "Aveuglement"; }

    public double getPourcentage() { return pourcentage; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est aveugle ! -" + (pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add("L'aveuglement de " + cible.getNom() + " se dissipe !");
        }
    }

}
