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
        // इसे 2.1.10 पर फिक्स कर दिया है
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
            minSdk = 26 // Min SDK थोड़ा बढ़ा दिया है बेहतर सपोर्ट के लिए
            targetSdk = 35
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType<KotlinJvmCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                // यहाँ हम नए Metadata को सपोर्ट करने के लिए एक्स्ट्रा आर्ग्यूमेंट जोड़ रहे हैं
                freeCompilerArgs.addAll("-Xskip-prerelease-check", "-Xallow-unstable-dependencies")
            }
        }
    }

    dependencies {
        val apk by configurations
        val implementation by configurations
        apk("com.lagradost:cloudstream3:pre-release")
        implementation(kotlin("stdlib"))
        implementation("com.github.Blatzar:NiceHttp:0.4.11")
        implementation("org.json:json:20240303") // देशी JSON सपोर्ट के लिए
    }
}
