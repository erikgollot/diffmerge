package test.org.m4is.diffmerge.model;

import org.m4is.diffmerge.stereotype.BusinessReference;

public class BaseEntity {
	public String getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(String businessEntity) {
		this.businessEntity = businessEntity;
	}

	@BusinessReference
	String businessEntity;
	EntityWithRelation parent;

	public void setParent(EntityWithRelation parent) {
		this.parent = parent;
	}

	public EntityWithRelation getParent() {
		return parent;
	}
	public BaseEntity(String businessEntity) {
		super();
		this.businessEntity = businessEntity;
	}
}
