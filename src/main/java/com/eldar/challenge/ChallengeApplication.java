package com.eldar.challenge;

import com.eldar.challenge.exception.CampoVacioException;
import com.eldar.challenge.exception.FormatoInvalidoException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@SpringBootApplication
public class ChallengeApplication {


	public static void main(String[] args) {
		// Con sólo esta linea ya es suficiente para que funcione el ejercicio 2.
		SpringApplication.run(ChallengeApplication.class, args);
		mostrarMenu();
	}

	private static List<Persona> personas = new ArrayList<>();
	private static List<Tarjeta> tarjetas = new ArrayList<>();

	private static void mostrarMenu() {
		Scanner scanner = new Scanner(System.in);
		boolean salir = false;

		while (!salir) {
			System.out.println("Seleccione una opción:");
			System.out.println("1. Registrar Persona");
			System.out.println("2. Registrar Tarjeta");
			System.out.println("3. Consultar tarjetas de un usuario por DNI");
			System.out.println("4. Consultar tasas de todas las marcas por fecha");
			System.out.println("5. Salir");

			int opcion = scanner.nextInt();
			scanner.nextLine();

			switch (opcion) {
				case 1:
					registrarPersona(scanner);
					break;
				case 2:
					registrarTarjeta(scanner);
					break;
				case 3:
					consultarTarjetasPorDni(scanner);
					break;
				case 4:
					consultarTasas(scanner);
					break;
				case 5:
					salir = true;
					System.out.println("Saliendo del sistema...");
					break;
				default:
					System.out.println("Opción inválida. Intente de nuevo.");
			}
		}
		scanner.close();
	}

	private static void registrarPersona(Scanner scanner) {
		String nombre,apellido,dni,email;
		try {
			System.out.println("Ingrese el nombre:");
			nombre = scanner.nextLine();
			Persona.validarNombre(nombre);

			System.out.println("Ingrese el apellido:");
			apellido = scanner.nextLine();
			Persona.validarNombre(apellido);

			System.out.println("Ingrese el DNI:");
			dni = scanner.nextLine();
			Persona.validarDni(dni);

			System.out.println("Ingrese el email:");
			email = scanner.nextLine();
			Persona.validarEmail(email);

		} catch (FormatoInvalidoException | CampoVacioException e) {
			System.out.println(e);
			return;
		}

		System.out.println("Ingrese la fecha de nacimiento (formato dd-MM-yyyy):");
		String fechaNacimientoStr = scanner.nextLine();
		LocalDate fechaNacimiento;

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fechaNacimiento = LocalDate.parse(fechaNacimientoStr, formatter);
		} catch (DateTimeParseException e) {
			System.out.println("Fecha no válida. Intente de nuevo.");
			return;
		}

		try {
			Persona.validarFechaNacimiento(fechaNacimiento);
		} catch(FormatoInvalidoException e) {
			System.out.println(e);
		}

		// Crear instancia de Persona y añadirla a la lista
		Persona persona = new Persona(nombre, apellido, dni, fechaNacimiento, email);
		personas.add(persona);

		System.out.println("Persona registrada exitosamente: " + persona);

	}

	private static void registrarTarjeta(Scanner scanner) {
		String dni,marca,numero,cvv;
		try {
			System.out.println("Ingrese el DNI del titular:");
			dni = scanner.nextLine();
			Persona.validarDni(dni);

			System.out.println("Ingrese la marca de la tarjeta (VISA, NARA, AMEX):");
			marca = scanner.nextLine().toUpperCase();
			Tarjeta.validarMarca(marca);

			System.out.println("Ingrese el número de la tarjeta:");
			numero = scanner.nextLine();
			Tarjeta.validarNumero(numero);

			System.out.println("Ingrese el CVV (3 dígitos):");
			cvv = scanner.nextLine();
			Tarjeta.validarCvv(cvv);

		} catch (FormatoInvalidoException | CampoVacioException e) {
			System.out.println(e);
			return;
		}

		System.out.println("Ingrese la fecha de vencimiento (formato MM-yyyy):");
		String fechaVencimientoStr = scanner.nextLine();
		LocalDate fechaVencimiento;

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
			fechaVencimiento = LocalDate.parse("01-" + fechaVencimientoStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		} catch (DateTimeParseException e) {
			System.out.println("Fecha de vencimiento no válida. Intente de nuevo.");
			return;
		}

		// Validar que la tarjeta no esté vencida
		if (fechaVencimiento.isBefore(LocalDate.now())) {
			System.out.println("La tarjeta está vencida y no puede ser registrada.");
			return;
		}

		Persona titular = personas.stream()
				.filter(p -> p.getDni().equals(dni))
				.findFirst()
				.orElse(null);

		// Crear y agregar la tarjeta a la lista
		Tarjeta tarjeta = new Tarjeta(marca, numero, fechaVencimiento, titular, cvv);
		tarjetas.add(tarjeta);

		System.out.println("Tarjeta registrada exitosamente: " + tarjeta);
	}

	private static void consultarTarjetasPorDni(Scanner scanner) {
		String dni;
		try {
			System.out.println("Ingrese el DNI del usuario:");
			dni = scanner.nextLine();
			Persona.validarDni(dni);
		} catch (FormatoInvalidoException | CampoVacioException e) {
			System.out.println(e);
			return;
		}
		// Buscar todas las tarjetas que coincidan con el DNI
		List<Tarjeta> tarjetasEncontradas = new ArrayList<>();
		for (Tarjeta tarjeta : tarjetas) {
			if (tarjeta.getTitular().getDni().equals(dni)) {
				tarjetasEncontradas.add(tarjeta);
			}
		}

		// Mostrar resultados
		if (tarjetasEncontradas.isEmpty()) {
			System.out.println("No se encontraron tarjetas asociadas al DNI " + dni);
		} else {
			System.out.println("Tarjetas asociadas al DNI " + dni + ":");
			for (Tarjeta tarjeta : tarjetasEncontradas) {
				System.out.println(tarjeta);
			}
		}
	}

	private static void consultarTasas(Scanner scanner) {
		System.out.println("Ingrese una fecha en formato dd-MM-yyyy (o presione Enter para usar la fecha actual):");
		String fechaStr = scanner.nextLine();
		LocalDate fecha;

		if (fechaStr.isEmpty()) {
			fecha = LocalDate.now();
		} else {
			try {
				fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			} catch (DateTimeParseException e) {
				System.out.println("Fecha no válida. Usando la fecha actual.");
				fecha = LocalDate.now();
			}
		}

		// Calcular y mostrar tasas para cada marca
		System.out.println("Tasa para VISA: " + calcularTasa("VISA", fecha));
		System.out.println("Tasa para NARA: " + calcularTasa("NARA", fecha));
		System.out.println("Tasa para AMEX: " + calcularTasa("AMEX", fecha));
	}

	public static double calcularTasa(String marca, LocalDate fecha) {
        return switch (marca) {
            case "VISA" -> (double) Math.max(0.3, Math.min((fecha.getYear() % 100) / fecha.getMonthValue(), 5));
            case "NARA" -> Math.max(0.3, Math.min(fecha.getDayOfMonth() * 0.5, 5));
            case "AMEX" -> Math.max(0.3, Math.min(fecha.getMonthValue() * 0.1, 5));
            default -> throw new IllegalArgumentException("Marca de tarjeta no reconocida.");
        };

	}
}
