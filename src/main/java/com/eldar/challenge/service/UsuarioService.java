package com.eldar.challenge.service;

import com.eldar.challenge.Persona;
import com.eldar.challenge.exception.CampoVacioException;
import com.eldar.challenge.exception.FormatoInvalidoException;
import com.eldar.challenge.exception.PersonaNoEncontradaException;
import com.eldar.challenge.exception.ObjetoDuplicadoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private List<Persona> personas = new ArrayList<>();

    public Persona buscarPersonaPorDni(String dni) {
        return personas.stream()
                .filter(p -> p.getDni().equals(dni))
                .findFirst()
                .orElseThrow(() -> new PersonaNoEncontradaException("No se encontró un usuario con el DNI especificado."));
    }

    public void registrarUsuario(Persona persona) {
        if (personas.stream().anyMatch(p -> p.getDni().equals(persona.getDni()))) {
            throw new ObjetoDuplicadoException("Ya existe un usuario con el mismo DNI.");
        }
        personas.add(persona);
    }

    public void eliminarUsuario(String dni) {
        Persona persona = buscarPersonaPorDni(dni);
        personas.remove(persona);
    }

    public Persona modificarUsuario(String dni,
                                    Optional<String> nombre,
                                    Optional<String> apellido,
                                    Optional<String> nuevoDni,
                                    Optional<String> fechaNacimientoStr,
                                    Optional<String> email
                                    ) {

        List<Optional<String>> variables = Arrays.asList(nombre,apellido,nuevoDni,fechaNacimientoStr,email);
        boolean allEmpty = variables.stream().allMatch(Optional::isEmpty);
        if (allEmpty) throw new CampoVacioException("No se realizaron cambios debido a campos vacíos o mal declarados");

        Persona persona = buscarPersonaPorDni(dni);
        Optional<LocalDate> fechaNacimiento = Optional.empty();
        if (fechaNacimientoStr.isPresent()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                fechaNacimiento = Optional.of(LocalDate.parse(fechaNacimientoStr.get(), formatter));
            } catch (DateTimeParseException e) {
                throw new FormatoInvalidoException("Fecha no válida. Intente de nuevo.");
            }
        }

        comprobarCambiosPersona(nombre, apellido, nuevoDni, email, fechaNacimiento);

        realizarCambiosPersona(nombre, apellido, nuevoDni, email, fechaNacimiento, persona);

        return persona;
    }

    private static void realizarCambiosPersona(Optional<String> nombre, Optional<String> apellido, Optional<String> nuevoDni, Optional<String> email, Optional<LocalDate> fechaNacimiento, Persona persona) {
        fechaNacimiento.ifPresent(persona::setFechaNacimiento);
        nombre.ifPresent(persona::setNombre);
        apellido.ifPresent(persona::setApellido);
        email.ifPresent(persona::setEmail);
        nuevoDni.ifPresent(persona::setDni);
    }

    private static void comprobarCambiosPersona(Optional<String> nombre, Optional<String> apellido, Optional<String> nuevoDni, Optional<String> email, Optional<LocalDate> fechaNacimiento) {
        nombre.ifPresent(Persona::validarNombre);
        apellido.ifPresent(Persona::validarNombre);
        nuevoDni.ifPresent(Persona::validarDni);
        email.ifPresent(Persona::validarEmail);
        fechaNacimiento.ifPresent(Persona::validarFechaNacimiento);
    }


}
