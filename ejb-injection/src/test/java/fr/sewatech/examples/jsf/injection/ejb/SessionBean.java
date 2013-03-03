package fr.sewatech.examples.jsf.injection.ejb;

import javax.ejb.Stateless;

@Stateless
public class SessionBean implements SessionBeanLocal {

    public static final String VALUE = "VALUE";

    @Override
    public String getValue() {
        return VALUE;
    }

}
