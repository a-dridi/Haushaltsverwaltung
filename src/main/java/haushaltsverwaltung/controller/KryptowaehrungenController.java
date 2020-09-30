/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import haushaltsverwaltung.main.KryptowaehrungWaehrungWertGruppe;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.KryptowaehrungenExchange;
import haushaltsverwaltung.model.KryptowaehrungenKaufVerkauf;
import haushaltsverwaltung.model.KryptowaehrungenUeberweisungen;
import haushaltsverwaltung.model.KryptowaehrungenVermoegen;
import haushaltsverwaltung.model.KryptowaehrungenVorgang;
import haushaltsverwaltung.model.KryptowaehrungenWaehrungen;
import haushaltsverwaltung.model.KryptowaehrungenWerteVerz;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 *
 * Cryptocurrency / Kryptowährungen
 *
 * @author A.Dridi
 */
@Named(value = "kryptowaehrungenController")
@ViewScoped
public class KryptowaehrungenController implements Serializable {

    private LineChartModel chartVermoegenWertineuro;
    private DonutChartModel chartPortfolioWertineuro;
    private List<KryptowaehrungWaehrungWertGruppe> zuwachsListe = new ArrayList<>();
    private List<KryptowaehrungWaehrungWertGruppe> filteredZuwachsListe = new ArrayList<>();

    private List<KryptowaehrungenExchange> exchangesList = new ArrayList<>();
    private List<KryptowaehrungenVorgang> vorgaengeList = new ArrayList<>();
    private List<KryptowaehrungenWaehrungen> waehrungenList = new ArrayList<>();

    private List<KryptowaehrungenKaufVerkauf> kaufverkaufListenSQL = new ArrayList<>();
    private List<KryptowaehrungenKaufVerkauf> filteredKaufverkaufListenSQL = new ArrayList<>();
    private List<KryptowaehrungenVermoegen> vermoegenListenSQL = new ArrayList<>();
    private List<KryptowaehrungenVermoegen> filteredVermoegenListenSQL = new ArrayList<>();
    private List<KryptowaehrungenUeberweisungen> ueberweisungenListenSQL = new ArrayList<>();
    private List<KryptowaehrungenUeberweisungen> filteredUeberweisungenListenSQL = new ArrayList<>();
    private DatenbankNotizen dbnotizEintrag = null;

    //ExportColumns - WICHTIG ANZAHL AN SPALTENANZAHL ANPASSEN (ausgenommen Anhang/D Spalte)!!!:
    private List<Boolean> columnList = Arrays.asList(true, true, true, true, true, true, true, true, true, true);
    private List<Boolean> columnListVermoegen = Arrays.asList(true, true, true, true, true, true, true);

    private String datensaetzeAnzahlTextKaufverkauf;
    private String datensaetzeAnzahlTextUeberweisungen;
    private String datensaetzeAnzahlTextVermoegen;

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String tabellenname = "Kryptowaehrungen";
    private String baseUrlKaufverkauf = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/KaufVerkauf";
    private String downloadUrlKaufverkauf = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/KaufVerkauf";
    private String baseUrlVermoegen = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Vermoegen";
    private String downloadUrlVermoegen = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Vermoegen";
    //KryptowaehrungenUeberweisungen:
    private String baseUrlTransfer = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Transfer";
    private String downloadUrlTransfer = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Kryptowaehrungen/Transfer";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    //Global Kryptowaehrungen
    private String notiztext;

    //KryptowaehrungenKaufVerkauf:
    private Date kaufverkaufDatum;
    private KryptowaehrungenVorgang kryptowaehrungVorgangEintrag;
    private Float kaufverkaufAusgangsbetrag;
    private KryptowaehrungenWaehrungen kaufverkaufAusgangswaehrungEintrag;
    private Float kaufverkaufEndbetrag;
    private KryptowaehrungenWaehrungen kaufverkaufEndwaehrungEintrag;
    private Double kaufverkaufWertInEuro;
    private KryptowaehrungenExchange kaufverkaufExchangeEintrag;
    private String kaufverkaufBemerkungen;

    //KryptowaehrungenVermoegen:
    private KryptowaehrungenWaehrungen vermoegenWaehrungEintrag;
    private Float vermoegenBetrag;
    private String vermoegenLagerort;
    private Float vermoegenWertineuro;
    private String vermoegenBemerkungen;

    private Double gesamtwertEuro;

    //KryptowaehrungenUeberweisungen (Transfer):
    private Date ueberweisungenDatum;
    private String ueberweisungenSender;
    private Float ueberweisungenBetrag;
    private KryptowaehrungenWaehrungen ueberweisungenWaehrungEintrag;
    private String ueberweisungenEmpfaenger;
    private String ueberweisungenZustand;
    private String ueberweisungenBemerkungen;

    private DAO dao;
    private String neuExchange;
    private String neuKryptowaehrungVorgang;
    private String neuWaehrung;

    private String change_exchange;
    private String change_waehrung;
    private String change_vorgang;

    private KryptowaehrungenExchange deleteKryptowaehrungenExchange;
    private KryptowaehrungenVorgang deleteKryptowaehrungVorgang;
    private KryptowaehrungenWaehrungen deleteKryptowaehrungenWaehrungen;

    private Integer rownumbers = 15;
    private String anhangID;
    private String deleteID;
    private String anhangname;
    private String anhangtype;
    private byte[] anhang;

    private String datumNotiztext;

    private List<KryptowaehrungenKaufVerkauf> deletedKaufverkaufListenSQL = new ArrayList<>();
    private List<KryptowaehrungenVermoegen> deletedVermoegenListenSQL = new ArrayList<>();
    private List<KryptowaehrungenUeberweisungen> deletedUeberweisungenListenSQL = new ArrayList<>();

    /**
     * Creates a new instance of KryptowaehrungenController
     */
    public KryptowaehrungenController() {
    }

