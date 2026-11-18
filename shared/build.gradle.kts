import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    android {
        namespace = "mrpapermoon.customebutton.shared"
        compileSdk = 37
        minSdk = 30
        withHostTest {}
    }
    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            api(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
    iosArm64()
    iosSimulatorArm64()
}


dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
