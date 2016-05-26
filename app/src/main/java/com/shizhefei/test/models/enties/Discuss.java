package com.shizhefei.test.models.enties;

public class Discuss {
	private String name;
	private String content;
	private long time;

	public Discuss(String name, String content, long time) {
		super();
		this.name = name;
		this.content = content;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
