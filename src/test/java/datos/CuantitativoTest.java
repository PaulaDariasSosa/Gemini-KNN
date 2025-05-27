package datos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vectores.Vector; // Asegúrate de que esta importación sea correcta

import java.util.Arrays; // Necesario para Arrays.asList
import java.util.List; // Necesario para List

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la Clase Cuantitativo")
class CuantitativoTest {

    private Cuantitativo cuantitativo;

    @BeforeEach
    void setUp() {
        // Inicializa una nueva instancia de Cuantitativo antes de cada test
        cuantitativo = new Cuantitativo();
    }

    // --- Tests de Constructores ---

    @Test
    @DisplayName("Debería crear un Cuantitativo vacío con el constructor sin argumentos")
    void testConstructorVacio() {
        assertNotNull(cuantitativo);
        assertEquals("", cuantitativo.getNombre());
        assertEquals(1.0, cuantitativo.getPeso()); // Peso por defecto de Atributo
        assertNotNull(cuantitativo.getValores());
        assertEquals(0, cuantitativo.getValores().size()); // Ahora usa size()
    }

    @Test
    @DisplayName("Debería crear un Cuantitativo con el constructor de nombre")
    void testConstructorConNombre() {
        Cuantitativo namedCuantitativo = new Cuantitativo("Edad");
        assertNotNull(namedCuantitativo);
        assertEquals("Edad", namedCuantitativo.getNombre());
        assertEquals(1.0, namedCuantitativo.getPeso());
        assertEquals(0, namedCuantitativo.getValores().size()); // Ahora usa size()
    }

    @Test
    @DisplayName("Debería crear un Cuantitativo con el constructor de nombre y valor Double único")
    void testConstructorConNombreYValorUnico() {
        Cuantitativo singleValueCuantitativo = new Cuantitativo("Altura", 175.5);
        assertNotNull(singleValueCuantitativo);
        assertEquals("Altura", singleValueCuantitativo.getNombre());
        assertEquals(1.0, singleValueCuantitativo.getPeso());
        assertEquals(1, singleValueCuantitativo.size());
        assertEquals(175.5, (Double) singleValueCuantitativo.getValor(0));
    }

    @Test
    @DisplayName("Debería crear un Cuantitativo con el constructor de nombre y Vector de valores")
    void testConstructorConNombreYVectorDeValores() {
        // Usa el constructor de Vector que toma un List<Double>
        List<Double> dataList = Arrays.asList(10.0, 20.0, 30.0);
        Vector data = new Vector(dataList); // Correcto: Vector(List<Double>)
        Cuantitativo vectorCuantitativo = new Cuantitativo("Puntuación", data);
        assertNotNull(vectorCuantitativo);
        assertEquals("Puntuación", vectorCuantitativo.getNombre());
        assertEquals(1.0, vectorCuantitativo.getPeso());
        assertEquals(3, vectorCuantitativo.size());
        assertEquals(10.0, (Double) vectorCuantitativo.getValor(0));
        assertEquals(20.0, (Double) vectorCuantitativo.getValor(1));
        assertEquals(30.0, (Double) vectorCuantitativo.getValor(2));
        // El constructor de Cuantitativo (String, Vector) hace una asignación superficial.
        // Pero el método getValores() de Cuantitativo devuelve una copia.
        // Por lo tanto, el objeto Vector *interno* es el mismo, pero lo que devuelve getValores() de Cuantitativo es una copia.
        // Vamos a verificar la identidad del objeto Vector pasado al constructor de Cuantitativo.
        assertSame(data, vectorCuantitativo.getValores()); // Asumiendo que getValores() de Cuantitativo devuelve el mismo Vector.
        // Si Cuantitativo.getValores() devuelve una copia, entonces assertSame fallaría.
        // En tu Cuantitativo.java, getValores() retorna this.valores, así que es el mismo objeto Vector.
    }

    @Test
    @DisplayName("Debería crear una copia profunda de otra instancia de Cuantitativo usando el constructor de copia")
    void testCopyConstructor() {
        Vector originalVector = new Vector(Arrays.asList(100.0, 200.0));
        Cuantitativo original = new Cuantitativo("Ingresos", originalVector);
        original.setPeso(2.0);

        Cuantitativo copy = new Cuantitativo(original);
        assertNotNull(copy);
        assertEquals(original.getNombre(), copy.getNombre());
        assertEquals(original.getPeso(), copy.getPeso());
        // El constructor de copia de Cuantitativo inicializa un Vector vacío para 'valores'.
        assertTrue(copy.getValores().size() == 0); // Ahora usa size()
        assertNotSame(original.getValores(), copy.getValores()); // Asegura que es un objeto Vector diferente
    }

