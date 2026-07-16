package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffPrecision implements Effet {
    private int toursRestants;
    private double pourcentage;
    private double bonusApplique;

    public BuffPrecision(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        bonusApplique = cible.getTauxPrecisions() * pourcentage;
        cible.setPrecisions(cible.getTauxPrecisions() + bonusApplique);
        System.out.println(cible.getNom() + " gagne " + (pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        
        if (toursRestants <= 0) {
            cible.setPrecisions(cible.getTauxPrecisions() - bonusApplique);
            System.out.println("Le buff de precision de " + cible.getNom() + " se termine !");
        }
        toursRestants--;
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "Buff Precision"; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        bonusApplique = cible.getTauxPrecisions() * pourcentage;
        cible.setPrecisions(cible.getTauxPrecisions() + bonusApplique);
        log.add(cible.getNom() + " gagne " + (pourcentage * 100)
                + "% de precision pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestants <= 0) {
            cible.setPrecisions(cible.getTauxPrecisions() - bonusApplique);
            log.add("Le buff de precision de " + cible.getNom() + " se termine !");
        }
        toursRestants--;
    }

}
