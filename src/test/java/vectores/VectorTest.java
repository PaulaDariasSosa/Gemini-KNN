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

@DisplayName("Vector Class Tests")
class VectorTest {

    // TempDir for file I/O tests
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);
        // If you need to mock the logger to assert log messages,
        // you'd typically set it up here or use a custom appender for Logback/Log4j
        // For basic coverage, we just let the actual logger run.
    }

    // --- Constructor Tests ---

    @Test
    @DisplayName("Should create an empty vector with no-arg constructor")
    void testConstructorEmpty() {
        Vector vector = new Vector();
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

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

    @Test
    @DisplayName("Should handle null double array in constructor gracefully")
    void testConstructorNullDoubleArray() {
        // Logging a warning is acceptable behavior, so we just check for no exception
        // and an empty vector.
        Vector vector = new Vector((double[]) null);
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

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

    @Test
    @DisplayName("Should handle null List<Double> in constructor gracefully")
    void testConstructorNullListDouble() {
        Vector vector = new Vector((List<Double>) null);
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should create a vector of specified size initialized with zeros")
    void testConstructorSize() {
        Vector vector = new Vector(5);
        assertEquals(5, vector.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(0.0, vector.get(i));
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for negative size in constructor")
    void testConstructorNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new Vector(-1));
    }

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

    @Test
    @DisplayName("Should throw IllegalArgumentException for null file in Scanner constructor")
    void testConstructorNullFileScanner() {
        assertThrows(IllegalArgumentException.class, () -> new Vector((File) null));
    }

    @Test
    @DisplayName("Should throw FileNotFoundException if file for Scanner constructor does not exist")
    void testConstructorFileNotFoundScanner() {
        File nonExistentFile = tempDir.resolve("non_existent_file.txt").toFile();
        assertThrows(FileNotFoundException.class, () -> new Vector(nonExistentFile));
    }

    @Test
    @DisplayName("Should create a vector from a comma-separated String")
    void testConstructorString() {
        Vector vector = new Vector("1.1,2.2,3.3");
        assertEquals(3, vector.size());
        assertEquals(1.1, vector.get(0));
        assertEquals(2.2, vector.get(1));
        assertEquals(3.3, vector.get(2));
    }

    @Test
    @DisplayName("Should handle empty string in constructor")
    void testConstructorEmptyString() {
        Vector vector = new Vector("");
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should handle null string in constructor gracefully")
    void testConstructorNullString() {
        Vector vector = new Vector((String) null);
        assertNotNull(vector);
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should throw NumberFormatException for invalid string format in constructor")
    void testConstructorInvalidStringFormat() {
        assertThrows(NumberFormatException.class, () -> new Vector("1.0,abc,3.0"));
    }

    @Test
    @DisplayName("Should clone a vector using copy constructor")
    void testConstructorCopy() {
        Vector original = new Vector(new double[]{1.0, 2.0, 3.0});
        Vector copy = new Vector(original);
        assertEquals(original.size(), copy.size());
        assertEquals(original.get(0), copy.get(0));
        assertNotSame(original, copy, "Should be a deep copy, not the same instance");
    }

    @Test
    @DisplayName("Should handle null vector in copy constructor gracefully")
    void testConstructorCopyNull() {
        Vector copy = new Vector((Vector) null);
        assertNotNull(copy);
        assertEquals(0, copy.size());
    }

    // --- Basic Operations Tests ---

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

    @Test
    @DisplayName("Should clear the vector")
    void testClear() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertFalse(vector.coef.isEmpty()); // Accessing coef for verification
        vector.clear();
        assertTrue(vector.coef.isEmpty());
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertEquals("[1.0, 2.0]", vector.toString());
        Vector emptyVector = new Vector();
        assertEquals("[]", emptyVector.toString());
    }

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


    @Test
    @DisplayName("Should get value at valid index")
    void testGetValidIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        assertEquals(2.0, vector.get(1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting value at negative index")
    void testGetNegativeIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(-1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting value at out-of-bounds index")
    void testGetOutOfBoundsIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.get(2));
    }

    @Test
    @DisplayName("Should set value at valid index")
    void testSetValidIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        vector.set(1, 2.5);
        assertEquals(2.5, vector.get(1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting value at negative index")
    void testSetNegativeIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.set(-1, 0.0));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting value at out-of-bounds index")
    void testSetOutOfBoundsIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.set(2, 0.0));
    }

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

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding a null vector")
    void testAddNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertThrows(IllegalArgumentException.class, () -> v1.add((Vector) null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding vectors of different sizes")
    void testAddVectorDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertThrows(IllegalArgumentException.class, () -> v1.add(v2));
    }

    @Test
    @DisplayName("Should remove element at valid index")
    void testRemoveValidIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        vector.remove(1);
        assertEquals(2, vector.size());
        assertEquals(1.0, vector.get(0));
        assertEquals(3.0, vector.get(1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when removing at negative index")
    void testRemoveNegativeIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.remove(-1));
    }

    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when removing at out-of-bounds index")
    void testRemoveOutOfBoundsIndex() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        assertThrows(IndexOutOfBoundsException.class, () -> vector.remove(2));
    }

    // --- Mathematical Operations Tests ---

    @Test
    @DisplayName("Should return the maximum value in the vector")
    void testGetMax() {
        Vector vector = new Vector(new double[]{10.0, 5.0, 20.0, 8.0});
        assertEquals(20.0, vector.getMax());
    }

    @Test
    @DisplayName("Should handle single element vector for getMax")
    void testGetMaxSingleElement() {
        Vector vector = new Vector(new double[]{7.0});
        assertEquals(7.0, vector.getMax());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when getting max from an empty vector")
    void testGetMaxEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::getMax);
    }

    @Test
    @DisplayName("Should return the index of the maximum value")
    void testGetMaxInt() {
        Vector vector = new Vector(new double[]{10.0, 5.0, 20.0, 8.0});
        assertEquals(2, vector.getMaxInt()); // 20.0 is at index 2
    }

    @Test
    @DisplayName("Should handle multiple maximum values for getMaxInt (first occurrence)")
    void testGetMaxIntMultipleMax() {
        Vector vector = new Vector(new double[]{10.0, 20.0, 20.0, 8.0});
        assertEquals(1, vector.getMaxInt()); // Should return the index of the first 20.0
    }

    @Test
    @DisplayName("Should throw IllegalStateException when getting max index from an empty vector")
    void testGetMaxIntEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::getMaxInt);
    }

    @Test
    @DisplayName("Should return the minimum value in the vector")
    void testGetMin() {
        Vector vector = new Vector(new double[]{10.0, 5.0, 20.0, 2.0});
        assertEquals(2.0, vector.getMin());
    }

    @Test
    @DisplayName("Should handle single element vector for getMin")
    void testGetMinSingleElement() {
        Vector vector = new Vector(new double[]{3.0});
        assertEquals(3.0, vector.getMin());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when getting min from an empty vector")
    void testGetMinEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::getMin);
    }

    @Test
    @DisplayName("Should calculate the dot product correctly")
    void testProductoEscalar() {
        Vector v1 = new Vector(new double[]{1.0, 2.0, 3.0});
        Vector v2 = new Vector(new double[]{4.0, 5.0, 6.0});
        assertEquals(32.0, v1.productoEscalar(v2)); // (1*4) + (2*5) + (3*6) = 4 + 10 + 18 = 32
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null vector in productoEscalar")
    void testProductoEscalarNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertThrows(IllegalArgumentException.class, () -> v1.productoEscalar(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for different sizes in productoEscalar")
    void testProductoEscalarDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertThrows(IllegalArgumentException.class, () -> v1.productoEscalar(v2));
    }

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

    @Test
    @DisplayName("Should throw IllegalArgumentException for null vector in sum(Vector)")
    void testSumNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertThrows(IllegalArgumentException.class, () -> v1.sum((Vector) null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for different sizes in sum(Vector)")
    void testSumVectorDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertThrows(IllegalArgumentException.class, () -> v1.sum(v2));
    }

    @Test
    @DisplayName("Should calculate the module of the vector")
    void testModule() {
        Vector vector = new Vector(new double[]{3.0, 4.0});
        assertEquals(5.0, vector.module(), 0.001); // sqrt(3^2 + 4^2) = sqrt(9 + 16) = sqrt(25) = 5
    }

    @Test
    @DisplayName("Should return 0 for the module of an empty vector")
    void testModuleEmptyVector() {
        Vector vector = new Vector();
        assertEquals(0.0, vector.module());
    }

    @Test
    @DisplayName("Should multiply each element by a scalar")
    void testMultiplyScalar() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        vector.multiply(2.0);
        assertEquals(2.0, vector.get(0));
        assertEquals(4.0, vector.get(1));
        assertEquals(6.0, vector.get(2));
    }

    @Test
    @DisplayName("Should handle multiplication by zero")
    void testMultiplyByZero() {
        Vector vector = new Vector(new double[]{1.0, 2.0});
        vector.multiply(0.0);
        assertEquals(0.0, vector.get(0));
        assertEquals(0.0, vector.get(1));
    }

    @Test
    @DisplayName("Should normalize the vector to range [0, 1]")
    void testNormalize() {
        Vector vector = new Vector(new double[]{0.0, 5.0, 10.0});
        vector.normalize();
        assertEquals(0.0, vector.get(0), 0.001);
        assertEquals(0.5, vector.get(1), 0.001);
        assertEquals(1.0, vector.get(2), 0.001);
    }

    @Test
    @DisplayName("Should normalize a vector with negative values")
    void testNormalizeNegativeValues() {
        Vector vector = new Vector(new double[]{-10.0, 0.0, 10.0});
        vector.normalize();
        assertEquals(0.0, vector.get(0), 0.001); // (-10 - -10) / (10 - -10) = 0/20 = 0
        assertEquals(0.5, vector.get(1), 0.001); // (0 - -10) / (10 - -10) = 10/20 = 0.5
        assertEquals(1.0, vector.get(2), 0.001); // (10 - -10) / (10 - -10) = 20/20 = 1
    }

    @Test
    @DisplayName("Should not normalize an empty vector")
    void testNormalizeEmptyVector() {
        Vector vector = new Vector();
        // This test ensures no exception is thrown and the vector remains empty.
        // A warning is logged, but we can't assert it easily without mocking.
        assertDoesNotThrow(vector::normalize);
        assertEquals(0, vector.size());
    }

    @Test
    @DisplayName("Should not normalize a vector with zero range (all elements equal)")
    void testNormalizeZeroRangeVector() {
        Vector vector = new Vector(new double[]{5.0, 5.0, 5.0});
        assertDoesNotThrow(vector::normalize);
        assertEquals(5.0, vector.get(0)); // Values should remain unchanged
        assertEquals(5.0, vector.get(1));
        assertEquals(5.0, vector.get(2));
    }


    @Test
    @DisplayName("Should calculate the average of the vector elements")
    void testAvg() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0, 4.0, 5.0});
        assertEquals(3.0, vector.avg()); // (1+2+3+4+5)/5 = 15/5 = 3
    }

    @Test
    @DisplayName("Should throw IllegalStateException when calculating average of an empty vector")
    void testAvgEmptyVector() {
        Vector vector = new Vector();
        assertThrows(IllegalStateException.class, vector::avg);
    }

    // --- Utility Methods Tests ---

    @Test
    @DisplayName("Should return true for equal vectors")
    void testEqualsTrue() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{1.0, 2.0});
        assertTrue(v1.equals(v2));
    }

    @Test
    @DisplayName("Should return false for different vectors")
    void testEqualsFalse() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{1.0, 3.0});
        assertFalse(v1.equals(v2));
    }

    @Test
    @DisplayName("Should return false for vectors of different sizes")
    void testEqualsDifferentSize() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{1.0, 2.0, 3.0});
        assertFalse(v1.equals(v2));
    }

    @Test
    @DisplayName("Should return false when comparing with null vector")
    void testEqualsNull() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertFalse(v1.equals(null));
    }

    @Test
    @DisplayName("Should return true for vectors with equal dimensions")
    void testEqualDimensionTrue() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0, 4.0});
        assertTrue(v1.equalDimension(v2));
    }

    @Test
    @DisplayName("Should return false for vectors with different dimensions")
    void testEqualDimensionFalse() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector(new double[]{3.0});
        assertFalse(v1.equalDimension(v2));
    }

    @Test
    @DisplayName("Should return false when comparing dimension with null vector")
    void testEqualDimensionNull() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        assertFalse(v1.equalDimension(null));
    }

    @Test
    @DisplayName("Should return true if value is contained in the vector")
    void testIsContentTrue() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        assertTrue(vector.isContent(2.0));
    }

    @Test
    @DisplayName("Should return false if value is not contained in the vector")
    void testIsContentFalse() {
        Vector vector = new Vector(new double[]{1.0, 2.0, 3.0});
        assertFalse(vector.isContent(4.0));
    }

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

    @Test
    @DisplayName("Should handle concatenating an empty vector")
    void testConcatEmptyVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        Vector v2 = new Vector();
        v1.concat(v2);
        assertEquals(2, v1.size()); // Should remain unchanged
    }

    @Test
    @DisplayName("Should handle concatenating a null vector gracefully")
    void testConcatNullVector() {
        Vector v1 = new Vector(new double[]{1.0, 2.0});
        // No exception should be thrown, and v1 should remain unchanged.
        assertDoesNotThrow(() -> v1.concat(null));
        assertEquals(2, v1.size());
    }

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

    @Test
    @DisplayName("Should throw IllegalArgumentException for null filename in write(String)")
    void testWriteNullFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.write((String) null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for empty filename in write(String)")
    void testWriteEmptyFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.write(""));
    }

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

    @Test
    @DisplayName("Should throw IllegalArgumentException for null File object in write(File)")
    void testWriteNullFileObject() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.write((File) null));
    }

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

    @Test
    @DisplayName("Should throw IllegalArgumentException for null filename in read(String)")
    void testReadNullFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read((String) null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for empty filename in read(String)")
    void testReadEmptyFilename() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read(""));
    }

    @Test
    @DisplayName("Should throw FileNotFoundException if file for read(String) does not exist")
    void testReadFileNotFound() {
        Vector vector = new Vector();
        assertThrows(FileNotFoundException.class, () -> vector.read("non_existent_file.txt"));
    }

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

    @Test
    @DisplayName("Should throw IllegalArgumentException for null File object in read(File)")
    void testReadNullFileObject() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read((File) null));
    }

    @Test
    @DisplayName("Should throw FileNotFoundException if file for read(File) does not exist")
    void testReadFileObjectFileNotFound() {
        Vector vector = new Vector();
        File nonExistentFile = tempDir.resolve("non_existent_file_scanner.txt").toFile();
        assertThrows(FileNotFoundException.class, () -> vector.read(nonExistentFile));
    }

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

    @Test
    @DisplayName("Should handle empty scanner in read(Scanner)")
    void testReadEmptyScanner() {
        Scanner testScanner = new Scanner("");
        Vector vector = new Vector();
        vector.read(testScanner);
        assertEquals(0, vector.size());
        testScanner.close();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null Scanner object in read(Scanner)")
    void testReadNullScanner() {
        Vector vector = new Vector();
        assertThrows(IllegalArgumentException.class, () -> vector.read((Scanner) null));
    }

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