package lancement.Menus;

import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.Inventaire;
import Equipement.ParcheminXP;
import lancement.Formation;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireLiens;
import lancement.Gestionnaires.GestionnaireQuetes;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuPersonnage {

    public void afficher(GameContext ctx, Scanner scanner, Formation formation) {
        boolean retour = false;

        while (!retour) {
            ArrayList<PersonnageBase> tousLesPersonnages = new ArrayList<>();
            tousLesPersonnages.add(ctx.joueur);
            tousLesPersonnages.addAll(ctx.personnagesRecruites);

            List<GestionnaireLiens.Lien> liensActifs = formation.getLiensActifs();

            System.out.println("\n========================================");
            System.out.println("         MENU PERSONNAGES");
            System.out.println("========================================");

            if (!liensActifs.isEmpty()) {
                System.out.println("[ Liens actifs ]");
                for (GestionnaireLiens.Lien l : liensActifs)
                    System.out.println("  ✦ " + l.nom + " — " + l.description);
                System.out.println();
            }

            for (int i = 0; i < tousLesPersonnages.size(); i++) {
                PersonnageBase p = tousLesPersonnages.get(i);
                int piecesC = compterPiecesRangC(p);
                String set  = piecesC > 0 ? "  Set C : " + piecesC + "/6" : "";
                boolean dansFormation = formation.getEquipe().contains(p);
                String tag = dansFormation ? " [F]" : "";
                System.out.println((i + 1) + ". " + p.getNom()
                        + "  Niv." + p.getNiveau()
                        + "  [" + p.getRarete() + "]"
                        + "  " + (p.getType() != null ? p.getType() : "")
                        + "  " + p.getRole()
                        + tag + set);
            }
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                continue;
            }

            if (choix == 0) {
                retour = true;
            } else if (choix < 1 || choix > tousLesPersonnages.size()) {
                System.out.println("Choix invalide.");
            } else {
                PersonnageBase cible = tousLesPersonnages.get(choix - 1);
                // Le joueur principal ne peut pas utiliser de parchemins XP
                boolean estJoueurPrincipal = cible.estPersonnagePrincipal();
                afficherFiche(cible, estJoueurPrincipal, ctx, formation, scanner);
            }
        }
    }

    // ── Fiche complète d'un personnage ────────────────────────────────────
    private void afficherFiche(PersonnageBase perso,
                               boolean estJoueurPrincipal,
                               GameContext ctx,
                               Formation formation,
                               Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            int piecesC = compterPiecesRangC(perso);
            List<GestionnaireLiens.Lien> liensActifs = formation.getLiensActifs();

            System.out.println("\n========================================");
            System.out.println("  " + perso.getNom()
                    + "  Niv." + perso.getNiveau()
                    + "  [" + perso.getRarete() + "]"
                    + "  " + (perso.getType() != null ? perso.getType() : "")
                    + "  " + perso.getRole());
            System.out.println("========================================");

            System.out.println("\n[ Stats ]");
            double bonusPVSet = piecesC >= 3 ? 200 : 0;
            // Bonus arbre de competences (uniquement pour le personnage principal)
            double arbreATK = 0, arbreDEF = 0, arbrePV = 0, arbreVIT = 0;
            if (perso instanceof Joueur.Personnage_principale pp) {
                arbreATK = pp.getArbreCompetences().getBonusATK();
                arbreDEF = pp.getArbreCompetences().getBonusDEF();
                arbrePV  = pp.getArbreCompetences().getBonusPV();
                arbreVIT = pp.getArbreCompetences().getBonusVIT();
            }
            // Total % par stat = arbre + lien + set
            double totalPctATK = arbreATK + perso.getBonusLienATK() + (piecesC >= 6 ? 0.05 : 0);
            double totalPctDEF = arbreDEF + perso.getBonusLienDEF();
            double totalPctPV  = arbrePV  + perso.getBonusLienPV();
            double totalPctVIT = arbreVIT + perso.getBonusLienVIT() + (piecesC >= 4 ? 0.02 : 0);

            System.out.println("  PV  : " + String.format("%.0f", perso.getVie())
                    + " / " + String.format("%.0f", perso.getVieMax())
                    + "  (equip +" + String.format("%.0f", perso.getBonusEquipementPV())
                    + (bonusPVSet > 0 ? " +set" + String.format("%.0f", bonusPVSet) : "")
                    + "  +" + String.format("%.0f", totalPctPV * 100) + "%)");
            System.out.println("  ATK : " + String.format("%.0f", perso.getAttaque())
                    + "  (equip +" + String.format("%.0f", perso.getBonusEquipementATK())
                    + "  +" + String.format("%.0f", totalPctATK * 100) + "%)");
            System.out.println("  DEF : " + String.format("%.0f", perso.getDefense())
                    + "  (equip +" + String.format("%.0f", perso.getBonusEquipementDEF())
                    + "  +" + String.format("%.0f", totalPctDEF * 100) + "%)");
            System.out.println("  VIT : " + String.format("%.0f", perso.getVitesse())
                    + "  (equip +" + String.format("%.0f", perso.getBonusEquipementVIT())
                    + "  +" + String.format("%.0f", totalPctVIT * 100) + "%)");
            System.out.println("  Crit    : " + String.format("%.0f", perso.getTauxCritique() * 100) + "%"
                    + "  Degat crit : x" + String.format("%.2f", perso.getTauxDegatCritique()));
            System.out.println("  Esquive : " + String.format("%.0f", perso.getTauxEsquives() * 100) + "%"
                    + "  Blocage    : " + String.format("%.0f", perso.getTauxBlocage() * 100) + "%");
            System.out.println("  XP : " + perso.getExperience() + " / " + perso.getExperienceMax());

            // Liens actifs
            if (!liensActifs.isEmpty()) {
                System.out.println("\n[ Liens actifs ]");
                for (GestionnaireLiens.Lien l : liensActifs) {
                    boolean membreDuLien = false;
                    for (String m : l.membres)
                        if (m.equals(perso.getNom())) { membreDuLien = true; break; }
                    String tag = membreDuLien ? " ★" : "";
                    System.out.println("  ✦ " + l.nom + tag + " — " + l.description);
                }
            } else {
                System.out.println("\n[ Liens ] Aucun lien actif dans la formation.");
            }

            // Bonus de set
            System.out.println("\n[ Bonus de Set — Rang C (" + piecesC + "/6) ]");
            if (piecesC < 3) {
                System.out.println("  Aucun bonus actif.  Prochain : 3 pieces — +200 PV");
            } else {
                System.out.println("  [OK] 3 pieces : +200 PV");
                if (piecesC < 4)
                    System.out.println("  Prochain : 4 pieces — +2% VIT");
                else {
                    System.out.println("  [OK] 4 pieces : +2% VIT");
                    if (piecesC < 6)
                        System.out.println("  Prochain : 6 pieces — +5% ATK  (manque " + (6 - piecesC) + ")");
                    else
                        System.out.println("  [OK] 6 pieces : +5% ATK");
                }
            }

            // Slots d'équipement
            System.out.println("\n[ Equipements ]  (entrez le numero du slot pour equiper)");
            Equipement.Slot[] slots = Equipement.Slot.values();
            for (int i = 0; i < slots.length; i++) {
                Equipement equipe = perso.getEquipement(slots[i]);
                String equipeStr = equipe != null
                        ? equipe.getNomRarete() + " " + equipe.getNomAffiche()
                          + " (" + equipe.getDescriptionBonus() + ")"
                        : "[vide]";
                System.out.println("  " + (i + 1) + ". " + nomSlot(slots[i]) + " : " + equipeStr);
            }

            // Inventaire compatible
            System.out.println("\n[ Inventaire compatible ]");
            ArrayList<Inventaire.StackEquipement> stacks = ctx.inventaire.getStacks();
            boolean aucunCompat = true;
            for (int i = 0; i < stacks.size(); i++) {
                Inventaire.StackEquipement s = stacks.get(i);
                if (estCompatible(perso, s.getEquipement())) {
                    String ligne = "  " + (i + 1) + ". " + s.getEquipement();
                    if (s.getQuantite() > 1) ligne += " x" + s.getQuantite();
                    System.out.println(ligne);
                    aucunCompat = false;
                }
            }
            if (aucunCompat) System.out.println("  Aucun equipement compatible.");

            // Parchemins XP (recrutés uniquement)
            if (!estJoueurPrincipal) {
                System.out.println("\n[ Consommables — Parchemins XP ]");
                afficherStockParchemins(ctx.inventaire);
            }

            System.out.println("\nEntrez le numero du slot pour equiper/desequiper");
            if (!estJoueurPrincipal) {
                System.out.println("P. Utiliser un Parchemin XP");
            }
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                retour = true;
            } else if (!estJoueurPrincipal && input.equalsIgnoreCase("P")) {
                utiliserParcheminXP(perso, ctx, scanner);
            } else {
                int choixSlot;
                try {
                    choixSlot = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Entree invalide.");
                    continue;
                }
                if (choixSlot < 1 || choixSlot > slots.length) {
                    System.out.println("Choix invalide.");
                } else {
                    gererSlot(perso, slots[choixSlot - 1], ctx.inventaire, scanner);
                }
            }
        }
    }

    // ── Utilisation d'un parchemin XP ─────────────────────────────────────
    private void utiliserParcheminXP(PersonnageBase cible, GameContext ctx, Scanner scanner) {
        int niveauMax = ctx.joueur.getNiveau();

        // Vérifier si le personnage est déjà au niveau maximum autorisé
        if (cible.getNiveau() >= niveauMax) {
            System.out.println("  " + cible.getNom() + " est deja au niveau maximum autorise ("
                    + niveauMax + " = niveau du personnage principal).");
            return;
        }

        // Vérifier s'il y a des parchemins disponibles
        if (ctx.inventaire.getParchemins().isEmpty()) {
            System.out.println("  Aucun parchemin XP dans l'inventaire.");
            return;
        }

        System.out.println("\n[ Utiliser des Parchemins XP sur " + cible.getNom()
                + "  (Niv." + cible.getNiveau() + " / max " + niveauMax + ") ]");
        afficherStockParchemins(ctx.inventaire);

        System.out.println("\n1. Parchemin XP [C]  (+500 XP)");
        System.out.println("2. Parchemin XP [B]  (+1500 XP)");
        System.out.println("3. Parchemin XP [A]  (+5000 XP)");
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        ParcheminXP.Rarete rarete;
        switch (scanner.nextLine().trim()) {
            case "1" -> rarete = ParcheminXP.Rarete.C;
            case "2" -> rarete = ParcheminXP.Rarete.B;
            case "3" -> rarete = ParcheminXP.Rarete.A;
            case "0" -> { return; }
            default  -> { System.out.println("Choix invalide."); return; }
        }

        int stock = ctx.inventaire.getQuantiteParcheminXP(rarete);
        if (stock <= 0) {
            System.out.println("Aucun parchemin XP [" + rarete.name() + "] en stock.");
            return;
        }

        // Demander la quantité
        System.out.println("Stock disponible : " + stock
                + "  (MAX pour plafonner au niveau " + niveauMax + ")");
        System.out.print("Quantite a utiliser (1-" + stock + ", ou MAX) : ");
        String inputQte = scanner.nextLine().trim();

        int quantite;
        if (inputQte.equalsIgnoreCase("MAX") || inputQte.equalsIgnoreCase("max")) {
            quantite = stock;
        } else {
            try {
                quantite = Integer.parseInt(inputQte);
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                return;
            }
            if (quantite < 1 || quantite > stock) {
                System.out.println("Quantite invalide (doit etre entre 1 et " + stock + ").");
                return;
            }
        }

        System.out.println("\n>> " + appliquerParcheminsXP(cible, ctx, rarete, quantite));
    }

    /**
     * Applique jusqu'a {@code quantite} parchemins XP de la rarete donnee sur {@code cible}
     * (arret anticipe si le niveau max autorise est atteint). Retourne le message resultat.
     * Reutilisable par la console et l'interface graphique.
     */
    public String appliquerParcheminsXP(PersonnageBase cible, GameContext ctx,
                                          ParcheminXP.Rarete rarete, int quantite) {
        int niveauMax = ctx.joueur.getNiveau();
        ParcheminXP parchemin = new ParcheminXP(rarete);
        int xpTotaleAccordee = 0;
        int parcheminsUtilises = 0;

        for (int i = 0; i < quantite; i++) {
            if (cible.getNiveau() >= niveauMax) break;

            int xpBrute = parchemin.getXP();
            int xpPourNiveauMax = xpJusquauNiveauMax(cible, niveauMax);
            int xpAccordee = Math.min(xpBrute, xpPourNiveauMax);

            ctx.inventaire.retirerParcheminXP(rarete);
            cible.gagnerExperience(xpAccordee);
            xpTotaleAccordee += xpAccordee;
            parcheminsUtilises++;
        }

        ctx.sauvegarde.sauvegarder(ctx);

        StringBuilder sb = new StringBuilder();
        sb.append(parcheminsUtilises).append("x ").append(parchemin.getNom())
          .append(" utilise(s) sur ").append(cible.getNom()).append(" !\n");
        sb.append("+").append(xpTotaleAccordee).append(" XP au total.");
        if (parcheminsUtilises < quantite) {
            sb.append("\n(Arret anticipe - niveau max ").append(niveauMax).append(" atteint)");
        }
        return sb.toString();
    }

    /**
     * Calcule l'XP totale qu'il manque à {@code cible} pour atteindre {@code niveauMax}.
     * Permet de plafonner précisément sans dépasser le niveau du joueur principal.
     */
    private int xpJusquauNiveauMax(PersonnageBase cible, int niveauMax) {
        // Simulation : on additionne les seuils de niveau jusqu'à niveauMax
        int xpRestanteNiveauActuel = cible.getExperienceMax() - cible.getExperience();
        if (cible.getNiveau() >= niveauMax) return 0;

        int total = xpRestanteNiveauActuel;
        int seuil = cible.getExperienceMax(); // seuil du niveau en cours

        for (int n = cible.getNiveau() + 1; n < niveauMax; n++) {
            seuil = (int)(seuil * 1.20);
            total += seuil;
        }
        return total;
    }

    // ── Affichage stock parchemins ─────────────────────────────────────────
    private void afficherStockParchemins(Inventaire inventaire) {
        int stockC = inventaire.getQuantiteParcheminXP(ParcheminXP.Rarete.C);
        int stockB = inventaire.getQuantiteParcheminXP(ParcheminXP.Rarete.B);
        int stockA = inventaire.getQuantiteParcheminXP(ParcheminXP.Rarete.A);
        System.out.println("  Parchemin XP [C] (+500 XP)  : " + stockC);
        System.out.println("  Parchemin XP [B] (+1500 XP) : " + stockB);
        System.out.println("  Parchemin XP [A] (+5000 XP) : " + stockA);
    }

    // ── Gérer l'équipement d'un slot ──────────────────────────────────────
    private void gererSlot(PersonnageBase perso, Equipement.Slot slot,
                            Inventaire inventaire, Scanner scanner) {
        Equipement actuel = perso.getEquipement(slot);

        System.out.println("\n[ " + nomSlot(slot) + " ]");
        System.out.println("  Equipe : " + (actuel != null
                ? actuel.getNomRarete() + " " + actuel.getNomAffiche()
                  + " (" + actuel.getDescriptionBonus() + ")"
                : "[vide]"));

        ArrayList<Inventaire.StackEquipement> compatibles = new ArrayList<>();
        for (Inventaire.StackEquipement s : inventaire.getStacks()) {
            if (s.getEquipement().getSlot() == slot && estCompatible(perso, s.getEquipement()))
                compatibles.add(s);
        }

        if (compatibles.isEmpty() && actuel == null) {
            System.out.println("  Aucun equipement disponible pour ce slot.");
            return;
        }

        System.out.println("\nInventaire pour ce slot :");
        for (int i = 0; i < compatibles.size(); i++) {
            Inventaire.StackEquipement s = compatibles.get(i);
            String ligne = "  " + (i + 1) + ". " + s.getEquipement();
            if (s.getQuantite() > 1) ligne += " x" + s.getQuantite();
            System.out.println(ligne);
        }
        if (actuel != null)
            System.out.println("  " + (compatibles.size() + 1) + ". Desequiper " + actuel.getNom());
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

        if (actuel != null && choix == compatibles.size() + 1) {
            perso.desequiper(slot);
            inventaire.ajouterEquipement(actuel);
            System.out.println("  " + actuel.getNom() + " remis en inventaire.");
            return;
        }

        if (choix < 1 || choix > compatibles.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        Equipement nouvel = compatibles.get(choix - 1).getEquipement();
        if (actuel != null) {
            perso.desequiper(slot);
            inventaire.ajouterEquipement(actuel);
            System.out.println("  " + actuel.getNom() + " remis en inventaire.");
        }
        perso.equiper(nouvel);
        inventaire.retirerEquipement(nouvel);
        System.out.println("  " + nouvel.getNomAffiche() + " equipe sur " + perso.getNom() + " !");
    }

    // ── Utilitaires ───────────────────────────────────────────────────────
    private int compterPiecesRangC(PersonnageBase perso) {
        int count = 0;
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement e = perso.getEquipement(slot);
            if (e != null && e.getRarete() == Equipement.Rarete.C) count++;
        }
        return count;
    }

    private boolean estCompatible(PersonnageBase perso, Equipement e) {
        String type = perso.getType();
        if (type == null && perso instanceof Joueur.Personnage_principale pp) {
            type = pp.getChoixClasses();
        }
        if (type == null) return true;
        return EquipementFactory.estCompatibleArme(type, e);
    }

    private String nomSlot(Equipement.Slot slot) {
        return switch (slot) {
            case ARME        -> "Arme       ";
            case COUVRE_CHEF -> "Couvre-chef";
            case TORSE       -> "Torse      ";
            case MAINS       -> "Mains      ";
            case JAMBIERES   -> "Jambieres  ";
            case BOTTES      -> "Bottes     ";
        };
    }
}