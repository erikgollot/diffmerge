package org.m4is.diffmerge.merge;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.m4is.diffmerge.diff.CollectionValueChange;
import org.m4is.diffmerge.diff.DataPrimitiveValueChange;
import org.m4is.diffmerge.diff.DeletedObject;
import org.m4is.diffmerge.diff.Diff;
import org.m4is.diffmerge.diff.Differences;
import org.m4is.diffmerge.diff.EntitySwitchValueChange;
import org.m4is.diffmerge.diff.IDiffEntity;
import org.m4is.diffmerge.diff.ModifiedObject;
import org.m4is.diffmerge.diff.NewObject;
import org.m4is.diffmerge.diff.ValueChange;
import org.m4is.diffmerge.reflection.ReflectionUtil;

/**
 * A class to manage Diff 3 merge information
 * 
 * @author 278144
 * 
 */
public class Diff3Merge{

	Differences		left;
	Differences		right;
	ReflectionUtil	reflection;

	/**
	 * 
	 * @param left
	 *            result of a compare between "base version" and "left version"
	 * @param right
	 *            result of a compare between "base version" and "right version"
	 */
	public Diff3Merge(Differences left , Differences right , ReflectionUtil reflection) {
		super();
		this.left = left;
		this.right = right;
		this.reflection = reflection;
	}

	boolean					isAnalysisDone				= false;
	// This list contains differences that do not belong to the same objects.
	// They've been found in left and right differences but none are both in
	// left or right.
	List<Diff>				nonConflictingDifferences	= new ArrayList<Diff>();
	// Here we have differences on the same objects, objects that hve been
	// modified in left and right. Diffs will be analyzed so that they will be
	// marked as conflicting or not. They're also grouped by object so that
	// choosing from left or right is easier.
	List<Diff3ValueChange>	sameObjetChanges			= new ArrayList<Diff3ValueChange>();

	public List<Diff> getNonConflictingDifferences() {
		if (!isAnalysisDone) {
			analyze();
		}
		return nonConflictingDifferences;
	}

	public List<Diff3ValueChange> getSameObjetChanges() {
		if (!isAnalysisDone) {
			analyze();
		}
		return sameObjetChanges;
	}

	public void setSameObjetChanges(List<Diff3ValueChange> sameObjetChanges) {
		this.sameObjetChanges = sameObjetChanges;
	}

	private void analyze() {
		isAnalysisDone = true;
		// Create map of pair differences based on Class and BusinessReference
		Map<Class<?>, List<PairDiff>> map = new Hashtable<Class<?>, List<PairDiff>>();
		List<Diff> rightAlreadyAnalyzed = new ArrayList<Diff>();
		findPotentialConflictsAndDispatchOthers(map,rightAlreadyAnalyzed);
		analyzePotentialConflicts(map);
	}

	private void analyzePotentialConflicts(Map<Class<?>, List<PairDiff>> map) {
		// Real conflicts are those where the same field has changed with a
		// different value in both diff objects.
		// If a pair is not in conflict, dispatch both elements
		for (Class<?> clazzKey : map.keySet()) {
			List<PairDiff> diffs = map.get(clazzKey);
			for (PairDiff diff : diffs) {
				analyzePair(clazzKey,diff);
			}
		}
	}

	/**
	 * Analyze a potential conflict form a pair diff
	 * 
	 * @param clazzKey
	 * @param diff
	 */
	private void analyzePair(Class<?> clazzKey,PairDiff diff) {
		// TODO Auto-generated method stub

		// We've NewObject or ModifiedObject pairs
		if (diff.getLeft() instanceof ModifiedObject) {
			ModifiedObject left = (ModifiedObject) diff.getLeft();
			ModifiedObject right = (ModifiedObject) diff.getRight();

			for (Diff leftDiff : left.getModifications()) {
				if (leftDiff instanceof ValueChange) {
					ValueChange leftVC = (ValueChange) leftDiff;
					Diff rightDiff = right.getModificationForField(leftVC.getField());
					if (rightDiff != null) {
						// Need to check newValues on both sides
						if (leftDiff instanceof EntitySwitchValueChange && rightDiff instanceof EntitySwitchValueChange) {
							EntitySwitchValueChange esLeft = (EntitySwitchValueChange) leftDiff;
							EntitySwitchValueChange esRight = (EntitySwitchValueChange) rightDiff;
							Diff3ValueChange diff3 = new Diff3ValueChange(esLeft.getOldValue(),esLeft.getNewValue(),esRight.getNewValue(),esLeft.getField(),
									esLeft.getFieldInfo().getOpposite(),inConflict(esLeft,esRight));
							sameObjetChanges.add(diff3);
						} else if (leftDiff instanceof DataPrimitiveValueChange && rightDiff instanceof DataPrimitiveValueChange) {
							DataPrimitiveValueChange dpLeft = (DataPrimitiveValueChange) leftDiff;
							DataPrimitiveValueChange dpRight = (DataPrimitiveValueChange) rightDiff;
							Diff3ValueChange diff3 = new Diff3ValueChange(dpLeft.getOldValue(),dpLeft.getNewValue(),dpRight.getNewValue(),dpLeft.getField(),
									dpLeft.getFieldInfo().getOpposite(),inConflict(dpLeft,dpRight));
							sameObjetChanges.add(diff3);
						} else if (leftDiff instanceof CollectionValueChange && rightDiff instanceof CollectionValueChange) {
							CollectionValueChange clLeft = (CollectionValueChange) leftDiff;
							CollectionValueChange clRight = (CollectionValueChange) rightDiff;
							// TODO
						} else if (leftDiff instanceof ModifiedObject && rightDiff instanceof ModifiedObject) {
							ModifiedObject moLeft = (ModifiedObject) leftDiff;
							ModifiedObject moRight = (ModifiedObject) rightDiff;
							// TODO
						}

					}
				} else if (leftDiff instanceof ModifiedObject) {
					// In case of ValueObject changes
					// TODO
					ModifiedObject moLeft = (ModifiedObject) leftDiff;
				}
			}

		} else if (diff.getLeft() instanceof NewObject) {
			NewObject left = (NewObject) diff.getLeft();
			NewObject right = (NewObject) diff.getRight();
			// TODO
		} else {
			// should not happen
		}
	}

