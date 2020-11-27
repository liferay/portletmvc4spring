<%@ include file="/WEB-INF/flows/init.jsp" %>

<portlet:renderURL var="submit">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="submit" />
</portlet:renderURL>

<h2>My Flow - Page 1 (<c:out value="${flowExecutionKey}" />)</h2>

<a href="${submit}">Start</a>