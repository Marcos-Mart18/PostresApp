# Clean Architecture - PostresApp

## 📁 Estructura del Proyecto

```
com.marcos.postresapp/
│
├── 📦 di/                                    # Dependency Injection (Hilt)
│   ├── AppModule.kt                          ✅ Provee PrefsManager
│   └── NetworkModule.kt                      ✅ Provee Retrofit, ApiServices
│
├── 📊 data/                                  # Capa de Datos
│   ├── local/
│   │   └── PrefsManager.kt                   ✅ SharedPreferences
│   │
│   ├── remote/
│   │   ├── api/                              # Servicios de API
│   │   │   ├── AuthApiService.kt             ✅
│   │   │   ├── ProductoApiService.kt         ✅
│   │   │   ├── CategoriaApiService.kt        ✅
│   │   │   └── NetworkClient.kt              ⚠️  (deprecado, usar Hilt)
│   │   │
│   │   ├── dto/                              # Data Transfer Objects
│   │   │   ├── LoginRequestDto.kt            ✅
│   │   │   ├── LoginResponseDto.kt           ✅
│   │   │   └── ProductoDto.kt                ✅
│   │   │
│   │   └── models/                           ⚠️  (legacy - migrar a dto/)
│   │       ├── LoginRequest.kt
│   │       ├── LoginResponse.kt
│   │       └── ...
│   │
│   ├── mapper/                               # Conversión DTO → Domain
│   │   ├── AuthMapper.kt                     ✅
│   │   └── ProductoMapper.kt                 ✅
│   │
│   └── repository/                           # Implementaciones
│       └── AuthRepositoryImpl.kt             ⚠️  (refactorizar)
│
├── 🎯 domain/                                # Capa de Dominio (Lógica de Negocio)
│   ├── model/                                # Entidades de negocio
│   │   ├── User.kt                           ✅
│   │   ├── Producto.kt                       ✅
│   │   └── Categoria.kt                      ✅
│   │
│   ├── repository/                           # Interfaces (contratos)
│   │   ├── AuthRepository.kt                 ✅
│   │   ├── ProductoRepository.kt             ✅
│   │   └── CategoriaRepository.kt            ✅
│   │
│   ├── usecase/                              # Casos de uso
│   │   └── (pendiente)
│   │
│   └── dto/                                  ❌ ELIMINADO (DTOs van en data/)
│
└── 🎨 presentation/                          # Capa de Presentación
    ├── state/
    │   └── UiState.kt                        ✅ Sealed class para estados
    │
    ├── viewmodel/
    │   ├── LoginViewModel.kt                 ⚠️  (refactorizar con Hilt)
    │   └── LoginViewModelFactory.kt          ⚠️  (eliminar, usar Hilt)
    │
    └── ui/
        ├── activity/
        ├── fragment/
        └── adapter/
```

## ✅ Lo que ya está hecho

### 1. **Hilt configurado**
- ✅ Dependencies agregadas
- ✅ `PostresApplication` con `@HiltAndroidApp`
- ✅ `AppModule` para dependencias generales
- ✅ `NetworkModule` para Retrofit y ApiServices

### 2. **Estructura de estados**
- ✅ `UiState<T>` sealed class (Idle, Loading, Success, Error)

### 3. **DTOs creados**
- ✅ `LoginRequestDto`, `LoginResponseDto`
- ✅ `ProductoDto`, `CategoriaDto`

### 4. **Modelos de dominio**
- ✅ `User`, `Producto`, `Categoria`

### 5. **Mappers**
- ✅ `AuthMapper` (DTO → Domain)
- ✅ `ProductoMapper` (DTO → Domain)

### 6. **Interfaces de repositorio**
- ✅ `AuthRepository`
- ✅ `ProductoRepository`
- ✅ `CategoriaRepository`

## 🔄 Próximos pasos

### Fase 1: Refactorizar Auth (AHORA)
1. ⏳ Actualizar `AuthRepositoryImpl` para usar interfaces
2. ⏳ Crear módulo Hilt para repositorios
3. ⏳ Refactorizar `LoginViewModel` con Hilt
4. ⏳ Actualizar `LoginActivity` para usar Hilt

### Fase 2: Implementar repositorios restantes
5. ⏳ `ProductoRepositoryImpl`
6. ⏳ `CategoriaRepositoryImpl`

### Fase 3: Crear UseCases
7. ⏳ `LoginUseCase`
8. ⏳ `GetProductosUseCase`
9. ⏳ `CreateProductoUseCase`

### Fase 4: Refactorizar UI
10. ⏳ Actualizar fragments con Hilt
11. ⏳ Usar `UiState` en ViewModels
12. ⏳ Eliminar código legacy

## 📝 Principios de Clean Architecture

### Regla de dependencia
```
Presentation → Domain ← Data
```

- **Presentation** depende de **Domain**
- **Data** depende de **Domain**
- **Domain** NO depende de nadie (independiente)

### Flujo de datos
```
UI → ViewModel → UseCase → Repository (Interface) → RepositoryImpl → ApiService → API
                    ↓
                  Domain Models
```

## 🎯 Beneficios

1. **Testeable**: Cada capa se puede testear independientemente
2. **Mantenible**: Código organizado y fácil de entender
3. **Escalable**: Fácil agregar nuevas features
4. **Independiente**: Domain no depende de frameworks
5. **Reutilizable**: Lógica de negocio separada de UI
