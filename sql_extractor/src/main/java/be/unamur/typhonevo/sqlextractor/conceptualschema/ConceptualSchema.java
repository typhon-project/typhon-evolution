package be.unamur.typhonevo.sqlextractor.conceptualschema;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Column;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.FK;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Group;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.GroupComponent;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Table;

public class ConceptualSchema {
	public static int MAX_NB_OF_IDS = 1;
	public static int MAX_NB_OF_INDEXES = 1;

	private List<Collection> collections;
	private String databaseName;
	private String documentdb;

	private Map<String, EntityType> entityTypes = new HashMap<String, EntityType>();
	private List<RelationshipType> relationshipTypes = new ArrayList<RelationshipType>();

	public ConceptualSchema(List<Collection> collections) {
		this.collections = collections;
//		databaseName = "";
//		for (Collection coll : collections)
//			databaseName += "_" + coll.getName();

		databaseName = "RelationalDatabase";
		documentdb = "DocumentDatabase";

	}

	public static void configure(int nbOfIds, int nbOfIndexes) {
		MAX_NB_OF_IDS = nbOfIds;
		MAX_NB_OF_INDEXES = nbOfIndexes;
	}

	public static ConceptualSchema transform(List<Collection> collections) {
		ConceptualSchema res = new ConceptualSchema(collections);
		res.transform();
		return res;
	}

	private void transform() {
		Map<String, EntityType> map = new HashMap<String, EntityType>();
		List<RelationshipType> rels = new ArrayList<RelationshipType>();

		for (Collection coll : collections) {
			for (Table table : coll.getTables().values()) {
				//// gonna handle many-to-many after
				RelationshipType rel = table.isManyToMany();
				if (rel == null) {
					EntityType e = table.getEntityType();
					if (e == null) {
						e = new EntityType(table);
						map.put(e.getName(), e);
					}

					// Columns

					List<Column> columns = table.getColumnsNotPartOfFk();
					for (Column col : columns) {
						Attribute a = new Attribute(col);
						e.addAttribute(a);

						if (isTechnicalId(col, table))
							a.setTechnicalIdentifier(true);

					}

					List<Column> splitColumns = table.getSplitColumns();
					for (Column col : splitColumns) {
						Attribute a = new Attribute(col);
						e.addAttribute(a);
						if (isTechnicalId(col, table))
							a.setTechnicalIdentifier(true);
					}

					// Foreign keys
					List<FK> fks = table.getFks();
					for (FK fk : fks) {
						boolean optional = !fk.isMandatory();
						String primaryTable = fk.getPkTableName();
						EntityType table1 = map.get(primaryTable);
						if (table1 == null) {
							Table t = coll.getTables().get(primaryTable);
							table1 = new EntityType(t);
							map.put(table1.getName(), table1);
						}

						String name = primaryTable;
						if (fk.getFKGroup().size() == 1)
							name = fk.getFKGroup().get(0).getComponent().getName();
						Role role1 = new Role(0, 2, fk.getFkTableName()); // 0-N
						Role role2 = new Role(optional ? 0 : 1, 1, name); // (0,1)-1

						RelationshipType rel_ = new RelationshipType(table1, e, role1, role2, null);
						rel_.setFk(fk);
						rels.add(rel_);

					}
				}

			}

			for (Table table : coll.getTables().values()) {

				//// gonna handle many-to-many now
				RelationshipType rel = table.isManyToMany();
				if (rel != null) {
					EntityType e1 = rel.getTable1();
					EntityType e1_ = map.get(e1.getName());
					if (e1_ == null) {
						e1_ = e1;
						map.put(e1.getName(), e1);
					}

					rel.setTable1(e1_);

					///////

					EntityType e2 = rel.getTable2();
					EntityType e2_ = map.get(e2.getName());
					if (e2_ == null) {
						e2_ = e2;
						map.put(e2.getName(), e2);
					}

					rel.setTable2(e2_);

					rels.add(rel);

				}
			}
		}

		setEntityTypes(map);
		setRelationshipTypes(rels);

		createSimpleIdentifiers(map);
		createSimpleIndexes(map);

	}

	private boolean isTechnicalId(Column col, Table table) {
		if(!col.isAutoIncrement())
			return false;
		
		boolean technical = false;
		for (Group pk : table.getIds()) {
			if (pk.getColumns().size() == 1 && pk.contains(col))
				technical = true;

			if (pk.getColumns().size() > 1 && pk.contains(col))
				return false;
		}

		return technical;
	}