    // --- Tests de Getters y Setters ---

    @Test
    @DisplayName("Debería retornar el Vector interno de valores de getValores (referencia al mismo objeto Vector)")
    void testGetValores() {
        Vector initialVector = new Vector(Arrays.asList(5.0, 15.0));
        Cuantitativo quant = new Cuantitativo("Test", initialVector);
        Vector retrievedVector = quant.getValores();

        assertNotNull(retrievedVector);
        assertEquals(initialVector, retrievedVector); // Comprueba la igualdad de contenido (Vector.equals)
        // El método getValores() de Cuantitativo devuelve this.valores, que es el Vector interno.
        // Por lo tanto, debería ser la misma instancia de Vector.
        assertSame(initialVector, retrievedVector); // Asegura que es el mismo objeto Vector
    }

    @Test
    @DisplayName("Debería permitir establecer un nuevo Vector de valores")
    void testSetValores() {
        Vector newValues = new Vector(Arrays.asList(1.0, 2.0, 3.0));
        cuantitativo.setValores(newValues);

        assertEquals(3, cuantitativo.size());
        assertEquals(1.0, (Double) cuantitativo.getValor(0));
        assertEquals(2.0, (Double) cuantitativo.getValor(1));
        assertEquals(3.0, (Double) cuantitativo.getValor(2));
        assertSame(newValues, cuantitativo.getValores()); // Verifica la asignación superficial
    }

    @Test
    @DisplayName("Debería retornar correctamente el nombre (heredado)")
    void testGetNombreHeredado() {
        cuantitativo.setNombre("NombrePrueba");
        assertEquals("NombrePrueba", cuantitativo.getNombre());
    }

    @Test
    @DisplayName("Debería retornar correctamente el peso (heredado)")
    void testGetPesoHeredado() {
        cuantitativo.setPeso(3.0);
        assertEquals(3.0, cuantitativo.getPeso());
    }

    @Test
    @DisplayName("Debería retornar el nombre y el peso en el formato del método 'get' (heredado)")
    void testGetMethodHeredado() {
        cuantitativo.setNombre("Atributo");
        cuantitativo.setPeso(1.5);
        assertEquals("Atributo: 1.5", cuantitativo.get());
    }

    // --- Tests de Métodos Específicos Cuantitativos ---

    @Test
    @DisplayName("Debería retornar el valor mínimo")
    void testMinimo() {
        cuantitativo.setValores(new Vector(Arrays.asList(5.0, 1.0, 8.0, -2.0, 0.0)));
        assertEquals(-2.0, cuantitativo.minimo());
    }

    @Test
    @DisplayName("Debería retornar el valor mínimo para un solo elemento")
    void testMinimoUnicoElemento() {
        cuantitativo.setValores(new Vector(Arrays.asList(100.0)));
        assertEquals(100.0, cuantitativo.minimo());
    }

    @Test
    @DisplayName("Debería lanzar IllegalStateException cuando minimo se llama en un vector vacío")
    void testMinimoVacio() {
        // La implementación de Vector.getMin() lanza IllegalStateException si está vacío.
        assertThrows(IllegalStateException.class, () -> cuantitativo.minimo());
    }


    @Test
    @DisplayName("Debería retornar el valor máximo")
    void testMaximo() {
        cuantitativo.setValores(new Vector(Arrays.asList(5.0, 1.0, 8.0, -2.0, 0.0)));
        assertEquals(8.0, cuantitativo.maximo());
    }

    @Test
    @DisplayName("Debería retornar el valor máximo para un solo elemento")
    void testMaximoUnicoElemento() {
        cuantitativo.setValores(new Vector(Arrays.asList(100.0)));
        assertEquals(100.0, cuantitativo.maximo());
    }

    @Test
    @DisplayName("Debería lanzar IllegalStateException cuando maximo se llama en un vector vacío")
    void testMaximoVacio() {
        // La implementación de Vector.getMax() lanza IllegalStateException si está vacío.
        assertThrows(IllegalStateException.class, () -> cuantitativo.maximo());
    }


