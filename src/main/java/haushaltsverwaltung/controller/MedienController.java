/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.MedienMusik;
import haushaltsverwaltung.model.MedienMusikGenre;
import haushaltsverwaltung.model.MedienSoftware;
import haushaltsverwaltung.model.MedienSoftwareBetriebssystem;
import haushaltsverwaltung.model.MedienSoftwareHersteller;
import haushaltsverwaltung.model.MedienVideoclips;
import haushaltsverwaltung.model.MedienVideoclipsSprache;
import haushaltsverwaltung.model.MedienVideos;
import haushaltsverwaltung.model.MedienVideosGenre;
import haushaltsverwaltung.model.MedienVideosSprache;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.inject.Named;
import java.io.Serializable;
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
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.primefaces.model.charts.ChartData;

/**
 * Für die Tabellen MedienSoftware, MedienMusik, MedienVideoclips, MedienVideos
 * - For the tables MedienSoftware, MedienMusik, MedienVideoclips, MedienVideos
 *
 * @author A.Dridi
 */
@Named(value = "medienController")
@ViewScoped
public class MedienController implements Serializable {

    private PieChartModel chartBarVideosSpracheAnzahl;
    private PieChartModel chartBarVideosGenreAnzahl;
    private PieChartModel chartBarVideoclipsSpracheAnzahl;

