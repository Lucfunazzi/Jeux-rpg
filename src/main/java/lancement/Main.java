package lancement;

import lancement.ChapitreElite.Chapitre1Elite;
import lancement.ChapitreElite.Chapitre2Elite;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.Chapitres.Chapitre3;
import lancement.Gestionnaires.GestionnaireTitres;
import lancement.Gestionnaires.GestionnaireSauvegarde;
import lancement.Gestionnaires.GestionnaireEnergie;
import lancement.Gestionnaires.GestionnaireQuetes;
import lancement.Gestionnaires.GestionnaireDonjon;
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
import lancement.Menus.MenuRecrutement;
import lancement.Menus.MenuRecrutementRare;
import lancement.Menus.MenuCompagnons;
import lancement.Menus.MenuCreaturesSacrees;
import lancement.Gestionnaires.GestionnaireEtoiles;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.GestionnaireCreaturesSacrees;
import lancement.Gestionnaires.GestionnaireClefsCelestes;
import Joueur.*;
import Personnage.*;
import java.util.ArrayList;
import java.util.Scanner;
import Equipement.Inventaire;

public class Main {

    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);

        // ── Construction du GameContext ────────────────────────────────────
        GameContext ctx = new GameContext();
        ctx.sauvegarde           = new GestionnaireSauvegarde();
        ctx.inventaire           = new Inventaire();
        ctx.menuRecrutement      = new MenuRecrutement();
        ctx.chapitre1            = new Chapitre1();
        ctx.chapitre2            = new Chapitre2();
        ctx.chapitre3            = new Chapitre3();
        ctx.gestionnaireQuetes   = new GestionnaireQuetes();
        ctx.gestionnaireEnergie  = new GestionnaireEnergie();
        ctx.rangJoueur           = new RangJoueur();
        ctx.gestionnaireTitres   = new GestionnaireTitres();
        ctx.gestionnaireDonjon   = new GestionnaireDonjon();
        ctx.gestionnaireEtoiles    = new GestionnaireEtoiles();
        ctx.gestionnaireCompagnons       = new GestionnaireCompagnons();
        ctx.gestionnaireCreaturesSacrees = new GestionnaireCreaturesSacrees();
        ctx.gestionnaireClefsCelestes    = new GestionnaireClefsCelestes();
        ctx.chapitre1Elite         = new Chapitre1Elite(ctx.chapitre1);
        ctx.chapitre2Elite         = new Chapitre2Elite(ctx.chapitre1, ctx.chapitre2, ctx.chapitre1Elite);
        ctx.chapitre3Elite         = new Chapitre3Elite(ctx.chapitre3, ctx.chapitre2Elite);

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
        MenuCreaturesSacrees menuCreaturesSacrees = new MenuCreaturesSacrees();

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
                    ctx.joueur               = ctx.sauvegarde.restaurerJoueur(data, ctx);
                    ctx.personnagesRecruites = ctx.sauvegarde.restaurerPersonnagesRecruites(data);
                    ctx.sauvegarde.restaurerCompagnons(ctx.gestionnaireCompagnons, data);
                    ctx.formation            = new Formation(ctx.joueur, ctx.gestionnaireCompagnons);
                    ctx.sauvegarde.restaurerFormation(ctx.formation, data, ctx.personnagesRecruites);
                    ctx.sauvegarde.restaurerChapitre1(ctx.chapitre1, data);
                    ctx.sauvegarde.restaurerChapitre1Elite(ctx.chapitre1Elite, data);
                    ctx.sauvegarde.restaurerChapitre2(ctx.chapitre2, data);
                    ctx.sauvegarde.restaurerChapitre3(ctx.chapitre3, data);
                    ctx.sauvegarde.restaurerChapitre2Elite2(ctx.chapitre2Elite, data);
                    ctx.sauvegarde.restaurerChapitre3Elite(ctx.chapitre3Elite, data);
                    ctx.sauvegarde.restaurerInventaire(ctx.inventaire, data);
                    ctx.sauvegarde.restaurerQuetes(ctx.gestionnaireQuetes, data);
                    ctx.sauvegarde.restaurerEnergie(ctx.gestionnaireEnergie, data);
                    ctx.sauvegarde.restaurerRangEtTitres(ctx.rangJoueur, ctx.gestionnaireTitres, data);
                    ctx.sauvegarde.restaurerDonjon(ctx.gestionnaireDonjon, data);
                    ctx.menuRecrutement.setParcheminC(data.parcheminC);
                    ctx.menuRecrutement.setParcheminB(data.parcheminB);
                    ctx.sauvegarde.restaurerEtoiles(ctx.gestionnaireEtoiles, data);
                    ctx.coupons = data.coupons;
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
            if (ctx.joueur.getNiveau() >= GestionnaireCreaturesSacrees.NIVEAU_DEBLOCAGE)      System.out.println("15. Créatures Sacrées");
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
                    if (ctx.joueur.getNiveau() >= GestionnaireCreaturesSacrees.NIVEAU_DEBLOCAGE) { menuCreaturesSacrees.afficher(ctx, scanner); modifieDepuisSauvegarde = true; }
                    else System.out.println("Choix invalide.");
                }
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

    private static Personnage_principale creerNouveauJoueur(Scanner scanner, String pseudo, GameContext ctx) {
        Personnage_principale joueur = new Personnage_principale(pseudo, 1);
        joueur.setGameContext(ctx);
        Competences competences = null;

        System.out.println("\nChoisissez votre classe :\n");
        for (int i = 0; i < joueur.getClasses().length; i++)
            System.out.println((i + 1) + ". " + joueur.getClasses()[i]);

        int choixClasseNum = 0;
        while (choixClasseNum < 1 || choixClasseNum > joueur.getClasses().length) {
            System.out.print("Votre choix : ");
            try { choixClasseNum = Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Entree invalide."); }
        }
        joueur.setChoixClasses(joueur.getClasses()[choixClasseNum - 1]);

        competences = switch (joueur.getChoixClasses()) {
            case "Mage"     -> new Mage();
            case "Ninja"    -> new Ninja();
            case "Guerrier" -> new Guerrier();
            default         -> null;
        };

        String[] nomCompetences = competences.getNomsCompetences();
        System.out.println("\nVoici les 3 competences disponibles, vous devez en choisir une :");
        for (int i = 0; i < nomCompetences.length; i++)
            System.out.println((i + 1) + ". " + nomCompetences[i]);

        System.out.println("\nVoulez-vous la description des competences ? (1 : Oui / 2 : Non) :");
        int choixDesc = scanner.nextInt(); scanner.nextLine();
        if (choixDesc == 1) {
            boolean continuerDesc = true;
            while (continuerDesc) {
                System.out.println("\nChoisissez la description a afficher (1, 2, 3 ou 4 pour Quitter) :");
                int c = scanner.nextInt(); scanner.nextLine();
                joueur.setChoixDescription_comp(c);
                switch (joueur.getchoixDescription_comp()) {
                    case 1 -> competences.descriptionCompetence1();
                    case 2 -> competences.descriptionCompetence2();
                    case 3 -> competences.descriptionCompetence3();
                    case 4 -> continuerDesc = false;
                    default -> System.out.println("Choix invalide.");
                }
            }
        }

        int choixComp = 0;
        while (choixComp < 1 || choixComp > 3) {
            System.out.println("\nChoisissez votre competence active (entre 1 et 3) :");
            choixComp = scanner.nextInt(); scanner.nextLine();
            if (choixComp < 1 || choixComp > 3)
                System.out.println("Numero invalide ! Veuillez choisir 1, 2 ou 3.");
        }
        System.out.println("\n-> Vous avez choisi la competence : " + nomCompetences[choixComp - 1]);
        joueur.setChoixComp(choixComp);
        joueur.setCompetencesChoisie(competences);
        return joueur;
    }
}