/**
 * Copyright (c) 2000-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.spring.tests.sample.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;

import org.springframework.util.ObjectUtils;


/**
 * Simple test bean used for testing bean factories, the AOP framework etc.
 *
 * @author  Rod Johnson
 * @author  Juergen Hoeller
 * @author  Stephane Nicoll
 * @since   15 April 2001
 */
public class TestBean implements BeanNameAware, BeanFactoryAware, ITestBean, IOther, Comparable<Object> {

	private String beanName;

	private String country;

	private BeanFactory beanFactory;

	private boolean postProcessed;

	private String name;

	private String sex;

	private int age;

	private boolean jedi;

	private ITestBean spouse;

	protected ITestBean[] spouses;

	private String touchy;

	private String[] stringArray;

	private Integer[] someIntegerArray;

	private Integer[][] nestedIntegerArray;

	private int[] someIntArray;

	private int[][] nestedIntArray;

	private Date date = new Date();

	private Float myFloat = new Float(0.0);

	private Collection<? super Object> friends = new LinkedList<>();

	private Set<?> someSet = new HashSet<>();

	private Map<?, ?> someMap = new HashMap<>();

	private List<?> someList = new ArrayList<>();

	private Properties someProperties = new Properties();

	private INestedTestBean doctor = new NestedTestBean();

	private INestedTestBean lawyer = new NestedTestBean();

	private IndexedTestBean nestedIndexedBean;

	private boolean destroyed;

	private Number someNumber;

	private Colour favouriteColour;

	private Boolean someBoolean;

	private List<?> otherColours;

	private List<?> pets;

	public TestBean() {
	}

	public TestBean(String name) {
		this.name = name;
	}

	public TestBean(ITestBean spouse) {
		this.spouse = spouse;
	}

	public TestBean(List<?> someList) {
		this.someList = someList;
	}

	public TestBean(Set<?> someSet) {
		this.someSet = someSet;
	}

	public TestBean(Map<?, ?> someMap) {
		this.someMap = someMap;
	}

	public TestBean(Properties someProperties) {
		this.someProperties = someProperties;
	}

