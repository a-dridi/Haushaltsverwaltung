/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import haushaltsverwaltung.main.AusgabenKategorienBetraege;
import haushaltsverwaltung.model.Ausgabenausgabezeitraum;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.Einnahmen;
import haushaltsverwaltung.model.EinnahmenJahrEntwicklung;
import haushaltsverwaltung.model.EinnahmenKategorie;
import haushaltsverwaltung.model.EinnahmenMonatEntwicklung;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.poi.util.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;

/**
 * Earnings / Einnahmen
 *
 * @author A.Dridi
 */
@Named(value = "einnahmenController")
@ViewScoped
public class EinnahmenController implements Serializable {

    private DonutChartModel chartBarEinnahmenMonatlich;
    private DonutChartModel chartBarEinnahmenJaehrlich;
    private LineChartModel chartEinnahmenMonatEntwicklung;
    private LineChartModel chartEinnahmenJahrEntwicklung;

    private EinnahmenKategorie einnahmenkategorie;
    private List<EinnahmenKategorie> einnahmenkategorieList = new ArrayList<>();

    private List<Ausgabenausgabezeitraum> ausgabenausgabezeitraumList = new ArrayList<>();
    private List<Einnahmen> einnahmenList = new ArrayList<>();
    private List<Einnahmen> filteredEinnahmenList = new ArrayList<>();
    private List<EinnahmenMonatEntwicklung> einnahmenMonatList = new ArrayList<>();
    private List<EinnahmenMonatEntwicklung> filteredEinnahmenMonatList = new ArrayList<>();
    private List<EinnahmenJahrEntwicklung> einnahmenJahrList = new ArrayList<>();
    private List<EinnahmenJahrEntwicklung> filteredEinnahmenJahrList = new ArrayList<>();

    private DatenbankNotizen dbnotizEintrag = null;
    private List<Einnahmen> deletedEinnahmenList = new ArrayList<>();

    private Integer rownumbers = 15;
    private String tabellenname = "Einnahmen";
    private String datensaetzeAnzahlText;
    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Einnahmen";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Einnahmen";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    private String anhangname;
    private String anhangtype;
    private DAO dao;
    private String notiztext;

    private String deleteID;
    private String anhangID;
    private byte[] anhang;
    private String neuEinnahmenkategorie;
    private String change_einnahmenkategorie;
    private EinnahmenKategorie deleteEinnahmenkategorie;

    private String bezeichnung;
    private Double betrag;
    private Ausgabenausgabezeitraum haeufigkeit;
    private Date eingangsdatum; //(Überschrift: Datum (Eingangsdatum)
    private String informationen;

    private Double einnahmenMonatSumme;
    private Double einnahmenJaehrlichSumme;
    private Double ausgabenMonatSumme;
    private Double ausgabenJaehrlichSumme;
    private Double gewinnMonatlich;
    private Double gewinnJaehrlich;

    private Double durchschnittlicheMonatlicheEinnahmen;
    private String einnahmenBestimmtenJahres;

    private List<AusgabenKategorienBetraege> einnahmenKategorienBetragMonatlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragMonatlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> einnahmenKategorienBetragJaehrlichListe = new ArrayList<>();
    private List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragJaehrlichListe = new ArrayList<>();

    private String jahrmonat;
    private Date selectMonatJahr;

    //Alle Einnahmen die jemals (monatlich u. jaehrlich bis jetzt stattfanden)
    private Double gesamtMonatJahrEinnahmen = 0.0;

    /**
     * Creates a new instance of EinnahmenController
     */
    public EinnahmenController() {
        this.dao = new DAO();
    }

    @PostConstruct
    private void init() {
        Date d = new Date();

        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
        }
        this.einnahmenkategorieList = dao.getAllEinnahmenKategorie();

