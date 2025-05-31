package clasificacion;

import datos.Dataset;
import datos.Instancia;
import datos.Atributo; // Necesario para la clase Dataset simplificada
import datos.Cuantitativo; // Necesario para la clase Dataset simplificada
import datos.Cualitativo; // Necesario para la clase Dataset simplificada
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vectores.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @class KNNTest
 * @brief Clase de pruebas unitarias para la clase KNN.
 *
 * Contiene una serie de pruebas para verificar el correcto funcionamiento del
 * algoritmo K-Nearest Neighbors (KNN) implementado en la clase `KNN`.
 * Abarca casos como la clasificación con diferentes valores de K, manejo de empates,
 * validación de entradas nulas o vacías, y el efecto de los pesos de los atributos
 * en la distancia euclídea ponderada.
 */
@DisplayName("Tests para la clase KNN")
class KNNTest {

    /**
     * @brief Instancia de la clase KNN a probar.
     */
    private KNN knn;

    /**
     * @brief Método de configuración que se ejecuta antes de cada prueba.
     *
     * Se inicializa una nueva instancia de KNN con un valor por defecto de K=3
     * antes de cada test. Esto asegura un estado limpio en cada ejecución.
     * El valor de k puede ser modificado en tests específicos si es necesario.
     */
    @BeforeEach
    void setUp() {
        // Se inicializa una nueva instancia de KNN antes de cada test.
        // El valor de k se puede cambiar en tests específicos si es necesario.
        knn = new KNN(3); // K por defecto para la mayoría de los tests
    }


    // --- Clases de soporte simplificadas para los tests ---
    // (Estas deben estar en sus respectivos paquetes en tu proyecto real)

    /**
     * @class TestVector
     * @brief Clase Vector simplificada para propósitos de prueba.
     *
     * Extiende la clase `Vector` y proporciona una implementación básica
     * y controlada para los tests, incluyendo un constructor que acepta
     * un número variable de argumentos y una implementación de `normalize`
     * para la normalización Min-Max.
     */
    static class TestVector extends Vector {
        /**
         * @brief Lista de valores (coeficientes) del vector.
         */
        private List<Double> values;

        /**
         * @brief Constructor que inicializa el vector con los valores proporcionados.
         * @param values Un número variable de valores double para el vector.
         */
        public TestVector(double... values) {
            this.values = Arrays.stream(values).boxed().collect(Collectors.toList());
        }

        /**
         * @brief Obtiene el valor en el índice especificado.
         * @param index El índice del valor a obtener.
         * @return El valor double en el índice.
         */
        @Override
        public double get(int index) {
            return values.get(index);
        }

        /**
         * @brief Obtiene el tamaño (número de elementos) del vector.
         * @return El número de elementos en el vector.
         */
        @Override
        public int size() {
            return values.size();
        }

        /**
         * @brief Retorna una representación en cadena del vector.
         * @return Una cadena que representa el vector.
         */
        @Override
        public String toString() {
            return values.toString();
        }

        /**
         * @brief Normaliza los valores del vector al rango [0, 1] (Min-Max Normalization).
         *
         * Si el vector está vacío, no hace nada. Si todos los valores son iguales,
         * los valores normalizados se establecen en NaN para indicar un rango cero.
         */
        @Override
        public void normalize() {
            if (values.isEmpty()) return;
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (Double val : values) {
                if (val < min) min = val;
                if (val > max) max = val;
            }

            if (min == max) {
                for (int i = 0; i < values.size(); i++) {
                    values.set(i, Double.NaN); // O 0.0, dependiendo de la política para min=max
                }
            } else {
                for (int i = 0; i < values.size(); i++) {
                    values.set(i, (values.get(i) - min) / (max - min));
                }
            }
        }
    }

