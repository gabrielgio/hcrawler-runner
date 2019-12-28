pipeline {
    agent { 
        docker {
            image 'gabrielgio/clojure'
            args '-e HOME=/root'
        }
    }
    stages {
        stage('Run test') {
            steps {
                sh 'whoami'
                sh 'clojure -A:test'
            }
        }
    }
}
