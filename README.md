# 🖥️ Sistema de Soporte Técnico IT — Mesa de Ayuda
**Estructuras de Datos | Universidad de Guatemala**
**Modalidad: Grupal (2 integrantes)**

---

## 📋 Descripción del sistema

Sistema distribuido que simula la **mesa de ayuda de tecnología (Help Desk)** de una organización. Los usuarios reportan problemas técnicos desde un punto de registro, y los técnicos los atienden desde estaciones especializadas según la criticidad y tipo de incidente.

---

## 🗺️ Arquitectura del sistema

```
                    ┌─────────────────────────┐
                    │   PC1 — Servidor Central  │
                    │   Cola de tickets IT      │
                    │   (Mesa de ayuda)         │
                    └────────────┬────────────┘
                                 │  TCP Sockets (puerto 9090)
          ┌──────────────────────┼──────────────────────┐
          │                      │                       │
   ┌──────┴──────┐      ┌────────┴───────┐     ┌────────┴───────┐
   │ PC2         │      │ PC3            │     │ PC4            │
   │ Reporte de  │      │ Soporte        │     │ Incidentes     │
   │ problema    │      │ General        │     │ Críticos       │
   └─────────────┘      └────────────────┘     └────────┬───────┘
                                                        │
                                               ┌────────┴───────┐
                                               │ PC5            │
                                               │ Soporte        │
                                               │ Especializado  │
                                               │ (Redes/Servers)│
                                               └────────────────┘
```

**Comunicación:** TCP Sockets serializados | **Puerto por defecto:** 9090

---

## 🖥️ Descripción de cada interfaz

### PC1 — Servidor Central (Mesa de Ayuda)
- Muestra en tiempo real la **cola de tickets** pendientes
- Separa visualmente tickets por tipo: General / Críticos / Especializados
- Permite **buscar historial por ID de empleado** usando la tabla hash
- Visualiza estadísticas: tickets atendidos, tiempo promedio, incidentes por tipo
- Administra la comunicación con todos los clientes vía sockets

### PC2 — Reporte de Problemas (Usuario)
- El usuario ingresa su **número de empleado** (ID)
- Selecciona la **categoría del problema:**
  - 🖨️ Hardware (impresoras, equipos, periféricos)
  - 💾 Software (instalaciones, errores de aplicación)
  - 🌐 Conectividad (red, internet, VPN)
  - 🔐 Accesos (contraseñas, permisos, cuentas)
  - 🖥️ Servidores / Infraestructura
  - ⚠️ Incidente Crítico (sistema caído, pérdida de datos)
- Describe brevemente el problema
- Recibe un **número de ticket** generado automáticamente
- El sistema asigna la **cola correcta** según la categoría

### PC3 — Soporte General
- Atiende tickets de Hardware, Software y Accesos
- Cola **FIFO** (primero en llegar, primero en atenderse)
- Registra: técnico, solución aplicada, tiempo de resolución
- Recurso bloqueado mientras hay un ticket en atención

### PC4 — Incidentes Críticos (Prioridad)
- Atiende tickets marcados como **Incidente Crítico**
- Cola de **prioridad** (mayor urgencia = se atiende primero)
- Niveles de prioridad:
  - P1 — Sistema caído / impacto total
  - P2 — Impacto parcial / múltiples usuarios afectados
  - P3 — Degradación de servicio
- Registra impacto, usuarios afectados y acciones tomadas
- Recurso bloqueado durante la atención

### PC5 — Soporte Especializado (Redes / Servidores)
- Atiende tickets de Conectividad e Infraestructura
- Cola **FIFO** con clasificación por subtipo
- Técnicos especializados en redes, servidores y telecomunicaciones
- Registra configuraciones aplicadas y referencias técnicas

---

## 🗂️ Estructuras de datos

| Estructura | Clase Java | Uso en el sistema |
|---|---|---|
| Cola FIFO | `LinkedList<Ticket>` sincronizada | Tickets generales (PC3) y especializados (PC5) |
| Cola de prioridad | `PriorityBlockingQueue<Ticket>` | Incidentes críticos (PC4) — P1 antes que P2 |
| Tabla Hash | `HashMap<String, List<Ticket>>` | Historial de tickets por ID de empleado — O(1) |
| Árbol B+ | `ArbolBPlus` (implementación propia, t=3) | Búsqueda de tickets por número — O(log_t n) |
| Pila | `Stack<Ticket>` | Últimos tickets resueltos (historial reciente) |

