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

@DisplayName("Tests para la clase KNN")
class KNNTest {

    private KNN knn;

    @BeforeEach
    void setUp() {
        // Se inicializa una nueva instancia de KNN antes de cada test.
        // El valor de k se puede cambiar en tests específicos si es necesario.
        knn = new KNN(3); // K por defecto para la mayoría de los tests
    }


    // --- Clases de soporte simplificadas para los tests ---
    // (Estas deben estar en sus respectivos paquetes en tu proyecto real)

    // Simplified Vector class for testing
    static class TestVector extends Vector {
        private List<Double> values;

        public TestVector(double... values) {
            this.values = Arrays.stream(values).boxed().collect(Collectors.toList());
        }

        @Override
        public double get(int index) {
            return values.get(index);
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public String toString() {
            return values.toString();
        }

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
                    values.set(i, Double.NaN);
                }
            } else {
                for (int i = 0; i < values.size(); i++) {
                    values.set(i, (values.get(i) - min) / (max - min));
                }
            }
        }
    }

    // Simplified Instancia class for testing
    static class TestInstancia extends Instancia {
        private TestVector vector;
        private String clase;

        public TestInstancia(TestVector vector, String clase) {
            // FIX: Removed super(vector, clase); as the parent Instancia class
            // might not have a matching constructor.
            // The fields are directly set for this test helper class.
            this.vector = vector;
            this.clase = clase;
        }

        @Override
        public Vector getVector() {
            return vector;
        }

        @Override
        public String getClase() {
            return clase;
        }

        @Override
        public String toString() {
            return "Instancia{vector=" + vector + ", clase='" + clase + "'}";
        }
    }

    // Simplified Dataset class for testing
    static class TestDataset extends Dataset {
        private List<Instancia> instances;
        private List<Double> pesos; // Expected to be 1 element larger than feature vector size
        private List<Atributo> atributos; // Not directly used by KNN, but Dataset has it

        public TestDataset(List<Instancia> instances, List<Double> pesos) {
            super(); // Call default constructor of Dataset if it exists
            this.instances = Objects.requireNonNullElseGet(instances, ArrayList::new);
            this.pesos = Objects.requireNonNullElseGet(pesos, ArrayList::new);
            this.atributos = new ArrayList<>();
        }

        @Override
        public int numeroCasos() {
            return instances.size();
        }

        @Override
        public Instancia getInstance(int index) {
            return instances.get(index);
        }

        @Override
        public List<Double> getPesosDouble() {
            return new ArrayList<>(pesos);
        }

        @Override
        public List<Atributo> getAtributos() {
            return new ArrayList<>(atributos);
        }
    }

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

    @Test
    @DisplayName("Debería clasificar correctamente con k=1 (vecino más cercano)")
    void testClasificarConK1() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");
        TestInstancia inst3 = new TestInstancia(new TestVector(3.0, 3.0), "ClaseC");

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2, inst3);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Con k=1, debería clasificar como la clase del vecino más cercano");
    }

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
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Con k=3, debería clasificar como ClaseA (3 vecinos de ClaseA)");
    }

    @Test
    @DisplayName("Debería manejar empates en la clase mayoritaria (selección arbitraria)")
    void testClasificarConEmpate() {
        knn = new KNN(2);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(1.1, 1.1), "ClaseB");
        TestInstancia inst3 = new TestInstancia(new TestVector(1.2, 1.2), "ClaseA");
        TestInstancia inst4 = new TestInstancia(new TestVector(1.3, 1.3), "ClaseB");

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2, inst3, inst4);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertTrue(clasePredicha.equals("ClaseA") || clasePredicha.equals("ClaseB"), "Debería clasificar como ClaseA o ClaseB en caso de empate");
    }

    @Test
    @DisplayName("Debería retornar null si el Dataset de entrenamiento es nulo")
    void testClasificarDatasetEntrenamientoNulo() {
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        String clasePredicha = knn.clasificar(null, prueba);
        assertNull(clasePredicha, "Debería retornar null si el Dataset de entrenamiento es nulo");
    }

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

    @Test
    @DisplayName("Debería retornar null si el Vector de la Instancia de prueba es nulo")
    void testClasificarVectorPruebaNulo() {
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        List<Instancia> entrenamiento = Arrays.asList(inst1);
        TestInstancia prueba = new TestInstancia(null, null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);
        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertNull(clasePredicha, "Debería retornar null si el Vector de la Instancia de prueba es nulo");
    }

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

        // Caso 2: pesos con tamaño incorrecto (ej. 2 elementos para un vector de 2, cuando se espera 3)
        List<Double> pesosIncorrectos = Arrays.asList(1.0, 1.0);
        Dataset datasetIncorrectPesos = new TestDataset(entrenamiento, pesosIncorrectos);
        String clasePredichaIncorrectSize = knn.clasificar(datasetIncorrectPesos, prueba);
        assertNull(clasePredichaIncorrectSize, "Debería retornar null si el tamaño de los pesos es incorrecto");
    }

    @Test
    @DisplayName("Debería clasificar con pesos cero, resultando en distancia cero para todos los vecinos")
    void testClasificarConPesosCero() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");
        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(0.0, 0.0), null);
        List<Double> pesos = Arrays.asList(0.0, 0.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertTrue(clasePredicha.equals("ClaseA") || clasePredicha.equals("ClaseB"), "Debería clasificar como ClaseA o ClaseB si todas las distancias son cero");
    }

    @Test
    @DisplayName("Debería ignorar instancias de entrenamiento con Vector nulo")
    void testClasificarConInstanciasDeEntrenamientoConVectorNulo() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia instNullVector = new TestInstancia(null, "ClaseNull");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");

        List<Instancia> entrenamiento = Arrays.asList(instNullVector, inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Debería ignorar instancias de entrenamiento con vector nulo");
    }

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

    @Test
    @DisplayName("Debería clasificar correctamente con diferentes pesos de atributos")
    void testClasificarConPesosDiferentes() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(0.1, 0.1), "ClaseB");

        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(0.0, 0.0), null);
        List<Double> pesos = Arrays.asList(1.0, 100.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseB", clasePredicha, "Debería clasificar como ClaseB debido al mayor peso del segundo atributo");
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso donde la distancia es 0 (instancia idéntica)")
    void testClasificarConDistanciaCero() {
        knn = new KNN(1);
        TestInstancia inst1 = new TestInstancia(new TestVector(1.0, 1.0), "ClaseA");
        TestInstancia inst2 = new TestInstancia(new TestVector(2.0, 2.0), "ClaseB");
        List<Instancia> entrenamiento = Arrays.asList(inst1, inst2);
        TestInstancia prueba = new TestInstancia(new TestVector(1.0, 1.0), null);
        List<Double> pesos = Arrays.asList(1.0, 1.0, 0.0);

        Dataset dataset = new TestDataset(entrenamiento, pesos);

        String clasePredicha = knn.clasificar(dataset, prueba);
        assertEquals("ClaseA", clasePredicha, "Debería clasificar como la clase de la instancia idéntica");
    }
}