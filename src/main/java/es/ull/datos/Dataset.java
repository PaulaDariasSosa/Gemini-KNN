package datos;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import vectores.Vector; // Necesario si Cuantitativo usa Vector

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors; // Añadir esta importación

/**
 * @file Dataset.java
 * @brief Clase que representa un conjunto de datos (dataset), compuesto por una lista de atributos.
 *
 * Un dataset puede contener atributos de diferentes tipos (cuantitativos y cualitativos).
 * Proporciona funcionalidades para leer y escribir datasets desde/hacia archivos,
 * acceder y manipular atributos, y realizar operaciones de preprocesamiento
 * como la estandarización y normalización de los atributos cuantitativos.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class Dataset {
	/**
	 * @brief Lista de objetos {@link Atributo} que componen el dataset.
	 */
	private List<Atributo> atributos;
	/**
	 * @brief Campo que indica el estado de preprocesamiento del dataset.
	 * <p>
	 * El valor `0` indica que no se ha preprocesado, `1` indica normalización, `2` indica estandarización.
	 */
	int preprocesado; // No se utiliza directamente en los métodos provistos, pero se mantiene.

	/**
	 * @brief Constructor por defecto.
	 * <p>
	 * Inicializa la lista de atributos como una nueva {@link ArrayList} vacía y el estado de preprocesamiento a 0.
	 */
	public Dataset() {
		this.atributos = new ArrayList<Atributo>();
		this.preprocesado = 0; // Inicializar preprocesado
	}

	/**
	 * @brief Constructor que inicializa el dataset con una lista existente de atributos.
	 * <p>
	 * Realiza una copia defensiva de la lista de atributos proporcionada.
	 *
	 * @param nuevos La {@link List} de {@link Atributo} para inicializar el dataset. Puede ser nula.
	 */
	public Dataset(List<Atributo> nuevos) {
		this(); // Llama al constructor vacío para inicializar 'atributos' y 'preprocesado'
		// CORRECCIÓN: Realizar una copia defensiva de la lista de atributos
		if (nuevos != null) {
			this.atributos.addAll(nuevos); // Copia los elementos a la nueva lista
		}
	}

	/**
	 * @brief Constructor que lee los datos de un dataset desde un archivo.
	 *
	 * @param filename El nombre (o ruta) del archivo a leer.
	 * @throws IOException Si ocurre un error de E/S durante la lectura del archivo.
	 */
	public Dataset(String filename) throws IOException {
		this(); // Llama al constructor vacío para inicializar 'atributos' y 'preprocesado'
		this.read(filename);
	}

	/**
	 * @brief Constructor de copia.
	 * <p>
	 * Crea una nueva instancia de {@link Dataset} copiando los atributos de otro dataset.
	 *
	 * @param datos El objeto {@link Dataset} del cual copiar los atributos.
	 * @throws IllegalArgumentException Si el dataset de origen es nulo.
	 */
	public Dataset(Dataset datos) {
		this(); // Llama al constructor vacío
		// CORRECCIÓN: Realizar una copia defensiva de la lista de atributos
		// (el constructor original ya lo hacía bien para datos.atributos)
		this.atributos = new ArrayList<>(datos.atributos);
		this.preprocesado = datos.preprocesado;
	}

	/**
	 * @brief Cambia el peso de todos los atributos del dataset según una lista de nuevos pesos.
	 *
	 * @param nuevosPesos La {@link List} de {@link String} que contiene los nuevos pesos.
	 * Cada string debe ser un número que pueda parsearse a {@link Double}.
	 * @throws IllegalArgumentException Si el número de pesos no coincide con el número de atributos,
	 * si algún peso no es un número válido, o si un peso está fuera del rango [0, 1].
	 * @throws IndexOutOfBoundsException Nunca debería ocurrir si `nuevosPesos.size()` es validado correctamente.
	 */
	public void cambiarPeso(List<String> nuevosPesos) {
		if (nuevosPesos.size() != atributos.size()) {
			throw new IllegalArgumentException("El número de pesos para asignar debe ser igual al número de atributos.");
		}
		for (int i = 0; i < nuevosPesos.size(); i++) {
			Atributo aux = atributos.get(i);
			try {
				double peso = Double.parseDouble(nuevosPesos.get(i));
				if (peso < 0 || peso > 1) {
					throw new IllegalArgumentException("Los pesos deben estar entre 0 y 1.");
				}
				aux.setPeso(peso);
				// No es necesario set(i, aux) si 'aux' es la misma referencia y se modifica directamente.
				// atributos.set(i, aux);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("El peso '" + nuevosPesos.get(i) + "' no es un número válido.");
			}
		}
	}

	/**
	 * @brief Cambia el peso de un atributo específico en el dataset.
	 *
	 * @param index El índice (basado en cero) del atributo cuyo peso se desea cambiar.
	 * @param peso El nuevo valor de peso (debe estar entre 0 y 1).
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango de la lista de atributos.
	 * @throws IllegalArgumentException Si el peso está fuera del rango [0, 1].
	 */
	public void cambiarPeso(int index, double peso) {
		// Añadir validación de índice
		if (index < 0 || index >= atributos.size()) {
			throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango para atributos.");
		}
		if (peso < 0 || peso > 1) { // Añadir validación de rango para el peso
			throw new IllegalArgumentException("Los pesos deben estar entre 0 y 1.");
		}
		Atributo aux = this.atributos.get(index);
		aux.setPeso(peso);
		// atributos.set(index, aux); // Redundante si 'aux' es la misma referencia
	}

	/**
	 * @brief Cambia el peso de todos los atributos del dataset a un valor único.
	 *
	 * @param peso El nuevo valor de peso para todos los atributos (debe estar entre 0 y 1).
	 * @throws IllegalArgumentException Si el peso está fuera del rango [0, 1].
	 */
	public void cambiarPeso(double peso) {
		if (peso < 0 || peso > 1) { // Añadir validación de rango para el peso
			throw new IllegalArgumentException("Los pesos deben estar entre 0 y 1.");
		}
		for (int i = 0; i <  atributos.size(); i++) {
			Atributo aux = atributos.get(i);
			aux.setPeso(peso);
			// atributos.set(i, aux); // Redundante
		}
	}

	/**
	 * @brief Imprime la representación en cadena del dataset a través del logger.
	 * <p>
	 * La salida se registra a nivel INFO.
	 */
	public void print() {
		Logger logger = LoggerFactory.getLogger(Dataset.class); // Usar getLogger(Class)
		if (logger.isInfoEnabled()) {
			logger.info(this.toString());
		}
	}

	/**
	 * @brief Devuelve una representación en cadena del dataset, incluyendo los nombres de los atributos y los valores de cada instancia.
	 * <p>
	 * Los valores numéricos se formatean con un decimal y se utiliza el punto como separador decimal.
	 *
	 * @return Una cadena de texto que representa el dataset en formato CSV, con encabezado y sin línea final vacía.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		List<String> nombres = nombreAtributos();
		if (!nombres.isEmpty()) {
			sb.append(String.join(",", nombres));
			sb.append("\n");
		} else {
			return "";
		}

		int numCasos = numeroCasos();
		if (numCasos > 0) {
			for (int i = 0; i < numCasos; ++i) {
				List<String> filaValores = new ArrayList<>();
				for (int j = 0; j < atributos.size(); ++j) {
					Object valor = atributos.get(j).getValor(i);
					if (valor instanceof Double) {
						// CORRECCIÓN: Usar Locale.US para asegurar el punto decimal
						filaValores.add(String.format(Locale.US, "%.1f", (Double) valor));
					} else {
						filaValores.add(String.valueOf(valor));
					}
				}
				sb.append(String.join(",", filaValores));
				sb.append("\n");
			}
		}
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * @brief Añade una nueva instancia (fila de valores) al dataset.
	 * <p>
	 * Los valores de la instancia se añaden a los atributos correspondientes.
	 *
	 * @param nueva El objeto {@link Instancia} a añadir.
	 * @throws IllegalArgumentException Si la instancia es nula o si el número de valores en la instancia
	 * no coincide con el número de atributos del dataset.
	 */
	public void add(Instancia nueva) {
		// CORRECCIÓN: Validar el tamaño de la instancia
		if (nueva == null || nueva.getValores().size() != atributos.size()) {
			throw new IllegalArgumentException("El número de valores en la instancia no coincide con el número de atributos del dataset.");
		}
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux =  atributos.get(i);
			aux.add(nueva.getValores().get(i));
			// atributos.set(i, aux); // Redundante
		}
	}

	/**
	 * @brief Añade una nueva instancia (fila de valores) al dataset a partir de una lista de cadenas.
	 * <p>
	 * Intenta convertir cada cadena a {@link Double} para atributos cuantitativos;
	 * de lo contrario, lo añade como {@link String} (asumiendo que el atributo es cualitativo).
	 *
	 * @param nueva La {@link List} de {@link String} que representa la nueva instancia.
	 * @throws IllegalArgumentException Si la lista es nula o si el número de valores en la lista
	 * no coincide con el número de atributos del dataset.
	 * @throws NumberFormatException Si se intenta añadir un valor no numérico a un atributo cuantitativo.
	 * @throws ClassCastException Si hay una inconsistencia de tipo al añadir el valor.
	 */
	public void add(List<String> nueva) {
		// CORRECCIÓN: Validar el tamaño de la lista de strings
		if (nueva == null || nueva.size() != atributos.size()) {
			throw new IllegalArgumentException("El número de valores en la lista no coincide con el número de atributos del dataset.");
		}
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux =  atributos.get(i);
			try {
				aux.add(Double.parseDouble(nueva.get(i))); // Intenta añadir como Double
			} catch (NumberFormatException e) {
				// Si no es Double, intenta añadir como String
				// Esto solo funcionará si 'aux' es un Cualitativo y su método add() acepta String
				// Si 'aux' es Cuantitativo y no es un número, ClassCastException o NumberFormatException
				// en el método add() del atributo es el comportamiento esperado.
				aux.add(nueva.get(i));
			}
			// atributos.set(i, aux); // Redundante
		}
	}
	/**
	 * @brief Elimina una instancia (fila) del dataset en el índice especificado.
	 * <p>
	 * Elimina el valor correspondiente de cada atributo en el índice dado.
	 *
	 * @param nueva El índice (basado en cero) de la instancia a eliminar.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango de las instancias existentes.
	 */
	public void delete(int nueva) {
		// Añadir validación de índice
		if (numeroCasos() == 0 || nueva < 0 || nueva >= numeroCasos()) {
			throw new IndexOutOfBoundsException("Índice " + nueva + " fuera de rango para los casos del dataset.");
		}
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux = atributos.get(i);
			aux.delete(nueva);
			// atributos.set(i, aux); // Redundante
		}
	}

	/**
	 * @brief Escribe los datos del dataset en un archivo CSV.
	 * <p>
	 * Utiliza la representación en cadena del dataset ({@link #toString()}) para la escritura.
	 *
	 * @param filename El nombre (o ruta) del archivo CSV a escribir.
	 * @throws IOException Si ocurre un error de E/S durante la escritura del archivo.
	 */
	public void write(String filename) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write(this.toString());
		}
	}

	/**
	 * @brief Lee los datos del dataset desde un archivo CSV.
	 * <p>
	 * La primera línea del archivo se considera el encabezado con los nombres de los atributos.
	 * Los tipos de atributo ({@link Cuantitativo} o {@link Cualitativo}) se infieren
	 * automáticamente basándose en los valores de la primera fila de datos.
	 *
	 * @param filename El nombre (o ruta) del archivo CSV a leer.
	 * @throws IOException Si ocurre un error de E/S durante la lectura del archivo,
	 * si el formato del archivo es inconsistente (ej. número de columnas),
	 * o si hay una inconsistencia de tipo de dato en una columna.
	 * @throws IllegalArgumentException Si el nombre del archivo es nulo o vacío.
	 */
	public void read(String filename) throws IOException {
		this.atributos.clear(); // CORRECCIÓN: Limpiar atributos existentes antes de leer
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String headerLine = reader.readLine();

			// CORRECCIÓN: Manejar archivo vacío o con solo líneas en blanco
			if (headerLine == null || headerLine.trim().isEmpty()) {
				return; // Dataset permanece vacío
			}

			String[] attributeNamesArray = headerLine.split(",");
			if (attributeNamesArray.length == 0) { // Si el encabezado está vacío o es solo una coma
				return; // Dataset permanece vacío
			}

			String firstDataLine = reader.readLine(); // Leer la primera línea de datos

			if (firstDataLine != null && !firstDataLine.trim().isEmpty()) {
				String[] firstValues = firstDataLine.split(",");
				if (firstValues.length != attributeNamesArray.length) {
					throw new IOException("El número de valores en la primera fila de datos (" + firstValues.length + ") no coincide con el número de atributos en el encabezado (" + attributeNamesArray.length + ").");
				}

				// Inicializar atributos basándose en la primera fila de datos para inferir el tipo
				for (int i = 0; i < attributeNamesArray.length; ++i) {
					try {
						Double.parseDouble(firstValues[i]); // Intentar parsear a double
						this.atributos.add(new Cuantitativo(attributeNamesArray[i], Double.parseDouble(firstValues[i])));
					} catch (NumberFormatException e) {
						// Si no se puede parsear como double, es un atributo cualitativo
						this.atributos.add(new Cualitativo(attributeNamesArray[i], firstValues[i]));
					}
				}
			} else {
				// Si el archivo solo tiene encabezado o encabezado seguido de líneas vacías,
				// inicializar atributos como Cuantitativo por defecto
				for (String name : attributeNamesArray) {
					this.atributos.add(new Cuantitativo(name));
				}
			}

			// Leer las líneas de datos restantes
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue; // Saltar líneas vacías
				String[] values = line.split(",");

				if (values.length != atributos.size()) {
					LoggerFactory.getLogger(Dataset.class).warn("Fila con número de valores inconsistente y será omitida: '{}'", line);
					continue;
				}

				for (int i = 0; i < atributos.size(); ++i) {
					Atributo currentAttr = this.atributos.get(i);
					try {
						if (currentAttr instanceof Cuantitativo) {
							currentAttr.add(Double.parseDouble(values[i]));
						} else { // Es Cualitativo
							currentAttr.add(values[i]);
						}
					} catch (NumberFormatException e) {
						// Esto ocurre si un valor no numérico aparece en una columna que fue inferida como Cuantitativa
						throw new IOException("Inconsistencia de tipo de dato en la columna '" + currentAttr.getNombre() + "'. Valor '" + values[i] + "' no es numérico.");
					} catch (ClassCastException e) {
						// Esto puede ocurrir si add(Object) en Cuantitativo/Cualitativo no maneja el tipo correctamente.
						throw new IOException("Error de tipo de dato al añadir valor a la columna '" + currentAttr.getNombre() + "'. Valor '" + values[i] + "'. Causa: " + e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * @brief Obtiene el número de atributos (columnas) en el dataset.
	 *
	 * @return Un entero que representa la cantidad de atributos.
	 */
	public int numeroAtributos() {
		return atributos.size();
	}

	/**
	 * @brief Obtiene una lista con los nombres de todos los atributos del dataset.
	 *
	 * @return Una {@link ArrayList} de {@link String} que contiene los nombres de los atributos.
	 */
	public List<String> nombreAtributos(){
		ArrayList<String> nombres = new ArrayList<>();
		for(int i = 0; i < atributos.size(); ++i) {
			nombres.add(atributos.get(i).getNombre());
		}
		return nombres;
	}

	/**
	 * @brief Obtiene la lista de todos los atributos del dataset.
	 * <p>
	 * Devuelve la referencia directa a la lista interna de atributos, no una copia.
	 *
	 * @return Una {@link List} de {@link Atributo} que contiene los atributos.
	 */
	public List<Atributo> getAtributos(){
		return atributos; // Devuelve la referencia directa, consistente con el constructor de copia
	}

	/**
	 * @brief Crea y devuelve una nueva lista de atributos vacíos, manteniendo los nombres y pesos de los atributos originales.
	 * <p>
	 * Esto es útil para crear una estructura de dataset similar sin los datos.
	 *
	 * @return Una {@link ArrayList} de {@link Atributo} con la misma estructura (nombre, peso) pero sin valores.
	 * @throws IllegalStateException Si se encuentra un tipo de atributo desconocido.
	 */
	public List<Atributo> getAtributosEmpty() {
		ArrayList<Atributo> aux = new ArrayList<Atributo>(atributos.size());
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo originalAttr = atributos.get(i);
			Atributo newEmptyAttr;
			if (originalAttr instanceof Cualitativo) {
				newEmptyAttr = new Cualitativo(originalAttr.getNombre());
			} else if (originalAttr instanceof Cuantitativo) { // Asegúrate de manejar Cuantitativo
				newEmptyAttr = new Cuantitativo(originalAttr.getNombre());
			} else {
				// Manejar otros tipos de Atributo si existen, o lanzar un error
				throw new IllegalStateException("Tipo de atributo desconocido: " + originalAttr.getClass().getName());
			}
			newEmptyAttr.setPeso(originalAttr.getPeso());
			aux.add(newEmptyAttr); // Añadir a la lista
		}
		return aux;
	}

	/**
	 * @brief Obtiene el número de casos (filas o instancias) en el dataset.
	 * <p>
	 * Se basa en el tamaño del primer atributo. Se asume que todos los atributos tienen el mismo número de valores.
	 *
	 * @return Un entero que representa el número de casos en el dataset, o 0 si el dataset no tiene atributos.
	 */
	public int numeroCasos() {
		if (atributos.isEmpty()) { // CORRECCIÓN: Si no hay atributos, no hay casos
			return 0;
		}
		return atributos.get(0).size();
	}

	/**
	 * @brief Obtiene una lista con todos los valores del dataset, linealizados en una sola lista de cadenas.
	 * <p>
	 * Los valores se obtienen fila por fila, y dentro de cada fila, columna por columna.
	 * Los valores numéricos se formatean con un decimal y se utiliza el punto como separador decimal.
	 *
	 * @return Una {@link ArrayList} de {@link String} que contiene todos los valores del dataset.
	 */
	public List<String> getValores(){
		ArrayList<String> valores = new ArrayList<String>();
		if (atributos.isEmpty()) {
			return valores;
		}
		int numCasos = atributos.get(0).size();
		for (int i = 0; i < numCasos; ++i) {
			for (int j = 0; j < atributos.size(); ++j) {
				Object valor = atributos.get(j).getValor(i);
				if (valor instanceof Double) {
					// CORRECCIÓN: Usar Locale.US para asegurar el punto decimal
					valores.add(String.format(Locale.US, "%.1f", (Double) valor));
				} else {
					valores.add(String.valueOf(valor));
				}
			}
		}
		return valores;
	}

	/**
	 * @brief Obtiene el atributo en el índice especificado.
	 *
	 * @param index El índice (basado en cero) del atributo a obtener.
	 * @return El objeto {@link Atributo} en el índice especificado.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango de la lista de atributos.
	 */
	public Atributo get(int index) {
		return atributos.get(index);
	}

	/**
	 * @brief Obtiene una instancia (fila) específica del dataset.
	 * <p>
	 * Recopila el i-ésimo valor de cada atributo para formar una instancia.
	 *
	 * @param index El índice (basado en cero) de la instancia a obtener.
	 * @return Un objeto {@link Instancia} que representa la fila en el índice especificado.
	 * @throws IndexOutOfBoundsException Si el índice está fuera del rango de las instancias del dataset.
	 */
	public Instancia getInstance(int index){
		// Añadir validación de índice
		if (numeroCasos() == 0 || index < 0 || index >= numeroCasos()) {
			throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango para las instancias del dataset.");
		}
		ArrayList<Object> auxiliar = new ArrayList<>();
		for (int i = 0; i < atributos.size(); ++i) {
			auxiliar.add(atributos.get(i).getValor(index));
		}
		return new Instancia (auxiliar);
	}

	/**
	 * @brief Obtiene una lista de cadenas que representan los nombres y pesos de los atributos.
	 * <p>
	 * Cada cadena tiene el formato "nombre: peso".
	 *
	 * @return Una {@link ArrayList} de {@link String} con la representación de cada atributo (nombre:peso).
	 */
	public List<String> getPesos() {
		ArrayList<String> valores = new ArrayList<String>();
		for (Atributo valor : this.atributos) {
			valores.add(valor.get()); // Assuming Atributo.get() gives "Nombre: Peso"
		}
		return valores;
	}

	/**
	 * @brief Obtiene los pesos de los atributos como una lista de Double.
	 *
	 * @return Una lista de Double con los pesos de los atributos.
	 */
	public List<Double> getPesosDouble() {
		List<Double> pesosDouble = new ArrayList<>();
		for (int i = 0; i < atributos.size(); ++i) { // Iterar sobre todos los atributos
			pesosDouble.add(atributos.get(i).getPeso());
		}
		return pesosDouble;
	}

	/**
	 * @brief Obtiene una lista de todas las clases únicas presentes en el último atributo del dataset.
	 * <p>
	 * Se asume que el último atributo es de tipo {@link Cualitativo} y representa la columna de clase.
	 *
	 * @return Una {@link List} de {@link String} con las clases únicas.
	 * @throws IllegalStateException Si el dataset no tiene atributos.
	 * @throws ClassCastException Si el último atributo no es de tipo {@link Cualitativo}.
	 */
	public List<String> getClases() {
		if (atributos.isEmpty()) {
			throw new IllegalStateException("El dataset no tiene atributos para obtener las clases.");
		}
		// Asegúrate de que el último atributo es Cualitativo
		Atributo lastAttr = this.atributos.get(atributos.size()-1);
		if (!(lastAttr instanceof Cualitativo)) {
			throw new ClassCastException("El último atributo no es de tipo Cualitativo y no se pueden obtener clases.");
		}
		return ((Cualitativo) lastAttr).clases();
	}

	/**
	 * @brief Obtiene el estado actual de preprocesamiento del dataset.
	 *
	 * @return Un entero que representa el estado de preprocesamiento (0: no preprocesado, 1: normalizado, 2: estandarizado).
	 */
	public int getPreprocesado() {
		return preprocesado;
	}

	/**
	 * @brief Establece el estado de preprocesamiento del dataset.
	 *
	 * @param opcion El nuevo estado de preprocesamiento (0: no preprocesado, 1: normalizado, 2: estandarizado).
	 */
	public void setPreprocesado(int opcion) {
		this.preprocesado = opcion;
	}

	/**
	 * @brief Establece la lista de atributos del dataset.
	 * <p>
	 * Este método reemplaza la lista de atributos existente con la nueva lista.
	 *
	 * @param nuevos La nueva {@link List} de {@link Atributo} para el dataset.
	 */
	public void setAtributos(List<Atributo> nuevos) {
		this.atributos = nuevos;
	}

}