def call(String branch, String repositoryUrl) {
    checkout([$class: 'GitSCM', branches: [[name: '*/${branch}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'ssh', url: 'git@gitlab01.mitake.com.tw:apptech/${repositoryUrl}.git']]])
}