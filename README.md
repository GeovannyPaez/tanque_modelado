# Simulador de Control de Nivel de Tanque

Aplicacion de escritorio en Java Swing para simular el control de nivel de un tanque de agua. El sistema permite operar en modo automatico y manual, visualizar el tanque en tiempo real, configurar el set point, ingresar la altura maxima del tanque en metros, graficar la respuesta Nivel vs Tiempo y activar estados de seguridad ante riesgo de desbordamiento.

## Caracteristicas

- Simulacion del nivel del tanque en porcentaje.
- Calculo del nivel actual en metros a partir de la altura maxima configurada.
- Modo automatico con control por set point.
- Modo manual para abrir o cerrar la valvula de entrada.
- Activacion y desactivacion del consumo.
- Grafica de respuesta Nivel vs Tiempo.
- Log de eventos de operacion y seguridad.
- Estados de seguridad: `NORMAL`, `ADVERTENCIA` y `EMERGENCIA`.
- Barra visual de seguridad:
  - Verde: operacion normal.
  - Amarillo/naranja: advertencia por nivel alto.
  - Rojo: emergencia por riesgo de desbordamiento.
- Simulacion de falla automatica: valvula de control atascada abierta.
- Valvula de seguridad independiente `ESD`, accionada por el sistema SIS/ESD.
- Lazo de control visual con `LT`, `LC` y `SP`.
- Lazo de seguridad visual con `LSHH 100 %`, `SIS/ESD` y valvula ESD independiente.

## Requisitos

- JDK 17 o superior.
- Maven 3.8+ opcional, si se desea compilar con Maven.

Verificar Java:

```powershell
java -version
javac -version
```

## Estructura del proyecto

```text
src/main/java/org/example
|-- Main.java
|-- controller
|   `-- SimulacionController.java
|-- model
|   |-- AperturaValvula.java
|   |-- EventoModo.java
|   |-- EventoNivel.java
|   |-- EventoSeguridad.java
|   |-- ModoAutomata.java
|   |-- ModoEstado.java
|   |-- NivelAutomata.java
|   |-- NivelEstado.java
|   |-- SeguridadAutomata.java
|   `-- SeguridadEstado.java
`-- view
    |-- EstadoUI.java
    |-- MainFrame.java
    |-- ResponseChartPanel.java
    `-- TankPanel.java
```

## Arquitectura

El proyecto sigue una organizacion tipo MVC:

- `model`: contiene estados, eventos y automatas del sistema.
- `view`: contiene la interfaz grafica, tanque visual, grafica, indicadores y log.
- `controller`: coordina eventos de usuario, temporizador, logica de simulacion y actualizacion de la vista.

## Como ejecutar

### Opcion 1: usando Maven

Compilar:

```powershell
mvn clean compile
```

Ejecutar:

```powershell
mvn exec:java -Dexec.mainClass="org.example.Main"
```

Nota: el `pom.xml` actual no incluye el plugin `exec-maven-plugin`, pero Maven puede descargarlo automaticamente al usar el objetivo `exec:java`.

### Opcion 2: usando javac y java

En Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force -Path target\classes | Out-Null
javac -d target\classes (Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName })
java -cp target\classes org.example.Main
```

En Linux/macOS:

```bash
mkdir -p target/classes
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes org.example.Main
```

## Como generar el ejecutable para entregar

El proyecto esta configurado para generar un JAR ejecutable con la clase principal `org.example.Main`.

Si tienes Maven instalado:

```powershell
mvn clean package
```

El archivo para enviar queda en:

```text
target/modelado-1.0-SNAPSHOT.jar
```

Si no tienes Maven instalado, pero si tienes JDK 17:

```powershell
New-Item -ItemType Directory -Force -Path target\classes | Out-Null
javac -encoding UTF-8 -d target\classes (Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName })
jar --create --file target\modelado-ejecutable.jar --main-class org.example.Main -C target\classes .
```

El archivo para enviar queda en:

```text
target/modelado-ejecutable.jar
```

La profesora lo puede ejecutar con:

```powershell
java -jar modelado-ejecutable.jar
```

Nota: la profesora necesita tener instalado Java 17 o superior.

### Generar aplicacion Windows con .exe

Si tienes JDK 17 o superior, puedes generar una aplicacion para Windows con `jpackage`:

```powershell
Remove-Item -LiteralPath target\package-input -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path target\package-input | Out-Null
Copy-Item -LiteralPath target\modelado-ejecutable.jar -Destination target\package-input\modelado-ejecutable.jar
jpackage --type app-image --name ModeladoTanque --input target\package-input --main-jar modelado-ejecutable.jar --main-class org.example.Main --dest target\dist --java-options "-Dfile.encoding=UTF-8"
Compress-Archive -Path target\dist\ModeladoTanque -DestinationPath target\ModeladoTanque-Windows.zip -Force
```

El archivo recomendado para entregar es:

```text
target/ModeladoTanque-Windows.zip
```

La profesora debe descomprimir el `.zip` y abrir:

```text
ModeladoTanque/ModeladoTanque.exe
```

Esta version incluye un runtime de Java, asi que no depende de que Java este instalado en el computador de la profesora.

## Uso basico

1. Ejecutar la aplicacion.
2. Seleccionar modo `AUTOMATICO` o `MANUAL`.
3. En modo automatico, ajustar el `Set Point (%)`.
4. Configurar la `Altura tanque (m)` para calcular el nivel en metros.
5. Usar `Iniciar`, `Pausar` o `Reiniciar` para controlar la simulacion.
6. Activar o desactivar el consumo con el boton correspondiente.
7. Usar `Simular falla automatica` para demostrar una valvula de control atascada abierta.
8. Observar la barra de seguridad, el log y la grafica Nivel vs Tiempo.

## Estados de seguridad

- `NORMAL`: operacion segura.
- `ADVERTENCIA`: nivel alto, se muestra una alerta visual.
- `EMERGENCIA`: riesgo de desbordamiento; la valvula de seguridad ESD se cierra, se detiene el llenado y se muestra un mensaje de emergencia.

## Notas

- La simulacion corre en memoria y no usa base de datos.
- El log de eventos se muestra en la interfaz durante la ejecucion.
- El sistema esta pensado como apoyo academico para modelar un sistema de control de nivel y su capa de seguridad.
