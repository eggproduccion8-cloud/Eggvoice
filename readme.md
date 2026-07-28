# EGG VOICE 🥚📣

Bienvenido al sistema de comunicación por voz oficial y personalizado para **Egg Producción WDP** en Minecraft Forge 1.20.1.

Este mod está basado en *Simple Voice Chat*, rediseñado por completo y ampliado con sistemas de reproducción de audio personalizados por proximidad para eventos de rol y supervivencia extrema.

---

## 🚀 Características Principales

1. **Diseño de Interfaz Premium**:
   * Pantalla completa moderna y traslúcida (efecto de vidrio templado/borroso).
   * Encabezado integrado: `VOICE WDP x EGG PRODUCTIONS ®`.
   * Panel lateral de navegación con las secciones principales (`CONTROLES`, `GRUPOS`, `AJUSTES`, `VOLÚMENES`).
   * Botones amplios, limpios y dinámicos completamente en español.

2. **Apartado de Administración Egg (Exclusivo para OPs)**:
   * Los operadores del servidor verán un botón exclusivo llamado `Panel Admin Egg` en el menú principal (`V`).
   * Este panel permite seleccionar uno de los **40 audios** del Resource Pack (`song1` a `song40`) mediante una cómoda lista con scroll.
   * Cuenta con un listado en vivo de todos los jugadores conectados con indicadores de flechas (`->`).
   * Permite reproducir el sonido seleccionado **únicamente para los jugadores marcados** de forma instantánea.

3. **Control de Sonidos de Egg Producción**:
   * Todos los jugadores tienen un nuevo deslizador en la sección de **Ajustes** del mod llamado:
     🔊 **`Sonidos Egg Producción`**
   * Este deslizador permite a cada jugador ajustar o silenciar independientemente la música y los efectos reproducidos desde el panel de administración, sin afectar el volumen del chat de voz principal.

---

## ⌨️ Controles por Defecto (Configurables)

* 🗣️ **Pulsar para hablar (Push-to-Talk)**: Tecla `Bloq Mayús` (Caps Lock).
* ⚙️ **Abrir la GUI de Egg Voice**: Tecla `V`.
* 👥 **Menú de Grupos de Voz**: Tecla `G`.

---

## 🛠️ Comandos de Administración y Permisos (`eggvoice`)

El comando principal ha sido renombrado a `/eggvoice`. Los permisos asociados ahora utilizan el prefijo `eggvoice.*` en lugar de `voicechat.*`.

### Comandos de Consola y Chat:
* `/eggvoice help`: Muestra el menú de ayuda.
* `/eggvoice play <sonido> <jugadores>`: *(Exclusivo OP / Permiso: `eggvoice.admin`)* Reproduce un sonido del Resource Pack de Egg Producción únicamente a los jugadores especificados.
* `/eggvoice invite <jugador>`: Invita a un jugador a tu grupo de voz privado.
* `/eggvoice join <grupo>`: Te une a un grupo de voz.
* `/eggvoice leave`: Te saca del grupo de voz actual.

### Nodos de Permisos (para administradores de rangos como LuckPerms):
* `eggvoice.listen`: Permiso para escuchar a otros (otorgado por defecto a todos).
* `eggvoice.speak`: Permiso para hablar por micrófono (otorgado por defecto a todos).
* `eggvoice.groups`: Permiso para crear o unirse a salas de chat privadas globales (otorgado por defecto a todos).
* `eggvoice.admin`: Permiso para acceder al panel de administración y reproducir los sonidos de Egg Producción (otorgado a OPs por defecto).

---

## 📦 El Resource Pack de Audios (EggVoiceSoundPack)

En el repositorio tienes la carpeta del Resource Pack oficial:
📁 `resourcepacks/EggVoiceSoundPack`

### Cómo añadir tus canciones y audios:
1. Convierte tus audios a formato **`.ogg`**.
2. Nómbralos exactamente como `song1.ogg`, `song2.ogg`, ..., hasta `song40.ogg`.
3. Pégalos en la ruta:
   `resourcepacks/EggVoiceSoundPack/assets/eggvoice/sounds/` (reemplazando los archivos de marcador que ya creé por ti).
4. Sube este Resource Pack a tu servidor de Minecraft o compártelo con tus jugadores para que lo activen en sus carpetas `resourcepacks/`.

---

## 🛠️ Guía de Compilación en Termux (Android)

Si deseas volver a compilar el mod en el futuro, los comandos están listos y limpios:

1. **Establece Java 17 en tu Termux:**
   ```bash
   export JAVA_HOME=$PREFIX/lib/jvm/java-17-openjdk
   ```
2. **Compila la versión de Forge 1.20.1:**
   ```bash
   bash gradlew :forge:shadowJar
   ```
3. **Mueve el archivo compilado a tus Descargas de Android:**
   ```bash
   cp forge/build/reobfShadowJar/output.jar /sdcard/Download/EGG_VOICE_FORGE_1.20.1.jar
   ```

---

*Desarrollado y optimizado con ❤️ para el universo de series y eventos de **Egg Producción WDP**.*
