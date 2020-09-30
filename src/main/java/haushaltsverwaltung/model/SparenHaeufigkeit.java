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
public class SparenHaeufigkeit implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int kategoriebezid;

    @Column(unique = true)
    private String kategoriebezeichnung;

    public int getKategoriebezid() {
        return kategoriebezid;
    }

    public void setKategoriebezid(int kategoriebezid) {
        this.kategoriebezid = kategoriebezid;
    }

    public String getKategoriebezeichnung() {
        return kategoriebezeichnung;
    }

    public void setKategoriebezeichnung(String kategoriebezeichnung) {
        this.kategoriebezeichnung = kategoriebezeichnung;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.kategoriebezid;
        hash = 89 * hash + Objects.hashCode(this.kategoriebezeichnung);
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
        final SparenHaeufigkeit other = (SparenHaeufigkeit) obj;
        if (this.kategoriebezid != other.kategoriebezid) {
            return false;
        }
        if (!Objects.equals(this.kategoriebezeichnung, other.kategoriebezeichnung)) {
            return false;
        }
        return true;
    }
    
}
