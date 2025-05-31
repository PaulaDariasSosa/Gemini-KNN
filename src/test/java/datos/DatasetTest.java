package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import vectores.Vector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @file DatasetTest.java
 * @brief Clase de pruebas unitarias para la clase Dataset.
 *
 * Esta clase contiene pruebas JUnit 5 para verificar la correcta funcionalidad
 * de la clase Dataset, incluyendo sus constructores, métodos de manipulación
 * de datos (atributos y casos), y operaciones de lectura/escritura de archivos.
 */
@DisplayName("Tests de la Clase Dataset")
class DatasetTest {

    private Dataset dataset;

    /**
     * @brief Directorio temporal para la creación de archivos de prueba.
     *
     * Anotado con @TempDir, JUnit creará y limpiará automáticamente este directorio
     * para cada método de prueba que lo necesite.
     */
    @TempDir
    File tempDir;

    /**
     * @brief Configuración inicial antes de cada prueba.
     *
     * Inicializa una nueva instancia de Dataset antes de la ejecución de cada test,
     * asegurando un estado limpio para cada prueba.
     */
    @BeforeEach
    void setUp() {
        dataset = new Dataset();
    }

    // --- Constructor Tests ---

    /**
     * @brief Prueba el constructor sin argumentos de la clase Dataset.
     *
     * Verifica que se cree una instancia de Dataset no nula, con una lista de atributos
     * vacía pero no nula, y que el valor de preprocesado por defecto sea 0.
     */
    @Test
    @DisplayName("Debería crear un Dataset vacío con el constructor sin argumentos")
    void testConstructorVacio() {
        assertNotNull(dataset);
        assertNotNull(dataset.getAtributos());
        assertTrue(dataset.getAtributos().isEmpty());
        assertEquals(0, dataset.getPreprocesado()); // Valor por defecto
    }

    /**
     * @brief Prueba el constructor de Dataset que acepta una lista de atributos.
     *
     * Verifica que el Dataset se inicialice correctamente con la lista de atributos
     * proporcionada, que la lista interna sea una copia defensiva (nueva instancia),
     * pero que los objetos Atributo sean las mismas referencias.
     */
    @Test
    @DisplayName("Debería crear un Dataset con una lista de atributos existente")
    void testConstructorConListaAtributos() {
        Atributo attr1 = new Cuantitativo("Edad");
        Atributo attr2 = new Cualitativo("Genero");
        List<Atributo> atributosIniciales = new ArrayList<>(Arrays.asList(attr1, attr2));

        Dataset newDataset = new Dataset(atributosIniciales);
        assertNotNull(newDataset);
        assertEquals(2, newDataset.numeroAtributos());
        assertEquals("Edad", newDataset.getAtributos().get(0).getNombre());
        assertEquals("Genero", newDataset.getAtributos().get(1).getNombre());
        // CORRECCIÓN: Ahora se espera que la lista interna sea una nueva instancia, pero los elementos sean los mismos.
        assertNotSame(atributosIniciales, newDataset.getAtributos());
        assertSame(attr1, newDataset.getAtributos().get(0)); // Las referencias a los atributos son las mismas
    }

    /**
     * @brief Prueba el constructor de Dataset que lee datos desde un archivo.
     *
     * Crea un archivo CSV temporal, lo escribe con datos de prueba y luego
     * verifica que el constructor lea e interprete correctamente los datos,
     * incluyendo el número de atributos y casos, y la inferencia de tipos (Cuantitativo/Cualitativo).
     * @throws IOException Si ocurre un error durante la creación o lectura del archivo.
     */
    @Test
    @DisplayName("Debería crear un Dataset leyendo desde un archivo")
    void testConstructorConFilename() throws IOException {
        // Crear un archivo CSV de prueba
        File testCsvFile = new File(tempDir, "test_data.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testCsvFile))) {
            writer.write("Attr1,Attr2,Attr3\n");
            writer.write("10.5,textoA,20.0\n"); // Usar .0 para consistencia con String.format
            writer.write("11.0,textoB,22.5\n");
            writer.write("12.0,textoA,25.0\n");
        }

        Dataset datasetFromFile = new Dataset(testCsvFile.getAbsolutePath());
        assertNotNull(datasetFromFile);
        assertEquals(3, datasetFromFile.numeroAtributos());
        assertEquals(3, datasetFromFile.numeroCasos());

        // Verificar nombres de atributos
        List<String> nombres = datasetFromFile.nombreAtributos();
        assertEquals("Attr1", nombres.get(0));
        assertEquals("Attr2", nombres.get(1));
        assertEquals("Attr3", nombres.get(2));

        // Verificar tipos de atributos y valores
        assertTrue(datasetFromFile.getAtributos().get(0) instanceof Cuantitativo);
        assertEquals(10.5, ((Cuantitativo) datasetFromFile.getAtributos().get(0)).getValor(0));
        assertEquals(11.0, ((Cuantitativo) datasetFromFile.getAtributos().get(0)).getValor(1));
        assertEquals(12.0, ((Cuantitativo) datasetFromFile.getAtributos().get(0)).getValor(2));

        assertTrue(datasetFromFile.getAtributos().get(1) instanceof Cualitativo);
        assertEquals("textoA", ((Cualitativo) datasetFromFile.getAtributos().get(1)).getValor(0));
        assertEquals("textoB", ((Cualitativo) datasetFromFile.getAtributos().get(1)).getValor(1));
        assertEquals("textoA", ((Cualitativo) datasetFromFile.getAtributos().get(1)).getValor(2));

        assertTrue(datasetFromFile.getAtributos().get(2) instanceof Cuantitativo);
        assertEquals(20.0, ((Cuantitativo) datasetFromFile.getAtributos().get(2)).getValor(0));
        assertEquals(22.5, ((Cuantitativo) datasetFromFile.getAtributos().get(2)).getValor(1));
        assertEquals(25.0, ((Cuantitativo) datasetFromFile.getAtributos().get(2)).getValor(2));
    }