	private void createSimpleIndexes(Map<String, EntityType> map) {
		for (EntityType e : map.values()) {
			Table table = e.getTable();
			List<Group> indexes = table.getIndexes();
			List<Column> columnsNotInFK = table.getColumnsNotPartOfFk();

			for (Group id : indexes) {
				boolean isHybridIndex = false;
				for (GroupComponent gc : id.getColumns()) {
					Column col = gc.getComponent();
					if (!contains(columnsNotInFK, col)) {
						isHybridIndex = true;
						break;
					}

				}

				if (isHybridIndex)
					continue;

				Index index = new Index();
				for (GroupComponent gc : id.getColumns()) {
					Attribute a = e.getAttributes().get(gc.getComponent().getName());

					index.addAttribute(a);
				}

				e.addIndex(index);

			}
		}
	}

	private void createSimpleIdentifiers(Map<String, EntityType> map) {
		for (EntityType e : map.values()) {
			Table table = e.getTable();
			List<Group> ids = table.getIds();
			List<Column> columnsNotInFK = table.getColumnsNotPartOfFk();

			for (Group id : ids) {
				boolean isHybridId = false;
				for (GroupComponent gc : id.getColumns()) {
					Column col = gc.getComponent();
					if (!contains(columnsNotInFK, col)) {
						isHybridId = true;
						break;
					}

				}

				if (isHybridId)
					continue;

				Identifier iden = new Identifier();
				for (GroupComponent gc : id.getColumns()) {
					iden.addAttribute(e.getAttributes().get(gc.getComponent().getName()));
				}
				e.addIdentifier(iden);

			}
		}

	}

	private static boolean contains(List<Column> columnsNotInFK, Column col) {
		for (Column c : columnsNotInFK)
			if (c.getName().equals(col.getName()))
				return true;
		return false;
	}

	public Map<String, EntityType> getEntityTypes() {
		return entityTypes;
	}

	public void setEntityTypes(Map<String, EntityType> entityTypes) {
		this.entityTypes = entityTypes;
	}

	public List<RelationshipType> getRelationshipTypes() {
		return relationshipTypes;
	}

	public void setRelationshipTypes(List<RelationshipType> relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
	}

	@Override
	public String toString() {
		String res = "";
		for (EntityType e : entityTypes.values()) {
			res += e.getName() + "(";
			int i = 0;
			for (Attribute a : e.getAttributes().values()) {
				if (i > 0)
					res += ", ";
				res += a.getName();
				i++;
			}
			res += ")\n";

			for (Identifier id : e.getIds()) {
				res += "id:" + id.getAttributes() + "\n";
			}

			for (Index index : e.getIndexes()) {
				res += "index:" + index.getAttributes() + "\n";
			}
		}

		for (RelationshipType rel : relationshipTypes)
			res += rel.getTable1().getName() + "-- [" + rel.getRole1().getMinCard() + "," + rel.getRole1().getMaxCard()
					+ "] ----- [" + rel.getRole2().getMinCard() + "," + rel.getRole2().getMaxCard() + "] -- "
					+ rel.getTable2().getName() + "\n";

		return res;
	}

