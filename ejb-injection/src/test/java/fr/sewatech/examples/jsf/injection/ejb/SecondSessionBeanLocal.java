package fr.sewatech.examples.jsf.injection.ejb;

import javax.ejb.Local;

@Local
public interface SecondSessionBeanLocal {
  public String getValue();
}
