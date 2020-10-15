package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

public interface EvolutionService {

    Model prepareEvolution(String changeOperatorsFilePath) throws Exception;

    Model evolveEntity(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported;

    Model evolveRelation(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported;

    Model evolveAttribute(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported;

    Model evolveIndex(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported;

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
