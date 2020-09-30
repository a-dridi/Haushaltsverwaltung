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
@SequenceGenerator(name = "videos_seq", sequenceName = "videos_id_seq", initialValue=700, allocationSize = 1)

public class MedienVideos implements Serializable {

    @GeneratedValue(strategy=GenerationType.AUTO, generator = "videos_seq")
    @Id
    private Integer videos_id;
    private String name;
    private boolean ard_entertainement;
    private String sprache;
    private boolean hd;
    private String genre;
    private Integer dauer;
    private Integer jahr;
    private boolean serie;
    private String link;
    private String nativer_titel;

    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getVideos_id() {
        return videos_id;
    }

    public void setVideos_id(Integer videos_id) {
        this.videos_id = videos_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isArd_entertainement() {
        return ard_entertainement;
    }

    public void setArd_entertainement(boolean ard_entertainement) {
        this.ard_entertainement = ard_entertainement;
    }

    public String getSprache() {
        return sprache;
    }

    public void setSprache(String sprache) {
        this.sprache = sprache;
    }

    public boolean isHd() {
        return hd;
    }

    public void setHd(boolean hd) {
        this.hd = hd;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getDauer() {
        return dauer;
    }

    public void setDauer(Integer dauer) {
        this.dauer = dauer;
    }

    public Integer getJahr() {
        return jahr;
    }

    public void setJahr(Integer jahr) {
        this.jahr = jahr;
    }

    public boolean isSerie() {
        return serie;
    }

    public void setSerie(boolean serie) {
        this.serie = serie;
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
        hash = 67 * hash + Objects.hashCode(this.videos_id);
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + (this.ard_entertainement ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.sprache);
        hash = 67 * hash + (this.hd ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.genre);
        hash = 67 * hash + Objects.hashCode(this.dauer);
        hash = 67 * hash + Objects.hashCode(this.jahr);
        hash = 67 * hash + (this.serie ? 1 : 0);
        hash = 67 * hash + Objects.hashCode(this.link);
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
        final MedienVideos other = (MedienVideos) obj;
        if (this.ard_entertainement != other.ard_entertainement) {
            return false;
        }
        if (this.hd != other.hd) {
            return false;
        }
        if (this.serie != other.serie) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.sprache, other.sprache)) {
            return false;
        }
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.videos_id, other.videos_id)) {
            return false;
        }
        if (!Objects.equals(this.dauer, other.dauer)) {
            return false;
        }
        if (!Objects.equals(this.jahr, other.jahr)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MedienVideos{" + "videos_id=" + videos_id + ", name=" + name + ", ard_entertainement=" + ard_entertainement + ", sprache=" + sprache + ", hd=" + hd + ", genre=" + genre + ", dauer=" + dauer + ", jahr=" + jahr + ", serie=" + serie + ", link=" + link + '}';
    }

}
