/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.model;

/**
 *
 * @author A.Dridi
 */
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author A.Dridi
 */
@Entity
@SequenceGenerator(name = "kryptowaehrungenVorgang_seq", sequenceName = "vorang_id_seq", allocationSize = 1)
/**
 * Vorgaenge werden auto. in die DB geschrieben
 */
public class KryptowaehrungenVorgang implements Serializable {

    @GeneratedValue(generator = "kryptowaehrungenVorgang_seq")
    @Id
    private Integer id;

    private String vorgangbeschreibung;

    public KryptowaehrungenVorgang() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVorgangbeschreibung() {
        return vorgangbeschreibung;
    }

    public void setVorgangbeschreibung(String vorgangbeschreibung) {
        this.vorgangbeschreibung = vorgangbeschreibung;
    }


    @Override
    public String toString() {
        return this.vorgangbeschreibung;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.vorgangbeschreibung);
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
        final KryptowaehrungenVorgang other = (KryptowaehrungenVorgang) obj;
        if (!Objects.equals(this.vorgangbeschreibung, other.vorgangbeschreibung)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    
    
}
