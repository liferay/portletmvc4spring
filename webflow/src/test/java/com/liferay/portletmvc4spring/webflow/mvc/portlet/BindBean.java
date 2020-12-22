package com.liferay.portletmvc4spring.webflow.mvc.portlet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public class BindBean {
	private String stringProperty;
	private Integer integerProperty = 3;
	private Date dateProperty;
	private boolean booleanProperty = true;
	private NestedBean beanProperty;
	private MultipartFile multipartFile;

	private String[] stringArrayProperty;
	private Integer[] integerArrayProperty;
	private int[] primitiveArrayProperty;
	private List<Object> listProperty;
	private Map<Object, Object> mapProperty;
	private boolean validationMethodInvoked;

	public BindBean() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, 2008);
		dateProperty = cal.getTime();
		beanProperty = new NestedBean();
	}

	public String getStringProperty() {
		return stringProperty;
	}

	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	public Integer getIntegerProperty() {
		return integerProperty;
	}

	public void setIntegerProperty(Integer integerProperty) {
		this.integerProperty = integerProperty;
	}

	public boolean getBooleanProperty() {
		return booleanProperty;
	}

	public void setBooleanProperty(boolean booleanProperty) {
		this.booleanProperty = booleanProperty;
	}

	public Date getDateProperty() {
		return dateProperty;
	}

	public void setDateProperty(Date dateProperty) {
		this.dateProperty = dateProperty;
	}

	public NestedBean getBeanProperty() {
		return beanProperty;
	}

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public String[] getStringArrayProperty() {
		return stringArrayProperty;
	}

	public void setStringArrayProperty(String[] stringArrayProperty) {
		this.stringArrayProperty = stringArrayProperty;
	}

	public Integer[] getIntegerArrayProperty() {
		return integerArrayProperty;
	}

	public void setIntegerArrayProperty(Integer[] integerArrayProperty) {
		this.integerArrayProperty = integerArrayProperty;
	}

	public int[] getPrimitiveArrayProperty() {
		return primitiveArrayProperty;
	}

	public void setPrimitiveArrayProperty(int[] primitiveArrayProperty) {
		this.primitiveArrayProperty = primitiveArrayProperty;
	}

	public List<Object> getListProperty() {
		return listProperty;
	}

	public void setListProperty(List<Object> listProperty) {
		this.listProperty = listProperty;
	}

	public Map<Object, Object> getMapProperty() {
		return mapProperty;
	}

	public void setMapProperty(Map<Object, Object> mapProperty) {
		this.mapProperty = mapProperty;
	}

	public void setBeanProperty(NestedBean beanProperty) {
		this.beanProperty = beanProperty;
	}
	
	public static class NestedBean {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}