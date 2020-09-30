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
public class Buecherzustand implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int zustandid;

    @Column(unique = true)
    private String zustandbezeichnung;

    public int getZustandid() {
        return zustandid;
    }

    public void setZustandid(int zustandid) {
        this.zustandid = zustandid;
    }

    
    
    public String getZustandbezeichnung() {
        return zustandbezeichnung;
    }

    public void setZustandbezeichnung(String zustandbezeichnung) {
        this.zustandbezeichnung = zustandbezeichnung;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.zustandbezeichnung);
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
        final Buecherzustand other = (Buecherzustand) obj;
        if (!Objects.equals(this.zustandbezeichnung, other.zustandbezeichnung)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return zustandbezeichnung;
    }

}
