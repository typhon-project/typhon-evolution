package com.typhon.evolutiontool.handlers.EntityHandlers;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import typhonml.Model;

public class EntityMergeHandler extends BaseHandler {
    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.MERGE){
            return mergeEntities(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }

    private Model mergeEntities(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {
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

        throw new EvolutionOperationNotSupported("The Merge Entity operation exist but some lazy developers have not finish the job. SHAME !");
    }
}
