package org.m4is.diffmerge.reflection;

public class AssociationInfo {
	boolean isCollection;
	boolean isBidirectional;
	String opposite;

	public AssociationInfo(boolean isCollection,
			boolean isBidirectional, String opposite) {
		super();
		this.isCollection = isCollection;
		this.isBidirectional = isBidirectional;
		this.opposite = opposite;
	}

	public boolean isBidirectional() {
		return isBidirectional;
	}

	public String getOpposite() {
		return opposite;
	}	

	public void setBidirectional(boolean isBidirectional) {
		this.isBidirectional = isBidirectional;
	}

	public void setOpposite(String opposite) {
		this.opposite = opposite;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}
}
