package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface EvolutionService {
    Model addEntityType(SMO smo, Model model) throws InputParameterException;

    Model removeEntityType(SMO smo, Model model) throws InputParameterException;

    Model renameEntityType(SMO smo, Model model) throws InputParameterException;

    Model splitHorizontal(SMO smo, Model model) throws InputParameterException;

    String splitVertical(SMO smo, Model model) throws InputParameterException;

    Model migrateEntity(SMO smo, Model model) throws InputParameterException;

    String mergeEntities(SMO smo, Model model) throws InputParameterException;

    Model addRelationship(SMO smo, Model model) throws InputParameterException;

    Model removeRelationship(SMO smo, Model model);

    String enableContainmentInRelationship(SMO smo, Model model) throws InputParameterException;    // Or modifyRelationship()? Generalize enable, disable containment or opposite?

    String disableContainmentInRelationship(SMO smo, Model model) throws InputParameterException;

    String enableOppositeRelationship(SMO smo, Model model) throws InputParameterException;

    String disableOppositeRelationship(SMO smo, Model model) throws InputParameterException;

    String changeCardinality(SMO smo, Model model);

    String addAttribute(SMO smo, Model model);

    String removeAttribute(SMO smo, Model model);

    String renameAttribute(SMO smo, Model model);

    String changeTypeAttribute(SMO smo, Model model);

    String addIdentifier(SMO smo, Model model);

    String addComponentToIdentifier(SMO smo, Model model);

    String removeIdentifier(SMO smo, Model model);

    String removeComponentToIdentifier(SMO smo, Model model);

    String addIndex(SMO smo, Model model);

    String removeIndex(SMO smo, Model model);

    String addComponentToIndex(SMO smo, Model model);

    String removeComponentToIndex(SMO smo, Model model);

    String renameRelationalTable(SMO smo, Model model);

    String renameDocumentCollection(SMO smo, Model model);

    String renameColumnFamilyName(SMO smo, Model model);

}
