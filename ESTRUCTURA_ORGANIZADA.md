# 📁 Estructura Organizada por Features

## ✅ Nueva Organización

La app ahora está organizada por **features** (funcionalidades), lo que facilita:
- 🔍 Encontrar código rápidamente
- 🧪 Testear features independientemente
- 🚀 Escalar la aplicación
- 👥 Trabajar en equipo sin conflictos

---

## 📊 Estructura Completa

```
com.marcos.postresapp/
│
├── 📱 PostresApplication.kt (@HiltAndroidApp)
│
├── 💉 di/                                    # Dependency Injection
│   ├── AppModule.kt                          # Dependencias generales
│   ├── NetworkModule.kt                      # Retrofit, OkHttp, ApiServices
│   └── RepositoryModule.kt                   # Repositorios
│
├── 📊 data/                                  # Capa de Datos
│   │
│   ├── local/                                # Almacenamiento local
│   │   └── PrefsManager.kt                   # SharedPreferences
│   │
│   ├── remote/                               # Comunicación con API
│   │   │
│   │   ├── api/                              # Servicios Retrofit
│   │   │   ├── AuthApiService.kt
│   │   │   ├── ProductoApiService.kt
│   │   │   └── CategoriaApiService.kt
│   │   │
│   │   ├── dto/                              # Data Transfer Objects
│   │   │   │
│   │   │   ├── auth/                         🔐 AUTH FEATURE
│   │   │   │   ├── LoginRequestDto.kt
│   │   │   │   ├── LoginResponseDto.kt
│   │   │   │   └── RefreshTokenDto.kt
│   │   │   │
│   │   │   └── producto/                     🍰 PRODUCTO FEATURE
│   │   │       ├── ProductoDto.kt
│   │   │       ├── CategoriaDto.kt
│   │   │       └── ProductoRequestDto.kt
│   │   │
│   │   └── models/                           ⚠️ LEGACY (migrar a dto/)
│   │       ├── LoginRequest.kt
│   │       ├── LoginResponse.kt
│   │       └── UserResponse.kt
│   │
│   ├── mapper/                               # Conversión DTO → Domain
│   │   │
│   │   ├── auth/                             🔐 AUTH MAPPERS
│   │   │   └── AuthMapper.kt
│   │   │
│   │   └── producto/                         🍰 PRODUCTO MAPPERS
│   │       └── ProductoMapper.kt
│   │
│   └── repository/                           # Implementaciones
│       │
│       ├── auth/                             🔐 AUTH REPOSITORY
│       │   └── AuthRepositoryImpl.kt
│       │
│       └── AuthRepositoryImpl.kt             ⚠️ LEGACY (eliminar)
│
├── 🎯 domain/                                # Capa de Dominio
│   │
│   ├── model/                                # Entidades de negocio
│   │   ├── User.kt
│   │   ├── Producto.kt
│   │   └── Categoria.kt
│   │
│   ├── repository/                           # Interfaces (contratos)
│   │   │
│   │   ├── auth/                             🔐 AUTH CONTRACTS
│   │   │   └── AuthRepository.kt
│   │   │
│   │   ├── ProductoRepository.kt
│   │   └── CategoriaRepository.kt
│   │
│   └── usecase/                              # Casos de uso
│       │
│       └── auth/                             🔐 AUTH USE CASES
│           ├── LoginUseCase.kt
│           ├── LogoutUseCase.kt
│           └── GetUserRolesUseCase.kt
│
└── 🎨 presentation/                          # Capa de Presentación
    │
    ├── state/                                # Estados UI
    │   └── UiState.kt
    │
    ├── viewmodel/                            # ViewModels
    │   │
    │   ├── auth/                             🔐 AUTH VIEWMODELS
    │   │   └── LoginViewModel.kt
    │   │
    │   ├── LoginViewModel.kt                 ⚠️ LEGACY (eliminar)
    │   └── LoginViewModelFactory.kt          ⚠️ LEGACY (eliminar)
    │
    └── ui/                                   # UI Components
        │
        ├── activity/
        │   ├── auth/                         🔐 AUTH ACTIVITIES
        │   │   ├── LoginActivity.kt
        │   │   └── RegisterActivity.kt
        │   │
        │   ├── admin/
        │   │   └── HomeAdminActivity.kt
        │   │
        │   ├── user/
        │   │   └── HomeUserActivity.kt
        │   │
        │   ├── repartidor/
        │   │   └── HomeRepartidorActivity.kt
        │   │
        │   ├── SplashActivity.kt
        │   └── SideActivity.kt
        │
        ├── fragment/
        │   ├── auth/                         🔐 AUTH FRAGMENTS
        │   │   ├── Register1Fragment.kt
        │   │   └── Register2Fragment.kt
        │   │
        │   ├── admin/
        │   │   ├── CatalogoAdminFragment.kt
        │   │   └── PedidoAdminFragment.kt
        │   │
        │   ├── user/
        │   │   ├── CatalogoUserFragment.kt
        │   │   └── PedidoUserFragment.kt
        │   │
        │   └── ProfileFragment.kt
        │
        └── adapter/
            ├── ProductoAdapter.kt
            ├── ProductoAdminAdapter.kt
            ├── CategoriaAdapter.kt
            └── ImageAdapter.kt
```

