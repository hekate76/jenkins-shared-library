def call(Map params) {
	def subModules = []
	for (int i = 0; i < params.submodules.size(); i++) { 
		subModules << [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: false, reference: "https://gitlab01.mitake.com.tw/apptech/${params.submodules[i]}.git", trackingSubmodules: false]
	}
	node {
		try{
			stage('Git Clone'){
				echo 'Clone Start'
				checkout([
					$class: 'GitSCM', 
					branches: [[name: "${params.branch}"]], 
					extensions: subModules,
					userRemoteConfigs: [[credentialsId: 'ssh', url: "${params.repositoryUrl}"]]
				])
				echo 'Clone End'
			}
			stage('Nuget Restore'){
				echo 'Restore Start'
				bat '%Nuget% restore' + " ${params.solution}"
				echo 'Restore End'
			}
			stage('Build And Publish'){
				echo 'Build And Publish Start'
				bat '"%MSBuild14.0%"' + " ${params.solution} " + '"/p:DeployOnBuild=true;Configuration=Release;PublishProfile=%PublishFolder%\\%JOB_NAME%.pubxml"'
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