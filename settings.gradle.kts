pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dream11/ascend-android-sdk")

            val githubProperties = java.util.Properties().apply {
                val file = file("github.properties")
                if (file.exists()) load(file.inputStream())
            }

            credentials {
                username = githubProperties["githubUser"] as? String
                    ?: System.getenv("GPR_USER")
                password = githubProperties["githubKey"] as? String
                    ?: System.getenv("GPR_API_KEY")
            }
        }

    }
}

rootProject.name = "ascend-android-sdk"
include(":app")
include(":ascend-android")