    /**
     * @brief Prueba el constructor de copia superficial de la clase Dataset.
     *
     * Verifica que al copiar un Dataset, la nueva instancia tenga su propia lista
     * de atributos, pero que los objetos Atributo dentro de esa lista sean los
     * mismos objetos (copia superficial). También verifica la copia del estado de preprocesado.
     */
    @Test
    @DisplayName("Debería crear una copia superficial de otro Dataset usando el constructor de copia")
    void testConstructorCopia() {
        Cuantitativo attr1 = new Cuantitativo("X"); // Usar el tipo concreto
        attr1.add(10.0);
        attr1.add(20.0);
        Cualitativo attr2 = new Cualitativo("Y"); // Usar el tipo concreto
        attr2.add("A");
        attr2.add("B");

        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);
        dataset.setPreprocesado(1);

        Dataset copiedDataset = new Dataset(dataset);
        assertNotNull(copiedDataset);
        assertEquals(2, copiedDataset.numeroAtributos());
        assertEquals(1, copiedDataset.getPreprocesado());
        assertNotSame(dataset.getAtributos(), copiedDataset.getAtributos()); // La lista de atributos es una nueva instancia
        assertSame(attr1, copiedDataset.getAtributos().get(0)); // Los atributos individuales son los mismos objetos (copia superficial)
        assertSame(attr2, copiedDataset.getAtributos().get(1));
    }

    // --- Métodos de Manipulación de Pesos ---

    /**
     * @brief Prueba el método cambiarPeso(List<String>).
     *
     * Verifica que los pesos de los atributos se actualicen correctamente
     * cuando se proporciona una lista de strings que representan los nuevos pesos.
     */
    @Test
    @DisplayName("Debería cambiar los pesos de los atributos correctamente")
    void testCambiarPesoListaString() {
        Atributo attr1 = new Cuantitativo("A");
        Atributo attr2 = new Cualitativo("B");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        List<String> nuevosPesos = Arrays.asList("0.2", "0.8");
        dataset.cambiarPeso(nuevosPesos);

        assertEquals(0.2, dataset.getAtributos().get(0).getPeso(), 0.001);
        assertEquals(0.8, dataset.getAtributos().get(1).getPeso(), 0.001);
    }

    /**
     * @brief Prueba el método cambiarPeso(List<String>) cuando el número de pesos no coincide.
     *
     * Verifica que se lance una IllegalArgumentException si el número de pesos en la lista
     * proporcionada no coincide con el número de atributos en el Dataset.
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el número de pesos no coincide")
    void testCambiarPesoListaStringNumeroNoCoincide() {
        Atributo attr1 = new Cuantitativo("A");
        dataset.getAtributos().add(attr1);
        List<String> nuevosPesos = Arrays.asList("0.2", "0.8");
        assertThrows(IllegalArgumentException.class, () -> dataset.cambiarPeso(nuevosPesos));
    }

    /**
     * @brief Prueba el método cambiarPeso(List<String>) con un formato de peso inválido.
     *
     * Verifica que se lance una IllegalArgumentException si alguno de los strings
     * de la lista no puede ser parseado como un número de punto flotante válido.
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si un peso no es un número válido")
    void testCambiarPesoListaStringFormatoInvalido() {
        Atributo attr1 = new Cuantitativo("A");
        Atributo attr2 = new Cualitativo("B");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);
        List<String> nuevosPesos = Arrays.asList("0.5", "invalid");
        assertThrows(IllegalArgumentException.class, () -> dataset.cambiarPeso(nuevosPesos));
    }

    /**
     * @brief Prueba el método cambiarPeso(List<String>) con un peso fuera del rango [0, 1].
     *
     * Verifica que se lance una IllegalArgumentException si alguno de los pesos
     * proporcionados está fuera del rango permitido (0 a 1).
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si un peso está fuera del rango [0, 1]")
    void testCambiarPesoListaStringRangoInvalido() {
        Atributo attr1 = new Cuantitativo("A");
        Atributo attr2 = new Cualitativo("B");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);
        List<String> nuevosPesos = Arrays.asList("0.5", "1.5");
        assertThrows(IllegalArgumentException.class, () -> dataset.cambiarPeso(nuevosPesos));
    }

    /**
     * @brief Prueba el método cambiarPeso(int, double).
     *
     * Verifica que el peso de un atributo específico se actualice correctamente
     * dado su índice y un nuevo valor de peso.
     */
    @Test
    @DisplayName("Debería cambiar el peso de un atributo específico por índice")
    void testCambiarPesoPorIndice() {
        Atributo attr1 = new Cuantitativo("A");
        attr1.setPeso(0.5);
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(new Cualitativo("B"));

        dataset.cambiarPeso(0, 0.9);
        assertEquals(0.9, dataset.getAtributos().get(0).getPeso(), 0.001);
        assertEquals(1.0, dataset.getAtributos().get(1).getPeso(), 0.001); // El otro no cambia
    }

    /**
     * @brief Prueba el método cambiarPeso(int, double) con un índice inválido.
     *
     * Verifica que se lance una IndexOutOfBoundsException si el índice proporcionado
     * para cambiar el peso de un atributo está fuera de los límites.
     */
    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al cambiar peso por índice inválido")
    void testCambiarPesoPorIndiceInvalido() {
        dataset.getAtributos().add(new Cuantitativo("A"));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.cambiarPeso(1, 0.5));
    }

    /**
     * @brief Prueba el método cambiarPeso(int, double) con un peso fuera del rango [0, 1].
     *
     * Verifica que se lance una IllegalArgumentException si el peso proporcionado
     * para un atributo está fuera del rango permitido (0 a 1).
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException al cambiar peso por índice con peso fuera de rango")
    void testCambiarPesoPorIndiceRangoInvalido() {
        dataset.getAtributos().add(new Cuantitativo("A"));
        assertThrows(IllegalArgumentException.class, () -> dataset.cambiarPeso(0, 1.5));
    }

    /**
     * @brief Prueba el método cambiarPeso(double).
     *
     * Verifica que el peso de todos los atributos en el Dataset se establezca
     * al valor único proporcionado.
     */
    @Test
    @DisplayName("Debería cambiar el peso de todos los atributos a un valor único")
    void testCambiarPesoTodos() {
        dataset.getAtributos().add(new Cuantitativo("A"));
        dataset.getAtributos().add(new Cualitativo("B"));
        dataset.getAtributos().add(new Cuantitativo("C"));

        dataset.cambiarPeso(0.75);
        assertEquals(0.75, dataset.getAtributos().get(0).getPeso(), 0.001);
        assertEquals(0.75, dataset.getAtributos().get(1).getPeso(), 0.001);
        assertEquals(0.75, dataset.getAtributos().get(2).getPeso(), 0.001);
    }

    /**
     * @brief Prueba el método cambiarPeso(double) con un peso fuera del rango [0, 1].
     *
     * Verifica que se lance una IllegalArgumentException si el peso único
     * proporcionado está fuera del rango permitido (0 a 1).
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException al cambiar peso a todos con peso fuera de rango")
    void testCambiarPesoTodosRangoInvalido() {
        dataset.getAtributos().add(new Cuantitativo("A"));
        assertThrows(IllegalArgumentException.class, () -> dataset.cambiarPeso(1.5));
    }

    // --- Métodos de Manipulación de Casos (Instancias) ---

    /**
     * @brief Prueba el método add(Instancia).
     *
     * Verifica que una nueva Instancia se añada correctamente a los atributos
     * correspondientes del Dataset, incrementando el número de casos.
     */
    @Test
    @DisplayName("Debería añadir una nueva instancia a los atributos existentes")
    void testAddInstancia() {
        Cuantitativo attr1 = new Cuantitativo("Num");
        attr1.add(1.0);
        Cualitativo attr2 = new Cualitativo("Cat");
        attr2.add("X");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        List<Object> valoresNuevaInstancia = Arrays.asList(2.0, "Y");
        Instancia nueva = new Instancia(valoresNuevaInstancia);
        dataset.add(nueva);

        assertEquals(2, attr1.size()); // Ahora 2 casos en Cuantitativo
        assertEquals(2, attr2.size()); // Ahora 2 casos en Cualitativo
        assertEquals(2.0, (Double) attr1.getValor(1));
        assertEquals("Y", (String) attr2.getValor(1));
    }

    /**
     * @brief Prueba el método add(Instancia) con una instancia de tamaño incorrecto.
     *
     * Verifica que se lance una IllegalArgumentException si la Instancia a añadir
     * no tiene el mismo número de valores que el número de atributos del Dataset.
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la instancia tiene un tamaño incorrecto")
    void testAddInstanciaTamanioIncorrecto() {
        Cuantitativo attr1 = new Cuantitativo("Num");
        dataset.getAtributos().add(attr1); // Dataset tiene 1 atributo

        List<Object> valoresNuevaInstancia = Arrays.asList(2.0, "Y"); // Instancia tiene 2 valores
        Instancia nueva = new Instancia(valoresNuevaInstancia);
        // CORRECCIÓN: Esperamos IllegalArgumentException ahora
        assertThrows(IllegalArgumentException.class, () -> dataset.add(nueva));
    }

    /**
     * @brief Prueba el método add(Instancia) con una instancia nula.
     *
     * Verifica que se lance una IllegalArgumentException si la Instancia a añadir es nula.
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la instancia es nula")
    void testAddInstanciaNula() {
        dataset.getAtributos().add(new Cuantitativo("Num"));
        assertThrows(IllegalArgumentException.class, () -> dataset.add((Instancia) null));
    }


    /**
     * @brief Prueba el método add(List<String>).
     *
     * Verifica que una nueva fila de valores (proporcionada como una lista de strings)
     * se añada correctamente a los atributos, con la conversión de tipos adecuada
     * (e.g., "25.0" a Double para Cuantitativo, "Madrid" a String para Cualitativo).
     */
    @Test
    @DisplayName("Debería añadir una nueva fila de valores (String) convirtiendo según el tipo de atributo")
    void testAddListaString() {
        Cuantitativo attr1 = new Cuantitativo("Edad");
        Cualitativo attr2 = new Cualitativo("Ciudad");
        Cuantitativo attr3 = new Cuantitativo("Ingresos");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);
        dataset.getAtributos().add(attr3);

        List<String> nuevaFila = Arrays.asList("25.0", "Madrid", "50000.0"); // Usar .0 para doubles
        dataset.add(nuevaFila);

        assertEquals(1, attr1.size());
        assertEquals(1, attr2.size());
        assertEquals(1, attr3.size());

        assertEquals(25.0, (Double) attr1.getValor(0));
        assertEquals("Madrid", (String) attr2.getValor(0));
        assertEquals(50000.0, (Double) attr3.getValor(0));

        // Añadir otra fila
        List<String> otraFila = Arrays.asList("30.0", "Barcelona", "60000.75");
        dataset.add(otraFila);

        assertEquals(2, attr1.size());
        assertEquals(2, attr2.size());
        assertEquals(2, attr3.size());
        assertEquals(30.0, (Double) attr1.getValor(1));
        assertEquals("Barcelona", (String) attr2.getValor(1));
        assertEquals(60000.75, (Double) attr3.getValor(1));
    }

    /**
     * @brief Prueba el método add(List<String>) con una lista de strings de tamaño incorrecto.
     *
     * Verifica que se lance una IllegalArgumentException si el número de valores en la lista
     * de strings no coincide con el número de atributos del Dataset.
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la lista de strings tiene tamaño incorrecto")
    void testAddListaStringTamanioIncorrecto() {
        Cuantitativo attr1 = new Cuantitativo("Num");
        dataset.getAtributos().add(attr1);
        List<String> nuevaFila = Arrays.asList("25.0", "Extra");
        assertThrows(IllegalArgumentException.class, () -> dataset.add(nuevaFila));
    }

    /**
     * @brief Prueba el método add(List<String>) con una lista de strings nula.
     *
     * Verifica que se lance una IllegalArgumentException si la lista de strings a añadir es nula.
     */
    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si la lista de strings es nula")
    void testAddListaStringNula() {
        dataset.getAtributos().add(new Cuantitativo("Num"));
        assertThrows(IllegalArgumentException.class, () -> dataset.add((List<String>) null));
    }


    /**
     * @brief Prueba el método add(List<String>) con un tipo de valor incompatible para un atributo cuantitativo.
     *
     * Verifica que se lance una ClassCastException (o similar) si se intenta añadir
     * un valor no numérico a un atributo de tipo Cuantitativo.
     */
    @Test
    @DisplayName("Debería lanzar ClassCastException si un valor no numérico se añade a un atributo cuantitativo")
    void testAddListaStringTipoIncompatibleCuantitativo() {
        Cuantitativo attr1 = new Cuantitativo("Numero");
        dataset.getAtributos().add(attr1);
        List<String> nuevaFila = Arrays.asList("noEsUnNumero");
        // Se espera ClassCastException desde Cuantitativo.add(Object valor) si el parseo falla y se intenta añadir String.
        assertThrows(ClassCastException.class, () -> dataset.add(nuevaFila));
    }

    /**
     * @brief Prueba el método add(List<String>) con un tipo de valor incompatible para un atributo cualitativo.
     *
     * Verifica que se lance una ClassCastException (o similar) si se intenta añadir
     * un valor numérico (que se parsea a Double) a un atributo de tipo Cualitativo.
     */
    @Test
    @DisplayName("Debería lanzar ClassCastException si un valor numérico se añade a un atributo cualitativo")
    void testAddListaStringTipoIncompatibleCualitativo() {
        Cualitativo attr1 = new Cualitativo("Texto");
        dataset.getAtributos().add(attr1);
        List<String> nuevaFila = Arrays.asList("123.45"); // Se parsea a Double, luego se intenta añadir a Cualitativo
        // Se espera ClassCastException desde Cualitativo.add(Object valor) si el valor no es String
        assertThrows(ClassCastException.class, () -> dataset.add(nuevaFila));
    }

    /**
     * @brief Prueba el método delete(int).
     *
     * Verifica que un caso (fila completa de valores) se elimine correctamente
     * de todos los atributos dado su índice.
     */
    @Test
    @DisplayName("Debería eliminar un caso (fila) por índice")
    void testDelete() {
        Cuantitativo attr1 = new Cuantitativo("A");
        attr1.add(1.0); attr1.add(2.0); attr1.add(3.0);
        Cualitativo attr2 = new Cualitativo("B");
        attr2.add("X"); attr2.add("Y"); attr2.add("Z");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        assertEquals(3, dataset.numeroCasos());
        dataset.delete(1); // Eliminar el segundo caso (índice 1)

        assertEquals(2, dataset.numeroCasos());
        assertEquals(1.0, (Double) attr1.getValor(0));
        assertEquals(3.0, (Double) attr1.getValor(1)); // El tercer caso ahora es el segundo

        assertEquals("X", (String) attr2.getValor(0));
        assertEquals("Z", (String) attr2.getValor(1)); // El tercer caso ahora es el segundo
    }

    /**
     * @brief Prueba el método delete(int) con un índice fuera de límites.
     *
     * Verifica que se lance una IndexOutOfBoundsException si el índice proporcionado
     * para eliminar un caso está fuera de los límites válidos del Dataset.
     */
    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al eliminar un caso fuera de límites")
    void testDeleteOutOfBounds() {
        Cuantitativo attr1 = new Cuantitativo("A");
        attr1.add(1.0);
        dataset.getAtributos().add(attr1);
        assertEquals(1, dataset.numeroCasos());
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.delete(1));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.delete(-1));
    }

    /**
     * @brief Prueba el método delete(int) en un Dataset vacío.
     *
     * Verifica que se lance una IndexOutOfBoundsException si se intenta eliminar
     * un caso de un Dataset que no contiene ningún caso.
     */
    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al eliminar un caso de un Dataset vacío")
    void testDeleteDatasetVacio() {
        assertEquals(0, dataset.numeroCasos());
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.delete(0));
    }

    // --- Métodos de Lectura/Escritura de Archivos ---

    /**
     * @brief Prueba los métodos write y read para la persistencia del Dataset.
     *
     * Prepara un Dataset con datos, lo escribe en un archivo CSV temporal,
     * y luego lee ese archivo en una nueva instancia de Dataset para verificar
     * que los datos se han guardado y cargado correctamente.
     * @throws IOException Si ocurre un error durante las operaciones de I/O.
     */
    @Test
    @DisplayName("Debería escribir el dataset en un archivo CSV y luego leerlo correctamente")
    void testWriteAndRead() throws IOException {
        // 1. Preparar Dataset para escribir
        Cuantitativo attrNum = new Cuantitativo("Numerico");
        attrNum.add(1.1); attrNum.add(2.2);
        Cualitativo attrCat = new Cualitativo("Categorico");
        attrCat.add("Roja"); attrCat.add("Azul");
        dataset.getAtributos().add(attrNum);
        dataset.getAtributos().add(attrCat);

        File outputFile = new File(tempDir, "output_data.csv");
        dataset.write(outputFile.getAbsolutePath());

        // 2. Leer el Dataset desde el archivo recién escrito
        Dataset readDataset = new Dataset();
        readDataset.read(outputFile.getAbsolutePath());

        // 3. Verificar que el dataset leído es igual al original
        assertEquals(2, readDataset.numeroAtributos());
        assertEquals(2, readDataset.numeroCasos());

        // Nombres de atributos
        assertEquals("Numerico", readDataset.nombreAtributos().get(0));
        assertEquals("Categorico", readDataset.nombreAtributos().get(1));

        // Tipos y valores
        assertTrue(readDataset.getAtributos().get(0) instanceof Cuantitativo);
        assertEquals(1.1, ((Cuantitativo) readDataset.getAtributos().get(0)).getValor(0));
        assertEquals(2.2, ((Cuantitativo) readDataset.getAtributos().get(0)).getValor(1));

        assertTrue(readDataset.getAtributos().get(1) instanceof Cualitativo);
        assertEquals("Roja", ((Cualitativo) readDataset.getAtributos().get(1)).getValor(0));
        assertEquals("Azul", ((Cualitativo) readDataset.getAtributos().get(1)).getValor(1));
    }

    /**
     * @brief Prueba el método read cuando se intenta leer un archivo vacío.
     *
     * Verifica que el Dataset resultante esté vacío (cero atributos y cero casos)
     * cuando se lee desde un archivo CSV sin contenido.
     * @throws IOException Si ocurre un error de I/O.
     */
    @Test
    @DisplayName("Debería manejar archivo vacío al leer")
    void testReadEmptyFile() throws IOException {
        File emptyFile = new File(tempDir, "empty.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(emptyFile))) {
            // Archivo vacío
        }
        Dataset emptyDataset = new Dataset();
        emptyDataset.read(emptyFile.getAbsolutePath());
        // CORRECCIÓN: Ahora el Dataset debería estar vacío
        assertEquals(0, emptyDataset.numeroAtributos());
        assertEquals(0, emptyDataset.numeroCasos()); // numeroCasos() ya no lanza error para dataset vacío
    }

    /**
     * @brief Prueba el método read cuando se intenta leer un archivo con solo el encabezado.
     *
     * Verifica que se creen los atributos basándose en el encabezado, pero que el Dataset
     * no contenga ningún caso, y que los atributos estén vacíos de valores.
     * @throws IOException Si ocurre un error de I/O.
     */
    @Test
    @DisplayName("Debería manejar archivo con solo encabezado al leer")
    void testReadHeaderOnlyFile() throws IOException {
        File headerOnlyFile = new File(tempDir, "header_only.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(headerOnlyFile))) {
            writer.write("Attr1,Attr2\n"); // Solo encabezado
        }
        Dataset headerOnlyDataset = new Dataset();
        headerOnlyDataset.read(headerOnlyFile.getAbsolutePath());
        // CORRECCIÓN: Ahora se espera que se creen los atributos por defecto (Cuantitativo)
        assertEquals(2, headerOnlyDataset.numeroAtributos());
        assertEquals("Attr1", headerOnlyDataset.nombreAtributos().get(0));
        assertEquals("Attr2", headerOnlyDataset.nombreAtributos().get(1));
        // Los atributos deben estar vacíos de valores
        assertTrue(headerOnlyDataset.getAtributos().get(0).size() == 0);
        assertTrue(headerOnlyDataset.getAtributos().get(1).size() == 0);
        assertEquals(0, headerOnlyDataset.numeroCasos()); // numeroCasos() ahora maneja esto
    }

    // --- Métodos de Acceso y Utilidad ---

    /**
     * @brief Prueba el método numeroAtributos().
     *
     * Verifica que el método devuelva el número correcto de atributos en el Dataset.
     */
    @Test
    @DisplayName("Debería retornar el número correcto de atributos")
    void testNumeroAtributos() {
        assertEquals(0, dataset.numeroAtributos());
        dataset.getAtributos().add(new Cuantitativo("A"));
        assertEquals(1, dataset.numeroAtributos());
        dataset.getAtributos().add(new Cualitativo("B"));
        assertEquals(2, dataset.numeroAtributos());
    }

    /**
     * @brief Prueba el método nombreAtributos().
     *
     * Verifica que el método devuelva una lista con los nombres de todos los atributos.
     */
    @Test
    @DisplayName("Debería retornar los nombres de los atributos")
    void testNombreAtributos() {
        dataset.getAtributos().add(new Cuantitativo("Attr1"));
        dataset.getAtributos().add(new Cualitativo("Attr2"));
        List<String> nombres = dataset.nombreAtributos();
        assertEquals(2, nombres.size());
        assertTrue(nombres.contains("Attr1"));
        assertTrue(nombres.contains("Attr2"));
    }

    /**
     * @brief Prueba el método getAtributos().
     *
     * Verifica que el método devuelva la lista interna de objetos Atributo
     * y que las referencias sean las mismas.
     */
    @Test
    @DisplayName("Debería retornar la lista de objetos Atributo")
    void testGetAtributos() {
        Atributo attr = new Cuantitativo("Test");
        dataset.getAtributos().add(attr);
        List<Atributo> retrievedAttrs = dataset.getAtributos();
        assertNotNull(retrievedAttrs);
        assertEquals(1, retrievedAttrs.size());
        assertSame(attr, retrievedAttrs.get(0)); // Verifica que sea la misma referencia
    }

    /**
     * @brief Prueba el método getAtributosEmpty().
     *
     * Verifica que el método devuelva una nueva lista de atributos, donde
     * cada atributo es una nueva instancia del mismo tipo, con el mismo nombre y peso,
     * pero con su lista de valores vacía.
     */
    @Test
    @DisplayName("Debería retornar una lista de atributos vacíos con los mismos nombres y pesos")
    void testGetAtributosEmpty() {
        Cuantitativo originalQuant = new Cuantitativo("Edad");
        originalQuant.add(25.0);
        originalQuant.setPeso(0.7);
        Cualitativo originalQual = new Cualitativo("Color");
        originalQual.add("Rojo");
        originalQual.setPeso(0.3);

        dataset.getAtributos().add(originalQuant);
        dataset.getAtributos().add(originalQual);

        List<Atributo> emptyAttrs = dataset.getAtributosEmpty();
        assertNotNull(emptyAttrs);
        assertEquals(2, emptyAttrs.size());

        assertTrue(emptyAttrs.get(0) instanceof Cuantitativo);
        assertEquals("Edad", emptyAttrs.get(0).getNombre());
        assertEquals(0.7, emptyAttrs.get(0).getPeso(), 0.001);
        assertEquals(0, emptyAttrs.get(0).size()); // Debe estar vacío

        assertTrue(emptyAttrs.get(1) instanceof Cualitativo);
        assertEquals("Color", emptyAttrs.get(1).getNombre());
        assertEquals(0.3, emptyAttrs.get(1).getPeso(), 0.001);
        assertEquals(0, emptyAttrs.get(1).size()); // Debe estar vacío

        assertNotSame(originalQuant, emptyAttrs.get(0)); // Debe ser una nueva instancia
        assertNotSame(originalQual, emptyAttrs.get(1)); // Debe ser una nueva instancia
    }

    /**
     * @brief Prueba el método numeroCasos().
     *
     * Verifica que el método devuelva el número correcto de casos (filas) en el Dataset,
     * que es el tamaño de los valores del primer atributo.
     */
    @Test
    @DisplayName("Debería retornar el número correcto de casos (filas)")
    void testNumeroCasos() {
        Cuantitativo attr1 = new Cuantitativo("A");
        Cualitativo attr2 = new Cualitativo("B");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        // Añadir casos para que numeroCasos funcione
        attr1.add(1.0); attr1.add(2.0); attr1.add(3.0);
        attr2.add("X"); attr2.add("Y"); attr2.add("Z");

        assertEquals(3, dataset.numeroCasos());
    }

    /**
     * @brief Prueba el método numeroCasos() en un Dataset vacío.
     *
     * Verifica que el método devuelva 0 si el Dataset no contiene atributos.
     */
    @Test
    @DisplayName("Debería retornar 0 si numeroCasos se llama en un Dataset vacío")
    void testNumeroCasosDatasetVacio() {
        assertEquals(0, dataset.numeroCasos());
    }

    /**
     * @brief Prueba el método getValores().
     *
     * Verifica que el método devuelva una lista de String que contiene todos los valores
     * del Dataset, serializados y concatenados por filas (aunque se devuelvan en una lista plana).
     */
    @Test
    @DisplayName("Debería retornar una lista de todos los valores del dataset serializados como String")
    void testGetValores() {
        Cuantitativo attr1 = new Cuantitativo("Num");
        attr1.add(10.0); attr1.add(20.0);
        Cualitativo attr2 = new Cualitativo("Cat");
        attr2.add("A"); attr2.add("B");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        // Los valores se obtienen por fila y luego concatenados
        // Fila 1: 10.0, A
        // Fila 2: 20.0, B
        // Resultado: [10.0, A, 20.0, B]
        List<String> valores = dataset.getValores();
        assertEquals(4, valores.size());
        assertEquals("10.0", valores.get(0));
        assertEquals("A", valores.get(1));
        assertEquals("20.0", valores.get(2));
        assertEquals("B", valores.get(3));
    }


    /**
     * @brief Prueba el método get(int) para obtener un atributo por índice.
     *
     * Verifica que el método devuelva el objeto Atributo correcto en el índice especificado.
     */
    @Test
    @DisplayName("Debería retornar un atributo específico por índice")
    void testGetAtributoPorIndice() {
        Atributo attr1 = new Cuantitativo("First");
        Atributo attr2 = new Cualitativo("Second");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        assertSame(attr1, dataset.get(0));
        assertSame(attr2, dataset.get(1));
    }

    /**
     * @brief Prueba el método get(int) con un índice fuera de límites.
     *
     * Verifica que se lance una IndexOutOfBoundsException si el índice proporcionado
     * para obtener un atributo está fuera de los límites válidos.
     */
    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al obtener atributo por índice fuera de límites")
    void testGetAtributoPorIndiceFueraLimites() {
        dataset.getAtributos().add(new Cuantitativo("Only"));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.get(1));
    }

    /**
     * @brief Prueba el método getInstance(int).
     *
     * Verifica que el método construya y devuelva correctamente una Instancia (fila de datos)
     * para el índice de caso especificado, combinando los valores de los atributos.
     */
    @Test
    @DisplayName("Debería retornar una instancia (fila) por índice")
    void testGetInstance() {
        Cuantitativo attr1 = new Cuantitativo("Edad");
        attr1.add(25.0); attr1.add(30.0);
        Cualitativo attr2 = new Cualitativo("Estado");
        attr2.add("Activo"); attr2.add("Inactivo");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        Instancia instance0 = dataset.getInstance(0);
        assertNotNull(instance0);
        assertEquals(2, instance0.getValores().size());
        assertEquals(25.0, (Double) instance0.getValores().get(0));
        assertEquals("Activo", (String) instance0.getValores().get(1));

        Instancia instance1 = dataset.getInstance(1);
        assertNotNull(instance1);
        assertEquals(2, instance1.getValores().size());
        assertEquals(30.0, (Double) instance1.getValores().get(0));
        assertEquals("Inactivo", (String) instance1.getValores().get(1));
    }

    /**
     * @brief Prueba el método getInstance(int) con un índice fuera de límites.
     *
     * Verifica que se lance una IndexOutOfBoundsException si el índice proporcionado
     * para obtener una instancia está fuera de los límites válidos del Dataset.
     */
    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al obtener instancia fuera de límites")
    void testGetInstanceOutOfBounds() {
        Cuantitativo attr1 = new Cuantitativo("Edad");
        attr1.add(25.0);
        dataset.getAtributos().add(attr1);
        assertEquals(1, dataset.numeroCasos()); // Asegurar que hay al menos un caso
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getInstance(1));
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getInstance(-1));
    }

    /**
     * @brief Prueba el método getInstance(int) en un Dataset vacío.
     *
     * Verifica que se lance una IndexOutOfBoundsException si se intenta obtener
     * una instancia de un Dataset que no contiene ningún caso.
     */
    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al obtener instancia de un Dataset vacío")
    void testGetInstanceDatasetVacio() {
        assertEquals(0, dataset.numeroCasos());
        assertThrows(IndexOutOfBoundsException.class, () -> dataset.getInstance(0));
    }

    /**
     * @brief Prueba el método getPesos().
     *
     * Verifica que el método devuelva una lista de String que representa
     * los nombres y pesos de cada atributo, en el formato "Nombre: Peso".
     * @return Una lista de String con los pesos de los atributos.
     */
    @Test
    @DisplayName("Debería retornar una lista de pesos de atributos como String")
    void testGetPesos() {
        Cuantitativo attr1 = new Cuantitativo("A");
        attr1.setPeso(0.5);
        Cualitativo attr2 = new Cualitativo("B");
        attr2.setPeso(1.0);
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        List<String> pesos = dataset.getPesos();
        assertEquals(2, pesos.size());
        assertEquals("A: 0.5", pesos.get(0)); // Formato Atributo.get()
        assertEquals("B: 1.0", pesos.get(1));
    }

    /**
     * @brief Prueba el método getPesosDouble().
     *
     * Verifica que el método devuelva una lista de Double que contiene
     * solo los valores numéricos de los pesos de los atributos.
     * @return Una lista de Double con los pesos de los atributos.
     */
    @Test
    @DisplayName("Debería retornar una lista de pesos de atributos como Double")
    void testGetPesosDouble() {
        Cuantitativo attr1 = new Cuantitativo("A");
        attr1.setPeso(0.5);
        Cualitativo attr2 = new Cualitativo("B");
        attr2.setPeso(1.0);
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        List<Double> pesos = dataset.getPesosDouble();
        assertEquals(2, pesos.size()); // CORRECCIÓN: Ahora se esperan 2 elementos
        assertEquals(0.5, pesos.get(0), 0.001);
        assertEquals(1.0, pesos.get(1), 0.001);
    }

    /**
     * @brief Prueba el método getClases().
     *
     * Verifica que el método identifique el último atributo como el atributo de clase
     * (debe ser Cualitativo) y devuelva una lista de sus valores únicos (clases).
     * @return Una lista de String con las clases únicas.
     */
    @Test
    @DisplayName("Debería obtener las clases del atributo cualitativo final")
    void testGetClases() {
        Cualitativo attrClass = new Cualitativo("Clase");
        attrClass.add("ClaseA");
        attrClass.add("ClaseB");
        attrClass.add("ClaseA");

        Cuantitativo attrFeature = new Cuantitativo("Feature");
        attrFeature.add(1.0); attrFeature.add(2.0); attrFeature.add(3.0);

        dataset.getAtributos().add(attrFeature); // Primer atributo
        dataset.getAtributos().add(attrClass); // Último atributo (el de clase)

        List<String> clases = dataset.getClases();
        assertNotNull(clases);
        assertEquals(2, clases.size()); // "ClaseA", "ClaseB" (valores únicos)
        assertTrue(clases.contains("ClaseA"));
        assertTrue(clases.contains("ClaseB"));
    }

    /**
     * @brief Prueba el método getClases() cuando el último atributo no es Cualitativo.
     *
     * Verifica que se lance una ClassCastException si el último atributo del Dataset
     * no es de tipo Cualitativo, impidiendo la obtención de clases.
     */
    @Test
    @DisplayName("Debería lanzar ClassCastException si el último atributo no es cualitativo para getClases")
    void testGetClasesLastAttrNotCualitativo() {
        Cuantitativo attrQuant = new Cuantitativo("Ultimo");
        attrQuant.add(10.0);
        dataset.getAtributos().add(attrQuant);
        assertThrows(ClassCastException.class, () -> dataset.getClases());
    }

    /**
     * @brief Prueba el método getClases() en un Dataset sin atributos.
     *
     * Verifica que se lance una IllegalStateException si se intenta obtener
     * las clases de un Dataset que no contiene ningún atributo.
     */
    @Test
    @DisplayName("Debería lanzar IllegalStateException si getClases se llama en un Dataset sin atributos")
    void testGetClasesNoAttributes() {
        assertThrows(IllegalStateException.class, () -> dataset.getClases());
    }

    /**
     * @brief Prueba los métodos getPreprocesado() y setPreprocesado().
     *
     * Verifica que el valor de preprocesado se pueda obtener y establecer correctamente.
     * @return El valor entero de preprocesado.
     */
    @Test
    @DisplayName("Debería obtener y establecer el valor de preprocesado")
    void testGetSetPreprocesado() {
        assertEquals(0, dataset.getPreprocesado());
        dataset.setPreprocesado(2);
        assertEquals(2, dataset.getPreprocesado());
    }

    /**
     * @brief Prueba el método setAtributos(List<Atributo>).
     *
     * Verifica que la lista interna de atributos del Dataset sea reemplazada
     * por la nueva lista proporcionada.
     */
    @Test
    @DisplayName("Debería establecer una nueva lista de atributos")
    void testSetAtributos() {
        Atributo attr1 = new Cuantitativo("NewA");
        attr1.add(5.0);
        List<Atributo> nuevosAtributos = new ArrayList<>(Arrays.asList(attr1));
        dataset.setAtributos(nuevosAtributos);

        assertEquals(1, dataset.numeroAtributos());
        assertSame(attr1, dataset.getAtributos().get(0)); // Verifica que la referencia sea la misma
    }

    // --- Otros Métodos ---

    /**
     * @brief Prueba el método toString().
     *
     * Verifica que el método devuelva una representación en formato CSV
     * del Dataset, incluyendo encabezados y filas de datos.
     * @return Una representación String del Dataset en formato CSV.
     */
    @Test
    @DisplayName("Debería generar una representación String correcta del Dataset")
    void testToString() {
        Cuantitativo attr1 = new Cuantitativo("Edad");
        attr1.add(25.0);
        attr1.add(30.0);
        Cualitativo attr2 = new Cualitativo("Ciudad");
        attr2.add("Madrid");
        attr2.add("Barcelona");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        // CORRECCIÓN: Formato esperado de toString() (CSV estándar)
        String expected = "Edad,Ciudad\n" +
                "25.0,Madrid\n" +
                "30.0,Barcelona"; // Sin \n al final
        assertEquals(expected, dataset.toString());
    }

    /**
     * @brief Prueba el método toString() para un Dataset vacío.
     *
     * Verifica que el método devuelva una cadena vacía cuando el Dataset
     * no contiene ningún atributo ni caso.
     * @return Una cadena vacía.
     */
    @Test
    @DisplayName("Debería generar una representación String vacía para un Dataset vacío")
    void testToStringVacio() {
        // CORRECCIÓN: Ahora se espera una cadena vacía
        assertEquals("", dataset.toString());
    }
}