package com.eldar.challenge;

import com.eldar.challenge.exception.CampoVacioException;
import com.eldar.challenge.exception.FormatoInvalidoException;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;


public class Persona {
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaNacimiento;
    private String email;

    public Persona(String nombre, String apellido, String dni, LocalDate fechaNacimiento, String email) {

        validarNombre(nombre);
        validarNombre(apellido);
        validarDni(dni);
        validarEmail(email);
        validarFechaNacimiento(fechaNacimiento);

        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;

    }

    public static void validarEmail(String email) {
        if (email == null || email.isEmpty()) throw new CampoVacioException("El email no puede estar vacío.");
        if (!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email))
            throw new FormatoInvalidoException("El email tiene un formato incorrecto.");
    }

    public static void validarDni(String dni) {
        if (dni == null || dni.isEmpty()) throw new CampoVacioException("El DNI no puede estar vacío.");
        if (!dni.matches("\\d{8}")) throw new FormatoInvalidoException("El DNI debe tener 8 dígitos.");
    }

    public static void validarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) throw new CampoVacioException("El nombre/apellido no puede estar vacío.");
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) throw new FormatoInvalidoException("El nombre/apellido solo debe contener letras y espacios.");
    }

    public static void validarFechaNacimiento(LocalDate fechaVencimiento) {

        // Validar que la tarjeta no esté vencida
        if (fechaVencimiento.isAfter(LocalDate.now().minusYears(16))) {
            throw new FormatoInvalidoException("La persona es muy joven para tener tarjeta de crédito.");
        }
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDni() { return dni; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getEmail() { return email; }

    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        validarNombre(apellido);
        this.apellido = apellido;
    }

    public void setDni(String dni) {
        validarDni(dni);
        this.dni = dni;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setEmail(String email) {
        validarEmail(email);
        this.email = email;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", email='" + email + '\'' +
                '}';
    }
}