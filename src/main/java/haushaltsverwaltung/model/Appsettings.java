/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
//import org.hibernate.annotations.Check;

/**
 * Appsettings und Applikationseinstellungen und Infos
 *
 * @author
 */
@Entity
//Passwort hat eine Länge von min. 6 Zeichen
//@Check(constraints = "character_length(passwort) >= 6")
@SequenceGenerator(name = "appsettings_seq", sequenceName = "appsettings_id_seq", allocationSize = 1)
public class Appsettings implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "appsettings_seq")
    @Id
    @Column(name = "appssetings_id")
    private Integer appssetings_id;

    @Column(name = "username")
    private String username;

    //Passwort min. 6 Zeichen´
    @Column(name = "password")
    private String password;

    /*Ausgabenaufteilung */
    //Name von Person 1
    @Column(name = "person1name")
    private String person1name;
    //Aufteilungschlussel in Prozent
    private Integer person1aufteilung;

    //Name von Person 2
    @Column(name = "person2name")
    private String person2name;
    private Integer person2aufteilung;

    public Integer getAppssetings_id() {
        return appssetings_id;
    }

    public void setAppssetings_id(Integer appssetings_id) {
        this.appssetings_id = appssetings_id;
    }

    public Appsettings() {
    }

    public String getUsername() {
        return username;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.appssetings_id);
        hash = 97 * hash + Objects.hashCode(this.username);
        hash = 97 * hash + Objects.hashCode(this.password);
        hash = 97 * hash + Objects.hashCode(this.person1name);
        hash = 97 * hash + Objects.hashCode(this.person1aufteilung);
        hash = 97 * hash + Objects.hashCode(this.person2name);
        hash = 97 * hash + Objects.hashCode(this.person2aufteilung);
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
        final Appsettings other = (Appsettings) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        return true;
    }

}
