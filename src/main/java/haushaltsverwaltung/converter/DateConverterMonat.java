/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package haushaltsverwaltung.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * DATUM überprüfen Version: Datum ohne Zeit
 *
 * @author A.Dridi
 */
@FacesConverter("dateConverterMonat")
public class DateConverterMonat implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";

        } else {
            Date datumConverter;
            SimpleDateFormat sdf = new SimpleDateFormat("MM.yyyy");

            try {
                datumConverter = sdf.parse(value);
                return datumConverter;
                
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bitte im Format mm.yyyy eingeben! -- ", "" + e.toString()));
                return null;
            }
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value == null) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM.yyyy");
            Date datumConverter = (Date) value;

            return sdf.format(datumConverter);
        }

    }

}
