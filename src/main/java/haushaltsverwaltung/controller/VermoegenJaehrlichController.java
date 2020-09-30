/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import haushaltsverwaltung.model.VermoegenJaehrlich;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 *
 * Financial assets (monthly) - monatliches Vermögen
 *
 * Verbunden mit VermoegenController Daten von VermoegenController werden hier
 * in Jahre zusammengefasst auto. abgespeichert.
 *
 * @author A.Dridi
 */
@Named(value = "vermoegenJaehrlichController")
@ViewScoped
public class VermoegenJaehrlichController implements Serializable {

    private LineChartModel vermoegenWachstumJaehrlich;
    private LineChartModel vermoegenWachstumJaehrlichProzent;

    private List<VermoegenJaehrlich> vermogenJaehrlichListe = new ArrayList<>();
    private List<VermoegenJaehrlich> filteredVermogenJaehrlichListe = new ArrayList<>();
    private String tabellenname = "Vermoegen_Jaehrlich";

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Vermoegen_Jaehrlich";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Vermoegen_Jaehrlich";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    private String deleteID;
    private String anhangID;

    private Integer jahr;
    private Double einnahmen;
    private Double ausgaben;
    private String bemerkungen;

    private byte[] anhang;
    private String anhangname;
    private String anhangtype;
    private String datensaetzeAnzahlText;

    private Integer rownumbers = 15;
    private Integer insert_rownumber;

    private Double gesamtsummeZuwachs;

    private DAO dao;
    private List<VermoegenJaehrlich> deletedVermogenJaehrlichListe = new ArrayList<>();

    /**
     * Creates a new instance of VermoegenJaehrlich
     */
    public VermoegenJaehrlichController() {
        this.dao = new DAO();
    }

    @PostConstruct
    private void init() {

        /*
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }*/
        //Wachstumbeträge abfragen und dann pro Jahr in Tabelle VermoegenJaehrlich speichern
        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        if (urlName.contains("vermoegen_grafik_jaehrlich.xhtml")) {
            createVermoegenZuwachsJaehrlichChart();
            createVermoegenZuwachsJaehrlichProzentChart();
            flushAnhang();

        } else {
            this.vermogenJaehrlichListe = dao.getAllVermoegenJaehrlich();
            this.filteredVermogenJaehrlichListe = new ArrayList<>(this.vermogenJaehrlichListe);
            flushAnhang();

            calculateGesamtsummeZuwachs();
            this.datensaetzeAnzahlText = ("Insgesamt: " + this.vermogenJaehrlichListe.size() + " Datensaetze in der DB gespeichert");
        }

    }

    /**
     * Ausgabe in Euro: Erstellt ein kombiniertes Diagramm (Line- und Barchart)
     * mit dem jährlichen Geldwachstum (Differenz) (Summe v. Spalte Differenz)
     * abbildet.
     */
    public void createVermoegenZuwachsJaehrlichChart() {
        this.vermoegenWachstumJaehrlich = new LineChartModel();
        List<VermoegenJaehrlich> vermoegenJaehrlichListe = dao.getAllVermoegenJaehrlich();
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (VermoegenJaehrlich vj : vermoegenJaehrlichListe) {
            labels.add(vj.getJahr().toString());
            values.add(vj.getDifferenz());
        }
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        this.vermoegenWachstumJaehrlich.setData(data);
        //Options
        LineChartOptions options = new LineChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        dataSet.setData(values);
        dataSet.setLabel("Vermögenswachstum in EURO");
        dataSet.setYaxisID("left-y-axis");

        linearAxes.setId("left-y-axis");
        linearAxes.setPosition("left");

        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Jährliches Vermögenswachstum (Gewinn) in EURO");
        options.setTitle(title);

        this.vermoegenWachstumJaehrlich.setOptions(options);
    }

