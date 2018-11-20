package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;

public class StructureChange {
    private SMO smo;
    private String tqlQuery;

    public StructureChange(SMO smo, String tqlQuery) {
        this.smo = smo;
        this.tqlQuery = tqlQuery;
    }

    public StructureChange() {
    }

    public StructureChange(String tqlquery) {
        this.tqlQuery=tqlquery;
    }

    public SMO getSmo() {
        return smo;
    }

    public void setSmo(SMO smo) {
        this.smo = smo;
    }

    public String getTqlQuery() {
        return tqlQuery;
    }

    public void setTqlQuery(String tqlQuery) {
        this.tqlQuery = tqlQuery;
    }

    public StructureChange computeChanges(SMO smo) {
        //TODO Check SMO Object consistency
        if (smo.getEvolutionOperator() == EvolutionOperator.ADD) {
            if (smo.getTyphonObject() == TyphonMLObject.ENTITY) {
                return new StructureChange(smo, "CREATE ENTITY "+smo.getInputParameter().get("entity").textValue());
            }
        }
        return this;
    }
}
