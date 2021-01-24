// harbor镜像仓库地址
def registry = "registry.cn-hangzhou.aliyuncs.com/zjy-namespace/"

// 上传到harbor项目名称，jenkins每次构建版本名称
def project = "baiwan"
def app_name = "deal-core2-server"
def image_name = "${registry}/${project}/${app_name}:${BUILD_NUMBER}"

//git地址换成自己的仓库地址
def git_address = "https://github.com/kkb-deal/deal-core2-server.git"
//def k8s_auth = "ce7ed9e9-c88d-4af5-a5e9-dc6d4c615050"

// 认证-账号脱敏
//def secret_name = "registry-pull-secret"
//def docker_registry_auth = "b51ba954-a17b-40a5-8c2b-df297d7dc60f"
//def git_auth = "f3774951-6115-43d1-84da-066629855a5c"

//pipeline中jenkins-slave配置
podTemplate(label: 'jenkins-slave', cloud: 'kubernetes', containers: [
    containerTemplate(
        name: 'jnlp',
        //image: "openshift/jenkins-slave-maven-centos7"
        image: "registry.cn-beijing.aliyuncs.com/kkb2/jenkins:v01"
    ),
  ],
  volumes: [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
    hostPathVolume(mountPath: '/usr/bin/docker', hostPath: '/usr/bin/docker')
  ],
)

{
  node("jenkins-slave"){
      // 第一步，拉取你的项目代码到本地
      stage('拉取代码'){
//         checkout([$class: 'GitSCM', branches: [[name: '${Branch}']], userRemoteConfigs: [[credentialsId: "${git_auth}", url: "${git_address}"]]])
         checkout([$class: 'GitSCM', branches: [[name: '${Branch}']], userRemoteConfigs: [[ url: "${git_address}"]]])
      }
      // 第二步进行编译，编译完成copy到你的镜像中
      stage('代码编译'){
          sh "java -version"
          sh "./gradlew clean build"
          sh "pwd"
          sh "ls build/libs/"
      }
      // 第三步，构建你的docker镜像，dockerfile是在你的代码仓库中，以拉取到本地，直接docker build即可
      stage('构建镜像'){
//          withCredentials([usernamePassword(credentialsId: "${docker_registry_auth}", passwordVariable: 'password', usernameVariable: 'username')]) {
          withCredentials() {
            sh """
              ls
              docker build -t ${image_name} .
              docker push ${image_name}
               """
            }
      }
      // 第四步，将你的打包好的镜像发布到k8s中，Deploy.yml也是在你的代码仓库，yml文件需要根据你的需求自己去定义，不是通配的
      stage('部署到K8S平台完成'){
          sh """
          sed -i 's#\$IMAGE_NAME#${image_name}#' Deploy.yml
          sed -i 's#\$SECRET_NAME#${secret_name}#' Deploy.yml
          """
          kubernetesDeploy configs: 'Deploy.yml'
      }
  }
}