package lancement.Gestionnaires;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Equipement.Inventaire;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import lancement.Chapitres.Chapitre;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.ChapitreElite.ChapitreElite;
import lancement.Formation;
import lancement.GameContext;
import lancement.RangJoueur;
import lancement.SauvegardeData;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GestionnaireSauvegarde {

    private static final String URL_FIREBASE =
            "https://rpgjava-d89d3-default-rtdb.europe-west1.firebasedatabase.app/";
    private final Gson       gson   = new GsonBuilder().setPrettyPrinting().create();
    private final HttpClient client = HttpClient.newHttpClient();

    // ── Vérifie si une sauvegarde existe ─────────────────────────────────
    public boolean sauvegardeExiste(String nomJoueur) {
        String url = URL_FIREBASE + "joueurs/" + securiser(nomJoueur) + ".json";
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200 && !res.body().trim().equals("null");
        } catch (Exception e) {
            System.out.println("Erreur verification Firebase : " + e.getMessage());
            return false;
        }
    }

    // ── Sauvegarde via GameContext ────────────────────────────────────────
    private SauvegardeData construireDonnees(GameContext ctx) {
        SauvegardeData data = new SauvegardeData();

        SauvegardeJoueur.sauvegarder(ctx, data);
        SauvegardeChapitres.sauvegarder(ctx, data);

        // Parchemins
        data.parcheminC = ctx.menuRecrutement.getParcheminC();
        data.parcheminB = ctx.menuRecrutement.getParcheminB();
        data.parcheminA = ctx.menuRecrutement.getParcheminA();

        // Tirages
        if (ctx.menuTirage != null) {
            data.parcheminTirageOrdinaire    = ctx.menuTirage.getParcheminOrdinaire();
            data.parcheminTirageElite        = ctx.menuTirage.getParcheminElite();
            data.tirageEliteCompteurPityA    = ctx.menuTirage.getCompteurPityA();
            data.tirageEliteCompteurSansS    = ctx.menuTirage.getCompteurPityS();
            data.tirageEliteCompteurSansSS   = ctx.menuTirage.getCompteurPitySS();
        }

        // Inventaire
        SauvegardeInventaire.sauvegarder(ctx, data);

        // Quêtes
        SauvegardeQuetes.sauvegarder(ctx, data);

        // Énergie, donjon, chasse au tresor, examen de rang S
        SauvegardeActivites.sauvegarder(ctx, data);

        // Etoiles/coffres, compagnons, recompenses, creature sacree, rang & titres, tutoriel
        SauvegardeProgression.sauvegarder(ctx, data);

        // Coupons
        data.coupons = ctx.coupons;

        return data;
    }

    /**
     * Sauvegarde asynchrone (par defaut) : l'envoi Firebase se fait en arriere-plan pour ne pas
     * geler l'interface a chaque action. A utiliser partout SAUF juste avant de fermer l'appli
     * (voir {@link #sauvegarderSynchrone}), ou l'attente de la reponse n'est pas garantie.
     */
    public void sauvegarder(GameContext ctx) {
        envoyerFirebase(ctx.joueur, construireDonnees(ctx), false);
    }

    /**
     * Sauvegarde bloquante : attend la reponse Firebase avant de continuer. Reservee aux moments
     * ou l'application risque de se fermer juste apres (bouton Quitter), pour garantir que la
     * sauvegarde parte reellement avant que le processus ne s'arrete.
     */
    public void sauvegarderSynchrone(GameContext ctx) {
        envoyerFirebase(ctx.joueur, construireDonnees(ctx), true);
    }

    private void envoyerFirebase(Personnage_principale joueur, SauvegardeData data, boolean synchrone) {
        String url     = URL_FIREBASE + "joueurs/" + securiser(joueur.getNom()) + ".json";
        String payload = gson.toJson(data);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        if (synchrone) {
            try {
                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                logResultatSauvegarde(joueur, res.statusCode(), res.body());
            } catch (Exception e) {
                System.out.println("Impossible de se connecter a Firebase : " + e.getMessage());
            }
        } else {
            client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(res -> logResultatSauvegarde(joueur, res.statusCode(), res.body()))
                    .exceptionally(e -> {
                        System.out.println("Impossible de se connecter a Firebase : " + e.getMessage());
                        return null;
                    });
        }
    }

    private void logResultatSauvegarde(Personnage_principale joueur, int statusCode, String body) {
        if (statusCode == 200)
            System.out.println("Partie de " + joueur.getNom() + " sauvegardee dans le Cloud !");
        else
            System.out.println("Erreur Firebase (Code " + statusCode + ") : " + body);
    }

    // ── Chargement ────────────────────────────────────────────────────────
    public SauvegardeData charger(String nomJoueur) {
        String url = URL_FIREBASE + "joueurs/" + securiser(nomJoueur) + ".json";
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200 && !res.body().trim().equals("null"))
                return gson.fromJson(res.body(), SauvegardeData.class);
            System.out.println("Joueur introuvable sur Firebase.");
            return null;
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement Cloud : " + e.getMessage());
            return null;
        }
        // NOTE : ctx.dernierCoffreArene est restauré dans restaurerJoueur()
    }

    // ── Restauration ──────────────────────────────────────────────────────
    /**
     * Restaure le joueur ET injecte le GameContext pour que le multiplicateur
     * de rang soit lu dynamiquement dans getAttaque/getDefense/getVieMax/getVitesse.
     * Restaure aussi le coffre arène.
     */
    public Personnage_principale restaurerJoueur(SauvegardeData data, GameContext ctx) {
        return SauvegardeJoueur.restaurerJoueur(data, ctx);
    }

    /**
     * Surcharge sans ctx pour compatibilité ascendante si appelée ailleurs.
     * Le setGameContext devra être fait manuellement dans ce cas.
     */
    public Personnage_principale restaurerJoueur(SauvegardeData data) {
        return SauvegardeJoueur.restaurerJoueur(data, null);
    }

    public ArrayList<PersonnageBase> restaurerPersonnagesRecruites(SauvegardeData data) {
        return SauvegardeJoueur.restaurerPersonnagesRecruites(data);
    }

    public void restaurerFormation(Formation formation,
                                   SauvegardeData data,
                                   ArrayList<PersonnageBase> personnagesRecruites) {
        SauvegardeJoueur.restaurerFormation(formation, data, personnagesRecruites);
    }

    /** Restaure les 13 chapitres normaux depuis les maps generiques (cle = "c" + numero de chapitre). */
    public void restaurerChapitres(java.util.List<Chapitre> chapitres,
                                    java.util.Map<String, boolean[]> debloquesMap,
                                    java.util.Map<String, boolean[]> reussisMap) {
        SauvegardeChapitres.restaurerChapitres(chapitres, debloquesMap, reussisMap);
    }

    /** Restaure les 13 chapitres elite depuis les maps generiques (cle = "c" + numero de chapitre). */
    public void restaurerChapitresElite(java.util.List<ChapitreElite> chapitresElite,
                                         java.util.Map<String, boolean[]> debloquesMap,
                                         java.util.Map<String, boolean[]> reussisMap) {
        SauvegardeChapitres.restaurerChapitresElite(chapitresElite, debloquesMap, reussisMap);
    }

    /** Specifique au Chapitre 3 Elite : restaure le suivi de premiere victoire (drop de fragments). */
    public void restaurerChapitre3ElitePremiereVictoire(Chapitre3Elite c, SauvegardeData data) {
        SauvegardeChapitres.restaurerChapitre3ElitePremiereVictoire(c, data);
    }

    public void restaurerInventaire(Inventaire inventaire, SauvegardeData data) {
        SauvegardeInventaire.restaurer(inventaire, data);
    }

    public void restaurerQuetes(lancement.GameContext ctx, SauvegardeData data) {
        SauvegardeQuetes.restaurer(ctx, data);
    }

    /**
     * Restaure l'etat du tutoriel guide. Cas particulier de compatibilite retroactive :
     * une sauvegarde qui a deja de la progression (niveau &gt; 1 ou stage 1 deja reussi) mais
     * dont le champ tutorielPropose est absent/false vient forcement d'avant l'ajout de cette
     * fonctionnalite -> on ne propose jamais le tutoriel a un joueur qui a deja avance dans le jeu.
     */
    public void restaurerTutoriel(lancement.GameContext ctx, SauvegardeData data) {
        SauvegardeProgression.restaurerTutoriel(ctx, data);
    }

    public void restaurerEnergie(GestionnaireEnergie ge, SauvegardeData data) {
        SauvegardeActivites.restaurerEnergie(ge, data);
    }

    public void restaurerRangEtTitres(RangJoueur rangJoueur,
                                       GestionnaireTitres gestionnaireTitres,
                                       SauvegardeData data) {
        SauvegardeProgression.restaurerRangEtTitres(rangJoueur, gestionnaireTitres, data);
    }

    public void restaurerEtoiles(GestionnaireEtoiles ge, SauvegardeData data) {
        SauvegardeProgression.restaurerEtoiles(ge, data);
    }

    public void restaurerCompagnons(GestionnaireCompagnons gc, SauvegardeData data) {
        SauvegardeProgression.restaurerCompagnons(gc, data);
    }

    public void restaurerCreaturesSacrees(Gestionnaire_pet gcs, SauvegardeData data) {
        SauvegardeProgression.restaurerCreaturesSacrees(gcs, data);
    }

    public void restaurerRecompenses(GestionnaireRecompenses gr, SauvegardeData data) {
        SauvegardeProgression.restaurerRecompenses(gr, data);
    }

    public void restaurerDonjon(GestionnaireDonjon gd, SauvegardeData data) {
        SauvegardeActivites.restaurerDonjon(gd, data);
    }

    public void restaurerChasseTresor(GestionnaireChasseTresor gc, SauvegardeData data) {
        SauvegardeActivites.restaurerChasseTresor(gc, data);
    }

    public void restaurerExamenS(GestionnaireExamenS ge, SauvegardeData data) {
        SauvegardeActivites.restaurerExamenS(ge, data);
    }

    // ── Utilitaires privés ────────────────────────────────────────────────
    private String securiser(String nom) {
        return nom.trim().toLowerCase().replace(" ", "_");
    }

    public PersonnageBase creerPersonnageParNom(String nom) {
        return SauvegardeJoueur.creerPersonnageParNom(nom);
    }
}