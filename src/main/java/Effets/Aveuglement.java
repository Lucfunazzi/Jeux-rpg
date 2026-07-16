package Effets;
import Personnage.PersonnageBase;
import java.util.List;
public class Aveuglement implements Effet {
    private int toursRestants;
    private double pourcentage;
    private double malusApplique;
    public Aveuglement(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }
    @Override
    public void appliquer(PersonnageBase cible) {
        malusApplique = cible.getTauxPrecisions() * pourcentage;
        cible.setPrecisions(cible.getTauxPrecisions() - malusApplique);
        System.out.println(cible.getNom() + " est aveugle ! -" + (pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }
    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestants <= 0) {
            cible.setPrecisions(cible.getTauxPrecisions() + malusApplique);
            System.out.println("L'aveuglement de " + cible.getNom() + " se dissipe !");
        }
        toursRestants--;
    }
    @Override
    public boolean estTermine() { return toursRestants <= 0; }
    @Override
    public String getNom() { return "Aveuglement"; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        malusApplique = cible.getTauxPrecisions() * pourcentage;
        cible.setPrecisions(cible.getTauxPrecisions() - malusApplique);
        log.add(cible.getNom() + " est aveugle ! -" + (pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestants <= 0) {
            cible.setPrecisions(cible.getTauxPrecisions() + malusApplique);
            log.add("L'aveuglement de " + cible.getNom() + " se dissipe !");
        }
        toursRestants--;
    }

}
