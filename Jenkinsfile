 properties([
    parameters([
        choice(name: 'CLEAN', choices: ['false', 'true'], description: 'Clean workspace before build'),
        string(name: 'BRANCH', defaultValue: 'develop', description: 'Default branch'),
        string(name: 'RELEASE_VERSION', defaultValue: '', description: 'Release version')
    ])
])

pipeline {
    agent {
        docker { 
	  image 'maven:3.6.3-openjdk-8'
          args '--network=host'
	}
    }

    stages {
        stage('Clean') {
            when{
                expression{
                    return params.CLEAN == 'true';
                }
            }
            steps {
            	// Cleaning with cleanWS() has known issues use 'rm' instead
                sh 'rm -rf *'
            }
        }
        stage('Clone') {
            steps {
                git branch: params.BRANCH, url: 'https://github.com/takipi/overops-gitlab-plugin.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
                archive (includes: 'target/*.jar')
                sh 'mvn dockerfile:build'
            }
        }
        stage('Report'){
            steps{
                jacoco(
                      execPattern: 'target/*.exec',
                      classPattern: 'target/classes',
                      sourcePattern: 'src/main/java',
                      exclusionPattern: 'src/test*'
                )
            }
        }
        stage('Deploy') {
            when{
                expression{
                    return params.RELEASE_VERSION != '';
                }
            }
            steps {
				echo 'TBD'
				// TODO Figure out how to inject RELEASE_VERSION / TAG / PUSH
				// sh 'mvn dockerfile:push'
            }
        }
    }
}