	public void printTyphonML(String file) throws Exception {
		int index_counter = 0;
		List<String> content = new ArrayList<String>();
//		Set<String> types = new HashSet<String>();
//		for (EntityType ent : entityTypes.values()) {
//			for (Attribute a : ent.getAttributes().values()) {
//				if (types.add(Attribute.getTyphonType(a.getColumn())))
//					content.add("datatype " + Attribute.getTyphonType(a.getColumn()));
//			}
//		}

		Set<String> splitTable = new HashSet<String>();
		List<String> splitTableContent = new ArrayList<String>();

		for (EntityType ent : entityTypes.values()) {
			Set<String> attrAndRelList = new HashSet<String>();
			content.add("entity \"" + ent.getName() + "\" {");
			for (Attribute a : ent.getAttributes().values()) {
				if (a.isTechnicalIdentifier())
					continue;

				if (!a.getColumn().isSplit()) {
					attrAndRelList.add(a.getName());
					content.add("   \"" + a.getName() + "\" : " + Attribute.getTyphonType(a.getColumn()));
				} else {
					// split column
					String newEntityName = ent.getName() + "_" + a.getName();
					String name = newEntityName;
					int i = 0;
					while (entityTypes.get(name) != null || splitTable.contains(name)) {
						name = newEntityName + "_" + i;
					}
					splitTable.add(name);
					String entityDeclaration = "entity \"" + name + "\" {\n";
					entityDeclaration += "   \"" + a.getName() + "\" : " + Attribute.getTyphonType(a.getColumn())
							+ "\n";
					entityDeclaration += "   \"" + ent.getName() + "\" -> \"" + ent.getName() + "\"" + ("[1]") + "\n";
					entityDeclaration += "}\n";
					splitTableContent.add(entityDeclaration);
					a.setSplitTable(name);
				}
			}

			List<RelationshipType> oneToManyRels = getOneToMany(ent);
			for (RelationshipType rel : oneToManyRels) {
				EntityType ent1 = rel.getTable1();
				Role role = rel.getRole2();
				String relName = role.getTmlName();

				if (relName == null) {
					String beginName = ent1.getName();
					relName = beginName;
					boolean ok = !attrAndRelList.contains(relName);
					int i = 1;
					while (!ok) {
						relName = beginName + "_" + i;
						ok = !attrAndRelList.contains(relName);
						i++;
					}
				}

				role.setTmlName(relName);
				attrAndRelList.add(relName);
				content.add("   \"" + relName + "\" -> \"" + ent1.getName() + "\""
						+ (role.getMinCard() == 0 ? "[0..1]" : "[1]"));
			}

			// Opposite
			List<RelationshipType> manyToOneRels = getManyToOne(ent);

			for (RelationshipType rel : manyToOneRels) {
				EntityType ent2 = rel.getTable2();
				Role role = rel.getRole1();
				String beginName = ent2.getName();
				String relName = beginName;
				boolean ok = !attrAndRelList.contains(relName);
				int i = 1;
				while (!ok) {
					relName = beginName + "_" + i;
					ok = !attrAndRelList.contains(relName);
					i++;
				}

				role.setTmlName(relName);
				attrAndRelList.add(relName);

				String relName2 = rel.getRole2().getTmlName();
				if (relName2 == null) {
					String beginName2 = rel.getTable1().getName();
					relName2 = beginName2;
					ok = !attrAndRelList.contains(relName2);
					i = 1;
					while (!ok) {
						relName2 = beginName2 + "_" + i;
						ok = !attrAndRelList.contains(relName2);
						i++;
					}

				}

				rel.getRole2().setTmlName(relName2);
				attrAndRelList.add(relName2);

				content.add("   \"" + relName + "\" -> \"" + ent2.getName() + "\".\"" + ent2.getName() + "." + relName2
						+ "\"[0..*]");
			}

			//////////////////////

			List<RelationshipType> manyToManyRels = getManyToMany(ent);

			for (RelationshipType rel : manyToManyRels) {
//				ici opposite
				if (rel.isLoop()) {
					EntityType e = rel.getTable1();

					String relName1 = rel.getRole1().getTmlName();
					if (relName1 == null) {
						relName1 = rel.getManyToManyTable().getName();
						boolean ok = !attrAndRelList.contains(relName1);
						int i = 1;
						while (!ok) {
							relName1 = rel.getManyToManyTable().getName() + "_" + i;
							ok = !attrAndRelList.contains(relName1);
							i++;
						}
					}

					String relName2 = rel.getRole2().getTmlName();
					if (relName2 == null) {
						relName2 = rel.getManyToManyTable().getName();

						attrAndRelList.add(relName1);
						boolean ok = !attrAndRelList.contains(relName2);
						int i = 1;
						while (!ok) {
							relName2 = rel.getManyToManyTable().getName() + "_" + i;
							ok = !attrAndRelList.contains(relName2);
							i++;
						}
					}

					attrAndRelList.add(relName2);
					rel.getRole1().setTmlName(relName1);
					rel.getRole2().setTmlName(relName2);

					content.add("   \"" + relName1 + "\" -> \"" + e.getName() + "\"[0..*]");
					content.add("   \"" + relName2 + "\" -> \"" + e.getName() + "\".\"" + e.getName() + "." + relName1
							+ "\"[0..*]");

				} else {
					EntityType e;
					EntityType e2;
					Role role;
					Role role2;
					if (rel.getTable1().getName().equals(ent.getName())) {
						e = rel.getTable2();
						role = rel.getRole2();
						e2 = rel.getTable1();
						role2 = rel.getRole1();
					} else {
						e = rel.getTable1();
						role = rel.getRole1();
						e2 = rel.getTable2();
						role2 = rel.getRole2();
					}

					String relName = e.getName();
					boolean ok = !attrAndRelList.contains(relName);
					int i = 1;
					while (!ok) {
						relName = e.getName() + "_" + i;
						ok = !attrAndRelList.contains(relName);
						i++;
					}

					role.setTmlName(relName);
					attrAndRelList.add(relName);

					if (role2.getTmlName() != null) {
						// opposite
						content.add("   \"" + relName + "\" -> \"" + e.getName() + "\".\"" + e.getName() + "."
								+ role2.getTmlName() + "\"[0..*]");
					} else {
						content.add("   \"" + relName + "\" -> \"" + e.getName() + "\"[0..*]");
					}

				}

			}

			content.add("}");
			content.add("");

		}

		for (String split : splitTableContent)
			content.add(split);

		content.add("relationaldb " + databaseName + " {");
		content.add("   tables {");
		for (EntityType ent : entityTypes.values()) {
			content.add("      table {");
			content.add("         \"" + ent.getName() + "\" : \"" + ent.getName() + "\"");

			int counter = 0;
			for (Index index : ent.getIndexes()) {
				if(index.isTechnical())
					continue;
				
				if (counter >= MAX_NB_OF_INDEXES)
					break;
				String indString = "         index \"index_" + index_counter + "\" {\n";
				indString += "            attributes (";
				int i = 0;
				for (Attribute attr : index.getAttributes()) {
					if (i > 0)
						indString += ", ";
					indString += "\"" + ent.getName() + "." + attr.getName() + "\"";
					i++;
				}
				indString += ")\n";
				indString += "         }";
				content.add(indString);
				index_counter++;
				counter++;
			}

			int id_counter = 0;
			for (Identifier id : ent.getIds()) {
				if(id.isTechnical())
					continue;
				
				if (id_counter >= MAX_NB_OF_IDS)
					break;

				String idString = "         idSpec (";
				int i = 0;
				for (Attribute attr : id.getAttributes()) {
					if (i > 0)
						idString += ", ";

					idString += "\"" + ent.getName() + "." + attr.getName() + "\"";
					i++;
				}
				idString += ")";
				content.add(idString);
				id_counter++;
			}

			content.add("      }");
		}
		content.add("   }");
		content.add("}");

		content.add("");
		content.add("documentdb " + documentdb + " {");

		if (isExistsDocumentDatabase() && splitTable.size() > 0) {

			content.add("   collections {");
			for (String s : splitTable)
				content.add("      \"" + s + "\" : \"" + s + "\"");
			content.add("   }");

//			    collections {
//			            Review : Review
//			            Comment : Comment
//			            Product : Product
//			    }
		}

		content.add("}");

		writeToFile(file, content);
	}

