package test.org.m4is.diffmerge.model;

import org.m4is.diffmerge.stereotype.DiffMergeIgnore;

public class EntityLinked extends BaseEntity implements Comparable {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String name;

	@DiffMergeIgnore
	float salary;

	

	ApplicationVersion appVersion;

	public ApplicationVersion getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(ApplicationVersion appVersion) {
		this.appVersion = appVersion;
	}

	public EntityLinked(String businessEntity, String name, float salary) {
		super(businessEntity);
		this.name = name;

		this.salary = salary;
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
