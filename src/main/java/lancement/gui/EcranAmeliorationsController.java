package lancement.gui;

import Equipement.Equipement;
import Equipement.Inventaire;
import Equipement.Pierre;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Menus.MenuAmeliorations;

/**
 * Ecran unique de gestion des ameliorations : selection du personnage, de sa piece
 * d'equipement, puis fortification/affinage (clic direct, sans popup) ou incrustation
 * de pierres (glisser-deposer ou double-clic).
 */
public class EcranAmeliorationsController {

    private static final Equipement.Slot[] ORDRE_SLOTS = {
        Equipement.Slot.ARME, Equipement.Slot.COUVRE_CHEF,
        Equipement.Slot.TORSE, Equipement.Slot.JAMBIERES,
        Equipement.Slot.MAINS, Equipement.Slot.BOTTES
    };

    private GameContext ctx;

    @FXML private VBox personnagesBox;
    @FXML private VBox equipementBox;
    @FXML private VBox panneauDroit;

    private PersonnageBase personnageSelectionne;
    private Equipement.Slot slotSelectionne;
    private boolean modePierres = false;

    private final Map<Equipement.Slot, Label> nomLabelsSlot = new EnumMap<>(Equipement.Slot.class);
    private final Map<Equipement.Slot, Label> detailLabelsSlot = new EnumMap<>(Equipement.Slot.class);

