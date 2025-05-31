package datos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vectores.Vector;

/**
 * @file Instancia.java
 * @brief Representa una única instancia o registro de datos, compuesta por una lista de valores y una clase.
 *
 * Una instancia puede contener valores de diferentes tipos (numéricos o cadenas).
 * Ofrece métodos para acceder y manipular estos valores, incluyendo la extracción de un
 * {@link Vector} de valores numéricos (excluyendo la clase), operaciones de normalización
 * y estandarización, y gestión de la clase de la instancia.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class Instancia {
	/**
	 * @brief Lista de objetos que representan los valores de la instancia.
	 *  
	 * El último elemento de la lista se considera la clase de la instancia.
	 */
	private List<Object> valores;

	/**
	 * @brief Constructor por defecto.
	 *  
	 * Inicializa la lista de valores como una nueva {@link ArrayList} vacía.
	 */
	public Instancia(){
		this.valores = new ArrayList<Object>();
	}

	/**
	 * @brief Constructor que inicializa la instancia con una lista de objetos.
	 *  
	 * Si la lista proporcionada es nula, se inicializa con una lista vacía.
	 *
	 * @param nuevos La {@link List} de {@link Object} con los valores para la instancia.
	 */
	public Instancia(List<Object> nuevos){
		if (nuevos == null) {
			this.valores = new ArrayList<>(); // Or throw IllegalArgumentException, depending on desired behavior
		} else {
			this.valores = new ArrayList<>(nuevos); // THIS IS THE KEY CHANGE
		}
	}

	/**
	 * @brief Constructor que inicializa la instancia a partir de una cadena de texto.
	 *  
	 * La cadena debe contener valores separados por comas.
	 *
	 * @param nuevos La cadena de texto con los valores de la instancia (ej. "1.0,rojo,3.5").
	 */
	public Instancia(String nuevos){
		String[] subcadenas = nuevos.split(",");
		ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(subcadenas)); // This creates a new modifiable ArrayList
		this.valores = arrayList;
	}

	/**
	 * @brief Obtiene la lista de todos los valores de la instancia (incluyendo la clase).
	 *
	 * @return Una {@link List} de {@link Object} que contiene todos los valores.
	 */
	public List<Object> getValores() {
		return this.valores;
	}

	/**
	 * @brief Devuelve una representación en cadena de la lista de valores de la instancia.
	 *
	 * @return Una cadena de texto que representa la lista de valores.
	 */
	public String toString() {
		return valores.toString();
	}

	/**
	 * @brief Extrae los valores numéricos de la instancia (excluyendo el último elemento que es la clase)
	 * y los convierte en un objeto {@link Vector}.
	 *
	 * @return Un {@link Vector} que contiene los valores numéricos de la instancia.
	 */
	public Vector getVector() {
		Vector aux = new Vector();
		for (int i = 0; i < valores.size()-1; ++i) {
			if (valores.get(i) instanceof Double) {
				aux.add((Double) valores.get(i));
			} else if (valores.get(i) instanceof Integer) {
				aux.add((int) valores.get(i));
			}
		}
		return aux;
	}

	/**
	 * @brief Obtiene el valor de la clase de la instancia (el último elemento de la lista de valores).
	 *
	 * @return Una cadena de texto que representa la clase de la instancia.
	 * @throws IndexOutOfBoundsException Si la instancia está vacía y no tiene un elemento de clase.
	 */
	public String getClase() {
		return (String) this.valores.get(valores.size()-1);
	}

	/**
	 * @brief Normaliza los valores numéricos de la instancia (todos excepto la clase) utilizando la normalización Min-Max.
	 *  
	 * Este método modifica los valores numéricos internos de la instancia.
	 *
	 * @see vectores.Vector#normalize()
	 */
	public void normalizar() {
		Vector aux = this.getVector();
		aux.normalize();
		ArrayList<Object> arrayListObject = new ArrayList<>();
		for (Double d : aux.getValores()) {
			arrayListObject.add(d); // La conversión automática de tipos se encarga de convertir Double a Object
		}
		this.valores = arrayListObject;
	}

	/**
	 * @brief Estandariza los valores numéricos de la instancia (todos excepto la clase) utilizando la estandarización Z-score.
	 *  
	 * Este método modifica los valores numéricos internos de la instancia.
	 * Si la desviación estándar es cero, todos los valores numéricos se establecerán en 0.0.
	 *
	 * @see vectores.Vector#avg()
     */
	public void estandarizar() {
		Vector aux = this.getVector();
		if (aux.size() == 0) { // Handle empty vector case gracefully
			this.valores = new ArrayList<>(); // Clear values or keep as is? Current normalize clears.
			return;
		}

		double media = 0.0;
		for(int i = 0; i < aux.size(); ++i) {
			media += aux.get(i);
		}
		media =  media/aux.size();

		double auxiliar = 0; // sum of squared differences
		for(int i = 0; i < aux.size(); ++i) {
			auxiliar += (aux.get(i) - media) * (aux.get(i) - media);
		}

		// Corrected line: Divide by aux.size() for population variance
		double desviacion = 0.0;
		if (aux.size() > 0) { // Ensure no division by zero if aux.size() was 0 (already handled above but good for clarity)
			desviacion = Math.sqrt(auxiliar / aux.size());
		}


		// Handle division by zero for standard deviation
		if (desviacion == 0.0) {
			// If std dev is 0, all values are the same. Standardization makes them 0 (or original value if definition allows).
			// A common approach is to set them all to 0, or leave them as is if they are already 0.
			// Given normalization sets them to original value if range is 0, let's follow that.
			// Or more strictly, if std dev is 0, then x - mean is 0, so result is 0.
			ArrayList<Object> arrayListObject = new ArrayList<>();
			for (int i = 0; i < aux.size(); ++i) {
				arrayListObject.add(0.0); // All standardized values become 0
			}
			this.valores = arrayListObject;
			return;
		}

		for (int i = 0; i < aux.size(); ++i) {
			aux.set(i, (aux.get(i)-media)/desviacion);
		}
		ArrayList<Object> arrayListObject = new ArrayList<>();
		for (Double d : aux.getValores()) {
			arrayListObject.add(d);
		}
		this.valores = arrayListObject;
	}

	/**
	 * @brief Elimina el último valor de la instancia, que se considera la clase.
	 *
	 * @throws IndexOutOfBoundsException Si la instancia está vacía.
	 */
	public void deleteClase() {
		valores.remove(valores.size() - 1);
	}

	/**
	 * @brief Añade un nuevo valor de clase al final de la lista de valores de la instancia.
	 *
	 * @param clase La cadena de texto que representa el nuevo valor de clase.
	 */
	public void addClase(String clase) {
		valores.add(clase);
	}

	/**
	 * @brief Establece un nuevo valor en un índice específico de la lista de valores de la instancia.
	 *
	 * @param i El índice (basado en cero) donde se establecerá el nuevo valor.
	 * @param nuevo El nuevo objeto a establecer.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango de la lista de valores.
	 */
	public void set (int i, Object nuevo) {
		valores.set(i, nuevo);
	}

	/**
	 * @brief Devuelve una representación en cadena de los valores de la instancia, separados por comas.
	 *
	 * @return Una cadena de texto con los valores de la instancia, sin corchetes ni espacios adicionales.
	 */
	public String getValoresString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < valores.size(); ++i) {
			sb.append(valores.get(i));
			if (i < valores.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
}