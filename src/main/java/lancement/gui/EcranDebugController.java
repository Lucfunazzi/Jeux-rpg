package lancement.gui;

import Personnage.PersonnageBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireChasseTresor;

/**
 * Menu de debug reserve aux tests : accessible uniquement une fois deverrouille via le code
 * secret dans Options. Permet de monter de niveau, modifier les stats et ajouter des ressources
 * sur le personnage choisi, sans passer par la progression normale du jeu.
 */
public class EcranDebugController {

    private GameContext ctx;

    @FXML private VBox contenuBox;

    public void initData(GameContext ctx) {
        this.ctx = ctx;
        construire();
    }

    private void construire() {
        ArrayList<PersonnageBase> personnages = new ArrayList<>();
        personnages.add(ctx.joueur);
        personnages.addAll(ctx.personnagesRecruites);

        ComboBox<PersonnageBase> comboPerso = new ComboBox<>();
        comboPerso.getItems().addAll(personnages);
        comboPerso.setConverter(new StringConverter<PersonnageBase>() {
            @Override public String toString(PersonnageBase p) {
                return p == null ? "" : p.getNom() + " (Niv. " + p.getNiveau() + ")";
            }
            @Override public PersonnageBase fromString(String s) { return null; }
        });
        comboPerso.getSelectionModel().selectFirst();
        comboPerso.setPrefWidth(300);

        Label labelNiveau = new Label();
        Runnable rafraichirNiveau = () -> {
            PersonnageBase p = comboPerso.getValue();
            labelNiveau.setText(p == null ? "" : "Niveau actuel : " + p.getNiveau());
        };
        rafraichirNiveau.run();
        comboPerso.setOnAction(e -> rafraichirNiveau.run());

        TextField champNiveaux = new TextField("1");
        champNiveaux.setPrefWidth(60);
        Button boutonMonter = new Button("Monter de niveau(x)");
        boutonMonter.getStyleClass().add("menu-bouton");
        boutonMonter.setOnAction(e -> {
            PersonnageBase p = comboPerso.getValue();
            if (p == null) return;
            int n = parseEntierPositif(champNiveaux.getText(), 1);
            for (int i = 0; i < n; i++) p.monterDeNiveauSilencieux();
            rafraichirNiveau.run();
        });
        HBox ligneNiveau = new HBox(8, champNiveaux, boutonMonter);
        ligneNiveau.setAlignment(Pos.CENTER);

        TextField champVie     = new TextField();
        TextField champAttaque = new TextField();
        TextField champDefense = new TextField();
        TextField champVitesse = new TextField();
        champVie.setPromptText("PV max");
        champAttaque.setPromptText("Attaque");
        champDefense.setPromptText("Defense");
        champVitesse.setPromptText("Vitesse");
        Button boutonAppliquerStats = new Button("Appliquer les stats");
        boutonAppliquerStats.getStyleClass().add("menu-bouton");
        boutonAppliquerStats.setOnAction(e -> {
            PersonnageBase p = comboPerso.getValue();
            if (p == null) return;
            if (!champVie.getText().isBlank()) {
                double v = parseDoublePositif(champVie.getText(), p.getVieMax());
                p.setVieMax(v);
                p.setVie(v);
            }
            if (!champAttaque.getText().isBlank())
                p.setAttaque(parseDoublePositif(champAttaque.getText(), p.getAttaque()));
            if (!champDefense.getText().isBlank())
                p.setDefense(parseDoublePositif(champDefense.getText(), p.getDefense()));
            if (!champVitesse.getText().isBlank())
                p.setVitesse(parseDoublePositif(champVitesse.getText(), p.getVitesse()));
        });

        TextField champOr = new TextField();
        champOr.setPromptText("Montant d'or");
        Button boutonOr = new Button("Ajouter");
        boutonOr.getStyleClass().add("menu-bouton");
        boutonOr.setOnAction(e -> ctx.joueur.ajouterOr(parseEntierPositif(champOr.getText(), 0)));
        HBox ligneOr = new HBox(8, champOr, boutonOr);
        ligneOr.setAlignment(Pos.CENTER);

        Map<String, Runnable> ajoutsParchemin = new LinkedHashMap<>();
        ComboBox<String> comboParchemin = new ComboBox<>();
        TextField champParcheminQte = new TextField("1");
        champParcheminQte.setPrefWidth(60);
        ajoutsParchemin.put("Parchemin C (recrutement)",
                () -> ctx.menuRecrutement.ajouterParcheminC(parseEntierPositif(champParcheminQte.getText(), 1)));
        ajoutsParchemin.put("Parchemin B (recrutement)",
                () -> ctx.menuRecrutement.ajouterParcheminB(parseEntierPositif(champParcheminQte.getText(), 1)));
        ajoutsParchemin.put("Parchemin A (recrutement)",
                () -> ctx.menuRecrutement.ajouterParcheminA(parseEntierPositif(champParcheminQte.getText(), 1)));
        ajoutsParchemin.put("Parchemin S (recrutement)",
                () -> ctx.menuRecrutement.ajouterParcheminS(parseEntierPositif(champParcheminQte.getText(), 1)));
        ajoutsParchemin.put("Parchemin de Chasse A (recrutement rare)",
                () -> ctx.inventaire.ajouterMateriau(GestionnaireChasseTresor.PARCHEMIN_A,
                        parseEntierPositif(champParcheminQte.getText(), 1)));
        ajoutsParchemin.put("Parchemin de Chasse S (recrutement rare)",
                () -> ctx.inventaire.ajouterMateriau(GestionnaireChasseTresor.PARCHEMIN_S,
                        parseEntierPositif(champParcheminQte.getText(), 1)));
        ajoutsParchemin.put("Parchemin de Chasse SS (recrutement rare)",
                () -> ctx.inventaire.ajouterMateriau(GestionnaireChasseTresor.PARCHEMIN_SS,
                        parseEntierPositif(champParcheminQte.getText(), 1)));
        comboParchemin.getItems().addAll(ajoutsParchemin.keySet());
        comboParchemin.getSelectionModel().selectFirst();
        comboParchemin.setPrefWidth(280);

        TextField champMateriauNom = new TextField();
        champMateriauNom.setPromptText("Ou autre materiau (nom libre)");

        Button boutonMateriau = new Button("Ajouter");
        boutonMateriau.getStyleClass().add("menu-bouton");
        boutonMateriau.setOnAction(e -> {
            String nomLibre = champMateriauNom.getText().trim();
            if (!nomLibre.isEmpty()) {
                ctx.inventaire.ajouterMateriau(nomLibre, parseEntierPositif(champParcheminQte.getText(), 1));
            } else {
                Runnable action = ajoutsParchemin.get(comboParchemin.getValue());
                if (action != null) action.run();
            }
        });
        HBox ligneParchemin = new HBox(8, comboParchemin, champParcheminQte);
        ligneParchemin.setAlignment(Pos.CENTER);
        VBox blocMateriau = new VBox(6, ligneParchemin, champMateriauNom, boutonMateriau);
        blocMateriau.setAlignment(Pos.CENTER);

        TextField champCoupons = new TextField();
        champCoupons.setPromptText("Coupons");
        Button boutonCoupons = new Button("Ajouter");
        boutonCoupons.getStyleClass().add("menu-bouton");
        boutonCoupons.setOnAction(e ->
                ctx.joueur.setCoupons(ctx.joueur.getCoupons() + parseEntierPositif(champCoupons.getText(), 0)));
        HBox ligneCoupons = new HBox(8, champCoupons, boutonCoupons);
        ligneCoupons.setAlignment(Pos.CENTER);

        Button boutonSauvegarder = new Button("Sauvegarder");
        boutonSauvegarder.getStyleClass().add("menu-bouton");
        boutonSauvegarder.setOnAction(e -> ctx.sauvegarde.sauvegarder(ctx));

        VBox contenu = new VBox(10,
                new Label("Personnage"), comboPerso, labelNiveau,
                new Separator(),
                new Label("Niveaux a ajouter"), ligneNiveau,
                new Separator(),
                new Label("Stats (laisser vide = inchange)"),
                champVie, champAttaque, champDefense, champVitesse, boutonAppliquerStats,
                new Separator(),
                new Label("Or"), ligneOr,
                new Label("Parchemins / Materiau"), blocMateriau,
                new Label("Coupons"), ligneCoupons,
                new Separator(),
                boutonSauvegarder
        );
        contenu.setAlignment(Pos.CENTER);
        contenu.setMaxWidth(380);

        contenuBox.getChildren().setAll(contenu);
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

    private static int parseEntierPositif(String texte, int defaut) {
        try {
            int v = Integer.parseInt(texte.trim());
            return Math.max(v, 0);
        } catch (NumberFormatException e) {
            return defaut;
        }
    }

    private static double parseDoublePositif(String texte, double defaut) {
        try {
            double v = Double.parseDouble(texte.trim());
            return Math.max(v, 0);
        } catch (NumberFormatException e) {
            return defaut;
        }
    }
}
