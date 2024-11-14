# Fase de compilación
FROM maven:3.8.4-openjdk-17 AS build

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo pom.xml y descargar las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente de la aplicación
COPY src ./src

# Compilar el proyecto y generar el archivo JAR
RUN mvn clean package -DskipTests

# Fase de ejecución
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR desde la fase de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto 8080 (puerto por defecto de Spring Boot)
EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]