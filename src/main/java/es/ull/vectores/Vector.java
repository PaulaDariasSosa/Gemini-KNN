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
    private static final Logger logger = LoggerFactory.getLogger(Vector.class);

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
        if (array != null) {
            for (double value : array) {
                coef.add(value);
            }
        } else {
            logger.warn("Se proporcionó un array nulo al constructor.");
        }
    }

    /**
     * Constructor que recibe un ArrayList de double
     * @param coef
     */
    public Vector(List<Double> coef) {
        this.coef = (coef != null) ? new ArrayList<>(coef) : new ArrayList<>();
        if (coef == null) {
            logger.warn("Se proporcionó una lista nula al constructor.");
        }
    }

    /**
     * Constructor que recibe un entero de tamaño
     * @param size
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
     * Constructor que lee de un fichero usando Scanner
     * @param file
     * @throws FileNotFoundException
     */
    public Vector(File file) throws FileNotFoundException {
        coef = new ArrayList<>();
        if (file == null) {
            throw new IllegalArgumentException("El archivo proporcionado no puede ser nulo.");
        }
        readFileWithScanner(file);
    }

    /**
     * Constructor que lee de un String
     * @param str
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
     * Método para clonar un vector
     * @return la copia del vector original
     */
    public Vector(Vector other) {
        this.coef = (other != null && other.coef != null) ? new ArrayList<>(other.coef) : new ArrayList<>();
        if (other == null) {
            logger.warn("Se intentó clonar un vector nulo.");
        }
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
        if(logger.isInfoEnabled()){
            logger.info(this.toString());
        }
    }

    public double get(int index) {
        if (index < 0 || index >= coef.size()) {
            throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango [0, " + (coef.size() - 1) + "]");
        }
        return coef.get(index);
    }

    public void set(int index, double value) {
        if (index < 0 || index >= coef.size()) {
            throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango [0, " + (coef.size() - 1) + "]");
        }
        coef.set(index, value);
    }

    public void add(double value) {
        coef.add(value);
    }

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

    public void remove(int index) {
        if (index < 0 || index >= coef.size()) {
            throw new IndexOutOfBoundsException("Índice " + index + " fuera de rango [0, " + (coef.size() - 1) + "]");
        }
        coef.remove(index);
    }

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

    // cambiar nombre
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

    public Vector sum(double value) {
        Vector suma = new Vector(this.size());
        for (int i = 0; i < coef.size(); i++) {
            suma.set(i, coef.get(i) + value);
        }
        return suma;
    }

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

    public boolean equals(Vector other) {
        return other != null && this.coef.equals(other.coef);
    }

    public boolean equalDimension(Vector other) {
        return other != null && this.size() == other.size();
    }

    public boolean isContent(double value) {
        return coef.contains(value);
    }

    public void concat(Vector other) {
        if (other != null && other.coef != null) {
            coef.addAll(other.coef);
        } else if (other == null) {
            logger.warn("Se intentó concatenar un vector nulo.");
        }
    }

    public void write(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(this.toString());
        }
    }

    public void write(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.toString());
        }
    }

    public void read(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo o vacío.");
        }
        coef.clear();
        readFile(filename);
    }

    public void read(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }
        coef.clear();
        readFileWithScanner(file);
    }

    public void read(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("El scanner no puede ser nulo.");
        }
        coef.clear();
        while (scanner.hasNextDouble()) {
            try {
                coef.add(scanner.nextDouble());
            } catch (java.util.InputMismatchException e) {
                logger.warn("Entrada no válida en el scanner: {}", scanner.next());
                // Considerar si quieres detener la lectura o ignorar el valor
            }
        }
    }

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

    public void multiply(double scalar) {
        for (int i = 0; i < coef.size(); i++) {
            coef.set(i, coef.get(i) * scalar);
        }
    }

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

    private void readFileWithScanner(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo.");
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextDouble()) {
                try {
                    coef.add(scanner.nextDouble());
                } catch (java.util.InputMismatchException e) {
                    logger.warn("Entrada no válida en el archivo {}: {}", file.getName(), scanner.next());
                    // Considerar si quieres detener la lectura o ignorar el valor
                }
            }
        }
    }

    public List<Double> getValores() {
        return (coef != null) ? new ArrayList<>(coef) : new ArrayList<>();
    }
}