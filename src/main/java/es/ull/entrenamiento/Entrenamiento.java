package entrenamiento;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import clasificacion.KNN;
import datos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vectores.Matriz;

/**
 * @file Entrenamiento.java
 * @brief Clase que gestiona la división de un dataset en conjuntos de entrenamiento y prueba,
 * y la evaluación de un clasificador K-Nearest Neighbors (KNN).
 *
 * Permite dividir un dataset de forma secuencial o aleatoria, y luego evaluar
 * el rendimiento del clasificador KNN a través de una matriz de confusión y un reporte de clasificación.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class Entrenamiento {
	/**
	 * @brief El {@link Dataset} utilizado para el entrenamiento del modelo.
	 */
	private Dataset train;
	/**
	 * @brief El {@link Dataset} utilizado para la prueba del modelo.
	 */
	private Dataset test;
	/**
	 * @brief Lista de las clases (etiquetas) presentes en el dataset.
	 */
	private List<String> clases;
	/**
	 * @brief Separador utilizado en los informes de salida.
	 */
	private static final String REPORT_SEPARATOR = "-------------------------";

	/**
	 * @brief Constructor por defecto.
	 * <p>
	 * Inicializa los datasets de entrenamiento y prueba a nulo.
	 */
	public Entrenamiento() {
	}

	/**
	 * @brief Constructor que divide un dataset dado en conjuntos de entrenamiento y prueba.
	 * <p>
	 * La división se realiza de forma secuencial: las primeras `porcentaje`% instancias
	 * van al conjunto de entrenamiento y el resto al conjunto de prueba.
	 *
	 * @param datos El {@link Dataset} original a dividir.
	 * @param porcentaje El porcentaje (ej. 0.7 para 70%) de datos que se destinarán a entrenamiento.
	 * @throws IllegalArgumentException Si el porcentaje no está en el rango (0, 1).
	 */
	public Entrenamiento(Dataset datos, double porcentaje) {
		Dataset trainset = new Dataset(datos.getAtributosEmpty());
		Dataset testset = new Dataset(datos.getAtributosEmpty());
		clases = datos.getClases();
		int indice = 0;
		while(indice < datos.numeroCasos()*porcentaje) {
			trainset.add(datos.getInstance(indice));
			indice += 1;
		}
		for (int i = indice; i < datos.numeroCasos(); ++i) {
			testset.add(datos.getInstance(i));
		}
		this.test = testset;
		this.train = trainset;
		this.test.setPreprocesado(datos.getPreprocesado());
		this.train.setPreprocesado(datos.getPreprocesado());
	}

	/**
	 * @brief Constructor que divide un dataset dado en conjuntos de entrenamiento y prueba de forma aleatoria.
	 * <p>
	 * Utiliza una semilla para la generación de números aleatorios, lo que permite reproducibilidad.
	 *
	 * @param datos El {@link Dataset} original a dividir.
	 * @param porcentaje El porcentaje (ej. 0.7 para 70%) de datos que se destinarán a entrenamiento.
	 * @param semilla La semilla para el generador de números aleatorios.
	 * @throws IllegalArgumentException Si el porcentaje no está en el rango (0, 1).
	 */
	public Entrenamiento(Dataset datos, double porcentaje, int semilla) {
		Dataset trainset = new Dataset(datos.getAtributosEmpty());
		Dataset testset = new Dataset(datos.getAtributosEmpty());
		clases = datos.getClases();
		ArrayList<Integer> indices = new ArrayList<>();
		// Suprimir la advertencia de SonarCloud (java:S2245) ya que el uso de
		// Random con una semilla es intencional para la repetibilidad de la
		// división del dataset con fines de experimentación reproducible.
		@SuppressWarnings("java:S2245")
		Random random = new Random(semilla);
		while(indices.size() < datos.numeroCasos()*porcentaje) {
			int randomNumber = random.nextInt(datos.numeroCasos());
			if (!indices.contains(randomNumber)) {
				trainset.add(datos.getInstance(randomNumber));
				indices.add(randomNumber);
			}
		}
		for (int i = 0; i < datos.numeroCasos(); ++i) {
			if (!indices.contains(i)) {
				testset.add(datos.getInstance(i));
			}
		}
		this.test = testset;
		this.train =  trainset;
		this.test.setPreprocesado(datos.getPreprocesado());
		this.train.setPreprocesado(datos.getPreprocesado());
	}

	/**
	 * @brief Genera y muestra un reporte detallado de las predicciones del clasificador KNN.
	 * <p>
	 * Calcula la precisión global y métricas por clase (precisión, exhaustividad, F1-score)
	 * para el clasificador KNN utilizando el conjunto de prueba.
	 *
	 * @param valorK El valor de K a utilizar en el clasificador KNN.
	 */
	public void generarPrediccion(int valorK) {
		Dataset pruebas = new Dataset(test);
		List<String> predicciones = new ArrayList<>();
		List<String> clasesReales = new ArrayList<>();
		Double aciertos = 0.0;

		for (int i = 0; i < pruebas.numeroCasos(); ++i) {
			ArrayList<Object> instance = new ArrayList<>();
			// Construir una nueva instancia sin la clase para la clasificación
			for (int j = 0; j < pruebas.numeroAtributos(); ++j) {
				instance.add(pruebas.getInstance(i).getValores().get(j));
			}
			Instancia nueva = new Instancia(instance);
			// Clasificar la instancia con KNN
			String clasePredicha = (new KNN(valorK).clasificar(train, nueva));
			predicciones.add(clasePredicha);
			clasesReales.add(test.getInstance(i).getClase());

			if (clasePredicha != null && clasePredicha.equals(test.getInstance(i).getClase())) {
				aciertos += 1;
			}
		}

		Logger logger = LoggerFactory.getLogger(Entrenamiento.class);
		if (logger.isInfoEnabled()) {
			double precisionGlobal = aciertos / test.numeroCasos() * 100;
			logger.info("Precisión global: {} / {} = {}%", aciertos, test.numeroCasos(), String.format("%.2f", precisionGlobal));

			// Calcular y loguear las métricas adicionales
			Map<String, Map<String, Integer>> resultadosPorClase = calcularResultadosPorClase(clasesReales, predicciones, clases);
			imprimirReporteClasificacion(resultadosPorClase, logger);
		}
	}

	/**
	 * @brief Calcula los resultados de Verdaderos Positivos (TP), Falsos Positivos (FP) y Falsos Negativos (FN)
	 * para cada clase.
	 *
	 * @param reales La {@link List} de clases reales para cada instancia.
	 * @param predichas La {@link List} de clases predichas para cada instancia.
	 * @param etiquetasClase La {@link List} de todas las etiquetas de clase posibles en el dataset.
	 * @return Un {@link Map} donde la clave es el nombre de la clase y el valor es otro {@link Map}
	 * que contiene los conteos de "TP", "FP", y "FN" para esa clase.
	 */
	private Map<String, Map<String, Integer>> calcularResultadosPorClase(List<String> reales, List<String> predichas, List<String> etiquetasClase) {
		Map<String, Map<String, Integer>> resultados = new HashMap<>();
		for (String clase : etiquetasClase) {
			resultados.put(clase, new HashMap<>());
			resultados.get(clase).put("TP", 0);
			resultados.get(clase).put("FP", 0);
			resultados.get(clase).put("FN", 0);
		}

		for (int i = 0; i < reales.size(); i++) {
			String real = reales.get(i);
			String predicha = predichas.get(i);

			for (String clase : etiquetasClase) {
				if (real.equals(clase) && predicha.equals(clase)) {
					resultados.get(clase).put("TP", resultados.get(clase).get("TP") + 1);
				} else if (!real.equals(clase) && predicha.equals(clase)) {
					resultados.get(clase).put("FP", resultados.get(clase).get("FP") + 1);
				} else if (real.equals(clase) && !predicha.equals(clase)) {
					resultados.get(clase).put("FN", resultados.get(clase).get("FN") + 1);
				}
			}
		}
		return resultados;
	}

	/**
	 * @brief Imprime un reporte de clasificación detallado en el log.
	 * <p>
	 * Incluye métricas por clase (precisión, exhaustividad, F1-score) y métricas macro y ponderadas.
	 *
	 * @param resultados Un {@link Map} con los conteos de TP, FP, FN para cada clase.
	 * @param logger La instancia de {@link Logger} a utilizar para imprimir el reporte.
	 */
	private void imprimirReporteClasificacion(Map<String, Map<String, Integer>> resultados, Logger logger) {
		logger.info("\nReporte de Clasificación:");
		logger.info(REPORT_SEPARATOR);

		// Imprimir métricas por clase
		for (Map.Entry<String, Map<String, Integer>> entry : resultados.entrySet()) {
			int tp = entry.getValue().get("TP");
			int fp = entry.getValue().get("FP");
			int fn = entry.getValue().get("FN");
			int support = tp + fn; // Soporte es el número de instancias reales de la clase

			double[] metrics = calcularMetricasClase(tp, fp, fn);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Clase %s: Precisión = %.2f%%, Exhaustividad = %.2f%%, F1-score = %.2f%%, Soporte = %d",
						entry.getKey(), metrics[0] * 100, metrics[1] * 100, metrics[2] * 100, support));
			}
		}

		double[] macroMetrics = calcularMacroMetrics(resultados);
		if (logger.isInfoEnabled()) {
			logger.info(REPORT_SEPARATOR);
			logger.info(String.format("Precisión Macro Promedio: %.2f%%", macroMetrics[0] * 100));
			logger.info(String.format("Exhaustividad Macro Promedio: %.2f%%", macroMetrics[1] * 100));
			logger.info(String.format("F1-score Macro Promedio: %.2f%%", macroMetrics[2] * 100));
		}


		double[] weightedMetrics = calcularWeightedMetrics(resultados);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Precisión Ponderada: %.2f%%", weightedMetrics[0] * 100));
			logger.info(String.format("Exhaustividad Ponderada: %.2f%%", weightedMetrics[1] * 100));
			logger.info(String.format("F1-score Ponderado: %.2f%%", weightedMetrics[2] * 100));
		}

		logger.info(REPORT_SEPARATOR);
	}

	/**
	 * @brief Calcula la precisión, exhaustividad (recall) y F1-score para una clase dada.
	 *
	 * @param tp El número de verdaderos positivos (True Positives).
	 * @param fp El número de falsos positivos (False Positives).
	 * @param fn El número de falsos negativos (False Negatives).
	 * @return Un array de {@link Double} conteniendo [precisión, exhaustividad, F1-score].
	 * Retorna 0.0 para cualquier métrica si el denominador es cero para evitar divisiones por cero.
	 */
	private double[] calcularMetricasClase(int tp, int fp, int fn) {
		double precision = (tp + fp == 0) ? 0 : (double) tp / (tp + fp);
		double recall = (tp + fn == 0) ? 0 : (double) tp / (tp + fn);
		double f1 = (precision == 0 || recall == 0) ? 0 : 2 * (precision * recall) / (precision + recall);
		return new double[]{precision, recall, f1};
	}

	/**
	 * @brief Calcula las métricas macro-promedio (precisión, exhaustividad, F1-score)
	 * promediando las métricas de cada clase.
	 *
	 * @param resultados Un {@link Map} con los conteos de TP, FP, FN para cada clase.
	 * @return Un array de {@link Double} conteniendo [precisión macro, exhaustividad macro, F1-score macro].
	 */
	private double[] calcularMacroMetrics(Map<String, Map<String, Integer>> resultados) {
		double precisionMacro = 0;
		double recallMacro = 0;
		double f1Macro = 0;
		for (Map.Entry<String, Map<String, Integer>> entry : resultados.entrySet()) {
			double[] metrics = calcularMetricasClase(entry.getValue().get("TP"), entry.getValue().get("FP"), entry.getValue().get("FN"));
			precisionMacro += metrics[0];
			recallMacro += metrics[1];
			f1Macro += metrics[2];
		}
		return new double[]{precisionMacro / resultados.size(), recallMacro / resultados.size(), f1Macro / resultados.size()};
	}

	/**
	 * @brief Calcula las métricas ponderadas (precisión, exhaustividad, F1-score)
	 * promediando las métricas de cada clase, ponderadas por el número de instancias
	 * reales de esa clase (soporte).
	 *
	 * @param resultados Un {@link Map} con los conteos de TP, FP, FN para cada clase.
	 * @return Un array de {@link Double} conteniendo [precisión ponderada, exhaustividad ponderada, F1-score ponderado].
	 */
	private double[] calcularWeightedMetrics(Map<String, Map<String, Integer>> resultados) {
		double precisionPonderada = 0;
		double recallPonderada = 0;
		double f1Ponderada = 0;
		int totalSupport = 0;
		for (Map.Entry<String, Map<String, Integer>> entry : resultados.entrySet()) {
			int tp = entry.getValue().get("TP");
			int fp = entry.getValue().get("FP");
			int fn = entry.getValue().get("FN");
			int support = tp + fn; // Soporte para la clase actual
			totalSupport += support;
			double[] metrics = calcularMetricasClase(tp, fp, fn);
			precisionPonderada += metrics[0] * support;
			recallPonderada += metrics[1] * support;
			f1Ponderada += metrics[2] * support;
		}
		return new double[]{(totalSupport == 0) ? 0 : precisionPonderada / totalSupport,
				(totalSupport == 0) ? 0 : recallPonderada / totalSupport,
				(totalSupport == 0) ? 0 : f1Ponderada / totalSupport};
	}

	/**
	 * @brief Calcula y muestra la matriz de confusión para la clasificación KNN.
	 * <p>
	 * La matriz de confusión es una tabla que permite visualizar el rendimiento
	 * de un algoritmo de clasificación, mostrando el número de predicciones correctas
	 * e incorrectas para cada clase.
	 *
	 * @param valorK El valor de K a utilizar en el clasificador KNN.
	 * @throws IllegalStateException Si el conjunto de prueba o las clases están vacías.
	 */
	public void generarMatriz(int valorK) {
		Dataset pruebas = new Dataset(test);
		Matriz confusion = new Matriz (clases.size(), clases.size());
		for (int i = 0; i < pruebas.numeroCasos(); ++i) {
			ArrayList<Object> instance = new ArrayList<>();
			for (int j = 0; j < pruebas.numeroAtributos(); ++j) {
				instance.add(pruebas.getInstance(i).getValores().get(j));
			}
			Instancia nueva = new Instancia(instance);
			String clase = (new KNN(valorK).clasificar(train, nueva));
			confusion.set( clases.indexOf(test.getInstance(i).getClase()),clases.indexOf(clase),confusion.get(clases.indexOf(test.getInstance(i).getClase()),clases.indexOf(clase))+1);
		}
		Logger logger = LoggerFactory.getLogger(Entrenamiento.class);
		if(logger.isInfoEnabled()) {
			logger.info(clases.toString());
		}
		confusion.print();
	}

	/**
	 * @brief Escribe los conjuntos de entrenamiento y prueba en archivos separados.
	 *
	 * @param filename1 El nombre (o ruta) del archivo para el conjunto de entrenamiento.
	 * @param filename2 El nombre (o ruta) del archivo para el conjunto de prueba.
	 * @throws IOException Si ocurre un error de E/S durante la escritura de los archivos.
	 */
	public void write(String filename1, String filename2) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename1))) {
			train.write(filename1);
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename2))) {
			test.write(filename2);
		}
	}

	/**
	 * @brief Lee los conjuntos de entrenamiento y prueba desde archivos separados.
	 * <p>
	 * Inicializa los datasets de entrenamiento y prueba a partir de los contenidos de los archivos.
	 * Las clases se actualizan para incluir todas las clases presentes en ambos datasets.
	 *
	 * @param filename1 El nombre (o ruta) del archivo para el conjunto de entrenamiento.
	 * @param filename2 El nombre (o ruta) del archivo para el conjunto de prueba.
	 * @throws IOException Si ocurre un error de E/S durante la lectura de los archivos.
	 */
	public void read(String filename1, String filename2) throws IOException {
		train = new Dataset(filename1);
		test = new Dataset(filename2);
		List<String> clasesA = train.getClases();
		List<String> clasesB = test.getClases();
		for (int i = 0; i < clasesB.size(); i++) {
			if (!clasesA.contains(clasesB.get(i))) clasesA.add(clasesB.get(i));
		}
		clases = clasesA; // Ahora 'clases' contiene todas las clases únicas de ambos datasets
	}

}