package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Bouclier implements Effet {
    private double pointsBouclier;

    /**
     * @param montant Directement le montant de points de vie du bouclier (ex: 315.0 PV).
     */
    public Bouclier(double montant) {
        this.pointsBouclier = montant;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " reçoit un bouclier de "
                + String.format("%.1f", pointsBouclier) + " PV !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        // Le bouclier n'a pas de durée, il ne s'épuise pas avec le temps
    }

    public double absorberDegats(double degats) {
        if (pointsBouclier >= degats) {
            pointsBouclier -= degats;
            return 0; // Tous les dégâts sont absorbés
        } else {
            double degatsRestants = degats - pointsBouclier;
            pointsBouclier = 0; // Le bouclier est brisé
            return degatsRestants; // Le surplus de dégâts touche la vie du personnage
        }
    }

    @Override
    public boolean estTermine() {
        // L'effet s'arrête UNIQUEMENT quand le bouclier n'a plus de PV
        return pointsBouclier <= 0;
    }

    @Override
    public String getNom() {
        return "Bouclier";
    }

    public double getPointsBouclier() {
        return pointsBouclier;
    }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " recoit un bouclier de "
                + String.format("%.1f", pointsBouclier) + " PV !");
    }

}
