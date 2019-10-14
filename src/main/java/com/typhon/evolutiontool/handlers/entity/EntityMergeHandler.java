package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

public class EntityMergeHandler extends BaseHandler {

    public EntityMergeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        //TODO
        /*
        TyphonML :
        - Check cardinality between the two entities (one_to_many only)
        - Delete relation between the two
        - Check that second entity is not any relationship. If yes, cancel.
        - Copy attribute of secondentity
        - Rename entity.

        - TyphonQL :
        -
         */

        throw new InputParameterException("The Merge Entity operation is not yet implemented");
    }

}
