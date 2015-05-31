package test.org.m4is.diffmerge.model.uml;

import org.m4is.diffmerge.stereotype.BusinessReference;
import org.m4is.diffmerge.stereotype.DiffMergeIgnore;

public class Edge {
	public Edge(Long id, String businessEntity, String name) {
		super();
		this.id = id;
		this.businessEntity = businessEntity;
		this.name = name;
	}

	@BusinessReference
	String businessEntity;
	String name;
	@DiffMergeIgnore
	Long id;

	Node startNode;
	Node endNode;

	public String getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(String businessEntity) {
		this.businessEntity = businessEntity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Node getStartNode() {
		return startNode;
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}
}
