package de.boris.mavendep2plantuml.templates;

public class TemplateVariable {
	private String name;
	private String value;
	
	public TemplateVariable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String key) {
		this.name = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static TemplateVariable from(String name, String value) {
		return new TemplateVariable(name, value);
	}
}
