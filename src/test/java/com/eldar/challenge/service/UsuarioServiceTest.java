package com.eldar.challenge.service;

import com.eldar.challenge.Persona;
import com.eldar.challenge.exception.ObjetoDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
    }

    @Test
    void registrarUsuario_exito() {
        // Crear un usuario de prueba
        Persona persona = new Persona("Juan", "Perez", "12345678", null, "juan.perez@example.com");

        // Registrar el usuario
        usuarioService.registrarUsuario(persona);

        // Verificar que el usuario fue registrado
        assertTrue(usuarioService.buscarPersonaPorDni("12345678").getNombre().equals("Juan"));
    }

    @Test
    void registrarUsuario_usuarioDuplicado() {
        // Crear un usuario de prueba
        Persona persona = new Persona("Juan", "Perez", "12345678", null, "juan.perez@example.com");

        // Registrar el usuario una vez
        usuarioService.registrarUsuario(persona);

        // Intentar registrar el mismo usuario y verificar que lanza una excepción
        assertThrows(ObjetoDuplicadoException.class, () -> usuarioService.registrarUsuario(persona));
    }
}