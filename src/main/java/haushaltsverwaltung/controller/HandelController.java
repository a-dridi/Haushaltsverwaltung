/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.db.HibernateUtil;
import haushaltsverwaltung.model.DatenbankNotizen;
import haushaltsverwaltung.model.Handel;
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
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
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

/**
 * Managment of trades or transactions / Verwaltung von Austauschtransaktionen
 * 
 * @author A.Dridi
 */
@Named(value = "handelController")
@ViewScoped
public class HandelController implements Serializable {

    private List<Handel> handelList = new ArrayList<>();
    private List<Handel> filteredHandelList;
    //Cache Liste um gelöschte Datensätze rückgängig zu machen (nur innerhalb einer Session)
    private List<Handel> deletedHandelList = new ArrayList<>();
    private List<String> handelspartnerList;
    private List<String> filteredHandelspartnerList;
    private String tabellenname = "Handel";

    //immer Ändern - OHNE / (SLASH) AM ENDE:
    private String baseUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/dav/files/haushaltsverwaltung/Handel";
    private String downloadUrl = "https://MY-CLOUD-SERVER.TLD/haushaltsverwaltung-cloud/remote.php/webdav/Handel";
    private final String cloudUsername = "CLOUDUSERNAME";
    private final String cloudPassword = "CLOUDPASSWORD";

    private Date datum;
    private String bezeichnung;
    private String handelspartner;
    private String gegenleistung;
    private String ort;
    private String bemerkung;

    private DAO dao;
    private Integer rownumbers = 15;
    private String anhangname;
    private String anhangtype;
    private byte[] anhang;
    private DatenbankNotizen dbnotizEintrag = null;
    private String notiztext;
    private String datensaetzeAnzahlText;
    private String deleteID;
    private String anhangID;

    /**
     * Creates a new instance of HandelController
     */
    public HandelController() {
        this.dao = new DAO();

    }

