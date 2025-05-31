package knn_tfg;

import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clasificacion.KNN;
import datos.*;
import procesamiento.*;
import entrenamiento.*;

/**
 * @file KnnTfg.java
 * @brief Clase principal que implementa la aplicación del algoritmo KNN para un Trabajo de Fin de Grado (TFG).
 *
 * Esta clase proporciona una interfaz de línea de comandos para cargar datasets,
 * preprocesarlos, dividirlos en conjuntos de entrenamiento y prueba,
 * y luego clasificar nuevas instancias o evaluar el rendimiento del modelo KNN.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class KnnTfg {
	/**
	 * @brief Instancia de {@link Logger} para registrar mensajes de la aplicación.
	 */
	private static final Logger logger = LoggerFactory.getLogger(KnnTfg.class);
	/**
	 * @brief Mensaje para solicitar el valor de K.
	 */
	private static final String MENSAJE_INTRODUCIR_K = "Introduce el valor de k: ";
	/**
	 * @brief Mensaje para solicitar los valores de una instancia.
	 */
	private static final String MENSAJE_INTRODUCIR_VALORES = "Introduce los valores: ";
	/**
	 * @brief Mensaje para solicitar el porcentaje para el conjunto de entrenamiento.
	 */
	private static final String MENSAJE_INTRODUCIR_PORCENTAJE = "Introduzca el porcentaje para el conjunto de entrenamiento";
	/**
	 * @brief Mensaje de error para entrada inválida (no numérica).
	 */
	private static final String MENSAJE_INVALIDA_NUMERO = "Entrada inválida. Por favor, introduce un número.";
	/**
	 * @brief Mensaje de error para opción de menú inválida.
	 */
	private static final String MENSAJE_OPCION_INVALIDA= "Opción inválida.";
	/**
	 * @brief Mensaje de error para índice fuera de rango.
	 */
	private static final String MENSAJE_INDICE_RANGO= "Índice fuera de rango.";
	/**
	 * @brief Mensaje para solicitar el nombre del archivo de entrenamiento.
	 */
	private static final String MENSAJE_ARCHIVO_ENTRENAMIENTO = "Introduzca el nombre para el archivo de entrenamiento: ";
	/**
	 * @brief Mensaje para solicitar el nombre del archivo de pruebas.
	 */
	private static final String MENSAJE_ARCHIVO_PRUEBAS = "Introduzca el nombre del archivo de pruebas: ";


	/**
	 * @brief {@link Dataset} original cargado, antes de cualquier preprocesamiento.
	 */
	private static Dataset datosCrudos = new Dataset();
	/**
	 * @brief {@link Dataset} actualmente en uso, que puede haber sido preprocesado.
	 */
	private static Dataset datos = new Dataset();
	/**
	 * @brief Objeto {@link Scanner} para leer la entrada del usuario.
	 */
	private static Scanner scanner = new Scanner(System.in);

	/**
	 * @brief Método principal de la aplicación.
	 * <p>
	 * Contiene el bucle principal del menú interactivo para el usuario.
	 *
	 * @param args Argumentos de la línea de comandos (no utilizados).
	 * @throws IOException Si ocurre un error de E/S durante la lectura o escritura de archivos.
	 */
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

	/**
	 * @brief Muestra el menú principal de opciones al usuario.
	 */
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

	/**
	 * @brief Lee una opción numérica del usuario.
	 *
	 * @return La opción seleccionada por el usuario, o -1 si la entrada no es un número válido.
	 */
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

	/**
	 * @brief Procesa la opción seleccionada por el usuario, llamando al método correspondiente.
	 *
	 * @param opcion La opción numérica seleccionada por el usuario.
	 * @return `true` si el usuario seleccionó la opción de salir, `false` en caso contrario.
	 * @throws IOException Si ocurre un error de E/S en alguna de las operaciones.
	 */
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

	/**
	 * @brief Carga un dataset desde un archivo especificado por el usuario.
	 * <p>
	 * Una vez cargado, el dataset se preprocesa automáticamente según la configuración por defecto.
	 *
	 * @throws IOException Si ocurre un error durante la lectura del archivo.
	 */
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

	/**
	 * @brief Guarda el dataset actual en un archivo especificado por el usuario.
	 *
	 * @throws IOException Si ocurre un error durante la escritura del archivo.
	 */
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

	/**
	 * @brief Permite al usuario modificar el dataset actual, incluyendo añadir, eliminar o modificar instancias.
	 */
	private static void modificarDataset() {
		try {
			datos = modify(datos);
		} catch (IndexOutOfBoundsException e) {
			logger.error("Error de índice al modificar el dataset: {}", e.getMessage());
		}
	}

	/**
	 * @brief Muestra información detallada sobre el dataset actual, como su contenido, instancias específicas,
	 * y estadísticas de atributos cuantitativos y cualitativos.
	 */
	private static void mostrarInformacion() {
		try {
			info(datos);
		} catch (IllegalArgumentException e) {
			logger.error("Error al mostrar información del dataset: {}", e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			logger.error("Error de índice al mostrar información del dataset: {}", e.getMessage());
		}
	}

	/**
	 * @brief Inicia el proceso de experimentación del algoritmo KNN, permitiendo al usuario
	 * dividir el dataset y realizar predicciones.
	 *
	 * @throws IOException Si ocurre un error de E/S durante la experimentación.
	 */
	private static void realizarExperimentacion() throws IOException {
		try {
			experimentar(datos);
		} catch (IOException e) {
			logger.error("Error durante la experimentación: {}", e.getMessage());
			throw new IOException("Error durante la experimentación"); // Relanzar la excepción
		}
	}

	/**
	 * @brief Permite al usuario introducir una nueva instancia y clasificarla utilizando el algoritmo KNN.
	 *
	 * @throws InputMismatchException Si el usuario introduce un valor no numérico cuando se espera un número.
	 * @throws IllegalArgumentException Si se produce un error en los argumentos proporcionados (ej. k <= 0).
	 * @throws IndexOutOfBoundsException Si se accede a un índice fuera de los límites.
	 */
	static void algoritmoKNNInstancia() {
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

	/**
	 * @brief Solicita al usuario el valor de K para el algoritmo KNN.
	 *
	 * @return El valor de K introducido por el usuario.
	 * @throws InputMismatchException Si la entrada del usuario no es un número.
	 * @throws IllegalArgumentException Si el valor de K introducido es menor o igual a cero.
	 */
	private static int obtenerK() throws InputMismatchException, IllegalArgumentException {
		logger.info(MENSAJE_INTRODUCIR_K);
		int k = scanner.nextInt();
		if (k <= 0) {
			throw new IllegalArgumentException("El valor de k debe ser mayor que cero.");
		}
		return k;
	}

	/**
	 * @brief Obtiene los valores para una nueva instancia a clasificar a partir de la entrada del usuario.
	 *
	 * @return Un objeto {@link Instancia} con los valores introducidos.
	 * @throws IllegalArgumentException Si ocurre un error al convertir los valores introducidos.
	 */
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

	/**
	 * @brief Prepara una copia del dataset crudo y la instancia a clasificar aplicando el mismo
	 * preprocesamiento que el dataset principal.
	 *
	 * @param instanceToClassify La instancia que se va a clasificar.
	 * @return Una copia del dataset crudo con el preprocesamiento aplicado, lista para la clasificación.
	 * @throws IllegalArgumentException Si el número de valores introducidos no coincide con el número de atributos del dataset.
	 */
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

	/**
	 * @brief Aplica el preprocesamiento adecuado a un dataset basándose en la configuración de `datos.getPreprocesado()`.
	 *
	 * @param dataset El dataset al que se aplicará el preprocesamiento.
	 * @return Un nuevo {@link Dataset} con los atributos preprocesados.
	 */
	private static Dataset preprocesarDataset(Dataset dataset) {
		Preprocesado preprocesador = null;
		if (datos.getPreprocesado() == 2) {
			preprocesador = new Normalizacion();
		} else if (datos.getPreprocesado() == 3) {
			preprocesador = new Estandarizacion();
		}
		return (preprocesador != null) ? new Dataset(preprocesador.procesar(dataset)) : dataset;
	}

	/**
	 * @brief Registra el resultado de la clasificación de una instancia.
	 *
	 * @param claseElegida La clase predicha para la instancia, o `null` si la clasificación falló.
	 */
	private static void logClasificacionResultado(String claseElegida) {
		if (claseElegida != null) {
			logger.info("La clase elegida es: {}", claseElegida);
		} else {
			logger.warn("No se pudo clasificar la instancia.");
		}
	}


	/**
	 * @brief Permite al usuario especificar la ruta y el nombre de un archivo.
	 * Proporciona opciones para introducir el nombre, mostrar la ruta actual y cambiar la ruta.
	 *
	 * @param ruta La ruta inicial.
	 * @return El nombre del archivo introducido por el usuario.
	 */
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

	/**
	 * @brief Añade una nueva instancia al dataset. Solicita al usuario los valores de la instancia
	 * separados por comas.
	 *
	 * @param data El dataset al que se añadirá la instancia.
	 * @return El dataset modificado.
	 */
	private static Dataset agregarInstancia(Dataset data) {
		logger.info(MENSAJE_INTRODUCIR_VALORES);
		String valores = scanner.nextLine();
		String[] subcadenas = valores.split(",");
		ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
		logger.info("Añadiendo instancia: {}", arrayList);
		data.add(arrayList);
		return data;
	}

	/**
	 * @brief Elimina una instancia del dataset basándose en el índice proporcionado por el usuario.
	 *
	 * @param data El dataset del que se eliminará la instancia.
	 * @return El dataset modificado.
	 */
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

	/**
	 * @brief Modifica una instancia existente en el dataset. Solicita al usuario los nuevos valores
	 * y el índice de la instancia a modificar.
	 *
	 * @param data El dataset que se va a modificar.
	 * @return El dataset modificado.
	 */
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
				}
				instanciaAModificar.addClase(arrayListMod.get(arrayListMod.size()-1));
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

	/**
	 * @brief Wrapper para el método {@link #cambiarPesos(Dataset)}, que permite al usuario
	 * cambiar los pesos de los atributos en el dataset.
	 *
	 * @param data El dataset cuyos pesos de atributos se van a cambiar.
	 * @return El dataset con los pesos de atributos modificados.
	 */
	private static Dataset cambiarPesosWrapper(Dataset data) {
		return cambiarPesos(data);
	}

	/**
	 * @brief Proporciona un menú para modificar el dataset, incluyendo añadir, eliminar o modificar instancias,
	 * y cambiar los pesos de los atributos.
	 *
	 * @param data El dataset que se va a modificar.
	 * @return El dataset modificado.
	 */
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

	/**
	 * @brief Permite al usuario seleccionar una opción de preprocesamiento para el dataset.
	 * Las opciones incluyen datos crudos, normalización (Rango 0-1) y estandarización.
	 *
	 * @param data El dataset a preprocesar.
	 * @return El dataset con el preprocesamiento aplicado.
	 */
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

	/**
	 * @brief Permite al usuario cambiar los pesos de los atributos del dataset.
	 * Las opciones incluyen asignar pesos distintos a todos los atributos, un mismo peso para todos,
	 * o cambiar el peso de un atributo específico.
	 *
	 * @param data El dataset cuyos pesos de atributos se van a cambiar.
	 * @return El dataset con los pesos de atributos modificados.
	 */
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

	/**
	 * @brief Proporciona un menú para mostrar información del dataset, incluyendo el dataset completo,
	 * instancias individuales, y estadísticas de atributos cuantitativos y cualitativos.
	 *
	 * @param data El dataset del que se mostrará la información.
	 */
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

	/**
	 * @brief Muestra un menú de opciones para obtener información específica de atributos cuantitativos
	 * (nombre, media, máximo, mínimo, desviación típica).
	 *
	 * @param data El dataset que contiene los atributos.
	 */
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

	/**
	 * @brief Muestra un menú de opciones para obtener información específica de atributos cualitativos
	 * (nombre, número de clases, clases y frecuencia).
	 *
	 * @param data El dataset que contiene los atributos.
	 */
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

	/**
	 * @brief Permite al usuario realizar experimentaciones con el algoritmo KNN,
	 * incluyendo la generación de conjuntos de entrenamiento y prueba (normal o aleatoria),
	 * y la carga/guardado de estos datasets.
	 *
	 * @param datos El dataset sobre el que se realizará la experimentación.
	 * @throws IOException Si ocurre un error de E/S durante la operación.
	 */
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

	/**
	 * @brief Carga los datasets de entrenamiento y prueba previamente guardados y realiza la predicción.
	 * Solicita al usuario los nombres de los archivos y el valor de K.
	 *
	 * @param nuevo Objeto {@link Entrenamiento} donde se cargarán los datos.
	 * @param archivoCargar1 Ruta del archivo del dataset de entrenamiento.
	 * @param archivoCargar2 Ruta del archivo del dataset de prueba.
	 */
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

	/**
	 * @brief Realiza la generación de conjuntos de entrenamiento y prueba de forma aleatoria.
	 * Solicita al usuario el porcentaje para el conjunto de entrenamiento y la semilla.
	 *
	 * @param datos El dataset original.
	 * @return Un objeto {@link Entrenamiento} con los datasets de entrenamiento y prueba,
	 * o `null` si la entrada del usuario es inválida.
	 */
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