    private List<MedienMusik> medienMusikListe;
    private List<MedienMusik> filteredMedienMusikListe;
    private List<MedienMusikGenre> medienMusikGenreListe;
    private List<MedienSoftware> medienSoftwareListe;
    private List<MedienSoftware> filteredMedienSoftwareListe;
    private List<MedienSoftwareBetriebssystem> medienSoftwareBetriebssystemListe;
    private List<MedienSoftwareHersteller> medienSoftwareHerstellerListe;
    private List<MedienVideoclips> medienVideoclipsListe;
    private List<MedienVideoclips> filteredMedienVideoclipsListe;
    private List<MedienVideoclipsSprache> medienVideoclipsSpracheListe;
    private List<MedienVideos> medienVideosListe;
    private List<MedienVideos> filteredMedienVideosListe;
    private List<MedienVideosGenre> medienVideosGenreListe;
    private List<MedienVideosSprache> medienVideosSpracheListe;

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String tabellenname = "Medien";
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/MedienSoftware";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/MedienSoftware";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    //MedienVideos ExportColumns - WICHTIG ANZAHL AN SPALTENANZAHL ANPASSEN (ausgenommen Anhang/D Spalte)!!!:
    private List<Boolean> columnListMusik = Arrays.asList(true, true, true, true, true, true, true, true, true);
    private List<Boolean> columnListSoftware = Arrays.asList(true, true, true, true, true, true, true, true, true);
    private List<Boolean> columnListVideoclips = Arrays.asList(true, true, true, true, true, true, true);
    private List<Boolean> columnListVideos = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);

    private String datensaetzeAnzahlTextMusik;
    private String datensaetzeAnzahlTextSoftware;
    private String datensaetzeAnzahlTextVideoclips;
    private String datensaetzeAnzahlTextVideos;

    private List<MedienMusik> deletedMedienMusikListe = new ArrayList<>();
    private List<MedienSoftware> deletedMedienSoftwareListe = new ArrayList<>();
    private List<MedienVideoclips> deletedMedienVideoclipsListe = new ArrayList<>();
    private List<MedienVideos> deletedMedienVideosListe = new ArrayList<>();

    //Musik
    private String interpretMusik;
    private String songnameMusik;
    private boolean altMusik;
    private Integer jahrMusik;
    private MedienMusikGenre genreEintragMusik;
    private String codeMusik;
    private String linkMusik;
    private String bemerkungenMusik;

    //Software
    private String programmnameSoftware;
    private MedienSoftwareHersteller herstellerEintragSoftware;
    private MedienSoftwareBetriebssystem betriebssystemEintragSoftware;
    private String spracheSoftware;
    private String versionSoftware;
    private String sonstige_infosSoftware;
    private String linkSoftware;

    //Videoclips
    private String interpretVideoclips;
    private String titelVideoclips;
    private MedienVideoclipsSprache spracheEintragVideoclips;
    private Integer jahrVideoclips;
    private String linkVideoclips;
    private String nativer_TitelVideoclips;

    //Videos
    private String nameVideos;
    private boolean ard_entertainementVideos;
    private MedienVideosSprache spracheEintragVideos;
    private boolean hdVideos;
    private MedienVideosGenre genreEintragVideos;
    private Integer dauerVideos;
    private Integer jahrVideos;
    private boolean serieVideos;
    private String linkVideos;
    private String nativer_TitelVideos;

    private DAO dao;
    private String neuMedienMusikGenre;
    private String neuMedienSoftwareBetriebssystem;
    private String neuMedienSoftwareHersteller;
    private String neuMedienVideoclipsSprache;
    private String neuMedienVideosGenre;
    private String neuMedienVideosSprache;

    private String deleteIDMusik;
    private String deleteIDSoftware;
    private String deleteIDVideoclips;
    private String deleteIDVideos;

    private MedienMusikGenre deleteMedienMusikGenre;
    private MedienSoftwareBetriebssystem deleteMedienSoftwareBetriebssystem;
    private MedienSoftwareHersteller deleteMedienSoftwareHersteller;
    private MedienVideoclipsSprache deleteMedienVideoclipsSprache;
    private MedienVideosGenre deleteMedienVideosGenre;
    private MedienVideosSprache deleteMedienVideosSprache;

    private String change_MedienMusikGenre;
    private String change_MedienSoftwareBetriebssystem;
    private String change_MedienSoftwareHersteller;
    private String change_MedienVideoclipsSprache;
    private String change_MedienVideosGenre;
    private String change_MedienVideosSprache;

    //Nur für Tabelle MedienSoftware:
    private byte[] anhang;
    private String anhangID;
    private String anhangname;
    private String anhangtype;

    private Integer rownumbers = 15;

    private DatenbankNotizen dbnotizEintrag = null;
    private String notiztext;

    private String summeMedienVideos;

    /**
     * Creates a new instance of MedienController
     */
    public MedienController() {
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

        //Tabelle MedienMusik
        if (urlName.contains("medien_musik.xhtml")) {
            updateDataMusik();
        } //Tabelle MedienSoftware
        else if (urlName.contains("medien_software.xhtml")) {
            HttpClient client = new HttpClient();
            Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
            client.getState().setCredentials(AuthScope.ANY, creds);
            GetMethod method = new GetMethod(this.downloadUrl);
            try {
                client.executeMethod(method);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
            }
            updateDataSoftware();
            flushAnhang();
        } //Tabelle MedienVideoclips
        else if (urlName.contains("medien_videoclips.xhtml")) {
            updateDataVideoclips();
        } //Tabelle MedienVideos
        else if (urlName.contains("medien_videos.xhtml")) {
            updateDataVideos();
        } //Ansicht Medien Grafik
        else if (urlName.contains("medien_grafik.xhtml")) {
            this.medienVideoclipsSpracheListe = dao.getAllMedienVideoclipsSprache();
            this.medienVideosSpracheListe = dao.getAllMedienVideosSprache();
            this.medienVideosGenreListe = dao.getAllMedienVideosGenre();

            createBarVideoclips();
            createBarVideos();
        } else {
            updateDataMusik();
        }
    }

    /**
     * FÜR TABELLE MedienSoftware - Anhang bearbeiten: Aber bei Übergabe eines
     * leeren Anhangs wird der Anhang für die betroffene Zeile gelöscht
     */
    public void editAnhang() {
        try {
            int zeilenID = Integer.parseInt(this.anhangID);
            boolean id_existiert = false;
            List<MedienSoftware> liste = new ArrayList<>(this.medienSoftwareListe);
            gefunden:
            for (MedienSoftware a : liste) {
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
                        a.setAnhangpfad(this.downloadUrl + "/" + ((a.getId()) + "." + dateiext));

                        InputStream ins = new ByteArrayInputStream(this.anhang);
                        PutMethod method = new PutMethod(this.baseUrl + "/" + ((a.getId()) + "." + dateiext));
                        RequestEntity requestEntity = new InputStreamRequestEntity(ins);
                        method.setRequestEntity(requestEntity);
                        client.executeMethod(method);
                        System.out.println(method.getStatusCode() + " " + method.getStatusText());
                        dao.updateMedienSoftware(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getId()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateMedienSoftware(a);
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
     * Berechnet Gesamtdauer aller Videos (von Tabelle MedienVideos) in Tagen
     * und Stunden
     */
    public void calculateGesamtMedienVideosDauer() {
        this.summeMedienVideos = "Gesamtdauer: ";
        Long gesamtdauerMinuten = 0L;
        Long dauerTage;
        Long dauerStunden;
        Session s = HibernateUtil.getSessionFactory().openSession();

        try {
            //jaehrliche Einnahmen
            String sqlstring = "Select sum(dauer) FROM MedienVideos where deleted=false";
            Query qu = s.createQuery(sqlstring);
            List<Long> waehrunggruppe = qu.list();

            if (waehrunggruppe.get(0) != null) {
                gesamtdauerMinuten = waehrunggruppe.get(0);

                if (gesamtdauerMinuten != 0) {
                    dauerTage = (gesamtdauerMinuten / 60) / 24;
                    dauerStunden = (gesamtdauerMinuten / 60) % 24;
                    this.summeMedienVideos = "Gesamtdauer: " + dauerTage + " Tage (d) und " + dauerStunden + " Stunden (h)";
                    //Abfrage von Gesamtdauer von ard Entertainement Videos
                    sqlstring = "Select sum(dauer) FROM MedienVideos where deleted=false and ard_entertainement=true";
                    qu = s.createQuery(sqlstring);
                    waehrunggruppe = qu.list();
                    if (waehrunggruppe.get(0) != null) {
                        gesamtdauerMinuten = waehrunggruppe.get(0);
                        if (gesamtdauerMinuten != 0) {
                            dauerTage = (gesamtdauerMinuten / 60) / 24;
                            dauerStunden = (gesamtdauerMinuten / 60) % 24;
                            this.summeMedienVideos += " -- Davon sind " + dauerTage + " Tage und " + dauerStunden + " Stunden Eigenproduktionen ard Entertainement";
                        }
                    }
                }
            }
            s.close();

        } catch (Exception e) {
            s.close();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Berechnung von MedienVideos Gesammtdauer: ", "" + e));
        }

    }

    /**
     * Charts für Videos - Verteilung (Anzahl) nach Sprachen und Genres
     */
    public void createBarVideos() {

        //Videos Anzahl - Sprachen
        this.chartBarVideosSpracheAnzahl = new PieChartModel();
        ChartData data = new ChartData();
        PieChartDataSet dataSet = new PieChartDataSet();

        List<String> labels = new ArrayList<>();
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();

        //SQL Abruf
        // Liefert alle Einträge 
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<MedienVideosSprache> waehrungenliste = this.medienVideosSpracheListe;
        abbrechen:
        for (MedienVideosSprache w : waehrungenliste) {
            try {
                String sqlstring = "Select count(videos_id) FROM MedienVideos where deleted=false and sprache = :einsatzvariable";

                Query qu = s.createQuery(sqlstring);
                qu.setString("einsatzvariable", w.getBezeichnung());
                List<Long> waehrunggruppe = qu.list();

                if (waehrunggruppe != null && !waehrunggruppe.isEmpty()) {
                    //Alle Datumeinträge hinzufügen
                    if (waehrunggruppe.get(0) != 0) {
                        // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));
                        labels.add(w.getBezeichnung());
                        values.add(waehrunggruppe.get(0));
                        //Hintergrundfarben durch Zufall erstellen.
                        Random rnd = new Random();
                        bgColors.add("rgb(" + rnd.nextInt(240) + "," + rnd.nextInt(240) + "," + rnd.nextInt(240) + ")");
                    }
                }

            } catch (Exception e) {
                System.out.println("Fehler in createBarVideos: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Abfrage von MedienVideos:", "" + e));
                break abbrechen;
            }
        }
        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        this.chartBarVideosSpracheAnzahl.setData(data);

        //Videos Anzahl - Genre
        this.chartBarVideosGenreAnzahl = new PieChartModel();
        ChartData data2 = new ChartData();
        PieChartDataSet dataSet2 = new PieChartDataSet();

        List<String> labels2 = new ArrayList<>();
        List<Number> values2 = new ArrayList<>();
        List<String> bgColors2 = new ArrayList<>();

        //SQL Abruf
        // Liefert alle Einträge 
        s = HibernateUtil.getSessionFactory().openSession();
        List<MedienVideosGenre> waehrungenliste2 = this.medienVideosGenreListe;
        abbrechen:
        for (MedienVideosGenre w : waehrungenliste2) {
            try {
                String sqlstring = "Select count(videos_id) FROM MedienVideos where genre = :einsatzvariable";

                Query qu = s.createQuery(sqlstring);
                qu.setString("einsatzvariable", w.getBezeichnung());
                List<Long> waehrunggruppe = qu.list();

                if (waehrunggruppe != null && !waehrunggruppe.isEmpty() && waehrunggruppe.get(0) != null) {
                    //Alle Datumeinträge hinzufügen
                    if (waehrunggruppe.get(0) != 0) {
                        // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));
                        labels2.add(w.getBezeichnung());
                        values2.add(waehrunggruppe.get(0));
                        //Hintergrundfarben durch Zufall erstellen.
                        Random rnd = new Random();
                        bgColors2.add("rgb(" + rnd.nextInt(240) + "," + rnd.nextInt(240) + "," + rnd.nextInt(240) + ")");
                    }
                }
            } catch (Exception e) {
                System.out.println("Fehler in createBarVideos: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Abfrage von MedienVideos:", "" + e));
                break abbrechen;
            }
        }

        dataSet2.setData(values2);
        dataSet2.setBackgroundColor(bgColors2);
        data2.addChartDataSet(dataSet2);
        data2.setLabels(labels2);
        this.chartBarVideosGenreAnzahl.setData(data2);
    }

    /**
     * Charts für Videoclips - Verteilung (Anzahl) nach Sprachen
     */
    public void createBarVideoclips() {

        //Videos Anzahl - Sprachen
        this.chartBarVideoclipsSpracheAnzahl = new PieChartModel();
        ChartData data = new ChartData();
        PieChartDataSet dataSet = new PieChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();

        //SQL Abruf
        // Liefert alle Einträge 
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<MedienVideoclipsSprache> waehrungenliste = this.medienVideoclipsSpracheListe;
        abbrechen:
        for (MedienVideoclipsSprache w : waehrungenliste) {
            try {
                String sqlstring = "Select count(id) FROM MedienVideoclips where sprache = :einsatzvariable and deleted=false";

                Query qu = s.createQuery(sqlstring);
                qu.setString("einsatzvariable", w.getBezeichnung());
                List<Long> waehrunggruppe = qu.list();

                if (waehrunggruppe != null && !waehrunggruppe.isEmpty() && waehrunggruppe.get(0) != null) {
                    //Alle Datumeinträge hinzufügen
                    if (waehrunggruppe.get(0) != 0) {
                        // Double przwert = (Double) ((((Double) o[1]) / ((Double) this.gesamtwertEuro)));
                        labels.add(w.getBezeichnung());
                        values.add(waehrunggruppe.get(0));
                        //Hintergrundfarben durch Zufall erstellen.
                        Random rnd = new Random();
                        bgColors.add("rgb(" + rnd.nextInt(240) + "," + rnd.nextInt(240) + "," + rnd.nextInt(240) + ")");
                    }
                }

            } catch (Exception e) {
                System.out.println("Fehler in createBarVideoclips: " + e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Abfrage von MedienVideoclips:", "" + e));
                break abbrechen;
            }

        }
        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        this.chartBarVideoclipsSpracheAnzahl.setData(data);
    }

    /**
     * Editor für Zeile aufrufen
     */
    public void editRow(CellEditEvent event) {

        //Aufgerufene Tabellenwebseite überprüfen
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlName = request.getRequestURI();

        //Tabelle MedienMusik
        if (urlName.contains("medien_musik.xhtml")) {

            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                MedienMusik a = (this.dao.getSingleMedienMusik((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Interpret")) {
                    a.setInterpret((String) event.getNewValue());
                }
                if (spaltenname.equals("Songname")) {
                    a.setSongname((String) event.getNewValue());
                }

                if (spaltenname.equals("Alt")) {
                    a.setAlt((Boolean) event.getNewValue());
                }
                if (spaltenname.equals("Jahr")) {
                    a.setJahr((Integer) event.getNewValue());
                }
                if (spaltenname.equals("Genre")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (MedienMusikGenre m : this.medienMusikGenreListe) {
                        if (m.getBezeichnung().equals(auswahl)) {
                            a.setGenre((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Code")) {
                    a.setCode((String) event.getNewValue());
                }
                if (spaltenname.equals("Link")) {
                    a.setLink((String) event.getNewValue());
                }
                if (spaltenname.equals("Bemerkungen")) {
                    a.setBemerkungen((String) event.getNewValue());
                }

                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateMedienMusik(a);
                updateDataMusik();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        } //Tabelle MedienSoftware
        else if (urlName.contains("medien_software.xhtml")) {

            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                MedienSoftware a = (this.dao.getSingleMedienSoftware((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Programmname")) {
                    a.setProgrammname((String) event.getNewValue());
                }
                if (spaltenname.equals("Hersteller")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (MedienSoftwareHersteller m : this.medienSoftwareHerstellerListe) {
                        if (m.getBezeichnung().equals(auswahl)) {
                            a.setHersteller((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Betriebssystem")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (MedienSoftwareBetriebssystem m : this.medienSoftwareBetriebssystemListe) {
                        if (m.getBezeichnung().equals(auswahl)) {
                            a.setBetriebssystem((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Sprache")) {
                    a.setSprache((String) event.getNewValue());
                }
                if (spaltenname.equals("Version")) {
                    a.setVersion((String) event.getNewValue());
                }
                if (spaltenname.equals("Sonstige Infos")) {
                    a.setSonstige_infos((String) event.getNewValue());
                }
                if (spaltenname.equals("Link")) {
                    a.setLink((String) event.getNewValue());
                }
                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateMedienSoftware(a);
                updateDataSoftware();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        } //Tabelle MedienVideoclips
        else if (urlName.contains("medien_videoclips.xhtml")) {

            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                MedienVideoclips a = (this.dao.getSingleMedienVideoclips((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Interpret")) {
                    a.setInterpret((String) event.getNewValue());
                }
                if (spaltenname.equals("Titel")) {
                    a.setTitel((String) event.getNewValue());
                }

                if (spaltenname.equals("Sprache")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (MedienVideoclipsSprache m : this.medienVideoclipsSpracheListe) {
                        if (m.getBezeichnung().equals(auswahl)) {
                            a.setSprache((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Jahr")) {
                    a.setJahr((Integer) event.getNewValue());
                }
                if (spaltenname.equals("Link")) {
                    a.setLink((String) event.getNewValue());
                }
                if (spaltenname.equals("Nativer Titel")) {
                    a.setNativer_titel((String) event.getNewValue());
                }

                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateMedienVideoclips(a);
                updateDataVideoclips();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        } //Tabelle MedienVideos
        else if (urlName.contains("medien_videos.xhtml")) {

            try {
                DataTable tabelle = (DataTable) event.getSource();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                MedienVideos a = (this.dao.getSingleMedienVideos((Integer) tabelle.getRowKey())).get(0);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Name")) {
                    a.setName((String) event.getNewValue());
                }
                if (spaltenname.equals("ard Entertainement")) {
                    a.setArd_entertainement((boolean) event.getNewValue());
                }

                if (spaltenname.equals("Sprache")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (MedienVideosSprache m : this.medienVideosSpracheListe) {
                        if (m.getBezeichnung().equals(auswahl)) {
                            a.setSprache((String) event.getNewValue());
                            break gefunden;
                        }

                    }
                }
                if (spaltenname.equals("HD")) {
                    a.setHd((boolean) event.getNewValue());
                }
                if (spaltenname.equals("Genre")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (MedienVideosGenre m : this.medienVideosGenreListe) {
                        if (m.getBezeichnung().equals(auswahl)) {
                            a.setGenre((String) event.getNewValue());
                            break gefunden;
                        }

                    }
                }
                if (spaltenname.equals("Dauer")) {
                    a.setDauer((Integer) event.getNewValue());
                }
                if (spaltenname.equals("Jahr")) {
                    a.setJahr((Integer) event.getNewValue());
                }
                if (spaltenname.equals("Serie")) {
                    a.setSerie((boolean) event.getNewValue());
                }
                if (spaltenname.equals("Link")) {
                    a.setLink((String) event.getNewValue());
                }
                if (spaltenname.equals("Nativer Titel")) {
                    a.setNativer_titel((String) event.getNewValue());
                }
                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateMedienVideos(a);
                updateDataVideos();
                FacesContext.getCurrentInstance().addMessage("nachrichtGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Datensatz wurde aktualisiert", ""));

                //DEBUG:
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kategorie: ", kategorie));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", e.toString()));
            }
        } else {
            //Startseite von Medien = Tabelle MedienMusik
            try {
                DataTable tabelle = (DataTable) event.getSource();
                int zeile = tabelle.getRowIndex();
                String spaltenname = event.getColumn().getHeaderText();
                this.dao = new DAO();

                MedienMusik a = (new ArrayList<>(this.medienMusikListe)).get(zeile);
                //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

                if (spaltenname.equals("Interpret")) {
                    a.setInterpret((String) event.getNewValue());
                }
                if (spaltenname.equals("Songname")) {
                    a.setSongname((String) event.getNewValue());
                }

                if (spaltenname.equals("Alt")) {
                    a.setAlt((Boolean) event.getNewValue());
                }
                if (spaltenname.equals("Jahr")) {
                    a.setJahr((Integer) event.getNewValue());
                }
                if (spaltenname.equals("Genre")) {
                    String auswahl = (String) event.getNewValue();
                    gefunden:
                    for (MedienMusikGenre m : this.medienMusikGenreListe) {
                        if (m.getBezeichnung().equals(auswahl)) {
                            a.setGenre((String) event.getNewValue());
                            break gefunden;
                        }
                    }
                }
                if (spaltenname.equals("Code")) {
                    a.setCode((String) event.getNewValue());
                }
                if (spaltenname.equals("Link")) {
                    a.setLink((String) event.getNewValue());
                }
                if (spaltenname.equals("Bemerkungen")) {
                    a.setBemerkungen((String) event.getNewValue());
                }

                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
                dao.updateMedienMusik(a);
                updateDataMusik();
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
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        this.medienMusikListe = dao.getAllMedienMusik();
        this.filteredMedienMusikListe = new ArrayList<>(this.medienMusikListe);
        this.medienMusikGenreListe = dao.getAllMedienMusikGenre();
        this.medienSoftwareListe = dao.getAllMedienSoftware();
        this.filteredMedienSoftwareListe = new ArrayList<>(this.medienSoftwareListe);
        this.medienSoftwareBetriebssystemListe = dao.getAllMedienSoftwareBetriebssystem();
        this.medienSoftwareHerstellerListe = dao.getAllMedienSoftwareHersteller();
        this.medienVideoclipsListe = dao.getAllMedienVideoclips();
        this.filteredMedienVideoclipsListe = new ArrayList<>(this.medienVideoclipsListe);
        this.medienVideoclipsSpracheListe = dao.getAllMedienVideoclipsSprache();
        this.medienVideosListe = dao.getAllMedienVideos();
        this.filteredMedienVideosListe = new ArrayList<>(this.medienVideosListe);
        this.medienVideosSpracheListe = dao.getAllMedienVideosSprache();
        this.medienVideosGenreListe = dao.getAllMedienVideosGenre();
        flushAnhang();
        calculateGesamtMedienVideosDauer();
        this.datensaetzeAnzahlTextMusik = ("Insgesamt: " + this.medienMusikListe.size() + " Datensaetze in der DB gespeichert");
        this.datensaetzeAnzahlTextSoftware = ("Insgesamt: " + this.medienSoftwareListe.size() + " Datensaetze in der DB gespeichert");
        this.datensaetzeAnzahlTextVideos = ("Insgesamt: " + this.medienVideosListe.size() + " Datensaetze in der DB gespeichert");
        this.datensaetzeAnzahlTextVideoclips = ("Insgesamt: " + this.medienVideoclipsListe.size() + " Datensaetze in der DB gespeichert");

    }

    public void updateDataMusik() {
        this.medienMusikListe = dao.getAllMedienMusik();
        this.filteredMedienMusikListe = new ArrayList<>(this.medienMusikListe);
        this.medienMusikGenreListe = dao.getAllMedienMusikGenre();
        this.datensaetzeAnzahlTextMusik = ("Insgesamt: " + this.medienMusikListe.size() + " Datensaetze in der DB gespeichert");
    }

    public void updateDataSoftware() {

        this.medienSoftwareListe = dao.getAllMedienSoftware();
        this.filteredMedienSoftwareListe = new ArrayList<>(this.medienSoftwareListe);
        this.medienSoftwareBetriebssystemListe = dao.getAllMedienSoftwareBetriebssystem();
        this.medienSoftwareHerstellerListe = dao.getAllMedienSoftwareHersteller();
        flushAnhang();
        this.datensaetzeAnzahlTextSoftware = ("Insgesamt: " + this.medienSoftwareListe.size() + " Datensaetze in der DB gespeichert");

    }

    public void updateDataVideoclips() {
        this.medienVideoclipsListe = dao.getAllMedienVideoclips();
        this.filteredMedienVideoclipsListe = new ArrayList<>(this.medienVideoclipsListe);
        this.medienVideoclipsSpracheListe = dao.getAllMedienVideoclipsSprache();
        this.datensaetzeAnzahlTextVideoclips = ("Insgesamt: " + this.medienVideoclipsListe.size() + " Datensaetze in der DB gespeichert");

    }

    public void updateDataVideos() {
        this.medienVideosListe = dao.getAllMedienVideos();
        this.filteredMedienVideosListe = new ArrayList<>(this.medienVideosListe);
        this.medienVideosSpracheListe = dao.getAllMedienVideosSprache();
        this.medienVideosGenreListe = dao.getAllMedienVideosGenre();
        calculateGesamtMedienVideosDauer();
        this.datensaetzeAnzahlTextVideos = ("Insgesamt: " + this.medienVideosListe.size() + " Datensaetze in der DB gespeichert");
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

    public void speichernMedienMusik() {
        MedienMusik musik = new MedienMusik();
        musik.setDeleted(false);

        if (this.interpretMusik != null) {
            musik.setInterpret(interpretMusik);
        }
        if (this.songnameMusik != null) {
            musik.setSongname(songnameMusik);
        }
        musik.setAlt(altMusik);
        musik.setJahr(jahrMusik);

        if (this.genreEintragMusik != null) {
            musik.setGenre(this.genreEintragMusik.getBezeichnung());
        }

        if (this.codeMusik != null) {
            musik.setCode(this.codeMusik);
        }

        if (this.linkMusik != null) {
            musik.setLink(linkMusik);
        }

        if (this.bemerkungenMusik != null) {
            musik.setBemerkungen(this.bemerkungenMusik);
        }
        //IMMER VOR DEM INSERT BEFEHL
        //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
        this.medienMusikListe.add(musik);
        this.filteredMedienMusikListe.add(musik);

        dao.insertMedienMusik(musik);
        updateDataMusik();

    }

    public void speichernMedienSoftware() {
        MedienSoftware software = new MedienSoftware();
        software.setDeleted(false);

        if (this.programmnameSoftware != null) {
            software.setProgrammname(programmnameSoftware);
        }

        if (this.herstellerEintragSoftware != null) {
            software.setHersteller(this.herstellerEintragSoftware.getBezeichnung());
        }
        if (this.betriebssystemEintragSoftware != null) {
            software.setBetriebssystem(this.betriebssystemEintragSoftware.getBezeichnung());
        }
        if (this.spracheSoftware != null) {
            software.setSprache(spracheSoftware);
        }

        if (this.versionSoftware != null) {
            software.setVersion(versionSoftware);
        }
        if (this.sonstige_infosSoftware != null) {
            software.setSonstige_infos(sonstige_infosSoftware);
        }
        if (this.linkSoftware != null) {
            software.setLink(linkSoftware);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            //IMMER VOR DEM INSERT BEFEHL
            //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
            software.setAnhang(true);
            this.medienSoftwareListe.add(software);
            this.filteredMedienSoftwareListe.add(software);

            dao.insertMedienSoftware(software);

            List<MedienSoftware> softwareListe = new ArrayList<>(this.medienSoftwareListe);
            int letzteNr = softwareListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = softwareListe.get(letzteNr).getId();
                try {
                    MedienSoftware a = softwareListe.get(letzteNr);
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
                    dao.updateMedienSoftware(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }

                updateDataSoftware();
            }

        } else {
            software.setAnhang(false);
            //IMMER VOR DEM INSERT BEFEHL
            //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
            this.medienSoftwareListe.add(software);
            this.filteredMedienSoftwareListe.add(software);

            dao.insertMedienSoftware(software);
            updateDataSoftware();
        }
    }

    public void speichernMedienVideoclips() {
        MedienVideoclips videoclips = new MedienVideoclips();
        videoclips.setDeleted(false);

        if (this.interpretVideoclips != null) {
            videoclips.setInterpret(interpretVideoclips);
        }
        if (this.titelVideoclips != null) {
            videoclips.setTitel(titelVideoclips);
        }
        if (this.spracheEintragVideoclips != null) {
            videoclips.setSprache(this.spracheEintragVideoclips.getBezeichnung());
        }

        videoclips.setJahr(this.jahrVideoclips);
        if (this.linkVideoclips != null) {
            videoclips.setLink(this.linkVideoclips);
        }
        if (this.nativer_TitelVideoclips != null) {
            videoclips.setNativer_titel(this.nativer_TitelVideoclips);
        }

        //IMMER VOR DEM INSERT BEFEHL
        //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
        this.medienVideoclipsListe.add(videoclips);
        this.filteredMedienVideoclipsListe.add(videoclips);

        dao.insertMedienVideoclips(videoclips);
        updateDataVideoclips();
    }

    public void speichernMedienVideos() {
        MedienVideos videos = new MedienVideos();
        videos.setDeleted(false);

        if (this.nameVideos != null) {
            videos.setName(nameVideos);
        }
        videos.setArd_entertainement(ard_entertainementVideos);

        if (this.spracheEintragVideos != null) {
            videos.setSprache(this.spracheEintragVideos.getBezeichnung());
        }
        videos.setHd(ard_entertainementVideos);

        if (this.genreEintragVideos != null) {
            videos.setGenre(this.genreEintragVideos.getBezeichnung());
        }

        videos.setSerie(this.serieVideos);

        videos.setDauer(dauerVideos);
        videos.setJahr(jahrVideos);
        videos.setLink(linkVideos);
        if (this.nativer_TitelVideos != null) {
            videos.setNativer_titel(this.nativer_TitelVideos);
        }

        //IMMER VOR DEM INSERT BEFEHL
        //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
        this.medienVideosListe.add(videos);
        this.filteredMedienVideosListe.add(videos);

        dao.insertMedienVideos(videos);
        updateDataVideos();
    }

    public void medienMusikGenreSpeichern() {

        if (!this.neuMedienMusikGenre.isEmpty()) {
            MedienMusikGenre ak = new MedienMusikGenre();
            ak.setBezeichnung(this.neuMedienMusikGenre);
            dao.insertMedienMusikGenre(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataMusik();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienMusikGenreLoeschen() {

        if (this.deleteMedienMusikGenre != null) {

            List<MedienMusikGenre> akList = dao.getAllMedienMusikGenre();
            List<MedienMusik> ausgabenList = dao.getAllMedienMusik();
            boolean kategorieExist = false;

            for (MedienMusikGenre a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteMedienMusikGenre.getBezeichnung().toLowerCase())) {
                    dao.deleteMedienMusikGenre(a);
                    for (MedienMusik medienmusik : ausgabenList) {
                        if ((medienmusik.getGenre().toLowerCase()).equals(this.deleteMedienMusikGenre.getBezeichnung().toLowerCase())) {
                            this.medienMusikListe.remove(medienmusik);
                            this.filteredMedienMusikListe.remove(medienmusik);

                            medienmusik.setGenre(this.change_MedienMusikGenre);
                            dao.updateMedienMusik(medienmusik);
                            this.medienMusikListe.add(medienmusik);
                            this.filteredMedienMusikListe.add(medienmusik);

                        }
                    }
                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_MedienMusikGenre.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                MedienMusikGenre neu = new MedienMusikGenre();
                neu.setBezeichnung(this.change_MedienMusikGenre);
                dao.insertMedienMusikGenre(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataMusik();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienSoftwareBetriebssystemSpeichern() {

        if (!this.neuMedienSoftwareBetriebssystem.isEmpty()) {
            MedienSoftwareBetriebssystem ak = new MedienSoftwareBetriebssystem();
            ak.setBezeichnung(this.neuMedienSoftwareBetriebssystem);
            dao.insertMedienSoftwareBetriebssystem(ak);

            updateDataSoftware();

        }

    }

    public void medienSoftwareBetriebssystemLoeschen() {

        if (this.deleteMedienSoftwareBetriebssystem != null) {

            List<MedienSoftwareBetriebssystem> akList = dao.getAllMedienSoftwareBetriebssystem();
            List<MedienSoftware> softwareList = dao.getAllMedienSoftware();
            boolean kategorieExist = false;
            for (MedienSoftwareBetriebssystem a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteMedienSoftwareBetriebssystem.getBezeichnung().toLowerCase())) {

                    dao.deleteMedienSoftwareBetriebssystem(a);
                    for (MedienSoftware software : softwareList) {
                        if ((software.getBetriebssystem().toLowerCase()).equals(this.deleteMedienSoftwareBetriebssystem.getBezeichnung().toLowerCase())) {
                            this.medienSoftwareListe.remove(software);
                            this.filteredMedienSoftwareListe.remove(software);
                            software.setBetriebssystem(this.change_MedienSoftwareBetriebssystem);
                            dao.updateMedienSoftware(software);
                            this.medienSoftwareListe.add(software);
                            this.filteredMedienSoftwareListe.add(software);
                        }
                    }

                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_MedienSoftwareBetriebssystem.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                MedienSoftwareBetriebssystem neu = new MedienSoftwareBetriebssystem();
                neu.setBezeichnung(this.change_MedienSoftwareBetriebssystem);
                dao.insertMedienSoftwareBetriebssystem(neu);
            }

            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));
            updateDataSoftware();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienSoftwareHerstellerSpeichern() {

        if (!this.neuMedienSoftwareHersteller.isEmpty()) {
            MedienSoftwareHersteller ak = new MedienSoftwareHersteller();
            ak.setBezeichnung(this.neuMedienSoftwareHersteller);
            dao.insertMedienSoftwareHersteller(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataSoftware();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienSoftwareHerstellerLoeschen() {

        if (this.deleteMedienSoftwareHersteller != null) {

            List<MedienSoftwareHersteller> akList = dao.getAllMedienSoftwareHersteller();
            List<MedienSoftware> softwareList = dao.getAllMedienSoftware();
            boolean kategorieExist = false;

            for (MedienSoftwareHersteller a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteMedienSoftwareHersteller.getBezeichnung().toLowerCase())) {
                    dao.deleteMedienSoftwareHersteller(a);
                    for (MedienSoftware software : softwareList) {
                        if ((software.getHersteller().toLowerCase()).equals(this.deleteMedienSoftwareHersteller.getBezeichnung().toLowerCase())) {
                            this.medienSoftwareListe.remove(software);
                            this.filteredMedienSoftwareListe.remove(software);

                            software.setHersteller(this.change_MedienSoftwareHersteller);
                            dao.updateMedienSoftware(software);
                            this.medienSoftwareListe.add(software);
                            this.filteredMedienSoftwareListe.add(software);

                        }
                    }
                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_MedienSoftwareHersteller.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                MedienSoftwareHersteller neu = new MedienSoftwareHersteller();
                neu.setBezeichnung(this.change_MedienSoftwareHersteller);
                dao.insertMedienSoftwareHersteller(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataSoftware();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienVideoclipsSpracheSpeichern() {

        if (!this.neuMedienVideoclipsSprache.isEmpty()) {
            MedienVideoclipsSprache ak = new MedienVideoclipsSprache();
            ak.setBezeichnung(this.neuMedienVideoclipsSprache);
            dao.insertMedienVideoclipsSprache(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataVideoclips();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienVideoclipsSpracheLoeschen() {

        if (this.deleteMedienVideoclipsSprache != null) {

            List<MedienVideoclipsSprache> akList = dao.getAllMedienVideoclipsSprache();
            List<MedienVideoclips> videoclipsList = dao.getAllMedienVideoclips();
            boolean kategorieExist = false;

            for (MedienVideoclipsSprache a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteMedienVideoclipsSprache.getBezeichnung().toLowerCase())) {
                    dao.deleteMedienVideoclipsSprache(a);
                    for (MedienVideoclips videoclip : videoclipsList) {
                        if ((videoclip.getSprache().toLowerCase()).equals(this.deleteMedienVideoclipsSprache.getBezeichnung().toLowerCase())) {
                            this.medienVideoclipsListe.remove(videoclip);
                            this.filteredMedienVideoclipsListe.remove(videoclip);

                            videoclip.setSprache(this.change_MedienVideoclipsSprache);
                            dao.updateMedienVideoclips(videoclip);
                            this.medienVideoclipsListe.add(videoclip);
                            this.filteredMedienVideoclipsListe.add(videoclip);

                        }
                    }
                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_MedienVideoclipsSprache.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                MedienVideoclipsSprache neu = new MedienVideoclipsSprache();
                neu.setBezeichnung(this.change_MedienVideoclipsSprache);
                dao.insertMedienVideoclipsSprache(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataVideoclips();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienVideosGenreSpeichern() {

        if (!this.neuMedienVideosGenre.isEmpty()) {
            MedienVideosGenre ak = new MedienVideosGenre();
            ak.setBezeichnung(this.neuMedienVideosGenre);
            dao.insertMedienVideosGenre(ak);
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataVideos();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienVideosGenreLoeschen() {

        if (this.deleteMedienVideosGenre != null) {

            List<MedienVideosGenre> akList = dao.getAllMedienVideosGenre();
            List<MedienVideos> videosList = dao.getAllMedienVideos();
            boolean kategorieExist = false;

            for (MedienVideosGenre a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteMedienVideosGenre.getBezeichnung().toLowerCase())) {
                    dao.deleteMedienVideosGenre(a);
                    for (MedienVideos video : videosList) {
                        if ((video.getGenre().toLowerCase()).equals(this.deleteMedienVideosGenre.getBezeichnung().toLowerCase())) {
                            this.medienVideosListe.remove(video);
                            this.filteredMedienVideosListe.remove(video);

                            video.setGenre(this.change_MedienVideosGenre);
                            dao.updateMedienVideos(video);
                            this.medienVideosListe.add(video);
                            this.filteredMedienVideosListe.add(video);
                        }
                    }
                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_MedienVideosGenre.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                MedienVideosGenre neu = new MedienVideosGenre();
                neu.setBezeichnung(this.change_MedienVideosGenre);
                dao.insertMedienVideosGenre(neu);
            }
            // System.out.println(" --- DEBUG: " + this.neuAusgabenkategorie);
            //  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debug ", " --- DEBUG: " + this.neuAusgabenkategorie));

            updateDataVideos();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienVideosSpracheSpeichern() {

        if (!this.neuMedienVideosSprache.isEmpty()) {
            MedienVideosSprache ak = new MedienVideosSprache();
            ak.setBezeichnung(this.neuMedienVideosSprache);
            dao.insertMedienVideosSprache(ak);

            updateDataVideos();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void medienVideosSpracheLoeschen() {

        if (this.deleteMedienVideosSprache != null) {

            List<MedienVideosSprache> akList = dao.getAllMedienVideosSprache();
            List<MedienVideos> videosList = dao.getAllMedienVideos();
            boolean kategorieExist = false;

            for (MedienVideosSprache a : akList) {
                if ((a.getBezeichnung().toLowerCase()).equals(this.deleteMedienVideosSprache.getBezeichnung().toLowerCase())) {
                    dao.deleteMedienVideosSprache(a);
                    for (MedienVideos video : videosList) {
                        if ((video.getSprache().toLowerCase()).equals(this.deleteMedienVideosSprache.getBezeichnung().toLowerCase())) {
                            this.medienVideosListe.remove(video);
                            this.filteredMedienVideosListe.remove(video);
                            video.setSprache(this.change_MedienVideosSprache);
                            dao.updateMedienVideos(video);
                            this.medienVideosListe.add(video);
                            this.filteredMedienVideosListe.add(video);
                        }
                    }
                }
                if ((a.getBezeichnung().toLowerCase()).equals(this.change_MedienVideosSprache.toLowerCase())) {
                    kategorieExist = true;
                }
            }
            if (!kategorieExist) {
                MedienVideosSprache neu = new MedienVideosSprache();
                neu.setBezeichnung(this.change_MedienVideosSprache);
                dao.insertMedienVideosSprache(neu);
            }

            updateDataVideos();
            //         throw new RuntimeException("DEBUG Kategorie: : " + this.neuAusgabenkategorie);

        }

    }

    public void datensatzLoeschenMedienMusik() {
        try {
            if (this.deleteIDMusik != null) {
                gefunden:
                for (MedienMusik a : this.medienMusikListe) {
                    if (a.getId().equals(Integer.parseInt(this.deleteIDMusik))) {
                        dao.deleteMedienMusik(a);
                        this.deletedMedienMusikListe.add(a);
                        this.medienMusikListe.remove(a);
                        this.filteredMedienMusikListe.remove(a);
                        break gefunden;
                    }
                }
                updateDataMusik();
                this.deleteIDMusik = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }

    }

    public void datensatzLoeschenMedienMusik(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (MedienMusik a : this.medienMusikListe) {
                    if (a.getId().equals(id)) {
                        dao.deleteMedienMusik(a);
                        this.deletedMedienMusikListe.add(a);
                        this.medienMusikListe.remove(a);
                        this.filteredMedienMusikListe.remove(a);
                        break gefunden;
                    }
                }
                updateDataMusik();
                this.deleteIDMusik = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }

    }

    public void datensatzLoeschenRueckgangigMachenMusik() {

        if (!this.deletedMedienMusikListe.isEmpty()) {
            for (MedienMusik a : this.deletedMedienMusikListe) {
                this.medienMusikListe.add(a);
                this.filteredMedienMusikListe.add(a);
                a.setDeleted(false);
                dao.updateMedienMusik(a);
            }
            updateDataMusik();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    public void datensatzLoeschenMedienSoftware() {
        try {
            if (this.deleteIDSoftware != null) {
                gefunden:
                for (MedienSoftware a : this.medienSoftwareListe) {
                    if (a.getId().equals(Integer.parseInt(this.deleteIDSoftware))) {
                        dao.deleteMedienSoftware(a);
                        this.deletedMedienSoftwareListe.add(a);
                        this.medienSoftwareListe.remove(a);
                        this.filteredMedienSoftwareListe.remove(a);

                        break gefunden;
                    }
                }
                updateDataSoftware();
                this.deleteIDSoftware = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenMedienSoftware(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (MedienSoftware a : this.medienSoftwareListe) {
                    if (a.getId().equals(id)) {
                        dao.deleteMedienSoftware(a);
                        this.deletedMedienSoftwareListe.add(a);
                        this.medienSoftwareListe.remove(a);
                        this.filteredMedienSoftwareListe.remove(a);

                        break gefunden;
                    }
                }
                updateDataSoftware();
                this.deleteIDSoftware = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenRueckgangigMachenSoftware() {

        if (!this.deletedMedienSoftwareListe.isEmpty()) {
            for (MedienSoftware a : this.deletedMedienSoftwareListe) {
                this.medienSoftwareListe.add(a);
                this.filteredMedienSoftwareListe.add(a);
                a.setDeleted(false);
                dao.updateMedienSoftware(a);
            }
            updateDataSoftware();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    public void datensatzLoeschenMedienVideoclips() {
        try {
            if (this.deleteIDVideoclips != null) {
                gefunden:
                for (MedienVideoclips a : this.medienVideoclipsListe) {
                    if (a.getId().equals(Integer.parseInt(this.deleteIDVideoclips))) {
                        dao.deleteMedienVideoclips(a);
                        this.deletedMedienVideoclipsListe.add(a);
                        this.medienVideoclipsListe.remove(a);
                        this.filteredMedienVideoclipsListe.remove(a);

                        break gefunden;
                    }
                }
                updateDataVideoclips();
                this.deleteIDVideoclips = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }

    }

    public void datensatzLoeschenMedienVideoclips(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (MedienVideoclips a : this.medienVideoclipsListe) {
                    if (a.getId().equals(id)) {
                        dao.deleteMedienVideoclips(a);
                        this.deletedMedienVideoclipsListe.add(a);
                        this.medienVideoclipsListe.remove(a);
                        this.filteredMedienVideoclipsListe.remove(a);

                        break gefunden;
                    }
                }
                updateDataVideoclips();
                this.deleteIDVideoclips = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }

    }

    public void datensatzLoeschenRueckgangigMachenVideoclips() {

        if (!this.deletedMedienVideoclipsListe.isEmpty()) {
            for (MedienVideoclips a : this.deletedMedienVideoclipsListe) {
                this.medienVideoclipsListe.add(a);
                this.filteredMedienVideoclipsListe.add(a);
                a.setDeleted(false);
                dao.updateMedienVideoclips(a);
            }
            updateDataVideoclips();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    public void datensatzLoeschenMedienVideos() {
        try {
            if (this.deleteIDVideos != null) {
                gefunden:
                for (MedienVideos a : this.medienVideosListe) {
                    if (a.getVideos_id().equals(Integer.parseInt(this.deleteIDVideos))) {
                        dao.deleteMedienVideos(a);
                        this.deletedMedienVideosListe.add(a);
                        this.medienVideosListe.remove(a);
                        this.filteredMedienVideosListe.remove(a);

                        break gefunden;
                    }
                }
                updateDataVideos();
                this.deleteIDVideos = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenMedienVideos(Integer id) {
        try {
            if (id != null) {
                gefunden:
                for (MedienVideos a : this.medienVideosListe) {
                    if (a.getVideos_id().equals(id)) {
                        dao.deleteMedienVideos(a);
                        this.deletedMedienVideosListe.add(a);
                        this.medienVideosListe.remove(a);
                        this.filteredMedienVideosListe.remove(a);

                        break gefunden;
                    }
                }
                updateDataVideos();
                this.deleteIDVideos = "";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte Zahl übergeben!", "FEHLER: " + e));
        }
    }

    public void datensatzLoeschenRueckgangigMachenVideos() {

        if (!this.deletedMedienVideosListe.isEmpty()) {
            for (MedienVideos a : this.deletedMedienVideosListe) {
                this.medienVideosListe.add(a);
                this.filteredMedienVideosListe.add(a);
                a.setDeleted(false);
                dao.updateMedienVideos(a);
            }
            updateDataVideos();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));
        }
    }

    /**
     * Methode zum Freimachen des Anhangs nach dem Speichern
     */
    public void flushAnhang() {
        this.anhang = null;
        this.anhangname = "";
        this.anhangtype = "";

    }

    public void onToggleMusik(ToggleEvent e) {
        this.columnListMusik.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void onToggleSoftware(ToggleEvent e) {
        this.columnListSoftware.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void onToggleVideoclips(ToggleEvent e) {
        this.columnListVideoclips.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void onToggleVideos(ToggleEvent e) {
        this.columnListVideos.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
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

    public PieChartModel getChartBarVideosSpracheAnzahl() {
        return chartBarVideosSpracheAnzahl;
    }

    public void setChartBarVideosSpracheAnzahl(PieChartModel chartBarVideosSpracheAnzahl) {
        this.chartBarVideosSpracheAnzahl = chartBarVideosSpracheAnzahl;
    }

    public PieChartModel getChartBarVideosGenreAnzahl() {
        return chartBarVideosGenreAnzahl;
    }

    public void setChartBarVideosGenreAnzahl(PieChartModel chartBarVideosGenreAnzahl) {
        this.chartBarVideosGenreAnzahl = chartBarVideosGenreAnzahl;
    }

    public PieChartModel getChartBarVideoclipsSpracheAnzahl() {
        return chartBarVideoclipsSpracheAnzahl;
    }

    public void setChartBarVideoclipsSpracheAnzahl(PieChartModel chartBarVideoclipsSpracheAnzahl) {
        this.chartBarVideoclipsSpracheAnzahl = chartBarVideoclipsSpracheAnzahl;
    }

    public List<MedienMusik> getMedienMusikListe() {
        return medienMusikListe;
    }

    public void setMedienMusikListe(List<MedienMusik> medienMusikListe) {
        this.medienMusikListe = medienMusikListe;
    }

    public List<MedienMusik> getFilteredMedienMusikListe() {
        return filteredMedienMusikListe;
    }

    public void setFilteredMedienMusikListe(List<MedienMusik> filteredMedienMusikListe) {
        this.filteredMedienMusikListe = filteredMedienMusikListe;
    }

    public List<MedienMusikGenre> getMedienMusikGenreListe() {
        return medienMusikGenreListe;
    }

    public void setMedienMusikGenreListe(List<MedienMusikGenre> medienMusikGenreListe) {
        this.medienMusikGenreListe = medienMusikGenreListe;
    }

    public List<MedienSoftware> getMedienSoftwareListe() {
        return medienSoftwareListe;
    }

    public void setMedienSoftwareListe(List<MedienSoftware> medienSoftwareListe) {
        this.medienSoftwareListe = medienSoftwareListe;
    }

    public List<MedienSoftware> getFilteredMedienSoftwareListe() {
        return filteredMedienSoftwareListe;
    }

    public void setFilteredMedienSoftwareListe(List<MedienSoftware> filteredMedienSoftwareListe) {
        this.filteredMedienSoftwareListe = filteredMedienSoftwareListe;
    }

    public List<MedienSoftwareBetriebssystem> getMedienSoftwareBetriebssystemListe() {
        return medienSoftwareBetriebssystemListe;
    }

    public void setMedienSoftwareBetriebssystemListe(List<MedienSoftwareBetriebssystem> medienSoftwareBetriebssystemListe) {
        this.medienSoftwareBetriebssystemListe = medienSoftwareBetriebssystemListe;
    }

    public List<MedienSoftwareHersteller> getMedienSoftwareHerstellerListe() {
        return medienSoftwareHerstellerListe;
    }

    public void setMedienSoftwareHerstellerListe(List<MedienSoftwareHersteller> medienSoftwareHerstellerListe) {
        this.medienSoftwareHerstellerListe = medienSoftwareHerstellerListe;
    }

    public List<MedienVideoclips> getMedienVideoclipsListe() {
        return medienVideoclipsListe;
    }

    public void setMedienVideoclipsListe(List<MedienVideoclips> medienVideoclipsListe) {
        this.medienVideoclipsListe = medienVideoclipsListe;
    }

    public List<MedienVideoclips> getFilteredMedienVideoclipsListe() {
        return filteredMedienVideoclipsListe;
    }

    public void setFilteredMedienVideoclipsListe(List<MedienVideoclips> filteredMedienVideoclipsListe) {
        this.filteredMedienVideoclipsListe = filteredMedienVideoclipsListe;
    }

    public List<MedienVideoclipsSprache> getMedienVideoclipsSpracheListe() {
        return medienVideoclipsSpracheListe;
    }

    public void setMedienVideoclipsSpracheListe(List<MedienVideoclipsSprache> medienVideoclipsSpracheListe) {
        this.medienVideoclipsSpracheListe = medienVideoclipsSpracheListe;
    }

    public List<MedienVideos> getMedienVideosListe() {
        return medienVideosListe;
    }

    public void setMedienVideosListe(List<MedienVideos> medienVideosListe) {
        this.medienVideosListe = medienVideosListe;
    }

    public List<MedienVideos> getFilteredMedienVideosListe() {
        return filteredMedienVideosListe;
    }

    public void setFilteredMedienVideosListe(List<MedienVideos> filteredMedienVideosListe) {
        this.filteredMedienVideosListe = filteredMedienVideosListe;
    }

    public List<MedienVideosGenre> getMedienVideosGenreListe() {
        return medienVideosGenreListe;
    }

    public void setMedienVideosGenreListe(List<MedienVideosGenre> medienVideosGenreListe) {
        this.medienVideosGenreListe = medienVideosGenreListe;
    }

    public List<MedienVideosSprache> getMedienVideosSpracheListe() {
        return medienVideosSpracheListe;
    }

    public void setMedienVideosSpracheListe(List<MedienVideosSprache> medienVideosSpracheListe) {
        this.medienVideosSpracheListe = medienVideosSpracheListe;
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

    public String getInterpretMusik() {
        return interpretMusik;
    }

    public void setInterpretMusik(String interpretMusik) {
        this.interpretMusik = interpretMusik;
    }

    public String getSongnameMusik() {
        return songnameMusik;
    }

    public void setSongnameMusik(String songnameMusik) {
        this.songnameMusik = songnameMusik;
    }

    public boolean isAltMusik() {
        return altMusik;
    }

    public void setAltMusik(boolean altMusik) {
        this.altMusik = altMusik;
    }

    public Integer getJahrMusik() {
        return jahrMusik;
    }

    public void setJahrMusik(Integer jahrMusik) {
        this.jahrMusik = jahrMusik;
    }

    public MedienMusikGenre getGenreEintragMusik() {
        return genreEintragMusik;
    }

    public void setGenreEintragMusik(MedienMusikGenre genreEintragMusik) {
        this.genreEintragMusik = genreEintragMusik;
    }

    public String getCodeMusik() {
        return codeMusik;
    }

    public void setCodeMusik(String codeMusik) {
        this.codeMusik = codeMusik;
    }

    public String getLinkMusik() {
        return linkMusik;
    }

    public void setLinkMusik(String linkMusik) {
        this.linkMusik = linkMusik;
    }

    public String getBemerkungenMusik() {
        return bemerkungenMusik;
    }

    public void setBemerkungenMusik(String bemerkungenMusik) {
        this.bemerkungenMusik = bemerkungenMusik;
    }

    public String getProgrammnameSoftware() {
        return programmnameSoftware;
    }

    public void setProgrammnameSoftware(String programmnameSoftware) {
        this.programmnameSoftware = programmnameSoftware;
    }

    public MedienSoftwareHersteller getHerstellerEintragSoftware() {
        return herstellerEintragSoftware;
    }

    public void setHerstellerEintragSoftware(MedienSoftwareHersteller herstellerEintragSoftware) {
        this.herstellerEintragSoftware = herstellerEintragSoftware;
    }

    public MedienSoftwareBetriebssystem getBetriebssystemEintragSoftware() {
        return betriebssystemEintragSoftware;
    }

    public void setBetriebssystemEintragSoftware(MedienSoftwareBetriebssystem betriebssystemEintragSoftware) {
        this.betriebssystemEintragSoftware = betriebssystemEintragSoftware;
    }

    public String getSpracheSoftware() {
        return spracheSoftware;
    }

    public void setSpracheSoftware(String spracheSoftware) {
        this.spracheSoftware = spracheSoftware;
    }

    public String getVersionSoftware() {
        return versionSoftware;
    }

    public void setVersionSoftware(String versionSoftware) {
        this.versionSoftware = versionSoftware;
    }

    public String getSonstige_infosSoftware() {
        return sonstige_infosSoftware;
    }

    public void setSonstige_infosSoftware(String sonstige_infosSoftware) {
        this.sonstige_infosSoftware = sonstige_infosSoftware;
    }

    public String getLinkSoftware() {
        return linkSoftware;
    }

    public void setLinkSoftware(String linkSoftware) {
        this.linkSoftware = linkSoftware;
    }

    public String getInterpretVideoclips() {
        return interpretVideoclips;
    }

    public void setInterpretVideoclips(String interpretVideoclips) {
        this.interpretVideoclips = interpretVideoclips;
    }

    public String getTitelVideoclips() {
        return titelVideoclips;
    }

    public void setTitelVideoclips(String titelVideoclips) {
        this.titelVideoclips = titelVideoclips;
    }

    public MedienVideoclipsSprache getSpracheEintragVideoclips() {
        return spracheEintragVideoclips;
    }

    public void setSpracheEintragVideoclips(MedienVideoclipsSprache spracheEintragVideoclips) {
        this.spracheEintragVideoclips = spracheEintragVideoclips;
    }

    public Integer getJahrVideoclips() {
        return jahrVideoclips;
    }

    public void setJahrVideoclips(Integer jahrVideoclips) {
        this.jahrVideoclips = jahrVideoclips;
    }

    public String getLinkVideoclips() {
        return linkVideoclips;
    }

    public void setLinkVideoclips(String linkVideoclips) {
        this.linkVideoclips = linkVideoclips;
    }

    public String getNameVideos() {
        return nameVideos;
    }

    public void setNameVideos(String nameVideos) {
        this.nameVideos = nameVideos;
    }

    public boolean isArd_entertainementVideos() {
        return ard_entertainementVideos;
    }

    public void setArd_entertainementVideos(boolean ard_entertainementVideos) {
        this.ard_entertainementVideos = ard_entertainementVideos;
    }

    public MedienVideosSprache getSpracheEintragVideos() {
        return spracheEintragVideos;
    }

    public void setSpracheEintragVideos(MedienVideosSprache spracheEintragVideos) {
        this.spracheEintragVideos = spracheEintragVideos;
    }

    public boolean isHdVideos() {
        return hdVideos;
    }

    public void setHdVideos(boolean hdVideos) {
        this.hdVideos = hdVideos;
    }

    public MedienVideosGenre getGenreEintragVideos() {
        return genreEintragVideos;
    }

    public void setGenreEintragVideos(MedienVideosGenre genreEintragVideos) {
        this.genreEintragVideos = genreEintragVideos;
    }

    public Integer getDauerVideos() {
        return dauerVideos;
    }

    public void setDauerVideos(Integer dauerVideos) {
        this.dauerVideos = dauerVideos;
    }

    public Integer getJahrVideos() {
        return jahrVideos;
    }

    public void setJahrVideos(Integer jahrVideos) {
        this.jahrVideos = jahrVideos;
    }

    public boolean isSerieVideos() {
        return serieVideos;
    }

    public void setSerieVideos(boolean serieVideos) {
        this.serieVideos = serieVideos;
    }

    public String getLinkVideos() {
        return linkVideos;
    }

    public void setLinkVideos(String linkVideos) {
        this.linkVideos = linkVideos;
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    public String getNeuMedienMusikGenre() {
        return neuMedienMusikGenre;
    }

    public void setNeuMedienMusikGenre(String neuMedienMusikGenre) {
        this.neuMedienMusikGenre = neuMedienMusikGenre;
    }

    public String getNeuMedienSoftwareBetriebssystem() {
        return neuMedienSoftwareBetriebssystem;
    }

    public void setNeuMedienSoftwareBetriebssystem(String neuMedienSoftwareBetriebssystem) {
        this.neuMedienSoftwareBetriebssystem = neuMedienSoftwareBetriebssystem;
    }

    public String getNeuMedienSoftwareHersteller() {
        return neuMedienSoftwareHersteller;
    }

    public void setNeuMedienSoftwareHersteller(String neuMedienSoftwareHersteller) {
        this.neuMedienSoftwareHersteller = neuMedienSoftwareHersteller;
    }

    public String getNeuMedienVideoclipsSprache() {
        return neuMedienVideoclipsSprache;
    }

    public void setNeuMedienVideoclipsSprache(String neuMedienVideoclipsSprache) {
        this.neuMedienVideoclipsSprache = neuMedienVideoclipsSprache;
    }

    public String getNeuMedienVideosGenre() {
        return neuMedienVideosGenre;
    }

    public void setNeuMedienVideosGenre(String neuMedienVideosGenre) {
        this.neuMedienVideosGenre = neuMedienVideosGenre;
    }

    public String getNeuMedienVideosSprache() {
        return neuMedienVideosSprache;
    }

    public void setNeuMedienVideosSprache(String neuMedienVideosSprache) {
        this.neuMedienVideosSprache = neuMedienVideosSprache;
    }

    public String getDeleteIDMusik() {
        return deleteIDMusik;
    }

    public void setDeleteIDMusik(String deleteIDMusik) {
        this.deleteIDMusik = deleteIDMusik;
    }

    public String getDeleteIDSoftware() {
        return deleteIDSoftware;
    }

    public void setDeleteIDSoftware(String deleteIDSoftware) {
        this.deleteIDSoftware = deleteIDSoftware;
    }

    public String getDeleteIDVideoclips() {
        return deleteIDVideoclips;
    }

    public void setDeleteIDVideoclips(String deleteIDVideoclips) {
        this.deleteIDVideoclips = deleteIDVideoclips;
    }

    public String getDeleteIDVideos() {
        return deleteIDVideos;
    }

    public void setDeleteIDVideos(String deleteIDVideos) {
        this.deleteIDVideos = deleteIDVideos;
    }

    public MedienMusikGenre getDeleteMedienMusikGenre() {
        return deleteMedienMusikGenre;
    }

    public void setDeleteMedienMusikGenre(MedienMusikGenre deleteMedienMusikGenre) {
        this.deleteMedienMusikGenre = deleteMedienMusikGenre;
    }

    public MedienSoftwareBetriebssystem getDeleteMedienSoftwareBetriebssystem() {
        return deleteMedienSoftwareBetriebssystem;
    }

    public void setDeleteMedienSoftwareBetriebssystem(MedienSoftwareBetriebssystem deleteMedienSoftwareBetriebssystem) {
        this.deleteMedienSoftwareBetriebssystem = deleteMedienSoftwareBetriebssystem;
    }

    public MedienSoftwareHersteller getDeleteMedienSoftwareHersteller() {
        return deleteMedienSoftwareHersteller;
    }

    public void setDeleteMedienSoftwareHersteller(MedienSoftwareHersteller deleteMedienSoftwareHersteller) {
        this.deleteMedienSoftwareHersteller = deleteMedienSoftwareHersteller;
    }

    public MedienVideoclipsSprache getDeleteMedienVideoclipsSprache() {
        return deleteMedienVideoclipsSprache;
    }

    public void setDeleteMedienVideoclipsSprache(MedienVideoclipsSprache deleteMedienVideoclipsSprache) {
        this.deleteMedienVideoclipsSprache = deleteMedienVideoclipsSprache;
    }

    public MedienVideosGenre getDeleteMedienVideosGenre() {
        return deleteMedienVideosGenre;
    }

    public void setDeleteMedienVideosGenre(MedienVideosGenre deleteMedienVideosGenre) {
        this.deleteMedienVideosGenre = deleteMedienVideosGenre;
    }

    public MedienVideosSprache getDeleteMedienVideosSprache() {
        return deleteMedienVideosSprache;
    }

    public void setDeleteMedienVideosSprache(MedienVideosSprache deleteMedienVideosSprache) {
        this.deleteMedienVideosSprache = deleteMedienVideosSprache;
    }

    public String getChange_MedienMusikGenre() {
        return change_MedienMusikGenre;
    }

    public void setChange_MedienMusikGenre(String change_MedienMusikGenre) {
        this.change_MedienMusikGenre = change_MedienMusikGenre;
    }

    public String getChange_MedienSoftwareBetriebssystem() {
        return change_MedienSoftwareBetriebssystem;
    }

    public void setChange_MedienSoftwareBetriebssystem(String change_MedienSoftwareBetriebssystem) {
        this.change_MedienSoftwareBetriebssystem = change_MedienSoftwareBetriebssystem;
    }

    public String getChange_MedienSoftwareHersteller() {
        return change_MedienSoftwareHersteller;
    }

    public void setChange_MedienSoftwareHersteller(String change_MedienSoftwareHersteller) {
        this.change_MedienSoftwareHersteller = change_MedienSoftwareHersteller;
    }

    public String getChange_MedienVideoclipsSprache() {
        return change_MedienVideoclipsSprache;
    }

    public void setChange_MedienVideoclipsSprache(String change_MedienVideoclipsSprache) {
        this.change_MedienVideoclipsSprache = change_MedienVideoclipsSprache;
    }

    public String getChange_MedienVideosGenre() {
        return change_MedienVideosGenre;
    }

    public void setChange_MedienVideosGenre(String change_MedienVideosGenre) {
        this.change_MedienVideosGenre = change_MedienVideosGenre;
    }

    public String getChange_MedienVideosSprache() {
        return change_MedienVideosSprache;
    }

    public void setChange_MedienVideosSprache(String change_MedienVideosSprache) {
        this.change_MedienVideosSprache = change_MedienVideosSprache;
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

    public DatenbankNotizen getDbnotizEintrag() {
        return dbnotizEintrag;
    }

    public void setDbnotizEintrag(DatenbankNotizen dbnotizEintrag) {
        this.dbnotizEintrag = dbnotizEintrag;
    }

    public String getNotiztext() {
        return notiztext;
    }

    public List<Boolean> getColumnListMusik() {
        return columnListMusik;
    }

    public void setColumnListMusik(List<Boolean> columnListMusik) {
        this.columnListMusik = columnListMusik;
    }

    public List<Boolean> getColumnListSoftware() {
        return columnListSoftware;
    }

    public void setColumnListSoftware(List<Boolean> columnListSoftware) {
        this.columnListSoftware = columnListSoftware;
    }

    public List<Boolean> getColumnListVideoclips() {
        return columnListVideoclips;
    }

    public void setColumnListVideoclips(List<Boolean> columnListVideoclips) {
        this.columnListVideoclips = columnListVideoclips;
    }

    public List<Boolean> getColumnListVideos() {
        return columnListVideos;
    }

    public void setColumnListVideos(List<Boolean> columnListVideos) {
        this.columnListVideos = columnListVideos;
    }

    public void setNotiztext(String notiztext) {
        this.notiztext = notiztext;
    }

    public String getSummeMedienVideos() {
        return summeMedienVideos;
    }

    public void setSummeMedienVideos(String summeMedienVideos) {
        this.summeMedienVideos = summeMedienVideos;
    }

    public String getDatensaetzeAnzahlTextMusik() {
        return datensaetzeAnzahlTextMusik;
    }

    public void setDatensaetzeAnzahlTextMusik(String datensaetzeAnzahlTextMusik) {
        this.datensaetzeAnzahlTextMusik = datensaetzeAnzahlTextMusik;
    }

    public String getDatensaetzeAnzahlTextSoftware() {
        return datensaetzeAnzahlTextSoftware;
    }

    public void setDatensaetzeAnzahlTextSoftware(String datensaetzeAnzahlTextSoftware) {
        this.datensaetzeAnzahlTextSoftware = datensaetzeAnzahlTextSoftware;
    }

    public String getDatensaetzeAnzahlTextVideoclips() {
        return datensaetzeAnzahlTextVideoclips;
    }

    public void setDatensaetzeAnzahlTextVideoclips(String datensaetzeAnzahlTextVideoclips) {
        this.datensaetzeAnzahlTextVideoclips = datensaetzeAnzahlTextVideoclips;
    }

    public String getDatensaetzeAnzahlTextVideos() {
        return datensaetzeAnzahlTextVideos;
    }

    public void setDatensaetzeAnzahlTextVideos(String datensaetzeAnzahlTextVideos) {
        this.datensaetzeAnzahlTextVideos = datensaetzeAnzahlTextVideos;
    }

    public String getNativer_TitelVideoclips() {
        return nativer_TitelVideoclips;
    }

    public void setNativer_TitelVideoclips(String nativer_TitelVideoclips) {
        this.nativer_TitelVideoclips = nativer_TitelVideoclips;
    }

    public String getNativer_TitelVideos() {
        return nativer_TitelVideos;
    }

    public void setNativer_TitelVideos(String nativer_TitelVideos) {
        this.nativer_TitelVideos = nativer_TitelVideos;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.chartBarVideosSpracheAnzahl);
        hash = 97 * hash + Objects.hashCode(this.chartBarVideosGenreAnzahl);
        hash = 97 * hash + Objects.hashCode(this.chartBarVideoclipsSpracheAnzahl);
        hash = 97 * hash + Objects.hashCode(this.medienMusikListe);
        hash = 97 * hash + Objects.hashCode(this.filteredMedienMusikListe);
        hash = 97 * hash + Objects.hashCode(this.medienMusikGenreListe);
        hash = 97 * hash + Objects.hashCode(this.medienSoftwareListe);
        hash = 97 * hash + Objects.hashCode(this.filteredMedienSoftwareListe);
        hash = 97 * hash + Objects.hashCode(this.medienSoftwareBetriebssystemListe);
        hash = 97 * hash + Objects.hashCode(this.medienSoftwareHerstellerListe);
        hash = 97 * hash + Objects.hashCode(this.medienVideoclipsListe);
        hash = 97 * hash + Objects.hashCode(this.filteredMedienVideoclipsListe);
        hash = 97 * hash + Objects.hashCode(this.medienVideoclipsSpracheListe);
        hash = 97 * hash + Objects.hashCode(this.medienVideosListe);
        hash = 97 * hash + Objects.hashCode(this.filteredMedienVideosListe);
        hash = 97 * hash + Objects.hashCode(this.medienVideosGenreListe);
        hash = 97 * hash + Objects.hashCode(this.medienVideosSpracheListe);
        hash = 97 * hash + Objects.hashCode(this.tabellenname);
        hash = 97 * hash + Objects.hashCode(this.baseUrl);
        hash = 97 * hash + Objects.hashCode(this.downloadUrl);
        hash = 97 * hash + Objects.hashCode(this.interpretMusik);
        hash = 97 * hash + Objects.hashCode(this.songnameMusik);
        hash = 97 * hash + (this.altMusik ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.jahrMusik);
        hash = 97 * hash + Objects.hashCode(this.genreEintragMusik);
        hash = 97 * hash + Objects.hashCode(this.codeMusik);
        hash = 97 * hash + Objects.hashCode(this.linkMusik);
        hash = 97 * hash + Objects.hashCode(this.bemerkungenMusik);
        hash = 97 * hash + Objects.hashCode(this.programmnameSoftware);
        hash = 97 * hash + Objects.hashCode(this.herstellerEintragSoftware);
        hash = 97 * hash + Objects.hashCode(this.betriebssystemEintragSoftware);
        hash = 97 * hash + Objects.hashCode(this.spracheSoftware);
        hash = 97 * hash + Objects.hashCode(this.versionSoftware);
        hash = 97 * hash + Objects.hashCode(this.sonstige_infosSoftware);
        hash = 97 * hash + Objects.hashCode(this.linkSoftware);
        hash = 97 * hash + Objects.hashCode(this.interpretVideoclips);
        hash = 97 * hash + Objects.hashCode(this.titelVideoclips);
        hash = 97 * hash + Objects.hashCode(this.spracheEintragVideoclips);
        hash = 97 * hash + Objects.hashCode(this.jahrVideoclips);
        hash = 97 * hash + Objects.hashCode(this.linkVideoclips);
        hash = 97 * hash + Objects.hashCode(this.nameVideos);
        hash = 97 * hash + (this.ard_entertainementVideos ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.spracheEintragVideos);
        hash = 97 * hash + (this.hdVideos ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.genreEintragVideos);
        hash = 97 * hash + Objects.hashCode(this.dauerVideos);
        hash = 97 * hash + Objects.hashCode(this.jahrVideos);
        hash = 97 * hash + (this.serieVideos ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.linkVideos);
        hash = 97 * hash + Objects.hashCode(this.dao);
        hash = 97 * hash + Objects.hashCode(this.neuMedienMusikGenre);
        hash = 97 * hash + Objects.hashCode(this.neuMedienSoftwareBetriebssystem);
        hash = 97 * hash + Objects.hashCode(this.neuMedienSoftwareHersteller);
        hash = 97 * hash + Objects.hashCode(this.neuMedienVideoclipsSprache);
        hash = 97 * hash + Objects.hashCode(this.neuMedienVideosGenre);
        hash = 97 * hash + Objects.hashCode(this.neuMedienVideosSprache);
        hash = 97 * hash + Objects.hashCode(this.deleteIDMusik);
        hash = 97 * hash + Objects.hashCode(this.deleteIDSoftware);
        hash = 97 * hash + Objects.hashCode(this.deleteIDVideoclips);
        hash = 97 * hash + Objects.hashCode(this.deleteIDVideos);
        hash = 97 * hash + Objects.hashCode(this.deleteMedienMusikGenre);
        hash = 97 * hash + Objects.hashCode(this.deleteMedienSoftwareBetriebssystem);
        hash = 97 * hash + Objects.hashCode(this.deleteMedienSoftwareHersteller);
        hash = 97 * hash + Objects.hashCode(this.deleteMedienVideoclipsSprache);
        hash = 97 * hash + Objects.hashCode(this.deleteMedienVideosGenre);
        hash = 97 * hash + Objects.hashCode(this.deleteMedienVideosSprache);
        hash = 97 * hash + Objects.hashCode(this.change_MedienMusikGenre);
        hash = 97 * hash + Objects.hashCode(this.change_MedienSoftwareBetriebssystem);
        hash = 97 * hash + Objects.hashCode(this.change_MedienSoftwareHersteller);
        hash = 97 * hash + Objects.hashCode(this.change_MedienVideoclipsSprache);
        hash = 97 * hash + Objects.hashCode(this.change_MedienVideosGenre);
        hash = 97 * hash + Objects.hashCode(this.change_MedienVideosSprache);
        hash = 97 * hash + Arrays.hashCode(this.anhang);
        hash = 97 * hash + Objects.hashCode(this.anhangID);
        hash = 97 * hash + Objects.hashCode(this.anhangname);
        hash = 97 * hash + Objects.hashCode(this.anhangtype);
        hash = 97 * hash + Objects.hashCode(this.rownumbers);
        hash = 97 * hash + Objects.hashCode(this.dbnotizEintrag);
        hash = 97 * hash + Objects.hashCode(this.notiztext);
        hash = 97 * hash + Objects.hashCode(this.summeMedienVideos);
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
        final MedienController other = (MedienController) obj;
        if (this.altMusik != other.altMusik) {
            return false;
        }
        if (this.ard_entertainementVideos != other.ard_entertainementVideos) {
            return false;
        }
        if (this.hdVideos != other.hdVideos) {
            return false;
        }
        if (this.serieVideos != other.serieVideos) {
            return false;
        }
        if (!Objects.equals(this.tabellenname, other.tabellenname)) {
            return false;
        }
        if (!Objects.equals(this.baseUrl, other.baseUrl)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrl, other.downloadUrl)) {
            return false;
        }
        if (!Objects.equals(this.interpretMusik, other.interpretMusik)) {
            return false;
        }
        if (!Objects.equals(this.songnameMusik, other.songnameMusik)) {
            return false;
        }
        if (!Objects.equals(this.codeMusik, other.codeMusik)) {
            return false;
        }
        if (!Objects.equals(this.linkMusik, other.linkMusik)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungenMusik, other.bemerkungenMusik)) {
            return false;
        }
        if (!Objects.equals(this.programmnameSoftware, other.programmnameSoftware)) {
            return false;
        }
        if (!Objects.equals(this.spracheSoftware, other.spracheSoftware)) {
            return false;
        }
        if (!Objects.equals(this.versionSoftware, other.versionSoftware)) {
            return false;
        }
        if (!Objects.equals(this.sonstige_infosSoftware, other.sonstige_infosSoftware)) {
            return false;
        }
        if (!Objects.equals(this.linkSoftware, other.linkSoftware)) {
            return false;
        }
        if (!Objects.equals(this.interpretVideoclips, other.interpretVideoclips)) {
            return false;
        }
        if (!Objects.equals(this.titelVideoclips, other.titelVideoclips)) {
            return false;
        }
        if (!Objects.equals(this.linkVideoclips, other.linkVideoclips)) {
            return false;
        }
        if (!Objects.equals(this.nameVideos, other.nameVideos)) {
            return false;
        }
        if (!Objects.equals(this.linkVideos, other.linkVideos)) {
            return false;
        }
        if (!Objects.equals(this.neuMedienMusikGenre, other.neuMedienMusikGenre)) {
            return false;
        }
        if (!Objects.equals(this.neuMedienSoftwareBetriebssystem, other.neuMedienSoftwareBetriebssystem)) {
            return false;
        }
        if (!Objects.equals(this.neuMedienSoftwareHersteller, other.neuMedienSoftwareHersteller)) {
            return false;
        }
        if (!Objects.equals(this.neuMedienVideoclipsSprache, other.neuMedienVideoclipsSprache)) {
            return false;
        }
        if (!Objects.equals(this.neuMedienVideosGenre, other.neuMedienVideosGenre)) {
            return false;
        }
        if (!Objects.equals(this.neuMedienVideosSprache, other.neuMedienVideosSprache)) {
            return false;
        }
        if (!Objects.equals(this.deleteIDMusik, other.deleteIDMusik)) {
            return false;
        }
        if (!Objects.equals(this.deleteIDSoftware, other.deleteIDSoftware)) {
            return false;
        }
        if (!Objects.equals(this.deleteIDVideoclips, other.deleteIDVideoclips)) {
            return false;
        }
        if (!Objects.equals(this.deleteIDVideos, other.deleteIDVideos)) {
            return false;
        }
        if (!Objects.equals(this.change_MedienMusikGenre, other.change_MedienMusikGenre)) {
            return false;
        }
        if (!Objects.equals(this.change_MedienSoftwareBetriebssystem, other.change_MedienSoftwareBetriebssystem)) {
            return false;
        }
        if (!Objects.equals(this.change_MedienSoftwareHersteller, other.change_MedienSoftwareHersteller)) {
            return false;
        }
        if (!Objects.equals(this.change_MedienVideoclipsSprache, other.change_MedienVideoclipsSprache)) {
            return false;
        }
        if (!Objects.equals(this.change_MedienVideosGenre, other.change_MedienVideosGenre)) {
            return false;
        }
        if (!Objects.equals(this.change_MedienVideosSprache, other.change_MedienVideosSprache)) {
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
        if (!Objects.equals(this.notiztext, other.notiztext)) {
            return false;
        }
        if (!Objects.equals(this.summeMedienVideos, other.summeMedienVideos)) {
            return false;
        }
        if (!Objects.equals(this.chartBarVideosSpracheAnzahl, other.chartBarVideosSpracheAnzahl)) {
            return false;
        }
        if (!Objects.equals(this.chartBarVideosGenreAnzahl, other.chartBarVideosGenreAnzahl)) {
            return false;
        }
        if (!Objects.equals(this.chartBarVideoclipsSpracheAnzahl, other.chartBarVideoclipsSpracheAnzahl)) {
            return false;
        }
        if (!Objects.equals(this.medienMusikListe, other.medienMusikListe)) {
            return false;
        }
        if (!Objects.equals(this.filteredMedienMusikListe, other.filteredMedienMusikListe)) {
            return false;
        }
        if (!Objects.equals(this.medienMusikGenreListe, other.medienMusikGenreListe)) {
            return false;
        }
        if (!Objects.equals(this.medienSoftwareListe, other.medienSoftwareListe)) {
            return false;
        }
        if (!Objects.equals(this.filteredMedienSoftwareListe, other.filteredMedienSoftwareListe)) {
            return false;
        }
        if (!Objects.equals(this.medienSoftwareBetriebssystemListe, other.medienSoftwareBetriebssystemListe)) {
            return false;
        }
        if (!Objects.equals(this.medienSoftwareHerstellerListe, other.medienSoftwareHerstellerListe)) {
            return false;
        }
        if (!Objects.equals(this.medienVideoclipsListe, other.medienVideoclipsListe)) {
            return false;
        }
        if (!Objects.equals(this.filteredMedienVideoclipsListe, other.filteredMedienVideoclipsListe)) {
            return false;
        }
        if (!Objects.equals(this.medienVideoclipsSpracheListe, other.medienVideoclipsSpracheListe)) {
            return false;
        }
        if (!Objects.equals(this.medienVideosListe, other.medienVideosListe)) {
            return false;
        }
        if (!Objects.equals(this.filteredMedienVideosListe, other.filteredMedienVideosListe)) {
            return false;
        }
        if (!Objects.equals(this.medienVideosGenreListe, other.medienVideosGenreListe)) {
            return false;
        }
        if (!Objects.equals(this.medienVideosSpracheListe, other.medienVideosSpracheListe)) {
            return false;
        }
        if (!Objects.equals(this.jahrMusik, other.jahrMusik)) {
            return false;
        }
        if (!Objects.equals(this.genreEintragMusik, other.genreEintragMusik)) {
            return false;
        }
        if (!Objects.equals(this.herstellerEintragSoftware, other.herstellerEintragSoftware)) {
            return false;
        }
        if (!Objects.equals(this.betriebssystemEintragSoftware, other.betriebssystemEintragSoftware)) {
            return false;
        }
        if (!Objects.equals(this.spracheEintragVideoclips, other.spracheEintragVideoclips)) {
            return false;
        }
        if (!Objects.equals(this.jahrVideoclips, other.jahrVideoclips)) {
            return false;
        }
        if (!Objects.equals(this.spracheEintragVideos, other.spracheEintragVideos)) {
            return false;
        }
        if (!Objects.equals(this.genreEintragVideos, other.genreEintragVideos)) {
            return false;
        }
        if (!Objects.equals(this.dauerVideos, other.dauerVideos)) {
            return false;
        }
        if (!Objects.equals(this.jahrVideos, other.jahrVideos)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Objects.equals(this.deleteMedienMusikGenre, other.deleteMedienMusikGenre)) {
            return false;
        }
        if (!Objects.equals(this.deleteMedienSoftwareBetriebssystem, other.deleteMedienSoftwareBetriebssystem)) {
            return false;
        }
        if (!Objects.equals(this.deleteMedienSoftwareHersteller, other.deleteMedienSoftwareHersteller)) {
            return false;
        }
        if (!Objects.equals(this.deleteMedienVideoclipsSprache, other.deleteMedienVideoclipsSprache)) {
            return false;
        }
        if (!Objects.equals(this.deleteMedienVideosGenre, other.deleteMedienVideosGenre)) {
            return false;
        }
        if (!Objects.equals(this.deleteMedienVideosSprache, other.deleteMedienVideosSprache)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Objects.equals(this.dbnotizEintrag, other.dbnotizEintrag)) {
            return false;
        }
        return true;
    }

}
