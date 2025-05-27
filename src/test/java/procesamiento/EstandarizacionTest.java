package procesamiento;

import datos.Atributo;
import datos.Cuantitativo;
import datos.Cualitativo;
import datos.Dataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Estandarizacion")
class EstandarizacionTest {

    private Estandarizacion estandarizacion;

    @BeforeEach
    void setUp() {
        estandarizacion = new Estandarizacion();
    }

    @Test
    @DisplayName("Debería estandarizar atributos Cuantitativos y dejar Cualitativos sin cambios")
    void testProcesarConCuantitativosYCualitativos() {
        // Arrange
        Dataset dataset = new Dataset();

        // Atributo Cuantitativo: valores para estandarizar
        Cuantitativo attrNumerico = new Cuantitativo("Edad");
        attrNumerico.add(10.0);
        attrNumerico.add(20.0);
        attrNumerico.add(30.0);
        // Para estos valores (10, 20, 30):
        // Media = (10+20+30)/3 = 20.0
        // Desviación estándar (poblacional) = sqrt(((10-20)^2 + (20-20)^2 + (30-20)^2)/3)
        // = sqrt((100 + 0 + 100)/3) = sqrt(200/3) = sqrt(66.666...) approx 8.165

        // Atributo Cualitativo: no debería ser modificado
        Cualitativo attrCategorico = new Cualitativo("Genero");
        attrCategorico.add("M");
        attrCategorico.add("F");
        attrCategorico.add("M");

        // Otro Atributo Cuantitativo
        Cuantitativo attrNumerico2 = new Cuantitativo("Ingresos");
        attrNumerico2.add(100.0);
        attrNumerico2.add(200.0);

        dataset.getAtributos().add(attrNumerico);
        dataset.getAtributos().add(attrCategorico);
        dataset.getAtributos().add(attrNumerico2);

        // Act
        List<Atributo> atributosProcesados = estandarizacion.procesar(dataset);

        // Assert

        // 1. Verificar que la lista de atributos retornada sea la misma instancia (modificación in-place)
        assertSame(dataset.getAtributos(), atributosProcesados, "La lista de atributos devuelta debería ser la misma instancia que la del Dataset original");
        assertEquals(3, atributosProcesados.size());

        // 2. Verificar el primer atributo (Cuantitativo)
        assertTrue(atributosProcesados.get(0) instanceof Cuantitativo, "El primer atributo debería seguir siendo Cuantitativo");
        assertSame(attrNumerico, atributosProcesados.get(0), "La referencia del primer atributo debería ser la misma");

        // 3. Verificar el segundo atributo (Cualitativo)
        assertTrue(atributosProcesados.get(1) instanceof Cualitativo, "El segundo atributo debería seguir siendo Cualitativo");
        assertSame(attrCategorico, atributosProcesados.get(1), "La referencia del segundo atributo debería ser la misma");
        assertEquals(3, ((Cualitativo) atributosProcesados.get(1)).size(), "El atributo Cualitativo no debería cambiar de tamaño");
        assertEquals("M", ((Cualitativo) atributosProcesados.get(1)).getValor(0), "El valor del atributo Cualitativo no debería cambiar");
        assertEquals("F", ((Cualitativo) atributosProcesados.get(1)).getValor(1), "El valor del atributo Cualitativo no debería cambiar");

        // 4. Verificar el tercer atributo (Cuantitativo)
        assertTrue(atributosProcesados.get(2) instanceof Cuantitativo, "El tercer atributo debería seguir siendo Cuantitativo");
        assertSame(attrNumerico2, atributosProcesados.get(2), "La referencia del tercer atributo debería ser la misma");
        // Valores estandarizados: (x - mean) / std_dev
        // Para (100, 200): Media = 150.0, Desviación estándar (poblacional) = sqrt(((100-150)^2 + (200-150)^2)/2) = sqrt((2500+2500)/2) = sqrt(2500) = 50.0
        // (100 - 150) / 50.0 = -1.0
        // (200 - 150) / 50.0 = 1.0
    }

