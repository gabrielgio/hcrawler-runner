pipeline {
    agent { 
        docker {
            image 'gabrielgio/clojure'
            args '-v /var/lib/jenkins/workspace/hcrawler-runner:/var/lib/jenkins/workspace/hcrawler-runner'
        }
    }
    stages {
        stage('Run test') {
            steps {
                sh 'clojure -A:test'
            }
        }
    }
}
