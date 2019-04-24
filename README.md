# Typhon evolution tool

## How to run

Main Spring Boot App class : Run `com.typhon.evolutiontool.EvolutionToolApplication` 

Default running address : http://localhost:8000 (Can be changed in `application.properties`)

## How to use

- Send a HTTP POST request to http://localhost:8000/evolve. It can receive Schema Modifier Object in JSON format.
```
{
   "typhonobject": "ENTITY",
   "operator": "ADD",
   "parameters":
   { ... }
 }
 ```
 
 ## Schema Modifiers currently supported
 
 - ENTITY / ADD (*entity, targetmodel, databasename, databasetype*)
 - ENTITY / RENAME (*oldentityname, newentityname, targetmodel*)
 - ENTITY / MIGRATE (*entity, targetmodel, sourcemodel, databasename, databasetype*)
 
 ## How it works
 
 The tool receives the SMO and verify its arguments. Afterwards it executes the needed steps via the corresponding Typhon interfaces functions (TyphonML `com.typhon.evolutiontool.services.typhonML.TyphonMLInterface`, TyphonDL `com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface`, TyphonQL `com.typhon.evolutiontool.services.typhonQL.TyphonQLConnection`).
 Those interfaces are currently implemented with a dummy class `com.typhon.evolutiontool.dummy.DummyImplementation` which prints execution message on the console log.
 
Actual implementation and connection to the different work packages to be done.