    /**
     * @class TestInstancia
     * @brief Clase Instancia simplificada para propósitos de prueba.
     *
     * Extiende la clase `Instancia` y permite la creación de instancias
     * de prueba con un `TestVector` y una clase asociada.
     */
    static class TestInstancia extends Instancia {
        /**
         * @brief El vector de características de la instancia.
         */
        private TestVector vector;
        /**
         * @brief La etiqueta de clase de la instancia.
         */
        private String clase;

        /**
         * @brief Constructor que crea una instancia de prueba con un vector y una clase.
         * @param vector El `TestVector` que representa las características.
         * @param clase La cadena que representa la clase de la instancia.
         */
        public TestInstancia(TestVector vector, String clase) {
            // FIX: Removed super(vector, clase); as the parent Instancia class
            // might not have a matching constructor.
            // The fields are directly set for this test helper class.
            this.vector = vector;
            this.clase = clase;
        }

        /**
         * @brief Obtiene el vector de características de la instancia.
         * @return El objeto `Vector` de la instancia.
         */
        @Override
        public Vector getVector() {
            return vector;
        }

        /**
         * @brief Obtiene la etiqueta de clase de la instancia.
         * @return La cadena que representa la clase.
         */
        @Override
        public String getClase() {
            return clase;
        }

        /**
         * @brief Retorna una representación en cadena de la instancia.
         * @return Una cadena que representa la instancia.
         */
        @Override
        public String toString() {
            return "Instancia{vector=" + vector + ", clase='" + clase + "'}";
        }
    }

    /**
     * @class TestDataset
     * @brief Clase Dataset simplificada para propósitos de prueba.
     *
     * Extiende la clase `Dataset` y proporciona una implementación controlada
     * para los tests, incluyendo el almacenamiento de instancias y pesos,
     * y la sobrescritura de métodos clave como `numeroCasos`, `getInstance`,
     * y `getPesosDouble`.
     */
    static class TestDataset extends Dataset {
        /**
         * @brief Lista de instancias contenidas en el dataset.
         */
        private List<Instancia> instances;
        /**
         * @brief Lista de pesos asociados a los atributos.
         * Se espera que sea un elemento más grande que el tamaño del vector de características.
         */
        private List<Double> pesos; // Expected to be 1 element larger than feature vector size
        /**
         * @brief Lista de atributos del dataset. No es directamente utilizada por KNN en estos tests,
         * pero forma parte de la estructura `Dataset`.
         */
        private List<Atributo> atributos; // Not directly used by KNN, but Dataset has it

        /**
         * @brief Constructor que crea un dataset de prueba con una lista de instancias y pesos.
         * @param instances La lista de objetos `Instancia` para el dataset.
         * @param pesos La lista de pesos `Double` para los atributos.
         */
        public TestDataset(List<Instancia> instances, List<Double> pesos) {
            super(); // Call default constructor of Dataset if it exists
            this.instances = Objects.requireNonNullElseGet(instances, ArrayList::new);
            this.pesos = Objects.requireNonNullElseGet(pesos, ArrayList::new);
            this.atributos = new ArrayList<>();
        }

        /**
         * @brief Obtiene el número total de casos (instancias) en el dataset.
         * @return El número de instancias.
         */
        @Override
        public int numeroCasos() {
            return instances.size();
        }

        /**
         * @brief Obtiene una instancia específica del dataset por su índice.
         * @param index El índice de la instancia a obtener.
         * @return La `Instancia` en el índice especificado.
         */
        @Override
        public Instancia getInstance(int index) {
            return instances.get(index);
        }

        /**
         * @brief Obtiene los pesos de los atributos como una lista de `Double`.
         * @return Una nueva lista que contiene los pesos.
         */
        @Override
        public List<Double> getPesosDouble() {
            return new ArrayList<>(pesos);
        }

        /**
         * @brief Obtiene la lista de atributos del dataset.
         * @return Una nueva lista que contiene los atributos.
         */
        @Override
        public List<Atributo> getAtributos() {
            return new ArrayList<>(atributos);
        }
    }

