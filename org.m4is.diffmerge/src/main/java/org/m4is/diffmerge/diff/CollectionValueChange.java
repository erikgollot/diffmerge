package org.m4is.diffmerge.diff;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.m4is.diffmerge.reflection.FieldInfo;

public class CollectionValueChange extends ValueChange {
	// Does the order of items has changed into collection
	boolean orderChanged = false;
	// items added in newValue compared to oldValue
	List<Object> addedItems;
	// items removed from oldValue
	List<Object> removedItems;
	boolean isSomethingDifferent = false;

	public boolean isSomethingDifferent() {
		return isSomethingDifferent;
	}

	protected void setSomethingDifferent(boolean isSomethingDifferent) {
		this.isSomethingDifferent = isSomethingDifferent;
	}

	public CollectionValueChange(Field field,FieldInfo fieldInfo, Object oldValue, Object newValue,
			String opposite) {
		super(field, fieldInfo,oldValue, newValue, opposite);
	}

	public void addAddedItem(Object o) {
		if (getAddedItems() == null) {
			this.addedItems = new ArrayList<Object>();
		}
		getAddedItems().add(o);
		setSomethingDifferent(true);
	}

	public void addRemovedItem(Object o) {
		if (getRemovedItems() == null) {
			this.removedItems = new ArrayList<Object>();
		}
		getRemovedItems().add(o);
		setSomethingDifferent(true);
	}

	public boolean isOrderChanged() {
		return orderChanged;
	}

	public List<Object> getAddedItems() {
		return addedItems;
	}

	public List<Object> getRemovedItems() {
		return removedItems;
	}

	public void setOrderChanged(boolean orderChanged) {
		this.orderChanged = orderChanged;
		setSomethingDifferent(orderChanged == true);
	}

	public Class<?> getClazz() {
		// No need
		return null;
	}
}
