package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * \brief Test class for the Cualitativo attribute type.
 *
 * This class contains unit tests to verify the correct behavior of the
 * `Cualitativo` class, including its constructors, inherited methods from `Atributo`,
 * and its specific methods like `clases()`, `nClases()`, and `frecuencia()`.
 */
@DisplayName("Cualitativo Class Tests")
class CualitativoTest {

    private Cualitativo cualitativo;

    /**
     * \brief Sets up the test environment before each test method is executed.
     *
     * Initializes a new `Cualitativo` instance to ensure a clean state for every test.
     */
    @BeforeEach
    void setUp() {
        // Initialize a fresh Cualitativo instance before each test
        cualitativo = new Cualitativo();
    }

    // --- Constructor Tests ---

    /**
     * \brief Tests the no-argument constructor of `Cualitativo`.
     *
     * Verifies that the `Cualitativo` instance is correctly initialized with a
     * default empty name, default peso of 1.0 (inherited from Atributo), and an empty list of values.
     */
    @Test
    @DisplayName("Should create an empty Cualitativo with no-arg constructor")
    void testConstructorEmpty() {
        assertNotNull(cualitativo);
        assertEquals("", cualitativo.getNombre());
        assertEquals(1.0, cualitativo.getPeso()); // Default peso from Atributo
        assertTrue(cualitativo.getValores().isEmpty());
    }

    /**
     * \brief Tests the constructor that takes a name.
     *
     * Verifies that the `Cualitativo` instance is correctly initialized with the
     * provided name, default peso, and an empty list of values.
     */
    @Test
    @DisplayName("Should create Cualitativo with name constructor")
    void testConstructorWithName() {
        Cualitativo namedCualitativo = new Cualitativo("Color");
        assertNotNull(namedCualitativo);
        assertEquals("Color", namedCualitativo.getNombre());
        assertEquals(1.0, namedCualitativo.getPeso());
        assertTrue(namedCualitativo.getValores().isEmpty());
    }

    /**
     * \brief Tests the constructor that takes a name and a single value.
     *
     * Verifies that the `Cualitativo` instance is initialized with the specified
     * name and the single value added to its list.
     */
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

    /**
     * \brief Tests the constructor that takes a name and a list of values.
     *
     * Verifies that the `Cualitativo` instance is initialized with the provided
     * name and the list of values. It also checks for shallow copy behavior.
     */
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

    /**
     * \brief Tests the copy constructor of `Cualitativo`.
     *
     * Verifies that a new `Cualitativo` instance is created with the same name and
     * peso as the original, but with an independent (empty) list of values.
     */
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

    /**
     * \brief Tests the `getValores` method.
     *
     * Verifies that `getValores` returns a reference to the internal list of values,
     * exhibiting shallow copy behavior (changes to the returned list will affect the internal state).
     */
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

    /**
     * \brief Tests the `setValores` method.
     *
     * Verifies that `setValores` correctly replaces the internal list of values
     * with a new one and maintains shallow copy behavior.
     */
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

    /**
     * \brief Tests the inherited `getNombre` method from `Atributo`.
     *
     * Verifies that the name of the attribute can be correctly retrieved.
     */
    @Test
    @DisplayName("Should correctly return the name (inherited)")
    void testGetNombreInherited() {
        cualitativo.setNombre("TestName");
        assertEquals("TestName", cualitativo.getNombre());
    }

    /**
     * \brief Tests the inherited `getPeso` method from `Atributo`.
     *
     * Verifies that the weight of the attribute can be correctly retrieved.
     */
    @Test
    @DisplayName("Should correctly return the peso (inherited)")
    void testGetPesoInherited() {
        cualitativo.setPeso(3.0);
        assertEquals(3.0, cualitativo.getPeso());
    }

    /**
     * \brief Tests the inherited `get` method from `Atributo`.
     *
     * Verifies that the `get` method returns a string in the expected "name: peso" format.
     */
    @Test
    @DisplayName("Should return name and peso in 'get' method format (inherited)")
    void testGetMethodInherited() {
        cualitativo.setNombre("Attr");
        cualitativo.setPeso(1.5);
        assertEquals("Attr: 1.5", cualitativo.get());
    }

    // --- Qualitative Specific Methods Tests ---

    /**
     * \brief Tests the `clases` method.
     *
     * Verifies that `clases` returns a list containing only the unique values
     * (classes) present in the attribute's data.
     */
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

    /**
     * \brief Tests `clases` method when the attribute has no values.
     *
     * Verifies that `clases` returns an empty list if the attribute contains no data.
     */
    @Test
    @DisplayName("Should return an empty list of classes if no values")
    void testClasesEmpty() {
        List<String> classes = cualitativo.clases();
        assertNotNull(classes);
        assertTrue(classes.isEmpty());
    }

