package com.eldar.challenge.service;

import com.eldar.challenge.Tarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Autowired
    private EmailService emailService;



    /**
     * Envia notificación al usuario al darse de alta una tarjeta
     * @param tarjeta
     */
    public void enviarCorreoRegistroTarjeta(Tarjeta tarjeta) {
        String mensaje = String.format(
                "##Este mail es sólo para realizar pruebas. Ignore su contenido##\n\nHola %s,\n\nSe ha registrado una nueva tarjeta con éxito.\nNúmero: %s\nMarca: %s\nFecha de Vencimiento: %s\nCVV: %s\n\n##Este mail es sólo para realizar pruebas. Ignore su contenido##",
                tarjeta.getTitular(), tarjeta.getNumero(), tarjeta.getMarca(), tarjeta.getFechaVencimiento(), tarjeta.getCvv());
        emailService.enviarCorreo("ufs.testemail@gmail.com", "Registro de Tarjeta Exitoso", mensaje); // En realidad el mail iría al de la persona, no al mío. Pero está configurado así para que no cause problemas.
    }

    /**
     * Envía una notificación al usuario al realizar una compra con los detalles de la misma
     * @param tarjeta
     * @param detalle
     * @param monto
     * @param tasa
     */
    public void enviarCorreoCompraRealizada(Tarjeta tarjeta, String detalle, double monto,double tasa) {
        String mensaje = String.format(
                "##Este mail es sólo para realizar pruebas. Ignore su contenido##\n\nHola %s,\n\nSe ha realizado una compra exitosamente.\nDetalle: %s\nMonto: $%.2f\nTasa: $%.2f\n\n##Este mail es sólo para realizar pruebas. Ignore su contenido##",
                tarjeta.getTitular().getNombre(), detalle, monto,tasa);
        emailService.enviarCorreo("ufs.testemail@gmail.com", "Compra realizada exitosamente", mensaje);
    }
}