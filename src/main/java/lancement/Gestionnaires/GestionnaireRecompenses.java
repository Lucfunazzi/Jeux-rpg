package lancement.Gestionnaires;

import Equipement.CarteOr;
import Equipement.Inventaire;
import Equipement.PotionEnergie;
import Joueur.Personnage_principale;
import Personnage.FairyTail.perso_Rogue;
import Personnage.FairyTail.perso_Sting;
import Personnage.FairyTail.perso_Yukino;
import Personnage.PersonnageBase;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;

/**
 * Regroupe les 4 systèmes de récompenses : niveau, pointage du mois,
 * connexion 7 jours, et récompense quotidienne (paliers de temps).
 * Nom du matériau utilisé pour les boites de pierre Lv.1 : voir
 * {@link lancement.Menus.MenuExamenS#MATERIAU_BOITE_PIERRE_LV1}.
 */
public class GestionnaireRecompenses {

    private static final String BOITE_PIERRE_LV1 = "Boite de pierre Lv.1";

    // ── Récompenses de niveau ────────────────────────────────────────────
    public static final int[] PALIERS_NIVEAU = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private boolean[] niveauReclame = new boolean[PALIERS_NIVEAU.length];

    public boolean estNiveauDisponible(int index, int niveauJoueur) {
        return niveauJoueur >= PALIERS_NIVEAU[index] && !niveauReclame[index];
    }

    public boolean isNiveauReclame(int index) { return niveauReclame[index]; }

    /** Or / Cartes d'Or Lv.1 / Boites de pierre Lv.1 offerts par ce palier. */
    public int[] recompenseNiveau(int index) {
        int or     = PALIERS_NIVEAU[index] * 500;
        int cartes = index + 1;
        int boites = Math.max(0, index - 3);
        return new int[]{or, cartes, boites};
    }

    public String afficherRecompenseNiveau(int index) {
        int[] r = recompenseNiveau(index);
        StringBuilder sb = new StringBuilder(r[0] + " or, " + r[1] + " Carte(s) d'Or Lv.1");
        if (r[2] > 0) sb.append(", ").append(r[2]).append(" Boite(s) de pierre Lv.1");
        return sb.toString();
    }

