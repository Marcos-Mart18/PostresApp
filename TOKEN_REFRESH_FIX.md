# Fix del Bucle de Error 401 - Token Refresh

## Problema Identificado

La aplicación estaba atrapada en un bucle de error de autenticación (HTTP 401) debido a:

1. **Falta de Authenticator**: OkHttp solo tenía un interceptor básico que añadía el token, pero no un `Authenticator` que manejara automáticamente la renovación cuando recibía un 401.

2. **Manejo Manual Problemático**: El código manejaba manualmente el refresh del token en cada `catch` de 401, creando un bucle donde:
   - Se enviaba la petición con token expirado
   - Se recibía 401
   - Se renovaba el token exitosamente
   - OkHttp reintentaba automáticamente con el token VIEJO
   - Se recibía 401 nuevamente

3. **Problemas con Multipart**: Las peticiones con archivos (RequestBody) no podían ser reintentadas porque el InputStream ya se había leído.

## Solución Implementada

### 1. TokenAuthenticator
**Archivo**: `data/remote/interceptor/TokenAuthenticator.kt`

- Implementa `okhttp3.Authenticator` para manejar automáticamente los 401
- Usa `ReentrantLock` para evitar condiciones de carrera
- Verifica si el token ya fue actualizado por otro hilo
- Renueva el token de forma síncrona usando `runBlocking`
- Limpia los tokens si el refresh token también expiró

### 2. AuthInterceptor Mejorado
**Archivo**: `data/remote/interceptor/AuthInterceptor.kt`

- Interceptor dedicado solo a añadir el token
- Logs mejorados para debugging
- Separación de responsabilidades

### 3. ReusableRequestBody
**Archivo**: `data/remote/utils/ReusableRequestBody.kt`

- RequestBody que puede ser leído múltiples veces
- Soluciona el problema de reintentos con archivos
- Carga el archivo en memoria una sola vez

### 4. ServiceLocator Actualizado
**Archivo**: `di/ServiceLocator.kt`

- Integra el nuevo `TokenAuthenticator`
- Usa el `AuthInterceptor` mejorado
- Configuración limpia y mantenible

### 5. CatalogoAdminFragment Refactorizado
**Archivo**: `presentation/ui/fragment/admin/CatalogoAdminFragment.kt`

- Eliminado el manejo manual de refresh de tokens
- Usa `ReusableRequestBody` para archivos
- Manejo de errores simplificado y consistente
- Método `handleSessionExpired()` centralizado

### 6. PrefsManager Mejorado
**Archivo**: `data/local/PrefsManager.kt`

- Añadido método `clearTokens()` específico
- Mejor separación de responsabilidades

## Flujo Corregido

### Antes (Problemático):
1. Petición con token expirado → 401
2. Catch manual renueva token
3. OkHttp reintenta con token viejo → 401 (bucle)

### Ahora (Correcto):
1. Petición con token expirado → 401
2. `TokenAuthenticator` detecta 401 automáticamente
3. Renueva token de forma thread-safe
4. Retorna nueva Request con token actualizado
5. OkHttp reintenta con token nuevo → 200 ✅

## Beneficios

- ✅ **Eliminado el bucle de 401**: El Authenticator maneja correctamente los reintentos
- ✅ **Thread-safe**: ReentrantLock evita condiciones de carrera
- ✅ **Multipart funcional**: ReusableRequestBody permite reintentos con archivos
- ✅ **Código más limpio**: Eliminado manejo manual duplicado
- ✅ **Logs mejorados**: Mejor debugging y monitoreo
- ✅ **Manejo centralizado**: Una sola lógica de refresh para toda la app

## Pruebas Recomendadas

1. **Crear producto con imagen**: Verificar que funciona sin bucles 401
2. **Token expirado**: Simular expiración y verificar refresh automático
3. **Refresh token expirado**: Verificar logout automático
4. **Sin conexión**: Verificar manejo de errores de red
5. **Múltiples peticiones simultáneas**: Verificar thread-safety

## Archivos Modificados

- ✅ `TokenAuthenticator.kt` (nuevo)
- ✅ `AuthInterceptor.kt` (nuevo)  
- ✅ `ReusableRequestBody.kt` (nuevo)
- ✅ `ServiceLocator.kt` (actualizado)
- ✅ `CatalogoAdminFragment.kt` (refactorizado)
- ✅ `PrefsManager.kt` (mejorado)

El sistema ahora maneja automáticamente la renovación de tokens sin intervención manual, eliminando completamente el bucle de error 401.