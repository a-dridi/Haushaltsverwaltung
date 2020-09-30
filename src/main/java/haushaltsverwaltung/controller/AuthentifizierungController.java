/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.Benutzer;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Führt das Login durch. Authentifikation Es wird die Email-Addresse
 * (Benutzername) und das Passwort von der DB abgefragt.
 *
 * @author a.dridi
 */
@Named(value = "authentifizierungController")
@SessionScoped
/**
 * Controller für das Einloggen von Benutzern
 */
public class AuthentifizierungController implements Serializable {

    @Inject
    private BenutzerController benutzerCon;
    private String username = "APPLICATION_USERNAME"; //Benutzername vom einzien Benutzer, der sich einloggen kann.
    private String password = ""; //DO NOT EDIT THIS - Durch Benutzer eingegebenes Passwort
    private String echtesPasswort = "APPLICATION_PASSWORD"; //EDIT THIS - Das Passwort für den einzigen Benutzer, der sich einloggen kann.
    private String viewVersion = "width=device-width, initial-scale=1";

    /**
     *
     */
    public AuthentifizierungController() {

    }

    public void showDesktopVersion() throws IOException {
        viewVersion = "";
        refreshSite();
    }

    public void showMobileVersion() throws IOException {
        viewVersion = "width=device-width, initial-scale=1";
        refreshSite();
    }

    public void refreshSite() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    /**
     * @PostConstruct public void init() { System.out.println("------
     * Initialsierung von BenutzerController: " + benutzerCon); }
     *
     */
    public String getUsername() {
        return username;
    }

    public BenutzerController getBenutzerCon() {
        return benutzerCon;
    }

    public void setBenutzerCon(BenutzerController benutzerCon) {
        this.benutzerCon = benutzerCon;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEchtesPasswort() {
        return echtesPasswort;
    }

    public void setEchtesPasswort(String echtesPasswort) {
        this.echtesPasswort = echtesPasswort;
    }

    public String getViewVersion() {
        return viewVersion;
    }

    public void setViewVersion(String viewVersion) {
        this.viewVersion = viewVersion;
    }

    /**
     * Hinweis das die Session abgelaufen ist.
     */
    public void onIdle() {
        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "SIE WURDEN ABGEMELDET!", "Aufgrund Inaktivität müssen Sie sich nocheinmal anmelden. Siehe Menüpunkt: Startseite"));
    }

    /**
     * Überprüfen ob eingegebener Benutzername und Passwort existieren mittels
     * DB-Abfrage. Erfolgs oder Fehlermeldungen werden über FacesMessages
     * geliefert.
     */
    public void login() {

        DAO dao = new DAO();

        if (this.echtesPasswort.equals(this.password)) {
            //Überprüfung ob  Passwort in der DB existieren.

            //Benutzer als eingeloggt setzten und Meldung ausgeben.
            Benutzer standardbenutzer = new Benutzer();
            standardbenutzer.setUsername(this.username);
            standardbenutzer.setPassword(this.password);
            benutzerCon.setBenutzer(standardbenutzer);
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put("user", username);

            /*
                        FacesMessage m = new FacesMessage("Das Einloggen war erfolgreich!");
            FacesContext.getCurrentInstance().addMessage(null, m);
             */
            HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(false);

            //Session für Benutzer nach 3 Stunden Inaktivität beenden
            s.setMaxInactiveInterval(10800);

            return;
        } else {
            //Benutzer existiert nicht in DB - Fehlermeldung ausgeben
            FacesMessage m = new FacesMessage("Das Einloggen ist fehlgeschlagen! Bitte Eingaben überprüfen!");
            m.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, m);
            username = password = "";

        }

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.benutzerCon);
        hash = 97 * hash + Objects.hashCode(this.username);
        hash = 97 * hash + Objects.hashCode(this.password);
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
        final AuthentifizierungController other = (AuthentifizierungController) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.benutzerCon, other.benutzerCon)) {
            return false;
        }
        return true;
    }

}
