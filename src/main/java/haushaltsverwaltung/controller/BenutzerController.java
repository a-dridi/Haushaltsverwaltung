/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.controller;

import haushaltsverwaltung.db.DAO;
import haushaltsverwaltung.model.Benutzer;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Sessionverwaltung für Benutzer und stellt Methoden zur Verfügung die dem
 * eingeloggten Benutzer betreffen
 *
 * Hier befindet sich die Logout-Methode und eine Methode zum Aktualisieren des
 * Benutzerprofils
 *
 * @author A.Dridi
 */
@Named(value = "benutzerController")
@SessionScoped
public class BenutzerController implements Serializable {

    private Benutzer benutzer = null;

    private boolean newsletter = false;

    /**
     * Creates a new instance of BenutzerController
     */
    public BenutzerController() {
        DAO dao = new DAO();
    }

    @PostConstruct
    private void init() {
        DAO dao = new DAO();

    }

    /**
     * Ausloggen des Benutzers. Aktualisieren der Benutzerdaten Wird aufgerufen,
     * wenn der Logout-Button vom Benutzer getätigt wird. ODER: Beim Menüpunkt
     * Profil
     *
     * @return String Retourniert Loginseite als String
     */
    public String logout() {
        benutzer = null;
        HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        // Session holen und für ungültig erklären
        s.invalidate();
        return "index.xhtml";
    }

    /**
     * Prüft ob ein gültiges Login besteht.
     *
     * @return true wenn ein Benutzer eingeloggt ist, sonst false
     *
     */
    public boolean isLoggedIn() {
        return benutzer != null;

    }

   
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.benutzer);
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
        final BenutzerController other = (BenutzerController) obj;
        if (!Objects.equals(this.benutzer, other.benutzer)) {
            return false;
        }
        return true;
    }

    public Benutzer getBenutzer() {
        return benutzer;
    }

    public void setBenutzer(Benutzer benutzer) {
        this.benutzer = benutzer;
    }

    public boolean isNewsletter() {
        return newsletter;
    }

    public void setNewsletter(boolean newsletter) {
        this.newsletter = newsletter;
    }

  
}
