plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cholera.eagleeye"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cholera.eagleeye"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/io.netty.versions.properties")
        exclude("META-INF/INDEX.LIST")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.gms:play-services-maps:18.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.tensorflow:tensorflow-lite:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.work:work-runtime:2.7.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.preference:preference:1.1.0-alpha02")

    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    implementation("com.google.api-client:google-api-client-android:1.34.1")
    implementation("com.google.api-client:google-api-client-gson:1.34.1")
    implementation("com.google.apis:google-api-services-gmail:v1-rev20240520-2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.35.0")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("com.google.android.gms:play-services-auth:20.4.0")
    implementation("com.google.http-client:google-http-client-gson:1.40.1")
    implementation ("commons-codec:commons-codec:1.15")
    implementation ("com.google.android.gms:play-services-auth:20.3.0")
    implementation ("com.google.api-client:google-api-client-android:1.33.0")
    implementation ("com.google.api-client:google-api-client-gson:1.33.0")
    implementation ("com.google.apis:google-api-services-gmail:v1-rev20230708-1.33.0")

}






//plugins {
//    id("com.android.application")
//    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
//    id("com.google.gms.google-services")
//}
//
//android {
//    namespace = "com.cholera.eagleeye"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.cholera.eagleeye"
//        minSdk = 24
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//
//    packagingOptions {
//        exclude("META-INF/DEPENDENCIES")
//        exclude("META-INF/LICENSE.md")
//        exclude("META-INF/NOTICE.md")
//        exclude ("META-INF/io.netty.versions.properties")
//        exclude ("META-INF/INDEX.LIST")
//    }
//}
//
//dependencies {
//    implementation("androidx.appcompat:appcompat:1.7.0")
//    implementation("com.google.android.material:material:1.4.0")
//    implementation("com.google.firebase:firebase-analytics")
//    implementation("com.google.firebase:firebase-messaging:24.0.0")
//    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
//    implementation("com.google.firebase:firebase-database:21.0.0")
//    implementation("com.android.volley:volley:1.2.1")
//    implementation("com.google.firebase:firebase-auth:23.0.0")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    implementation("com.google.android.gms:play-services-maps:18.0.0")
//    implementation("com.google.android.gms:play-services-location:21.0.1")
//    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation("com.squareup.okhttp3:okhttp:4.9.0")
//    implementation("org.tensorflow:tensorflow-lite:2.16.1")
//    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
//    implementation("com.github.bumptech.glide:glide:4.12.0")
//    implementation("androidx.work:work-runtime:2.7.1")
//    implementation ("de.hdodenhof:circleimageview:3.1.0")
//    implementation ("androidx.cardview:cardview:1.0.0")
//    implementation ("androidx.preference:preference:1.1.1")
//    implementation ("androidx.preference:preference:1.2.0")
//    implementation ("androidx.preference:preference:1.1.0-alpha02")
//
//    implementation ("com.sun.mail:android-mail:1.6.7")
//    implementation ("com.sun.mail:android-activation:1.6.7")
//
//
//
//    implementation ("com.google.api-client:google-api-client-android:1.34.1")
//    implementation ("com.google.api-client:google-api-client-gson:1.34.1")
//    implementation ("com.google.apis:google-api-services-gmail:v1-rev20240520-2.0.0")
//    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.35.0")
//    implementation ("com.google.code.gson:gson:2.8.8")
//
//    implementation ("com.google.android.gms:play-services-auth:19.2.0")
//    implementation ("com.google.http-client:google-http-client-gson:1.40.1")
//
//
//
//
//}
//
//
//
//
