import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

// Automatically restore official debug.keystore from base64 if missing
val officialKeystoreBase64 = "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"
val debugKeystoreFile = file("${rootDir}/debug.keystore")
if (!debugKeystoreFile.exists()) {
  try {
    val decoded = Base64.getDecoder().decode(officialKeystoreBase64)
    debugKeystoreFile.writeBytes(decoded)
  } catch (_: Exception) {}
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.typingmaster.bngtyp"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.firestore)

  // Firebase Auth and Google Sign-In via Credential Manager:
  implementation(libs.firebase.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  implementation(libs.play.services.ads)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
