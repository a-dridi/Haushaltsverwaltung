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
 * @author
 */
@Entity
@SequenceGenerator(name = "musik_seq", sequenceName = "musik_id_seq", initialValue = 40, allocationSize = 1)
public class MedienMusik implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "musik_seq")
    @Id
    private Integer id;
    private String interpret;
    private String songname;
    private boolean alt;
    private Integer jahr;
    private String genre;
    private String code;
    private String link;
    @Column(length = 10000)
    private String bemerkungen;
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

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isAlt() {
        return alt;
    }

    public void setAlt(boolean alt) {
        this.alt = alt;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.id);
        hash = 19 * hash + Objects.hashCode(this.interpret);
        hash = 19 * hash + Objects.hashCode(this.songname);
        hash = 19 * hash + (this.alt ? 1 : 0);
        hash = 19 * hash + Objects.hashCode(this.jahr);
        hash = 19 * hash + Objects.hashCode(this.genre);
        hash = 19 * hash + Objects.hashCode(this.code);
        hash = 19 * hash + Objects.hashCode(this.link);
        hash = 19 * hash + Objects.hashCode(this.bemerkungen);
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
        final MedienMusik other = (MedienMusik) obj;
        if (this.alt != other.alt) {
            return false;
        }
        if (!Objects.equals(this.interpret, other.interpret)) {
            return false;
        }
        if (!Objects.equals(this.songname, other.songname)) {
            return false;
        }
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.bemerkungen, other.bemerkungen)) {
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
        return "MedienMusik{" + "id=" + id + ", interpret=" + interpret + ", songname=" + songname + ", jahr=" + jahr + ", genre=" + genre + ", code=" + code + ", link=" + link + '}';
    }

}
