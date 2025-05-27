package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vectores.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Instancia Class Tests")
class InstanciaTest {

    @BeforeEach
    void setUp() {
        // Any setup needed before each test can go here.
    }

    // --- Constructor Tests ---

    @Test
    @DisplayName("Should create an empty Instancia with no-arg constructor")
    void testConstructorEmpty() {
        Instancia instancia = new Instancia();
        assertNotNull(instancia);
        assertTrue(instancia.getValores().isEmpty());
    }

    @Test
    @DisplayName("Should create an Instancia from a List<Object>")
    void testConstructorListOfObjects() {
        List<Object> values = Arrays.asList(1.0, 2, "ClaseA");
        Instancia instancia = new Instancia(values);
        assertNotNull(instancia);
        assertEquals(3, instancia.getValores().size());
        assertEquals(1.0, instancia.getValores().get(0));
        assertEquals(2, instancia.getValores().get(1));
        assertEquals("ClaseA", instancia.getValores().get(2));
    }

    @Test
    @DisplayName("Should handle null List<Object> in constructor (creates an empty list)")
    void testConstructorNullListOfObjects() {
        Instancia instancia = new Instancia((List<Object>) null);
        assertNotNull(instancia.getValores()); // Expect non-null list
        assertTrue(instancia.getValores().isEmpty()); // Expect empty list
    }

    @Test
    @DisplayName("Should create an Instancia from a comma-separated String")
    void testConstructorString() {
        Instancia instancia = new Instancia("val1,val2,val3");
        assertNotNull(instancia);
        assertEquals(3, instancia.getValores().size());
        assertEquals("val1", instancia.getValores().get(0));
        assertEquals("val2", instancia.getValores().get(1));
        assertEquals("val3", instancia.getValores().get(2));
    }

    @Test
    @DisplayName("Should handle empty string in constructor String")
    void testConstructorEmptyString() {
        Instancia instancia = new Instancia("");
        assertNotNull(instancia);
        assertEquals(1, instancia.getValores().size()); // split(",") on "" yields [""]
        assertEquals("", instancia.getValores().get(0));
    }

    @Test
    @DisplayName("Should handle null string in constructor String")
    void testConstructorNullString() {
        // String.split(null) throws NullPointerException.
        // The constructor doesn't guard against this.
        assertThrows(NullPointerException.class, () -> new Instancia((String) null));
    }


    // --- Getters and toString Tests ---

    // In InstanciaTest.java
    @Test
    @DisplayName("Should return the internal list of values (now a safe copy)")
    void testGetValores() {
        List<Object> values = Arrays.asList(1.0, "ClaseA");
        Instancia instancia = new Instancia(values); // This will now use new ArrayList<>(nuevos)
        List<Object> retrievedValues = instancia.getValores();

        assertNotNull(retrievedValues);
        assertEquals(values.size(), retrievedValues.size()); // Check size first
        // Check element by element if needed, but List.equals() should do this
        assertEquals(values.get(0), retrievedValues.get(0));
        assertEquals(values.get(1), retrievedValues.get(1));

        // The direct assertEquals(values, retrievedValues) usually works for content,
        // but the error message might be misleading. Let's simplify and make sure.
        // The main point is that retrievedValues should now be a modifiable ArrayList,
        // and its contents should match 'values'.
        assertEquals(values, retrievedValues); // This will pass if content is equal.

        // DO NOT USE assertSame here, as it will always fail after the constructor change
        // assertSame(values, retrievedValues); // This must be removed or commented out!
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        List<Object> values = Arrays.asList(1.0, 2, "ClaseA");
        Instancia instancia = new Instancia(values);
        assertEquals("[1.0, 2, ClaseA]", instancia.toString());
    }

    @Test
    @DisplayName("Should return correct string representation for empty instance")
    void testToStringEmpty() {
        Instancia instancia = new Instancia();
        assertEquals("[]", instancia.toString());
    }

