package fr.sewatech.examples.jsf.injection.web;

import fr.sewatech.examples.jsf.injection.ejb.SessionBeanLocal;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.io.Serializable;

public class BackingBean implements Serializable {
    public static final String FAIL = "FAIL";

    @EJB public SessionBeanLocal ejb;

    public String getValueFromEjb() {
        System.out.println("JSF : " + FacesContext.class.getClassLoader());
        if (ejb == null) {
            return FAIL;
        } else {
            return ejb.getValue();
        }
    }

}
