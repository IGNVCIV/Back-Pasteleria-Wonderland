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
│ ├── java/com/wonderland/WonderlandApp/
│ │ ├── controller/ # Controladores REST
│ │ ├── entity/ # Entidades JPA
│ │ ├── repository/ # Repositorios JPA
│ │ ├── service/ # Servicios y reglas de negocio
│ │ └── config/ # Configuración general (CORS, seguridad, etc.)
│ └── resources/ # application.properties, data.sql, etc.


## Ejecución del proyecto

Para ejecutar directamente desde consola:

mvn spring-boot:run


## Configuración necesaria

El archivo `application.properties` no se incluye en el repositorio por motivos de seguridad.  
Debe crearse manualmente con la configuración de la base de datos.  
Ejemplo mínimo:

spring.datasource.url=jdbc:mysql://localhost:3306/wonderland
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update


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

