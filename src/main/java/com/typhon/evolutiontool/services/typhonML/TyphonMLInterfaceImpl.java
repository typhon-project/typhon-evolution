package com.typhon.evolutiontool.services.typhonML;

import com.typhon.evolutiontool.entities.Database;
import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.Relation;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import typhonml.Attribute;
import typhonml.DataType;
import typhonml.Model;
import typhonml.TyphonmlFactory;
import typhonml.impl.DataTypeImpl;
import typhonml.impl.PrimitiveDataTypeImpl;


@Component
public class TyphonMLInterfaceImpl implements TyphonMLInterface {

	Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

	@Override
	public void setNewTyphonMLModel(String newModelIdentifier) {
		logger.info("Setting current TyphonML to [{}] ", newModelIdentifier);
		//TODO Implement TyphonML interface
	}

	@Override
	public Entity getEntityTypeFromId(String entityid, String sourcemodelid) {
		logger.info("Getting Entity type object from Id [{}] on model [{}]", entityid, sourcemodelid);
		//TODO Implement real retrieving + casting to Entity object
		return null;
	}

	@Override
	public String getAttributeIdOfEntityType(String sourceEntityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRelationship(String entityname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Database getDatabaseType(String entityname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttributeOfType(String entityname, Entity targetEntityType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Relation getRelationFromName(String relationname) {
		return null;
	}

	@Override
	public Model createEntityType(Model sourceModel, Entity newEntity) {
		Model newModel;
		newModel = EcoreUtil.copy(sourceModel);

		//ENTITY
		typhonml.Entity entity = TyphonmlFactory.eINSTANCE.createEntity();
		entity.setName(newEntity.getName());
		newEntity.getAttributes().entrySet().forEach(entry -> entity.getAttributes().add(this.createAttribute(entry.getKey(), entity)));
		newModel.getDataTypes().add(entity);
		return newModel;
	}

	private Attribute createAttribute(String name, DataType type) {
		//TODO Handling of dataTypes
		Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
		attribute.setName(name);
		attribute.setType(type);
		return attribute;
	}

}
