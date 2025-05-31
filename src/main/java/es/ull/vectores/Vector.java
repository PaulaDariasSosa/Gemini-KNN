package vectores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * @file Vector.java
 * @brief Implementa una clase para representar y manipular vectores de números de doble precisión.
 *
 * Esta clase proporciona diversas funcionalidades para trabajar con vectores,
 * incluyendo constructores para diferentes tipos de inicialización, operaciones
 * matemáticas (suma, producto escalar, módulo, normalización), acceso y modificación
 * de elementos, lectura y escritura a archivos, y cálculo de estadísticas básicas.
 * Utiliza una {@link java.util.List} de {@link java.lang.Double} internamente para almacenar los coeficientes.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class Vector {
    /**
     * @brief Lista de coeficientes (valores) del vector.
     * <p>
     * Almacena los elementos de doble precisión que componen el vector.
     */
    List<Double> coef;
    /**
     * @brief Objeto Logger para registrar mensajes informativos y de error.
     * <p>
     * Utiliza SLF4J para el registro de eventos dentro de la clase.
     */
    private static final Logger logger = LoggerFactory.getLogger(Vector.class);

    /**
     * @brief Constructor vacío.
     * <p>
     * Inicializa un nuevo vector con una lista vacía de coeficientes.
     */
    public Vector() {
        coef = new ArrayList<>();
    }

    /**
     * @brief Constructor que inicializa el vector con los valores de un array de dobles.
     *
     * Los elementos del array se añaden a la lista de coeficientes del vector.
     *
     * @param array Array de valores double para inicializar el vector.
     */
    public Vector(double[] array) {
        this();
        if (array != null) {
            for (double value : array) {
                coef.add(value);
            }
        } else {
            logger.warn("Se proporcionó un array nulo al constructor.");
        }
    }

    /**
     * @brief Constructor que inicializa el vector con los valores de una lista de dobles.
     *
     * Se crea una nueva lista a partir de la lista proporcionada para evitar problemas de referencia.
     *
     * @param coef Lista de valores Double para inicializar el vector.
     */
    public Vector(List<Double> coef) {
        this.coef = (coef != null) ? new ArrayList<>(coef) : new ArrayList<>();
        if (coef == null) {
            logger.warn("Se proporcionó una lista nula al constructor.");
        }
    }

    /**
     * @brief Constructor que inicializa un vector de un tamaño específico, rellenado con ceros.
     *
     * @param size El tamaño deseado del vector.
     * @throws IllegalArgumentException Si el tamaño proporcionado es negativo.
     */
    public Vector(int size) {
        this();
        if (size < 0) {
            throw new IllegalArgumentException("El tamaño del vector no puede ser negativo.");
        }
        for (int i = 0; i < size; ++i) {
            coef.add(0.0);
        }
    }

    /**
     * @brief Constructor que lee los coeficientes de un vector desde un archivo.
     *
     * Utiliza un {@link java.util.Scanner} para leer valores de doble precisión del archivo.
     *
     * @param file El archivo desde el cual se leerán los coeficientes.
     * @throws FileNotFoundException Si el archivo especificado no se encuentra.
     * @throws IllegalArgumentException Si el archivo proporcionado es nulo.
     */
    public Vector(File file) throws FileNotFoundException {
        coef = new ArrayList<>(); // Initializes coef
        if (file == null) {
            throw new IllegalArgumentException("El archivo proporcionado no puede ser nulo.");
        }
        readFileWithScanner(file); // Calls the private method
    }

    /**
     * @brief Constructor que inicializa el vector a partir de una cadena de texto.
     *
     * La cadena de texto debe contener valores numéricos separados por comas (ej. "1.0,2.5,3.0").
     *
     * @param str La cadena de texto con los valores del vector.
     * @throws NumberFormatException Si la cadena contiene valores no numéricos.
     */
    public Vector(String str) {
        coef = new ArrayList<>();
        if (str != null && !str.isEmpty()) {
            String[] values = str.split(",");
            try {
                for (String value : values) {
                    coef.add(Double.parseDouble(value.trim()));
                }
            } catch (NumberFormatException e) {
                logger.error("Error al parsear un valor del string: {}", e.getMessage());
                coef.clear(); // O podrías considerar añadir los valores válidos y omitir los inválidos
                throw new NumberFormatException("El string contiene valores no numéricos");
            }
        } else if (str == null) {
            logger.warn("Se proporcionó un string nulo al constructor.");
        }
    }

    /**
     * @brief Constructor de copia que crea un nuevo vector a partir de otro vector existente.
     *
     * Realiza una copia profunda de la lista de coeficientes.
     *
     * @param other El objeto Vector a clonar.
     */
    public Vector(Vector other) {
        this.coef = (other != null && other.coef != null) ? new ArrayList<>(other.coef) : new ArrayList<>();
        if (other == null) {
            logger.warn("Se intentó clonar un vector nulo.");
        }
    }

    /**
     * @brief Devuelve la dimensión (número de elementos) del vector.
     *
     * @return Un entero que representa el número de coeficientes en el vector.
     */
    public int size() {
        return coef.size();
    }

    /**
     * @brief Vacía el vector, eliminando todos sus coeficientes.
     */
    public void clear() {
        coef.clear();
    }

    /**
     * @brief Devuelve una representación en cadena del vector.
     *
     * La representación es la de la lista interna de coeficientes (ej. "[1.0, 2.0, 3.0]").
     *
     * @return Una cadena de texto que representa el vector.
     */
    public String toString() {
        return coef.toString();
    }

    /**
     * @brief Imprime la representación en cadena del vector en el log de información.
     */
    public void print() {
        if(logger.isInfoEnabled()){
            logger.info(this.toString());
        }
    }

    /**
     * @brief Obtiene el valor de un coeficiente en una posición específica del vector.
     *
     * @param index El índice del coeficiente a obtener (basado en cero).
     * @return El valor de doble precisión en el índice especificado.
     * @throws IndexOutOfBoundsException Si el índice está fuera del rango válido del vector.
     */
    public double get(int index) {
        if (index < 0 || index >= coef.size()) {
            throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango [0, " + (coef.size() - 1) + "]");
        }
        return coef.get(index);
    }

    /**
     * @brief Establece un nuevo valor para un coeficiente en una posición específica del vector.
     *
     * @param index El índice del coeficiente a modificar (basado en cero).
     * @param value El nuevo valor de doble precisión a establecer.
     * @throws IndexOutOfBoundsException Si el índice está fuera del rango válido del vector.
     */
    public void set(int index, double value) {
        if (index < 0 || index >= coef.size()) {
            throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango [0, " + (coef.size() - 1) + "]");
        }
        coef.set(index, value);
    }

    /**
     * @brief Añade un nuevo valor al final del vector.
     *
     * @param value El valor de doble precisión a añadir.
     */
    public void add(double value) {
        coef.add(value);
    }

    /**
     * @brief Suma otro vector a este vector, modificando los coeficientes de este vector.
     *
     * Ambos vectores deben tener la misma dimensión para que la operación sea válida.
     *
     * @param other El vector a sumar.
     * @throws IllegalArgumentException Si el vector 'other' es nulo o si las dimensiones de los vectores no coinciden.
     */
    public void add(Vector other) {
        if (other == null || other.coef == null) {
            throw new IllegalArgumentException("El vector a añadir no puede ser nulo.");
        }
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño para la adición.");
        }
        for (int i = 0; i < this.size(); i++) {
            coef.set(i, coef.get(i) + other.get(i));
        }
    }

    /**
     * @brief Elimina un coeficiente en una posición específica del vector.
     *
     * @param index El índice del coeficiente a eliminar (basado en cero).
     * @throws IndexOutOfBoundsException Si el índice está fuera del rango válido del vector.
     */
    public void remove(int index) {
        if (index < 0 || index >= coef.size()) {
            throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango [0, " + (coef.size() - 1) + "]");
        }
        coef.remove(index);
    }

    /**
     * @brief Calcula y devuelve el valor máximo entre los coeficientes del vector.
     *
     * @return El valor máximo de doble precisión en el vector.
     * @throws IllegalStateException Si el vector está vacío.
     */
    public double getMax() {
        if (coef.isEmpty()) {
            throw new IllegalStateException("No se puede obtener el máximo de un vector vacío.");
        }
        double max = Double.NEGATIVE_INFINITY;
        for (double value : coef) {
            if (value > max) max = value;
        }
        return max;
    }

    /**
     * @brief Calcula y devuelve el índice del valor máximo entre los coeficientes del vector.
     *
     * Si hay múltiples ocurrencias del valor máximo, devuelve el índice de la primera ocurrencia.
     *
     * @return El índice del valor máximo en el vector.
     * @throws IllegalStateException Si el vector está vacío.
     */
    public int getMaxInt() {
        if (coef.isEmpty()) {
            throw new IllegalStateException("No se puede obtener el índice máximo de un vector vacío.");
        }
        double max = Double.NEGATIVE_INFINITY;
        int maxint = -1;
        for (int i = 0; i < coef.size(); ++i) {
            if (coef.get(i) > max) {
                max = coef.get(i);
                maxint = i;
            }
        }
        return maxint;
    }

    /**
     * @brief Calcula y devuelve el valor mínimo entre los coeficientes del vector.
     *
     * @return El valor mínimo de doble precisión en el vector.
     * @throws IllegalStateException Si el vector está vacío.
     */
    public double getMin() {
        if (coef.isEmpty()) {
            throw new IllegalStateException("No se puede obtener el mínimo de un vector vacío.");
        }
        double min = Double.POSITIVE_INFINITY;
        for (double value : coef) {
            if (value < min) min = value;

        }
        return min;
    }

    /**
     * @brief Calcula el producto escalar (producto punto) entre este vector y otro vector.
     *
     * Ambos vectores deben tener la misma dimensión para que la operación sea válida.
     *
     * @param other El otro vector para calcular el producto escalar.
     * @return El resultado del producto escalar.
     * @throws IllegalArgumentException Si el vector 'other' es nulo o si las dimensiones de los vectores no coinciden.
     */
    public double productoEscalar(Vector other) {
        if (other == null || other.coef == null) {
            throw new IllegalArgumentException("El vector para el producto escalar no puede ser nulo.");
        }
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño para el producto escalar.");
        }
        double result = 0;
        for (int i = 0; i < this.size(); i++) {
            result += this.get(i) * other.get(i);
        }
        return result;
    }

    /**
     * @brief Suma un valor escalar a cada coeficiente del vector.
     *
     * Retorna un *nuevo* vector con los resultados de la suma, sin modificar el vector original.
     *
     * @param value El valor escalar a sumar.
     * @return Un nuevo {@link Vector} con cada coeficiente sumado al valor escalar.
     */
    public Vector sum(double value) {
        Vector suma = new Vector(this.size());
        for (int i = 0; i < coef.size(); i++) {
            suma.set(i, coef.get(i) + value);
        }
        return suma;
    }

    /**
     * @brief Suma este vector con otro vector, creando un nuevo vector con los resultados.
     *
     * Ambos vectores deben tener la misma dimensión.
     *
     * @param other El vector a sumar.
     * @return Un nuevo {@link Vector} que es el resultado de la suma de ambos vectores.
     * @throws IllegalArgumentException Si el vector 'other' es nulo o si las dimensiones de los vectores no coinciden.
     */
    public Vector sum(Vector other) {
        if (other == null || other.coef == null) {
            throw new IllegalArgumentException("El vector para la suma no puede ser nulo.");
        }
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño para la suma.");
        }
        Vector suma = new Vector(this.size());
        for (int i = 0; i < this.size(); i++) {
            suma.set(i, coef.get(i) + other.get(i));
        }
        return suma;
    }

    /**
     * @brief Compara este vector con otro para determinar si son iguales en contenido.
     *
     * Dos vectores se consideran iguales si tienen los mismos coeficientes en el mismo orden.
     *
     * @param other El otro vector con el que comparar.
     * @return `true` si los vectores son iguales en contenido, `false` en caso contrario.
     */
    public boolean equals(Vector other) {
        return other != null && this.coef.equals(other.coef);
    }

    /**
     * @brief Compara este vector con otro para determinar si tienen la misma dimensión.
     *
     * @param other El otro vector con el que comparar la dimensión.
     * @return `true` si ambos vectores tienen el mismo número de elementos, `false` en caso contrario.
     */
    public boolean equalDimension(Vector other) {
        return other != null && this.size() == other.size();
    }

    /**
     * @brief Comprueba si el vector contiene un valor específico.
     *
     * @param value El valor de doble precisión a buscar en el vector.
     * @return `true` si el valor está presente en el vector, `false` en caso contrario.
     */
    public boolean isContent(double value) {
        return coef.contains(value);
    }

    /**
     * @brief Concatena otro vector al final de este vector, modificándolo.
     *
     * Los coeficientes del vector 'other' se añaden al final de la lista de coeficientes de este vector.
     *
     * @param other El vector a concatenar.
     */
    public void concat(Vector other) {
        if (other != null && other.coef != null) {
            coef.addAll(other.coef);
        } else if (other == null) {
            logger.warn("Se intentó concatenar un vector nulo.");
        }
    }

    /**
     * @brief Escribe la representación en cadena del vector en un archivo.
     *
     * @param filename El nombre (o ruta) del archivo donde se escribirá el vector.
     * @throws IOException Si ocurre un error de E/S durante la escritura del archivo.
     * @throws IllegalArgumentException Si el nombre del archivo es nulo o vacío.
     */
    public void write(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(this.toString());
        }
    }

    /**
     * @brief Escribe la representación en cadena del vector en un archivo.
     *
     * @param file El objeto {@link java.io.File} donde se escribirá el vector.
     * @throws IOException Si ocurre un error de E/S durante la escritura del archivo.
     * @throws IllegalArgumentException Si el archivo proporcionado es nulo.
     */
    public void write(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.toString());
        }
    }

    /**
     * @brief Lee los coeficientes de un vector desde un archivo y reemplaza el contenido actual del vector.
     *
     * Se espera que el archivo contenga un número por línea.
     *
     * @param filename El nombre (o ruta) del archivo desde el cual se leerán los coeficientes.
     * @throws IOException Si ocurre un error de E/S o el archivo no se encuentra.
     * @throws IllegalArgumentException Si el nombre del archivo es nulo o vacío.
     */
    public void read(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        coef.clear();
        readFile(filename);
    }

    /**
     * @brief Lee los coeficientes de un vector desde un archivo y reemplaza el contenido actual del vector.
     *
     * Utiliza un {@link java.util.Scanner} para leer los valores.
     *
     * @param file El objeto {@link java.io.File} desde el cual se leerán los coeficientes.
     * @throws FileNotFoundException Si el archivo especificado no se encuentra.
     * @throws IllegalArgumentException Si el archivo proporcionado es nulo.
     */
    public void read(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }
        coef.clear();
        readFileWithScanner(file);
    }

    /**
     * @brief Lee los coeficientes de un vector utilizando un objeto {@link java.util.Scanner} existente.
     *
     * El contenido actual del vector se borra antes de la lectura.
     * El Scanner se configura para usar el formato de punto decimal (Locale.US).
     *
     * @param scanner El objeto Scanner desde el cual se leerán los coeficientes.
     * @throws IllegalArgumentException Si el scanner proporcionado es nulo.
     */
    public void read(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("El scanner no puede ser nulo.");
        }
        coef.clear();
        scanner.useLocale(Locale.US); // Still needed for correct parsing
        while (scanner.hasNext()) { // CHANGE: Loop while there are any more tokens
            if (scanner.hasNextDouble()) { // Check if the next token is a double
                coef.add(scanner.nextDouble()); // Add if it's a double
            } else {
                logger.warn("Entrada no válida en el scanner: {}", scanner.next()); // Consume the invalid token
            }
        }
    }

    /**
     * @brief Calcula el módulo (magnitud euclidiana) del vector.
     *
     * @return El módulo del vector como un valor de doble precisión.
     * Retorna 0.0 si el vector está vacío.
     */
    public double module() {
        if (coef.isEmpty()) {
            return 0.0; // El módulo de un vector vacío es 0
        }
        double sum = 0;
        for (double value : coef) {
            sum += Math.pow(value, 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * @brief Multiplica cada coeficiente del vector por un escalar.
     *
     * Este método modifica el vector original.
     *
     * @param scalar El valor escalar por el que se multiplicarán los coeficientes.
     */
    public void multiply(double scalar) {
        for (int i = 0; i < coef.size(); i++) {
            coef.set(i, coef.get(i) * scalar);
        }
    }

    /**
     * @brief Normaliza el vector utilizando la normalización Min-Max.
     *
     * Los valores se escalan entre 0 y 1. Este método modifica el vector original.
     * Si el rango (max - min) es cero, el vector no se modificará y se emitirá una advertencia.
     *
     * @note No se puede normalizar un vector vacío.
     */
    public void normalize() {
        if (coef.isEmpty()) {
            logger.warn("No se puede normalizar un vector vacío.");
            return;
        }
        double min  = this.getMin();
        double max = this.getMax();
        if (max - min == 0) {
            logger.warn("El rango del vector es cero. No se puede normalizar.");
            return;
        }
        for (int i = 0; i < coef.size(); ++i) {
            coef.set(i, (coef.get(i) - min) / (max - min));
        }
    }

    /**
     * @brief Calcula el promedio de los coeficientes del vector.
     *
     * @return El valor promedio de los coeficientes.
     * @throws IllegalStateException Si el vector está vacío.
     */
    public double avg() {
        if (coef.isEmpty()) {
            throw new IllegalStateException("No se puede calcular el promedio de un vector vacío.");
        }
        double sum = 0;
        for (double value : coef) {
            sum += value;
        }
        return sum / coef.size();
    }

    /**
     * @brief Método auxiliar privado para leer coeficientes de un archivo usando BufferedReader.
     *
     * Cada línea del archivo se intenta parsear como un Double.
     *
     * @param filename El nombre (o ruta) del archivo a leer.
     * @throws IOException Si ocurre un error de E/S durante la lectura del archivo.
     * @throws IllegalArgumentException Si el nombre del archivo es nulo o vacío.
     */
    private void readFile(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    coef.add(Double.parseDouble(line));
                } catch (NumberFormatException e) {
                    logger.error("Error al parsear una línea del archivo {}: {}", filename, e.getMessage());
                    // Considerar si quieres detener la lectura o ignorar la línea
                }
            }
        }
    }

    /**
     * @brief Método auxiliar privado para leer coeficientes de un archivo usando Scanner.
     *
     * Los valores numéricos se leen del archivo, ignorando entradas no válidas.
     *
     * @param file El objeto {@link java.io.File} a leer.
     * @throws FileNotFoundException Si el archivo especificado no se encuentra.
     * @throws IllegalArgumentException Si el archivo proporcionado es nulo.
     */
    private void readFileWithScanner(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }
        try (Scanner scanner = new Scanner(file)) {
            scanner.useLocale(Locale.US); // Still needed for correct parsing
            while (scanner.hasNext()) { // CHANGE: Loop while there are any more tokens
                if (scanner.hasNextDouble()) { // Check if the next token is a double
                    coef.add(scanner.nextDouble()); // Add if it's a double
                } else {
                    logger.warn("Entrada no válida en el archivo {}: {}", file.getName(), scanner.next()); // Consume the invalid token
                }
            }
        }
    }

    /**
     * @brief Devuelve una copia de la lista de valores (coeficientes) del vector.
     *
     * Esto asegura que las modificaciones a la lista retornada no afecten el estado interno del vector.
     *
     * @return Una nueva {@link java.util.List} de {@link java.lang.Double} que contiene los coeficientes del vector.
     */
    public List<Double> getValores() {
        return (coef != null) ? new ArrayList<>(coef) : new ArrayList<>();
    }
}