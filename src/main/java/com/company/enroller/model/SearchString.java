package com.company.enroller.model;

public class SearchString {
	private String title;
	private String description;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	// konstruktor pusty
	public SearchString() {
		super();
	}
	
	// konstruktor z parametrami
	public SearchString(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}
	
	
}
