
pipeline {
    agent any
 
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'echo "Checked out branch: ${GIT_BRANCH}"'
            }
        }
 
        stage('Build') {
            steps {
                echo 'Build step placeholder — replace with your real build command.'
            }
        }
 
        stage('Test') {
            steps {
                echo 'Test step placeholder — replace with your real test command.'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(credentials: ['jenkins_deploy_key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no pwallgren@192.168.0.22 \
                            "echo 'Deployed by Jenkins at' \$(date) > /tmp/jenkins-deploy-test.txt && cat /tmp/jenkins-deploy-test.txt"
                    '''
                }
            }
        }
    }
 
    post {
        success {
            echo "Build #${BUILD_NUMBER} succeeded."
        }
        failure {
            echo "Build #${BUILD_NUMBER} failed — check the console output above."
        }
    }
}
 
