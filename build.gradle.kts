import com.android.build.gradle.BaseExtension
import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("com.github.recloudstream:gradle:master-SNAPSHOT")
        // आपके Gradle 8.12 के साथ यह वर्जन बेस्ट काम करेगा
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10") 
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    extensions.configure<CloudstreamExtension> {
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "Rahul6051276/MegaRepo")
    }

    extensions.configure<BaseExtension> {
        namespace = "com.mega"
        compileSdkVersion(35)
        defaultConfig {
            minSdk = 26
            targetSdk = 35
        }
        compileOptions {
            // नई मशीन (8.12) के लिए Java 17 अनिवार्य है
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        tasks.withType<KotlinJvmCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                // यह लाइन वर्जन के छोटे-मोटे झगड़ों को खत्म कर देगी
                freeCompilerArgs.addAll("-Xskip-prerelease-check", "-Xallow-unstable-dependencies")
            }
        }
    }

    dependencies {
        val apk by configurations
        val implementation by configurations
        apk("com.lagradost:cloudstream3:pre-release")
        implementation(kotlin("stdlib"))
        implementation("org.json:json:20240303")
    }
}
