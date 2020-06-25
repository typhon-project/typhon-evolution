declare var process: Process;

interface Process {
  env: Env;
}

interface Env {
  JAVA_HOME: string;
}

interface GlobalEnvironment {
  process: Process;
}