	public TestBean(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public TestBean(ITestBean spouse, Properties someProperties) {
		this.spouse = spouse;
		this.someProperties = someProperties;
	}

	/**
	 * @see  IOther#absquatulate()
	 */
	@Override
	public void absquatulate() {
	}

	@Override
	public int compareTo(Object other) {

		if ((this.name != null) && (other instanceof TestBean)) {
			return this.name.compareTo(((TestBean) other).getName());
		}
		else {
			return 1;
		}
	}

	public void destroy() {
		this.destroyed = true;
	}

	@Override
	public boolean equals(Object other) {

		if (this == other) {
			return true;
		}

		if ((other == null) || !(other instanceof TestBean)) {
			return false;
		}

		TestBean tb2 = (TestBean) other;

		return (ObjectUtils.nullSafeEquals(this.name, tb2.name) && (this.age == tb2.age));
	}

	/**
	 * @see  com.liferay.spring.tests.sample.beans.ITestBean#exceptional(Throwable)
	 */
	@Override
	public void exceptional(Throwable t) throws Throwable {

		if (t != null) {
			throw t;
		}
	}

	@Override
	public int getAge() {
		return age;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public String getBeanName() {
		return beanName;
	}

	public String getCountry() {
		return country;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public INestedTestBean getDoctor() {
		return doctor;
	}

	public Colour getFavouriteColour() {
		return favouriteColour;
	}

	public Collection<? super Object> getFriends() {
		return friends;
	}

	@Override
	public INestedTestBean getLawyer() {
		return lawyer;
	}

	public Float getMyFloat() {
		return myFloat;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IndexedTestBean getNestedIndexedBean() {
		return nestedIndexedBean;
	}

	@Override
	public int[][] getNestedIntArray() {
		return nestedIntArray;
	}

	@Override
	public Integer[][] getNestedIntegerArray() {
		return nestedIntegerArray;
	}

	public List<?> getOtherColours() {
		return otherColours;
	}

	public List<?> getPets() {
		return pets;
	}

	public String getSex() {
		return sex;
	}

	public Boolean getSomeBoolean() {
		return someBoolean;
	}

	@Override
	public int[] getSomeIntArray() {
		return someIntArray;
	}

	@Override
	public Integer[] getSomeIntegerArray() {
		return someIntegerArray;
	}

	public List<?> getSomeList() {
		return someList;
	}

	public Map<?, ?> getSomeMap() {
		return someMap;
	}

	public Number getSomeNumber() {
		return someNumber;
	}

	public Properties getSomeProperties() {
		return someProperties;
	}

	public Set<?> getSomeSet() {
		return someSet;
	}

	@Override
	public ITestBean getSpouse() {
		return this.spouse;
	}

	@Override
	public ITestBean[] getSpouses() {
		return ((spouse != null) ? new ITestBean[] { spouse } : null);
	}

	@Override
	public String[] getStringArray() {
		return stringArray;
	}

	public String getTouchy() {
		return touchy;
	}

	@Override
	public int hashCode() {
		return this.age;
	}

	@Override
	public int haveBirthday() {
		return age++;
	}

	public boolean isJedi() {
		return jedi;
	}

	public boolean isPostProcessed() {
		return postProcessed;
	}

	/**
	 * @see  com.liferay.spring.tests.sample.beans.ITestBean#returnsThis()
	 */
	@Override
	public Object returnsThis() {
		return this;
	}

	@Override
	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDoctor(INestedTestBean doctor) {
		this.doctor = doctor;
	}

	public void setFavouriteColour(Colour favouriteColour) {
		this.favouriteColour = favouriteColour;
	}

	public void setFriends(Collection<? super Object> friends) {
		this.friends = friends;
	}

	public void setJedi(boolean jedi) {
		this.jedi = jedi;
	}

	public void setLawyer(INestedTestBean lawyer) {
		this.lawyer = lawyer;
	}

	public void setMyFloat(Float myFloat) {
		this.myFloat = myFloat;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setNestedIndexedBean(IndexedTestBean nestedIndexedBean) {
		this.nestedIndexedBean = nestedIndexedBean;
	}

	@Override
	public void setNestedIntArray(int[][] nestedIntArray) {
		this.nestedIntArray = nestedIntArray;
	}

	@Override
	public void setNestedIntegerArray(Integer[][] nestedIntegerArray) {
		this.nestedIntegerArray = nestedIntegerArray;
	}

	public void setOtherColours(List<?> otherColours) {
		this.otherColours = otherColours;
	}

	public void setPets(List<?> pets) {
		this.pets = pets;
	}

	public void setPostProcessed(boolean postProcessed) {
		this.postProcessed = postProcessed;
	}

	public void setSex(String sex) {
		this.sex = sex;

		if (this.name == null) {
			this.name = sex;
		}
	}

	public void setSomeBoolean(Boolean someBoolean) {
		this.someBoolean = someBoolean;
	}

	@Override
	public void setSomeIntArray(int[] someIntArray) {
		this.someIntArray = someIntArray;
	}

	@Override
	public void setSomeIntegerArray(Integer[] someIntegerArray) {
		this.someIntegerArray = someIntegerArray;
	}

	public void setSomeList(List<?> someList) {
		this.someList = someList;
	}

	public void setSomeMap(Map<?, ?> someMap) {
		this.someMap = someMap;
	}

	public void setSomeNumber(Number someNumber) {
		this.someNumber = someNumber;
	}

	public void setSomeProperties(Properties someProperties) {
		this.someProperties = someProperties;
	}

	public void setSomeSet(Set<?> someSet) {
		this.someSet = someSet;
	}

	@Override
	public void setSpouse(ITestBean spouse) {
		this.spouse = spouse;
	}

	@Override
	public void setStringArray(String[] stringArray) {
		this.stringArray = stringArray;
	}

	public void setTouchy(String touchy) throws Exception {

		if (touchy.indexOf('.') != -1) {
			throw new Exception("Can't contain a .");
		}

		if (touchy.indexOf(',') != -1) {
			throw new NumberFormatException("Number format exception: contains a ,");
		}

		this.touchy = touchy;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public void unreliableFileOperation() throws IOException {
		throw new IOException();
	}

	public boolean wasDestroyed() {
		return destroyed;
	}

}