    @PostConstruct
    private void init() {
        List<DatenbankNotizen> notizList = dao.getDatenbankNotiz(this.tabellenname);
        if (notizList != null && !notizList.isEmpty()) {
            this.notiztext = notizList.get(0).getNotiztext();
            this.dbnotizEintrag = notizList.get(0);
        }

        this.handelList = dao.getAllHandel();
        this.filteredHandelList = new ArrayList<>(this.handelList);

        flushAnhang();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.handelList.size() + " Datensaetze in der DB gespeichert");
        createHandelspartnerList();

    }

    public void createHandelspartnerList() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        String sqlstring = "Select distinct handelspartner FROM Handel where deleted=false";
        Query qu = s.createQuery(sqlstring);
        this.handelspartnerList = qu.list();
        this.filteredHandelspartnerList = new ArrayList<>(this.handelspartnerList);
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
            List<Handel> liste = new ArrayList<>(this.handelList);
            gefunden:
            for (Handel a : liste) {
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
                        dao.updateHandel(a);
                        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Anhang mit der ID" + a.getId() + " wurde aktualisiert ", " "));
                    } else {
                        //Anhang loeschen und nicht ersetzen
                        DeleteMethod m = new DeleteMethod(this.baseUrl + "/" + ((a.getId()) + "." + dateiext));
                        client.executeMethod(m);
                        a.setAnhang(false);
                        a.setAnhangname("");
                        a.setAnhangtype("");
                        a.setAnhangpfad("");
                        dao.updateHandel(a);
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
     * Editor für Zeile aufrufen
     */
    public void editRow(CellEditEvent event) {
        try {
            DataTable tabelle = (DataTable) event.getSource();
            String spaltenname = event.getColumn().getHeaderText();
            this.dao = new DAO();

            Handel a = (this.dao.getSingleHandel((Integer) tabelle.getRowKey())).get(0);
            //   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "zeile rowindex: ", "" + event.getNewValue()));

            if (spaltenname.equals("Datum")) {
                if (event.getNewValue() != null) {
                    a.setDatum((Date) event.getNewValue());
                }
            }
            if (spaltenname.equals("Bezeichnung")) {
                a.setBezeichnung((String) event.getNewValue());
            }
            if (spaltenname.equals("Handelspartner")) {
                a.setHandelspartner((String) event.getNewValue());
            }
            if (spaltenname.equals("Gegenleistung")) {
                a.setGegenleistung((String) event.getNewValue());
            }
            if (spaltenname.equals("Ort")) {
                a.setOrt((String) event.getNewValue());
            }
            if (spaltenname.equals("Bemerkung")) {
                a.setBemerkung((String) event.getNewValue());
            }
            // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "old value: ", "" + event.getNewValue()));
            dao.updateHandel(a);
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
        HttpClient client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(this.cloudUsername, this.cloudPassword);
        client.getState().setCredentials(AuthScope.ANY, creds);
        GetMethod method = new GetMethod(this.downloadUrl);
        try {
            client.executeMethod(method);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler bei Verb. mit Cloud: ", "" + e));
        }
        this.handelList = dao.getAllHandel();
        this.filteredHandelList = new ArrayList<>(this.handelList);
        flushAnhang();
        createHandelspartnerList();
        this.datensaetzeAnzahlText = ("Insgesamt: " + this.handelList.size() + " Datensaetze in der DB gespeichert");

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

    public void speichern() {
        Handel handel = new Handel();
        handel.setDeleted(false);
        if (this.bezeichnung != null) {
            handel.setBezeichnung(bezeichnung);
        }

        if (this.handelspartner != null) {
            handel.setHandelspartner(handelspartner);
            if (!this.handelspartner.isEmpty()) {
                handelspartnerList.add(this.handelspartner);
                filteredHandelspartnerList.add(this.handelspartner);
            }
        }

        if (this.datum != null) {
            handel.setDatum(this.datum);
        } else {
            handel.setDatum(new Date());
        }

        if (this.gegenleistung != null) {
            handel.setGegenleistung(this.gegenleistung);
        }

        if (this.ort != null) {
            handel.setOrt(this.ort);
        }

        if (this.bemerkung != null) {
            handel.setBemerkung(this.bemerkung);
        }

        if (this.anhang != null && !this.anhangname.isEmpty()) {
            handel.setAnhang(true);
            //IMMER VOR DEM INSERT BEFEHL
            //Neuen Datensatz direkt auch in der Tabelle ohne neuladen der Seite anzeigen
            this.handelList.add(handel);
            this.filteredHandelList.add(handel);

            dao.insertHandel(handel);

            List<Handel> ausgabenListe = new ArrayList<>(this.handelList);
            int letzteNr = ausgabenListe.size() - 1;
            if (letzteNr >= 0) {
                int neueID = ausgabenListe.get(letzteNr).getId();
                try {
                    Handel a = ausgabenListe.get(letzteNr);
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

                    dao.updateHandel(a);

                } catch (HttpException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anhang: Upload Fehler ", "" + ex));
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: ", "" + ex));
                }
                updateData();

            }

        } else {
            handel.setAnhang(false);
            this.handelList.add(handel);
            this.filteredHandelList.add(handel);

            dao.insertHandel(handel);
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

    public void datensatzLoeschenRueckgangigMachen() {

        if (!this.deletedHandelList.isEmpty()) {
            for (Handel a : this.deletedHandelList) {
                a.setDeleted(false);
                this.handelList.add(a);
                this.filteredHandelList.add(a);
                dao.updateHandel(a);
            }
            updateData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gelöschte Datensätze wurden wiederhergestellt", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler: Cache ist leer!", "Bitte manuell den Wert der Spalte delete auf false ändern!"));

        }
        //  return "ausgaben.xhtml";

    }

    public void datensatzLoeschen() {
        try {
            if (this.deleteID != null) {
                gefunden:
                for (Handel a : this.handelList) {
                    if (a.getId().equals(Integer.parseInt(this.deleteID))) {
                        this.deletedHandelList.add(a);
                        dao.deleteHandel(a);
                        this.handelList.remove(a);
                        this.filteredHandelList.remove(a);
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
                for (Handel a : this.handelList) {
                    if (a.getId().equals(id)) {
                        this.deletedHandelList.add(a);
                        dao.deleteHandel(a);
                        this.handelList.remove(a);
                        this.filteredHandelList.remove(a);
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

    //BIETE DIESE IMMER ÜBERPRÜFEN:
    public void scrollTop() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("ueberschriftPanel");
        PrimeFaces.current().scrollTo("ueberschriftPanel");

    }

    public void scrollTabelle() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("listenForm:tabelleausgabenPanel");
        PrimeFaces.current().scrollTo("listenForm:tabelleausgabenPanel");

    }

    public void scrollHinzufuegen() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("hinzufuegenForm:neuenDatensatzFormular");
        PrimeFaces.current().scrollTo("hinzufuegenForm:neuenDatensatzFormular");
    }

    public void scrollHandelspartnerList() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("handelspartnerForm:handelspartnerPanel");
        PrimeFaces.current().scrollTo("handelspartnerForm:handelspartnerPanel");

    }

    public void scrollAnhangEdit() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.scrollTo("neuAnhangForm:AnhangEditPanel");
        PrimeFaces.current().scrollTo("neuAnhangForm:AnhangEditPanel");
    }

    public List<Handel> getHandelList() {
        return handelList;
    }

    public void setHandelList(List<Handel> handelList) {
        this.handelList = handelList;
    }

    public List<Handel> getFilteredHandelList() {
        return filteredHandelList;
    }

    public void setFilteredHandelList(List<Handel> filteredHandelList) {
        this.filteredHandelList = filteredHandelList;
    }

    public List<Handel> getDeletedHandelList() {
        return deletedHandelList;
    }

    public void setDeletedHandelList(List<Handel> deletedHandelList) {
        this.deletedHandelList = deletedHandelList;
    }

    public List<String> getHandelspartnerList() {
        return handelspartnerList;
    }

    public void setHandelspartnerList(List<String> handelspartnerList) {
        this.handelspartnerList = handelspartnerList;
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

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getHandelspartner() {
        return handelspartner;
    }

    public void setHandelspartner(String handelspartner) {
        this.handelspartner = handelspartner;
    }

    public String getGegenleistung() {
        return gegenleistung;
    }

    public void setGegenleistung(String gegenleistung) {
        this.gegenleistung = gegenleistung;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
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

    public byte[] getAnhang() {
        return anhang;
    }

    public void setAnhang(byte[] anhang) {
        this.anhang = anhang;
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

    public String getDatensaetzeAnzahlText() {
        return datensaetzeAnzahlText;
    }

    public void setDatensaetzeAnzahlText(String datensaetzeAnzahlText) {
        this.datensaetzeAnzahlText = datensaetzeAnzahlText;
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

    public List<String> getFilteredHandelspartnerList() {
        return filteredHandelspartnerList;
    }

    public void setFilteredHandelspartnerList(List<String> filteredHandelspartnerList) {
        this.filteredHandelspartnerList = filteredHandelspartnerList;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.handelList);
        hash = 67 * hash + Objects.hashCode(this.filteredHandelList);
        hash = 67 * hash + Objects.hashCode(this.deletedHandelList);
        hash = 67 * hash + Objects.hashCode(this.handelspartnerList);
        hash = 67 * hash + Objects.hashCode(this.tabellenname);
        hash = 67 * hash + Objects.hashCode(this.baseUrl);
        hash = 67 * hash + Objects.hashCode(this.downloadUrl);
        hash = 67 * hash + Objects.hashCode(this.datum);
        hash = 67 * hash + Objects.hashCode(this.bezeichnung);
        hash = 67 * hash + Objects.hashCode(this.handelspartner);
        hash = 67 * hash + Objects.hashCode(this.gegenleistung);
        hash = 67 * hash + Objects.hashCode(this.ort);
        hash = 67 * hash + Objects.hashCode(this.bemerkung);
        hash = 67 * hash + Objects.hashCode(this.dao);
        hash = 67 * hash + Objects.hashCode(this.rownumbers);
        hash = 67 * hash + Objects.hashCode(this.anhangname);
        hash = 67 * hash + Objects.hashCode(this.anhangtype);
        hash = 67 * hash + Arrays.hashCode(this.anhang);
        hash = 67 * hash + Objects.hashCode(this.dbnotizEintrag);
        hash = 67 * hash + Objects.hashCode(this.notiztext);
        hash = 67 * hash + Objects.hashCode(this.datensaetzeAnzahlText);
        hash = 67 * hash + Objects.hashCode(this.deleteID);
        hash = 67 * hash + Objects.hashCode(this.anhangID);
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
        final HandelController other = (HandelController) obj;
        if (!Objects.equals(this.tabellenname, other.tabellenname)) {
            return false;
        }
        if (!Objects.equals(this.baseUrl, other.baseUrl)) {
            return false;
        }
        if (!Objects.equals(this.downloadUrl, other.downloadUrl)) {
            return false;
        }
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.handelspartner, other.handelspartner)) {
            return false;
        }
        if (!Objects.equals(this.gegenleistung, other.gegenleistung)) {
            return false;
        }
        if (!Objects.equals(this.ort, other.ort)) {
            return false;
        }
        if (!Objects.equals(this.bemerkung, other.bemerkung)) {
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
        if (!Objects.equals(this.datensaetzeAnzahlText, other.datensaetzeAnzahlText)) {
            return false;
        }
        if (!Objects.equals(this.deleteID, other.deleteID)) {
            return false;
        }
        if (!Objects.equals(this.anhangID, other.anhangID)) {
            return false;
        }
        if (!Objects.equals(this.handelList, other.handelList)) {
            return false;
        }
        if (!Objects.equals(this.filteredHandelList, other.filteredHandelList)) {
            return false;
        }
        if (!Objects.equals(this.deletedHandelList, other.deletedHandelList)) {
            return false;
        }
        if (!Objects.equals(this.handelspartnerList, other.handelspartnerList)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (!Objects.equals(this.dao, other.dao)) {
            return false;
        }
        if (!Objects.equals(this.rownumbers, other.rownumbers)) {
            return false;
        }
        if (!Arrays.equals(this.anhang, other.anhang)) {
            return false;
        }
        if (!Objects.equals(this.dbnotizEintrag, other.dbnotizEintrag)) {
            return false;
        }
        return true;
    }

}
