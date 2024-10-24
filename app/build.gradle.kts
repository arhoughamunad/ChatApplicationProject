plugins {
    //alias(libs.plugins.android.application)
    //Nuevos plugins desde Firebase
    id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.chatapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chatapplication"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Firebase
    implementation("com.google.firebase:firebase-database:21.0.0") // Base de Datos
    implementation("com.google.firebase:firebase-storage:21.0.1")  // Base de Archivos
    implementation("com.google.firebase:firebase-auth:23.1.0") // Autenticarse a la BD


    // Diseño para el chat
    implementation("androidx.recyclerview:recyclerview:1.2.1") // Lista de chat
    implementation("androidx.cardview:cardview:1.0.0")  // Perfil de usuario


    // Agregados
    implementation("de.hdodenhof:circleimageview:1.3.0") // Imagen Circular
    implementation("com.github.bumptech.glide:glide:4.12.0") // Actualiza a Glide 4.12.0

    //Dependencias agregadas necesarias
    implementation(platform("com.google.firebase:firebase-bom:33.5.0"))
    implementation("com.google.firebase:firebase-analytics")

    //Dependencias para notificaciones
    implementation("com.google.firebase:firebase-messaging:24.0.3")
}