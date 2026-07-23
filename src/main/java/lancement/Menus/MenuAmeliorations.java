package lancement.Menus;

import Equipement.Equipement;
import Equipement.Inventaire;
import Equipement.Pierre;
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
            System.out.println("3. Pierres");

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
                case "3" -> menuPierres(joueur, personnagesRecruites, inventaire, scanner);
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
    // PIERRES (Force, Agilite, Vie, Precision, Attaque S, Contre, Critique, Blocage, Esquive)
    // =========================================================================

    private void menuPierres(Personnage_principale joueur,
                              ArrayList<PersonnageBase> personnagesRecruites,
                              Inventaire inventaire,
                              Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n--- PIERRES ---");
            System.out.println("Chaque piece d'equipement a " + Equipement.NB_EMPLACEMENTS_PIERRES + " emplacements de pierres.");
            System.out.println("2 pierres du meme type et niveau se synthetisent en 1 pierre de niveau superieur (max " + Pierre.NIVEAU_MAX + ").");
            System.out.println();
            System.out.println("1. Voir mon stock de pierres");
            System.out.println("2. Synthetiser des pierres");
            System.out.println("3. Gerer les pierres d'un equipement equipe");
            System.out.println("4. Gerer les pierres d'un equipement dans l'inventaire");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> afficherStockPierres(inventaire);
                case "2" -> synthetiserPierresMenu(inventaire, scanner);
                case "3" -> gererPierresEquipe(joueur, personnagesRecruites, inventaire, scanner);
                case "4" -> gererPierresInventaire(inventaire, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherStockPierres(Inventaire inventaire) {
        ArrayList<Inventaire.StackPierre> stock = inventaire.getPierres();
        if (stock.isEmpty()) {
            System.out.println("Aucune pierre en stock.");
            return;
        }
        System.out.println("\nStock de pierres :");
        for (int i = 0; i < stock.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + stock.get(i));
        }
    }

    private void synthetiserPierresMenu(Inventaire inventaire, Scanner scanner) {
        ArrayList<Inventaire.StackPierre> eligibles = new ArrayList<>();
        for (Inventaire.StackPierre s : inventaire.getPierres()) {
            if (s.getQuantite() >= 2 && s.getNiveau() < Pierre.NIVEAU_MAX) eligibles.add(s);
        }
        if (eligibles.isEmpty()) {
            System.out.println("Aucune pierre synthetisable (il faut 2 pierres identiques, niveau < " + Pierre.NIVEAU_MAX + ").");
            return;
        }

        System.out.println("\nPierres synthetisables (2 pierres -> 1 de niveau superieur) :");
        for (int i = 0; i < eligibles.size(); i++) {
            Inventaire.StackPierre s = eligibles.get(i);
            System.out.println("  " + (i + 1) + ". " + s + "  ->  " + new Pierre(s.getType(), s.getNiveau() + 1));
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > eligibles.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            Inventaire.StackPierre choisi = eligibles.get(choix - 1);
            System.out.println(inventaire.synthetiserPierre(choisi.getType(), choisi.getNiveau()));
        } catch (NumberFormatException ex) {
            System.out.println("Entree invalide.");
        }
    }

    private void gererPierresEquipe(Personnage_principale joueur,
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

        afficherListePierres(portes);
        Equipement choisi = choisirEquipementDansListe(portes, scanner);
        if (choisi == null) return;
        gererPierresEquipement(choisi, inventaire, scanner);
    }

    private void gererPierresInventaire(Inventaire inventaire, Scanner scanner) {
        ArrayList<Equipement> equips = inventaire.getEquipements();
        if (equips.isEmpty()) {
            System.out.println("Aucun equipement dans l'inventaire.");
            return;
        }

        afficherListePierres(equips);
        Equipement choisi = choisirEquipementDansListe(equips, scanner);
        if (choisi == null) return;
        gererPierresEquipement(choisi, inventaire, scanner);
    }

    private void afficherListePierres(ArrayList<Equipement> liste) {
        System.out.println();
        for (int i = 0; i < liste.size(); i++) {
            Equipement e = liste.get(i);
            int rempli = 0;
            for (Pierre p : e.getPierres()) if (p != null) rempli++;
            System.out.println((i + 1) + ". " + e + "  |  Pierres : " + rempli + "/" + Equipement.NB_EMPLACEMENTS_PIERRES);
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");
    }

    /** Ecran de gestion des 5 emplacements de pierres d'une piece d'equipement. Reutilisable par la GUI. */
    private void gererPierresEquipement(Equipement equip, Inventaire inventaire, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n--- Pierres de " + equip.getNomAffiche() + " ---");
            for (int i = 0; i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) {
                Pierre p = equip.getPierre(i);
                System.out.println("  " + (i + 1) + ". " + (p != null ? p : "[vide]"));
            }
            System.out.println("0. Retour");
            System.out.print("Choisissez un emplacement : ");

            int emplacement;
            try {
                emplacement = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Entree invalide.");
                continue;
            }
            if (emplacement == 0) { retour = true; continue; }
            if (emplacement < 1 || emplacement > Equipement.NB_EMPLACEMENTS_PIERRES) {
                System.out.println("Emplacement invalide.");
                continue;
            }

            int index = emplacement - 1;
            Pierre actuelle = equip.getPierre(index);
            if (actuelle != null) {
                System.out.println("Cet emplacement contient : " + actuelle);
                System.out.print("Retirer cette pierre ? (1 : Oui / 0 : Non) : ");
                if (scanner.nextLine().trim().equals("1")) {
                    equip.retirerPierre(index);
                    inventaire.ajouterPierre(actuelle.getType(), actuelle.getNiveau(), 1);
                    System.out.println("Pierre retiree et remise en stock.");
                }
            } else {
                ArrayList<Inventaire.StackPierre> dispo = inventaire.getPierres();
                if (dispo.isEmpty()) {
                    System.out.println("Aucune pierre en stock.");
                    continue;
                }
                System.out.println("\nPierres disponibles :");
                for (int i = 0; i < dispo.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + dispo.get(i));
                }
                System.out.println("0. Annuler");
                System.out.print("Choisissez une pierre a inserer : ");

                int choixPierre;
                try {
                    choixPierre = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException ex) {
                    System.out.println("Entree invalide.");
                    continue;
                }
                if (choixPierre == 0) continue;
                if (choixPierre < 1 || choixPierre > dispo.size()) {
                    System.out.println("Choix invalide.");
                    continue;
                }

                Inventaire.StackPierre choisi = dispo.get(choixPierre - 1);
                Pierre nouvelle = new Pierre(choisi.getType(), choisi.getNiveau());
                String resultat = equip.insererPierre(index, nouvelle);
                if (resultat.equals("OK")) {
                    inventaire.retirerPierre(choisi.getType(), choisi.getNiveau(), 1);
                    System.out.println("Pierre inseree : " + nouvelle);
                } else {
                    System.out.println(resultat);
                }
            }
        }
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