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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author A.Dridi
 */
@Entity
@SequenceGenerator(name = "kryptowaehrungenExchange_seq", sequenceName = "exchangename_id_seq", allocationSize = 1)
public class KryptowaehrungenExchange implements Serializable {

    @GeneratedValue(generator = "kryptowaehrungenExchange_seq")
    @Id
    private Integer id;
    
    @Column(unique = true)
    private String exchangeName;

    public KryptowaehrungenExchange() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.exchangeName);
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
        final KryptowaehrungenExchange other = (KryptowaehrungenExchange) obj;
        if (!Objects.equals(this.exchangeName, other.exchangeName)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.exchangeName;
    }

}
