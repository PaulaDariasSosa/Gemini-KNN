package vectores;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Matriz {
    private List<Vector> matrix;
    private int numRows;
    private int numCols;
    private boolean isTransposed;

    // Constructor que crea una matriz de dimensión 1x1 con un único elemento 0
    public Matriz() {
        this(1, 1);
        matrix = new ArrayList<Vector>();
        matrix.add(new Vector(1));
        isTransposed = false;
    }

    // Constructor que crea una matriz de dimensión mxn con todos sus elementos a 0
    public Matriz(int m, int n) {
        this.numRows = m;
        this.numCols = n;
        matrix = new ArrayList<Vector>(m);
        for (int i = 0; i < m; i++) {
            matrix.add(i, (new Vector(n)));
        }
        isTransposed = false;
    }

    // Constructor que crea una matriz de dimensión mxn con los elementos del array bidimensional coef
    public Matriz(int m, int n, double[][] coef) {
        this(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
            	Vector aux = matrix.get(i);
            	aux.set(j, coef[i][j]);
                matrix.set(i, aux);
            }
        }
        isTransposed = false;
    }
    
    // Constructor que crea una matriz a partir de un vector de Vector
    public Matriz(List<Vector> vectors) {
    	this(vectors.size(), vectors.get(0).size());
        // Verificar si el ArrayList está vacío
        if (vectors == null || vectors.isEmpty()) {
            throw new IllegalArgumentException("ArrayList<Vector> no puede estar vacío");
        }
        matrix = vectors;
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
            matrix.get(i).print();
        }
    }
    
    public static Matriz multiply(Matriz a, Matriz b) {
        if (a.getNumCols() != b.getNumRows())  throw new IllegalArgumentException("Número de columnas de A no coincide con el número de filas de B");
        Matriz result = new Matriz(a.getNumRows(), b.getNumCols());
        for (int i = 0; i < a.getNumRows(); i++) {
            for (int j = 0; j < b.getNumCols(); j++) {
                double value = a.matrix.get(i).productoEscalar(getColumn(b.matrix, j));
                Vector aux = result.matrix.get(i);
                aux.set(j, value);
                result.matrix.set(i, aux);
            }
        }
        return result;
    }
    
    // Método auxiliar para obtener una columna de una matriz
    private static Vector getColumn(List<Vector> matrix, int colIndex) {
        Vector column = new Vector();
        for (int i = 0; i < matrix.size(); i++) {
            column.add(matrix.get(i).get(colIndex));
        }
        return column;
    }
    
    public Matriz read(String filename) throws IOException {
    	try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int m = Integer.parseInt(reader.readLine());
            int n = Integer.parseInt(reader.readLine());
            double[][] coef = new double[m][n];
            for (int i = 0; i < m; i++) {
                String[] lineValues = reader.readLine().split(" ");
                for (int j = 0; j < n; j++) {
                    coef[i][j] = Double.parseDouble(lineValues[j]);
                }
            }
            return new Matriz (m, n, coef);
        }
    }
    
    // Método para escribir los datos de la matriz en un archivo de texto
    public void write(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(numRows + "\n");
            writer.write(numCols + "\n");
            for (int i = 0; i < numRows; i++) {
                writer.write(matrix.get(i).toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
            }
        }
    }
    
    public double get(int x, int y) {
        if (x < 0 || x >= numRows || y < 0 || y >= numCols) {
            throw new IndexOutOfBoundsException("Los índices están fuera del rango de la matriz.");
        }
        return matrix.get(x).get(y);
    }
    
    public boolean equals(Matriz other) {
        if (numRows != other.numRows || numCols != other.numCols) {
            return false;
        }
        for (int i = 0; i < numRows; i++) {
            if (!this.matrix.get(i).equals(other.matrix.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public void transpose() {
    	int temp = numRows;
    	numRows = numCols;
    	numCols = temp;
    	// Crear una nueva matriz transpuesta
    	ArrayList<Vector> transposedMatrix = new ArrayList<Vector>(numRows);
        for (int i = 0; i < numRows; i++) {
            transposedMatrix.set(i, (new Vector(numCols)));
            for (int j = 0; j < numCols; j++) {
            	Vector aux = transposedMatrix.get(i);
            	aux.set(j, matrix.get(j).get(i));
                transposedMatrix.set(i, aux);
            }
        }
        // Actualizar la matriz original con la matriz transpuesta
        matrix = transposedMatrix;
        // Actualizar el estado de orientación
        isTransposed = !isTransposed;
    }
    
    public void deleteRows(int indice) {
    	matrix.remove(indice);
    }
    
    // hace la traspuesta y luego llama a deleterows
    public void deleteCols(int indice) {
    	this.transpose();
    	this.deleteRows(indice);
    }
    
    public void addRows() {
    	this.numRows += 1;
    }
    
    public void addCols() {
    	this.numCols += 1;
    }
    
    public List<Vector> normalizar(){
    	this.transpose();
    	List<Vector> nueva = this.matrix;
    	for (Vector fila: nueva) fila.normalize();
    	this.transpose();
    	return nueva;
    }
    
    public void set(int i, int j, double valor) {
    	Vector fila = matrix.get(i);
    	fila.set(j, valor);
    	matrix.set(i, fila);
    }
}
