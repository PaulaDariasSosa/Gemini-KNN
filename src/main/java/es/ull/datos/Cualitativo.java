package datos;

import java.util.ArrayList;
import java.util.List;

/**
 * @file Cualitativo.java
 * @brief Implementación de un atributo cualitativo.
 *
 * Esta clase extiende {@link Atributo} y representa un atributo
 * cuyos valores son cadenas de texto discretas (ej. "Rojo", "Verde", "Azul").
 * Proporciona métodos para gestionar estos valores, calcular clases únicas
 * y sus frecuencias.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class Cualitativo extends Atributo{
	/**
	 * @brief Lista de valores {@link String} asociados a este atributo cualitativo.
	 */
	private List<String> valores;

	/**
	 * @brief Constructor por defecto.
	 * <p>
	 * Inicializa el nombre del atributo como cadena vacía y la lista de valores como una nueva {@link ArrayList}.
	 */
	public Cualitativo() {
		this.nombre = "";
		this.valores = new ArrayList<String>();
	}

	/**
	 * @brief Constructor que inicializa el atributo con un nombre específico.
	 *
	 * @param name El nombre del atributo.
	 */
	public Cualitativo(String name) {
		this();
		this.nombre = name;
	}

	/**
	 * @brief Constructor que inicializa el atributo con un nombre y un primer valor.
	 *
	 * @param name El nombre del atributo.
	 * @param valor El primer valor {@link String} a añadir al atributo.
	 */
	public Cualitativo(String name, String valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}

	/**
	 * @brief Constructor que inicializa el atributo con un nombre y una lista de valores.
	 *
	 * @param name El nombre del atributo.
	 * @param valor La {@link List} de valores {@link String} para inicializar el atributo.
	 */
	public Cualitativo(String name, List<String> valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}

	/**
	 * @brief Constructor de copia.
	 * <p>
	 * Crea una nueva instancia de {@link Cualitativo} copiando el nombre y el peso
	 * de otro atributo cualitativo. La lista de valores se inicializa vacía.
	 *
	 * @param otro El objeto {@link Cualitativo} del cual copiar la información base.
	 */
	public Cualitativo(Cualitativo otro) {
		this();
		this.nombre = otro.getNombre();
		this.peso = otro.getPeso();
		this.valores = new ArrayList<String>(); // La lista de valores se inicializa vacía en el constructor de copia
	}

	/**
	 * @brief Obtiene la lista de todos los valores del atributo cualitativo.
	 *
	 * @return Una {@link List} de {@link String} que contiene todos los valores.
	 */
	@Override
	public List<String> getValores() {
		return this.valores;
	}

	/**
	 * @brief Establece una nueva lista de valores para el atributo cualitativo.
	 *
	 * @param nuevos La nueva {@link List} de {@link String} con los valores.
	 */
	public void setValores(List<String> nuevos) {
		this.valores = nuevos;
	}

	/**
	 * @brief Calcula y devuelve una lista de las clases (valores únicos) presentes en el atributo.
	 *
	 * @return Una {@link ArrayList} de {@link String} con las clases únicas.
	 */
	public List<String> clases() {
		ArrayList<String> clases = new ArrayList<>();
		for(int i = 0; i < this.valores.size(); ++i) {
			if(!clases.contains(this.valores.get(i))) clases.add(this.valores.get(i));
		}
		return clases;
	}

	/**
	 * @brief Calcula y devuelve el número de clases (valores únicos) presentes en el atributo.
	 *
	 * @return Un entero que representa la cantidad de clases únicas.
	 */
	public int nClases() {
		return this.clases().size();
	}

	/**
	 * @brief Calcula la frecuencia de cada clase (valor único) en el atributo.
	 * <p>
	 * La frecuencia se devuelve como una lista de proporciones (double),
	 * donde cada elemento corresponde a la frecuencia de una clase en el orden
	 * devuelto por {@link #clases()}.
	 *
	 * @return Una {@link List} de {@link Double} con las frecuencias de cada clase.
	 */
	public List<Double> frecuencia() {
		List<String> clases = this.clases();
		ArrayList<Double> frecuencias = new ArrayList<>();
		for (int j = 0; j < this.nClases(); ++j) {
			double auxiliar = 0;
			for(int i = 0; i < this.valores.size(); ++i) {
				if(clases.get(j).equals(this.valores.get(i))) auxiliar++;
			}
			frecuencias.add(auxiliar/this.valores.size());
		}
		return frecuencias;
	}

	/**
	 * @brief Obtiene el número de valores almacenados en este atributo cualitativo.
	 *
	 * @return Un entero que representa el tamaño de la lista de valores.
	 */
	@Override
	public int size() {
		return this.valores.size();
	}

	/**
	 * @brief Añade un nuevo valor a la lista de valores del atributo.
	 *
	 * @param valor El valor a añadir, que debe ser de tipo {@link String}.
	 * @throws ClassCastException Si el objeto `valor` no es de tipo {@link String}.
	 */
	@Override
	public void add(Object valor) {
		valores.add((String) valor);
	}

	/**
	 * @brief Obtiene el valor en un índice específico de la lista de valores.
	 *
	 * @param i El índice (basado en cero) del valor a obtener.
	 * @return El valor {@link String} en el índice especificado.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango de la lista de valores.
	 */
	@Override
	public Object getValor(int i) {
		return valores.get(i);

	}

	/**
	 * @brief Elimina el valor en un índice específico de la lista de valores.
	 *
	 * @param index El índice (basado en cero) del valor a eliminar.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango de la lista de valores.
	 */
	@Override
	public void delete(int index) {
		valores.remove(index);
	}

	/**
	 * @brief Devuelve una representación en cadena de la lista de valores del atributo.
	 *
	 * @return Una cadena de texto que representa la lista de valores.
	 */
	@Override
	public String toString() {
		return valores.toString();

	}

	/**
	 * @brief Elimina todos los valores de la lista del atributo.
	 */
	@Override
	public void clear() {
		valores.clear();
	}

}