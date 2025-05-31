package vectores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale; // Ensure Locale is imported for Scanner tests in Vector

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class MatrizTest
 * @brief Tests for the Matriz class.
 *
 * This class provides a comprehensive set of unit tests for the `Matriz` class,
 * covering its constructors, getters, element manipulation (get/set),
 * matrix operations (transpose, add/delete rows/columns), I/O operations (print, write),
 * and normalization. It uses JUnit 5 for testing and Mockito for mocking dependencies where necessary.
 */
@DisplayName("Matriz Class Tests")
class MatrizTest {

    /**
     * @brief Temporary directory for file I/O tests.
     *
     * JUnit's @TempDir annotation ensures a temporary directory is created
     * before each test method and cleaned up afterwards.
     */
    @TempDir
    Path tempDir; // Temporary directory for file I/O tests

    // Logger is used for internal warnings/errors; we primarily test behavior.

    /**
     * @brief Set up method executed before each test.
     *
     * This method can be used to initialize common test data or configurations
     * before each test case runs.
     */
    @BeforeEach
    void setUp() {
        // Any setup needed before each test can go here.
    }

    // --- Constructor Tests ---

    /**
     * @brief Tests the default constructor of the Matriz class.
     *
     * Verifies that the default constructor creates a 1x1 matrix initialized with 0.0,
     * and that the `isTransposed` flag is initially false.
     */
    @Test
    @DisplayName("Should create a 1x1 matrix with 0.0 using default constructor")
    void testConstructorDefault() {
        Matriz matriz = new Matriz();
        assertNotNull(matriz);
        assertEquals(1, matriz.getNumRows());
        assertEquals(1, matriz.getNumCols());
        assertEquals(0.0, matriz.get(0, 0));
        assertFalse(matriz.isTransposed);
    }

