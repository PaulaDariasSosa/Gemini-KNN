package vectores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Matriz {
    private List<Vector> matrix;
    private int numRows;
    private int numCols;
    private boolean isTransposed;
    private static final Logger logger = LoggerFactory.getLogger(Matriz.class);

    // Constructor que crea una matriz de dimensión 1x1 con un único elemento 0
    public Matriz() {
        this(1, 1);
        matrix = new ArrayList<>();
        matrix.add(new Vector(1));
        isTransposed = false;
    }

    // Constructor que crea una matriz de dimensión mxn con todos sus elementos a 0
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

    // Constructor que crea una matriz de dimensión mxn con los elementos del array bidimensional coef
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

    // Constructor que crea una matriz a partir de un vector de Vector
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

    // Método para obtener el número de filas
    public int getNumRows() {
        return isTransposed ? numCols : numRows;
    }

    // Método para obtener el número de columnas
    public int getNumCols() {
        return isTransposed ? numRows : numCols;
    }

    // Método para imprimir la matriz
    public void print() {
        for (int i = 0; i < numRows; i++) {
            if (matrix.get(i) != null) {
                matrix.get(i).print();
            } else {
                logger.warn("Fila {} es nula.", i);
            }
        }
    }

    // Método para escribir los datos de la matriz en un archivo de texto
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

    public void transpose() {
        // No es necesario transponer realmente la matriz en memoria para el manejo de isTransposed
        isTransposed = !isTransposed;
    }

    public void deleteRows(int indice) {
        if (indice < 0 || indice >= numRows) {
            throw new IndexOutOfBoundsException("Índice de fila " + indice + " fuera de rango [0, " + (numRows - 1) + "].");
        }
        matrix.remove(indice);
        numRows--;
    }

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

    public void addRows() {
        matrix.add(new Vector(numCols));
        numRows++;
    }

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