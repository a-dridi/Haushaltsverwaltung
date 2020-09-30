/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import haushaltsverwaltung.main.AusgabenKategorienBetraege;
import haushaltsverwaltung.model.Appsettings;
import haushaltsverwaltung.model.Ausgaben;
import haushaltsverwaltung.model.AusgabenBudget;
import haushaltsverwaltung.model.Ausgabenausgabezeitraum;
import haushaltsverwaltung.model.Ausgabenkategorie;
import haushaltsverwaltung.model.DatenbankNotizen;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Named;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.poi.util.IOUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.Visibility;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;

/**
 * Expenses / Ausgaben
 *
 * @author A.Dridi
 */
@Named(value = "ausgabenController")
@ViewScoped
public class AusgabenController implements Serializable {

    private DonutChartModel chartBarAusgabenJaehrlich;
    private DonutChartModel chartBarAusgabenMonatlich;

    private Ausgabenausgabezeitraum ausgabenausgabezeitraum;
    private Ausgabenkategorie ausgabenkategorie;
    private List<Ausgabenkategorie> ausgabenkategorien = new ArrayList<>();
    private List<Ausgabenausgabezeitraum> ausgabenausgabezeitraeume = new ArrayList<>();
    private List<Ausgaben> ausgabenListenSQL = new ArrayList<>();
    private List<Ausgaben> filteredAusgabenListenSQL;
    private List<AusgabenBudget> ausgabenBudgetListenSQL = new ArrayList<>();
    private List<AusgabenBudget> filteredAusgabenBudgetListenSQL;
    private List<AusgabenBudget> ausgabenBudgetJaehrlichListenSQL = new ArrayList<>();
    private List<AusgabenBudget> filteredAusgabenBudgetJaehrlichListenSQL;

    //AusgabenBudget Variablen
    //Variable um Summe von allen Ausgaben des gewählten/laufenden Monats und Jahres
    private Double alleAusgabenMonatSumme = 0.0;
    private Double alleAusgabenJahrSumme = 0.0;
    private String alleAusgabenMonatSummeAusgabe;
    private String alleAusgabenJahrSummeAusgabe;
    private String regelmaessigeAusgabenJahrSummeAusgabe;
    private String regelmaessigeAusgabenMonatSummeAusgabe;
    private Double ausgabenBudgetSumme;
    private String differenzAusgabenBudgetMonat;
    private String differenzAusgabenBudgetJahr;
    private String ausgabenBudgetUeberschrift;
    private boolean alleAusgabenAnzeigen = true;

    //Cache Liste um gelöschte Datensätze rückgängig zu machen (nur innerhalb einer Session)
    private List<Ausgaben> deletedAusgabenListenSQL = new ArrayList<>();

    private List<AusgabenKategorienBetraege> ausgabenKategorienBetragMonatlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> filteredAusgabenKategorienBetragMonatlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> ausgabenKategorienBetragJaehrlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> filteredAusgabenKategorienBetragJaehrlichListe = new ArrayList<>();

    private ScheduleModel ausgabenKalender;

    private String datensaetzeAnzahlText;

    //ExportColumns - WICHTIG ANZAHL AN SPALTENANZAHL ANPASSEN (ausgenommen Anhang/D Spalte)!!!:
    private List<Boolean> columnList = Arrays.asList(true, true, true, true, true, true, true);
    private List<Boolean> columnListAusgabenBudget = Arrays.asList(true, true, true, true, true, true, true);
    private String tabellenname = "Ausgaben";

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Ausgaben";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Ausgaben";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    private String bezeichnung;
    private Double betrag;
    private String deleteID;
    private String anhangID;

    private Date zahlungsdatum;
    private String informationen;
    private byte[] anhang;
    private DatenbankNotizen dbnotizEintrag = null;
    private String notiztext;

    private DAO dao;
    private String neuAusgabenausgabezeitraum;
    private String neuAusgabenkategorie;
    private String zahlungsdatumEingabe;

    private String change_Kategorie;
    private String change_ausgabezeitraum;
    //Wird auch für das Hinzufügen AusgabenBudget (AusgabenKategorie speichern) verwendet 
    private Ausgabenkategorie deleteAusgabenkategorie;
    private Ausgabenausgabezeitraum deleteAusgabenausgabezeitraum;

    private Integer rownumbers = 15;

    private String anhangname;
    private String anhangtype;

    private Double ausgabenMonatSumme;
    private Double ausgabenJaehrlichSumme;

    private String gewinnMonatlich;
    private String gewinnJaehrlich;
    private Double einnahmenMonatSumme;
    private Double einnahmenJaehrlichSumme;

    private String durchschnittlicheMonatlicheAusgaben;

    private String jahrmonat;
    private Integer aktuellesJahr;
    private Integer aktuelleMonat;
    private Date selectMonatJahr;

    private String datumNotiztext;

    private String durchschnittlAusgabenBestimmterMonat;
    private ScheduleEvent event = new DefaultScheduleEvent();

    //Aufteilung der Ausgaben
    private String person1name = "Person A";
    private Integer person1aufteilung = 0;
    private Double person1gesamtteilbetrag = 0.0;
    private Double person1einkommen = 0.0;

    private String person2name = "Person B";
    private Integer person2aufteilung = 0;
    private Double person2gesamtteilbetrag = 0.0;
    private Double person2einkommen = 0.0;

    /**
     * Creates a new instance of AusgabenController
     */
    public AusgabenController() {
        this.dao = new DAO();

    }

