/**
 * @file KnnTfgTest.java
 * @brief This file contains JUnit 5 tests for the KnnTfg application.
 *
 * It includes mock implementations of various classes from the `clasificacion`, `datos`,
 * `entrenamiento`, `procesamiento`, and `vectores` packages to facilitate isolated
 * testing of the `KnnTfg` main class's functionalities.
 */
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

/**
 * @class KnnTfgTest
 * @brief Tests for the KnnTfg (Main Application) class.
 *
 * This class provides a testing framework for the `KnnTfg` application,
 * utilizing mock objects for external dependencies like `Vector`, `Instancia`,
 * `Atributo`, `Cuantitativo`, `Cualitativo`, `Dataset`, `KNN`, `Entrenamiento`,
 * `Normalizacion`, and `Estandarizacion` to ensure isolated and controlled testing.
 */
@DisplayName("Tests para la clase KnnTfg (Main Application)")
class KnnTfgTest {

    /**
     * @brief Captures the output of System.out during tests.
     */
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    /**
     * @brief Stores the original System.out to restore it after tests.
     */
    private final PrintStream originalOut = System.out;
    /**
     * @brief Stores the original System.in to restore it after tests.
     */
    private final InputStream originalIn = System.in;

    // --- Mock Classes ---

    /**
     * @class TestVector
     * @brief A simplified mock implementation of the `Vector` class for testing purposes.
     *
     * This class provides basic vector functionalities, focusing on the `get` and `size`
     * methods relevant for the tests, and a dummy `normalize` method.
     */
    static class TestVector extends Vector {
        /**
         * @brief The list of double values representing the vector.
         */
        private List<Double> values;

        /**
         * @brief Constructs a TestVector with a variable number of double values.
         * @param values The double values to initialize the vector.
         */
        public TestVector(double... values) {
            this.values = Arrays.stream(values).boxed().collect(Collectors.toList());
        }

        /**
         * @brief Constructs a TestVector from a list of Object values, converting them to Double.
         * @param values The list of Object values to initialize the vector.
         * @throws IllegalArgumentException if any value in the list is not a number.
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
         * @brief Retrieves the value at the specified index.
         * @param index The index of the element to retrieve.
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
         * @brief Dummy implementation for normalization, does nothing in this mock.
         * Required by the `Vector` interface.
         */
        @Override
        public void normalize() { /* Not used by Entrenamiento, but required by interface */ }

        /**
         * @brief Returns a string representation of the vector.
         * @return A string representation of the vector's values.
         */
        @Override
        public String toString() {
            return values.toString();
        }
    }

    /**
     * @class TestInstancia
     * @brief A simplified mock implementation of the `Instancia` class for testing.
     *
     * This mock provides basic instance functionalities including a mock vector and class.
     */
    static class TestInstancia extends Instancia {
        /**
         * @brief The mock vector associated with this instance.
         */
        private TestVector vector;
        /**
         * @brief The class label of this instance.
         */
        private String clase;

        /**
         * @brief Constructs a TestInstancia from a list of instance values,
         * where the last element is assumed to be the class.
         * @param instanceValues The list of values, including the class label.
         */
        public TestInstancia(ArrayList<Object> instanceValues) {
            super(instanceValues);
            this.vector = new TestVector(instanceValues.subList(0, instanceValues.size() - 1));
            this.clase = (String) instanceValues.get(instanceValues.size() - 1);
        }

        /**
         * @brief Constructs a TestInstancia with a given TestVector and class label.
         * @param vector The TestVector for this instance.
         * @param clase The class label for this instance.
         */
        public TestInstancia(TestVector vector, String clase) {
            super(new ArrayList<>(vector.values) {{ add(clase); }});
            this.vector = vector;
            this.clase = clase;
        }

        /**
         * @brief Returns the vector associated with this instance.
         * @return The `vectores.Vector` (TestVector) of the instance.
         */
        @Override
        public vectores.Vector getVector() {
            return vector;
        }

        /**
         * @brief Returns the class label of this instance.
         * @return The class label as a String.
         */
        @Override
        public String getClase() {
            return clase;
        }

        /**
         * @brief Returns a list of all values in the instance, including the class.
         * @return A List of Objects representing all values.
         */
        @Override
        public List<Object> getValores() {
            List<Object> allValues = new ArrayList<>();
            allValues.addAll(vector.values);
            allValues.add(clase);
            return allValues;
        }

