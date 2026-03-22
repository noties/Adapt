plugins {
    `java-platform`
    alias(libs.plugins.vanniktech.publish)
}

dependencies {
    constraints {
        api(project(":adapt"))
        api(project(":adapt-kt"))
        api(project(":adapt-ui"))
        api(project(":adapt-ui-flex"))
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
}