        flushAnhang();

        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        if (urlName.contains("einnahmen_grafik.xhtml")) {
            this.einnahmenMonatList = dao.getAllEinnahmenMonatEntwicklung();
            this.einnahmenJahrList = dao.getAllEinnahmenJahrEntwicklung();
            this.einnahmenList = dao.getAllEinnahmen();

            createPortfolioWertinEuro();
            createMonatEinnahmenEntwicklungChart();
            createJahrEinnahmenEntwicklungChart();

        } else if (urlName.contains("einnahmen_kategorien.xhtml")) {
            createPortfolioWertinEuro();
            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();

            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();

            calculateDifferenzAusgabenEinnahmen();
        } else if (urlName.contains("einnahmen_monatlich.xhtml")) {
            //Datensatz erstellen (wenn nicht schon existent) für aktuelles Monat und Jahr mit aktuellen Monat und Jahr als Beschreibung
            //Datensatz kann dann von Benutzer bearbeitet werden. 
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

            if (dao.getEinnahmenMonatEntwicklungByMonatJahr(sdf.format(d)).isEmpty()) {
                EinnahmenMonatEntwicklung e = new EinnahmenMonatEntwicklung();
                e.setMonatjahr(sdf.format(d));
                e.setBetrag(0.0);
                dao.insertEinnahmenMonatEntwicklung(e);
            }

            this.einnahmenMonatList = dao.getAllEinnahmenMonatEntwicklung();
            this.filteredEinnahmenMonatList = new ArrayList<>(this.einnahmenMonatList);
            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();

            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();

            calculateDifferenzAusgabenEinnahmen();

        } else if (urlName.contains("einnahmen_jaehrlich.xhtml")) {
            //Datensatz erstellen (wenn nicht schon existent) für aktuelles Jahr mit aktuellen Jahr als Beschreibung
            //Datensatz kann dann von Benutzer bearbeitet werden. 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            //Neues Jahr hinzufügen
            if (dao.getEinnahmenJahrEntwicklungByJahr(Integer.parseInt(sdf.format(d))).isEmpty()) {
                EinnahmenJahrEntwicklung e = new EinnahmenJahrEntwicklung();
                e.setJahr(Integer.parseInt(sdf.format(d)));
                e.setBetrag(0.0);
                dao.insertEinnahmenJahrEntwicklung(e);
            }
            this.einnahmenJahrList = dao.getAllEinnahmenJahrEntwicklung();
            this.filteredEinnahmenJahrList = new ArrayList<>(this.einnahmenJahrList);
            calculateSummeEinnahmenEntwicklung();
            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();

            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();

            calculateDifferenzAusgabenEinnahmen();

        } else { //Tabelle Einnahmen
            this.einnahmenList = dao.getAllEinnahmen();
            this.filteredEinnahmenList = new ArrayList<>(this.einnahmenList);
            this.ausgabenausgabezeitraumList = dao.getAllAusgabenausgabezeitraum();

            calculateAusgabenMonatSumme();
            calculateAusgabenJahrSumme();

            calculateEinnahmenMonatSumme();
            calculateEinnahmenJahrSumme();

            calculateDifferenzAusgabenEinnahmen();
            this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            this.jahrmonat = sdf.format(d);

        }

    }

    /**
     * Erstellt ein Chart (Typ Area) mit den Einnahmen Entwicklungen des
     * jeweiligem Monat und jeweiligen Jahr. Nur eine Linie wird dargestellt,
     * daher wird die Legende ausgeblendet.
     */
    public void createMonatEinnahmenEntwicklungChart() {
        this.chartEinnahmenMonatEntwicklung = new LineChartModel();

        ChartData chartMonatEinahmmenEntwicklungData = new ChartData();
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        LineChartDataSet chartMonatEinahmmenEntwicklungDataSet = new LineChartDataSet();

        //Linienchart-Punkt für jeden Monat-Jahr-Zeitpunkt erstellen
        for (EinnahmenMonatEntwicklung e : this.einnahmenMonatList) {
            //Linienchart-Punkt und Beschreibung hinzufügen
            labels.add(e.getMonatjahr());
            values.add((Number) e.getBetrag());
        }

        chartMonatEinahmmenEntwicklungDataSet.setData(values);
        chartMonatEinahmmenEntwicklungDataSet.setLabel("Einnahmen");
        chartMonatEinahmmenEntwicklungDataSet.setYaxisID("monatl-einnahmen");
        chartMonatEinahmmenEntwicklungData.setLabels(labels);

        chartMonatEinahmmenEntwicklungData.addChartDataSet(chartMonatEinahmmenEntwicklungDataSet);

        this.chartEinnahmenMonatEntwicklung.setData(chartMonatEinahmmenEntwicklungData);
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setId("monatl-einnahmen");
        linearAxes.setPosition("left");
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Monatliche Einnahmen in Euro");
        options.setTitle(title);

        this.chartEinnahmenMonatEntwicklung.setOptions(options);
    }

    /**
     * Erstellt ein Chart (Typ Area) mit den Einnahmen Entwicklungen pro
     * jeweiligem Monat und jeweiligen Jahr. Nur eine Linie wird dargestellt,
     * daher wird die Legende ausgeblendet.
     */
    public void createJahrEinnahmenEntwicklungChart() {
        this.chartEinnahmenJahrEntwicklung = new LineChartModel();
        //Linienchart-Punkt für jeden Jahr-Zeitpunkt erstellen
        ChartData chartJahrEinahmmenEntwicklungData = new ChartData();
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        LineChartDataSet chartJahrEinahmmenEntwicklungDataSet = new LineChartDataSet();

        //Linienchart-Punkt für jeden Monat-Jahr-Zeitpunkt erstellen
        for (EinnahmenJahrEntwicklung e : this.einnahmenJahrList) {
            //Linienchart-Punkt und Beschreibung hinzufügen
            labels.add(e.getJahr().toString());
            values.add((Number) e.getBetrag());
        }

        chartJahrEinahmmenEntwicklungDataSet.setData(values);
        chartJahrEinahmmenEntwicklungDataSet.setLabel("Einnahmen");
        chartJahrEinahmmenEntwicklungDataSet.setYaxisID("jaehrl-einnahmen");
        chartJahrEinahmmenEntwicklungData.setLabels(labels);

        chartJahrEinahmmenEntwicklungData.addChartDataSet(chartJahrEinahmmenEntwicklungDataSet);

        this.chartEinnahmenJahrEntwicklung.setData(chartJahrEinahmmenEntwicklungData);
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setId("jaehrl-einnahmen");
        linearAxes.setPosition("left");
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Jährliche Einnahmen in Euro");
        options.setTitle(title);

        this.chartEinnahmenJahrEntwicklung.setOptions(options);

    }

    //Gesamte Summe von Einnahmen Entwicklung (Alle monatlichen und jährlichen Einnahmen zusammengezählt bis jetzt) 
    public void calculateSummeEinnahmenEntwicklung() {
        this.gesamtMonatJahrEinnahmen = dao.getSummeJahrEinnahmen();
    }

    /**
     * Einnahmen wo einmalige Einnahmen nicht dieses Jahr stattgefunden haben.
     */
    public void showAlteEinnahmen() {
        this.jahrmonat = "ALLE";

        this.einnahmenList.clear();
        this.filteredEinnahmenList.clear();
        this.einnahmenList.addAll(dao.getAllEinnahmenAlle());
        this.filteredEinnahmenList.addAll(this.einnahmenList);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

        //RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
        PrimeFaces.current().ajax().update("listenForm:einnahmenTabelleDaten");

    }

    /**
     * Einnahmen wo einmalige Einnahmen nicht dieses Jahr stattgefunden haben.
     */
    public void showEinnahmenDiesesJahres() {
        Date d = new Date();
        SimpleDateFormat sdfJ = new SimpleDateFormat("yyyy");
        this.jahrmonat = sdfJ.format(d);

        this.einnahmenList.clear();
        this.filteredEinnahmenList.clear();
        this.einnahmenList.addAll(dao.getAllEinnahmen());
        this.filteredEinnahmenList.addAll(this.einnahmenList);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

        //RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
        PrimeFaces.current().ajax().update("listenForm:einnahmenTabelleDaten");

    }

    /**
     * Auswahl von Monat und Jahr durch Benutzer über Kalender-Picker
     */
    public void changeMonatJahrAnsicht() {

        if (this.selectMonatJahr != null) {

            SimpleDateFormat sdfJ = new SimpleDateFormat("yyyy");
            this.jahrmonat = sdfJ.format(this.selectMonatJahr);
            calculateEinnahmenCustomJahrSumme(Integer.parseInt(this.jahrmonat));
            this.einnahmenList.clear();
            this.filteredEinnahmenList.clear();
            this.einnahmenList.addAll(dao.getAllEinnahmenCustom(null, Integer.parseInt(sdfJ.format(this.selectMonatJahr))));
            this.filteredEinnahmenList.addAll(this.einnahmenList);

            this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

            //RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
            PrimeFaces.current().ajax().update("listenForm:einnahmenTabelleDaten");

        }
    }

    /**
     * Einnahmen wo einmalige Einnahmen nicht dieses Jahr stattgefunden haben.
     */
    public void showRegelmaessigeEinnahmen() {
        this.jahrmonat = "Nur Regelmaessige";
        this.einnahmenList.clear();
        this.filteredEinnahmenList.clear();
        this.einnahmenList.addAll(dao.getAllEinnahmenRegelmaessig());
        this.filteredEinnahmenList.addAll(this.einnahmenList);

        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

        //RequestContext.getCurrentInstance().update("listenForm:einnahmenTabelleDaten");
        PrimeFaces.current().ajax().update("listenForm:einnahmenTabelleDaten");
    }

    /**
     * Barchart für monatl. und jaerhliche Einnahmen
     */
    public void createPortfolioWertinEuro() {

        //Monatliche Einnahmen
        this.chartBarEinnahmenMonatlich = new DonutChartModel();
        ChartData chartBarEinnahmenMonatlichData = new ChartData();
        DonutChartDataSet chartBarEinnahmenMonatlichDataSet = new DonutChartDataSet();
        List<Number> valuesMonatl = new ArrayList<>();
        List<String> labelsMonatl = new ArrayList<>();

        //Jährliche Einnahmen
        this.chartBarEinnahmenJaehrlich = new DonutChartModel();
        ChartData chartBarEinnahmenJaehrlichData = new ChartData();
        DonutChartDataSet chartBarEinnahmenJaehrlichDataSet = new DonutChartDataSet();
        List<Number> valuesJaehrl = new ArrayList<>();
        List<String> labelsJaehrl = new ArrayList<>();

        List<String> bgColors = new ArrayList<>();

        //SQL Abruf
        // Liefert alle Einträge (Einnahmen) für eine Kategorie sortiert
        // aufsteigend nach dem Datum
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<EinnahmenKategorie> kategorienliste = this.einnahmenkategorieList;
        Double einnahmenSumme;

        abbrechen:
        for (EinnahmenKategorie w : kategorienliste) {
            einnahmenSumme = 0.0;
            try {
                String sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where deleted=false and haeufigkeit= 'monatlich' and kategorie = :kategoriename group by kategorie order by kategorie asc";

                Query qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                List<Object[]> kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += Math.round((((Double) o[1])) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='woechentlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));
                            einnahmenSumme += Math.round((((Double) o[1]) * 4) * 100.0) / 100.0;
                        }
                    }
                }
                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='14-taegig' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += Math.round((((Double) o[1]) * 2) * 100.0) / 100.0;
                        }
                    }
                }
                AusgabenKategorienBetraege akb = new AusgabenKategorienBetraege();
                if (einnahmenSumme > 0.0) {
                    valuesMonatl.add(einnahmenSumme);
                    labelsMonatl.add(w.getBezeichnung());
                    akb.setBetrag(einnahmenSumme);
                    akb.setBezeichnung(w.getBezeichnung());
                    this.einnahmenKategorienBetragMonatlichListe.add(akb);
                }
                //jaehrliche Berechnung:
                einnahmenSumme = Math.round(((einnahmenSumme * 12)) * 100.0) / 100.0;
                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='alle 2 Monate' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += Math.round((((Double) o[1] * 6)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='vierteljaehrlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += Math.round((((Double) o[1] * 4)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='alle 6 Monate' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += Math.round((((Double) o[1] * 2)) * 100.0) / 100.0;
                        }
                    }
                }

                sqlstring = "Select kategorie, sum(betrag) FROM Einnahmen where kategorie = :kategoriename and deleted=false and haeufigkeit='jaehrlich' group by kategorie order by kategorie asc";

                qu = s.createQuery(sqlstring);
                qu.setString("kategoriename", w.getBezeichnung());
                kategoriegruppe = qu.list();

                if (kategoriegruppe != null && !kategoriegruppe.isEmpty()) {
                    //Alle Ausgabeninträge hinzufügen
                    for (Object[] o : kategoriegruppe) {
                        if ((Double) o[1] != 0.0) {
                            einnahmenSumme += Math.round((((Double) o[1])) * 100.0) / 100.0;
                        }
                    }
                }
                if (einnahmenSumme > 0.0) {
                    valuesJaehrl.add(einnahmenSumme);
                    labelsJaehrl.add(w.getBezeichnung());
                    akb = new AusgabenKategorienBetraege();
                    akb.setBetrag(einnahmenSumme);
                    akb.setBezeichnung(w.getBezeichnung());

                    //Hintergrundfarben durch Zufall erstellen.
                    Random rnd = new Random();
                    bgColors.add("rgb(" + rnd.nextInt(240) + "," + rnd.nextInt(240) + "," + rnd.nextInt(240) + ")");

                    this.einnahmenKategorienBetragJaehrlichListe.add(akb);
                }

            } catch (Exception e) {
                System.out.println("Fehler in createPortfolioWertinEuro: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Abfrage von Einnahmenkategorie:", "" + e));
                break abbrechen;
            }
        }

        chartBarEinnahmenMonatlichDataSet.setData(valuesMonatl);
        chartBarEinnahmenJaehrlichDataSet.setData(valuesJaehrl);
        chartBarEinnahmenMonatlichData.setLabels(labelsMonatl);
        chartBarEinnahmenJaehrlichData.setLabels(labelsJaehrl);
        chartBarEinnahmenMonatlichDataSet.setBackgroundColor(bgColors);
        chartBarEinnahmenJaehrlichDataSet.setBackgroundColor(bgColors);
        chartBarEinnahmenMonatlichData.addChartDataSet(chartBarEinnahmenMonatlichDataSet);
        chartBarEinnahmenJaehrlichData.addChartDataSet(chartBarEinnahmenJaehrlichDataSet);
        this.chartBarEinnahmenMonatlich.setData(chartBarEinnahmenMonatlichData);
        this.chartBarEinnahmenJaehrlich.setData(chartBarEinnahmenJaehrlichData);

        this.filteredEinnahmenKategorienBetragJaehrlichListe = new ArrayList<>(this.einnahmenKategorienBetragJaehrlichListe);
        this.filteredEinnahmenKategorienBetragMonatlichListe = new ArrayList<>(this.einnahmenKategorienBetragMonatlichListe);
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

    /**
     * Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs wird der Anhang
     * für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<Einnahmen> liste = new ArrayList<>(this.einnahmenList);
            gefunden:
            for (Einnahmen a : liste) {
                if (a.getEinnahmen_id().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getEinnahmen_id()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getEinnahmen_id()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getEinnahmen_id()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateEinnahmen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getEinnahmen_id() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getEinnahmen_id()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateEinnahmen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getEinnahmen_id() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
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

            Einnahmen a = (this.dao.getSingleEinnahmen((Integer) tabelle.getRowKey())).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Bezeichnung")) {
                a.setBezeichnung((String) event.getNewValue());
            }

            if (spaltenname.equals("Kategorie")) {

                String auswahl = (String) event.getNewValue();
                gefunden:
                for (EinnahmenKategorie m : this.einnahmenkategorieList) {
                    if (m.getBezeichnung().equals(auswahl)) {
                        a.setKategorie((String) event.getNewValue());
                        break gefunden;
                    }
                }
            }
            if (spaltenname.equals("Betrag")) {
                a.setBetrag((Double) event.getNewValue());
            }

            if (spaltenname.equals("Haeufigkeit")) {
                String auswahl = (String) event.getNewValue();
                gefunden:
                for (Ausgabenausgabezeitraum m : this.ausgabenausgabezeitraumList) {
                    if (m.getAusgabezeitraumbezeichnung().equals(auswahl)) {
                        a.setHaeufigkeit((String) event.getNewValue());
                        break gefunden;
                    }
                }

            }

            if (spaltenname.equals("Eingangsdatum")) {
                if (event.getNewValue() != null) {
                    a.setEingangsdatum((Date) event.getNewValue());
                }
            }

            if (spaltenname.equals("Informationen")) {
                a.setInformationen((String) event.getNewValue());
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateEinnahmen(a);
            updateData();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

            //DEBUG:
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void editRowEinnahmenMonatEntwicklung(CellEditEvent event) {
        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            EinnahmenMonatEntwicklung a = (this.dao.getSingleEinnahmenMonatEntwicklung((Integer) tabelle.getRowKey())).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Monat/Jahr")) {
                a.setMonatjahr((String) event.getNewValue());
            }

            if (spaltenname.equals("Betrag")) {
                a.setBetrag((Double) event.getNewValue());
            }

            if (spaltenname.equals("Bemerkungen")) {
                a.setBemerkungen((String) event.getNewValue());
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateEinnahmenMonatEntwicklung(a);
            updateDataEntwicklung();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

            //DEBUG:
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void editRowEinnahmenJahrEntwicklung(CellEditEvent event) {
        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            EinnahmenJahrEntwicklung a = (this.dao.getSingleEinnahmenJahrEntwicklung((Integer) tabelle.getRowKey())).get(0);

            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));
            if (spaltenname.equals("Jahr")) {
                a.setJahr((Integer) event.getNewValue());
            }

            if (spaltenname.equals("Betrag")) {
                a.setBetrag((Double) event.getNewValue());
            }

            if (spaltenname.equals("Bemerkungen")) {
                a.setBemerkungen((String) event.getNewValue());
            }

            dao.updateEinnahmenJahrEntwicklung(a);
            updateDataEntwicklung();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void updateData() {
        this.dao = new DAO();
        this.einnahmenList = dao.getAllEinnahmen();
        this.filteredEinnahmenList = new ArrayList<>(this.einnahmenList);
        this.einnahmenkategorieList = dao.getAllEinnahmenKategorie();
        this.ausgabenausgabezeitraumList = dao.getAllAusgabenausgabezeitraum();
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        calculateEinnahmenMonatSumme();
        calculateEinnahmenJahrSumme();
        calculateDifferenzAusgabenEinnahmen();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.einnahmenList.size() + " Datensaetze in der DB gespeichert");

    }

    public void updateDataEntwicklung() {
        this.dao = new DAO();
        this.einnahmenMonatList = dao.getAllEinnahmenMonatEntwicklung();
        this.einnahmenJahrList = dao.getAllEinnahmenJahrEntwicklung();
        this.filteredEinnahmenMonatList = new ArrayList<>(this.einnahmenMonatList);
        this.filteredEinnahmenJahrList = new ArrayList<>(this.einnahmenJahrList);
        calculateSummeEinnahmenEntwicklung();

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

    public void speichern() {
        Einnahmen ausgabe = new Einnahmen();
        ausgabe.setDeleted(false);

        if (this.bezeichnung != null) {
            ausgabe.setBezeichnung(bezeichnung);
        }

        ausgabe.setBetrag(betrag);

        if (this.einnahmenkategorie != null) {
            ausgabe.setKategorie(this.einnahmenkategorie.getBezeichnung());
        }

        if (this.haeufigkeit != null) {
            ausgabe.setHaeufigkeit(this.haeufigkeit.getAusgabezeitraumbezeichnung());
        }

        if (this.eingangsdatum != null) {
            ausgabe.setEingangsdatum(eingangsdatum);
        } else {
            ausgabe.setEingangsdatum(new Date());
        }

        if (this.informationen != null) {
            ausgabe.setInformationen(informationen);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            ausgabe.setAnhang(true);
            this.einnahmenList.add(ausgabe);
            this.filteredEinnahmenList.add(ausgabe);
            dao.insertEinnahmen(ausgabe);

            List<Einnahmen> ausgabenListe = new ArrayList<>(this.einnahmenList);
            int letzteNr = ausgabenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = ausgabenListe.get(letzteNr).getEinnahmen_id();
                try {
                    Einnahmen a = ausgabenListe.get(letzteNr);
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
                    dao.updateEinnahmen(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateData();
            }

        } else {
            ausgabe.setAnhang(false);
            this.einnahmenList.add(ausgabe);
            this.filteredEinnahmenList.add(ausgabe);
            dao.insertEinnahmen(ausgabe);
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

        if (!this.neuEinnahmenkategorie.isEmpty()) {
            EinnahmenKategorie ak = new EinnahmenKategorie();
            ak.setBezeichnung(this.neuEinnahmenkategorie);
            dao.insertEinnahmenKategorie(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void kategorieLoeschen() {

        if (this.deleteEinnahmenkategorie != null) {

            List<EinnahmenKategorie> akList = dao.getAllEinnahmenKategorie();
            List<Einnahmen> ausgabenList = dao.getAllEinnahmen();
            boolean kategorieExist = false;

            for (EinnahmenKategorie a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteEinnahmenkategorie.getBezeichnung().toLowerCase())) {
                    dao.deleteEinnahmenKategorie(a);
                    for (Einnahmen ausgabe : ausgabenList) {
                        if ((ausgabe.getKategorie().toLowerCase()).equals(this.deleteEinnahmenkategorie.getBezeichnung().toLowerCase())) {
                            this.einnahmenList.remove(ausgabe);
                            this.filteredEinnahmenList.remove(ausgabe);

                            ausgabe.setKategorie(this.change_einnahmenkategorie);
                            dao.updateEinnahmen(ausgabe);
                            this.einnahmenList.add(ausgabe);
                            this.filteredEinnahmenList.add(ausgabe);

                        }
                    }
                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_einnahmenkategorie.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                EinnahmenKategorie neu = new EinnahmenKategorie();
                neu.setBezeichnung(this.change_einnahmenkategorie);
                dao.insertEinnahmenKategorie(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (Einnahmen a : this.einnahmenList) {
                    if (a.getEinnahmen_id().equals(Integer.parseInt(this.deleteID))) {
                        dao.deleteEinnahmen(a);
                        this.deletedEinnahmenList.add(a);
                        this.einnahmenList.remove(a);
                        this.filteredEinnahmenList.remove(a);

                        break gefunden;
                    }
                }
                updateData();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }

    }

    public void datensatzLoeschen(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (Einnahmen a : this.einnahmenList) {
                    if (a.getEinnahmen_id().equals(id)) {
                        dao.deleteEinnahmen(a);
                        this.deletedEinnahmenList.add(a);
                        this.einnahmenList.remove(a);
                        this.filteredEinnahmenList.remove(a);

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

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedEinnahmenList.isEmpty()) {
            for (Einnahmen a : this.deletedEinnahmenList) {
                this.einnahmenList.add(a);
                this.filteredEinnahmenList.add(a);
                a.setDeleted(false);
                dao.updateEinnahmen(a);
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    public void calculateAusgabenJahrSumme() {
        this.ausgabenJaehrlichSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += waehrunggruppe.get(0);
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='einmalig' and deleted=False and EXTRACT(year FROM zahlungsdatum)  = :jahrWert";
            Date d = new Date();
            SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", Integer.parseInt(formatJahr.format(d)));
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0));
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 52);
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 24);
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Ausgaben where ausgabezeitraum='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.ausgabenJaehrlichSumme += (waehrunggruppe.get(0) * 12);
            }
            //jedes 2.Monate Einnahmen
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

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Ausgaben-Berechnung Summe: ", "" + e));

        }

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
        s.close();

    }

    public void calculateEinnahmenMonatSumme() {
        this.einnahmenMonatSumme = 0.0;

        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += waehrunggruppe.get(0);

        }
        //woechentliche Einnahmen
        sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += (waehrunggruppe.get(0) * 4);
        }

        //14-tägige Einnahmen
        sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
        qu = s.createQuery(sqlstring);
        waehrunggruppe = qu.list();
        if (waehrunggruppe.get(0) != null) {
            this.einnahmenMonatSumme += (waehrunggruppe.get(0) * 2);
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
                this.einnahmenJaehrlichSumme += waehrunggruppe.get(0);
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='einmalig' and deleted=False and EXTRACT(year FROM eingangsdatum)  = :jahrWert";
            Date d = new Date();
            SimpleDateFormat formatJahr = new SimpleDateFormat("yyyy");
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", Integer.parseInt(formatJahr.format(d)));
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0));
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 52);
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 24);
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 12);
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 6);
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 4);
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                this.einnahmenJaehrlichSumme += (waehrunggruppe.get(0) * 2);
            }

            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Einnahmen-Berechnung Summe: ", "" + e));

        }

    }

    public void calculateEinnahmenCustomJahrSumme(int jahr) {
        Double einnahmenBestimmtesJahrSumme = 0.0;
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='jaehrlich' and deleted=False";
            Query qu = s.createQuery(sqlstring);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += waehrunggruppe.get(0);
            }

            //Einnahmen, die dieses Jahr erfolgt sind
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='einmalig' and deleted=False and EXTRACT(year FROM eingangsdatum)  = :jahrWert";
            qu = s.createQuery(sqlstring);
            qu.setInteger("jahrWert", jahr);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0));
            }

            //woechentliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='woechentlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 52);
            }

            //14-tägige Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='14-taegig' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 24);
            }
            //monatliche Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='monatlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 12);
            }
            //jedes 2.Monate Einnahmen
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 2 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 6);
            }
            //vierteljährlich - alle 3 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='vierteljaehrlich' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 4);
            }
            //vierteljährlich - alle 6 Monate
            sqlstring = "Select sum(betrag) FROM Einnahmen where haeufigkeit='alle 6 Monate' and deleted=False";
            qu = s.createQuery(sqlstring);
            waehrunggruppe = qu.list();
            if (waehrunggruppe.get(0) != null) {
                einnahmenBestimmtesJahrSumme += (waehrunggruppe.get(0) * 2);
            }

            this.einnahmenBestimmtenJahres = "Einnahmen dieses Jahres: " + einnahmenBestimmtesJahrSumme + "€";
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
        this.gewinnMonatlich = (this.einnahmenMonatSumme - this.ausgabenMonatSumme);
        this.gewinnJaehrlich = (this.einnahmenJaehrlichSumme - this.ausgabenJaehrlichSumme);
        this.durchschnittlicheMonatlicheEinnahmen = (this.einnahmenJaehrlichSumme / 12);
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
        PrimeFaces.current().scrollTo("tabellenAnsicht:listenForm");

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

    public List<AusgabenKategorienBetraege> getEinnahmenKategorienBetragMonatlichListe() {
        return einnahmenKategorienBetragMonatlichListe;
    }

    public void setEinnahmenKategorienBetragMonatlichListe(List<AusgabenKategorienBetraege> einnahmenKategorienBetragMonatlichListe) {
        this.einnahmenKategorienBetragMonatlichListe = einnahmenKategorienBetragMonatlichListe;
    }

    public List<AusgabenKategorienBetraege> getFilteredEinnahmenKategorienBetragMonatlichListe() {
        return filteredEinnahmenKategorienBetragMonatlichListe;
    }

    public void setFilteredEinnahmenKategorienBetragMonatlichListe(List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragMonatlichListe) {
        this.filteredEinnahmenKategorienBetragMonatlichListe = filteredEinnahmenKategorienBetragMonatlichListe;
    }

    public List<AusgabenKategorienBetraege> getEinnahmenKategorienBetragJaehrlichListe() {
        return einnahmenKategorienBetragJaehrlichListe;
    }

    public void setEinnahmenKategorienBetragJaehrlichListe(List<AusgabenKategorienBetraege> einnahmenKategorienBetragJaehrlichListe) {
        this.einnahmenKategorienBetragJaehrlichListe = einnahmenKategorienBetragJaehrlichListe;
    }

    public List<AusgabenKategorienBetraege> getFilteredEinnahmenKategorienBetragJaehrlichListe() {
        return filteredEinnahmenKategorienBetragJaehrlichListe;
    }

    public void setFilteredEinnahmenKategorienBetragJaehrlichListe(List<AusgabenKategorienBetraege> filteredEinnahmenKategorienBetragJaehrlichListe) {
        this.filteredEinnahmenKategorienBetragJaehrlichListe = filteredEinnahmenKategorienBetragJaehrlichListe;
    }

    public EinnahmenKategorie getEinnahmenkategorie() {
        return einnahmenkategorie;
    }

    public void setEinnahmenkategorie(EinnahmenKategorie einnahmenkategorie) {
        this.einnahmenkategorie = einnahmenkategorie;
    }

    public List<EinnahmenKategorie> getEinnahmenkategorieList() {
        return einnahmenkategorieList;
    }

    public void setEinnahmenkategorieList(List<EinnahmenKategorie> einnahmenkategorieList) {
        this.einnahmenkategorieList = einnahmenkategorieList;
    }

    public List<Einnahmen> getEinnahmenList() {
        return einnahmenList;
    }

    public void setEinnahmenList(List<Einnahmen> einnahmenList) {
        this.einnahmenList = einnahmenList;
    }

    public List<Einnahmen> getFilteredEinnahmenList() {
        return filteredEinnahmenList;
    }

    public void setFilteredEinnahmenList(List<Einnahmen> filteredEinnahmenList) {
        this.filteredEinnahmenList = filteredEinnahmenList;
    }

    public Integer getRownumbers() {
        return rownumbers;
    }

    public void setRownumbers(Integer rownumbers) {
        this.rownumbers = rownumbers;
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

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public String getDeleteID() {
        return deleteID;
    }

    public void setDeleteID(String deleteID) {
        this.deleteID = deleteID;
    }

    public String getAnhangID() {
        return anhangID;
    }

    public void setAnhangID(String anhangID) {
        this.anhangID = anhangID;
    }

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
    }

    public String getNeuEinnahmenkategorie() {
        return neuEinnahmenkategorie;
    }

    public void setNeuEinnahmenkategorie(String neuEinnahmenkategorie) {
        this.neuEinnahmenkategorie = neuEinnahmenkategorie;
    }

    public String getChange_einnahmenkategorie() {
        return change_einnahmenkategorie;
    }

    public void setChange_einnahmenkategorie(String change_einnahmenkategorie) {
        this.change_einnahmenkategorie = change_einnahmenkategorie;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public List<Ausgabenausgabezeitraum> getAusgabenausgabezeitraumList() {
        return ausgabenausgabezeitraumList;
    }

    public void setAusgabenausgabezeitraumList(List<Ausgabenausgabezeitraum> ausgabenausgabezeitraumList) {
        this.ausgabenausgabezeitraumList = ausgabenausgabezeitraumList;
    }

    public EinnahmenKategorie getDeleteEinnahmenkategorie() {
        return deleteEinnahmenkategorie;
    }

    public void setDeleteEinnahmenkategorie(EinnahmenKategorie deleteEinnahmenkategorie) {
        this.deleteEinnahmenkategorie = deleteEinnahmenkategorie;
    }

    public Ausgabenausgabezeitraum getHaeufigkeit() {
        return haeufigkeit;
    }

    public void setHaeufigkeit(Ausgabenausgabezeitraum haeufigkeit) {
        this.haeufigkeit = haeufigkeit;
    }

    public Date getEingangsdatum() {
        return eingangsdatum;
    }

    public void setEingangsdatum(Date eingangsdatum) {
        this.eingangsdatum = eingangsdatum;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    public Double getEinnahmenMonatSumme() {
        return einnahmenMonatSumme;
    }

    public void setEinnahmenMonatSumme(Double einnahmenMonatSumme) {
        this.einnahmenMonatSumme = einnahmenMonatSumme;
    }

    public Double getEinnahmenJaehrlichSumme() {
        return einnahmenJaehrlichSumme;
    }

    public void setEinnahmenJaehrlichSumme(Double einnahmenJaehrlichSumme) {
        this.einnahmenJaehrlichSumme = einnahmenJaehrlichSumme;
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

    public DonutChartModel getChartBarEinnahmenMonatlich() {
        return chartBarEinnahmenMonatlich;
    }

    public void setChartBarEinnahmenMonatlich(DonutChartModel chartBarEinnahmenMonatlich) {
        this.chartBarEinnahmenMonatlich = chartBarEinnahmenMonatlich;
    }

    public DonutChartModel getChartBarEinnahmenJaehrlich() {
        return chartBarEinnahmenJaehrlich;
    }

    public void setChartBarEinnahmenJaehrlich(DonutChartModel chartBarEinnahmenJaehrlich) {
        this.chartBarEinnahmenJaehrlich = chartBarEinnahmenJaehrlich;
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

    public Double getGewinnMonatlich() {
        return gewinnMonatlich;
    }

    public void setGewinnMonatlich(Double gewinnMonatlich) {
        this.gewinnMonatlich = gewinnMonatlich;
    }

    public Double getGewinnJaehrlich() {
        return gewinnJaehrlich;
    }

    public void setGewinnJaehrlich(Double gewinnJaehrlich) {
        this.gewinnJaehrlich = gewinnJaehrlich;
    }

    public Double getBetrag() {
        return betrag;
    }

    public void setBetrag(Double betrag) {
        this.betrag = betrag;
    }

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
    }

    public Double getDurchschnittlicheMonatlicheEinnahmen() {
        return durchschnittlicheMonatlicheEinnahmen;
    }

    public void setDurchschnittlicheMonatlicheEinnahmen(Double durchschnittlicheMonatlicheEinnahmen) {
        this.durchschnittlicheMonatlicheEinnahmen = durchschnittlicheMonatlicheEinnahmen;
    }

    public String getEinnahmenBestimmtenJahres() {
        return einnahmenBestimmtenJahres;
    }

    public void setEinnahmenBestimmtenJahres(String einnahmenBestimmtenJahres) {
        this.einnahmenBestimmtenJahres = einnahmenBestimmtenJahres;
    }

    public String getJahrmonat() {
        return jahrmonat;
    }

    public void setJahrmonat(String jahrmonat) {
        this.jahrmonat = jahrmonat;
    }

    public Date getSelectMonatJahr() {
        return selectMonatJahr;
    }

    public void setSelectMonatJahr(Date selectMonatJahr) {
        this.selectMonatJahr = selectMonatJahr;
    }

    public List<EinnahmenMonatEntwicklung> getEinnahmenMonatList() {
        return einnahmenMonatList;
    }

    public void setEinnahmenMonatList(List<EinnahmenMonatEntwicklung> einnahmenMonatList) {
        this.einnahmenMonatList = einnahmenMonatList;
    }

    public List<EinnahmenMonatEntwicklung> getFilteredEinnahmenMonatList() {
        return filteredEinnahmenMonatList;
    }

    public void setFilteredEinnahmenMonatList(List<EinnahmenMonatEntwicklung> filteredEinnahmenMonatList) {
        this.filteredEinnahmenMonatList = filteredEinnahmenMonatList;
    }

    public List<EinnahmenJahrEntwicklung> getEinnahmenJahrList() {
        return einnahmenJahrList;
    }

    public void setEinnahmenJahrList(List<EinnahmenJahrEntwicklung> einnahmenJahrList) {
        this.einnahmenJahrList = einnahmenJahrList;
    }

    public List<EinnahmenJahrEntwicklung> getFilteredEinnahmenJahrList() {
        return filteredEinnahmenJahrList;
    }

    public void setFilteredEinnahmenJahrList(List<EinnahmenJahrEntwicklung> filteredEinnahmenJahrList) {
        this.filteredEinnahmenJahrList = filteredEinnahmenJahrList;
    }

    public LineChartModel getChartEinnahmenMonatEntwicklung() {
        return chartEinnahmenMonatEntwicklung;
    }

    public void setChartEinnahmenMonatEntwicklung(LineChartModel chartEinnahmenMonatEntwicklung) {
        this.chartEinnahmenMonatEntwicklung = chartEinnahmenMonatEntwicklung;
    }

    public LineChartModel getChartEinnahmenJahrEntwicklung() {
        return chartEinnahmenJahrEntwicklung;
    }

    public void setChartEinnahmenJahrEntwicklung(LineChartModel chartEinnahmenJahrEntwicklung) {
        this.chartEinnahmenJahrEntwicklung = chartEinnahmenJahrEntwicklung;
    }

    public Double getGesamtMonatJahrEinnahmen() {
        return gesamtMonatJahrEinnahmen;
    }

    public void setGesamtMonatJahrEinnahmen(Double gesamtMonatJahrEinnahmen) {
        this.gesamtMonatJahrEinnahmen = gesamtMonatJahrEinnahmen;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.einnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.einnahmenkategorieList);
        hash = 29 * hash + Objects.hashCode(this.ausgabenausgabezeitraumList);
        hash = 29 * hash + Objects.hashCode(this.einnahmenList);
        hash = 29 * hash + Objects.hashCode(this.filteredEinnahmenList);
        hash = 29 * hash + Objects.hashCode(this.rownumbers);
        hash = 29 * hash + Objects.hashCode(this.baseUrl);
        hash = 29 * hash + Objects.hashCode(this.downloadUrl);
        hash = 29 * hash + Objects.hashCode(this.anhangname);
        hash = 29 * hash + Objects.hashCode(this.anhangtype);
        hash = 29 * hash + Objects.hashCode(this.dao);
        hash = 29 * hash + Objects.hashCode(this.deleteID);
        hash = 29 * hash + Objects.hashCode(this.anhangID);
        hash = 29 * hash + Arrays.hashCode(this.anhang);
        hash = 29 * hash + Objects.hashCode(this.neuEinnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.change_einnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.deleteEinnahmenkategorie);
        hash = 29 * hash + Objects.hashCode(this.bezeichnung);
        hash = 29 * hash + Objects.hashCode(this.betrag);
        hash = 29 * hash + Objects.hashCode(this.haeufigkeit);
        hash = 29 * hash + Objects.hashCode(this.eingangsdatum);
        hash = 29 * hash + Objects.hashCode(this.informationen);
        hash = 29 * hash + Objects.hashCode(this.einnahmenMonatSumme);
        hash = 29 * hash + Objects.hashCode(this.einnahmenJaehrlichSumme);
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
        final EinnahmenController other = (EinnahmenController) obj;
        if (!Objects.equals(this.baseUrl, other.baseUrl)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrl, other.downloadUrl)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.anhangID, other.anhangID)) {
            return false;
        }
        if (!Objects.equals(this.neuEinnahmenkategorie, other.neuEinnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.change_einnahmenkategorie, other.change_einnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenkategorie, other.einnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenkategorieList, other.einnahmenkategorieList)) {
            return false;
        }
        if (!Objects.equals(this.ausgabenausgabezeitraumList, other.ausgabenausgabezeitraumList)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenList, other.einnahmenList)) {
            return false;
        }
        if (!Objects.equals(this.filteredEinnahmenList, other.filteredEinnahmenList)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.deleteEinnahmenkategorie, other.deleteEinnahmenkategorie)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        if (!Objects.equals(this.haeufigkeit, other.haeufigkeit)) {
            return false;
        }
        if (!Objects.equals(this.eingangsdatum, other.eingangsdatum)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenMonatSumme, other.einnahmenMonatSumme)) {
            return false;
        }
        if (!Objects.equals(this.einnahmenJaehrlichSumme, other.einnahmenJaehrlichSumme)) {
            return false;
        }
        return true;
    }

}
