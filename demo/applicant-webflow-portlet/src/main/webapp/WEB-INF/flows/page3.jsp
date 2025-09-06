<?xml version="1.0" encoding="UTF-8"?>
<jsp:root xmlns:c="jakarta.tags.core"
		  xmlns:form="http://www.springframework.org/tags/form"
		  xmlns:jsp="http://java.sun.com/JSP/Page"
		  xmlns:portlet="jakarta.tags.portlet"
		  xmlns:spring="http://www.springframework.org/tags"
		  version="3.1">

	<portlet:renderURL var="returnURL">
	  <portlet:param name="execution" value="${flowExecutionKey}" />
	  <portlet:param name="_eventId" value="return" />
	</portlet:renderURL>

	<portlet:actionURL var="submitApplicantURL">
		<portlet:param name="execution" value="${flowExecutionKey}" />
		<portlet:param name="_eventId" value="submit" />
	</portlet:actionURL>

	<h2>Applicant Flow - Comments (<c:out value="${flowExecutionKey}" />)</h2>

	<div class="container">
		<form:form id="${namespace}mainForm" method="post" action="${submitApplicantURL}" modelAttribute="applicant">
			<c:if test="${not empty globalInfoMessage}">
				<span class="portlet-msg-info">${globalInfoMessage}</span>
			</c:if>
			<c:if test="${not empty globalErrorMessage}">
				<span class="portlet-msg-error">${globalErrorMessage}</span>
			</c:if>

			<fieldset>
				<div class="row">
					<div class="col">
						<div class="form-group">
							<form:label for="${namespace}comments" path="comments">
								<spring:message code="comments" />
							</form:label>
							<form:textarea id="${namespace}comments" cssClass="form-control" path="comments"/>
							<form:errors path="comments" cssClass="portlet-msg-error"/>
						</div>
					</div>
				</div>
			</fieldset>

			<spring:message code="return" var="return" />
			<input class="btn btn-primary" formaction="${returnURL}" value="${return}" type="submit"/>

			<spring:message code="submit" var="submit" />
			<input class="btn btn-primary" value="${submit}" type="submit"/>
		</form:form>
	</div>
</jsp:root>