package com.eldar.challenge;

import com.eldar.challenge.exception.CampoVacioException;
import com.eldar.challenge.exception.FormatoInvalidoException;

import java.time.LocalDate;

public class Tarjeta {

    private String marca;
    private String numero;
    private LocalDate fechaVencimiento;
    private Persona titular;
    private String cvv;


    public Tarjeta(String marca, String numero, LocalDate fechaVencimiento, Persona titular, String cvv) {
        validarMarca(marca);
        validarNumero(numero);
        validarTitular(titular);
        validarCvv(cvv);

        this.marca = marca;
        this.numero = numero;
        this.fechaVencimiento = fechaVencimiento;
        this.titular = titular;
        this.cvv = cvv;
    }

    /**
     * @param cvv
     */
    public static void validarCvv(String cvv) {
        if (cvv == null || cvv.isEmpty()) throw new CampoVacioException("El CVV no puede estar vacío.");
        if (!cvv.matches("\\d{3}")) throw new FormatoInvalidoException("El CVV debe tener 3 dígitos.");
    }

    public static void validarTitular(Persona titular) {
        if (titular == null) throw new CampoVacioException("Toda persona debe ser poseída por un titular");
    }

    public static void validarNumero(String numero) {
        if (numero == null || numero.isEmpty()) throw new CampoVacioException("El número de tarjeta no puede estar vacío.");
        if (!numero.matches("\\d{16}")) throw new FormatoInvalidoException("El número de tarjeta debe tener 16 dígitos.");
    }

    public static void validarMarca(String marca) {
        if (marca == null || marca.isEmpty()) throw new CampoVacioException("La marca no puede estar vacía.");
        if (!marca.equals("VISA") && !marca.equals("NARA") && !marca.equals("AMEX"))
            throw new FormatoInvalidoException("La marca debe ser VISA, NARA o AMEX.");
    }

    // Getters y setters
    public String getMarca() { return marca; }
    public String getNumero() { return numero; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public Persona getTitular() { return titular; }
    public String getCvv() { return cvv; }

    public void setMarca(String marca) {
        validarMarca(marca);
        this.marca = marca;
    }

    public void setNumero(String numero) {
        validarNumero(numero);
        this.numero = numero;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public void setTitular(Persona titular) {
        validarNumero(numero);
        this.titular = titular;
    }


    public void setCvv(String cvv) {
        validarCvv(cvv);
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "Tarjeta{" +
                "marca='" + marca + '\'' +
                ", numero='***'" +
                ", fechaVencimiento=" + fechaVencimiento +
                ", titular='" + titular + '\'' +
                ", cvv='***'" +
                '}';
    }
}