pipeline {
    agent any

    environment {
        GIT_URL = 'https://github.com/PodongDang/Hstagram.git'
        GIT_BRANCH = 'master' // 또는 master
        GIT_ID = 'skala-github-id' // GitHub PAT credential ID
        IMAGE_REGISTRY = 'amdp-registry.skala-ai.com/skala25a'
        IMAGE_NAME = 'sk030-my-app'
        IMAGE_TAG = '1.0.0'
        DOCKER_CREDENTIAL_ID = 'skala-image-registry-id'  // Harbor 인증 정보 ID
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: "${GIT_BRANCH}",
                    url: "${GIT_URL}",
                    credentialsId: "${GIT_ID}"   // GitHub PAT credential ID
            }
        }

        stage('Build with Gradle') {
            steps {
                sh '''
                    cd Hstagram
                    chmod +x gradlew
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry("https://${IMAGE_REGISTRY}", "${DOCKER_CREDENTIAL_ID}") {
                        def appImage = docker.build("${IMAGE_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}", "--platform=linux/amd64 Hstagram")
                        appImage.push()
                    }
                }
            }
        }
    }
}

