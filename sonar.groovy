pipeline {
    agent any
    
    parameters {
        string(name: 'GIT_URL', defaultValue: 'git@gitlab.devopsnonprd.vayuktbcs:ctp/phase2/ctp-api-gateway.git', description: 'Git repository URL')
        string(name: 'GIT_BRANCH', defaultValue: 'develop', description: 'Git branch to build')
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'jenkins', description: 'SonarQube project key')
    }
    
    environment {
        SONAR_JAVA_BINARIES = 'target/classes'
    }

    stages { 
        stage('SCM Checkout') {
            steps {
                git branch: "${params.GIT_BRANCH}",
                    url: "${params.GIT_URL}",
                    credentialsId: 'gitlabpwd'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean build' // Build the Spring Kotlin project with Gradle
            }
        }
        stage('Run SonarQube') {
            environment {
                scannerHome = tool 'sonar_tool'
            }
            steps {
                withSonarQubeEnv(credentialsId: 'sonarpwd', installationName: 'sonar_server') {
                    sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${params.SONAR_PROJECT_KEY} -Dsonar.java.binaries=${env.SONAR_JAVA_BINARIES}"
                }
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline completed.'
            cleanWs() // Clean up the workspace after the pipeline execution
        }
        success {
            echo 'SonarQube analysis successful.'
        }
        failure {
            echo 'SonarQube analysis failed.'
        }
    }
}
