# 👨‍💼 Funcionalidades del ADMIN

## 📋 Resumen Ejecutivo

El ADMIN es el rol con más permisos en la aplicación. Gestiona productos, categorías, pedidos y repartidores.

---

## 🎯 Funcionalidades Principales

### **1. Gestión de Productos** 🍰

#### **CRUD Completo:**
- ✅ **Crear** producto (con imagen)
- ✅ **Ver** todos los productos
- ✅ **Actualizar** producto
- ✅ **Eliminar** producto (lógica: `isActive = 'I'`)

#### **Endpoints:**
```
GET    /api/v1/productos              - Listar todos
GET    /api/v1/productos/{id}         - Ver uno
POST   /api/v1/productos              - Crear
POST   /api/v1/productos/createWithImage - Crear con imagen
PUT    /api/v1/productos/{id}         - Actualizar
DELETE /api/v1/productos/{id}         - Eliminar (lógica)
POST   /api/v1/productos/uploadImage/{id} - Subir imagen
```

#### **Estado Actual:**
- ✅ Implementado en `CatalogoAdminFragment`
- ⚠️ Sin Clean Architecture

---

### **2. Gestión de Categorías** 🏷️

#### **CRUD Completo:**
- ✅ **Crear** categoría
- ✅ **Ver** todas las categorías
- ✅ **Actualizar** categoría
- ✅ **Eliminar** categoría (lógica: `isActive = 'I'`)

#### **Endpoints:**
```
GET    /api/v1/categorias             - Listar todas
GET    /api/v1/categorias/{id}        - Ver una
POST   /api/v1/categorias             - Crear
PUT    /api/v1/categorias/{id}        - Actualizar
DELETE /api/v1/categorias/{id}        - Eliminar (lógica)
```

#### **Estado Actual:**
- ✅ Listar implementado en `CatalogoAdminFragment`
- ❌ CRUD completo sin implementar

---

### **3. Gestión de Pedidos** 📦

#### **Funcionalidades:**

##### **A. Ver Pedidos**
- ✅ Ver **todos** los pedidos
- ✅ Ver **detalle** de un pedido
- ✅ Filtrar por estado

##### **B. Gestionar Estados del Pedido**
Flujo de estados:
```
PENDIENTE
    ↓ (Admin acepta)
ACEPTADO
    ↓ (Admin marca en preparación)
EN_PREPARACION
    ↓ (Admin marca listo)
LISTO_PARA_ENTREGA
    ↓ (Admin asigna repartidor)
ASIGNADO
    ↓ (Repartidor inicia entrega)
EN_CAMINO
    ↓ (Repartidor marca entregado)
ENTREGADO

(En cualquier momento Admin puede CANCELAR)
```

##### **C. Acciones del Admin:**
1. **Aceptar Pedido** → Cambia a `ACEPTADO`
2. **Marcar en Preparación** → Cambia a `EN_PREPARACION`
3. **Marcar Listo** → Cambia a `LISTO_PARA_ENTREGA`
4. **Asignar Repartidor** → Cambia a `ASIGNADO`
5. **Cancelar Pedido** → Cambia a `CANCELADO`

#### **Endpoints:**
```
GET    /api/v1/pedidos                - Listar todos
GET    /api/v1/pedidos/{id}           - Ver uno
GET    /api/v1/pedidos/{id}/detalle   - Ver detalle completo
POST   /api/v1/pedidos                - Crear (admin)
PUT    /api/v1/pedidos/{id}           - Actualizar
DELETE /api/v1/pedidos/{id}           - Eliminar (lógica)

# Cambios de estado (ADMIN)
PUT    /api/v1/pedidos/{id}/aceptar              - PENDIENTE → ACEPTADO
PUT    /api/v1/pedidos/{id}/en-preparacion       - ACEPTADO → EN_PREPARACION
PUT    /api/v1/pedidos/{id}/listo-para-entrega  - EN_PREPARACION → LISTO_PARA_ENTREGA
PUT    /api/v1/pedidos/{id}/asignar/{idRepartidor} - LISTO → ASIGNADO
PUT    /api/v1/pedidos/{id}/cancelar             - Cualquier estado → CANCELADO
```

