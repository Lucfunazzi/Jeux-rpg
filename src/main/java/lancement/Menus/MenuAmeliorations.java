package lancement.Menus;

import Equipement.Equipement;
import Equipement.Inventaire;
import Personnage.PersonnageBase;
import Joueur.Personnage_principale;
import java.util.ArrayList;
import java.util.Scanner;
import lancement.GameContext;

/**
 * Menu Ameliorations — regroupe Fortification et Affinage.
 * Accessible depuis le menu principal.
 * L'affinage se debloque au niveau 20 du joueur.
 */
public class MenuAmeliorations {

    public static final String MATERIAU_AFFINAGE = "Pierre d'affinage";
    public static final int    NIVEAU_DEBLOCAGE_AFFINAGE = 20;
    public static final int    NIVEAU_MAX_AFFINAGE       = 100;

    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale     joueur               = ctx.joueur;
        ArrayList<PersonnageBase> personnagesRecruites = ctx.personnagesRecruites;
        Inventaire                inventaire           = ctx.inventaire;
        boolean retour = false;
        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("          AMELIORATIONS");
            System.out.println("========================================");
            System.out.println("1. Fortification");

            boolean affinageDebloque = joueur.getNiveau() >= NIVEAU_DEBLOCAGE_AFFINAGE;
            if (affinageDebloque) {
                System.out.println("2. Affinage");
            } else {
                System.out.println("2. Affinage  [Debloque niveau " + NIVEAU_DEBLOCAGE_AFFINAGE + "]");
            }

            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> menuFortification(joueur, personnagesRecruites, inventaire, scanner);
                case "2" -> {
                    if (affinageDebloque) {
                        menuAffinage(joueur, personnagesRecruites, inventaire, scanner);
                    } else {
                        System.out.println("L'affinage se debloque au niveau "
                                + NIVEAU_DEBLOCAGE_AFFINAGE + " !");
                    }
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // =========================================================================
    // FORTIFICATION
    // =========================================================================

    private void menuFortification(Personnage_principale joueur,
                                   ArrayList<PersonnageBase> personnagesRecruites,
                                   Inventaire inventaire,
                                   Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n--- FORTIFICATION ---");
            System.out.println("Renforcez vos equipements en depensant de l'or.");
            System.out.println("Chaque niveau double les stats de la piece.");
            System.out.println("Or disponible : " + String.format("%.0f", joueur.getOr()));
            System.out.println();
            System.out.println("1. Fortifier un equipement equipe");
            System.out.println("2. Fortifier un equipement dans l'inventaire");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> fortifierEquipe(joueur, personnagesRecruites, inventaire, scanner);
                case "2" -> fortifierInventaire(joueur, inventaire, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void fortifierEquipe(Personnage_principale joueur,
                                  ArrayList<PersonnageBase> personnagesRecruites,
                                  Inventaire inventaire,
                                  Scanner scanner) {
        PersonnageBase cible = choisirPersonnage(joueur, personnagesRecruites, scanner);
        if (cible == null) return;

        ArrayList<Equipement> portes = cible.getEquipementsPortes();
        if (portes.isEmpty()) {
            System.out.println(cible.getNom() + " ne porte aucun equipement.");
            return;
        }

        System.out.println("\nEquipements de " + cible.getNom() + " :");
        for (int i = 0; i < portes.size(); i++) {
            Equipement e = portes.get(i);
            int coutProchain = calculerCoutFortificationProchain(e);
            System.out.println((i + 1) + ". " + e
                    + "  ->  Fort." + e.getNiveauFortification()
                    + "  |  Prochain niveau : " + coutProchain + " or");
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > portes.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            Equipement cible2 = portes.get(choix - 1);
            executerFortification(joueur, cible2, scanner);
        } catch (NumberFormatException ex) {
            System.out.println("Entree invalide.");
        }
    }

    private void fortifierInventaire(Personnage_principale joueur,
                                      Inventaire inventaire,
                                      Scanner scanner) {
        ArrayList<Equipement> equips = inventaire.getEquipements();
        if (equips.isEmpty()) {
            System.out.println("Aucun equipement dans l'inventaire.");
            return;
        }

        System.out.println("\nEquipements dans l'inventaire :");
        for (int i = 0; i < equips.size(); i++) {
            Equipement e = equips.get(i);
            int coutProchain = calculerCoutFortificationProchain(e);
            System.out.println((i + 1) + ". " + e
                    + "  ->  Fort." + e.getNiveauFortification()
                    + "  |  Prochain niveau : " + coutProchain + " or");
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > equips.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            executerFortification(joueur, equips.get(choix - 1), scanner);
        } catch (NumberFormatException ex) {
            System.out.println("Entree invalide.");
        }
    }

    /**
     * Affiche le recapitulatif de fortification par tranches de x5 et demande confirmation.
     */
    private void executerFortification(Personnage_principale joueur,
                                        Equipement equip,
                                        Scanner scanner) {
        int niveauActuel = equip.getNiveauFortification();

        System.out.println("\n--- Fortification : " + equip.getNomAffiche() + " ---");
        System.out.println("Niveau actuel : " + niveauActuel);
        System.out.println();
        System.out.println("Jusqu'a quel niveau voulez-vous fortifier ?");
        System.out.println("(Entrez un niveau superieur a " + niveauActuel
                + " ou 0 pour annuler)");

        // Afficher les paliers x5 suivants à titre indicatif
        int fortMax = joueur.getNiveau();
        System.out.println();
        System.out.println("Paliers suivants (cout total depuis niveau actuel) :");
        System.out.println("  [Fortification max : Fort." + fortMax + " (niveau joueur)]");
        int coutCumul = 0;
        int affiche   = 0;
        for (int n = niveauActuel + 1; n <= Math.min(niveauActuel + 25, fortMax) && affiche < 5; n++) {
            coutCumul += calculerCoutFortificationNiveau(n - 1);
            if ((n - niveauActuel) % 5 == 0) {
                System.out.println("  -> +" + (n - niveauActuel) + " niveaux (jusqu'a Fort."
                        + n + ") : " + coutCumul + " or");
                affiche++;
            }
        }
        if (niveauActuel >= fortMax) {
            System.out.println("  Cet equipement est deja a la fortification maximale !");
            return;
        }
        System.out.println();
        System.out.print("Niveau cible : ");

        try {
            int niveauCible = Integer.parseInt(scanner.nextLine().trim());
            if (niveauCible == 0) return;

            int coutTotal = calculerCoutTotalFortification(niveauActuel, niveauCible);
            System.out.println("Cout total pour Fort." + niveauActuel
                    + " -> Fort." + niveauCible + " : " + coutTotal + " or");
            System.out.println("Or disponible : " + String.format("%.0f", joueur.getOr()));

            System.out.print("Confirmer ? (1 : Oui / 0 : Non) : ");
            if (!scanner.nextLine().trim().equals("1")) return;

            System.out.println(appliquerFortification(joueur, equip, niveauCible));

        } catch (NumberFormatException ex) {
            System.out.println("Entree invalide.");
        }
    }

    /** Cout total pour passer de niveauActuel a niveauCible. */
    public int calculerCoutTotalFortification(int niveauActuel, int niveauCible) {
        int total = 0;
        for (int n = niveauActuel; n < niveauCible; n++) total += calculerCoutFortificationNiveau(n);
        return total;
    }

    /**
     * Applique une fortification jusqu'au niveau cible si les conditions sont remplies
     * (niveau cible valide, plafond = niveau du joueur, or suffisant). Retourne le message resultat.
     * Reutilisable par la console et l'interface graphique.
     */
    public String appliquerFortification(Personnage_principale joueur, Equipement equip, int niveauCible) {
        int niveauActuel = equip.getNiveauFortification();
        if (niveauActuel >= joueur.getNiveau()) {
            return "Cet equipement est deja a la fortification maximale !";
        }
        if (niveauCible <= niveauActuel) {
            return "Le niveau cible doit etre superieur au niveau actuel (" + niveauActuel + ").";
        }
        if (niveauCible > joueur.getNiveau()) {
            return "Fortification maximale atteinte ! (Fort." + joueur.getNiveau()
                    + " max au niveau " + joueur.getNiveau() + ")";
        }

        int coutTotal = calculerCoutTotalFortification(niveauActuel, niveauCible);
        if (joueur.getOr() < coutTotal) {
            return "Or insuffisant ! (cout : " + coutTotal + " or, vous avez : "
                    + String.format("%.0f", joueur.getOr()) + ")";
        }

        joueur.retirerOr(coutTotal);
        equip.setNiveauFortification(niveauCible);
        return "Fortification reussie ! " + equip.getNomAffiche() + " est maintenant Fort." + niveauCible
                + "\nCout : " + coutTotal + " or"
                + "\nNouveaux bonus : " + equip.getDescriptionBonus();
    }

    /** Cout pour passer du niveau n au niveau n+1 : 200 × (n + 1) */
    public static int calculerCoutFortificationNiveau(int niveauActuel) {
        return 200 * (niveauActuel + 1);
    }

    private int calculerCoutFortificationProchain(Equipement e) {
        return calculerCoutFortificationNiveau(e.getNiveauFortification());
    }

    // =========================================================================
    // AFFINAGE
    // =========================================================================

    private void menuAffinage(Personnage_principale joueur,
                               ArrayList<PersonnageBase> personnagesRecruites,
                               Inventaire inventaire,
                               Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            int pierres = inventaire.getQuantiteMateriau(MATERIAU_AFFINAGE);
            System.out.println("\n--- AFFINAGE ---");
            System.out.println("Ameliore en % les stats natives de vos equipements.");
            System.out.println("Niveau d'affinage 1 = +1%, niveau 2 = +2%, etc.");
            System.out.println("Cout : (niveau suivant) x 2 pierres d'affinage.");
            System.out.println("Pierres d'affinage disponibles : " + pierres);
            System.out.println();
            System.out.println("1. Affiner un equipement equipe");
            System.out.println("2. Affiner un equipement dans l'inventaire");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> affinerEquipe(joueur, personnagesRecruites, inventaire, scanner);
                case "2" -> affinerInventaire(inventaire, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void affinerEquipe(Personnage_principale joueur,
                                ArrayList<PersonnageBase> personnagesRecruites,
                                Inventaire inventaire,
                                Scanner scanner) {
        PersonnageBase cible = choisirPersonnage(joueur, personnagesRecruites, scanner);
        if (cible == null) return;

        ArrayList<Equipement> portes = cible.getEquipementsPortes();
        if (portes.isEmpty()) {
            System.out.println(cible.getNom() + " ne porte aucun equipement.");
            return;
        }

        afficherListeAffinage(portes);
        Equipement choisi = choisirEquipementDansListe(portes, scanner);
        if (choisi == null) return;
        executerAffinage(choisi, inventaire, scanner);
    }

    private void affinerInventaire(Inventaire inventaire, Scanner scanner) {
        ArrayList<Equipement> equips = inventaire.getEquipements();
        if (equips.isEmpty()) {
            System.out.println("Aucun equipement dans l'inventaire.");
            return;
        }

        afficherListeAffinage(equips);
        Equipement choisi = choisirEquipementDansListe(equips, scanner);
        if (choisi == null) return;
        executerAffinage(choisi, inventaire, scanner);
    }

    private void afficherListeAffinage(ArrayList<Equipement> liste) {
        System.out.println();
        for (int i = 0; i < liste.size(); i++) {
            Equipement e = liste.get(i);
            int niveauAff = e.getNiveauAffinage();
            String statut = niveauAff >= NIVEAU_MAX_AFFINAGE
                    ? " [MAX]"
                    : "  |  Prochain : " + e.getCoutAffinageProchainNiveau()
                      + " pierres -> +" + (niveauAff + 1) + "%";
            System.out.println((i + 1) + ". " + e
                    + "  ->  Aff." + niveauAff + statut);
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");
    }

    private Equipement choisirEquipementDansListe(ArrayList<Equipement> liste, Scanner scanner) {
        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return null;
            if (choix < 1 || choix > liste.size()) {
                System.out.println("Choix invalide.");
                return null;
            }
            return liste.get(choix - 1);
        } catch (NumberFormatException ex) {
            System.out.println("Entree invalide.");
            return null;
        }
    }

    /**
     * Demande un niveau cible d'affinage, calcule le cout total en pierres et confirme.
     */
    private void executerAffinage(Equipement equip, Inventaire inventaire, Scanner scanner) {
        int niveauActuel = equip.getNiveauAffinage();

        if (niveauActuel >= NIVEAU_MAX_AFFINAGE) {
            System.out.println(equip.getNom() + " a atteint le niveau d'affinage maximum !");
            return;
        }

        int pierresDisponibles = inventaire.getQuantiteMateriau(MATERIAU_AFFINAGE);

        System.out.println("\n--- Affinage : " + equip.getNomAffiche() + " ---");
        System.out.println("Niveau actuel : Aff." + niveauActuel
                + " (+" + niveauActuel + "%)");
        System.out.println("Pierres d'affinage disponibles : " + pierresDisponibles);
        System.out.println();

        // Afficher les 5 prochains niveaux atteignables
        System.out.println("Prochains niveaux (cout total depuis niveau actuel) :");
        int coutCumul    = 0;
        int limiteAffiche = Math.min(niveauActuel + 5, NIVEAU_MAX_AFFINAGE);
        for (int n = niveauActuel + 1; n <= limiteAffiche; n++) {
            coutCumul += n * 2;   // cout pour passer de (n-1) a n
            String atteignable = coutCumul <= pierresDisponibles ? "" : " [pierres insuffisantes]";
            System.out.println("  -> Aff." + n + " (+" + n + "%) : "
                    + coutCumul + " pierres" + atteignable);
        }
        System.out.println();
        System.out.println("(Entrez un niveau entre " + (niveauActuel + 1)
                + " et " + NIVEAU_MAX_AFFINAGE + ", ou 0 pour annuler)");
        System.out.print("Niveau cible : ");

        try {
            int niveauCible = Integer.parseInt(scanner.nextLine().trim());
            if (niveauCible == 0) return;
            if (niveauCible <= niveauActuel || niveauCible > NIVEAU_MAX_AFFINAGE) {
                System.out.println("Niveau invalide.");
                return;
            }

            int coutTotal = calculerCoutTotalAffinage(niveauActuel, niveauCible);
            System.out.println("Cout total : " + coutTotal + " pierres d'affinage");
            System.out.println("Pierres disponibles : " + pierresDisponibles);

            System.out.print("Confirmer ? (1 : Oui / 0 : Non) : ");
            if (!scanner.nextLine().trim().equals("1")) return;

            System.out.println(appliquerAffinage(equip, inventaire, niveauCible));

        } catch (NumberFormatException ex) {
            System.out.println("Entree invalide.");
        }
    }

    /** Cout total (en pierres d'affinage) pour passer de niveauActuel a niveauCible. */
    public int calculerCoutTotalAffinage(int niveauActuel, int niveauCible) {
        int total = 0;
        for (int n = niveauActuel + 1; n <= niveauCible; n++) total += n * 2;
        return total;
    }

    /**
     * Applique un affinage jusqu'au niveau cible si les conditions sont remplies
     * (niveau cible valide, pierres suffisantes). Retourne le message resultat.
     * Reutilisable par la console et l'interface graphique.
     */
    public String appliquerAffinage(Equipement equip, Inventaire inventaire, int niveauCible) {
        int niveauActuel = equip.getNiveauAffinage();
        if (niveauActuel >= NIVEAU_MAX_AFFINAGE) {
            return equip.getNom() + " a atteint le niveau d'affinage maximum !";
        }
        if (niveauCible <= niveauActuel || niveauCible > NIVEAU_MAX_AFFINAGE) {
            return "Niveau invalide.";
        }

        int coutTotal = calculerCoutTotalAffinage(niveauActuel, niveauCible);
        int pierresDisponibles = inventaire.getQuantiteMateriau(MATERIAU_AFFINAGE);
        if (pierresDisponibles < coutTotal) {
            return "Pierres d'affinage insuffisantes ! (cout : " + coutTotal + ", vous avez : " + pierresDisponibles + ")";
        }

        inventaire.retirerMateriau(MATERIAU_AFFINAGE, coutTotal);
        equip.setNiveauAffinage(niveauCible);
        return "Affinage reussi ! " + equip.getNomAffiche() + " -> Aff." + niveauCible + " (+" + niveauCible + "%)"
                + "\nCout : " + coutTotal + " pierres"
                + "\nNouveaux bonus : " + equip.getDescriptionBonus();
    }

    // =========================================================================
    // UTILITAIRES COMMUNS
    // =========================================================================

    private PersonnageBase choisirPersonnage(Personnage_principale joueur,
                                              ArrayList<PersonnageBase> recrutes,
                                              Scanner scanner) {
        ArrayList<PersonnageBase> tous = new ArrayList<>();
        tous.add(joueur);
        tous.addAll(recrutes);

        System.out.println("\nChoisissez un personnage :");
        for (int i = 0; i < tous.size(); i++) {
            PersonnageBase p = tous.get(i);
            System.out.println((i + 1) + ". " + p.getNom()
                    + " [" + p.getRole() + "] Niv." + p.getNiveau());
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return null;
            if (choix < 1 || choix > tous.size()) {
                System.out.println("Choix invalide.");
                return null;
            }
            return tous.get(choix - 1);
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return null;
        }
    }
}