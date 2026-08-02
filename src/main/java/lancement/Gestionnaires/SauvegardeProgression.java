package lancement.Gestionnaires;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import lancement.EtoilesStage;
import lancement.GameContext;
import lancement.RangJoueur;
import lancement.SauvegardeData;
import lancement.Titre;

/**
 * Sauvegarde/restauration des systemes de progression : etoiles/coffres, compagnons,
 * creature sacree, recompenses de connexion, rang &amp; titres, tutoriel guide.
 */
public class SauvegardeProgression {

    public static void sauvegarder(GameContext ctx, SauvegardeData data) {
        for (Map.Entry<String, EtoilesStage> entry : ctx.gestionnaireEtoiles.getEtoilesMap().entrySet()) {
            EtoilesStage e = entry.getValue();
            data.etoiles.add(new SauvegardeData.EtoileData(
                    entry.getKey(), e.isEtoile1(), e.isEtoile2(), e.isEtoile3()));
        }
        for (Map.Entry<String, Boolean> entry : ctx.gestionnaireEtoiles.getCoffresClaimes().entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) data.coffresClaimes.add(entry.getKey());
        }

        if (ctx.gestionnaireCompagnons != null) {
            data.compagnonsType   = ctx.gestionnaireCompagnons.getType().name();
            data.compagnonsNiveau = ctx.gestionnaireCompagnons.getNiveau();
        }

        if (ctx.gestionnaireRecompenses != null) {
            GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
            data.recompensesNiveauReclame           = gr.getNiveauReclame();
            data.recompensesJoursCumulesMois         = gr.getJoursCumulesMois();
            data.recompensesDernierJourMois          = gr.getDernierJourComptePointage() != null
                    ? gr.getDernierJourComptePointage().toString() : null;
            data.recompensesMoisCompte               = gr.getMoisComptePointage();
            data.recompensesMoisReclame              = gr.getMoisReclame();
            data.recompensesJourConnexion            = gr.getJourConnexion();
            data.recompensesDernierJourConnexion     = gr.getDernierJourConnexion() != null
                    ? gr.getDernierJourConnexion().toString() : null;
            data.recompensesJourReclame              = gr.getJourReclame();
            data.recompensesTerminee                 = gr.estTerminee();
            data.recompensesDerniereReclamation30min = gr.getDerniereReclamation30min() != null
                    ? gr.getDerniereReclamation30min().toString() : null;
            data.recompensesPointsMois               = gr.getPointsMois();
        }

        if (ctx.gestionnaireCreaturesSacrees != null) {
            Gestionnaire_pet gcs = ctx.gestionnaireCreaturesSacrees;
            data.creatureType              = gcs.getType().name();
            data.creatureNiveau            = gcs.getNiveau();
            data.creatureExperience        = gcs.getExperience();
            data.creatureOeufDebloque      = gcs.isOeufDebloque();
            data.creatureEntrainement      = gcs.getEntrainementActif() != null
                    ? gcs.getEntrainementActif().name() : null;
            data.creatureDebutEntrainement = gcs.getDebutEntrainement() != null
                    ? gcs.getDebutEntrainement().toString() : null;
        }

        data.rangJoueur = ctx.rangJoueur.getRangNom();
        data.rangJoueurCoffresReclames = ctx.rangJoueur.getCoffreRangReclame();
        for (Titre t : ctx.gestionnaireTitres.getTitresObtenus())
            data.titresObtenus.add(t.getNom());
        if (ctx.gestionnaireTitres.getTitreActif() != null)
            data.titreActifNom = ctx.gestionnaireTitres.getTitreActif().getNom();

