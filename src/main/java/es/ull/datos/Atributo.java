package datos;

/**
 * @file Atributo.java
 * @brief Clase abstracta base para representar un atributo en un dataset.
 *
 * Define la estructura común y los métodos abstractos que deben implementar
 * las clases concretas de atributos (ej. Cuantitativo, Cualitativo).
 * Un atributo tiene un nombre y un peso que indica su importancia.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public abstract class Atributo {
	/**
	 * @brief El peso o importancia del atributo.
	 * <p>
	 * Por defecto se inicializa a 1.
	 */
	protected double peso = 1;
	/**
	 * @brief El nombre del atributo.
	 */
	protected String nombre;

	/**
	 * @brief Método abstracto para obtener todos los valores asociados a este atributo.
	 *
	 * Las clases concretas deben implementar cómo se devuelven los valores
	 * (ej. una lista de números, una lista de cadenas, etc.).
	 *
	 * @return Un objeto que contiene los valores del atributo (la implementación concreta definirá el tipo).
	 */
	public abstract Object getValores();

	/**
	 * @brief Obtiene el nombre del atributo.
	 *
	 * @return El nombre del atributo como una cadena de texto.
	 */
	public String getNombre() {
		return this.nombre;
	}

	/**
	 * @brief Obtiene el peso del atributo.
	 *
	 * @return El peso del atributo como un valor de doble precisión.
	 */
	public double getPeso() {
		return this.peso;
	}

	/**
	 * @brief Establece un nuevo nombre para el atributo.
	 *
	 * @param nuevo La nueva cadena de texto para el nombre del atributo.
	 */
	public void setNombre(String nuevo) {
		this.nombre = nuevo;
	}

	/**
	 * @brief Establece un nuevo peso para el atributo.
	 *
	 * @param nuevo El nuevo valor de doble precisión para el peso del atributo.
	 */
	public void setPeso(double nuevo) {
		this.peso = nuevo;
	}

	/**
	 * @brief Devuelve una representación en cadena del atributo, mostrando su nombre y peso.
	 *
	 * @return Una cadena de texto con el formato "nombre: peso".
	 */
	public String get() {
		return (this.nombre + ": " + this.peso);
	}

	/**
	 * @brief Método abstracto para obtener el número de valores asociados a este atributo.
	 *
	 * @return Un entero que representa la cantidad de valores que tiene el atributo.
	 */
	public abstract int size();

	/**
	 * @brief Método abstracto para añadir un nuevo valor al atributo.
	 *
	 * Las clases concretas deben definir cómo se añade el valor
	 * y qué tipo de objeto se espera.
	 *
	 * @param valor El valor a añadir (el tipo dependerá de la implementación concreta).
	 */
	public abstract void add(Object valor);

	/**
	 * @brief Método abstracto para eliminar un valor del atributo en un índice específico.
	 *
	 * @param indice El índice (basado en cero) del valor a eliminar.
	 */
	public abstract void delete(int indice);

	/**
	 * @brief Método abstracto para obtener un valor del atributo en un índice específico.
	 *
	 * @param i El índice (basado en cero) del valor a obtener.
	 * @return El valor en el índice especificado (el tipo dependerá de la implementación concreta).
	 */
	public abstract Object getValor(int i);

	/**
	 * @brief Método abstracto para obtener la representación en cadena del atributo.
	 *
	 * @return Una cadena de texto que representa el atributo y sus valores.
	 */
	@Override
	public abstract String toString();

	/**
	 * @brief Método abstracto para limpiar todos los valores asociados al atributo.
	 */
	public abstract void clear();

}