	private static void writeToFile(String file, List<String> content) throws Exception {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (String line : content)
				writer.write(line + "\n");

			writer.close();
		} catch (Exception e) {
			throw new Exception("Impossible to save the result TML in file: " + file);
		}

	}

	private List<RelationshipType> getManyToOne(EntityType ent) {
		List<RelationshipType> res = new ArrayList<RelationshipType>();
		for (RelationshipType rel : relationshipTypes) {
			if (!rel.isManyToMany()) {
				EntityType e = rel.getTable1();
//				if (rel.getRole1().getMaxCard() == 1)
//					e = rel.getTable1();
//				else
//					e = rel.getTable2();

				if (e.getName().equals(ent.getName()))
					res.add(rel);

			}
		}

		return res;

	}

	public List<RelationshipType> getOneToMany(EntityType ent) {
		List<RelationshipType> res = new ArrayList<RelationshipType>();
		for (RelationshipType rel : relationshipTypes) {
			if (!rel.isManyToMany()) {
				EntityType e = rel.getTable2();
//				if (rel.getRole1().getMaxCard() == 1)
//					e = rel.getTable1();
//				else
//					e = rel.getTable2();

				if (e.getName().equals(ent.getName()))
					res.add(rel);

			}
		}

		return res;
	}

	public List<RelationshipType> getManyToManyBasedOnRole1(EntityType ent) {
		List<RelationshipType> res = new ArrayList<RelationshipType>();
		for (RelationshipType rel : relationshipTypes) {
			if (rel.isManyToMany() && rel.getTable1().getName().equals(ent.getName())) {
				res.add(rel);
			}
		}

		return res;
	}

	public List<RelationshipType> getManyToMany(EntityType ent) {
		List<RelationshipType> res = new ArrayList<RelationshipType>();
		for (RelationshipType rel : relationshipTypes) {
			if (rel.isManyToMany() && (rel.getTable1().getName().equals(ent.getName())
					|| rel.getTable2().getName().equals(ent.getName()))) {

				res.add(rel);

			}
		}

		return res;
	}

	public boolean isExistsDocumentDatabase() {
		for (EntityType en : entityTypes.values()) {
			for (Attribute a : en.getAttributes().values())
				if (a.getColumn().isSplit())
					return true;
		}

		return false;
	}

}
