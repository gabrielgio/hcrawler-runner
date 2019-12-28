pipeline {
    agent { 
        docker 'gabrielgio/clojure' 
        args '-v $HOME/.m2:/root/.m2'
    }
    stages {
        stage('Run test') {
            steps {
                sh 'clojure -A:test'
            }
        }
    }
}
