package lancement.Gestionnaires;

import Equipement.Inventaire;
import Joueur.Personnage_principale;
import java.time.LocalDate;
import java.util.Random;

/**
 * Mini-jeu "Chasse au tresor" : un nombre limite de fouilles par jour (reset a
 * minuit, meme principe que GestionnaireDonjon), chacune tirant au sort un
 * Parchemin de Chasse A/S/SS (ou un petit tresor d'or). Ces parchemins servent
 * ensuite a recruter/faire evoluer les personnages rares (voir MenuRecrutementRare).
 */
public class GestionnaireChasseTresor {

    public static final int MAX_FOUILLES_PAR_JOUR = 5;

    public static final String PARCHEMIN_A  = "Parchemin de Chasse A";
    public static final String PARCHEMIN_S  = "Parchemin de Chasse S";
    public static final String PARCHEMIN_SS = "Parchemin de Chasse SS";

    private static final Random RNG = new Random();

    private int       fouilles     = 0;
    private LocalDate dernierReset = LocalDate.now();

    // ── Reset quotidien ───────────────────────────────────────────────────
    public void mettreAJour() {
        LocalDate aujourdhui = LocalDate.now();
        if (!aujourdhui.equals(dernierReset)) {
            fouilles     = 0;
            dernierReset = aujourdhui;
        }
    }

    public boolean peutFouiller() {
        mettreAJour();
        return fouilles < MAX_FOUILLES_PAR_JOUR;
    }

    public int getFouillesRestantes() {
        mettreAJour();
        return MAX_FOUILLES_PAR_JOUR - fouilles;
    }

    /** Resultat d'une fouille : materiau obtenu (null si petit tresor d'or seul), quantite, or gagne. */
    public record ResultatFouille(String materiau, int quantite, int or) {}

    /** Fouille un emplacement : consomme une tentative du jour et tire au sort la recompense. */
    public ResultatFouille fouiller(Inventaire inventaire, Personnage_principale joueur) {
        fouilles++;

        double roll = RNG.nextDouble();
        if (roll < 0.10) {
            int or = 800 + RNG.nextInt(701);
            joueur.ajouterOr(or);
            return new ResultatFouille(null, 0, or);
        } else if (roll < 0.55) {
            int qte = 1 + RNG.nextInt(2);
            inventaire.ajouterMateriau(PARCHEMIN_A, qte);
            return new ResultatFouille(PARCHEMIN_A, qte, 0);
        } else if (roll < 0.85) {
            inventaire.ajouterMateriau(PARCHEMIN_S, 1);
            return new ResultatFouille(PARCHEMIN_S, 1, 0);
        } else {
            inventaire.ajouterMateriau(PARCHEMIN_SS, 1);
            return new ResultatFouille(PARCHEMIN_SS, 1, 0);
        }
    }

    // ── Getters/Setters pour sauvegarde ───────────────────────────────────
    public int       getFouillesUtilisees()          { return fouilles; }
    public void       setFouillesUtilisees(int n)     { this.fouilles = n; }
    public LocalDate  getDernierReset()               { return dernierReset; }
    public void       setDernierReset(LocalDate date) { this.dernierReset = date; }
}
