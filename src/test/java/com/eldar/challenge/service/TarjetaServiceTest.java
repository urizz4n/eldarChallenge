package com.eldar.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarjetaServiceTest {

    private TarjetaService tarjetaService;

    @BeforeEach
    void setUp() {
        tarjetaService = new TarjetaService();
    }

    @Test
    void consultarTasa_visa() {
        // Fecha de prueba para el cálculo de tasa
        String fecha = "01-11-2024";
        double tasaEsperada = 24.0/11;  // Año dividido por mes

        // Llamar al método consultarTasa
        double tasaCalculada = tarjetaService.consultarTasa("VISA", fecha);

        // Verificar que la tasa calculada sea la esperada
        assertEquals(tasaEsperada, tasaCalculada);
    }

    @Test
    void consultarTasa_nara() {
        // Fecha de prueba para el cálculo de tasa
        String fecha = "15-11-2024";
        double tasaEsperada = 5;  // Día del mes multiplicado por 0.5

        // Llamar al método consultarTasa
        double tasaCalculada = tarjetaService.consultarTasa("NARA", fecha);

        // Verificar que la tasa calculada sea la esperada
        assertEquals(tasaEsperada, tasaCalculada);
    }
}