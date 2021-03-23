def call(String branch, String repositoryUrl) {
    checkout([
		$class: 'GitSCM',
		branches: [[name: '*/${branch}']],
		userRemoteConfigs: [[credentialsId: 'ssh', url: 'git@gitlab01.mitake.com.tw:apptech/${repositoryUrl}.git']]
	])
}