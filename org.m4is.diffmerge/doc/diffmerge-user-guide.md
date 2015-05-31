# DiffMerge - User Guide #
## Objective ##
**DiffMerge** is an api that automatically create a diff of 2 versions of an object graph and help you to do a merge from 3 version of an object graph (base version, left/old version and right/new version). It supports the following types  for your object attributes: **primitive java types**, standard **object references**, **collections** and **array** of objects. The usage is mainly for objects that are stored into a relational database using JPA annotations or not. Because in such cases, no Map or complex containers are used, DiffMerge does not support these kind of object containers.

## Object identity ##
In DiffMerge, **object identity** is use to know if we're comparing two versions of the same object. This identity cannot be the java object reference itself, of course but cannot always be the attribute annoted with @Id JPA annotation because we suppose that both versions of an object can be stored into the database. In that cas, your 2 versions will have a different database primary key (the @Id annotated attribute). So, it means that DiffMerge expect that you've a dedicated attribute or method that represents the **business identity** of an object. This business identity does not change between two versions of an object.
To mark an attribute (and only one) or a method (and only one) as your business identity, just use the **@BusinessReference** annotation. 

> Notes that if you do not expect to store your 2 versions and just want to make a diff of an already stored version and, for example, a new version received from a client (when in the client UI, the user of your application change some objects), you can annotate the attribute already annotated with @Id with @BusinessReference.

## Ignoring attributes ##
You probably don't want that DiffMerge use all the attributes of an object during its diff process. Some attributes like the one you annotate with @Id or @Version or any other attributes not necessarely annotated with JPA annotations can be excluded. To do that, just annotate them with **@DiffMergeIgnore**.
> **Notes** that when you ignore an attribute, DiffMerge will never access this attribute, preventing any lazy loading if you use JPA provider like Hibernate. In that case, no database access is triggered for this attribute.

Sometimes, you want to do a diffmerge in different contexts. In context A you want to ignore some attributes, but not in context B. So to specifically ignore an attribute in a context A, use @DiffMergeIgnore like this:

    @DiffMergeIgnore(inContext="A")
    private String myAttribute;

If your situation is more, "I want to exclude myAttribute expect in context B", do:

    @DiffMergeIgnore(excludingContext="B")
    private String myAttribute;

You can mix inContext and excludingContext.

The context will be set when you'll start a diffmerge operation at runtime.

## Managing relationships ##
Your object graph is a set of objects linked by relations. In Java you use object references and collection objects (or array). If you use JPA, you also annotate your attributes with annotation such as @OneToMany, @OneToOne or @ManyToMany. If you use theses annotations, DiffMerge will automatically detect them. To be able to help you to do a consistent merge, DiffMerge needs to know both sides of the relations. Meaning that if in a parent-child relationship you decide to keep the child in the list of the children of the parent, DiffMerge prevent you to choose another parent for the child attribute that represents the parent. To do that, DiffMerge detects the **mappedBy** attribute of the JPA annotations. If you do not want to use the mappedBy attribute for any reason or if you do not use JPA annotations, use the **@Association** annotation and its **opposite** attribute. The opposite attribute works like the mappedBy attribute in JPA. 


> You can of course uses both JPA annotations and @Association on the same attribute. But if you use the mappedBy attribute, the opposite attribute will be ignored.

## Accessors ##
DiffMerge do not access field directly, you **must provide a get accessor** using JavaBean convention.

## Usage ##
### Annotate your classes ###
A very basic entity

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

Entities linked with a **one-to many** relationship and one class with an **annoted method for its business identity**.

    public class EntityWithRelation {
	  @BusinessReference
	  String businessEntity;
	  String name;
	  int age;
	  @Association
	  List<EntityLinked> children;
	
	  public List<EntityLinked> getChildren() {
		return children;
	  }
	  public void addLink(EntityLinked l) {
		if (getChildren()==null) {
			this.children=new ArrayList<EntityLinked>();
		}
		getChildren().add(l);
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

    public class EntityLinked {
	  @BusinessReference
	  String businessEntity;
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
	  public String getBusinessEntity() {
		return businessEntity;
	  }
	  public String getName() {
		return name;
	  }
	  public EntityLinked(String businessEntity, String name, float salary) {
		super();
		this.businessEntity = businessEntity;
		this.name = name;
		this.salary = salary;
	  }
    }

    public class Application {
	  String name;
      @BusinessReference
	  String businessIdentity;

	  public Application(String businessIdentity, String name) {
		super();
		this.businessIdentity = businessIdentity;
		this.name = name;
	  }
	  public String getName() {
		return name;
	  }
	  public String getBusinessIdentity() {
		return businessIdentity;
	  }
    }

    public class ApplicationVersion {
	  @DiffMergeIgnore
	  Application app;
      String version;

	  public ApplicationVersion(Application app, String version) {
		super();
		this.app = app;
		this.version = version;
	  }
	  public Application getApp() {
		return app;
	  }
	  public String getVersion() {
		return version;
	  }
	  @BusinessReference
	  public String getBusinessIdentity() {
		return getApp().getBusinessIdentity();
	  }
    }

### Launch a diff ###
Without context specification

    DiffMergeService service = new DiffMergeService();
    List<Diff> differences = service.compare(objectVersion1, objectVersion2);

With context specification

    DiffMergeService service = new DiffMergeService("A");
    List<Diff> differences = service.compare(objectVersion1, objectVersion2);

### Inspect diffs ###

    for (Diff diff : differences) {
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

### Manage a merge ###
