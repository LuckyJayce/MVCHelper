package com.shizhefei.test.models.enties;

public class User {
	private String uid;
	private String name;
	private int age;
	private String info;

	public User() {
		super();
	}

	public User(String uid, String name, int age, String info) {
		super();
		this.uid = uid;
		this.name = name;
		this.age = age;
		this.info = info;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
