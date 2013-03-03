package fr.sewatech.examples.jsf.injection.ejb;

import javax.ejb.Stateless;

@Stateless
public class SecondSessionBean implements SecondSessionBeanLocal {

    public static final String SECOND = "SECOND";

    @Override
    public String getValue() {
        return SECOND;
    }

}
