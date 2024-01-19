package com.intelizign.documenttailoring;

import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.IWorkItem;

public class MappingHeadingPojo {

	private String id;
	private String moduleName;
	private String title;

	public MappingHeadingPojo(IWorkItem id, IModule name, String wiTitle) {
		this.id = id.getId();
		this.moduleName = name.getModuleName();
		this.title = wiTitle;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getWiTitle() {
		return title;
	}

	public void setWiTitle(String wiTitle) {
		this.title = wiTitle;
	}

}
