package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group  implements Serializable {

	private String name;
	private boolean pk = false;
	private boolean unique = false;
	private boolean ref = false;
	private boolean key = false;

	private List<GroupComponent> columns = new ArrayList<GroupComponent>();
	private FK fk;

	private Set<String> listOfUnknownConstraintName = new HashSet<String>();

	public void addUnknownConstraint(String name) {
		listOfUnknownConstraintName.add(name);
	}

	public Group(String name, Boolean pk, Boolean unique, Boolean ref) {
		this.name = name;
		setPk(pk);
		setUnique(unique);
		setRef(ref);

	}

	public void addColumn(GroupComponent comp) {
		columns.add(comp);
	}

	public boolean isPk() {
		return pk;
	}

	public void setPk(Boolean pk) {
		if (pk == null)
			return;

		this.pk = pk;
		if (pk)
			unique = false;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		if (unique == null)
			return;

		if (unique && pk)
			this.unique = false;
		else
			this.unique = unique;
	}

	public List<GroupComponent> getColumns() {
		return columns;
	}

	public Column getColumn(int pos) {
		for (GroupComponent gc : columns)
			if (gc.getPos() == pos)
				return gc.getComponent();
		return null;
	}

	public String toString() {
		String res = "";
		if (pk)
			res += "id ";
		if (unique)
			res += "id' ";
		if (ref)
			res += "fk -> " + fk.getPkTableName() + "\n";
		if (isKey())
			res += "acc ";

		res += columns.get(0).getComponent().getName();
		for (int i = 1; i < columns.size(); i++) {
			res += "\n   " + columns.get(i).getComponent().getName();

		}

		return res;
	}

	public boolean isRef() {
		return ref;
	}

	public void setRef(Boolean ref) {
		if (ref == null)
			return;
		this.ref = ref;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public boolean isKey() {
		if (key)
			return true;

		return !listOfUnknownConstraintName.isEmpty();
	}

	public Set<String> getListOfUnknownConstraintName() {
		return listOfUnknownConstraintName;
	}

	public FK getFk() {
		return fk;
	}

	public void setFk(FK fk) {
		this.fk = fk;
	}

	public boolean contains(Column col) {
		for (GroupComponent c : columns)
			if (c.getComponent().getName().equals(col.getName()))
				return true;
		return false;
	}

}