    /**
     * @brief Prueba que el constructor de KNN establece el valor de 'k' correctamente para un valor válido.
     *
     * Utiliza reflexión para acceder al campo privado 'k' y verificar su valor.
     */
    @Test
    @DisplayName("El constructor de KNN debería establecer k correctamente para un valor válido")
    void testConstructorKValido() {
        KNN knn2 = new KNN(5);
        try {
            java.lang.reflect.Field field = KNN.class.getDeclaredField("k");
            field.setAccessible(true);
            assertEquals(5, field.get(knn2));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("No se pudo acceder al campo 'k' para la prueba: " + e.getMessage());
        }
    }

    /**
     * @brief Prueba que el constructor de KNN establece el valor de 'k' correctamente
     * incluso para valores no válidos como 0 o negativos.
     *
     * El constructor no debería lanzar una excepción, sino simplemente almacenar el valor
     * proporcionado, asumiendo que la lógica de clasificación manejará estos valores.
     */
    @Test
    @DisplayName("El constructor de KNN debería establecer k correctamente incluso para un valor no válido (0 o negativo)")
    void testConstructorKNoValido() {
        KNN knn2 = new KNN(0);
        try {
            java.lang.reflect.Field field = KNN.class.getDeclaredField("k");
            field.setAccessible(true);
            assertEquals(0, field.get(knn2));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("No se pudo acceder al campo 'k' para la prueba: " + e.getMessage());
        }

        KNN knn3 = new KNN(-1);
        try {
            java.lang.reflect.Field field = KNN.class.getDeclaredField("k");
            field.setAccessible(true);
            assertEquals(-1, field.get(knn3));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("No se pudo acceder al campo 'k' para la prueba: " + e.getMessage());
        }
    }

