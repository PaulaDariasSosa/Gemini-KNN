package vectores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Unit tests for the Vector class.
 *
 * This class contains a comprehensive set of unit tests for the {@link Vector} class,
 * covering its constructors, basic operations, mathematical operations, utility methods,
 * and file I/O functionalities. It uses JUnit 5 for testing and Mockito for mocking
 * (though Mockito is commented out as it's not fully utilized in the provided snippet).
 */
@DisplayName("Vector Class Tests")
class VectorTest {

    /**
     * @brief Temporary directory for file I/O tests.
     *
     * This field is annotated with {@code @TempDir} from JUnit 5, which provides a
     * temporary directory for file-based tests, ensuring that test files are
     * created and cleaned up properly.
     */
    @TempDir
    Path tempDir;

    /**
     * @brief Set up method executed before each test.
     *
     * This method is annotated with {@code @BeforeEach} and is intended for
     * common setup tasks that need to be performed before every test method.
     * Currently, it contains commented-out code for Mockito initialization and
     * a note about logging.
     */
    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);
        // If you need to mock the logger to assert log messages,
        // you'd typically set it up here or use a custom appender for Logback/Log4j
        // For basic coverage, we just let the actual logger run.
    }

    // --- Constructor Tests ---

    /**
     * @brief Tests the no-argument constructor of the Vector class.
     *
     * Verifies that the {@link Vector#Vector()} constructor correctly
     * creates an empty vector with a size of 0.
     */
    @Test
    @DisplayName("Should create an empty vector with no-arg constructor")
    void testConstructorEmpty() {
        Vector vector = new Vector();
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests the constructor that takes a double array.
     *
     * Verifies that the {@link Vector#Vector(double[])} constructor correctly
     * initializes the vector with the elements from the provided double array.
     */
    @Test
    @DisplayName("Should create a vector from a double array")
    void testConstructorDoubleArray() {
        double[] array = {1.0, 2.0, 3.0};
        Vector vector = new Vector(array);
        assertEquals(3, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1));
        assertEquals(3.0, vector.get(2));
    }

    /**
     * @brief Tests the constructor with a null double array.
     *
     * Verifies that the {@link Vector#Vector(double[])} constructor handles
     * a null double array gracefully by creating an empty vector without throwing
     * an exception.
     */
    @Test
    @DisplayName("Should handle null double array in constructor gracefully")
    void testConstructorNullDoubleArray() {
        // Logging a warning is acceptable behavior, so we just check for no exception
        // and an empty vector.
        Vector vector = new Vector((double[]) null);
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests the constructor that takes a List of Doubles.
     *
     * Verifies that the {@link Vector#Vector(List)} constructor correctly
     * initializes the vector with the elements from the provided List of Doubles.
     */
    @Test
    @DisplayName("Should create a vector from a List<Double>")
    void testConstructorListDouble() {
        List<Double> list = Arrays.asList(4.0, 5.0, 6.0);
        Vector vector = new Vector(list);
        assertEquals(3, vector.size());
        assertEquals(4.0, vector.get(0));
        assertEquals(5.0, vector.get(1));
        assertEquals(6.0, vector.get(2));
    }

    /**
     * @brief Tests the constructor with a null List of Doubles.
     *
     * Verifies that the {@link Vector#Vector(List)} constructor handles a
     * null List of Doubles gracefully by creating an empty vector without
     * throwing an exception.
     */
    @Test
    @DisplayName("Should handle null List<Double> in constructor gracefully")
    void testConstructorNullListDouble() {
        Vector vector = new Vector((List<Double>) null);
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests the constructor that takes an integer size.
     *
     * Verifies that the {@link Vector#Vector(int)} constructor creates a
     * vector of the specified size, initialized with zero values.
     */
    @Test
    @DisplayName("Should create a vector of specified size initialized with zeros")
    void testConstructorSize() {
        Vector vector = new Vector(5);
        assertEquals(5, vector.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(0.0, vector.get(i));
        }
    }

    /**
     * @brief Tests the constructor with a negative size.
     *
     * Verifies that the {@link Vector#Vector(int)} constructor throws an
     * {@link IllegalArgumentException} when provided with a negative size.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for negative size in constructor")
    void testConstructorNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(-1));
    }

    /**
     * @brief Tests the constructor that reads from a file using a Scanner.
     *
     * Verifies that the {@link Vector#Vector(File)} constructor correctly
     * reads double values from a file and initializes the vector.
     * @throws IOException If an I/O error occurs during file operations.
     */
    @Test
    @DisplayName("Should create a vector by reading from a file with Scanner constructor")
    void testConstructorFileScanner() throws IOException {
        File tempFile = tempDir.resolve("test_vector_scanner.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("10.0\n20.5\n30.0\n");
        }
        Vector vector = new Vector(tempFile);
        assertEquals(3, vector.size());
        assertEquals(10.0, vector.get(0));
        assertEquals(20.5, vector.get(1));
        assertEquals(30.0, vector.get(2));
    }

    /**
     * @brief Tests the constructor with a null File object.
     *
     * Verifies that the {@link Vector#Vector(File)} constructor throws an
     * {@link IllegalArgumentException} when provided with a null File object.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null file in Scanner constructor")
    void testConstructorNullFileScanner() {
        assertThrows(IllegalArgumentException.class, () -> new Vector((File) null));
    }

    /**
     * @brief Tests the constructor when the file for Scanner does not exist.
     *
     * Verifies that the {@link Vector#Vector(File)} constructor throws a
     * {@link FileNotFoundException} when the specified file does not exist.
     */
    @Test
    @DisplayName("Should throw FileNotFoundException if file for Scanner constructor does not exist")
    void testConstructorFileNotFoundScanner() {
        File nonExistentFile = tempDir.resolve("non_existent_file.txt").toFile();
        assertThrows(FileNotFoundException.class, () -> new Vector(nonExistentFile));
    }

    /**
     * @brief Tests the constructor that takes a comma-separated String.
     *
     * Verifies that the {@link Vector#Vector(String)} constructor correctly
     * parses a comma-separated string of double values and initializes the vector.
     */
    @Test
    @DisplayName("Should create a vector from a comma-separated String")
    void testConstructorString() {
        Vector vector = new Vector("1.1,2.2,3.3");
        assertEquals(3, vector.size());
        assertEquals(1.1, vector.get(0));
        assertEquals(2.2, vector.get(1));
        assertEquals(3.3, vector.get(2));
    }

    /**
     * @brief Tests the constructor with an empty string.
     *
     * Verifies that the {@link Vector#Vector(String)} constructor handles
     * an empty string by creating an empty vector.
     */
    @Test
    @DisplayName("Should handle empty string in constructor")
    void testConstructorEmptyString() {
        Vector vector = new Vector("");
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests the constructor with a null string.
     *
     * Verifies that the {@link Vector#Vector(String)} constructor handles a
     * null string gracefully by creating an empty vector without throwing
     * an exception.
     */
    @Test
    @DisplayName("Should handle null string in constructor gracefully")
    void testConstructorNullString() {
        Vector vector = new Vector((String) null);
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests the constructor with an invalid string format.
     *
     * Verifies that the {@link Vector#Vector(String)} constructor throws a
     * {@link NumberFormatException} when the input string contains non-numeric
     * values that cannot be parsed as doubles.
     */
    @Test
    @DisplayName("Should throw NumberFormatException for invalid string format in constructor")
    void testConstructorInvalidStringFormat() {
        assertThrows(NumberFormatException.class, () -> new Vector("1.0,abc,3.0"));
    }

    /**
     * @brief Tests the copy constructor.
     *
     * Verifies that the {@link Vector#Vector(Vector)} copy constructor
     * creates a deep copy of an existing vector, ensuring that modifications
     * to the copy do not affect the original.
     */
    @Test
    @DisplayName("Should clone a vector using copy constructor")
    void testConstructorCopy() {
        Vector original = new Vector(new double[]{1.0, 2.0, 3.0});
        Vector copy = new Vector(original);
        assertEquals(original.size(), copy.size());
        assertEquals(original.get(0), copy.get(0));
        assertNotSame(original, copy, "Should be a deep copy, not the same instance");
    }

    /**
     * @brief Tests the copy constructor with a null vector.
     *
     * Verifies that the {@link Vector#Vector(Vector)} copy constructor handles
     * a null input vector gracefully by creating an empty vector without
     * throwing an exception.
     */
    @Test
    @DisplayName("Should handle null vector in copy constructor gracefully")
    void testConstructorCopyNull() {
        Vector copy = new Vector((Vector) null);
        assertNotNull(copy);
        assertEquals(0, copy.size());
    }

    // --- Basic Operations Tests ---

    /**
     * @brief Tests the {@link Vector#size()} method.
     *
     * Verifies that the {@code size()} method correctly returns the number of
     * elements in the vector after additions and clearing.
     */
    @Test
    @DisplayName("Should return correct size")
    void testSize() {
        Vector vector = new Vector(Arrays.asList(1.0, 2.0));
        assertEquals(2, vector.size());
        vector.add(3.0);
        assertEquals(3, vector.size());
        vector.clear();
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests the {@link Vector#clear()} method.
     *
     * Verifies that the {@code clear()} method removes all elements from the
     * vector, resulting in an empty vector.
     */
    @Test
    @DisplayName("Should clear the vector")
    void testClear() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertFalse(vector.coef.isEmpty()); // Accessing coef for verification
        vector.clear();
        assertTrue(vector.coef.isEmpty());
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests the {@link Vector#toString()} method.
     *
     * Verifies that the {@code toString()} method returns the correct
     * string representation of the vector.
     */
    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertEquals("[1.0, 2.0]", vector.toString());
        Vector emptyVector = new Vector();
        assertEquals("[]", emptyVector.toString());
    }

    /**
     * @brief Tests the {@link Vector#print()} method.
     *
     * This test primarily ensures that the {@code print()} method
     * executes without throwing any errors, as directly asserting on
     * logging output would require mocking frameworks.
     */
    @Test
    @DisplayName("Should print vector to info logger")
    void testPrint() {
        Logger actualLogger = LoggerFactory.getLogger(Vector.class);
        // We can't directly assert on logger.info calls without a custom appender.
        // This test primarily checks that the method runs without error.
        // If testing logging is critical, Mockito or a test appender would be needed.
        Vector vector = new Vector(new double[]{1.0, 2.0});
        vector.print();
        // No direct assertion, as it relies on logging side effect.
        // You would typically use a mocking framework like Mockito if you needed to verify logger interactions.
    }


    /**
     * @brief Tests the {@link Vector#get(int)} method with a valid index.
     *
     * Verifies that the {@code get()} method returns the correct value
     * at a specified valid index.
     */
    @Test
    @DisplayName("Should get value at valid index")
    void testGetValidIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        assertEquals(2.0, vector.get(1));
    }

    /**
     * @brief Tests the {@link Vector#get(int)} method with a negative index.
     *
     * Verifies that the {@code get()} method throws an {@link IndexOutOfBoundsException}
     * when provided with a negative index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting value at negative index")
    void testGetNegativeIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(-1));
    }

    /**
     * @brief Tests the {@link Vector#get(int)} method with an out-of-bounds index.
     *
     * Verifies that the {@code get()} method throws an {@link IndexOutOfBoundsException}
     * when provided with an index that is greater than or equal to the vector's size.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting value at out-of-bounds index")
    void testGetOutOfBoundsIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(2));
    }

    /**
     * @brief Tests the {@link Vector#set(int, double)} method with a valid index.
     *
     * Verifies that the {@code set()} method correctly updates the value
     * at a specified valid index.
     */
    @Test
    @DisplayName("Should set value at valid index")
    void testSetValidIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        vector.set(1, 2.5);
        assertEquals(2.5, vector.get(1));
    }

    /**
     * @brief Tests the {@link Vector#set(int, double)} method with a negative index.
     *
     * Verifies that the {@code set()} method throws an {@link IndexOutOfBoundsException}
     * when provided with a negative index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting value at negative index")
    void testSetNegativeIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.set(-1, 0.0));
    }

    /**
     * @brief Tests the {@link Vector#set(int, double)} method with an out-of-bounds index.
     *
     * Verifies that the {@code set()} method throws an {@link IndexOutOfBoundsException}
     * when provided with an index that is greater than or equal to the vector's size.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting value at out-of-bounds index")
    void testSetOutOfBoundsIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.set(2, 0.0));
    }

    /**
     * @brief Tests the {@link Vector#add(double)} method.
     *
     * Verifies that the {@code add()} method correctly adds a double value
     * to the end of the vector.
     */
    @Test
    @DisplayName("Should add a double value to the vector")
    void testAddDouble() {
        Vector vector = new Vector();
        vector.add(1.0);
        assertEquals(1, vector.size());
        assertEquals(1.0, vector.get(0));
        vector.add(2.0);
        assertEquals(2, vector.size());
        assertEquals(2.0, vector.get(1));
    }

    /**
     * @brief Tests the {@link Vector#add(Vector)} method.
     *
     * Verifies that the {@code add()} method correctly adds another vector
     * element-wise to the current vector.
     */
    @Test
    @DisplayName("Should add another vector element-wise")
    void testAddVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0, 4.0});
        v1.add(v2);
        assertEquals(2, v1.size());
        assertEquals(4.0, v1.get(0));
        assertEquals(6.0, v1.get(1));
    }

    /**
     * @brief Tests the {@link Vector#add(Vector)} method with a null vector.
     *
     * Verifies that the {@code add()} method throws an {@link IllegalArgumentException}
     * when attempting to add a null vector.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException when adding a null vector")
    void testAddNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertThrows(IllegalArgumentException.class, () -> v1.add((Vector) null));
    }

    /**
     * @brief Tests the {@link Vector#add(Vector)} method with vectors of different sizes.
     *
     * Verifies that the {@code add()} method throws an {@link IllegalArgumentException}
     * when attempting to add vectors with different sizes.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException when adding vectors of different sizes")
    void testAddVectorDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertThrows(IllegalArgumentException.class, () -> v1.add(v2));
    }

    /**
     * @brief Tests the {@link Vector#remove(int)} method with a valid index.
     *
     * Verifies that the {@code remove()} method correctly removes the element
     * at the specified valid index.
     */
    @Test
    @DisplayName("Should remove element at valid index")
    void testRemoveValidIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        vector.remove(1);
        assertEquals(2, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(3.0, vector.get(1));
    }

    /**
     * @brief Tests the {@link Vector#remove(int)} method with a negative index.
     *
     * Verifies that the {@code remove()} method throws an {@link IndexOutOfBoundsException}
     * when provided with a negative index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when removing at negative index")
    void testRemoveNegativeIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.remove(-1));
    }

    /**
     * @brief Tests the {@link Vector#remove(int)} method with an out-of-bounds index.
     *
     * Verifies that the {@code remove()} method throws an {@link IndexOutOfBoundsException}
     * when provided with an index that is greater than or equal to the vector's size.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when removing at out-of-bounds index")
    void testRemoveOutOfBoundsIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.remove(2));
    }

    // --- Mathematical Operations Tests ---

    /**
     * @brief Tests the {@link Vector#getMax()} method.
     *
     * Verifies that the {@code getMax()} method correctly returns the maximum
     * value present in the vector.
     */
    @Test
    @DisplayName("Should return the maximum value in the vector")
    void testGetMax() {
        Vector vector = new Vector(new double[]{10.0, 5.0, 20.0, 8.0});
        assertEquals(20.0, vector.getMax());
    }

    /**
     * @brief Tests the {@link Vector#getMax()} method with a single-element vector.
     *
     * Verifies that the {@code getMax()} method correctly returns the value
     * for a vector containing only one element.
     */
    @Test
    @DisplayName("Should handle single element vector for getMax")
    void testGetMaxSingleElement() {
        Vector vector = new Vector(new double[]{7.0});
        assertEquals(7.0, vector.getMax());
    }

    /**
     * @brief Tests the {@link Vector#getMax()} method with an empty vector.
     *
     * Verifies that the {@code getMax()} method throws an {@link IllegalStateException}
     * when attempting to find the maximum value in an empty vector.
     */
    @Test
    @DisplayName("Should throw IllegalStateException when getting max from an empty vector")
    void testGetMaxEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::getMax);
    }

    /**
     * @brief Tests the {@link Vector#getMaxInt()} method.
     *
     * Verifies that the {@code getMaxInt()} method correctly returns the index
     * of the maximum value in the vector. If multiple maximum values exist,
     * it should return the index of the first occurrence.
     */
    @Test
    @DisplayName("Should return the index of the maximum value")
    void testGetMaxInt() {
        Vector vector = new Vector(new double[]{10.0, 5.0, 20.0, 8.0});
        assertEquals(2, vector.getMaxInt()); // 20.0 is at index 2
    }

    /**
     * @brief Tests the {@link Vector#getMaxInt()} method with multiple maximum values.
     *
     * Verifies that the {@code getMaxInt()} method returns the index of the
     * first occurrence of the maximum value when multiple elements have the
     * same maximum value.
     */
    @Test
    @DisplayName("Should handle multiple maximum values for getMaxInt (first occurrence)")
    void testGetMaxIntMultipleMax() {
        Vector vector = new Vector(new double[]{10.0, 20.0, 20.0, 8.0});
        assertEquals(1, vector.getMaxInt()); // Should return the index of the first 20.0
    }

    /**
     * @brief Tests the {@link Vector#getMaxInt()} method with an empty vector.
     *
     * Verifies that the {@code getMaxInt()} method throws an {@link IllegalStateException}
     * when attempting to find the index of the maximum value in an empty vector.
     */
    @Test
    @DisplayName("Should throw IllegalStateException when getting max index from an empty vector")
    void testGetMaxIntEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::getMaxInt);
    }

    /**
     * @brief Tests the {@link Vector#getMin()} method.
     *
     * Verifies that the {@code getMin()} method correctly returns the minimum
     * value present in the vector.
     */
    @Test
    @DisplayName("Should return the minimum value in the vector")
    void testGetMin() {
        Vector vector = new Vector(new double[]{10.0, 5.0, 20.0, 2.0});
        assertEquals(2.0, vector.getMin());
    }

    /**
     * @brief Tests the {@link Vector#getMin()} method with a single-element vector.
     *
     * Verifies that the {@code getMin()} method correctly returns the value
     * for a vector containing only one element.
     */
    @Test
    @DisplayName("Should handle single element vector for getMin")
    void testGetMinSingleElement() {
        Vector vector = new Vector(new double[]{3.0});
        assertEquals(3.0, vector.getMin());
    }

    /**
     * @brief Tests the {@link Vector#getMin()} method with an empty vector.
     *
     * Verifies that the {@code getMin()} method throws an {@link IllegalStateException}
     * when attempting to find the minimum value in an empty vector.
     */
    @Test
    @DisplayName("Should throw IllegalStateException when getting min from an empty vector")
    void testGetMinEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::getMin);
    }

    /**
     * @brief Tests the {@link Vector#productoEscalar(Vector)} method.
     *
     * Verifies that the {@code productoEscalar()} method correctly calculates
     * the dot product of two vectors.
     */
    @Test
    @DisplayName("Should calculate the dot product correctly")
    void testProductoEscalar() {
        Vector v1 = new Vector(new double[]{1.0, 2.0, 3.0});
        Vector v2 = new Vector(new double[]{4.0, 5.0, 6.0});
        assertEquals(32.0, v1.productoEscalar(v2)); // (1*4) + (2*5) + (3*6) = 4 + 10 + 18 = 32
    }

    /**
     * @brief Tests the {@link Vector#productoEscalar(Vector)} method with a null vector.
     *
     * Verifies that the {@code productoEscalar()} method throws an
     * {@link IllegalArgumentException} when provided with a null vector.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null vector in productoEscalar")
    void testProductoEscalarNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertThrows(IllegalArgumentException.class, () -> v1.productoEscalar(null));
    }

    /**
     * @brief Tests the {@link Vector#productoEscalar(Vector)} method with vectors of different sizes.
     *
     * Verifies that the {@code productoEscalar()} method throws an
     * {@link IllegalArgumentException} when provided with vectors of different sizes.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for different sizes in productoEscalar")
    void testProductoEscalarDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertThrows(IllegalArgumentException.class, () -> v1.productoEscalar(v2));
    }

    /**
     * @brief Tests the {@link Vector#sum(double)} method.
     *
     * Verifies that the {@code sum()} method correctly adds a scalar value
     * to each element of the vector and returns a new vector.
     */
    @Test
    @DisplayName("Should sum a scalar value to each element")
    void testSumScalar() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        Vector result = vector.sum(5.0);
        assertEquals(3, result.size());
        assertEquals(6.0, result.get(0));
        assertEquals(7.0, result.get(1));
        assertEquals(8.0, result.get(2));
        // Ensure original vector is not modified
        assertEquals(1.0, vector.get(0));
    }

    /**
     * @brief Tests the {@link Vector#sum(Vector)} method.
     *
     * Verifies that the {@code sum()} method correctly adds two vectors
     * element-wise and returns a new vector.
     */
    @Test
    @DisplayName("Should sum two vectors element-wise and return new vector")
    void testSumVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0, 4.0});
        Vector result = v1.sum(v2);
        assertEquals(2, result.size());
        assertEquals(4.0, result.get(0));
        assertEquals(6.0, result.get(1));
        // Ensure original vectors are not modified
        assertEquals(1.0, v1.get(0));
        assertEquals(3.0, v2.get(0));
    }

    /**
     * @brief Tests the {@link Vector#sum(Vector)} method with a null vector.
     *
     * Verifies that the {@code sum()} method throws an {@link IllegalArgumentException}
     * when attempting to sum with a null vector.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null vector in sum(Vector)")
    void testSumNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertThrows(IllegalArgumentException.class, () -> v1.sum((Vector) null));
    }

    /**
     * @brief Tests the {@link Vector#sum(Vector)} method with vectors of different sizes.
     *
     * Verifies that the {@code sum()} method throws an {@link IllegalArgumentException}
     * when attempting to sum vectors with different sizes.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for different sizes in sum(Vector)")
    void testSumVectorDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertThrows(IllegalArgumentException.class, () -> v1.sum(v2));
    }

    /**
     * @brief Tests the {@link Vector#module()} method.
     *
     * Verifies that the {@code module()} method correctly calculates the
     * Euclidean norm (magnitude) of the vector.
     */
    @Test
    @DisplayName("Should calculate the module of the vector")
    void testModule() {
        Vector vector = new Vector(new double[]{3.0, 4.0});
        assertEquals(5.0, vector.module(), 0.001); // sqrt(3^2 + 4^2) = sqrt(9 + 16) = sqrt(25) = 5
    }

    /**
     * @brief Tests the {@link Vector#module()} method with an empty vector.
     *
     * Verifies that the {@code module()} method returns 0.0 for an empty vector.
     */
    @Test
    @DisplayName("Should return 0 for the module of an empty vector")
    void testModuleEmptyVector() {
        Vector vector = new Vector();
        assertEquals(0.0, vector.module());
    }

    /**
     * @brief Tests the {@link Vector#multiply(double)} method.
     *
     * Verifies that the {@code multiply()} method correctly multiplies
     * each element of the vector by a scalar value.
     */
    @Test
    @DisplayName("Should multiply each element by a scalar")
    void testMultiplyScalar() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        vector.multiply(2.0);
        assertEquals(2.0, vector.get(0));
        assertEquals(4.0, vector.get(1));
        assertEquals(6.0, vector.get(2));
    }

    /**
     * @brief Tests the {@link Vector#multiply(double)} method with zero.
     *
     * Verifies that the {@code multiply()} method correctly handles multiplication
     * by zero, setting all elements to 0.0.
     */
    @Test
    @DisplayName("Should handle multiplication by zero")
    void testMultiplyByZero() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        vector.multiply(0.0);
        assertEquals(0.0, vector.get(0));
        assertEquals(0.0, vector.get(1));
    }

    /**
     * @brief Tests the {@link Vector#normalize()} method.
     *
     * Verifies that the {@code normalize()} method correctly scales the
     * vector elements to a range of [0, 1] (min-max normalization).
     */
    @Test
    @DisplayName("Should normalize the vector to range [0, 1]")
    void testNormalize() {
        Vector vector = new Vector(new double[]{0.0, 5.0, 10.0});
        vector.normalize();
        assertEquals(0.0, vector.get(0), 0.001);
        assertEquals(0.5, vector.get(1), 0.001);
        assertEquals(1.0, vector.get(2), 0.001);
    }

    /**
     * @brief Tests the {@link Vector#normalize()} method with negative values.
     *
     * Verifies that the {@code normalize()} method correctly handles
     * vectors containing negative values, scaling them to the [0, 1] range.
     */
    @Test
    @DisplayName("Should normalize a vector with negative values")
    void testNormalizeNegativeValues() {
        Vector vector = new Vector(new double[]{-10.0, 0.0, 10.0});
        vector.normalize();
        assertEquals(0.0, vector.get(0), 0.001); // (-10 - -10) / (10 - -10) = 0/20 = 0
        assertEquals(0.5, vector.get(1), 0.001); // (0 - -10) / (10 - -10) = 10/20 = 0.5
        assertEquals(1.0, vector.get(2), 0.001); // (10 - -10) / (10 - -10) = 20/20 = 1
    }

    /**
     * @brief Tests that {@link Vector#normalize()} does not modify an empty vector.
     *
     * Verifies that calling {@code normalize()} on an empty vector does not
     * throw an exception and leaves the vector unchanged.
     */
    @Test
    @DisplayName("Should not normalize an empty vector")
    void testNormalizeEmptyVector() {
        Vector vector = new Vector();
        // This test ensures no exception is thrown and the vector remains empty.
        // A warning is logged, but we can't assert it easily without mocking.
        assertDoesNotThrow(vector::normalize);
        assertEquals(0, vector.size());
    }

    /**
     * @brief Tests that {@link Vector#normalize()} handles vectors with zero range.
     *
     * Verifies that calling {@code normalize()} on a vector where all elements
     * are equal (zero range) does not throw an exception and leaves the
     * values unchanged (as min-max normalization would result in division by zero).
     */
    @Test
    @DisplayName("Should not normalize a vector with zero range (all elements equal)")
    void testNormalizeZeroRangeVector() {
        Vector vector = new Vector(new double[]{5.0, 5.0, 5.0});
        assertDoesNotThrow(vector::normalize);
        assertEquals(5.0, vector.get(0)); // Values should remain unchanged
        assertEquals(5.0, vector.get(1));
        assertEquals(5.0, vector.get(2));
    }


    /**
     * @brief Tests the {@link Vector#avg()} method.
     *
     * Verifies that the {@code avg()} method correctly calculates the
     * average of the vector's elements.
     */
    @Test
    @DisplayName("Should calculate the average of the vector elements")
    void testAvg() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0, 4.0, 5.0});
        assertEquals(3.0, vector.avg()); // (1+2+3+4+5)/5 = 15/5 = 3
    }

    /**
     * @brief Tests the {@link Vector#avg()} method with an empty vector.
     *
     * Verifies that the {@code avg()} method throws an {@link IllegalStateException}
     * when attempting to calculate the average of an empty vector.
     */
    @Test
    @DisplayName("Should throw IllegalStateException when calculating average of an empty vector")
    void testAvgEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::avg);
    }

    // --- Utility Methods Tests ---

    /**
     * @brief Tests the {@link Vector#equals(Object)} method for equal vectors.
     *
     * Verifies that the {@code equals()} method returns {@code true} when
     * comparing two vectors that have the same elements in the same order.
     */
    @Test
    @DisplayName("Should return true for equal vectors")
    void testEqualsTrue() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{1.0, 2.0});
        assertTrue(v1.equals(v2));
    }

    /**
     * @brief Tests the {@link Vector#equals(Object)} method for different vectors.
     *
     * Verifies that the {@code equals()} method returns {@code false} when
     * comparing two vectors that have different elements.
     */
    @Test
    @DisplayName("Should return false for different vectors")
    void testEqualsFalse() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{1.0, 3.0});
        assertFalse(v1.equals(v2));
    }

    /**
     * @brief Tests the {@link Vector#equals(Object)} method for vectors of different sizes.
     *
     * Verifies that the {@code equals()} method returns {@code false} when
     * comparing two vectors that have different sizes.
     */
    @Test
    @DisplayName("Should return false for vectors of different sizes")
    void testEqualsDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{1.0, 2.0, 3.0});
        assertFalse(v1.equals(v2));
    }

    /**
     * @brief Tests the {@link Vector#equals(Object)} method when comparing with null.
     *
     * Verifies that the {@code equals()} method returns {@code false} when
     * comparing a vector with a null object.
     */
    @Test
    @DisplayName("Should return false when comparing with null vector")
    void testEqualsNull() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertFalse(v1.equals(null));
    }

    /**
     * @brief Tests the {@link Vector#equalDimension(Vector)} method for equal dimensions.
     *
     * Verifies that the {@code equalDimension()} method returns {@code true}
     * when comparing two vectors that have the same number of elements.
     */
    @Test
    @DisplayName("Should return true for vectors with equal dimensions")
    void testEqualDimensionTrue() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0, 4.0});
        assertTrue(v1.equalDimension(v2));
    }

    /**
     * @brief Tests the {@link Vector#equalDimension(Vector)} method for different dimensions.
     *
     * Verifies that the {@code equalDimension()} method returns {@code false}
     * when comparing two vectors that have a different number of elements.
     */
    @Test
    @DisplayName("Should return false for vectors with different dimensions")
    void testEqualDimensionFalse() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertFalse(v1.equalDimension(v2));
    }

    /**
     * @brief Tests the {@link Vector#equalDimension(Vector)} method when comparing with null.
     *
     * Verifies that the {@code equalDimension()} method returns {@code false}
     * when comparing a vector's dimension with a null vector.
     */
    @Test
    @DisplayName("Should return false when comparing dimension with null vector")
    void testEqualDimensionNull() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertFalse(v1.equalDimension(null));
    }

    /**
     * @brief Tests the {@link Vector#isContent(double)} method for a contained value.
     *
     * Verifies that the {@code isContent()} method returns {@code true} when
     * the specified value is present in the vector.
     */
    @Test
    @DisplayName("Should return true if value is contained in the vector")
    void testIsContentTrue() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        assertTrue(vector.isContent(2.0));
    }

    /**
     * @brief Tests the {@link Vector#isContent(double)} method for a non-contained value.
     *
     * Verifies that the {@code isContent()} method returns {@code false} when
     * the specified value is not present in the vector.
     */
    @Test
    @DisplayName("Should return false if value is not contained in the vector")
    void testIsContentFalse() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        assertFalse(vector.isContent(4.0));
    }

    /**
     * @brief Tests the {@link Vector#concat(Vector)} method.
     *
     * Verifies that the {@code concat()} method correctly appends the elements
     * of another vector to the current vector.
     */
    @Test
    @DisplayName("Should concatenate another vector to the current one")
    void testConcat() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0, 4.0});
        v1.concat(v2);
        assertEquals(4, v1.size());
        assertEquals(1.0, v1.get(0));
        assertEquals(2.0, v1.get(1));
        assertEquals(3.0, v1.get(2));
        assertEquals(4.0, v1.get(3));
    }

    /**
     * @brief Tests the {@link Vector#concat(Vector)} method with an empty vector.
     *
     * Verifies that concatenating an empty vector does not change the
     * current vector.
     */
    @Test
    @DisplayName("Should handle concatenating an empty vector")
    void testConcatEmptyVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector();
        v1.concat(v2);
        assertEquals(2, v1.size()); // Should remain unchanged
    }

    /**
     * @brief Tests the {@link Vector#concat(Vector)} method with a null vector.
     *
     * Verifies that concatenating a null vector is handled gracefully
     * without throwing an exception and without modifying the current vector.
     */
    @Test
    @DisplayName("Should handle concatenating a null vector gracefully")
    void testConcatNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        // No exception should be thrown, and v1 should remain unchanged.
        assertDoesNotThrow(() -> v1.concat(null));
        assertEquals(2, v1.size());
    }

    /**
     * @brief Tests the {@link Vector#getValores()} method.
     *
     * Verifies that the {@code getValores()} method returns a new list
     * containing the vector's elements, ensuring it's a defensive copy.
     */
    @Test
    @DisplayName("Should return a new list containing the vector's values")
    void testGetValores() {
        List<Double> originalList = Arrays.asList(1.0, 2.0, 3.0);
        Vector vector = new Vector(originalList);
        List<Double> returnedList = vector.getValores();

        assertNotNull(returnedList);
        assertEquals(originalList, returnedList);
        assertNotSame(originalList, returnedList, "Should return a new list, not the same instance");
    }

    /**
     * @brief Tests the {@link Vector#getValores()} method when the internal list is null.
     *
     * This test uses reflection to simulate a scenario where the internal
     * {@code coef} list is null, ensuring that {@code getValores()}
     * returns an empty list gracefully.
     * @throws NoSuchFieldException If the 'coef' field is not found.
     * @throws IllegalAccessException If access to the 'coef' field is denied.
     */
    @Test
    @DisplayName("Should return an empty list if internal coef is null (though it shouldn't happen with current constructors)")
    void testGetValoresNullCoef() throws NoSuchFieldException, IllegalAccessException {
        Vector vector = new Vector();
        // Use reflection to set coef to null for this specific test
        java.lang.reflect.Field field = Vector.class.getDeclaredField("coef");
        field.setAccessible(true);
        field.set(vector, null);

        List<Double> returnedList = vector.getValores();
        assertNotNull(returnedList);
        assertTrue(returnedList.isEmpty());
    }

    // --- File I/O Tests ---

    /**
     * @brief Tests the {@link Vector#write(String)} method.
     *
     * Verifies that the {@code write()} method correctly writes the vector's
     * string representation to a file specified by its filename.
     * @throws IOException If an I/O error occurs during file writing.
     */
    @Test
    @DisplayName("Should write vector to a file using filename")
    void testWriteFilename() throws IOException {
        File tempFile = tempDir.resolve("output_vector.txt").toFile();
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        vector.write(tempFile.getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            assertEquals("[1.0, 2.0, 3.0]", reader.readLine());
        }
    }

    /**
     * @brief Tests the {@link Vector#write(String)} method with a null filename.
     *
     * Verifies that the {@code write()} method throws an {@link IllegalArgumentException}
     * when provided with a null filename.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null filename in write(String)")
    void testWriteNullFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.write((String) null));
    }

    /**
     * @brief Tests the {@link Vector#write(String)} method with an empty filename.
     *
     * Verifies that the {@code write()} method throws an {@link IllegalArgumentException}
     * when provided with an empty filename.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for empty filename in write(String)")
    void testWriteEmptyFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.write(""));
    }

    /**
     * @brief Tests the {@link Vector#write(File)} method.
     *
     * Verifies that the {@code write()} method correctly writes the vector's
     * string representation to a file specified by a {@link File} object.
     * @throws IOException If an I/O error occurs during file writing.
     */
    @Test
    @DisplayName("Should write vector to a file using File object")
    void testWriteFileObject() throws IOException {
        File tempFile = tempDir.resolve("output_vector_file.txt").toFile();
        Vector vector = new Vector(new double[]{4.0, 5.0});
        vector.write(tempFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            assertEquals("[4.0, 5.0]", reader.readLine());
        }
    }

    /**
     * @brief Tests the {@link Vector#write(File)} method with a null File object.
     *
     * Verifies that the {@code write()} method throws an {@link IllegalArgumentException}
     * when provided with a null {@link File} object.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null File object in write(File)")
    void testWriteNullFileObject() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.write((File) null));
    }

    /**
     * @brief Tests the {@link Vector#read(String)} method using BufferedReader.
     *
     * Verifies that the {@code read()} method correctly reads double values
     * from a file specified by its filename, using a {@link BufferedReader}.
     * @throws IOException If an I/O error occurs during file reading.
     */
    @Test
    @DisplayName("Should read vector from a file using filename (BufferedReader)")
    void testReadFilename() throws IOException {
        File tempFile = tempDir.resolve("input_vector_reader.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("10.0\n20.0\n30.0\n");
        }
        Vector vector = new Vector();
        vector.read(tempFile.getAbsolutePath());
        assertEquals(3, vector.size());
        assertEquals(10.0, vector.get(0));
        assertEquals(20.0, vector.get(1));
        assertEquals(30.0, vector.get(2));
    }

    /**
     * @brief Tests the {@link Vector#read(String)} method with a null filename.
     *
     * Verifies that the {@code read()} method throws an {@link IllegalArgumentException}
     * when provided with a null filename.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null filename in read(String)")
    void testReadNullFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read((String) null));
    }

    /**
     * @brief Tests the {@link Vector#read(String)} method with an empty filename.
     *
     * Verifies that the {@code read()} method throws an {@link IllegalArgumentException}
     * when provided with an empty filename.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for empty filename in read(String)")
    void testReadEmptyFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read(""));
    }

    /**
     * @brief Tests the {@link Vector#read(String)} method when the file does not exist.
     *
     * Verifies that the {@code read()} method throws a {@link FileNotFoundException}
     * when the specified file does not exist.
     */
    @Test
    @DisplayName("Should throw FileNotFoundException if file for read(String) does not exist")
    void testReadFileNotFound() {
        Vector vector = new Vector();
        assertThrows(FileNotFoundException.class, () -> vector.read("non_existent_file.txt"));
    }

    /**
     * @brief Tests the {@link Vector#read(String)} method with non-numeric lines.
     *
     * Verifies that the {@code read()} method gracefully handles lines
     * that are not valid numbers, skipping them and continuing to read valid data.
     * @throws IOException If an I/O error occurs during file reading.
     */
    @Test
    @DisplayName("Should handle non-numeric lines in read(String) gracefully")
    void testReadNonNumericLines() throws IOException {
        File tempFile = tempDir.resolve("input_non_numeric.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1.0\nabc\n2.0\n");
        }
        Vector vector = new Vector();
        // This test ensures it doesn't crash and continues reading valid numbers.
        assertDoesNotThrow(() -> vector.read(tempFile.getAbsolutePath()));
        assertEquals(2, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1));
    }


    /**
     * @brief Tests the {@link Vector#read(File)} method using Scanner.
     *
     * Verifies that the {@code read()} method correctly reads double values
     * from a file specified by a {@link File} object, using a {@link Scanner}.
     * @throws IOException If an I/O error occurs during file reading.
     */
    @Test
    @DisplayName("Should read vector from a file using File object (Scanner)")
    void testReadFileObject() throws IOException {
        File tempFile = tempDir.resolve("input_vector_file.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1.0 2.0\n3.0\n"); // Scanner handles whitespace and newlines
        }
        Vector vector = new Vector();
        vector.read(tempFile);
        assertEquals(3, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1));
        assertEquals(3.0, vector.get(2));
    }

    /**
     * @brief Tests the {@link Vector#read(File)} method with a null File object.
     *
     * Verifies that the {@code read()} method throws an {@link IllegalArgumentException}
     * when provided with a null {@link File} object.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null File object in read(File)")
    void testReadNullFileObject() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read((File) null));
    }

    /**
     * @brief Tests the {@link Vector#read(File)} method when the file does not exist.
     *
     * Verifies that the {@code read()} method throws a {@link FileNotFoundException}
     * when the specified file for {@code read(File)} does not exist.
     */
    @Test
    @DisplayName("Should throw FileNotFoundException if file for read(File) does not exist")
    void testReadFileObjectFileNotFound() {
        Vector vector = new Vector();
        File nonExistentFile = tempDir.resolve("non_existent_file_scanner.txt").toFile();
        assertThrows(FileNotFoundException.class, () -> vector.read(nonExistentFile));
    }

    /**
     * @brief Tests a method with non-numeric input.
     *
     * Verifies that the private helper method {@code readFileWithScanner}
     * gracefully handles non-numeric tokens in the input file, skipping them
     * and continuing to read valid double values.
     * @throws IOException If an I/O error occurs during file reading.
     */
    @Test
    @DisplayName("Should handle non-numeric input in readFileWithScanner gracefully")
    void testReadFileWithScannerNonNumeric() throws IOException {
        File tempFile = tempDir.resolve("input_scanner_non_numeric.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("1.0 hola 2.0\n");
        }
        Vector vector = new Vector();
        assertDoesNotThrow(() -> vector.read(tempFile)); // Should not throw exception
        assertEquals(2, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1));
    }

    /**
     * @brief Tests the {@link Vector#read(Scanner)} method.
     *
     * Verifies that the {@code read()} method correctly reads double values
     * from a {@link Scanner} object.
     */
    @Test
    @DisplayName("Should read vector from a Scanner object")
    void testReadScanner() {
        Scanner testScanner = new Scanner("1.0 2.5 3.0");
        Vector vector = new Vector();
        vector.read(testScanner);
        assertEquals(3, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.5, vector.get(1));
        assertEquals(3.0, vector.get(2));
        testScanner.close();
    }

    /**
     * @brief Tests the {@link Vector#read(Scanner)} method with an empty Scanner.
     *
     * Verifies that the {@code read()} method handles an empty {@link Scanner}
     * by resulting in an empty vector.
     */
    @Test
    @DisplayName("Should handle empty scanner in read(Scanner)")
    void testReadEmptyScanner() {
        Scanner testScanner = new Scanner("");
        Vector vector = new Vector();
        vector.read(testScanner);
        assertEquals(0, vector.size());
        testScanner.close();
    }

    /**
     * @brief Tests the {@link Vector#read(Scanner)} method with a null Scanner object.
     *
     * Verifies that the {@code read()} method throws an {@link IllegalArgumentException}
     * when provided with a null {@link Scanner} object.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null Scanner object in read(Scanner)")
    void testReadNullScanner() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read((Scanner) null));
    }

    /**
     * @brief Tests the {@link Vector#read(Scanner)} method with non-double input.
     *
     * Verifies that the {@code read()} method gracefully handles non-double
     * input from a {@link Scanner}, skipping invalid tokens and continuing
     * to read valid double values.
     */
    @Test
    @DisplayName("Should handle non-double input in read(Scanner) gracefully")
    void testReadScannerNonDouble() {
        Scanner testScanner = new Scanner("1.0 abc 2.0");
        Vector vector = new Vector();
        vector.read(testScanner);
        assertEquals(2, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(2.0, vector.get(1));
        testScanner.close();
    }
}