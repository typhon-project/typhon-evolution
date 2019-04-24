package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;

public interface EvolutionService {
    String addEntityType(SMO smo) throws InputParameterException;

    String removeEntityType(SMO smo) throws InputParameterException;

    String renameEntityType(SMO smo) throws InputParameterException;

    String splitHorizontal(SMO smo) throws InputParameterException;

    String splitVertical(SMO smo) throws InputParameterException;

    String migrateEntity(SMO smo) throws InputParameterException;

    String mergeEntities(SMO smo) throws InputParameterException;

    String addRelationship(SMO smo) throws InputParameterException;

    String removeRelationship(SMO smo);

    String enableContainmentInRelationship(SMO smo);    // Or modifyRelationship()? Generalize enable, disable containment or opposite?

    String disableContainmentInRelationship(SMO smo);

    String enableOppositeRelationship(SMO smo);

    String disableOppositeRelationship(SMO smo);

    String changeCardinality(SMO smo);

    String addAttribute(SMO smo);

    String removeAttribute(SMO smo);

    String renameAttribute(SMO smo);

    String changeTypeAttribute(SMO smo);

    String addIdentifier(SMO smo);

    String addComponentToIdentifier(SMO smo);

    String removeIdentifier(SMO smo);

    String removeComponentToIdentifier(SMO smo);

    String addIndex(SMO smo);

    String removeIndex(SMO smo);

    String addComponentToIndex(SMO smo);

    String removeComponentToIndex(SMO smo);

    String renameRelationalTable(SMO smo);

    String renameDocumentCollection(SMO smo);

    String renameColumnFamilyName(SMO smo);

}
