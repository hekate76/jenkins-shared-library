def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

	node {
		try{
			stage('Git Clone'){
				echo 'Clone Start'
				checkout(${config.branch}, ${config.repositoryUrl})
				echo 'Clone End'
			}
			stage('Nuget Restore'){
				echo 'Restore Start'
				restore(${config.solutionName})
				echo 'Restore End'
			}
			stage('Build And Publish'){
				echo 'Build And Publish Start'
				deploy(${config.solutionName})
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