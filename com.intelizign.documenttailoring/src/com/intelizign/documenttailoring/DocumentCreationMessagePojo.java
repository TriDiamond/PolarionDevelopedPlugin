package com.intelizign.documenttailoring;

public class DocumentCreationMessagePojo {
	private String moduleName;
	private String title;
	private String space;
	private String projectId;
	private String moduleType;

	public DocumentCreationMessagePojo(String moduleName, String  title, String space, String projectId ,String type) {
		this.moduleName = moduleName;
		this.title = title;
		this.space = space;
		this.projectId =projectId;
		this.moduleType =type;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getTitle() {
		return title;
	}

	public String getSpace() {
		return space;
	}
	
	public String getProjectId() {
		return projectId;
	}
	public String getModuleType() {
		return moduleType;
	}
}