    @PostConstruct
    private void init() {
        this.dao = new DAO();
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
            this.datumNotiztext = "Zuletzt akualisiert: " + notizList.get(0).getDatum();
        }

        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        //Tabelle Kauf-Verkauf
        if (urlName.contains("kryptowaehrungen.xhtml")) {
            updateDataKaufverkauf();
        } else if (urlName.contains("kryptowaehrungen_ueberweisung.xhtml")) {
            updateDataUeberweisungen();

        } else if (urlName.contains("kryptowaehrungen_vermoegen.xhtml")) {
            updateDataVermoegen();

        } else if (urlName.contains("portfolio_zustand.xhtml")) {
            this.waehrungenList = dao.getAllKryptowaehrungenWaehrungen();
            createVermoegenWertineuroChart();
            createPortfolioWertinEuro();
        } else if (urlName.contains("portfolio_zustand_tabelle.xhtml")) {
            this.waehrungenList = dao.getAllKryptowaehrungenWaehrungen();
            createVermoegenWertineuroChart();
        } else {
            updateDataKaufverkauf();
        }
    }

    /**
     * Gesamtvermögen in EURO
     *
     * @return
     */
    public void calculateGesamtWertinEuro() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(wertInEuro) FROM KryptowaehrungenVermoegen where deleted=false";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.gesamtwertEuro = waehrunggruppe.get(0);

            s.close();

        } else {
            s.close();
            this.gesamtwertEuro = 0.0;
        }

    }

    /**
     * DB Abfrage für Summe von WertinEuro einer Währung
     */
    public Double getSummeWertinEuro(String waehrung) {
        if (waehrung != null && !waehrung.isEmpty()) {
            Session s = HibernateUtil.getSessionFactory().openSession();
            String sqlstring = "Select sum(wertineuro) FROM KryptowaehrungenWerteVerz where waehrung = :waehrungvariable";
            Query qu = s.createQuery(sqlstring);
            qu.setString("waehrungvariable", waehrung);
            List<Double> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                s.close();
                return waehrunggruppe.get(0);
            } else {
                s.close();
                return 0.0;
            }

        } else {
            return 0.0;
        }
    }

    /**
     * DB Abfrage für Gesamtvermoegen in Kryptowaehrung von Kryptowaehrung
     */
    public Double getSummeKryptowaehrung(String waehrung) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(betrag) FROM KryptowaehrungenWerteVerz where waehrung = :waehrungvariable";
        Query qu = s.createQuery(sqlstring);
        qu.setString("waehrungvariable", waehrung);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            s.close();
            return waehrunggruppe.get(0);
        } else {
            s.close();
            return 0.0;
        }

    }

    /**
     * Erstellt ein Liniendiagramm (Chart- Typ Area) mit Kryptowaehrungen und
     * deren Wert in Euro dargestellt mit dem jeweiligen Datum ERSTELLT AUCH
     * Zuwachsvergleiche
     */
    public void createVermoegenWertineuroChart() {
        this.chartVermoegenWertineuro = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        //SQL Abruf
        // Liefert alle Einträge (Werte in Euro) für eine Währung sortiert
        // aufsteigend nach dem Datum
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<KryptowaehrungenWaehrungen> waehrungenliste = this.waehrungenList;
        abbrechen:
        for (KryptowaehrungenWaehrungen w : waehrungenliste) {
            try {
                //Die letzten zwei (aktuellesten) Werte (Werte in Euro) von der DB holen und zur Zuwachsberechnung verwenden. 
                String sqlstring = "Select datum, sum(wertineuro), sum(betrag) FROM KryptowaehrungenWerteVerz where waehrung = :waehrungvariable group by datum order by datum asc";

                Query qu = s.createQuery(sqlstring);
                qu.setString("waehrungvariable", w.getWaehrungsname());
                List<Object[]> waehrunggruppe = qu.list();

                if (waehrunggruppe != null && !waehrunggruppe.isEmpty()) {
                    // this.gesamtwertEuro = 0.0;
                    //Chart für die jetzt aufgerufene Währung einrichten                    
                    KryptowaehrungWaehrungWertGruppe zuwachsvergl = new KryptowaehrungWaehrungWertGruppe();
                    //Alle Datumeinträge hinzufügen
                    for (Object[] o : waehrunggruppe) {
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                        values.add((Number) o[1]);
                        labels.add(df.format(o[0]));
                        // this.gesamtwertEuro += ((Double) o[1]);
                        //Speichern von Werten für Zuwachsberechnung (zwischen Alt und Neu Wert
                    }
                    //Werte für Zuwachs vorhanden (wenn schon Werte in Euro einmal aktualisiert wurden)
                    if (waehrunggruppe.size() >= 2) {
                        zuwachsvergl.setWaehrung(w.getWaehrungsname());
                        zuwachsvergl.setBetrag((Double) waehrunggruppe.get((waehrunggruppe.size() - 1))[2]);
                        zuwachsvergl.setWertineuroAlt((Double) waehrunggruppe.get((waehrunggruppe.size() - 2))[1]);
                        zuwachsvergl.setWertineuroNeu((Double) waehrunggruppe.get((waehrunggruppe.size() - 1))[1]);
                        zuwachsvergl.setZuwachsProzent((((zuwachsvergl.getWertineuroNeu()) - (zuwachsvergl.getWertineuroAlt())) / (zuwachsvergl.getWertineuroAlt())) * 100);
                        if (zuwachsvergl.getZuwachsProzent() < 0) {
                            zuwachsvergl.setIsVerringerung(true);
                            zuwachsvergl.setIsZuwachs(false);
                        } else {
                            zuwachsvergl.setIsZuwachs(true);
                            zuwachsvergl.setIsVerringerung(false);
                        }
                        zuwachsListe.add(zuwachsvergl);
                    } else {
                        zuwachsvergl.setWaehrung(w.getWaehrungsname());
                        zuwachsvergl.setBetrag((Double) waehrunggruppe.get((waehrunggruppe.size() - 1))[2]);
                        zuwachsvergl.setWertineuroAlt((Double) waehrunggruppe.get((waehrunggruppe.size() - 1))[1]);
                        zuwachsvergl.setWertineuroNeu((Double) waehrunggruppe.get((waehrunggruppe.size() - 1))[1]);
                        zuwachsvergl.setZuwachsProzent(0.0);
                        zuwachsvergl.setIsZuwachs(false);
                        zuwachsvergl.setIsVerringerung(false);
                        zuwachsListe.add(zuwachsvergl);
                    }
                }
            } catch (Exception e) {
                System.out.println("Fehler in createVermoegenWertineuroChart: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Abfrage von KryptowaehrungenWerteVerz:", "" + e));
                break abbrechen;
            }

        }
        dataSet.setData(values);
        dataSet.setLabel("Gesamtvermögen");
        dataSet.setYaxisID("left-y-axis");
        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setId("left-y-axis");
        linearAxes.setPosition("left");

        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Gesamtvermögen in Euro");
        options.setTitle(title);
        this.chartVermoegenWertineuro.setData(data);
        this.chartVermoegenWertineuro.setOptions(options);

        this.filteredZuwachsListe = new ArrayList<>(this.zuwachsListe);
        s.close();
    }

    public void createPortfolioWertinEuro() {

        this.chartPortfolioWertineuro = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        //SQL Abruf
        // Liefert alle Einträge (Werte in Euro) für eine Währung sortiert
        // aufsteigend nach dem Datum
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<KryptowaehrungenWaehrungen> waehrungenliste = this.waehrungenList;
        abbrechen:
        for (KryptowaehrungenWaehrungen w : waehrungenliste) {
            try {
                String sqlstring = "Select waehrung, sum(wertineuro) FROM KryptowaehrungenWerteVerz where waehrung = :waehrungvariable group by waehrung order by waehrung asc";

                Query qu = s.createQuery(sqlstring);
                qu.setString("waehrungvariable", w.getWaehrungsname());
                List<Object[]> waehrunggruppe = qu.list();

                if (waehrunggruppe != null && !waehrunggruppe.isEmpty()) {
                    //Alle Datumeinträge hinzufügen
                    for (Object[] o : waehrunggruppe) {
                        if ((Double) o[1] != 0.0) {
                            // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));
                            labels.add((String) o[0]);
                            values.add((Double) o[1]);
                            //Hintergrundfarben durch Zufall erstellen.
                            Random rnd = new Random();
                            bgColors.add("rgb(" + rnd.nextInt(240) + "," + rnd.nextInt(240) + "," + rnd.nextInt(240) + ")");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Fehler in createPortfolioWertinEuro: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Abfrage von KryptowaehrungenWerteVerz:", "" + e));
                break abbrechen;
            }
        }
        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        this.chartPortfolioWertineuro.setData(data);
    }

    /**
     * KaufVerkauf Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs
     * wird der Anhang für die betroffene Zeile gelöscht
     */
    public void editAnhangKaufverkauf() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<KryptowaehrungenKaufVerkauf> liste = new ArrayList<>(this.kaufverkaufListenSQL);
            gefunden:
            for (KryptowaehrungenKaufVerkauf a : liste) {
                if (a.getId().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getId()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrlKaufverkauf + "/" + ((a.getId()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrlKaufverkauf + "/" + ((a.getId()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateKryptowaehrungenKaufVerkauf(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrlKaufverkauf + "/" + ((a.getId()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateKryptowaehrungenKaufVerkauf(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
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
     * KryptowaehrungenVermoegen Anhang bearbeiten: Aber bei Übergabe eines
     * leeren Anhangs wird der Anhang für die betroffene Zeile gelöscht
     */
    public void editAnhangKryptowaehrungenVermoegen() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<KryptowaehrungenVermoegen> liste = new ArrayList<>(this.vermoegenListenSQL);
            gefunden:
            for (KryptowaehrungenVermoegen a : liste) {
                if (a.getId().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getId()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrlVermoegen + "/" + ((a.getId()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrlVermoegen + "/" + ((a.getId()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateKryptowaehrungenVermoegen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrlVermoegen + "/" + ((a.getId()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateKryptowaehrungenVermoegen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
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
     * KryptowaehrungenUeberweisungen Anhang bearbeiten: Aber bei Übergabe eines
     * leeren Anhangs wird der Anhang für die betroffene Zeile gelöscht
     */
    public void editAnhangKryptowaehrungenUeberweisungen() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<KryptowaehrungenUeberweisungen> liste = new ArrayList<>(this.ueberweisungenListenSQL);
            gefunden:
            for (KryptowaehrungenUeberweisungen a : liste) {
                if (a.getId().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getId()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrlTransfer + "/" + ((a.getId()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrlTransfer + "/" + ((a.getId()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateKryptowaehrungenUeberweisungen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrlTransfer + "/" + ((a.getId()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateKryptowaehrungenUeberweisungen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
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
     * Tabelle Kauf-Verkauf, Ueberweisung, Vermoegen - Editor für Zeile aufrufen
     */
    public void editRow(CellEditEvent event) {

        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        //Tabelle Kauf-Verkauf
        if (urlName.contains("kryptowaehrungen.xhtml")) {
            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                KryptowaehrungenKaufVerkauf a = (this.dao.getSingleKryptowaehrungenKaufVerkauf((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Datum")) {
                    if (event.getNewValue() != null) {
                        a.setDatum((Date) event.getNewValue());
                    }
                }
                if (spaltenname.equals("Vorgang")) {

                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenVorgang m : this.vorgaengeList) {
                        if (m.getVorgangbeschreibung().equals(auswahl)) {
                            a.setVorgang((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Ausgangs-Betrag")) {
                    a.setAusgangsbetrag((Float) event.getNewValue());
                }
                if (spaltenname.equals("Ausgangs-Waehrung")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenWaehrungen m : this.waehrungenList) {
                        if (m.getWaehrungsname().equals(auswahl)) {
                            a.setAusgangswaehrung((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }

                if (spaltenname.equals("End-Betrag")) {
                    a.setEndbetrag((Float) event.getNewValue());
                }

                if (spaltenname.equals("End-Waehrung")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenWaehrungen m : this.waehrungenList) {
                        if (m.getWaehrungsname().equals(auswahl)) {
                            a.setEndwaehrung((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Wert in EURO")) {
                    a.setWertineuro((Double) event.getNewValue());
                }

                if (spaltenname.equals("Exchange")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenExchange m : this.exchangesList) {
                        if (m.getExchangeName().equals(auswahl)) {
                            a.setExchange((String) event.getNewValue());
                            break gefunden;
                        }
                    }

                }

                if (spaltenname.equals("Informationen")) {
                    a.setBemerkungen((String) event.getNewValue());
                }

                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateKryptowaehrungenKaufVerkauf(a);
                updateDataKaufverkauf();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        } //Tabelle Ueberweisung
        else if (urlName.contains("kryptowaehrungen_ueberweisung.xhtml")) {
            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                KryptowaehrungenUeberweisungen a = (this.dao.getSingleKryptowaehrungenUeberweisungen((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Datum")) {
                    if (event.getNewValue() != null) {
                        a.setDatum((Date) event.getNewValue());
                    }
                }

                if (spaltenname.equals("Sender")) {
                    a.setSender((String) event.getNewValue());
                }
                if (spaltenname.equals("Betrag")) {
                    a.setBetrag((Float) event.getNewValue());
                }

                if (spaltenname.equals("Waehrung")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenWaehrungen m : this.waehrungenList) {
                        if (m.getWaehrungsname().equals(auswahl)) {
                            a.setWaehrung((String) event.getNewValue());
                            break gefunden;
                        }
                    }

                }

                if (spaltenname.equals("Empfaenger")) {
                    a.setEmpfaenger((String) event.getNewValue());
                }
                if (spaltenname.equals("Zustand")) {
                    a.setZustand((String) event.getNewValue());
                }
                if (spaltenname.equals("Bemerkungen")) {
                    a.setBemerkungen((String) event.getNewValue());
                }

                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateKryptowaehrungenUeberweisungen(a);
                updateDataUeberweisungen();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        } //Tabelle Vermoegen
        else if (urlName.contains("kryptowaehrungen_vermoegen.xhtml")) {
            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                //Zeile der Tabelle (Eintrag in der DB), welche bearbeitet wird
                KryptowaehrungenVermoegen a = (this.dao.getSingleKryptowaehrungenVermoegen((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Waehrung")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenWaehrungen m : this.waehrungenList) {
                        if (m.getWaehrungsname().equals(auswahl)) {
                            a.setWaehrung((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }

                if (spaltenname.equals("Betrag")) {
                    a.setBetrag((Float) event.getNewValue());

                    KryptowaehrungenWerteVerz v = new KryptowaehrungenWerteVerz();
                    Date d = new Date();
                    v.setWaehrung(a.getWaehrung());
                    v.setBetrag(this.vermoegenBetrag);
                    v.setLagerort(a.getLagerort());
                    v.setWertineuro((Float) event.getNewValue());
                    v.setDatum(d);
                    this.dao.insertKryptowaehrungenWerteVerz(v);

                }
                if (spaltenname.equals("Lagerort")) {
                    List<KryptowaehrungenWerteVerz> vList = dao.getLagerortAllKryptowaehrungenWerteVerz((String) event.getOldValue());
                    KryptowaehrungenWerteVerz v = vList.get(0);
                    v.setLagerort((String) event.getNewValue());
                    this.dao.updateKryptowaehrungenWerteVerz(v);
                    a.setLagerort((String) event.getNewValue());
                    vList = null;
                    v = null;
                }

                if (spaltenname.equals("Wert in EURO")) {

                    a.setWertInEuro((Float) event.getNewValue());
                    KryptowaehrungenWerteVerz v = new KryptowaehrungenWerteVerz();
                    Date d = new Date();
                    v.setBetrag(a.getBetrag());
                    v.setWaehrung(a.getWaehrung());
                    v.setLagerort(a.getLagerort());
                    v.setWertineuro((Float) event.getNewValue());
                    v.setDatum(d);
                    this.dao.insertKryptowaehrungenWerteVerz(v);
                }

                if (spaltenname.equals("Bemerkungen")) {
                    a.setBemerkungen((String) event.getNewValue());
                }

                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateKryptowaehrungenVermoegen(a);
                updateDataVermoegen();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        } else {
            //Startseite von Kryptowaehrungen = Tabelle Kaufverkauf
            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                KryptowaehrungenKaufVerkauf a = (this.dao.getSingleKryptowaehrungenKaufVerkauf((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Datum")) {
                    if (event.getNewValue() != null) {
                        a.setDatum((Date) event.getNewValue());
                    }
                }
                if (spaltenname.equals("Vorgang")) {

                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenVorgang m : this.vorgaengeList) {
                        if (m.getVorgangbeschreibung().equals(auswahl)) {
                            a.setVorgang((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Ausgangs-Betrag")) {
                    a.setAusgangsbetrag((Float) event.getNewValue());
                }
                if (spaltenname.equals("Ausgangs-Waehrung")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenWaehrungen m : this.waehrungenList) {
                        if (m.getWaehrungsname().equals(auswahl)) {
                            a.setAusgangswaehrung((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }

                if (spaltenname.equals("End-Betrag")) {
                    a.setEndbetrag((Float) event.getNewValue());
                }

                if (spaltenname.equals("End-Waehrung")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenWaehrungen m : this.waehrungenList) {
                        if (m.getWaehrungsname().equals(auswahl)) {
                            a.setEndwaehrung((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Wert in EURO")) {
                    a.setWertineuro((Double) event.getNewValue());
                }

                if (spaltenname.equals("Exchange")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (KryptowaehrungenExchange m : this.exchangesList) {
                        if (m.getExchangeName().equals(auswahl)) {
                            a.setExchange((String) event.getNewValue());
                            break gefunden;
                        }
                    }

                }

                if (spaltenname.equals("Informationen")) {
                    a.setBemerkungen((String) event.getNewValue());
                }

                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateKryptowaehrungenKaufVerkauf(a);
                updateDataKaufverkauf();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        }

    }

    public void updateData() {
        this.dao = new DAO();
        this.exchangesList = dao.getAllKryptowaehrungenExchange();
        this.vorgaengeList = dao.getAllKryptowaehrungenVorgang();
        this.waehrungenList = dao.getAllKryptowaehrungenWaehrungen();
        this.kaufverkaufListenSQL = dao.getAllKryptowaehrungenKaufVerkauf();
        this.filteredKaufverkaufListenSQL = new ArrayList<>(this.kaufverkaufListenSQL);

        this.vermoegenListenSQL = dao.getAllKryptowaehrungenVermoegen();
        this.ueberweisungenListenSQL = dao.getAllKryptowaehrungenUeberweisungen();
        this.filteredUeberweisungenListenSQL = new ArrayList<>(this.ueberweisungenListenSQL);
        this.filteredVermoegenListenSQL = new ArrayList<>(this.vermoegenListenSQL);

        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrlKaufverkauf);

        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        flushAnhang();
        calculateGesamtWertinEuro();
        this.datensaetzeAnzahlTextKaufverkauf = ("Insgesamt: " + this.kaufverkaufListenSQL.size() + " Datensaetze in der DB gespeichert");
        this.datensaetzeAnzahlTextUeberweisungen = ("Insgesamt: " + this.ueberweisungenListenSQL.size() + " Datensaetze in der DB gespeichert");
        this.datensaetzeAnzahlTextVermoegen = ("Insgesamt: " + this.vermoegenListenSQL.size() + " Datensaetze in der DB gespeichert");

    }

    public void updateDataKaufverkauf() {
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrlKaufverkauf);

        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }

        this.kaufverkaufListenSQL = dao.getAllKryptowaehrungenKaufVerkauf();
        this.filteredKaufverkaufListenSQL = new ArrayList<>(this.kaufverkaufListenSQL);
        this.exchangesList = dao.getAllKryptowaehrungenExchange();
        this.vorgaengeList = dao.getAllKryptowaehrungenVorgang();
        this.waehrungenList = dao.getAllKryptowaehrungenWaehrungen();

        this.datensaetzeAnzahlTextKaufverkauf = ("Insgesamt: " + this.kaufverkaufListenSQL.size() + " Datensaetze in der DB gespeichert");
        flushAnhang();

    }

    public void updateDataVermoegen() {
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrlKaufverkauf);

        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        this.waehrungenList = dao.getAllKryptowaehrungenWaehrungen();
        this.vermoegenListenSQL = dao.getAllKryptowaehrungenVermoegen();
        this.filteredVermoegenListenSQL = new ArrayList<>(this.vermoegenListenSQL);
        this.datensaetzeAnzahlTextUeberweisungen = ("Insgesamt: " + this.ueberweisungenListenSQL.size() + " Datensaetze in der DB gespeichert");
        flushAnhang();
        calculateGesamtWertinEuro();

    }

    public void updateDataUeberweisungen() {
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrlKaufverkauf);

        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }

        this.waehrungenList = dao.getAllKryptowaehrungenWaehrungen();
        this.ueberweisungenListenSQL = dao.getAllKryptowaehrungenUeberweisungen();
        this.filteredUeberweisungenListenSQL = new ArrayList<>(this.ueberweisungenListenSQL);

        flushAnhang();
        this.datensaetzeAnzahlTextUeberweisungen = ("Insgesamt: " + this.ueberweisungenListenSQL.size() + " Datensaetze in der DB gespeichert");

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

    public void speichernKaufverkauf() {
        KryptowaehrungenKaufVerkauf kaufverkauf = new KryptowaehrungenKaufVerkauf();
        kaufverkauf.setDeleted(false);

        if (this.kaufverkaufDatum != null) {
            kaufverkauf.setDatum(this.kaufverkaufDatum);
        } else {
            kaufverkauf.setDatum(new Date());
        }
        if (this.kryptowaehrungVorgangEintrag != null) {
            kaufverkauf.setVorgang(this.kryptowaehrungVorgangEintrag.getVorgangbeschreibung());
        }

        kaufverkauf.setAusgangsbetrag(this.kaufverkaufAusgangsbetrag);
        if (this.kaufverkaufAusgangswaehrungEintrag != null) {
            kaufverkauf.setAusgangswaehrung(this.kaufverkaufAusgangswaehrungEintrag.getWaehrungsname());
        }

        kaufverkauf.setEndbetrag(this.kaufverkaufEndbetrag);

        if (this.kaufverkaufEndwaehrungEintrag != null) {
            kaufverkauf.setEndwaehrung(this.kaufverkaufEndwaehrungEintrag.getWaehrungsname());
        }
        if (this.kaufverkaufExchangeEintrag != null) {
            kaufverkauf.setExchange(this.kaufverkaufExchangeEintrag.getExchangeName());
        }
        kaufverkauf.setWertineuro(this.kaufverkaufWertInEuro);
        if (this.kaufverkaufBemerkungen != null) {
            kaufverkauf.setBemerkungen(this.kaufverkaufBemerkungen);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            kaufverkauf.setAnhang(true);
            this.kaufverkaufListenSQL.add(kaufverkauf);
            this.filteredKaufverkaufListenSQL.add(kaufverkauf);

            dao.insertKryptowaehrungenKaufVerkauf(kaufverkauf);

            List<KryptowaehrungenKaufVerkauf> kaufverkaufListe = new ArrayList(this.kaufverkaufListenSQL);
            int letzteNr = kaufverkaufListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = kaufverkaufListe.get(letzteNr).getId();
                try {
                    KryptowaehrungenKaufVerkauf a = kaufverkaufListe.get(letzteNr);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    InputStream ins = new ByteArrayInputStream(this.anhang);
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);

                    PutMethod method = new PutMethod(this.baseUrlKaufverkauf + "/" + (neueID) + "." + dateiext);
                    RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                    method.setRequestEntity(requestEntity);
                    client.executeMethod(method);
                    System.out.println(method.getStatusCode() + " " + method.getStatusText());
                    a.setAnhangpfad(this.downloadUrlKaufverkauf + "/" + (neueID) + "." + dateiext);

                    a.setAnhang(true);
                    a.setAnhangname((neueID) + "." + dateiext);
                    a.setAnhangtype(anhangtype);
                    dao.updateKryptowaehrungenKaufVerkauf(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateDataKaufverkauf();
            }

        } else {
            kaufverkauf.setAnhang(false);
            this.kaufverkaufListenSQL.add(kaufverkauf);
            this.filteredKaufverkaufListenSQL.add(kaufverkauf);

            dao.insertKryptowaehrungenKaufVerkauf(kaufverkauf);
            updateDataKaufverkauf();
        }
    }

    public void speichernVermoegen() {
        KryptowaehrungenVermoegen vermoegen = new KryptowaehrungenVermoegen();
        vermoegen.setDeleted(false);

        if (this.vermoegenWaehrungEintrag != null) {
            vermoegen.setWaehrung(this.vermoegenWaehrungEintrag.getWaehrungsname());
        }
        vermoegen.setBetrag(this.vermoegenBetrag);
        if (this.vermoegenLagerort != null) {
            vermoegen.setLagerort(this.vermoegenLagerort);
        }

        vermoegen.setWertInEuro(this.vermoegenWertineuro);
        KryptowaehrungenWerteVerz v = new KryptowaehrungenWerteVerz();
        Date d = new Date();
        v.setBetrag(this.vermoegenBetrag);
        v.setWaehrung(this.vermoegenWaehrungEintrag.getWaehrungsname());
        v.setLagerort(this.vermoegenLagerort);
        v.setWertineuro((Float) this.vermoegenWertineuro);
        v.setDatum(d);
        this.dao.insertKryptowaehrungenWerteVerz(v);

        if (this.vermoegenBemerkungen != null) {
            vermoegen.setBemerkungen(this.vermoegenBemerkungen);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            vermoegen.setAnhang(true);
            this.vermoegenListenSQL.add(vermoegen);
            this.filteredVermoegenListenSQL.add(vermoegen);

            dao.insertKryptowaehrungenVermoegen(vermoegen);

            List<KryptowaehrungenVermoegen> vermoegenListe = new ArrayList<>(this.vermoegenListenSQL);
            int letzteNr = vermoegenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = vermoegenListe.get(letzteNr).getId();
                try {
                    KryptowaehrungenVermoegen a = vermoegenListe.get(letzteNr);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    InputStream ins = new ByteArrayInputStream(this.anhang);
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);

                    PutMethod method = new PutMethod(this.baseUrlVermoegen + "/" + (neueID) + "." + dateiext);
                    RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                    method.setRequestEntity(requestEntity);
                    client.executeMethod(method);
                    System.out.println(method.getStatusCode() + " " + method.getStatusText());
                    a.setAnhangpfad(this.downloadUrlVermoegen + "/" + (neueID) + "." + dateiext);

                    a.setAnhang(true);
                    a.setAnhangname((neueID) + "." + dateiext);
                    a.setAnhangtype(anhangtype);
                    dao.updateKryptowaehrungenVermoegen(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateDataVermoegen();
            }

        } else {
            vermoegen.setAnhang(false);
            this.vermoegenListenSQL.add(vermoegen);
            this.filteredVermoegenListenSQL.add(vermoegen);
            dao.insertKryptowaehrungenVermoegen(vermoegen);
            updateDataVermoegen();
        }
    }

    public void speichernUeberweisungen() {
        KryptowaehrungenUeberweisungen ueberweisung = new KryptowaehrungenUeberweisungen();
        ueberweisung.setDeleted(false);

        if (this.ueberweisungenSender != null) {
            ueberweisung.setSender(ueberweisungenSender);
        }

        if (this.ueberweisungenDatum != null) {
            ueberweisung.setDatum(this.ueberweisungenDatum);
        } else {
            ueberweisung.setDatum(new Date());
        }

        ueberweisung.setBetrag(this.ueberweisungenBetrag);

        if (this.ueberweisungenWaehrungEintrag != null) {
            ueberweisung.setWaehrung(this.ueberweisungenWaehrungEintrag.getWaehrungsname());
        }
        if (this.ueberweisungenEmpfaenger != null) {
            ueberweisung.setEmpfaenger(this.ueberweisungenEmpfaenger);
        }
        if (this.ueberweisungenZustand != null) {
            ueberweisung.setZustand(this.ueberweisungenZustand);
        }
        if (this.ueberweisungenBemerkungen != null) {
            ueberweisung.setBemerkungen(this.ueberweisungenBemerkungen);
        }
        if (this.anhang != null && !this.anhangname.isEmpty()) {
            ueberweisung.setAnhang(true);
            this.ueberweisungenListenSQL.add(ueberweisung);
            this.filteredUeberweisungenListenSQL.add(ueberweisung);

            dao.insertKryptowaehrungenUeberweisungen(ueberweisung);

            List<KryptowaehrungenUeberweisungen> ueberweisungenListe = new ArrayList<>(this.ueberweisungenListenSQL);
            int letzteNr = ueberweisungenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = ueberweisungenListe.get(letzteNr).getId();
                try {
                    KryptowaehrungenUeberweisungen a = ueberweisungenListe.get(letzteNr);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    InputStream ins = new ByteArrayInputStream(this.anhang);
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);

                    PutMethod method = new PutMethod(this.baseUrlTransfer + "/" + (neueID) + "." + dateiext);
                    RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                    method.setRequestEntity(requestEntity);
                    client.executeMethod(method);
                    System.out.println(method.getStatusCode() + " " + method.getStatusText());
                    a.setAnhangpfad(this.downloadUrlTransfer + "/" + (neueID) + "." + dateiext);

                    a.setAnhang(true);
                    a.setAnhangname((neueID) + "." + dateiext);
                    a.setAnhangtype(anhangtype);
                    dao.updateKryptowaehrungenUeberweisungen(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateDataUeberweisungen();
            }

        } else {
            ueberweisung.setAnhang(false);
            this.ueberweisungenListenSQL.add(ueberweisung);
            this.filteredUeberweisungenListenSQL.add(ueberweisung);
            dao.insertKryptowaehrungenUeberweisungen(ueberweisung);
            updateDataUeberweisungen();
        }
    }

    /**
     * Methode nach dem Speichern. ! WIRD auto. in updateData() ausgeführt
     */
    public void flushAnhang() {
        this.anhang = null;
        this.anhangname = "";
        this.anhangtype = "";

    }

    public void exchangeSpeichern() {

        if (!this.neuExchange.isEmpty()) {
            KryptowaehrungenExchange ak = new KryptowaehrungenExchange();
            ak.setExchangeName(this.neuExchange);
            dao.insertKryptowaehrungenExchange(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataKaufverkauf();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void vorgangSpeichern() {

        if (!this.neuKryptowaehrungVorgang.isEmpty()) {
            KryptowaehrungenVorgang ak = new KryptowaehrungenVorgang();
            ak.setVorgangbeschreibung(this.neuKryptowaehrungVorgang);
            dao.insertKryptowaehrungenVorgang(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataKaufverkauf();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void waehrungSpeichern() {

        if (!this.neuWaehrung.isEmpty()) {

            KryptowaehrungenWaehrungen ak = new KryptowaehrungenWaehrungen();
            ak.setWaehrungsname(this.neuWaehrung);
            dao.insertKryptowaehrungenWaehrungen(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateData();
        }
    }

    public void exchangeLoeschen() {

        if (this.deleteKryptowaehrungenExchange != null) {

            List<KryptowaehrungenExchange> akList = new ArrayList(this.exchangesList);
            List<KryptowaehrungenKaufVerkauf> kaufverkaufList = new ArrayList(this.kaufverkaufListenSQL);
            boolean kategorieExist = false;

            for (KryptowaehrungenExchange a : akList) {
                if ((a.getExchangeName().toLowerCase()).equals(this.deleteKryptowaehrungenExchange.getExchangeName().toLowerCase())) {
                    dao.deleteKryptowaehrungenExchange(a);
                    for (KryptowaehrungenKaufVerkauf eintrag : kaufverkaufList) {
                        if ((eintrag.getExchange().toLowerCase()).equals(this.deleteKryptowaehrungenExchange.getExchangeName().toLowerCase())) {
                            this.kaufverkaufListenSQL.remove(eintrag);
                            this.filteredKaufverkaufListenSQL.remove(eintrag);

                            eintrag.setExchange(this.change_exchange);
                            dao.updateKryptowaehrungenKaufVerkauf(eintrag);
                            this.kaufverkaufListenSQL.add(eintrag);
                            this.filteredKaufverkaufListenSQL.add(eintrag);
                        }
                    }
                }
                if ((a.getExchangeName().toLowerCase()).equals(this.change_exchange.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                if (change_exchange != null && !change_exchange.equals("") && !change_exchange.equals(" ")) {
                    KryptowaehrungenExchange neu = new KryptowaehrungenExchange();
                    neu.setExchangeName(this.change_exchange);
                    dao.insertKryptowaehrungenExchange(neu);

                }
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));
            updateDataKaufverkauf();
        }
    }

    public void vorgangLoeschen() {

        if (this.deleteKryptowaehrungVorgang != null) {

            List<KryptowaehrungenVorgang> akList = new ArrayList(this.vorgaengeList);
            List<KryptowaehrungenKaufVerkauf> kaufverkaufList = new ArrayList();
            boolean kategorieExist = false;

            for (KryptowaehrungenVorgang a : akList) {
                if ((a.getVorgangbeschreibung().toLowerCase()).equals(this.deleteKryptowaehrungVorgang.getVorgangbeschreibung().toLowerCase())) {
                    dao.deleteKryptowaehrungenVorgang(a);
                    for (KryptowaehrungenKaufVerkauf kkv : kaufverkaufList) {
                        if ((kkv.getVorgang().toLowerCase()).equals(this.deleteKryptowaehrungVorgang.getVorgangbeschreibung().toLowerCase())) {
                            this.kaufverkaufListenSQL.remove(kkv);
                            this.filteredKaufverkaufListenSQL.remove(kkv);

                            kkv.setVorgang(this.change_vorgang);
                            dao.updateKryptowaehrungenKaufVerkauf(kkv);
                            this.kaufverkaufListenSQL.add(kkv);
                            this.filteredKaufverkaufListenSQL.add(kkv);

                        }
                    }
                }
                if ((a.getVorgangbeschreibung().toLowerCase()).equals(this.change_vorgang.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                if (change_vorgang != null && !change_vorgang.equals("") && !change_vorgang.equals(" ")) {
                    KryptowaehrungenVorgang neu = new KryptowaehrungenVorgang();
                    neu.setVorgangbeschreibung(this.change_vorgang);
                    dao.insertKryptowaehrungenVorgang(neu);
                }
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataKaufverkauf();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    /**
     * Betrifft alle Kryptowaehrungen Tabellen
     */
    public void waehrungLoeschen() {

        if (this.deleteKryptowaehrungenWaehrungen != null) {

            List<KryptowaehrungenWaehrungen> waehrungList = new ArrayList(this.waehrungenList);
            List<KryptowaehrungenKaufVerkauf> kaufverkaufList = new ArrayList(this.kaufverkaufListenSQL);
            List<KryptowaehrungenVermoegen> vermoegenList = new ArrayList(this.vermoegenListenSQL);
            List<KryptowaehrungenUeberweisungen> ueberweisungList = new ArrayList(this.ueberweisungenListenSQL);

            boolean kategorieExist = false;

            for (KryptowaehrungenWaehrungen a : waehrungList) {
                if ((a.getWaehrungsname().toLowerCase()).equals(this.deleteKryptowaehrungenWaehrungen.getWaehrungsname().toLowerCase())) {
                    dao.deleteKryptowaehrungenWaehrungen(a);
                    for (KryptowaehrungenKaufVerkauf kkv : kaufverkaufList) {
                        if ((kkv.getAusgangswaehrung().toLowerCase()).equals(this.deleteKryptowaehrungenWaehrungen.getWaehrungsname().toLowerCase())) {

                            this.kaufverkaufListenSQL.remove(kkv);
                            this.filteredKaufverkaufListenSQL.remove(kkv);

                            kkv.setAusgangswaehrung(this.change_waehrung);
                            dao.updateKryptowaehrungenKaufVerkauf(kkv);
                            this.kaufverkaufListenSQL.add(kkv);
                            this.filteredKaufverkaufListenSQL.add(kkv);

                        } else if ((kkv.getEndwaehrung().toLowerCase()).equals(this.deleteKryptowaehrungenWaehrungen.getWaehrungsname().toLowerCase())) {
                            this.kaufverkaufListenSQL.remove(kkv);
                            this.filteredKaufverkaufListenSQL.remove(kkv);

                            kkv.setEndwaehrung(this.change_waehrung);
                            dao.updateKryptowaehrungenKaufVerkauf(kkv);
                            this.kaufverkaufListenSQL.add(kkv);
                            this.filteredKaufverkaufListenSQL.add(kkv);

                        }
                    }

                    for (KryptowaehrungenVermoegen v : vermoegenList) {
                        if ((v.getWaehrung().toLowerCase()).equals(this.deleteKryptowaehrungenWaehrungen.getWaehrungsname().toLowerCase())) {
                            this.vermoegenListenSQL.remove(v);
                            this.filteredVermoegenListenSQL.remove(v);

                            v.setWaehrung(this.change_waehrung);
                            dao.updateKryptowaehrungenVermoegen(v);
                            this.vermoegenListenSQL.add(v);
                            this.filteredVermoegenListenSQL.add(v);

                        }
                    }

                    for (KryptowaehrungenUeberweisungen ueb : ueberweisungList) {
                        if ((ueb.getWaehrung().toLowerCase()).equals(this.deleteKryptowaehrungenWaehrungen.getWaehrungsname().toLowerCase())) {
                            this.ueberweisungenListenSQL.remove(ueb);
                            this.filteredUeberweisungenListenSQL.remove(ueb);

                            ueb.setWaehrung(this.change_waehrung);
                            dao.updateKryptowaehrungenUeberweisungen(ueb);
                            this.ueberweisungenListenSQL.add(ueb);
                            this.filteredUeberweisungenListenSQL.add(ueb);
                        }
                    }
                }
                if ((a.getWaehrungsname().toLowerCase()).equals(this.change_waehrung.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                if (change_waehrung != null && !change_waehrung.equals("") && !change_waehrung.equals(" ")) {
                    KryptowaehrungenWaehrungen neu = new KryptowaehrungenWaehrungen();
                    neu.setWaehrungsname(this.change_waehrung);
                    dao.insertKryptowaehrungenWaehrungen(neu);
                }
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));
            updateData();
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

    public void datensatzLoeschenKaufverkauf() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (KryptowaehrungenKaufVerkauf a : this.kaufverkaufListenSQL) {
                    if (a.getId().equals(Integer.parseInt(this.deleteID))) {
                        this.kaufverkaufListenSQL.remove(a);
                        this.filteredKaufverkaufListenSQL.remove(a);
                        dao.deleteKryptowaehrungenKaufVerkauf(a);
                        this.deletedKaufverkaufListenSQL.add(a);

                        break gefunden;
                    }
                }
                updateDataKaufverkauf();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenKaufverkauf(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (KryptowaehrungenKaufVerkauf a : this.kaufverkaufListenSQL) {
                    if (a.getId().equals(id)) {
                        this.kaufverkaufListenSQL.remove(a);
                        this.filteredKaufverkaufListenSQL.remove(a);
                        dao.deleteKryptowaehrungenKaufVerkauf(a);
                        this.deletedKaufverkaufListenSQL.add(a);

                        break gefunden;
                    }
                }
                updateDataKaufverkauf();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenRueckgangigMachenKaufverkauf() {

        if (!this.deletedKaufverkaufListenSQL.isEmpty()) {
            for (KryptowaehrungenKaufVerkauf a : this.deletedKaufverkaufListenSQL) {
                a.setDeleted(false);
                dao.updateKryptowaehrungenKaufVerkauf(a);
                this.kaufverkaufListenSQL.add(a);
                this.filteredKaufverkaufListenSQL.add(a);

            }
            updateDataKaufverkauf();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    public void datensatzLoeschenVermoegen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (KryptowaehrungenVermoegen a : this.vermoegenListenSQL) {
                    if (a.getId().equals(Integer.parseInt(this.deleteID))) {
                        dao.deleteKryptowaehrungenVermoegen(a);
                        this.deletedVermoegenListenSQL.add(a);
                        this.vermoegenListenSQL.remove(a);
                        this.filteredVermoegenListenSQL.remove(a);

                        break gefunden;
                    }
                }
                updateDataVermoegen();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenVermoegen(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (KryptowaehrungenVermoegen a : this.vermoegenListenSQL) {
                    if (a.getId().equals(id)) {
                        dao.deleteKryptowaehrungenVermoegen(a);
                        this.deletedVermoegenListenSQL.add(a);
                        this.vermoegenListenSQL.remove(a);
                        this.filteredVermoegenListenSQL.remove(a);

                        break gefunden;
                    }
                }
                updateDataVermoegen();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenRueckgangigMachenVermoegen() {

        if (!this.deletedVermoegenListenSQL.isEmpty()) {
            for (KryptowaehrungenVermoegen a : this.deletedVermoegenListenSQL) {
                a.setDeleted(false);
                dao.updateKryptowaehrungenVermoegen(a);
                this.vermoegenListenSQL.add(a);
                this.filteredVermoegenListenSQL.add(a);
            }
            updateDataVermoegen();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    public void datensatzLoeschenUeberweisungen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (KryptowaehrungenUeberweisungen a : this.ueberweisungenListenSQL) {
                    if (a.getId().equals(Integer.parseInt(this.deleteID))) {
                        dao.deleteKryptowaehrungenUeberweisungen(a);
                        this.deletedUeberweisungenListenSQL.add(a);
                        this.ueberweisungenListenSQL.remove(a);
                        this.filteredUeberweisungenListenSQL.remove(a);
                        break gefunden;
                    }
                }
                updateDataUeberweisungen();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenUeberweisungen(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (KryptowaehrungenUeberweisungen a : this.ueberweisungenListenSQL) {
                    if (a.getId().equals(id)) {
                        dao.deleteKryptowaehrungenUeberweisungen(a);
                        this.deletedUeberweisungenListenSQL.add(a);
                        this.ueberweisungenListenSQL.remove(a);
                        this.filteredUeberweisungenListenSQL.remove(a);
                        break gefunden;
                    }
                }
                updateDataUeberweisungen();
                this.deleteID = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenRueckgangigMachenUeberweisungen() {

        if (!this.deletedUeberweisungenListenSQL.isEmpty()) {
            for (KryptowaehrungenUeberweisungen a : this.deletedUeberweisungenListenSQL) {
                a.setDeleted(false);
                dao.updateKryptowaehrungenUeberweisungen(a);
                this.ueberweisungenListenSQL.add(a);
                this.filteredUeberweisungenListenSQL.add(a);
            }
            updateDataUeberweisungen();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }

    }

    //BIETE DIESE IMMER ÜBERPRÜFEN:
    public void scrollTop() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("ueberschriftPanelGlobal");
        PrimeFaces.current().scrollTo("ueberschriftPanelGlobal");

    }

    public void scrollTabelleKaufverkauf() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("listenFormKaufverkauf:tabelleausgabenPanelKaufverkauf");
        PrimeFaces.current().scrollTo("listenFormKaufverkauf:tabelleausgabenPanelKaufverkauf");

    }

    public void scrollTabelleVermoegen() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("listenFormVermoegen:tabelleausgabenPanelVermoegen");
        PrimeFaces.current().scrollTo("listenFormVermoegen:tabelleausgabenPanelVermoegen");

    }

    public void scrollHinzufuegenKaufverkauf() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("hinzufuegenFormKaufverkauf:neuenDatensatzFormularKaufverkauf");
        PrimeFaces.current().scrollTo("hinzufuegenFormKaufverkauf:neuenDatensatzFormularKaufverkauf");

    }

    public void scrollHinzufuegenVermoegen() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("hinzufuegenVermoegen:neuenDatensatzFormularVermoegen");
        PrimeFaces.current().scrollTo("hinzufuegenVermoegen:neuenDatensatzFormularVermoegen");

    }

    public void scrollHinzufuegenUeberweisungen() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("hinzufuegenFormUeberweisungen:neuenDatensatzFormularUeberweisungen");
        PrimeFaces.current().scrollTo("hinzufuegenFormUeberweisungen:neuenDatensatzFormularUeberweisungen");

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

    public void onToggle(ToggleEvent e) {
        this.columnList.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void onToggleVermoegen(ToggleEvent e) {
        this.columnListVermoegen.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public List<Boolean> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Boolean> columnList) {
        this.columnList = columnList;
    }

    public List<KryptowaehrungenExchange> getExchangesList() {
        return exchangesList;
    }

    public void setExchangesList(List<KryptowaehrungenExchange> exchangesList) {
        this.exchangesList = exchangesList;
    }

    public List<KryptowaehrungenVorgang> getVorgaengeList() {
        return vorgaengeList;
    }

    public void setVorgaengeList(List<KryptowaehrungenVorgang> vorgaengeList) {
        this.vorgaengeList = vorgaengeList;
    }

    public List<KryptowaehrungenWaehrungen> getWaehrungenList() {
        return waehrungenList;
    }

    public void setWaehrungenList(List<KryptowaehrungenWaehrungen> waehrungenList) {
        this.waehrungenList = waehrungenList;
    }

    public List<KryptowaehrungenKaufVerkauf> getKaufverkaufListenSQL() {
        return kaufverkaufListenSQL;
    }

    public void setKaufverkaufListenSQL(List<KryptowaehrungenKaufVerkauf> kaufverkaufListenSQL) {
        this.kaufverkaufListenSQL = kaufverkaufListenSQL;
    }

    public List<KryptowaehrungenKaufVerkauf> getFilteredKaufverkaufListenSQL() {
        return filteredKaufverkaufListenSQL;
    }

    public void setFilteredKaufverkaufListenSQL(List<KryptowaehrungenKaufVerkauf> filteredKaufverkaufListenSQL) {
        this.filteredKaufverkaufListenSQL = filteredKaufverkaufListenSQL;
    }

    public List<KryptowaehrungenVermoegen> getVermoegenListenSQL() {
        return vermoegenListenSQL;
    }

    public void setVermoegenListenSQL(List<KryptowaehrungenVermoegen> vermoegenListenSQL) {
        this.vermoegenListenSQL = vermoegenListenSQL;
    }

    public List<KryptowaehrungenVermoegen> getFilteredVermoegenListenSQL() {
        return filteredVermoegenListenSQL;
    }

    public void setFilteredVermoegenListenSQL(List<KryptowaehrungenVermoegen> filteredVermoegenListenSQL) {
        this.filteredVermoegenListenSQL = filteredVermoegenListenSQL;
    }

    public List<KryptowaehrungenUeberweisungen> getUeberweisungenListenSQL() {
        return ueberweisungenListenSQL;
    }

    public void setUeberweisungenListenSQL(List<KryptowaehrungenUeberweisungen> ueberweisungenListenSQL) {
        this.ueberweisungenListenSQL = ueberweisungenListenSQL;
    }

    public List<KryptowaehrungenUeberweisungen> getFilteredUeberweisungenListenSQL() {
        return filteredUeberweisungenListenSQL;
    }

    public void setFilteredUeberweisungenListenSQL(List<KryptowaehrungenUeberweisungen> filteredUeberweisungenListenSQL) {
        this.filteredUeberweisungenListenSQL = filteredUeberweisungenListenSQL;
    }

    public Date getKaufverkaufDatum() {
        return kaufverkaufDatum;
    }

    public void setKaufverkaufDatum(Date kaufverkaufDatum) {
        this.kaufverkaufDatum = kaufverkaufDatum;
    }

    public KryptowaehrungenVorgang getKryptowaehrungVorgangEintrag() {
        return kryptowaehrungVorgangEintrag;
    }

    public void setKryptowaehrungVorgangEintrag(KryptowaehrungenVorgang kryptowaehrungVorgangEintrag) {
        this.kryptowaehrungVorgangEintrag = kryptowaehrungVorgangEintrag;
    }

    public Float getKaufverkaufAusgangsbetrag() {
        return kaufverkaufAusgangsbetrag;
    }

    public void setKaufverkaufAusgangsbetrag(Float kaufverkaufAusgangsbetrag) {
        this.kaufverkaufAusgangsbetrag = kaufverkaufAusgangsbetrag;
    }

    public KryptowaehrungenWaehrungen getKaufverkaufAusgangswaehrungEintrag() {
        return kaufverkaufAusgangswaehrungEintrag;
    }

    public void setKaufverkaufAusgangswaehrungEintrag(KryptowaehrungenWaehrungen kaufverkaufAusgangswaehrungEintrag) {
        this.kaufverkaufAusgangswaehrungEintrag = kaufverkaufAusgangswaehrungEintrag;
    }

    public Float getKaufverkaufEndbetrag() {
        return kaufverkaufEndbetrag;
    }

    public void setKaufverkaufEndbetrag(Float kaufverkaufEndbetrag) {
        this.kaufverkaufEndbetrag = kaufverkaufEndbetrag;
    }

    public KryptowaehrungenWaehrungen getKaufverkaufEndwaehrungEintrag() {
        return kaufverkaufEndwaehrungEintrag;
    }

    public void setKaufverkaufEndwaehrungEintrag(KryptowaehrungenWaehrungen kaufverkaufEndwaehrungEintrag) {
        this.kaufverkaufEndwaehrungEintrag = kaufverkaufEndwaehrungEintrag;
    }

    public KryptowaehrungenExchange getKaufverkaufExchangeEintrag() {
        return kaufverkaufExchangeEintrag;
    }

    public void setKaufverkaufExchangeEintrag(KryptowaehrungenExchange kaufverkaufExchangeEintrag) {
        this.kaufverkaufExchangeEintrag = kaufverkaufExchangeEintrag;
    }

    public String getKaufverkaufBemerkungen() {
        return kaufverkaufBemerkungen;
    }

    public void setKaufverkaufBemerkungen(String kaufverkaufBemerkungen) {
        this.kaufverkaufBemerkungen = kaufverkaufBemerkungen;
    }

    public KryptowaehrungenWaehrungen getVermoegenWaehrungEintrag() {
        return vermoegenWaehrungEintrag;
    }

    public void setVermoegenWaehrungEintrag(KryptowaehrungenWaehrungen vermoegenWaehrungEintrag) {
        this.vermoegenWaehrungEintrag = vermoegenWaehrungEintrag;
    }

    public Float getVermoegenBetrag() {
        return vermoegenBetrag;
    }

    public void setVermoegenBetrag(Float vermoegenBetrag) {
        this.vermoegenBetrag = vermoegenBetrag;
    }

    public String getVermoegenLagerort() {
        return vermoegenLagerort;
    }

    public void setVermoegenLagerort(String vermoegenLagerort) {
        this.vermoegenLagerort = vermoegenLagerort;
    }

    public Float getVermoegenWertineuro() {
        return vermoegenWertineuro;
    }

    public void setVermoegenWertineuro(Float vermoegenWertineuro) {
        this.vermoegenWertineuro = vermoegenWertineuro;
    }

    public String getVermoegenBemerkungen() {
        return vermoegenBemerkungen;
    }

    public void setVermoegenBemerkungen(String vermoegenBemerkungen) {
        this.vermoegenBemerkungen = vermoegenBemerkungen;
    }

    public String getUeberweisungenSender() {
        return ueberweisungenSender;
    }

    public void setUeberweisungenSender(String ueberweisungenSender) {
        this.ueberweisungenSender = ueberweisungenSender;
    }

    public Float getUeberweisungenBetrag() {
        return ueberweisungenBetrag;
    }

    public void setUeberweisungenBetrag(Float ueberweisungenBetrag) {
        this.ueberweisungenBetrag = ueberweisungenBetrag;
    }

    public KryptowaehrungenWaehrungen getUeberweisungenWaehrungEintrag() {
        return ueberweisungenWaehrungEintrag;
    }

    public void setUeberweisungenWaehrungEintrag(KryptowaehrungenWaehrungen ueberweisungenWaehrungEintrag) {
        this.ueberweisungenWaehrungEintrag = ueberweisungenWaehrungEintrag;
    }

    public String getUeberweisungenEmpfaenger() {
        return ueberweisungenEmpfaenger;
    }

    public void setUeberweisungenEmpfaenger(String ueberweisungenEmpfaenger) {
        this.ueberweisungenEmpfaenger = ueberweisungenEmpfaenger;
    }

    public String getUeberweisungenZustand() {
        return ueberweisungenZustand;
    }

    public void setUeberweisungenZustand(String ueberweisungenZustand) {
        this.ueberweisungenZustand = ueberweisungenZustand;
    }

    public String getUeberweisungenBemerkungen() {
        return ueberweisungenBemerkungen;
    }

    public void setUeberweisungenBemerkungen(String ueberweisungenBemerkungen) {
        this.ueberweisungenBemerkungen = ueberweisungenBemerkungen;
    }

    public String getNeuExchange() {
        return neuExchange;
    }

    public void setNeuExchange(String neuExchange) {
        this.neuExchange = neuExchange;
    }

    public String getNeuKryptowaehrungVorgang() {
        return neuKryptowaehrungVorgang;
    }

    public void setNeuKryptowaehrungVorgang(String neuKryptowaehrungVorgang) {
        this.neuKryptowaehrungVorgang = neuKryptowaehrungVorgang;
    }

    public String getNeuWaehrung() {
        return neuWaehrung;
    }

    public void setNeuWaehrung(String neuWaehrung) {
        this.neuWaehrung = neuWaehrung;
    }

    public String getChange_exchange() {
        return change_exchange;
    }

    public void setChange_exchange(String change_exchange) {
        this.change_exchange = change_exchange;
    }

    public String getChange_waehrung() {
        return change_waehrung;
    }

    public void setChange_waehrung(String change_waehrung) {
        this.change_waehrung = change_waehrung;
    }

    public String getChange_vorgang() {
        return change_vorgang;
    }

    public void setChange_vorgang(String change_vorgang) {
        this.change_vorgang = change_vorgang;
    }

    public KryptowaehrungenExchange getDeleteKryptowaehrungenExchange() {
        return deleteKryptowaehrungenExchange;
    }

    public void setDeleteKryptowaehrungenExchange(KryptowaehrungenExchange deleteKryptowaehrungenExchange) {
        this.deleteKryptowaehrungenExchange = deleteKryptowaehrungenExchange;
    }

    public KryptowaehrungenVorgang getDeleteKryptowaehrungVorgang() {
        return deleteKryptowaehrungVorgang;
    }

    public void setDeleteKryptowaehrungVorgang(KryptowaehrungenVorgang deleteKryptowaehrungVorgang) {
        this.deleteKryptowaehrungVorgang = deleteKryptowaehrungVorgang;
    }

    public KryptowaehrungenWaehrungen getDeleteKryptowaehrungenWaehrungen() {
        return deleteKryptowaehrungenWaehrungen;
    }

    public void setDeleteKryptowaehrungenWaehrungen(KryptowaehrungenWaehrungen deleteKryptowaehrungenWaehrungen) {
        this.deleteKryptowaehrungenWaehrungen = deleteKryptowaehrungenWaehrungen;
    }

    public Integer getRownumbers() {
        return rownumbers;
    }

    public void setRownumbers(Integer rownumbers) {
        this.rownumbers = rownumbers;
    }

    public String getAnhangID() {
        return anhangID;
    }

    public void setAnhangID(String anhangID) {
        this.anhangID = anhangID;
    }

    public String getDeleteID() {
        return deleteID;
    }

    public void setDeleteID(String deleteID) {
        this.deleteID = deleteID;
    }

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
    }

    public Double getKaufverkaufWertInEuro() {
        return kaufverkaufWertInEuro;
    }

    public void setKaufverkaufWertInEuro(Double kaufverkaufWertInEuro) {
        this.kaufverkaufWertInEuro = kaufverkaufWertInEuro;
    }

    public Double getGesamtwertEuro() {
        return gesamtwertEuro;
    }

    public void setGesamtwertEuro(Double gesamtwertEuro) {
        this.gesamtwertEuro = gesamtwertEuro;
    }

    public LineChartModel getChartVermoegenWertineuro() {
        return chartVermoegenWertineuro;
    }

    public void setChartVermoegenWertineuro(LineChartModel chartVermoegenWertineuro) {
        this.chartVermoegenWertineuro = chartVermoegenWertineuro;
    }

    public Date getUeberweisungenDatum() {
        return ueberweisungenDatum;
    }

    public void setUeberweisungenDatum(Date ueberweisungenDatum) {
        this.ueberweisungenDatum = ueberweisungenDatum;
    }

    public String getNotiztext() {
        return notiztext;
    }

    public void setNotiztext(String notiztext) {
        this.notiztext = notiztext;
    }

    public DonutChartModel getChartPortfolioWertineuro() {
        return chartPortfolioWertineuro;
    }

    public void setChartPortfolioWertineuro(DonutChartModel chartPortfolioWertineuro) {
        this.chartPortfolioWertineuro = chartPortfolioWertineuro;
    }

    public List<KryptowaehrungWaehrungWertGruppe> getZuwachsListe() {
        return zuwachsListe;
    }

    public void setZuwachsListe(List<KryptowaehrungWaehrungWertGruppe> zuwachsListe) {
        this.zuwachsListe = zuwachsListe;
    }

    public List<KryptowaehrungWaehrungWertGruppe> getFilteredZuwachsListe() {
        return filteredZuwachsListe;
    }

    public void setFilteredZuwachsListe(List<KryptowaehrungWaehrungWertGruppe> filteredZuwachsListe) {
        this.filteredZuwachsListe = filteredZuwachsListe;
    }

    public DatenbankNotizen getDbnotizEintrag() {
        return dbnotizEintrag;
    }

    public void setDbnotizEintrag(DatenbankNotizen dbnotizEintrag) {
        this.dbnotizEintrag = dbnotizEintrag;
    }

    public String getDatensaetzeAnzahlTextKaufverkauf() {
        return datensaetzeAnzahlTextKaufverkauf;
    }

    public void setDatensaetzeAnzahlTextKaufverkauf(String datensaetzeAnzahlTextKaufverkauf) {
        this.datensaetzeAnzahlTextKaufverkauf = datensaetzeAnzahlTextKaufverkauf;
    }

    public String getDatensaetzeAnzahlTextUeberweisungen() {
        return datensaetzeAnzahlTextUeberweisungen;
    }

    public void setDatensaetzeAnzahlTextUeberweisungen(String datensaetzeAnzahlTextUeberweisungen) {
        this.datensaetzeAnzahlTextUeberweisungen = datensaetzeAnzahlTextUeberweisungen;
    }

    public String getDatensaetzeAnzahlTextVermoegen() {
        return datensaetzeAnzahlTextVermoegen;
    }

    public void setDatensaetzeAnzahlTextVermoegen(String datensaetzeAnzahlTextVermoegen) {
        this.datensaetzeAnzahlTextVermoegen = datensaetzeAnzahlTextVermoegen;
    }

    public List<Boolean> getColumnListVermoegen() {
        return columnListVermoegen;
    }

    public void setColumnListVermoegen(List<Boolean> columnListVermoegen) {
        this.columnListVermoegen = columnListVermoegen;
    }

    public String getDatumNotiztext() {
        return datumNotiztext;
    }

    public void setDatumNotiztext(String datumNotiztext) {
        this.datumNotiztext = datumNotiztext;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.chartVermoegenWertineuro);
        hash = 43 * hash + Objects.hashCode(this.chartPortfolioWertineuro);
        hash = 43 * hash + Objects.hashCode(this.zuwachsListe);
        hash = 43 * hash + Objects.hashCode(this.filteredZuwachsListe);
        hash = 43 * hash + Objects.hashCode(this.exchangesList);
        hash = 43 * hash + Objects.hashCode(this.vorgaengeList);
        hash = 43 * hash + Objects.hashCode(this.waehrungenList);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufListenSQL);
        hash = 43 * hash + Objects.hashCode(this.filteredKaufverkaufListenSQL);
        hash = 43 * hash + Objects.hashCode(this.vermoegenListenSQL);
        hash = 43 * hash + Objects.hashCode(this.filteredVermoegenListenSQL);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenListenSQL);
        hash = 43 * hash + Objects.hashCode(this.filteredUeberweisungenListenSQL);
        hash = 43 * hash + Objects.hashCode(this.baseUrlKaufverkauf);
        hash = 43 * hash + Objects.hashCode(this.downloadUrlKaufverkauf);
        hash = 43 * hash + Objects.hashCode(this.baseUrlVermoegen);
        hash = 43 * hash + Objects.hashCode(this.downloadUrlVermoegen);
        hash = 43 * hash + Objects.hashCode(this.baseUrlTransfer);
        hash = 43 * hash + Objects.hashCode(this.downloadUrlTransfer);
        hash = 43 * hash + Objects.hashCode(this.notiztext);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufDatum);
        hash = 43 * hash + Objects.hashCode(this.kryptowaehrungVorgangEintrag);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufAusgangsbetrag);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufAusgangswaehrungEintrag);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufEndbetrag);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufEndwaehrungEintrag);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufWertInEuro);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufExchangeEintrag);
        hash = 43 * hash + Objects.hashCode(this.kaufverkaufBemerkungen);
        hash = 43 * hash + Objects.hashCode(this.vermoegenWaehrungEintrag);
        hash = 43 * hash + Objects.hashCode(this.vermoegenBetrag);
        hash = 43 * hash + Objects.hashCode(this.vermoegenLagerort);
        hash = 43 * hash + Objects.hashCode(this.vermoegenWertineuro);
        hash = 43 * hash + Objects.hashCode(this.vermoegenBemerkungen);
        hash = 43 * hash + Objects.hashCode(this.gesamtwertEuro);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenDatum);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenSender);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenBetrag);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenWaehrungEintrag);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenEmpfaenger);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenZustand);
        hash = 43 * hash + Objects.hashCode(this.ueberweisungenBemerkungen);
        hash = 43 * hash + Objects.hashCode(this.dao);
        hash = 43 * hash + Objects.hashCode(this.neuExchange);
        hash = 43 * hash + Objects.hashCode(this.neuKryptowaehrungVorgang);
        hash = 43 * hash + Objects.hashCode(this.neuWaehrung);
        hash = 43 * hash + Objects.hashCode(this.change_exchange);
        hash = 43 * hash + Objects.hashCode(this.change_waehrung);
        hash = 43 * hash + Objects.hashCode(this.change_vorgang);
        hash = 43 * hash + Objects.hashCode(this.deleteKryptowaehrungenExchange);
        hash = 43 * hash + Objects.hashCode(this.deleteKryptowaehrungVorgang);
        hash = 43 * hash + Objects.hashCode(this.deleteKryptowaehrungenWaehrungen);
        hash = 43 * hash + Objects.hashCode(this.rownumbers);
        hash = 43 * hash + Objects.hashCode(this.anhangID);
        hash = 43 * hash + Objects.hashCode(this.deleteID);
        hash = 43 * hash + Objects.hashCode(this.anhangname);
        hash = 43 * hash + Objects.hashCode(this.anhangtype);
        hash = 43 * hash + Arrays.hashCode(this.anhang);
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
        final KryptowaehrungenController other = (KryptowaehrungenController) obj;
        if (!Objects.equals(this.baseUrlKaufverkauf, other.baseUrlKaufverkauf)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrlKaufverkauf, other.downloadUrlKaufverkauf)) {
            return false;
        }
        if (!Objects.equals(this.baseUrlVermoegen, other.baseUrlVermoegen)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrlVermoegen, other.downloadUrlVermoegen)) {
            return false;
        }
        if (!Objects.equals(this.baseUrlTransfer, other.baseUrlTransfer)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrlTransfer, other.downloadUrlTransfer)) {
            return false;
        }
        if (!Objects.equals(this.notiztext, other.notiztext)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufBemerkungen, other.kaufverkaufBemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.vermoegenLagerort, other.vermoegenLagerort)) {
            return false;
        }
        if (!Objects.equals(this.vermoegenBemerkungen, other.vermoegenBemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenSender, other.ueberweisungenSender)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenEmpfaenger, other.ueberweisungenEmpfaenger)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenZustand, other.ueberweisungenZustand)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenBemerkungen, other.ueberweisungenBemerkungen)) {
            return false;
        }
        if (!Objects.equals(this.neuExchange, other.neuExchange)) {
            return false;
        }
        if (!Objects.equals(this.neuKryptowaehrungVorgang, other.neuKryptowaehrungVorgang)) {
            return false;
        }
        if (!Objects.equals(this.neuWaehrung, other.neuWaehrung)) {
            return false;
        }
        if (!Objects.equals(this.change_exchange, other.change_exchange)) {
            return false;
        }
        if (!Objects.equals(this.change_waehrung, other.change_waehrung)) {
            return false;
        }
        if (!Objects.equals(this.change_vorgang, other.change_vorgang)) {
            return false;
        }
        if (!Objects.equals(this.anhangID, other.anhangID)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Objects.equals(this.chartVermoegenWertineuro, other.chartVermoegenWertineuro)) {
            return false;
        }
        if (!Objects.equals(this.chartPortfolioWertineuro, other.chartPortfolioWertineuro)) {
            return false;
        }
        if (!Objects.equals(this.zuwachsListe, other.zuwachsListe)) {
            return false;
        }
        if (!Objects.equals(this.filteredZuwachsListe, other.filteredZuwachsListe)) {
            return false;
        }
        if (!Objects.equals(this.exchangesList, other.exchangesList)) {
            return false;
        }
        if (!Objects.equals(this.vorgaengeList, other.vorgaengeList)) {
            return false;
        }
        if (!Objects.equals(this.waehrungenList, other.waehrungenList)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufListenSQL, other.kaufverkaufListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredKaufverkaufListenSQL, other.filteredKaufverkaufListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.vermoegenListenSQL, other.vermoegenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredVermoegenListenSQL, other.filteredVermoegenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenListenSQL, other.ueberweisungenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.filteredUeberweisungenListenSQL, other.filteredUeberweisungenListenSQL)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufDatum, other.kaufverkaufDatum)) {
            return false;
        }
        if (!Objects.equals(this.kryptowaehrungVorgangEintrag, other.kryptowaehrungVorgangEintrag)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufAusgangsbetrag, other.kaufverkaufAusgangsbetrag)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufAusgangswaehrungEintrag, other.kaufverkaufAusgangswaehrungEintrag)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufEndbetrag, other.kaufverkaufEndbetrag)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufEndwaehrungEintrag, other.kaufverkaufEndwaehrungEintrag)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufWertInEuro, other.kaufverkaufWertInEuro)) {
            return false;
        }
        if (!Objects.equals(this.kaufverkaufExchangeEintrag, other.kaufverkaufExchangeEintrag)) {
            return false;
        }
        if (!Objects.equals(this.vermoegenWaehrungEintrag, other.vermoegenWaehrungEintrag)) {
            return false;
        }
        if (!Objects.equals(this.vermoegenBetrag, other.vermoegenBetrag)) {
            return false;
        }
        if (!Objects.equals(this.vermoegenWertineuro, other.vermoegenWertineuro)) {
            return false;
        }
        if (!Objects.equals(this.gesamtwertEuro, other.gesamtwertEuro)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenDatum, other.ueberweisungenDatum)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenBetrag, other.ueberweisungenBetrag)) {
            return false;
        }
        if (!Objects.equals(this.ueberweisungenWaehrungEintrag, other.ueberweisungenWaehrungEintrag)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Objects.equals(this.deleteKryptowaehrungenExchange, other.deleteKryptowaehrungenExchange)) {
            return false;
        }
        if (!Objects.equals(this.deleteKryptowaehrungVorgang, other.deleteKryptowaehrungVorgang)) {
            return false;
        }
        if (!Objects.equals(this.deleteKryptowaehrungenWaehrungen, other.deleteKryptowaehrungenWaehrungen)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        return true;
    }

}
