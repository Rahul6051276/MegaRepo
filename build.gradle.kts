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
        // Cloudstream gradle plugin
        classpath("com.github.recloudstream:gradle:master-SNAPSHOT")
        // यहाँ हमने वर्जन को 2.1.10 कर दिया है ताकि लेटेस्ट Metadata मैच हो सके
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

fun Project.cloudstream(configuration: CloudstreamExtension.() -> Unit) = extensions.getByName<CloudstreamExtension>("cloudstream").configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "Rahul6051276/MegaRepo")
    }

    android {
        namespace = "com.mega" // यहाँ नाम वही रखें जो आपके पैकेज का है

        defaultConfig {
            minSdk = 21
            targetSdk = 35
            compileSdkVersion(35)
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17 // Java 8 से 17 पर शिफ्ट होना जरूरी है
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType<KotlinJvmCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17) // Cloudstream अब Java 17 मांगता है
                freeCompilerArgs.addAll(
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

        // Cloudstream classes
        apk("com.lagradost:cloudstream3:pre-release")

        implementation(kotlin("stdlib")) 
        implementation("com.github.Blatzar:NiceHttp:0.4.11") 
        // Jackson का वर्जन भी बढ़ा दिया है ताकि 'mapper' वाले एरर न आएं
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
        implementation("org.jsoup:jsoup:1.18.3") 
    }
}

task<Delete>("clean") {
    delete(layout.buildDirectory)
}
