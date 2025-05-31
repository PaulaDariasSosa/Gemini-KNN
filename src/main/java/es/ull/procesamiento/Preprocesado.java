package procesamiento;
import java.util.List;

import datos.Atributo;
import datos.Dataset;

/**
 * @file Preprocesado.java
 * @brief Interfaz para definir operaciones de preprocesamiento de un conjunto de datos.
 *
 * Esta interfaz establece un contrato para las clases que implementarán
 * diferentes algoritmos o técnicas de preprocesamiento de datos.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 */
public interface Preprocesado {

	/**
	 * @brief Procesa un {@link Dataset} y devuelve una lista de {@link Atributo}s preprocesados.
	 * <p>
	 * Este método abstracto debe ser implementado por cualquier clase que desee
	 * proporcionar una funcionalidad de preprocesamiento de datos.
	 *
	 * @param datos El {@link Dataset} de entrada que se va a procesar.
	 * @return Una {@link List} de {@link Atributo}s resultantes del preprocesamiento.
	 */
	public List<Atributo> procesar(Dataset datos);
}