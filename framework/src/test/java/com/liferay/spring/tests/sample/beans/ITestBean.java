/**
 * Copyright (c) 2000-2020 the original author or authors.
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


/**
 * Interface used for {@link TestBean}.
 *
 * <p>Two methods are the same as on Person, but if this extends person it breaks quite a few tests..
 *
 * @author  Rod Johnson
 * @author  Juergen Hoeller
 */
public interface ITestBean extends AgeHolder {

	/**
	 * Throws a given (non-null) exception.
	 */
	void exceptional(Throwable t) throws Throwable;

	INestedTestBean getDoctor();

	INestedTestBean getLawyer();

	String getName();

	IndexedTestBean getNestedIndexedBean();

	int[][] getNestedIntArray();

	Integer[][] getNestedIntegerArray();

	int[] getSomeIntArray();

	Integer[] getSomeIntegerArray();

	ITestBean getSpouse();

	ITestBean[] getSpouses();

	String[] getStringArray();

	/**
	 * Increment the age by one.
	 *
	 * @return  the previous age
	 */
	int haveBirthday();

	Object returnsThis();

	void setName(String name);

	void setNestedIntArray(int[][] someNestedArray);

	void setNestedIntegerArray(Integer[][] nestedIntegerArray);

	void setSomeIntArray(int[] someIntArray);

	void setSomeIntegerArray(Integer[] someIntegerArray);

	void setSpouse(ITestBean spouse);

	void setStringArray(String[] stringArray);

	void unreliableFileOperation() throws IOException;

}
