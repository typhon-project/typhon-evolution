package com.typhon.evolutiontool;

public class Main {

    public static void main(String[] args) {
        EvolutionTool evolutionTool = new EvolutionTool();
        if (args != null && args.length > 0) {
            String modelInitialPath = args[0];
            String modelFinalPath = args[1];
            String resultMessage = evolutionTool.evolve(modelInitialPath, modelFinalPath);
            System.out.println("Evolution result: " + resultMessage);
        } else {
            System.err.println("Missing input parameters, modelInitialPath and modelFinalPath");
        }
    }
}
