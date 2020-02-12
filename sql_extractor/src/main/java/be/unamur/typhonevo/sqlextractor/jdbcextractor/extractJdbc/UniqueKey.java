package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.util.ArrayList;
import java.util.List;

public class UniqueKey {
	
	private String name;
	private List<Column> columns = new ArrayList<Column>();
	
	public UniqueKey(String name) {
		this.setName(name);
	}
	
	public void addColumn(Column col) {
		columns.add(col);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		String res = "id'  :" + columns.get(0).getName() + "\n";
		for(int i = 1; i < columns.size(); i++)
			res += "     " + columns.get(i) + "\n";
		return res;
	}

}
