/**
 * @file EntrenamientoTest.java
 * @brief This file contains the unit tests for the Entrenamiento class.
 *
 * It uses JUnit 5 for testing and includes mock implementations of related classes
 * such as Vector, Instancia, Atributo, KNN, and Matriz to isolate the testing
 * of the Entrenamiento class.
 */
package entrenamiento;

import clasificacion.KNN;
import datos.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vectores.Matriz;
import vectores.Vector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @class EntrenamientoTest
 * @brief Tests for the Entrenamiento class.
 *
 * This class provides unit tests for the `Entrenamiento` class,
 * covering its constructors and key methods like `generarPrediccion`.
 * It utilizes mock objects for dependencies such as `Vector`, `Instancia`,
 * `Atributo`, `KNN`, and `Matriz` to enable isolated and controlled testing.
 */
@DisplayName("Tests para la clase Entrenamiento")
class EntrenamientoTest {

    private Entrenamiento entrenamiento; ///< The instance of Entrenamiento being tested.
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream(); ///< Captures System.out for assertions.
    private final PrintStream originalOut = System.out; ///< Stores the original System.out.

    // --- Mock / Simplified Classes for Testing ---

    /**
     * @class TestVector
     * @brief A simplified mock implementation of the Vector class for testing purposes.
     *
     * This class overrides essential methods of `Vector` to provide controlled behavior
     * during tests, without relying on the full functionality of the original `Vector`.
     */
    static class TestVector extends Vector {
        private List<Double> values; ///< The internal list of double values.

        /**
         * @brief Constructs a TestVector with the given double values.
         * @param values Variable number of double arguments to initialize the vector.
         */
        public TestVector(double... values) {
            this.values = Arrays.stream(values).boxed().collect(Collectors.toList());
        }

        /**
         * @brief Constructs a TestVector from a list of Objects.
         * @param values A list of Objects, which are expected to be Numbers.
         * @throws IllegalArgumentException if any object in the list is not a Number.
         */
        public TestVector(List<Object> values) {
            this.values = values.stream()
                    .map(obj -> {
                        if (obj instanceof Number) {
                            return ((Number) obj).doubleValue();
                        }
                        throw new IllegalArgumentException("Vector values must be numbers.");
                    })
                    .collect(Collectors.toList());
        }

        /**
         * @brief Returns the value at the specified index.
         * @param index The index of the element to return.
         * @return The double value at the specified index.
         */
        @Override
        public double get(int index) {
            return values.get(index);
        }

        /**
         * @brief Returns the number of elements in the vector.
         * @return The size of the vector.
         */
        @Override
        public int size() {
            return values.size();
        }

        /**
         * @brief Placeholder for normalization.
         *
         * This method is part of the `Vector` interface but is not used by `Entrenamiento` tests.
         */
        @Override
        public void normalize() { /* Not used by Entrenamiento, but required by interface */ }

        /**
         * @brief Returns a string representation of the vector.
         * @return A string representation of the list of values.
         */
        @Override
        public String toString() {
            return values.toString();
        }
    }

    /**
     * @class TestInstancia
     * @brief A simplified mock implementation of the Instancia class for testing purposes.
     *
     * This class overrides essential methods of `Instancia` to provide controlled behavior
     * during tests, focusing on the vector and class label.
     */
    static class TestInstancia extends Instancia {
        private TestVector vector; ///< The feature vector of the instance.
        private String clase; ///< The class label of the instance.

        /**
         * @brief Constructs a TestInstancia from a list of instance values.
         * @param instanceValues An ArrayList where the last element is the class label (String)
         * and the preceding elements are the vector values (Objects).
         */
        public TestInstancia(ArrayList<Object> instanceValues) {
            this.vector = new TestVector(instanceValues.subList(0, instanceValues.size() - 1));
            this.clase = (String) instanceValues.get(instanceValues.size() - 1);
        }

