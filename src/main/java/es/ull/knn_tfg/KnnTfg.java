package knn_tfg;

import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clasificacion.KNN;
import datos.*;
import procesamiento.*;
import entrenamiento.*;

public class KnnTfg {
	private static final Logger logger = LoggerFactory.getLogger(KnnTfg.class);
	private static final String MENSAJE_INTRODUCIR_K = "Introduce el valor de k: ";
	private static final String MENSAJE_INTRODUCIR_VALORES = "Introduce los valores: ";
	private static final String MENSAJE_INTRODUCIR_PORCENTAJE = "Introduzca el porcentaje para el conjunto de entrenamiento";
	private static final String MENSAJE_INVALIDA_NUMERO = "Entrada inválida. Por favor, introduce un número.";
	private static final String MENSAJE_OPCION_INVALIDA= "Opción inválida.";
	private static final String MENSAJE_INDICE_RANGO= "Índice fuera de rango.";
	private static final String MENSAJE_ARCHIVO_ENTRENAMIENTO = "Introduzca el nombre para el archivo de entrenamiento: ";
	private static final String MENSAJE_ARCHIVO_PRUEBAS = "Introduzca el nombre del archivo de pruebas: ";


	private static Dataset datosCrudos = new Dataset();
	private static Dataset datos = new Dataset();
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		logger.info("El programa KNN_TFG ha comenzado.");
		boolean salida = false;

