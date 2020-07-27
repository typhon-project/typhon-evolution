package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FK  implements Serializable {
	private String fkName;
	private Table fkTable;
	private Map<Integer, GroupComponent> fkCols = new HashMap<Integer, GroupComponent>();
	private Table pkTable;
	private Map<Integer, GroupComponent> pkCols = new HashMap<Integer, GroupComponent>();

	public FK(String fkName, Table fkTable, Table pkTable) {
		this.setFkName(fkName);
		this.fkTable = fkTable;
		this.pkTable = pkTable;
	}
	

	public void createFk() {
		Group group = Extract.getGroup(fkTable, fkName, new ArrayList<GroupComponent>(fkCols.values()), null, null, true);
		group.setFk(this);
		if (fkName != null && group.getListOfUnknownConstraintName().contains(fkName)) {
			group.getListOfUnknownConstraintName().remove(fkName);
		}

	}

	public void addCol(int index, String fkCol, String pkCol) {
		if (fkCols.size() >= index) {
			System.out.println("replace fkCols elt");
		} else {
			Column fkCol_ = fkTable.findColumn(fkCol);
			Column pkCol_ = pkTable.findColumn(pkCol);

			fkCols.put(index, new GroupComponent(index, fkCol_));
			pkCols.put(index, new GroupComponent(index, pkCol_));
		}
	}

	public String getPkTableName() {
		if (pkTable == null) {
			return ("??");
		} else {
			return (pkTable.getName());
		}
	}

	public String getFkName() {
		return fkName;
	}

	public void setFkName(String fkName) {
		this.fkName = fkName;
	}

	@Override
	public String toString() {
		String res = "fk:" + fkTable.getName() + "->" + pkTable.getName() + "\n";
		for (int i = 0; i < fkCols.size(); i++) {
			String colName1 = fkCols.get(i).getComponent().getName();
			String colName2 = pkCols.get(i).getComponent().getName();
			res += colName1 + ":" + colName2 + "\n";
		}
		return res;

	}

	public boolean isMandatory() {
		for (GroupComponent gc : fkCols.values())
			if (gc.getComponent().getColumnMinCard() == 0)
				return false;

		return true;
	}


	public List<GroupComponent> getPrimaryGroup() {
		return new ArrayList<GroupComponent>(pkCols.values());
	}


	public List<GroupComponent> getFKGroup() {
		return new ArrayList<GroupComponent>(fkCols.values());
	}


	public String getFkTableName() {
		return fkTable.getName();
	}
	
	public Table getFkTable() {
		return fkTable;
	}
}
