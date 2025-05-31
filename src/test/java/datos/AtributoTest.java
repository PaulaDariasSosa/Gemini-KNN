package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * \brief Test class for the abstract Atributo class using a concrete DummyAtributo implementation.
 *
 * This class contains unit tests to verify the correct behavior of the
 * `Atributo` abstract class's public methods, specifically focusing on
 * its getters, setters, and abstract methods implemented by `DummyAtributo`.
 */
@DisplayName("Atributo Abstract Class Tests")
class AtributoTest {

    private DummyAtributo atributo; // Use the concrete dummy implementation

    /**
     * \brief Sets up the test environment before each test method is executed.
     *
     * Initializes a new `DummyAtributo` instance with a default name and weight
     * to ensure a clean state for every test.
     */
    @BeforeEach
    void setUp() {
        // Initialize a fresh DummyAtributo before each test
        atributo = new DummyAtributo("InitialName", 5.0);
    }

    // --- Constructor-like (for DummyAtributo) and Basic Getters/Setters Tests ---

    /**
     * \brief Tests if the constructor correctly initializes the name and peso (weight).
     *
     * Verifies that the `DummyAtributo` constructor, which calls the `Atributo`'s
     * protected setters, correctly sets the initial name and peso.
     */
    @Test
    @DisplayName("Should initialize name and peso correctly via constructor")
    void testConstructorInitialization() {
        assertEquals("InitialName", atributo.getNombre());
        assertEquals(5.0, atributo.getPeso());
    }

    /**
     * \brief Tests the `setNombre` and `getNombre` methods.
     *
     * Verifies that the name of the attribute can be correctly set and retrieved.
     */
    @Test
    @DisplayName("Should allow setting and getting the name")
    void testSetAndGetNombre() {
        atributo.setNombre("NewName");
        assertEquals("NewName", atributo.getNombre());
    }

    /**
     * \brief Tests the `setPeso` and `getPeso` methods.
     *
     * Verifies that the weight of the attribute can be correctly set and retrieved.
     */
    @Test
    @DisplayName("Should allow setting and getting the peso")
    void testSetAndGetPeso() {
        atributo.setPeso(10.5);
        assertEquals(10.5, atributo.getPeso());
    }

    /**
     * \brief Tests the default weight assignment in `DummyAtributo`'s constructor.
     *
     * Verifies that if the weight is not explicitly specified during `DummyAtributo`
     * instantiation, it defaults to 1.0.
     */
    @Test
    @DisplayName("Should default peso to 1 if not specified (DummyAtributo constructor)")
    void testDefaultPeso() {
        DummyAtributo defaultAtributo = new DummyAtributo("DefaultTest");
        assertEquals(1.0, defaultAtributo.getPeso());
    }

    /**
     * \brief Tests the `get` method's string formatting.
     *
     * Verifies that the `get` method returns a string in the expected "name: peso" format.
     */
    @Test
    @DisplayName("Should return name and peso in 'get' method format")
    void testGetMethod() {
        assertEquals("InitialName: 5.0", atributo.get());
        atributo.setNombre("Updated");
        atributo.setPeso(2.5);
        assertEquals("Updated: 2.5", atributo.get());
    }

    // --- Abstract Method Implementation Tests (via DummyAtributo) ---

    /**
     * \brief Tests the `add`, `size`, and `getValor` methods.
     *
     * Verifies that values can be added, the size correctly reflects the number of elements,
     * and individual values can be retrieved by index.
     */
    @Test
    @DisplayName("Should correctly add values and reflect in size and getValor")
    void testAddAndSizeAndGetValor() {
        assertEquals(0, atributo.size());
        atributo.add("Value1");
        assertEquals(1, atributo.size());
        assertEquals("Value1", atributo.getValor(0));

        atributo.add(123);
        assertEquals(2, atributo.size());
        assertEquals(123, atributo.getValor(1));
    }

    /**
     * \brief Tests for `IndexOutOfBoundsException` when calling `getValor` with invalid indices.
     *
     * Verifies that `getValor` throws the expected exception for negative or out-of-bounds indices.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException for invalid index in getValor")
    void testGetValorOutOfBounds() {
        atributo.add("A");
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.getValor(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.getValor(1)); // Size is 1, index 1 is out of bounds
    }

    /**
     * \brief Tests the `delete` method's functionality.
     *
     * Verifies that values can be correctly deleted from the attribute's list and
     * the size is updated accordingly. Also checks that subsequent elements shift correctly.
     */
    @Test
    @DisplayName("Should correctly delete values and update size")
    void testDelete() {
        atributo.add("Value1");
        atributo.add("Value2");
        atributo.add("Value3");
        assertEquals(3, atributo.size());

        atributo.delete(1); // Delete "Value2"
        assertEquals(2, atributo.size());
        assertEquals("Value1", atributo.getValor(0));
        assertEquals("Value3", atributo.getValor(1)); // "Value3" shifted
    }

    /**
     * \brief Tests for `IndexOutOfBoundsException` when calling `delete` with invalid indices.
     *
     * Verifies that `delete` throws the expected exception for negative or out-of-bounds indices.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException for invalid index in delete")
    void testDeleteOutOfBounds() {
        atributo.add("SingleValue");
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.delete(1)); // Size is 1, index 1 is out of bounds
    }

    /**
     * \brief Tests the `clear` method's functionality.
     *
     * Verifies that all values are removed from the attribute and its size becomes zero.
     */
    @Test
    @DisplayName("Should clear all values and set size to 0")
    void testClear() {
        atributo.add("A");
        atributo.add("B");
        assertEquals(2, atributo.size());
        atributo.clear();
        assertEquals(0, atributo.size());
        assertTrue(((List<?>) atributo.getValores()).isEmpty()); // Verify the underlying list is empty
    }

    /**
     * \brief Tests that `getValores` returns a defensive copy of the internal list.
     *
     * Verifies that modifying the list returned by `getValores` does not affect
     * the internal state of the `Atributo` instance.
     */
    @Test
    @DisplayName("Should return a copy of internal values in getValores")
    void testGetValoresReturnsCopy() {
        atributo.add("Item1");
        atributo.add("Item2");

        List<Object> retrievedValues = (List<Object>) atributo.getValores();
        assertNotNull(retrievedValues);
        assertEquals(2, retrievedValues.size());
        assertEquals("Item1", retrievedValues.get(0));

        // Modify the retrieved list - original attribute's values should remain unchanged
        retrievedValues.add("NewItem");
        assertEquals(3, retrievedValues.size());
        assertEquals(2, atributo.size()); // Original size should be unchanged
    }

    /**
     * \brief Tests the `toString` method's output.
     *
     * Verifies that the `toString` method provides a descriptive string representation
     * of the `DummyAtributo` instance, including its name, peso, and current values.
     */
    @Test
    @DisplayName("Should return a descriptive string representation")
    void testToString() {
        atributo.add("TestVal");
        String expectedString = "DummyAtributo [name=InitialName, peso=5.0, values=[TestVal]]";
        assertEquals(expectedString, atributo.toString());
    }
}