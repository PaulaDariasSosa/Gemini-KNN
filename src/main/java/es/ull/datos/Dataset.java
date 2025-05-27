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

public class Dataset {
	private List<Atributo> atributos;
	int preprocesado; // No se utiliza directamente en los métodos provistos, pero se mantiene.

	public Dataset() {
		this.atributos = new ArrayList<Atributo>();
		this.preprocesado = 0; // Inicializar preprocesado
	}

	public Dataset(List<Atributo> nuevos) {
		this(); // Llama al constructor vacío para inicializar 'atributos' y 'preprocesado'
		// CORRECCIÓN: Realizar una copia defensiva de la lista de atributos
		if (nuevos != null) {
			this.atributos.addAll(nuevos); // Copia los elementos a la nueva lista
		}
	}

	public Dataset(String filename) throws IOException {
		this(); // Llama al constructor vacío para inicializar 'atributos' y 'preprocesado'
		this.read(filename);
	}

	public Dataset(Dataset datos) {
		this(); // Llama al constructor vacío
		// CORRECCIÓN: Realizar una copia defensiva de la lista de atributos
		// (el constructor original ya lo hacía bien para datos.atributos)
		this.atributos = new ArrayList<>(datos.atributos);
		this.preprocesado = datos.preprocesado;
	}

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

	// Cambiar peso para uno
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

	// Print
	public void print() {
		Logger logger = LoggerFactory.getLogger(Dataset.class); // Usar getLogger(Class)
		if (logger.isInfoEnabled()) {
			logger.info(this.toString());
		}
	}

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

	// Modify (mezcla de add y delete)
	// Add instancia
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
	// Delete
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

	// Método para escribir el dataset en un archivo CSV
	public void write(String filename) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write(this.toString());
		}
	}

	// read (CORREGIDO para manejo de archivos vacíos/solo encabezado y determinación de tipo)
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

	// numero atributos
	public int numeroAtributos() {
		return atributos.size();
	}

	// nombre atributos
	public List<String> nombreAtributos(){
		ArrayList<String> nombres = new ArrayList<>();
		for(int i = 0; i < atributos.size(); ++i) {
			nombres.add(atributos.get(i).getNombre());
		}
		return nombres;
	}

	public List<Atributo> getAtributos(){
		return atributos; // Devuelve la referencia directa, consistente con el constructor de copia
	}

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

	// numero casos (CORREGIDO para Dataset vacío)
	public int numeroCasos() {
		if (atributos.isEmpty()) { // CORRECCIÓN: Si no hay atributos, no hay casos
			return 0;
		}
		return atributos.get(0).size();
	}

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

	public Atributo get(int index) {
		return atributos.get(index);
	}

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

	public List<String> getPesos() {
		ArrayList<String> valores = new ArrayList<String>();
		for (Atributo valor : this.atributos) {
			valores.add(valor.get()); // Assuming Atributo.get() gives "Nombre: Peso"
		}
		return valores;
	}

	/**
	 * Obtiene los pesos de los atributos como una lista de Double.
	 * Si hay errores en la conversión, se loguea y se devuelve null.
	 *
	 * @return Una lista de Double con los pesos de los atributos, o null si hay un error.
	 */
	public List<Double> getPesosDouble() {
		List<Double> pesosDouble = new ArrayList<>();
		// CORRECCIÓN: El bucle debe ir hasta atributos.size(), no size()-1
		for (int i = 0; i < atributos.size(); ++i) { // Iterar sobre todos los atributos
			// getPeso() ya devuelve Double, no hay NumberFormatException aquí a menos que sea en Atributo.getPeso()
			pesosDouble.add(atributos.get(i).getPeso());
		}
		return pesosDouble;
	}

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

	public int getPreprocesado() {
		return preprocesado;
	}

	public void setPreprocesado(int opcion) {
		this.preprocesado = opcion;
	}

	public void setAtributos(List<Atributo> nuevos) {
		this.atributos = nuevos;
	}

}