package fr.sewatech.test.jboss.nico;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@SessionScoped
public class MyBean implements Serializable {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}