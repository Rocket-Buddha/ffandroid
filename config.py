def can_build(plat):
	return (plat == "android")

def configure(env):
    if env['platform'] == 'android':
	# Admob
        env.android_add_dependency("compile 'com.google.android.gms:play-services-ads:11.8.0'")
        env.android_add_dependency("compile 'com.google.firebase:firebase-ads:11.8.0'")
        # Analitycs
        env.android_add_dependency("compile 'com.google.firebase:firebase-core:11.8.0'")
        # Auth
        env.android_add_dependency("compile 'com.google.code.gson:gson:2.8.2'")
        #env.android_add_dependency("compile 'com.google.firebase:firebase-auth:11.8.0'")
        env.android_add_dependency("compile 'com.google.android.gms:play-services-auth:11.8.0'")
        # Firebase real time database
        #env.android_add_dependency("compile 'com.google.firebase:firebase-database:11.8.0'")
        #env.android_add_dependency("compile 'com.fasterxml.jackson.core:jackson-core:2.9.4'")
        #env.android_add_dependency("compile 'com.fasterxml.jackson.core:jackson-databind:2.9.4'")
        # Resource Generator
        env.android_add_dependency("compile 'com.firebase:firebase-jobdispatcher:0.5.2'")
        env.android_add_dependency("compile 'commons-net:commons-net:3.6'")
        # FB Remote config
        env.android_add_dependency("compile 'com.google.firebase:firebase-config:11.8.0'")
	# Facebook SDK
	env.android_add_dependency("compile 'com.facebook.android:facebook-android-sdk:[5,6)'")
        # Generals
        env.android_add_dependency("compile 'com.google.android.gms:play-services-drive:11.8.0'")
        env.android_add_dependency("compile 'com.google.android.gms:play-services-games:11.8.0'")
        env.android_add_default_config("minSdkVersion 21")
        env.android_add_maven_repository("url 'https://maven.google.com'")
        env.android_add_gradle_plugin("com.google.gms.google-services")
        env.android_add_gradle_classpath("com.google.gms:google-services:3.1.2")
        env.android_add_to_manifest("android/AndroidManifestChunk.xml")
        env.android_add_to_permissions("android/AndroidPermissionsChunk.xml");
        env.android_add_default_config("applicationId 'com.fiftyfive.studios.fandango'")
        env.android_add_java_dir("android/src")
        env.android_add_res_dir("android/res")
        env.disable_module()
        