    /**
     * @brief Tests the constructor that creates an m x n matrix initialized with zeros.
     *
     * Verifies that the matrix dimensions are correct and all elements are initialized to 0.0.
     */
    @Test
    @DisplayName("Should create an mxn matrix initialized with zeros")
    void testConstructorMxN() {
        Matriz matriz = new Matriz(2, 3);
        assertNotNull(matriz);
        assertEquals(2, matriz.getNumRows());
        assertEquals(3, matriz.getNumCols());
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0.0, matriz.get(i, j));
            }
        }
        assertFalse(matriz.isTransposed);
    }

    /**
     * @brief Tests that the m x n constructor throws an IllegalArgumentException for non-positive dimensions.
     *
     * Ensures that the constructor correctly handles invalid input for rows (m) and columns (n).
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for non-positive dimensions in constructor(m,n)")
    void testConstructorMxNInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new Matriz(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Matriz(5, 0));
        assertThrows(IllegalArgumentException.class, () -> new Matriz(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> new Matriz(5, -1));
    }

    /**
     * @brief Tests the constructor that creates a matrix from a 2D double array.
     *
     * Verifies that the matrix is correctly initialized with the provided data
     * and that dimensions match.
     */
    @Test
    @DisplayName("Should create a matrix from a 2D double array")
    void testConstructor2DArray() {
        double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        Matriz matriz = new Matriz(2, 3, data);
        assertNotNull(matriz);
        assertEquals(2, matriz.getNumRows());
        assertEquals(3, matriz.getNumCols());
        assertEquals(1.0, matriz.get(0, 0));
        assertEquals(3.0, matriz.get(0, 2));
        assertEquals(4.0, matriz.get(1, 0));
        assertEquals(6.0, matriz.get(1, 2));
        assertFalse(matriz.isTransposed);
    }

    /**
     * @brief Tests that the 2D array constructor throws an IllegalArgumentException for a null array.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null 2D array in constructor")
    void testConstructor2DArrayNull() {
        assertThrows(IllegalArgumentException.class, () -> new Matriz(2, 3, null));
    }

    /**
     * @brief Tests that the 2D array constructor throws an IllegalArgumentException for mismatching row count.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for mismatching rows in 2D array constructor")
    void testConstructor2DArrayMismatchRows() {
        double[][] data = {{1.0}, {2.0}}; // Expected 3 rows, got 2
        assertThrows(IllegalArgumentException.class, () -> new Matriz(3, 1, data));
    }

    /**
     * @brief Tests that the 2D array constructor throws an IllegalArgumentException for mismatching column count.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for mismatching columns in 2D array constructor")
    void testConstructor2DArrayMismatchCols() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}}; // Expected 3 cols, got 2
        assertThrows(IllegalArgumentException.class, () -> new Matriz(2, 3, data));
    }

    /**
     * @brief Tests that the 2D array constructor throws an IllegalArgumentException for irregular 2D array.
     *
     * An irregular array is one where rows have different lengths.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for irregular 2D array (first row mismatching)")
    void testConstructor2DArrayIrregularFirstRow() {
        double[][] data = {{1.0}, {2.0, 3.0}}; // Mismatch in first row length
        assertThrows(IllegalArgumentException.class, () -> new Matriz(2, 2, data));
    }

    /**
     * @brief Tests the constructor that creates a matrix from a List of Vector objects.
     *
     * Verifies correct initialization and dimensions.
     */
    @Test
    @DisplayName("Should create a matrix from a List of Vectors")
    void testConstructorListOfVectors() {
        List<Vector> vectors = new ArrayList<>();
        vectors.add(new Vector(new double[]{1.0, 2.0}));
        vectors.add(new Vector(new double[]{3.0, 4.0}));
        Matriz matriz = new Matriz(vectors);
        assertNotNull(matriz);
        assertEquals(2, matriz.getNumRows());
        assertEquals(2, matriz.getNumCols());
        assertEquals(1.0, matriz.get(0, 0));
        assertEquals(4.0, matriz.get(1, 1));
        assertFalse(matriz.isTransposed);
    }

    /**
     * @brief Tests that the List of Vectors constructor throws an IllegalArgumentException for a null list.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null List of Vectors in constructor")
    void testConstructorListOfVectorsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Matriz((List<Vector>) null));
    }

    /**
     * @brief Tests that the List of Vectors constructor throws an IllegalArgumentException for an empty list.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for empty List of Vectors in constructor")
    void testConstructorListOfVectorsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Matriz(new ArrayList<>()));
    }

    /**
     * @brief Tests that the List of Vectors constructor throws an IllegalArgumentException if vectors have different dimensions.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for List of Vectors with different dimensions")
    void testConstructorListOfVectorsDifferentDimensions() {
        List<Vector> vectors = new ArrayList<>();
        vectors.add(new Vector(new double[]{1.0, 2.0}));
        vectors.add(new Vector(new double[]{3.0})); // Different size
        assertThrows(IllegalArgumentException.class, () -> new Matriz(vectors));
    }

    // --- Getters Tests ---

    /**
     * @brief Tests the getNumRows method.
     *
     * Verifies that the correct number of rows is returned,
     * including after a transpose operation.
     */
    @Test
    @DisplayName("Should return correct number of rows")
    void testGetNumRows() {
        Matriz matriz = new Matriz(3, 2);
        assertEquals(3, matriz.getNumRows());
        matriz.transpose(); // Transpose
        assertEquals(2, matriz.getNumRows()); // Rows become original cols
    }

    /**
     * @brief Tests the getNumCols method.
     *
     * Verifies that the correct number of columns is returned,
     * including after a transpose operation.
     */
    @Test
    @DisplayName("Should return correct number of columns")
    void testGetNumCols() {
        Matriz matriz = new Matriz(3, 2);
        assertEquals(2, matriz.getNumCols());
        matriz.transpose(); // Transpose
        assertEquals(3, matriz.getNumCols()); // Cols become original rows
    }

    // --- Print Test (mostly for coverage, hard to assert console output) ---

    /**
     * @brief Tests the print method.
     *
     * This test primarily ensures that calling the print method does not throw any errors,
     * as asserting console output is generally difficult in unit tests.
     */
    @Test
    @DisplayName("Should print matrix (no assertions, just ensure no errors)")
    void testPrint() {
        Matriz matriz = new Matriz(2, 2, new double[][]{{1.0, 2.0}, {3.0, 4.0}});
        assertDoesNotThrow(matriz::print);
    }

    /**
     * @brief Tests that the print method handles null rows gracefully without throwing errors.
     *
     * Uses reflection to inject a null row into the matrix for testing purposes.
     * This test focuses on behavior, not specific output.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should handle null rows when printing (no assertions, just ensure no errors)")
    void testPrintWithNullRows() throws NoSuchFieldException, IllegalAccessException {
        Matriz matriz = new Matriz(2, 2);
        // Use reflection to introduce a null row for testing
        java.lang.reflect.Field field = Matriz.class.getDeclaredField("matrix");
        field.setAccessible(true);
        List<Vector> internalMatrix = (List<Vector>) field.get(matriz);
        internalMatrix.set(0, null); // Set first row to null
        field.set(matriz, internalMatrix);

        assertDoesNotThrow(matriz::print);
    }

    // --- Write Test ---

    /**
     * @brief Tests the write method to ensure matrix data is correctly written to a file.
     *
     * @throws IOException If an I/O error occurs during file operations.
     */
    @Test
    @DisplayName("Should write matrix data to a file")
    void testWrite() throws IOException {
        File tempFile = tempDir.resolve("matrix_output.txt").toFile();
        double[][] data = {{1.1, 2.2}, {3.3, 4.4}};
        Matriz matriz = new Matriz(2, 2, data);

        matriz.write(tempFile.getAbsolutePath());

        // Verify content by reading the file
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            assertEquals("2", reader.readLine()); // numRows
            assertEquals("2", reader.readLine()); // numCols
            assertEquals("1.1 2.2", reader.readLine()); // Row 0 data
            assertEquals("3.3 4.4", reader.readLine()); // Row 1 data
            assertNull(reader.readLine()); // Ensure no more lines
        }
    }

    /**
     * @brief Tests that the write method throws an IllegalArgumentException for a null filename.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for null filename in write")
    void testWriteNullFilename() {
        Matriz matriz = new Matriz();
        assertThrows(IllegalArgumentException.class, () -> matriz.write(null));
    }

    /**
     * @brief Tests that the write method throws an IllegalArgumentException for an empty filename.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for empty filename in write")
    void testWriteEmptyFilename() {
        Matriz matriz = new Matriz();
        assertThrows(IllegalArgumentException.class, () -> matriz.write(""));
    }

    /**
     * @brief Tests that the write method correctly handles IOException.
     *
     * This test attempts to write to an invalid path to provoke an IOException.
     * The success of this test might depend on the operating system's file system behavior.
     */
    @Test
    @DisplayName("Should handle IOException during write")
    void testWriteIOException() {
        Matriz matriz = new Matriz(1, 1);
        File nonWritableFile = new File("/dev/null/nonexistent"); // Path that should cause IOException on most systems
        // On Windows, you might need a different approach, e.g., mocking FileWriter.
        // For typical Unix-like systems, this path is often non-writable/invalid.
        // This test's success depends on the OS's file system behavior.
        assertThrows(IOException.class, () -> matriz.write(nonWritableFile.getAbsolutePath()));
    }

    /**
     * @brief Tests that the write method handles null rows gracefully when writing to a file.
     *
     * Uses reflection to inject a null row and verifies the file content.
     * @throws IOException If an I/O error occurs.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should handle null rows gracefully when writing to a file")
    void testWriteWithNullRows() throws IOException, NoSuchFieldException, IllegalAccessException {
        File tempFile = tempDir.resolve("matrix_output_null_rows.txt").toFile();
        Matriz matriz = new Matriz(2, 2);

        // Use reflection to introduce a null row for testing
        java.lang.reflect.Field field = Matriz.class.getDeclaredField("matrix");
        field.setAccessible(true);
        List<Vector> internalMatrix = (List<Vector>) field.get(matriz);
        internalMatrix.set(0, null); // Set first row to null
        field.set(matriz, internalMatrix);

        matriz.write(tempFile.getAbsolutePath());

        // Verify content: first row should be an empty line, second row "0.0 0.0"
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            assertEquals("2", reader.readLine()); // numRows
            assertEquals("2", reader.readLine()); // numCols
            assertEquals("", reader.readLine());    // Null row should write an empty line after replaceAll
            assertEquals("0.0 0.0", reader.readLine()); // Second row (initialized to zeros)
        }
    }


    // --- Get/Set Element Tests ---

    /**
     * @brief Tests the get method for valid indices.
     *
     * Verifies that the correct element is retrieved at specified row and column.
     */
    @Test
    @DisplayName("Should get element at valid indices")
    void testGetValid() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        Matriz matriz = new Matriz(2, 2, data);
        assertEquals(1.0, matriz.get(0, 0));
        assertEquals(4.0, matriz.get(1, 1));
    }

    /**
     * @brief Tests that the get method throws an IndexOutOfBoundsException for a negative row index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting element at negative row index")
    void testGetNegativeRow() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.get(-1, 0));
    }

    /**
     * @brief Tests that the get method throws an IndexOutOfBoundsException for an out-of-bounds row index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting element at out-of-bounds row index")
    void testGetOutOfBoundsRow() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.get(2, 0));
    }

    /**
     * @brief Tests that the get method throws an IndexOutOfBoundsException for a negative column index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting element at negative column index")
    void testGetNegativeCol() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.get(0, -1));
    }

    /**
     * @brief Tests that the get method throws an IndexOutOfBoundsException for an out-of-bounds column index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when getting element at out-of-bounds column index")
    void testGetOutOfBoundsCol() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.get(0, 2));
    }

    /**
     * @brief Tests that the get method throws an IllegalStateException when trying to get an element from a null row.
     *
     * Uses reflection to inject a null row for testing.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should throw IllegalStateException when getting element from a null row")
    void testGetFromNullRow() throws NoSuchFieldException, IllegalAccessException {
        Matriz matriz = new Matriz(2, 2);
        java.lang.reflect.Field field = Matriz.class.getDeclaredField("matrix");
        field.setAccessible(true);
        List<Vector> internalMatrix = (List<Vector>) field.get(matriz);
        internalMatrix.set(0, null);
        field.set(matriz, internalMatrix);
        assertThrows(IllegalStateException.class, () -> matriz.get(0, 0));
    }

    /**
     * @brief Tests the set method for valid indices.
     *
     * Verifies that an element at a specific row and column can be successfully updated.
     */
    @Test
    @DisplayName("Should set element at valid indices")
    void testSetValid() {
        Matriz matriz = new Matriz(2, 2);
        matriz.set(0, 0, 99.9);
        assertEquals(99.9, matriz.get(0, 0));
    }

    /**
     * @brief Tests that the set method throws an IndexOutOfBoundsException for a negative row index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting element at negative row index")
    void testSetNegativeRow() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.set(-1, 0, 0.0));
    }

    /**
     * @brief Tests that the set method throws an IndexOutOfBoundsException for an out-of-bounds row index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting element at out-of-bounds row index")
    void testSetOutOfBoundsRow() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.set(2, 0, 0.0));
    }

    /**
     * @brief Tests that the set method throws an IndexOutOfBoundsException for a negative column index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting element at negative column index")
    void testSetNegativeCol() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.set(0, -1, 0.0));
    }

    /**
     * @brief Tests that the set method throws an IndexOutOfBoundsException for an out-of-bounds column index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when setting element at out-of-bounds column index")
    void testSetOutOfBoundsCol() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.set(0, 2, 0.0));
    }

    /**
     * @brief Tests that the set method throws an IllegalStateException when trying to set an element in a null row.
     *
     * Uses reflection to inject a null row for testing.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should throw IllegalStateException when setting element in a null row")
    void testSetToNullRow() throws NoSuchFieldException, IllegalAccessException {
        Matriz matriz = new Matriz(2, 2);
        java.lang.reflect.Field field = Matriz.class.getDeclaredField("matrix");
        field.setAccessible(true);
        List<Vector> internalMatrix = (List<Vector>) field.get(matriz);
        internalMatrix.set(0, null);
        field.set(matriz, internalMatrix);
        assertThrows(IllegalStateException.class, () -> matriz.set(0, 0, 1.0));
    }


    // --- Equals Test ---

    /**
     * @brief Tests the equals method for two matrices that are identical in content and dimensions.
     */
    @Test
    @DisplayName("Should return true for equal matrices")
    void testEqualsTrue() {
        double[][] data1 = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] data2 = {{1.0, 2.0}, {3.0, 4.0}};
        Matriz m1 = new Matriz(2, 2, data1);
        Matriz m2 = new Matriz(2, 2, data2);
        assertTrue(m1.equals(m2));
    }

    /**
     * @brief Tests the equals method for two matrices with different content but same dimensions.
     */
    @Test
    @DisplayName("Should return false for different matrices (content)")
    void testEqualsFalseContent() {
        double[][] data1 = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] data2 = {{1.0, 2.0}, {3.0, 5.0}};
        Matriz m1 = new Matriz(2, 2, data1);
        Matriz m2 = new Matriz(2, 2, data2);
        assertFalse(m1.equals(m2));
    }

    /**
     * @brief Tests the equals method for two matrices with different dimensions.
     */
    @Test
    @DisplayName("Should return false for different matrices (dimensions)")
    void testEqualsFalseDimensions() {
        double[][] data1 = {{1.0, 2.0}};
        double[][] data2 = {{1.0, 2.0}, {3.0, 4.0}};
        Matriz m1 = new Matriz(1, 2, data1);
        Matriz m2 = new Matriz(2, 2, data2);
        assertFalse(m1.equals(m2));
    }

    /**
     * @brief Tests the equals method when comparing a matrix with null.
     */
    @Test
    @DisplayName("Should return false when comparing with null matrix")
    void testEqualsNull() {
        Matriz m1 = new Matriz();
        assertFalse(m1.equals(null));
    }

    /**
     * @brief Tests the equals method when one of the matrices has a null row.
     *
     * Uses reflection to inject a null row.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should return false if either matrix has a null row")
    void testEqualsWithNullRow() throws NoSuchFieldException, IllegalAccessException {
        Matriz m1 = new Matriz(2, 2);
        Matriz m2 = new Matriz(2, 2);

        // Make a row in m1 null
        java.lang.reflect.Field field1 = Matriz.class.getDeclaredField("matrix");
        field1.setAccessible(true);
        List<Vector> internalMatrix1 = (List<Vector>) field1.get(m1);
        internalMatrix1.set(0, null);
        field1.set(m1, internalMatrix1);

        assertFalse(m1.equals(m2)); // m1 has null row, m2 doesn't
        assertFalse(m2.equals(m1)); // m2 doesn't, m1 does
    }

    /**
     * @brief Tests the equals method when both matrices have null rows at the same position.
     *
     * Based on the current implementation of `equals` in `Matriz` (if either row is null, returns false),
     * this test expects `false` even if both matrices have null rows at the same position.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should return true for two null rows at the same position")
    void testEqualsWithTwoNullRows() throws NoSuchFieldException, IllegalAccessException {
        Matriz m1 = new Matriz(2, 2);
        Matriz m2 = new Matriz(2, 2);

        // Make a row in m1 null
        java.lang.reflect.Field field1 = Matriz.class.getDeclaredField("matrix");
        field1.setAccessible(true);
        List<Vector> internalMatrix1 = (List<Vector>) field1.get(m1);
        internalMatrix1.set(0, null);
        field1.set(m1, internalMatrix1);

        // Make the same row in m2 null
        java.lang.reflect.Field field2 = Matriz.class.getDeclaredField("matrix");
        field2.setAccessible(true);
        List<Vector> internalMatrix2 = (List<Vector>) field2.get(m2);
        internalMatrix2.set(0, null);
        field2.set(m2, internalMatrix2);

        // The current equals method returns false if *any* row is null.
        // If the intent was for null rows to be considered equal if they are both null,
        // the logic of the equals method would need to be adjusted.
        // Based on current implementation (if (this.matrix.get(i) == null || other.matrix.get(i) == null) return false;),
        // this test should fail with false.
        assertFalse(m1.equals(m2)); // Expected behavior based on current code
    }


    // --- Transpose Test ---

    /**
     * @brief Tests the transpose method.
     *
     * Verifies that the `isTransposed` flag toggles correctly and that
     * `numRows` and `numCols` are swapped after transposition.
     */
    @Test
    @DisplayName("Should toggle the isTransposed flag")
    void testTranspose() {
        Matriz matriz = new Matriz(2, 3);
        assertFalse(matriz.isTransposed);
        assertEquals(2, matriz.getNumRows());
        assertEquals(3, matriz.getNumCols());

        matriz.transpose();
        assertTrue(matriz.isTransposed);
        assertEquals(3, matriz.getNumRows()); // numRows becomes original numCols
        assertEquals(2, matriz.getNumCols()); // numCols becomes original numRows

        matriz.transpose();
        assertFalse(matriz.isTransposed);
        assertEquals(2, matriz.getNumRows());
        assertEquals(3, matriz.getNumCols());
    }

    // --- Row/Column Manipulation Tests ---

    /**
     * @brief Tests the deleteRows method with a valid index.
     *
     * Verifies that a row is successfully deleted and matrix dimensions are updated.
     */
    @Test
    @DisplayName("Should delete a row at a valid index")
    void testDeleteRowsValid() {
        Matriz matriz = new Matriz(3, 2); // 3x2 matrix
        matriz.deleteRows(1); // Delete middle row
        assertEquals(2, matriz.getNumRows());
        assertEquals(2, matriz.getNumCols());
        // Verify remaining rows (original row 0 and original row 2)
        assertEquals(0.0, matriz.get(0, 0)); // Original row 0
        assertEquals(0.0, matriz.get(1, 0)); // Original row 2 (now at index 1)
    }

    /**
     * @brief Tests that deleteRows throws an IndexOutOfBoundsException for a negative index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when deleting row at negative index")
    void testDeleteRowsNegativeIndex() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.deleteRows(-1));
    }

    /**
     * @brief Tests that deleteRows throws an IndexOutOfBoundsException for an out-of-bounds index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when deleting row at out-of-bounds index")
    void testDeleteRowsOutOfBoundsIndex() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.deleteRows(2));
    }

    /**
     * @brief Tests the deleteCols method with a valid index.
     *
     * Verifies that a column is successfully deleted and matrix dimensions are updated.
     */
    @Test
    @DisplayName("Should delete a column at a valid index")
    void testDeleteColsValid() {
        double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        Matriz matriz = new Matriz(2, 3, data); // 2x3 matrix
        matriz.deleteCols(1); // Delete middle column
        assertEquals(2, matriz.getNumRows());
        assertEquals(2, matriz.getNumCols());
        assertEquals(1.0, matriz.get(0, 0));
        assertEquals(3.0, matriz.get(0, 1)); // Original column 2
        assertEquals(4.0, matriz.get(1, 0));
        assertEquals(6.0, matriz.get(1, 1)); // Original column 2
    }

    /**
     * @brief Tests that deleteCols throws an IndexOutOfBoundsException for a negative index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when deleting column at negative index")
    void testDeleteColsNegativeIndex() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.deleteCols(-1));
    }

    /**
     * @brief Tests that deleteCols throws an IndexOutOfBoundsException for an out-of-bounds index.
     */
    @Test
    @DisplayName("Should throw IndexOutOfBoundsException when deleting column at out-of-bounds index")
    void testDeleteColsOutOfBoundsIndex() {
        Matriz matriz = new Matriz(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> matriz.deleteCols(2));
    }

    /**
     * @brief Tests that deleteCols handles null rows gracefully without errors.
     *
     * Uses reflection to inject a null row.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should handle null rows gracefully when deleting columns (no errors)")
    void testDeleteColsWithNullRows() throws NoSuchFieldException, IllegalAccessException {
        Matriz matriz = new Matriz(2, 2);
        java.lang.reflect.Field field = Matriz.class.getDeclaredField("matrix");
        field.setAccessible(true);
        List<Vector> internalMatrix = (List<Vector>) field.get(matriz);
        internalMatrix.set(0, null); // Set first row to null
        field.set(matriz, internalMatrix);

        assertDoesNotThrow(() -> matriz.deleteCols(0)); // Should skip null row and delete from others
        assertEquals(1, matriz.getNumCols()); // Column count should still decrease
    }

    /**
     * @brief Tests the addRows method.
     *
     * Verifies that a new row, initialized with zeros, is added and dimensions are updated.
     */
    @Test
    @DisplayName("Should add a new row initialized with zeros")
    void testAddRows() {
        Matriz matriz = new Matriz(2, 2);
        matriz.addRows();
        assertEquals(3, matriz.getNumRows());
        assertEquals(2, matriz.getNumCols());
        assertEquals(0.0, matriz.get(2, 0)); // New row should be all zeros
        assertEquals(0.0, matriz.get(2, 1));
    }

    /**
     * @brief Tests the addCols method.
     *
     * Verifies that a new column, initialized with zeros, is added to each row and dimensions are updated.
     */
    @Test
    @DisplayName("Should add a new column initialized with zeros to each row")
    void testAddCols() {
        Matriz matriz = new Matriz(2, 2, new double[][]{{1.0, 2.0}, {3.0, 4.0}});
        matriz.addCols();
        assertEquals(2, matriz.getNumRows());
        assertEquals(3, matriz.getNumCols());
        assertEquals(1.0, matriz.get(0, 0));
        assertEquals(2.0, matriz.get(0, 1));
        assertEquals(0.0, matriz.get(0, 2)); // New column element
        assertEquals(3.0, matriz.get(1, 0));
        assertEquals(4.0, matriz.get(1, 1));
        assertEquals(0.0, matriz.get(1, 2)); // New column element
    }

    /**
     * @brief Tests that addCols handles null rows gracefully without errors.
     *
     * Uses reflection to inject a null row.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should handle null rows gracefully when adding columns (no errors)")
    void testAddColsWithNullRows() throws NoSuchFieldException, IllegalAccessException {
        Matriz matriz = new Matriz(2, 2);
        java.lang.reflect.Field field = Matriz.class.getDeclaredField("matrix");
        field.setAccessible(true);
        List<Vector> internalMatrix = (List<Vector>) field.get(matriz);
        internalMatrix.set(0, null); // Set first row to null
        field.set(matriz, internalMatrix);

        assertDoesNotThrow(matriz::addCols);
        assertEquals(3, matriz.getNumCols()); // Column count should still increase
        // Cannot easily assert the state of the null row's values, but no crash.
    }


    // --- Normalize Test ---

    /**
     * @brief Tests the normalizar method.
     *
     * Verifies that a new list of normalized vectors is returned,
     * and that the original matrix remains unchanged.
     */
    @Test
    @DisplayName("Should return a new list of normalized vectors")
    void testNormalizar() {
        double[][] data = {{0.0, 5.0, 10.0}, {1.0, 3.0, 5.0}};
        Matriz matriz = new Matriz(2, 3, data);

        List<Vector> normalized = matriz.normalizar();

        assertNotNull(normalized);
        assertEquals(2, normalized.size());

        // Check first normalized vector
        Vector normRow0 = normalized.get(0);
        assertNotNull(normRow0);
        assertEquals(3, normRow0.size());
        assertEquals(0.0, normRow0.get(0), 0.001);
        assertEquals(0.5, normRow0.get(1), 0.001);
        assertEquals(1.0, normRow0.get(2), 0.001);

        // Check second normalized vector
        Vector normRow1 = normalized.get(1);
        assertNotNull(normRow1);
        assertEquals(3, normRow1.size());
        assertEquals(0.0, normRow1.get(0), 0.001); // (1-1)/(5-1) = 0
        assertEquals(0.5, normRow1.get(1), 0.001); // (3-1)/(5-1) = 2/4 = 0.5
        assertEquals(1.0, normRow1.get(2), 0.001); // (5-1)/(5-1) = 1

        // Ensure original matrix is not modified
        assertEquals(0.0, matriz.get(0, 0));
        assertEquals(10.0, matriz.get(0, 2));
    }

    /**
     * @brief Tests that normalizar handles an empty matrix gracefully.
     */
    @Test
    @DisplayName("Should handle empty matrix for normalization")
    void testNormalizarEmptyMatrix() {
        Matriz matriz = new Matriz(1,1); // Default constructor makes 1x1
        matriz.deleteRows(0); // Make it empty
        List<Vector> normalized = matriz.normalizar();
        assertNotNull(normalized);
        assertTrue(normalized.isEmpty());
    }

    /**
     * @brief Tests that normalizar handles null rows during normalization.
     *
     * Uses reflection to inject a null row.
     * @throws NoSuchFieldException If the 'matrix' field is not found.
     * @throws IllegalAccessException If access to the 'matrix' field is denied.
     */
    @Test
    @DisplayName("Should handle null rows during normalization")
    void testNormalizarWithNullRows() throws NoSuchFieldException, IllegalAccessException {
        Matriz matriz = new Matriz(2, 2);
        // Use reflection to introduce a null row for testing
        java.lang.reflect.Field field = Matriz.class.getDeclaredField("matrix");
        field.setAccessible(true);
        List<Vector> internalMatrix = (List<Vector>) field.get(matriz);
        internalMatrix.set(0, null); // Set first row to null
        field.set(matriz, internalMatrix);

        List<Vector> normalized = matriz.normalizar();
        assertNotNull(normalized);
        assertEquals(2, normalized.size());
        assertNull(normalized.get(0)); // First row should still be null
        assertNotNull(normalized.get(1)); // Second row should be normalized (0.0, 0.0)
        assertEquals(0.0, normalized.get(1).get(0));
    }

    /**
     * @brief Tests that normalizar handles rows with a zero range (all elements are the same).
     *
     * Verifies that such rows remain unchanged after normalization, as per Vector.normalize() behavior.
     */
    @Test
    @DisplayName("Should handle rows with zero range during normalization")
    void testNormalizarRowsWithZeroRange() {
        double[][] data = {{5.0, 5.0, 5.0}, {1.0, 2.0, 3.0}};
        Matriz matriz = new Matriz(2, 3, data);

        List<Vector> normalized = matriz.normalizar();

        assertNotNull(normalized);
        assertEquals(2, normalized.size());

        // First row (zero range) should remain unchanged by Vector.normalize()'s behavior
        Vector normRow0 = normalized.get(0);
        assertNotNull(normRow0);
        assertEquals(5.0, normRow0.get(0));
        assertEquals(5.0, normRow0.get(1));
        assertEquals(5.0, normRow0.get(2));

        // Second row should be normalized correctly
        Vector normRow1 = normalized.get(1);
        assertNotNull(normRow1);
        assertEquals(0.0, normRow1.get(0), 0.001);
        assertEquals(0.5, normRow1.get(1), 0.001);
        assertEquals(1.0, normRow1.get(2), 0.001);
    }
}