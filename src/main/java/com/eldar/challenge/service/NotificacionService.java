package com.eldar.challenge.service;

import com.eldar.challenge.Tarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Autowired
    private EmailService emailService;

    // En realidad el mail iría al de la persona, no al mío. Pero está configurado así para que no cause problemas.
    public void enviarCorreoRegistroTarjeta(Tarjeta tarjeta) {
        String mensaje = String.format(
                "##Este mail es sólo para realizar pruebas. Ignore su contenido##\n\nHola %s,\n\nSe ha registrado una nueva tarjeta con éxito.\nNúmero: %s\nMarca: %s\nFecha de Vencimiento: %s\nCVV: %s\n\n##Este mail es sólo para realizar pruebas. Ignore su contenido##",
                tarjeta.getTitular(), tarjeta.getNumero(), tarjeta.getMarca(), tarjeta.getFechaVencimiento(), tarjeta.getCvv());
        emailService.enviarCorreo("ufs.testemail@gmail.com", "Registro de Tarjeta Exitoso", mensaje);
    }

    public void enviarCorreoCompraRealizada(Tarjeta tarjeta, String detalle, double monto,double tasa) {
        String mensaje = String.format(
                "##Este mail es sólo para realizar pruebas. Ignore su contenido##\n\nHola %s,\n\nSe ha realizado una compra exitosamente.\nDetalle: %s\nMonto: $%.2f\nTasa: $%.2f\n\n##Este mail es sólo para realizar pruebas. Ignore su contenido##",
                tarjeta.getTitular().getNombre(), detalle, monto,tasa);
        emailService.enviarCorreo("ufs.testemail@gmail.com", "Compra realizada exitosamente", mensaje);
    }
}