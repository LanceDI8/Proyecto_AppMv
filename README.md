# Proyecto_AppMv — Traductor offline (Jetpack Compose + ML Kit)

App de traducción **offline** hecha con **Jetpack Compose**. Permite:
- Traducir texto entre varios idiomas (p. ej. **ES ↔ EN**) usando **ML Kit On-Device Translation**.  
- Elegir el idioma de la **interfaz** (sistema / español / inglés) y el **tema** (claro/oscuro/sistema).
- Guardar **historial** y marcar **favoritos** en una base de datos **Room**.
- Descargar/gestionar **modelos offline** por idioma (con opción “solo Wi-Fi”).

> ℹ️ La traducción es local; solo se usa Internet para **descargar** los modelos de idioma cuando haga falta.

---

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

## ¿Cómo funciona? (flujo técnico)
1. **Preferencias (DataStore)** guardan: tema, idioma de la app y “solo Wi-Fi”.
2. Al arrancar, `MainActivity` aplica el idioma elegido con `AppCompatDelegate.setApplicationLocales(...)`.
3. En **TranslateScreen**:
   - Se calcula un par por defecto (ES↔EN) según el idioma de la app; el usuario puede invertir con **Swap**.
   - Al traducir:
     - `ensureModel(src, tgt)` descarga el modelo si falta (respeta “solo Wi-Fi”).
     - `translate(text)` obtiene la traducción local.
     - `savePhrase(...)` persiste la frase (historial/favoritos) en **Room**.
4. **SavedScreen** muestra **Historial/Favoritos** suscribiéndose a `Flow<List<Phrase>>`.
5. **SettingsScreen** gestiona tema/idioma de interfaz, “solo Wi-Fi” y **descarga/eliminación** de modelos offline.

##  Build APK
- **Debug**: `Build → Build APK(s)` → `app/build/outputs/apk/debug/app-debug.apk`
- **Release firmado**:
    1. `Build → Generate Signed Bundle / APK → APK`
    2. Crear/usar keystore (`.jks`), alias y contraseñas
    3. Elegir **release**, tildar **V1/V2**.
    4. Resultado: `app/build/outputs/apk/release/app-release.apk`



##  Licencia
MIT.
