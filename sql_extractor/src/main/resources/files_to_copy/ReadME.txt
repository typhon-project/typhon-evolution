1) Building with maven: mvn clean package

2) Command line to extract TML schema and data from existing relational databases: 
	WINDOWS: sql_extract.bat -extract extract.properties output
	LINUX: bash sql_extract.sh -extract extract.properties output
	
	NB: the 'extract.properties' file must contain the url and credentials of the relational databases to import in the polystore. 
	You should probably edit it.
	
3) Command line to inject data (extracted from previous phase) into polystore: 
	WINDOWS: sql_extract.bat -inject inject.properties output/data
	LINUX: sql_extract.sh -inject inject.properties output/data
	
	NB: the 'inject.properties' file must contain the url and credentials of the relational and document databases* created in the polystore
	*: the url and credentials of the document database can be optional if no document database is required