pipeline {
    agent { 
        docker {
            image 'gabrielgio/clojure-jenkins'
            args '-e HOME=/home/jguest -v $HOME/.m2:/home/jguest/.m2:rw,z'
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
