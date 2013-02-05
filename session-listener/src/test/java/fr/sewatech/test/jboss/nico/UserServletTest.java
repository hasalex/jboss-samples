package fr.sewatech.test.jboss.nico;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class UserServletTest {

    @Deployment(testable = false)
    public static Archive deploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(SessionBean.class, SessionListener.class, UserServlet.class)
                .addAsWebInfResource("jboss-deployment-structure.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test @RunAsClient
    public void should_several_get_requests_return_valid_beanValues(@ArquillianResource URL baseURL) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        // First GET usually works
        sendGetRequestAndVerifyResponse(baseURL, httpClient);
        // At second GET beanValue is null, but should be the same as sessionId
        sendGetRequestAndVerifyResponse(baseURL, httpClient);
    }

    private void sendGetRequestAndVerifyResponse(URL baseURL, HttpClient httpClient) throws URISyntaxException, IOException {
        HttpGet get = new HttpGet(baseURL.toURI());
        get.setHeader("Accept", "application/json");
        HttpResponse response = httpClient.execute(get);
        assertEquals(response.getStatusLine().getStatusCode(), 200L);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String content = reader.readLine();

        String[] objects = content.substring(1, content.length() - 1).split(",");
        String beanValue = extractStringValue(objects[0]);
        String sessionId = extractStringValue(objects[1]);

        assertEquals(sessionId, beanValue);
    }

    private String extractStringValue(String content) {
        return content.substring(content.indexOf('{'), content.indexOf('}')).split(":")[1];
    }

}
