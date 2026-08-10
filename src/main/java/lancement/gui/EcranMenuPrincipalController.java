package lancement.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.GestionnaireExamenS;
import lancement.Gestionnaires.GestionnaireQuetes;
import lancement.Gestionnaires.GestionnaireRecompenses;
import lancement.Gestionnaires.GestionnaireTutoriel;
import lancement.Gestionnaires.Gestionnaire_pet;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;

public class EcranMenuPrincipalController {

    private GameContext ctx;
    private final Map<String, Button> boutonsParLibelle = new LinkedHashMap<>();

    @FXML private FlowPane statsBox;
    @FXML private VBox xpBarBox;
    @FXML private VBox boutonsBox;
    @FXML private ScrollPane menuScrollPane;
    @FXML private Pane tutorielOverlay;
    @FXML private StackPane racineMenu;
    @FXML private ImageView fondImage;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        lancement.Gestionnaires.SuiviTempsJeu.demarrer(ctx);
        remplirFondCouvrant(fondImage, racineMenu);

        statsBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("♚", ctx.joueur.getNom(), "Niv. " + ctx.joueur.getNiveau()),
                GuiVisuels.creerFicheStat("◆", "Rang", ctx.rangJoueur.getRangNom()),
                GuiVisuels.creerFicheStat("●", "Or", GuiVisuels.formaterMontant(ctx.joueur.getOr())),
                GuiVisuels.creerFicheStat("✉", "Coupons", GuiVisuels.formaterMontant(ctx.joueur.getCoupons())),
                GuiVisuels.creerFicheStat("⚡", "Combativité", String.valueOf(ctx.formation.getCombativite())),
                GuiVisuels.creerFicheStat("✳", "Énergie", ctx.gestionnaireEnergie.afficherEnergie())
        );

        xpBarBox.getChildren().setAll(
                GuiVisuels.creerBarreXP(260, 8, ctx.joueur.getExperience(), ctx.joueur.getExperienceMax()));

        boolean chapitre2Fini      = ctx.chapitre2.getStagesReussis()[10];
        boolean chapitre1EliteFini = ctx.chapitre1Elite.getStagesReussis()[10];
        int     niveau             = ctx.joueur.getNiveau();

        List<BoutonDef> progression = new ArrayList<>();
        progression.add(new BoutonDef("Histoire", this::onHistoire));
        progression.add(new BoutonDef("Quetes", this::onQuetes));
        progression.add(new BoutonDef("Recompenses", this::onRecompenses));
        if (niveau >= 3)                                       progression.add(new BoutonDef("Abilites", this::onAbilites));
        if (chapitre1EliteFini)                                progression.add(new BoutonDef("Rang & Titres", this::onRangTitres));

        List<BoutonDef> personnages = new ArrayList<>();
        personnages.add(new BoutonDef("Formation", this::onFormation));
        personnages.add(new BoutonDef("Personnages", this::onPersonnages));
        personnages.add(new BoutonDef("Inventaire", this::onInventaire));
        if (niveau >= 10)                                       personnages.add(new BoutonDef("Ameliorations", this::onAmeliorations));
        if (niveau >= GestionnaireCompagnons.NIVEAU_DEBLOCAGE)  personnages.add(new BoutonDef("Compagnons", this::onCompagnons));
        if (niveau >= Gestionnaire_pet.NIVEAU_DEBLOCAGE)        personnages.add(new BoutonDef("Creatures Sacrees", this::onCreaturesSacrees));

        List<BoutonDef> recrutementGacha = new ArrayList<>();
        if (niveau >= 6)                                       recrutementGacha.add(new BoutonDef("Recrutement", this::onRecrutement));
        if (chapitre2Fini)                                     recrutementGacha.add(new BoutonDef("Recrutement Rare", this::onRecrutementRare));
        if (chapitre2Fini)                                     recrutementGacha.add(new BoutonDef("Chasse au tresor", this::onChasseTresor));
        recrutementGacha.add(new BoutonDef("Tirages", this::onTirages));
        if (niveau >= 6)                                       recrutementGacha.add(new BoutonDef("Etoiles & Fragments", this::onEtoiles));

        List<BoutonDef> modes = new ArrayList<>();
        if (niveau >= 10)                                       modes.add(new BoutonDef("Boutique", this::onBoutique));
        if (niveau >= 10)                                       modes.add(new BoutonDef("Donjon de ressources", this::onDonjon));
        if (niveau >= 20)                                       modes.add(new BoutonDef("Arene", this::onArene));
        if (niveau >= GestionnaireExamenS.NIVEAU_REQUIS)        modes.add(new BoutonDef("Examen de Rang S", this::onExamenS));

        boutonsParLibelle.clear();
        boutonsBox.getChildren().clear();
        ajouterSection("Progression", progression);
        ajouterSection("Personnages & Équipement", personnages);
        ajouterSection("Recrutement & Gacha", recrutementGacha);
        ajouterSection("Modes & Défis", modes);

        Platform.runLater(this::verifierTutoriel);
    }

    /** Fait remplir le fond d'ecran par l'image en "cover" (recadree, jamais deformee, jamais de
     *  bande vide) : recalcule un viewport sur l'image source a chaque redimensionnement de la
     *  fenetre, plutot qu'un simple fitWidth/fitHeight qui etirerait ou letterboxerait l'image. */
    private void remplirFondCouvrant(ImageView vue, Region conteneur) {
        Runnable maj = () -> {
            double w = conteneur.getWidth(), h = conteneur.getHeight();
            if (w <= 0 || h <= 0 || vue.getImage() == null) return;
            double imgW = vue.getImage().getWidth(), imgH = vue.getImage().getHeight();
            double echelle = Math.max(w / imgW, h / imgH);
            double vw = Math.min(imgW, w / echelle);
            double vh = Math.min(imgH, h / echelle);
            vue.setViewport(new Rectangle2D((imgW - vw) / 2, (imgH - vh) / 2, vw, vh));
            vue.setFitWidth(w);
            vue.setFitHeight(h);
        };
        conteneur.widthProperty().addListener((o, a, b) -> maj.run());
        conteneur.heightProperty().addListener((o, a, b) -> maj.run());
        maj.run();
    }

    private record BoutonDef(String libelle, EventHandler<ActionEvent> action) {}

    private void ajouterSection(String titre, List<BoutonDef> boutons) {
        if (boutons.isEmpty()) return;

        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("section-titre");
        boutonsBox.getChildren().add(titreLabel);

        FlowPane grille = new FlowPane(10, 10);
        grille.setAlignment(Pos.CENTER);
        grille.setPrefWrapLength(480);
        for (BoutonDef b : boutons) {
            Button bouton = new Button(b.libelle());
            bouton.getStyleClass().add("menu-tuile");
            Label icone = new Label(iconePour(b.libelle()));
            icone.getStyleClass().add("menu-tuile-icone");
            bouton.setGraphic(icone);
            bouton.setContentDisplay(ContentDisplay.TOP);
            bouton.setOnAction(b.action());
            boutonsParLibelle.put(b.libelle(), bouton);
            grille.getChildren().add(avecBadgeNotification(bouton, aNotification(b.libelle())));
        }
        boutonsBox.getChildren().add(grille);
    }

    /**
     * Icone affichee au-dessus du libelle de chaque tuile de navigation. Volontairement limitee
     * aux blocs Unicode "Miscellaneous Symbols"/"Dingbats" (U+2600-U+27BF) plutot qu'aux emojis
     * couleur modernes (U+1F300+) : ces derniers ne s'affichent pas de maniere fiable avec le
     * rendu de texte par defaut de JavaFX sur Windows (glyphes manquants/invisibles).
     */
    private static final Map<String, String> ICONES_MENU = Map.ofEntries(
            Map.entry("Histoire", "✎"),
            Map.entry("Quetes", "☰"),
            Map.entry("Recompenses", "✪"),
            Map.entry("Abilites", "✦"),
            Map.entry("Rang & Titres", "♛"),
            Map.entry("Formation", "⚑"),
            Map.entry("Personnages", "♟"),
            Map.entry("Inventaire", "▣"),
            Map.entry("Ameliorations", "⚒"),
            Map.entry("Compagnons", "♞"),
            Map.entry("Creatures Sacrees", "☄"),
            Map.entry("Recrutement", "✵"),
            Map.entry("Recrutement Rare", "✹"),
            Map.entry("Chasse au tresor", "⚓"),
            Map.entry("Tirages", "❉"),
            Map.entry("Etoiles & Fragments", "★"),
            Map.entry("Donjon de ressources", "♜"),
            Map.entry("Arene", "⚔"),
            Map.entry("Examen de Rang S", "✒")
    );

    private String iconePour(String libelle) {
        return ICONES_MENU.getOrDefault(libelle, "▶");
    }

    /** Enveloppe le bouton d'un petit point rouge en haut a droite s'il y a quelque chose a reclamer. */
    private Node avecBadgeNotification(Button bouton, boolean notification) {
        if (!notification) return bouton;

        Circle point = new Circle(5, Color.web("#e05656"));
        point.setStroke(Color.web("#1b1b28"));
        point.setStrokeWidth(1.5);
        point.setMouseTransparent(true);
        point.setTranslateX(-6);
        point.setTranslateY(6);

        StackPane wrapper = new StackPane(bouton, point);
        StackPane.setAlignment(point, Pos.TOP_RIGHT);
        return wrapper;
    }

    // =========================================================================
    // BADGES DE NOTIFICATION (quelque chose de reclamable dans ce menu)
    // =========================================================================

    private boolean aNotification(String libelle) {
        return switch (libelle) {
            case "Recompenses" -> notificationRecompenses();
            case "Quetes"      -> notificationQuetes();
            case "Arene"       -> notificationArene();
            default -> false;
        };
    }

    private boolean notificationRecompenses() {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        for (int i = 0; i < GestionnaireRecompenses.PALIERS_NIVEAU.length; i++) {
            if (gr.estNiveauDisponible(i, ctx.joueur.getNiveau())) return true;
        }
        int[] paliersMois = gr.getPaliersMois();
        for (int i = 0; i < paliersMois.length; i++) {
            if (gr.estMoisDisponible(i)) return true;
        }
        if (!gr.estTerminee()) {
            for (int jour = 1; jour <= 7; jour++) {
                if (gr.estJourDisponible(jour)) return true;
            }
        }
        for (int i = 0; i < GestionnaireRecompenses.SEUILS_TEMPS_MINUTES.length; i++) {
            if (gr.estTempsDisponible(i)) return true;
        }
        return false;
    }

    private boolean notificationQuetes() {
        GestionnaireQuetes gq = ctx.gestionnaireQuetes;
        if (ctx.joueur.getNiveau() >= GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES) {
            for (QueteJournaliere qj : gq.getQuetesJournalieres()) {
                if (qj.isCompletee() && !qj.isReclamee()) return true;
            }
            for (int i = 0; i < GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE.length; i++) {
                if (gq.estBarreDisponible(i)) return true;
            }
        }
        for (QueteProgression q : gq.getQuetesVisibles(ctx)) {
            if (!q.isAcceptee() || (q.isCompletee() && !q.isReclamee())) return true;
        }
        return false;
    }

    private boolean notificationArene() {
        if (ctx.rangJoueur.estCoffreRangDisponible()) return true;

        java.time.LocalDateTime maintenant = java.time.LocalDateTime.now();
        if (maintenant.getHour() < 20) return false;
        String cleJour  = maintenant.toLocalDate().toString();
        String derniere = ctx.dernierCoffreArene != null ? ctx.dernierCoffreArene : "";
        return !cleJour.equals(derniere);
    }

    // =========================================================================
    // TUTORIEL GUIDE
    // =========================================================================

    private void verifierTutoriel() {
        GestionnaireTutoriel gt = ctx.gestionnaireTutoriel;

        if (!gt.isPropose()) {
            gt.setPropose(true);
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Vous decouvrez le jeu ? Une visite guidee peut vous presenter chaque menu disponible.",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Tutoriel");
            confirm.setHeaderText(null);
            styliser(confirm);
            boolean accepte = confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
            gt.setActif(accepte);
            ctx.sauvegarde.sauvegarder(ctx);
        }

        if (gt.isActif()) {
            lancerEtapesEnAttente();
        }
    }

    private void lancerEtapesEnAttente() {
        List<GestionnaireTutoriel.EtapeTutoriel> aFaire = new ArrayList<>();
        for (GestionnaireTutoriel.EtapeTutoriel e : GestionnaireTutoriel.getEtapes()) {
            if (boutonsParLibelle.containsKey(e.libelle()) && !ctx.gestionnaireTutoriel.aEteVue(e.libelle())) {
                aFaire.add(e);
            }
        }
        if (!aFaire.isEmpty()) afficherEtape(aFaire, 0);
    }

    private void afficherEtape(List<GestionnaireTutoriel.EtapeTutoriel> etapes, int index) {
        GestionnaireTutoriel.EtapeTutoriel etape = etapes.get(index);
        Button cible = boutonsParLibelle.get(etape.libelle());
        if (cible == null) {
            avancer(etapes, index);
            return;
        }

        tutorielOverlay.setVisible(true);
        tutorielOverlay.setManaged(true);
        tutorielOverlay.getChildren().clear();

        Region fond = new Region();
        fond.setStyle("-fx-background-color: rgba(10,10,18,0.72);");
        fond.prefWidthProperty().bind(tutorielOverlay.widthProperty());
        fond.prefHeightProperty().bind(tutorielOverlay.heightProperty());
        tutorielOverlay.getChildren().add(fond);

        scrollVersBouton(cible);
        Platform.runLater(() -> positionnerBulle(etapes, index, cible));
    }

    /** Fait defiler le menu pour amener le bouton cible vers le centre du viewport. */
    private void scrollVersBouton(Button cible) {
        boutonsBox.applyCss();
        boutonsBox.layout();

        Bounds cibleDansBoutonsBox = boutonsBox.sceneToLocal(cible.localToScene(cible.getBoundsInLocal()));
        double hauteurContenu  = boutonsBox.getBoundsInLocal().getHeight();
        double hauteurViewport = menuScrollPane.getViewportBounds().getHeight();
        double centreCible     = cibleDansBoutonsBox.getMinY() + cibleDansBoutonsBox.getHeight() / 2.0;
        double denominateur    = Math.max(1, hauteurContenu - hauteurViewport);
        double vValue          = (centreCible - hauteurViewport / 2.0) / denominateur;

        menuScrollPane.setVvalue(Math.max(0, Math.min(1, vValue)));
    }

    /** Positionne la fleche et la bulle d'explication par-dessus le bouton cible, une fois le defilement applique. */
    private void positionnerBulle(List<GestionnaireTutoriel.EtapeTutoriel> etapes, int index, Button cible) {
        GestionnaireTutoriel.EtapeTutoriel etape = etapes.get(index);

        // Force une passe de mise en page synchrone : l'overlay vient d'etre rendu "managed",
        // sa taille doit etre a jour avant qu'on lise getWidth()/getHeight() ci-dessous.
        Parent racine = tutorielOverlay.getScene().getRoot();
        racine.applyCss();
        racine.layout();

        Bounds sceneBounds   = cible.localToScene(cible.getBoundsInLocal());
        Bounds overlayBounds = tutorielOverlay.sceneToLocal(sceneBounds);
        double centreX       = overlayBounds.getMinX() + overlayBounds.getWidth() / 2.0;
        boolean bulleEnBas   = overlayBounds.getMinY() < tutorielOverlay.getHeight() / 2.0;

        double flecheLargeur = 18, flecheHauteur = 14;
        Polygon fleche = new Polygon();
        if (bulleEnBas) {
            fleche.getPoints().addAll(
                    centreX - flecheLargeur / 2, overlayBounds.getMaxY() + flecheHauteur,
                    centreX + flecheLargeur / 2, overlayBounds.getMaxY() + flecheHauteur,
                    centreX, overlayBounds.getMaxY());
        } else {
            fleche.getPoints().addAll(
                    centreX - flecheLargeur / 2, overlayBounds.getMinY() - flecheHauteur,
                    centreX + flecheLargeur / 2, overlayBounds.getMinY() - flecheHauteur,
                    centreX, overlayBounds.getMinY());
        }
        fleche.setFill(Color.web("#f2c14e"));

        Label titre = new Label(etape.libelle());
        titre.getStyleClass().add("item-nom");
        Label texte = new Label(etape.texte());
        texte.getStyleClass().add("item-detail");
        texte.setWrapText(true);
        texte.setMaxWidth(330);

        Button suivant = new Button(index + 1 < etapes.size() ? "Suivant" : "Terminer");
        suivant.getStyleClass().add("menu-bouton");
        // La classe "menu-bouton" fixe une largeur de 220px pensee pour le menu principal
        // (un bouton par ligne) ; ici deux boutons partagent une bulle de 320px, donc on
        // laisse chaque bouton se dimensionner a son propre texte.
        suivant.setPrefWidth(Region.USE_COMPUTED_SIZE);
        suivant.setMinWidth(Region.USE_PREF_SIZE);
        suivant.setOnAction(e -> avancer(etapes, index));

        HBox boutons;
        if (etapes.size() > 1) {
            // Visite guidee (plusieurs etapes en chaine) : on peut l'abandonner en cours de route.
            Button passer = new Button("Passer le tutoriel");
            passer.getStyleClass().add("menu-bouton-danger");
            passer.setPrefWidth(Region.USE_COMPUTED_SIZE);
            passer.setMinWidth(Region.USE_PREF_SIZE);
            passer.setOnAction(e -> abandonnerTutoriel(etapes, index));
            boutons = new HBox(8, passer, suivant);
        } else {
            // Rejeu ponctuel depuis le bouton Aide : rien a "abandonner", juste a fermer.
            boutons = new HBox(8, suivant);
        }
        boutons.setAlignment(Pos.CENTER);

        VBox bulle = new VBox(8, titre, texte, boutons);
        bulle.setAlignment(Pos.CENTER);
        bulle.setPadding(new Insets(14));
        bulle.setStyle("-fx-background-color: #2e2e42; -fx-background-radius: 10;"
                + " -fx-border-color: #f2c14e; -fx-border-radius: 10; -fx-border-width: 2;");
        bulle.setMaxWidth(360);
        bulle.setPrefWidth(360);

        tutorielOverlay.getChildren().addAll(fleche, bulle);

        bulle.applyCss();
        bulle.layout();
        double largeurOverlay = tutorielOverlay.getWidth();
        double hauteurOverlay = tutorielOverlay.getHeight();
        double bulleHauteur   = bulle.prefHeight(360);

        double bulleX = clamp(centreX - 180, 10, Math.max(10, largeurOverlay - 370));
        double bulleY = bulleEnBas
                ? overlayBounds.getMaxY() + flecheHauteur + 4
                : overlayBounds.getMinY() - flecheHauteur - 4 - bulleHauteur;
        bulleY = clamp(bulleY, 10, Math.max(10, hauteurOverlay - bulleHauteur - 10));

        bulle.setLayoutX(bulleX);
        bulle.setLayoutY(bulleY);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private void avancer(List<GestionnaireTutoriel.EtapeTutoriel> etapes, int index) {
        ctx.gestionnaireTutoriel.marquerEtapeVue(etapes.get(index).libelle());
        if (index + 1 < etapes.size()) {
            afficherEtape(etapes, index + 1);
        } else {
            fermerOverlay();
            ctx.sauvegarde.sauvegarder(ctx);
        }
    }

    private void abandonnerTutoriel(List<GestionnaireTutoriel.EtapeTutoriel> etapes, int indexActuel) {
        for (int i = indexActuel; i < etapes.size(); i++) {
            ctx.gestionnaireTutoriel.marquerEtapeVue(etapes.get(i).libelle());
        }
        ctx.gestionnaireTutoriel.setActif(false);
        fermerOverlay();
        ctx.sauvegarde.sauvegarder(ctx);
    }

    private void fermerOverlay() {
        tutorielOverlay.setVisible(false);
        tutorielOverlay.setManaged(false);
        tutorielOverlay.getChildren().clear();
    }

    private void onHistoire(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranHistoire.fxml", c -> ((EcranHistoireController) c).initData(ctx));
    }

    private void onRecrutement(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRecrutement.fxml", c -> ((EcranRecrutementController) c).initData(ctx));
    }

    private void onInventaire(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranInventaire.fxml", c -> ((EcranInventaireController) c).initData(ctx));
    }

    private void onPersonnages(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranPersonnages.fxml", c -> ((EcranPersonnagesController) c).initData(ctx));
    }

    private void onFormation(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranFormation.fxml", c -> ((EcranFormationController) c).initData(ctx));
    }

    private void onQuetes(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranQuetes.fxml", c -> ((EcranQuetesController) c).initData(ctx));
    }

    private void onRecompenses(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRecompenses.fxml", c -> ((EcranRecompensesController) c).initData(ctx));
    }

    private void onRangTitres(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRangTitres.fxml", c -> ((EcranRangTitresController) c).initData(ctx));
    }

    private void onAmeliorations(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranAmeliorations.fxml", c -> ((EcranAmeliorationsController) c).initData(ctx));
    }

    private void onArene(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranArene.fxml", c -> ((EcranAreneController) c).initData(ctx));
    }

    private void onDonjon(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranDonjon.fxml", c -> ((EcranDonjonController) c).initData(ctx));
    }

    private void onBoutique(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranBoutique.fxml", c -> ((EcranBoutiqueController) c).initData(ctx));
    }

    private void onAbilites(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranAbilites.fxml", c -> ((EcranAbilitesController) c).initData(ctx));
    }

    private void onRecrutementRare(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranRecrutementRare.fxml", c -> ((EcranRecrutementRareController) c).initData(ctx));
    }

    private void onChasseTresor(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranChasseTresor.fxml", c -> ((EcranChasseTresorController) c).initData(ctx));
    }

    private void onTirages(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranTirages.fxml", c -> ((EcranTiragesController) c).initData(ctx));
    }

    private void onCompagnons(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranCompagnons.fxml", c -> ((EcranCompagnonsController) c).initData(ctx));
    }

    private void onCreaturesSacrees(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranCreaturesSacrees.fxml", c -> ((EcranCreaturesSacreesController) c).initData(ctx));
    }

    private void onEtoiles(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranEtoiles.fxml", c -> ((EcranEtoilesController) c).initData(ctx));
    }

    private void onExamenS(ActionEvent event) {
        naviguerVers(event, "/fxml/EcranExamenS.fxml", c -> ((EcranExamenSController) c).initData(ctx));
    }

    private void naviguerVers(ActionEvent event, String fxml, Consumer<Object> initialiser) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, fxml);
            initialiser.accept(loader.getController());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Object CARTE_EFFETS = new Object();
    private static final Object CARTE_BIENVENUE = new Object();
    private static final Object CARTE_NOUVEAUX_EFFETS = new Object();

    private static final String[][] EFFETS_NEGATIFS = {
        {"Brulure", "Inflige un pourcentage des PV actuels en degats a chaque tour, pendant plusieurs tours (contourne la defense)."},
        {"Saignement", "Inflige un pourcentage des PV max en degats a chaque tour, pendant plusieurs tours."},
        {"Poison", "Inflige un pourcentage des PV max en degats a chaque tour ; chaque stack supplementaire augmente les degats."},
        {"Gel", "Empeche totalement d'agir pendant plusieurs tours."},
        {"Petrification", "Empeche d'agir et fait recevoir 20% de degats supplementaires pendant sa duree."},
        {"Etourdissement", "Fait passer le prochain tour de la cible."},
        {"Silence", "Empeche l'utilisation de l'attaque speciale pendant plusieurs tours."},
        {"Paralysie", "Empeche d'agir, avec une chance de se liberer a chaque tour."},
        {"Sommeil", "Empeche d'agir jusqu'a la fin de la duree ; se termine immediatement si la cible subit des degats."},
        {"Confusion", "La cible attaque un allie au hasard au lieu de sa cible prevue."},
        {"Aveuglement", "Reduit la precision, augmentant les chances de rater ses attaques."},
        {"Ralentissement", "Reduit le gain de rage par tour."},
        {"Reduction d'attaque", "Reduit l'attaque de base pendant plusieurs tours."},
        {"Reduction de defense", "Reduit la defense pendant plusieurs tours."},
        {"Reduction de vitesse", "Reduit la vitesse pendant plusieurs tours."},
        {"Fragilite", "Augmente les degats recus pendant plusieurs tours (pourcentage et duree variables selon la competence)."},
        {"Malediction", "Reduit les soins recus pendant plusieurs tours."},
        {"Marquage", "Augmente les degats recus de 30% pendant 2 tours (valeurs fixes)."},
        {"Trempe", "Rend plus vulnerable au Gel et a la Paralysie."},
        {"Provocation", "Force la cible a attaquer en priorite l'auteur de la provocation."},
        {"Peur", "La cible evite d'attaquer la source de la peur tant que l'effet dure."},
        {"Marque explosive", "Ne fait aucun degat immediat, puis explose au dernier tour et inflige des degats bases sur les PV max de la cible."},
        {"Symbiose", "Lie deux personnages : une partie des degats et des soins subis par l'un est repercutee sur son partenaire."},
    };

    @FXML
    private void onAide(ActionEvent event) {
        List<Object> disponibles = new ArrayList<>();
        disponibles.add(CARTE_BIENVENUE);
        disponibles.add(CARTE_NOUVEAUX_EFFETS);
        disponibles.add(CARTE_EFFETS);
        for (GestionnaireTutoriel.EtapeTutoriel e : GestionnaireTutoriel.getEtapes()) {
            if (boutonsParLibelle.containsKey(e.libelle())) disponibles.add(e);
        }

        Object choix = GuiVisuels.choisirParmiCartes(
                "Aide — Tutoriels disponibles", disponibles, this::carteAide);
        if (choix == null) return;

        if (choix == CARTE_EFFETS) {
            afficherEffetsNegatifs();
            return;
        }
        if (choix == CARTE_BIENVENUE) {
            info("Conseils de bienvenue", GestionnaireTutoriel.explicationEcran("Bienvenue"));
            return;
        }
        if (choix == CARTE_NOUVEAUX_EFFETS) {
            info("Nouveaux effets de combat", GestionnaireTutoriel.explicationEcran("Effets"));
            return;
        }
        afficherEtape(List.of((GestionnaireTutoriel.EtapeTutoriel) choix), 0);
    }

    private Node carteAide(Object item) {
        String texte;
        if (item == CARTE_BIENVENUE) texte = "Conseils de bienvenue";
        else if (item == CARTE_NOUVEAUX_EFFETS) texte = "Nouveaux effets de combat";
        else if (item == CARTE_EFFETS) texte = "Les differents effets";
        else texte = "Tutoriel " + ((GestionnaireTutoriel.EtapeTutoriel) item).libelle();

        Label titre = new Label(texte);
        titre.getStyleClass().add("item-nom");

        HBox carte = new HBox(titre);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setPadding(new Insets(10, 16, 10, 16));
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(300);
        return carte;
    }

    private void afficherEffetsNegatifs() {
        VBox liste = new VBox(8);
        for (String[] effet : EFFETS_NEGATIFS) {
            Label nom = new Label(effet[0]);
            nom.getStyleClass().add("item-nom");

            Label description = new Label(effet[1]);
            description.getStyleClass().add("item-detail");
            description.setWrapText(true);
            description.setMaxWidth(460);

            VBox ligne = new VBox(2, nom, description);
            ligne.setPadding(new Insets(8, 16, 8, 16));
            ligne.getStyleClass().add("carte-item");
            liste.getChildren().add(ligne);
        }

        ScrollPane scroll = new ScrollPane(liste);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefViewportWidth(520);
        scroll.setPrefViewportHeight(480);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Les differents effets");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().setContent(scroll);
        dialog.showAndWait();
    }

    @FXML
    private void onOptions(ActionEvent event) {
        Label labelMusique = new Label("Musique");
        labelMusique.getStyleClass().add("item-nom");
        Slider sliderMusique = new Slider(0, 100, ParametresAudio.getVolumeMusique() * 100);
        sliderMusique.valueProperty().addListener((obs, ancien, nouveau) ->
                ParametresAudio.setVolumeMusique(nouveau.doubleValue() / 100.0));

        Label labelEffets = new Label("Effets sonores");
        labelEffets.getStyleClass().add("item-nom");
        Slider sliderEffets = new Slider(0, 100, ParametresAudio.getVolumeEffets() * 100);
        sliderEffets.valueProperty().addListener((obs, ancien, nouveau) ->
                ParametresAudio.setVolumeEffets(nouveau.doubleValue() / 100.0));

        ToggleButton muetBouton = new ToggleButton();
        muetBouton.getStyleClass().add("menu-bouton");
        muetBouton.setSelected(ParametresAudio.isMuet());
        muetBouton.setText(muetBouton.isSelected() ? "🔇 Muet" : "🔊 Son actif");
        muetBouton.setOnAction(e -> {
            ParametresAudio.setMuet(muetBouton.isSelected());
            muetBouton.setText(muetBouton.isSelected() ? "🔇 Muet" : "🔊 Son actif");
        });

        VBox contenu = new VBox(12, labelMusique, sliderMusique, labelEffets, sliderEffets, muetBouton);
        contenu.setAlignment(Pos.CENTER);
        contenu.setPadding(new Insets(16));
        contenu.setPrefWidth(300);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Options");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        contenu.getChildren().add(new Separator());
        if (ctx.debugDeverrouille) {
            Button debugBouton = new Button("🛠 Debug");
            debugBouton.getStyleClass().add("menu-bouton");
            debugBouton.setOnAction(e -> {
                dialog.setOnHidden(ev -> onDebug());
                dialog.close();
            });
            contenu.getChildren().add(debugBouton);
        } else {
            TextField champCode = new TextField();
            champCode.setPromptText("Code");
            Button validerCode = new Button("Valider");
            validerCode.getStyleClass().add("menu-bouton");
            validerCode.setOnAction(e -> {
                if ("210404".equals(champCode.getText().trim())) {
                    ctx.debugDeverrouille = true;
                    ctx.sauvegarde.sauvegarder(ctx);
                    dialog.close();
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "Mode debug deverrouille !");
                    info.setTitle("Debug");
                    info.setHeaderText(null);
                    styliser(info);
                    info.showAndWait();
                    Platform.runLater(this::onDebug);
                } else {
                    champCode.clear();
                }
            });
            HBox ligneCode = new HBox(8, champCode, validerCode);
            ligneCode.setAlignment(Pos.CENTER);
            contenu.getChildren().add(ligneCode);
        }

        dialog.getDialogPane().setContent(contenu);
        dialog.showAndWait();
    }

    /**
     * Bascule vers l'ecran de debug (menu plein comme les autres, pas une fenetre a part) :
     * accessible uniquement une fois deverrouille via le code secret dans Options.
     */
    private void onDebug() {
        try {
            Stage stage = (Stage) boutonsBox.getScene().getWindow();
            FXMLLoader loader = Navigation.changerEcran(stage, "/fxml/EcranDebug.fxml");
            ((EcranDebugController) loader.getController()).initData(ctx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onSauvegarder(ActionEvent event) {
        ctx.sauvegarde.sauvegarder(ctx);
        System.out.println("Partie sauvegardee.");
    }

    @FXML
    private void onQuitter(ActionEvent event) {
        ButtonType sauvegarderEtQuitter = new ButtonType("Sauvegarder et quitter");
        ButtonType quitterSansSauver    = new ButtonType("Quitter sans sauvegarder");
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous sauvegarder votre partie avant de quitter ?",
                sauvegarderEtQuitter, quitterSansSauver, ButtonType.CANCEL);
        confirm.setTitle("Quitter");
        confirm.setHeaderText(null);
        styliser(confirm);

        ButtonType choix = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (choix == ButtonType.CANCEL) return;
        if (choix == sauvegarderEtQuitter) {
            // Synchrone ici : on veut etre certain que la sauvegarde soit partie avant que
            // la fenetre (et potentiellement le processus) ne se ferme juste apres.
            ctx.sauvegarde.sauvegarderSynchrone(ctx);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void styliser(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root-menu");
    }

    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        styliser(alert);
        alert.showAndWait();
    }
}
