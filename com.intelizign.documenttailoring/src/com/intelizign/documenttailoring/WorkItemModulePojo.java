package com.intelizign.documenttailoring;

import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.IWorkItem;

public class WorkItemModulePojo {
	private IWorkItem key;
	private IWorkItem value;
	private IModule module;

	public WorkItemModulePojo(IWorkItem key, IWorkItem value, IModule module) {
		this.key = key;
		this.value = value;
		this.module = module;
	}

	public IWorkItem getKey() {
		return key;
	}

	public IWorkItem getValue() {
		return value;
	}

	public IModule getModule() {
		return module;
	}
}
