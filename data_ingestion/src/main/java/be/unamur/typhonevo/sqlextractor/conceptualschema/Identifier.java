package be.unamur.typhonevo.sqlextractor.conceptualschema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Identifier   implements Serializable{
	
	private List<Attribute> columns = new ArrayList<Attribute>();
	
	public void addAttribute(Attribute col) {
		columns.add(col);
	}

	public List<Attribute> getAttributes() {
		return columns;
	}
	
	public boolean isTechnical() {
		if(columns.size() != 1)
			return false;
		
		return columns.get(0).isTechnicalIdentifier();
	}

}
