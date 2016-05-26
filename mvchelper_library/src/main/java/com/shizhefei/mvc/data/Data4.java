/*
 Copyright 2015 shizhefei（LuckyJayce）

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.shizhefei.mvc.data;

public class Data4<VALUE1, VALUE2, VALUE3, VALUE4> {

	private VALUE1 value1;
	private VALUE2 value2;
	private VALUE3 value3;
	private VALUE4 value4;

	public Data4() {
		super();
	}

	public Data4(VALUE1 value1, VALUE2 value2, VALUE3 value3, VALUE4 value4) {
		super();
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
	}

	public VALUE4 getValue4() {
		return value4;
	}

	public void setValue4(VALUE4 value4) {
		this.value4 = value4;
	}

	public VALUE1 getValue1() {
		return value1;
	}

	public void setValue1(VALUE1 value1) {
		this.value1 = value1;
	}

	public VALUE2 getValue2() {
		return value2;
	}

	public void setValue2(VALUE2 value2) {
		this.value2 = value2;
	}

	public VALUE3 getValue3() {
		return value3;
	}

	public void setValue3(VALUE3 value3) {
		this.value3 = value3;
	}

}
