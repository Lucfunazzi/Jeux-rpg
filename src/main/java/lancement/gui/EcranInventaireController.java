package lancement.gui;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.FragmentEquipement;
import Equipement.GestionnaireFragments;
import Equipement.Inventaire;
import Equipement.ParcheminXP;
import Equipement.Pierre;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;
import lancement.Menus.MenuEtoilesPerso;
import lancement.Menus.MenuExamenS;

public class EcranInventaireController {

    private final GestionnaireFragments gestionnaireFragments = new GestionnaireFragments();

    // ── Prix de vente ────────────────────────────────────────────────────
    private static int prixVenteEquipement(Equipement.Rarete r) {
        return switch (r) {
            case C -> 50;
            case B -> 150;
            case A -> 400;
            case S -> 1000;
        };
    }

    private static int prixVentePierre(int niveau) {
        return niveau * 20;
    }

    private GameContext ctx;

    @FXML private VBox contenuBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichir();
    }

    private void rafraichir() {
        Inventaire inv = ctx.inventaire;
        contenuBox.getChildren().clear();

        contenuBox.getChildren().add(titreSection("Équipements (" + inv.getStacks().size() + ")"));
        if (inv.getStacks().isEmpty()) {
            contenuBox.getChildren().add(texteVide("Aucun équipement."));
        } else {
            FlowPane grille = new FlowPane(10, 10);
            for (Inventaire.StackEquipement s : inv.getStacks()) {
                grille.getChildren().add(carteEquipement(s));
            }
            contenuBox.getChildren().add(grille);
        }

        contenuBox.getChildren().add(titreSection("Matériaux (" + inv.getMateriaux().size() + ")"));
        if (inv.getMateriaux().isEmpty()) {
            contenuBox.getChildren().add(texteVide("Aucun matériau."));
        } else {
            FlowPane grille = new FlowPane(10, 10);
            for (var m : inv.getMateriaux()) {
                if (m.getNom().startsWith(GestionnaireEtoilesPerso.PREFIXE_FRAGMENT)) {
                    grille.getChildren().add(carteFragmentPerso(m.getNom(), m.getQuantite()));
                } else if (m.getNom().startsWith(FragmentEquipement.PREFIXE_FRAGMENT)) {
                    grille.getChildren().add(carteFragmentEquipement(m.getNom()));
                } else if (m.getNom().equals(MenuExamenS.MATERIAU_BOITE_PIERRE_LV1)) {
                    grille.getChildren().add(carteBoitePierre(m.getQuantite()));
                } else {
                    grille.getChildren().add(carteSimple(m.getNom(), "x" + m.getQuantite()));
                }
            }
            contenuBox.getChildren().add(grille);
        }

        contenuBox.getChildren().add(titreSection("Consommables"));
        boolean aucunConso = inv.getParchemins().isEmpty() && inv.getCartesOr().isEmpty();
        if (aucunConso) {
            contenuBox.getChildren().add(texteVide("Aucun consommable."));
        } else {
            FlowPane grille = new FlowPane(10, 10);
            for (var s : inv.getParchemins()) {
                grille.getChildren().add(carteParchemin(s));
            }
            for (var s : inv.getCartesOr()) {
                grille.getChildren().add(carteCarteOr(s));
            }
            contenuBox.getChildren().add(grille);
        }

        contenuBox.getChildren().add(titreSection("Pierres (" + inv.getPierres().size() + ")"));
        if (inv.getPierres().isEmpty()) {
            contenuBox.getChildren().add(texteVide("Aucune pierre."));
        } else {
            FlowPane grille = new FlowPane(10, 10);
            for (var s : inv.getPierres()) {
                grille.getChildren().add(cartePierre(s));
            }
            contenuBox.getChildren().add(grille);
        }
    }

    private Label titreSection(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("section-titre");
        return l;
    }

    private Label texteVide(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("item-vide");
        return l;
    }

    // ── Cartes ────────────────────────────────────────────────────────────

    /** Carte detaillee pour un equipement : badge de rarete, nom, slot + description, quantite. Clic -> Equiper/Vendre. */
    private Node carteEquipement(Inventaire.StackEquipement s) {
        Equipement e = s.getEquipement();
        Label badge = GuiVisuels.creerBadgeRarete(e.getRarete().name());

        Label nom = new Label(e.getNomAffiche());
        nom.getStyleClass().add("item-nom");

        Label detail = new Label(e.getNomSlot() + " — " + e.getDescription());
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(240);

        VBox texte = new VBox(2, nom, detail);
        HBox ligne = new HBox(10, badge, texte);
        ligne.setAlignment(Pos.CENTER_LEFT);

        if (s.getQuantite() > 1) {
            Label qte = new Label("x" + s.getQuantite());
            qte.getStyleClass().add("item-qte");
            ligne.getChildren().add(qte);
        }

        ligne.getStyleClass().add("carte-item");
        ligne.setPrefWidth(320);
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(ev -> actionsEquipement(e));
        return ligne;
    }

    /** Carte pour un parchemin d'XP : badge de rarete (C/B/A) + nom + quantite. Purement informatif (s'utilise depuis la fiche personnage). */
    private Node carteParchemin(Inventaire.StackParchemin s) {
        Label badge = GuiVisuels.creerBadgeRarete(s.getRarete().name());
        Label nom = new Label(new ParcheminXP(s.getRarete()).toString());
        nom.getStyleClass().add("item-nom");

        HBox ligne = new HBox(10, badge, nom);
        ligne.setAlignment(Pos.CENTER_LEFT);

        Label qte = new Label("x" + s.getQuantite());
        qte.getStyleClass().add("item-qte");
        ligne.getChildren().add(qte);

        ligne.getStyleClass().add("carte-item");
        return ligne;
    }

    /** Carte pour une carte d'or. Clic -> Utiliser directement. */
    private Node carteCarteOr(Inventaire.StackCarteOr s) {
        Node ligne = carteSimple(s.getCarte().nom,
                "x" + s.getQuantite() + "  (" + String.format("%,d", s.getCarte().valeurOr) + " or/carte)");
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(ev -> utiliserCarteOr(s));
        return ligne;
    }

    /** Carte pour un fragment de personnage recrutable. Clic -> Recruter (ou affiche la progression). */
    private Node carteFragmentPerso(String nomMateriau, int quantite) {
        String nomPerso = nomMateriau.substring(GestionnaireEtoilesPerso.PREFIXE_FRAGMENT.length());
        String rarete = rareteFragmentPerso(nomPerso);
        String suffixe = rarete != null
                ? "x" + quantite + " / " + GestionnaireEtoilesPerso.coutFragmentsRecrutement(rarete)
                : "x" + quantite;
        Node ligne = carteSimple(nomPerso + (rarete != null ? " [" + rarete + "]" : ""), suffixe);
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(ev -> actionsFragmentPerso(nomPerso, rarete));
        return ligne;
    }

    /** Carte pour un fragment d'equipement rang A. Clic -> Synthetiser (ou affiche la progression). */
    private Node carteFragmentEquipement(String nomMateriau) {
        FragmentEquipement fragment = trouverFragmentEquipement(nomMateriau);
        int qte = ctx.inventaire.getQuantiteMateriau(nomMateriau);
        String libelle = fragment != null
                ? fragment.getNomEquipement() + " [A]"
                : nomMateriau;
        String suffixe = "x" + qte + " / " + FragmentEquipement.QUANTITE_REQUISE;
        Node ligne = carteSimple(libelle, suffixe);
        if (fragment != null) {
            ligne.setCursor(Cursor.HAND);
            ligne.setOnMouseClicked(ev -> actionsFragmentEquipement(fragment));
        }
        return ligne;
    }

    /** Carte pour une boite de pierre. Clic -> Ouvrir directement. */
    private Node carteBoitePierre(int quantite) {
        Node ligne = carteSimple(MenuExamenS.MATERIAU_BOITE_PIERRE_LV1, "x" + quantite);
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(ev -> ouvrirBoitesPierre());
        return ligne;
    }

    /** Carte pour une pierre (jade). Clic -> Equiper/Vendre. */
    private Node cartePierre(Inventaire.StackPierre s) {
        Pierre p = new Pierre(s.getType(), s.getNiveau());
        Node ligne = carteSimple(p.toString(), "x" + s.getQuantite());
        ligne.setCursor(Cursor.HAND);
        ligne.setOnMouseClicked(ev -> actionsPierre(s));
        return ligne;
    }

    /** Carte simple nom + quantite (materiaux divers). */
    private Node carteSimple(String nom, String suffixe) {
        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        HBox ligne = new HBox(10, nomLabel);
        ligne.setAlignment(Pos.CENTER_LEFT);

        if (!suffixe.isEmpty()) {
            Label suf = new Label(suffixe);
            suf.getStyleClass().add("item-qte");
            ligne.getChildren().add(suf);
        }

        ligne.getStyleClass().add("carte-item");
        return ligne;
    }

    // ── Actions equipement (Equiper / Vendre) ───────────────────────────────

    private void actionsEquipement(Equipement e) {
        ButtonType equiperBtn = new ButtonType("Équiper");
        ButtonType vendreBtn  = new ButtonType("Vendre (" + prixVenteEquipement(e.getRarete()) + " or)");
        Optional<ButtonType> choix = choisirAction(e.getNomAffiche(), e.toString(), equiperBtn, vendreBtn);
        if (choix.isEmpty()) return;
        if (choix.get() == equiperBtn) equiperEquipement(e);
        else if (choix.get() == vendreBtn) vendreEquipement(e);
    }

    private void equiperEquipement(Equipement e) {
        PersonnageBase cible = choisirPersonnage("Équiper " + e.getNomAffiche());
        if (cible == null) return;

        if (!EquipementFactory.estCompatibleArme(cible.getType(), e)) {
            info("Équiper", cible.getNom() + " ne peut pas équiper cette pièce.");
            return;
        }

        Equipement.Slot slot = e.getSlot();
        Equipement actuel = cible.getEquipement(slot);
        if (actuel != null) {
            cible.desequiper(slot);
            ctx.inventaire.ajouterEquipement(actuel);
        }
        cible.equiper(e);
        ctx.inventaire.retirerEquipement(e);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Équiper", e.getNomAffiche() + " équipé sur " + cible.getNom() + " !");
        rafraichir();
    }

    private void vendreEquipement(Equipement e) {
        int prix = prixVenteEquipement(e.getRarete());
        if (!confirmer("Vendre " + e.getNomAffiche() + " pour " + prix + " or ?")) return;

        ctx.inventaire.retirerEquipement(e);
        ctx.joueur.ajouterOr(prix);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Vendre", e.getNomAffiche() + " vendu pour " + prix + " or.");
        rafraichir();
    }

    // ── Actions pierre / jade (Equiper / Vendre) ────────────────────────────

    private void actionsPierre(Inventaire.StackPierre s) {
        Pierre p = new Pierre(s.getType(), s.getNiveau());
        ButtonType equiperBtn = new ButtonType("Équiper");
        ButtonType vendreBtn  = new ButtonType("Vendre (" + prixVentePierre(s.getNiveau()) + " or)");
        Optional<ButtonType> choix = choisirAction(p.getNom(), p.toString(), equiperBtn, vendreBtn);
        if (choix.isEmpty()) return;
        if (choix.get() == equiperBtn) equiperPierre(s);
        else if (choix.get() == vendreBtn) vendrePierre(s);
    }

    private void equiperPierre(Inventaire.StackPierre s) {
        PersonnageBase cible = choisirPersonnage("Équiper une pierre");
        if (cible == null) return;

        List<Equipement> portes = cible.getEquipementsPortes();
        if (portes.isEmpty()) { info("Pierres", cible.getNom() + " ne porte aucun équipement."); return; }

        Equipement choisi = choisirEquipementPorte(portes);
        if (choisi == null) return;

        int emplacementLibre = -1;
        for (int i = 0; i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) {
            if (choisi.getPierre(i) == null) { emplacementLibre = i; break; }
        }
        if (emplacementLibre == -1) {
            info("Pierres", "Tous les emplacements de " + choisi.getNomAffiche()
                    + " sont pleins. Gérez-les depuis Améliorations > Pierres pour en remplacer un.");
            return;
        }

        Pierre nouvelle = new Pierre(s.getType(), s.getNiveau());
        String resultat = choisi.insererPierre(emplacementLibre, nouvelle);
        if (!resultat.equals("OK")) { info("Pierres", resultat); return; }

        ctx.inventaire.retirerPierre(s.getType(), s.getNiveau(), 1);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Pierres", nouvelle + " insérée sur " + choisi.getNomAffiche() + " (" + cible.getNom() + ") !");
        rafraichir();
    }

    private void vendrePierre(Inventaire.StackPierre s) {
        Pierre p = new Pierre(s.getType(), s.getNiveau());
        int prix = prixVentePierre(s.getNiveau());
        if (!confirmer("Vendre " + p + " pour " + prix + " or ?")) return;

        ctx.inventaire.retirerPierre(s.getType(), s.getNiveau(), 1);
        ctx.joueur.ajouterOr(prix);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Vendre", p + " vendue pour " + prix + " or.");
        rafraichir();
    }

    private Equipement choisirEquipementPorte(List<Equipement> liste) {
        return GuiVisuels.choisirParmiCartes("Choisir un équipement", liste, this::carteEquipementPorteChoix);
    }

    private Node carteEquipementPorteChoix(Equipement e) {
        int rempli = 0;
        for (Pierre p : e.getPierres()) if (p != null) rempli++;

        Label badge = GuiVisuels.creerBadgeRarete(e.getRarete().name());
        Label nom = new Label(e.getNomAffiche());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Pierres : " + rempli + "/" + Equipement.NB_EMPLACEMENTS_PIERRES);
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(260);
        return carte;
    }

    // ── Action carte d'or (Utiliser) ─────────────────────────────────────────

    private void utiliserCarteOr(Inventaire.StackCarteOr s) {
        var carte = s.getCarte();
        int dispo = s.getQuantite();

        TextInputDialog qteDialog = new TextInputDialog(String.valueOf(dispo));
        qteDialog.setTitle("Utiliser " + carte.nom);
        qteDialog.setHeaderText(null);
        qteDialog.setContentText("Vous avez " + dispo + " x " + carte.nom + " (" + carte.valeurOr + " or chacune).\nCombien en utiliser ?");
        styliser(qteDialog);
        Optional<String> qteStr = qteDialog.showAndWait();
        if (qteStr.isEmpty()) return;

        int quantite;
        try {
            quantite = Integer.parseInt(qteStr.get().trim());
        } catch (NumberFormatException e) {
            info("Cartes d'Or", "Entree invalide.");
            return;
        }
        if (quantite < 1 || quantite > dispo) {
            info("Cartes d'Or", "Quantite invalide. Vous en avez " + dispo + ".");
            return;
        }

        long orGagne = (long) carte.valeurOr * quantite;
        ctx.inventaire.retirerCartesOr(carte, quantite);
        long restant = orGagne;
        while (restant > 0) {
            int tranche = (int) Math.min(restant, Integer.MAX_VALUE);
            ctx.joueur.ajouterOr(tranche);
            restant -= tranche;
        }
        ctx.gestionnaireQuetes.notifierOrGagne((int) Math.min(orGagne, Integer.MAX_VALUE));
        ctx.sauvegarde.sauvegarder(ctx);

        info("Cartes d'Or", "Vous avez utilisé " + quantite + " x " + carte.nom + " !\n"
                + "+ " + String.format("%,d", orGagne) + " or gagné !\n"
                + "Or total : " + String.format("%,.0f", ctx.joueur.getOr()) + " or");
        rafraichir();
    }

    // ── Action fragment de personnage (Recruter) ────────────────────────────

    private String rareteFragmentPerso(String nomPerso) {
        for (String[] info : MenuEtoilesPerso.getCatalogue()) {
            if (info[0].equals(nomPerso)) return info[1];
        }
        return null;
    }

    private void actionsFragmentPerso(String nomPerso, String rarete) {
        if (rarete == null) {
            info("Fragments", "Fragment de personnage inconnu du catalogue.");
            return;
        }
        boolean dejaRecrute = GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, nomPerso);
        if (dejaRecrute) {
            info("Fragments", nomPerso + " est déjà recruté. Utilisez le menu Étoiles pour monter ses étoiles avec ces fragments.");
            return;
        }

        int qte  = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, nomPerso);
        int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(rarete);
        if (qte < cout) {
            info("Fragments", nomPerso + " [" + rarete + "] : " + qte + " / " + cout
                    + " fragments.\nContinuez à en collecter pour le recruter.");
            return;
        }

        if (!confirmer("Recruter " + nomPerso + " [" + rarete + "] pour " + cout + " fragments ?")) return;

        GestionnaireEtoilesPerso.recruterViaFragments(ctx.inventaire, nomPerso, rarete);
        PersonnageBase nouveau = ctx.sauvegarde.creerPersonnageParNom(nomPerso);
        if (nouveau == null) { info("Fragments", "Erreur : personnage introuvable."); return; }

        ctx.personnagesRecruites.add(nouveau);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Fragments", nomPerso + " [" + rarete + "] a rejoint l'équipe !\n" + cout + " fragments consommés.");
        rafraichir();
    }

    // ── Action fragment d'equipement (Synthetiser) ──────────────────────────

    private FragmentEquipement trouverFragmentEquipement(String nomMateriau) {
        for (FragmentEquipement f : gestionnaireFragments.getCatalogue()) {
            if (f.getNomFragment().equals(nomMateriau)) return f;
        }
        return null;
    }

    private void actionsFragmentEquipement(FragmentEquipement fragment) {
        int qte = ctx.inventaire.getQuantiteMateriau(fragment.getNomFragment());
        if (qte < FragmentEquipement.QUANTITE_REQUISE) {
            info("Synthèse", fragment.getNomEquipement() + " [A] : " + qte + " / " + FragmentEquipement.QUANTITE_REQUISE
                    + " fragments.\nContinuez à en collecter (Chapitre 3 Elite).");
            return;
        }

        if (!confirmer("Synthétiser " + fragment.getNomEquipement() + " [A] ? (" + FragmentEquipement.QUANTITE_REQUISE + " fragments consommés)")) return;

        Equipement nouvel = gestionnaireFragments.synthetiser(fragment, ctx.inventaire);
        if (nouvel == null) { info("Synthèse", "Fragments insuffisants."); return; }

        ctx.inventaire.ajouterEquipement(nouvel);
        ctx.sauvegarde.sauvegarder(ctx);
        info("Synthèse", "Synthèse réussie !\n" + nouvel + " ajouté à l'inventaire !");
        rafraichir();
    }

    // ── Action boite de pierre (Ouvrir) ─────────────────────────────────────

    private void ouvrirBoitesPierre() {
        int stock = ctx.inventaire.getQuantiteMateriau(MenuExamenS.MATERIAU_BOITE_PIERRE_LV1);
        if (stock <= 0) {
            info("Boites de pierre", "Aucune " + MenuExamenS.MATERIAU_BOITE_PIERRE_LV1 + " en stock.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(stock));
        dialog.setTitle("Ouvrir des Boites de pierre");
        dialog.setHeaderText(null);
        dialog.setContentText("Vous avez " + stock + " x " + MenuExamenS.MATERIAU_BOITE_PIERRE_LV1
                + ".\nCombien en ouvrir ?");
        styliser(dialog);
        Optional<String> reponse = dialog.showAndWait();
        if (reponse.isEmpty()) return;

        int quantite;
        try {
            quantite = Integer.parseInt(reponse.get().trim());
        } catch (NumberFormatException e) {
            info("Boites de pierre", "Entree invalide.");
            return;
        }
        if (quantite <= 0) return;
        if (quantite > stock) {
            info("Boites de pierre", "Quantite invalide. Vous en avez " + stock + ".");
            return;
        }

        StringBuilder resultats = new StringBuilder();
        for (int i = 0; i < quantite; i++) {
            resultats.append(MenuExamenS.ouvrirBoite(ctx.inventaire)).append("\n");
        }
        ctx.sauvegarde.sauvegarder(ctx);
        info("Boites de pierre", resultats.toString().trim());
        rafraichir();
    }

    @FXML
    private void onEquiperSet(ActionEvent event) {
        Inventaire inv = ctx.inventaire;
        if (inv.estVide()) { info("Equiper un set", "L'inventaire est vide !"); return; }

        PersonnageBase cible = choisirPersonnage("Equiper un set complet");
        if (cible == null) return;

        List<Equipement.Rarete> raretesDisponibles = new ArrayList<>();
        for (Equipement e : inv.getEquipements()) {
            if (!raretesDisponibles.contains(e.getRarete())) raretesDisponibles.add(e.getRarete());
        }
        if (raretesDisponibles.isEmpty()) { info("Equiper un set", "Aucun equipement disponible dans l'inventaire."); return; }

        Equipement.Rarete rareteChoisie = GuiVisuels.choisirParmiCartes(
                "Rareté à équiper sur " + cible.getNom(), raretesDisponibles, this::carteRareteChoix);
        if (rareteChoisie == null) return;

        List<Equipement> candidats = new ArrayList<>();
        for (Equipement e : inv.getEquipements()) {
            if (e.getRarete() == rareteChoisie && EquipementFactory.estCompatibleArme(cible.getType(), e)) {
                candidats.add(e);
            }
        }

        StringBuilder message = new StringBuilder();
        int equipesCount = 0;
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            if (cible.getEquipement(slot) != null) continue;
            Equipement aEquiper = null;
            for (Equipement e : candidats) {
                if (e.getSlot() == slot) { aEquiper = e; break; }
            }
            if (aEquiper == null) continue;

            inv.retirerEquipement(aEquiper);
            cible.equiper(aEquiper);
            candidats.remove(aEquiper);
            message.append(slot.name()).append(" : ").append(aEquiper.getNom()).append(" equipe !\n");
            equipesCount++;
        }

        if (equipesCount == 0) {
            info("Equiper un set", "Aucun slot libre compatible avec la rarete " + rareteChoisie.name() + ".");
        } else {
            ctx.sauvegarde.sauvegarder(ctx);
            info("Equiper un set", message.append(equipesCount).append(" piece(s) equipee(s) de rarete ").append(rareteChoisie.name()).append(".").toString());
        }
        rafraichir();
    }

    private PersonnageBase choisirPersonnage(String titre) {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);
        return GuiVisuels.choisirParmiCartes(titre, tous, this::cartePersonnageChoix);
    }

    private Node cartePersonnageChoix(PersonnageBase p) {
        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(p.getRole() + "  ·  Niv." + p.getNiveau() + "  ·  " + p.getType());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(260);
        return carte;
    }

    private Node carteRareteChoix(Equipement.Rarete r) {
        Label badge = GuiVisuels.creerBadgeRarete(r.name());
        Label nom = new Label("Rareté " + r.name());
        nom.getStyleClass().add("item-nom");

        HBox carte = new HBox(10, badge, nom);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(200);
        return carte;
    }

    @FXML
    private void onRetour(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranMenuPrincipal.fxml");
            EcranMenuPrincipalController controller = loader.getController();
            controller.initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<ButtonType> choisirAction(String titre, String message, ButtonType... actions) {
        ButtonType[] boutons = new ButtonType[actions.length + 1];
        System.arraycopy(actions, 0, boutons, 0, actions.length);
        boutons[actions.length] = ButtonType.CANCEL;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, boutons);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        styliser(alert);
        return alert.showAndWait();
    }

    private boolean confirmer(String question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        styliser(confirm);
        Optional<ButtonType> resultat = confirm.showAndWait();
        return resultat.isPresent() && resultat.get() == ButtonType.YES;
    }

    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        styliser(alert);
        alert.showAndWait();
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }
}
