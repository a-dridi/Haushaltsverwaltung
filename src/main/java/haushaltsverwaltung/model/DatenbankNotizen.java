/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Befindet sich auf Kryptowaehrungen (Globaler Abschnitt
 *
 * @author
 */
@Entity
@SequenceGenerator(name = "datenbankNotizen_seq", sequenceName = "notiz_id_seq", allocationSize = 1)
public class DatenbankNotizen implements Serializable {

    @GeneratedValue(generator = "datenbankNotizen_seq")
    @Id
    private Integer id;
    private String tabelle;
    @Column(length = 90000)
    private String notiztext;

    //Auto. vom Programm zu gewiesen
    private Date datum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotiztext() {
        return notiztext;
    }

    public void setNotiztext(String notiztext) {
        this.notiztext = notiztext;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getTabelle() {
        return tabelle;
    }

    public void setTabelle(String tabelle) {
        this.tabelle = tabelle;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.tabelle);
        hash = 97 * hash + Objects.hashCode(this.notiztext);
        hash = 97 * hash + Objects.hashCode(this.datum);
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
        final DatenbankNotizen other = (DatenbankNotizen) obj;
        if (!Objects.equals(this.tabelle, other.tabelle)) {
            return false;
        }
        if (!Objects.equals(this.notiztext, other.notiztext)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        return true;
    }

}
