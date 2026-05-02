# 💻 Sistema de Soporte Técnico IT — Mesa de Ayuda

**Proyecto:** Estructuras de Datos — Sistema de Colas  
**Modalidad:** Grupal (máximo 4 integrantes)  
**GitHub:** [Dev-Senju-M](https://github.com/Dev-Senju-M)

---

## 📋 Descripción

Sistema distribuido en **Java** que simula una **Mesa de Ayuda de Tecnología (Help Desk)**.  
Gestiona tickets de soporte técnico mediante estructuras de datos, concurrencia con hilos y comunicación entre procesos por **Sockets TCP**.

Cada módulo corre de forma independiente como un `.jar` ejecutable, simulando distintas estaciones de trabajo conectadas en red.

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

| Módulo | Rol                   | Descripción                                          |
|--------|-----------------------|------------------------------------------------------|
| PC1    | Servidor Central      | Administra colas, distribuye tickets, persiste datos |
| PC2    | Registro de Tickets   | El usuario reporta su problema y obtiene un ticket   |
| PC3    | Soporte General       | Atiende hardware y software común                    |
| PC4    | Soporte Prioritario   | Atiende incidentes críticos con prioridad            |
| PC5    | Soporte Especializado | Atiende redes, servidores e infraestructura          |

---

## 📂 Estructura de Carpetas

```
SoporteTecnicoIT/
├── src/
│   ├── server/                   # PC1 - Servidor central
│   ├── client/
│   │   ├── pc2_registro/         # PC2 - Registro de tickets
│   │   ├── pc3_general/          # PC3 - Soporte general
│   │   ├── pc4_prioritaria/      # PC4 - Incidentes críticos
│   │   └── pc5_especializado/    # PC5 - Soporte especializado
│   └── shared/
│       ├── models/               # Clases modelo (Ticket, Usuario, etc.)
│       ├── structures/           # Estructuras: Cola, Pila, BPlusTree, HashMap
│       └── utils/                # Constantes, serializacion, helpers
├── config/
│   └── socket.properties         # Configuración de puertos y host
├── docs/                         # Documentación y análisis de eficiencia
├── jars/                         # JARs ejecutables de cada módulo
└── test-files/                   # Archivos generados por el sistema
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

- **Java JDK 11** o superior
- **VS Code** con Extension Pack for Java
- No requiere librerías externas

---

## ▶️ Compilar y Ejecutar

### Compilar
```bash
# Windows
build.bat

# Linux / Mac
bash build.sh
```

### Ejecutar (siempre PC1 primero)
```bash
java -jar jars/PC1_Servidor.jar
java -jar jars/PC2_Registro.jar
java -jar jars/PC3_SoporteGeneral.jar
java -jar jars/PC4_Prioritario.jar
java -jar jars/PC5_Especializado.jar
```

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