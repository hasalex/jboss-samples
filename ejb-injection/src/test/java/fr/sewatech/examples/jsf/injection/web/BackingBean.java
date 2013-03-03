package fr.sewatech.examples.jsf.injection.web;

import fr.sewatech.examples.jsf.injection.ejb.FirstSessionBeanLocal;
import fr.sewatech.examples.jsf.injection.ejb.SecondSessionBeanLocal;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.io.Serializable;

public class BackingBean implements Serializable {
    public static final String FAIL = "FAIL";

    @EJB public FirstSessionBeanLocal ejb1;
    @EJB public SecondSessionBeanLocal ejb2;

    public String getValueFromEjb() {
        System.out.println("JSF : " + FacesContext.class.getClassLoader());
        if ( (ejb1 == null) || (ejb2 == null)) {
            return FAIL;
        } else {
            return ejb1.getValue() + "-" + ejb2.getValue();
        }
    }

}
