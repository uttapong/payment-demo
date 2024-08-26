pipeline {
    agent any
    tools {
        maven 'maven_3_9'
    }
    
    parameters {
        string(name: 'GIT_URL', defaultValue: 'https://github.com/maxca/spring-java-jenkins.git', description: 'Git repository URL')
        string(name: 'GIT_BRANCH', defaultValue: 'main', description: 'Git branch to build')
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'jenkins', description: 'SonarQube project key')
        string(name: 'DOCKER_IMAGE', defaultValue: 'your-docker-image:tag', description: 'Docker image to scan')
    }
    
    environment {
        SONAR_JAVA_BINARIES = 'target/classes'
        ZIP_FILE_NAME = "${params.DOCKER_IMAGE}".replace(':', '-') // Replace colon in Docker image name
    }

    stages { 
        stage('Prepare') {
            steps {
                script {
                    // Create directory to store artifacts
                    sh 'mkdir -p scan-artifacts'
                }
            }
        }
        stage('SCM Checkout') {
            steps {
                git branch: "${params.GIT_BRANCH}",
                    url: "${params.GIT_URL}",
                    credentialsId: 'gitlabpwd'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
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
        stage('OWASP Dependency-Check Vulnerabilities') {
            steps {
                dependencyCheck additionalArguments: ''' 
                            -o './scan-artifacts'
                            -s './'
                            -f 'ALL' 
                            --prettyPrint''', odcInstallation: 'dependency-check'
                
                dependencyCheckPublisher pattern: 'scan-artifacts/dependency-check-report.xml'
            }
        }
        stage('Check for Outdated Dependencies') {
            steps {
                script {
                    sh 'mvn versions:display-dependency-updates > scan-artifacts/dependency-updates.txt'
                    sh 'mvn versions:display-plugin-updates > scan-artifacts/plugin-updates.txt'
                }
            }
        }
        stage('Trivy Image Scan') {
            steps {
                script {
                    def result = sh(script: "/opt/homebrew/bin/trivy image --severity HIGH,CRITICAL --no-progress --exit-code 1 ${params.DOCKER_IMAGE}", returnStatus: true)
                    if (result != 0) {
                        echo "Trivy scan completed with exit code ${result}. Check the report for details."
                    } else {
                        echo "Trivy scan completed successfully."
                    }
                }
            }
        }
    }
    
    post {
        always {
            script {
                // Ensure the scan-artifacts directory exists
                sh 'ls -lah scan-artifacts'
                
                // Archive scan artifacts as a zip file
                def zipFileName = "scan-artifacts-${env.BUILD_NUMBER}.zip"
                sh "zip -r ${zipFileName} scan-artifacts || echo 'Zip command failed'"
                
                // Archive the zip file for Jenkins
                archiveArtifacts artifacts: zipFileName
                
                // Clean up workspace
                cleanWs() 
            }
        }
        success {
            echo 'SonarQube analysis and Trivy scan successful.'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}
