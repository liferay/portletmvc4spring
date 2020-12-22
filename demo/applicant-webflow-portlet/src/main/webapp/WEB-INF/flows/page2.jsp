<%@ include file="/WEB-INF/flows/init.jsp" %>

<portlet:renderURL var="returnURL">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="return" />
</portlet:renderURL>

<portlet:actionURL var="submitURL">
  <portlet:param name="execution" value="${flowExecutionKey}" />
  <portlet:param name="_eventId" value="submit" />
</portlet:actionURL>

<portlet:actionURL var="uploadAttachmentURL">
	<portlet:param name="execution" value="${flowExecutionKey}" />
	<portlet:param name="_eventId" value="uploadAttachment" />
</portlet:actionURL>

<portlet:actionURL var="deleteAttachmentURL">
	<portlet:param name="execution" value="${flowExecutionKey}" />
	<portlet:param name="_eventId" value="deleteAttachment" />
</portlet:actionURL>	

<h2>Applicant Flow - Attachments (<c:out value="${flowExecutionKey}" />)</h2>

<div class="container">
	<div class="row">
		<div class="col">
			<h3><spring:message code="attachments"/></h3>
			<table class="table">
				<thead>
					<tr>
						<th><img src="${renderRequest.contextPath}/images/icon-delete.png" style="display:none;"/></th>
						<th><spring:message code="file-name"/></th>
						<th><spring:message code="size"/></th>
					</tr>
				</thead>
				<tbody id="${namespace}attachmentsTableBody">
					<c:forEach items="${applicant.attachments}" var="attachment" varStatus="attachmentStatus">
						<tr>
							<td>
								<form:form id="${namespace}deleteFileForm" method="post" action="${deleteAttachmentURL}" modelAttribute="applicant">
									<input name="attachmentIndex" type="hidden" value="${attachmentStatus.index}" />  
									<input name="attachmentDeleteButton" src="${renderRequest.contextPath}/images/icon-delete.png" type="image"/>
								</form:form>
							</td>
							<td>
								<c:out value="${attachment.name}"/>
							</td>
							<td>
								<c:out value="${attachment.size}"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr/>

			<form:form id="${namespace}fileUploadForm" method="post" action="${uploadAttachmentURL}" modelAttribute="transientUpload" enctype="multipart/form-data"> 
				<div class="form-group">
					<input id="${namespace}inputFile" class="form-control" name="multipartFiles" multiple="multiple" type="file"/>
					<spring:message code="add-attachment" var="addAttachment" />
					<input class="btn btn-primary" value="${addAttachment}" type="submit"/>
				</div>
			</form:form>
			<spring:message code="return" var="return" />
			<a href="${returnURL}" class="btn btn-light"><c:out value="${return}" /></a>
			<spring:message code="submit" var="submit" />
			<a href="${submitURL}" class="btn btn-primary"><c:out value="${submit}" /></a>
		</div>
	</div>
</div>
