<%@ include file="/WEB-INF/flows/init.jsp" %>

<portlet:renderURL var="cancel">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="cancel" />
</portlet:renderURL>

<portlet:renderURL var="submit">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="submit" />
</portlet:renderURL>

<h2>Applicant Flow - Page 2 (<c:out value="${flowExecutionKey}" />)</h2>

<a href="${cancel}">Cancel</a>
<a href="${submit}">Submit</a>