module lang::typhonml::XMIReader

import lang::xml::IO;
import lang::typhonml::TyphonML;
import lang::ecore::Refs;

import lang::typhonml::Util;

import IO;
import Node;
import Type;

import util::ValueUI;


void smokeTest() {
  str xmi = readFile(|project://typhonql/src/newmydb4.xmi|);
  Model m = xmiString2Model(xmi);
  iprintln(m);
  iprintln(model2schema(m));
}


void smokeTest2() {
  str xmi = readFile(|project://query-evolution/src/lang/typhonml/customdatatypes.xmi|);
  Model m = xmiString2Model(xmi);
  Schema s = model2schema(m);
  //iprintln(m);
  iprintln(s);
}

Model loadTyphonML(loc l) = xmiString2Model(readFile(l));

Model xmiString2Model(str s) = xmiNode2Model(readXML(s));

Schema loadSchemaFromXMI(str s) = model2schema(m)
	when Model m := xmiString2Model(s);

@doc{
Convert a node representation of the XMI serialization of a TyphonML model
to a `lang::typhoml::TyphonML::Model` value.

Omissions for now:
 - evolution operators
 - database types other than document and relation
}
Model xmiNode2Model(node n) {  
  Realm realm = newRealm();
  
  list[Database] dbs = [];
  list[DataType] dts = [];
  
  str get(node n, str name) = x 
    when str x := getKeywordParameters(n)[name];
  
  map[str, DataType] typeMap = ();
  map[str, Relation] relMap = ();
  
  
  DataType ensureEntity(str path) {
    if (path notin typeMap) {
      typeMap[path] = DataType(realm.new(#Entity, Entity("", [], [], [])));
    }
    return typeMap[path];
  }
  
  DataType ensurePrimitive(str path) {
    if (path notin typeMap) {
      typeMap[path] = DataType(realm.new(#PrimitiveDataType, PrimitiveDataType("")));
    }
    else {
      typeMap[path] = DataType(realm.new(#PrimitiveDataType, PrimitiveDataType(""), id = typeMap[path].uid));
    }
    return typeMap[path];
  }
  
  DataType ensureCustom(str path) {
    if (path notin typeMap) {
      typeMap[path] = DataType(realm.new(#CustomDataType, CustomDataType("", [])));
    }
    else {
      typeMap[path] = DataType(realm.new(#CustomDataType, CustomDataType("", []), id = typeMap[path].uid));
    }
    
    return typeMap[path];
  }
  
  
  
  Relation ensureRel(str path) {
    if (path notin relMap) {
      relMap[path] = realm.new(#Relation, Relation("", zero_one()));
    }
    return relMap[path];
  }
  
  if ("Model"(list[node] kids) := n) {

    for (xdb:"databases"(list[node] xelts) <- kids) {
      switch (get(xdb, "type")) {
        case "typhonml:RelationalDB": {
          tbls = [];
          for (xtbl:"tables"(_) <- xelts) {
            tbl = realm.new(#Table, Table(get(xtbl, "name")));
            ep = get(xtbl, "entity");
            tbl.entity = referTo(#Entity, ensureEntity(ep).entity);
            tbls += [tbl];
          }
          
          dbs += [ realm.new(#Database, Database(RelationalDB(get(xdb, "name"), tbls)))];
        }
        
        case "typhonml:DocumentDB": {
          colls = [];
          for (xcoll:"collections"(_) <- xelts) {
            coll = realm.new(#Collection, Collection(get(xcoll, "name")));
            ep = get(xcoll, "entity");
            coll.entity = referTo(#Entity, ensureEntity(ep).entity);
            colls += [coll];
          }
          
          dbs += [ realm.new(#Database, Database(DocumentDB(get(xdb, "name"), colls))) ];
        }
        
        default:
          throw "Non implemented database type: <xdb.\type>";
      }
      
    }
    
    int dtPos = 0;
    for (xdt:"dataTypes"(list[node] xelts) <- kids) {
       dtPath = "//@dataTypes.<dtPos>";
           
       switch (get(xdt, "type")) {
       	 case "typhonml:PrimitiveDataType": {
           pr = ensurePrimitive(dtPath).primitiveDataType;
           pr.name = get(xdt, "name");
           dts += [DataType(pr)];
         }
         
         case "typhonml:CustomDataType": {
           list[DataTypeItem] elements = [];
           for (xattr:"elements"(_) <- xelts) {
             el = realm.new(#DataTypeItem, DataTypeItem(get(xattr, "name"), DataTypeImplementationPackage()));
             aPath = get(xattr, "type");
             el.\type = referTo(#DataType, ensurePrimitive(aPath));
             elements += [el]; 
           }
           custom = ensureCustom(dtPath).customDataType;
           custom.name = get(xdt, "name");
           custom.elements = elements;   
           dt = DataType(custom);  
           dts+= [dt];
         }
         
         case "typhonml:Entity": {
           attrs = [];
           for (xattr:"attributes"(_) <- xelts) {
             attr = realm.new(#Attribute, Attribute(get(xattr, "name")));
             aPath = get(xattr, "type");
             attr.\type = referTo(#DataType, ensurePrimitive(aPath));
             attrs += [attr]; 
           }  
         
           rels = [];
           relPos = 0;
           for (xrel:"relations"(_) <- xelts) {
             relPath = "<dtPath>/@relations.<relPos>";
             myrel = ensureRel(relPath);
             myrel.name = get(xrel, "name");
             myrel.cardinality = make(#Cardinality, get(xrel, "cardinality"), []);
             
             ePath = get(xrel, "type");
             myrel.\type = referTo(#Entity, ensureEntity(ePath).entity);
             
             if ("opposite" in getKeywordParameters(xrel)) {
               oppPath = get(xrel, "opposite");
               myrel.opposite = referTo(#Relation, ensureRel(oppPath));
             }
             
             if ("isContainment" in getKeywordParameters(xrel)) {
               myrel.isContainment = get(xrel, "isContainment") == "true";
             } 
             
             
             rels += [myrel];
             relPos += 1;
           }

           entity = ensureEntity(dtPath).entity;
           entity.name = get(xdt, "name");
           entity.attributes = attrs;
           entity.fretextAttributes = []; // todo;
           entity.relations = rels;
           dt = DataType(entity);              
           dts += [dt];
         }
       }
       
       dtPos += 1;
    }
    
    return  realm.new(#Model, Model(dbs, dts, [] /* todo */));
  }
  else {
    throw "Invalid Typhon ML XMI node <n>";
  }
  
  
  
}