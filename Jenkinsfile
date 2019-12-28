pipeline {
    agent { 
        docker {
            image 'gabrielgio/clojure'
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
