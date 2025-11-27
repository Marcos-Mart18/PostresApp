# 🎯 Flujo Completo: Login → Vista ADMIN

## 📱 Flujo de Navegación

```
1. SplashActivity
   ↓
2. LoginActivity (compartida)
   ↓ Login con rol ADMIN
3. HomeAdminActivity
   ↓ ViewPager2 con 3 tabs
   ├─ Tab 0: CatalogoAdminFragment (Inicio) ✅ FUNCIONAL
   ├─ Tab 1: PedidoAdminFragment (Pedidos) ⚠️ VACÍO
   └─ Tab 2: ProfileFragment (Perfil) ⚠️ VACÍO
```

---

## 🏠 HomeAdminActivity - Vista Principal del Admin

### **Componentes:**

#### **1. Toolbar** 🔝
- Color morado
- Botón hamburguesa (abre drawer)

#### **2. ViewPager2** 📄
- Permite deslizar entre 3 fragments
- Ocupa todo el espacio central

#### **3. TabLayout** 📑
- 3 tabs en la parte inferior:
  - **"Inicio"** → CatalogoAdminFragment
  - **"Pedidos"** → PedidoAdminFragment
  - **"Perfil"** → ProfileFragment
- Color morado
- Indicador blanco

#### **4. NavigationDrawer** 🍔
- Menu lateral (hamburguesa)
- Header personalizado
- Opción "Salir" (logout)

---

## ✅ Tab 0: CatalogoAdminFragment (FUNCIONAL)

### **Funcionalidades Implementadas:**

#### **1. Ver Productos** 👀
- ✅ Lista de productos en grid (2 columnas)
- ✅ Muestra: foto, nombre, precio, descripción
- ✅ Carga desde API

#### **2. Filtrar por Categorías** 🏷️
- ✅ RecyclerView horizontal de categorías
- ✅ Chip "Todos" para ver todo
- ✅ Click en categoría filtra productos

#### **3. Crear Producto** ➕
- ✅ Card "Agregar" abre formulario
- ✅ Formulario con:
  - Nombre
  - Precio
  - Descripción
  - Spinner de categorías
  - Selector de imagen
- ✅ Sube imagen a Cloudinary
- ✅ Crea producto en API
- ✅ Loading con Lottie

#### **4. Eliminar Producto** 🗑️
- ✅ Long press en producto
- ✅ Diálogo de confirmación (Material)
- ✅ Elimina de API
- ✅ Actualiza lista

#### **5. Carrusel de Imágenes** 🎠
- ✅ ViewPager2 con fotos de productos
- ✅ Auto-scroll cada 3 segundos

### **Arquitectura Actual:**
```
❌ Sin Clean Architecture
- Fragment llama directamente a ApiService
- Sin ViewModel
- Sin UseCases
- Sin Repository
- Lógica mezclada con UI
```

---

## ⚠️ Tab 1: PedidoAdminFragment (VACÍO)

### **Estado Actual:**
```kotlin
class PedidoAdminFragment : Fragment() {
    // TODO: Implementar
}
```

### **Funcionalidades Necesarias:**
- [ ] Ver todos los pedidos
- [ ] Filtrar por estado
- [ ] Ver detalle de pedido
- [ ] Cambiar estado (Aceptar, En preparación, etc.)
- [ ] Asignar repartidor
- [ ] Cancelar pedido

---

## ⚠️ Tab 2: ProfileFragment (VACÍO)

### **Estado Actual:**
```kotlin
class ProfileFragment : Fragment() {
    // TODO: Implementar
}
```

### **Funcionalidades Necesarias:**
- [ ] Ver datos del usuario
- [ ] Editar perfil
- [ ] Cambiar foto de perfil
- [ ] Cambiar contraseña
- [ ] Ver estadísticas (si es admin)

---

## 🎨 Layouts Relacionados

### **activity_home_admin.xml**
```xml
DrawerLayout
├── LinearLayout (contenido principal)
│   ├── Toolbar (morado)
│   ├── ViewPager2 (fragments)
│   └── TabLayout (tabs inferiores)
└── NavigationView (menu lateral)
```

### **fragment_catalogo_admin.xml**
```xml
ConstraintLayout
├── ViewPager2 (carrusel de imágenes)
├── RecyclerView (categorías horizontal)
├── Chip "Todos"
├── CardView "Agregar Producto"
├── RecyclerView (productos grid)
├── LinearLayout (formulario crear producto)
└── View (loading overlay con Lottie)
```

### **fragment_pedido_admin.xml**
```xml
FrameLayout
└── TextView "PedidoAdminFragment" (placeholder)
```

### **fragment_profile.xml**
```xml
FrameLayout
└── TextView "ProfileFragment" (placeholder)
```

