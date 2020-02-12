package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;


class CatalogSchema {
	private String catalog = null;
	private String schema = null;

	public CatalogSchema(String catalog, String schema) {
		this.catalog = catalog;
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	@Override
	public String toString() {
		if(catalog != null && schema != null){
			return(catalog + "." + schema);
		}
		if(catalog != null){
			return(catalog);
		}
		if(schema != null){
			return(schema);
		}
		return "null";
	}


}