    @Test
    @DisplayName("Should return a Vector of numeric values (excluding last element)")
    void testGetVector() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2, 3.5, "Class"));
        Vector vector = instancia.getVector();
        assertNotNull(vector);
        assertEquals(3, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1)); // Integer should be converted to Double
        assertEquals(3.5, vector.get(2));
    }

    @Test
    @DisplayName("Should return an empty Vector if Instancia has only a class label")
    void testGetVectorOnlyClass() {
        Instancia instancia = new Instancia(Arrays.asList("ClassA"));
        Vector vector = instancia.getVector();
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should return an empty Vector if Instancia is empty")
    void testGetVectorEmpty() {
        Instancia instancia = new Instancia();
        Vector vector = instancia.getVector();
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should return an empty Vector if no numeric values before class label")
    void testGetVectorNoNumericValues() {
        Instancia instancia = new Instancia(Arrays.asList("a", "b", "Class"));
        Vector vector = instancia.getVector();
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should return the class label (last element)")
    void testGetClase() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, "ClaseB"));
        assertEquals("ClaseB", instancia.getClase());
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException if getClase on empty Instancia")
    void testGetClaseEmpty() {
        Instancia instancia = new Instancia();
        assertThrows(IndexOutOfBoundsException.class, instancia::getClase);
    }

    @Test
    @DisplayName("Should throw ClassCastException if last element is not a String")
    void testGetClaseNotString() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, 3.0));
        assertThrows(ClassCastException.class, instancia::getClase);
    }

    // --- Data Manipulation Methods ---

    @Test
    @DisplayName("Should normalize numeric values and replace original values")
    void testNormalizar() {
        Instancia instancia = new Instancia(Arrays.asList(0.0, 5.0, 10.0, "ClassA"));
        instancia.normalizar();
        List<Object> normalizedValues = instancia.getValores();

        assertNotNull(normalizedValues);
        assertEquals(3, normalizedValues.size()); // Class label is removed for normalization, then re-added? No, not re-added.
        // This is a key observation: getVector ignores the last element,
        // but normalize() replaces the entire 'valores' list with the normalized vector.
        // This is a potential design flaw in Instancia.normalizar().
        // The class label will be lost.
        assertEquals(0.0, (Double) normalizedValues.get(0), 0.001);
        assertEquals(0.5, (Double) normalizedValues.get(1), 0.001);
        assertEquals(1.0, (Double) normalizedValues.get(2), 0.001);
        // The class label "ClassA" is expected to be lost based on current implementation.
    }

    @Test
    @DisplayName("Should handle empty numeric vector during normalization (no-op)")
    void testNormalizarEmptyVector() {
        Instancia instancia = new Instancia(Arrays.asList("ClassA")); // Only class label
        instancia.normalizar();
        // It should still replace 'valores' with an empty list from 'aux.getValores()'
        assertTrue(instancia.getValores().isEmpty());
    }

    @Test
    @DisplayName("Should handle numeric vector with zero range during normalization")
    void testNormalizarZeroRange() {
        Instancia instancia = new Instancia(Arrays.asList(5.0, 5.0, 5.0, "ClassA"));
        instancia.normalizar();
        List<Object> normalizedValues = instancia.getValores();

        assertEquals(3, normalizedValues.size());
        assertEquals(5.0, (Double) normalizedValues.get(0), 0.001);
        assertEquals(5.0, (Double) normalizedValues.get(1), 0.001);
        assertEquals(5.0, (Double) normalizedValues.get(2), 0.001);
    }


    @Test
    @DisplayName("Should standardize numeric values and replace original values")
    void testEstandarizar() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, 3.0, "ClassA"));
        instancia.estandarizar();
        List<Object> standardizedValues = instancia.getValores();

        assertNotNull(standardizedValues);
        assertEquals(3, standardizedValues.size()); // Class label is lost, similar to normalize()

        // Expected values for standardization:
        // Mean = (1+2+3)/3 = 2
        // Variance = ((1-2)^2 + (2-2)^2 + (3-2)^2)/3 = (1 + 0 + 1)/3 = 2/3
        // Std Dev = sqrt(2/3) ~ 0.816496
        // (1-2)/0.816496 = -1/0.816496 ~ -1.2247
        // (2-2)/0.816496 = 0
        // (3-2)/0.816496 = 1/0.816496 ~ 1.2247
        assertEquals(-1.2247, (Double) standardizedValues.get(0), 0.001);
        assertEquals(0.0, (Double) standardizedValues.get(1), 0.001);
        assertEquals(1.2247, (Double) standardizedValues.get(2), 0.001);
    }

    @Test
    @DisplayName("Should handle empty numeric vector during standardization (no-op, results in empty list)")
    void testEstandarizarEmptyVector() {
        Instancia instancia = new Instancia(Arrays.asList("ClassA"));
        instancia.estandarizar();
        assertTrue(instancia.getValores().isEmpty());
    }

    // In InstanciaTest.java
    @Test
    @DisplayName("Should handle single element numeric vector during standardization (results in 0.0)")
    void testEstandarizarSingleElement() {
        Instancia instancia = new Instancia(new ArrayList<>(Arrays.asList(5.0, "ClassA")));
        instancia.estandarizar();
        List<Object> standardizedValues = instancia.getValores();
        assertEquals(1, standardizedValues.size());
        assertEquals(0.0, (Double) standardizedValues.get(0), 0.001); // Expect 0.0
    }

    @Test
    @DisplayName("Should delete the class label (last element)")
    void testDeleteClase() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, "ClaseC"));
        instancia.deleteClase();
        assertEquals(2, instancia.getValores().size());
        assertEquals(1.0, instancia.getValores().get(0));
        assertEquals(2.0, instancia.getValores().get(1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when deleting class from empty Instancia")
    void testDeleteClaseEmpty() {
        Instancia instancia = new Instancia();
        assertThrows(IndexOutOfBoundsException.class, instancia::deleteClase);
    }

    @Test
    @DisplayName("Should add a new class label")
    void testAddClase() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0));
        instancia.addClase("NewClass");
        assertEquals(3, instancia.getValores().size());
        assertEquals("NewClass", instancia.getValores().get(2));
    }

    @Test
    @DisplayName("Should set a value at a valid index")
    void testSetValid() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0, "ClaseD"));
        instancia.set(1, 99.9);
        assertEquals(99.9, instancia.getValores().get(1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting value at negative index")
    void testSetNegativeIndex() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0));
        assertThrows(IndexOutOfBoundsException.class, () -> instancia.set(-1, 0.0));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting value at out-of-bounds index")
    void testSetOutOfBoundsIndex() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2.0));
        assertThrows(IndexOutOfBoundsException.class, () -> instancia.set(2, 0.0));
    }

    @Test
    @DisplayName("Should return comma-separated string of all values")
    void testGetValoresString() {
        Instancia instancia = new Instancia(Arrays.asList(1.0, 2, "ClaseE"));
        assertEquals("1.0,2,ClaseE", instancia.getValoresString());
    }

    @Test
    @DisplayName("Should return empty string for an empty Instancia in getValoresString")
    void testGetValoresStringEmpty() {
        Instancia instancia = new Instancia();
        assertEquals("", instancia.getValoresString());
    }

    @Test
    @DisplayName("Should handle single element in getValoresString")
    void testGetValoresStringSingleElement() {
        Instancia instancia = new Instancia(Arrays.asList("Solo"));
        assertEquals("Solo", instancia.getValoresString());
    }
}
