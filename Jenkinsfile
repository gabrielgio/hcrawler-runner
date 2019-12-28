pipeline {
    agent { 
        docker {
            image 'gabrielgio/clojure'
            args '-u 1000:1000'
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
