package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Atributo Abstract Class Tests")
class AtributoTest {

    private DummyAtributo atributo; // Use the concrete dummy implementation

    @BeforeEach
    void setUp() {
        // Initialize a fresh DummyAtributo before each test
        atributo = new DummyAtributo("InitialName", 5.0);
    }

    // --- Constructor-like (for DummyAtributo) and Basic Getters/Setters Tests ---

    @Test
    @DisplayName("Should initialize name and peso correctly via constructor")
    void testConstructorInitialization() {
        assertEquals("InitialName", atributo.getNombre());
        assertEquals(5.0, atributo.getPeso());
    }

    @Test
    @DisplayName("Should allow setting and getting the name")
    void testSetAndGetNombre() {
        atributo.setNombre("NewName");
        assertEquals("NewName", atributo.getNombre());
    }

    @Test
    @DisplayName("Should allow setting and getting the peso")
    void testSetAndGetPeso() {
        atributo.setPeso(10.5);
        assertEquals(10.5, atributo.getPeso());
    }

    @Test
    @DisplayName("Should default peso to 1 if not specified (DummyAtributo constructor)")
    void testDefaultPeso() {
        DummyAtributo defaultAtributo = new DummyAtributo("DefaultTest");
        assertEquals(1.0, defaultAtributo.getPeso());
    }

    @Test
    @DisplayName("Should return name and peso in 'get' method format")
    void testGetMethod() {
        assertEquals("InitialName: 5.0", atributo.get());
        atributo.setNombre("Updated");
        atributo.setPeso(2.5);
        assertEquals("Updated: 2.5", atributo.get());
    }

    // --- Abstract Method Implementation Tests (via DummyAtributo) ---

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

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException for invalid index in getValor")
    void testGetValorOutOfBounds() {
        atributo.add("A");
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.getValor(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.getValor(1)); // Size is 1, index 1 is out of bounds
    }

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

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException for invalid index in delete")
    void testDeleteOutOfBounds() {
        atributo.add("SingleValue");
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> atributo.delete(1)); // Size is 1, index 1 is out of bounds
    }

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

    @Test
    @DisplayName("Should return a descriptive string representation")
    void testToString() {
        atributo.add("TestVal");
        String expectedString = "DummyAtributo [name=InitialName, peso=5.0, values=[TestVal]]";
        assertEquals(expectedString, atributo.toString());
    }
}
