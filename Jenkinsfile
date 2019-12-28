pipeline {
    agent none
    stages {
        stage('Run test') {
            agent { 
                docker {
                    image 'gabrielgio/clojure-jenkins'
                    args '-e HOME=/home/jguest'
                }
            }
            steps {
                sh 'clojure -A:test'
            }
        }
        stage('Docker building') {
            agent any
            steps {
                sh "docker build -t gabrielgio/hcrawler-runner:0.0.${env.BUILD_NUMBER} -t gabrielgio/hcrawler:latest ."
            }
        }
        stage('Docker pushing') {
            agent any
            steps {
                sh "docker push gabrielgio/hcrawler-runner:0.0.${env.BUILD_NUMBER}"
                sh "docker push gabrielgio/hcrawler-runner:latest"
            }
        }   
    }
}
