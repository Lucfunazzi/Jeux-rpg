package lancement;

import lancement.Menus.MenuAbilite;
import lancement.Menus.MenuAmeliorations;
import lancement.Menus.MenuArene;
import lancement.Menus.MenuDonjon;
import lancement.Menus.MenuFormation;
import lancement.Menus.MenuHistoire;
import lancement.Menus.MenuInventaire;
import lancement.Menus.MenuPersonnage;
import lancement.Menus.MenuQuetes;
import lancement.Menus.MenuRang;
import lancement.Menus.MenuRecrutementRare;
import lancement.Menus.MenuCompagnons;
import lancement.Menus.Menu_Pet;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.Gestionnaire_pet;
import Joueur.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);

        // ── Construction du GameContext ────────────────────────────────────
        GameContext ctx = GameContext.creerContexteBase();

        // ── Menus ─────────────────────────────────────────────────────────
        MenuInventaire      menuInventaire      = new MenuInventaire();
        MenuPersonnage      menuPersonnage      = new MenuPersonnage();
        MenuRecrutementRare menuRecrutementRare = new MenuRecrutementRare();
        MenuAbilite         menuAbilite         = new MenuAbilite();
        MenuAmeliorations   menuAmeliorations   = new MenuAmeliorations();
        MenuFormation       menuFormation       = new MenuFormation();
        MenuRang            menuRang            = new MenuRang();
        MenuHistoire        menuHistoire        = new MenuHistoire(ctx.chapitre1, ctx.chapitre1Elite, ctx.chapitre2, ctx.chapitre2Elite, ctx.chapitre3, ctx.chapitre3Elite);
        MenuQuetes          menuQuetes          = new MenuQuetes();
        MenuDonjon          menuDonjon          = new MenuDonjon();
        MenuCompagnons      menuCompagnons      = new MenuCompagnons();
        Menu_Pet menuCreaturesSacrees = new Menu_Pet();

        // ── Pseudo & chargement ───────────────────────────────────────────
        System.out.println("========================================");
        System.out.println("          BIENVENUE DANS LE JEU !");
        System.out.println("========================================");
        System.out.print("Entrez votre pseudo : ");
        String pseudo = scanner.nextLine().trim();
        if (pseudo.isEmpty()) pseudo = "Aventurier";

        if (ctx.sauvegarde.sauvegardeExiste(pseudo)) {
            System.out.println("\nUne sauvegarde existe pour " + pseudo + " !");
            System.out.println("1. Charger la partie");
            System.out.println("2. Ecraser et recommencer une Nouvelle partie");
            System.out.print("Votre choix : ");

            if (scanner.nextLine().trim().equals("1")) {
                SauvegardeData data = ctx.sauvegarde.charger(pseudo);
                if (data != null) {
                    ctx.restaurerDepuis(data);
                    System.out.println("\nPartie chargee ! Bon retour, " + ctx.joueur.getNom() + " !");
                } else {
                    System.out.println("Erreur de chargement. Creation d'une nouvelle partie.");
                    ctx.joueur               = creerNouveauJoueur(scanner, pseudo, ctx);
                    ctx.formation            = new Formation(ctx.joueur, ctx.gestionnaireCompagnons);
                    ctx.personnagesRecruites = new ArrayList<>();
                }
            } else {
                ctx.joueur               = creerNouveauJoueur(scanner, pseudo, ctx);
                ctx.formation            = new Formation(ctx.joueur, ctx.gestionnaireCompagnons);
                ctx.personnagesRecruites = new ArrayList<>();
            }
        } else {
            System.out.println("\nBienvenue pour votre premiere aventure, " + pseudo + " !");
            ctx.joueur               = creerNouveauJoueur(scanner, pseudo, ctx);
            ctx.formation            = new Formation(ctx.joueur, ctx.gestionnaireCompagnons);
            ctx.personnagesRecruites = new ArrayList<>();
        }

        // ── Menu principal ────────────────────────────────────────────────
        boolean quitter                 = false;
        boolean modifieDepuisSauvegarde = false;

        while (!quitter) {
            boolean areneDisponible = ctx.joueur.getNiveau() >= 20;

            System.out.println("\n========================================");
            System.out.println("          MENU PRINCIPAL");
            System.out.println("========================================");
            System.out.println("Joueur : " + ctx.joueur.getNom()
                    + "  |  Niv." + ctx.joueur.getNiveau()
                    + "  |  Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  Rang : " + ctx.rangJoueur.getRangNom());
            System.out.println();
            // Conditions de déblocage des menus
            boolean chapitre2Fini      = ctx.chapitre2.getStagesReussis()[10];
            boolean chapitre1EliteFini = ctx.chapitre1Elite.getStagesReussis()[10];

            System.out.println("1.  Histoire");
            System.out.println("2.  Formation");
            if (ctx.joueur.getNiveau() >= 6)                                                   System.out.println("3.  Recrutement");
            System.out.println("4.  Inventaire");
            System.out.println("5.  Personnages");
            System.out.println("6.  Quetes");
            if (chapitre2Fini)                                                                 System.out.println("7.  Recrutement Rare");
            if (ctx.joueur.getNiveau() >= 3)                                                   System.out.println("8.  Abilites");
            if (chapitre1EliteFini)                                                            System.out.println("9.  Rang & Titres");
            if (ctx.joueur.getNiveau() >= 10)                                                  System.out.println("10. Ameliorations");
            if (ctx.joueur.getNiveau() >= 10)                                                  System.out.println("11. Donjon de ressources");
            if (areneDisponible)                                                               System.out.println("13. Arene  ⚔");
            if (ctx.joueur.getNiveau() >= GestionnaireCompagnons.NIVEAU_DEBLOCAGE)            System.out.println("14. Compagnons");
            if (ctx.joueur.getNiveau() >= Gestionnaire_pet.NIVEAU_DEBLOCAGE)      System.out.println("15. Créatures Sacrées");
            if (ctx.joueur.getNiveau() >= 6)                                                   System.out.println("16. Etoiles & Fragments");
            System.out.println("17. Tirages");
            System.out.println("12. Sauvegarder");
            System.out.println("0.  Quitter");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1"  -> { menuHistoire.afficher(ctx, scanner);                        modifieDepuisSauvegarde = true; }
                case "2"  -> { menuFormation.afficher(ctx, scanner);                       modifieDepuisSauvegarde = true; }
                case "3"  -> { ctx.menuRecrutement.afficher(ctx, scanner);                 modifieDepuisSauvegarde = true; }
                case "4"  -> { menuInventaire.afficher(ctx, scanner);                      modifieDepuisSauvegarde = true; }
                case "5"  -> { menuPersonnage.afficher(ctx, scanner, ctx.formation);       modifieDepuisSauvegarde = true; }
                case "6"  -> { menuQuetes.afficher(ctx, scanner);                          modifieDepuisSauvegarde = true; }
                case "7"  -> {
                    if (ctx.chapitre2.getStagesReussis()[10]) { menuRecrutementRare.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "8"  -> {
                    if (ctx.joueur.getNiveau() >= 3) { menuAbilite.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "9"  -> {
                    if (ctx.chapitre1Elite.getStagesReussis()[10]) { menuRang.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "10" -> {
                    if (ctx.joueur.getNiveau() >= 10) { menuAmeliorations.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "11" -> {
                    if (ctx.joueur.getNiveau() >= 10) { menuDonjon.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "12" -> { ctx.sauvegarde.sauvegarder(ctx);                            modifieDepuisSauvegarde = false; }
                case "13" -> {
                    if (areneDisponible) { new MenuArene(ctx, scanner).afficher(); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "14" -> {
                    if (ctx.joueur.getNiveau() >= GestionnaireCompagnons.NIVEAU_DEBLOCAGE) { menuCompagnons.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "15" -> {
                    if (ctx.joueur.getNiveau() >= Gestionnaire_pet.NIVEAU_DEBLOCAGE) { menuCreaturesSacrees.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "16" -> {
                    if (ctx.joueur.getNiveau() >= 6) { ctx.menuEtoilesPerso.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
                case "17" -> { ctx.menuTirage.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                case "0"  -> {
                    if (modifieDepuisSauvegarde) {
                        System.out.println("Vous avez des modifications non sauvegardees.");
                        System.out.println("Quitter sans sauvegarder ? (1 : Oui / 2 : Non)");
                        if (scanner.nextLine().trim().equals("1")) quitter = true;
                    } else {
                        quitter = true;
                    }
                }
                default -> System.out.println("Choix invalide.");
            }
        }

        System.out.println("\nA bientot, " + ctx.joueur.getNom() + " !");
        scanner.close();
    }

    private static Competences competencesPour(String classe) {
        return switch (classe) {
            case "Mage"                -> new Elementaliste();
            case "Chasseur de Dragon"  -> new ChasseurDeDragon();
            case "Chevalier"           -> new Chevalier();
            case "Constellationniste"  -> new Invocateur();
            default         -> null;
        };
    }

    private static Personnage_principale creerNouveauJoueur(Scanner scanner, String pseudo, GameContext ctx) {
        Personnage_principale joueur = new Personnage_principale(pseudo, 1);
        joueur.setGameContext(ctx);

        Competences competences = null;
        String classeChoisie = null;

        while (competences == null) {
            System.out.println("\nChoisissez votre classe :\n");
            for (int i = 0; i < joueur.getClasses().length; i++)
                System.out.println((i + 1) + ". " + joueur.getClasses()[i]);
            System.out.println("\nEntrez le numero d'une classe pour voir ses competences avant de valider.");

            int choixClasseNum = 0;
            while (choixClasseNum < 1 || choixClasseNum > joueur.getClasses().length) {
                System.out.print("Votre choix : ");
                try { choixClasseNum = Integer.parseInt(scanner.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("Entree invalide."); }
            }
            String classeApercu = joueur.getClasses()[choixClasseNum - 1];
            Competences apercu = competencesPour(classeApercu);

            String[] nomCompetences = apercu.getNomsCompetences();
            System.out.println("\n[ " + classeApercu + " ]");
            System.out.println("  Speciale : " + nomCompetences[0]);
            apercu.descriptionAttaqueSpeciale();
            System.out.println("  Ultime   : " + nomCompetences[1]);
            apercu.descriptionUltime();

            System.out.println("\nValider ce choix de classe ? (1 : Oui / 2 : Non, revoir les classes) :");
            if (scanner.nextLine().trim().equals("1")) {
                classeChoisie = classeApercu;
                competences   = apercu;
            }
        }

        joueur.setChoixClasses(classeChoisie);
        System.out.println("\nD'autres compétences seront débloquées via l'Arbre de Compétences !");
        joueur.setCompetencesChoisie(competences);
        return joueur;
    }
}