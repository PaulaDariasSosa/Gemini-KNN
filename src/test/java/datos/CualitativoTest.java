package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cualitativo Class Tests")
class CualitativoTest {

    private Cualitativo cualitativo;

    @BeforeEach
    void setUp() {
        // Initialize a fresh Cualitativo instance before each test
        cualitativo = new Cualitativo();
    }

    // --- Constructor Tests ---

    @Test
    @DisplayName("Should create an empty Cualitativo with no-arg constructor")
    void testConstructorEmpty() {
        assertNotNull(cualitativo);
        assertEquals("", cualitativo.getNombre());
        assertEquals(1.0, cualitativo.getPeso()); // Default peso from Atributo
        assertTrue(cualitativo.getValores().isEmpty());
    }

    @Test
    @DisplayName("Should create Cualitativo with name constructor")
    void testConstructorWithName() {
        Cualitativo namedCualitativo = new Cualitativo("Color");
        assertNotNull(namedCualitativo);
        assertEquals("Color", namedCualitativo.getNombre());
        assertEquals(1.0, namedCualitativo.getPeso());
        assertTrue(namedCualitativo.getValores().isEmpty());
    }

    @Test
    @DisplayName("Should create Cualitativo with name and single value constructor")
    void testConstructorWithNameAndSingleValue() {
        Cualitativo singleValueCualitativo = new Cualitativo("Size", "Large");
        assertNotNull(singleValueCualitativo);
        assertEquals("Size", singleValueCualitativo.getNombre());
        assertEquals(1.0, singleValueCualitativo.getPeso());
        assertEquals(1, singleValueCualitativo.size());
        assertEquals("Large", singleValueCualitativo.getValor(0));
    }

    @Test
    @DisplayName("Should create Cualitativo with name and list of values constructor")
    void testConstructorWithNameAndListOfValues() {
        List<String> categories = Arrays.asList("Red", "Green", "Blue");
        Cualitativo listCualitativo = new Cualitativo("Category", categories);
        assertNotNull(listCualitativo);
        assertEquals("Category", listCualitativo.getNombre());
        assertEquals(1.0, listCualitativo.getPeso());
        assertEquals(3, listCualitativo.size());
        assertEquals("Red", listCualitativo.getValor(0));
        assertEquals("Green", listCualitativo.getValor(1));
        assertEquals("Blue", listCualitativo.getValor(2));
        // Verify that the internal list is the one passed (shallow copy behavior)
        assertSame(categories, listCualitativo.getValores());
    }

    @Test
    @DisplayName("Should create a deep copy of another Cualitativo instance using copy constructor")
    void testCopyConstructor() {
        Cualitativo original = new Cualitativo("Rating", Arrays.asList("Good", "Bad"));
        original.setPeso(2.5); // Set peso to test copy
        Cualitativo copy = new Cualitativo(original);

        assertNotNull(copy);
        assertEquals(original.getNombre(), copy.getNombre());
        assertEquals(original.getPeso(), copy.getPeso());
        assertTrue(copy.getValores().isEmpty()); // Copy constructor initializes empty list, not values
        assertNotSame(original.getValores(), copy.getValores()); // Ensure different list object
    }

    // --- Getters and Setters Tests ---

    @Test
    @DisplayName("Should return the internal list of values from getValores")
    void testGetValores() {
        List<String> initialValues = new ArrayList<>(Arrays.asList("A", "B"));
        Cualitativo qual = new Cualitativo("Test", initialValues);
        List<String> retrievedValues = qual.getValores();

        assertNotNull(retrievedValues);
        assertEquals(initialValues, retrievedValues);
        // Current implementation is a shallow copy for the constructor and getter.
        // If internal list is modified via retrievedValues, it affects qual.
        assertSame(initialValues, retrievedValues); // Asserts shallow copy
    }

    @Test
    @DisplayName("Should allow setting a new list of values")
    void testSetValores() {
        List<String> newValues = new ArrayList<>(Arrays.asList("X", "Y", "Z"));
        cualitativo.setValores(newValues);

        assertEquals(3, cualitativo.size());
        assertEquals("X", cualitativo.getValor(0));
        assertEquals("Y", cualitativo.getValor(1));
        assertEquals("Z", cualitativo.getValor(2));
        assertSame(newValues, cualitativo.getValores()); // Verify shallow copy assignment
    }

    @Test
    @DisplayName("Should correctly return the name (inherited)")
    void testGetNombreInherited() {
        cualitativo.setNombre("TestName");
        assertEquals("TestName", cualitativo.getNombre());
    }

    @Test
    @DisplayName("Should correctly return the peso (inherited)")
    void testGetPesoInherited() {
        cualitativo.setPeso(3.0);
        assertEquals(3.0, cualitativo.getPeso());
    }

    @Test
    @DisplayName("Should return name and peso in 'get' method format (inherited)")
    void testGetMethodInherited() {
        cualitativo.setNombre("Attr");
        cualitativo.setPeso(1.5);
        assertEquals("Attr: 1.5", cualitativo.get());
    }

    // --- Qualitative Specific Methods Tests ---

