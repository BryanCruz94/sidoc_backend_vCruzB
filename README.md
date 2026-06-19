# SIDOC Backend Spring Boot

API principal de SIDOC desarrollada con Spring Boot. Gestiona tipos, categorías, subcategorías y manuales; permite cargar, consultar y descargar documentos, procesa archivos PDF y portadas, y protege las operaciones mediante JWT de Auth0 y control de roles.

## Arquitectura básica

El proyecto sigue una arquitectura por capas:

- `controller`: endpoints REST.
- `service`: reglas de negocio, procesamiento de PDF y acceso FTP.
- `repository`: persistencia mediante Spring Data JPA.
- `model`: entidades del dominio almacenadas en MySQL.
- `config`: seguridad Auth0, CORS, caché y configuración auxiliar.

Flujo principal: `Frontend Angular -> controladores/Spring Security -> servicios -> JPA/MySQL y servidor FTP`.

## Requisitos y configuración

- Java 17.
- MySQL 8 o compatible.
- Un servidor FTP accesible para documentos y portadas.

La aplicación se configura con variables de entorno. Las principales son:

```text
PORT=8080
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/sidoc?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=change_me
FTP_HOST=127.0.0.1
FTP_PORT=21
FTP_USER=user
FTP_PASSWORD=change_me
FTP_PASSIVE=true
FTP_RUTA_ARCHIVOS=/Reportes-Doctrina/manuales/archivos/
FTP_RUTA_PORTADAS=/Reportes-Doctrina/manuales/portadas/
OPENAI_API_KEY=change_me
OPENAI_MODEL=gpt-4o-mini
ISSUER_URI=https://tu-tenant.auth0.com/
JWKS_URI=https://tu-tenant.auth0.com/.well-known/jwks.json
ALLOWED_ORIGINS=http://localhost:4200
```

## Desarrollo

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

La API queda disponible por defecto en `http://localhost:8080/cedmt/sidoc`.

## Producción

Configure las variables de entorno de producción y genere el archivo ejecutable:

```powershell
.\mvnw.cmd clean package -DskipTests -Pproduction
java -jar target/SIDOC-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

En Linux/macOS reemplace `.\mvnw.cmd` por `./mvnw`. El puerto puede ser asignado mediante la variable `PORT` de la plataforma de despliegue.
