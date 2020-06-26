declare var process: Process;

interface Process {
  env: Env;
}

interface Env {
  BACKEND_ENDPOINT: string;
  JAVA_HOME: string;
}

interface GlobalEnvironment {
  process: Process;
}
