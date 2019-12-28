pipeline {
    agent { 
        docker {
            image 'gabrielgio/clojure'
            args '-e HOME=/home/jguest'
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
