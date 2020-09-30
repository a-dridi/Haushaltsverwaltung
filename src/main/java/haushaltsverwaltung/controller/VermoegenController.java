/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.Vermoegen;
import haushaltsverwaltung.model.VermoegenJaehrlich;

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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
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
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 *
 * Financial assets (monthly) - monatliches Vermögen
 *
 * @author A.Dridi
 */
@Named(value = "vermoegenController")
@ViewScoped
public class VermoegenController implements Serializable {

    private LineChartModel vermoegenWachstumMonatlich;
    private LineChartModel vermoegenWachstumMonatlichProzent;
    private LineChartModel vermoegenWachstumJaehrlich;

    private String tabellenname = "Vermoegen";
    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Vermoegen";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Vermoegen";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    private String deleteID;
    private String anhangID;
    private byte[] anhang;
    private DAO dao;
    private Integer rownumbers = 15;
    private Integer insert_rownumber;
    private String anhangname;
    private String anhangtype;

    private Double einnahmen;
    private Double ausgaben;
    private Date datum;
    private String bemerkungen;
    private String notiztext;
    private String datensaetzeAnzahlText;

    private List<Vermoegen> vermoegenList = new ArrayList<>();
    private List<Vermoegen> filteredVermoegenList = new ArrayList<>();
    private List<VermoegenJaehrlich> jaehrlicheVermoegenList = new ArrayList<>();
    private List<VermoegenJaehrlich> filteredJaehrlicheVermoegenList = new ArrayList<>();
    private DatenbankNotizen dbnotizEintrag = null;

    private Double jaehrlicheProzentZuwachs;
    private Double gesamtsummeZuwachs;
    private Double jaehrlichesummeZuwachs;
    private List<Vermoegen> deletedVermoegenList = new ArrayList<>();

    /**
     * Creates a new instance of VermoegenController
     */
    public VermoegenController() {
        this.dao = new DAO();
    }

