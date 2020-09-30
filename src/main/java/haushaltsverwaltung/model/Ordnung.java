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

/**
 *
 * @author A.Dridi
 */
@Entity
@SequenceGenerator(name = "ordnung_seq", sequenceName = "ordnung_id_seq", allocationSize = 1)
public class Ordnung implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ordnung_seq")
    @Id
    private Integer ordnung_id;
    private String bezeichnung;
    private String kategorie;
    private String lagerort;
    private String zustand;
    @Column(length = 10000)
    private String informationen;
    private boolean anhang = false;
    private String anhangpfad;
    private String anhangname;
    private String anhangtype;
    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getOrdnung_id() {
        return ordnung_id;
    }

    public void setOrdnung_id(Integer ordnung_id) {
        this.ordnung_id = ordnung_id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getLagerort() {
        return lagerort;
    }

    public void setLagerort(String lagerort) {
        this.lagerort = lagerort;
    }

    public String getZustand() {
        return zustand;
    }

    public void setZustand(String zustand) {
        this.zustand = zustand;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    public boolean isAnhang() {
        return anhang;
    }

    public void setAnhang(boolean anhang) {
        this.anhang = anhang;
    }

    public String getAnhangpfad() {
        return anhangpfad;
    }

    public void setAnhangpfad(String anhangpfad) {
        this.anhangpfad = anhangpfad;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.ordnung_id);
        hash = 31 * hash + Objects.hashCode(this.bezeichnung);
        hash = 31 * hash + Objects.hashCode(this.kategorie);
        hash = 31 * hash + Objects.hashCode(this.lagerort);
        hash = 31 * hash + Objects.hashCode(this.zustand);
        hash = 31 * hash + Objects.hashCode(this.informationen);
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
        final Ordnung other = (Ordnung) obj;
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        if (!Objects.equals(this.kategorie, other.kategorie)) {
            return false;
        }
        if (!Objects.equals(this.lagerort, other.lagerort)) {
            return false;
        }
        if (!Objects.equals(this.zustand, other.zustand)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.ordnung_id, other.ordnung_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Ordnung{" + "ordnung_id=" + ordnung_id + ", bezeichnung=" + bezeichnung + ", kategorie=" + kategorie + ", lagerort=" + lagerort + ", zustand=" + zustand + ", informationen=" + informationen + '}';
    }

}
