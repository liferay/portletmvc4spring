<%@ include file="/WEB-INF/flows/init.jsp" %>

<!-- 
<portlet:renderURL var="submit">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="submit" />
</portlet:renderURL>
 -->
 
<portlet:actionURL var="submitApplicantURL">
	<portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:actionURL>		
 
<!-- 
<portlet:resourceURL id="autoFill" var="autoFillCityURL" />
-->

<h2>Applicant Flow - Personal information (<c:out value="${flowExecutionKey}" />)</h2>

<div class="container">
	
	<form:form id="${namespace}mainForm" method="post" action="${submitApplicantURL}" modelAttribute="applicant">

		<input id="${namespace}_eventId" name="${namespace}_eventId" type="hidden" value="submit" />

		<c:if test="${not empty globalInfoMessage}">
			<span class="portlet-msg-info">${globalInfoMessage}</span>
		</c:if>
		<c:if test="${not empty globalErrorMessage}">
			<span class="portlet-msg-error">${globalErrorMessage}</span>
		</c:if>

		<fieldset>
			<div class="row">
				<div class="col-6">
					<div class="form-group">
						<form:label for="${namespace}firstName" path="firstName">
							<spring:message code="first-name" />
						</form:label>
						<form:input id="${namespace}firstName" cssClass="form-control" path="firstName"/>
						<form:errors path="firstName" cssClass="portlet-msg-error"/>
					</div>
					<div class="form-group">
						<form:label for="${namespace}lastName" path="lastName">
							<spring:message code="last-name" />
						</form:label>
						<form:input id="${namespace}lastName" cssClass="form-control" path="lastName"/>
						<form:errors path="lastName" cssClass="portlet-msg-error"/>
					</div>
					<div class="form-group">
						<form:label for="${namespace}emailAddress" path="emailAddress">
							<spring:message code="email-address" />
						</form:label>
						<form:input id="${namespace}emailAddress" cssClass="form-control" path="emailAddress" type="email"/>
						<form:errors path="emailAddress" cssClass="portlet-msg-error"/>
					</div>
					<div class="form-group">
						<form:label for="${namespace}phoneNumber" path="phoneNumber">
							<spring:message code="phone-number" />
						</form:label>
						<form:input id="${namespace}phoneNumber" cssClass="form-control" path="phoneNumber" type="tel"/>
						<form:errors path="phoneNumber" cssClass="portlet-msg-error"/>
					</div>
				</div>
				<div class="col-6">
					<div class="form-group">
						<form:label for="${namespace}dateOfBirth" path="dateOfBirth">
							<spring:message code="date-of-birth" />
							<img src="${renderRequest.contextPath}/images/icon-help.png" title="<spring:message code="date-format-hint" />"/>
						</form:label>
						<form:input id="${namespace}dateOfBirth" cssClass="form-control" path="dateOfBirth"/>
						<form:errors path="dateOfBirth" cssClass="portlet-msg-error"/>
					</div>
					<div class="form-group">
						<form:label for="${namespace}postalCode" path="postalCode">
							<spring:message code="zip-postal" />
							<img src="${renderRequest.contextPath}/images/icon-help.png" title="<spring:message code="postal-code-hint" />"/>
						</form:label>
						<form:input id="${namespace}postalCode" cssClass="form-control" path="postalCode" />
						<script type="text/javascript">
						    $(document).ready(function () {
						        $('#<c:out value="${namespace}" />postalCode').change(function() {
						        
						        	$('#<c:out value="${namespace}" />_eventId').val("autoFill");

						        	$(this).closest('form').submit();
						        	
						        });
						    });
						</script>						
						<form:errors path="postalCode" cssClass="portlet-msg-error"/>
					</div>
					<div class="form-group">
						<form:label for="${namespace}city" path="city">
							<spring:message code="city" />
						</form:label>
						<form:input id="${namespace}city" cssClass="form-control" path="city"/>
						<form:errors path="city" cssClass="portlet-msg-error"/>
					</div>
					<div class="form-group">
						<form:label for="${namespace}provinceId" path="provinceId">
							<spring:message code="state-province" />
						</form:label>
						<spring:message code="select" var="select" />
						<form:select id="${namespace}provinceId" cssClass="form-control" path="provinceId">
							<form:option label="${select}" value=""/>
							<form:options items="${provinces}" itemLabel="provinceName" itemValue="provinceId" />
						</form:select>
						<form:errors path="provinceId" cssClass="portlet-msg-error"/>
					</div>
				</div>
			</div>
		</fieldset>
		<spring:message code="submit" var="submit" />
		<input id="<c:out value="${namespace}" />mainFormSubmit" class="btn btn-primary" value="${submit}" type="submit"/>
	</form:form>
</div>