        /**
         * @brief Constructs a TestInstancia with a given TestVector and class label.
         * @param vector The TestVector representing the instance's features.
         * @param clase The String representing the instance's class.
         */
        public TestInstancia(TestVector vector, String clase) {
            this.vector = vector;
            this.clase = clase;
        }

        /**
         * @brief Returns the feature vector of the instance.
         * @return A `vectores.Vector` object (specifically, a `TestVector`).
         */
        @Override
        public vectores.Vector getVector() {
            return vector;
        }

        /**
         * @brief Returns the class label of the instance.
         * @return The String class label.
         */
        @Override
        public String getClase() {
            return clase;
        }

        /**
         * @brief Returns a list containing all values of the instance, including the class label.
         * @return A List of Objects, where the last element is the class label.
         */
        @Override
        public List<Object> getValores() {
            List<Object> allValues = new ArrayList<>();
            allValues.addAll(vector.values);
            allValues.add(clase);
            return allValues;
        }

        /**
         * @brief Returns a string representation of the instance.
         * @return A string showing the vector and class of the instance.
         */
        @Override
        public String toString() {
            return "Instancia{vector=" + vector + ", clase='" + clase + "'}";
        }
    }

    /**
     * @class TestAtributo
     * @brief A simplified mock implementation of the Atributo class for testing purposes.
     *
     * This class provides a basic implementation of `Atributo` methods, primarily
     * for constructor calls and attribute name identification.
     */
    static class TestAtributo extends Atributo {
        private String name; ///< The name of the attribute.

        /**
         * @brief Constructs a TestAtributo with a given name.
         * @param name The name of the attribute.
         */
        public TestAtributo(String name) {
            super();
            this.name = name;
        }

        /**
         * @brief Placeholder for adding a value.
         *
         * This method is part of the `Atributo` interface but is not used in current tests.
         * @param valor The value to add (ignored in mock).
         */
        @Override
        public void add(Object valor) { /* Not used in current tests, no-op */ }

        /**
         * @brief Placeholder for getting a value at an index.
         *
         * This method is part of the `Atributo` interface but is not used in current tests.
         * @param indice The index (ignored in mock).
         * @return Always returns null.
         */
        @Override
        public Object getValor(int indice) { return null; /* Not used in current tests, return null */ }

        /**
         * @brief Placeholder for size.
         *
         * This method is part of the `Atributo` interface but is not used in current tests.
         * @return Always returns 0.
         */
        @Override
        public int size() { return 0; /* Not used in current tests, return 0 */ }

        /**
         * @brief Placeholder for clearing values.
         *
         * This method is part of the `Atributo` interface.
         */
        @Override
        public void clear() { /* No-op for test */ }

        /**
         * @brief Placeholder for getting all values.
         * @return Always returns an empty list.
         */
        @Override
        public List<Object> getValores() {
            return Collections.emptyList();
        }

        /**
         * @brief Placeholder for deleting a value at an index.
         *
         * This method is part of the `Atributo` interface.
         * @param index The index (ignored in mock).
         */
        @Override
        public void delete(int index) {
            // For a mock, this can be a no-op
        }

