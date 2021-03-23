def call(String solutionName) {
    bat '"%MSBuild14.0%" ${solutionName} "/p:DeployOnBuild=true;Configuration=Release;PublishProfile=%PublishFolder%\\%JOB_NAME%.pubxml"'
}