    @PostConstruct
    private void init() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
            this.datumNotiztext = "Zuletzt akualisiert: " + notizList.get(0).getDatum();

        }

        //Datum setzen - Atkuelles Datum (laufender Monat und laufendes Jahr)
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        this.aktuellesJahr = Integer.parseInt(sdf.format(d));
        this.jahrmonat = sdf.format(d);
        sdf = new SimpleDateFormat("MM");
        this.aktuelleMonat = Integer.parseInt(sdf.format(d));

        this.ausgabenListenSQL = dao.getAllAusgaben();
        this.filteredAusgabenListenSQL = new ArrayList<>(ausgabenListenSQL);

        this.ausgabenkategorien = dao.getAllAusgabenkategorie();
        this.ausgabenausgabezeitraeume = dao.getAllAusgabenausgabezeitraum();

        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        //Ausgabe Grafik
        if (urlName.contains("ausgaben_grafik.xhtml")) {
            createPortfolioWertinEuro();
            //Ausgaben-Kalender
        } else if (urlName.contains("ausgaben_kalender.xhtml")) {
            createAusgabenKalender();
        } else if (urlName.contains("ausgaben_kategorien.xhtml")) {
            createPortfolioWertinEuro();
            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();
            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();
            calculateDifferenzAusgabenEinnahmen();
        } else if (urlName.contains("ausgaben_budget.xhtml")) {
            //AusgabenBudget Kategorien (Datensätze) erstellen/aktualisieren - Holt Werte von Ausgaben Tabelle
            dao.updateAusgabenBudgetDataMonatAlle();
            this.ausgabenBudgetListenSQL = dao.getAllAusgabenBudget();
            this.filteredAusgabenBudgetListenSQL = dao.getAllAusgabenBudget();
            calculateAusgabenBudgetSumme();
            //Ausgaben (inkl. Einmalige) und Budget Differenz für ausgewählten Monat oder laufenden Monat berechnen
            calculateAusgabenDesLaufendenMonatJahr();
            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();
            calculateDifferenzAusgabenEinnahmenAlle();
            showJaehrlichAusgabenBudget();
            this.ausgabenBudgetUeberschrift = "(" + this.aktuelleMonat + "/" + this.aktuellesJahr + ")" + " Alle";

            //AusgabenJaehrlichSumme auf Summe aller Ausgaben eines Jahres ohne einmalige Ausgaben.
            try {
                this.differenzAusgabenBudgetMonat = String.format("%.2f", (this.ausgabenBudgetSumme) - this.alleAusgabenMonatSumme);
                this.differenzAusgabenBudgetJahr = String.format("%.2f", (this.ausgabenBudgetSumme * 12) - this.alleAusgabenJahrSumme);

            } catch (NullPointerException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "NullPointerException", "Ausgaben-Budget konnte nicht gerechnet werden! - NullPointerException"));
                System.out.println(e);
            }

        } else { //Tabelle Ausgaben 
            flushAnhang();
            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();
            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();
            calculateDifferenzAusgabenEinnahmen();

            //* PROBLEM !!! - Please fix this
            /*
            try {
            } catch (NullPointerException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Funktion Teilbetrags-Rechnung nicht verfügbar.", ""));
            }
             */
            //
            loadAppsettingsAusgaben();

            this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");
        }

    }

    /**
     * Gesamtbetrag der Ausgaben für Person 1 und Person 2 aufteilen in
     * Teilbeträge
     */
    public void calculateGesamtTeilbetrag() {

        if ((this.person1aufteilung + this.person2aufteilung) == 100) {
            this.person1gesamtteilbetrag = Double.valueOf(String.format("%.2f", this.ausgabenMonatSumme * (this.getPerson1aufteilung() / 100.0)));
            this.person2gesamtteilbetrag = Double.valueOf(String.format("%.2f", this.ausgabenMonatSumme * (this.getPerson2aufteilung() / 100.0)));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Summe beider Teilschlüssel müssen insgesamt 100 ergeben.", ""));
        }
    }

    /**
     * Nach dao.getAllAusgabenBudget ausführen. AusgabenBudget in jährlicher
     * Anrechung
     */
    public void showJaehrlichAusgabenBudget() {
        this.dao = new DAO();
        this.ausgabenBudgetJaehrlichListenSQL.clear();
        /*
        for (AusgabenBudget ab : this.ausgabenBudgetListenSQL) {
            AusgabenBudget abJaehrlich = new AusgabenBudget();
            abJaehrlich.setAusgabenbudget_id(ab.getAusgabenbudget_id());
            abJaehrlich.setBemerkungen(ab.getBemerkungen());
            abJaehrlich.setBetrag(ab.getBetrag());
            abJaehrlich.setKategorie(ab.getKategorie());
            abJaehrlich.setS(ab.getS());
            abJaehrlich.setTatsaechlicheAusgaben(Math.round((ab.getTatsaechlicheAusgaben() * 12) * 100.0) / 100.0);
            abJaehrlich.setDifferenz(Math.round((ab.getDifferenz() * 12) * 100.0) / 100.0);
            abJaehrlich.setBetrag(Math.round((ab.getBetrag() * 12) * 100.0) / 100.0);
            this.ausgabenBudgetJaehrlichListenSQL.add(abJaehrlich);
        }
         */
        this.ausgabenBudgetJaehrlichListenSQL.addAll(this.dao.getAusgabenBudgetDataJahrAlle(this.aktuellesJahr));

        this.filteredAusgabenBudgetJaehrlichListenSQL = new ArrayList<>(this.ausgabenBudgetJaehrlichListenSQL);
    }

    /**
     * Regelmäßige Ausgaben und einmalige Ausgaben die im gleichen Jahr
     * stattgefunden haben.
     */
    public void createAusgabenKalender() {
        //Ausgaben-Kalender

        //Calendar vorlage = Calendar.getInstance();
        //vorlage.set(vorlage.get(Calendar.YEAR), vorlage.get(Calendar.MONTH), vorlage.get(Calendar.DATE), 0, 0, 0);
        this.ausgabenKalender = new DefaultScheduleModel();
        Session s = HibernateUtil.getSessionFactory().openSession();

        String datum;
        Date d = new Date();
        SimpleDateFormat datumParseFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM");

        List<Ausgaben> listenAusgaben = new ArrayList();

        //Einmalige Ausgaben in diesem Jahr
        String sqlstring = "FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(year FROM zahlungsdatum)  = :yearWert";
        Query qu = s.createQuery(sqlstring);
        qu.setInteger("yearWert", Integer.parseInt(sdf.format(d)));
        listenAusgaben.addAll(qu.list());

        ausgabenKalender.addEvent(new DefaultScheduleEvent("[HEUTE]", new Date(), new Date()));

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                ausgabenKalender.addEvent(new DefaultScheduleEvent(a.getBezeichnung() + ": " + a.getBetrag() + "€", a.getZahlungsdatum(), a.getZahlungsdatum()));
            }
        }
        sdf = new SimpleDateFormat("MM.yyyy");
        sdf2 = new SimpleDateFormat("dd");

        //Andere Intervalle
        listenAusgaben = new ArrayList();
        sqlstring = "FROM Ausgaben where ausgabezeitraum='taeglich' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                //Datum auf Kalender adjustieren
                datum = sdf2.format(d);
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[T] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }

        listenAusgaben = new ArrayList();
        sqlstring = "FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                //Datum auf Kalender adjustieren
                datum = sdf2.format(a.getZahlungsdatum()) + "." + sdf.format(d);
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[W] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }

        listenAusgaben = new ArrayList();

        sqlstring = "FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (!listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                datum = sdf2.format(a.getZahlungsdatum()) + "." + sdf.format(d);
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[14] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }

        listenAusgaben = new ArrayList();

        sqlstring = "FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                //Datum auf Kalender adjustieren
                datum = sdf2.format(a.getZahlungsdatum()) + "." + sdf.format(d);
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[M] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }

        listenAusgaben = new ArrayList();
        sdf = new SimpleDateFormat("yyyy");
        sdf2 = new SimpleDateFormat("dd.MM");
        sqlstring = "FROM Ausgaben where ausgabezeitraum='alle 2 Monate' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                datum = sdf2.format(a.getZahlungsdatum()) + "." + sdf.format(d);
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[2M] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }

        listenAusgaben = new ArrayList();

        sqlstring = "FROM Ausgaben where ausgabezeitraum='vierteljaehrlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                //Datum auf Kalender adjustieren
                datum = sdf2.format(a.getZahlungsdatum()) + "." + sdf.format(d);
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[3M] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }

        listenAusgaben = new ArrayList();

        sqlstring = "FROM Ausgaben where ausgabezeitraum='alle 6 Monate' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                //Datum auf Kalender adjustieren
                datum = sdf2.format(a.getZahlungsdatum()) + "." + sdf.format(d);
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[6M] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }

        listenAusgaben = new ArrayList();

        sqlstring = "FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        listenAusgaben.addAll(qu.list());

        if (listenAusgaben != null && !listenAusgaben.isEmpty()) {
            for (Ausgaben a : listenAusgaben) {
                datum = sdf2.format(a.getZahlungsdatum()) + "." + sdf.format(d);
                //Datum auf Kalender adjustieren
                try {
                    ausgabenKalender.addEvent(new DefaultScheduleEvent("[J] " + a.getBezeichnung() + ": " + a.getBetrag() + "€", datumParseFormat.parse(datum), datumParseFormat.parse(datum)));
                } catch (ParseException e) {
                    System.out.println("!!! Ausgaben-Kalender Parse FEHLER: " + e);
                }
            }
        }
        s.close();

    }

    //Für AusgabenKalender
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }

    /**
     * Ausgaben wo einmalige Ausgaben nicht dieses Jahr stattgefunden haben.
     */
    public void showAlteAusgaben() {
        this.jahrmonat = "ALLE";
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        this.aktuellesJahr = Integer.parseInt(sdf.format(d));
        sdf = new SimpleDateFormat("MM");
        this.aktuelleMonat = Integer.parseInt(sdf.format(d));
        this.durchschnittlAusgabenBestimmterMonat = "";
        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenAlle());
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);
        calculateAusgabenJahrSumme();
        this.durchschnittlicheMonatlicheAusgaben = String.format("%.2f", this.ausgabenJaehrlichSumme / 12);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");

        //RequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");

    }

    /**
     * Ausgaben wo einmalige Ausgaben nicht dieses Jahr stattgefunden haben.
     */
    public void showRegelmaessigeAusgaben() {
        this.jahrmonat = "Nur Regelmäßige - durchschnittliche Ausgaben pro Monat: ohne einmaligen Ausgaben";
        this.durchschnittlAusgabenBestimmterMonat = "";
        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenRegelmaessig());
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");
        calculateRegelmaessigeAusgabenJahrSumme();
        this.durchschnittlicheMonatlicheAusgaben = String.format("%.2f", this.ausgabenJaehrlichSumme / 12);

        //PF6: RequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");
    }

    /**
     * Auswahl von Monat und Jahr durch Benutzer über DropDown Liste
     */
    public void changeMonatJahrAnsicht() {

        if (this.selectMonatJahr != null) {

            SimpleDateFormat sdfM = new SimpleDateFormat("M");
            SimpleDateFormat sdfJ = new SimpleDateFormat("yyyy");

            this.jahrmonat = sdfM.format(this.selectMonatJahr) + "/" + sdfJ.format(this.selectMonatJahr);
            sdfM = new SimpleDateFormat("MM");

            this.aktuelleMonat = Integer.parseInt(sdfM.format(this.selectMonatJahr));
            this.aktuellesJahr = Integer.parseInt(sdfJ.format(this.selectMonatJahr));
            Session s = HibernateUtil.getSessionFactory().openSession();

            String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(month FROM zahlungsdatum)  = :monatWert";
            Date d = new Date();
            Query qu = s.createQuery(sqlstring);
            qu.setInteger("monatWert", Integer.parseInt(sdfM.format(this.selectMonatJahr)));
            List<Double> ergebnis = qu.list();
            if (ergebnis != null && !ergebnis.isEmpty() && ergebnis.get(0) != null) {
                this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben + ergebnis.get(0)) + "€";
            } else {
                this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben) + "€";
            }

            this.ausgabenListenSQL.clear();
            this.filteredAusgabenListenSQL.clear();
            this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenCustom(sdfM.format(this.selectMonatJahr), Integer.parseInt(sdfJ.format(this.selectMonatJahr))));
            this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);

            this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");
            //RequestContext.getCurrentInstance().update("tabellenAnsicht");
            PrimeFaces.current().ajax().update("tabellenAnsicht");

            s.close();
        }
    }

    public void showAktuellerMonatAnsicht() {

        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(month FROM zahlungsdatum)  = :monatWert";
        Date d = new Date();
        SimpleDateFormat formatMonat = new SimpleDateFormat("MM");
        Query qu = s.createQuery(sqlstring);
        qu.setInteger("monatWert", Integer.parseInt(formatMonat.format(d)));
        List<Double> ergebnis = qu.list();
        if (ergebnis != null && !ergebnis.isEmpty() && ergebnis.get(0) != null) {
            this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben + ergebnis.get(0)) + "€";
        } else {
            this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben) + "€";
        }
        SimpleDateFormat sdfJ = new SimpleDateFormat("yyyy");

        this.jahrmonat = formatMonat.format(d) + "/" + sdfJ.format(d);
        this.aktuelleMonat = Integer.parseInt(formatMonat.format(d));
        this.aktuellesJahr = Integer.parseInt(sdfJ.format(d));
        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenMonat());
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");

        //RequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");

        s.close();
    }

    public void showAktuellesJahrAnsicht() {
        this.durchschnittlAusgabenBestimmterMonat = " ";
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        this.jahrmonat = sdf.format(d);
        this.aktuellesJahr = Integer.parseInt(sdf.format(d));

        sdf = new SimpleDateFormat("MM");
        this.aktuelleMonat = Integer.parseInt(sdf.format(d));
        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenCustom(null, this.aktuellesJahr));
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);
        calculateAusgabenJahrSumme();

        this.durchschnittlicheMonatlicheAusgaben = String.format("%.2f", this.ausgabenJaehrlichSumme / 12);
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");

        //RequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");

    }

    public void showVorherigerMonatAnsicht() {
        Date d = new Date();
        SimpleDateFormat formatMonat = new SimpleDateFormat("MM");
        SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");

        if (this.aktuelleMonat < 1) {
            this.aktuelleMonat = 12;
        }
        if (this.aktuelleMonat > 12) {
            this.aktuelleMonat = 1;
        }
        this.aktuelleMonat = this.aktuelleMonat - 1;

        this.aktuellesJahr = Integer.parseInt(formatJahr.format(d));
        this.jahrmonat = (this.aktuelleMonat) + "/" + formatJahr.format(d);

        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(month FROM zahlungsdatum)  = :monatWert";
        Query qu = s.createQuery(sqlstring);
        Integer monat = Integer.parseInt(formatMonat.format(d)) - 1;
        if (monat < 1) {
            qu.setInteger("monatWert", 12);
        } else {
            qu.setInteger("monatWert", monat);
        }
        List<Double> ergebnis = qu.list();
        if (ergebnis != null && !ergebnis.isEmpty() && ergebnis.get(0) != null) {
            this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben + ergebnis.get(0)) + "€";
        } else {
            this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben) + "€";
        }

        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenCustom(String.valueOf((this.aktuelleMonat)), this.aktuellesJahr));
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);

        //PrimeRequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");

        s.close();

    }

    public void showVorherigesJahrAnsicht() {
        this.durchschnittlAusgabenBestimmterMonat = "";
        Date d = new Date();
        SimpleDateFormat formatMonat = new SimpleDateFormat("MM");
        this.aktuelleMonat = Integer.parseInt(formatMonat.format(d));
        this.aktuellesJahr = (this.aktuellesJahr - 1);
        this.jahrmonat = "" + (this.aktuellesJahr);

        this.durchschnittlAusgabenBestimmterMonat = "Ausgaben dieses Jahres: " + calculateAusgabenJahrSummeCustom((this.aktuellesJahr)) + "€";
        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenCustom(null, (this.aktuellesJahr)));
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");
        //RequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");

    }

    public void showNaechstesJahrAnsicht() {
        this.durchschnittlAusgabenBestimmterMonat = "Ausgaben dieses Jahres: " + calculateAusgabenJahrSummeCustom((this.aktuellesJahr + 1)) + "€";
        Date d = new Date();
        SimpleDateFormat formatMonat = new SimpleDateFormat("MM");
        this.aktuelleMonat = Integer.parseInt(formatMonat.format(d));
        this.aktuellesJahr = (this.aktuellesJahr + 1);
        this.jahrmonat = "" + (this.aktuellesJahr);
        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenCustom(null, (this.aktuellesJahr)));
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");

        //RequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");

    }

    public void showNaechstenMonatAnsicht() {
        Date d = new Date();
        SimpleDateFormat formatMonat = new SimpleDateFormat("MM");
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(month FROM zahlungsdatum)  = :monatWert";
        Query qu = s.createQuery(sqlstring);
        Integer monat = Integer.parseInt(formatMonat.format(d)) + 1;
        if (monat > 12) {
            qu.setInteger("monatWert", 1);
        } else {
            qu.setInteger("monatWert", monat);
        }

        SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");
        if (this.aktuelleMonat > 12) {
            this.aktuelleMonat = 1;
        }
        this.aktuelleMonat = this.aktuelleMonat + 1;
        this.aktuellesJahr = Integer.parseInt(formatJahr.format(d));

        this.jahrmonat = this.aktuelleMonat + "/" + formatJahr.format(d);
        this.ausgabenListenSQL.clear();
        this.filteredAusgabenListenSQL.clear();
        this.ausgabenListenSQL.addAll(this.dao.getAllAusgabenCustom(String.valueOf((this.aktuelleMonat)), this.aktuellesJahr));
        this.filteredAusgabenListenSQL.addAll(this.ausgabenListenSQL);

        List<Double> ergebnis = qu.list();
        if (ergebnis != null && !ergebnis.isEmpty() && ergebnis.get(0) != null) {
            this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben + ergebnis.get(0)) + "€";
        } else {
            this.durchschnittlAusgabenBestimmterMonat = "Durchschnittliche Ausgaben dieses Monats: " + (this.durchschnittlicheMonatlicheAusgaben) + "€";
        }
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");
        //RequestContext.getCurrentInstance().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsicht");

        s.close();

    }

    /*
    Erstelle Barchart und Tabelle für Auflistung von monatlichen und jaehrlichen Ausgaben
     */
    public void createPortfolioWertinEuro() {
        List<String> bgColors = new ArrayList<>();

        //Monatliche Ausgaben
        this.chartBarAusgabenMonatlich = new DonutChartModel();
        DonutChartDataSet ausgabenMonatlichDataSet = new DonutChartDataSet();
        List<String> monatlichAusgabenKategorieBezeichnungen = new ArrayList<>();
        List<Number> monatlichAusgabenWerte = new ArrayList<>();
        ChartData ausgabenMonatlichData = new ChartData();

        //Jaehrliche Ausgaben
        this.chartBarAusgabenJaehrlich = new DonutChartModel();
        DonutChartDataSet ausgabenJaehrlichDataSet = new DonutChartDataSet();
        List<String> jaehrlichAusgabenKategorieBezeichnungen = new ArrayList<>();
        List<Number> jaehrlichAusgabenWerte = new ArrayList<>();
        ChartData ausgabenJaehrlichData = new ChartData();

        //SQL Abruf ALLE (monatlich und jaehrlich:
        // Liefert alle Einträge (Ausgabenbetrag) für eine Kategorie sortiert
        // aufsteigend nach dem Datum
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Ausgabenkategorie> kategorienliste = this.ausgabenkategorien;
        //Ausgaben (nur regelmässige monatliche und jährlich angerechnet

        Double ausgabenSummeMonat = 0.0;
        Double ausgabenSummeJahr = 0.0;

        try {
            for (Ausgabenkategorie w : kategorienliste) {
                ausgabenSummeMonat = 0.0;
                ausgabenSummeJahr = 0.0;

                //Hintergrundfarben durch Zufall erstellen.
                Random rnd = new Random();
                bgColors.add("rgb(" + rnd.nextInt(240) + "," + rnd.nextInt(240) + "," + rnd.nextInt(240) + ")");

                //Monatliche Berechnung:
                String sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='taeglich' group by kategorie order by kategorie asc";
                Query qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                List<Object[]> kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));

                            //Anzahl von Tagen des Monats und Schaltjahr überprüfen
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                            Date date = new Date();
                            int jahr = Integer.parseInt(sdf.format(date));

                            if (jahr % 4 == 0 && (jahr % 100 != 0 || jahr % 400 == 0)) {
                                ausgabenSummeJahr += Math.round((((Double) o[1] * 366)) * 100.0) / 100.0;
                            } else {
                                ausgabenSummeJahr += Math.round((((Double) o[1] * 365)) * 100.0) / 100.0;
                            }

                            sdf = new SimpleDateFormat("MM");
                            int anzahlMonate = YearMonth.of(jahr, Integer.parseInt(sdf.format(date))).lengthOfMonth();
                            ausgabenSummeMonat += Math.round(((Double) o[1] * anzahlMonate) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='woechentlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));
                            ausgabenSummeMonat += Math.round((((Double) o[1] * 4)) * 100.0) / 100.0;

                            ausgabenSummeJahr += Math.round((((Double) o[1] * 52)) * 100.0) / 100.0;
                        }
                    }
                }
                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='14-taegig' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1] * 2)) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1] * 26)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='monatlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1])) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1] * 12)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='alle 2 Monate' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1] / 2)) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1] * 6)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='vierteljaehrlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1] / 3)) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1] * 4)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='alle 6 Monate' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1] / 6)) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1] * 2)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='jaehrlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1] / 12)) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1])) * 100.0) / 100.0;

                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='alle 2 Jahre' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1] / 24)) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1] * 2)) * 100.0) / 100.0;

                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Ausgaben where kategorie = :kategoriename and deleted=false and ausgabezeitraum='alle 5 Jahre' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getKategoriebezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            ausgabenSummeMonat += Math.round((((Double) o[1] / 60)) * 100.0) / 100.0;
                            ausgabenSummeJahr += Math.round((((Double) o[1] * 5)) * 100.0) / 100.0;
                        }
                    }
                }

                //AusgabeKategorie hinzufügen - standardmäßig ist der AusgabeBetrag 0, wenn keine Summen vorhanden sind.
                AusgabenKategorienBetraege akbMonat = new AusgabenKategorienBetraege();
                AusgabenKategorienBetraege akbJahr = new AusgabenKategorienBetraege();
                akbMonat.setBezeichnung(w.getKategoriebezeichnung());
                akbMonat.setBetrag(0.0);
                akbJahr.setBezeichnung(w.getKategoriebezeichnung());
                akbJahr.setBetrag(0.0);

                if (ausgabenSummeMonat > 0.0) {
                    akbMonat.setBetrag(ausgabenSummeMonat);

                    monatlichAusgabenKategorieBezeichnungen.add(w.getKategoriebezeichnung());
                    monatlichAusgabenWerte.add(ausgabenSummeMonat);
                }

                if (ausgabenSummeJahr > 0.0) {
                    akbJahr.setBetrag(ausgabenSummeJahr);

                    jaehrlichAusgabenKategorieBezeichnungen.add(w.getKategoriebezeichnung());
                    jaehrlichAusgabenWerte.add(ausgabenSummeJahr);
                }
                this.ausgabenKategorienBetragMonatlichListe.add(akbMonat);
                this.ausgabenKategorienBetragJaehrlichListe.add(akbJahr);

            }

            ausgabenMonatlichDataSet.setData(monatlichAusgabenWerte);
            ausgabenJaehrlichDataSet.setData(jaehrlichAusgabenWerte);
            ausgabenMonatlichDataSet.setBackgroundColor(bgColors);
            ausgabenJaehrlichDataSet.setBackgroundColor(bgColors);

            ausgabenMonatlichData.addChartDataSet(ausgabenMonatlichDataSet);
            ausgabenJaehrlichData.addChartDataSet(ausgabenJaehrlichDataSet);
            ausgabenMonatlichData.setLabels(monatlichAusgabenKategorieBezeichnungen);
            ausgabenJaehrlichData.setLabels(jaehrlichAusgabenKategorieBezeichnungen);
            this.chartBarAusgabenMonatlich.setData(ausgabenMonatlichData);
            this.chartBarAusgabenJaehrlich.setData(ausgabenJaehrlichData);
            s.close();

        } catch (Exception e) {
            System.out.println("Fehler in createPortfolioWertinEuro: " + e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Erstellung von Charts: ", "" + e));
            s.close();
        }

        this.filteredAusgabenKategorienBetragMonatlichListe = new ArrayList(this.ausgabenKategorienBetragMonatlichListe);
        this.filteredAusgabenKategorienBetragJaehrlichListe = new ArrayList(this.ausgabenKategorienBetragJaehrlichListe);

    }

    /**
     * Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs wird der Anhang
     * für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<Ausgaben> liste = new ArrayList<>(this.ausgabenListenSQL);
            gefunden:
            for (Ausgaben a : liste) {
                if (a.getAusgaben_id().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getAusgaben_id()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getAusgaben_id()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getAusgaben_id()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateAusgaben(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getAusgaben_id() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getAusgaben_id()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateAusgaben(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getAusgaben_id() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
                    }
                    id_existiert = true;
                    flushAnhang();
                    this.anhangID = "";
                    break gefunden;
                }
            }
            if (!id_existiert) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ID existiert nicht ", "Bitte geben Sie eine existierende ID eines Datensatzes ein"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Keine ID übergeben ", "" + e));

        }
    }

    /**
     * Editor für Zeile aufrufen
     */
    public void editRow(CellEditEvent event) {
        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tabelle Rowkey: ", "" + tabelle.getRowKey()));
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rowindex: ", "" + event.getRowIndex()));
            Ausgaben a = this.dao.getSingleAusgaben((Integer) tabelle.getRowKey()).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Bezeichnung")) {
                a.setBezeichnung((String) event.getNewValue());
            }
            if (spaltenname.equals("Betrag")) {
                a.setBetrag((Double) event.getNewValue());
            }

            if (spaltenname.equals("Kategorie")) {
                String auswahl = (String) event.getNewValue();
                gefunden:
                for (Ausgabenkategorie m : this.ausgabenkategorien) {
                    if (m.getKategoriebezeichnung().equals(auswahl)) {
                        a.setKategorie((String) event.getNewValue());
                        break gefunden;
                    }
                }
            }

            if (spaltenname.equals("Ausgabezeitraum")) {
                String auswahl = (String) event.getNewValue();
                gefunden:
                for (Ausgabenausgabezeitraum m : this.ausgabenausgabezeitraeume) {
                    if (m.getAusgabezeitraumbezeichnung().equals(auswahl)) {
                        a.setAusgabezeitraum((String) event.getNewValue());
                        break gefunden;
                    }
                }
            }

            if (spaltenname.equals("Zahlungsdatum")) {
                if (event.getNewValue() != null) {
                    a.setZahlungsdatum((Date) event.getNewValue());
                }
            }

            if (spaltenname.equals("Informationen")) {
                a.setInformationen((String) event.getNewValue());
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateAusgaben(a);
            updateData();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void editRowAusgabenBudget(CellEditEvent event) {

        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tabelle Rowkey: ", "" + tabelle.getRowKey()));
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rowindex: ", "" + event.getRowIndex()));
            AusgabenBudget a = this.dao.getSingleAusgabenBudget((Integer) tabelle.getRowKey()).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Kategorie")) { //Nur eine View
            }

            if (spaltenname.equals("Budget-Betrag")) {
                a.setBetrag((Double) event.getNewValue());
                a.setDifferenz(((Double) event.getNewValue() - a.getTatsaechlicheAusgaben()));
                if (a.getDifferenz() >= 0) {
                    a.setS("+");
                } else {
                    a.setS("-");
                }
            }
            if (spaltenname.equals("Ausgaben-Betrag")) {//Nur eine View

            }

            if (spaltenname.equals("Bemerkungen")) {
                a.setBemerkungen(String.valueOf(event.getNewValue()));
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateAusgabenBudget(a);
            // anzeigenAlleRegelmaessigAusgabenMonat();
            FacesContext.getCurrentInstance().getExternalContext().redirect("ausgaben_budget.xhtml");
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "OK. Seite wird NEU GELADEN.", ""));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void updateData() {
        this.dao = new DAO();
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        this.ausgabenListenSQL = dao.getAllAusgaben();
        this.ausgabenkategorien = dao.getAllAusgabenkategorie();
        this.ausgabenausgabezeitraeume = dao.getAllAusgabenausgabezeitraum();
        this.filteredAusgabenListenSQL = dao.getAllAusgaben();
        flushAnhang();
        calculateAusgabenMonatSumme();
        calculateAusgabenJahrSumme();
        calculateDifferenzAusgabenEinnahmen();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.ausgabenListenSQL.size() + " Datensaetze in der DB gespeichert");

    }

    public void updateDataAusgabenBudget() {
        this.dao = new DAO();
        this.ausgabenBudgetListenSQL.clear();
        this.ausgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
        this.filteredAusgabenBudgetListenSQL = new ArrayList<>(this.ausgabenBudgetListenSQL);
        flushAnhang();
        showJaehrlichAusgabenBudget();
        PrimeFaces.current().ajax().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsichtJaehrlich");

    }

    public void change_rownumber() {
        flushAnhang();

        FacesContext context = FacesContext.getCurrentInstance();
        Map valueMap = context.getExternalContext().getRequestParameterMap();
        this.rownumbers = Integer.parseInt((String) valueMap.get("newRowsAmount"));
    }

    public void anhangSpeichern(FileUploadEvent event) {
        try {
            InputStream input = event.getFile().getInputstream();
            if (input != null) {

                this.anhang = IOUtils.toByteArray(input);
                this.anhangname = event.getFile().getFileName();
                this.anhangtype = event.getFile().getContentType();

            } else {
                this.anhang = null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + e));
        }

    }

    public void speichernNotiz() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.dbnotizEintrag = notizList.get(0);
        }

        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.dbnotizEintrag != null) {
            //Notiz-Eintrag aktualisieren
            this.dbnotizEintrag.setTabelle(this.tabellenname);
            this.dbnotizEintrag.setDatum(new Date());
            this.dbnotizEintrag.setNotiztext(notiztext);
            this.dao.updateDatenbankNotizen(this.dbnotizEintrag);

        } else {
            //Neuen Notiz-Eintrag erstellen
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext(notiztext);
            this.dao.insertDatenbankNotizen(n);
        }
    }

    public void clearNotizen() {

        //Notiz-DB Eintrag für diese Tabelle schon zuvor erstellt wurde
        if (this.notiztext != null) {
            //Notiz-Eintrag als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext("");
            this.dao.updateDatenbankNotizen(n);
        } else {
            //Neuen Notiz-Eintrag erstellen und als leer speichern
            DatenbankNotizen n = new DatenbankNotizen();
            n.setTabelle(this.tabellenname);
            n.setDatum(new Date());
            n.setNotiztext("");
            this.dao.insertDatenbankNotizen(n);
        }
    }

    public void updatePerson1TeilschluesselView() {
        this.person1aufteilung = 100 - this.person2aufteilung;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Person 2 Aufteilung: ", "" + this.person2aufteilung));

    }

    public void updatePerson2TeilschluesselView() {
        this.person2aufteilung = 100 - this.person1aufteilung;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Person 2 Aufteilung: ", "" + this.person2aufteilung));

    }

    /*
    Teilschlüssel + Personenname berechnen und speichern 
     */
    public void teilbetragEinstellungenSpeichern() {
        calculateGesamtTeilbetrag();
        this.dao = new DAO();
        Appsettings a;
        try {
            a = this.dao.getAllAppsettings().get((this.dao.getAllAppsettings()).size() - 1);
            a.setPerson1aufteilung(this.person1aufteilung);
            a.setPerson1name(this.person1name);
            a.setPerson2aufteilung(this.person2aufteilung);
            a.setPerson2name(this.person2name);
            dao.updateAppsettings(a);

        } catch (NullPointerException e) {
            a = new Appsettings();
            a.setPerson1aufteilung(this.person1aufteilung);
            a.setPerson1name(this.person1name);
            a.setPerson2aufteilung(this.person2aufteilung);
            a.setPerson2name(this.person2name);
            dao.insertAppsettings(a);
        }
        //Teilbeträge der Ausgaben neuberechen
        this.person1gesamtteilbetrag = Double.valueOf(String.format("%.2f", this.ausgabenMonatSumme * this.person1aufteilung));
        this.person2gesamtteilbetrag = Double.valueOf(String.format("%.2f", this.ausgabenMonatSumme * this.person2aufteilung));

    }

    public void speichernPersonname() {
        teilbetragEinstellungenSpeichern();
        this.dao = new DAO();
        Appsettings a;
        try {
            a = this.dao.getAllAppsettings().get((this.dao.getAllAppsettings()).size() - 1);
            a.setPerson1name(this.person1name);
            a.setPerson2name(this.person2name);
            dao.updateAppsettings(a);

        } catch (NullPointerException e) {
            a = new Appsettings();
            a.setPerson1aufteilung(this.person1aufteilung);
            a.setPerson1name(this.person1name);
            a.setPerson2aufteilung(this.person2aufteilung);
            a.setPerson2name(this.person2name);
            dao.insertAppsettings(a);
        }
    }

    public void speichern() {
        Ausgaben ausgabe = new Ausgaben();
        ausgabe.setDeleted(false);
        if (this.bezeichnung != null) {
            ausgabe.setBezeichnung(bezeichnung);
        }

        ausgabe.setBetrag(betrag);

        if (this.ausgabenkategorie != null) {
            ausgabe.setKategorie(this.ausgabenkategorie.getKategoriebezeichnung());
        }

        if (this.ausgabenausgabezeitraum != null) {
            ausgabe.setAusgabezeitraum(this.ausgabenausgabezeitraum.getAusgabezeitraumbezeichnung());
        } else {
            ausgabe.setAusgabezeitraum("einmalig");
        }

        if (this.zahlungsdatum != null) {
            ausgabe.setZahlungsdatum(zahlungsdatum);
        } else {
            ausgabe.setZahlungsdatum(new Date());
        }

        if (this.informationen != null) {
            ausgabe.setInformationen(informationen);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            ausgabe.setAnhang(true);
            //IMMER VOR DEM INSERT BEFEHL
            //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
            this.ausgabenListenSQL.add(ausgabe);
            this.filteredAusgabenListenSQL.add(ausgabe);

            dao.insertAusgaben(ausgabe);

            List<Ausgaben> ausgabenListe = new ArrayList<>(this.ausgabenListenSQL);
            int letzteNr = ausgabenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = ausgabenListe.get(letzteNr).getAusgaben_id();
                try {
                    Ausgaben a = ausgabenListe.get(letzteNr);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    InputStream ins = new ByteArrayInputStream(this.anhang);
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);

                    PutMethod method = new PutMethod(this.baseUrl + "/" + (neueID) + "." + dateiext);
                    RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                    method.setRequestEntity(requestEntity);
                    client.executeMethod(method);
                    System.out.println(method.getStatusCode() + " " + method.getStatusText());
                    a.setAnhangpfad(this.downloadUrl + "/" + (neueID) + "." + dateiext);

                    a.setAnhang(true);
                    a.setAnhangname((neueID) + "." + dateiext);
                    a.setAnhangtype(anhangtype);

                    dao.updateAusgaben(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }
                updateData();

            }

        } else {
            ausgabe.setAnhang(false);
            this.ausgabenListenSQL.add(ausgabe);
            this.filteredAusgabenListenSQL.add(ausgabe);

            dao.insertAusgaben(ausgabe);
            updateData();
        }
    }

    /**
     * Methode nach dem Speichern
     */
    public void flushAnhang() {
        this.anhang = null;
        this.anhangname = "";
        this.anhangtype = "";

    }

    public void kategorieSpeichern() {

        if (!this.neuAusgabenkategorie.isEmpty()) {
            Ausgabenkategorie ak = new Ausgabenkategorie();
            ak.setKategoriebezeichnung(this.neuAusgabenkategorie);
            dao.insertAusgabenkategorie(ak);
            updateData();
        }
    }

    /**
     * Ausgaben-Kategorie löschen und Datensätze mit der Kategorie, die gelöscht
     * wird, bekommen eine neue Kategorie (Benutzer entscheidet, leer oder neue
     * Kat-Bez.) zugewiesen
     */
    public void kategorieLoeschen() {

        if (this.deleteAusgabenkategorie != null) {

            List<Ausgabenkategorie> akList = dao.getAllAusgabenkategorie();
            List<Ausgaben> ausgabenList = dao.getAllAusgaben();
            boolean kategorieExist = false;

            for (Ausgabenkategorie a : akList) {
                if ((a.getKategoriebezeichnung().toLowerCase()).equals(this.deleteAusgabenkategorie.getKategoriebezeichnung().toLowerCase())) {
                    //Ausgaben-Kategorie löschen
                    dao.deleteAusgabenkategorie(a);
                    for (Ausgaben ausgabe : ausgabenList) {
                        if ((ausgabe.getKategorie().toLowerCase()).equals(this.deleteAusgabenkategorie.getKategoriebezeichnung().toLowerCase())) {
                            this.ausgabenListenSQL.remove(ausgabe);
                            this.filteredAusgabenListenSQL.remove(ausgabe);
                            ausgabe.setKategorie(this.change_Kategorie);
                            dao.updateAusgaben(ausgabe);
                            this.ausgabenListenSQL.add(ausgabe);
                            this.filteredAusgabenListenSQL.add(ausgabe);
                        }
                    }
                }
                if ((a.getKategoriebezeichnung().toLowerCase()).equals(this.change_Kategorie.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) { //Benutzer möchte Kategorien von betroffenen Datensätze
                //(die die gelöschte Kategorie benutzen) auf eine neue Kategorie ändern, die auch hinzugefügt wird. 
                Ausgabenkategorie neu = new Ausgabenkategorie();
                neu.setKategoriebezeichnung(this.change_Kategorie);
                dao.insertAusgabenkategorie(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void ausgabezeitraumLoeschen() {

        if (this.deleteAusgabenausgabezeitraum != null) {

            List<Ausgabenausgabezeitraum> akList = dao.getAllAusgabenausgabezeitraum();
            List<Ausgaben> ausgabenList = dao.getAllAusgaben();
            boolean kategorieExist = false;
            for (Ausgabenausgabezeitraum a : akList) {
                if ((a.getAusgabezeitraumbezeichnung().toLowerCase()).equals(this.deleteAusgabenausgabezeitraum.getAusgabezeitraumbezeichnung().toLowerCase())) {

                    dao.deleteAusgabenausgabezeitraum(a);
                    for (Ausgaben ausgabe : ausgabenList) {
                        if ((ausgabe.getAusgabezeitraum().toLowerCase()).equals(this.deleteAusgabenausgabezeitraum.getAusgabezeitraumbezeichnung().toLowerCase())) {
                            this.ausgabenListenSQL.remove(ausgabe);
                            this.filteredAusgabenListenSQL.remove(ausgabe);
                            ausgabe.setAusgabezeitraum(this.change_ausgabezeitraum);
                            dao.updateAusgaben(ausgabe);
                            this.ausgabenListenSQL.add(ausgabe);
                            this.filteredAusgabenListenSQL.add(ausgabe);
                        }
                    }

                }
                if ((a.getAusgabezeitraumbezeichnung().toLowerCase()).equals(this.change_ausgabezeitraum.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                Ausgabenausgabezeitraum neu = new Ausgabenausgabezeitraum();
                neu.setAusgabezeitraumbezeichnung(this.change_ausgabezeitraum);
                dao.insertAusgabenausgabezeitraum(neu);
            }

            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));
            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void ausgabezeitraumSpeichern() {

        if (!this.neuAusgabenausgabezeitraum.isEmpty()) {
            Ausgabenausgabezeitraum aaz = new Ausgabenausgabezeitraum();
            aaz.setAusgabezeitraumbezeichnung(neuAusgabenausgabezeitraum);
            dao.insertAusgabenausgabezeitraum(aaz);
            updateData();
            //        throw new RuntimeException("DEBUG Zeitraum: : " + this.neuAusgabenausgabezeitraum);

        }
    }

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedAusgabenListenSQL.isEmpty()) {
            for (Ausgaben a : this.deletedAusgabenListenSQL) {
                this.ausgabenListenSQL.add(a);
                this.filteredAusgabenListenSQL.add(a);
                a.setDeleted(false);
                dao.updateAusgaben(a);
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));

        }
        //  return "ausgaben.xhtml";

    }

    /**
     * Change / Wechsel zu regelmäßigen oder allen Ausgaben. Was der Benutzer im
     * Switch-Button angeklickt hat
     */
    public void anzeigenAlleRegelmaessigAusgabenMonat() {
        this.dao = new DAO();
        this.ausgabenBudgetListenSQL.clear();
        this.filteredAusgabenBudgetListenSQL.clear();

        if (this.alleAusgabenAnzeigen) {
            this.differenzAusgabenBudgetMonat = String.format("%.2f", (this.ausgabenBudgetSumme) - this.alleAusgabenMonatSumme);
            this.ausgabenBudgetUeberschrift = "(" + this.aktuelleMonat + "/" + this.aktuellesJahr + ")" + " Alle";
            this.alleAusgabenMonatSummeAusgabe = String.format("%.2f", this.alleAusgabenMonatSumme);
            calculateDifferenzAusgabenEinnahmenAlle();
            //AusgabenBudget Kategorien (Datensätze) erstellen/aktualisieren - Holt Werte von Ausgaben Tabelle
            dao.updateAusgabenBudgetDataMonatAlle();

        } else {
            this.ausgabenBudgetUeberschrift = "(" + this.aktuelleMonat + "/" + this.aktuellesJahr + ")" + " Regelmäßig";
            this.differenzAusgabenBudgetMonat = String.format("%.2f", (this.ausgabenBudgetSumme) - this.ausgabenMonatSumme);
            this.alleAusgabenMonatSummeAusgabe = String.format("%.2f", this.ausgabenMonatSumme);
            calculateDifferenzAusgabenEinnahmen();
            //AusgabenBudget Kategorien (Datensätze) erstellen/aktualisieren - Holt Werte von Ausgaben Tabelle
            dao.updateAusgabenBudgetDataMonatRegelmaessig();
        }
        this.ausgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
        this.filteredAusgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
        flushAnhang();
        PrimeFaces.current().ajax().update("tabellenAnsicht");
        PrimeFaces.current().ajax().update("tabellenAnsichtJaehrlich");
    }

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (Ausgaben a : this.ausgabenListenSQL) {
                    if (a.getAusgaben_id().equals(Integer.parseInt(this.deleteID))) {
                        this.deletedAusgabenListenSQL.add(a);
                        dao.deleteAusgaben(a);
                        this.ausgabenListenSQL.remove(a);
                        this.filteredAusgabenListenSQL.remove(a);
                        break gefunden;
                    }
                }
                updateData();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
        //     return "ausgaben.xhtml";

    }
    
        public void datensatzLoeschen(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (Ausgaben a : this.ausgabenListenSQL) {
                    if (a.getAusgaben_id().equals(id)) {
                        this.deletedAusgabenListenSQL.add(a);
                        dao.deleteAusgaben(a);
                        this.ausgabenListenSQL.remove(a);
                        this.filteredAusgabenListenSQL.remove(a);
                        break gefunden;
                    }
                }
                updateData();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datemsatz konnte nicht gelöscht werden!", "FEHLER: " + e));
        }
        //     return "ausgaben.xhtml";

    }

    /**
     * Für AusgabenBudget: Monat und Jahr wechseln. Was der Benutzer eingegben
     * hat
     */
    public void changeMonthYear() {
        if (this.alleAusgabenAnzeigen) {
            this.dao = new DAO();
            this.ausgabenBudgetListenSQL.clear();
            this.filteredAusgabenBudgetListenSQL.clear();

            this.dao.updateAusgabenBudgetDataMonatAlleCustom(this.aktuelleMonat, this.aktuellesJahr);

            this.differenzAusgabenBudgetMonat = String.format("%.2f", (this.ausgabenBudgetSumme) - this.alleAusgabenMonatSumme);
            this.ausgabenBudgetUeberschrift = "(" + this.aktuelleMonat + "/" + this.aktuellesJahr + ")" + " Alle";
            this.alleAusgabenMonatSummeAusgabe = String.format("%.2f", this.alleAusgabenMonatSumme);

            this.ausgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
            this.filteredAusgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
            flushAnhang();
            showJaehrlichAusgabenBudget();
            PrimeFaces.current().ajax().update("tabellenAnsicht");
            PrimeFaces.current().ajax().update("tabellenAnsichtJaehrlich");
        }

    }

    public void changetoprevmonth() {
        if (this.alleAusgabenAnzeigen) {
            if (this.aktuelleMonat == 1) {
                this.aktuelleMonat = 12;
                this.aktuellesJahr = this.aktuellesJahr - 1;
            } else {
                this.aktuelleMonat = this.aktuelleMonat - 1;
            }

            this.dao = new DAO();
            this.ausgabenBudgetListenSQL.clear();
            this.filteredAusgabenBudgetListenSQL.clear();

            this.dao.updateAusgabenBudgetDataMonatAlleCustom(this.aktuelleMonat, this.aktuellesJahr);

            this.differenzAusgabenBudgetMonat = String.format("%.2f", (this.ausgabenBudgetSumme) - this.alleAusgabenMonatSumme);
            this.ausgabenBudgetUeberschrift = "(" + this.aktuelleMonat + "/" + this.aktuellesJahr + ")" + " Alle";
            this.alleAusgabenMonatSummeAusgabe = String.format("%.2f", this.alleAusgabenMonatSumme);

            this.ausgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
            this.filteredAusgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
            flushAnhang();
            showJaehrlichAusgabenBudget();
            PrimeFaces.current().ajax().update("tabellenAnsicht");
            PrimeFaces.current().ajax().update("tabellenAnsichtJaehrlich");
        }

    }

    public void changetonextmonth() {
        if (this.alleAusgabenAnzeigen) {
            if (this.aktuelleMonat == 12) {
                this.aktuelleMonat = 1;
                this.aktuellesJahr = this.aktuellesJahr + 1;
            } else {
                this.aktuelleMonat = this.aktuelleMonat + 1;
            }

            this.dao = new DAO();
            this.ausgabenBudgetListenSQL.clear();
            this.filteredAusgabenBudgetListenSQL.clear();

            this.dao.updateAusgabenBudgetDataMonatAlleCustom(this.aktuelleMonat, this.aktuellesJahr);

            this.differenzAusgabenBudgetMonat = String.format("%.2f", (this.ausgabenBudgetSumme) - this.alleAusgabenMonatSumme);
            this.ausgabenBudgetUeberschrift = "(" + this.aktuelleMonat + "/" + this.aktuellesJahr + ")" + " Alle";
            this.alleAusgabenMonatSummeAusgabe = String.format("%.2f", this.alleAusgabenMonatSumme);

            this.ausgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
            this.filteredAusgabenBudgetListenSQL.addAll(dao.getAllAusgabenBudget());
            flushAnhang();
            showJaehrlichAusgabenBudget();
            PrimeFaces.current().ajax().update("tabellenAnsicht");
            PrimeFaces.current().ajax().update("tabellenAnsichtJaehrlich");
        }

    }

    /**
     *
     * Für AusgabenBudget Ausgaben (einmalige und regelmaessige) des laufenden
     * Monats und Jahres summieren, dazu auch die Anteile für jährliche(auch
     * vierteljährliche, etc. - Ausgaben: ausserhalb von Monatszyklen) anrechnen
     * und in die Variable alleAusgabenMonatSumme für AusgabenBudget speichern.
     */
    public void calculateAusgabenDesLaufendenMonatJahr() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date date = new Date();
        double jaehrlicheAusgabenRegelmaessig = 0.0;
        double monatlicheAusgabenRegelmaessig = 0.0;
        double jaehrlicheAusgabenAlle = 0.0;
        double monatlicheAusgabenAlle = 0.0;

        //einmalige Ausgaben in diesem Monat summieren:
        String sqlstring = "Select sum(betrag) FROM Ausgaben where (ausgabezeitraum='einmalig' and deleted=false) and (EXTRACT(month FROM zahlungsdatum)  = :monthWert)";
        Query qu = s.createQuery(sqlstring);

        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        qu.setInteger("monthWert", Integer.parseInt(sdf.format(date)));
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += waehrunggruppe.get(0);
        }

        // einmalige Ausgaben in diesem Jahr summieren:
        sqlstring = "Select sum(betrag) FROM Ausgaben where (ausgabezeitraum='einmalig' and deleted=false) and (EXTRACT(year FROM zahlungsdatum)  = :yearWert)";
        qu = s.createQuery(sqlstring);
        sdf = new SimpleDateFormat("yyyy");
        qu.setInteger("yearWert", Integer.parseInt(sdf.format(date)));
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            jaehrlicheAusgabenAlle += waehrunggruppe.get(0);
        }

        //taegliche Ausgaben
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='taeglich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {

            //für jaehrliche Ausgaben
            //Überprüfung auf Schaltjahr
            sdf = new SimpleDateFormat("yyyy");
            int jahr = Integer.parseInt(sdf.format(date));
            if (jahr % 4 == 0 && (jahr % 100 != 0 || jahr % 400 == 0)) {
                jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 366);
                jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 366);
            } else {
                jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 365);
                jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 366);
            }

            //Für monatliche Ausgaben - Anzahl von Tagen des Monats und Schaltjahr überprüfen
            sdf = new SimpleDateFormat("MM");
            int anzahlMonate = YearMonth.of(jahr, Integer.parseInt(sdf.format(date))).lengthOfMonth();
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) * anzahlMonate);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * anzahlMonate);
        }

        //woechentliche Ausgaben
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) * 4);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 4);

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 52);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 52);
        }

        //AusgabenMonat Summe + für AusgabenJaehrlich aufsummieren
        //14-tägige Ausgaben + für AusgabenJaehrlich aufsummieren
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) * 2);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 2);

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 26);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 26);
        }

        //monatliche Ausgaben
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0));
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0));

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 12);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 12);
        }

        //jedes 2.Monate Einnahmen
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Monate' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) / 2);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 2);

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 6);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 6);
        }

        //vierteljährlich - alle 3 Monate
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='vierteljaehrlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) / 3);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 3);

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 4);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 4);
        }

        //halbjährlich - alle 6 Monate
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 6 Monate' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) / 6);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 6);

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) * 2);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) * 2);
        }

        //jaehrliche Ausgaben
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) / 12);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 12);

            jaehrlicheAusgabenAlle += waehrunggruppe.get(0);
            jaehrlicheAusgabenRegelmaessig += waehrunggruppe.get(0);
        }

        //alle 2 Jahre Ausgaben
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Jahre' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) / 24);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 24);

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) / 2);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 2);
        }

        //alle 5 Jahre Ausgaben
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 5 Jahre' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            monatlicheAusgabenAlle += (waehrunggruppe.get(0) / 60);
            monatlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 60);

            jaehrlicheAusgabenAlle += (waehrunggruppe.get(0) / 5);
            jaehrlicheAusgabenRegelmaessig += (waehrunggruppe.get(0) / 5);
        }

        this.alleAusgabenMonatSumme = Math.round((monatlicheAusgabenAlle) * 100.0) / 100.0;
        this.alleAusgabenJahrSumme = Math.round((jaehrlicheAusgabenAlle) * 100.0) / 100.0;
        this.ausgabenMonatSumme = Math.round((monatlicheAusgabenRegelmaessig) * 100.0) / 100.0;
        this.ausgabenJaehrlichSumme = Math.round((jaehrlicheAusgabenRegelmaessig) * 100.0) / 100.0;

        s.close();
        this.alleAusgabenMonatSummeAusgabe = String.format("%.2f", monatlicheAusgabenAlle);
        this.alleAusgabenJahrSummeAusgabe = String.format("%.2f", jaehrlicheAusgabenAlle);
        this.regelmaessigeAusgabenMonatSummeAusgabe = String.format("%.2f", monatlicheAusgabenRegelmaessig);
        this.regelmaessigeAusgabenJahrSummeAusgabe = String.format("%.2f", monatlicheAusgabenRegelmaessig);

    }

    public void calculateAusgabenMonatSumme() {
        this.ausgabenMonatSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=false";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.ausgabenMonatSumme += waehrunggruppe.get(0);
        }

        //woechentliche Einnahmen
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=false";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.ausgabenMonatSumme += (waehrunggruppe.get(0) * 4);
        }

        //14-tägige Einnahmen
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=false";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.ausgabenMonatSumme += (waehrunggruppe.get(0) * 2);
        }

        //taegliche Ausgaben
        sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='taeglich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {

            //Anzahl von Tagen des Monats überprüfen und auf Schaltjahr überprüfen
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date date = new Date();
            int jahr = Integer.parseInt(sdf.format(date));
            sdf = new SimpleDateFormat("MM");
            int anzahlMonate = YearMonth.of(jahr, Integer.parseInt(sdf.format(date))).lengthOfMonth();
            this.ausgabenMonatSumme += (waehrunggruppe.get(0) * anzahlMonate);
        }
        this.ausgabenMonatSumme = Math.round((this.ausgabenMonatSumme) * 100.0) / 100.0;
        s.close();

    }

    public Double calculateAusgabenJahrSummeCustom(int jahr) {
        Double ausgabenJaehrlichSummeCustom = 0.0;
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {

            //jaehrliche Ausgaben
            String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += waehrunggruppe.get(0);
            }

            //alle 2 Jahre Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Jahre' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) / 2);
            }

            //alle 5 Jahre Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 5 Jahre' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) / 5);
            }

            //taegliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='taeglich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {

                //Überprüfung auf Schaltjahr
                if (jahr % 4 == 0 && (jahr % 100 != 0 || jahr % 400 == 0)) {
                    this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 366);
                } else {
                    this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 365);
                }
            }

            //Ausgaben, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(year FROM zahlungsdatum)  = :jahrWert";
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", jahr);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0));
            }

            //woechentliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 52);
            }

            //14-tägige Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 24);
            }
            //monatliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 12);
            }
            //jedes 2.Monate Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 6);
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 4);
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 2);
            }

            s.close();
            this.ausgabenJaehrlichSumme = Math.round((this.ausgabenJaehrlichSumme) * 100.0) / 100.0;
        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Ausgaben-Custom-Berechnung Summe: ", "" + e));

        }
        return ausgabenJaehrlichSummeCustom;
    }

    public void loadAppsettingsAusgaben() {

        this.dao = new DAO();
        List<Appsettings> appsettingsList = this.dao.getAllAppsettings();
        if (appsettingsList != null && appsettingsList.size() > 0) {
            Appsettings a = appsettingsList.get(appsettingsList.size() - 1);
            this.person1aufteilung = a.getPerson1aufteilung();
            this.person1name = a.getPerson1name();
            this.person2aufteilung = a.getPerson2aufteilung();
            this.person2name = a.getPerson2name();
            //Teilbeträge der Ausgaben neuberechen
            this.person1gesamtteilbetrag = Double.valueOf(String.format("%.2f", this.ausgabenMonatSumme * (a.getPerson1aufteilung() / 100.0)));
            this.person2gesamtteilbetrag = Double.valueOf(String.format("%.2f", this.ausgabenMonatSumme * (a.getPerson2aufteilung() / 100.0)));

        } else {
            Appsettings a = new Appsettings();
            a.setPerson1aufteilung(this.person1aufteilung);
            a.setPerson1name(this.person1name);
            a.setPerson2aufteilung(this.person2aufteilung);
            a.setPerson2name(this.person2name);
            dao.insertAppsettings(a);
        }

    }

    public void calculateTeilschlüssel() {

        if (this.person1einkommen > 0 || this.person2einkommen > 0) {
            if (this.person1einkommen <= 0) {
                this.person1aufteilung = 0;
                this.person2aufteilung = 100;

            } else if (this.person2einkommen <= 0) {
                this.person2aufteilung = 0;
                this.person1aufteilung = 100;

            } else {
                //Verhältnis berechnen für das kleinere Einkommen - und der Teilschlüssel für das größere Einkommen aus dem Teilschlüssel des kleineren Einkommen berechnen
                if (this.person1einkommen > this.person2einkommen) {
                    this.person2aufteilung = (int) ((this.person2einkommen / this.person1einkommen) * 100);
                    this.person1aufteilung = 100 - this.person2aufteilung;
                } else {
                    this.person1aufteilung = (int) ((this.person1einkommen / this.person2einkommen) * 100);
                    this.person2aufteilung = 100 - this.person1aufteilung;
                }
                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Berechnung: Einkommen PA: " + this.person1einkommen + " PB: " + this.person2einkommen + " TA und TB: " + this.person1aufteilung + " - " + this.person2aufteilung, ""));
            }
        }
    }

    public void calculateAusgabenBudgetSumme() {
        this.ausgabenBudgetSumme = 0.0;
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM AusgabenBudget";
            Query qu = s.createQuery(sqlstring);
            List<Double> summeGruppe = qu.list();

            if (summeGruppe.get(0) != null) {
                this.ausgabenBudgetSumme += summeGruppe.get(0);
            }
            s.close();
            this.ausgabenBudgetSumme = Math.round((this.ausgabenBudgetSumme) * 100.0) / 100.0;
        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Ausgaben-Budget-Berechnung Summe: ", "" + e));

        }
    }

    /**
     * + Anteilberechnung für monatliche Ausgaben + einmalige Ausgaben im
     * betreffenden Jahr
     */
    public void calculateAusgabenJahrSumme() {
        this.ausgabenJaehrlichSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Ausgaben
            String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0)) * 100.0) / 100.0;
            }

            //alle 2 Jahre Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Jahre' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) / 2) * 100.0) / 100.0;

            }

            //alle 5 Jahre Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 5 Jahre' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) / 5) * 100.0) / 100.0;
            }

            //taegliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='taeglich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {

                //Überprüfung auf Schaltjahr
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                Date date = new Date();
                int jahr = Integer.parseInt(sdf.format(date));
                if (jahr % 4 == 0 && (jahr % 100 != 0 || jahr % 400 == 0)) {
                    this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 366) * 100.0) / 100.0;

                } else {
                    this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 366) * 100.0) / 100.0;
                }
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(year FROM zahlungsdatum)  = :jahrWert";
            Date d = new Date();
            SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", Integer.parseInt(formatJahr.format(d)));
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0)) * 100.0) / 100.0;
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 52) * 100.0) / 100.0;
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 24) * 100.0) / 100.0;
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 12) * 100.0) / 100.0;
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 6) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 4) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 2) * 100.0) / 100.0;
            }

            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Ausgaben-Berechnung Summe: ", "" + e));

        }
    }

    /**
     * + Anteilberechnung für monatliche Ausgaben. Ohne einmalige Ausgaben
     */
    public void calculateAusgabenJahrOhneEinmaligeSumme() {
        this.ausgabenJaehrlichSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0)) * 100.0) / 100.0;
            }

            //taegliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='taeglich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {

                //Überprüfung auf Schaltjahr
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                int jahr = Integer.parseInt(sdf.format(date));
                if (jahr % 4 == 0 && (jahr % 100 != 0 || jahr % 400 == 0)) {
                    this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 366) * 100.0) / 100.0;
                } else {
                    this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 365) * 100.0) / 100.0;
                }
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 52) * 100.0) / 100.0;
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 24) * 100.0) / 100.0;
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 12) * 100.0) / 100.0;
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 6) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 4) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 2) * 100.0) / 100.0;
            }

            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Ausgaben-Berechnung Summe: ", "" + e));

        }

    }

    /**
     *
     */
    public void calculateRegelmaessigeAusgabenJahrSumme() {
        this.ausgabenJaehrlichSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Ausgaben
            String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0)) * 100.0) / 100.0;
            }

            //taegliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='taeglich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {

                //Überprüfung auf Schaltjahr
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                Date date = new Date();
                int jahr = Integer.parseInt(sdf.format(date));
                if (jahr % 4 == 0 && (jahr % 100 != 0 || jahr % 400 == 0)) {
                    this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 366) * 100.0) / 100.0;
                } else {
                    this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 365) * 100.0) / 100.0;
                }
            }

            //woechentliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 52) * 100.0) / 100.0;
            }

            //14-tägige Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 24) * 100.0) / 100.0;
            }
            //monatliche Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 12) * 100.0) / 100.0;
            }
            //jedes 2.Monate Ausgaben
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 6) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 4) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += Math.round((waehrunggruppe.get(0) * 2) * 100.0) / 100.0;
            }

            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Ausgaben-Berechnung Regelmaessige Summe: ", "" + e));

        }

    }

    public void calculateEinnahmenMonatSumme() {
        this.einnahmenMonatSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += Math.round((waehrunggruppe.get(0)) * 100.0) / 100.0;
        }
        //woechentliche Einnahmen
        sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += Math.round(((waehrunggruppe.get(0) * 4)) * 100.0) / 100.0;
        }

        //14-tägige Einnahmen
        sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += Math.round(((waehrunggruppe.get(0) * 2)) * 100.0) / 100.0;
        }

        s.close();

    }

    public void calculateEinnahmenJahrSumme() {
        this.einnahmenJaehrlichSumme = 0.0;
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0))) * 100.0) / 100.0;
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='einmalig' and deleted=False and EXTRACT(year FROM eingangsdatum)  = :jahrWert";
            Date d = new Date();
            SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", Integer.parseInt(formatJahr.format(d)));
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0))) * 100.0) / 100.0;
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0)) * 52) * 100.0) / 100.0;
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0)) * 24) * 100.0) / 100.0;
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0)) * 12) * 100.0) / 100.0;
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0)) * 6) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0)) * 4) * 100.0) / 100.0;
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += Math.round(((waehrunggruppe.get(0)) * 2) * 100.0) / 100.0;
            }

            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Einnahmen-Berechnung Summe: ", "" + e));

        }

    }

    /**
     * Rechnet den monatlichen Gewinn/Ertrag - !!! calculateAusgaben.. und
     * calculateEinnahmen... müssen zuvor schon im init() aufgerufen worden sein
     */
    public void calculateDifferenzAusgabenEinnahmen() {
        this.gewinnMonatlich = String.format("%.2f", this.einnahmenMonatSumme - this.ausgabenMonatSumme);
        this.gewinnJaehrlich = String.format("%.2f", this.einnahmenJaehrlichSumme - this.ausgabenJaehrlichSumme);
        this.durchschnittlicheMonatlicheAusgaben = String.format("%.2f", this.ausgabenJaehrlichSumme / 12);
    }

    /**
     * Rechnet den monatlichen Gewinn/Ertrag - !!! calculateAusgaben.. und
     * calculateEinnahmen... müssen zuvor schon im init() aufgerufen worden sein
     */
    public void calculateDifferenzAusgabenEinnahmenAlle() {
        this.gewinnMonatlich = String.format("%.2f", this.einnahmenMonatSumme - this.alleAusgabenMonatSumme);
        this.gewinnJaehrlich = String.format("%.2f", this.einnahmenJaehrlichSumme - this.alleAusgabenJahrSumme);
        this.durchschnittlicheMonatlicheAusgaben = String.format("%.2f", this.ausgabenJaehrlichSumme / 12);
    }

    public void onToggle(ToggleEvent e) {
        this.columnList.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    //BIETE DIESE IMMER ÜBERPRÜFEN:
    public void scrollTop() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("ueberschriftPanel");
        PrimeFaces.current().scrollTo("ueberschriftPanel");
    }

    public void scrollTabelle() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("tabellenAnsicht:listenForm");
        PrimeFaces.current().scrollTo("listenForm:tabelleausgabenPanel");

    }

    public void scrollHinzufuegen() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("hinzufuegenForm:neuenDatensatzFormular");
        PrimeFaces.current().scrollTo("hinzufuegenForm:neuenDatensatzFormular");
    }

    public void scrollKategorieAdd() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("kategorieForm:kategorieAddPanel");
        PrimeFaces.current().scrollTo("kategorieForm:kategorieAddPanel");

    }

    public void scrollKategorieDel() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("del_kategorieForm:kategorieDelPanel");
        PrimeFaces.current().scrollTo("del_kategorieForm:kategorieDelPanel");

    }

    public void scrollAnhangEdit() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("neuAnhangForm:AnhangEditPanel");
        PrimeFaces.current().scrollTo("neuAnhangForm:AnhangEditPanel");
    }

    public Integer getRownumbers() {
        return rownumbers;
    }

    public void setRownumbers(Integer rownumbers) {
        this.rownumbers = rownumbers;
    }

    public String getZahlungsdatumEingabe() {
        return zahlungsdatumEingabe;
    }

    public void setZahlungsdatumEingabe(String zahlungsdatumEingabe) {
        this.zahlungsdatumEingabe = zahlungsdatumEingabe;
    }

    public String getNeuAusgabenausgabezeitraum() {
        return neuAusgabenausgabezeitraum;
    }

    public void setNeuAusgabenausgabezeitraum(String neuAusgabenausgabezeitraum) {
        this.neuAusgabenausgabezeitraum = neuAusgabenausgabezeitraum;
    }

    public String getNeuAusgabenkategorie() {
        return neuAusgabenkategorie;
    }

    public void setNeuAusgabenkategorie(String neuAusgabenkategorie) {
        this.neuAusgabenkategorie = neuAusgabenkategorie;
    }

    public String getDeleteID() {
        return deleteID;
    }

    public void setDeleteID(String deleteID) {
        this.deleteID = deleteID;
    }

    public Ausgabenausgabezeitraum getAusgabenausgabezeitraum() {
        return ausgabenausgabezeitraum;
    }

    public void setAusgabenausgabezeitraum(Ausgabenausgabezeitraum ausgabenausgabezeitraum) {
        this.ausgabenausgabezeitraum = ausgabenausgabezeitraum;
    }

    public Ausgabenkategorie getAusgabenkategorie() {
        return ausgabenkategorie;
    }

    public void setAusgabenkategorie(Ausgabenkategorie ausgabenkategorie) {
        this.ausgabenkategorie = ausgabenkategorie;
    }

    public List<Ausgabenkategorie> getAusgabenkategorien() {
        return ausgabenkategorien;
    }

    public void setAusgabenkategorien(List<Ausgabenkategorie> ausgabenkategorien) {
        this.ausgabenkategorien = ausgabenkategorien;
    }

    public List<Ausgabenausgabezeitraum> getAusgabenausgabezeitraeume() {
        return ausgabenausgabezeitraeume;
    }

    public void setAusgabenausgabezeitraeume(List<Ausgabenausgabezeitraum> ausgabenausgabezeitraeume) {
        this.ausgabenausgabezeitraeume = ausgabenausgabezeitraeume;
    }

    public List<Ausgaben> getAusgabenListenSQL() {
        return ausgabenListenSQL;
    }

    public void setAusgabenListenSQL(List<Ausgaben> ausgabenListenSQL) {
        this.ausgabenListenSQL = ausgabenListenSQL;
    }

    public List<Ausgaben> getFilteredAusgabenListenSQL() {
        return filteredAusgabenListenSQL;
    }

    public void setFilteredAusgabenListenSQL(List<Ausgaben> filteredAusgabenListenSQL) {
        this.filteredAusgabenListenSQL = filteredAusgabenListenSQL;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getAusgabenBudgetUeberschrift() {
        return ausgabenBudgetUeberschrift;
    }

    public void setAusgabenBudgetUeberschrift(String ausgabenBudgetUeberschrift) {
        this.ausgabenBudgetUeberschrift = ausgabenBudgetUeberschrift;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public String getDatumNotiztext() {
        return datumNotiztext;
    }

    public void setDatumNotiztext(String datumNotiztext) {
        this.datumNotiztext = datumNotiztext;
    }

    public Date getZahlungsdatum() {
        return zahlungsdatum;
    }

    public void setZahlungsdatum(Date zahlungsdatum) {
        this.zahlungsdatum = zahlungsdatum;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    public String getChange_Kategorie() {
        return change_Kategorie;
    }

    public void setChange_Kategorie(String change_Kategorie) {
        this.change_Kategorie = change_Kategorie;
    }

    public String getChange_ausgabezeitraum() {
        return change_ausgabezeitraum;
    }

    public void setChange_ausgabezeitraum(String change_ausgabezeitraum) {
        this.change_ausgabezeitraum = change_ausgabezeitraum;
    }

    public Ausgabenkategorie getDeleteAusgabenkategorie() {
        return deleteAusgabenkategorie;
    }

    public void setDeleteAusgabenkategorie(Ausgabenkategorie deleteAusgabenkategorie) {
        this.deleteAusgabenkategorie = deleteAusgabenkategorie;
    }

    public Ausgabenausgabezeitraum getDeleteAusgabenausgabezeitraum() {
        return deleteAusgabenausgabezeitraum;
    }

    public void setDeleteAusgabenausgabezeitraum(Ausgabenausgabezeitraum deleteAusgabenausgabezeitraum) {
        this.deleteAusgabenausgabezeitraum = deleteAusgabenausgabezeitraum;
    }

    public String getAlleAusgabenMonatSummeAusgabe() {
        return alleAusgabenMonatSummeAusgabe;
    }

    public void setAlleAusgabenMonatSummeAusgabe(String alleAusgabenMonatSummeAusgabe) {
        this.alleAusgabenMonatSummeAusgabe = alleAusgabenMonatSummeAusgabe;
    }

    public String getAlleAusgabenJahrSummeAusgabe() {
        return alleAusgabenJahrSummeAusgabe;
    }

    public void setAlleAusgabenJahrSummeAusgabe(String alleAusgabenJahrSummeAusgabe) {
        this.alleAusgabenJahrSummeAusgabe = alleAusgabenJahrSummeAusgabe;
    }

    public String getRegelmaessigeAusgabenJahrSummeAusgabe() {
        return regelmaessigeAusgabenJahrSummeAusgabe;
    }

    public void setRegelmaessigeAusgabenJahrSummeAusgabe(String regelmaessigeAusgabenJahrSummeAusgabe) {
        this.regelmaessigeAusgabenJahrSummeAusgabe = regelmaessigeAusgabenJahrSummeAusgabe;
    }

    public String getRegelmaessigeAusgabenMonatSummeAusgabe() {
        return regelmaessigeAusgabenMonatSummeAusgabe;
    }

    public void setRegelmaessigeAusgabenMonatSummeAusgabe(String regelmaessigeAusgabenMonatSummeAusgabe) {
        this.regelmaessigeAusgabenMonatSummeAusgabe = regelmaessigeAusgabenMonatSummeAusgabe;
    }

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
    }

    public String getAnhangID() {
        return anhangID;
    }

    public void setAnhangID(String anhangID) {
        this.anhangID = anhangID;
    }

    public Double getAusgabenMonatSumme() {
        return ausgabenMonatSumme;
    }

    public void setAusgabenMonatSumme(Double ausgabenMonatSumme) {
        this.ausgabenMonatSumme = ausgabenMonatSumme;
    }

    public Double getAusgabenJaehrlichSumme() {
        return ausgabenJaehrlichSumme;
    }

    public void setAusgabenJaehrlichSumme(Double ausgabenJaehrlichSumme) {
        this.ausgabenJaehrlichSumme = ausgabenJaehrlichSumme;
    }

    public DonutChartModel getChartBarAusgabenJaehrlich() {
        return chartBarAusgabenJaehrlich;
    }

    public void setChartBarAusgabenJaehrlich(DonutChartModel chartBarAusgabenJaehrlich) {
        this.chartBarAusgabenJaehrlich = chartBarAusgabenJaehrlich;
    }

    public DonutChartModel getChartBarAusgabenMonatlich() {
        return chartBarAusgabenMonatlich;
    }

    public void setChartBarAusgabenMonatlich(DonutChartModel chartBarAusgabenMonatlich) {
        this.chartBarAusgabenMonatlich = chartBarAusgabenMonatlich;
    }

    public DatenbankNotizen getDbnotizEintrag() {
        return dbnotizEintrag;
    }

    public void setDbnotizEintrag(DatenbankNotizen dbnotizEintrag) {
        this.dbnotizEintrag = dbnotizEintrag;
    }

    public String getNotiztext() {
        return notiztext;
    }

    public void setNotiztext(String notiztext) {
        this.notiztext = notiztext;
    }

    public Double getAusgabenBudgetSumme() {
        return ausgabenBudgetSumme;
    }

    public void setAusgabenBudgetSumme(Double ausgabenBudgetSumme) {
        this.ausgabenBudgetSumme = ausgabenBudgetSumme;
    }

    public Double getEinnahmenMonatSumme() {
        return einnahmenMonatSumme;
    }

    public Date getSelectMonatJahr() {
        return selectMonatJahr;
    }

    public void setSelectMonatJahr(Date selectMonatJahr) {
        this.selectMonatJahr = selectMonatJahr;
    }

    public String getDurchschnittlAusgabenBestimmterMonat() {
        return durchschnittlAusgabenBestimmterMonat;
    }

    public void setDurchschnittlAusgabenBestimmterMonat(String durchschnittlAusgabenBestimmterMonat) {
        this.durchschnittlAusgabenBestimmterMonat = durchschnittlAusgabenBestimmterMonat;
    }

    public Integer getAktuelleMonat() {
        return aktuelleMonat;
    }

    public void setAktuelleMonat(Integer aktuelleMonat) {
        this.aktuelleMonat = aktuelleMonat;
    }

    public void setEinnahmenMonatSumme(Double einnahmenMonatSumme) {
        this.einnahmenMonatSumme = einnahmenMonatSumme;
    }

    public Double getEinnahmenJaehrlichSumme() {
        return einnahmenJaehrlichSumme;
    }

    public Integer getAktuellesJahr() {
        return aktuellesJahr;
    }

    public void setAktuellesJahr(Integer aktuellesJahr) {
        this.aktuellesJahr = aktuellesJahr;
    }

    public void setEinnahmenJaehrlichSumme(Double einnahmenJaehrlichSumme) {
        this.einnahmenJaehrlichSumme = einnahmenJaehrlichSumme;
    }

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
    }

    public List<Boolean> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Boolean> columnList) {
        this.columnList = columnList;
    }

    public String getDifferenzAusgabenBudgetMonat() {
        return differenzAusgabenBudgetMonat;
    }

    public void setDifferenzAusgabenBudgetMonat(String differenzAusgabenBudgetMonat) {
        this.differenzAusgabenBudgetMonat = differenzAusgabenBudgetMonat;
    }

    public boolean isAlleAusgabenAnzeigen() {
        return alleAusgabenAnzeigen;
    }

    public void setAlleAusgabenAnzeigen(boolean alleAusgabenAnzeigen) {
        this.alleAusgabenAnzeigen = alleAusgabenAnzeigen;
    }

    public String getDifferenzAusgabenBudgetJahr() {
        return differenzAusgabenBudgetJahr;
    }

    public void setDifferenzAusgabenBudgetJahr(String differenzAusgabenBudgetJahr) {
        this.differenzAusgabenBudgetJahr = differenzAusgabenBudgetJahr;
    }

    public List<Ausgaben> getDeletedAusgabenListenSQL() {
        return deletedAusgabenListenSQL;
    }

    public void setDeletedAusgabenListenSQL(List<Ausgaben> deletedAusgabenListenSQL) {
        this.deletedAusgabenListenSQL = deletedAusgabenListenSQL;
    }

    public List<AusgabenKategorienBetraege> getAusgabenKategorienBetragMonatlichListe() {
        return ausgabenKategorienBetragMonatlichListe;
    }

    public void setAusgabenKategorienBetragMonatlichListe(List<AusgabenKategorienBetraege> ausgabenKategorienBetragMonatlichListe) {
        this.ausgabenKategorienBetragMonatlichListe = ausgabenKategorienBetragMonatlichListe;
    }

    public List<AusgabenKategorienBetraege> getFilteredAusgabenKategorienBetragMonatlichListe() {
        return filteredAusgabenKategorienBetragMonatlichListe;
    }

    public void setFilteredAusgabenKategorienBetragMonatlichListe(List<AusgabenKategorienBetraege> filteredAusgabenKategorienBetragMonatlichListe) {
        this.filteredAusgabenKategorienBetragMonatlichListe = filteredAusgabenKategorienBetragMonatlichListe;
    }

    public List<AusgabenKategorienBetraege> getAusgabenKategorienBetragJaehrlichListe() {
        return ausgabenKategorienBetragJaehrlichListe;
    }

    public void setAusgabenKategorienBetragJaehrlichListe(List<AusgabenKategorienBetraege> ausgabenKategorienBetragJaehrlichListe) {
        this.ausgabenKategorienBetragJaehrlichListe = ausgabenKategorienBetragJaehrlichListe;
    }

    public List<AusgabenKategorienBetraege> getFilteredAusgabenKategorienBetragJaehrlichListe() {
        return filteredAusgabenKategorienBetragJaehrlichListe;
    }

    public void setFilteredAusgabenKategorienBetragJaehrlichListe(List<AusgabenKategorienBetraege> filteredAusgabenKategorienBetragJaehrlichListe) {
        this.filteredAusgabenKategorienBetragJaehrlichListe = filteredAusgabenKategorienBetragJaehrlichListe;
    }

    public String getTabellenname() {
        return tabellenname;
    }

    public void setTabellenname(String tabellenname) {
        this.tabellenname = tabellenname;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public String getAnhangname() {
        return anhangname;
    }

    public void setAnhangname(String anhangname) {
        this.anhangname = anhangname;
    }

    public String getAnhangtype() {
        return anhangtype;
    }

    public void setAnhangtype(String anhangtype) {
        this.anhangtype = anhangtype;
    }

    public String getGewinnMonatlich() {
        return gewinnMonatlich;
    }

    public void setGewinnMonatlich(String gewinnMonatlich) {
        this.gewinnMonatlich = gewinnMonatlich;
    }

    public String getGewinnJaehrlich() {
        return gewinnJaehrlich;
    }

    public void setGewinnJaehrlich(String gewinnJaehrlich) {
        this.gewinnJaehrlich = gewinnJaehrlich;
    }

    public String getDurchschnittlicheMonatlicheAusgaben() {
        return durchschnittlicheMonatlicheAusgaben;
    }

    public void setDurchschnittlicheMonatlicheAusgaben(String durchschnittlicheMonatlicheAusgaben) {
        this.durchschnittlicheMonatlicheAusgaben = durchschnittlicheMonatlicheAusgaben;
    }

    public String getJahrmonat() {
        return jahrmonat;
    }

    public void setJahrmonat(String jahrmonat) {
        this.jahrmonat = jahrmonat;
    }

    public ScheduleModel getAusgabenKalender() {
        return ausgabenKalender;
    }

    public void setAusgabenKalender(ScheduleModel ausgabenKalender) {
        this.ausgabenKalender = ausgabenKalender;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public List<AusgabenBudget> getAusgabenBudgetListenSQL() {
        return ausgabenBudgetListenSQL;
    }

    public void setAusgabenBudgetListenSQL(List<AusgabenBudget> ausgabenBudgetListenSQL) {
        this.ausgabenBudgetListenSQL = ausgabenBudgetListenSQL;
    }

    public List<AusgabenBudget> getFilteredAusgabenBudgetListenSQL() {
        return filteredAusgabenBudgetListenSQL;
    }

    public void setFilteredAusgabenBudgetListenSQL(List<AusgabenBudget> filteredAusgabenBudgetListenSQL) {
        this.filteredAusgabenBudgetListenSQL = filteredAusgabenBudgetListenSQL;
    }

    public List<Boolean> getColumnListAusgabenBudget() {
        return columnListAusgabenBudget;
    }

    public void setColumnListAusgabenBudget(List<Boolean> columnListAusgabenBudget) {
        this.columnListAusgabenBudget = columnListAusgabenBudget;
    }

    public List<AusgabenBudget> getAusgabenBudgetJaehrlichListenSQL() {
        return ausgabenBudgetJaehrlichListenSQL;
    }

    public void setAusgabenBudgetJaehrlichListenSQL(List<AusgabenBudget> ausgabenBudgetJaehrlichListenSQL) {
        this.ausgabenBudgetJaehrlichListenSQL = ausgabenBudgetJaehrlichListenSQL;
    }

    public List<AusgabenBudget> getFilteredAusgabenBudgetJaehrlichListenSQL() {
        return filteredAusgabenBudgetJaehrlichListenSQL;
    }

    public void setFilteredAusgabenBudgetJaehrlichListenSQL(List<AusgabenBudget> filteredAusgabenBudgetJaehrlichListenSQL) {
        this.filteredAusgabenBudgetJaehrlichListenSQL = filteredAusgabenBudgetJaehrlichListenSQL;
    }

    public Double getAlleAusgabenMonatSumme() {
        return alleAusgabenMonatSumme;
    }

    public void setAlleAusgabenMonatSumme(Double alleAusgabenMonatSumme) {
        this.alleAusgabenMonatSumme = alleAusgabenMonatSumme;
    }

    public Double getAlleAusgabenJahrSumme() {
        return alleAusgabenJahrSumme;
    }

    public void setAlleAusgabenJahrSumme(Double alleAusgabenJahrSumme) {
        this.alleAusgabenJahrSumme = alleAusgabenJahrSumme;
    }

    public String getPerson1name() {
        return person1name;
    }

    public void setPerson1name(String person1name) {
        this.person1name = person1name;
    }

    public Integer getPerson1aufteilung() {
        return person1aufteilung;
    }

    public void setPerson1aufteilung(Integer person1aufteilung) {
        this.person1aufteilung = person1aufteilung;
    }

    public Double getPerson1gesamtteilbetrag() {
        return person1gesamtteilbetrag;
    }

    public void setPerson1gesamtteilbetrag(Double person1gesamtteilbetrag) {
        this.person1gesamtteilbetrag = person1gesamtteilbetrag;
    }

    public Double getPerson1einkommen() {
        return person1einkommen;
    }

    public void setPerson1einkommen(Double person1einkommen) {
        this.person1einkommen = person1einkommen;
    }

    public String getPerson2name() {
        return person2name;
    }

    public void setPerson2name(String person2name) {
        this.person2name = person2name;
    }

    public Integer getPerson2aufteilung() {
        return person2aufteilung;
    }

    public void setPerson2aufteilung(Integer person2aufteilung) {
        this.person2aufteilung = person2aufteilung;
    }

    public Double getPerson2gesamtteilbetrag() {
        return person2gesamtteilbetrag;
    }

    public void setPerson2gesamtteilbetrag(Double person2gesamtteilbetrag) {
        this.person2gesamtteilbetrag = person2gesamtteilbetrag;
    }

    public Double getPerson2einkommen() {
        return person2einkommen;
    }

    public void setPerson2einkommen(Double person2einkommen) {
        this.person2einkommen = person2einkommen;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.ausgabenausgabezeitraum);
        hash = 23 * hash + Objects.hashCode(this.ausgabenkategorie);
        hash = 23 * hash + Objects.hashCode(this.ausgabenkategorien);
        hash = 23 * hash + Objects.hashCode(this.ausgabenausgabezeitraeume);
        hash = 23 * hash + Objects.hashCode(this.ausgabenListenSQL);
        hash = 23 * hash + Objects.hashCode(this.filteredAusgabenListenSQL);
        hash = 23 * hash + Objects.hashCode(this.bezeichnung);
        hash = 23 * hash + Objects.hashCode(this.betrag);
        hash = 23 * hash + Objects.hashCode(this.zahlungsdatum);
        hash = 23 * hash + Objects.hashCode(this.informationen);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AusgabenController other = (AusgabenController) obj;
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenausgabezeitraum, other.ausgabenausgabezeitraum)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenkategorie, other.ausgabenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenkategorien, other.ausgabenkategorien)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenausgabezeitraeume, other.ausgabenausgabezeitraeume)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenListenSQL, other.ausgabenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredAusgabenListenSQL, other.filteredAusgabenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.zahlungsdatum, other.zahlungsdatum)) {
            return false;
        }
        return true;
    }

}
