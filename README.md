# Challenge Java Backend
Este proyecto es una solución a los ejercicios 1 y 2 del **Challenge Java Backend**, implementado con **Spring Boot, Maven** y siguiendo buenas prácticas de desarrollo como validaciones, manejo de excepciones, y uso de controladores REST.

## Tecnologías y Herramientas
- **Java 17**
- **Spring Boot 3** (Spring Web)
- **Maven**
- **Swagger UI**
- **JUnit** y **Mockito** (para tests unitarios)

---
# Ejercicio 1: Menú Interactivo para Usuarios y Tarjetas

## Descripción
El objetivo fue crear un menú interactivo en el que los usuarios puedan realizar operaciones relacionadas con la gestión de usuarios y tarjetas.

## Funcionalidades Implementadas
- Registrar usuarios con nombre, apellido, DNI, email y fecha de nacimiento.
- Registrar tarjetas con validación de duplicados y generación de datos aleatorios (número de tarjeta, CVV y fecha de vencimiento).
- Consultar tarjetas asociadas a un usuario por DNI.
- Realizar compras con validaciones como CVV y monto límite.
- Modificar y eliminar usuarios, actualizando o eliminando sus tarjetas asociadas automáticamente.

## Validaciones

### Usuarios:
- Nombre y apellido no vacíos y solo letras (incluyendo tildes).
- DNI con exactamente 8 dígitos.
- Email con formato válido.

### Tarjetas:
- Número de tarjeta con exactamente 16 dígitos.
- CVV con exactamente 3 dígitos.
- Marca válida (VISA, NARA, AMEX).

---

# Ejercicio 2: Exposición de Servicios REST

## Descripción
Se desarrollaron endpoints REST para gestionar las operaciones definidas en el menú del ejercicio 1, permitiendo interactuar con usuarios y tarjetas mediante solicitudes HTTP.

## Endpoints Principales

### Usuarios
- **Registrar Usuario**:  
  `POST /api/usuarios`  
  Recibe un objeto JSON con los datos del usuario y lo registra. Valida duplicados por DNI.

- **Modificar Usuario**:  
  `PUT /api/usuarios/{dni}`  
  Actualiza los datos de un usuario existente, propagando los cambios a las tarjetas asociadas.

- **Eliminar Usuario**:  
  `DELETE /api/usuarios/{dni}`  
  Elimina un usuario y todas sus tarjetas asociadas.

### Tarjetas
- **Registrar Tarjeta**:  
  `POST /api/tarjetas`  
  Registra una nueva tarjeta para un usuario, generando número, CVV y fecha de vencimiento.

- **Consultar Tarjetas**:  
  `GET /api/tarjetas/{dni}`  
  Obtiene todas las tarjetas asociadas a un DNI.

- **Eliminar Tarjeta**:  
  `DELETE /api/tarjetas/{numero}`  
  Elimina una tarjeta específica por su número.

- **Realizar Compra**:  
  `POST /api/compra`  
  Procesa una compra validando el CVV, el monto y el número de la tarjeta.

### Extra
- **Consultar Tasa**:  
  `GET /api/tasa`  
  Calcula la tasa según la marca de la tarjeta:  
  - VISA: Año dividido por mes.  
  - NARA: Día del mes multiplicado por 0.5.  
  - AMEX: Mes multiplicado por 0.1.

## Manejo de Excepciones
Se implementó un manejo centralizado de errores para devolver respuestas claras y consistentes:
- **404 Not Found**: Para usuarios o tarjetas no encontrados.
- **400 Bad Request**: Para validaciones fallidas.
- **409 Conflict**: Para casos de duplicados, como un usuario ya registrado.

---

# Cómo Ejecutar el Proyecto

## Prerrequisitos
- **Java 17** o superior.
- **Maven**.
- **Postman** o cualquier cliente HTTP para probar los endpoints.

## Pasos

### 1. Clonar el repositorio:
`git clone https://github.com/urizz4n/eldarChallenge.git`

`cd eldarChallenge`

### 2. Compilar el proyecto:
`mvn clean package`

### 3. Ejecutar la aplicación:
`mvn spring-boot:run`

### 4. Acceder a los endpoints:
Base URL: `http://localhost:8080/api`

# Ejemplo de Uso
### Registro de Usuario
Solicitud:

```
POST /api/usuarios
Content-Type: application/json

{
    "nombre": "Juan",
    "apellido": "Pérez",
    "dni": "12345678",
    "email": "juan.perez@example.com",
    "fechaNacimiento": "1990-01-01"
} 
```

Respuesta:
```
201 Created
Usuario registrado exitosamente
```

### Registro de Tarjeta
Solicitud:
```
POST /api/tarjetas?dni=12345678&marca=VISA
```

Respuesta:
```
201 Created
Tarjeta registrada exitosamente
```

## Tests Implementados
### Registro de Usuario:
- Verifica que un usuario se registre correctamente.
- Valida que no se puedan registrar usuarios duplicados.

### Cálculo de Tasa:
- Comprueba que la tasa se calcule correctamente para las marcas VISA, NARA y AMEX.

Ejecución de tests:
`mvn test`

---

## Javadoc y Swagger
### Javadoc
- La ruta para entrar al **Javadoc** es `../eldarExercise/challenge/docs/index.html`

### Swagger
- La ruta para **Swagger UI** es `http://localhost:8080/swagger-ui/index.html#/`