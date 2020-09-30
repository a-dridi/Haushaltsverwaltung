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
import haushaltsverwaltung.db.DAO;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name = "reservenWaehrungen_seq", sequenceName = "reserven_id_seq", allocationSize = 1)

public class ReservenWaehrung implements Serializable {

    @GeneratedValue(generator = "reservenWaehrungen_seq")
    @Id
    private Integer id;

    @Column(unique = true)
    private String waehrungsname;

    public ReservenWaehrung() {

    }

    public static List<ReservenWaehrung> getWaehrungen() {
        DAO dao = new DAO();
        return dao.getAllReservenWaehrung();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWaehrungsname() {
        return waehrungsname;
    }

    public void setWaehrungsname(String waehrungsname) {
        this.waehrungsname = waehrungsname;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.waehrungsname);
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
        final ReservenWaehrung other = (ReservenWaehrung) obj;
        if (!Objects.equals(this.waehrungsname, other.waehrungsname)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.waehrungsname;
    }

}