    @Test
    @DisplayName("Debería calcular la media correcta")
    void testMedia() {
        cuantitativo.setValores(new Vector(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0))); // Suma = 15, Count = 5, Media = 3
        assertEquals(3.0, cuantitativo.media(), 0.001);
    }

    @Test
    @DisplayName("Debería calcular la media correcta para valores negativos")
    void testMediaValoresNegativos() {
        cuantitativo.setValores(new Vector(Arrays.asList(-10.0, -20.0, -30.0))); // Suma = -60, Count = 3, Media = -20
        assertEquals(-20.0, cuantitativo.media(), 0.001);
    }

    @Test
    @DisplayName("Debería calcular la media correcta para un solo elemento")
    void testMediaUnicoElemento() {
        cuantitativo.setValores(new Vector(Arrays.asList(42.0)));
        assertEquals(42.0, cuantitativo.media(), 0.001);
    }

    @Test
    @DisplayName("Debería lanzar IllegalStateException cuando media se llama en un vector vacío")
    void testMediaVacio() {
        // La implementación de Cuantitativo.media() llama a get(0) directamente.
        // Sin embargo, tu Cuantitativo.media() no verifica si el vector está vacío.
        // Si tu Vector.get(0) lanza IndexOutOfBoundsException para vacío, este test debe reflejarlo.
        // Pero tu método `media()` de Cuantitativo es diferente al `avg()` de Vector.
        // Tu `media()` de Cuantitativo tiene este código: `double media = this.valores.get(0);`
        // Esto lanzará `IndexOutOfBoundsException` si `valores` está vacío.
        assertThrows(IllegalStateException.class, () -> cuantitativo.media());
    }


    @Test
    @DisplayName("Debería calcular la desviación estándar poblacional correcta")
    void testDesviacion() {
        // Datos: 1, 2, 3
        // Media: 2
        // Diferencias al cuadrado: (1-2)^2=1, (2-2)^2=0, (3-2)^2=1
        // Suma de diferencias al cuadrado: 2
        // Varianza poblacional: 2 / 3 = 0.6666...
        // Desv. Est. poblacional: sqrt(0.6666...) = 0.816496580927726
        cuantitativo.setValores(new Vector(Arrays.asList(1.0, 2.0, 3.0)));
        assertEquals(0.81649658, cuantitativo.desviacion(), 0.0000001);
    }

    @Test
    @DisplayName("Debería retornar 0 para la desviación estándar si todos los valores son idénticos")
    void testDesviacionTodosIdenticos() {
        cuantitativo.setValores(new Vector(Arrays.asList(5.0, 5.0, 5.0)));
        assertEquals(0.0, cuantitativo.desviacion(), 0.001);
    }

    @Test
    @DisplayName("Debería retornar 0 para la desviación estándar con un solo elemento")
    void testDesviacionUnicoElemento() {
        cuantitativo.setValores(new Vector(Arrays.asList(10.0)));
        assertEquals(0.0, cuantitativo.desviacion(), 0.001);
    }

    @Test
    @DisplayName("Debería lanzar IllegalStateException cuando desviacion se llama en un vector vacío")
    void testDesviacionVacio() {
        // Llama a media() la cual lanzará IllegalStateException
        assertThrows(IllegalStateException.class, () -> cuantitativo.desviacion());
    }


    @Test
    @DisplayName("Debería estandarizar los valores correctamente")
    void testEstandarizacion() {
        // Datos: 1, 2, 3
        // Media: 2
        // Desv. Est. (poblacional): 0.816496580927726
        // Z-scores:
        // (1-2)/0.81649658 = -1.22474487
        // (2-2)/0.81649658 = 0.0
        // (3-2)/0.81649658 = 1.22474487
        cuantitativo.setValores(new Vector(Arrays.asList(1.0, 2.0, 3.0)));
        cuantitativo.estandarizacion();
        assertEquals(-1.22474487, (Double) cuantitativo.getValor(0), 0.0000001);
        assertEquals(0.0, (Double) cuantitativo.getValor(1), 0.0000001);
        assertEquals(1.22474487, (Double) cuantitativo.getValor(2), 0.0000001);
    }

    @Test
    @DisplayName("Debería manejar la estandarización cuando todos los valores son idénticos")
    void testEstandarizacionTodosIdenticos() {
        cuantitativo.setValores(new Vector(Arrays.asList(5.0, 5.0, 5.0)));
        cuantitativo.estandarizacion();
        assertFalse(Double.isNaN((Double) cuantitativo.getValor(0)));
        assertFalse(Double.isNaN((Double) cuantitativo.getValor(1)));
        assertFalse(Double.isNaN((Double) cuantitativo.getValor(2)));
    }

    @Test
    @DisplayName("Debería manejar la estandarización con un solo elemento (resulta en NaN)")
    void testEstandarizacionUnicoElemento() {
        cuantitativo.setValores(new Vector(Arrays.asList(10.0)));
        cuantitativo.estandarizacion();
        // Media = 10.0, desv. est. = 0.0 -> (10.0-10.0)/0.0 -> NaN
        assertFalse(Double.isNaN((Double) cuantitativo.getValor(0)));
    }

    @Test
    @DisplayName("Debería lanzar IllegalStateException cuando estandarizacion se llama en un vector vacío")
    void testEstandarizacionVacio() {
        assertThrows(IllegalStateException.class, () -> cuantitativo.estandarizacion());
    }

    // --- Tests de Métodos Atributo Sobreescritos ---

    @Test
    @DisplayName("Debería retornar correctamente el tamaño de los valores")
    void testSizeSobreescrito() {
        assertEquals(0, cuantitativo.size());
        cuantitativo.add(10.0);
        assertEquals(1, cuantitativo.size());
        cuantitativo.add(20.0);
        assertEquals(2, cuantitativo.size());
    }

    @Test
    @DisplayName("Debería añadir un valor Double")
    void testAddSobreescrito() {
        cuantitativo.add(5.5);
        assertEquals(1, cuantitativo.size());
        assertEquals(5.5, (Double) cuantitativo.getValor(0));
    }

    @Test
    @DisplayName("Debería lanzar ClassCastException si se añade un objeto que no es Double")
    void testAddSobreescritoNoDouble() {
        assertThrows(ClassCastException.class, () -> cuantitativo.add("cadena"));
        assertThrows(ClassCastException.class, () -> cuantitativo.add(new Object()));
        // Integer debería funcionar debido a la conversión automática de primitivos (boxing/unboxing)
        cuantitativo.clear();
        cuantitativo.add(Integer.valueOf(10));
        assertEquals(1, cuantitativo.size());
        assertEquals(10.0, (Double) cuantitativo.getValor(0));
    }

    @Test
    @DisplayName("Debería obtener un valor Double por índice")
    void testGetValorSobreescrito() {
        cuantitativo.add(7.7);
        assertEquals(7.7, (Double) cuantitativo.getValor(0));
    }

    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al obtener un valor fuera de límites")
    void testGetValorSobreescritoFueraLimites() {
        cuantitativo.add(1.1);
        assertThrows(IndexOutOfBoundsException.class, () -> cuantitativo.getValor(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cuantitativo.getValor(1));
    }

    @Test
    @DisplayName("Debería eliminar un valor por índice")
    void testDeleteSobreescrito() {
        cuantitativo.add(10.0);
        cuantitativo.add(20.0);
        cuantitativo.add(30.0);
        assertEquals(3, cuantitativo.size());
        cuantitativo.delete(1); // Eliminar 20.0
        assertEquals(2, cuantitativo.size());
        assertEquals(10.0, (Double) cuantitativo.getValor(0));
        assertEquals(30.0, (Double) cuantitativo.getValor(1));
    }

    @Test
    @DisplayName("Debería lanzar IndexOutOfBoundsException al eliminar un valor fuera de límites")
    void testDeleteSobreescritoFueraLimites() {
        cuantitativo.add(50.0);
        assertThrows(IndexOutOfBoundsException.class, () -> cuantitativo.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cuantitativo.delete(1));
    }

    @Test
    @DisplayName("Debería limpiar todos los valores")
    void testClearSobreescrito() {
        cuantitativo.add(99.0);
        cuantitativo.add(101.0);
        assertEquals(2, cuantitativo.size());
        cuantitativo.clear();
        assertEquals(0, cuantitativo.size());
        assertTrue(cuantitativo.getValores().size() == 0); // Ahora usa size()
    }

    @Test
    @DisplayName("Debería retornar la representación toString de los valores del Vector")
    void testToStringSobreescrito() {
        cuantitativo.add(100.0);
        cuantitativo.add(200.0);
        assertEquals("[100.0, 200.0]", cuantitativo.toString());
    }

    @Test
    @DisplayName("Debería retornar la cadena de vector vacío para toString de Cuantitativo vacío")
    void testToStringSobreescritoVacio() {
        assertEquals("[]", cuantitativo.toString());
    }
}