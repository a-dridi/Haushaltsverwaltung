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
@SequenceGenerator(name = "buecher_seq", sequenceName = "buecher_id_seq", allocationSize = 1)
public class Buecher implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "buecher_seq")
    @Id
    private Integer buecher_id;

    private String buchtitel;
    private String kategorie;
    private String lagerort;
    private String zustand;
    private String isbn;
    private Integer jahr;
    private String sprache;
    @Column(length = 10000)
    private String informationen;
    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.buchtitel);
        hash = 97 * hash + Objects.hashCode(this.kategorie);
        hash = 97 * hash + Objects.hashCode(this.lagerort);
        hash = 97 * hash + Objects.hashCode(this.zustand);
        hash = 97 * hash + Objects.hashCode(this.isbn);
        hash = 97 * hash + Objects.hashCode(this.jahr);
        hash = 97 * hash + Objects.hashCode(this.sprache);
        hash = 97 * hash + Objects.hashCode(this.informationen);
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
        final Buecher other = (Buecher) obj;
        if (!Objects.equals(this.buchtitel, other.buchtitel)) {
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
        if (!Objects.equals(this.sprache, other.sprache)) {
            return false;
        }
        if (!Objects.equals(this.informationen, other.informationen)) {
            return false;
        }
        if (!Objects.equals(this.buecher_id, other.buecher_id)) {
            return false;
        }
        if (!Objects.equals(this.isbn, other.isbn)) {
            return false;
        }
        if (!Objects.equals(this.jahr, other.jahr)) {
            return false;
        }
        return true;
    }

    public Integer getBuecher_id() {
        return buecher_id;
    }

    public void setBuecher_id(Integer buecher_id) {
        this.buecher_id = buecher_id;
    }

    public String getBuchtitel() {
        return buchtitel;
    }

    public void setBuchtitel(String buchtitel) {
        this.buchtitel = buchtitel;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
    }

    public String getSprache() {
        return sprache;
    }

    public void setSprache(String sprache) {
        this.sprache = sprache;
    }

    public String getInformationen() {
        return informationen;
    }

    public void setInformationen(String informationen) {
        this.informationen = informationen;
    }

    @Override
    public String toString() {
        return "Buecher{" + "buecher_id=" + buecher_id + ", buchtitel=" + buchtitel + ", kategorie=" + kategorie + ", lagerort=" + lagerort + ", zustand=" + zustand + ", isbn=" + isbn + ", jahr=" + jahr + ", sprache=" + sprache + ", informationen=" + informationen + '}';
    }

}
