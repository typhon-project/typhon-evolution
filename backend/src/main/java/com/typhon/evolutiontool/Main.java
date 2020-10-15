package com.typhon.evolutiontool;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {

    	if (args != null && args.length > 0) {
			String changeOperatorsFilePath = args[0];
			if (changeOperatorsFilePath != null && !changeOperatorsFilePath.isEmpty()) {
				EvolutionTool evolutionTool = new EvolutionTool();
				String resultMessage = evolutionTool.evolveFromWebApplication(changeOperatorsFilePath);
			} else {
				System.out.println("Change operators file path is empty. Please add the file path as input parameter of this application.");
			}
		} else {
			// Doc for command line library : http://commons.apache.org/proper/commons-cli/javadocs/api-release/index.html
			// Settings the command lines options
			Options options = new Options();

			// Help
			Option help = new Option("h", "help", false, "Display command line usage");
			help.setRequired(false);
			options.addOption(help);


			CommandLineParser parser = new DefaultParser();
			HelpFormatter formatter = new HelpFormatter();
			CommandLine cmd;

			try {
				cmd = parser.parse(options, args);

				if (cmd.hasOption("help")) {
					System.out.println("Running the tool without parameters will generate a configuration file for "
							+ "the evolution. Edit the generated file for your case and run the tool again to perform "
							+ "the evolution.");
					System.exit(0);
				}
			} catch (ParseException e) {
				System.out.println(e.getMessage());
				formatter.printHelp("typhon-evolution", options);
				System.exit(1);
			}

			// Test if the config file exists. If not create it
			File config_file = new File("./application.properties");
			if (config_file.exists()) {
				// Read properties file
				Properties prop = new Properties();
				InputStream is;
				try {
					is = new FileInputStream(config_file);
					prop.load(is);
				} catch (IOException e) {
					System.out.println("Unable to load property file ");
					e.printStackTrace();
				}

				EvolutionTool evolutionTool = new EvolutionTool();
				String resultMessage = evolutionTool.evolve(prop.getProperty("INPUT_XMI"), prop.getProperty("RESULT_FILE"));
				System.out.println(resultMessage);
				System.exit(0);
			} else {
				// write properties file
				Properties props = new Properties();

				props.setProperty("INPUT_XMI", "");
				props.setProperty("RESULT_FILE", "");
				props.setProperty("POLYSTORE_API_URL", "http://localhost:8080/");
				props.setProperty("POLYSTORE_API_USER_PASSWORD", "admin:admin1@");
				props.setProperty("API_RESET_DATABASES_URL", "api/resetdatabases");
				props.setProperty("API_GET_USERS_URL", "users");
				props.setProperty("API_QUERY_URL", "api/query");
				props.setProperty("API_UPDATE_URL", "api/update");
				props.setProperty("API_GET_ML_MODEL_URL", "api/model/ml/");
				props.setProperty("API_GET_ML_MODELS_URL", "api/models/ml");
				props.setProperty("API_UPLOAD_ML_MODEL_URL", "api/model/ml");

				OutputStream os;
				try {
					os = new FileOutputStream(config_file);
					props.store(os, "");
					System.out.println("Configuration file 'application.properties' was created. "
							+ "open it and check the parameters then, run the command line again to perform evolution");
				} catch (IOException e) {
					System.out.println("Unable to create property file ");
					e.printStackTrace();
				}
			}
		}
    }
}
