package test.org.m4is.diffmerge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.m4is.diffmerge.diff.CollectionValueChange;
import org.m4is.diffmerge.diff.DataPrimitiveValueChange;
import org.m4is.diffmerge.diff.DeletedObject;
import org.m4is.diffmerge.diff.Diff;
import org.m4is.diffmerge.diff.Differences;
import org.m4is.diffmerge.diff.EntitySwitchValueChange;
import org.m4is.diffmerge.diff.ModifiedObject;
import org.m4is.diffmerge.diff.NewObject;
import org.m4is.diffmerge.service.DiffMergeService;

import test.org.m4is.diffmerge.model.Application;
import test.org.m4is.diffmerge.model.ApplicationVersion;
import test.org.m4is.diffmerge.model.BasicEntity;
import test.org.m4is.diffmerge.model.EntityLinked;
import test.org.m4is.diffmerge.model.EntityWithRelation;
import test.org.m4is.diffmerge.model.uml.Edge;
import test.org.m4is.diffmerge.model.uml.EndNode;
import test.org.m4is.diffmerge.model.uml.OpaqueNode;
import test.org.m4is.diffmerge.model.uml.StartNode;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testBasicEntityEquals() {
		BasicEntity e1 = new BasicEntity("1", "Erik", 1, 2.0f);
		BasicEntity e2 = new BasicEntity("1", "Erik", 1, 3.0f);
		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() == 0);
	}

	public void testBasicEntityOneAttributeNotEquals() {
		BasicEntity e1 = new BasicEntity("1", "Erik", 1, 2.0f);
		BasicEntity e2 = new BasicEntity("1", "Alex", 1, 2.0f);
		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() > 0);
	}

	public void testBasicEntityTwoAttributesNotEquals() {
		BasicEntity e1 = new BasicEntity("1", "Erik", 1, 2.0f);
		BasicEntity e2 = new BasicEntity("1", "Alex", 2, 3.0f);
		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() > 0);
	}

	public void testEntityWithRelationEquals() {
		EntityWithRelation e1 = new EntityWithRelation("1", "Erik", 1);
		EntityWithRelation e2 = new EntityWithRelation("1", "Erik", 1);
		EntityLinked child11 = new EntityLinked("2", "Alex", 1);

		e1.addLink(child11);
		e2.addLink(child11);
		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() == 0);

	}

	public void testEntityWithRelationEquals2() {
		EntityWithRelation e1 = new EntityWithRelation("1", "Erik", 1);
		EntityWithRelation e2 = new EntityWithRelation("1", "Erik", 1);
		EntityLinked child11 = new EntityLinked("2", "Alex", 1);
		EntityLinked child21 = new EntityLinked("2", "Alex", 1);
		e1.addLink(child11);
		e2.addLink(child21);
		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() == 0);

	}

	public void testEntityWithRelationNotEqualsInChildList() {
		EntityWithRelation e1 = new EntityWithRelation("1", "Erik", 1);
		EntityWithRelation e2 = new EntityWithRelation("1", "Erik", 1);
		EntityLinked child11 = new EntityLinked("2", "Alex", 1);
		EntityLinked child21 = new EntityLinked("3", "Alex", 1);
		e1.addLink(child11);
		e2.addLink(child21);
		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() > 0);

	}

	public void testEntityWithRelationWithOrderChangedOnly() {
		EntityWithRelation e1 = new EntityWithRelation("1", "Erik", 1);
		EntityWithRelation e2 = new EntityWithRelation("1", "Erik", 1);

		EntityLinked child11 = new EntityLinked("2", "Alex", 1);
		EntityLinked child21 = new EntityLinked("3", "Alex", 1);
		e1.addLink(child11);
		e1.addLink(child21);

		EntityLinked child11bis = new EntityLinked("2", "Alex", 1);
		EntityLinked child21bis = new EntityLinked("3", "Alex", 1);
		e2.addLink(child21bis);
		e2.addLink(child11bis);

		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() > 0);

	}

	public void testEntityWithRelationAndParentChange() {
		EntityWithRelation e1 = new EntityWithRelation("1", "Erik", 1);
		EntityWithRelation e2 = new EntityWithRelation("2", "Alex", 1);

		EntityLinked child11 = new EntityLinked("2", "Toto", 1);
		EntityLinked child21 = new EntityLinked("3", "Tutu", 1);
		e1.addLink(child11);
		e1.addLink(child21);

		EntityLinked child11bis = new EntityLinked("2", "Toto 2", 1);
		EntityLinked child21bis = new EntityLinked("3", "Tutu", 1);
		e2.addLink(child21bis);
		e2.addLink(child11bis);

		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);
		assertTrue(differences.size() > 0);

	}

	public void testEntityWithRefAsMethodNotEquals() {
		Application app = new Application("1", "app 01");
		ApplicationVersion v1 = new ApplicationVersion(app, "1.0.0");
		ApplicationVersion v2 = new ApplicationVersion(app, "2.0.0");

		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(v1, v2);
		assertTrue(differences.size() > 0);
	}

	public void testEntityWithRefAsMethodEquals() {
		Application app = new Application("1", "app 01");
		ApplicationVersion v1 = new ApplicationVersion(app, "1.0.0");
		ApplicationVersion v2 = new ApplicationVersion(app, "1.0.0");

		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(v1, v2);
		assertTrue(differences.size() == 0);
	}

	public void testEntityWithRefAsMethodNotEqualsWithComplexGraph() {
		Application app = new Application("1", "app 01");
		ApplicationVersion v1 = new ApplicationVersion(app, "1.0.0");
		ApplicationVersion v2 = new ApplicationVersion(app, "3.0.0");

		EntityWithRelation e1 = new EntityWithRelation("1", "Erik", 1);
		EntityWithRelation e2 = new EntityWithRelation("1", "Erik", 1);
		EntityLinked child11 = new EntityLinked("2", "Alex", 1);
		EntityLinked child21 = new EntityLinked("3", "Alex", 1);
		e1.addLink(child11);
		e2.addLink(child21);

		child11.setAppVersion(v1);
		child21.setAppVersion(v2);

		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(e1, e2);

		for (Diff diff : differences.getDifferences()) {
			if (diff instanceof NewObject) {
				// This is a new object in the graph. Will also be detected
				// elsewhere if it has been added to collections of other entity
				NewObject newObject = (NewObject) diff;
			} else if (diff instanceof DeletedObject) {
				// This is a deleted object in the graph. Will also be detected
				// elsewhere if it has been deleted from collections of other
				// entity
				DeletedObject deletedObject = (DeletedObject) diff;
			} else if (diff instanceof ModifiedObject) {
				ModifiedObject modifiedObject = (ModifiedObject) diff;
				System.out.println(modifiedObject.getOldObject());
				System.out.println(modifiedObject.getNewObject());
				// Lookup modifications of the object
				for (Diff modification : modifiedObject.getModifications()) {
					if (modification instanceof DataPrimitiveValueChange) {
						// A primitive attribute value has changed
					} else if (modification instanceof EntitySwitchValueChange) {
						// A one to one reference to another entity has changed
					} else if (modification instanceof ModifiedObject) {
						// A one to one reference to another valueobject has
						// changed. It's when you reference a object that does
						// not have a business reference. This object is
						// considered as a valueobject. ValueObject are the same
						// if all their attributes have the same value. It can
						// be a recursive check is your valueobject is also a
						// graph of valueobject. Recursive process stops when an
						// entity reference is reached or if this is the end of
						// the graph.
					} else if (modification instanceof CollectionValueChange) {
						// A collection or array attribute has changed.
						// You can see added and removed objects. If order has
						// changed it's also detected.
					}

				}
			}
		}
		assertTrue(differences.size() > 0);
	}

	public void testUMLActivityDiagram() {
		// Version 1
		StartNode sv1 = new StartNode(1L, "001", "start");
		EndNode ev1 = new EndNode(2L, "002", "end");
		OpaqueNode o1v1 = new OpaqueNode(3L, "003", "Do it yourself");
		Edge s1o1v1 = new Edge(4L, "004", "go");
		sv1.addOutgoing(s1o1v1);
		o1v1.addIncoming(s1o1v1);
		OpaqueNode o2v1 = new OpaqueNode(5L, "005", "Do it yourself 2");
		Edge o1o2v1 = new Edge(6L, "006", "go 2");
		o1v1.addOutgoing(o1o2v1);
		o2v1.addIncoming(o1o2v1);

		Edge o2ev1 = new Edge(7L, "007", "go to end");
		o2v1.addOutgoing(o2ev1);
		ev1.addIncoming(o2ev1);

		// Version 2
		StartNode sv2 = new StartNode(1L, "001", "start");
		EndNode ev2 = new EndNode(2L, "002", "end");
		OpaqueNode o1v2 = new OpaqueNode(3L, "003", "Do it yourself");
		Edge s1o1v2 = new Edge(4L, "004", "go");
		sv2.addOutgoing(s1o1v2);
		o1v2.addIncoming(s1o1v2);
		OpaqueNode o2v2 = new OpaqueNode(5L, "005", "Do it yourself 2");
		Edge o1o2v2 = new Edge(6L, "006", "go 2");
		o1v2.addOutgoing(o1o2v2);
		o2v2.addIncoming(o1o2v2);

		Edge o2ev2 = new Edge(7L, "007", "go to end");
		o2v2.addOutgoing(o2ev2);
		ev2.addIncoming(o2ev2);

		DiffMergeService service = new DiffMergeService();
		Differences differences = service.compare(sv1, sv2);

		assertTrue(differences.size() == 0);

		// Now make some changes
		ev2.addIncoming(s1o1v2);
		differences = service.compare(sv1, sv2);

		assertTrue(differences.size() > 0);
	}
}
