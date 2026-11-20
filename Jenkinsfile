pipeline {
    agent any

    stages {
        stage('Check Structure') {
            steps {
                bat 'if exist pom.xml (echo pom.xml found) else (echo pom.xml NOT found & exit /b 1)'
                bat 'if exist Jenkinsfile (echo Jenkinsfile found) else (echo Jenkinsfile NOT found & exit /b 1)'
                bat 'if exist src (echo src found) else (echo src NOT found & exit /b 1)'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn compile'
            }
        }
        stage('Static Analysis') {
            steps {
                bat 'mvn checkstyle:check'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }
}
