package test.org.m4is.diffmerge.model;

import org.m4is.diffmerge.stereotype.BusinessReference;
import org.m4is.diffmerge.stereotype.DiffMergeIgnore;

public class BasicEntity {
	@BusinessReference
	String businessEntity;
	String name;
	int age;
	
	@DiffMergeIgnore
	float salary;

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

	public BasicEntity(String businessEntity, String name,int age,float salary) {
		super();
		this.businessEntity = businessEntity;
		this.name = name;
		this.age=age;
		this.salary = salary;
	}

}