    /**
     * Ausgabe in Prozent: Erstellt ein kombiniertes Diagramm (Line- und
     * Barchart) mit dem jährlichen Geldwachstum (Differenz) (Summe v. Spalte
     * Differenz) abbildet.
     */
    public void createVermoegenZuwachsJaehrlichProzentChart() {
        this.vermoegenWachstumJaehrlichProzent = new LineChartModel();
        List<VermoegenJaehrlich> vermoegenJaehrlichListe = dao.getAllVermoegenJaehrlich();
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();

        for (VermoegenJaehrlich vj : vermoegenJaehrlichListe) {
            labels.add(vj.getJahr().toString());
            values.add(Double.parseDouble(vj.getProzentZuwachs().toString()));
        }
        data.addChartDataSet(dataSet);
        dataSet.setData(values);
        data.setLabels(labels);
        dataSet.setLabel("Vermögenswachstum in EURO");
        dataSet.setYaxisID("left-y-axis");

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
        title.setText("Jährliches Vermögenswachstum (Gewinn) in Prozent");
        options.setTitle(title);
        this.vermoegenWachstumJaehrlichProzent.setData(data);
        this.vermoegenWachstumJaehrlichProzent.setOptions(options);

    }

    public void calculateGesamtsummeZuwachs() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select sum(differenz) FROM VermoegenJaehrlich";
        Query qu = s.createQuery(sqlstring);
        List<Double> waehrunggruppe = qu.list();

