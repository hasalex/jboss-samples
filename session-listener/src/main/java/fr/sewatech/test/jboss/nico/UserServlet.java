package fr.sewatech.test.jboss.nico;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/")
public class UserServlet extends HttpServlet {
    @Inject
    SessionBean sessionBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.write(buildJsonContent(req.getSession()));
    }

    private String buildJsonContent(HttpSession session) {
        return "{bean:" + sessionBean.asJson() + ", session:" + asJson(session) + "}";
    }

    private String asJson(HttpSession session) {
        return "{id:" + session.getId() + "}";
    }
}
