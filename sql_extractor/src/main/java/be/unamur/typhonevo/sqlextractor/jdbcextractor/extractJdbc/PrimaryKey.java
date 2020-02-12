package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.util.ArrayList;
import java.util.List;

public class PrimaryKey {
	
	private List<Column> columns = new ArrayList<Column>();
	
	public void addColumn(Column col) {
		columns.add(col);
	}
	
	@Override
	public String toString() {
		String res = "id:   " + columns.get(0).getName() + "\n";
		for(int i = 1; i < columns.size(); i++)
			res += "      " + columns.get(i) + "\n";
		
		return res;
		
	}

}
