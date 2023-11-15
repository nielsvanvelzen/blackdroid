plugins {
	alias(libs.plugins.android.app)
	alias(libs.plugins.kotlin.android)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

android {
	namespace = "nl.ndat.blackdroid"
	compileSdk = 34

	defaultConfig {
		minSdk = 21
		targetSdk = 34

		applicationId = "nl.ndat.blackdroid"
		versionCode = 1_00_00
		versionName = "1.0.0"
	}

	buildFeatures {
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
}

dependencies {
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.foundation)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.media3.exoplayer)
	implementation(libs.androidx.media3.ui)
	implementation(libs.google.flexbox)
	debugImplementation(libs.androidx.compose.ui.tooling)
}
