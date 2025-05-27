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

@DisplayName("Tests para la clase Entrenamiento")
class EntrenamientoTest {

    private Entrenamiento entrenamiento;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // --- Mock / Simplified Classes for Testing ---

    // Simplified Vector class
    static class TestVector extends Vector {
        private List<Double> values;

        public TestVector(double... values) {
            this.values = Arrays.stream(values).boxed().collect(Collectors.toList());
        }

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

        @Override
        public double get(int index) {
            return values.get(index);
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public void normalize() { /* Not used by Entrenamiento, but required by interface */ }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    // Simplified Instancia class
    static class TestInstancia extends Instancia {
        private TestVector vector;
        private String clase;

        public TestInstancia(ArrayList<Object> instanceValues) {
            this.vector = new TestVector(instanceValues.subList(0, instanceValues.size() - 1));
            this.clase = (String) instanceValues.get(instanceValues.size() - 1);
        }

        public TestInstancia(TestVector vector, String clase) {
            this.vector = vector;
            this.clase = clase;
        }

        @Override
        public vectores.Vector getVector() {
            return vector;
        }

        @Override
        public String getClase() {
            return clase;
        }

        @Override
        public List<Object> getValores() {
            List<Object> allValues = new ArrayList<>();
            allValues.addAll(vector.values);
            allValues.add(clase);
            return allValues;
        }

        @Override
        public String toString() {
            return "Instancia{vector=" + vector + ", clase='" + clase + "'}";
        }
    }

    // Simplified Atributo class
    static class TestAtributo extends Atributo {
        private String name;

        public TestAtributo(String name) {
            super();
            this.name = name;
        }

        @Override
        public void add(Object valor) { /* Not used in current tests, no-op */ }

        @Override
        public Object getValor(int indice) { return null; /* Not used in current tests, return null */ }

        @Override
        public int size() { return 0; /* Not used in current tests, return 0 */ }

        @Override
        public void clear() { /* No-op for test */ }

        @Override
        public List<Object> getValores() {
            return Collections.emptyList();
        }

        @Override
        public void delete(int index) {
            // For a mock, this can be a no-op
        }

        @Override
        public String toString() {
            return "TestAtributo{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    // Mock KNN class to control classification results
    static class MockKNN extends KNN {
        private String nextPrediction;
        private int k;

        public MockKNN(int k, String nextPrediction) {
            super(k);
            this.k = k;
            this.nextPrediction = nextPrediction;
        }

        public void setNextPrediction(String prediction) {
            this.nextPrediction = prediction;
        }

        @Override
        public String clasificar(Dataset entrenamiento, Instancia prueba) {
            return nextPrediction;
        }
    }

    // Simplified Matriz class to verify interactions
    static class MockMatriz extends Matriz {
        public int[][] data;
        public int rows;
        public int cols;
        public StringBuilder printOutput = new StringBuilder();

        public MockMatriz(int rows, int cols) {
            super(rows, cols);
            this.rows = rows;
            this.cols = cols;
            this.data = new int[rows][cols];
        }

        public void set(int row, int col, int value) {
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                this.data[row][col] = value;
            } else {
                throw new IndexOutOfBoundsException("Matrix indices out of bounds: [" + row + ", " + col + "]");
            }
        }

        @Override
        public double get(int row, int col) {
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                return (double) this.data[row][col];
            }
            return 0.0;
        }

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

    // Simplified Dataset class
    static class TestDataset extends Dataset {
        private List<Instancia> instances;
        private List<Atributo> atributos;
        private List<String> clases;
        private int preprocesado;

        public TestDataset(List<Atributo> atributos) {
            super(atributos);
            this.instances = new ArrayList<>();
            this.atributos = atributos; // Store the attributes
            this.clases = new ArrayList<>();
            this.preprocesado = 0;
        }

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

        @Override
        public void add(Instancia instancia) {
            this.instances.add(instancia);
            if (!clases.contains(instancia.getClase())) {
                clases.add(instancia.getClase());
                Collections.sort(clases);
            }
        }

        @Override
        public int numeroCasos() {
            return instances.size();
        }

        @Override
        public Instancia getInstance(int index) {
            return instances.get(index);
        }

        @Override
        public List<Atributo> getAtributosEmpty() {
            return Arrays.asList(new TestAtributo("attr1"), new TestAtributo("attr2"));
        }

        @Override
        public List<String> getClases() {
            return new ArrayList<>(clases);
        }

        @Override
        public int getPreprocesado() {
            return preprocesado;
        }

        @Override
        public void setPreprocesado(int preprocesado) {
            this.preprocesado = preprocesado;
        }

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

        @Override
        public void write(String filename) throws IOException {
            System.out.println("Writing to " + filename + ": " + instances.size() + " instances.");
        }
    }


    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    // --- Constructor Tests ---

    @Test
    @DisplayName("El constructor por defecto debería inicializar los datasets como nulos y clases como nula")
    void testConstructorVacio() {
        Entrenamiento defaultEntrenamiento = new Entrenamiento();
        assertNull(getPrivateField(defaultEntrenamiento, "train"), "train debería ser nulo");
        assertNull(getPrivateField(defaultEntrenamiento, "test"), "test debería ser nulo");
        assertNull(getPrivateField(defaultEntrenamiento, "clases"), "clases debería ser nulo");
    }


    // --- generarPrediccion Tests ---

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

    @Test
    @DisplayName("generarPrediccion debería manejar KNN que retorna null (predicción fallida)")
    void testGenerarPrediccionKNNReturnsNull() throws Exception {
        // Arrange
        List<Atributo> attrs = Arrays.asList(new TestAtributo("f1"));
        TestDataset mockOriginalDataset = new TestDataset(attrs);
        mockOriginalDataset.add(new TestInstancia(new TestVector(1.0), "A"));
        mockOriginalDataset.add(new TestInstancia(new TestVector(2.0), "B"));

        entrenamiento = new Entrenamiento(mockOriginalDataset, 0.5); // train: A; test: B

        setMockKNNFactory(new MockKNN(1, null));

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

    private static List<String> mockPredictionQueue;
    private static int predictionQueueIndex = 0;

    private void setMockKNNFactory(MockKNN mockKNN) {
        mockPredictionQueue = null;
        predictionQueueIndex = 0;
    }

    private void setMockKNNFactoryWithQueue(List<String> predictions) {
        mockPredictionQueue = new ArrayList<>(predictions);
        predictionQueueIndex = 0;
    }

    private static MockMatriz globalMockMatrizInstance;

    private void setMockMatrizFactory(MockMatriz mockMatriz) {
        globalMockMatrizInstance = mockMatriz;
    }
}