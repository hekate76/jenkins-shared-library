def call(String solutionName) {
    bat '%Nuget% restore ${solutionName}.sln'
}