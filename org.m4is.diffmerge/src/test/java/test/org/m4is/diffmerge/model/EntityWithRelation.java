package test.org.m4is.diffmerge.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.m4is.diffmerge.stereotype.Association;
import org.m4is.diffmerge.stereotype.BusinessReference;

public class EntityWithRelation {
	@BusinessReference
	String businessEntity;
	String name;
	int age;
	
	@Association(opposite="parent")
	Set<BaseEntity> children;
	
	public Set<BaseEntity> getChildren() {
		return children;
	}
	public void addLink(BaseEntity l) {
		if (getChildren()==null) {
			this.children=new LinkedHashSet<BaseEntity>();
		}
		getChildren().add(l);
		l.setParent(this);
	}
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getBusinessEntity() {
		return businessEntity;
	}

	public String getName() {
		return name;
	}

	public EntityWithRelation(String businessEntity, String name,int age) {
		super();
		this.businessEntity = businessEntity;
		this.name = name;
		this.age=age;
		
	}

}
