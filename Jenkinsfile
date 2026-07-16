
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
 
