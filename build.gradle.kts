// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"

}
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(java.io.FileInputStream(localPropertiesFile))
}

fun getLocalProperty(key: String): String? = localProperties.getProperty(key)

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(getLocalProperty("stagingProfileId") ?: "")
            username.set(getLocalProperty("portalUsername") ?: "")
            password.set(getLocalProperty("portalPassword") ?: "")
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}