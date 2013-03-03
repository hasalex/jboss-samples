package fr.sewatech.examples.jsf.injection.ejb;

import javax.ejb.Stateless;

@Stateless
public class FirstSessionBean implements FirstSessionBeanLocal {

    public static final String FIRST = "FIRST";

    @Override
    public String getValue() {
        return FIRST;
    }

}