    /**
     * \brief Tests the `nClases` method.
     *
     * Verifies that `nClases` returns the correct count of unique classes.
     */
    @Test
    @DisplayName("Should return the correct number of unique classes")
    void testNClases() {
        cualitativo.add("Apple");
        cualitativo.add("Banana");
        cualitativo.add("Apple");
        cualitativo.add("Orange");

        assertEquals(3, cualitativo.nClases());
    }

    /**
     * \brief Tests `nClases` method when the attribute has no values.
     *
     * Verifies that `nClases` returns 0 if the attribute contains no data.
     */
    @Test
    @DisplayName("Should return 0 for nClases if no values")
    void testNClasesEmpty() {
        assertEquals(0, cualitativo.nClases());
    }

    /**
     * \brief Tests the `frecuencia` method.
     *
     * Verifies that `frecuencia` correctly calculates the relative frequency
     * of each unique class in the attribute's values.
     */
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

    /**
     * \brief Tests `frecuencia` method when the attribute has no values.
     *
     * Verifies that `frecuencia` returns an empty list if the attribute contains no data.
     */
    @Test
    @DisplayName("Should return empty frequencies list if no values")
    void testFrecuenciaEmpty() {
        List<Double> frequencies = cualitativo.frecuencia();
        assertNotNull(frequencies);
        assertTrue(frequencies.isEmpty());
    }

    // --- Overridden Atributo Methods Tests ---

    /**
     * \brief Tests the overridden `size` method.
     *
     * Verifies that `size` correctly returns the number of values stored.
     */
    @Test
    @DisplayName("Should correctly return size of values")
    void testSizeOverride() {
        assertEquals(0, cualitativo.size());
        cualitativo.add("First");
        assertEquals(1, cualitativo.size());
        cualitativo.add("Second");
        assertEquals(2, cualitativo.size());
    }

    /**
     * \brief Tests the overridden `add` method for String values.
     *
     * Verifies that a String value can be successfully added to the attribute.
     */
    @Test
    @DisplayName("Should add a String value")
    void testAddOverride() {
        cualitativo.add("NewValue");
        assertEquals(1, cualitativo.size());
        assertEquals("NewValue", cualitativo.getValor(0));
    }

    /**
     * \brief Tests `add` method's behavior with non-String objects.
     *
     * Verifies that `add` throws a `ClassCastException` if an object that is not
     * a String is attempted to be added, as `Cualitativo` expects String values.
     */
    @Test
    @DisplayName("Should throw ClassCastException if non-String object added")
    void testAddOverrideNonString() {
        assertThrows(ClassCastException.class, () -> cualitativo.add(123));
        assertThrows(ClassCastException.class, () -> cualitativo.add(new Object()));
    }

    /**
     * \brief Tests the overridden `getValor` method.
     *
     * Verifies that a String value can be correctly retrieved by its index.
     */
    @Test
    @DisplayName("Should get a String value by index")
    void testGetValorOverride() {
        cualitativo.add("ItemA");
        assertEquals("ItemA", cualitativo.getValor(0));
    }

    /**
     * \brief Tests `getValor` method's behavior with invalid indices.
     *
     * Verifies that `getValor` throws an `IndexOutOfBoundsException` for
     * negative or out-of-bounds indices.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting value out of bounds")
    void testGetValorOverrideOutOfBounds() {
        cualitativo.add("Single");
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.getValor(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.getValor(1));
    }

    /**
     * \brief Tests the overridden `delete` method.
     *
     * Verifies that a value can be correctly deleted by its index and that
     * subsequent elements shift accordingly.
     */
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

    /**
     * \brief Tests `delete` method's behavior with invalid indices.
     *
     * Verifies that `delete` throws an `IndexOutOfBoundsException` for
     * negative or out-of-bounds indices.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when deleting value out of bounds")
    void testDeleteOverrideOutOfBounds() {
        cualitativo.add("Single");
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cualitativo.delete(1));
    }

    /**
     * \brief Tests the overridden `clear` method.
     *
     * Verifies that `clear` removes all values from the attribute, resulting in an empty list.
     */
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

    /**
     * \brief Tests the overridden `toString` method.
     *
     * Verifies that `toString` returns a string representation of the internal
     * list of values, as defined by `ArrayList`'s `toString`.
     */
    @Test
    @DisplayName("Should return toString representation of values list")
    void testToStringOverride() {
        cualitativo.add("Cat");
        cualitativo.add("Dog");
        assertEquals("[Cat, Dog]", cualitativo.toString());
    }

    /**
     * \brief Tests `toString` method for an empty `Cualitativo` instance.
     *
     * Verifies that `toString` returns "[]" when the attribute has no values.
     */
    @Test
    @DisplayName("Should return empty list string for empty Cualitativo toString")
    void testToStringOverrideEmpty() {
        assertEquals("[]", cualitativo.toString());
    }
}