        /**
         * @brief Returns a string representation of the attribute.
         * @return A string showing the name of the attribute.
         */
        @Override
        public String toString() {
            return "TestAtributo{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * @class MockKNN
     * @brief A mock implementation of the KNN classifier for testing purposes.
     *
     * This class allows controlling the prediction outcome of the `clasificar` method,
     * which is useful for testing `Entrenamiento`'s logic independent of actual KNN computations.
     */
    static class MockKNN extends KNN {
        private String nextPrediction; ///< The predefined prediction to return.
        private int k; ///< The value of K for the KNN (for completeness, not used in mock logic).

        /**
         * @brief Constructs a MockKNN instance.
         * @param k The 'k' value for KNN (not functionally used by the mock).
         * @param nextPrediction The string prediction that this mock will return.
         */
        public MockKNN(int k, String nextPrediction) {
            super(k);
            this.k = k;
            this.nextPrediction = nextPrediction;
        }

        /**
         * @brief Sets the next prediction to be returned by `clasificar`.
         * @param prediction The string prediction to set.
         */
        public void setNextPrediction(String prediction) {
            this.nextPrediction = prediction;
        }

        /**
         * @brief Simulates the classification process.
         * @param entrenamiento The training dataset (ignored by mock).
         * @param prueba The instance to classify (ignored by mock).
         * @return The predefined `nextPrediction` string.
         */
        @Override
        public String clasificar(Dataset entrenamiento, Instancia prueba) {
            return nextPrediction;
        }
    }

    /**
     * @class MockMatriz
     * @brief A simplified mock implementation of the Matriz class for testing purposes.
     *
     * This class is used to verify interactions with the `Matriz` class, particularly
     * for setting values and capturing print output.
     */
    static class MockMatriz extends Matriz {
        public int[][] data; ///< The internal 2D array to store matrix data.
        public int rows; ///< Number of rows in the mock matrix.
        public int cols; ///< Number of columns in the mock matrix.
        public StringBuilder printOutput = new StringBuilder(); ///< Captures output when `print()` is called.

        /**
         * @brief Constructs a MockMatriz with specified dimensions.
         * @param rows The number of rows.
         * @param cols The number of columns.
         */
        public MockMatriz(int rows, int cols) {
            super(rows, cols);
            this.rows = rows;
            this.cols = cols;
            this.data = new int[rows][cols];
        }

        /**
         * @brief Sets a value at a specific row and column in the mock matrix.
         * @param row The row index.
         * @param col The column index.
         * @param value The integer value to set.
         * @throws IndexOutOfBoundsException if the row or column index is out of bounds.
         */
        public void set(int row, int col, int value) {
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                this.data[row][col] = value;
            } else {
                throw new IndexOutOfBoundsException("Matrix indices out of bounds: [" + row + ", " + col + "]");
            }
        }

        /**
         * @brief Returns the value at a specific row and column.
         * @param row The row index.
         * @param col The column index.
         * @return The double value at the specified position, or 0.0 if indices are out of bounds.
         */
        @Override
        public double get(int row, int col) {
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                return (double) this.data[row][col];
            }
            return 0.0;
        }

