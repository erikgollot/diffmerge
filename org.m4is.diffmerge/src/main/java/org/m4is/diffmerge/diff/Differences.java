package org.m4is.diffmerge.diff;

import java.util.List;

public class Differences {
	public Differences(List<Diff> differences) {
		super();
		this.differences = differences;
	}

	List<Diff> differences;

	public List<Diff> getDifferences() {
		return differences;
	}

	public void setDifferences(List<Diff> differences) {
		this.differences = differences;
	}

	public int size() {
		return getDifferences() == null ? 0 : getDifferences().size();
	}

	public ModifiedObject getSameModificationDifference(ModifiedObject diff) {
		for (Diff d : getDifferences()) {
			if (d instanceof ModifiedObject) {
				ModifiedObject modif = (ModifiedObject) d;
				if (modif.getClazz() == diff.getClazz()
						&& modif.getBusinessReference() != null
						&& modif.getBusinessReference().equals(
								diff.getBusinessReference())) {
					return modif;
				}
			}
		}
		return null;
	}

	public Diff getSameDeletedOrModified(DeletedObject left) {
		for (Diff d : getDifferences()) {
			if (d instanceof ModifiedObject || d instanceof DeletedObject) {
				IDiffEntity right = (IDiffEntity)d;
				if (left.getClazz() == right.getClazz()
						&& left.getBusinessReference() != null
						&& left.getBusinessReference().equals(
								right.getBusinessReference())) {
					return d;
				}
			}
		}
		return null;
	}

	public NewObject getSameNewObject(NewObject left) {
		for (Diff d : getDifferences()) {
			if (d instanceof NewObject) {
				IDiffEntity right = (IDiffEntity)d;
				if (left.getClazz() == right.getClazz()
						&& left.getBusinessReference() != null
						&& left.getBusinessReference().equals(
								right.getBusinessReference())) {
					return (NewObject)d;
				}
			}
		}
		return null;
	}
}
