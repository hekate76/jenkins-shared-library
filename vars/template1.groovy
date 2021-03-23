def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

	node {
		try{
			stage('Git Clone'){
				echo 'Clone Start'
				checkout([$class: 'GitSCM', branches: [[name: '*/${config.branch}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'ssh', url: 'git@gitlab01.mitake.com.tw:apptech/${config.repositoryUrl}.git']]])
				echo 'Clone End'
			}
			stage('Nuget Restore'){
				echo 'Restore Start'
				bat '%Nuget% restore ${config.solutionName}.sln'
				echo 'Restore End'
			}
			stage('Build And Publish'){
				echo 'Build And Publish Start'
				bat '"%MSBuild14.0%" ${config.solutionName} "/p:DeployOnBuild=true;Configuration=Release;PublishProfile=%PublishFolder%\\%JOB_NAME%.pubxml"'
				echo 'Build And Publish End'
			}
		}
		catch (ex)
		{
			currentBuild.result = 'FAILURE';
		}
		finally
		{
			stage('Send email') {
				def mailRecipients = "johnny@mitake.com.tw"
				def jobName = currentBuild.fullDisplayName
				
				emailext body: '''${SCRIPT, template="groovy-html.template"}''',
				subject: "[Jenkins] ${jobName}",
				to: "${mailRecipients}",
				replyTo: "${mailRecipients}",
				recipientProviders: [[$class: 'CulpritsRecipientProvider']]
			}
		}
	}
}