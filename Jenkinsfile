pipeline {
    agent { docker 'gabrielgio/clojure' }
    stages {
        stage('Run test') {
            steps {
                sh 'clojure -A:test'
            }
        }
    }
}