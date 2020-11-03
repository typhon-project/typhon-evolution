package be.unamur.typhonevo.sqlextractor.conceptualschema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Table;

public class EntityType  implements Serializable{
	private static Set<String> entityNamesAlreadyUsed = new HashSet<String>();
	
	
	private Table table;
	private String name;
	private Map<String, Attribute> attributes = new LinkedHashMap<String, Attribute>();
	private List<Identifier> ids = new ArrayList<Identifier>();
	private List<Index> indexes = new ArrayList<Index>();
	
 	public EntityType(Table table) {
		this.table = table;
		this.table.setEntityType(this);
		
		name = table.getName();
		int i = 1;
		while(entityNamesAlreadyUsed.contains(name)) {
			name = table.getName() + "_" + i;
			i++;
		}
		
		entityNamesAlreadyUsed.add(name);
		
	}
 	
 	public void addIndex(Index i) {
 		indexes.add(i);
 	}
 	
 	public void addIdentifier(Identifier i) {
 		ids.add(i);
 	}
 	
 	public void addAttribute(Attribute a) {
 		attributes.put(a.getName(), a);
 	}
 	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
	
	public String getName() {
		return name;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Identifier> getIds() {
		return ids;
	}

	public void setIds(List<Identifier> ids) {
		this.ids = ids;
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static String getTmlEntityName(String name) {
		if(name == null)
			return null;
		
		return name.replaceAll("\\s+", "_");
		
	}
	
	public String getAdaptedMLName() {
		return getTmlEntityName(name);
	}

}
