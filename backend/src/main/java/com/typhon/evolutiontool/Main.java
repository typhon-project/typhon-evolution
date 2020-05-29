package com.typhon.evolutiontool;
import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {
    	
    	// Doc for command line library : http://commons.apache.org/proper/commons-cli/javadocs/api-release/index.html
    	// Settings the command lines options 
    	Options options = new Options();
    	
    	// Help 
    	Option help = new Option("h", "help", false,  "Display command line usage");
    	help.setRequired(false);
    	options.addOption(help);
    	
    	// Input file Required :
    	Option input = new Option("i", "input", true,  "Path to the XMI file containing the change operators to apply");
    	input.setRequired(true);
    	options.addOption(input);
    	
    	// Output file Required for now
    	Option output = new Option("o", "output", true, "Output the resulting schema with change operators applied");
    	output.setRequired(true);
    	options.addOption(output);
    	
    	
    	CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

    	try {
    		cmd = parser.parse(options, args);
    		EvolutionTool evolutionTool = new EvolutionTool();
    		
    		if(cmd.hasOption("help")) {
    			formatter.printHelp("typhon-evolution", options);
    			System.exit(0);
    		}
            
            String modelInitialPath = cmd.getOptionValue("input");
            String modelFinalPath = cmd.getOptionValue("output");
            
            
            String resultMessage = evolutionTool.evolve(modelInitialPath, modelFinalPath);
            System.out.println("Evolution result: " + resultMessage);
    		
    	}
    	catch(ParseException e) {
    		System.out.println(e.getMessage());
    		formatter.printHelp("typhon-evolution", options);
    		
    		System.exit(1);
    	}
    }
}