		while (!salida) {
			mostrarMenu();
			int opcion = obtenerOpcionUsuario();
			salida = procesarOpcion(opcion);
		}
		logger.info("El programa KNN_TFG ha terminado.");
		scanner.close(); // Asegúrate de cerrar el scanner
	}

	private static void mostrarMenu() {
		logger.info("Seleccione una opción: ");
		logger.info("    [1] Cargar un dataset ");
		logger.info("    [2] Guardar un dataset ");
		logger.info("    [3] Modificar un dataset ");
		logger.info("    [4] Mostrar información ");
		logger.info("    [5] Salir del programa ");
		logger.info("    [6] Realizar experimentación ");
		logger.info("    [7] Algoritmo KNN para una instancia ");
	}

	private static int obtenerOpcionUsuario() {
		logger.info("Introduce el número de la opción: ");
		int opcion = -1;
		try {
			opcion = scanner.nextInt();
		} catch (java.util.InputMismatchException e) {
			logger.warn("Entrada inválida. Por favor, introduce un valor númerico.");
			scanner.next(); // Limpiar el buffer del scanner
		}
		scanner.nextLine(); // Consumir la nueva línea
		return opcion;
	}

	private static boolean procesarOpcion(int opcion) throws IOException {
		try {
			switch (opcion) {
				case 1:
					cargarDataset();
					break;
				case 2:
					guardarDataset();
					break;
				case 3:
					modificarDataset();
					break;
				case 4:
					mostrarInformacion();
					break;
				case 5:
					logger.info("Saliendo del programa.");
					return true;
				case 6:
					realizarExperimentacion();
					break;
				case 7:
					algoritmoKNNInstancia();
					break;
				default:
					logger.warn("Opción inválida. Por favor, selecciona una opción del menú.");
			}
		} catch (IOException e) {
			logger.error("Error de E/S al procesar la opción {}: {}", opcion, e.getMessage());
			// Considera si quieres detener el programa o continuar
		} catch (NumberFormatException e) {
			logger.error("Error de formato numérico al procesar la opción {}: {}", opcion, e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("Argumento inválido al procesar la opción {}: {}", opcion, e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			logger.error("Error de índice fuera de rango al procesar la opción {}: {}", opcion, e.getMessage());
		}
		return false;
	}

	private static void cargarDataset() throws IOException {
		String ruta = "";
		String archivo = readFile(ruta);
		try {
			datosCrudos = new Dataset(ruta + archivo);
			datos = new Dataset(ruta + archivo);
			datos = preprocesar(datos);
		} catch (IOException e) {
			logger.error("Error al cargar el dataset desde {}{}: {}", ruta, archivo, e.getMessage());
			throw new IOException("Error al cargar el dataset desde fichero"); // Relanzar la excepción para que sea manejada en procesarOpcion
		}
	}

	private static void guardarDataset() throws IOException {
		String ruta = "";
		String archivo = readFile(ruta);
		try {
			datos.write(ruta + archivo);
			logger.info("Dataset guardado en {}{}", ruta, archivo);
		} catch (IOException e) {
			logger.error("Error al guardar el dataset en {}{}: {}", ruta, archivo, e.getMessage());
			throw new IOException("Error al guardar el dataset en un fichero"); // Relanzar la excepción
		}
	}

	private static void modificarDataset() {
		try {
			datos = modify(datos);
		} catch (IndexOutOfBoundsException e) {
			logger.error("Error de índice al modificar el dataset: {}", e.getMessage());
		}
	}

	private static void mostrarInformacion() {
		try {
			info(datos);
		} catch (IllegalArgumentException e) {
			logger.error("Error al mostrar información del dataset: {}", e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			logger.error("Error de índice al mostrar información del dataset: {}", e.getMessage());
		}
	}

	private static void realizarExperimentacion() throws IOException {
		try {
			experimentar(datos);
		} catch (IOException e) {
			logger.error("Error durante la experimentación: {}", e.getMessage());
			throw new IOException("Error durante la experimentación"); // Relanzar la excepción
		}
	}

	private static void algoritmoKNNInstancia() {
		try {
			int k = obtenerK();
			KNN knnClassifier = new KNN(k);
			Instancia instanceToClassify = obtenerInstanciaParaClasificar();
			Dataset datasetForClassification = prepararDatasetParaClasificacion(instanceToClassify);
			instanceToClassify.addClase("clase");

			if (datasetForClassification.numeroCasos() > 0) {
				String claseElegida = knnClassifier.clasificar(datasetForClassification, instanceToClassify);
				logClasificacionResultado(claseElegida);
			} else {
				logger.warn("No hay instancias en el dataset para realizar la clasificación.");
			}

		} catch (InputMismatchException e) {
			logger.error("Entrada inválida: {}", e.getMessage());
			scanner.next(); // Limpiar el buffer
		} catch (IllegalArgumentException e) {
			logger.error("Error: {}", e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			logger.error("Error de índice: {}", e.getMessage());
		}
	}

	private static int obtenerK() throws InputMismatchException, IllegalArgumentException {
		logger.info(MENSAJE_INTRODUCIR_K);
		int k = scanner.nextInt();
		if (k <= 0) {
			throw new IllegalArgumentException("El valor de k debe ser mayor que cero.");
		}
		return k;
	}

	private static Instancia obtenerInstanciaParaClasificar() {
		logger.info("Introduce valores: ");
		Scanner scanner1 = new Scanner(System.in);
		String valoresString = scanner1.nextLine();
		// convertir la cadena en una lista de valores
		String[] subcadenas = valoresString.split(",");
		ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
		ArrayList<Double> valores = new ArrayList<>();
		for (String valor : arrayList) {
			try {
				valores.add(Double.parseDouble(valor.trim()));
			} catch (NumberFormatException e) {
				logger.error("Error al convertir el valor: {}", e.getMessage());
				throw new IllegalArgumentException("Error al convertir el valor: " + valor);
			}
		}
		ArrayList<Object> valoresObject = new ArrayList<>();
		valoresObject.addAll(valores);
		return new Instancia(valoresObject);
	}

	private static Dataset prepararDatasetParaClasificacion(Instancia instanceToClassify) {
		Dataset copiaCrudos = new Dataset(datosCrudos);
		List<String> valoresList = Arrays.asList(instanceToClassify.getValoresString().split(","));

		if (datos.getPreprocesado() != 1) {
			if (valoresList.size() != copiaCrudos.numeroAtributos()-1) {
				throw new IllegalArgumentException("El número de valores introducidos no coincide con el número de atributos del dataset.");
			}
			List<String> instanciaConClase = new ArrayList<>(valoresList);
			instanciaConClase.add("clase");
			copiaCrudos.add(instanciaConClase);
			copiaCrudos = preprocesarDataset(copiaCrudos);
			instanceToClassify = copiaCrudos.getInstance(copiaCrudos.numeroCasos() - 1);
			instanceToClassify.deleteClase();
			copiaCrudos.delete(copiaCrudos.numeroCasos()-1);
		} else {
			if (valoresList.size() != copiaCrudos.numeroAtributos()-1) {
				throw new IllegalArgumentException("El número de valores introducidos no coincide con el número de atributos del dataset.");
			}
		}
		return copiaCrudos;
	}

	private static Dataset preprocesarDataset(Dataset dataset) {
		Preprocesado preprocesador = null;
		if (datos.getPreprocesado() == 2) {
			preprocesador = new Normalizacion();
		} else if (datos.getPreprocesado() == 3) {
			preprocesador = new Estandarizacion();
		}
		return (preprocesador != null) ? new Dataset(preprocesador.procesar(dataset)) : dataset;
	}

	private static void logClasificacionResultado(String claseElegida) {
		if (claseElegida != null) {
			logger.info("La clase elegida es: {}", claseElegida);
		} else {
			logger.warn("No se pudo clasificar la instancia.");
		}
	}


	public static String readFile(String ruta) {
		int opcion = 2;
		String archivo = "";
		while (opcion != 4) {
			logger.info("Se debe especificar la ruta y nombre del archivo: ");
			logger.info("       [1] Introducir nombre");
			logger.info("       [2] Mostrar ruta ");
			logger.info("       [3] Cambiar ruta ");
			logger.info("       [4] Salir ");
			try {
				opcion = scanner.nextInt();
				scanner.nextLine(); // Consume newline
				switch (opcion) {
					case 1:
						logger.info("Introduzca el nombre del archivo: ");
						archivo = scanner.nextLine();
						break;
					case 2:
						logger.info("Ruta actual: {}", ruta);
						break;
					case 3:
						logger.info("Introduzca la nueva ruta: ");
						ruta = scanner.nextLine();
						break;
					case 4:
						break;
					default:
						logger.warn("Opción invalida.");
				}
			} catch (java.util.InputMismatchException e) {
				logger.warn("Entrada inválida. Por favor, introduce un númerico.");
				scanner.next(); // Limpiar el buffer
				scanner.nextLine(); // Consume newline
				opcion = -1; // Para que el bucle continúe
			}
			if (opcion == 1 || opcion == 4) break;
		}
		return archivo;
	}

	private static Dataset agregarInstancia(Dataset data) {
		logger.info(MENSAJE_INTRODUCIR_VALORES);
		String valores = scanner.nextLine();
		String[] subcadenas = valores.split(",");
		ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
		logger.info("Añadiendo instancia: {}", arrayList);
		data.add(arrayList);
		return data;
	}

	private static Dataset eliminarInstancia(Dataset data) {
		logger.info("Introduce el índice a eliminar: ");
		int valor = scanner.nextInt();
		scanner.nextLine(); // Consume newline
		if (valor >= 0 && valor < data.numeroCasos()) {
			data.delete(valor);
			logger.info("Instancia {} eliminada.", valor);
		} else {
			logger.warn("Índice de instancia a eliminar fuera de rango.");
		}
		return data;
	}

	private static Dataset modificarInstancia(Dataset data) {
		logger.info(MENSAJE_INTRODUCIR_VALORES);
		String valores = scanner.nextLine();
		String[] subcadenasMod = valores.split(",");
		ArrayList<String> arrayListMod = new ArrayList<>(Arrays.asList(subcadenasMod));
		logger.info("Introduce el índice de la instancia a modificar: ");
		int indiceMod = scanner.nextInt();
		scanner.nextLine(); // Consume newline
		if (indiceMod >= 0 && indiceMod < data.numeroCasos()) {
			Instancia instanciaAModificar = data.getInstance(indiceMod);
			if (instanciaAModificar != null && instanciaAModificar.getVector() != null && arrayListMod.size() == instanciaAModificar.getVector().size()+1) {
				for (int i = 0; i < arrayListMod.size()-1; i++) {
					try {
						instanciaAModificar.set(i, Double.parseDouble(arrayListMod.get(i).trim()));
					} catch (NumberFormatException e) {
						logger.error("Error al convertir valor en índice {}: {}", i, e.getMessage());
						return data; // Or handle differently
					}
					instanciaAModificar.addClase(arrayListMod.get(arrayListMod.size()-1));
				}
				logger.info("Instancia {} modificada.", indiceMod);
				data.delete(indiceMod);
				data.add(instanciaAModificar);
			} else {
				logger.warn("No se puede modificar la instancia {} debido a tamaño incorrecto o instancia nula.", indiceMod);
			}
		} else {
			logger.warn("Índice de instancia a modificar fuera de rango.");
		}

		return data;
	}

	private static Dataset cambiarPesosWrapper(Dataset data) {
		return cambiarPesos(data);
	}

	public static Dataset modify(Dataset data) {
		int opcion = 2;
		while (opcion != 5) {
			logger.info("Elija una opción de modificación ");
			logger.info("       [1] Añadir instancia ");
			logger.info("       [2] Eliminar instancia ");
			logger.info("       [3] Modificar instancia ");
			logger.info("       [4] Cambiar peso de los atributos ");
			logger.info("       [5] Salir ");
			try {
				opcion = scanner.nextInt();
				scanner.nextLine(); // Consume newline
				switch (opcion) {
					case 1:
						data = agregarInstancia(data);
						break;
					case 2:
						data = eliminarInstancia(data);
						break;
					case 3:
						data = modificarInstancia(data);
						break;
					case 4:
						data = cambiarPesosWrapper(data);
						break;
					case 5:
						break;
					default:
						logger.warn(MENSAJE_OPCION_INVALIDA);
				}
			} catch (InputMismatchException e) {
				logger.warn("Entrada inválida. Por favor, introduce un número válido.");
				scanner.next(); // Limpiar el buffer
				scanner.nextLine(); // Consume newline
				opcion = -1; // Para que el bucle continúe
			} catch (IllegalArgumentException e) {
				logger.error("Error al modificar el dataset: {}", e.getMessage());
				return data;
			}
		}
		return data;
	}

	public static Dataset preprocesar(Dataset data) {
		logger.info("Seleccione la opción de preprocesado: ");
		logger.info("       [1] Datos crudos ");
		logger.info("       [2] Rango 0-1 "); // por defecto
		logger.info("       [3] Estandarización ");
		logger.info("       [4] Salir (se usará la opción por defecto)");
		int opcion = 1;
		try {
			opcion = scanner.nextInt();
			scanner.nextLine(); // Consume newline
			switch (opcion) {
				case 1:
					data.setPreprocesado(1);
					break;
				case 2:
					Normalizacion intento1 = new Normalizacion();
					data = new Dataset(intento1.procesar(data));
					data.setPreprocesado(2);
					break;
				case 3:
					Estandarizacion intento2 = new Estandarizacion();
					data = new Dataset(intento2.procesar(data));
					data.setPreprocesado(3);
					break;
				case 4:
					break;
				default:
					Normalizacion intentoDefecto = new Normalizacion();
					data = new Dataset(intentoDefecto.procesar(data));
					data.setPreprocesado(2);
					logger.warn("Opción inválida. Preprocesando con normalización 0-1 por defecto.");
			}
		} catch (java.util.InputMismatchException e) {
			logger.warn("Entrada inválida. Seleccionando normalización 0-1 por defecto.");
			Normalizacion intentoDefecto = new Normalizacion();
			data = new Dataset(intentoDefecto.procesar(data));
			data.setPreprocesado(2);
			scanner.next(); // Limpiar el buffer
			scanner.nextLine(); // Consume newline
		}
		return data;
	}

	public static Dataset cambiarPesos(Dataset data) {
		logger.info("Opciones para cambiar los pesos de los atributos:");
		logger.info("           [1] Asignar pesos distintos a todos los atributos ");
		logger.info("           [2] Mismo peso para todos los atributos "); // por defecto ( valor 1 )
		logger.info("           [3] Cambiar peso un atributo");
		int opcion = 1;
		try {
			opcion = scanner.nextInt();
			scanner.nextLine(); // Consume newline
			switch (opcion) {
				case 1:
					logger.info("Introduce los pesos para cada atributo separados por comas:");
					String valores = scanner.nextLine();
					String[] subcadenas = valores.split(",");
					ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
					data.cambiarPeso(arrayList);
					break;
				case 2:
					logger.info("Introduce el peso para asignar a todos los atributos (entre 0 y 1):");
					double valoresD = scanner.nextDouble();
					data.cambiarPeso(valoresD);
					break;
				case 3:
					logger.info("Introduce el indice del atributo a modificar: ");
					int valorI = scanner.nextInt();
					logger.info("Peso para asignar (Debe estar entre 0 y 1): ");
					double nuevoPeso = scanner.nextDouble();
					if (nuevoPeso >= 0 && nuevoPeso <= 1) {
						data.cambiarPeso(valorI, nuevoPeso);
					} else {
						logger.warn("Peso fuera del rango [0, 1]. No se modificará el peso.");
					}
					break;
				default: logger.warn("Opción no válida.");
			}
		} catch (java.util.InputMismatchException e) {
			logger.warn( "Entrada no válida. Por favor, introduce un número.");
			scanner.next(); // Limpiar el buffer
			scanner.nextLine(); // Consume newline
		} catch (IllegalArgumentException e) {
			logger.error("Error al cambiar los pesos: {}", e.getMessage());
		}
		return data;
	}

	public static void info(Dataset data) {
		logger.info("           [1] Mostrar dataset ");
		logger.info("           [2] Mostrar instancia ");
		logger.info("           [3] Mostrar información atributos cuantitativos");
		logger.info("           [4] Mostrar información atributos cualitativos");
		logger.info("           [5] Mostrar pesos de los atributos");
		int opcion = -1;
		try {
			opcion = scanner.nextInt();
			scanner.nextLine(); // Consume newline
			switch (opcion) {
				case 1:
					data.print();
					break;
				case 2:
					logger.info("Introduce el índice de la instancia a mostrar: ");
					int valor = scanner.nextInt();
					scanner.nextLine(); // Consume newline
					if (valor >= 0 && valor < data.numeroCasos() && logger.isInfoEnabled()) {
						logger.info(data.getInstance(valor).toString());
					} else {
						logger.warn("Índice de instancia inválido.");
					}
					break;
				case 3:
					infoCuantitativo(data);
					break;
				case 4:
					infoCualitativo(data);
					break;
				case 5:
					List<String> pesos = data.getPesos();
					if (logger.isInfoEnabled() && pesos != null) {
						logger.info("Pesos de los atributos: {}", pesos);
					} else if (pesos == null) {
						logger.info("Los pesos de los atributos no han sido definidos.");
					}
					break;
				default:
					logger.warn("Opción no válida.");
			}
		} catch (java.util.InputMismatchException e) {
			logger.warn( "Entrada no válida. Por favor, introduce un número.");
			scanner.next(); // Limpiar el buffer
			scanner.nextLine(); // Consume newline
		} catch (IndexOutOfBoundsException e) {
			logger.warn("Índice fuera de rango");
		}
	}

	public static void infoCuantitativo(Dataset data) {
		logger.info("               [1] Mostrar nombre del atributo");
		logger.info("               [2] Mostrar media ");
		logger.info("               [3] Mostrar maximo");
		logger.info("               [4] Mostrar minimo");
		logger.info("               [5] Mostrar desviación tipica");
		int opcion = -1;
		try {
			opcion = scanner.nextInt();
			logger.info("Introduce el índice del atributo cuantitativo:");
			int valor = scanner.nextInt();
			scanner.nextLine(); // Consume newline after reading the index
			if (valor >= 0 && valor < data.numeroAtributos() && data.get(valor) instanceof Cuantitativo) {
				Cuantitativo auxiliar = (Cuantitativo) data.get(valor);
				switch (opcion) {
					case 1:
						logger.info("Nombre del atributo: {}", auxiliar.getNombre());
						break;
					case 2:
						logger.info("Media: {}", auxiliar.media());
						break;
					case 3:
						logger.info("Máximo: {}", auxiliar.maximo());
						break;
					case 4:
						logger.info("Mínimo: {}", auxiliar.minimo());
						break;
					case 5:
						logger.info("Desviación típica: {}", auxiliar.desviacion());
						break;
					default:
						logger.warn(MENSAJE_OPCION_INVALIDA);
				}
			} else {
				logger.warn("Índice de atributo inválido o no es cuantitativo.");
			}
		} catch (java.util.InputMismatchException e) {
			logger.warn(MENSAJE_INVALIDA_NUMERO);
			scanner.next(); // Limpiar el buffer
			scanner.nextLine(); // Consume newline
		} catch (IndexOutOfBoundsException e) {
			logger.warn("Índice fuera de rango");
		} catch (ClassCastException e) {
			logger.warn("El atributo en ese índice no es cuantitativo.");
		}
	}

	public static void infoCualitativo(Dataset data) {
		logger.info("               [1] Mostrar nombre ");
		logger.info("               [2] Mostrar número de clases ");
		logger.info("               [3] Mostrar clases");
		logger.info("               [4] Mostrar frecuencia");
		int opcion = -1;
		try {
			opcion = scanner.nextInt();
			logger.info("Introduce el índice del atributo cualitativo:");
			int valor = scanner.nextInt();
			scanner.nextLine(); // Consume newline after reading the index
			if (valor >= 0 && valor < data.numeroAtributos() && data.get(valor) instanceof Cualitativo) {
				Cualitativo auxiliar = (Cualitativo) data.get(valor);
				switch (opcion) {
					case 1:
						logger.info("Nombre: {}", auxiliar.getNombre());
						break;
					case 2:
						logger.info("Número de clases: {}", auxiliar.nClases());
						break;
					case 3:
						logger.info("Clases: {}", auxiliar.clases());
						break;
					case 4:
						logger.info("Frecuencia: {}", auxiliar.frecuencia());
						break;
					default:
						logger.warn(MENSAJE_OPCION_INVALIDA);
				}
			} else {
				logger.warn("Índice de atributo inválido o no es cualitativo.");
			}
		} catch (java.util.InputMismatchException e) {
			logger.warn(MENSAJE_INVALIDA_NUMERO);
			scanner.next(); // Limpiar el buffer
			scanner.nextLine(); // Consume newline
		} catch (IndexOutOfBoundsException e) {
			logger.warn(MENSAJE_INDICE_RANGO);
		} catch (ClassCastException e) {
			logger.warn("El atributo en ese índice no es cualitativo.");
		}
	}

	public static void experimentar(Dataset datos) throws IOException {
		int opcion = -1;
		Entrenamiento nuevo = new Entrenamiento();
		try {
			while (opcion != 5) {
				logger.info("               [1] Generacion experimentación normal");
				logger.info("               [2] Generacion experimentación aleatoria");
				logger.info("               [3] Guardar Dataset de experimentación ");
				logger.info("               [4] Cargar Dataset de experimentación ");
				logger.info("               [5] Salir");
				opcion = scanner.nextInt();
				scanner.nextLine(); // Consume newline

				switch (opcion) {
					case 1:
						logger.info(MENSAJE_INTRODUCIR_PORCENTAJE);
						int valor = scanner.nextInt();
						scanner.nextLine(); // Consume newline
						if (valor > 0 && valor <= 100) {
							nuevo = new Entrenamiento(datos, (double) valor / 100);
							logger.info(MENSAJE_INTRODUCIR_K);
							int k = scanner.nextInt();
							scanner.nextLine(); // Consume newline
							if (k > 0) {
								nuevo.generarPrediccion(k);
								nuevo.generarMatriz(k);
							} else {
								logger.warn("El valor de K debe ser mayor que cero.");
							}
						} else {
							logger.warn("El porcentaje debe estar entre 1 y 100.");
						}
						break;
					case 2:
						nuevo = experimentacionAleatoria(datos);
						break;
					case 3:
						logger.info(MENSAJE_ARCHIVO_ENTRENAMIENTO);
						String archivo1 = scanner.nextLine();
						logger.info(MENSAJE_ARCHIVO_PRUEBAS);
						String archivo2 = scanner.nextLine();
						nuevo.write(archivo1, archivo2);
						logger.info("Datasets de entrenamiento y pruebas guardados.");
						break;
					case 4:
						logger.info(MENSAJE_ARCHIVO_ENTRENAMIENTO);
						String archivoCargar1 = scanner.nextLine();
						logger.info(MENSAJE_ARCHIVO_PRUEBAS);
						String archivoCargar2 = scanner.nextLine();
						cargarPrediccon(nuevo, archivoCargar1, archivoCargar2);
						break;
					case 5:
						logger.info("Saliendo de las opciones de experimentación.");
						break;
					default:
						logger.warn(MENSAJE_OPCION_INVALIDA);
				}
			}
		} catch (java.util.InputMismatchException e) {
			logger.warn(MENSAJE_INVALIDA_NUMERO);
			scanner.next(); // Limpiar el buffer
			scanner.nextLine(); // Consume newline
		} catch (IllegalArgumentException e) {
			logger.error("Error en la experimentación: {}", e.getMessage());
		}
		// No cerrar el scanner aquí
	}

	private static void cargarPrediccon(Entrenamiento nuevo, String archivoCargar1, String archivoCargar2) {
		try {
			nuevo.read(archivoCargar1, archivoCargar2);
			logger.info(MENSAJE_INTRODUCIR_K);
			int kCargar = scanner.nextInt();
			scanner.nextLine(); // Consume newline
			if (kCargar > 0) {
				nuevo.generarPrediccion(kCargar);
				nuevo.generarMatriz(kCargar);
			} else {
				logger.warn("El valor de k debe ser mayor que cero.");
			}
		} catch (IOException e) {
			logger.error("Error al cargar los datasets de experimentación: {}", e.getMessage());
		}
	}

	public static Entrenamiento experimentacionAleatoria(Dataset datos) {
		logger.info("               [1] Semilla(Seed) por defecto");
		logger.info("               [2] Semilla(Seed) manual");
		int opcion = -2;
		try {
			opcion = scanner.nextInt();
			scanner.nextLine(); // Consume newline after reading the sub-option
			Entrenamiento nuevo = new Entrenamiento();
			switch (opcion) {
				case 1:
					logger.info("Introduzca el porcentaje del conjunto de entrenamiento");
					int valorExp = scanner.nextInt();
					scanner.nextLine(); // Consume newline after reading the percentage
					nuevo = new Entrenamiento(datos, (double) valorExp / 100, 1234);
					logger.info(MENSAJE_INTRODUCIR_K);
					int k = scanner.nextInt();
					scanner.nextLine(); // Consume newline after reading k
					nuevo.generarPrediccion(k);
					nuevo.generarMatriz(k);
					return nuevo;
				case 2:
					logger.info(MENSAJE_INTRODUCIR_PORCENTAJE);
					valorExp = scanner.nextInt();
					scanner.nextLine(); // Consume newline after reading the percentage
					logger.info("Introduzca la semilla para la generacion aleatoria");
					int valor2 = scanner.nextInt();
					scanner.nextLine(); // Consume newline after reading the seed
					nuevo = new Entrenamiento(datos, (double) valorExp / 100, valor2);
					logger.info(MENSAJE_INTRODUCIR_K);
					k = scanner.nextInt();
					scanner.nextLine(); // Consume newline after reading k
					nuevo.generarPrediccion(k);
					nuevo.generarMatriz(k);
					return nuevo;
				default:
					logger.warn(MENSAJE_OPCION_INVALIDA);
					return nuevo;
			}
		} catch (java.util.InputMismatchException e) {
			logger.warn("Entrada inválida. Por favor, introduce un numero.");
			scanner.next(); // Limpiar el buffer
			scanner.nextLine(); // Consume newline
			return null; // Or handle error appropriately
		}
	}
}