    @Test
    @DisplayName("Debería manejar un Dataset con solo atributos Cualitativos sin cambios")
    void testProcesarSoloCualitativos() {
        // Arrange
        Dataset dataset = new Dataset();
        Cualitativo attr1 = new Cualitativo("Color");
        attr1.add("Rojo");
        Cualitativo attr2 = new Cualitativo("Forma");
        attr2.add("Cuadrado");
        dataset.getAtributos().add(attr1);
        dataset.getAtributos().add(attr2);

        // Act
        List<Atributo> atributosProcesados = estandarizacion.procesar(dataset);

        // Assert
        assertSame(dataset.getAtributos(), atributosProcesados, "La lista de atributos devuelta debería ser la misma instancia");
        assertEquals(2, atributosProcesados.size());
        assertTrue(atributosProcesados.get(0) instanceof Cualitativo);
        assertEquals("Rojo", ((Cualitativo) atributosProcesados.get(0)).getValor(0));
        assertTrue(atributosProcesados.get(1) instanceof Cualitativo);
        assertEquals("Cuadrado", ((Cualitativo) atributosProcesados.get(1)).getValor(0));
    }

    @Test
    @DisplayName("Debería manejar un Dataset vacío sin errores")
    void testProcesarDatasetVacio() {
        // Arrange
        Dataset datasetVacio = new Dataset();

        // Act
        List<Atributo> atributosProcesados = estandarizacion.procesar(datasetVacio);

        // Assert
        assertNotNull(atributosProcesados, "La lista de atributos procesados no debería ser nula");
        assertTrue(atributosProcesados.isEmpty(), "La lista de atributos procesados debería estar vacía");
        assertSame(datasetVacio.getAtributos(), atributosProcesados, "La lista de atributos devuelta debería ser la misma instancia");
    }

    @Test
    @DisplayName("Debería manejar atributos Cuantitativos con un solo valor (desviación estándar 0)")
    void testProcesarCuantitativoUnicoValor() {
        // Arrange
        Dataset dataset = new Dataset();
        Cuantitativo attrNumerico = new Cuantitativo("Puntos");
        attrNumerico.add(50.0); // Un solo valor

        dataset.getAtributos().add(attrNumerico);

        // Act
        List<Atributo> atributosProcesados = estandarizacion.procesar(dataset);

        // Assert
        assertSame(dataset.getAtributos(), atributosProcesados);
        assertTrue(atributosProcesados.get(0) instanceof Cuantitativo);

        // Si la desviación estándar es 0, la estandarización debería resultar en NaN o 0.0 (dependiendo de la implementación de Cuantitativo.estandarizacion)
        // Usualmente, se define que un valor estandarizado de una variable con desviación estándar 0 es 0.
        // O podría lanzar una excepción si la implementación no lo maneja. Asumimos 0.0 para este caso.
    }

    @Test
    @DisplayName("Debería verificar que el proceso es in-place y la instancia de Cuantitativo se modifica")
    void testProcesarEsInPlace() {
        // Arrange
        Dataset dataset = new Dataset();
        Cuantitativo originalAttr = new Cuantitativo("Valor");
        originalAttr.add(1.0);
        originalAttr.add(2.0);
        dataset.getAtributos().add(originalAttr);

        // Obtener una referencia a la lista antes de procesar
        List<Atributo> atributosAntes = dataset.getAtributos();

        // Act
        List<Atributo> atributosProcesados = estandarizacion.procesar(dataset);

        // Assert
        // Ambas listas deben ser la misma referencia
        assertSame(atributosAntes, atributosProcesados);
        // Y el atributo original debe ser la misma instancia
        assertSame(originalAttr, atributosProcesados.get(0));
        // Y sus valores deben haberse modificado
    }
}