    private Label labelTitreEquip;
    private Label labelNiveauFort;
    private Label labelCoutFort;
    private Label labelOr;
    private Label labelFeedbackFort;
    private Label labelNiveauAff;
    private Label labelCoutAff;
    private Label labelPierresAff;
    private Label labelFeedbackAff;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        rafraichirPersonnages();
        selectionnerPersonnage(ctx.joueur);
    }

    // ─────────────────────────────────────────────────────────────────
    // Selection du personnage
    // ─────────────────────────────────────────────────────────────────

    private void rafraichirPersonnages() {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        List<Node> cartes = new ArrayList<>();
        for (PersonnageBase p : tous) cartes.add(cartePersonnage(p));
        personnagesBox.getChildren().setAll(cartes);
    }

    private Node cartePersonnage(PersonnageBase p) {
        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Niv. " + p.getNiveau() + "  ·  " + p.getRole());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add(p == personnageSelectionne ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(200);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(e -> selectionnerPersonnage(p));
        return carte;
    }

    private void selectionnerPersonnage(PersonnageBase p) {
        personnageSelectionne = p;
        slotSelectionne = null;
        modePierres = false;
        rafraichirPersonnages();
        rafraichirEquipement();
        rafraichirPanneauDroit();
    }

    // ─────────────────────────────────────────────────────────────────
    // Grille d'equipement (6 emplacements)
    // ─────────────────────────────────────────────────────────────────

    private void rafraichirEquipement() {
        nomLabelsSlot.clear();
        detailLabelsSlot.clear();

        Label titre = new Label("EQUIPEMENT");
        titre.getStyleClass().add("section-titre");

        VBox contenu = new VBox(10, titre);
        contenu.setAlignment(Pos.TOP_CENTER);
        for (int i = 0; i < ORDRE_SLOTS.length; i += 2) {
            HBox ligne = new HBox(10, carteSlot(ORDRE_SLOTS[i]), carteSlot(ORDRE_SLOTS[i + 1]));
            ligne.setAlignment(Pos.CENTER);
            contenu.getChildren().add(ligne);
        }
        equipementBox.getChildren().setAll(contenu);
    }

    private Node carteSlot(Equipement.Slot slot) {
        Equipement e = personnageSelectionne.getEquipement(slot);

        Label nom = new Label(e != null ? e.getNomAffiche() : "[Vide]");
        nom.getStyleClass().add(e != null ? "item-nom" : "item-vide");

        String infos = nomSlotAffiche(slot);
        if (e != null) infos += "  ·  Fort." + e.getNiveauFortification() + "  ·  Aff." + e.getNiveauAffinage();
        Label detail = new Label(infos);
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        texte.setAlignment(Pos.CENTER);
        VBox carte = new VBox(texte);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add(slot == slotSelectionne ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(140);
        carte.setPrefHeight(60);

        if (e != null) {
            carte.setCursor(Cursor.HAND);
            carte.setOnMouseClicked(ev -> selectionnerEquipement(slot));
        } else {
            carte.setCursor(Cursor.DEFAULT);
        }

        nomLabelsSlot.put(slot, nom);
        detailLabelsSlot.put(slot, detail);
        return carte;
    }

    private void selectionnerEquipement(Equipement.Slot slot) {
        if (personnageSelectionne.getEquipement(slot) == null) return;
        slotSelectionne = slot;
        modePierres = false;
        rafraichirEquipement();
        rafraichirPanneauDroit();
    }

    private void mettreAJourCarteSlot(Equipement.Slot slot, Equipement equip) {
        if (slot == null) return;
        Label nom = nomLabelsSlot.get(slot);
        Label detail = detailLabelsSlot.get(slot);
        if (nom != null) nom.setText(equip.getNomAffiche());
        if (detail != null) {
            detail.setText(nomSlotAffiche(slot) + "  ·  Fort." + equip.getNiveauFortification()
                    + "  ·  Aff." + equip.getNiveauAffinage());
        }
    }

    private static String nomSlotAffiche(Equipement.Slot slot) {
        return switch (slot) {
            case ARME        -> "Arme";
            case COUVRE_CHEF -> "Couvre-chef";
            case TORSE       -> "Torse";
            case MAINS       -> "Mains";
            case JAMBIERES   -> "Jambieres";
            case BOTTES      -> "Bottes";
        };
    }

    // ─────────────────────────────────────────────────────────────────
    // Panneau de droite (fortification/affinage ou pierres)
    // ─────────────────────────────────────────────────────────────────

    private void rafraichirPanneauDroit() {
        if (slotSelectionne == null) {
            panneauDroit.getChildren().setAll(placeholder("Selectionnez un equipement porte."));
            return;
        }
        Equipement equip = personnageSelectionne.getEquipement(slotSelectionne);
        if (equip == null) {
            slotSelectionne = null;
            panneauDroit.getChildren().setAll(placeholder("Selectionnez un equipement porte."));
            return;
        }
        if (modePierres) {
            construirePanneauPierres(equip);
        } else {
            construirePanneauNormal(equip);
        }
    }

    private Node placeholder(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("item-detail");
        l.setWrapText(true);
        l.setMaxWidth(260);
        l.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        VBox box = new VBox(l);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void construirePanneauNormal(Equipement equip) {
        labelTitreEquip = new Label(equip.getNomAffiche());
        labelTitreEquip.getStyleClass().add("section-titre");
        HBox header = new HBox(8, GuiVisuels.creerBadgeRarete(equip.getRarete().name()), labelTitreEquip);
        header.setAlignment(Pos.CENTER_LEFT);

        // ── Fortification ──
        Label titreFort = new Label("Fortification");
        titreFort.getStyleClass().add("item-nom");
        labelNiveauFort = new Label();
        labelNiveauFort.getStyleClass().add("item-detail");
        labelCoutFort = new Label();
        labelCoutFort.getStyleClass().add("item-qte");
        labelOr = new Label();
        labelOr.getStyleClass().add("item-detail");
        labelFeedbackFort = new Label();
        labelFeedbackFort.getStyleClass().add("item-vide");

        Button boutonFortifier = new Button("Fortifier");
        boutonFortifier.getStyleClass().add("menu-bouton");
        boutonFortifier.setOnAction(e -> onFortifier(equip));

        VBox blocFort = new VBox(4, titreFort, labelNiveauFort, labelCoutFort, boutonFortifier, labelOr, labelFeedbackFort);

        Label separateur = new Label("— ou —");
        separateur.getStyleClass().add("sous-titre");

        // ── Affinage ──
        boolean affinageDebloque = ctx.joueur.getNiveau() >= MenuAmeliorations.NIVEAU_DEBLOCAGE_AFFINAGE;
        VBox blocAff;
        if (affinageDebloque) {
            Label titreAff = new Label("Affinage");
            titreAff.getStyleClass().add("item-nom");
            labelNiveauAff = new Label();
            labelNiveauAff.getStyleClass().add("item-detail");
            labelCoutAff = new Label();
            labelCoutAff.getStyleClass().add("item-qte");
            labelPierresAff = new Label();
            labelPierresAff.getStyleClass().add("item-detail");
            labelFeedbackAff = new Label();
            labelFeedbackAff.getStyleClass().add("item-vide");

            Button boutonAffiner = new Button("Affiner");
            boutonAffiner.getStyleClass().add("menu-bouton");
            boutonAffiner.setOnAction(e -> onAffiner(equip));

            blocAff = new VBox(4, titreAff, labelNiveauAff, labelCoutAff, boutonAffiner, labelPierresAff, labelFeedbackAff);
        } else {
            labelNiveauAff = null;
            labelCoutAff = null;
            labelPierresAff = null;
            labelFeedbackAff = null;
            Label info = new Label("Affinage debloque au niveau " + MenuAmeliorations.NIVEAU_DEBLOCAGE_AFFINAGE + ".");
            info.getStyleClass().add("item-detail");
            info.setWrapText(true);
            blocAff = new VBox(info);
        }

        Button boutonPierres = new Button("Incruster pierre");
        boutonPierres.getStyleClass().add("menu-bouton");
        boutonPierres.setOnAction(e -> { modePierres = true; rafraichirPanneauDroit(); });

        mettreAJourValeursFort(equip);
        if (affinageDebloque) mettreAJourValeursAff(equip);

        panneauDroit.getChildren().setAll(header, blocFort, separateur, blocAff, boutonPierres);
    }

    private void onFortifier(Equipement equip) {
        int fortMax = ctx.joueur.getNiveau();
        if (equip.getNiveauFortification() >= fortMax) {
            labelFeedbackFort.setText("Fortification maximale atteinte.");
            return;
        }
        int cout = equip.getCoutFortification();
        if (ctx.joueur.getOr() < cout) {
            labelFeedbackFort.setText("Or insuffisant.");
            return;
        }
        ctx.joueur.retirerOr(cout);
        equip.fortifier();
        labelFeedbackFort.setText("");
        mettreAJourValeursFort(equip);
        mettreAJourCarteSlot(slotSelectionne, equip);
    }

    private void mettreAJourValeursFort(Equipement equip) {
        int fortMax = ctx.joueur.getNiveau();
        labelTitreEquip.setText(equip.getNomAffiche());
        labelNiveauFort.setText("Niveau actuel : Fort." + equip.getNiveauFortification());
        boolean max = equip.getNiveauFortification() >= fortMax;
        labelCoutFort.setText(max ? "Fortification max" : "Cout : " + GuiVisuels.formaterMontant(equip.getCoutFortification()) + " or");
        labelOr.setText("Or actuel : " + GuiVisuels.formaterMontant(ctx.joueur.getOr()));
    }

    private void onAffiner(Equipement equip) {
        if (equip.getNiveauAffinage() >= MenuAmeliorations.NIVEAU_MAX_AFFINAGE) {
            labelFeedbackAff.setText("Niveau d'affinage maximum atteint.");
            return;
        }
        int cout = equip.getCoutAffinageProchainNiveau();
        int dispo = ctx.inventaire.getQuantiteMateriau(MenuAmeliorations.MATERIAU_AFFINAGE);
        if (dispo < cout) {
            labelFeedbackAff.setText("Pierres d'affinage insuffisantes.");
            return;
        }
        ctx.inventaire.retirerMateriau(MenuAmeliorations.MATERIAU_AFFINAGE, cout);
        equip.affiner();
        labelFeedbackAff.setText("");
        mettreAJourValeursAff(equip);
        mettreAJourCarteSlot(slotSelectionne, equip);
    }

    private void mettreAJourValeursAff(Equipement equip) {
        labelTitreEquip.setText(equip.getNomAffiche());
        labelNiveauAff.setText("Niveau actuel : Aff." + equip.getNiveauAffinage() + " (+" + equip.getNiveauAffinage() + "%)");
        boolean max = equip.getNiveauAffinage() >= MenuAmeliorations.NIVEAU_MAX_AFFINAGE;
        labelCoutAff.setText(max ? "Affinage max" : "Cout : " + GuiVisuels.formaterMontant(equip.getCoutAffinageProchainNiveau()) + " pierres");
        labelPierresAff.setText("Pierres d'affinage actuelles : "
                + GuiVisuels.formaterMontant(ctx.inventaire.getQuantiteMateriau(MenuAmeliorations.MATERIAU_AFFINAGE)));
    }

    // ─────────────────────────────────────────────────────────────────
    // Incrustation de pierres (glisser-deposer + double-clic)
    // ─────────────────────────────────────────────────────────────────

    private void construirePanneauPierres(Equipement equip) {
        Label titre = new Label("Pierres — " + equip.getNomAffiche());
        titre.getStyleClass().add("section-titre");
        titre.setWrapText(true);
        titre.setMaxWidth(300);

        FlowPane emplacements = new FlowPane(8, 8);
        emplacements.setAlignment(Pos.CENTER);
        for (int i = 0; i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) {
            emplacements.getChildren().add(carteEmplacementPierre(equip, i));
        }

        Label invTitre = new Label("Inventaire de pierres");
        invTitre.getStyleClass().add("item-nom");

        FlowPane inventairePane = new FlowPane(8, 8);
        inventairePane.setAlignment(Pos.CENTER_LEFT);
        remplirInventairePierres(inventairePane, equip);

        inventairePane.setOnDragOver(ev -> {
            if (ev.getGestureSource() != inventairePane && ev.getDragboard().hasString()
                    && ev.getDragboard().getString().startsWith("SLOT:")) {
                ev.acceptTransferModes(TransferMode.MOVE);
            }
            ev.consume();
        });
        inventairePane.setOnDragDropped(ev -> {
            Dragboard db = ev.getDragboard();
            boolean succes = false;
            if (db.hasString() && db.getString().startsWith("SLOT:")) {
                int index = Integer.parseInt(db.getString().substring("SLOT:".length()));
                succes = retirerPierreVersStock(equip, index);
            }
            ev.setDropCompleted(succes);
            ev.consume();
        });

        ScrollPane invScroll = new ScrollPane(inventairePane);
        invScroll.setFitToWidth(true);
        invScroll.setPrefViewportHeight(180);
        invScroll.getStyleClass().add("scroll-pane");
        invScroll.setStyle("-fx-background-color: transparent;");

        Button retour = new Button("Retour");
        retour.getStyleClass().add("menu-bouton");
        retour.setOnAction(e -> { modePierres = false; rafraichirPanneauDroit(); });

        panneauDroit.getChildren().setAll(titre, emplacements, invTitre, invScroll, retour);
    }

    private Node carteEmplacementPierre(Equipement equip, int index) {
        Pierre p = equip.getPierre(index);

        Label label = new Label(p != null ? p.getNomType() + " Niv." + p.getNiveau() : "Vide");
        label.getStyleClass().add(p != null ? "item-nom" : "item-vide");
        label.setWrapText(true);
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        label.setMaxWidth(76);

        StackPane carte = new StackPane(label);
        carte.getStyleClass().add(p != null ? "carte-item-joueur" : "carte-item");
        carte.setPrefSize(84, 64);
        carte.setCursor(Cursor.HAND);

        carte.setOnDragOver(ev -> {
            if (ev.getGestureSource() != carte && equip.getPierre(index) == null
                    && ev.getDragboard().hasString() && ev.getDragboard().getString().startsWith("PIERRE:")) {
                ev.acceptTransferModes(TransferMode.MOVE);
            }
            ev.consume();
        });
        carte.setOnDragDropped(ev -> {
            Dragboard db = ev.getDragboard();
            boolean succes = false;
            if (db.hasString() && db.getString().startsWith("PIERRE:")) {
                String[] parts = db.getString().split(":");
                Pierre.Type type = Pierre.Type.valueOf(parts[1]);
                int niveau = Integer.parseInt(parts[2]);
                succes = insererPierreDansEmplacement(equip, index, type, niveau);
            }
            ev.setDropCompleted(succes);
            ev.consume();
        });

        if (p != null) {
            carte.setOnDragDetected(ev -> {
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("SLOT:" + index);
                db.setContent(content);
                ev.consume();
            });
            carte.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2) retirerPierreVersStock(equip, index);
            });
        }

        return carte;
    }

    private void remplirInventairePierres(FlowPane pane, Equipement equip) {
        pane.getChildren().clear();
        List<Inventaire.StackPierre> stock = ctx.inventaire.getPierres();
        if (stock.isEmpty()) {
            Label vide = new Label("Aucune pierre en stock.");
            vide.getStyleClass().add("item-vide");
            pane.getChildren().add(vide);
            return;
        }
        for (Inventaire.StackPierre s : stock) pane.getChildren().add(cartePierreInventaire(s, equip));
    }

    private Node cartePierreInventaire(Inventaire.StackPierre s, Equipement equip) {
        Label nom = new Label(s.toString());
        nom.getStyleClass().add("item-detail");

        HBox carte = new HBox(nom);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setCursor(Cursor.HAND);

        carte.setOnDragDetected(ev -> {
            Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("PIERRE:" + s.getType().name() + ":" + s.getNiveau());
            db.setContent(content);
            ev.consume();
        });
        carte.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                int emplacementVide = premierEmplacementVide(equip);
                if (emplacementVide != -1) insererPierreDansEmplacement(equip, emplacementVide, s.getType(), s.getNiveau());
            }
        });

        return carte;
    }

    private int premierEmplacementVide(Equipement equip) {
        for (int i = 0; i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) {
            if (equip.getPierre(i) == null) return i;
        }
        return -1;
    }

    private boolean insererPierreDansEmplacement(Equipement equip, int index, Pierre.Type type, int niveau) {
        String resultat = equip.insererPierre(index, new Pierre(type, niveau));
        if (!resultat.equals("OK")) return false;
        ctx.inventaire.retirerPierre(type, niveau, 1);
        rafraichirPanneauDroit();
        mettreAJourCarteSlot(slotSelectionne, equip);
        return true;
    }

    private boolean retirerPierreVersStock(Equipement equip, int index) {
        Pierre p = equip.retirerPierre(index);
        if (p == null) return false;
        ctx.inventaire.ajouterPierre(p.getType(), p.getNiveau(), 1);
        rafraichirPanneauDroit();
        mettreAJourCarteSlot(slotSelectionne, equip);
        return true;
    }

    // ─────────────────────────────────────────────────────────────────

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
}
