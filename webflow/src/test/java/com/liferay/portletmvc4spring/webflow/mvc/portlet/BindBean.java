/*
 * Copyright (c) 2000-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.portletmvc4spring.webflow.mvc.portlet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;


/**
 * @author  Fabian Bouché
 */
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

	public NestedBean getBeanProperty() {
		return beanProperty;
	}

	public boolean getBooleanProperty() {
		return booleanProperty;
	}

	public Date getDateProperty() {
		return dateProperty;
	}

	public Integer[] getIntegerArrayProperty() {
		return integerArrayProperty;
	}

	public Integer getIntegerProperty() {
		return integerProperty;
	}

	public List<Object> getListProperty() {
		return listProperty;
	}

	public Map<Object, Object> getMapProperty() {
		return mapProperty;
	}

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public int[] getPrimitiveArrayProperty() {
		return primitiveArrayProperty;
	}

	public String[] getStringArrayProperty() {
		return stringArrayProperty;
	}

	public String getStringProperty() {
		return stringProperty;
	}

	public void setBeanProperty(NestedBean beanProperty) {
		this.beanProperty = beanProperty;
	}

	public void setBooleanProperty(boolean booleanProperty) {
		this.booleanProperty = booleanProperty;
	}

	public void setDateProperty(Date dateProperty) {
		this.dateProperty = dateProperty;
	}

	public void setIntegerArrayProperty(Integer[] integerArrayProperty) {
		this.integerArrayProperty = integerArrayProperty;
	}

	public void setIntegerProperty(Integer integerProperty) {
		this.integerProperty = integerProperty;
	}

	public void setListProperty(List<Object> listProperty) {
		this.listProperty = listProperty;
	}

	public void setMapProperty(Map<Object, Object> mapProperty) {
		this.mapProperty = mapProperty;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public void setPrimitiveArrayProperty(int[] primitiveArrayProperty) {
		this.primitiveArrayProperty = primitiveArrayProperty;
	}

	public void setStringArrayProperty(String[] stringArrayProperty) {
		this.stringArrayProperty = stringArrayProperty;
	}

	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
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