### ¿Por qué Tabla Hash para historial por empleado?
La tabla hash permite acceso en **O(1) promedio** para recuperar todos los tickets de un empleado dado su ID. Es la estructura ideal cuando la clave de búsqueda (ID empleado) es fija y se necesita acceso frecuente al historial individual.

---

## ⚙️ Concurrencia e hilos

| Mecanismo | Dónde se usa | Por qué |
|---|---|---|
| `ExecutorService` pool fijo | Servidor PC1 | Un hilo por cliente conectado, sin crear hilos ilimitados |
| `synchronized` | `GestorTickets` | Protege las colas compartidas de condiciones de carrera |
| `PriorityBlockingQueue` | Cola crítica PC4 | Thread-safe nativo, bloquea si la cola está vacía |
| `AtomicBoolean enAtencion` | PC3, PC4, PC5 | Bloquea el recurso mientras se atiende un ticket |
| `SwingWorker` | Todas las UIs | Comunicación con servidor sin bloquear la interfaz gráfica |

**Regla de bloqueo:** Mientras un técnico está atendiendo un ticket, el botón "Siguiente" queda deshabilitado. No se puede atender más de un ticket simultáneamente en la misma estación.

---

## 🌐 Protocolo de mensajes (Sockets)

Los objetos `Mensaje` se serializan con `ObjectOutputStream` / `ObjectInputStream`.

| Acción del mensaje | Origen → Destino | Descripción |
|---|---|---|
| `REGISTRAR_TICKET` | PC2 → PC1 | Nuevo ticket: ID empleado, categoría, descripción |
| `SIGUIENTE_GENERAL` | PC3 → PC1 | Extrae siguiente ticket de cola general |
| `SIGUIENTE_CRITICO` | PC4 → PC1 | Extrae ticket más prioritario de cola crítica |
| `SIGUIENTE_ESPECIALIZADO` | PC5 → PC1 | Extrae siguiente ticket de cola especializada |
| `RESOLVER_TICKET` | PC3/4/5 → PC1 | Ticket resuelto: guarda en historial y CSV |
| `LISTAR_COLAS` | Cualquiera → PC1 | Estado actual de todas las colas |
| `BUSCAR_EMPLEADO` | PC1 UI → interno | Historial de tickets del empleado por hash |

---

## 🗃️ Clasificación automática de tickets

Al registrar un ticket en PC2, el sistema lo asigna automáticamente:

```
Categoría seleccionada    →  Cola destino         →  Prioridad
─────────────────────────────────────────────────────────────────
Hardware                  →  General (PC3)         →  Normal (10)
Software                  →  General (PC3)         →  Normal (10)
Accesos / Contraseñas     →  General (PC3)         →  Normal (10)
Conectividad / Red        →  Especializada (PC5)   →  Normal (5)
Servidores / Infra        →  Especializada (PC5)   →  Normal (5)
Incidente Crítico P1      →  Crítica (PC4)         →  Prioridad 1
Incidente Crítico P2      →  Crítica (PC4)         →  Prioridad 2
Incidente Crítico P3      →  Crítica (PC4)         →  Prioridad 3
```

---

## 💾 Persistencia

Cada ticket resuelto se guarda en `tickets.csv`:

```
numeroTicket|idEmpleado|categoria|prioridad|horaRegistro|tecnico|descripcion|solucion|horaInicio|horaFin|estado|tiempoAtencion|tiempoTotal
```

Al iniciar el servidor, carga el CSV y reconstruye:
- El **Árbol B+** (búsqueda por número de ticket)
- La **Tabla Hash** (historial por empleado)

---

## 📁 Estructura del proyecto

```
sistema-soporte-it/
├── pom.xml                              ← POM raíz multi-módulo
├── config.properties                    ← Configuración de red
├── tickets.csv                          ← Persistencia de tickets resueltos
├── build.sh / build.bat                 ← Scripts de compilación
├── README.md
│
└── servidor/
    ├── pom.xml                          ← Genera los 5 JARs ejecutables
    └── src/main/java/gt/edu/soporte/
        ├── modelo/
        │   ├── Ticket.java              ← Entidad principal del sistema
        │   ├── Mensaje.java             ← Protocolo de comunicación
        │   └── CategoriaTicket.java     ← Enum de categorías y prioridades
        ├── estructuras/
        │   ├── ArbolBPlus.java          ← Árbol B+ propio (t=3)
        │   └── TablaHash.java           ← Hash para historial por empleado
        ├── servidor/
        │   ├── ServidorTCP.java         ← PC1: servidor TCP, pool de hilos
        │   ├── GestorTickets.java       ← Singleton: gestiona las 3 colas
        │   └── ManejadorCliente.java    ← Hilo por cliente conectado
        ├── cliente/
        │   └── ConexionServidor.java    ← Conexión TCP reutilizable
        └── ui/
            ├── VentanaServidor.java          ← PC1: dashboard en tiempo real
            ├── VentanaReporte.java           ← PC2: reporte de problema
            ├── VentanaSoporteGeneral.java    ← PC3: soporte general
            ├── VentanaSoporteBase.java       ← Clase base para PC4 y PC5
            ├── VentanaIncidenteCritico.java  ← PC4: incidentes P1/P2/P3
            └── VentanaSoporteEspecial.java   ← PC5: redes y servidores
```