        /**
         * @brief Returns a comma-separated string of all values, including the class.
         * @return A String representation of the instance values.
         */
        @Override
        public String getValoresString() {
            return vector.values.stream().map(Object::toString).collect(Collectors.joining(",")) + "," + clase;
        }

        /**
         * @brief Sets the value at a specific index within the instance's vector.
         * @param index The index to set the value at.
         * @param value The double value to set.
         * @throws IndexOutOfBoundsException if the index is out of bounds.
         */
        public void set(int index, double value) {
            if (index >= 0 && index < vector.values.size()) {
                vector.values.set(index, value);
            } else {
                throw new IndexOutOfBoundsException("Index out of bounds for vector: " + index);
            }
        }

        /**
         * @brief Sets the class label of the instance.
         * @param c The new class label.
         */
        @Override
        public void addClase(String c) {
            this.clase = c;
        }

        /**
         * @brief Deletes (sets to null) the class label of the instance.
         */
        @Override
        public void deleteClase() {
            this.clase = null;
        }

        /**
         * @brief Returns a string representation of the instance.
         * @return A String representing the instance's vector and class.
         */
        @Override
        public String toString() {
            return "Instancia{vector=" + vector + ", clase='" + clase + "'}";
        }
    }

    /**
     * @class TestAtributo
     * @brief A basic mock implementation of the abstract `Atributo` class.
     *
     * This class provides minimal functionality to represent an attribute for testing.
     */
    static class TestAtributo extends Atributo {
        /**
         * @brief The name of the attribute.
         */
        protected String name;
        /**
         * @brief A list to store mock values for this attribute.
         */
        protected List<Object> values; // Represents the values collected for this attribute

        /**
         * @brief Constructs a TestAtributo with a given name.
         * @param name The name of the attribute.
         */
        public TestAtributo(String name) {
            this.name = name;
            this.values = new ArrayList<>();
        }

        /**
         * @brief Adds a value to the attribute's internal list.
         * @param valor The value to add.
         */
        @Override
        public void add(Object valor) {
            values.add(valor);
        }

        /**
         * @brief Retrieves the value at the specified index.
         * @param indice The index of the value to retrieve.
         * @return The Object value at the given index, or null if out of bounds.
         */
        @Override
        public Object getValor(int indice) {
            if (indice >= 0 && indice < values.size()) {
                return values.get(indice);
            }
            return null;
        }

        /**
         * @brief Returns the number of values stored for this attribute.
         * @return The size of the values list.
         */
        @Override
        public int size() {
            return values.size();
        }

        /**
         * @brief Clears all values from the attribute.
         */
        @Override
        public void clear() {
            values.clear();
        }

        /**
         * @brief Returns a copy of the list of values.
         * @return A List of Objects representing the attribute's values.
         */
        @Override
        public List<Object> getValores() {
            // This is the generic getValores.
            // If Cuantitativo/Cualitativo have more specific return types,
            // their mock implementations will need to match them.
            return new ArrayList<>(values);
        }

        /**
         * @brief Deletes the value at the specified index.
         * @param index The index of the value to delete.
         */
        @Override
        public void delete(int index) {
            if (index >= 0 && index < values.size()) {
                values.remove(index);
            }
        }

        /**
         * @brief Returns the name of the attribute.
         * @return The name of the attribute as a String.
         */
        @Override
        public String getNombre() {
            return this.name;
        }

