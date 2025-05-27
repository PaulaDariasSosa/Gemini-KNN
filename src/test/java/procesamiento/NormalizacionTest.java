package procesamiento;

import datos.Atributo;
import datos.Cuantitativo;
import datos.Cualitativo;
import datos.Dataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vectores.Vector; // Assuming your Vector class is here

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Normalizacion")
class NormalizacionTest {

    private Normalizacion normalizacion;

    @BeforeEach
    void setUp() {
        normalizacion = new Normalizacion();
    }

    @Test
    @DisplayName("Debería normalizar atributos Cuantitativos (0-1) y dejar Cualitativos sin cambios")
    void testProcesarConCuantitativosYCualitativos() {
        // Arrange
        Dataset dataset = new Dataset();

        // Atributo Cuantitativo: valores para normalizar
        Cuantitativo attrNumerico = new Cuantitativo("Edad");
        attrNumerico.add(10.0);
        attrNumerico.add(20.0);
        attrNumerico.add(30.0);
        // Para estos valores (10, 20, 30):
        // Min = 10, Max = 30
        // (10-10)/(30-10) = 0/20 = 0.0
        // (20-10)/(30-10) = 10/20 = 0.5
        // (30-10)/(30-10) = 20/20 = 1.0

        // Atributo Cualitativo: no debería ser modificado
        Cualitativo attrCategorico = new Cualitativo("Genero");
        attrCategorico.add("M");
        attrCategorico.add("F");
        attrCategorico.add("M");

        // Otro Atributo Cuantitativo
        Cuantitativo attrNumerico2 = new Cuantitativo("Puntuacion");
        attrNumerico2.add(50.0);
        attrNumerico2.add(100.0);
        attrNumerico2.add(75.0);
        // Para estos valores (50, 100, 75):
        // Min = 50, Max = 100
        // (50-50)/(100-50) = 0.0
        // (100-50)/(100-50) = 1.0
        // (75-50)/(100-50) = 25/50 = 0.5

        dataset.getAtributos().add(attrNumerico);
        dataset.getAtributos().add(attrCategorico);
        dataset.getAtributos().add(attrNumerico2);

        // Guardar la referencia a la lista de atributos original del Dataset
        List<Atributo> originalDatasetAttrs = dataset.getAtributos();

        // Act
        List<Atributo> atributosProcesados = normalizacion.procesar(dataset);

        // Assert

        // 1. Verificar que la lista de atributos retornada sea una NUEVA instancia
        assertNotSame(originalDatasetAttrs, atributosProcesados, "La lista de atributos devuelta debería ser una nueva instancia");
        assertEquals(3, atributosProcesados.size(), "La lista de atributos procesados debería tener el mismo número de atributos");

        // 2. Verificar el primer atributo (Cuantitativo - 'Edad')
        assertTrue(atributosProcesados.get(0) instanceof Cuantitativo, "El primer atributo debería seguir siendo Cuantitativo");
        // Los objetos Cuantitativo son los mismos, solo sus valores internos cambian.
        assertSame(attrNumerico, atributosProcesados.get(0), "La referencia al objeto Cuantitativo 'Edad' debería ser la misma");

        // 3. Verificar el segundo atributo (Cualitativo - 'Genero')
        assertTrue(atributosProcesados.get(1) instanceof Cualitativo, "El segundo atributo debería seguir siendo Cualitativo");
        assertSame(attrCategorico, atributosProcesados.get(1), "La referencia al objeto Cualitativo 'Genero' debería ser la misma");
        assertEquals(3, ((Cualitativo) atributosProcesados.get(1)).size(), "El atributo Cualitativo no debería cambiar de tamaño");
        assertEquals("M", ((Cualitativo) atributosProcesados.get(1)).getValor(0), "El valor del atributo Cualitativo no debería cambiar");
        assertEquals("F", ((Cualitativo) atributosProcesados.get(1)).getValor(1), "El valor del atributo Cualitativo no debería cambiar");

        // 4. Verificar el tercer atributo (Cuantitativo - 'Puntuacion')
        assertTrue(atributosProcesados.get(2) instanceof Cuantitativo, "El tercer atributo debería seguir siendo Cuantitativo");
        assertSame(attrNumerico2, atributosProcesados.get(2), "La referencia al objeto Cuantitativo 'Puntuacion' debería ser la misma");
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

        List<Atributo> originalDatasetAttrs = dataset.getAtributos();

        // Act
        List<Atributo> atributosProcesados = normalizacion.procesar(dataset);

        // Assert
        assertNotSame(originalDatasetAttrs, atributosProcesados, "La lista de atributos devuelta debería ser una nueva instancia");
        assertEquals(2, atributosProcesados.size());

        assertTrue(atributosProcesados.get(0) instanceof Cualitativo);
        assertSame(attr1, atributosProcesados.get(0), "La referencia al objeto Cualitativo 'Color' debería ser la misma");
        assertEquals("Rojo", ((Cualitativo) atributosProcesados.get(0)).getValor(0));

        assertTrue(atributosProcesados.get(1) instanceof Cualitativo);
        assertSame(attr2, atributosProcesados.get(1), "La referencia al objeto Cualitativo 'Forma' debería ser la misma");
        assertEquals("Cuadrado", ((Cualitativo) atributosProcesados.get(1)).getValor(0));
    }

    @Test
    @DisplayName("Debería manejar un Dataset vacío sin errores")
    void testProcesarDatasetVacio() {
        // Arrange
        Dataset datasetVacio = new Dataset();

        // Act
        List<Atributo> atributosProcesados = normalizacion.procesar(datasetVacio);

        // Assert
        assertNotNull(atributosProcesados, "La lista de atributos procesados no debería ser nula");
        assertTrue(atributosProcesados.isEmpty(), "La lista de atributos procesados debería estar vacía");
        // Aunque la lista original esté vacía, el constructor de ArrayList hace una nueva lista vacía.
        assertNotSame(datasetVacio.getAtributos(), atributosProcesados, "La lista de atributos devuelta debería ser una nueva instancia (aunque ambas estén vacías)");
    }

    @Test
    @DisplayName("Debería manejar atributos Cuantitativos con un solo valor (min=max)")
    void testProcesarCuantitativoUnicoValor() {
        // Arrange
        Dataset dataset = new Dataset();
        Cuantitativo attrNumerico = new Cuantitativo("Puntos");
        attrNumerico.add(50.0); // Un solo valor

        dataset.getAtributos().add(attrNumerico);

        // Act
        List<Atributo> atributosProcesados = normalizacion.procesar(dataset);

        // Assert
        assertTrue(atributosProcesados.get(0) instanceof Cuantitativo);
    }

    @Test
    @DisplayName("Debería verificar que los objetos Cuantitativo originales son modificados (mutación in-place)")
    void testCuantitativoObjetosModificadosInPlace() {
        // Arrange
        Dataset dataset = new Dataset();
        Cuantitativo originalAttr = new Cuantitativo("Altura");
        originalAttr.add(1.50);
        originalAttr.add(2.00);
        dataset.getAtributos().add(originalAttr);

        // Obtener la referencia a la lista de atributos original del Dataset
        List<Atributo> originalDatasetAttributes = dataset.getAtributos();

        // Act
        List<Atributo> processedAttributes = normalizacion.procesar(dataset);

        // Assert
        // La lista procesada es una nueva instancia, pero contiene la misma referencia a originalAttr
        assertNotSame(originalDatasetAttributes, processedAttributes);
        assertSame(originalAttr, processedAttributes.get(0));

    }
}