	private boolean inConflict(DataPrimitiveValueChange dpLeft,DataPrimitiveValueChange dpRight) {
		if (dpLeft.getNewValue() == null && dpRight.getNewValue() == null) {
			return false;
		} else if ((dpLeft.getNewValue() == null && dpRight.getNewValue() != null) || (dpLeft.getNewValue() != null && dpRight.getNewValue() == null)) {
			return true;
		} else if (dpLeft.getNewValue().equals(dpRight.getNewValue())) {
			return false;
		} else {
			return true;
		}
	}

	private boolean inConflict(EntitySwitchValueChange esLeft,EntitySwitchValueChange esRight) {
		Object leftBr = esLeft.getNewBusinessReference(getReflection());
		Object rightBr = esRight.getNewBusinessReference(getReflection());
		if (leftBr == null && rightBr == null) {
			return false;
		} else {
			if ((leftBr == null && rightBr != null) || (leftBr != null && rightBr == null)) {
				return true;
			} else {
				if (leftBr.equals(rightBr)) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	/**
	 * Add a left and right diff in the list sameObjetChanges. The Diff3 object
	 * will contains non conflics and conflicts differences belonging to the
	 * same object.
	 * 
	 * @param leftDiff
	 * @param rightDiff
	 * @param clazz
	 */
	private void addDiff3ValueChange(Diff leftDiff,Diff rightDiff,Class<?> clazz) {
		// TODO Auto-generated method stub

	}

	private void findPotentialConflictsAndDispatchOthers(Map<Class<?>, List<PairDiff>> map,List<Diff> rightAlreadyAnalyzed) {
		if (left.size() > 0) {
			if (right.size() == 0) {
				// Right is empty, every thing is not in conflict
				for (Diff diff : left.getDifferences()) {
					nonConflictingDifferences.add(diff);
				}
			} else {
				// Check non conflict and potential conflicts (store into map
				// for further analysis)
				for (Diff diff : left.getDifferences()) {
					if (diff instanceof ModifiedObject) {
						ModifiedObject otherInRight = right.getSameModificationDifference((ModifiedObject) diff);
						if (otherInRight == null) {
							nonConflictingDifferences.add(diff);
						} else {
							storeForPotentialConflict(map,(ModifiedObject) diff,otherInRight);
							rightAlreadyAnalyzed.add(otherInRight);
						}
					} else {
						if (diff instanceof DeletedObject) {
							// If on left side we've a deleted object but we
							// find a ModifiedObject for the same entity on
							// right side, we've
							// a conflict. If find also a DeletedObject, no
							// conflict.
							DeletedObject leftDeleted = (DeletedObject) diff;
							Diff rigthDeletedOrModified = right.getSameDeletedOrModified(leftDeleted);
							if (rigthDeletedOrModified != null) {
								rightAlreadyAnalyzed.add(rigthDeletedOrModified);
								if (rigthDeletedOrModified instanceof DeletedObject) {
									nonConflictingDifferences.add(leftDeleted);
								} else {
									// We found a ModifiedObject for the same
									// entity...conflict
									addDiff3ValueChange(diff,rigthDeletedOrModified,((DeletedObject) diff).getClazz());
								}
							}
						} else if (diff instanceof NewObject) {
							// It's not possible to have the same new object
							// ?...hum not so sure. So look up on right side and
							// analyze if there is a conflict later.
							NewObject rightDiff = right.getSameNewObject((NewObject) diff);
							if (rightDiff == null) {
								nonConflictingDifferences.add(diff);
							} else {
								storeForPotentialConflict(map,(NewObject) diff,rightDiff);
								rightAlreadyAnalyzed.add(rightDiff);
							}
						}
					}
				}
			}
		}
		// Now right
		if (right.size() > 0) {
			if (left.size() == 0) {
				// Nothing in conflict
				for (Diff diff : left.getDifferences()) {
					nonConflictingDifferences.add(diff);
				}
			} else {
				// We've found potential conflicts before. Not processed Diff
				// here, the ones that are not in rightAlreadyAnalyzed are
				// necessarily not in conflict
				for (Diff diff : left.getDifferences()) {
					if (!rightAlreadyAnalyzed.contains(diff)) {
						nonConflictingDifferences.add(diff);
					}
				}
			}
		}
	}

	private void storeForPotentialConflict(Map<Class<?>, List<PairDiff>> map,IDiffEntity left,IDiffEntity right) {
		List<PairDiff> pairs = map.get(left.getClazz());
		if (pairs == null) {
			pairs = new ArrayList<PairDiff>();
			map.put(left.getClazz(),pairs);
		}
		pairs.add(new PairDiff(left,right,left.getClazz()));
	}

	public ReflectionUtil getReflection() {
		return reflection;
	}
}
