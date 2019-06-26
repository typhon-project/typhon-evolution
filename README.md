[![Build Status](http://typhon.clmsuk.com:8081/buildStatus/icon?job=TyphonEvolution)](http://typhon.clmsuk.com:8081/job/TyphonEvolution)

# Typhon evolution tool

## How to run

Main Spring Boot App class : Run `com.typhon.evolutiontool.EvolutionToolApplication` 

Default running address : http://localhost:8000 (Can be changed in `application.properties`)

## How to use

- Send a HTTP POST request to http://localhost:8000/evolve. It requires two string parameters :
    * initialModelPath : Represents a path to an xmi file containing the TyphonML models with ChangeOperators. (exemple : "resources/tml_removeEntityChangeOp.xmi" OR "resources/tml_removeAndRenameChangeOp.xmi")
    *  finalModelPath : Represents a path and file name where the final model will be saved. (ex: resources/finalModel.xmi)
 
 