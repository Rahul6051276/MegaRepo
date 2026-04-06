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
        // इसे 2.0.0 या उससे ऊपर रखना ज़रूरी है नए Metadata के लिए
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

fun Project.cloudstream(configuration: CloudstreamExtension.() -> Unit) = 
    extensions.getByName<CloudstreamExtension>("cloudstream").configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = 
    extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        // आपका GitHub Repository का नाम यहाँ ऑटोमैटिक सेट होगा
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "Rahul6051276/MegaRepo")
    }

    android {
        namespace = "com.mega"

        defaultConfig {
            minSdk = 24 // आधुनिक एप्स के लिए 24 (Android 7) बेहतर है
            targetSdk = 35
            compileSdkVersion(35)
        }

        compileOptions {
            // CloudStream के नए वर्जन Java 17 पर ही चलते हैं
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType<KotlinJvmCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                // पुराने वर्जन चेक को स्किप करने के लिए एक्स्ट्रा आर्ग्यूमेंट्स
                freeCompilerArgs.addAll(
                    "-Xskip-prerelease-check",
                    "-Xallow-unstable-dependencies",
                    "-Xno-call-assertions",
                    "-Xno-param-assertions",
                    "-Xno-receiver-assertions"
                )
            }
        }
    }

    dependencies {
        val apk by configurations
        val implementation by configurations

        // Cloudstream की लेटेस्ट प्री-रिलीज़ लाइब्रेरी
        apk("com.lagradost:cloudstream3:pre-release")

        implementation(kotlin("stdlib"))
        implementation("com.github.Blatzar:NiceHttp:0.4.11")
        implementation("org.json:json:20240303")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    }
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
