package test.org.m4is.diffmerge.model;

import org.m4is.diffmerge.stereotype.BusinessReference;

public class Application {
	String name;
	String businessIdentity;

	public Application(String businessIdentity, String name) {
		super();
		this.businessIdentity = businessIdentity;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@BusinessReference
	public String getBusinessIdentity() {
		return businessIdentity;
	}
}
