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
- Simulacion de falla automatica: valvula atascada abierta.
- Lazo de control visual con `LT`, `LC` y `SP`.
- Lazo de seguridad visual con `LSHH 100 %` y `SIS/ESD`.

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

## Uso basico

1. Ejecutar la aplicacion.
2. Seleccionar modo `AUTOMATICO` o `MANUAL`.
3. En modo automatico, ajustar el `Set Point (%)`.
4. Configurar la `Altura tanque (m)` para calcular el nivel en metros.
5. Usar `Iniciar`, `Pausar` o `Reiniciar` para controlar la simulacion.
6. Activar o desactivar el consumo con el boton correspondiente.
7. Usar `Simular falla automatica` para demostrar una valvula atascada abierta.
8. Observar la barra de seguridad, el log y la grafica Nivel vs Tiempo.

## Estados de seguridad

- `NORMAL`: operacion segura.
- `ADVERTENCIA`: nivel alto, se muestra una alerta visual.
- `EMERGENCIA`: riesgo de desbordamiento; la valvula de entrada se cierra, se detiene el llenado y se muestra un mensaje de emergencia.

## Notas

- La simulacion corre en memoria y no usa base de datos.
- El log de eventos se muestra en la interfaz durante la ejecucion.
- El sistema esta pensado como apoyo academico para modelar un sistema de control de nivel y su capa de seguridad.
