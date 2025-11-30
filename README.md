# WonderlandApp – Backend

Este proyecto corresponde al backend de WonderlandApp, desarrollado para una evaluación académica.
Incluye la gestión de usuarios, empleados, productos, pedidos, autenticación y mensajes de contacto.
El backend está construido con Spring Boot y expone una API REST para ser consumida por el frontend.

## Tecnologías utilizadas

- Java 17 o superior  
- Spring Boot  
  - Spring Web  
  - Spring Data JPA  
  - Spring Security (solo para login)  
- Maven  
- MySQL  
- Visual Studio Code como entorno principal de desarrollo  

## Estructura general

src/
├── main/
│   ├── java/com/wonderland/WonderlandApp/
│   │   ├── controller/      # Controladores REST
│   │   ├── entity/          # Entidades JPA
│   │   ├── repository/      # Repositorios JPA
│   │   ├── service/         # Servicios y lógica de negocio
│   │   └── config/          # Configuración (CORS, seguridad, etc.)
│   └── resources/           # application.properties, application-prod.properties, etc.



## Ejecución del proyecto

Para ejecutar directamente desde consola:

mvn spring-boot:run


## Configuración necesaria

El proyecto incluye un archivo de configuración para producción:

- `application-prod.properties` (usado al desplegar en Render)

Las credenciales y valores sensibles (como contraseñas o claves JWT) se manejan mediante variables de entorno en Render.

Para ejecutar el proyecto en un entorno local, se debe crear manualmente un archivo de configuración (`application.properties`) que no está incluido en el repositorio.

Ejemplo mínimo de configuración local:

spring.datasource.url=jdbc:mysql://localhost:3306/db_wonderland?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop

## Endpoints principales

La API incluye controladores para las siguientes áreas:

- Autenticación  
- Usuarios  
- Empleados  
- Productos  
- Pedidos  
- Detalle de pedidos  
- Mensajes de contacto  

Cada controlador expone sus rutas REST correspondientes.

## Nota final

Este proyecto forma parte de una evaluación académica. El backend sigue una arquitectura por capas propia de Spring Boot, separando controladores, servicios, repositorios y entidades.

