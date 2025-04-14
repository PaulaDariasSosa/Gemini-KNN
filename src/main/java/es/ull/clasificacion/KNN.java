package clasificacion;

import datos.Dataset;
import datos.Instancia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vectores.Vector;

import java.util.*;

public class KNN {
	private int k;

	public KNN(int k) {
		this.k = k;
	}

	public String clasificar(Dataset entrenamiento, Instancia prueba) {
		if (entrenamiento == null || entrenamiento.numeroCasos() == 0 || prueba == null || prueba.getVector() == null) {
			return null;
		}

		PriorityQueue<Vecino> vecinos = new PriorityQueue<>(k, Comparator.comparingDouble(Vecino::getDistancia).reversed());
		for (int i = 0; i < entrenamiento.numeroCasos(); i++) {
			Instancia instanciaEntrenamiento = entrenamiento.getInstance(i);
			if (instanciaEntrenamiento.getVector() != null) {
				List<Double> pesosDouble = entrenamiento.getPesosDouble();
				if (pesosDouble != null && pesosDouble.size() == prueba.getVector().size()) {
					double distancia = calcularDistanciaEuclideaCuadrada(prueba.getVector(), instanciaEntrenamiento.getVector(), pesosDouble);
					String clase = instanciaEntrenamiento.getClase();
					vecinos.offer(new Vecino(distancia, clase));
					vecinos = pollVecinos(vecinos);
				} else {
					Logger logger = LoggerFactory.getLogger(KNN.class);
					if (logger.isErrorEnabled()) {
						logger.error("Error: La lista de pesos no es válida o su tamaño no coincide con el número de atributos.");
					}
				}
			}
		}

		return obtenerClaseMayoritaria(vecinos);
	}

	private PriorityQueue<Vecino> pollVecinos(PriorityQueue<Vecino> vecinos) {
		if (vecinos.size() > k) {
			// Eliminar el vecino más lejano si hay más de k vecinos
			vecinos.poll(); // Mantener solo los k vecinos más cercanos
		}
		return vecinos;
	}

	private double calcularDistanciaEuclideaCuadrada(Vector v1, Vector v2, java.util.List<Double> pesos) {
		if (v1 == null || v2 == null || pesos == null || v1.size() != v2.size() || v1.size() != pesos.size()) {
			Logger logger = LoggerFactory.getLogger(KNN.class);
			if (logger.isErrorEnabled()) {
				logger.error("Error: Los vectores y la lista de pesos deben ser no nulos y tener el mismo tamaño para calcular la distancia euclídea ponderada.");
			}
			return Double.MAX_VALUE;
		}
		double distanciaCuadrada = 0;
		for (int i = 0; i < v1.size(); i++) {
			double diferencia = v1.get(i) - v2.get(i);
			distanciaCuadrada += pesos.get(i) * diferencia * diferencia;
		}
		return distanciaCuadrada;
	}

	private String obtenerClaseMayoritaria(PriorityQueue<Vecino> vecinos) {
		Map<String, Integer> conteoClases = new HashMap<>();
		for (Vecino vecino : vecinos) {
			conteoClases.put(vecino.getClase(), conteoClases.getOrDefault(vecino.getClase(), 0) + 1);
		}

		String claseMayoritaria = null;
		int maxConteo = -1;
		for (Map.Entry<String, Integer> entry : conteoClases.entrySet()) {
			if (entry.getValue() > maxConteo) {
				maxConteo = entry.getValue();
				claseMayoritaria = entry.getKey();
			}
		}
		return claseMayoritaria;
	}

	private static class Vecino {
		private double distancia;
		private String clase;

		public Vecino(double distancia, String clase) {
			this.distancia = distancia;
			this.clase = clase;
		}

		public double getDistancia() {
			return distancia;
		}

		public String getClase() {
			return clase;
		}
	}
}