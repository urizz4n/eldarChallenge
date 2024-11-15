package com.eldar.challenge.service;

import com.eldar.challenge.Persona;
import com.eldar.challenge.exception.ObjetoDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
    }

    @Test
    void registrarUsuario_exito() {
        // Tomar fecha de nacimiento
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fechaNacimiento = LocalDate.parse("01-01-2000", formatter);

        // Crear un usuario de prueba
        Persona persona = new Persona("Juan", "Perez", "12345678", fechaNacimiento, "juan.perez@example.com");

        // Registrar el usuario
        usuarioService.registrarUsuario(persona);

        // Verificar que el usuario fue registrado
        assertEquals("Juan", usuarioService.buscarPersonaPorDni("12345678").getNombre());
    }

    @Test
    void registrarUsuario_usuarioDuplicado() {
        // Tomar fecha de nacimiento
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fechaNacimiento = LocalDate.parse("01-01-2000", formatter);

        // Crear un usuario de prueba
        Persona persona = new Persona("Juan", "Perez", "12345678", fechaNacimiento, "juan.perez@example.com");

        // Registrar el usuario una vez
        usuarioService.registrarUsuario(persona);

        // Intentar registrar el mismo usuario y verificar que lanza una excepción
        assertThrows(ObjetoDuplicadoException.class, () -> usuarioService.registrarUsuario(persona));
    }
}