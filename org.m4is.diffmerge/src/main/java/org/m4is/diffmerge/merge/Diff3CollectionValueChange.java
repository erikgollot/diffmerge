package org.m4is.diffmerge.merge;

import java.lang.reflect.Field;
import java.util.List;

public class Diff3CollectionValueChange extends Diff3ValueChange {
	boolean leftOrderChanged = false;
	boolean rightOrderChanged = false;
	
	List<Object> leftAddedItems;
	List<Object> leftRemovedItems;
	
	List<Object> rightAddedItems;
	List<Object> rightRemovedItems;
	
	public Diff3CollectionValueChange(Object baseValue, Object leftValue,
			Object rightValue, Field field, String opposite, boolean isConflict) {
		super(baseValue, leftValue, rightValue, field, opposite, isConflict);
		// TODO Auto-generated constructor stub
	}

	public boolean isLeftOrderChanged() {
		return leftOrderChanged;
	}

	public void setLeftOrderChanged(boolean leftOrderChanged) {
		this.leftOrderChanged = leftOrderChanged;
	}

	public boolean isRightOrderChanged() {
		return rightOrderChanged;
	}

	public void setRightOrderChanged(boolean rightOrderChanged) {
		this.rightOrderChanged = rightOrderChanged;
	}

	public List<Object> getLeftAddedItems() {
		return leftAddedItems;
	}

	public void setLeftAddedItems(List<Object> leftAddedItems) {
		this.leftAddedItems = leftAddedItems;
	}

	public List<Object> getLeftRemovedItems() {
		return leftRemovedItems;
	}

	public void setLeftRemovedItems(List<Object> leftRemovedItems) {
		this.leftRemovedItems = leftRemovedItems;
	}

	public List<Object> getRightAddedItems() {
		return rightAddedItems;
	}

	public void setRightAddedItems(List<Object> rightAddedItems) {
		this.rightAddedItems = rightAddedItems;
	}

	public List<Object> getRightRemovedItems() {
		return rightRemovedItems;
	}

	public void setRightRemovedItems(List<Object> rightRemovedItems) {
		this.rightRemovedItems = rightRemovedItems;
	}

}
