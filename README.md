# Readme

V1.0 by Rocket Buddha andres.julian.lusi@gmail.com. 19 de septiembre de 2017 10:01

V1.1 by Rocket Buddha andres.julian.lusi@gmail.com. 19 de septiembre de 2017 16:44


## Sobre el modulo en general
FFStudios ofrece a tu juego hecho en Godot engine 2.X funcionalidades de integración con Android, FireBase, Google Analitycs, Facebook SDK, Google Play Juegos entre otros. Para más información puedes revisar la [Wiki](https://bitbucket.org/55_studios/analitycsff/wiki/Home).

## Como integrar este módulo con Godot Engine
Antes de empezar quizás quieras revisar la [documentación oficial](http://docs.godotengine.org/en/stable/development/compiling/compiling_for_android.html#building-the-export-templates).

### Asegúrate de contar con todo lo necesario para trabajar con fuentes de Godot para compilar a Android
Asegúrate de tener todo lo que necesitas para compilar para x11(Linux):

En Fedora:

```bash
sudo dnf install scons pkgconfig libX11-devel libXcursor-devel libXrandr-devel libXinerama-devel mesa-libGL-devel alsa-lib-devel pulseaudio-libs-devel freetype-devel openssl-devel libudev-devel mesa-libGLU-devel
```

Descarga e instala Android Studio y asegúrate antes de iniciar de tenerlo actualizado. Puede ser que se produzcan algunos errores de compatibilidad a la hora de buildear el APK, sobre todo si no deseamos usar el wrapper de gradle (no recomendable).

Antes de continuar familiarízate con [Scons](http://scons.org/) y con [Android NDK](https://developer.android.com/ndk/index.html).

Setear las variables de entorno pero con TU PROPIA RUTA.

```bash
export ANDROID_HOME=~/Android/Sdk
export ANDROID_NDK_ROOT=~/Android/android-ndk-r*
```

Para ser agregadas permanentemente agregar las líneas al *.bash_profile.sh*(Linux, si usas Windows, seta variables de entorno desde propiedades de Mi Pc) en el home.

Scons utilizara estos paths para poder compilar el engine en librerías dinámicas .so (shared objects, simil dinamic link library de Win, pero en Unix) utilizables mediante NDK de Android.

### Baja la versión correcta de Godot
Recuerda que los templates de exportación deben ser compatibles con la versión del editor, entonces si estas utilizando la última versión del editor 2.1 por ejemplo, entonces deberás bajar el ultimo código de la 2.1, entonces te va a convenir bajar solo esa rama para no tener problemas de compatibilidad entre versiones.

```bash
git clone https://github.com/godotengine/godot.git --branch 2.1
```

### Bajar los modulos

Parado en:

*godot/modules/*

Ejecutar cambiando tu nombre de usuario:

```bash
git clone https://Edu_55@bitbucket.org/55_studios/ffandroid.git
```

#### Modificando el archivo config.py
El archivo config.py es el archivo que utiliza Scons para saber como armar el *build.gradle* el cual luego utilizaremos para buildear el .apk.

En este caso deberemos modificar la línea:

```python
env.android_add_default_config("applicationId '[your.app.package.id]'")
```

#### Modificando el archivo AndroidManifestChunk.xml
El archivo AndroidManifestChunk.xml es el archivo que utiliza Scons para construir el *AndroidManifest.xml* 
```xml
<meta-data android:name="com.google.android.gms.games.APP_ID" android:value="\u003<app_id>" />
```

### Compilando libgodot_android.so debug y release

Ahora vamos a compilar las librería .so que la .apk utilizara implementando la Native Developer Kit, NDK. Básicamente lo que estamos haciendo en este paso es compilar todo el engine en una librería dinámica que invocara la .apk.

Necesitamos dos librerías, para las dos .apk distinta que normalmente generamos, la de debug (con todos los símbolos) y la de release que es la que termina deployandose a producción.

Parados en el root del source de Godot, ejecutaremos:

```bash
scons platform=android target=release_debug android_arch=armv7 -j4
scons platform=android target=release_debug android_arch=arm64v8 -j4

scons platform=android target=release android_arch=armv7 -j4
scons platform=android target=release android_arch=arm64v8 -j4
```

Nota: Con -j especificamos cuantos procesadores dedicaremos a la compilación y con android_arch indicamos la arquitectura, 32bits con armv7 y 64bits con arm64v8.

Una vez finalizadas las dos compilaciones se deberían haber generado las librerías:

*godot/platform/android/java/libs/debug/armeabi-v7a/libgodot_android.so*
*godot/platform/android/java/libs/debug/arm64-v8a/libgodot_android.so*

*godot/platform/android/java/libs/release/armeabi-v7a/libgodot_android.so*
*godot/platform/android/java/libs/release/arm64-v8a/libgodot_android.so*

Scons también generara el el build.gradle, archivo que se usara para buildear el.apk:

*godot/platform/android/java/build.gradle*

Y el archivo:

*godot/modules/analitycsff/config.pyc*

Deberás familiarizarte un poco con [Gradle](https://gradle.org/), un build tool que cada vez está cobrando más fuerza.

#### Trubleshooting

* No poner -j con mas cantidad de procesadores de los que realmente se tiene por que dara un error de compilación.
* En algunos casos si alguno de los archivos que intentara sobre-escribir Scons esta tomado de alguna. Es recomendable limpiar antes de volver a compilar.

### El archivo google-services.json
Debes tener en cuenta que para setear la configuración de FireBase, Android utiliza el archivo google-services.json. Antes de continuar debes colocar tu archivo google-services.json en:

*godot/platform/android/java/*

Este archivo será empaquetado por Gradle en los .apk finales.

### Compilando custom templates .apk

Parado en:

*godot/platform/android/java/*

Ejecutar:

```bash
./gradlew build
```

Podría ejecutarse gradle build de estar instalado pero usando el gradle wrapper nos aseguramos de buildear la apk con la versión que utilizo el desarrollador, de esta forma podemos evitar problemas de compatibilidad. Gradlew descargara la versión correcta y la utilizara para buildear la apk.

#### Trubleshooting
* Gradle a veces no piso los archivos, es recomendable borrar el contenido de las carpetas:

	*godot/bin*

	*godot/plataform/android/java/build*

	*godot/plataform/android/java/libs*

### Usando los custom templates
El resultado serán los archivos *android_debug.apk* y *android_release.apk*, ubicados en:

*godot/bin*

Colocar estos como custom templates en el editor en:

*Exportar > Android*

En la sección de "Custom Package".

### Permisos
Existen dos formas de configurar los permisos para poder utilizar el modulo.

#### Permisos desde Godot
La primera, la más fácil, desde el editor de Godot ingresar:

*Exportar > Android*

En la sección "Permissions": tildar las opciones:

* INTERNET
* ACCESS_NETWORK_STATE

Godot incorporara los permisos al archivo *Androidmanifest.xml* cuando exporte el proyecto.

#### Permisos desde el modulo
Previo al iniciar la compilación agregar al archivo config.py la línea:

```bash
env.android_add_to_permissions("android/AndroidPermissionsChunk.xml");
```

Y agregar en la carpeta *android/* el archivo *AndroidPermissionsChunk.xml*, con el siguiente contenido:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
De esta manera los nuevos permisos ya serán agregados al *Androidmanifest.xml* del template que se generara.

## Desarrollo en Android Studio

Se podrá desarrollar desde el IDE, importando el proyecto una vez generado el archivo *build.gradle* seleccionando este para la importación.
Con `CTRL` + `F9` podras comprobar si el producto podra ser constuido.
Seleccionando las opciones de Gradle, podras elegir buil para buildear las .apk, por defecto Android Studio utilizara el wrapper de Gradle.

### Debug
Para debuguear Analitycs deberas [setear el telefono en modo debug y tambien deberas configurar tu consola de FireBase](https://firebase.google.com/docs/analytics/debugview?hl=es-419).
