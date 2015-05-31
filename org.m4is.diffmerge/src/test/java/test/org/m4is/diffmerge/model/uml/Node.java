package test.org.m4is.diffmerge.model.uml;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import org.m4is.diffmerge.stereotype.BusinessReference;
import org.m4is.diffmerge.stereotype.DiffMergeIgnore;

public abstract class Node {
	public Node(Long id, String businessEntity, String name) {
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

	@OneToMany(mappedBy="endNode")
	List<Edge> incomings;
	
	@OneToMany(mappedBy="startNode")
	List<Edge> outgoings;

	public void addIncoming(Edge edge) {
		if (getIncomings() == null) {
			incomings = new ArrayList<Edge>();
		}
		getIncomings().add(edge);
		edge.setEndNode(this);
	}

	public void removeIncoming(Edge edge) {
		getIncomings().remove(edge);
		edge.setEndNode(null);
	}

	public void addOutgoing(Edge edge) {
		if (getOutgoings() == null) {
			outgoings = new ArrayList<Edge>();
		}
		getOutgoings().add(edge);
		edge.setStartNode(this);
	}

	public void removeOutgoing(Edge edge) {
		getOutgoings().remove(edge);
		edge.setStartNode(null);
	}

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

	public List<Edge> getIncomings() {
		return incomings;
	}

	public void setIncomings(List<Edge> incomings) {
		this.incomings = incomings;
	}

	public List<Edge> getOutgoings() {
		return outgoings;
	}

	public void setOutgoings(List<Edge> outgoings) {
		this.outgoings = outgoings;
	}
}