#### **Estado Actual:**
- ❌ `PedidoAdminFragment` vacío
- ❌ Sin implementar

---

### **4. Gestión de Repartidores** 🚚

#### **Funcionalidades:**
- ✅ **Registrar** nuevo repartidor
- ✅ **Ver** todos los repartidores
- ✅ **Actualizar** repartidor
- ✅ **Eliminar** repartidor (lógica: `isActive = 'I'`)
- ✅ **Asignar** repartidor a pedido

#### **Endpoints:**
```
# Auth
POST   /api/v1/auth/registerRepartidor - Registrar nuevo repartidor

# CRUD Repartidores
GET    /api/v1/repartidores            - Listar todos
GET    /api/v1/repartidores/{id}       - Ver uno
POST   /api/v1/repartidores            - Crear
PUT    /api/v1/repartidores/{id}       - Actualizar
DELETE /api/v1/repartidores/{id}       - Eliminar (lógica)
```

#### **Datos del Repartidor:**
```json
{
  "username": "repartidor1",
  "password": "Rep@123",
  "codigo": "R-0001"
}
```

#### **Estado Actual:**
- ❌ Sin implementar en la app

---

### **5. Gestión de Personas** 👤

#### **CRUD Completo:**
- ✅ **Crear** persona
- ✅ **Ver** todas las personas
- ✅ **Actualizar** persona
- ✅ **Eliminar** persona (lógica: `isActive = 'I'`)

#### **Endpoints:**
```
GET    /api/v1/personas               - Listar todas
GET    /api/v1/personas/{id}          - Ver una
POST   /api/v1/personas               - Crear
PUT    /api/v1/personas/{id}          - Actualizar
DELETE /api/v1/personas/{id}          - Eliminar (lógica)
```

#### **Estado Actual:**
- ❌ Sin implementar en la app

---

## 📱 Estructura de la Vista ADMIN

### **HomeAdminActivity**
```
Toolbar (morado)
    ↓
ViewPager2 con 3 tabs:
    ├─ Tab 0: "Inicio" → CatalogoAdminFragment
    ├─ Tab 1: "Pedidos" → PedidoAdminFragment
    └─ Tab 2: "Perfil" → ProfileFragment
    ↓
TabLayout (inferior)
    ↓
NavigationDrawer (lateral)
    └─ Opción "Salir"
```

---

## 🎨 Propuesta de Tabs

### **Tab 0: Catálogo (Inicio)** 🍰
**Funcionalidades:**
- Ver productos (grid)
- Filtrar por categorías
- Crear producto con imagen
- Editar producto
- Eliminar producto
- Gestionar categorías (CRUD)

**Estado:** ✅ Funcional (necesita refactorización)

---

### **Tab 1: Pedidos** 📦
**Funcionalidades:**
- Ver todos los pedidos (lista)
- Filtrar por estado (chips)
- Ver detalle de pedido (dialog/nueva activity)
- Cambiar estado del pedido:
  - Aceptar
  - Marcar en preparación
  - Marcar listo para entrega
  - Asignar repartidor (spinner)
  - Cancelar
- Ver historial de cambios de estado

**Estado:** ❌ Sin implementar