        if (waehrunggruppe.get(0) != null) {
            this.gesamtsummeZuwachs = waehrunggruppe.get(0);
            s.close();

        } else {
            s.close();
            this.gesamtsummeZuwachs = 0.0;
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
            List<VermoegenJaehrlich> liste = new ArrayList<>(this.vermogenJaehrlichListe);
            gefunden:
            for (VermoegenJaehrlich a : liste) {
                if (a.getVermoegenjaehrlich_id().equals(zeilenID)) {
                    Integer extPos = this.anhangname.lastIndexOf(".");
                    String dateiext = this.anhangname.substring(extPos + 1);
                    HttpClient client = new HttpClient();

                    Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
                    client.getState().setCredentials(AuthScope.ANY, creds);
                    if (this.anhang != null) {
                        a.setAnhang(true);
                        a.setAnhangname((a.getVermoegenjaehrlich_id()) + "." + dateiext);
                        a.setAnhangtype(this.anhangtype);
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getVermoegenjaehrlich_id()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getVermoegenjaehrlich_id()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateVermoegenJaehrlich(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getVermoegenjaehrlich_id() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getVermoegenjaehrlich_id()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateVermoegenJaehrlich(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getVermoegenjaehrlich_id() + " wurde gelöscht ", "Die phys. Datei muss dann manuell auf der Cloud von Ihnen gelöscht werden"));
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
            int zeile = tabelle.getRowIndex();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            VermoegenJaehrlich a = (this.dao.getSingleVermoegenJaehrlich((Integer) tabelle.getRowKey())).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Jahr")) {
                a.setJahr((Integer) event.getNewValue());
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
                    double vorherDiffwert = this.vermogenJaehrlichListe.get(zeile - 1).getDifferenz();
                    double jetztDiffwert = a.getDifferenz();
                    double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));

                    a.setProzentZuwachs(przZuwachs);
                }
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
                    double vorherDiffwert = this.vermogenJaehrlichListe.get(zeile - 1).getDifferenz();
                    double jetztDiffwert = a.getDifferenz();
                    double przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                    a.setProzentZuwachs(przZuwachs);
                }
            }
            if (spaltenname.equals("Bemerkungen")) {
                a.setBemerkungen((String) event.getNewValue());
            }

            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateVermoegenJaehrlich(a);
            updateData();
            FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

            //DEBUG:
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
        }
    }

    public void updateData() {

        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }

        this.vermogenJaehrlichListe = dao.getAllVermoegenJaehrlich();
        this.filteredVermogenJaehrlichListe = new ArrayList<>(this.vermogenJaehrlichListe);
        flushAnhang();
        calculateGesamtsummeZuwachs();
        createVermoegenZuwachsJaehrlichChart();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.vermogenJaehrlichListe.size() + " Datensaetze in der DB gespeichert");

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
        VermoegenJaehrlich v = new VermoegenJaehrlich();
        v.setDeleted(false);
        if (this.bemerkungen != null) {
            v.setBemerkungen(bemerkungen);
        }

        if (this.jahr != null) {
            v.setJahr(this.jahr);
        }

        if (this.ausgaben != null && this.einnahmen != null) {
            v.setAusgaben(this.ausgaben);
            v.setEinnahmen(this.einnahmen);
            Double differenzWert = v.getEinnahmen() - v.getAusgaben();
            v.setDifferenz(differenzWert);
            if (differenzWert >= 0) {
                v.setIsGewinn(true);
                v.setIsVerlust(false);
            } else {
                v.setIsGewinn(false);
                v.setIsVerlust(true);
            }

            List<VermoegenJaehrlich> neueListe = dao.getAllVermoegenJaehrlich();

            if (neueListe.size() > 0) {
                int lastIndex = neueListe.size() - 1;
                VermoegenJaehrlich vermoegenNeu = neueListe.get(lastIndex);
                double vorherDiffwert = this.vermogenJaehrlichListe.get(lastIndex).getDifferenz();
                double jetztDiffwert = v.getDifferenz();
                double przZuwachs;
                if ((jetztDiffwert - vorherDiffwert) == 0) {
                    przZuwachs = 0.0;
                } else {
                    przZuwachs = Double.parseDouble(String.format(Locale.US, "%.2f", ((jetztDiffwert - vorherDiffwert) / vorherDiffwert) * 100));
                }
                v.setProzentZuwachs(przZuwachs);
            } else {
                v.setProzentZuwachs(0.0);
            }

        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            v.setAnhang(true);
            this.vermogenJaehrlichListe.add(v);
            this.filteredVermogenJaehrlichListe.add(v);

            dao.insertVermoegenJaehrlich(v);

            List<VermoegenJaehrlich> ausgabenListe = new ArrayList<>(this.vermogenJaehrlichListe);
            int letzteNr = ausgabenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = ausgabenListe.get(letzteNr).getVermoegenjaehrlich_id();
                try {
                    VermoegenJaehrlich a = ausgabenListe.get(letzteNr);
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
                    dao.updateVermoegenJaehrlich(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateData();
            }

        } else {
            v.setAnhang(false);
            this.vermogenJaehrlichListe.add(v);
            this.filteredVermogenJaehrlichListe.add(v);

            dao.insertVermoegenJaehrlich(v);
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
                for (VermoegenJaehrlich a : this.vermogenJaehrlichListe) {
                    if (a.getVermoegenjaehrlich_id().equals(Integer.parseInt(this.deleteID))) {
                        dao.deleteVermoegenJaehrlich(a);
                        this.deletedVermogenJaehrlichListe.add(a);
                        this.vermogenJaehrlichListe.remove(a);
                        this.filteredVermogenJaehrlichListe.remove(a);
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
                for (VermoegenJaehrlich a : this.vermogenJaehrlichListe) {
                    if (a.getVermoegenjaehrlich_id().equals(id)) {
                        dao.deleteVermoegenJaehrlich(a);
                        this.deletedVermogenJaehrlichListe.add(a);
                        this.vermogenJaehrlichListe.remove(a);
                        this.filteredVermogenJaehrlichListe.remove(a);
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

        if (!this.deletedVermogenJaehrlichListe.isEmpty()) {
            for (VermoegenJaehrlich a : this.deletedVermogenJaehrlichListe) {
                a.setDeleted(false);
                this.vermogenJaehrlichListe.add(a);
                this.filteredVermogenJaehrlichListe.add(a);
                dao.updateVermoegenJaehrlich(a);
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
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

    public List<VermoegenJaehrlich> getVermogenJaehrlichListe() {
        return vermogenJaehrlichListe;
    }

    public void setVermogenJaehrlichListe(List<VermoegenJaehrlich> vermogenJaehrlichListe) {
        this.vermogenJaehrlichListe = vermogenJaehrlichListe;
    }

    public List<VermoegenJaehrlich> getFilteredVermogenJaehrlichListe() {
        return filteredVermogenJaehrlichListe;
    }

    public void setFilteredVermogenJaehrlichListe(List<VermoegenJaehrlich> filteredVermogenJaehrlichListe) {
        this.filteredVermogenJaehrlichListe = filteredVermogenJaehrlichListe;
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

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
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

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
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

    public Double getGesamtsummeZuwachs() {
        return gesamtsummeZuwachs;
    }

    public void setGesamtsummeZuwachs(Double gesamtsummeZuwachs) {
        this.gesamtsummeZuwachs = gesamtsummeZuwachs;
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    public LineChartModel getVermoegenWachstumJaehrlich() {
        return vermoegenWachstumJaehrlich;
    }

    public void setVermoegenWachstumJaehrlich(LineChartModel vermoegenWachstumJaehrlich) {
        this.vermoegenWachstumJaehrlich = vermoegenWachstumJaehrlich;
    }

    public LineChartModel getVermoegenWachstumJaehrlichProzent() {
        return vermoegenWachstumJaehrlichProzent;
    }

    public void setVermoegenWachstumJaehrlichProzent(LineChartModel vermoegenWachstumJaehrlichProzent) {
        this.vermoegenWachstumJaehrlichProzent = vermoegenWachstumJaehrlichProzent;
    }

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.vermoegenWachstumJaehrlich);
        hash = 97 * hash + Objects.hashCode(this.vermogenJaehrlichListe);
        hash = 97 * hash + Objects.hashCode(this.filteredVermogenJaehrlichListe);
        hash = 97 * hash + Objects.hashCode(this.baseUrl);
        hash = 97 * hash + Objects.hashCode(this.downloadUrl);
        hash = 97 * hash + Objects.hashCode(this.deleteID);
        hash = 97 * hash + Objects.hashCode(this.anhangID);
        hash = 97 * hash + Objects.hashCode(this.jahr);
        hash = 97 * hash + Objects.hashCode(this.einnahmen);
        hash = 97 * hash + Objects.hashCode(this.ausgaben);
        hash = 97 * hash + Arrays.hashCode(this.anhang);
        hash = 97 * hash + Objects.hashCode(this.anhangname);
        hash = 97 * hash + Objects.hashCode(this.anhangtype);
        hash = 97 * hash + Objects.hashCode(this.rownumbers);
        hash = 97 * hash + Objects.hashCode(this.insert_rownumber);
        hash = 97 * hash + Objects.hashCode(this.gesamtsummeZuwachs);
        hash = 97 * hash + Objects.hashCode(this.dao);
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
        final VermoegenJaehrlichController other = (VermoegenJaehrlichController) obj;
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
        if (!Objects.equals(this.vermoegenWachstumJaehrlich, other.vermoegenWachstumJaehrlich)) {
            return false;
        }
        if (!Objects.equals(this.vermogenJaehrlichListe, other.vermogenJaehrlichListe)) {
            return false;
        }
        if (!Objects.equals(this.filteredVermogenJaehrlichListe, other.filteredVermogenJaehrlichListe)) {
            return false;
        }
        if (!Objects.equals(this.jahr, other.jahr)) {
            return false;
        }
        if (!Objects.equals(this.einnahmen, other.einnahmen)) {
            return false;
        }
        if (!Objects.equals(this.ausgaben, other.ausgaben)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.insert_rownumber, other.insert_rownumber)) {
            return false;
        }
        if (!Objects.equals(this.gesamtsummeZuwachs, other.gesamtsummeZuwachs)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        return true;
    }

}
