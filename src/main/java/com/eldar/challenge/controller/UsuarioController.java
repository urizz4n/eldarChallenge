package com.eldar.challenge.controller;

import com.eldar.challenge.Persona;
import com.eldar.challenge.Tarjeta;
import com.eldar.challenge.service.UsuarioService;
import com.eldar.challenge.service.TarjetaService;
import com.eldar.challenge.service.NotificacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TarjetaService tarjetaService;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Registra un nuevo usuario dada Persona como JSON
     */
    @PostMapping("/usuarios")
    public ResponseEntity<String> registrarUsuario(@RequestBody Persona persona) {
        usuarioService.registrarUsuario(persona);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
    }

    /**
     * Da de alta una tarjeta, dado un DNI y la Marca como parametros
     */
    @PostMapping("/tarjetas")
    public ResponseEntity<String> registrarTarjeta(@RequestParam String dni, @RequestParam String marca) {
        Persona titular = usuarioService.buscarPersonaPorDni(dni);
        tarjetaService.verificarTarjetaDuplicada(dni, marca);

        Tarjeta tarjeta = tarjetaService.crearTarjetaAleatoria(marca, titular);
        notificacionService.enviarCorreoRegistroTarjeta(tarjeta);

        return ResponseEntity.status(HttpStatus.CREATED).body("Tarjeta registrada exitosamente");
    }

    /**
     * Consulta todas las tarjetas de una persona según el DNI como parametros
     * @return lista de tarjetas
     */
    @GetMapping("/tarjetas/{dni}")
    public ResponseEntity<List<Tarjeta>> consultarTarjetasPorDni(@PathVariable String dni) {
        List<Tarjeta> tarjetas = tarjetaService.buscarTarjetasPorDni(dni);
        return ResponseEntity.ok(tarjetas);
    }

    /**
     * Realiza una compra, dada una tarjeta, su cvv, el monto y detalle de la compra como parametros.
     * Envía un email al usuario como notificación de la misma.
     */
    @PostMapping("/compra")
    public ResponseEntity<String> realizarCompra(@RequestParam String numeroTarjeta,
                                                 @RequestParam String cvv, @RequestParam double monto,
                                                 @RequestParam String detalle) {
        Tarjeta tarjeta = tarjetaService.buscarTarjetaPorNumero(numeroTarjeta);
        tarjetaService.validarCvv(tarjeta, cvv);
        tarjetaService.validarMonto(monto);
        Double tasa = tarjetaService.consultarTasa(tarjeta.getMarca(),"");

        notificacionService.enviarCorreoCompraRealizada(tarjeta, detalle, monto ,tasa);
        return ResponseEntity.ok("Compra realizada exitosamente y notificación enviada.");
    }

    /**
     * Da de baja a un usuario, y con él se dan de baja todas sus tarjetas de crédito, utilizando el DNI en la ruta del endpoint
     */
    @DeleteMapping("/usuarios/{dni}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String dni) {
        usuarioService.eliminarUsuario(dni);
        tarjetaService.eliminarTarjetasPorDni(dni);
        return ResponseEntity.ok("Usuario y sus tarjetas asociadas eliminados exitosamente.");
    }

    /**
     * Elimina una tarjeta específica utilizando el numero en la ruta del endpoint
     */
    @DeleteMapping("/tarjetas/{numero}")
    public ResponseEntity<String> eliminarTarjeta(@PathVariable String numero) {
        tarjetaService.eliminarTarjetaPorNumero(numero);
        return ResponseEntity.ok("Tarjeta eliminada exitosamente.");
    }

    /**
     * Modifica los datos de un usuario. La cantidad de datos modificados es opcional, proporcionado por los parametros.
      * @param dni utilizando el DNI en la ruta del endpoint
     */
    @PutMapping("/usuarios/{dni}")
    public ResponseEntity<String> modificarUsuario(@PathVariable String dni,
                                                   @RequestParam Optional<String> nombre,
                                                   @RequestParam Optional<String> apellido,
                                                   @RequestParam Optional<String> nuevoDni,
                                                   @RequestParam Optional<String> fechaNacimiento,
                                                   @RequestParam Optional<String> email) {
        Persona usuario = usuarioService.modificarUsuario(dni, nombre,apellido,nuevoDni,fechaNacimiento,email);
        tarjetaService.actualizarTitularDeTarjetas(dni, usuario);
        return ResponseEntity.ok("Usuario y sus tarjetas asociadas actualizados exitosamente.");
    }

    /**
     * Modifica los datos de una tarjeta. La cantidad de datos modificados es opcional, proporcionado por los parametros.
     * @param numero utilizando el numero en la ruta del endpoint
     */    @PutMapping("/tarjetas/{numero}")
    public ResponseEntity<String> modificarTarjeta(@PathVariable String numero,
                                                   @RequestParam Optional<String> marca,
                                                   @RequestParam Optional<String> fechaVencimiento,
                                                   @RequestParam Optional<String> dni,
                                                   @RequestParam Optional<String> numeroNuevo,
                                                   @RequestParam Optional<String> cvv){
        Persona titular = null;
        if (dni.isPresent()) {
            titular = usuarioService.buscarPersonaPorDni(dni.get());
        }
        tarjetaService.modificarTarjeta(numero, marca, fechaVencimiento, Optional.ofNullable(titular), numeroNuevo, cvv);
        return ResponseEntity.ok("Tarjeta actualizada exitosamente.");
    }

    /**
     * Consulta la tasa que se cobraría al realizar un pago con el monto y marca de tarjeta otorgada como parámetro. La fecha es opcional
     * @param fecha La fecha es opcional, en caso de no otorgarse, será en base al día actual.
     */
    @GetMapping("/tasa")
    public ResponseEntity<String> consultarTasa(@RequestParam String marca, @RequestParam double monto, @RequestParam Optional<String> fecha) {
        double tasa = tarjetaService.consultarTasa(marca, fecha.orElse(""));
        return ResponseEntity.ok(String.format("La tasa es de $%.2f",monto*tasa/100));
    }
}