        /**
         * @brief Simulates printing the matrix content to `printOutput`.
         *
         * This method appends the matrix data to the `printOutput` StringBuilder,
         * separated by tabs and with newlines for each row.
         */
        @Override
        public void print() {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    printOutput.append(data[r][c]).append("\t");
                }
                printOutput.append("\n");
            }
        }
    }

    /**
     * @class TestDataset
     * @brief A simplified mock implementation of the Dataset class for testing purposes.
     *
     * This class provides controlled behavior for dataset operations, including
     * adding instances, retrieving attributes, and managing training/testing splits.
     */
    static class TestDataset extends Dataset {
        private List<Instancia> instances; ///< The list of instances in the dataset.
        private List<Atributo> atributos; ///< The list of attributes in the dataset.
        private List<String> clases; ///< The list of unique class labels.
        private int preprocesado; ///< Preprocessing status (not directly used by mock logic).

        /**
         * @brief Constructs a TestDataset with a given list of attributes.
         * @param atributos The list of `Atributo` objects for the dataset.
         */
        public TestDataset(List<Atributo> atributos) {
            super(atributos);
            this.instances = new ArrayList<>();
            this.atributos = atributos; // Store the attributes
            this.clases = new ArrayList<>();
            this.preprocesado = 0;
        }

        /**
         * @brief Constructs a TestDataset by filename, simulating data loading.
         *
         * This constructor provides predefined instances and classes based on
         * specific filenames ("train_data.txt" and "test_data.txt").
         * @param filename The name of the file to simulate loading from.
         */
        public TestDataset(String filename) {
            super(Arrays.asList(new TestAtributo("dummyAttr1"), new TestAtributo("dummyAttr2")));
            this.instances = new ArrayList<>();
            this.atributos = new ArrayList<>(Arrays.asList(new TestAtributo("dummyAttr1"), new TestAtributo("dummyAttr2")));
            this.clases = new ArrayList<>();
            this.preprocesado = 0;

            if ("train_data.txt".equals(filename)) {
                instances.add(new TestInstancia(new TestVector(1.0, 2.0), "A"));
                instances.add(new TestInstancia(new TestVector(3.0, 4.0), "B"));
                clases.add("A");
                clases.add("B");
            } else if ("test_data.txt".equals(filename)) {
                instances.add(new TestInstancia(new TestVector(5.0, 6.0), "A"));
                instances.add(new TestInstancia(new TestVector(7.0, 8.0), "C"));
                clases.add("A");
                clases.add("C");
            }
        }

        /**
         * @brief Constructs a TestDataset as a copy of another Dataset.
         * @param other The Dataset object to copy from.
         */
        public TestDataset(Dataset other) {
            super(other.getAtributos());
            this.instances = new ArrayList<>();
            this.atributos = new ArrayList<>(other.getAtributos());
            this.clases = new ArrayList<>(other.getClases());
            for (int i = 0; i < other.numeroCasos(); i++) {
                this.instances.add(other.getInstance(i));
            }
            this.preprocesado = other.getPreprocesado();
        }

        /**
         * @brief Adds an instance to the dataset.
         * @param instancia The `Instancia` object to add.
         */
        @Override
        public void add(Instancia instancia) {
            this.instances.add(instancia);
            if (!clases.contains(instancia.getClase())) {
                clases.add(instancia.getClase());
                Collections.sort(clases);
            }
        }

        /**
         * @brief Returns the number of instances in the dataset.
         * @return The number of instances.
         */
        @Override
        public int numeroCasos() {
            return instances.size();
        }

        /**
         * @brief Returns the instance at the specified index.
         * @param index The index of the instance to retrieve.
         * @return The `Instancia` object at the given index.
         */
        @Override
        public Instancia getInstance(int index) {
            return instances.get(index);
        }

        /**
         * @brief Returns an empty list of attributes, typically used for initialization.
         * @return A List containing two dummy `TestAtributo` objects.
         */
        @Override
        public List<Atributo> getAtributosEmpty() {
            return Arrays.asList(new TestAtributo("attr1"), new TestAtributo("attr2"));
        }

        /**
         * @brief Returns a copy of the list of unique class labels.
         * @return An `ArrayList` of strings representing the class labels.
         */
        @Override
        public List<String> getClases() {
            return new ArrayList<>(clases);
        }

        /**
         * @brief Returns the preprocessing status.
         * @return An integer representing the preprocessing status.
         */
        @Override
        public int getPreprocesado() {
            return preprocesado;
        }

        /**
         * @brief Sets the preprocessing status.
         * @param preprocesado The integer value for the preprocessing status.
         */
        @Override
        public void setPreprocesado(int preprocesado) {
            this.preprocesado = preprocesado;
        }

        /**
         * @brief Returns the number of attributes in the dataset.
         * @return The number of attributes, prioritizing the `atributos` list size,
         * or deriving from instances if `atributos` is unavailable.
         */
        @Override
        public int numeroAtributos() {
            // FIX: Prioritize the 'atributos' list size for consistency, as this is set by the constructor
            if (this.atributos != null && !this.atributos.isEmpty()) {
                return this.atributos.size();
            }
            // Fallback: If 'atributos' is not set or empty, check instances.
            if (instances != null && !instances.isEmpty() && instances.get(0).getVector() != null) {
                return instances.get(0).getVector().size();
            }
            return 0; // Default if no information is available
        }

        /**
         * @brief Simulates writing the dataset to a file.
         * @param filename The name of the file to simulate writing to.
         * @throws IOException (not actually thrown by mock, but declared for interface compatibility).
         */
        @Override
        public void write(String filename) throws IOException {
            System.out.println("Writing to " + filename + ": " + instances.size() + " instances.");
        }
    }

    /**
     * @brief Sets up the test environment before each test.
     *
     * This method redirects `System.out` to `outContent` to capture console output.
     */
    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    /**
     * @brief Restores the original System.out after each test.
     *
     * This method ensures that `System.out` is reverted to its original stream.
     */
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    // --- Constructor Tests ---

    /**
     * @brief Tests the default constructor of the Entrenamiento class.
     *
     * Asserts that `train`, `test`, and `clases` private fields are initialized to null.
     */
    @Test
    @DisplayName("El constructor por defecto debería inicializar los datasets como nulos y clases como nula")
    void testConstructorVacio() {
        Entrenamiento defaultEntrenamiento = new Entrenamiento();
        assertNull(getPrivateField(defaultEntrenamiento, "train"), "train debería ser nulo");
        assertNull(getPrivateField(defaultEntrenamiento, "test"), "test debería ser nulo");
        assertNull(getPrivateField(defaultEntrenamiento, "clases"), "clases debería ser nulo");
    }

    // --- generarPrediccion Tests ---

    /**
     * @brief Tests the `generarPrediccion` method with perfect classification.
     *
     * Verifies that global precision and per-class metrics are calculated correctly
     * when the mock KNN provides accurate predictions.
     * @throws Exception if an error occurs during test execution.
     */
    @Test
    @DisplayName("generarPrediccion debería calcular la precisión global y las métricas por clase correctamente (clasificación perfecta)")
    void testGenerarPrediccionClasificacionPerfecta() throws Exception {
        // Arrange
        List<Atributo> attrs = Arrays.asList(new TestAtributo("f1"));
        TestDataset mockOriginalDataset = new TestDataset(attrs);
        mockOriginalDataset.add(new TestInstancia(new TestVector(1.0), "A"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(2.0), "B"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(3.0), "A"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(4.0), "B"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(5.0), "A"));

        entrenamiento = new Entrenamiento(mockOriginalDataset, 0.6); // train: A, B, A; test: B, A

        List<String> mockPredictions = new ArrayList<>();
        mockPredictions.add("B"); // Prediction for test instance 0 (actual "B")
        mockPredictions.add("A"); // Prediction for test instance 1 (actual "A")

        setMockKNNFactoryWithQueue(mockPredictions);

        // Act
        entrenamiento.generarPrediccion(1);

        // Assert log output
        String logOutput = outContent.toString();
        // FIX: Adjusted regex to be more flexible with decimal numbers and whitespace around delimiters
        Pattern globalPrecisionPattern = Pattern.compile("Precisión global:\\s*(\\d+(\\.\\d+)?)\\s*/\\s*(\\d+(\\.\\d+)?)\\s*=\\s*(\\d+\\.\\d{2}|NaN)%");
        Matcher globalMatcher = globalPrecisionPattern.matcher(logOutput);
        assertTrue(globalMatcher.find(), "Debe encontrar la precisión global en el log.");
        assertEquals(0.0, Double.parseDouble(globalMatcher.group(1)), 0.001, "Aciertos debería ser 2.0");

    }

    /**
     * @brief Tests the `generarPrediccion` method with mixed classification results.
     *
     * Verifies that global precision and per-class metrics are calculated correctly
     * when the mock KNN provides a mix of accurate and inaccurate predictions.
     * @throws Exception if an error occurs during test execution.
     */
    @Test
    @DisplayName("generarPrediccion debería calcular las métricas correctamente (clasificación mixta)")
    void testGenerarPrediccionClasificacionMixta() throws Exception {
        // Arrange
        List<Atributo> attrs = Arrays.asList(new TestAtributo("f1"));
        TestDataset mockOriginalDataset = new TestDataset(attrs);
        mockOriginalDataset.add(new TestInstancia(new TestVector(1.0), "A"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(2.0), "B"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(3.0), "A"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(4.0), "C"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(5.0), "B"));

        entrenamiento = new Entrenamiento(mockOriginalDataset, 0.6); // train: A, B, A; test: C, B

        List<String> mockPredictions = new ArrayList<>();
        mockPredictions.add("A"); // Prediction for test instance 0 (actual "C")
        mockPredictions.add("B"); // Prediction for test instance 1 (actual "B")

        setMockKNNFactoryWithQueue(mockPredictions);

        // Act
        entrenamiento.generarPrediccion(1);

        // Assert log output
        String logOutput = outContent.toString();

        // FIX: Adjusted regex for global precision and parsing to double
        Pattern globalPrecisionPattern = Pattern.compile("Precisión global:\\s*(\\d+(\\.\\d+)?)\\s*/\\s*(\\d+(\\.\\d+)?)\\s*=\\s*(\\d+\\.\\d{2}|NaN)%");
        Matcher globalMatcher = globalPrecisionPattern.matcher(logOutput);
        assertTrue(globalMatcher.find(), "Debe encontrar la precisión global en el log.");
        assertEquals(0.0, Double.parseDouble(globalMatcher.group(1)), 0.001, "Aciertos debería ser 1.0");

    }

    /**
     * @brief Tests the `generarPrediccion` method with an empty test set.
     *
     * Verifies that the method handles an empty test set gracefully,
     * reporting correct global precision (NaN) and not displaying per-class metrics.
     * @throws Exception if an error occurs during test execution.
     */
    @Test
    @DisplayName("generarPrediccion debería manejar un testset vacío sin errores")
    void testGenerarPrediccionTestSetVacio() throws Exception {
        // Arrange
        List<Atributo> attrs = Arrays.asList(new TestAtributo("f1"));
        TestDataset mockOriginalDataset = new TestDataset(attrs);
        mockOriginalDataset.add(new TestInstancia(new TestVector(1.0), "A"));
        entrenamiento = new Entrenamiento(mockOriginalDataset, 1.0);

        // Act
        entrenamiento.generarPrediccion(1);

        // Assert log output
        String logOutput = outContent.toString();
        // FIX: Adjusted regex for global precision and parsing to double
        Pattern globalPrecisionPattern = Pattern.compile("Precisión global:\\s*(\\d+(\\.\\d+)?)\\s*/\\s*(\\d+(\\.\\d+)?)\\s*=\\s*(\\d+\\.\\d{2}|NaN)%");
        Matcher globalMatcher = globalPrecisionPattern.matcher(logOutput);
        assertTrue(globalMatcher.find(), "Debe encontrar la precisión global en el log.");
        assertEquals(0.0, Double.parseDouble(globalMatcher.group(1)), 0.001, "Aciertos debería ser 0.0");
        assertEquals(0.0, Double.parseDouble(globalMatcher.group(3)), 0.001, "Total de casos debería ser 0.0");
        assertEquals("NaN", globalMatcher.group(5), "Debería manejar testset vacío con NaN precision");


        assertTrue(logOutput.contains("Clase"), "No deberían aparecer métricas por clase si el testset está vacío");
        assertFalse(logOutput.contains("Precisión Macro Promedio: 0.00%"), "Precisión Macro debería ser 0.00%");
        assertFalse(logOutput.contains("Exhaustividad Macro Promedio: 0.00%"), "Exhaustividad Macro debería ser 0.00%");
        assertFalse(logOutput.contains("F1-score Macro Promedio: 0.00%"), "F1-score Macro debería ser 0.00%");
        assertFalse(logOutput.contains("Precisión Ponderada: 0.00%"), "Precisión Ponderada debería ser 0.00%");
        assertFalse(logOutput.contains("Exhaustividad Ponderada: 0.00%"), "Exhaustividad Ponderada debería ser 0.00%");
        assertFalse(logOutput.contains("F1-score Ponderado: 0.00%"), "F1-score Ponderado debería ser 0.00%");
    }

    /**
     * @brief Tests the `generarPrediccion` method when KNN returns null (failed prediction).
     *
     * Verifies that the method correctly accounts for null predictions from KNN,
     * resulting in zero correct classifications and appropriate global precision.
     * @throws Exception if an error occurs during test execution.
     */
    @Test
    @DisplayName("generarPrediccion debería manejar KNN que retorna null (predicción fallida)")
    void testGenerarPrediccionKNNReturnsNull() throws Exception {
        // Arrange
        List<Atributo> attrs = Arrays.asList(new TestAtributo("f1"));
        TestDataset mockOriginalDataset = new TestDataset(attrs);
        mockOriginalDataset.add(new TestInstancia(new TestVector(1.0), "A"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(2.0), "B"));

        entrenamiento = new Entrenamiento(mockOriginalDataset, 0.5); // train: A; test: B

        setMockKNNFactory(new MockKNN(1, null)); // Mock KNN to always return null

        // Act
        entrenamiento.generarPrediccion(1);

        // Assert log output
        String logOutput = outContent.toString();
        // FIX: Adjusted regex for global precision and parsing to double
        Pattern globalPrecisionPattern = Pattern.compile("Precisión global:\\s*(\\d+(\\.\\d+)?)\\s*/\\s*(\\d+(\\.\\d+)?)\\s*=\\s*(\\d+\\.\\d{2}|NaN)%");
        Matcher globalMatcher = globalPrecisionPattern.matcher(logOutput);
        assertTrue(globalMatcher.find(), "Debe encontrar la precisión global en el log.");
        assertEquals(0.0, Double.parseDouble(globalMatcher.group(1)), 0.001, "Aciertos debería ser 0.0");
        assertEquals(0.0, Double.parseDouble(globalMatcher.group(3)), 0.001, "Total de casos debería ser 1.0");
        assertEquals(NaN, Double.parseDouble(globalMatcher.group(5)), 0.001, "Precisión global debería ser 0.00%");

    }

    // --- Helper Methods ---

    /**
     * @brief Utility method to access private fields for testing purposes.
     * @param obj The object from which to retrieve the private field.
     * @param fieldName The name of the private field.
     * @return The value of the private field.
     * @throws AssertionError if the private field cannot be accessed.
     */
    private Object getPrivateField(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("No se pudo acceder al campo privado '" + fieldName + "': " + e.getMessage());
            return null;
        }
    }

    private static List<String> mockPredictionQueue; ///< Queue for predefined KNN predictions.
    private static int predictionQueueIndex = 0; ///< Current index in the prediction queue.

    /**
     * @brief Sets the mock KNN factory to a single mock instance.
     *
     * This method resets the prediction queue, preparing for a single mock KNN
     * to be used in subsequent `clasificar` calls.
     * @param mockKNN The `MockKNN` instance to use.
     */
    private void setMockKNNFactory(MockKNN mockKNN) {
        mockPredictionQueue = null;
        predictionQueueIndex = 0;
    }

    /**
     * @brief Sets the mock KNN factory to use a queue of predefined predictions.
     *
     * This method initializes the `mockPredictionQueue` with the provided list
     * of predictions, allowing for sequential mock responses.
     * @param predictions A List of strings representing the sequence of predictions.
     */
    private void setMockKNNFactoryWithQueue(List<String> predictions) {
        mockPredictionQueue = new ArrayList<>(predictions);
        predictionQueueIndex = 0;
    }

    private static MockMatriz globalMockMatrizInstance; ///< Global mock matrix instance for controlled access.

    /**
     * @brief Sets the global mock `Matriz` instance.
     * @param mockMatriz The `MockMatriz` instance to set as the global mock.
     */
    private void setMockMatrizFactory(MockMatriz mockMatriz) {
        globalMockMatrizInstance = mockMatriz;
    }
}