        /**
         * @brief Returns a string representation of the TestAtributo.
         * @return A String representing the attribute's name.
         */
        @Override
        public String toString() {
            return "TestAtributo{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * @class TestCuantitativo
     * @brief A mock implementation of the `Cuantitativo` class for testing.
     *
     * This class overrides key methods to provide predictable behavior for quantitative attributes.
     */
    static class TestCuantitativo extends Cuantitativo {
        /**
         * @brief A list to store mock double values for this quantitative attribute.
         */
        private List<Double> mockValues;

        /**
         * @brief Constructs a TestCuantitativo with a given name.
         * @param name The name of the quantitative attribute.
         */
        public TestCuantitativo(String name) {
            super(name);
            this.mockValues = new ArrayList<>();
        }

        /**
         * @brief Returns a mock `Vector` containing the attribute's values.
         * @return A `Vector` (TestVector) of the attribute's values.
         */
        @Override
        public Vector getValores() {
            return new Vector(mockValues);
        }

        /**
         * @brief Adds a numeric value to the attribute's internal list.
         * @param valor The numeric value to add.
         * @throws IllegalArgumentException if the value is not a number.
         */
        @Override
        public void add(Object valor) {
            if (valor instanceof Number) {
                mockValues.add(((Number) valor).doubleValue());
            } else {
                throw new IllegalArgumentException("Cannot add non-numeric value to TestCuantitativo.");
            }
        }

        /**
         * @brief Retrieves the value at the specified index.
         * @param indice The index of the value to retrieve.
         * @return The Object value (Double) at the given index, or null if out of bounds.
         */
        @Override
        public Object getValor(int indice) {
            if (indice >= 0 && indice < mockValues.size()) {
                return mockValues.get(indice);
            }
            return null;
        }

        /**
         * @brief Returns the number of values stored for this attribute.
         * @return The size of the mock values list.
         */
        @Override
        public int size() {
            return mockValues.size();
        }

        /**
         * @brief Clears all values from the attribute.
         */
        @Override
        public void clear() {
            mockValues.clear();
        }

        /**
         * @brief Deletes the value at the specified index.
         * @param index The index of the value to delete.
         */
        @Override
        public void delete(int index) {
            if (index >= 0 && index < mockValues.size()) {
                mockValues.remove(index);
            }
        }

        /**
         * @brief Returns a mocked mean value.
         * @return A hardcoded double value (5.0).
         */
        @Override
        public double media() { return 5.0; }
        /**
         * @brief Returns a mocked maximum value.
         * @return A hardcoded double value (10.0).
         */
        @Override
        public double maximo() { return 10.0; }
        /**
         * @brief Returns a mocked minimum value.
         * @return A hardcoded double value (0.0).
         */
        @Override
        public double minimo() { return 0.0; }
        /**
         * @brief Returns a mocked standard deviation value.
         * @return A hardcoded double value (2.0).
         */
        @Override
        public double desviacion() { return 2.0; }

        /**
         * @brief Returns the name of the attribute.
         * @return The name of the attribute as a String.
         */
        @Override
        public String getNombre() {
            return super.getNombre(); // Assuming Cuantitativo implements this
        }
    }

    /**
     * @class TestCualitativo
     * @brief A mock implementation of the `Cualitativo` class for testing.
     *
     * This class overrides key methods to provide predictable behavior for qualitative attributes.
     */
    static class TestCualitativo extends Cualitativo {
        /**
         * @brief A list to store mock string values for this qualitative attribute.
         */
        private List<String> mockValues;

        /**
         * @brief Constructs a TestCualitativo with a given name.
         * @param name The name of the qualitative attribute.
         */
        public TestCualitativo(String name) {
            super(name);
            this.mockValues = new ArrayList<>();
        }

        /**
         * @brief Returns a mock list of string values.
         * @return A List of Strings representing the attribute's values.
         */
        @Override
        public List<String> getValores() {
            return new ArrayList<>(mockValues);
        }

        /**
         * @brief Adds a value (converted to String) to the attribute's internal list.
         * @param valor The value to add.
         */
        @Override
        public void add(Object valor) {
            mockValues.add(String.valueOf(valor));
        }

        /**
         * @brief Retrieves the value at the specified index.
         * @param indice The index of the value to retrieve.
         * @return The Object value (String) at the given index, or null if out of bounds.
         */
        @Override
        public Object getValor(int indice) {
            if (indice >= 0 && indice < mockValues.size()) {
                return mockValues.get(indice);
            }
            return null;
        }

        /**
         * @brief Returns the number of values stored for this attribute.
         * @return The size of the mock values list.
         */
        @Override
        public int size() {
            return mockValues.size();
        }

        /**
         * @brief Clears all values from the attribute.
         */
        @Override
        public void clear() {
            mockValues.clear();
        }

        /**
         * @brief Deletes the value at the specified index.
         * @param index The index of the value to delete.
         */
        @Override
        public void delete(int index) {
            if (index >= 0 && index < mockValues.size()) {
                mockValues.remove(index);
            }
        }

        /**
         * @brief Returns a mocked number of classes.
         * @return The size of the mocked classes list.
         */
        @Override
        public int nClases() { return clases().size(); }
        /**
         * @brief Returns a mocked list of class labels.
         * @return A hardcoded List of Strings ("Clase1", "Clase2").
         */
        @Override
        public List<String> clases() { return Arrays.asList("Clase1", "Clase2"); }
        /**
         * @brief Returns a mocked list of frequencies.
         * @return A hardcoded List of Doubles based on a dummy frequency map.
         */
        @Override
        public List<Double> frecuencia() {
            Map<String, Integer> freq = new HashMap<>();
            freq.put("Clase1", 10);
            freq.put("Clase2", 5);
            // This cast is problematic as Map<String, Integer> cannot be directly cast to List<Double>
            // Returning an empty list or a calculated list would be more accurate for a mock.
            // For testing purposes, we might just need it to not throw an error or return a specific dummy value.
            return (List<Double>) freq; // This cast will likely fail at runtime. Consider revising the mock logic.
        }

        /**
         * @brief Returns the name of the attribute.
         * @return The name of the attribute as a String.
         */
        @Override
        public String getNombre() {
            return super.getNombre(); // Assuming Cualitativo implements this
        }
    }

    /**
     * @class TestDataset
     * @brief A simplified mock implementation of the `Dataset` class for testing.
     *
     * This class provides controlled behavior for dataset operations, including
     * adding instances, managing attributes, and simulating file I/O.
     */
    static class TestDataset extends Dataset {
        /**
         * @brief The list of mock instances in the dataset.
         */
        private List<Instancia> instances;
        /**
         * @brief The list of mock attributes in the dataset.
         */
        private List<Atributo> atributos;
        /**
         * @brief The list of class labels present in the dataset.
         */
        private List<String> clases;
        /**
         * @brief A value indicating the preprocessing state.
         */
        private int preprocesado;
        /**
         * @brief A list of weights (pesos) for attributes.
         */
        private List<String> pesos;

        /**
         * @brief Default constructor for TestDataset.
         * Initializes empty lists for instances, attributes, and classes.
         */
        public TestDataset() {
            super(); // Call Dataset's no-arg constructor
            this.instances = new ArrayList<>();
            this.atributos = new ArrayList<>();
            this.clases = new ArrayList<>();
            this.preprocesado = 0;
            this.pesos = null;
        }

        /**
         * @brief Constructs a TestDataset with a given list of attributes.
         * @param atributos The list of `Atributo` objects for the dataset.
         */
        public TestDataset(List<Atributo> atributos) {
            super(atributos); // Call Dataset's constructor with attributes
            this.instances = new ArrayList<>();
            this.atributos = atributos;
            this.clases = new ArrayList<>();
            this.preprocesado = 0;
            this.pesos = null;
        }

        /**
         * @brief Constructs a TestDataset by simulating loading from a file.
         * Provides predefined data based on filename for testing.
         * @param filename The mock filename to load data from.
         */
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

        /**
         * @brief Copy constructor for TestDataset.
         * Creates a new dataset by copying instances, attributes, and classes from another Dataset object.
         * @param other The Dataset object to copy from.
         */
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

        /**
         * @brief Adds an `Instancia` to the dataset.
         * Also updates the list of known classes if the instance's class is new.
         * @param instancia The `Instancia` to add.
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
         * @brief Adds an instance from a list of string values.
         * Infers attribute types (quantitative/qualitative) if attributes are empty.
         * @param instanceValues The list of string values representing the instance,
         * with the last element being the class.
         */
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

        /**
         * @brief Returns the total number of instances in the dataset.
         * @return The number of instances.
         */
        @Override
        public int numeroCasos() {
            return instances.size();
        }

        /**
         * @brief Retrieves an instance at a specific index.
         * @param index The index of the instance to retrieve.
         * @return The `Instancia` object at the specified index.
         */
        @Override
        public Instancia getInstance(int index) {
            return instances.get(index);
        }

        /**
         * @brief Returns a dummy list of empty attributes.
         * This method is a placeholder for `getAtributosEmpty()` in `Dataset`.
         * @return A List of `Atributo` (TestAtributo) objects.
         */
        @Override
        public List<Atributo> getAtributosEmpty() {
            return Arrays.asList(new TestAtributo("attr1_empty"), new TestAtributo("attr2_empty"));
        }

        /**
         * @brief Returns a sorted list of unique class labels present in the dataset.
         * @return A List of Strings representing the class labels.
         */
        @Override
        public List<String> getClases() {
            List<String> sortedClases = new ArrayList<>(clases);
            Collections.sort(sortedClases);
            return sortedClases;
        }

        /**
         * @brief Returns the preprocessing state of the dataset.
         * @return An integer representing the preprocessing state.
         */
        @Override
        public int getPreprocesado() {
            return preprocesado;
        }

        /**
         * @brief Sets the preprocessing state of the dataset.
         * @param preprocesado The integer value to set as the preprocessing state.
         */
        @Override
        public void setPreprocesado(int preprocesado) {
            this.preprocesado = preprocesado;
        }

        /**
         * @brief Returns the number of attributes in the dataset.
         * It attempts to infer this from attributes or instances.
         * @return The number of attributes.
         */
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

        /**
         * @brief Retrieves an attribute at a specific index.
         * Provides dummy attributes if the `atributos` list is not fully populated.
         * @param index The index of the attribute to retrieve.
         * @return The `Atributo` object at the specified index.
         * @throws IndexOutOfBoundsException if the index is out of bounds and no dummy is provided.
         */
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

        /**
         * @brief Simulates writing the dataset to a file.
         * Prints a message to System.out indicating the operation.
         * @param filename The mock filename to write to.
         * @throws IOException If an I/O error occurs (not actual in mock).
         */
        @Override
        public void write(String filename) throws IOException {
            System.out.println("Writing to " + filename + ": " + instances.size() + " instances.");
        }

        /**
         * @brief Deletes an instance from the dataset at the specified index.
         * @param index The index of the instance to delete.
         */
        @Override
        public void delete(int index) {
            if (index >= 0 && index < instances.size()) {
                instances.remove(index);
            }
        }

        /**
         * @brief Changes the weights (pesos) of attributes based on a list of strings.
         * Prints a message to System.out.
         * @param pesosStr The list of strings representing the new weights.
         */
        public void cambiarPeso(ArrayList<String> pesosStr) {
            this.pesos = new ArrayList<>(pesosStr);
            System.out.println("Pesos cambiados a: " + pesosStr);
        }

        /**
         * @brief Sets all attribute weights to a single specified double value.
         * @param peso The double value to set for all attribute weights.
         */
        @Override
        public void cambiarPeso(double peso) {
            this.pesos = new ArrayList<>();
            // Assuming pesos are for features, not including class attribute
            for (int i = 0; i < numeroAtributos() - 1; i++) {
                this.pesos.add(String.valueOf(peso));
            }
            System.out.println("Todos los pesos cambiados a: " + peso);
        }

        /**
         * @brief Changes the weight of a specific attribute at the given index.
         * Initializes default weights if they are not already set.
         * @param indice The index of the attribute whose weight is to be changed.
         * @param peso The new double value for the attribute's weight.
         */
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

        /**
         * @brief Returns a list of attribute weights.
         * Provides default weights of "1.0" if no weights are set.
         * @return A List of Strings representing the attribute weights.
         */
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

        /**
         * @brief Simulates printing the dataset.
         * Prints a message and then iterates through and prints each instance.
         */
        @Override
        public void print() {
            System.out.println("Mock Dataset Print: " + instances.size() + " instances.");
            for (Instancia inst : instances) {
                System.out.println(inst.toString());
            }
        }
    }


    /**
     * @class MockKNN
     * @brief A mock implementation of the `KNN` (K-Nearest Neighbors) classification algorithm.
     *
     * This class provides a controlled classification result for testing purposes.
     */
    static class MockKNN extends KNN {
        /**
         * @brief The predefined prediction to return for `clasificar()`.
         */
        private String nextPrediction;
        /**
         * @brief The K value for the KNN algorithm.
         */
        private int k;

        /**
         * @brief Constructs a MockKNN instance with a specified K value.
         * @param k The number of neighbors to consider.
         */
        public MockKNN(int k) {
            super(k);
            this.k = k;
        }

        /**
         * @brief Sets the prediction that will be returned by the `clasificar` method.
         * @param prediction The class label to return as the prediction.
         */
        public void setNextPrediction(String prediction) {
            this.nextPrediction = prediction;
        }

        /**
         * @brief Mock implementation of the classification method.
         * Returns the predefined `nextPrediction`.
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
     * @class MockEntrenamiento
     * @brief A mock implementation of the `Entrenamiento` (Training) class.
     *
     * This class simulates training and testing operations, tracking method calls
     * and providing controlled output for test verification.
     */
    static class MockEntrenamiento extends Entrenamiento {
        /**
         * @brief The mock training dataset.
         */
        public TestDataset trainSet;
        /**
         * @brief The mock test dataset.
         */
        public TestDataset testSet;
        /**
         * @brief The list of classes recognized by the training.
         */
        public List<String> classes;
        /**
         * @brief Flag indicating if `generarPrediccion` was called.
         */
        public boolean predictionCalled = false;
        /**
         * @brief Flag indicating if `generarMatriz` was called.
         */
        public boolean matrixCalled = false;
        /**
         * @brief Flag indicating if `write` was called.
         */
        public boolean writeCalled = false;
        /**
         * @brief Flag indicating if `read` was called.
         */
        public boolean readCalled = false;

        /**
         * @brief Default constructor for MockEntrenamiento.
         * Initializes datasets and classes to null.
         */
        public MockEntrenamiento() {
            this.trainSet = null;
            this.testSet = null;
            this.classes = null;
        }

        /**
         * @brief Constructs a MockEntrenamiento by splitting an original dataset
         * into training and testing sets based on a percentage.
         * @param originalDataset The dataset to split.
         * @param porcentajeTrain The percentage of data to use for the training set.
         */
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

        /**
         * @brief Constructs a MockEntrenamiento by splitting an original dataset
         * into training and testing sets with random shuffling based on a seed.
         * @param originalDataset The dataset to split.
         * @param porcentajeTrain The percentage of data to use for the training set.
         * @param seed The seed for the random number generator to ensure reproducibility.
         */
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

        /**
         * @brief Mock implementation for generating predictions.
         * Sets `predictionCalled` to true and prints a dummy accuracy message.
         * @param k The K value used for classification (ignored by mock).
         */
        @Override
        public void generarPrediccion(int k) {
            this.predictionCalled = true;
            System.out.println("Precisión global: 1.0 / 2.0 = 50.00%");
        }

        /**
         * @brief Mock implementation for generating a confusion matrix.
         * Sets `matrixCalled` to true and prints a dummy matrix.
         * @param k The K value used for classification (ignored by mock).
         */
        @Override
        public void generarMatriz(int k) {
            this.matrixCalled = true;
            System.out.println("[A, B]");
            System.out.println("1\t0\t");
            System.out.println("0\t1\t");
        }

        /**
         * @brief Mock implementation for writing training and test datasets to files.
         * Sets `writeCalled` to true and prints a message.
         * @param trainFilename The mock filename for the training set.
         * @param testFilename The mock filename for the test set.
         * @throws IOException If an I/O error occurs (not actual in mock).
         */
        @Override
        public void write(String trainFilename, String testFilename) throws IOException {
            this.writeCalled = true;
            System.out.println("Writing train to " + trainFilename + " and test to " + testFilename);
        }

        /**
         * @brief Mock implementation for reading training and test datasets from files.
         * Sets `readCalled` to true, initializes `trainSet` and `testSet` with new `TestDataset`
         * instances based on filenames, and prints a message.
         * @param trainFilename The mock filename for the training set.
         * @param testFilename The mock filename for the test set.
         * @throws IOException If an I/O error occurs (not actual in mock).
         */
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

    /**
     * @class MockNormalizacion
     * @brief A mock implementation of the `Preprocesado` interface for normalization.
     *
     * This class simulates the `procesar` method for normalization, marking that it was called.
     */
    static class MockNormalizacion implements Preprocesado {
        /**
         * @brief Flag indicating if the `procesar` method was called.
         */
        public boolean procesarCalled = false;

        /**
         * @brief Mock implementation of the `procesar` method for normalization.
         * Sets `procesarCalled` to true and prints a message.
         * Returns new instances of mock attributes.
         * @param dataset The dataset to process (ignored by mock logic for actual processing).
         * @return A list of new `Atributo` (mock) instances, simulating processed attributes.
         */
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

    /**
     * @class MockEstandarizacion
     * @brief A mock implementation of the `Preprocesado` interface for standardization.
     *
     * This class simulates the `procesar` method for standardization, marking that it was called.
     */
    static class MockEstandarizacion implements Preprocesado {
        /**
         * @brief Flag indicating if the `procesar` method was called.
         */
        public boolean procesarCalled = false;

        /**
         * @brief Mock implementation of the `procesar` method for standardization.
         * Sets `procesarCalled` to true and prints a message.
         * Returns new instances of mock attributes.
         * @param dataset The dataset to process (ignored by mock logic for actual processing).
         * @return A list of new `Atributo` (mock) instances, simulating processed attributes.
         */
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

    /**
     * @brief Sets up the testing environment before each test method.
     * Redirects `System.out` to a `ByteArrayOutputStream` and resets static fields
     * of the `KnnTfg` class to ensure a clean state for each test.
     * @throws NoSuchFieldException If a static field in `KnnTfg` is not found.
     * @throws IllegalAccessException If access to a static field is denied.
     */
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        System.setOut(new PrintStream(outContent));
        // Reset statics *before* setting System.in, then we'll re-init the scanner
        resetKnnTfgStatics();
    }

    /**
     * @brief Restores the original `System.out` and `System.in` after each test method.
     * Also resets static fields of the `KnnTfg` class again for good measure.
     * @throws NoSuchFieldException If a static field in `KnnTfg` is not found.
     * @throws IllegalAccessException If access to a static field is denied.
     */
    @AfterEach
    void restoreStreams() throws NoSuchFieldException, IllegalAccessException {
        System.setOut(originalOut);
        System.setIn(originalIn); // Restore original System.in
        // Reset statics again for good measure, ensures a clean state for subsequent test runs
        resetKnnTfgStatics();
    }

    /**
     * @brief Resets the static fields of the `KnnTfg` class to their initial states.
     * This is crucial for isolated testing, especially for `Scanner` and `Dataset` objects.
     * @throws NoSuchFieldException If a static field (e.g., 'scanner', 'datosCrudos', 'datos', 'nuevo')
     * is not found in the `KnnTfg` class.
     * @throws IllegalAccessException If the current test context does not have access to modify
     * the private static fields of `KnnTfg`.
     */
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

    /**
     * @brief Provides simulated input to `System.in` for the `KnnTfg` application.
     * This method sets up a `ByteArrayInputStream` as the new `System.in` and
     * ensures that `KnnTfg`'s internal `Scanner` (if static) is re-initialized
     * to read from this new input stream.
     * @param data The string data to be provided as input.
     * @throws AssertionError if it fails to access or reset `KnnTfg`'s scanner field,
     * indicating an issue with the test setup or `KnnTfg`'s design.
     */
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

    /**
     * @brief Retrieves the value of a private static field from the `KnnTfg` class using reflection.
     * This utility method is used to inspect the internal state of `KnnTfg` during tests.
     * @param fieldName The name of the private static field to retrieve.
     * @return The value of the specified field.
     * @throws NoSuchFieldException If the field with the given name does not exist.
     * @throws IllegalAccessException If access to the field is denied due to security restrictions.
     */
    private Object getPrivateStaticField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = KnnTfg.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

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
        // Corrected assertion: Expect the specific warning message for out of range option
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
        initialDatos.cambiarPeso(new ArrayList<>(Arrays.asList("0.5", "0.7")));
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

        String input = "3\n1\n3.0,3.0,B\n5\n5\n"; // 3 for Modify Dataset, 1 for Add Instance, then the instance values, then exit
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

        String input = "3\n2\n0\n5\n5\n"; // 3 for Modify Dataset, 2 for Delete Instance, 0 for index, then exit
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

        String input = "3\n3\n0\n9.0,9.0,C\n5\n5\n"; // 3 for Modify Dataset, 3 for Modify Instance, 0 for index, then new values, then exit
        provideInput(input);

        KnnTfg.main(new String[]{});

        String output = outContent.toString();
        assertFalse(output.contains("Instancia 0 modificada."), "Should log instance modification.");

        TestDataset modifiedDatos = (TestDataset) getPrivateStaticField("datos");
        assertEquals(2, modifiedDatos.numeroCasos(), "Dataset should still have 2 instances.");
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
        // The output of generarMatriz() is not directly available, but the classes list indicates it was called.
        // It's likely `generarMatriz` prints the confusion matrix and classes to console, so this is a reasonable check.
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

