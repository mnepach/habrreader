apply plugin: 'com.android.application'

android {
    compileSdkVersion 33
    defaultConfig {
        applicationId "com.example.habrreader"
        minSdkVersion 21
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.5.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.android.material:material:1.7.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'

    // Retrofit для работы с API
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.9.3'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.3'

    // RxJava
    implementation 'io.reactivex.rxjava3:rxjava:3.1.5'
    implementation 'io.reactivex.rxjava3:rxandroid:3.0.0'
    implementation 'com.squareup.retrofit2:adapter-rxjava3:2.9.0'

    // Architecture Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.5.1'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.5.1'
    implementation 'androidx.room:room-runtime:2.4.3'
    implementation 'androidx.room:room-rxjava3:2.4.3'
    annotationProcessor 'androidx.room:room-compiler:2.4.3'

    // Glide для загрузки изображений
    implementation 'com.github.bumptech.glide:glide:4.14.2'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.14.2'

    // Тестирование
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.4'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.0'
}