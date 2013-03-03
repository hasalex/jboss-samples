<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <h:outputText value="{value:#{backingBean.valueFromEjb}}" />
</f:view>
