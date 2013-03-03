package fr.sewatech.examples.jsf.injection;

import com.sun.faces.spi.InjectionProvider;
import com.sun.faces.spi.InjectionProviderException;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;

import javax.ejb.EJB;
import javax.naming.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EjbInjectionProvider implements InjectionProvider {
    private final JndiByClass jndi;

    public EjbInjectionProvider() throws InjectionProviderException {
        try {
            jndi = new JndiByClass();
        } catch (IOException e) {
            throw new InjectionProviderException(e);
        } catch (NamingException e) {
            throw new InjectionProviderException(e);
        }
    }

    public void inject(Object o) throws InjectionProviderException {
        Field[] fields = o.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.getAnnotation(EJB.class) != null) {
                    field.setAccessible(true);
                    field.set(o, jndi.getBean(field.getType()));
                }
            }
        } catch (Exception e) {
            throw new InjectionProviderException(e);
        }
    }
 
    public void invokePreDestroy(Object o) throws InjectionProviderException {
    }
 
    public void invokePostConstruct(Object o) throws InjectionProviderException {
    }


    private class JndiByClass {
        private static final String APP_ROOT_NAME = "java:app/";
        private final ManagementOperations managementOperations;
        private Context jndiContext;

        private JndiByClass() throws IOException, NamingException {
            managementOperations = new ManagementOperations();

            InitialContext initialContext = new InitialContext();
            jndiContext = (Context) initialContext.lookup(APP_ROOT_NAME);
        }

        private Object getBean(Class<?> type) throws NamingException, IOException {
            for (String ejbJarName : managementOperations.getEjb3SubDeployments(getEarName())) {
                String contextName = ejbJarName.substring(0, ejbJarName.lastIndexOf('.'));
                NamingEnumeration<Binding> bindings = jndiContext.listBindings(contextName);
                while (bindings.hasMoreElements()) {
                    Binding binding = bindings.nextElement();
                    Object object = binding.getObject();
                    if (type.isAssignableFrom(object.getClass())) {
                        return object;
                    }
                }
            }
            return null;
        }

        private String getEarName() throws NamingException {
            return jndiContext.lookup("AppName") + ".ear";
        }
    }


    private static class ManagementOperations {

        private final ModelControllerClient controllerClient;

        public ManagementOperations() throws IOException {
            String portAsString = System.getProperty("jboss.management.native.port");
            int port = portAsString == null ? 9999 : Integer.parseInt(portAsString);
            controllerClient = ModelControllerClient.Factory.create("localhost", port);
        }

        public List<String> getEjb3SubDeployments(String earName) throws IOException {
            List<String> ejb3SubDeployments = new ArrayList<String>();

            List<Property> subDeploymentProperties = readSubDeployments(earName);
            for (Property subDeploymentProperty : subDeploymentProperties) {
                ModelNode subsystem = subDeploymentProperty.getValue().get("subsystem");
                if (subsystem.has("ejb3")) {
                    ejb3SubDeployments.add(subDeploymentProperty.getName());
                }
            }
            return ejb3SubDeployments;
        }

        private List<Property> readSubDeployments(String earName) throws IOException {
            ModelNode operation = buildReadSubDeploymentsOperation();
            ModelNode address = operation.get("address");
            address.add("deployment", earName);

            ModelNode returnVal = controllerClient.execute(operation);
            return returnVal.get("result").asPropertyList();
        }

        private ModelNode buildReadSubDeploymentsOperation() {
            ModelNode operation = new ModelNode();
            operation.get("operation").set("read-children-resources");
            operation.get("child-type").set("subdeployment");
            return operation;
        }

    }

}