**Diseño Propuesto:**
```
┌─────────────────────────────────────┐
│ Chips de filtro:                    │
│ [Todos] [Pendiente] [Aceptado]      │
│ [En Preparación] [Listo] [Asignado] │
│ [En Camino] [Entregado] [Cancelado] │
├─────────────────────────────────────┤
│ RecyclerView de Pedidos:            │
│ ┌─────────────────────────────────┐ │
│ │ Pedido #ORD-001                 │ │
│ │ Cliente: Juan Pérez             │ │
│ │ Estado: PENDIENTE               │ │
│ │ Total: S/. 99.90                │ │
│ │ Fecha: 31/12/2030 14:30         │ │
│ │ [Ver Detalle] [Aceptar]         │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Pedido #ORD-002                 │ │
│ │ ...                             │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

---

### **Tab 2: Perfil** 👤
**Funcionalidades:**
- Ver datos del admin
- Editar perfil
- Cambiar foto de perfil
- Cambiar contraseña
- **Gestión de Repartidores:**
  - Ver lista de repartidores
  - Registrar nuevo repartidor
  - Editar repartidor
  - Eliminar repartidor

**Estado:** ❌ Sin implementar

**Diseño Propuesto:**
```
┌─────────────────────────────────────┐
│ Foto de Perfil (circular)           │
│ Nombre: Admin                       │
│ Username: admin                     │
│ Rol: ADMIN                          │
├─────────────────────────────────────┤
│ [Editar Perfil]                     │
│ [Cambiar Contraseña]                │
├─────────────────────────────────────┤
│ Gestión de Repartidores:            │
│ [+ Registrar Repartidor]            │
│                                     │
│ Lista de Repartidores:              │
│ ┌─────────────────────────────────┐ │
│ │ R-0001 - Juan Repartidor        │ │
│ │ [Editar] [Eliminar]             │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

---

## 🚀 Plan de Implementación

### **FASE 1: Refactorizar CatalogoAdminFragment** ⏳
**Tiempo estimado:** 1-2 horas

**Tareas:**
1. Crear ProductoRepository + Impl
2. Crear CategoriaRepository + Impl
3. Crear UseCases:
   - GetProductosUseCase
   - GetCategoriasUseCase
   - CreateProductoUseCase
   - DeleteProductoUseCase
   - UpdateProductoUseCase
4. Crear CatalogoAdminViewModel
5. Actualizar Fragment con Clean Architecture
6. Agregar gestión de categorías (CRUD)

---

### **FASE 2: Implementar PedidoAdminFragment** ⏳
**Tiempo estimado:** 3-4 horas

**Tareas:**
1. Crear DTOs de Pedido
2. Crear PedidoRepository + Impl
3. Crear UseCases:
   - GetPedidosUseCase
   - GetPedidoDetalleUseCase
   - AceptarPedidoUseCase
   - MarcarEnPreparacionUseCase
   - MarcarListoUseCase
   - AsignarRepartidorUseCase
   - CancelarPedidoUseCase
4. Crear PedidoAdminViewModel
5. Diseñar layout
6. Implementar funcionalidades

---

### **FASE 3: Implementar ProfileFragment** ⏳
**Tiempo estimado:** 2-3 horas

**Tareas:**
1. Crear DTOs de Perfil y Repartidor
2. Crear PerfilRepository + Impl
3. Crear RepartidorRepository + Impl
4. Crear UseCases
5. Crear ProfileViewModel
6. Diseñar layout
7. Implementar funcionalidades

---

## 📝 Resumen de Estados

### **✅ Implementado:**
- Login con rol ADMIN
- CatalogoAdminFragment (funcional pero sin Clean Arch)
  - Ver productos
  - Filtrar categorías
  - Crear producto con imagen
  - Eliminar producto

### **⏳ Pendiente:**
- Refactorizar CatalogoAdminFragment con Clean Architecture
- Agregar CRUD de categorías
- Implementar PedidoAdminFragment
- Implementar ProfileFragment
- Gestión de repartidores

---

## 🎯 Recomendación

**Empezar con FASE 1: Refactorizar CatalogoAdminFragment**

**¿Por qué?**
1. Ya funciona → bajo riesgo
2. Aprenderás el patrón completo
3. Podrás replicarlo en PedidoAdminFragment
4. Base sólida para continuar

**Siguiente:** FASE 2 (PedidoAdminFragment) - Feature crítica

---

¿Empezamos con la refactorización del CatalogoAdminFragment? 🚀