    /**
     * @brief Prueba la clasificación con K=1 (algoritmo del vecino más cercano).
     *
     * Verifica que la clase predicha sea la misma que la clase del vecino más cercano
     * a la instancia de prueba.
     */
    @Test
    @DisplayName("Debería clasificar correctamente con k=1 (vecino más cercano)")
    void testClasificarConK1() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");
        TestInstancia inst3 = new TestInstancia(new TestVector(3.0, 3.0), "ClaseC");

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2, inst3);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0); // Pesos para los 2 atributos + 1 para la clase si Dataset lo espera

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Con k=1, debería clasificar como la clase del vecino más cercano");
    }

    /**
     * @brief Prueba la clasificación con K > 1 y una mayoría clara de vecinos.
     *
     * Verifica que la clase predicha sea la de la clase que tiene la mayoría
     * entre los K vecinos más cercanos.
     */
    @Test
    @DisplayName("Debería clasificar correctamente con k > 1 y una mayoría clara")
    void testClasificarConKMayorYMayoriaClara() {
        knn = new KNN(3);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(1.1, 1.1), "ClaseA");
        TestInstancia inst3 = new TestInstancia(new TestVector(5.0, 5.0), "ClaseB");
        TestInstancia inst4 = new TestInstancia(new TestVector(1.2, 1.2), "ClaseA");
        TestInstancia inst5 = new TestInstancia(new TestVector(10.0, 10.0), "ClaseC");

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2, inst3, inst4, inst5);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0); // Pesos para 2 atributos + 1 para la clase

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Con k=3, debería clasificar como ClaseA (3 vecinos de ClaseA)");
    }

    /**
     * @brief Prueba el manejo de empates en la clase mayoritaria.
     *
     * Cuando hay un empate en el número de votos entre las clases de los K vecinos,
     * el algoritmo debería seleccionar una de las clases empatadas de forma arbitraria
     * (el orden puede depender de la implementación de la cola de prioridad o del mapa de conteo).
     */
    @Test
    @DisplayName("Debería manejar empates en la clase mayoritaria (selección arbitraria)")
    void testClasificarConEmpate() {
        knn = new KNN(2);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(1.1, 1.1), "ClaseB");
        TestInstancia inst3 = new TestInstancia(new TestVector(1.2, 1.2), "ClaseA"); // Esta podría ser la tercera más cercana, pero K=2
        TestInstancia inst4 = new TestInstancia(new TestVector(1.3, 1.3), "ClaseB");

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2, inst3, inst4);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertTrue(clasePredicha.equals("ClaseA") || clasePredicha.equals("ClaseB"), "Debería clasificar como ClaseA o ClaseB en caso de empate");
    }

    /**
     * @brief Prueba que el método `clasificar` retorna null si el Dataset de entrenamiento es nulo.
     */
    @Test
    @DisplayName("Debería retornar null si el Dataset de entrenamiento es nulo")
    void testClasificarDatasetEntrenamientoNulo() {
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        String clasePredicha = knn.clasificar(null, prueba);
        assertNull(clasePredicha, "Debería retornar null si el Dataset de entrenamiento es nulo");
    }

    /**
     * @brief Prueba que el método `clasificar` retorna null si el Dataset de entrenamiento está vacío.
     */
    @Test
    @DisplayName("Debería retornar null si el Dataset de entrenamiento está vacío")
    void testClasificarDatasetEntrenamientoVacio() {
        List<Instancia> entrenamientoVacio = Collections.emptyList();
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);
        Dataset dataset = new TestDataset(entrenamientoVacio, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertNull(clasePredicha, "Debería retornar null si el Dataset de entrenamiento está vacío");
    }

    /**
     * @brief Prueba que el método `clasificar` retorna null si la Instancia de prueba es nula.
     */
    @Test
    @DisplayName("Debería retornar null si la Instancia de prueba es nula")
    void testClasificarInstanciaPruebaNula() {
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        List<Instancia> entrenamiento = Arrays.asList(inst1);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);
        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, null);
        assertNull(clasePredicha, "Debería retornar null si la Instancia de prueba es nula");
    }

    /**
     * @brief Prueba que el método `clasificar` retorna null si el Vector de la Instancia de prueba es nulo.
     */
    @Test
    @DisplayName("Debería retornar null si el Vector de la Instancia de prueba es nulo")
    void testClasificarVectorPruebaNulo() {
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        List<Instancia> entrenamiento = Arrays.asList(inst1);
        TestInstancia prueba = new TestInstancia(null, null); // Instancia de prueba con vector nulo
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);
        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertNull(clasePredicha, "Debería retornar null si el Vector de la Instancia de prueba es nulo");
    }

    /**
     * @brief Prueba que el método `clasificar` retorna null si la lista de pesos del Dataset es nula o inválida.
     *
     * Verifica dos escenarios:
     * 1. La lista de pesos obtenida del Dataset es nula.
     * 2. La lista de pesos tiene un tamaño incorrecto en relación con el vector de características.
     */
    @Test
    @DisplayName("Debería retornar null si la lista de pesos del Dataset es nula o inválida")
    void testClasificarConPesosInvalidosEnDataset() {
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        List<Instancia> entrenamiento = Arrays.asList(inst1);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);

        // Caso 1: pesos nulos
        Dataset datasetNullPesos = new TestDataset(entrenamiento, null);
        String clasePredichaNull = knn.clasificar(datasetNullPesos, prueba);
        assertNull(clasePredichaNull, "Debería retornar null si los pesos del Dataset son nulos");

        // Caso 2: pesos con tamaño incorrecto (ej. 2 elementos para un vector de 2, cuando se espera 3 para incluir el de la clase)
        List<Double> pesosIncorrectos = Arrays.asList(1.0, 1.0); // Para un vector de 2 características, se esperaría 3 pesos (feature1, feature2, clase)
        Dataset datasetIncorrectPesos = new TestDataset(entrenamiento, pesosIncorrectos);
        String clasePredichaIncorrectSize = knn.clasificar(datasetIncorrectPesos, prueba);
        assertNull(clasePredichaIncorrectSize, "Debería retornar null si el tamaño de los pesos es incorrecto");
    }

    /**
     * @brief Prueba la clasificación cuando todos los pesos de los atributos son cero.
     *
     * Si los pesos son cero, todas las distancias euclídeas ponderadas serán cero.
     * El algoritmo debería manejar esto y clasificar arbitrariamente entre las clases
     * de las instancias de entrenamiento.
     */
    @Test
    @DisplayName("Debería clasificar con pesos cero, resultando en distancia cero para todos los vecinos")
    void testClasificarConPesosCero() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");
        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(0.0, 0.0), null);
        List<Double> pesos = Arrays.asList(0.0, 0.0, 0.0); // Pesos cero para ambos atributos

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertTrue(clasePredicha.equals("ClaseA") || clasePredicha.equals("ClaseB"), "Debería clasificar como ClaseA o ClaseB si todas las distancias son cero");
    }

    /**
     * @brief Prueba que el algoritmo ignora las instancias de entrenamiento que tienen un Vector nulo.
     *
     * La lógica de clasificación debería omitir estas instancias al calcular las distancias
     * y buscar los K vecinos más cercanos.
     */
    @Test
    @DisplayName("Debería ignorar instancias de entrenamiento con Vector nulo")
    void testClasificarConInstanciasDeEntrenamientoConVectorNulo() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia instNullVector = new TestInstancia(null, "ClaseNull"); // Instancia de entrenamiento con vector nulo
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");

        List<Instancia> entrenamiento = Arrays.asList(instNullVector, inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Debería ignorar instancias de entrenamiento con vector nulo");
    }

    /**
     * @brief Prueba la clasificación cuando el valor de K es mayor que el número de casos de entrenamiento disponibles.
     *
     * En este escenario, el algoritmo debería usar todos los casos de entrenamiento disponibles
     * para determinar la clase mayoritaria.
     */
    @Test
    @DisplayName("Debería clasificar correctamente cuando K es mayor que el número de casos de entrenamiento disponibles")
    void testClasificarCuandoKEsMayorQueNumeroDeCasos() {
        knn = new KNN(5);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");
        TestInstancia inst3 = new TestInstancia(new TestVector(3.0, 3.0), "ClaseA");

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2, inst3);
        TestInstancia prueba = new TestInstancia(new TestVector(1.5, 1.5), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Debería clasificar correctamente considerando todos los vecinos disponibles");
    }

    /**
     * @brief Prueba la clasificación con diferentes pesos asignados a los atributos.
     *
     * Verifica que la distancia euclídea ponderada se calcula correctamente y que
     * los atributos con mayor peso tienen una influencia más significativa en el resultado
     * de la clasificación.
     */
    @Test
    @DisplayName("Debería clasificar correctamente con diferentes pesos de atributos")
    void testClasificarConPesosDiferentes() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(0.1, 0.1), "ClaseB"); // Más cerca de (0,0) en valor absoluto

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(0.0, 0.0), null);
        List<Double> pesos = Arrays.asList(1.0, 100.0, 0.0); // Mayor peso para el segundo atributo

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        // Distancia a ClaseA: (1.0-0.0)^2 * 1.0 + (1.0-0.0)^2 * 100.0 = 1 + 100 = 101
        // Distancia a ClaseB: (0.1-0.0)^2 * 1.0 + (0.1-0.0)^2 * 100.0 = 0.01 + 1.0 = 1.01
        // ClaseB debería ser el vecino más cercano.
        assertEquals("ClaseB", clasePredicha, "Debería clasificar como ClaseB debido al mayor peso del segundo atributo");
    }

    /**
     * @brief Prueba el caso donde una instancia de entrenamiento es idéntica a la instancia de prueba.
     *
     * La distancia euclídea cuadrada a la instancia idéntica será 0.
     * El algoritmo debería clasificar la instancia de prueba con la clase de esa instancia idéntica.
     */
    @Test
    @DisplayName("Debería manejar correctamente el caso donde la distancia es 0 (instancia idéntica)")
    void testClasificarConDistanciaCero() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");
        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null); // Idéntica a inst1
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Debería clasificar como la clase de la instancia idéntica");
    }
}