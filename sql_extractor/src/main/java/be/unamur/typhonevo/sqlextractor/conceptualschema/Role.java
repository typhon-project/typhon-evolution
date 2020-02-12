package be.unamur.typhonevo.sqlextractor.conceptualschema;

import java.io.Serializable;
import java.util.List;

import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Group;

public class Role  implements Serializable {

	
	private int minCard;
	private int maxCard;
	private Group group = null;
	
	private String name;
	private String tmlName;
	
	public Role(int minCard, int maxCard, String name) {
		this(null, minCard, maxCard);
		setName(name);
	}
	
	public Role(Group group, int minCard, int maxCard) {
		this.setGroup(group);
		this.setMinCard(minCard);
		this.setMaxCard(maxCard);
	}

	public int getMinCard() {
		return minCard;
	}

	public void setMinCard(int minCard) {
		this.minCard = minCard;
	}

	public int getMaxCard() {
		return maxCard;
	}

	public void setMaxCard(int maxCard) {
		this.maxCard = maxCard;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public boolean isMonoColumn() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTmlName() {
		return tmlName;
	}

	public void setTmlName(String tmlName) {
		this.tmlName = tmlName;
	}

}
