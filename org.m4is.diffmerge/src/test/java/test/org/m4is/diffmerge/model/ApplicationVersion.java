package test.org.m4is.diffmerge.model;

import org.m4is.diffmerge.stereotype.BusinessReference;
import org.m4is.diffmerge.stereotype.DiffMergeIgnore;

public class ApplicationVersion {
	@DiffMergeIgnore
	Application app;

	public ApplicationVersion(Application app, String version) {
		super();
		this.app = app;
		this.version = version;
	}


	public Application getApp() {
		return app;
	}


	public String getVersion() {
		return version;
	}

	String version;
	
	@BusinessReference
	public String getBusinessIdentity() {
		return getApp().getBusinessIdentity();
	}
}
