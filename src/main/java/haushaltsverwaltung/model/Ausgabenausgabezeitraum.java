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

/**
 *
 * @author A.Dridi
 */
@Entity
public class Ausgabenausgabezeitraum implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int zeitraumid;

    @Column(unique = true)
    private String ausgabezeitraumbezeichnung;

    public String getAusgabezeitraumbezeichnung() {
        return ausgabezeitraumbezeichnung;
    }

    public void setAusgabezeitraumbezeichnung(String ausgabezeitraumbezeichnung) {
        this.ausgabezeitraumbezeichnung = ausgabezeitraumbezeichnung;
    }

    public int getZeitraumid() {
        return zeitraumid;
    }

    public void setZeitraumid(int zeitraumid) {
        this.zeitraumid = zeitraumid;
    }
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.ausgabezeitraumbezeichnung);
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
        final Ausgabenausgabezeitraum other = (Ausgabenausgabezeitraum) obj;
        if (!Objects.equals(this.ausgabezeitraumbezeichnung, other.ausgabezeitraumbezeichnung)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ausgabezeitraumbezeichnung;
    }

}
