package vectores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @file Matriz.java
 * @brief Implementa una clase para representar y manipular matrices de números de doble precisión.
 *
 * Esta clase gestiona matrices donde cada fila es un objeto {@link Vector}.
 * Proporciona constructores para diferentes inicializaciones, métodos para acceder
 * y modificar elementos, operaciones de redimensionamiento (añadir/eliminar filas/columnas),
 * transposición conceptual y normalización de filas.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public class Matriz {
    /**
     * @brief Lista de objetos {@link Vector} que representan las filas de la matriz.
     */
    private List<Vector> matrix;
    /**
     * @brief Número de filas de la matriz.
     */
    private int numRows;
    /**
     * @brief Número de columnas de la matriz.
     */
    private int numCols;
    /**
     * @brief Bandera que indica si la matriz debe considerarse transpuesta para operaciones de acceso.
     * <p>
     * Este valor no altera la estructura de almacenamiento real de la matriz, solo su interpretación
     * para `getNumRows()` y `getNumCols()`.
     */
    boolean isTransposed;
    /**
     * @brief Objeto Logger para registrar mensajes informativos y de error.
     * <p>
     * Utiliza SLF4J para el registro de eventos dentro de la clase.
     */
    private static final Logger logger = LoggerFactory.getLogger(Matriz.class);

    /**
     * @brief Constructor que crea una matriz de dimensión 1x1 con un único elemento inicializado a 0.0.
     */
    public Matriz() {
        this(1, 1);
        matrix = new ArrayList<>();
        matrix.add(new Vector(1));
        isTransposed = false;
    }

    /**
     * @brief Constructor que crea una matriz de dimensión mxn con todos sus elementos inicializados a 0.0.
     *
     * @param m El número de filas de la matriz.
     * @param n El número de columnas de la matriz.
     * @throws IllegalArgumentException Si m o n son menores o iguales a cero.
     */
    public Matriz(int m, int n) {
        if (m <= 0 || n <= 0) {
            throw new IllegalArgumentException("Las dimensiones de la matriz deben ser positivas (m > 0, n > 0).");
        }
        this.numRows = m;
        this.numCols = n;
        matrix = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            matrix.add(new Vector(n));
        }
        isTransposed = false;
    }

    /**
     * @brief Constructor que crea una matriz de dimensión mxn con los elementos de un array bidimensional.
     *
     * @param m El número de filas de la matriz.
     * @param n El número de columnas de la matriz.
     * @param coef El array bidimensional de dobles con los valores para inicializar la matriz.
     * @throws IllegalArgumentException Si el array de coeficientes es nulo o sus dimensiones no coinciden con m y n.
     */
    public Matriz(int m, int n, double[][] coef) {
        this(m, n);
        if (coef == null || coef.length != m || (m > 0 && coef[0].length != n)) {
            throw new IllegalArgumentException("El array de coeficientes no coincide con las dimensiones proporcionadas.");
        }
        for (int i = 0; i < m; i++) {
            Vector aux = matrix.get(i);
            for (int j = 0; j < n; j++) {
                aux.set(j, coef[i][j]);
            }
        }
        isTransposed = false;
    }

    /**
     * @brief Constructor que crea una matriz a partir de una lista de objetos {@link Vector}.
     *
     * Se verifica que todos los vectores de la lista tengan la misma dimensión, la cual se convertirá
     * en el número de columnas de la matriz. El número de filas será el tamaño de la lista.
     *
     * @param vectors La lista de objetos {@link Vector} que formarán las filas de la matriz.
     * @throws IllegalArgumentException Si la lista de vectores es nula, vacía o si los vectores tienen dimensiones diferentes.
     */
    public Matriz(List<Vector> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalArgumentException("La lista de vectores no puede ser nula o vacía.");
        }
        int firstVectorSize = vectors.get(0).size();
        for (Vector vector : vectors) {
            if (vector.size() != firstVectorSize) {
                throw new IllegalArgumentException("Todos los vectores de la lista deben tener la misma dimensión.");
            }
        }
        this.numRows = vectors.size();
        this.numCols = firstVectorSize;
        this.matrix = new ArrayList<>(vectors);
        isTransposed = false;
    }

    /**
     * @brief Obtiene el número de filas de la matriz.
     *
     * Si la matriz está marcada como transpuesta (`isTransposed` es true),
     * devuelve el número original de columnas; de lo contrario, devuelve el número original de filas.
     *
     * @return El número efectivo de filas de la matriz.
     */
    public int getNumRows() {
        return isTransposed ? numCols : numRows;
    }

    /**
     * @brief Obtiene el número de columnas de la matriz.
     *
     * Si la matriz está marcada como transpuesta (`isTransposed` es true),
     * devuelve el número original de filas; de lo contrario, devuelve el número original de columnas.
     *
     * @return El número efectivo de columnas de la matriz.
     */
    public int getNumCols() {
        return isTransposed ? numRows : numCols;
    }

    /**
     * @brief Imprime cada fila de la matriz en la salida estándar.
     *
     * Utiliza el método `print()` de la clase {@link Vector} para cada fila.
     */
    public void print() {
        for (int i = 0; i < numRows; i++) {
            if (matrix.get(i) != null) {
                matrix.get(i).print();
            } else {
                logger.warn("Fila {} es nula.", i);
            }
        }
    }

    /**
     * @brief Escribe los datos de la matriz en un archivo de texto.
     *
     * El archivo contendrá primero el número de filas y columnas, seguido de cada fila del vector.
     * Cada fila del vector se escribe en una nueva línea con sus elementos separados por espacios.
     *
     * @param filename El nombre (o ruta) del archivo donde se escribirá la matriz.
     * @throws IOException Si ocurre un error de E/S durante la escritura del archivo.
     * @throws IllegalArgumentException Si el nombre del archivo es nulo o vacío.
     */
    public void write(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(numRows + "\n");
            writer.write(numCols + "\n");
            for (int i = 0; i < numRows; i++) {
                if (matrix.get(i) != null) {
                    writer.write(matrix.get(i).toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
                } else {
                    logger.warn("Fila {} es nula. No se escribirá correctamente.", i);
                    writer.write("\n"); // Escribir una línea vacía para indicar la fila nula
                }
            }
        } catch (IOException e) {
            throw new IOException("Error al escribir en el archivo " + filename + ": " + e.getMessage(), e);
        }
    }

    /**
     * @brief Obtiene el valor de un elemento específico de la matriz.
     *
     * @param x El índice de la fila (basado en cero).
     * @param y El índice de la columna (basado en cero).
     * @return El valor de doble precisión en la posición (x, y).
     * @throws IndexOutOfBoundsException Si los índices x o y están fuera del rango de la matriz.
     * @throws IllegalStateException Si la fila a la que se intenta acceder es nula.
     */
    public double get(int x, int y) {
        if (x < 0 || x >= numRows || y < 0 || y >= numCols) {
            throw new IndexOutOfBoundsException("Índices (" + x + ", " + y + ") fuera del rango de la matriz [" + numRows + "x" + numCols + "].");
        }
        if (matrix.get(x) == null) {
            logger.error("Se intentó acceder a una fila nula en el índice {}.", x);
            throw new IllegalStateException("Fila nula en la matriz.");
        }
        return matrix.get(x).get(y);
    }

    /**
     * @brief Establece un nuevo valor para un elemento específico de la matriz.
     *
     * @param i El índice de la fila (basado en cero).
     * @param j El índice de la columna (basado en cero).
     * @param valor El nuevo valor de doble precisión a establecer.
     * @throws IndexOutOfBoundsException Si los índices i o j están fuera del rango de la matriz.
     * @throws IllegalStateException Si la fila a la que se intenta acceder es nula.
     */
    public void set(int i, int j, double valor) {
        if (i < 0 || i >= numRows || j < 0 || j >= numCols) {
            throw new IndexOutOfBoundsException("Índices (" + i + ", " + j + ") fuera del rango de la matriz [" + numRows + "x" + numCols + "].");
        }
        if (matrix.get(i) == null) {
            logger.error("Se intentó modificar una fila nula en el índice {}.", i);
            throw new IllegalStateException("Fila nula en la matriz.");
        }
        try {
            Vector fila = matrix.get(i);
            fila.set(j, valor);
            matrix.set(i, fila);
        } catch (IndexOutOfBoundsException e) {
            logger.error("Error al establecer el valor en la matriz ({}, {}): {}", i, j, e.getMessage());
            throw new IndexOutOfBoundsException("Índices (" + i + ", " + j + ") fuera del rango de la matriz [" + numRows + "x" + numCols + "].");
        }
    }

    /**
     * @brief Compara esta matriz con otra para determinar si son iguales en dimensiones y contenido.
     *
     * @param other La otra matriz con la que comparar.
     * @return `true` si las matrices tienen las mismas dimensiones y todos sus elementos son iguales, `false` en caso contrario.
     */
    public boolean equals(Matriz other) {
        if (other == null) {
            return false;
        }
        if (numRows != other.numRows || numCols != other.numCols) {
            return false;
        }
        for (int i = 0; i < numRows; i++) {
            if (this.matrix.get(i) == null || other.matrix.get(i) == null) {
                return false; // Si alguna fila es nula, las matrices no son iguales
            }
            if (!this.matrix.get(i).equals(other.matrix.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @brief Alterna el estado de transposición de la matriz.
     *
     * Este método no realiza una transposición física de los datos en memoria,
     * sino que simplemente cambia el valor de la bandera `isTransposed`.
     * Las dimensiones reportadas por `getNumRows()` y `getNumCols()` se invertirán.
     */
    public void transpose() {
        // No es necesario transponer realmente la matriz en memoria para el manejo de isTransposed
        isTransposed = !isTransposed;
    }

    /**
     * @brief Elimina una fila específica de la matriz.
     *
     * @param indice El índice de la fila a eliminar (basado en cero).
     * @throws IndexOutOfBoundsException Si el índice de la fila está fuera del rango válido.
     */
    public void deleteRows(int indice) {
        if (indice < 0 || indice >= numRows) {
            throw new IndexOutOfBoundsException("Índice de fila " + indice + " fuera de rango [0, " + (numRows - 1) + "].");
        }
        matrix.remove(indice);
        numRows--;
    }

    /**
     * @brief Elimina una columna específica de la matriz.
     *
     * Elimina el elemento en el índice de columna especificado de cada {@link Vector} que compone las filas.
     *
     * @param indice El índice de la columna a eliminar (basado en cero).
     * @throws IndexOutOfBoundsException Si el índice de la columna está fuera del rango válido.
     */
    public void deleteCols(int indice) {
        if (indice < 0 || indice >= numCols) {
            throw new IndexOutOfBoundsException("Índice de columna " + indice + " fuera de rango [0, " + (numCols - 1) + "].");
        }
        for (Vector fila : matrix) {
            if (fila != null && indice < fila.size()) {
                fila.remove(indice);
            } else if (fila != null) {
                logger.warn("Intento de eliminar columna {} de una fila con tamaño {}.", indice, fila.size());
            }
        }
        numCols--;
    }

    /**
     * @brief Añade una nueva fila al final de la matriz, rellenada con ceros.
     * <p>
     * La nueva fila tendrá la misma dimensión (número de columnas) que las filas existentes.
     */
    public void addRows() {
        matrix.add(new Vector(numCols));
        numRows++;
    }

    /**
     * @brief Añade una nueva columna a cada fila de la matriz, rellenada con ceros.
     * <p>
     * Se añade un 0.0 al final de cada {@link Vector} que compone las filas.
     */
    public void addCols() {
        for (Vector fila : matrix) {
            if (fila != null) {
                fila.add(0.0);
            } else {
                logger.warn("Fila nula encontrada al añadir columna.");
            }
        }
        numCols++;
    }

    /**
     * @brief Normaliza cada fila de la matriz utilizando la normalización Min-Max.
     *
     * Este método no modifica la matriz original, sino que devuelve una *nueva* lista de
     * {@link Vector} con las filas normalizadas. Las filas nulas en la matriz original
     * resultarán en entradas nulas en la lista normalizada.
     *
     * @return Una {@link java.util.List} de {@link Vector} donde cada {@link Vector} representa
     * una fila normalizada de la matriz original.
     */
    public List<Vector> normalizar() {
        // Crear una nueva lista para no modificar la matriz original directamente durante la normalización
        List<Vector> normalizedMatrix = new ArrayList<>(numRows);
        for (Vector fila : matrix) {
            if (fila != null) {
                Vector normalizedRow = new Vector(fila); // Crear una copia para normalizar
                normalizedRow.normalize();
                normalizedMatrix.add(normalizedRow);
            } else {
                logger.warn("Fila nula encontrada durante la normalización.");
                normalizedMatrix.add(null); // O manejar de otra manera
            }
        }
        return normalizedMatrix;
    }
}