##  Requisitos
- Android Studio **Giraffe/Koala/↑**
- JDK 17+
- Min SDK (ajusta): **24** | Target/Compile SDK (ajusta): **34/35**

##  Ejecución
1. Abrir en Android Studio y **Sync Gradle**.
2. Ejecutar en emulador o dispositivo.
3. En **Settings**:
    - Elegir tema.
    - Elegir **App language** (Follow system / Spanish / English).
    - Opcional: “Download models on Wi-Fi only”.
    - **Offline languages**: descargar los modelos que usarás (ej. Español).

##  Uso
- Pestaña **Translate**: escribir texto, pulsar **Traducir**.
- Botón **Swap** invierte `origen ↔ destino`.
- **Copiar/Compartir** el resultado.
- **Favorito** guarda la frase.
- Pestaña **Saved**: **Historial / Favoritos**.

##  Build APK
- **Debug**: `Build → Build APK(s)` → `app/build/outputs/apk/debug/app-debug.apk`
- **Release firmado**:
    1. `Build → Generate Signed Bundle / APK → APK`
    2. Crear/usar keystore (`.jks`), alias y contraseñas
    3. Elegir **release**, tildar **V1/V2**.
    4. Resultado: `app/build/outputs/apk/release/app-release.apk`

> Nota: no subas tu `.jks` ni contraseñas al repo (están ignorados en `.gitignore`).

##  Licencia
MIT (o la que prefieras).