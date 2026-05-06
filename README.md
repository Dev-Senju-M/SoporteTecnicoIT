# Sistema de Soporte Técnico IT

Proyecto del curso de **Estructuras de Datos** — Sistema distribuido en Java para la gestión de atención de tickets de soporte técnico mediante colas, hilos y comunicación por sockets.

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
- [Configuración](#configuración)
- [Compilación](#compilación)
- [Ejecución](#ejecución)
- [Protocolo de Comunicación](#protocolo-de-comunicación)
- [Persistencia y Búsqueda](#persistencia-y-búsqueda)
- [Capturas de Pantalla](#capturas-de-pantalla)
- [Pruebas](#pruebas)

---

## Descripción

El sistema simula la operación de un **Departamento de Soporte Técnico IT** en el que los usuarios reportan incidentes tecnológicos y son atendidos por distintos niveles de soporte según el tipo y la prioridad del incidente.

El sistema está distribuido en **5 nodos** que se comunican por sockets TCP, donde uno funciona como servidor central y los otros cuatro como clientes especializados.

---

## Integrantes

| Nombre | Carné | Rol |
|---|---|---|
| Erick Alexander Palomo Mazariegos | 1290-24-25790 | PC1 — Servidor |
| Bryant Alexander To Castillo | 1290-24-12239 | PC2 — Registro |
| Marcos Daniel Valle Larios | 1290-24-16029 | [Rol] |

**Universidad:** [Nombre de la universidad]
**Curso:** Estructuras de Datos
**Catedrático:** [Nombre del catedrático]
**Sección:** [Sección]
**Año:** 2026

---

## Arquitectura del Sistema

```
                        ┌──────────────────────┐
                        │   PC1 - SERVIDOR     │
                        │   (Help Desk)        │
                        │                      │
                        │  - Cola General      │
                        │  - Cola Prioritaria  │
                        │  - Cola Especial     │
                        │  - Árbol B+          │
                        └──────────┬───────────┘
                                   │  Sockets TCP
            ┌──────────────────────┼──────────────────────┐
            │           │          │          │           │
       ┌────┴────┐ ┌────┴────┐ ┌───┴────┐ ┌───┴────┐ ┌────┴────┐
       │   PC2   │ │   PC3   │ │  PC4   │ │  PC5   │ │   ...   │
       │Registro │ │Soporte  │ │Soporte │ │Soporte │ │         │
       │         │ │  N1     │ │Crítico │ │  N2    │ │         │
       └─────────┘ └─────────┘ └────────┘ └────────┘ └─────────┘
```

---

## Componentes

### PC1 — Servidor Central (Help Desk)
- Mantiene las colas de tickets pendientes.
- Maneja un hilo por cada cliente conectado.
- Visualiza en tiempo real los tickets en cola y los que están siendo atendidos.
- Persiste las atenciones finalizadas en archivo.
- Carga el archivo en un **árbol B+** para búsqueda eficiente por DPI.

### PC2 — Registro de Tickets
- Punto de ingreso de usuarios al sistema.
- Solicita el DPI del usuario.
- Genera automáticamente un número de ticket (`TK-XXXX`).
- Envía la información al servidor para que el ticket sea encolado.

### PC3 — Soporte Nivel 1 (Atención General)
- Atiende tickets de la **cola general**.
- Casos típicos: contraseñas, correo, software básico, impresoras, red.
- Mientras atiende, la estación queda **bloqueada** hasta finalizar.

### PC4 — Soporte Crítico (Atención Prioritaria)
- Atiende tickets de la **cola de prioridad**.
- Casos críticos: caídas de servidor, brechas de seguridad, sistemas productivos detenidos.
- Niveles de prioridad: `CRÍTICO (1) > ALTO (2) > MEDIO (3) > BAJO (4)`.

### PC5 — Soporte Nivel 2 (Atención Especial)
- Atiende casos especializados: instalación de hardware nuevo, licenciamiento, migraciones, consultoría.
- Cola dedicada gestionada por el servidor.

---

## Estructuras de Datos Utilizadas

| Estructura | Implementación | Uso |
|---|---|---|
| Cola FIFO | `LinkedList` / implementación propia | Cola general (PC3) |
| Cola de Prioridad | `PriorityQueue` | Cola crítica (PC4) |
| Cola FIFO | `LinkedList` / implementación propia | Cola especial (PC5) |
| Árbol B+ | Implementación propia | Búsqueda histórica por DPI |
| Tabla Hash | `HashMap` | Índice rápido de tickets activos |

---

## Tecnologías

- **Lenguaje:** Java 17
- **UI:** Swing
- **Build Tool:** Maven
- **Comunicación:** Sockets TCP
- **Concurrencia:** Hilos, `synchronized`, `ReentrantLock`
- **Persistencia:** Archivos planos / serialización
- **Control de versiones:** Git + GitHub

---

## Estructura del Proyecto

```
SoporteIT/
├── common/                       # Clases compartidas entre todos los nodos
│   ├── src/main/java/
│   │   ├── modelo/
│   │   │   ├── Ticket.java
│   │   │   ├── Atencion.java
│   │   │   ├── Prioridad.java
│   │   │   └── EstadoTicket.java
│   │   ├── protocolo/
│   │   │   ├── Mensaje.java
│   │   │   └── TipoMensaje.java
│   │   └── config/
│   │       └── Configuracion.java
│   └── pom.xml
│
├── servidor/                     # PC1
│   ├── src/main/java/
│   └── pom.xml
│
├── registro/                     # PC2
│   ├── src/main/java/
│   └── pom.xml
│
├── soporteN1/                    # PC3
├── soporteCritico/               # PC4
├── soporteN2/                    # PC5
│
├── config/
│   ├── servidor.properties       # Puerto, ruta de archivos
│   └── cliente.properties        # IP del servidor, puerto
│
├── data/
│   └── atenciones.dat            # Archivo persistente de atenciones
│
├── docs/
│   ├── capturas/                 # Pantallazos de las interfaces
│   └── pruebas/                  # Archivos de prueba generados
│
├── pom.xml                       # POM padre
└── README.md
```

---

## Requisitos Previos

- **JDK 17** o superior
- **Maven 3.8+**
- **Git**
- 5 PCs en la misma red (o una sola PC para pruebas locales con `localhost`)

---

## Configuración

Cada cliente lee su archivo de configuración desde `config/cliente.properties`:

```properties
# IP del servidor central
servidor.host=192.168.1.100
servidor.puerto=5000
```

El servidor usa `config/servidor.properties`:

```properties
servidor.puerto=5000
archivo.atenciones=data/atenciones.dat
```

> Modifica la IP según la red local donde se ejecute el sistema. Para pruebas locales usar `127.0.0.1`.

---

## Compilación

Desde la raíz del proyecto:

```bash
mvn clean package
```

Esto generará un `.jar` ejecutable por cada componente en su respectiva carpeta `target/`.

---

## Ejecución

> **IMPORTANTE:** El servidor (PC1) debe iniciarse **antes** que cualquier cliente.

### 1. Iniciar el servidor (PC1)
```bash
java -jar servidor/target/ServidorHelpDesk.jar
```

### 2. Iniciar la interfaz de registro (PC2)
```bash
java -jar registro/target/RegistroTickets.jar
```

### 3. Iniciar las estaciones de atención
```bash
java -jar soporteN1/target/SoporteN1.jar
java -jar soporteCritico/target/SoporteCritico.jar
java -jar soporteN2/target/SoporteN2.jar
```

---

## Protocolo de Comunicación

Los mensajes entre cliente y servidor se envían como objetos `Mensaje` serializados sobre sockets TCP.

| Tipo de Mensaje | Origen | Descripción |
|---|---|---|
| `REGISTRAR_TICKET` | PC2 → PC1 | Registra un nuevo usuario en cola |
| `SOLICITAR_TICKET` | PC3/PC4/PC5 → PC1 | Pide el siguiente ticket de la cola correspondiente |
| `ENTREGAR_TICKET` | PC1 → PC3/PC4/PC5 | Servidor entrega el siguiente ticket |
| `FINALIZAR_ATENCION` | PC3/PC4/PC5 → PC1 | Reporta el cierre de una atención |
| `ACTUALIZAR_VISTA` | PC1 → Todos | Difunde el estado actual de las colas |
| `BUSCAR_DPI` | PC1 (interno) | Búsqueda en árbol B+ |

---

## Persistencia y Búsqueda

- Cada atención finalizada se serializa y se guarda en `data/atenciones.dat`.
- Al solicitar una búsqueda, el archivo se carga en un **árbol B+** en memoria.
- La búsqueda por DPI es de complejidad **O(log n)**.
- La eficiencia se documenta en `docs/eficiencia.md`.

---

## Capturas de Pantalla

Las capturas de cada interfaz están en `docs/capturas/`.

| Interfaz | Captura |
|---|---|
| Servidor (PC1) | `docs/capturas/pc1_servidor.png` |
| Registro (PC2) | `docs/capturas/pc2_registro.png` |
| Soporte N1 (PC3) | `docs/capturas/pc3_soporten1.png` |
| Soporte Crítico (PC4) | `docs/capturas/pc4_soportecritico.png` |
| Soporte N2 (PC5) | `docs/capturas/pc5_soporten2.png` |

---

## Pruebas

Los archivos de prueba se encuentran en `docs/pruebas/`:
- `atenciones_prueba.dat` — archivo de atenciones generado durante las pruebas.
- `busquedas.txt` — resultados de búsquedas por DPI.

---

## Repositorio

[Enlace al repositorio en GitHub]

---

## Licencia

Proyecto académico — uso educativo.
