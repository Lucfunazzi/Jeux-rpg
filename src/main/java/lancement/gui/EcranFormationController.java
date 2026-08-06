package lancement.gui;

import Joueur.ArbreCompetences;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lancement.Formation;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireLiens;
import lancement.Menus.MenuAbilite;

public class EcranFormationController {

    private GameContext ctx;

    @FXML private HBox ligneTankBox;
    @FXML private HBox ligneDpsBox;
    @FXML private HBox ligneSupportBox;
    @FXML private VBox competenceBox;
    @FXML private FlowPane liensBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        GuiVisuels.afficherExplicationPremiereVisite(ctx, "Formation", "Formation");
        rafraichir();
    }

    private void rafraichir() {
        Formation f = ctx.formation;

        ligneTankBox.getChildren().setAll(
                f.getTank() != null ? creerCarte(f.getTank()) : creerCarteVide("Tank"));

        List<Node> dps = new ArrayList<>();
        dps.add(creerCarte(ctx.joueur));
        for (PersonnageBase a : f.getAttaquants()) dps.add(creerCarte(a));
        ligneDpsBox.getChildren().setAll(dps);

        if (f.getSupports().isEmpty()) {
            ligneSupportBox.getChildren().setAll(creerCarteVide("Support"));
        } else {
            List<Node> supports = new ArrayList<>();
            for (PersonnageBase s : f.getSupports()) supports.add(creerCarte(s));
            ligneSupportBox.getChildren().setAll(supports);
        }

        Label detailSpeciale = new Label(GuiVisuels.capturerDescription(ctx.joueur::descriptionAttaqueSpeciale));
        detailSpeciale.getStyleClass().add("item-detail");
        detailSpeciale.setWrapText(true);
        detailSpeciale.setMaxWidth(320);

        Label detailUltime = new Label(GuiVisuels.capturerDescription(ctx.joueur::descriptionAttaqueUltime));
        detailUltime.getStyleClass().add("item-detail");
        detailUltime.setWrapText(true);
        detailUltime.setMaxWidth(320);

        competenceBox.getChildren().setAll(
                GuiVisuels.creerFicheStat("✦", "Compétence spéciale active", nomCompetenceActive()),
                detailSpeciale,
                GuiVisuels.creerFicheStat("★", "Attaque ultime active", nomUltimeActive()),
                detailUltime);

        List<GestionnaireLiens.Lien> liens = f.getLiensActifs();
        if (liens.isEmpty()) {
            liensBox.getChildren().clear();
        } else {
            List<Node> chips = new ArrayList<>();
            for (GestionnaireLiens.Lien l : liens) chips.add(carteLien(l));
            liensBox.getChildren().setAll(chips);
        }
    }

    private Node carteLien(GestionnaireLiens.Lien l) {
        Label nom = new Label(l.nom);
        nom.getStyleClass().add("item-nom");
        Label detail = new Label(l.description);
        detail.getStyleClass().add("item-detail");
        detail.setWrapText(true);
        detail.setMaxWidth(200);

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item-joueur");
        carte.setPrefWidth(220);
        return carte;
    }

    @FXML
    private void onAjouter(ActionEvent event) {
        if (ctx.formation.estPleine()) { info("Formation", "La formation est pleine (5/5) !"); return; }

        List<PersonnageBase> disponibles = new ArrayList<>();
        disponibles.add(ctx.joueur);
        disponibles.addAll(ctx.personnagesRecruites);

        List<PersonnageBase> equipe = ctx.formation.getEquipe();
        List<PersonnageBase> ajoutables = new ArrayList<>();
        for (PersonnageBase p : disponibles) if (!equipe.contains(p)) ajoutables.add(p);

        if (ajoutables.isEmpty()) { info("Formation", "Aucun personnage disponible a ajouter."); return; }

        PersonnageBase choisi = choisirPersonnage("Ajouter un personnage", ajoutables);
        if (choisi == null) return;

        info("Formation", ctx.formation.ajouterPersonnage(choisi));
        rafraichir();
    }

    @FXML
    private void onRetirer(ActionEvent event) {
        List<PersonnageBase> equipe = ctx.formation.getEquipe();
        List<PersonnageBase> retirables = new ArrayList<>(equipe);
        retirables.remove(0); // index 0 = joueur, toujours en premier, non retirable

        if (retirables.isEmpty()) { info("Formation", "Aucun personnage a retirer (vous etes seul dans l'equipe)."); return; }

        PersonnageBase choisi = choisirPersonnage("Retirer un personnage", retirables);
        if (choisi == null) return;

        info("Formation", ctx.formation.retirerPersonnage(choisi));
        rafraichir();
    }

    /** Numeros des arbres qui debloquent une speciale alternative (4 et 8 sont des arbres d'ultime). */
    private static final int[] ARBRES_SPECIALE = {1, 2, 3, 5, 6, 7};
    /** Numeros des arbres qui debloquent un ultime alternatif. */
    private static final int[] ARBRES_ULTIME = {4, 8};

    /** Texte de description d'une competence/ultime alternative pour un numero d'arbre donne
     *  (1/2/3/5/6/7 = specials, 4/8 = ultimes), capture depuis les methodes description*()
     *  existantes (qui impriment sur la console) via GuiVisuels.capturerDescription. */
    private String descriptionArbre(int numArbre) {
        var comp = ctx.joueur.getCompetencesChoisie();
        if (comp == null) return "";
        return GuiVisuels.capturerDescription(() -> {
            switch (numArbre) {
                case 1 -> comp.descriptionCompetenceArbre();
                case 2 -> comp.descriptionCompetenceArbre2();
                case 3 -> comp.descriptionCompetenceArbre3();
                case 5 -> comp.descriptionCompetenceArbre5();
                case 6 -> comp.descriptionCompetenceArbre6();
                case 7 -> comp.descriptionCompetenceArbre7();
                case 4 -> comp.descriptionUltimeArbre4();
                case 8 -> comp.descriptionUltimeArbre8();
                default -> {}
            }
        });
    }

    @FXML
    private void onChangerCompetence(ActionEvent event) {
        Personnage_principale joueur = ctx.joueur;
        ArbreCompetences arbre = joueur.getArbreCompetences();
        String classe = joueur.getChoixClasses();
        int actuelle = joueur.getCompetenceSpecialeActive();

        String nomOriginal = joueur.getCompetencesChoisie() != null
                ? joueur.getCompetencesChoisie().getNomsCompetences()[joueur.getChoixComp() - 1]
                : "Competence originale";

        // Les competences alternatives non debloquees restent masquees (pas de spoil) : seule
        // l'option originale et les arbres deja completes apparaissent dans la liste.
        List<Integer> options = new ArrayList<>();
        options.add(0);
        for (int numArbre : ARBRES_SPECIALE) if (arbre.getNoeud(numArbre, 10).isDebloque()) options.add(numArbre);

        Integer choix = GuiVisuels.choisirParmiCartes("Compétence spéciale", options,
                numArbre -> carteCompetenceChoix(numArbre, nomOriginal,
                        n -> MenuAbilite.getNomCompetence(classe, n), actuelle));
        if (choix == null) return;

        if (choix == 0) {
            joueur.setCompetenceSpecialeActive(0);
            info("Competence", "Competence active : " + nomOriginal);
        } else {
            joueur.setCompetenceSpecialeActive(choix);
            info("Competence", "Competence active : " + MenuAbilite.getNomCompetence(classe, choix));
        }
        rafraichir();
    }

    @FXML
    private void onChangerUltime(ActionEvent event) {
        Personnage_principale joueur = ctx.joueur;
        ArbreCompetences arbre = joueur.getArbreCompetences();
        String classe = joueur.getChoixClasses();
        int actuelle = joueur.getCompetenceUltimeActive();

        String nomOriginal = joueur.getCompetencesChoisie() != null
                ? joueur.getCompetencesChoisie().getNomsCompetences()[1]
                : "Ultime originale";

        List<Integer> options = new ArrayList<>();
        options.add(0);
        for (int numArbre : ARBRES_ULTIME) if (arbre.getNoeud(numArbre, 10).isDebloque()) options.add(numArbre);

        Integer choix = GuiVisuels.choisirParmiCartes("Attaque ultime", options,
                numArbre -> carteCompetenceChoix(numArbre, nomOriginal,
                        n -> Personnage_principale.getNomUltimeArbre(classe, n), actuelle));
        if (choix == null) return;

        if (choix == 0) {
            joueur.setCompetenceUltimeActive(0);
            info("Ultime", "Ultime active : " + nomOriginal);
        } else {
            joueur.setCompetenceUltimeActive(choix);
            info("Ultime", "Ultime active : " + Personnage_principale.getNomUltimeArbre(classe, choix));
        }
        rafraichir();
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

    // ── Utilitaires ───────────────────────────────────────────────────────

    /** Petite carte visuelle (rareté + PV) pour un slot de formation occupé. */
    private Node creerCarte(PersonnageBase p) {
        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("texte");
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        HBox entete = new HBox(6, badge, nom);
        entete.setAlignment(Pos.CENTER);

        Label role = new Label(iconeRole(p.getRole()) + " " + p.getRole());
        role.setStyle("-fx-font-size: 11px; -fx-text-fill: #9a9ac0;");

        VBox carte = new VBox(4, entete, role, GuiVisuels.creerBarrePV(110, 10, p.getVie(), p.getVieMax()));
        carte.getStyleClass().add("carte-combat");
        carte.setAlignment(Pos.CENTER);
        carte.setPrefWidth(130);
        return carte;
    }

    /** Carte grisée pour un slot de formation vide (ex: Tank non assigné). */
    private Node creerCarteVide(String role) {
        Label label = new Label(role + "\n[vide]");
        label.getStyleClass().add("texte");
        label.setStyle("-fx-opacity: 0.5; -fx-text-alignment: center;");
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox carte = new VBox(label);
        carte.getStyleClass().add("carte-combat");
        carte.setAlignment(Pos.CENTER);
        carte.setPrefWidth(130);
        carte.setPrefHeight(74);
        return carte;
    }

    private String iconeRole(String role) {
        return switch (role) {
            case "Tank"    -> "⛨";
            case "Support" -> "✚";
            default        -> "⚔"; // DPS
        };
    }

    private String nomCompetenceActive() {
        Personnage_principale joueur = ctx.joueur;
        int actuelle = joueur.getCompetenceSpecialeActive();
        if (actuelle == 0) {
            return joueur.getCompetencesChoisie() != null
                    ? joueur.getCompetencesChoisie().getNomsCompetences()[joueur.getChoixComp() - 1]
                    : "Aucune";
        }
        return MenuAbilite.getNomCompetence(joueur.getChoixClasses(), actuelle);
    }

    private String nomUltimeActive() {
        Personnage_principale joueur = ctx.joueur;
        int actuelle = joueur.getCompetenceUltimeActive();
        if (actuelle == 0) {
            return joueur.getCompetencesChoisie() != null
                    ? joueur.getCompetencesChoisie().getNomsCompetences()[1]
                    : "Aucune";
        }
        return Personnage_principale.getNomUltimeArbre(joueur.getChoixClasses(), actuelle);
    }

    /**
     * Carte d'un choix de competence/ultime dans le selecteur (uniquement les options
     * debloquees y figurent deja, voir onChangerCompetence/onChangerUltime).
     * @param numArbre 0 = option originale, sinon numero de l'arbre (1-8)
     * @param nomDe    resout le nom affiche pour un numero d'arbre donne
     */
    private Node carteCompetenceChoix(int numArbre, String nomOriginal,
            java.util.function.IntFunction<String> nomDe, int actuelle) {
        String nom;
        String statut;
        String description;
        if (numArbre == 0) {
            nom = nomOriginal + " (originale)";
            statut = actuelle == 0 ? "ACTIVE" : "";
            description = "";
        } else {
            nom = nomDe.apply(numArbre) + " (Arbre " + numArbre + ")";
            statut = actuelle == numArbre ? "ACTIVE" : "";
            description = descriptionArbre(numArbre);
        }

        Label nomLabel = new Label(nom);
        nomLabel.getStyleClass().add("item-nom");

        VBox texte = new VBox(2, nomLabel);
        if (!description.isEmpty()) {
            Label descLabel = new Label(description);
            descLabel.getStyleClass().add("item-detail");
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(320);
            texte.getChildren().add(descLabel);
        }

        HBox carte = new HBox(10, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("ACTIVE".equals(statut) ? "carte-item-joueur" : "carte-item");
        carte.setPrefWidth(340);

        if (!statut.isEmpty()) {
            Label statutLabel = new Label(statut);
            statutLabel.getStyleClass().add("item-qte");
            carte.getChildren().add(statutLabel);
        }
        return carte;
    }

    private PersonnageBase choisirPersonnage(String titre, List<PersonnageBase> options) {
        return GuiVisuels.choisirParmiCartes(titre, options, this::cartePersonnageChoix);
    }

    private Node cartePersonnageChoix(PersonnageBase p) {
        Label badge = GuiVisuels.creerBadgeRarete(p.getRarete());
        Label nom = new Label(p.getNom());
        nom.getStyleClass().add("item-nom");
        Label detail = new Label("Niv. " + p.getNiveau() + "  ·  " + iconeRole(p.getRole()) + " " + p.getRole());
        detail.getStyleClass().add("item-detail");

        VBox texte = new VBox(2, nom, detail);
        HBox carte = new HBox(10, badge, texte);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.getStyleClass().add("carte-item");
        carte.setPrefWidth(260);
        return carte;
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