        data.tutorielPropose    = ctx.gestionnaireTutoriel.isPropose();
        data.tutorielActif      = ctx.gestionnaireTutoriel.isActif();
        data.tutorielEtapesVues = new java.util.ArrayList<>(ctx.gestionnaireTutoriel.getEtapesVues());
    }

    public static void restaurerEtoiles(GestionnaireEtoiles ge, SauvegardeData data) {
        if (data.etoiles != null)
            for (SauvegardeData.EtoileData ed : data.etoiles)
                ge.setEtoiles(ed.cle, ed.e1, ed.e2, ed.e3);
        if (data.coffresClaimes != null)
            for (String cle : data.coffresClaimes)
                ge.setCoffreClaime(cle, true);
    }

    public static void restaurerCompagnons(GestionnaireCompagnons gc, SauvegardeData data) {
        if (data.compagnonsType != null) {
            gc.restaurer(data.compagnonsType, data.compagnonsNiveau);
        }
    }

    public static void restaurerCreaturesSacrees(Gestionnaire_pet gcs, SauvegardeData data) {
        if (data.creatureType != null) {
            gcs.restaurer(data.creatureType, data.creatureNiveau, data.creatureExperience,
                    data.creatureOeufDebloque, data.creatureEntrainement, data.creatureDebutEntrainement);
        }
    }

    public static void restaurerRecompenses(GestionnaireRecompenses gr, SauvegardeData data) {
        gr.setNiveauReclame(data.recompensesNiveauReclame);
        gr.setJoursCumulesMois(data.recompensesJoursCumulesMois);
        if (data.recompensesDernierJourMois != null)
            gr.setDernierJourComptePointage(LocalDate.parse(data.recompensesDernierJourMois));
        gr.setMoisComptePointage(data.recompensesMoisCompte);
        gr.setMoisReclame(data.recompensesMoisReclame);
        gr.setJourConnexion(data.recompensesJourConnexion);
        if (data.recompensesDernierJourConnexion != null)
            gr.setDernierJourConnexion(LocalDate.parse(data.recompensesDernierJourConnexion));
        gr.setJourReclame(data.recompensesJourReclame);
        gr.setTerminee(data.recompensesTerminee);
        if (data.recompensesDerniereReclamation30min != null)
            gr.setDerniereReclamation30min(LocalDateTime.parse(data.recompensesDerniereReclamation30min));
        gr.setPointsMois(data.recompensesPointsMois);
    }

    public static void restaurerRangEtTitres(RangJoueur rangJoueur,
                                              GestionnaireTitres gestionnaireTitres,
                                              SauvegardeData data) {
        if (data.rangJoueur != null) rangJoueur.setRangDepuisNom(data.rangJoueur);
        if (data.rangJoueurCoffresReclames != null) rangJoueur.setCoffreRangReclame(data.rangJoueurCoffresReclames);
        if (data.titresObtenus != null)
            for (String nom : data.titresObtenus)
                gestionnaireTitres.debloquerTitre(nom);
        if (data.titreActifNom != null)
            gestionnaireTitres.equiperTitre(data.titreActifNom);
    }

    /**
     * Restaure l'etat du tutoriel guide. Cas particulier de compatibilite retroactive :
     * une sauvegarde qui a deja de la progression (niveau &gt; 1 ou stage 1 deja reussi) mais
     * dont le champ tutorielPropose est absent/false vient forcement d'avant l'ajout de cette
     * fonctionnalite -> on ne propose jamais le tutoriel a un joueur qui a deja avance dans le jeu.
     */
    public static void restaurerTutoriel(GameContext ctx, SauvegardeData data) {
        GestionnaireTutoriel gt = ctx.gestionnaireTutoriel;
        boolean sauvegardeAnterieureALaFonctionnalite = !data.tutorielPropose
                && (ctx.joueur.getNiveau() > 1 || ctx.chapitre1.getStagesReussis()[1]);

        if (sauvegardeAnterieureALaFonctionnalite) {
            gt.setPropose(true);
            gt.setActif(false);
            for (GestionnaireTutoriel.EtapeTutoriel e : GestionnaireTutoriel.getEtapes()) {
                gt.marquerEtapeVue(e.libelle());
            }
        } else {
            gt.setPropose(data.tutorielPropose);
            gt.setActif(data.tutorielActif);
            gt.setEtapesVues(data.tutorielEtapesVues);
        }
    }
}
