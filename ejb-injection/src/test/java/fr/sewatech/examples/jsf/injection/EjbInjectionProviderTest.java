package fr.sewatech.examples.jsf.injection;

import fr.sewatech.examples.jsf.injection.ejb.SessionBean;
import fr.sewatech.examples.jsf.injection.ejb.SessionBeanLocal;
import fr.sewatech.examples.jsf.injection.web.BackingBean;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static fr.sewatech.examples.jsf.injection.ejb.SessionBean.VALUE;
import static fr.sewatech.examples.jsf.injection.web.BackingBean.FAIL;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class EjbInjectionProviderTest {

    @Deployment(testable = false, name = "withInjectionProvider")
    public static Archive deployWithInjectionProvider() {
        WebArchive webArchive = buildWebArchive()
                .addClasses(EjbInjectionProvider.class)
                .addAsWebInfResource("web/web-with-injectionProvider.xml", "web.xml");
        return ShrinkWrap.create(EnterpriseArchive.class)
                .addAsModules(webArchive, buildEjbArchive())
                .addAsManifestResource("jboss-deployment-structure.xml");
    }

    @Deployment(testable = false, name = "withoutInjectionProvider")
    public static Archive deployWithoutInjectionProvider() {
        WebArchive webArchive = buildWebArchive()
                .addAsWebInfResource("web/web-without-injectionProvider.xml", "web.xml");
        return ShrinkWrap.create(EnterpriseArchive.class)
                .addAsModules(webArchive, buildEjbArchive())
                .addAsManifestResource("jboss-deployment-structure.xml");
    }

    private static WebArchive buildWebArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(BackingBean.class)
                .addAsWebResource("web/bean.jsp")
                .addAsWebInfResource("web/faces-config.xml");
    }

    private static JavaArchive buildEjbArchive() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(SessionBean.class, SessionBeanLocal.class);
    }

    @Test @OperateOnDeployment("withInjectionProvider") @RunAsClient
    public void should_injection_work_with_injectionProvider(@ArquillianResource URL baseURL) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        String result = sendGetRequestAndReturnValue(baseURL.toString() + "bean.faces", httpClient);
        assertEquals(VALUE, result);
    }

    @Test @OperateOnDeployment("withoutInjectionProvider") @RunAsClient
    public void should_injection_NOT_work_without_injectionProvider(@ArquillianResource URL baseURL) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        String result = sendGetRequestAndReturnValue(baseURL.toString() + "bean.faces", httpClient);
        assertEquals(FAIL, result);
    }

    private String sendGetRequestAndReturnValue(String url, HttpClient httpClient) throws IOException {
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "application/json");
        HttpResponse response = httpClient.execute(get);
        assertEquals(200L, response.getStatusLine().getStatusCode());


        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String content = "";
        while (content.isEmpty())
            content = reader.readLine();

        return extractStringValue(content);
    }

    private String extractStringValue(String content) {
        return content.substring(content.indexOf('{'), content.indexOf('}')).split(":")[1];
    }

}
