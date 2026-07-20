package lancement.Gestionnaires;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Joueur.Personnage_principale;
import Joueur.Mage;
import Joueur.Ninja;
import Joueur.Guerrier;
import Joueur.Competences;
import Personnage.PersonnageBase;


import Personnage.FairyTail.*;



import Personnage.FairyTail.perso_Arzak;
import Personnage.FairyTail.perso_Biska;
import Personnage.FairyTail.perso_Elfman;
import Personnage.FairyTail.perso_Max;
import Personnage.FairyTail.perso_Droy;
import Personnage.FairyTail.perso_Jett;
import Personnage.FairyTail.perso_Warren;
import Personnage.FairyTail.perso_Nab;
import Personnage.FairyTail.perso_Romeo;
import Personnage.FairyTail.perso_Levy;
import Personnage.FairyTail.perso_Lisanna;
import Personnage.FairyTail.perso_ElfmanBete;
import Personnage.FairyTail.perso_Bixrow;
import Personnage.FairyTail.perso_Evergreen;
import Personnage.FairyTail.perso_Kana;
import Personnage.FairyTail.perso_Loke;
import Personnage.FairyTail.perso_Angel;
import Personnage.FairyTail.perso_Freed;
import Personnage.FairyTail.perso_Gajeel;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Jubia_4elements;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Wendy;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Mirajane;
import Personnage.FairyTail.perso_Natsu_Etherion;
import Personnage.FairyTail.perso_Rogue;
import Personnage.FairyTail.perso_Sting;
import Personnage.FairyTail.perso_Yukino;
import Personnage.FairyTail.perso_Lucas;
import Personnage.FairyTail.perso_Mirajane_Halphas;
import Equipement.CarteOr;
import Equipement.Equipement;
import Equipement.Inventaire;
import Equipement.Materiau;
import Equipement.ParcheminXP;
import lancement.Gestionnaires.ClefCeleste;
import lancement.Gestionnaires.GestionnaireClefsCelestes;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.Chapitres.Chapitre3;
import lancement.ChapitreElite.Chapitre1Elite;
import lancement.ChapitreElite.Chapitre2Elite;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.Formation;
import lancement.GameContext;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;
import lancement.RangJoueur;
import lancement.SauvegardeData;
import lancement.Titre;
import lancement.Gestionnaires.GestionnaireDonjon;
import lancement.Gestionnaires.GestionnaireEtoiles;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.EtoilesStage;
import java.util.Map;
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
    public void sauvegarder(GameContext ctx) {
        SauvegardeData data = new SauvegardeData();
        Personnage_principale joueur = ctx.joueur;

        // Joueur de base
        data.joueurNom           = joueur.getNom();
        data.joueurNiveau        = joueur.getNiveau();
        data.joueurExperience    = joueur.getExperience();
        data.joueurExperienceMax = joueur.getExperienceMax();
        data.joueurOr            = joueur.getOr();
        data.joueurClasse        = joueur.getChoixClasses();
        data.joueurChoixComp     = joueur.getChoixComp();

        // Coffre arène
        data.dernierCoffreArene = ctx.dernierCoffreArene;

        // Arbre de compétences
        data.arbreNoeudDebloques      = joueur.getArbreCompetences().getEtatNoeuds();
        data.arbreNoeudDebloques2     = joueur.getArbreCompetences().getEtatNoeuds2();
        data.arbrePointsDisponibles   = joueur.getArbreCompetences().getPointsDisponibles();
        data.arbre2Debloque           = joueur.getArbreCompetences().isArbre2Debloque();
        data.competenceSpecialeActive = joueur.getCompetenceSpecialeActive();

        // Équipements portés par le joueur principal
        for (Equipement e : joueur.getEquipementsPortes())
            data.joueurEquipementsPortes.add(versEquipementData(e, 1));

        // Personnages recrutés
        for (PersonnageBase p : ctx.personnagesRecruites) {
            SauvegardeData.PersonnageData pd = new SauvegardeData.PersonnageData(
                p.getNom(), p.getNiveau(), p.getExperience(), p.getExperienceMax()
            );
            pd.nbreEtoiles = p.getNbreEtoiles();
            for (Equipement e : p.getEquipementsPortes())
                pd.equipementsPortes.add(versEquipementData(e, 1));
            data.personnagesRecruites.add(pd);
        }

        // Formation
        if (ctx.formation.getTank() != null)
            data.formationTank = ctx.formation.getTank().getNom();
        for (PersonnageBase a : ctx.formation.getAttaquants())
            data.formationAttaquants.add(a.getNom());
        for (PersonnageBase s : ctx.formation.getSupports())
            data.formationSupports.add(s.getNom());

        // Chapitre 1
        copierTableaux(ctx.chapitre1.getStagesDebloques(), data.chapitre1Debloques);
        copierTableaux(ctx.chapitre1.getStagesReussis(),   data.chapitre1Reussis);

        // Chapitre 1 Elite
        copierTableaux(ctx.chapitre1Elite.getStagesDebloques(), data.chapitre1EliteDebloques);
        copierTableaux(ctx.chapitre1Elite.getStagesReussis(),   data.chapitre1EliteReussis);

        // Chapitre 2
        copierTableaux(ctx.chapitre2.getStagesDebloques(), data.chapitre2Debloques);
        copierTableaux(ctx.chapitre2.getStagesReussis(),   data.chapitre2Reussis);

        if (ctx.chapitre2Elite != null) {
            copierTableaux(ctx.chapitre2Elite.getStagesDebloques(), data.chapitre2EliteDebloques);
            copierTableaux(ctx.chapitre2Elite.getStagesReussis(),   data.chapitre2EliteReussis);
        }
        if (ctx.chapitre3Elite != null) {
            copierTableaux(ctx.chapitre3Elite.getStagesDebloques(),  data.chapitre3EliteDebloques);
            copierTableaux(ctx.chapitre3Elite.getStagesReussis(),    data.chapitre3EliteReussis);
            copierTableaux(ctx.chapitre3Elite.getPremiereVictoire(), data.chapitre3ElitePremiereVictoire);
        }

        if (ctx.chapitre3 != null) {
            copierTableaux(ctx.chapitre3.getStagesDebloques(), data.chapitre3Debloques);
            copierTableaux(ctx.chapitre3.getStagesReussis(),   data.chapitre3Reussis);
        }

        // Parchemins
        data.parcheminC = ctx.menuRecrutement.getParcheminC();
        data.parcheminB = ctx.menuRecrutement.getParcheminB();

        // Tirages
        if (ctx.menuTirage != null) {
            data.parcheminTirageOrdinaire    = ctx.menuTirage.getParcheminOrdinaire();
            data.parcheminTirageElite        = ctx.menuTirage.getParcheminElite();
            data.tirageEliteCompteurSansS    = ctx.menuTirage.getCompteurSansSRang();
            data.tirageEliteCompteurSansSS   = ctx.menuTirage.getCompteurSansSS();
        }

        // Inventaire
        for (Inventaire.StackEquipement s : ctx.inventaire.getStacks())
            data.inventaireEquipements.add(versEquipementData(s.getEquipement(), s.getQuantite()));

        // Matériaux
        for (Materiau m : ctx.inventaire.getMateriaux())
            data.materiaux.add(new SauvegardeData.MateriauData(m.getNom(), m.getQuantite()));

        // Parchemins XP (consommables)
        for (Inventaire.StackParchemin s : ctx.inventaire.getParchemins())
            data.inventaireParcheminsXP.add(
                new SauvegardeData.ParcheminXPData(s.getRarete().name(), s.getQuantite()));

        // Cartes d'or
        for (Inventaire.StackCarteOr s : ctx.inventaire.getCartesOr())
            data.inventaireCartesOr.add(
                new SauvegardeData.CarteOrData(s.getCarte().name(), s.getQuantite()));

        // Quêtes
        GestionnaireQuetes gq = ctx.gestionnaireQuetes;
        data.dernierRenouvellementQuete = gq.getDernierRenouvellement().toString();
        data.indexQueteJournaliere      = gq.getIndexQueteJournaliere();
        QueteJournaliere qj = gq.getQueteJournaliere();
        if (qj != null) {
            SauvegardeData.QueteJournaliereData qjd = new SauvegardeData.QueteJournaliereData();
            qjd.id            = qj.getId();
            qjd.titre         = qj.getTitre();
            qjd.description   = qj.getDescription();
            qjd.typeObjectif  = qj.getTypeObjectif().name();
            qjd.objectifCible = qj.getObjectifCible();
            qjd.recompenseXP  = qj.getRecompenseXP();
            qjd.recompenseOr  = qj.getRecompenseOr();
            qjd.progression   = qj.getProgressionValeur();
            qjd.completee     = qj.isCompletee();
            qjd.reclamee      = qj.isReclamee();
            data.queteJournaliereActive = qjd;
        }
        for (QueteProgression q : gq.getToutesQuetesProgression()) {
            if (q.isReclamee())  data.quetesProgressionReclamees.add(q.getId());
            if (q.isCompletee()) data.quetesProgressionCompletees.add(q.getId());
        }

        // Énergie
        GestionnaireEnergie ge = ctx.gestionnaireEnergie;
        data.energie                 = ge.getEnergie();
        data.derniereRechargeEnergie = ge.getDerniereRecharge().toString();
        data.rechargesUtilisees      = ge.getRechargesUtilisees();
        data.dernierResetRecharge    = ge.getDernierResetRecharge().toString();
        data.runsEliteParStage       = ge.getRunsEliteParStage();
        data.dernierResetRunsElite   = ge.getDernierResetRunsElite().toString();

        // Etoiles & coffres
        for (Map.Entry<String, EtoilesStage> entry : ctx.gestionnaireEtoiles.getEtoilesMap().entrySet()) {
            EtoilesStage e = entry.getValue();
            data.etoiles.add(new SauvegardeData.EtoileData(
                    entry.getKey(), e.isEtoile1(), e.isEtoile2(), e.isEtoile3()));
        }
        for (Map.Entry<String, Boolean> entry : ctx.gestionnaireEtoiles.getCoffresClaimes().entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) data.coffresClaimes.add(entry.getKey());
        }

        // Compagnons
        if (ctx.gestionnaireCompagnons != null) {
            data.compagnonsType   = ctx.gestionnaireCompagnons.getType().name();
            data.compagnonsNiveau = ctx.gestionnaireCompagnons.getNiveau();
        }

        // Clefs Célestes
        if (ctx.gestionnaireClefsCelestes != null) {
            data.clefsCelestes.clear();
            for (ClefCeleste clef : ClefCeleste.values()) {
                GestionnaireClefsCelestes.EtatClef e = ctx.gestionnaireClefsCelestes.getCollection().get(clef);
                if (e.debloquee) // n'enregistrer que les clefs obtenues
                    data.clefsCelestes.add(new SauvegardeData.ClefCelesteData(clef.name(), true, e.niveau));
            }
            ClefCeleste active = ctx.gestionnaireClefsCelestes.getClefActive();
            data.clefCelesteActive = (active != null) ? active.name() : null;
        }

        // Coupons
        data.coupons = ctx.coupons;

        // Rang & titres (sans prestige)
        data.rangJoueur = ctx.rangJoueur.getRangNom();
        for (Titre t : ctx.gestionnaireTitres.getTitresObtenus())
            data.titresObtenus.add(t.getNom());
        if (ctx.gestionnaireTitres.getTitreActif() != null)
            data.titreActifNom = ctx.gestionnaireTitres.getTitreActif().getNom();

        // Donjon
        data.donjonRuns         = ctx.gestionnaireDonjon.getRuns();
        data.donjonDernierReset = ctx.gestionnaireDonjon.getDernierReset().toString();

        // Envoi Firebase
        String url     = URL_FIREBASE + "joueurs/" + securiser(joueur.getNom()) + ".json";
        String payload = gson.toJson(data);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200)
                System.out.println("Partie de " + joueur.getNom() + " sauvegardee dans le Cloud !");
            else
                System.out.println("Erreur Firebase (Code " + res.statusCode() + ") : " + res.body());
        } catch (Exception e) {
            System.out.println("Impossible de se connecter a Firebase : " + e.getMessage());
        }
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
        Personnage_principale joueur = new Personnage_principale(data.joueurNom, 1);
        Competences comp = switch (data.joueurClasse) {
            case "Mage"     -> new Mage();
            case "Ninja"    -> new Ninja();
            case "Guerrier" -> new Guerrier();
            default         -> null;
        };
        joueur.setChoixClasses(data.joueurClasse);
        joueur.setChoixComp(data.joueurChoixComp);
        joueur.setCompetencesChoisie(comp);
        joueur.setOr(data.joueurOr);
        joueur.getArbreCompetences().setEtatNoeuds(data.arbreNoeudDebloques);
        joueur.getArbreCompetences().setPointsDisponibles(data.arbrePointsDisponibles);
        joueur.getArbreCompetences().setEtatNoeuds2(data.arbreNoeudDebloques2);
        joueur.getArbreCompetences().setArbre2Debloque(data.arbre2Debloque);
        joueur.setCompetenceSpecialeActive(data.competenceSpecialeActive);
        while (joueur.getNiveau() < data.joueurNiveau) joueur.monterDeNiveau();
        joueur.setExperience(data.joueurExperience);
        joueur.setExperienceMax(data.joueurExperienceMax);
        if (data.joueurEquipementsPortes != null)
            for (SauvegardeData.EquipementData ed : data.joueurEquipementsPortes)
                joueur.equiper(versEquipement(ed));

        // Injection du contexte pour le multiplicateur de rang
        joueur.setGameContext(ctx);

        // Restauration du coffre arène
        if (ctx != null) {
            ctx.dernierCoffreArene = data.dernierCoffreArene;
        }

        return joueur;
    }

    /**
     * Surcharge sans ctx pour compatibilité ascendante si appelée ailleurs.
     * Le setGameContext devra être fait manuellement dans ce cas.
     */
    public Personnage_principale restaurerJoueur(SauvegardeData data) {
        return restaurerJoueur(data, null);
    }

    public ArrayList<PersonnageBase> restaurerPersonnagesRecruites(SauvegardeData data) {
        ArrayList<PersonnageBase> liste = new ArrayList<>();
        for (SauvegardeData.PersonnageData pd : data.personnagesRecruites) {
            PersonnageBase p = creerPersonnageParNom(pd.nom);
            if (p != null) {
                appliquerNiveaux(p, pd);
                p.setNbreEtoiles(pd.nbreEtoiles);
                if (pd.equipementsPortes != null)
                    for (SauvegardeData.EquipementData ed : pd.equipementsPortes)
                        p.equiper(versEquipement(ed));
                liste.add(p);
            }
        }
        return liste;
    }

    public void restaurerFormation(Formation formation,
                                   SauvegardeData data,
                                   ArrayList<PersonnageBase> personnagesRecruites) {
        for (PersonnageBase p : personnagesRecruites) {
            if (data.formationTank != null && p.getNom().equals(data.formationTank))
                formation.ajouterPersonnage(p);
            else if (data.formationAttaquants.contains(p.getNom()))
                formation.ajouterPersonnage(p);
            else if (data.formationSupports.contains(p.getNom()))
                formation.ajouterPersonnage(p);
        }
    }

    public void restaurerChapitre1(Chapitre1 c, SauvegardeData data) {
        c.setStagesDebloques(data.chapitre1Debloques);
        c.setStagesReussis(data.chapitre1Reussis);
    }

    public void restaurerChapitre1Elite(Chapitre1Elite c, SauvegardeData data) {
        if (data.chapitre1EliteDebloques != null) c.setStagesDebloques(data.chapitre1EliteDebloques);
        if (data.chapitre1EliteReussis   != null) c.setStagesReussis(data.chapitre1EliteReussis);
    }

    public void restaurerChapitre2(Chapitre2 c, SauvegardeData data) {
        if (data.chapitre2Debloques != null) c.setStagesDebloques(data.chapitre2Debloques);
        if (data.chapitre2Reussis   != null) c.setStagesReussis(data.chapitre2Reussis);
    }

    public void restaurerInventaire(Inventaire inventaire, SauvegardeData data) {
        if (data.inventaireEquipements != null)
            for (SauvegardeData.EquipementData ed : data.inventaireEquipements) {
                Equipement e = versEquipement(ed);
                int q = ed.quantite > 0 ? ed.quantite : 1;
                for (int i = 0; i < q; i++) inventaire.ajouterEquipement(e);
            }
        if (data.materiaux != null)
            for (SauvegardeData.MateriauData md : data.materiaux)
                inventaire.ajouterMateriau(md.nom, md.quantite);

        // Parchemins XP
        if (data.inventaireParcheminsXP != null)
            for (SauvegardeData.ParcheminXPData pd : data.inventaireParcheminsXP)
                inventaire.ajouterParcheminXP(
                    ParcheminXP.Rarete.valueOf(pd.rarete), pd.quantite);

        // Cartes d'or
        if (data.inventaireCartesOr != null)
            for (SauvegardeData.CarteOrData cd : data.inventaireCartesOr) {
                try {
                    CarteOr niveau = CarteOr.valueOf(cd.niveau);
                    inventaire.ajouterCartesOr(niveau, cd.quantite);
                } catch (IllegalArgumentException ignored) {}
            }
    }

    public void restaurerQuetes(GestionnaireQuetes gq, SauvegardeData data) {
        if (data.dernierRenouvellementQuete != null)
            gq.setDernierRenouvellement(LocalDate.parse(data.dernierRenouvellementQuete));
        gq.setIndexQueteJournaliere(data.indexQueteJournaliere);
        if (data.queteJournaliereActive != null) {
            SauvegardeData.QueteJournaliereData qjd = data.queteJournaliereActive;
            QueteJournaliere qj = new QueteJournaliere(
                qjd.id, qjd.titre, qjd.description,
                QueteJournaliere.TypeObjectif.valueOf(qjd.typeObjectif),
                qjd.objectifCible, qjd.recompenseXP, qjd.recompenseOr
            );
            qj.ajouterProgression(qjd.progression);
            qj.setCompletee(qjd.completee);
            qj.setReclamee(qjd.reclamee);
            gq.setQueteJournaliere(qj);
        }
        for (QueteProgression q : gq.getToutesQuetesProgression()) {
            if (data.quetesProgressionReclamees.contains(q.getId())) {
                q.setCompletee(true); q.setReclamee(true);
            } else if (data.quetesProgressionCompletees.contains(q.getId())) {
                q.setCompletee(true);
            }
        }
    }

    public void restaurerEnergie(GestionnaireEnergie ge, SauvegardeData data) {
        ge.setEnergie(data.energie);
        if (data.derniereRechargeEnergie != null)
            ge.setDerniereRecharge(LocalDateTime.parse(data.derniereRechargeEnergie));
        ge.setRechargesUtilisees(data.rechargesUtilisees);
        if (data.dernierResetRecharge != null)
            ge.setDernierResetRecharge(LocalDate.parse(data.dernierResetRecharge));
        if (data.runsEliteParStage != null)
            ge.setRunsEliteParStage(data.runsEliteParStage);
        if (data.dernierResetRunsElite != null)
            ge.setDernierResetRunsElite(LocalDate.parse(data.dernierResetRunsElite));
    }

    public void restaurerRangEtTitres(RangJoueur rangJoueur,
                                       GestionnaireTitres gestionnaireTitres,
                                       SauvegardeData data) {
        if (data.rangJoueur != null) rangJoueur.setRangDepuisNom(data.rangJoueur);
        if (data.titresObtenus != null)
            for (String nom : data.titresObtenus)
                gestionnaireTitres.debloquerTitre(nom);
        if (data.titreActifNom != null)
            gestionnaireTitres.equiperTitre(data.titreActifNom);
    }

    public void restaurerEtoiles(GestionnaireEtoiles ge, SauvegardeData data) {
        if (data.etoiles != null)
            for (SauvegardeData.EtoileData ed : data.etoiles)
                ge.setEtoiles(ed.cle, ed.e1, ed.e2, ed.e3);
        if (data.coffresClaimes != null)
            for (String cle : data.coffresClaimes)
                ge.setCoffreClaime(cle, true);
    }

    public void restaurerCompagnons(GestionnaireCompagnons gc, SauvegardeData data) {
        if (data.compagnonsType != null) {
            gc.restaurer(data.compagnonsType, data.compagnonsNiveau);
        }
    }

    public void restaurerClefsCelestes(GestionnaireClefsCelestes gcc, SauvegardeData data) {
        if (data.clefsCelestes != null) {
            for (SauvegardeData.ClefCelesteData cd : data.clefsCelestes) {
                gcc.restaurer(cd.nomClef, cd.debloquee, cd.niveau,
                        cd.nomClef.equals(data.clefCelesteActive));
            }
        }
    }

    public void restaurerChapitre3(Chapitre3 c, SauvegardeData data) {
        if (data.chapitre3Debloques != null) c.setStagesDebloques(data.chapitre3Debloques);
        if (data.chapitre3Reussis   != null) c.setStagesReussis(data.chapitre3Reussis);
    }

    public void restaurerChapitre2Elite2(Chapitre2Elite c, SauvegardeData data) {
        if (data.chapitre2EliteDebloques != null)
            c.setStagesDebloques(data.chapitre2EliteDebloques);
        if (data.chapitre2EliteReussis != null)
            c.setStagesReussis(data.chapitre2EliteReussis);
    }

    public void restaurerChapitre3Elite(Chapitre3Elite c, SauvegardeData data) {
        if (data.chapitre3EliteDebloques != null)
            c.setStagesDebloques(data.chapitre3EliteDebloques);
        if (data.chapitre3EliteReussis != null)
            c.setStagesReussis(data.chapitre3EliteReussis);
        if (data.chapitre3ElitePremiereVictoire != null)
            c.setPremiereVictoire(data.chapitre3ElitePremiereVictoire);
    }

    public void restaurerDonjon(GestionnaireDonjon gd, SauvegardeData data) {
        if (data.donjonRuns != null)        gd.setRuns(data.donjonRuns);
        if (data.donjonDernierReset != null) gd.setDernierReset(LocalDate.parse(data.donjonDernierReset));
    }

    // ── Utilitaires privés ────────────────────────────────────────────────
    private String securiser(String nom) {
        return nom.trim().toLowerCase().replace(" ", "_");
    }

    private void copierTableaux(boolean[] src, boolean[] dst) {
        for (int i = 0; i < src.length && i < dst.length; i++) dst[i] = src[i];
    }

    private SauvegardeData.EquipementData versEquipementData(Equipement e, int quantite) {
        SauvegardeData.EquipementData ed = new SauvegardeData.EquipementData(
            e.getNom(), e.getSlot().name(), e.getRarete().name(), e.getTypeArme().name(),
            e.getBonusATKBase(), e.getBonusDEFBase(), e.getBonusPVBase(), e.getBonusVITBase(),
            quantite
        );
        ed.niveauFortification = e.getNiveauFortification();
        ed.niveauAffinage      = e.getNiveauAffinage();
        return ed;
    }

    private Equipement versEquipement(SauvegardeData.EquipementData ed) {
        Equipement e = new Equipement(
            ed.nom,
            Equipement.Slot.valueOf(ed.slot),
            Equipement.Rarete.valueOf(ed.rarete),
            Equipement.TypeArme.valueOf(ed.typeArme),
            ed.bonusATK, ed.bonusDEF, ed.bonusPV, ed.bonusVIT
        );
        e.setNiveauFortification(ed.niveauFortification);
        e.setNiveauAffinage(ed.niveauAffinage);
        return e;
    }

   public PersonnageBase creerPersonnageParNom(String nom) {
    return switch (nom) {
        // Rang C
        case "Alzack"         -> new perso_Arzak();
        case "Bisca"          -> new perso_Biska();
        case "Elfman"         -> new perso_Elfman();
        case "Max"            -> new perso_Max();
        case "Droy"           -> new perso_Droy();
        case "Jet"            -> new perso_Jett();
        case "Warren"         -> new perso_Warren();
        case "Nab"            -> new perso_Nab();
        case "Romeo"          -> new perso_Romeo();
        // Rang B
        case "Bickslow"       -> new perso_Bixrow();
        case "Evergreen"      -> new perso_Evergreen();
        case "Cana"           -> new perso_Kana();
        case "Loke"           -> new perso_Loke();
        case "Levy"           -> new perso_Levy();
        case "Lisanna"        -> new perso_Lisanna();
        case "Elfman Bête"    -> new perso_ElfmanBete();
        // Boutique arène — Rang B
        case "Elfman Bete"    -> new perso_ElfmanBete();
        // Rang A
        case "Angel"          -> new perso_Angel();
        case "Freed"          -> new perso_Freed();
        case "Gajeel"         -> new perso_Gajeel();
        case "Gray"           -> new perso_Gray();
        case "Jubia"          -> new perso_Jubia_4elements();
        case "Lucy"           -> new perso_Lucy();
        case "Natsu"          -> new perso_Natsu();
        case "Wendy"          -> new perso_Wendy();
        // Rang S+
        case "Erza"           -> new perso_Erza();
        case "Mirajane"       -> new perso_Mirajane();
        case "Natsu Etherion" -> new perso_Natsu_Etherion();
        case "Rogue"          -> new perso_Rogue();
        case "Sting"          -> new perso_Sting();
        case "Yukino"         -> new perso_Yukino();
        case "Lucas"          -> new perso_Lucas();
        case "Mirajane Halphas" -> new perso_Mirajane_Halphas();
        default               -> null;
    };
}

    private void appliquerNiveaux(PersonnageBase p, SauvegardeData.PersonnageData pd) {
        while (p.getNiveau() < pd.niveau) p.monterDeNiveau();
        p.setExperience(pd.experience);
        p.setExperienceMax(pd.experienceMax);
    }
}