# 💻 Sistema de Soporte Técnico IT — Mesa de Ayuda

Proyecto de Estructuras de Datos — Sistema de Colas  
Modalidad: Grupal (máximo 4 integrantes)

---

## 📋 Descripción

Sistema distribuido en Java que simula una **Mesa de Ayuda de Tecnología (Help Desk)**.  
Gestiona tickets de soporte mediante colas, prioridades, hilos y comunicación por Sockets.

---

## 🏗️ Arquitectura del Sistema

```
PC1 (Servidor)        ← Centro de control, cola principal, árbol B+
PC2 (Registro)        ← Usuario reporta problema → genera ticket
PC3 (Soporte General) ← Atiende cola general (hardware, software común)
PC4 (Prioritario)     ← Atiende incidentes críticos (prioridad alta)
PC5 (Especializado)   ← Redes, servidores, infraestructura
```

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
└── test-files/                   # Archivos de prueba generados
```

---

## 🔧 Estructuras de Datos Utilizadas

| Estructura       | Uso en el sistema                                        |
|------------------|----------------------------------------------------------|
| `Queue<Ticket>`  | Cola general de tickets pendientes (FIFO)                |
| `PriorityQueue`  | Cola prioritaria para incidentes críticos                |
| `Stack`          | Historial de acciones por sesión de agente               |
| `HashMap`        | Historial de tickets por DPI de usuario                  |
| `B+ Tree`        | Persistencia y búsqueda eficiente de tickets atendidos   |

---

## 🌐 Comunicación por Sockets

| Módulo | Puerto  | Rol      |
|--------|---------|----------|
| PC1    | 5000    | Servidor |
| PC2    | cliente | Cliente  |
| PC3    | cliente | Cliente  |
| PC4    | cliente | Cliente  |
| PC5    | cliente | Cliente  |

Configuración en: `config/socket.properties`

---

## ▶️ Ejecución

Cada módulo se ejecuta con su `.jar` correspondiente desde `jars/`:

```bash
# Primero iniciar el servidor
java -jar jars/PC1_Servidor.jar

# Luego iniciar los clientes (en cualquier orden)
java -jar jars/PC2_Registro.jar
java -jar jars/PC3_SoporteGeneral.jar
java -jar jars/PC4_Prioritario.jar
java -jar jars/PC5_Especializado.jar
```

---

## 📊 Clasificación de Tickets

| Tipo         | Prioridad | Módulo Destino |
|--------------|-----------|----------------|
| Hardware     | Normal    | PC3            |
| Software     | Normal    | PC3            |
| Red/Internet | Alta      | PC4 / PC5      |
| Servidor     | Crítica   | PC4 / PC5      |
| Accesos      | Alta      | PC4            |
| Otro         | Normal    | PC3            |

---

## 👥 Integrantes

| Nombre | Carné |
|--------|-------|
|        |       |
|        |       |
|        |       |
|        |       |

---

## 🔗 Repositorio

GitHub: _[URL del repositorio]_