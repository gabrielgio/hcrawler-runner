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
                sh 'clojure -A:test'
            }
        }
    }
}
