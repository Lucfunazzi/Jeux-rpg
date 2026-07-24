package lancement.Menus;

import Equipement.CarteOr;
import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.FragmentEquipement;
import Equipement.GestionnaireFragments;
import Equipement.Inventaire;
import Equipement.Pierre;
import Equipement.PotionEnergie;
import Personnage.PersonnageBase;
import Joueur.Personnage_principale;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lancement.GameContext;

public class MenuInventaire {

    private final GestionnaireFragments gestionnaireFragments = new GestionnaireFragments();

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("            INVENTAIRE");
            System.out.println("========================================");
            System.out.println("1. Voir les equipements");
            System.out.println("2. Equiper un personnage");
            System.out.println("3. Voir l'equipement d'un personnage");
            System.out.println("4. Equiper un set complet");
            System.out.println("5. Utiliser des Cartes d'Or");
            System.out.println("6. Synthetiser un equipement [A] (fragments)");
            System.out.println("7. Ouvrir des Boites de pierre");
            System.out.println("8. Utiliser une Potion d'Energie");
            System.out.println("9. Recycler un equipement (Pieces d'equipement)");
            System.out.println("10. Boutique d'equipement (fragments)");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> ctx.inventaire.afficher();
                case "2" -> menuEquiper(ctx, scanner);
                case "3" -> menuVoirEquipement(ctx, scanner);
                case "4" -> menuEquiperSet(ctx, scanner);
                case "5" -> menuCartesOr(ctx, scanner);
                case "6" -> menuSynthese(ctx, scanner);
                case "7" -> menuOuvrirBoitesPierre(ctx, scanner);
                case "8" -> menuUtiliserPotionEnergie(ctx, scanner);
                case "9" -> menuRecyclerEquipement(ctx, scanner);
                case "10" -> menuBoutiqueEquipement(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Equiper un set complet d'un même rang ─────────────────────────────
    private void menuEquiperSet(GameContext ctx, Scanner scanner) {
        Inventaire inventaire = ctx.inventaire;

        if (inventaire.estVide()) {
            System.out.println("L'inventaire est vide !");
            return;
        }

        PersonnageBase cible = choisirPersonnage(ctx, scanner);
        if (cible == null) return;

        // Collecter les raretés disponibles dans l'inventaire
        List<Equipement.Rarete> raretesDisponibles = new ArrayList<>();
        for (Equipement e : inventaire.getEquipements()) {
            if (!raretesDisponibles.contains(e.getRarete()))
                raretesDisponibles.add(e.getRarete());
        }

        if (raretesDisponibles.isEmpty()) {
            System.out.println("Aucun equipement disponible dans l'inventaire.");
            return;
        }

        System.out.println("\nRaretes disponibles dans l'inventaire :");
        for (int i = 0; i < raretesDisponibles.size(); i++)
            System.out.println((i + 1) + ". Rarete " + raretesDisponibles.get(i).name());
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        int choixRarete;
        try {
            choixRarete = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choixRarete == 0) return;
        if (choixRarete < 1 || choixRarete > raretesDisponibles.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        Equipement.Rarete rareteChoisie = raretesDisponibles.get(choixRarete - 1);

        // Filtrer les équipements compatibles de la rareté choisie
        List<Equipement> candidats = new ArrayList<>();
        for (Equipement e : inventaire.getEquipements()) {
            if (e.getRarete() == rareteChoisie && estCompatible(cible, e))
                candidats.add(e);
        }

        // Afficher l'état des slots avant d'équiper
        System.out.println("\nEtat des slots de " + cible.getNom() + " :");
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement actuel = cible.getEquipement(slot);
            if (actuel != null)
                System.out.println("  [OCCUPE] " + slot.name() + " : " + actuel.getNom());
        }

        // Pour chaque slot libre, équiper le premier candidat disponible
        int equipesCount = 0;
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            if (cible.getEquipement(slot) != null) continue;

            Equipement aEquiper = null;
            for (Equipement e : candidats) {
                if (e.getSlot() == slot) { aEquiper = e; break; }
            }
            if (aEquiper == null) continue;

            inventaire.retirerEquipement(aEquiper);
            cible.equiper(aEquiper);
            candidats.remove(aEquiper);
            System.out.println("  → " + slot.name() + " : " + aEquiper.getNom() + " equipe !");
            equipesCount++;
        }

        if (equipesCount == 0)
            System.out.println("Aucun slot libre compatible avec la rarete " + rareteChoisie.name() + ".");
        else {
            System.out.println(equipesCount + " piece(s) equipee(s) de rarete " + rareteChoisie.name() + ".");
            ctx.sauvegarde.sauvegarder(ctx);
        }
    }

