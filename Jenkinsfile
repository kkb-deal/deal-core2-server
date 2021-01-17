@Library('pipeline@Nex-Gen')_

//Discard old build
properties([buildDiscarder(logRotator(numToKeepStr: '50', artifactNumToKeepStr: '100'))])

delivery {
    apply plugin: "Main_line"
    apply plugin: "K8S_line"

    mainLine {
        name = "deal-core2"
        version = "2.0.0-${env.COMMIT_ID}"
        branch = "${env.gitlabSourceBranch}"
        deployT2Node = "aliyun360"
        projectType = "java"
        toMail = "zhuguoliang@kuick.cn"
        testBase = "Y"
        changeLog = "Y"
        useUnitTest = false
        useApiTest = false
        timeout = 24
    }
}