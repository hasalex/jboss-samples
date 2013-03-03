package fr.sewatech.examples.jboss;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {
    @Inject private SessionBean userState;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        String sessionId = event.getSession().getId();
        System.out.println("New HTTP Session created : " + sessionId);
        userState.setValue(sessionId);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("HTTP Session destroyed : " + event.getSession().getId());
    }
}
