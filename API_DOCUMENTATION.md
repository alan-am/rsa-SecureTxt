# API Documentation - RSA File Encryption Service

## Descripción
Esta API proporciona servicios de encriptación y desencriptación de archivos usando el algoritmo RSA. Los usuarios pueden registrarse, generar pares de claves RSA automáticamente, y encriptar/desencriptar archivos de texto.

## Endpoints

### Gestión de Usuarios

#### 1. Registrar Usuario
- **URL**: `POST /api/usuarios/registrar`
- **Descripción**: Registra un nuevo usuario y genera automáticamente un par de claves RSA
- **Body**:
```json
{
    "nombre": "Juan Pérez"
}
```
- **Respuesta**:
```json
{
    "id": 1,
    "nombre": "Juan Pérez",
    "clavePublica": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
}
```

#### 2. Buscar Usuario por ID
- **URL**: `GET /api/usuarios/{id}`
- **Descripción**: Obtiene la información de un usuario específico
- **Respuesta**:
```json
{
    "id": 1,
    "nombre": "Juan Pérez",
    "clavePublica": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
}
```

#### 3. Listar Todos los Usuarios
- **URL**: `GET /api/usuarios`
- **Descripción**: Obtiene la lista de todos los usuarios registrados
- **Respuesta**:
```json
[
    {
        "id": 1,
        "nombre": "Juan Pérez",
        "clavePublica": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
    },
    {
        "id": 2,
        "nombre": "María García",
        "clavePublica": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
    }
]
```

### Encriptación y Desencriptación

#### 4. Encriptar Archivo
- **URL**: `POST /api/rsa/encriptar`
- **Descripción**: Encripta un archivo .txt usando la clave pública del receptor
- **Content-Type**: `multipart/form-data`
- **Parámetros**:
  - `archivo`: Archivo .txt a encriptar
  - `receptorId`: ID del usuario receptor
  - `emisorId`: ID del usuario emisor
- **Respuesta**:
```json
{
    "nombreArchivo": "documento.txt",
    "contenidoCifrado": "base64_encrypted_content...",
    "emisorId": 1,
    "receptorId": 2,
    "mensaje": "Archivo encriptado exitosamente"
}
```

#### 5. Desencriptar Archivo
- **URL**: `POST /api/rsa/desencriptar`
- **Descripción**: Desencripta un archivo usando la clave privada del usuario
- **Content-Type**: `application/x-www-form-urlencoded`
- **Parámetros**:
  - `contenidoCifrado`: Contenido cifrado en Base64
  - `nombreArchivo`: Nombre del archivo original
  - `usuarioId`: ID del usuario que desencripta
- **Respuesta**:
```json
{
    "nombreArchivo": "documento.txt",
    "contenidoDescifrado": "Contenido original del archivo...",
    "usuarioId": 2,
    "mensaje": "Archivo desencriptado exitosamente"
}
```

## Códigos de Estado HTTP

- `200 OK`: Operación exitosa
- `201 Created`: Usuario registrado exitosamente
- `400 Bad Request`: Error en los parámetros de entrada
- `404 Not Found`: Usuario no encontrado
- `500 Internal Server Error`: Error interno del servidor

## Manejo de Errores

Todos los errores devuelven un objeto JSON con el siguiente formato:
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 404,
    "error": "Usuario no encontrado",
    "message": "Usuario no encontrado con ID: 999",
    "path": "uri=/api/usuarios/999"
}
```

## Configuración de Base de Datos

La aplicación requiere las siguientes variables de entorno:
- `DB_HOST`: Host de la base de datos MySQL
- `DB_PORT`: Puerto de la base de datos MySQL
- `DB_NAME`: Nombre de la base de datos
- `DB_USER`: Usuario de la base de datos
- `DB_PASSWORD`: Contraseña de la base de datos

## Notas Importantes

1. Solo se aceptan archivos con extensión `.txt`
2. Las claves RSA se generan automáticamente con 2048 bits
3. La clave privada nunca se expone en las respuestas de la API por seguridad
4. Los archivos se encriptan en bloques debido a las limitaciones del algoritmo RSA
5. El tamaño máximo de archivo soportado es 10MB