---

## 🔨 Requisitos

- **Java 17+**
- **Maven 3.8+**
- Los JARs se ejecutan desde terminal — **no requieren Netbeans ni IntelliJ**

---

## 🚀 Compilar y generar JARs

### Windows
```bat
build.bat
```

### Linux / Mac
```bash
chmod +x build.sh && ./build.sh
```

### Manual con Maven
```bash
mvn clean package -f servidor/pom.xml
```

JARs generados en `servidor/target/`:
```
PC1-ServidorIT.jar
PC2-ReporteProblema.jar
PC3-SoporteGeneral.jar
PC4-IncidenteCritico.jar
PC5-SoporteEspecializado.jar
```

---

## 🌐 Configuración de red

Edita `config.properties` en **cada máquina**:

```properties
# Servidor (PC1)
servidor.puerto=9090
servidor.maxHilos=20

# Clientes (PC2-PC5) — cambiar por la IP real de PC1 en red local
servidor.host=192.168.1.10
servidor.puerto=9090
```

---

## ▶️ Orden de ejecución

```bash
# 1. PRIMERO — Servidor
java -jar PC1-ServidorIT.jar

# 2. LUEGO — Clientes (en cualquier orden)
java -jar PC2-ReporteProblema.jar
java -jar PC3-SoporteGeneral.jar
java -jar PC4-IncidenteCritico.jar
java -jar PC5-SoporteEspecializado.jar
```

> ⚠️ Siempre ejecutar PC1 antes de conectar los clientes.

---

## 📊 Eficiencia de búsqueda

### Árbol B+ — búsqueda por número de ticket
| Operación | Complejidad |
|---|---|
| Búsqueda | O(log_t n) |
| Inserción | O(log_t n) amortizado |
| Recorrido secuencial | O(n) |

Para 1000 tickets con t=3: ≈ log₃(1000) ≈ **6 comparaciones de nodo**.
Ventaja sobre BST: menos niveles, mejor aprovechamiento de caché.

### Tabla Hash — historial por ID de empleado
| Operación | Complejidad promedio | Complejidad peor caso |
|---|---|---|
| Búsqueda | O(1) | O(n) con muchas colisiones |
| Inserción | O(1) | O(n) |

Factor de carga recomendado: < 0.75 para mantener O(1) efectivo.
Java `HashMap` usa rehashing automático al superar este umbral.

> La función `reporteEficiencia()` imprime en tiempo de ejecución la altura del árbol y el factor de carga de la tabla hash.

---

## 👥 División del trabajo

| Módulo | Responsable | IDE | Estado |
|---|---|---|---|
| PC1 — Servidor + GestorTickets + ManejadorCliente | Integrante 1 | Netbeans | 🔄 En progreso |
| PC2 — Reporte de problema (VentanaReporte) | Integrante 2 | IntelliJ | 🔄 En progreso |
| PC3 — Soporte general | Por definir | — | ⏳ Pendiente |
| PC4 — Incidentes críticos | Por definir | — | ⏳ Pendiente |
| PC5 — Soporte especializado | Por definir | — | ⏳ Pendiente |
| Árbol B+ (ArbolBPlus.java) | Por definir | — | ⏳ Pendiente |
| Tabla Hash (TablaHash.java) | Por definir | — | ⏳ Pendiente |
| README / Documentación | Ambos | — | ✅ En revisión |

---

## 🔗 GitHub — Convención de commits

```bash
git init
git add .
git commit -m "feat: estructura base del proyecto"
git remote add origin https://github.com/TU_USUARIO/sistema-soporte-it
git push -u origin main
```

**Prefijos de commits:**
- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `docs:` cambio en documentación
- `refactor:` mejora de código sin cambio funcional
- `test:` pruebas
