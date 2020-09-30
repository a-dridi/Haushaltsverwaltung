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
@SequenceGenerator(name = "software_seq", sequenceName = "software_id_seq", initialValue=40, allocationSize = 1)
public class MedienSoftware implements Serializable {

    @GeneratedValue(strategy=GenerationType.AUTO,generator = "software_seq")
    @Id
    private Integer id;

    private String programmname;
    private String hersteller;
    private String betriebssystem;
    private String sprache;
    private String version;
    private String sonstige_infos;
    private String link;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProgrammname() {
        return programmname;
    }

    public void setProgrammname(String programmname) {
        this.programmname = programmname;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getBetriebssystem() {
        return betriebssystem;
    }

    public void setBetriebssystem(String betriebssystem) {
        this.betriebssystem = betriebssystem;
    }

    public String getSprache() {
        return sprache;
    }

    public void setSprache(String sprache) {
        this.sprache = sprache;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSonstige_infos() {
        return sonstige_infos;
    }

    public void setSonstige_infos(String sonstige_infos) {
        this.sonstige_infos = sonstige_infos;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
    public String toString() {
        return "MedienSoftware{" + "id=" + id + ", programmname=" + programmname + ", hersteller=" + hersteller + ", betriebssystem=" + betriebssystem + ", sprache=" + sprache + ", version=" + version + ", sonstige_infos=" + sonstige_infos + ", link=" + link + ", anhang=" + anhang + ", anhangpfad=" + anhangpfad + ", anhangname=" + anhangname + ", anhangtype=" + anhangtype + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.programmname);
        hash = 47 * hash + Objects.hashCode(this.hersteller);
        hash = 47 * hash + Objects.hashCode(this.betriebssystem);
        hash = 47 * hash + Objects.hashCode(this.sprache);
        hash = 47 * hash + Objects.hashCode(this.version);
        hash = 47 * hash + Objects.hashCode(this.sonstige_infos);
        hash = 47 * hash + Objects.hashCode(this.link);
        hash = 47 * hash + (this.anhang ? 1 : 0);
        hash = 47 * hash + Objects.hashCode(this.anhangpfad);
        hash = 47 * hash + Objects.hashCode(this.anhangname);
        hash = 47 * hash + Objects.hashCode(this.anhangtype);
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
        final MedienSoftware other = (MedienSoftware) obj;
        if (this.anhang != other.anhang) {
            return false;
        }
        if (!Objects.equals(this.programmname, other.programmname)) {
            return false;
        }
        if (!Objects.equals(this.hersteller, other.hersteller)) {
            return false;
        }
        if (!Objects.equals(this.betriebssystem, other.betriebssystem)) {
            return false;
        }
        if (!Objects.equals(this.sprache, other.sprache)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.sonstige_infos, other.sonstige_infos)) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.anhangpfad, other.anhangpfad)) {
            return false;
        }
        if (!Objects.equals(this.anhangname, other.anhangname)) {
            return false;
        }
        if (!Objects.equals(this.anhangtype, other.anhangtype)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
