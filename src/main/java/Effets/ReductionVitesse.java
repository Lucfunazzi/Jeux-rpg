package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class ReductionVitesse implements Effet {
    private int toursRestants;
    private double pourcentage;

    public ReductionVitesse(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " perd " + (int)(pourcentage * 100)
                + "% de vitesse pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println("La réduction de vitesse de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "ReductionVitesse"; }

    public double getPourcentage() { return pourcentage; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " perd " + (int)(pourcentage * 100)
                + "% de vitesse pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add("La reduction de vitesse de " + cible.getNom() + " se termine !");
        }
    }

}
