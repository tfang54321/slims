
node {

  def mvnHome = tool 'gradle61'


stage('Checkout') {


 git 'https://github.com/tfang54321/slims.git'
mvnHome = tool 'gradle61'

}


stage('buildimage') {
sh  "'${mvnHome}/bin/gradle'   artifactoryPublish"

//sh  "'${mvnHome}/bin/gradle'   docker"

// sh " docker run -p 8095:5000 springio/gs-spring-boot-docker-slims1127"


}
}
