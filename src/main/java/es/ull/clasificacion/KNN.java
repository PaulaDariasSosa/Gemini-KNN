package clasificacion;

import datos.Dataset;
import datos.Instancia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vectores.Vector;

import java.util.*;

/**
 * @file KNN.java
 * @brief Implementación del algoritmo de clasificación K-Nearest Neighbors (KNN).
 *
 * Esta clase proporciona la funcionalidad para clasificar una nueva instancia
 * basándose en las K instancias más cercanas de un conjunto de datos de entrenamiento.
 * Utiliza la distancia euclídea ponderada para determinar la cercanía.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class KNN {
	/**
	 * @brief El número de vecinos más cercanos (K) a considerar para la clasificación.
	 */
	private int k;

	/**
	 * @brief Constructor de la clase KNN.
	 *
	 * @param k El número de vecinos a utilizar para la clasificación. Debe ser un entero positivo.
	 * @throws IllegalArgumentException Si k es menor o igual a 0.
	 */
	public KNN(int k) {
		this.k = k;
	}

	/**
	 * @brief Clasifica una instancia de prueba basándose en un dataset de entrenamiento.
	 * <p>
	 * Identifica los K vecinos más cercanos en el dataset de entrenamiento a la instancia de prueba,
	 * y luego determina la clase mayoritaria entre estos vecinos.
	 *
	 * @param entrenamiento El {@link Dataset} utilizado para entrenar el clasificador.
	 * @param prueba La {@link Instancia} a clasificar.
	 * @return La clase predicha para la instancia de prueba como una cadena de texto, o `null`
	 * si el dataset de entrenamiento o la instancia de prueba son inválidos.
	 */
	public String clasificar(Dataset entrenamiento, Instancia prueba) {
		if (entrenamiento == null || entrenamiento.numeroCasos() == 0 || prueba == null || prueba.getVector() == null) {
			return null;
		}

		// PriorityQueue para mantener los K vecinos más cercanos.
		// El comparador es inverso porque queremos que el elemento de mayor distancia (peor)
		// se elimine cuando la cola exceda el tamaño k.
		PriorityQueue<Vecino> vecinos = new PriorityQueue<>(k, Comparator.comparingDouble(Vecino::getDistancia).reversed());

		for (int i = 0; i < entrenamiento.numeroCasos(); i++) {
			Instancia instanciaEntrenamiento = entrenamiento.getInstance(i);
			if (instanciaEntrenamiento.getVector() != null) {
				List<Double> pesosDouble = entrenamiento.getPesosDouble();
				// Se asume que el último elemento de pesosDouble es el peso de la clase,
				// por lo que el tamaño del vector de la instancia de prueba debe ser
				// igual a pesosDouble.size() - 1.
				if (pesosDouble != null && pesosDouble.size()-1 == prueba.getVector().size()) {
					double distancia = calcularDistanciaEuclideaCuadrada(prueba.getVector(), instanciaEntrenamiento.getVector(), pesosDouble);
					String clase = instanciaEntrenamiento.getClase();

					// Añadir el vecino a la cola de prioridad
					vecinos.offer(new Vecino(distancia, clase));

					// Si la cola excede K elementos, eliminar el más lejano (el que tiene mayor distancia)
					vecinos = pollVecinos(vecinos);
				} else {
					Logger logger = LoggerFactory.getLogger(KNN.class);
					if (logger.isErrorEnabled()) {
						logger.error("Error: La lista de pesos no es válida o su tamaño no coincide con el número de atributos.");
					}
				}
			}
		}

		// Obtener la clase mayoritaria de los K vecinos
		return obtenerClaseMayoritaria(vecinos);
	}

	/**
	 * @brief Ajusta el tamaño de la cola de prioridad para mantener solo los K elementos más cercanos.
	 *
	 * Si la cola de prioridad tiene más de `k` elementos, elimina el elemento con la distancia más grande
	 * (ya que el comparador es inverso y el elemento más grande estará en la cima de la cola).
	 *
	 * @param vecinos La {@link PriorityQueue} de {@link Vecino}s a ajustar.
	 * @return La {@link PriorityQueue} ajustada con un máximo de K vecinos.
	 */
	private PriorityQueue<Vecino> pollVecinos(PriorityQueue<Vecino> vecinos) {
		if (vecinos.size() > k) {
			// Eliminar el vecino más lejano si hay más de k vecinos
			vecinos.poll(); // Mantener solo los k vecinos más cercanos
		}
		return vecinos;
	}

	/**
	 * @brief Calcula la distancia euclídea al cuadrado entre dos vectores, ponderada por los pesos.
	 *
	 * @param v1 El primer {@link Vector} de valores.
	 * @param v2 El segundo {@link Vector} de valores.
	 * @param pesos Una {@link List} de {@link Double} que contiene los pesos para cada dimensión.
	 * El tamaño de `pesos` debe ser igual al tamaño de los vectores más uno (para la clase).
	 * @return La distancia euclídea al cuadrado ponderada entre los dos vectores.
	 * Retorna {@link Double#MAX_VALUE} y registra un error si los inputs son inválidos.
	 */
	private double calcularDistanciaEuclideaCuadrada(Vector v1, Vector v2, List<Double> pesos) {
		if (v1 == null || v2 == null || pesos == null || v1.size() != v2.size() || v1.size() != pesos.size()-1) {
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

	/**
	 * @brief Determina la clase mayoritaria entre un conjunto de vecinos.
	 * <p>
	 * Cuenta la frecuencia de cada clase entre los vecinos y devuelve la clase
	 * con el conteo más alto. En caso de empate, la clase devuelta puede depender
	 * del orden de iteración del mapa.
	 *
	 * @param vecinos Una {@link PriorityQueue} de {@link Vecino}s de la cual obtener las clases.
	 * @return La clase (como {@link String}) que aparece con mayor frecuencia entre los vecinos.
	 */
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

	/**
	 * @brief Clase interna privada para representar un vecino en el algoritmo KNN.
	 * <p>
	 * Contiene la distancia a la instancia de prueba y la clase asociada a esa instancia.
	 */
	private static class Vecino {
		/**
		 * @brief La distancia de este vecino a la instancia de prueba.
		 */
		private double distancia;
		/**
		 * @brief La clase a la que pertenece este vecino.
		 */
		private String clase;

		/**
		 * @brief Constructor para crear un objeto Vecino.
		 *
		 * @param distancia La distancia calculada.
		 * @param clase La clase del vecino.
		 */
		public Vecino(double distancia, String clase) {
			this.distancia = distancia;
			this.clase = clase;
		}

		/**
		 * @brief Obtiene la distancia del vecino.
		 *
		 * @return La distancia como un {@link Double}.
		 */
		public double getDistancia() {
			return distancia;
		}

		/**
		 * @brief Obtiene la clase del vecino.
		 *
		 * @return La clase como un {@link String}.
		 */
		public String getClase() {
			return clase;
		}
	}
}