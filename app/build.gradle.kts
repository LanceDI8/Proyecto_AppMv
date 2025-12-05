plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // KSP para Room
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.di8.proyecto_appmv"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.di8.proyecto_appmv"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    // -- Compose BOM (se ajusta a la version estable usaremos la clasica compatible)
    val composeBom = (platform("androidx.compose:compose-bom:2025.11.00"))
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Iconos de Compose (para Icons.Filled.*
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")

    // Navegación Compose
    implementation("androidx.navigation:navigation-compose:2.9.6")

    // Lifecycle/ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // AppCompat (para cambio dinámico de idioma)
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-ktx:1.17.0")

    // Room + KSP
    implementation("androidx.room:room-ktx:2.8.3")
    ksp("androidx.room:room-compiler:2.8.3")

    // ML Kit Translate + coroutines para Play Services
    implementation("com.google.mlkit:translate:17.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // Coil para imágenes
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Material clásico (no es obligatorio, pero no esta demas)
    implementation("com.google.android.material:material:1.13.0")

}