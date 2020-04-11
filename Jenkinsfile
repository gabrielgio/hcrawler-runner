pipeline {
    agent none
    stages {
        stage('Run test') {
            agent { 
                docker {
                    image 'registry.gitlab.com/gabrielgio/clojure-jenkins'
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
                sh "docker build -t registry.gitlab.com/gabrielgio/hcrawler-runner:0.0.${env.BUILD_NUMBER} -t registry.gitlab.com/gabrielgio/hcrawler-runner:latest ."
            }
        }
        stage('Docker pushing') {
            agent any
            steps {
                sh "docker push registry.gitlab.com/gabrielgio/hcrawler-runner:0.0.${env.BUILD_NUMBER}"
                sh "docker push registry.gitlab.com/gabrielgio/hcrawler-runner:latest"
            }
        }   
    }
}