    public String reclamerNiveau(int index, Personnage_principale joueur, Inventaire inventaire) {
        int[] r = recompenseNiveau(index);
        joueur.ajouterOr(r[0]);
        inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, r[1]);
        if (r[2] > 0) inventaire.ajouterMateriau(BOITE_PIERRE_LV1, r[2]);
        niveauReclame[index] = true;
        return "Niveau " + PALIERS_NIVEAU[index] + " reclame ! " + afficherRecompenseNiveau(index);
    }

    public boolean[] getNiveauReclame()            { return niveauReclame; }
    public void      setNiveauReclame(boolean[] v) { this.niveauReclame = redimensionner(v, PALIERS_NIVEAU.length); }

    // ── Pointage du mois ──────────────────────────────────────────────────
    private static final int[] PALIERS_MOIS_FIXES = {2, 5, 14, 21}; // + dernier jour du mois en cours

    private int       joursCumulesMois = 0;
    private LocalDate dernierJourComptePointage;
    private int       moisComptePointage = -1;
    private boolean[] moisReclame = new boolean[5];

    /** À appeler à chaque ouverture du jeu / du menu : incrémente le compteur si nouveau jour, reset si nouveau mois. */
    public void mettreAJourPointageMois() {
        LocalDate aujourdhui  = LocalDate.now();
        int       moisCourant = aujourdhui.getYear() * 12 + aujourdhui.getMonthValue();

        if (moisCourant != moisComptePointage) {
            joursCumulesMois          = 0;
            moisReclame               = new boolean[5];
            moisComptePointage        = moisCourant;
            dernierJourComptePointage = null;
        }
        if (dernierJourComptePointage == null || !dernierJourComptePointage.equals(aujourdhui)) {
            joursCumulesMois++;
            dernierJourComptePointage = aujourdhui;
        }
    }

    /** Paliers en jours pour le mois en cours (le dernier s'ajuste à 28-31 selon le calendrier). */
    public int[] getPaliersMois() {
        int joursDuMois = YearMonth.now().lengthOfMonth();
        return new int[]{PALIERS_MOIS_FIXES[0], PALIERS_MOIS_FIXES[1], PALIERS_MOIS_FIXES[2], PALIERS_MOIS_FIXES[3], joursDuMois};
    }

    public int getJoursCumulesMois() { return joursCumulesMois; }

    public boolean estMoisDisponible(int index) {
        return joursCumulesMois >= getPaliersMois()[index] && !moisReclame[index];
    }

    public boolean isMoisReclame(int index) { return moisReclame[index]; }

    /**
     * Points du mois offerts par chacun des 5 paliers (2/5/14/21 jours + dernier jour du mois).
     * Total = 40 000 points = de quoi acheter 80 fragments [S] (500 pts/fragment) a la
     * boutique du mois, soit exactement le cout de recrutement d'un rang S en fragments.
     */
    private static final int[] POINTS_PAR_PALIER = {1000, 3000, 8000, 12000, 16000};

    public int recompenseMois(int index) { return POINTS_PAR_PALIER[index]; }

    public String afficherRecompenseMois(int index) {
        return POINTS_PAR_PALIER[index] + " points du mois";
    }

    public String reclamerMois(int index, Personnage_principale joueur, Inventaire inventaire) {
        pointsMois += POINTS_PAR_PALIER[index];
        moisReclame[index] = true;
        return "Palier " + getPaliersMois()[index] + " jours reclame ! +" + POINTS_PAR_PALIER[index]
                + " points du mois ! (Total : " + pointsMois + ")";
    }

    // ── Points du mois : monnaie persistante (ne reset pas), pour la boutique du mois ──
    private int pointsMois = 0;

    public int getPointsMois()          { return pointsMois; }
    public void setPointsMois(int p)     { this.pointsMois = p; }

    /** Prix (en points) d'un fragment de personnage pour la boutique du mois, selon la rarete. */
    public static int prixFragmentBoutiqueMois(String rarete) {
        return switch (rarete) {
            case "C" -> 50;
            case "B" -> 100;
            case "A" -> 200;
            default  -> 500; // S, SS, UR
        };
    }

    /** Achete 1 fragment du personnage donne a la boutique du mois. */
    public String acheterFragmentBoutiqueMois(String nomPersonnage, String rarete, Inventaire inventaire) {
        int prix = prixFragmentBoutiqueMois(rarete);
        if (pointsMois < prix) {
            return "Points insuffisants : " + pointsMois + " / " + prix + " points.";
        }
        pointsMois -= prix;
        GestionnaireEtoilesPerso.ajouterFragments(inventaire, nomPersonnage, 1);
        return "1 fragment de " + nomPersonnage + " achete pour " + prix + " points ! (Points restants : " + pointsMois + ")";
    }

    public void setJoursCumulesMois(int n)                { this.joursCumulesMois = n; }
    public LocalDate getDernierJourComptePointage()       { return dernierJourComptePointage; }
    public void setDernierJourComptePointage(LocalDate d) { this.dernierJourComptePointage = d; }
    public int getMoisComptePointage()                    { return moisComptePointage; }
    public void setMoisComptePointage(int m)               { this.moisComptePointage = m; }
    public boolean[] getMoisReclame()                      { return moisReclame; }
    public void setMoisReclame(boolean[] v)                { this.moisReclame = redimensionner(v, 5); }

    // ── Récompense de connexion 7 jours ─────────────────────────────────────
    private int       jourConnexion = 0; // 0 = pas encore commencee, 1-7 en cours
    private LocalDate dernierJourConnexion;
    private boolean[] jourReclame = new boolean[7];
    private boolean   terminee = false;

    /** À appeler à chaque ouverture du jeu / du menu : avance d'un jour si nouveau jour calendaire. */
    public void mettreAJourConnexion() {
        if (terminee) return;
        LocalDate aujourdhui = LocalDate.now();
        if (dernierJourConnexion == null || !dernierJourConnexion.equals(aujourdhui)) {
            if (jourConnexion < 7) jourConnexion++;
            dernierJourConnexion = aujourdhui;
        }
    }

    public boolean estTerminee()      { return terminee; }
    public int getJourConnexion()     { return jourConnexion; }

    public boolean estJourDisponible(int jour) { // jour : 1-7
        return jour <= jourConnexion && !jourReclame[jour - 1];
    }

    public boolean isJourReclame(int jour) { return jourReclame[jour - 1]; }

    /** Materiau et quantite requise pour recruter Natsu [A] — voir MenuRecrutementRare. */
    public static final String MATERIAU_IGNIR       = lancement.Menus.MenuRecrutementRare.MATERIAU_NATSU;
    public static final int    QUANTITE_IGNIR_JOUR2 = lancement.Menus.MenuRecrutementRare.COUT_RECRUTEMENT;

    /** Personnages proposes dans le coffre de rang S du jour 7 (l'utilisateur en choisit un). */
    public static final String[] CHOIX_COFFRE_RANG_S = {"Yukino", "Sting", "Rogue"};

    public String afficherRecompenseJour(int jour) {
        return switch (jour) {
            case 1 -> "100 Carte(s) d'Or Lv.1, 2 Potion(s) d'Energie";
            case 2 -> QUANTITE_IGNIR_JOUR2 + "x " + MATERIAU_IGNIR + " (recrute Natsu [A] instantanement)";
            case 3 -> "2 Petite(s) Potion(s) d'Energie";
            case 4 -> "3 000 or";
            case 5 -> "1 Boite de pierre Lv.1";
            case 6 -> "5 Carte(s) d'Or Lv.1";
            default -> "Coffre de personnage [S] au choix : " + String.join(" / ", CHOIX_COFFRE_RANG_S);
        };
    }

    /** Reclame un jour 1-6 (pas de choix). Le jour 7 passe par {@link #reclamerJour7}. */
    public String reclamerJour(int jour, Personnage_principale joueur, Inventaire inventaire) {
        switch (jour) {
            case 1 -> {
                inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, 100);
                inventaire.ajouterMateriau(PotionEnergie.MOYENNE.nom, 2);
            }
            case 2 -> inventaire.ajouterMateriau(MATERIAU_IGNIR, QUANTITE_IGNIR_JOUR2);
            case 3 -> inventaire.ajouterMateriau(PotionEnergie.PETITE.nom, 2);
            case 4 -> joueur.ajouterOr(3000);
            case 5 -> inventaire.ajouterMateriau(BOITE_PIERRE_LV1, 1);
            case 6 -> inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, 5);
            default -> throw new IllegalArgumentException("Le jour 7 necessite un choix, voir reclamerJour7().");
        }
        jourReclame[jour - 1] = true;
        return "Jour " + jour + " reclame ! " + afficherRecompenseJour(jour);
    }

    /** Reclame le jour 7 : {@code choix} doit etre l'un de {@link #CHOIX_COFFRE_RANG_S}. */
    public String reclamerJour7(String choix, ArrayList<PersonnageBase> personnagesRecruites) {
        PersonnageBase nouveau = switch (choix) {
            case "Yukino" -> new perso_Yukino();
            case "Sting"  -> new perso_Sting();
            case "Rogue"  -> new perso_Rogue();
            default -> throw new IllegalArgumentException("Choix invalide : " + choix);
        };
        boolean dejaPresent = personnagesRecruites.stream()
                .anyMatch(p -> p.getClass().equals(nouveau.getClass()));
        if (!dejaPresent) personnagesRecruites.add(nouveau);

        jourReclame[6] = true;
        terminee = true;
        return "Jour 7 reclame ! " + choix + " [S] " + (dejaPresent ? "deja possede — recompense ignoree." : "rejoint votre equipe !")
                + "\nRecompense des 7 jours terminee !";
    }

    public void setJourConnexion(int j)                  { this.jourConnexion = j; }
    public LocalDate getDernierJourConnexion()           { return dernierJourConnexion; }
    public void setDernierJourConnexion(LocalDate d)     { this.dernierJourConnexion = d; }
    public boolean[] getJourReclame()                    { return jourReclame; }
    public void setJourReclame(boolean[] v)               { this.jourReclame = redimensionner(v, 7); }
    public void setTerminee(boolean t)                    { this.terminee = t; }

    // ── Récompense quotidienne (paliers de temps) ───────────────────────────
    private LocalDateTime derniereReclamation30min;

    public boolean peutReclamer30min() {
        return derniereReclamation30min == null
                || Duration.between(derniereReclamation30min, LocalDateTime.now()).toMinutes() >= 30;
    }

    public String getTempsRestant30min() {
        if (peutReclamer30min()) return "Disponible";
        long restant = 30 - Duration.between(derniereReclamation30min, LocalDateTime.now()).toMinutes();
        return restant + " min restantes";
    }

    public String reclamer30min(Inventaire inventaire) {
        if (!peutReclamer30min()) return "Pas encore disponible (" + getTempsRestant30min() + ").";
        inventaire.ajouterMateriau(PotionEnergie.PETITE.nom, 2);
        inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, 10);
        derniereReclamation30min = LocalDateTime.now();
        return "Recompense (30 min) reclamee ! 2 Petite(s) Potion(s) d'Energie, 10 Carte(s) d'Or Lv.1";
    }

    public LocalDateTime getDerniereReclamation30min()          { return derniereReclamation30min; }
    public void setDerniereReclamation30min(LocalDateTime d)    { this.derniereReclamation30min = d; }

    // ── Utilitaire ────────────────────────────────────────────────────────
    private boolean[] redimensionner(boolean[] v, int taille) {
        boolean[] resultat = new boolean[taille];
        if (v != null) System.arraycopy(v, 0, resultat, 0, Math.min(v.length, resultat.length));
        return resultat;
    }
}
