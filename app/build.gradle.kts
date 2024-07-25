plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	id("com.google.devtools.ksp")
}

android {
	namespace = "final_project.pemrograman_mobile.kelompok_7.mymoney"
	compileSdk = 34

	defaultConfig {
		applicationId = "final_project.pemrograman_mobile.kelompok_7.mymoney"
		minSdk = 26
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		ksp {
			arg("room.schemaLocation", "$projectDir/schemas")
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			signingConfig = signingConfigs.getByName("debug")
		}

		create("debugpro") {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			signingConfig = signingConfigs.getByName("debug")
		}
	}

	signingConfigs {

	}

	viewBinding {
		enable = true
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

	kotlinOptions {
		jvmTarget = "1.8"
	}
}

dependencies {
	implementation(libs.material)

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.androidx.fragment)
	implementation(libs.androidx.fragment.ktx)

	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.kotlin.reflect)

	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	annotationProcessor(libs.androidx.room.compiler)
	ksp(libs.androidx.room.compiler)
}