    @Test
    @DisplayName("Should return a list of unique classes")
    void testClases() {
        cualitativo.add("Red");
        cualitativo.add("Blue");
        cualitativo.add("Red");
        cualitativo.add("Green");
        cualitativo.add("Blue");

        List<String> classes = cualitativo.clases();
        assertNotNull(classes);
        assertEquals(3, classes.size());
        assertTrue(classes.containsAll(Arrays.asList("Red", "Blue", "Green")));
        assertFalse(classes.contains("Yellow")); // Ensure no extra classes
    }

    @Test
    @DisplayName("Should return an empty list of classes if no values")
    void testClasesEmpty() {
        List<String> classes = cualitativo.clases();
        assertNotNull(classes);
        assertTrue(classes.isEmpty());
    }

    @Test
    @DisplayName("Should return the correct number of unique classes")
    void testNClases() {
        cualitativo.add("Apple");
        cualitativo.add("Banana");
        cualitativo.add("Apple");
        cualitativo.add("Orange");

        assertEquals(3, cualitativo.nClases());
    }

    @Test
    @DisplayName("Should return 0 for nClases if no values")
    void testNClasesEmpty() {
        assertEquals(0, cualitativo.nClases());
    }

    @Test
    @DisplayName("Should calculate correct frequencies for each class")
    void testFrecuencia() {
        cualitativo.add("A");
        cualitativo.add("B");
        cualitativo.add("A");
        cualitativo.add("C");
        cualitativo.add("A");

        List<Double> frequencies = cualitativo.frecuencia();
        assertNotNull(frequencies);
        assertEquals(3, frequencies.size()); // Frequencies for A, B, C

        // Get classes to match frequencies correctly
        List<String> classes = cualitativo.clases();
        double freqA = 0, freqB = 0, freqC = 0;

        for (int i = 0; i < classes.size(); i++) {
            String className = classes.get(i);
            Double frequency = frequencies.get(i);
            if ("A".equals(className)) {
                freqA = frequency;
            } else if ("B".equals(className)) {
                freqB = frequency;
            } else if ("C".equals(className)) {
                freqC = frequency;
            }
        }

        assertEquals(3.0 / 5.0, freqA, 0.001); // A appears 3 times out of 5
        assertEquals(1.0 / 5.0, freqB, 0.001); // B appears 1 time out of 5
        assertEquals(1.0 / 5.0, freqC, 0.001); // C appears 1 time out of 5

        // Sum of frequencies should be approximately 1.0
        assertEquals(1.0, freqA + freqB + freqC, 0.001);
    }

    @Test
    @DisplayName("Should return empty frequencies list if no values")
    void testFrecuenciaEmpty() {
        List<Double> frequencies = cualitativo.frecuencia();
        assertNotNull(frequencies);
        assertTrue(frequencies.isEmpty());
    }

    // --- Overridden Atributo Methods Tests ---

    @Test
    @DisplayName("Should correctly return size of values")
    void testSizeOverride() {
        assertEquals(0, cualitativo.size());
        cualitativo.add("First");
        assertEquals(1, cualitativo.size());
        cualitativo.add("Second");
        assertEquals(2, cualitativo.size());
    }

    @Test
    @DisplayName("Should add a String value")
    void testAddOverride() {
        cualitativo.add("NewValue");
        assertEquals(1, cualitativo.size());
        assertEquals("NewValue", cualitativo.getValor(0));
    }

    @Test
    @DisplayName("Should throw ClassCastException if non-String object added")
    void testAddOverrideNonString() {
        assertThrows(ClassCastException.class, () -> cualitativo.add(123));
        assertThrows(ClassCastException.class, () -> cualitativo.add(new Object()));
    }

    @Test
    @DisplayName("Should get a String value by index")
    void testGetValorOverride() {
        cualitativo.add("ItemA");
        assertEquals("ItemA", cualitativo.getValor(0));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting value out of bounds")
    void testGetValorOverrideOutOfBounds() {
        cualitativo.add("Single");
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.getValor(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.getValor(1));
    }

    @Test
    @DisplayName("Should delete a value by index")
    void testDeleteOverride() {
        cualitativo.add("One");
        cualitativo.add("Two");
        cualitativo.add("Three");
        assertEquals(3, cualitativo.size());
        cualitativo.delete(1); // Delete "Two"
        assertEquals(2, cualitativo.size());
        assertEquals("One", cualitativo.getValor(0));
        assertEquals("Three", cualitativo.getValor(1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when deleting value out of bounds")
    void testDeleteOverrideOutOfBounds() {
        cualitativo.add("Single");
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.delete(1));
    }

    @Test
    @DisplayName("Should clear all values")
    void testClearOverride() {
        cualitativo.add("X");
        cualitativo.add("Y");
        assertEquals(2, cualitativo.size());
        cualitativo.clear();
        assertEquals(0, cualitativo.size());
        assertTrue(cualitativo.getValores().isEmpty());
    }

    @Test
    @DisplayName("Should return toString representation of values list")
    void testToStringOverride() {
        cualitativo.add("Cat");
        cualitativo.add("Dog");
        assertEquals("[Cat, Dog]", cualitativo.toString());
    }

    @Test
    @DisplayName("Should return empty list string for empty Cualitativo toString")
    void testToStringOverrideEmpty() {
        assertEquals("[]", cualitativo.toString());
    }
}