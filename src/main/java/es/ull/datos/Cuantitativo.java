package datos;

import vectores.Vector;

/**
 * @file Cuantitativo.java
 * @brief Implementación de un atributo cuantitativo (numérico).
 *
 * Esta clase extiende {@link Atributo} y representa un atributo
 * cuyos valores son numéricos (de doble precisión). Utiliza la clase
 * {@link Vector} para almacenar y manipular estos valores.
 * Proporciona métodos para calcular estadísticas como mínimo, máximo,
 * media y desviación estándar, así como para realizar la estandarización Z-score.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class Cuantitativo extends Atributo{
	/**
	 * @brief Objeto {@link Vector} que almacena los valores numéricos del atributo.
	 */
	private Vector valores;

	/**
	 * @brief Constructor por defecto.
	 * <p>
	 * Inicializa el nombre del atributo como cadena vacía y el vector de valores como un nuevo {@link Vector} vacío.
	 */
	public Cuantitativo() {
		this.nombre = "";
		this.valores = new Vector();
	}

	/**
	 * @brief Constructor que inicializa el atributo con un nombre específico.
	 *
	 * @param name El nombre del atributo.
	 */
	public Cuantitativo(String name) {
		this();
		this.nombre = name;
	}

	/**
	 * @brief Constructor que inicializa el atributo con un nombre y un primer valor.
	 *
	 * @param name El nombre del atributo.
	 * @param valor El primer valor {@link Double} a añadir al atributo.
	 */
	public Cuantitativo(String name, Double valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}

	/**
	 * @brief Constructor que inicializa el atributo con un nombre y un objeto {@link Vector} de valores.
	 *
	 * @param name El nombre del atributo.
	 * @param valor El objeto {@link Vector} que contiene los valores para inicializar el atributo.
	 */
	public Cuantitativo(String name, Vector valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}

	/**
	 * @brief Constructor de copia.
	 * <p>
	 * Crea una nueva instancia de {@link Cuantitativo} copiando el nombre y el peso
	 * de otro atributo cuantitativo. El vector de valores se inicializa vacío.
	 *
	 * @param otro El objeto {@link Cuantitativo} del cual copiar la información base.
	 */
	public Cuantitativo(Cuantitativo otro) {
		this();
		this.nombre = otro.getNombre();
		this.peso = otro.getPeso();
		this.valores = new Vector(); // La lista de valores se inicializa vacía en el constructor de copia
	}

	/**
	 * @brief Obtiene el objeto {@link Vector} que contiene los valores del atributo.
	 *
	 * @return El {@link Vector} de valores del atributo.
	 */
	@Override
	public Vector getValores() {
		return this.valores;
	}

	/**
	 * @brief Establece un nuevo objeto {@link Vector} para los valores del atributo.
	 *
	 * @param nuevos El nuevo {@link Vector} con los valores.
	 */
	public void setValores(Vector nuevos) {
		this.valores = nuevos;
	}

	/**
	 * @brief Calcula y devuelve el valor mínimo entre los coeficientes del vector de valores.
	 *
	 * @return El valor mínimo de doble precisión en el vector.
	 * @throws IllegalStateException Si el vector de valores está vacío.
	 */
	public double minimo() {
		// Delegar en el método de Vector que ya maneja el caso vacío
		return this.valores.getMin();
	}

	/**
	 * @brief Calcula y devuelve el valor máximo entre los coeficientes del vector de valores.
	 *
	 * @return El valor máximo de doble precisión en el vector.
	 * @throws IllegalStateException Si el vector de valores está vacío.
	 */
	public double maximo() {
		// Delegar en el método de Vector que ya maneja el caso vacío
		return this.valores.getMax();
	}

	/**
	 * @brief Calcula el promedio (media aritmética) de los valores del atributo.
	 *
	 * @return El valor promedio de los coeficientes.
	 * @throws IllegalStateException Si el vector de valores está vacío.
	 */
	public double media() {
		if (this.valores.size() == 0) { // Añadir esta validación
			throw new IllegalStateException("No se puede calcular la media de un vector vacío.");
		}
		double suma = 0.0; // Cambiar inicialización
		for(int i = 0; i < this.valores.size(); ++i) {
			suma += this.valores.get(i);
		}
		return suma / this.valores.size();
	}

	/**
	 * @brief Calcula la desviación estándar poblacional de los valores del atributo.
	 *
	 * @return La desviación estándar de los valores.
	 * @throws IllegalStateException Si el vector de valores está vacío (debido a la llamada a {@link #media()}).
	 */
	public double desviacion() {
		double media = this.media();
		double auxiliar = 0;
		for(int i = 0; i < this.valores.size(); ++i) {
			auxiliar += (this.valores.get(i) - media) * (this.valores.get(i) - media);
		}
		auxiliar /= this.valores.size(); // Tu calculas desviación poblacional. Eso está bien.
		return Math.sqrt(auxiliar);
	}

	/**
	 * @brief Obtiene el número de valores almacenados en este atributo cuantitativo.
	 *
	 * @return Un entero que representa el tamaño del vector de valores.
	 */
	@Override
	public int size() {
		return this.valores.size();
	}

	/**
	 * @brief Estandariza los valores del atributo usando la estandarización Z-score.
	 * <p>
	 * Cada valor se transforma en `(valor - media) / desviación_estándar`.
	 * Este método modifica el vector de valores original. Si la desviación estándar es cero,
	 * todos los valores se establecerán en 0.0.
	 *
	 * @throws IllegalStateException Si el vector de valores está vacío.
	 */
	public void estandarizacion() {
		if (this.valores.size() == 0) { // Añadir esta validación
			throw new IllegalStateException("No se puede estandarizar un vector vacío.");
		}
		double media = this.media();
		double desviacion = this.desviacion(); // Calcular una vez
		if (desviacion == 0.0) { // Manejar el caso de desviación cero
			for (int i = 0; i < valores.size(); ++i) {
				valores.set(i, 0.0); // Si todos los valores son iguales, su Z-score es 0.
			}
			return;
		}
		for (int i = 0; i < valores.size(); ++i) {
			valores.set(i, (valores.get(i) - media) / desviacion);
		}
	}

	/**
	 * @brief Añade un nuevo valor al vector de valores del atributo.
	 *
	 * @param valor El valor a añadir, que debe ser un tipo numérico ({@link java.lang.Number}).
	 * @throws ClassCastException Si el objeto `valor` no es una instancia de {@link java.lang.Number}.
	 */
	@Override
	public void add(Object valor) {
		if (valor instanceof Number) { // Verifica si es un número (Integer, Double, etc.)
			valores.add(((Number) valor).doubleValue()); // Convierte a Double
		} else {
			throw new ClassCastException("El valor añadido debe ser un número convertible a Double.");
		}
	}

	/**
	 * @brief Obtiene el valor en un índice específico del vector de valores.
	 *
	 * @param i El índice (basado en cero) del valor a obtener.
	 * @return El valor {@link Double} en el índice especificado.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango del vector de valores.
	 */
	@Override
	public Object getValor(int i) {
		return valores.get(i);

	}

	/**
	 * @brief Elimina el valor en un índice específico del vector de valores.
	 *
	 * @param index El índice (basado en cero) del valor a eliminar.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango del vector de valores.
	 */
	@Override
	public void delete(int index) {
		valores.remove(index);

	}

	/**
	 * @brief Devuelve una representación en cadena del vector de valores del atributo.
	 *
	 * @return Una cadena de texto que representa el vector de valores.
	 */
	@Override
	public String toString() {
		return valores.toString();

	}

	/**
	 * @brief Elimina todos los valores del vector del atributo.
	 */
	@Override
	public void clear() {
		valores.clear();
	}

}