    // ── Choisir un personnage puis une pièce à équiper ────────────────────
    private void menuEquiper(GameContext ctx, Scanner scanner) {
        if (ctx.inventaire.estVide()) {
            System.out.println("L'inventaire est vide !");
            return;
        }

        PersonnageBase cible = choisirPersonnage(ctx, scanner);
        if (cible == null) return;

        System.out.println("\nQue voulez-vous faire ?");
        System.out.println("1. Equiper une piece");
        System.out.println("2. Desequiper une piece");
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        switch (scanner.nextLine().trim()) {
            case "1" -> equiperPiece(ctx, cible, scanner);
            case "2" -> desequiperPiece(ctx, cible, scanner);
            case "0" -> {}
            default  -> System.out.println("Choix invalide.");
        }
    }

    private void equiperPiece(GameContext ctx, PersonnageBase cible, Scanner scanner) {
        List<Equipement> disponibles = ctx.inventaire.getEquipements();

        List<Equipement> filtres = new ArrayList<>();
        for (Equipement e : disponibles)
            if (estCompatible(cible, e)) filtres.add(e);

        if (filtres.isEmpty()) {
            System.out.println("Aucun equipement compatible dans l'inventaire.");
            return;
        }

        System.out.println("\nEquipements disponibles pour " + cible.getNom() + " :");
        for (int i = 0; i < filtres.size(); i++) {
            Equipement e = filtres.get(i);
            Equipement actuel = cible.getEquipement(e.getSlot());
            String actuelStr = actuel != null ? " (remplace : " + actuel.getNom() + ")" : "";
            System.out.println((i + 1) + ". " + e + actuelStr);
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > filtres.size()) {
                System.out.println("Choix invalide.");
                return;
            }

            Equipement choisi = filtres.get(choix - 1);
            int rangRequis = EquipementFactory.rangJoueurRequisPourEquiper(choisi.getRarete());
            if (ctx.rangJoueur.getRang().ordinal() < rangRequis) {
                System.out.println(choisi.getNom() + " necessite le rang joueur "
                        + lancement.RangJoueur.Rang.values()[rangRequis] + " (actuel : " + ctx.rangJoueur.getRangNom() + ").");
                return;
            }

            Equipement ancien = cible.getEquipement(choisi.getSlot());
            if (ancien != null) ctx.inventaire.ajouterEquipement(ancien);

            ctx.inventaire.retirerEquipement(choisi);
            cible.equiper(choisi);
            System.out.println(cible.getNom() + " equipe : " + choisi.getNom() + " !");
            ctx.sauvegarde.sauvegarder(ctx);

        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    private void desequiperPiece(GameContext ctx, PersonnageBase cible, Scanner scanner) {
        List<Equipement> portes = cible.getEquipementsPortes();
        if (portes.isEmpty()) {
            System.out.println(cible.getNom() + " ne porte aucun equipement.");
            return;
        }

        System.out.println("\nEquipements portes par " + cible.getNom() + " :");
        for (int i = 0; i < portes.size(); i++)
            System.out.println((i + 1) + ". " + portes.get(i));
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > portes.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            Equipement retire = portes.get(choix - 1);
            cible.desequiper(retire.getSlot());
            ctx.inventaire.ajouterEquipement(retire);
            System.out.println(retire.getNom() + " retire et remis dans l'inventaire.");
            ctx.sauvegarde.sauvegarder(ctx);

        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    // ── Voir l'équipement d'un personnage ─────────────────────────────────
    private void menuVoirEquipement(GameContext ctx, Scanner scanner) {
        PersonnageBase cible = choisirPersonnage(ctx, scanner);
        if (cible == null) return;
        afficherEquipementPersonnage(cible);
    }

    private void afficherEquipementPersonnage(PersonnageBase p) {
        System.out.println("\n--- Equipement de " + p.getNom() + " ---");
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement e = p.getEquipement(slot);
            String nomSlot = switch (slot) {
                case ARME        -> "Arme       ";
                case COUVRE_CHEF -> "Tete       ";
                case TORSE       -> "Torse      ";
                case MAINS       -> "Mains      ";
                case JAMBIERES   -> "Jambieres  ";
                case BOTTES      -> "Bottes     ";
            };
            System.out.println(nomSlot + " : " + (e != null ? e.toString() : "vide"));
        }
        System.out.println("\nBonus totaux equipement :");
        System.out.println("  ATK +" + String.format("%.0f", p.getBonusEquipementATK())
                + " | DEF +" + String.format("%.0f", p.getBonusEquipementDEF())
                + " | PV +"  + String.format("%.0f", p.getBonusEquipementPV())
                + " | VIT +" + String.format("%.0f", p.getBonusEquipementVIT()));
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    /**
     * Vérifie si un équipement est compatible avec un personnage.
     * Les pièces non-armes sont toujours compatibles.
     * Les armes dépendent du type de classe du personnage.
     */
    // ── Synthèse d'équipements (A/S/SS/SSS/UR) par fragments ───────────────
    private void menuSynthese(GameContext ctx, Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("    SYNTHESE — EQUIPEMENTS");
        System.out.println("========================================");
        System.out.println("Collectez assez de fragments identiques pour creer un equipement.");
        System.out.println("Rang A : drops en Chapitre 3 Elite. Rangs S/SS/SSS/UR : Boutique d'equipement.\n");

        // Afficher tous les fragments (progression même à 0)
        List<FragmentEquipement> catalogue = gestionnaireFragments.getCatalogue();

        // Séparer : synthétisables en premier, puis les autres
        List<FragmentEquipement> prets    = new ArrayList<>();
        List<FragmentEquipement> enCours  = new ArrayList<>();

        for (FragmentEquipement f : catalogue) {
            int qte = ctx.inventaire.getQuantiteMateriau(f.getNomFragment());
            if (qte >= f.getQuantiteRequise()) prets.add(f);
            else if (qte > 0) enCours.add(f);
        }

        if (!prets.isEmpty()) {
            System.out.println("[ Prêts à synthetiser ]");
            for (int i = 0; i < prets.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1,
                        gestionnaireFragments.afficherProgression(prets.get(i), ctx.inventaire));
            }
        }

        if (!enCours.isEmpty()) {
            System.out.println("[ En cours de collecte ]");
            for (FragmentEquipement f : enCours) {
                System.out.println("      " +
                        gestionnaireFragments.afficherProgression(f, ctx.inventaire));
            }
        }

        if (prets.isEmpty() && enCours.isEmpty()) {
            System.out.println("Vous n'avez encore aucun fragment.");
            System.out.println("Jouez le Chapitre 3 Elite pour en obtenir !");
            return;
        }

        if (prets.isEmpty()) {
            System.out.println("\nAucun equipement n'est encore synthetisable.");
            return;
        }

        System.out.println("\nChoisissez un equipement a synthetiser (0 pour annuler) :");
        System.out.print("Votre choix : ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choix == 0) return;
        if (choix < 1 || choix > prets.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        FragmentEquipement fragment = prets.get(choix - 1);
        Equipement nouvel = gestionnaireFragments.synthetiser(fragment, ctx.inventaire);

        if (nouvel == null) {
            // Ne devrait pas arriver (déjà filtré), sécurité
            System.out.println("Fragments insuffisants.");
            return;
        }

        ctx.inventaire.ajouterEquipement(nouvel);
        System.out.println("\n>> Synthèse réussie !");
        System.out.println("   " + nouvel + " ajouté à l'inventaire !");
        System.out.println("   " + fragment.getQuantiteRequise()
                + " fragments consommés.");

        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Cartes d'or ───────────────────────────────────────────────────────
    private void menuCartesOr(GameContext ctx, Scanner scanner) {
        List<Inventaire.StackCarteOr> stacks = new ArrayList<>();
        for (Inventaire.StackCarteOr s : ctx.inventaire.getCartesOr()) {
            if (!s.estVide()) stacks.add(s);
        }

        if (stacks.isEmpty()) {
            System.out.println("\nVous n'avez aucune Carte d'Or dans votre inventaire.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("         CARTES D'OR");
        System.out.println("========================================");
        System.out.printf("Or actuel : %,.0f or%n", ctx.joueur.getOr());
        System.out.println();

        for (int i = 0; i < stacks.size(); i++) {
            Inventaire.StackCarteOr s = stacks.get(i);
            System.out.printf("  [%d] %s%n", i + 1, s);
        }
        System.out.println("  [0] Retour");
        System.out.print("Choisissez une carte a utiliser : ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choix == 0) return;
        if (choix < 1 || choix > stacks.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        Inventaire.StackCarteOr stackChoisi = stacks.get(choix - 1);
        CarteOr carte    = stackChoisi.getCarte();
        int     dispo    = stackChoisi.getQuantite();

        System.out.printf("%nVous avez %d x %s (%,d or chacune).%n",
                dispo, carte.nom, carte.valeurOr);
        System.out.printf("Combien voulez-vous en utiliser ? (1-%d, 0 pour annuler) : ", dispo);

        int quantite;
        try {
            quantite = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (quantite == 0) return;
        if (quantite < 1 || quantite > dispo) {
            System.out.printf("Quantite invalide. Vous en avez %d.%n", dispo);
            return;
        }

        long orGagne = (long) carte.valeurOr * quantite;
        ctx.inventaire.retirerCartesOr(carte, quantite);
        // ajouterOr prend un int ; on applique par tranches si nécessaire
        long restant = orGagne;
        while (restant > 0) {
            int tranche = (int) Math.min(restant, Integer.MAX_VALUE);
            ctx.joueur.ajouterOr(tranche);
            restant -= tranche;
        }
        ctx.gestionnaireQuetes.notifierOrGagne((int) Math.min(orGagne, Integer.MAX_VALUE));

        System.out.printf("%n>> Vous avez utilise %d x %s !%n", quantite, carte.nom);
        System.out.printf("   + %,d or gagne !%n", orGagne);
        System.out.printf("   Or total : %,.0f or%n", ctx.joueur.getOr());

        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Boites de pierre (recompenses de l'Examen de Rang S) ───────────────
    private void menuOuvrirBoitesPierre(GameContext ctx, Scanner scanner) {
        List<Integer> niveauxDisponibles = new ArrayList<>();
        for (int n = 1; n <= MenuExamenS.NIVEAU_BOITE_MAX; n++) {
            if (ctx.inventaire.getQuantiteMateriau(MenuExamenS.nomBoite(n)) > 0) niveauxDisponibles.add(n);
        }
        if (niveauxDisponibles.isEmpty()) {
            System.out.println("Aucune boite de pierre en stock.");
            return;
        }

        System.out.println("\nBoites disponibles :");
        for (int i = 0; i < niveauxDisponibles.size(); i++) {
            int n = niveauxDisponibles.get(i);
            System.out.println("  " + (i + 1) + ". " + MenuExamenS.nomBoite(n)
                    + " x" + ctx.inventaire.getQuantiteMateriau(MenuExamenS.nomBoite(n)));
        }
        System.out.println("  0. Annuler");
        System.out.print("Laquelle ouvrir ? ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choix == 0) return;
        if (choix < 1 || choix > niveauxDisponibles.size()) {
            System.out.println("Choix invalide.");
            return;
        }
        int niveauBoite = niveauxDisponibles.get(choix - 1);
        int stock = ctx.inventaire.getQuantiteMateriau(MenuExamenS.nomBoite(niveauBoite));

        System.out.print("Combien en ouvrir ? (1-" + stock + ", 0 pour annuler) : ");

        int quantite;
        try {
            quantite = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (quantite == 0) return;
        if (quantite < 1 || quantite > stock) {
            System.out.println("Quantite invalide. Vous en avez " + stock + ".");
            return;
        }

        for (int i = 0; i < quantite; i++) {
            System.out.println(MenuExamenS.ouvrirBoite(ctx.inventaire, niveauBoite));
        }
        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Potions d'energie ────────────────────────────────────────────────
    private void menuUtiliserPotionEnergie(GameContext ctx, Scanner scanner) {
        List<PotionEnergie> dispo = new ArrayList<>();
        for (PotionEnergie p : PotionEnergie.values()) {
            if (ctx.inventaire.getQuantiteMateriau(p.nom) > 0) dispo.add(p);
        }
        if (dispo.isEmpty()) {
            System.out.println("Aucune potion d'energie en stock.");
            return;
        }

        System.out.println("\nPotions disponibles :");
        for (int i = 0; i < dispo.size(); i++) {
            PotionEnergie p = dispo.get(i);
            System.out.println("  " + (i + 1) + ". " + p + " x" + ctx.inventaire.getQuantiteMateriau(p.nom));
        }
        System.out.println("  0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > dispo.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            System.out.println(ctx.gestionnaireEnergie.utiliserPotion(ctx.inventaire, dispo.get(choix - 1)));
            ctx.sauvegarde.sauvegarder(ctx);
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    // ── Recyclage d'equipement (contre des Pieces d'equipement) ────────────
    private void menuRecyclerEquipement(GameContext ctx, Scanner scanner) {
        List<Equipement> equipements = ctx.inventaire.getEquipements();
        if (equipements.isEmpty()) {
            System.out.println("L'inventaire ne contient aucun equipement.");
            return;
        }

        System.out.println("\n--- Recycler un equipement ---");
        for (int i = 0; i < equipements.size(); i++) {
            Equipement e = equipements.get(i);
            System.out.println("  " + (i + 1) + ". " + e
                    + "  -> " + EquipementFactory.valeurRecyclage(e.getRarete()) + " Pieces d'equipement");
        }
        System.out.println("  0. Annuler");
        System.out.print("Votre choix : ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choix == 0) return;
        if (choix < 1 || choix > equipements.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        Equipement choisi = equipements.get(choix - 1);
        int valeur = EquipementFactory.valeurRecyclage(choisi.getRarete());
        ctx.inventaire.retirerEquipement(choisi);
        ctx.inventaire.ajouterMateriau(EquipementFactory.MATERIAU_PIECE_EQUIPEMENT, valeur);
        ctx.sauvegarde.sauvegarder(ctx);
        System.out.println(choisi.getNomAffiche() + " recycle pour " + valeur + " Pieces d'equipement.");
    }

    // ── Boutique d'equipement (fragments contre Pieces d'equipement) ───────
    private void menuBoutiqueEquipement(GameContext ctx, Scanner scanner) {
        List<FragmentEquipement> catalogue = gestionnaireFragments.getCatalogue();
        int solde = ctx.inventaire.getQuantiteMateriau(EquipementFactory.MATERIAU_PIECE_EQUIPEMENT);

        System.out.println("\n--- Boutique d'equipement — " + solde + " Piece(s) d'equipement ---");
        for (int i = 0; i < catalogue.size(); i++) {
            FragmentEquipement f = catalogue.get(i);
            int prix = EquipementFactory.prixFragmentBoutiqueEquipement(f.getRarete());
            int possede = ctx.inventaire.getQuantiteMateriau(f.getNomFragment());
            System.out.println("  " + (i + 1) + ". " + f.getNomEquipement() + " [" + f.getRarete() + "] — "
                    + prix + " pieces/fragment  (possede : " + possede + "/" + f.getQuantiteRequise() + ")");
        }
        System.out.println("  0. Annuler");
        System.out.print("Acheter 1 fragment de quoi ? ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choix == 0) return;
        if (choix < 1 || choix > catalogue.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        FragmentEquipement fragment = catalogue.get(choix - 1);
        int prix = EquipementFactory.prixFragmentBoutiqueEquipement(fragment.getRarete());
        if (solde < prix) {
            System.out.println("Pieces insuffisantes : " + solde + " / " + prix + ".");
            return;
        }
        ctx.inventaire.retirerMateriau(EquipementFactory.MATERIAU_PIECE_EQUIPEMENT, prix);
        ctx.inventaire.ajouterMateriau(fragment.getNomFragment(), 1);
        ctx.sauvegarde.sauvegarder(ctx);
        System.out.println("1x " + fragment.getNomFragment() + " achete !");
    }

    private boolean estCompatible(PersonnageBase cible, Equipement e) {
        return EquipementFactory.estCompatibleArme(cible.getType(), e);
    }

    private PersonnageBase choisirPersonnage(GameContext ctx, Scanner scanner) {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        System.out.println("\nChoisissez un personnage :");
        for (int i = 0; i < tous.size(); i++) {
            PersonnageBase p = tous.get(i);
            System.out.println((i + 1) + ". " + p.getNom()
                    + " [" + p.getRole() + "] Niv." + p.getNiveau()
                    + " — Type : " + p.getType());
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