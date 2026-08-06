package Effets;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Symbiose : lie deux personnages, une partie des degats/soins subis par l'un
 * est repercutee sur son partenaire. A appliquer sur les deux personnages du lien
 * (chacun avec une instance pointant vers l'autre).
 *
 * Le drapeau statique {@code partageEnCours} empeche une boucle infinie quand les
 * deux partenaires sont lies l'un a l'autre (partage a sens unique par declenchement).
 */
public class Symbiose implements Effet {
    private static boolean partageEnCours = false;

    private int toursRestant;
    private final double pourcentagePartage;
    private final PersonnageBase partenaire;

    /**
     * @param partenaire         Le personnage qui recoit une part des degats/soins.
     * @param pourcentagePartage Fraction des degats/soins repercutee (ex: 0.30 pour 30%).
     * @param toursRestant       Duree du lien en tours.
     */
    public Symbiose(PersonnageBase partenaire, double pourcentagePartage, int toursRestant) {
        this.partenaire = partenaire;
        this.pourcentagePartage = pourcentagePartage;
        this.toursRestant = toursRestant;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est lie par symbiose a " + partenaire.getNom()
                + " pendant " + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (estTermine()) {
            System.out.println("La symbiose de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0 || !partenaire.estVivant(); }

    @Override
    public String getNom() { return "Symbiose"; }

    public PersonnageBase getPartenaire() { return partenaire; }
    public int getToursRestant() { return toursRestant; }

    /** Partage une partie des degats subis par le porteur vers son partenaire (attaques directes). */
    public void partagerDegats(PersonnageBase porteur, double degatsSubis) {
        if (partageEnCours || degatsSubis <= 0 || !partenaire.estVivant()) return;
        double partage = degatsSubis * pourcentagePartage;
        partageEnCours = true;
        try {
            partenaire.subirDegats(partage);
            System.out.println("🔗 Symbiose : " + partenaire.getNom() + " ressent "
                    + String.format("%.0f", partage) + " PV de degats de " + porteur.getNom() + " !");
        } finally {
            partageEnCours = false;
        }
    }

    /** Variante avec log, pour le partage des degats de zone (DoT). */
    public void partagerDegats(PersonnageBase porteur, double degatsSubis, List<String> log) {
        if (partageEnCours || degatsSubis <= 0 || !partenaire.estVivant()) return;
        double partage = degatsSubis * pourcentagePartage;
        partageEnCours = true;
        try {
            partenaire.retirerVie(partage, log);
            log.add("🔗 Symbiose : " + partenaire.getNom() + " ressent "
                    + String.format("%.0f", partage) + " PV de degats de " + porteur.getNom() + " !");
        } finally {
            partageEnCours = false;
        }
    }

    /** Partage une partie des soins recus par le porteur vers son partenaire. */
    public void partagerSoin(PersonnageBase porteur, double soinRecu, List<String> log) {
        if (partageEnCours || soinRecu <= 0 || !partenaire.estVivant()) return;
        double partage = soinRecu * pourcentagePartage;
        partageEnCours = true;
        try {
            partenaire.recevoirSoin(partage, log);
        } finally {
            partageEnCours = false;
        }
    }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est lie par symbiose a " + partenaire.getNom()
                + " pendant " + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (estTermine()) {
            log.add("La symbiose de " + cible.getNom() + " se termine !");
        }
    }
}
