
public class Main {

	public static void main(String[] args) {

		if (args == null || args.length == 0) {
			help();
			return;
		}

		if (args.length == 1 && args[0].equals("-help")) {
			help();
			return;
		}

		fixSparkHadoopBugWithJavaVersion();

		String operation = args[0];
		boolean ok = false;
		switch (operation) {
		case "-extract":
			if (args.length == 3) {
				String configFile = args[1];
				String outputDir = args[2];
				ok = true;
				args = new String[] { configFile, outputDir };
				TMLExtractor.DEV_MODE = false;
				TMLExtractor.main(args);
			}
			break;
		case "-inject":
			if (args.length == 3) {
				String configFile = args[1];
				String inputDir = args[2];
				ok = true;
				args = new String[] { configFile, inputDir };
				PolyStoreFiller.DEV_MODE = false;
				PolyStoreFiller.main(args);
			}
			break;
		default:
			break;
		}

		if (!ok)
			help();

	}

	private static void fixSparkHadoopBugWithJavaVersion() {
		try {
			String java_version = System.getProperty("java.version");
			if (java_version.length() < 3) {
				java_version += ".0.0";
				System.setProperty("java.version", java_version);
			}
		} catch (Exception | Error e) {
			System.err.println("The Java version cannot be modified");
		}

	}

	private static void help() {
		System.out.println("Possible command lines:");
		System.out.println("   -help");
		System.out.println("   -extract <config file> <output directory where the migration scripts will be copied>");
		System.out.println("   -inject <config file> <directory containing the TQL migration scripts>");
	}

}
