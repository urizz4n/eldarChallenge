package com.eldar.challenge;

import com.eldar.challenge.exception.PersonaNoEncontradaException;
import com.eldar.challenge.service.UsuarioService;
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
		System.out.println("Ingrese el nombre:");
		String nombre = scanner.nextLine();

		System.out.println("Ingrese el apellido:");
		String apellido = scanner.nextLine();

		System.out.println("Ingrese el DNI:");
		String dni = scanner.nextLine();

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

		System.out.println("Ingrese el email:");
		String email = scanner.nextLine();

		// Crear instancia de Persona y añadirla a la lista
		Persona persona = new Persona(nombre, apellido, dni, fechaNacimiento, email);
		personas.add(persona);

		System.out.println("Persona registrada exitosamente: " + persona);

	}

	private static void registrarTarjeta(Scanner scanner) {
		System.out.println("Ingrese el DNI del titular:");
		String dni = scanner.nextLine();

		System.out.println("Ingrese la marca de la tarjeta (VISA, NARA, AMEX):");
		String marca = scanner.nextLine().toUpperCase();

		// Validar marca
		if (!marca.equals("VISA") && !marca.equals("NARA") && !marca.equals("AMEX")) {
			System.out.println("Marca no válida. Debe ser VISA, NARA o AMEX.");
			return;
		}

		System.out.println("Ingrese el número de la tarjeta:");
		String numero = scanner.nextLine();

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

		System.out.println("Ingrese el CVV (3 dígitos):");
		String cvv = scanner.nextLine();

		// Validar que el CVV tenga 3 dígitos
		if (cvv.length() != 3 || !cvv.matches("\\d{3}")) {
			System.out.println("CVV no válido. Debe ser un número de 3 dígitos.");
			return;
		}

		Persona titular = personas.stream()
				.filter(p -> p.getDni().equals(dni))
				.findFirst()
				.orElseThrow(() -> new PersonaNoEncontradaException("No se encontró un usuario con el DNI especificado."));

		// Crear y agregar la tarjeta a la lista
		Tarjeta tarjeta = new Tarjeta(marca, numero, fechaVencimiento, titular, cvv);
		tarjetas.add(tarjeta);

		System.out.println("Tarjeta registrada exitosamente: " + tarjeta);
	}

	private static void consultarTarjetasPorDni(Scanner scanner) {
		System.out.println("Ingrese el DNI del usuario:");
		String dni = scanner.nextLine();

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
		switch (marca) {
			case "VISA":
				return (double) Math.max(0.3, Math.min((fecha.getYear() % 100)/ fecha.getMonthValue(), 5)) ;
			case "NARA":
				return Math.max(0.3, Math.min(fecha.getDayOfMonth() * 0.5, 5));
			case "AMEX":
				return Math.max(0.3, Math.min(fecha.getMonthValue() * 0.1, 5));
			default:
				throw new IllegalArgumentException("Marca de tarjeta no reconocida.");
		}

	}
}
