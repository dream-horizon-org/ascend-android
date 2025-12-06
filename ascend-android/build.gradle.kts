import java.util.Properties

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
    id("jacoco")
}

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

jacoco {
    toolVersion = "0.8.14"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
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
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.14.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

publishing {
    publications {
        create<MavenPublication>("staging") {
            groupId = "com.dream.sports"
            artifactId = "ascend-android"
            version = "1.0.1-staging"

            afterEvaluate {
                artifact(tasks.getByName("bundleReleaseAar"))
            }

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")

                fun addDependency(dep: Dependency, scope: String) {
                    if (dep.group == null || dep.version == null || dep.name == "unspecified") return
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", dep.group)
                    dependencyNode.appendNode("artifactId", dep.name)
                    dependencyNode.appendNode("version", dep.version)
                    dependencyNode.appendNode("scope", scope)
                }

                configurations.getByName("api").dependencies.forEach {
                    addDependency(
                        it,
                        "compile"
                    )
                }
                configurations.getByName("implementation").dependencies.forEach {
                    addDependency(
                        it,
                        "runtime"
                    )
                }
            }
        }
    }


    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dream11/ascend-android-sdk")

            val githubProperties = Properties()
            val githubPropsFile = rootProject.file("github.properties")
            if (githubPropsFile.exists()) {
                githubProperties.load(githubPropsFile.inputStream())
            }

            val username = githubProperties["githubUser"] as? String ?: System.getenv("GPR_USER")
            val password = githubProperties["githubKey"] as? String ?: System.getenv("GPR_API_KEY")

            if (username == null || password == null) {
                throw GradleException("GitHub credentials not set! Define github.properties or env variables GPR_USER/GPR_API_KEY")
            }

            credentials {
                this.username = username
                this.password = password
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    // ✅ Make sure dependent Android tasks are finished first
    dependsOn(
        tasks.withType<Test>(),
        "processDebugManifest",
        "compileDebugLibraryResources"
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*"
    )

    val buildDirFile = layout.buildDirectory.asFile.get()

    val debugTree = fileTree("${buildDirFile}/intermediates/javac/debug") {
        exclude(fileFilter)
    }

    val kotlinDebugTree = fileTree("${buildDirFile}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    executionData.setFrom(fileTree(buildDirFile).include("**/*.exec", "**/*.ec"))
}