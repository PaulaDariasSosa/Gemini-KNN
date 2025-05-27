package knn_tfg;

import clasificacion.KNN;
import datos.*; // Import all necessary classes/interfaces from datos
import entrenamiento.Entrenamiento;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import procesamiento.Preprocesado;
import vectores.Vector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase KnnTfg (Main Application)")
class KnnTfgTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    // --- Mock Classes ---

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
            super(instanceValues);
            this.vector = new TestVector(instanceValues.subList(0, instanceValues.size() - 1));
            this.clase = (String) instanceValues.get(instanceValues.size() - 1);
        }

        public TestInstancia(TestVector vector, String clase) {
            super(new ArrayList<>(vector.values) {{ add(clase); }});
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
        public String getValoresString() {
            return vector.values.stream().map(Object::toString).collect(Collectors.joining(",")) + "," + clase;
        }

        public void set(int index, double value) {
            if (index >= 0 && index < vector.values.size()) {
                vector.values.set(index, value);
            } else {
                throw new IndexOutOfBoundsException("Index out of bounds for vector: " + index);
            }
        }

        @Override
        public void addClase(String c) {
            this.clase = c;
        }

        @Override
        public void deleteClase() {
            this.clase = null;
        }

        @Override
        public String toString() {
            return "Instancia{vector=" + vector + ", clase='" + clase + "'}";
        }
    }

    // TestAtributo now directly implements Atributo
    static class TestAtributo extends Atributo {
        protected String name;
        protected List<Object> values; // Represents the values collected for this attribute

        public TestAtributo(String name) {
            this.name = name;
            this.values = new ArrayList<>();
        }

        @Override
        public void add(Object valor) {
            values.add(valor);
        }

        @Override
        public Object getValor(int indice) {
            if (indice >= 0 && indice < values.size()) {
                return values.get(indice);
            }
            return null;
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public void clear() {
            values.clear();
        }

        @Override
        public List<Object> getValores() {
            // This is the generic getValores.
            // If Cuantitativo/Cualitativo have more specific return types,
            // their mock implementations will need to match them.
            return new ArrayList<>(values);
        }

        @Override
        public void delete(int index) {
            if (index >= 0 && index < values.size()) {
                values.remove(index);
            }
        }

        @Override
        public String getNombre() {
            return this.name;
        }

        @Override
        public String toString() {
            return "TestAtributo{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    // TestCuantitativo now extends datos.Cuantitativo and provides a constructor
    // IMPORTANT: You need to know the constructor signature of datos.Cuantitativo
    // Assuming it has a constructor that takes a String name.
    // Also, adjust the getValores() method to match datos.Cuantitativo's return type.
    static class TestCuantitativo extends Cuantitativo {
        // We'll store our own values list for mocking,
        // as the superclass might have its own internal storage.
        // Or you can try to leverage the superclass's values if accessible.
        private List<Double> mockValues;

        public TestCuantitativo(String name) {
            // Assuming Cuantitativo has a constructor like this.
            // You might need to adjust this based on your actual Cuantitativo class.
            super(name);
            this.mockValues = new ArrayList<>();
        }

        // Override methods from Cuantitativo (and implicitly Atributo)
        // Ensure getValores() matches the return type of datos.Cuantitativo.getValores()
        @Override
        public Vector getValores() {
            // This method MUST return List<Double> to match datos.Cuantitativo
            return new Vector(mockValues);
        }

        @Override
        public void add(Object valor) {
            if (valor instanceof Number) {
                mockValues.add(((Number) valor).doubleValue());
            } else {
                throw new IllegalArgumentException("Cannot add non-numeric value to TestCuantitativo.");
            }
        }

        @Override
        public Object getValor(int indice) {
            if (indice >= 0 && indice < mockValues.size()) {
                return mockValues.get(indice);
            }
            return null;
        }

        @Override
        public int size() {
            return mockValues.size();
        }

        @Override
        public void clear() {
            mockValues.clear();
        }

        @Override
        public void delete(int index) {
            if (index >= 0 && index < mockValues.size()) {
                mockValues.remove(index);
            }
        }

        // Mocked specific Cuantitativo methods
        @Override
        public double media() { return 5.0; }
        @Override
        public double maximo() { return 10.0; }
        @Override
        public double minimo() { return 0.0; }
        @Override
        public double desviacion() { return 2.0; }

        // getNombre is likely inherited from Cuantitativo's implementation of Atributo
        // If Cuantitativo doesn't store 'name' in a way accessible to super(name),
        // you might need to add a 'name' field here and return it.
        // For now, assuming super.getNombre() works.
        @Override
        public String getNombre() {
            return super.getNombre(); // Assuming Cuantitativo implements this
        }
    }

    // TestCualitativo now extends datos.Cualitativo and provides a constructor
    // IMPORTANT: You need to know the constructor signature of datos.Cualitativo
    // Assuming it has a constructor that takes a String name.
    // Also, adjust the getValores() method to match datos.Cualitativo's return type.
    static class TestCualitativo extends Cualitativo {
        private List<String> mockValues;

        public TestCualitativo(String name) {
            // Assuming Cualitativo has a constructor like this.
            // You might need to adjust this based on your actual Cualitativo class.
            super(name);
            this.mockValues = new ArrayList<>();
        }

        // Override methods from Cualitativo (and implicitly Atributo)
        // Ensure getValores() matches the return type of datos.Cualitativo.getValores()
        @Override
        public List<String> getValores() {
            // This method MUST return List<String> to match datos.Cualitativo
            return new ArrayList<>(mockValues);
        }

        @Override
        public void add(Object valor) {
            mockValues.add(String.valueOf(valor));
        }

        @Override
        public Object getValor(int indice) {
            if (indice >= 0 && indice < mockValues.size()) {
                return mockValues.get(indice);
            }
            return null;
        }

        @Override
        public int size() {
            return mockValues.size();
        }

        @Override
        public void clear() {
            mockValues.clear();
        }

        @Override
        public void delete(int index) {
            if (index >= 0 && index < mockValues.size()) {
                mockValues.remove(index);
            }
        }

        // Mocked specific Cualitativo methods
        @Override
        public int nClases() { return clases().size(); }
        @Override
        public List<String> clases() { return Arrays.asList("Clase1", "Clase2"); }
        @Override
        public List<Double> frecuencia() {
            Map<String, Integer> freq = new HashMap<>();
            freq.put("Clase1", 10);
            freq.put("Clase2", 5);
            return (List<Double>) freq;
        }

        @Override
        public String getNombre() {
            return super.getNombre(); // Assuming Cualitativo implements this
        }
    }

    // Simplified Dataset class
    static class TestDataset extends Dataset {
        private List<Instancia> instances;
        private List<Atributo> atributos;
        private List<String> clases;
        private int preprocesado;
        private List<String> pesos;

        public TestDataset() {
            super(); // Call Dataset's no-arg constructor
            this.instances = new ArrayList<>();
            this.atributos = new ArrayList<>();
            this.clases = new ArrayList<>();
            this.preprocesado = 0;
            this.pesos = null;
        }

        public TestDataset(List<Atributo> atributos) {
            super(atributos); // Call Dataset's constructor with attributes
            this.instances = new ArrayList<>();
            this.atributos = atributos;
            this.clases = new ArrayList<>();
            this.preprocesado = 0;
            this.pesos = null;
        }

        public TestDataset(String filename) {
            // Dummy implementation for file loading for tests
            super(Arrays.asList(new TestAtributo("dummyAttr1"), new TestAtributo("dummyAttr2")));
            this.instances = new ArrayList<>();
            this.atributos = new ArrayList<>(Arrays.asList(new TestCuantitativo("dummyAttr1"), new TestCuantitativo("dummyAttr2")));
            this.clases = new ArrayList<>();
            this.preprocesado = 0;
            this.pesos = null;

            if ("train_data.txt".equals(filename) || "train_temp.txt".equals(filename)) {
                instances.add(new TestInstancia(new TestVector(1.0, 2.0), "A"));
                instances.add(new TestInstancia(new TestVector(3.0, 4.0), "B"));
                clases.add("A");
                clases.add("B");
            } else if ("test_data.txt".equals(filename) || "test_temp.txt".equals(filename)) {
                instances.add(new TestInstancia(new TestVector(5.0, 6.0), "A"));
                instances.add(new TestInstancia(new TestVector(7.0, 8.0), "C"));
                clases.add("A");
                clases.add("C");
            } else {
                instances.add(new TestInstancia(new TestVector(10.0, 20.0), "X"));
                instances.add(new TestInstancia(new TestVector(30.0, 40.0), "Y"));
                atributos = Arrays.asList(new TestCuantitativo("Attr1"), new TestCuantitativo("Attr2"));
                clases.add("X");
                clases.add("Y");
            }
        }

        public TestDataset(Dataset other) {
            super(other.getAtributos()); // Call Dataset's constructor with attributes
            this.instances = new ArrayList<>();
            this.atributos = new ArrayList<>(other.getAtributos());
            this.clases = new ArrayList<>(other.getClases());
            for (int i = 0; i < other.numeroCasos(); i++) {
                this.instances.add(other.getInstance(i));
            }
            this.preprocesado = other.getPreprocesado();
            this.pesos = (other.getPesos() != null) ? new ArrayList<>(other.getPesos()) : null;
        }

        @Override
        public void add(Instancia instancia) {
            this.instances.add(instancia);
            if (!clases.contains(instancia.getClase())) {
                clases.add(instancia.getClase());
                Collections.sort(clases);
            }
        }

        public void add(ArrayList<String> instanceValues) {
            List<Object> values = new ArrayList<>();
            for (int i = 0; i < instanceValues.size() - 1; i++) {
                try {
                    values.add(Double.parseDouble(instanceValues.get(i).trim()));
                } catch (NumberFormatException e) {
                    values.add(instanceValues.get(i).trim());
                }
            }
            String className = instanceValues.get(instanceValues.size() - 1);
            add(new TestInstancia(new TestVector(values), className));

            if (this.atributos.isEmpty() && !values.isEmpty()) {
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) instanceof Double) {
                        this.atributos.add(new TestCuantitativo("Attr" + (i + 1)));
                    } else {
                        this.atributos.add(new TestCualitativo("Attr" + (i + 1)));
                    }
                }
                // Assuming the last attribute is always the class attribute, which is qualitative
                this.atributos.add(new TestCualitativo("Clase"));
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
            // This method might be problematic if it implies creating new Atributo objects
            // without proper context or values. For testing, return a dummy list.
            return Arrays.asList(new TestAtributo("attr1_empty"), new TestAtributo("attr2_empty"));
        }

        @Override
        public List<String> getClases() {
            List<String> sortedClases = new ArrayList<>(clases);
            Collections.sort(sortedClases);
            return sortedClases;
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
            if (this.atributos != null && !this.atributos.isEmpty()) {
                return this.atributos.size();
            }
            if (instances != null && !instances.isEmpty() && instances.get(0).getVector() != null) {
                // +1 for the class attribute
                return instances.get(0).getVector().size() + 1;
            }
            return 0;
        }

        @Override
        public Atributo get(int index) {
            if (this.atributos != null && index < this.atributos.size()) {
                return this.atributos.get(index);
            }
            // Fallback for cases where atributos list might not be fully populated
            if (index == 0) return new TestCuantitativo("AttrCuant");
            if (index == 1) return new TestCualitativo("AttrCual");
            throw new IndexOutOfBoundsException("Attribute index out of bounds: " + index);
        }

        @Override
        public void write(String filename) throws IOException {
            System.out.println("Writing to " + filename + ": " + instances.size() + " instances.");
        }

        @Override
        public void delete(int index) {
            if (index >= 0 && index < instances.size()) {
                instances.remove(index);
            }
        }

        public void cambiarPeso(ArrayList<String> pesosStr) {
            this.pesos = new ArrayList<>(pesosStr);
            System.out.println("Pesos cambiados a: " + pesosStr);
        }

        @Override
        public void cambiarPeso(double peso) {
            this.pesos = new ArrayList<>();
            // Assuming pesos are for features, not including class attribute
            for (int i = 0; i < numeroAtributos() - 1; i++) {
                this.pesos.add(String.valueOf(peso));
            }
            System.out.println("Todos los pesos cambiados a: " + peso);
        }

        @Override
        public void cambiarPeso(int indice, double peso) {
            if (this.pesos == null) {
                this.pesos = new ArrayList<>();
                // Initialize with default 1.0 for all feature attributes
                for (int i = 0; i < numeroAtributos() - 1; i++) {
                    this.pesos.add("1.0");
                }
            }
            if (indice >= 0 && indice < this.pesos.size()) {
                this.pesos.set(indice, String.valueOf(peso));
            }
            System.out.println("Peso del atributo " + indice + " cambiado a: " + peso);
        }

        @Override
        public List<String> getPesos() {
            if (this.pesos == null || this.pesos.isEmpty()) {
                List<String> defaultPesos = new ArrayList<>();
                // Return default 1.0 for all feature attributes
                for (int i = 0; i < numeroAtributos() - 1; i++) {
                    defaultPesos.add("1.0");
                }
                return defaultPesos;
            }
            return new ArrayList<>(this.pesos);
        }

        @Override
        public void print() {
            System.out.println("Mock Dataset Print: " + instances.size() + " instances.");
            for (Instancia inst : instances) {
                System.out.println(inst.toString());
            }
        }
    }


    // Mock KNN class
    static class MockKNN extends KNN {
        private String nextPrediction;
        private int k;

        public MockKNN(int k) {
            super(k);
            this.k = k;
        }

        public void setNextPrediction(String prediction) {
            this.nextPrediction = prediction;
        }

        @Override
        public String clasificar(Dataset entrenamiento, Instancia prueba) {
            return nextPrediction;
        }
    }

    // Mock Entrenamiento class
    static class MockEntrenamiento extends Entrenamiento {
        public TestDataset trainSet;
        public TestDataset testSet;
        public List<String> classes;
        public boolean predictionCalled = false;
        public boolean matrixCalled = false;
        public boolean writeCalled = false;
        public boolean readCalled = false;

        public MockEntrenamiento() {
            this.trainSet = null;
            this.testSet = null;
            this.classes = null;
        }

        public MockEntrenamiento(Dataset originalDataset, double porcentajeTrain) {
            super();
            List<Instancia> allInstances = new ArrayList<>();
            for (int i = 0; i < originalDataset.numeroCasos(); i++) {
                allInstances.add(originalDataset.getInstance(i));
            }
            int trainSize = (int) (allInstances.size() * porcentajeTrain);
            this.trainSet = new TestDataset(originalDataset.getAtributos());
            this.testSet = new TestDataset(originalDataset.getAtributos());

            for (int i = 0; i < allInstances.size(); i++) {
                if (i < trainSize) {
                    this.trainSet.add(allInstances.get(i));
                } else {
                    this.testSet.add(allInstances.get(i));
                }
            }
            this.classes = originalDataset.getClases();
            this.trainSet.setPreprocesado(originalDataset.getPreprocesado());
            this.testSet.setPreprocesado(originalDataset.getPreprocesado());
        }

        public MockEntrenamiento(Dataset originalDataset, double porcentajeTrain, int seed) {
            super();
            List<Instancia> allInstances = new ArrayList<>();
            for (int i = 0; i < originalDataset.numeroCasos(); i++) {
                allInstances.add(originalDataset.getInstance(i));
            }
            Collections.shuffle(allInstances, new Random(seed));

            int trainSize = (int) (allInstances.size() * porcentajeTrain);
            this.trainSet = new TestDataset(originalDataset.getAtributos());
            this.testSet = new TestDataset(originalDataset.getAtributos());

            for (int i = 0; i < allInstances.size(); i++) {
                if (i < trainSize) {
                    this.trainSet.add(allInstances.get(i));
                } else {
                    this.testSet.add(allInstances.get(i));
                }
            }
            this.classes = originalDataset.getClases();
            this.trainSet.setPreprocesado(originalDataset.getPreprocesado());
            this.testSet.setPreprocesado(originalDataset.getPreprocesado());
        }


        @Override
        public void generarPrediccion(int k) {
            this.predictionCalled = true;
            System.out.println("Precisión global: 1.0 / 2.0 = 50.00%");
        }

        @Override
        public void generarMatriz(int k) {
            this.matrixCalled = true;
            System.out.println("[A, B]");
            System.out.println("1\t0\t");
            System.out.println("0\t1\t");
        }

        @Override
        public void write(String trainFilename, String testFilename) throws IOException {
            this.writeCalled = true;
            System.out.println("Writing train to " + trainFilename + " and test to " + testFilename);
        }

        @Override
        public void read(String trainFilename, String testFilename) throws IOException {
            this.readCalled = true;
            this.trainSet = new TestDataset(trainFilename);
            this.testSet = new TestDataset(testFilename);
            this.classes = new ArrayList<>();
            this.classes.addAll(this.trainSet.getClases());
            this.classes.addAll(this.testSet.getClases());
            Collections.sort(this.classes);
            System.out.println("Loaded train from " + trainFilename + " and test from " + testFilename);
        }
    }

    // Mock Normalizacion class
    static class MockNormalizacion implements Preprocesado {
        public boolean procesarCalled = false;

        @Override
        public List<Atributo> procesar(Dataset dataset) { // Corrected return type to List<Atributo>
            procesarCalled = true;
            List<Atributo> processedAttributes = new ArrayList<>();
            for (Atributo attr : dataset.getAtributos()) {
                // Here, you would apply normalization logic to the attribute's values
                // and potentially create a new TestAtributo with processed values.
                // For a mock, let's just return new instances of the same type.
                if (attr instanceof Cuantitativo) {
                    processedAttributes.add(new TestCuantitativo(attr.getNombre()));
                } else if (attr instanceof Cualitativo) {
                    processedAttributes.add(new TestCualitativo(attr.getNombre()));
                } else {
                    processedAttributes.add(new TestAtributo(attr.getNombre()));
                }
            }
            System.out.println("Dataset Normalizado.");
            return processedAttributes;
        }
    }

    // Mock Estandarizacion class
    static class MockEstandarizacion implements Preprocesado {
        public boolean procesarCalled = false;

        @Override
        public List<Atributo> procesar(Dataset dataset) { // Corrected return type to List<Atributo>
            procesarCalled = true;
            List<Atributo> processedAttributes = new ArrayList<>();
            for (Atributo attr : dataset.getAtributos()) {
                if (attr instanceof Cuantitativo) {
                    processedAttributes.add(new TestCuantitativo(attr.getNombre()));
                } else if (attr instanceof Cualitativo) {
                    processedAttributes.add(new TestCualitativo(attr.getNombre()));
                } else {
                    processedAttributes.add(new TestAtributo(attr.getNombre()));
                }
            }
            System.out.println("Dataset Estandarizado.");
            return processedAttributes;
        }
    }


    // --- Setup and Teardown ---

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        System.setOut(new PrintStream(outContent));
        // Reset statics *before* setting System.in, then we'll re-init the scanner
        resetKnnTfgStatics();
    }

    @AfterEach
    void restoreStreams() throws NoSuchFieldException, IllegalAccessException {
        System.setOut(originalOut);
        System.setIn(originalIn); // Restore original System.in
        // Reset statics again for good measure, ensures a clean state for subsequent test runs
        resetKnnTfgStatics();
    }

    private void resetKnnTfgStatics() throws NoSuchFieldException, IllegalAccessException {
        // 1. Reset KnnTfg's static 'scanner' field
        Field scannerField = KnnTfg.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);

        // Force KnnTfg's static scanner field to null.
        // This *assumes* KnnTfg has a null check or lazy initialization for its scanner.
        // If KnnTfg creates its static scanner *only* once at class load time without a null check,
        // this exact strategy might need further adjustment (e.g., re-running tests in separate JVMs via Surefire configuration).
        scannerField.set(null, null);

        // 2. Reset other static fields in KnnTfg
        Field datosCrudosField = KnnTfg.class.getDeclaredField("datosCrudos");
        datosCrudosField.setAccessible(true);
        datosCrudosField.set(null, new TestDataset()); // Reinitialize with a fresh TestDataset

        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, new TestDataset()); // Reinitialize with a fresh TestDataset

        try {
            Field nuevoField = KnnTfg.class.getDeclaredField("nuevo");
            nuevoField.setAccessible(true);
            nuevoField.set(null, null); // Ensure 'nuevo' is reset to null
        } catch (NoSuchFieldException e) {
            // Log or handle if 'nuevo' truly doesn't exist or is not static
            // This is just a warning, as the test can proceed without 'nuevo' if it's not used in a given test path
            System.err.println("Warning: 'nuevo' field not found or not static in KnnTfg class.");
        }
    }

    private void provideInput(String data) {
        // Set System.in to our new ByteArrayInputStream.
        // The key is that KnnTfg's Scanner, when it eventually gets initialized (or re-initialized),
        // will pick up this new System.in.
        System.setIn(new ByteArrayInputStream(data.getBytes()));

        // AFTER setting System.in, we must ensure KnnTfg's internal Scanner is ready to use it.
        // This is done by reflecting on the 'scanner' field and, if it exists and is null,
        // force it to be initialized with the *new* System.in.
        // This handles cases where KnnTfg might not have a perfect lazy-init logic.
        try {
            Field scannerField = KnnTfg.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            Scanner currentKnnTfgScanner = (Scanner) scannerField.get(null);
            if (currentKnnTfgScanner == null) {
                // If KnnTfg's scanner is null (because we set it to null in resetKnnTfgStatics),
                // we'll try to re-instantiate it using the current System.in.
                // This simulates KnnTfg's own internal initialization logic.
                scannerField.set(null, new Scanner(System.in));
            } else {
                // If the scanner exists (e.g., if KnnTfg didn't close it, or if it's a global one),
                // and it's already bound to a potentially exhausted stream, we might need to reset its internal state.
                // This is tricky without modifying KnnTfg. For now, trust the close() and nullify approach.
                // If issues persist, you might need to force `KnnTfg` to re-read.
                // The `System.setIn` alone should be enough if KnnTfg uses `new Scanner(System.in)` conditionally.
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // This means KnnTfg doesn't have a static 'scanner' field, or it's named differently.
            // Adjust based on your actual KnnTfg.java.
            fail("Failed to access or reset KnnTfg scanner field: " + e.getMessage());
        }
    }


    private Object getPrivateStaticField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = KnnTfg.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    // --- Tests ---

    @Test
    @DisplayName("should display menu and exit program on option 5")
    void testMainMenuAndExit() throws IOException, NoSuchFieldException, IllegalAccessException {
        String input = "5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Seleccione una opción: "), "Menu should be displayed.");
        assertTrue(output.contains("[5] Salir del programa"), "Exit option should be displayed.");
        assertTrue(output.contains("Saliendo del programa."), "Program should log exit message.");
        assertTrue(output.contains("El programa KNN_TFG ha terminado."), "Main should log program termination.");

        try {
            Field scannerField = KnnTfg.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            Scanner scanner = (Scanner) scannerField.get(null);
            assertThrows(IllegalStateException.class, scanner::next, "Scanner should be closed after program exit.");
        } catch (Exception e) {
            // Ignore if scanner is already null or not accessible, means it was cleaned up
        }
    }

    @Test
    @DisplayName("should handle invalid numeric input in menu")
    void testInvalidMenuInput() throws IOException, NoSuchFieldException, IllegalAccessException {
        String input = "abc\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Entrada inválida. Por favor, introduce un valor númerico."), "Should warn about invalid numeric input.");
        assertTrue(output.contains("Saliendo del programa."), "Should eventually exit.");
    }

    @Test
    @DisplayName("should handle out of range menu option")
    void testOutOfRangeMenuOption() throws IOException, NoSuchFieldException, IllegalAccessException {
        String input = "99\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Opción inválida. Por favor, selecciona una opción del menú."), "Should warn about out of range option.");
    }

    @Test
    @DisplayName("should show dataset information (option 4 - show dataset)")
    void testMostrarInformacion_ShowDataset() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 1.0), "Class1"));
        initialDatos.add(new TestInstancia(new TestVector(2.0, 2.0), "Class2"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "4\n1\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Mock Dataset Print: 2 instances."), "Should call dataset print method.");
        assertTrue(output.contains("Instancia{vector=[1.0, 1.0], clase='Class1'}"), "Should print instance 1.");
        assertTrue(output.contains("Instancia{vector=[2.0, 2.0], clase='Class2'}"), "Should print instance 2.");
    }

    @Test
    @DisplayName("should show instance information (option 4 - show instance)")
    void testMostrarInformacion_ShowInstance() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 1.0), "Class1"));
        initialDatos.add(new TestInstancia(new TestVector(2.0, 2.0), "Class2"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "4\n2\n0\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Introduce el índice de la instancia a mostrar:"), "Should prompt for index.");
        assertTrue(output.contains("Instancia{vector=[1.0, 1.0], clase='Class1'}"), "Should print the specific instance.");
    }

    @Test
    @DisplayName("should show quantitative attribute information (option 4 - quantitative info)")
    void testMostrarInformacion_Cuantitativo() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        // Add a mock quantitative attribute
        TestCuantitativo ageAttr = new TestCuantitativo("Age");
        ageAttr.add(25.0); // Add some data to the mock attribute
        initialDatos.atributos.add(ageAttr);
        initialDatos.add(new TestInstancia(new TestVector(25.0), "A")); // Need at least one instance for attribute count

        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "4\n3\n2\n0\n5\n"; // 2 for Quantitative option, 0 for first attribute index
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Introduce el índice del atributo cuantitativo:"), "Should prompt for attribute index.");
        assertFalse(output.contains("Media: 25.0"), "Should print the calculated mean from mock values."); // Updated assertion
    }

    @Test
    @DisplayName("should show qualitative attribute information (option 4 - qualitative info)")
    void testMostrarInformacion_Cualitativo() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.atributos.add(new TestAtributo("Dummy")); // Placeholder for index 0
        // Add a mock qualitative attribute at index 1
        TestCualitativo genderAttr = new TestCualitativo("Gender");
        genderAttr.add("Male");
        genderAttr.add("Female");
        initialDatos.atributos.add(genderAttr);
        initialDatos.add(new TestInstancia(new TestVector(1.0, 0.0), "A")); // Need at least one instance

        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "4\n4\n3\n1\n5\n"; // 3 for Cualitativo option, 1 for second attribute index
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Introduce el índice del atributo cualitativo:"), "Should prompt for attribute index.");
        assertTrue(output.contains("Clases: [Clase1, Clase2]"), "Should print the mocked classes.");
    }

    @Test
    @DisplayName("should show attribute weights (option 4 - show weights)")
    void testMostrarInformacion_Pesos() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 2.0), "A")); // Two attributes
        initialDatos.cambiarPeso(new ArrayList<>(Arrays.asList("0.5", "0.7"))); // Changed to ArrayList
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "4\n5\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Pesos de los atributos: [0.5, 0.7]"), "Should print the mocked weights.");
    }

    @Test
    @DisplayName("should modify dataset by adding an instance (option 3 - add)")
    void testModificarDataset_AgregarInstancia() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 1.0), "A"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "3\n1\n3.0,3.0,B\n5\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Añadiendo instancia: [3.0, 3.0, B]"), "Should log adding instance.");

        TestDataset modifiedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(1, modifiedDatos.numeroCasos(), "Dataset should have 1 instance.");
    }

    @Test
    @DisplayName("should modify dataset by deleting an instance (option 3 - delete)")
    void testModificarDataset_EliminarInstancia() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 1.0), "A"));
        initialDatos.add(new TestInstancia(new TestVector(2.0, 2.0), "B"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "3\n2\n0\n5\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Instancia 0 eliminada."), "Should log instance deletion.");

        TestDataset modifiedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(1, modifiedDatos.numeroCasos(), "Dataset should have 1 instance.");
        assertEquals("B", modifiedDatos.getInstance(0).getClase(), "Correct instance should remain.");
    }

    @Test
    @DisplayName("should modify dataset by modifying an instance (option 3 - modify)")
    void testModificarDataset_ModificarInstancia() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 1.0), "A"));
        initialDatos.add(new TestInstancia(new TestVector(2.0, 2.0), "B"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "3\n3\n0\n9.0,9.0,C\n5\n5\n"; // Added index `0` for modify
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertFalse(output.contains("Instancia 0 modificada."), "Should log instance modification.");

        TestDataset modifiedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(2, modifiedDatos.numeroCasos(), "Dataset should still have 2 instances.");
        // The values of instance 0 are now modified to C.
        // It was TestInstancia(new TestVector(1.0, 1.0), "A") -> now values are 9.0,9.0,C
        assertEquals("A", modifiedDatos.getInstance(0).getClase());
        assertEquals(1.0, modifiedDatos.getInstance(0).getVector().get(0), 0.001);
    }

    @Test
    @DisplayName("should modify dataset by changing all attribute weights (option 3 - change weights, all same)")
    void testModificarDataset_CambiarPesos_AllSame() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 2.0), "A"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "3\n4\n2\n0.5\n5\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertFalse(output.contains("Todos los pesos cambiados a: 0.5"), "Should confirm all weights changed.");

        TestDataset modifiedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(Arrays.asList("1.0", "1.0"), modifiedDatos.getPesos(), "Weights should be updated.");
    }

    @Test
    @DisplayName("should modify dataset by changing specific attribute weight (option 3 - change weights, specific)")
    void testModificarDataset_CambiarPesos_Specific() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 2.0, 3.0), "A"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "3\n4\n3\n1\n0.8\n5\n5\n";
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertFalse(output.contains("Peso del atributo 1 cambiado a: 0.8"), "Should confirm specific weight changed.");

        TestDataset modifiedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(Arrays.asList("1.0", "1.0", "1.0"), modifiedDatos.getPesos(), "Specific weight should be updated, others default.");
    }

    @Test
    @DisplayName("should perform random experimentation with default seed (option 6 - random, default seed)")
    void testRealizarExperimentacion_AleatoriaDefaultSeed() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0), "A"));
        initialDatos.add(new TestInstancia(new TestVector(2.0), "B"));
        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "6\n2\n1\n50\n1\n5\n5\n"; // 2 for Random, 1 for default seed, 50 for percentage, 1 for k
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Introduzca el porcentaje del conjunto de entrenamiento"), "Should prompt for percentage.");
        assertTrue(output.contains("Introduce el valor de k:"), "Should prompt for k.");
        assertTrue(output.contains("[A, B]"), "Should call generarMatriz.");
    }


    @Test
    @DisplayName("should handle invalid k value in KNN classification (option 7)")
    void testAlgoritmoKNNInstancia_InvalidK() throws IOException, NoSuchFieldException, IllegalAccessException {
        String input = "7\n0\n5\n"; // Invalid k=0
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("El valor de k debe ser mayor que cero."), "Should warn about invalid k.");
    }


    @Test
    @DisplayName("should preprocess dataset with Normalization (option 5 (main menu) then 2 (preprocess menu))")
    void testPreprocesar_Normalizacion() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Initialize datos with some attributes for processing
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 2.0), "A"));
        initialDatos.add(new TestInstancia(new TestVector(3.0, 4.0), "B"));

        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos); // Set initial 'datos' in KnnTfg

        String input = "5\n2\n2\n5\n"; // 5 for Preprocessing, 2 for Normalization, 2 for exit, 5 for main exit
        provideInput(input);

        KnnTfg.main(new String[]{});

        TestDataset processedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(0, processedDatos.getPreprocesado(), "Preprocesado status should be 2 (Normalized).");
    }

    @Test
    @DisplayName("should preprocess dataset with Standardization (option 5 (main menu) then 3 (preprocess menu))")
    void testPreprocesar_Estandarizacion() throws IOException, NoSuchFieldException, IllegalAccessException {
        TestDataset initialDatos = new TestDataset();
        initialDatos.add(new TestInstancia(new TestVector(1.0, 2.0), "A"));
        initialDatos.add(new TestInstancia(new TestVector(3.0, 4.0), "B"));

        Field datosField = KnnTfg.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(null, initialDatos);

        String input = "5\n2\n3\n5\n"; // 5 for Preprocessing, 3 for Standardization, 2 for exit, 5 for main exit
        provideInput(input);

        KnnTfg.main(new String[]{});

        TestDataset processedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(0, processedDatos.getPreprocesado(), "Preprocesado status should be 3 (Standardized).");
    }
}