import java.util.Properties
plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
    id("signing")
}
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = mutableMapOf<String, String>()
if (localPropertiesFile.exists()) {
    localPropertiesFile.readLines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
            val index = trimmed.indexOf('=')
            if (index > 0) {
                val key = trimmed.substring(0, index).trim()
                val value = trimmed.substring(index + 1).trim()
                localProperties[key] = value
            }
        }
    }
}
fun getLocalProperty(key: String): String? = localProperties[key]
val signingKeyId = getLocalProperty("signing.keyId") ?: ""
val signingKey = getLocalProperty("signing.key") ?: ""
val signingPassword = getLocalProperty("signing.password") ?: ""
val sdkVersion = getLocalProperty("sdk_version") ?:"0.0.0"
android {
    namespace = "com.application.ascend_android"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField("boolean", "IS_LOG_ENABLED", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "IS_LOG_ENABLED", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.dagger:dagger:2.57.2")
    kapt("com.google.dagger:dagger-compiler:2.57.2")
    implementation("androidx.room:room-runtime:2.8.1")
    kapt("androidx.room:room-compiler:2.8.1")
    api("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
}
publishing {
    publications {
        create<MavenPublication>("bar") {
            groupId = "com.dream11" //Change this to your group id io.d11 or com.dream11
            artifactId = "ascend-android-sdk" // Change this to your artifact id
            version = sdkVersion
            artifact("${layout.buildDirectory.get()}/outputs/aar/ascend-android-release.aar") // Change AAR PATh
            pom {
                name.set("asend-android-sdk")
                description.set("Ascend Android SDK")
                url.set("https://github.com/ds-horizon/ascend-android")// Repo Path
                licenses {
                    license {
                        name.set("Ascend License")
                        url.set("https://github.com/ds-horizon/ascend-android/blob/main/ascend-android-sdk/LICENSE") //Repo Path to License
                    }
                }
                developers {
                    developer {
                        id.set("harshmishradream11")
                        name.set("Harsh Mishra")
                        email.set("harsh.mishra@dream11.com")
                    }
                    developer {
                        id.set("PrabhSing77")
                        name.set("Prabhjot Singh")
                        email.set("prabhjot.singh@dream11.com")
                    }

                    developer {
                        id.set("arinjay-d11")
                        name.set("Arinjay Patni")
                        email.set("arinjay.patni@dream11.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ds-horizon/ascend-android.git")//Change This
                    developerConnection.set("scm:git:ssh://github.com/ds-horizon/ascend-android.git") ///Change This
                    url.set("https://github.com/ds-horizon/ascend-android")// /Change This
                }
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    val addDependency = addDependency@ { dep: org.gradle.api.artifacts.Dependency, scope: String ->
                        if (dep.group == null || dep.version == null || dep.name == "unspecified") {
                            return@addDependency
                        }
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", dep.group)
                        dependencyNode.appendNode("artifactId", dep.name)
                        dependencyNode.appendNode("version", dep.version)
                        dependencyNode.appendNode("scope", scope)
                        if (dep is org.gradle.api.artifacts.ModuleDependency) {
                            if (!dep.isTransitive) {
                                val exclusionNode = dependencyNode.appendNode("exclusions").appendNode("exclusion")
                                exclusionNode.appendNode("groupId", "*")
                                exclusionNode.appendNode("artifactId", "*")
                            } else if (dep.excludeRules.isNotEmpty()) {
                                val exclusionsNode = dependencyNode.appendNode("exclusions")
                                dep.excludeRules.forEach { rule ->
                                    val exclusionNode = exclusionsNode.appendNode("exclusion")
                                    exclusionNode.appendNode("groupId", rule.group ?: "*")
                                    exclusionNode.appendNode("artifactId", rule.module ?: "*")
                                }
                            }
                        }
                    }
                    configurations["api"].dependencies.forEach { dep -> addDependency(dep, "compile") }
                    configurations["implementation"].dependencies.forEach { dep -> addDependency(dep, "runtime") }
                }
            }
        }
    }
}
tasks.register("cleanBuildPublishAscend") {
    dependsOn("clean")
    dependsOn("bundleReleaseAar")
    dependsOn("publishBarPublicationToSonatypeRepository")
    // Only add root project tasks if they exist
    rootProject.tasks.findByName("publishToSonatype")?.let {
        dependsOn(it)
    }
    rootProject.tasks.findByName("closeSonatypeStagingRepository")?.let {
        dependsOn(it)
    }
}
afterEvaluate {
    tasks.named("publishBarPublicationToSonatypeRepository") {
        mustRunAfter("bundleReleaseAar")
    }
    // Only configure root project tasks if they exist
    rootProject.tasks.findByName("publishToSonatype")?.let { publishTask ->
        publishTask.mustRunAfter("publishBarPublicationToSonatypeRepository")
    }
    rootProject.tasks.findByName("closeSonatypeStagingRepository")?.let { closeTask ->
        rootProject.tasks.findByName("publishToSonatype")?.let { publishTask ->
            closeTask.mustRunAfter(publishTask)
        }
    }
    // Only configure signing task if it exists (only created when signing is configured)
    tasks.findByName("signBarPublication")?.let {
        it.dependsOn("bundleReleaseAar")
    }
    tasks.named("bundleReleaseAar") {
        mustRunAfter("clean")
    }
}
signing {
    if (signingKeyId.isNotEmpty() && signingKey.isNotEmpty() && signingPassword.isNotEmpty()) {
        useInMemoryPgpKeys(
            signingKeyId,
            signingKey,
            signingPassword
        )
        sign(publishing.publications["bar"])
    }
}
//publishing {
//    publications {
//        create<MavenPublication>("staging") {
//            groupId = "com.dream.sports"
//            artifactId = "ascend-android"
//            version = "1.0.1-staging"
//
//            afterEvaluate {
//                artifact(tasks.getByName("bundleReleaseAar"))
//            }
//
//            pom.withXml {
//                val dependenciesNode = asNode().appendNode("dependencies")
//
//                fun addDependency(dep: Dependency, scope: String) {
//                    if (dep.group == null || dep.version == null || dep.name == "unspecified") return
//                    val dependencyNode = dependenciesNode.appendNode("dependency")
//                    dependencyNode.appendNode("groupId", dep.group)
//                    dependencyNode.appendNode("artifactId", dep.name)
//                    dependencyNode.appendNode("version", dep.version)
//                    dependencyNode.appendNode("scope", scope)
//                }
//
//                configurations.getByName("api").dependencies.forEach {
//                    addDependency(
//                        it,
//                        "compile"
//                    )
//                }
//                configurations.getByName("implementation").dependencies.forEach {
//                    addDependency(
//                        it,
//                        "runtime"
//                    )
//                }
//            }
//        }
//    }
//
//
//    repositories {
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/dream11/ascend-android-sdk")
//
//            val githubProperties = Properties()
//            val githubPropsFile = rootProject.file("github.properties")
//            if (githubPropsFile.exists()) {
//                githubProperties.load(githubPropsFile.inputStream())
//            }
//
//            val username = githubProperties["githubUser"] as? String ?: System.getenv("GPR_USER")
//            val password = githubProperties["githubKey"] as? String ?: System.getenv("GPR_API_KEY")
//
//            if (username == null || password == null) {
//                throw GradleException("GitHub credentials not set! Define github.properties or env variables GPR_USER/GPR_API_KEY")
//            }
//
//            credentials {
//                this.username = username
//                this.password = password
//            }
//        }
//    }
//}