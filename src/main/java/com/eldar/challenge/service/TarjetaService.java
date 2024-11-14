package com.eldar.challenge.service;

import com.eldar.challenge.Persona;
import com.eldar.challenge.Tarjeta;
import com.eldar.challenge.exception.CampoVacioException;
import com.eldar.challenge.exception.FormatoInvalidoException;
import com.eldar.challenge.exception.ObjetoDuplicadoException;
import com.eldar.challenge.exception.TarjetaNoEncontradaException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class TarjetaService {

    private List<Tarjeta> tarjetas = new ArrayList<>();
    private static final Set<String> numerosGenerados = new HashSet<>();
    private static final Random random = new Random();


    public List<Tarjeta> buscarTarjetasPorDni(String dni) {
        List<Tarjeta> tarjetasEncontradas = tarjetas.stream()
                .filter(t -> t.getTitular().getDni().equals(dni))
                .toList();

        if (tarjetasEncontradas.isEmpty()) {
            throw new TarjetaNoEncontradaException("No se encontraron tarjetas asociadas al DNI especificado.");
        }

        return tarjetasEncontradas;
    }

    public Tarjeta buscarTarjetaPorNumero(String numeroTarjeta) {
        return tarjetas.stream()
                .filter(t -> t.getNumero().equals(numeroTarjeta))
                .findFirst()
                .orElseThrow(() -> new TarjetaNoEncontradaException("No se encontró una tarjeta con el número especificado."));
    }

    public void verificarTarjetaDuplicada(String dni, String marca) {
        if (tarjetas.stream().anyMatch(t -> t.getMarca().equals(marca) && t.getTitular().getDni().equals(dni))) {
            throw new ObjetoDuplicadoException("Ya existe una tarjeta con la misma marca y DNI.");
        }
    }

    public Tarjeta crearTarjetaAleatoria(String marca, Persona titular) {
        String numeroTarjeta;
        // Genera numeros hasta generar una combinacion única
        do {
            numeroTarjeta = String.format("%04d%04d%04d%04d", random.nextInt(10000), random.nextInt(10000),
                    random.nextInt(10000), random.nextInt(10000));
        } while (numerosGenerados.contains(numeroTarjeta));
        numerosGenerados.add(numeroTarjeta);
        // Genera tres numeros aleatorios
        String cvv = String.format("%03d", random.nextInt(1000));
        // Genera la fecha de vencimiento de la tarjeta
        LocalDate fechaVencimiento = LocalDate.of(
                LocalDate.now().getYear() + random.nextInt(5) + 1, // Año entre el actual y +5
                1 + random.nextInt(12),
                1
        ).with(TemporalAdjusters.lastDayOfMonth());

        Tarjeta tarjeta = new Tarjeta(marca, numeroTarjeta, fechaVencimiento, titular, cvv);
        tarjetas.add(tarjeta);
        return tarjeta;
    }

    public void eliminarTarjetasPorDni(String dni) {
        tarjetas.removeIf(t -> t.getTitular().getDni().equals(dni));
    }

    public void eliminarTarjetaPorNumero(String numero) {
        Tarjeta tarjeta = tarjetas.stream()
                .filter(t -> t.getNumero().equals(numero))
                .findFirst()
                .orElseThrow(() -> new TarjetaNoEncontradaException("No se encontró una tarjeta con el número especificado."));
        tarjetas.remove(tarjeta);
    }

    public void actualizarTitularDeTarjetas(String dni,Persona nuevoTitular) {
        tarjetas.stream()
                .filter(t -> t.getTitular().getDni().equals(dni))
                .forEach(t -> {
                    t.setTitular(nuevoTitular);
                });
    }

    public void modificarTarjeta(String numero,
                                 Optional<String> marca,
                                 Optional<String> fechaVencimientoStr,
                                 Optional<Persona> titular,
                                 Optional<String> numeroNuevo,
                                 Optional<String> cvv) {

        List<Optional<?>> variables = Arrays.asList(marca,fechaVencimientoStr,numeroNuevo,cvv,titular);
        boolean todasVacias = variables.stream().allMatch(Optional::isEmpty);
        if (todasVacias) throw new CampoVacioException("No se realizaron cambios debido a campos vacíos o mal declarados");

        // Busca la tarjeta
        Tarjeta tarjeta = tarjetas.stream()
                .filter(t -> t.getNumero().equals(numero))
                .findFirst()
                .orElseThrow(() -> new TarjetaNoEncontradaException("No se encontró una tarjeta con el número especificado."));

        comprobarCambiosTarjeta(marca, fechaVencimientoStr, titular, numeroNuevo, cvv);

        realizarCambiosTarjeta(marca, fechaVencimientoStr, titular, numeroNuevo, cvv, tarjeta);
    }

    private static void comprobarCambiosTarjeta(Optional<String> marca, Optional<String> fechaVencimientoStr, Optional<Persona> titular, Optional<String> numeroNuevo, Optional<String> cvv) {
        marca.ifPresent(Tarjeta::validarMarca);
        fechaVencimientoStr.ifPresent(TarjetaService::validarFechaVencimiento);
        titular.ifPresent(Tarjeta::validarTitular);
        numeroNuevo.ifPresent(Tarjeta::validarNumero);
        cvv.ifPresent(Tarjeta::validarCvv);
    }

    private static void realizarCambiosTarjeta(Optional<String> marca, Optional<String> fechaVencimientoStr, Optional<Persona> titular, Optional<String> numeroNuevo, Optional<String> cvv, Tarjeta tarjeta) {
        marca.ifPresent(tarjeta::setMarca);
        if (fechaVencimientoStr.isPresent()) {
            LocalDate fechaVencimiento = validarFechaVencimiento(fechaVencimientoStr.get());
            tarjeta.setFechaVencimiento(fechaVencimiento);
        }
        titular.ifPresent(tarjeta::setTitular);
        numeroNuevo.ifPresent(tarjeta::setNumero);
        cvv.ifPresent(tarjeta::setCvv);
    }

    private static LocalDate validarFechaVencimiento(String fechaVencimientoStr) {
        LocalDate fechaVencimiento;

        try {
            fechaVencimiento = LocalDate.parse(fechaVencimientoStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            throw new FormatoInvalidoException("Fecha de vencimiento no válida. Use formato dd-MM-yyyy");
        }
        // Validar que la tarjeta no esté vencida
        if (fechaVencimiento.isBefore(LocalDate.now())) {
            throw new FormatoInvalidoException("La tarjeta está vencida y no puede ser registrada.");
        }
        return fechaVencimiento;
    }

    public void validarCvv(Tarjeta tarjeta, String cvv) {
        if (!tarjeta.getCvv().equals(cvv)) {
            throw new TarjetaNoEncontradaException("CVV incorrecto.");
        }
    }

    public void validarMonto(double monto) {
        if (monto >= 10000) {
            throw new FormatoInvalidoException("El monto excede el límite permitido para operaciones.");
        } else if (monto <= 0) {
            throw new FormatoInvalidoException("El monto no puede ser negativo ni 0");
        }
    }

    public double consultarTasa(String marca, String fechaStr) {
        LocalDate fecha;
        if (fechaStr.isEmpty()) {
            fecha = LocalDate.now();
        } else {
            try {
                fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (DateTimeParseException e) {
                throw new FormatoInvalidoException("Fecha no válida. Usa el formato dd-MM-yyyy.");
            }
        }
        return switch (marca) {
            case "VISA" -> (double) Math.max(0.3, Math.min((double)(fecha.getYear() % 100) / fecha.getMonthValue(), 5));
            case "NARA" -> Math.max(0.3, Math.min(fecha.getDayOfMonth() * 0.5, 5));
            case "AMEX" -> Math.max(0.3, Math.min(fecha.getMonthValue() * 0.1, 5));
            default -> throw new FormatoInvalidoException("Marca de tarjeta no reconocida.");
        };
    }
}