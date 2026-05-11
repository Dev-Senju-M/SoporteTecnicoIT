# Sistema de Soporte Técnico IT

Proyecto del curso de **Programación III** — Sistema distribuido en Java para la gestión de tickets de soporte técnico mediante colas, hilos y comunicación por sockets TCP.

---

## Tabla de Contenidos

- [Descripción](#descripción)
- [Integrantes](#integrantes)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Componentes](#componentes)
- [Estructuras de Datos Utilizadas](#estructuras-de-datos-utilizadas)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos Previos](#requisitos-previos)
- [Ejecución](#ejecución)
- [Protocolo de Comunicación](#protocolo-de-comunicación)
- [Persistencia y Búsqueda](#persistencia-y-búsqueda)

---

## Descripción

El sistema simula la operación de un **Departamento de Soporte Técnico IT** en el que los usuarios reportan incidentes tecnológicos y son atendidos según el tipo del ticket: general, prioritario o especial.

El sistema está distribuido en **5 nodos** que se comunican por sockets TCP. PC1 actúa como servidor central y PC2–PC5 como clientes especializados, cada uno con su interfaz JavaFX independiente.

---

## Integrantes

| Nombre | Carné | Rol |
|---|---|---|
| Erick Alexander Palomo Mazariegos | 1290-24-25790 | PC1 — Servidor Central |
| Bryant Alexander To Castillo | 1290-24-12239 | PC2 — Registro e Historial |
| Marcos Daniel Valle Larios | 1290-24-16029 | PC3 / PC4 / PC5 — Atención |

**Universidad:** Universidad Mariano Galvez  
**Curso:** Programación III  
**Catedrático:** Elmer  
**Sección:** A  
**Año:** 2026

---

## Arquitectura del Sistema

```
                     ┌──────────────────────────┐
                     │      PC1 - SERVIDOR      │
                     │                          │
                     │  Cola General            │
                     │  Cola Prioritaria        │
                     │  Cola Especial           │
                     │  Árbol B+ (historial)    │
                     └────────────┬─────────────┘
                                  │  Sockets TCP
          ┌───────────────────────┼───────────────────────┐
          │               │               │               │
     ┌────┴────┐     ┌────┴────┐    ┌────┴────┐    ┌────┴────┐
     │   PC2   │     │   PC3   │    │   PC4   │    │   PC5   │
     │Registro │     │Soporte  │    │Soporte  │    │Soporte  │
     │Historial│     │General  │    │Priorit. │    │Especial │
     └─────────┘     └─────────┘    └─────────┘    └─────────┘
```

---

## Componentes

### PC1 — Servidor Central
- Gestiona tres colas independientes: **General**, **Prioritaria** y **Especial**.
- Atiende un hilo por cliente conectado simultáneamente.
- Persiste cada atención finalizada en un archivo de datos.
- Carga el historial en un **Árbol B+** para búsqueda eficiente por DPI.
- Muestra en tiempo real el estado de las colas.

### PC2 — Registro e Historial
- Punto de ingreso de nuevos tickets al sistema.
- Solicita DPI, nombre, tipo de ticket y motivo.
- Genera automáticamente el número de ticket (`TK-XXXX`).
- Permite consultar el historial de tickets de un usuario por DPI, mostrando: ticket, DPI, nombre, motivo, tipo y tiempo total de atención.

### PC3 — Soporte General
- Atiende tickets de la **cola General** (`tipoCola = "GENERAL"`).
- Casos típicos: hardware, software, conectividad.
- El técnico responde un cuestionario de diagnóstico y simula la resolución antes de finalizar.

### PC4 — Soporte Prioritario
- Atiende tickets de la **cola Prioritaria** (`tipoCola = "PRIORITARIO"`).
- Casos urgentes que requieren atención inmediata.
- Flujo idéntico a PC3 pero consumiendo exclusivamente tickets de prioridad.

### PC5 — Soporte Especial
- Atiende tickets de la **cola Especial** (`tipoCola = "ESPECIAL"`).
- Casos especializados: instalaciones, licenciamiento, migraciones.
- Flujo idéntico a PC3/PC4 pero para la cola especial.

---

## Estructuras de Datos Utilizadas

| Estructura | Uso en el sistema |
|---|---|
| Cola FIFO | Cola General y Cola Especial (PC3, PC5) |
| Cola de Prioridad | Cola Prioritaria (PC4) |
| Árbol B+ | Historial de atenciones — búsqueda por DPI en PC2 |
| HashMap | Índice rápido de tickets activos en el servidor |

---

## Tecnologías

- **Lenguaje:** Java 21
- **UI:** JavaFX 21
- **Build:** Maven
- **Comunicación:** Sockets TCP (protocolo de objetos serializados)
- **Concurrencia:** `Thread`, `synchronized`
- **Persistencia:** Serialización de objetos en archivo `.dat`
- **Control de versiones:** Git + GitHub

---

## Estructura del Proyecto

```
SistemadeSoporteTecnicoIT/
├── src/
│   ├── main/
│   │   ├── java/sistema/sistemadesoportetecnicoit/
│   │   │   ├── PC1Application.java / PC1Launcher.java
│   │   │   ├── PC2Application.java / PC2Launcher.java
│   │   │   ├── PC3Application.java / PC3Launcher.java
│   │   │   ├── PC4Application.java / PC4Launcher.java
│   │   │   ├── PC5Application.java / PC5Launcher.java
│   │   │   ├── pc1/          # Servidor: controladores y lógica de colas
│   │   │   ├── pc2/          # Registro e historial: controladores
│   │   │   ├── pc3/          # Soporte General: controladores
│   │   │   ├── pc4/          # Soporte Prioritario: controladores
│   │   │   ├── pc5/          # Soporte Especial: controladores
│   │   │   └── shared/
│   │   │       ├── conexion/  # Clase base Conexion (sockets)
│   │   │       ├── models/    # Ticket.java
│   │   │       ├── protocolo/ # Mensaje.java, TipoMensaje.java
│   │   │       └── utils/     # BancoPreguntas.java
│   │   └── resources/sistema/sistemadesoportetecnicoit/
│   │       ├── pc2/           # pc2_menu.fxml, pc2_registro.fxml, pc2_historial.fxml
│   │       ├── pc3/           # pc3_login.fxml, pc3_estacion.fxml, pc3_atencion.fxml
│   │       ├── pc4/           # pc4_login.fxml, pc4_estacion.fxml, pc4_atencion.fxml
│   │       └── pc5/           # pc5_login.fxml, pc5_estacion.fxml, pc5_atencion.fxml
└── pom.xml
```

---

## Requisitos Previos

- **JDK 21** o superior
- **Maven 3.8+**
- **IntelliJ IDEA** (recomendado) o cualquier IDE con soporte JavaFX
- Las 5 PCs deben estar en la **misma red local** (o usar `localhost` para pruebas)

---

## Ejecución

> **IMPORTANTE:** PC1 (servidor) debe iniciarse **antes** que cualquier cliente.

Cada nodo se ejecuta desde su clase `Launcher` correspondiente en el IDE, o mediante Maven sobreescribiendo la clase principal:

```bash
# PC1 - Servidor
mvn javafx:run -Djavafx.mainClass=sistema.sistemadesoportetecnicoit.PC1Launcher

# PC2 - Registro e Historial
mvn javafx:run -Djavafx.mainClass=sistema.sistemadesoportetecnicoit.PC2Launcher

# PC3 - Soporte General
mvn javafx:run -Djavafx.mainClass=sistema.sistemadesoportetecnicoit.PC3Launcher

# PC4 - Soporte Prioritario
mvn javafx:run -Djavafx.mainClass=sistema.sistemadesoportetecnicoit.PC4Launcher

# PC5 - Soporte Especial
mvn javafx:run -Djavafx.mainClass=sistema.sistemadesoportetecnicoit.PC5Launcher
```

Para pruebas locales, la IP del servidor está configurada en `shared/conexion/Conexion.java`.

---

## Protocolo de Comunicación

Los mensajes se transmiten como objetos `Mensaje` serializados sobre TCP. Cada mensaje tiene un `TipoMensaje`, un payload y el identificador del origen.

| Tipo | Dirección | Descripción |
|---|---|---|
| `REGISTRAR_TICKET` | PC2 → PC1 | Envía un ticket nuevo para ser encolado |
| `SOLICITAR_TICKET` | PC3/PC4/PC5 → PC1 | Pide el siguiente ticket de su cola (`GENERAL`, `PRIORITARIO` o `ESPECIAL`) |
| `ENTREGAR_TICKET` | PC1 → PC3/PC4/PC5 | Respuesta con el objeto `Ticket` (o `null` si la cola está vacía) |
| `FINALIZAR_ATENCION` | PC3/PC4/PC5 → PC1 | Ticket completado con respuestas y tiempos, listo para persistir |
| `BUSCAR_DPI` | PC2 → PC1 | Solicita el historial de tickets de un DPI |
| `RESULTADO_BUSQUEDA` | PC1 → PC2 | Lista de tickets encontrados en el Árbol B+ |

---

## Persistencia y Búsqueda

- Cada atención finalizada se serializa y se guarda en archivo por el servidor.
- Al arrancar, el servidor carga el archivo en un **Árbol B+** indexado por DPI.
- La búsqueda desde PC2 (historial) tiene complejidad **O(log n)**.
- Los campos mostrados en el historial son: Ticket ID, DPI, Nombre, Motivo, Tipo y Tiempo total de atención.

---

## Repositorio

https://github.com/Dev-Senju-M/SoporteTecnicoIT
