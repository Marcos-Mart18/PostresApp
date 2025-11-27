# Fixes Aplicados - Productos CRUD

## ✅ Problema del Spinner Resuelto
**Antes**: Mostraba `Categoria(idCategoria=1, nombre=Tartas)`
**Después**: Muestra solo `Tartas`

**Solución**: Creado `CategoriaSpinnerAdapter` personalizado que sobrescribe:
- `getView()` - Para mostrar solo el nombre en el spinner cerrado
- `getDropDownView()` - Para mostrar solo el nombre en el dropdown

## ✅ Error 401 Resuelto
**Problema**: Las peticiones de productos retornaban 401 Unauthorized

**Solución**: Agregado token de autenticación a los servicios API:
- `productApiService` ahora incluye interceptor con Bearer token
- `categoriaApiService` ahora incluye interceptor con Bearer token
- Token obtenido desde `PrefsManager.getAccessToken()`

## ✅ Color de Fuente en Inputs
**Problema**: Texto invisible o difícil de leer en los inputs

**Solución**: Agregado a todos los EditText:
```xml
android:textColor="@color/black"
android:textColorHint="@color/gris"
```

Aplicado a:
- `etNombre` - Input de nombre
- `etPrecio` - Input de precio
- `etDescripcion` - Input de descripción

## ✅ Endpoint Correcto
**Confirmado**: Usando `POST /api/v1/productos/createWithImage`
- Envía `producto` como RequestBody (JSON)
- Envía `file` como MultipartBody.Part (imagen)

## Archivos Modificados
1. `CategoriaSpinnerAdapter.kt` - Nuevo adapter personalizado
2. `CatalogoAdminFragment.kt` - Servicios con autenticación
3. `fragment_catalogo_admin.xml` - Colores de texto en inputs
4. `ServiceLocator.kt` - Métodos con contexto para autenticación

## Estado Actual
✅ Spinner muestra nombres correctamente
✅ Peticiones autenticadas con token Bearer
✅ Inputs con texto visible (negro sobre fondo claro)
✅ CRUD completo funcional (Crear, Leer, Actualizar, Eliminar)
