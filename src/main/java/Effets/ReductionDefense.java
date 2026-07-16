package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class ReductionDefense implements Effet {
    private int toursRestants;
    private double pourcentage;

    public ReductionDefense(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " perd " + (int)(pourcentage * 100)
                + "% de défense pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println("La réduction de défense de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "ReductionDefense"; }

    public double getPourcentage() { return pourcentage; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " perd " + (int)(pourcentage * 100)
                + "% de defense pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add("La reduction de defense de " + cible.getNom() + " se termine !");
        }
    }

}
