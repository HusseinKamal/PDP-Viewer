plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.hussein.pdfreader.domain"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    // For Uri support if needed in domain, though string might be better
}