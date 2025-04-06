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
import java.util.Scanner;

public class Vector {
	private List<Double> coef;

    /**
     * Constructor vacio
     */
    public Vector() {
        coef = new ArrayList<>();
    }

    /**
     * Constructor que recibe un array de double
     * @param array
     */
    public Vector(double[] array) {
    	this();
        for (double value : array) {
            coef.add(value);
        }
    }
    
    /**
     * Constructor que recibe un ArrayList de double
     * @param coef
     */
    public Vector(ArrayList<Double> coef) {
    	this.coef = new ArrayList<>(coef);
    }
    
    /**
     * Constructor que recibe un entero de tamaño
     * @param size
     */
    public Vector(int size) {
    	this();
    	for (int i = 0; i < size; ++i) {
    		coef.add(0.0);
    	}
    }

    // Constructor que lee de un fichero
    /**
     * 
     * @param filename
     * @throws IOException
     *
    public Vector(File filename) throws IOException {
        coef = new ArrayList<>();
        readFile(filename);
    } */

    /**
     * Constructor que lee de un fichero usando Scanner
     * @param file
     * @throws FileNotFoundException
     */
    public Vector(File file) throws FileNotFoundException {
        coef = new ArrayList<>();
        readFileWithScanner(file);
    }

    /**
     * Constructor que lee de un String
     * @param str
     */
    public Vector(String str) {
        coef = new ArrayList<>();
        String[] values = str.split(",");
        for (String value : values) {
            coef.add(Double.parseDouble(value.trim()));
        }
    }

    /**
     * Método para clonar un vector
     * @return la copia del vector original
     */
    public Vector(Vector other) {
        this.coef = new ArrayList<>(other.coef);
    }
    
    /**
     * Método para mostrar la dimensión del vector
     * @return entero con la dimansion del vector
     */
    public int size() {
        return coef.size();
    }

    public void clear() {
        coef.clear();
    }

    
    public String toString() {
        return coef.toString();
    }

    public void print() {
        Logger logger = LoggerFactory.getLogger(Vector.class);
        logger.info(this.toString());
    }

    public double get(int index) {
        return coef.get(index);
    }

    public void set(int index, double value) {
        coef.set(index, value);
    }

    public void add(double value) {
        coef.add(value);
    }
    
    public void add(Vector other) {
        if (this.size() != other.size()) throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño");
        for (int i = 0; i < this.size(); i++) {
        	coef.set(i, coef.get(i) + other.get(i));
        }
    }

    public void remove(int index) {
        coef.remove(index);
    }

    public double getMax() {
        double max = Double.NEGATIVE_INFINITY;
        for (double value : coef) {
            if (value > max) max = value;
        }
        return max;
    }
    
    public int getMaxInt() {
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

    public double getMin() {
        double min = Double.POSITIVE_INFINITY;
        for (double value : coef) {
            if (value < min) min = value;
            
        }
        return min;
    }
    
    // cambiar nombre
    public double productoEscalar(Vector other) {
        if (this.size() != other.size())throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño");
        double result = 0;
        for (int i = 0; i < this.size(); i++) result += this.get(i) * other.get(i);
        return result;
    }

    public Vector sum(double value) {
    	Vector suma = new Vector();
        for (int i = 0; i < coef.size(); i++) {
        	suma.add(coef.get(i) + value);
        }
        return suma;
    }

    public Vector sum(Vector other) {
        if (this.size() != other.size()) throw new IllegalArgumentException("Los vectores deben tener el mismo tamaño");
        Vector suma = new Vector();
        for (int i = 0; i < this.size(); i++) {
        	suma.add(coef.get(i) + other.get(i));
        }
        return suma;
    }

    public boolean equals(Vector other) {
        return this.coef.equals(other.coef);
    }

    public boolean equalDimension(Vector other) {
        return this.size() == other.size();
    }

    public boolean isContent(double value) {
        return coef.contains(value);
    }

    public void concat(Vector other) {
        coef.addAll(other.coef);
    }

    public void write(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(this.toString());
        }
    }

    public void write(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.toString());
        }
    }

    public void read(String filename) throws IOException {
        coef.clear();
        readFile(filename);
    }

    public void read(File file) throws FileNotFoundException {
        coef.clear();
        readFileWithScanner(file);
    }

    public void read(Scanner scanner) {
        coef.clear();
        while (scanner.hasNextDouble()) {
            coef.add(scanner.nextDouble());
        }
    }

    public double module() {
        double sum = 0;
        for (double value : coef) {
            sum += Math.pow(value, 2);
        }
        return Math.sqrt(sum);
    }

    public void multiply(double scalar) {
        for (int i = 0; i < coef.size(); i++) {
            coef.set(i, coef.get(i) * scalar);
        }
    }

    public void normalize() {
        double min  = this.getMin();
        double max = this.getMax();
        for (int i = 0; i < coef.size(); ++i) coef.set(i, (coef.get(i) - min) / (max - min));
    }

    public double avg() {
        double sum = 0;
        for (double value : coef) {
            sum += value;
        }
        return sum / coef.size();
    }

    private void readFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                coef.add(Double.parseDouble(line));
            }
        }
    }

    private void readFileWithScanner(File file) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextDouble()) {
                coef.add(scanner.nextDouble());
            }
        }
    }
    
    public List<Double> getValores() {
        return this.coef;
    }
}
