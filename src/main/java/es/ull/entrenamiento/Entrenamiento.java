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

public class Entrenamiento {
	private Dataset train;
	private Dataset test;
	private List<String> clases;
	
	public Entrenamiento() {
	}
	
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

	public void generarPrediccion(int valorK) {
		Dataset pruebas = new Dataset(test);
		List<String> predicciones = new ArrayList<>();
		List<String> clasesReales = new ArrayList<>();
		Double aciertos = 0.0;

		for (int i = 0; i < pruebas.numeroCasos(); ++i) {
			ArrayList<Object> instance = new ArrayList<>();
			for (int j = 0; j < pruebas.numeroAtributos(); ++j) {
				instance.add(pruebas.getInstance(i).getValores().get(j));
			}
			Instancia nueva = new Instancia(instance);
			String clasePredicha = (new KNN(valorK).clasificar(train, nueva));
			predicciones.add(clasePredicha);
			clasesReales.add(test.getInstance(i).getClase());

			if (clasePredicha != null && clasePredicha.equals(test.getInstance(i).getClase())) {
				aciertos += 1;
			}
		}

		Logger logger = LoggerFactory.getLogger(Entrenamiento.class);
		if (logger.isInfoEnabled()) {
			double precisionGlobal = (double) aciertos / test.numeroCasos() * 100;
			logger.info("Precisión global: {} / {} = {}%", aciertos, test.numeroCasos(), String.format("%.2f", precisionGlobal));

			// Calcular y loguear las métricas adicionales
			Map<String, Map<String, Integer>> resultadosPorClase = calcularResultadosPorClase(clasesReales, predicciones, clases);
			imprimirReporteClasificacion(resultadosPorClase, test.numeroCasos(), logger);
		}
	}

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

	private void imprimirReporteClasificacion(Map<String, Map<String, Integer>> resultados, int totalInstancias, Logger logger) {
		logger.info("\nReporte de Clasificación:");
		logger.info("-------------------------");
		double precisionMacro = 0;
		double recallMacro = 0;
		double f1Macro = 0;
		int totalSupport = 0;

		for (Map.Entry<String, Map<String, Integer>> entry : resultados.entrySet()) {
			String clase = entry.getKey();
			int tp = entry.getValue().get("TP");
			int fp = entry.getValue().get("FP");
			int fn = entry.getValue().get("FN");
			int support = tp + fn; // Número real de instancias de esta clase
			totalSupport += support;

			double precision = (tp + fp == 0) ? 0 : (double) tp / (tp + fp);
			double recall = (tp + fn == 0) ? 0 : (double) tp / (tp + fn);
			double f1 = (precision == 0 || recall == 0) ? 0 : 2 * (precision * recall) / (precision + recall);

			logger.info(String.format("Clase %s: Precisión = %.2f, Exhaustividad = %.2f, F1-score = %.2f, Support = %d",
					clase, precision * 100, recall * 100, f1 * 100, support));

			precisionMacro += precision;
			recallMacro += recall;
			f1Macro += f1;
		}

		precisionMacro /= resultados.size();
		recallMacro /= resultados.size();
		f1Macro /= resultados.size();

		logger.info("-------------------------");
		logger.info(String.format("Precisión Macro Promedio: %.2f%%", precisionMacro * 100));
		logger.info(String.format("Exhaustividad Macro Promedio: %.2f%%", recallMacro * 100));
		logger.info(String.format("F1-score Macro Promedio: %.2f%%", f1Macro * 100));

		// Calcular métricas ponderadas por el soporte
		double precisionPonderada = 0;
		double recallPonderada = 0;
		double f1Ponderada = 0;
		for (Map.Entry<String, Map<String, Integer>> entry : resultados.entrySet()) {
			int tp = entry.getValue().get("TP");
			int fp = entry.getValue().get("FP");
			int fn = entry.getValue().get("FN");
			int support = tp + fn;
			double precision = (tp + fp == 0) ? 0 : (double) tp / (tp + fp);
			double recall = (tp + fn == 0) ? 0 : (double) tp / (tp + fn);
			double f1 = (precision == 0 || recall == 0) ? 0 : 2 * (precision * recall) / (precision + recall);
			precisionPonderada += precision * support;
			recallPonderada += recall * support;
			f1Ponderada += f1 * support;
		}
		logger.info(String.format("Precisión Ponderada: %.2f%%", (totalSupport == 0) ? 0 : (precisionPonderada / totalSupport) * 100));
		logger.info(String.format("Exhaustividad Ponderada: %.2f%%", (totalSupport == 0) ? 0 : (recallPonderada / totalSupport) * 100));
		logger.info(String.format("F1-score Ponderado: %.2f%%", (totalSupport == 0) ? 0 : (f1Ponderada / totalSupport) * 100));
		logger.info("-------------------------");
	}
	
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
	
	public void write(String filename1, String filename2) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename1))) {
            train.write(filename1);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename2))) {
            test.write(filename2);
        }
    }
	
	public void read(String filename1, String filename2) throws IOException {
		train = new Dataset(filename1);
        test = new Dataset(filename2);
        List<String> clasesA = train.getClases();
        List<String> clasesB = test.getClases();
        for (int i = 0; i < clasesB.size(); i++) {
        	if (!clasesA.contains(clasesB.get(i))) clasesA.add(clasesB.get(i));
        }
        clases = clasesA;
    }
	
}
