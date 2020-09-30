/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author
 */
@Entity
@SequenceGenerator(name = "videoclips_seq", initialValue=1530, sequenceName = "videoclips_id_seq", allocationSize = 1)
public class MedienVideoclips implements Serializable {

    @GeneratedValue(strategy=GenerationType.AUTO, generator = "videoclips_seq")
    @Id
    private Integer id;
    private String interpret;
    private String titel;
    private String sprache;
    private Integer jahr;
    private String link;
    private String nativer_titel;

    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getSprache() {
        return sprache;
    }

    public void setSprache(String sprache) {
        this.sprache = sprache;
    }

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNativer_titel() {
        return nativer_titel;
    }

    public void setNativer_titel(String nativer_titel) {
        this.nativer_titel = nativer_titel;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.interpret);
        hash = 23 * hash + Objects.hashCode(this.titel);
        hash = 23 * hash + Objects.hashCode(this.sprache);
        hash = 23 * hash + Objects.hashCode(this.jahr);
        hash = 23 * hash + Objects.hashCode(this.link);
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
        final MedienVideoclips other = (MedienVideoclips) obj;
        if (!Objects.equals(this.interpret, other.interpret)) {
            return false;
        }
        if (!Objects.equals(this.titel, other.titel)) {
            return false;
        }
        if (!Objects.equals(this.sprache, other.sprache)) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.jahr, other.jahr)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MedienVideoclips{" + "id=" + id + ", interpret=" + interpret + ", titel=" + titel + ", sprache=" + sprache + ", jahr=" + jahr + ", link=" + link + '}';
    }

}
