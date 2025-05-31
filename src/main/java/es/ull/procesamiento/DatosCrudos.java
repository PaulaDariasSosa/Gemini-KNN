package procesamiento;

import java.util.List;

import datos.*;

/**
 * @file DatosCrudos.java
 * @brief Implementación de la interfaz {@link Preprocesado} que devuelve el dataset sin modificaciones.
 *
 * Esta clase sirve para encapsular la operación de no realizar ningún
 * preprocesamiento sobre los datos, simplemente devolviendo los atributos tal como están.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 * @see Preprocesado
 */
public class DatosCrudos implements Preprocesado{

	/**
	 * @brief Procesa un {@link Dataset} devolviendo sus atributos sin realizar ninguna modificación.
	 * <p>
	 * Este método cumple con el contrato de la interfaz {@link Preprocesado}, pero no aplica
	 * ninguna transformación a los datos, actuando como un "passthrough".
	 *
	 * @param datos El {@link Dataset} de entrada que se va a "procesar".
	 * @return Una {@link List} de {@link Atributo}s idéntica a la obtenida del dataset de entrada.
	 */
	public List<Atributo> procesar(Dataset datos) {
		return datos.getAtributos();
	}
}