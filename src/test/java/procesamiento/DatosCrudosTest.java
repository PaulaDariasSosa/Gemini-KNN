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

@DisplayName("Tests para la clase DatosCrudos")
class DatosCrudosTest {

    private DatosCrudos datosCrudos; // Instancia de la clase a probar

    @BeforeEach
    void setUp() {
        // Se inicializa una nueva instancia de DatosCrudos antes de cada test
        datosCrudos = new DatosCrudos();
    }

    @Test
    @DisplayName("Debería procesar un Dataset vacío y retornar una lista de atributos vacía")
    void testProcesarDatasetVacio() {
        // Arrange
        Dataset datasetVacio = new Dataset();

        // Act
        List<Atributo> atributosProcesados = datosCrudos.procesar(datasetVacio);

        // Assert
        assertNotNull(atributosProcesados, "La lista de atributos procesados no debería ser nula");
        assertTrue(atributosProcesados.isEmpty(), "La lista de atributos procesados debería estar vacía para un Dataset vacío");
        // En DatosCrudos, el método procesar simplemente retorna la referencia directa de getAtributos().
        // Por lo tanto, esperamos que la lista devuelta sea la misma instancia que la del Dataset original.
        assertSame(datasetVacio.getAtributos(), atributosProcesados, "La lista de atributos devuelta debería ser la misma instancia que la del Dataset original");
    }

    @Test
    @DisplayName("Debería procesar un Dataset con atributos y retornar la misma lista de atributos")
    void testProcesarDatasetConAtributos() {
        // Arrange
        Dataset datasetConAtributos = new Dataset();

        // Crear algunos atributos y añadirlos al dataset
        Cuantitativo attrNumerico = new Cuantitativo("Edad");
        attrNumerico.add(25.0);
        attrNumerico.add(30.0);

        Cualitativo attrCategorico = new Cualitativo("Ciudad");
        attrCategorico.add("Madrid");
        attrCategorico.add("Barcelona");

        datasetConAtributos.getAtributos().add(attrNumerico);
        datasetConAtributos.getAtributos().add(attrCategorico);

        // Guardar la referencia a la lista de atributos original del Dataset
        List<Atributo> atributosOriginales = datasetConAtributos.getAtributos();

        // Act
        List<Atributo> atributosProcesados = datosCrudos.procesar(datasetConAtributos);

        // Assert
        assertNotNull(atributosProcesados, "La lista de atributos procesados no debería ser nula");
        assertEquals(2, atributosProcesados.size(), "La lista de atributos procesados debería tener el mismo número de atributos que el original");

        // Verificar que la lista devuelta sea la misma instancia que la lista interna del Dataset.
        // Esto es crucial para DatosCrudos, ya que su implementación es un paso a través directo.
        assertSame(atributosOriginales, atributosProcesados, "La lista de atributos devuelta debería ser la misma instancia que la lista interna del Dataset");

        // Verificar que los atributos dentro de la lista sean los mismos objetos (misma referencia)
        assertSame(attrNumerico, atributosProcesados.get(0), "El primer atributo debería ser el mismo objeto");
        assertSame(attrCategorico, atributosProcesados.get(1), "El segundo atributo debería ser el mismo objeto");

        // Opcional: verificar que los datos dentro de los atributos también se mantengan
        assertEquals(25.0, ((Cuantitativo)atributosProcesados.get(0)).getValor(0));
        assertEquals("Madrid", ((Cualitativo)atributosProcesados.get(1)).getValor(0));
    }

    @Test
    @DisplayName("Debería retornar una lista mutable, reflejando el comportamiento de Dataset.getAtributos()")
    void testProcesarRetornaListaMutable() {
        // Arrange
        Dataset dataset = new Dataset();
        Cuantitativo attr = new Cuantitativo("Test");
        dataset.getAtributos().add(attr);

        // Act
        List<Atributo> atributosProcesados = datosCrudos.procesar(dataset);

        // Assert
        assertFalse(atributosProcesados.isEmpty(), "La lista no debería estar vacía inicialmente");

        // Modificar la lista retornada por procesar
        atributosProcesados.add(new Cualitativo("NuevoAttr"));

        // Verificar que la lista interna del dataset también se haya modificado,
        // ya que procesar() devuelve la misma referencia (getAtributos()).
        assertEquals(2, dataset.numeroAtributos(), "Añadir a la lista procesada debería afectar al Dataset original");
        assertEquals("NuevoAttr", dataset.getAtributos().get(1).getNombre(), "El nuevo atributo debería estar en el Dataset original");
    }
}