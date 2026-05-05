# 💻 Sistema de Soporte Técnico IT — Mesa de Ayuda

**Proyecto:** Estructuras de Datos — Sistema de Colas  
**Modalidad:** Grupal (máximo 4 integrantes)  
**GitHub:** [Dev-Senju-M](https://github.com/Dev-Senju-M)

---

## 📋 Descripción

Sistema distribuido en **Java 21** con interfaz gráfica **JavaFX** que simula una **Mesa de Ayuda de Tecnología (Help Desk)**.  
Gestiona tickets de soporte técnico mediante estructuras de datos, concurrencia con hilos y comunicación entre procesos por **Sockets TCP**.

El proyecto está estructurado como un **proyecto Maven en NetBeans**, donde cada módulo cuenta con su propia interfaz gráfica JavaFX y corre de forma independiente como un `.jar` ejecutable, simulando distintas estaciones de trabajo conectadas en red.

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────┐
│               PC1 — Servidor Central                │
│   Cola general · Cola prioritaria · Árbol B+        │
│   Persistencia · Historial · Puerto 5000            │
└────────┬──────────┬──────────┬──────────┬───────────┘
         │          │          │          │
    ┌────┴───┐ ┌────┴───┐ ┌───┴────┐ ┌───┴────────┐
    │  PC2   │ │  PC3   │ │  PC4   │ │    PC5     │
    │Registro│ │General │ │Crítico │ │Especializado│
    └────────┘ └────────┘ └────────┘ └────────────┘
```

| Módulo | Rol                   | Descripción                                                                 |
|--------|-----------------------|-----------------------------------------------------------------------------|
| PC1    | Servidor Central      | Administra colas, distribuye tickets, persiste datos y monitorea el sistema |
| PC2    | Registro de Tickets   | Ventanilla de entrada: el usuario reporta su problema y obtiene un ticket   |
| PC3    | Soporte General       | Agente que atiende tickets de hardware y software común (prioridad normal)  |
| PC4    | Soporte Prioritario   | Agente que atiende incidentes críticos de servidores y accesos              |
| PC5    | Soporte Especializado | Agente que atiende problemas de redes, internet e infraestructura           |

---

## 🖥️ Módulos del Sistema — Flujo y Pantallas JavaFX

### PC1 — Servidor Central

Es el corazón del sistema. Debe ejecutarse **siempre primero** antes que cualquier otro módulo.

**Pantallas:**
- **Panel de Control Principal** — Muestra en tiempo real el estado de las colas (general y prioritaria), cantidad de tickets en espera, tickets atendidos en la sesión y agentes conectados.
- **Vista de Colas** — Tabla con los tickets actualmente en cola: número, tipo, prioridad, tiempo de espera y módulo destino.
- **Historial de Tickets** — Lista de todos los tickets atendidos, con opción de búsqueda por DPI usando el Árbol B+.
- **Log del Sistema** — Área de texto con eventos del servidor: conexiones, desconexiones, tickets enrutados y errores.

**Flujo:**
1. El servidor arranca y abre el socket en el puerto 5000.
2. Acepta conexiones entrantes de PC2–PC5 en hilos separados.
3. Recibe tickets de PC2, los clasifica por tipo y prioridad, y los encola en `ColaTickets` o `ColaPrioridad`.
4. Cuando un agente (PC3/PC4/PC5) solicita el siguiente ticket, el servidor lo desencola y se lo envía.
5. Al finalizar la atención, recibe el ticket completado y lo persiste en `tickets_atendidos.txt` e indexa en el Árbol B+.

---

### PC2 — Registro de Tickets

Es la **ventanilla de atención al usuario**. El operador ingresa los datos del usuario y el tipo de problema.

**Pantallas:**
- **Formulario de Registro** — Campos: DPI, nombre, apellido, tipo de problema (combo: Hardware / Software / Red / Servidor / Accesos / Otro), descripción del problema y nivel de urgencia percibido.
- **Confirmación de Ticket** — Al enviar, muestra el número de ticket generado, la prioridad asignada automáticamente y el tiempo estimado de espera.
- **Historial del Usuario** — Permite buscar por DPI y ver todos los tickets previos del usuario (consulta al servidor).

**Flujo:**
1. El operador llena el formulario con los datos del usuario.
2. Al presionar **"Registrar Ticket"**, PC2 envía los datos al servidor (PC1) vía socket.
3. PC1 asigna número de ticket, determina prioridad y módulo destino, y confirma.
4. PC2 muestra la confirmación con número de ticket y tiempo estimado de atención.
5. El usuario es informado y espera ser llamado por el módulo correspondiente.

---

### PC3 — Soporte General

Atiende tickets de **hardware y software** con prioridad normal (la mayoría de los casos).

**Pantallas:**
- **Panel del Agente** — Muestra el nombre del agente, tickets atendidos en la sesión y botón **"Siguiente Ticket"**.
- **Atención Activa** — Al aceptar un ticket, muestra los datos del usuario, tipo de problema y descripción. Incluye un temporizador de atención en curso.
- **Resolución del Ticket** — El agente selecciona la solución aplicada, agrega notas y marca el ticket como resuelto o escala a otro módulo.
- **Historial de Sesión** — Pila (`PilaHistorial`) con los últimos tickets atendidos en la sesión actual, navegable con deshacer.

**Flujo:**
1. El agente presiona **"Siguiente Ticket"**; PC3 solicita al servidor el próximo ticket de tipo Hardware/Software.
2. PC1 desencola el ticket de `ColaTickets` y lo envía a PC3.
3. PC3 muestra los datos al agente y arranca el temporizador.
4. El agente atiende al usuario, registra la solución y presiona **"Cerrar Ticket"**.
5. PC3 envía el ticket completado al servidor con duración y notas.

---

### PC4 — Soporte Prioritario

Atiende tickets **críticos** de servidores caídos y problemas de accesos/permisos. Opera sobre la `ColaPrioridad`.

**Pantallas:**
- **Panel del Agente Prioritario** — Similar a PC3 pero con indicadores visuales de alerta (colores rojos/naranjas) para tickets críticos. Muestra nivel de urgencia del ticket activo.
- **Atención Crítica** — Vista ampliada con datos del ticket, nivel de criticidad, tiempo máximo de resolución y contador regresivo.
- **Escalamiento** — Si el problema supera las capacidades del agente, permite escalar a un supervisor con un mensaje adjunto.
- **Historial de Sesión** — Igual que PC3, usando `PilaHistorial`.

**Flujo:**
1. El agente presiona **"Siguiente Ticket Crítico"**; PC4 solicita al servidor el próximo ticket de `ColaPrioridad`.
2. PC1 desencola el ticket de mayor prioridad y lo envía a PC4.
3. PC4 muestra alerta visual si el tiempo de espera ya superó el estimado.
4. El agente atiende, registra resolución o escala, y cierra el ticket.
5. PC4 reporta al servidor con duración, tipo de resolución y notas.

---

### PC5 — Soporte Especializado

Atiende tickets de **redes, internet e infraestructura** con prioridad alta.

**Pantallas:**
- **Panel del Especialista** — Muestra tickets de tipo Red/Internet pendientes, con indicador de conexión al servidor.
- **Diagnóstico de Red** — Vista del ticket activo con campos adicionales específicos: segmento de red afectado, número de usuarios impactados y tipo de falla reportada.
- **Atención Activa** — Temporizador, datos del usuario, descripción técnica del problema y área de notas del especialista.
- **Resolución** — El especialista documenta la causa raíz, la solución aplicada y si requiere seguimiento posterior.

**Flujo:**
1. El agente presiona **"Siguiente Ticket"**; PC5 solicita al servidor tickets de tipo Red/Internet/Infraestructura.
2. PC1 entrega el ticket correspondiente desde `ColaTickets` (filtrado por tipo).
3. PC5 despliega la vista de diagnóstico con los detalles del problema.
4. El especialista atiende, documenta la solución técnica y cierra el ticket.
5. PC5 envía el reporte al servidor incluyendo causa raíz y tiempo de resolución.

---

## 📂 Estructura de Carpetas

```
SoporteTecnicoIT/
├── pom.xml                          # Configuración Maven del proyecto
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── server/              # PC1 - Servidor central
│   │   │   ├── client/
│   │   │   │   ├── pc2_registro/    # PC2 - Registro de tickets
│   │   │   │   ├── pc3_general/     # PC3 - Soporte general
│   │   │   │   ├── pc4_prioritaria/ # PC4 - Incidentes críticos
│   │   │   │   └── pc5_especializado/ # PC5 - Soporte especializado
│   │   │   └── shared/
│   │   │       ├── models/          # Clases modelo (Ticket, Usuario, etc.)
│   │   │       ├── structures/      # Cola, Pila, BPlusTree, HashMap
│   │   │       └── utils/           # Constantes, serialización, helpers
│   │   └── resources/
│   │       ├── fxml/                # Archivos .fxml de cada ventana
│   │       ├── css/                 # Estilos de la interfaz JavaFX
│   │       └── images/              # Recursos gráficos
│   └── test/                        # Pruebas unitarias
├── config/
│   └── socket.properties            # Configuración de puertos y host
├── docs/                            # Documentación y análisis de eficiencia
├── jars/                            # JARs ejecutables de cada módulo
└── test-files/                      # Archivos generados por el sistema
```

---

## 🔧 Estructuras de Datos

| Clase             | Tipo           | Uso en el sistema                                    |
|-------------------|----------------|------------------------------------------------------|
| `ColaTickets`     | Queue (FIFO)   | Cola general de tickets pendientes                   |
| `ColaPrioridad`   | Priority Queue | Tickets críticos ordenados por nivel de urgencia     |
| `PilaHistorial`   | Stack          | Historial de acciones por sesión de agente           |
| `HashMapUsuarios` | Hash Map       | Historial de tickets por DPI del usuario             |
| `BPlusTree`       | Árbol B+       | Persistencia y búsqueda eficiente de tickets por DPI |

---

## 🎫 Clasificación de Tickets

| Tipo de Problema   | Prioridad | Módulo Destino    | Tiempo Estimado |
|--------------------|-----------|-------------------|-----------------|
| Hardware           | Normal    | PC3 General       | 2 min           |
| Software           | Normal    | PC3 General       | 5 min           |
| Red / Internet     | Alta      | PC5 Especializado | 5 min           |
| Servidor           | Crítica   | PC4 Prioritario   | 10 min          |
| Accesos / Permisos | Alta      | PC4 Prioritario   | 5 min           |
| Otro               | Normal    | PC3 General       | 2 min           |

---

## 🌐 Comunicación por Sockets

| Módulo | Puerto | Rol      |
|--------|--------|----------|
| PC1    | 5000   | Servidor |
| PC2    | —      | Cliente  |
| PC3    | —      | Cliente  |
| PC4    | —      | Cliente  |
| PC5    | —      | Cliente  |

Configuración completa en `config/socket.properties`.

---

## ⚙️ Requerimientos Técnicos

- **Java JDK 21** o superior
- **Apache NetBeans 21** o superior
- **Maven 3.8+** (incluido en NetBeans)
- **JavaFX SDK 21** (gestionado por Maven via dependencia `org.openjfx`)
- No requiere librerías externas adicionales

---

## 🛠️ Configuración del proyecto en NetBeans

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Dev-Senju-M/SoporteTecnicoIT.git
   ```
2. En NetBeans: **File → Open Project** y seleccionar la carpeta `SoporteTecnicoIT`.
3. NetBeans detectará automáticamente el `pom.xml` y descargará las dependencias Maven.
4. Verificar que el JDK configurado sea **Java 21**: **Tools → Java Platforms**.

---

## ▶️ Compilar y Ejecutar

### Compilar (desde NetBeans o terminal)

```bash
mvn clean package
```

Esto genera los `.jar` en la carpeta `jars/`.

### Ejecutar (siempre PC1 primero)

```bash
java -jar jars/PC1_Servidor.jar
java -jar jars/PC2_Registro.jar
java -jar jars/PC3_SoporteGeneral.jar
java -jar jars/PC4_Prioritario.jar
java -jar jars/PC5_Especializado.jar
```

> También se puede ejecutar cada módulo directamente desde NetBeans haciendo clic derecho sobre la clase principal → **Run File**.

---

## 📊 Información Registrada por Atención

Cada ticket atendido guarda:

- Número de ticket
- Fecha y hora de atención
- DPI del usuario
- Nombre y apellido
- Tipo y motivo del problema
- Duración de la atención
- Duración total (espera + atención) en minutos
- Agente que atendió

---

## 🔍 Persistencia y Búsqueda

- El servidor almacena todos los tickets atendidos en `test-files/tickets_atendidos.txt`
- Al cargar, la información se indexa en un **Árbol B+** por DPI
- Permite búsqueda eficiente O(log n) por número de DPI
- Complejidad documentada en `docs/`

---

## 👥 Integrantes

| Nombre                            | Carné         |
|-----------------------------------|---------------|
| Bryant Alexander To Castillo      | 1290-24-12239 |
| Marcos Daniel Valle Larios        | 1290-24-16029 |
| Erick Alexander Palomo Mazariegos | 1290-24-25790 |