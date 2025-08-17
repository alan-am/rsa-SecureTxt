# Proyecto RSA ESPOL

Este es un proyecto prototipo para una web que encripta y desencripta archivos usando el algoritmo RSA para usuarios en la base de datos.

## Características

- Generación automática de pares de claves RSA (2048 bits)
- Encriptación de archivos .txt usando claves públicas
- Desencriptación de archivos usando claves privadas
- Gestión de usuarios con claves únicas
- API REST completa con manejo de excepciones

## Tecnologías Utilizadas

- Java 21
- Spring Boot 3.5.4
- Spring Data JPA
- MySQL
- Lombok
- Maven

## Configuración de la Base de Datos

1. Asegúrate de tener MySQL instalado y ejecutándose
2. La aplicación creará automáticamente la base de datos `espol_rsa` si no existe
3. Las tablas se crearán automáticamente gracias a `spring.jpa.hibernate.ddl-auto=update`

## Configuración

1. Modifica `src/main/resources/application.properties` con tus credenciales de MySQL
2. Por defecto:
   - Host: localhost
   - Puerto: 3306
   - Base de datos: espol_rsa
   - Usuario: root
   - Contraseña: (vacía)

## Ejecución

1. Clona el repositorio
2. Navega al directorio del proyecto
3. Ejecuta: `mvn spring-boot:run`
4. La aplicación estará disponible en `http://localhost:8080`

## Endpoints de la API

### Base URL: `http://localhost:8080/api/rsa`

#### 1. Crear Usuario
- **POST** `/usuarios`
- **Parámetros**: `nombre` (String)
- **Respuesta**: UsuarioDTO con ID, nombre y clave pública
- **Descripción**: Crea un nuevo usuario y genera automáticamente un par de claves RSA

#### 2. Buscar Usuario por ID
- **GET** `/usuarios/{id}`
- **Parámetros**: `id` (Long) en la URL
- **Respuesta**: UsuarioDTO del usuario encontrado
- **Descripción**: Busca un usuario específico por su ID

#### 3. Listar Todos los Usuarios
- **GET** `/usuarios`
- **Respuesta**: Lista de UsuarioDTO
- **Descripción**: Obtiene todos los usuarios registrados

#### 4. Encriptar Archivo
- **POST** `/encriptar`
- **Parámetros**: 
  - `archivo` (MultipartFile) - Archivo .txt a encriptar
  - `destinatarioId` (Long) - ID del usuario destinatario
- **Respuesta**: ArchivoEncriptadoDTO con el contenido encriptado
- **Descripción**: Encripta un archivo .txt usando la clave pública del destinatario

#### 5. Desencriptar Archivo
- **POST** `/desencriptar`
- **Parámetros**:
  - `archivo` (MultipartFile) - Archivo .txt encriptado
  - `usuarioId` (Long) - ID del usuario que desencripta
- **Respuesta**: ArchivoDesencriptadoDTO con el contenido desencriptado
- **Descripción**: Desencripta un archivo usando la clave privada del usuario

#### 6. Generar Claves (Pruebas)
- **POST** `/generar-claves`
- **Respuesta**: Mensaje de confirmación
- **Descripción**: Genera un nuevo par de claves RSA (para pruebas)

## Estructura del Proyecto

```
src/main/java/com/espol/
├── controller/
│   └── RSAController.java          # Controlador principal con todos los endpoints
├── entity/
│   ├── Usuario.java                # Entidad de usuario
│   └── RegistroArchivo.java        # Entidad para registro de archivos
├── repository/
│   ├── UsuarioRepositorio.java     # Repositorio de usuarios
│   └── ArchivoRepositorio.java     # Repositorio de archivos
├── service/
│   ├── RSAService.java             # Servicio principal de RSA
│   └── estrategia/
│       ├── EstrategiaEncriptacion.java  # Interfaz de estrategia
│       └── RSAChatGPT.java         # Implementación de RSA
├── dto/
│   ├── UsuarioDTO.java             # DTO para respuestas de usuario
│   ├── ArchivoEncriptadoDTO.java   # DTO para archivos encriptados
│   └── ArchivoDesencriptadoDTO.java # DTO para archivos desencriptados
├── exception/
│   ├── GlobalExceptionHandler.java  # Manejador global de excepciones
│   ├── UsuarioNotFoundException.java # Excepción para usuario no encontrado
│   └── ArchivoException.java       # Excepción para errores de archivo
└── config/
    └── FileUploadConfig.java       # Configuración de archivos
```

## Seguridad

- Las claves privadas nunca se devuelven en las respuestas de la API
- Solo se muestran las claves públicas para encriptación
- Las claves se almacenan en la base de datos de forma segura
- Validación de tipos de archivo (solo .txt)

## Ejemplos de Uso

### Crear un Usuario
```bash
curl -X POST "http://localhost:8080/api/rsa/usuarios" \
  -F "nombre=Juan Pérez"
```

### Encriptar un Archivo
```bash
curl -X POST "http://localhost:8080/api/rsa/encriptar" \
  -F "archivo=@documento.txt" \
  -F "destinatarioId=1"
```

### Desencriptar un Archivo
```bash
curl -X POST "http://localhost:8080/api/rsa/desencriptar" \
  -F "archivo=@documento_encriptado.txt" \
  -F "usuarioId=1"
```

## Notas Importantes

- Solo se aceptan archivos .txt
- El tamaño máximo de archivo es 10MB
- Las claves RSA son de 2048 bits para mayor seguridad
- La aplicación maneja automáticamente la limpieza de archivos temporales
- Todas las operaciones son asíncronas y seguras

## Solución de Problemas

### Error de Conexión a Base de Datos
- Verifica que MySQL esté ejecutándose
- Confirma las credenciales en `application.properties`
- Asegúrate de que el puerto 3306 esté disponible

### Error de Archivo
- Verifica que el archivo sea .txt
- Confirma que el tamaño no exceda 10MB
- Asegúrate de que el archivo no esté corrupto

### Error de Usuario
- Verifica que el ID del usuario exista
- Confirma que el usuario tenga claves válidas

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

## Licencia

Este proyecto es parte del curso de Matemáticas Discretas de ESPOL.
