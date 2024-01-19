package com.intelizign.documenttailoring;

public class SubHeadingInfoPojo {
	private String subHeadingId;
	private String subHeadingTitle;
	private String headingId;
	private String moduleName;

	public SubHeadingInfoPojo(String subHeadingId, String subHeadingTitle, String headingId, String moduleName) {
		this.subHeadingId = subHeadingId;
		this.subHeadingTitle = subHeadingTitle;
		this.headingId = headingId;
		this.moduleName = moduleName;
	}

	public String getsubHeadingId() {
		return subHeadingId;
	}

	public void setsubHeadingId(String subHeadingId) {
		this.subHeadingId = subHeadingId;
	}

	public String getsubHeadingTitle() {
		return subHeadingTitle;
	}

	public void setsubHeadingTitle(String subHeadingTitle) {
		this.subHeadingTitle = subHeadingTitle;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getHeadingId() {
		return headingId;
	}

	public void setHeadingId(String headingId) {
		this.headingId = headingId;
	}

}
