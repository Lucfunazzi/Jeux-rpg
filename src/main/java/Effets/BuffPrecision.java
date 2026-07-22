package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffPrecision implements Effet {
    private int toursRestants;
    private double pourcentage;

    public BuffPrecision(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println("Le buff de precision de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "Buff Precision"; }

    public double getPourcentage() { return pourcentage; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add("Le buff de precision de " + cible.getNom() + " se termine !");
        }
    }

}