    @PostConstruct
    private void init() {

        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
        }
        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        if (urlName.contains("vermoegen_grafik_monatlich.xhtml")) {
            createVermoegenZuwachsMonatlichChart();
            createVermoegenZuwachsMonatlichProzentChart();
            flushAnhang();

        } else { //Tabelle Einnahmen
            this.vermoegenList = dao.getAllVermoegen();
            this.filteredVermoegenList = new ArrayList<>(this.vermoegenList);
            this.jaehrlicheVermoegenList = dao.getAllVermoegenJaehrlich();
            this.filteredJaehrlicheVermoegenList = new ArrayList<>(this.filteredJaehrlicheVermoegenList);

            flushAnhang();
            calculateGesamtsummeZuwachs();
            calculateJaehrlichesummeZuwachs();
            calculateJaehrlicheProzentZuwachs();

            this.datensaetzeAnzahlText = ("Insgesamt: " + this.vermoegenList.size() + " Datensaetze in der DB gespeichert");
        }

    }

    public void calculateJaehrlichesummeZuwachs() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String jahr = sdf.format(d);
        String sqlstring = "Select sum(differenz) FROM Vermoegen where deleted=false and EXTRACT(year FROM datum) = :datumWert";
        Query qu = s.createQuery(sqlstring);
        qu.setInteger("datumWert", Integer.parseInt(jahr));
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.jaehrlichesummeZuwachs = waehrunggruppe.get(0);
            s.close();

        } else {
            s.close();
            this.jaehrlichesummeZuwachs = 0.0;
        }

    }

    public void calculateGesamtsummeZuwachs() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(differenz) FROM Vermoegen where deleted=false";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe != null && waehrunggruppe.get(0) != null) {
            this.gesamtsummeZuwachs = waehrunggruppe.get(0);
            s.close();

        } else {
            s.close();
            this.gesamtsummeZuwachs = 0.0;
        }

    }

    public void calculateJaehrlicheProzentZuwachs() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String jahr = sdf.format(d);
        this.jaehrlicheProzentZuwachs = 0.0;
        String sqlstring = "Select sum(prozentZuwachs) FROM Vermoegen where deleted=false and EXTRACT(year FROM datum)  = :datumWert";
        Query qu = s.createQuery(sqlstring);
        qu.setInteger("datumWert", Integer.parseInt(jahr));
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe != null && waehrunggruppe.get(0) != null) {
            this.jaehrlicheProzentZuwachs += waehrunggruppe.get(0);
            s.close();

        } else {
            s.close();
            this.jaehrlicheProzentZuwachs = 0.0;
        }

    }

    public void calculateCustomJaehrlicheProzentZuwachs(Integer jahr) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Date d = new Date();
        this.jaehrlicheProzentZuwachs = 0.0;
        String sqlstring = "Select sum(prozentZuwachs) FROM Vermoegen where deleted=false and EXTRACT(year FROM datum)  = :datumWert";
        Query qu = s.createQuery(sqlstring);
        qu.setInteger("datumWert", jahr);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.jaehrlicheProzentZuwachs += waehrunggruppe.get(0);
            s.close();
        } else {
            s.close();
            this.jaehrlicheProzentZuwachs = 0.0;
        }

    }

    /**
     * Berechnet und speichert jaehrliche Wachstumswerte und schreib die in die
     * DB-Tabelle VermoegenJaehrlich
     */
    public void jaehrlicheWachstumwerteBerechnen() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select datum FROM Vermoegen where deleted=false order by datum asc";
        Query qu = s.createQuery(sqlstring);
        List<Date> datumListe = qu.list();

        Set<Integer> jahreList = new TreeSet<>();
        for (Date d : datumListe) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

            Integer jahrWert = Integer.parseInt(sdf.format(d));
            jahreList.add(jahrWert);
        }
        //Wachstumbeträge abfragen und dann pro Jahr in Tabelle VermoegenJaehrlich speichern
        sqlstring = "Select sum(differenz) FROM Vermoegen where deleted=false group by datum order by datum asc";
        qu = s.createQuery(sqlstring);
        List<Double> wachstumListe = qu.list();
        int datumZaehler = 0;
        for (Integer jahr : jahreList) {
            VermoegenJaehrlich vj = new VermoegenJaehrlich();
            vj.setJahr(jahr);
            vj.setDifferenz(wachstumListe.get(datumZaehler).doubleValue());
            if (vj.getDifferenz() >= 0) {
                vj.setIsGewinn(true);
                vj.setIsVerlust(false);
            } else {
                vj.setIsGewinn(false);
                vj.setIsVerlust(true);
            }
            this.dao.insertVermoegenJaehrlich(vj);
            datumZaehler++;
        }
    }

    /**
     * Erstellt ein Liniendiagramm mit dem monatlichen Geldwachstum (Differenz)
     * (Summe v. Spalte Differenz) abbildet.
     */
    public void createVermoegenZuwachsMonatlichChart() {
        this.vermoegenWachstumMonatlich = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();

        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        //SQL Abruf
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select datum,differenz FROM Vermoegen where deleted=false order by datum asc";
        Query qu = s.createQuery(sqlstring);
        List<Object[]> vermoegenwachstumListe = qu.list();
        //Kurve erstellen für Vermögen

        //Alle Datumeinträge hinzufügen
        DateFormat df = new SimpleDateFormat("MM.yyyy");
        for (Object[] o : vermoegenwachstumListe) {
            labels.add(df.format((Date) o[0]));
            values.add((Number) o[1]);
            // this.gesamtwertEuro += ((Double) o[1]);
            //Speichern von Werten für Zuwachsberechnung (zwischen Alt und Neu Wert
        }

        dataSet.setData(values);
        dataSet.setLabel("Vermögenswachstum");
        dataSet.setYaxisID("left-y-axis");
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        this.vermoegenWachstumMonatlich.setData(data);

        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setId("left-y-axis");
        linearAxes.setPosition("left");

        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Vermögenswachstum (Gewinn) in EURO pro Monat");
        options.setTitle(title);

        this.vermoegenWachstumMonatlich.setOptions(options);

        s.close();
    }

    /**
     * Werteangabe in Prozent: Erstellt ein Liniendiagramm mit dem monatlichen
     * Geldwachstum (Differenz) (Summe v. Spalte Differenz) abbildet.
     */
    public void createVermoegenZuwachsMonatlichProzentChart() {
        this.vermoegenWachstumMonatlichProzent = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        //SQL Abruf
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select datum,prozentZuwachs FROM Vermoegen where deleted=false order by datum asc";
        Query qu = s.createQuery(sqlstring);
        List<Object[]> vermoegenwachstumListe = qu.list();
        //Kurve erstellen für Vermögen
        //Alle Datumeinträge hinzufügen
        DateFormat df = new SimpleDateFormat("MM.yyyy");
        for (Object[] o : vermoegenwachstumListe) {
            labels.add(df.format((Date) o[0]));
            values.add((Number) o[1]);
            // this.gesamtwertEuro += ((Double) o[1]);
            //Speichern von Werten für Zuwachsberechnung (zwischen Alt und Neu Wert
        }
        data.setLabels(labels);

        //Options
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setId("left-y-axis");
        linearAxes.setPosition("left");

        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Vermögenswachstum (Gewinn) in Prozent pro Monat");
        options.setTitle(title);

        this.vermoegenWachstumMonatlichProzent.setOptions(options);

        s.close();
    }

    /**
     * Anhang bearbeiten: Aber bei Übergabe eines leeren Anhangs wird der Anhang
     * für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<Vermoegen> liste = new ArrayList<>(this.vermoegenList);
            gefunden:
            for (Vermoegen a : liste) {
                if (a.getVermoegen_id().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getVermoegen_id()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getVermoegen_id()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getVermoegen_id()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateVermoegen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getVermoegen_id() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getVermoegen_id()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateVermoegen(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getVermoegen_id() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
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
            int zeile = (Integer) tabelle.getRowKey();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            Vermoegen a = (this.dao.getSingleVermoegen((Integer) tabelle.getRowKey())).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Datum")) {
                a.setDatum((Date) event.getNewValue());
            }

            if (spaltenname.equals("Ausgaben")) {
                a.setAusgaben((Double) event.getNewValue());
                Double differenzWert = a.getEinnahmen() - a.getAusgaben();
                a.setDifferenz(differenzWert);
                if (differenzWert >= 0) {
                    a.setIsGewinn(true);
                    a.setIsVerlust(false);
                } else {
                    a.setIsGewinn(false);
                    a.setIsVerlust(true);
                }
                if (zeile > 0) {
                    double vorherDiffwert = this.vermoegenList.get(zeile - 1).getDifferenz();
                    double jetztDiffwert = a.getDifferenz();
                    double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                    a.setProzentZuwachs(przZuwachs);
                }
                // monatliche Vermoegen (Ausgaben/Einnahmen) welches als jaehrliches Vermögen (Ausgaben/Einnahmen) zusammengefasst wurde bearbeiten.
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                Integer aktuellesJahr = Integer.parseInt(sdf.format(this.vermoegenList.get(zeile)));
                Integer selectedIndex = 0; //Die Durchläufe in der Jahresliste (JaehrlichesVermoegen)
                gefunden:
                for (VermoegenJaehrlich vj : this.jaehrlicheVermoegenList) {
                    if (vj.getJahr().equals(aktuellesJahr)) { //Wenn das übergebene Jahr dem übergebenen Jahr entspricht
                        //Ausgaben in der jährlichen Zusammenfassung anpassen
                        vj.setAusgaben(((vj.getAusgaben()) - (Double) event.getOldValue()));
                        vj.setAusgaben(((vj.getAusgaben()) + (Double) event.getNewValue()));
                        vj.setDifferenz(vj.getEinnahmen() - vj.getAusgaben());
                        if (this.jaehrlicheVermoegenList.size() > 1) {
                            if (selectedIndex > 0) {
                                double vorherDiffwert = this.jaehrlicheVermoegenList.get(selectedIndex - 1).getDifferenz();
                                double jetztDiffwert = this.jaehrlicheVermoegenList.get(selectedIndex).getDifferenz();
                                double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                                vj.setProzentZuwachs(przZuwachs);
                            }
                        }
                        dao.updateVermoegenJaehrlich(vj);

                        break gefunden;
                    }
                    selectedIndex++;
                }
                // BIS HIER
            }
            if (spaltenname.equals("Einnahmen")) {
                a.setEinnahmen((Double) event.getNewValue());
                Double differenzWert = a.getEinnahmen() - a.getAusgaben();
                a.setDifferenz(differenzWert);
                if (differenzWert >= 0) {
                    a.setIsGewinn(true);
                    a.setIsVerlust(false);
                } else {
                    a.setIsGewinn(false);
                    a.setIsVerlust(true);
                }
                if (zeile > 0) {
                    double vorherDiffwert = this.vermoegenList.get(zeile - 1).getDifferenz();
                    double jetztDiffwert = a.getDifferenz();
                    double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                    a.setProzentZuwachs(przZuwachs);

                }

                // jaehrliches Vermögen
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                Integer aktuellesJahr = Integer.parseInt(sdf.format(this.vermoegenList.get(zeile)));
                Integer selectedIndex = 0; //Die Durchläufe in der Jahresliste (JaehrlichesVermoegen)
                gefunden:
                for (VermoegenJaehrlich vj : this.jaehrlicheVermoegenList) {
                    if (vj.getJahr().equals(aktuellesJahr)) {//Wenn das übergebene Jahr dem übergebenen Jahr entspricht
                        //Einnahmen in der jährlichen Zusammenfassung anpassen

                        vj.setEinnahmen(((vj.getEinnahmen()) - (Double) event.getOldValue()));
                        vj.setEinnahmen(((vj.getEinnahmen()) + (Double) event.getNewValue()));
                        vj.setDifferenz(vj.getEinnahmen() - vj.getAusgaben());

                        if (this.jaehrlicheVermoegenList.size() > 1) {
                            if (selectedIndex > 0) {
                                double vorherDiffwert = this.jaehrlicheVermoegenList.get(selectedIndex - 1).getDifferenz();
                                double jetztDiffwert = this.jaehrlicheVermoegenList.get(selectedIndex).getDifferenz();
                                double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                                vj.setProzentZuwachs(przZuwachs);
                            }
                        }
                        dao.updateVermoegenJaehrlich(vj);

                        break gefunden;
                    }
                    selectedIndex++;
                }
                // BIS HIER
            }
            if (spaltenname.equals("Bemerkungen")) {
                a.setBemerkungen((String) event.getNewValue());
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateVermoegen(a);
            updateData();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

            //DEBUG:
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void updateData() {
        this.dao = new DAO();
        this.vermoegenList = dao.getAllVermoegen();
        this.filteredVermoegenList = new ArrayList<>(this.vermoegenList);
        this.jaehrlicheVermoegenList = dao.getAllVermoegenJaehrlich();
        this.filteredJaehrlicheVermoegenList = new ArrayList<>(this.filteredJaehrlicheVermoegenList);

        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        flushAnhang();
        calculateGesamtsummeZuwachs();
        calculateJaehrlichesummeZuwachs();
        calculateJaehrlicheProzentZuwachs();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.vermoegenList.size() + " Datensaetze in der DB gespeichert");
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

    /**
     * NIMMT DAS AKTUELLE JAHR AUF (VOM SERVER) für die jährliche Berechnung.
     * Monatliches Vermögen in jährliches Vermögen speichern Wird in speichern()
     * ausgeführt und verwendetet vom Benutzer eingegebene Werte
     */
    public void alsJaehrlichesWachstumSpeichern() {
        Session s = HibernateUtil.getSessionFactory().openSession();

        //Gespeicherte Jahre holen
        String sqlstring = "Select jahr FROM VermoegenJaehrlich order by jahr asc";
        Query qu = s.createQuery(sqlstring);
        List<Integer> jahrListe = qu.list();
        Integer aktuellesJahr;
        //Jahr - Werte aktualisieren oder neues Jahr + Jahr - Werte hinzufügen
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        if (this.datum != null) {
            aktuellesJahr = Integer.parseInt(sdf.format(this.datum));
        } else { //Das heutige Datum - wenn nichts angegeben
            aktuellesJahr = Integer.parseInt(sdf.format(new Date()));

        }
        boolean wurdeGefunden = false;
        gefunden:
        for (VermoegenJaehrlich vj : this.jaehrlicheVermoegenList) {
            if (vj.getJahr().equals(aktuellesJahr)) {
                vj.setEinnahmen(((vj.getEinnahmen()) + this.einnahmen));
                vj.setAusgaben(((vj.getAusgaben()) + this.ausgaben));
                vj.setDifferenz((vj.getEinnahmen() - vj.getAusgaben()));

                if (this.jaehrlicheVermoegenList.size() > 1) {
                    int lastIndex = this.jaehrlicheVermoegenList.size() - 1;
                    double vorherDiffwert = this.jaehrlicheVermoegenList.get(lastIndex - 1).getDifferenz();
                    double jetztDiffwert = this.jaehrlicheVermoegenList.get(lastIndex).getDifferenz();
                    double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                    vj.setProzentZuwachs(przZuwachs);
                } else {
                    vj.setProzentZuwachs(0.0);

                }
                dao.updateVermoegenJaehrlich(vj);
                wurdeGefunden = true;
                break gefunden;
            }
        }

        //Wenn Jahr in VermoegenJaehrlich nicht existiert, neues Jahr erstellen
        if (!wurdeGefunden) {
            sqlstring = "Select datum FROM Vermoegen order by datum asc";
            qu = s.createQuery(sqlstring);
            List<Date> datumListe = qu.list();

            Set<Integer> jahreList = new TreeSet<>();
            Integer jahrWert = Integer.parseInt(sdf.format(this.datum));
            jahreList.add(jahrWert);

            //Wachstumbeträge abfragen und dann pro Jahr in Tabelle VermoegenJaehrlich speichern
            int datumZaehler = 0;
            for (Integer jahr : jahreList) {
                sqlstring = "Select sum(ausgaben),sum(einnahmen),sum(differenz) FROM Vermoegen where EXTRACT(year FROM datum)  = " + jahr + " group by datum order by datum asc ";
                qu = s.createQuery(sqlstring);
                List<Object[]> wachstumListe = qu.list();
                VermoegenJaehrlich vj = new VermoegenJaehrlich();

                double ausgabenWert = 0;
                double einnahmenWert = 0;
                double differenzWert = 0;

                for (Object o : wachstumListe) {
                    ausgabenWert += ((Double) wachstumListe.get(datumZaehler)[0]).doubleValue();
                    einnahmenWert += (((Double) wachstumListe.get(datumZaehler)[1]).doubleValue());
                    differenzWert += (((Double) wachstumListe.get(datumZaehler)[2]).doubleValue());
                    datumZaehler++;
                }
                vj.setJahr(jahr);
                vj.setAusgaben(ausgabenWert);
                vj.setEinnahmen(einnahmenWert);
                vj.setDifferenz(differenzWert);
                if (vj.getDifferenz() >= 0) {
                    vj.setIsGewinn(true);
                    vj.setIsVerlust(false);
                } else {
                    vj.setIsGewinn(false);
                    vj.setIsVerlust(true);
                }
                //Wachstum in % berechnen
                if (this.jaehrlicheVermoegenList.size() > 0) {
                    int lastIndex = this.jaehrlicheVermoegenList.size() - 1;
                    double vorherDiffwert = this.jaehrlicheVermoegenList.get(lastIndex).getDifferenz();
                    double jetztDiffwert = vj.getDifferenz();
                    double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                    vj.setProzentZuwachs(przZuwachs);
                } else {
                    vj.setProzentZuwachs(0.0);
                }

                this.dao.insertVermoegenJaehrlich(vj);
            }
        }
    }

    public void speichern() {
        Vermoegen vermoegen = new Vermoegen();
        vermoegen.setDeleted(false);
        if (this.bemerkungen != null) {
            vermoegen.setBemerkungen(bemerkungen);
        }

        if (this.ausgaben != null && this.einnahmen != null) {
            vermoegen.setAusgaben(this.ausgaben);
            vermoegen.setEinnahmen(this.einnahmen);
            Double differenzWert = vermoegen.getEinnahmen() - vermoegen.getAusgaben();
            vermoegen.setDifferenz(differenzWert);
            if (differenzWert >= 0) {
                vermoegen.setIsGewinn(true);
                vermoegen.setIsVerlust(false);
            } else {
                vermoegen.setIsGewinn(false);
                vermoegen.setIsVerlust(true);
            }

            if (this.datum != null) {
                vermoegen.setDatum(datum);
            } else {
                vermoegen.setDatum(new Date());
            }

            List<Vermoegen> neueListe = dao.getAllVermoegen();

            if (neueListe.size() > 0) {
                double przZuwachs;
                int lastIndex = neueListe.size() - 1;
                double vorherDiffwert = neueListe.get(lastIndex).getDifferenz();
                double jetztDiffwert = vermoegen.getDifferenz();
                if ((jetztDiffwert - vorherDiffwert) == 0) {
                    przZuwachs = 0.0;
                } else {
                    przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                }
                vermoegen.setProzentZuwachs(przZuwachs);
            } else {
                vermoegen.setProzentZuwachs(0.0);

            }
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            vermoegen.setAnhang(true);
            this.vermoegenList.add(vermoegen);
            this.filteredVermoegenList.add(vermoegen);

            dao.insertVermoegen(vermoegen);

            List<Vermoegen> vermoegenListe = new ArrayList<>(this.vermoegenList);
            int letzteNr = vermoegenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = vermoegenListe.get(letzteNr).getVermoegen_id();
                try {
                    Vermoegen a = vermoegenListe.get(letzteNr);
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
                    dao.updateVermoegen(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }
                alsJaehrlichesWachstumSpeichern();
                updateData();
            }

        } else {
            vermoegen.setAnhang(false);
            this.vermoegenList.add(vermoegen);
            this.filteredVermoegenList.add(vermoegen);

            dao.insertVermoegen(vermoegen);
            alsJaehrlichesWachstumSpeichern();
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

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (Vermoegen a : this.vermoegenList) {
                    if (a.getVermoegen_id().equals(Integer.parseInt(this.deleteID))) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                        int gewaehltesJahr = Integer.parseInt(sdf.format(a.getDatum()));
                        int jahrZaehler = 0;
                        jaehrlichGefunden:
                        for (VermoegenJaehrlich vj : this.jaehrlicheVermoegenList) {
                            if (vj.getJahr().equals(gewaehltesJahr)) {
                                vj.setAusgaben((vj.getAusgaben() - a.getAusgaben()));
                                vj.setEinnahmen((vj.getEinnahmen() - a.getEinnahmen()));
                                vj.setDifferenz(vj.getEinnahmen() - vj.getAusgaben());
                                if (jahrZaehler > 0) {
                                    double vorherDiffwert = this.jaehrlicheVermoegenList.get(jahrZaehler - 1).getDifferenz();
                                    double jetztDiffwert = this.jaehrlicheVermoegenList.get(jahrZaehler).getDifferenz();
                                    double przZuwachs = Double.parseDouble(String.format("%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));

                                    vj.setProzentZuwachs(przZuwachs);
                                }
                                dao.updateVermoegenJaehrlich(vj);
                                break jaehrlichGefunden;
                            }
                            jahrZaehler++;
                        }
                        this.deletedVermoegenList.add(a);
                        this.vermoegenList.remove(a);
                        this.filteredVermoegenList.remove(a);
                        dao.deleteVermoegen(a);
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
                for (Vermoegen a : this.vermoegenList) {
                    if (a.getVermoegen_id().equals(id)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                        int gewaehltesJahr = Integer.parseInt(sdf.format(a.getDatum()));
                        int jahrZaehler = 0;
                        jaehrlichGefunden:
                        for (VermoegenJaehrlich vj : this.jaehrlicheVermoegenList) {
                            if (vj.getJahr().equals(gewaehltesJahr)) {
                                vj.setAusgaben((vj.getAusgaben() - a.getAusgaben()));
                                vj.setEinnahmen((vj.getEinnahmen() - a.getEinnahmen()));
                                vj.setDifferenz(vj.getEinnahmen() - vj.getAusgaben());
                                if (jahrZaehler > 0) {
                                    double vorherDiffwert = this.jaehrlicheVermoegenList.get(jahrZaehler - 1).getDifferenz();
                                    double jetztDiffwert = this.jaehrlicheVermoegenList.get(jahrZaehler).getDifferenz();
                                    double przZuwachs = Double.parseDouble(String.format("%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));

                                    vj.setProzentZuwachs(przZuwachs);
                                }
                                dao.updateVermoegenJaehrlich(vj);
                                break jaehrlichGefunden;
                            }
                            jahrZaehler++;
                        }
                        this.deletedVermoegenList.add(a);
                        this.vermoegenList.remove(a);
                        this.filteredVermoegenList.remove(a);
                        dao.deleteVermoegen(a);
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

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedVermoegenList.isEmpty()) {
            for (Vermoegen a : this.deletedVermoegenList) {
                a.setDeleted(false);
                this.vermoegenList.add(a);
                this.filteredVermoegenList.add(a);
                dao.updateVermoegen(a);

                //VermoegenJaehrlich:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                int gewaehltesJahr = Integer.parseInt(sdf.format(a.getDatum()));
                int jahrZaehler = 0;
                jaehrlichGefunden:
                for (VermoegenJaehrlich vj : this.jaehrlicheVermoegenList) {
                    if (vj.getJahr().equals(gewaehltesJahr)) {
                        vj.setAusgaben((vj.getAusgaben() - a.getAusgaben()));
                        vj.setEinnahmen((vj.getEinnahmen() - a.getEinnahmen()));
                        if (jahrZaehler > 0) {
                            double vorherDiffwert = this.jaehrlicheVermoegenList.get(jahrZaehler - 1).getDifferenz();
                            double jetztDiffwert = this.jaehrlicheVermoegenList.get(jahrZaehler).getDifferenz();
                            double przZuwachs = Double.parseDouble(String.format("%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));

                            vj.setProzentZuwachs(przZuwachs);
                        }
                        dao.updateVermoegenJaehrlich(vj);
                        break jaehrlichGefunden;
                    }
                    jahrZaehler++;
                }
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
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

    public DatenbankNotizen getDbnotizEintrag() {
        return dbnotizEintrag;
    }

    public void setDbnotizEintrag(DatenbankNotizen dbnotizEintrag) {
        this.dbnotizEintrag = dbnotizEintrag;
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

    public void scrollAnhangEdit() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("neuAnhangForm:AnhangEditPanel");
        PrimeFaces.current().scrollTo("neuAnhangForm:AnhangEditPanel");

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

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public Integer getRownumbers() {
        return rownumbers;
    }

    public void setRownumbers(Integer rownumbers) {
        this.rownumbers = rownumbers;
    }

    public Integer getInsert_rownumber() {
        return insert_rownumber;
    }

    public void setInsert_rownumber(Integer insert_rownumber) {
        this.insert_rownumber = insert_rownumber;
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

    public Double getEinnahmen() {
        return einnahmen;
    }

    public void setEinnahmen(Double einnahmen) {
        this.einnahmen = einnahmen;
    }

    public Double getAusgaben() {
        return ausgaben;
    }

    public void setAusgaben(Double ausgaben) {
        this.ausgaben = ausgaben;
    }

    public List<Vermoegen> getVermoegenList() {
        return vermoegenList;
    }

    public void setVermoegenList(List<Vermoegen> vermoegenList) {
        this.vermoegenList = vermoegenList;
    }

    public List<Vermoegen> getFilteredVermoegenList() {
        return filteredVermoegenList;
    }

    public void setFilteredVermoegenList(List<Vermoegen> filteredVermoegenList) {
        this.filteredVermoegenList = filteredVermoegenList;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    public Double getJaehrlicheProzentZuwachs() {
        return jaehrlicheProzentZuwachs;
    }

    public void setJaehrlicheProzentZuwachs(Double jaehrlicheProzentZuwachs) {
        this.jaehrlicheProzentZuwachs = jaehrlicheProzentZuwachs;
    }

    public Double getGesamtsummeZuwachs() {
        return gesamtsummeZuwachs;
    }

    public void setGesamtsummeZuwachs(Double gesamtsummeZuwachs) {
        this.gesamtsummeZuwachs = gesamtsummeZuwachs;
    }

    public Double getJaehrlichesummeZuwachs() {
        return jaehrlichesummeZuwachs;
    }

    public void setJaehrlichesummeZuwachs(Double jaehrlichesummeZuwachs) {
        this.jaehrlichesummeZuwachs = jaehrlichesummeZuwachs;
    }

    public LineChartModel getVermoegenWachstumMonatlich() {
        return vermoegenWachstumMonatlich;
    }

    public void setVermoegenWachstumMonatlich(LineChartModel vermoegenWachstumMonatlich) {
        this.vermoegenWachstumMonatlich = vermoegenWachstumMonatlich;
    }

    public List<VermoegenJaehrlich> getJaehrlicheVermoegenList() {
        return jaehrlicheVermoegenList;
    }

    public void setJaehrlicheVermoegenList(List<VermoegenJaehrlich> jaehrlicheVermoegenList) {
        this.jaehrlicheVermoegenList = jaehrlicheVermoegenList;
    }

    public List<VermoegenJaehrlich> getFilteredJaehrlicheVermoegenList() {
        return filteredJaehrlicheVermoegenList;
    }

    public void setFilteredJaehrlicheVermoegenList(List<VermoegenJaehrlich> filteredJaehrlicheVermoegenList) {
        this.filteredJaehrlicheVermoegenList = filteredJaehrlicheVermoegenList;
    }

    public LineChartModel getVermoegenWachstumMonatlichProzent() {
        return vermoegenWachstumMonatlichProzent;
    }

    public void setVermoegenWachstumMonatlichProzent(LineChartModel vermoegenWachstumMonatlichProzent) {
        this.vermoegenWachstumMonatlichProzent = vermoegenWachstumMonatlichProzent;
    }

    public String getTabellenname() {
        return tabellenname;
    }

    public void setTabellenname(String tabellenname) {
        this.tabellenname = tabellenname;
    }

    public String getNotiztext() {
        return notiztext;
    }

    public void setNotiztext(String notiztext) {
        this.notiztext = notiztext;
    }

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.baseUrl);
        hash = 59 * hash + Objects.hashCode(this.downloadUrl);
        hash = 59 * hash + Objects.hashCode(this.deleteID);
        hash = 59 * hash + Objects.hashCode(this.anhangID);
        hash = 59 * hash + Arrays.hashCode(this.anhang);
        hash = 59 * hash + Objects.hashCode(this.dao);
        hash = 59 * hash + Objects.hashCode(this.rownumbers);
        hash = 59 * hash + Objects.hashCode(this.insert_rownumber);
        hash = 59 * hash + Objects.hashCode(this.anhangname);
        hash = 59 * hash + Objects.hashCode(this.anhangtype);
        hash = 59 * hash + Objects.hashCode(this.einnahmen);
        hash = 59 * hash + Objects.hashCode(this.ausgaben);
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
        final VermoegenController other = (VermoegenController) obj;
        if (!Objects.equals(this.baseUrl, other.baseUrl)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrl, other.downloadUrl)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.anhangID, other.anhangID)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.insert_rownumber, other.insert_rownumber)) {
            return false;
        }
        if (!Objects.equals(this.einnahmen, other.einnahmen)) {
            return false;
        }
        if (!Objects.equals(this.ausgaben, other.ausgaben)) {
            return false;
        }
        return true;
    }

}