---

## 🔐 Feature: AUTH (Autenticación)

### Flujo completo:

```
LoginActivity
    ↓
LoginViewModel (@HiltViewModel)
    ↓
LoginUseCase
    ↓
AuthRepository (interface)
    ↓
AuthRepositoryImpl
    ↓
AuthApiService
    ↓
API
```

### Archivos organizados:

```
📁 auth/
├── data/remote/dto/auth/
│   ├── LoginRequestDto.kt
│   ├── LoginResponseDto.kt
│   └── RefreshTokenDto.kt
│
├── data/mapper/auth/
│   └── AuthMapper.kt
│
├── data/repository/auth/
│   └── AuthRepositoryImpl.kt
│
├── domain/repository/auth/
│   └── AuthRepository.kt
│
├── domain/usecase/auth/
│   ├── LoginUseCase.kt
│   ├── LogoutUseCase.kt
│   └── GetUserRolesUseCase.kt
│
└── presentation/viewmodel/auth/
    └── LoginViewModel.kt
```

---

## 🍰 Feature: PRODUCTO (Catálogo)

### Archivos organizados:

```
📁 producto/
├── data/remote/dto/producto/
│   ├── ProductoDto.kt
│   ├── CategoriaDto.kt
│   └── ProductoRequestDto.kt
│
└── data/mapper/producto/
    └── ProductoMapper.kt
```

---

## 🎯 Beneficios de esta Organización

### 1. **Fácil de Navegar** 🗺️
```
¿Necesitas algo de Auth?
→ Busca en carpetas "auth/"

¿Necesitas algo de Productos?
→ Busca en carpetas "producto/"
```

### 2. **Escalable** 📈
```
Agregar nueva feature "Pedidos":
├── data/remote/dto/pedido/
├── data/mapper/pedido/
├── data/repository/pedido/
├── domain/repository/pedido/
├── domain/usecase/pedido/
└── presentation/viewmodel/pedido/
```

### 3. **Testeable** 🧪
```
Testear solo Auth:
- Todos los archivos están juntos
- Fácil crear mocks
- Tests independientes
```

### 4. **Trabajo en Equipo** 👥
```
Developer A: Trabaja en auth/
Developer B: Trabaja en producto/
Developer C: Trabaja en pedido/

Sin conflictos de merge!
```

---

## 📝 Próximas Features a Organizar

### 🛒 Pedidos
```
pedido/
├── data/remote/dto/pedido/
│   ├── PedidoDto.kt
│   ├── DetallePedidoDto.kt
│   └── EstadoDto.kt
├── data/mapper/pedido/
├── data/repository/pedido/
├── domain/repository/pedido/
├── domain/usecase/pedido/
└── presentation/viewmodel/pedido/
```

### 🚚 Repartidor
```
repartidor/
├── data/remote/dto/repartidor/
├── data/mapper/repartidor/
├── data/repository/repartidor/
├── domain/repository/repartidor/
├── domain/usecase/repartidor/
└── presentation/viewmodel/repartidor/
```

### 👤 Perfil
```
perfil/
├── data/remote/dto/perfil/
├── data/mapper/perfil/
├── data/repository/perfil/
├── domain/repository/perfil/
├── domain/usecase/perfil/
└── presentation/viewmodel/perfil/
```

---

## ⚠️ Archivos Legacy a Eliminar

Una vez que migremos completamente:

```
❌ data/remote/models/          (usar dto/ en su lugar)
❌ data/repository/AuthRepositoryImpl.kt  (usar auth/AuthRepositoryImpl.kt)
❌ presentation/viewmodel/LoginViewModel.kt  (usar auth/LoginViewModel.kt)
❌ presentation/viewmodel/LoginViewModelFactory.kt  (Hilt lo reemplaza)
```

---

## 🚀 Siguiente Paso

Ahora que tenemos la estructura organizada, vamos a:
1. ✅ Actualizar LoginActivity para usar el nuevo LoginViewModel
2. ✅ Probar el login con Clean Architecture
3. ⏳ Continuar con otras features