---

## 🔧 Problemas Actuales

### **1. HomeAdminActivity usa código legacy**
```kotlin
❌ Instancia manual de AuthRepositoryImpl
❌ Usa NetworkClient.createBasic() (no Hilt)
❌ No usa el nuevo AuthRepository con Clean Architecture
```

### **2. CatalogoAdminFragment sin Clean Architecture**
```kotlin
❌ Llama directamente a ApiService
❌ Sin ViewModel
❌ Sin UseCases
❌ Sin manejo de estados (UiState)
❌ Lógica mezclada con UI
```

### **3. Fragments vacíos**
```kotlin
❌ PedidoAdminFragment sin implementar
❌ ProfileFragment sin implementar
```

---

## 🚀 Plan de Refactorización

### **FASE 1: Refactorizar HomeAdminActivity** ⏳
```kotlin
1. Agregar @AndroidEntryPoint
2. Inyectar LogoutUseCase con Hilt
3. Crear LogoutViewModel
4. Usar UiState para logout
5. Eliminar código legacy
```

### **FASE 2: Refactorizar CatalogoAdminFragment** ⏳
```kotlin
1. Crear ProductoRepository + Impl
2. Crear CategoriaRepository + Impl
3. Crear UseCases:
   - GetProductosUseCase
   - GetCategoriasUseCase
   - CreateProductoUseCase
   - DeleteProductoUseCase
4. Crear CatalogoAdminViewModel
5. Usar UiState
6. Agregar @AndroidEntryPoint
```

### **FASE 3: Implementar PedidoAdminFragment** ⏳
```kotlin
1. Crear DTOs de Pedido
2. Crear PedidoRepository
3. Crear UseCases de Pedido
4. Crear PedidoAdminViewModel
5. Diseñar layout
6. Implementar funcionalidades
```

### **FASE 4: Implementar ProfileFragment** ⏳
```kotlin
1. Crear DTOs de Perfil
2. Crear PerfilRepository
3. Crear UseCases de Perfil
4. Crear ProfileViewModel
5. Diseñar layout
6. Implementar funcionalidades
```

---

## 📊 Estado Actual del Proyecto

### **✅ Completado:**
- [x] Clean Architecture base
- [x] Hilt configurado
- [x] Auth (Login) con Clean Architecture
- [x] Estructura organizada por features
- [x] UiState implementado
- [x] LoginActivity refactorizado

### **⏳ En Progreso:**
- [ ] HomeAdminActivity (necesita refactorización)
- [ ] CatalogoAdminFragment (funcional pero sin Clean Architecture)

### **❌ Pendiente:**
- [ ] PedidoAdminFragment (vacío)
- [ ] ProfileFragment (vacío)
- [ ] Vista Cliente (HomeUserActivity)
- [ ] Vista Repartidor (HomeRepartidorActivity)

---

## 🎯 Siguiente Paso Recomendado

### **Opción A: Refactorizar CatalogoAdminFragment** 🍰
**Ventaja:** Ya funciona, solo necesita Clean Architecture
**Tiempo:** Medio
**Impacto:** Alto (aprenderás el patrón para otros fragments)

### **Opción B: Implementar PedidoAdminFragment** 📦
**Ventaja:** Feature nueva y crítica
**Tiempo:** Alto
**Impacto:** Muy alto (funcionalidad core)

### **Opción C: Refactorizar HomeAdminActivity** 🏠
**Ventaja:** Limpia el código legacy
**Tiempo:** Bajo
**Impacto:** Medio

---

## 💡 Mi Recomendación

**Empezar con Opción A: Refactorizar CatalogoAdminFragment**

**¿Por qué?**
1. Ya funciona, solo necesita organización
2. Aprenderás el patrón completo
3. Podrás replicarlo en otros fragments
4. Menos riesgo de romper funcionalidad

**Pasos:**
1. Crear ProductoRepository con Clean Architecture
2. Crear CategoriaRepository con Clean Architecture
3. Crear UseCases
4. Crear ViewModel
5. Actualizar Fragment para usar ViewModel
6. Probar que todo siga funcionando

---

## 📝 Resumen Visual

```
Login (ADMIN) ✅
    ↓
HomeAdminActivity ⚠️ (legacy code)
    ↓
    ├─ CatalogoAdminFragment ⚠️ (funcional pero sin Clean Arch)
    │   ├─ Ver productos ✅
    │   ├─ Filtrar categorías ✅
    │   ├─ Crear producto ✅
    │   ├─ Eliminar producto ✅
    │   └─ Carrusel ✅
    │
    ├─ PedidoAdminFragment ❌ (vacío)
    │
    └─ ProfileFragment ❌ (vacío)
```

---

¿Por dónde quieres empezar? 🚀
