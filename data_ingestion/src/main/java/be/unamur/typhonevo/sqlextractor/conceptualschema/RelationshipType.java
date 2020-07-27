package be.unamur.typhonevo.sqlextractor.conceptualschema;

import java.io.Serializable;

import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.FK;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Table;

public class RelationshipType implements Serializable {

	//PK
	private EntityType table1;
	//FK
	private EntityType table2;
	private Table manyToManyTable;

	//0-N
	private Role role1;
	//0-1 or 0-N
	private Role role2;
	
	private FK fk;
	
	private boolean alreadyHandled = false;

	public RelationshipType(EntityType table1, EntityType table2, Role role1, Role role2, Table manyToManyTable) {
		this.setTable1(table1);
		this.setTable2(table2);
		this.setRole1(role1);
		this.setRole2(role2);
		this.setManyToManyTable(manyToManyTable);
	}

	public boolean isManyToMany() {
		return role1.getMaxCard() > 1 && role2.getMaxCard() > 1;
	}

	public EntityType getTable1() {
		return table1;
	}

	public void setTable1(EntityType table1) {
		this.table1 = table1;
	}

	public EntityType getTable2() {
		return table2;
	}

	public void setTable2(EntityType table2) {
		this.table2 = table2;
	}

	public Role getRole1() {
		return role1;
	}

	public void setRole1(Role role1) {
		this.role1 = role1;
	}

	public Role getRole2() {
		return role2;
	}

	public void setRole2(Role role2) {
		this.role2 = role2;
	}

	public boolean isLoop() {
		return getTable1().getName().equals(getTable2().getName());
	}

	public FK getFk() {
		return fk;
	}

	public void setFk(FK fk) {
		this.fk = fk;
	}

	public boolean isAlreadyHandled() {
		return alreadyHandled;
	}

	public void setAlreadyHandled(boolean alreadyHandled) {
		this.alreadyHandled = alreadyHandled;
	}

	public Table getManyToManyTable() {
		return manyToManyTable;
	}

	public void setManyToManyTable(Table manyToManyTable) {
		this.manyToManyTable = manyToManyTable;
	}

}
