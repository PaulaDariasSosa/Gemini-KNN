package procesamiento;

import java.util.ArrayList;
import java.util.List;

import datos.*;
import vectores.Vector;

/**
 * @file Normalizacion.java
 * @brief Implementación de la interfaz {@link Preprocesado} para aplicar la normalización Min-Max.
 *
 * Esta clase procesa un {@link Dataset} normalizando solo los atributos de tipo
 * {@link Cuantitativo} a un rango específico (típicamente [0, 1]).
 * Los atributos cualitativos no son modificados.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 * @see Preprocesado
 * @see vectores.Vector#normalize()
 */
public class Normalizacion implements Preprocesado{

	/**
	 * @brief Procesa un {@link Dataset} aplicando la normalización Min-Max a sus atributos cuantitativos.
	 * <p>
	 * Itera sobre la lista de atributos del dataset. Si un atributo es de tipo {@link Cuantitativo},
	 * obtiene sus valores como un {@link Vector} y aplica el método {@link Vector#normalize()}
	 * para transformar sus valores. Los atributos cualitativos no son afectados.
	 *
	 * @param datos El {@link Dataset} de entrada cuyos atributos serán normalizados.
	 * @return Una {@link List} de {@link Atributo}s con los atributos cuantitativos normalizados.
	 */
	public List<Atributo> procesar(Dataset datos) {
		List<Atributo> nuevos = new ArrayList<Atributo>(datos.getAtributos()); // Crear una copia defensiva
		Cuantitativo ejemplo = new Cuantitativo(); // Se utiliza una instancia para comparar el tipo de clase
		for (int i = 0; i < nuevos.size(); i++) {
			// Compara si el atributo actual es de la misma clase que un Cuantitativo
			if (nuevos.get(i).getClass() == ejemplo.getClass()) {
				ejemplo = (Cuantitativo) nuevos.get(i); // Realiza el casting a Cuantitativo
				Vector valores = ejemplo.getValores(); // Obtiene el Vector de valores
				valores.normalize(); // Llama al método de normalización del Vector
				ejemplo.setValores(valores); // Establece los valores normalizados de vuelta en el atributo
				nuevos.set(i,ejemplo); // Actualiza el atributo en la lista (aunque la referencia ya es la misma)
			}
		}
		return nuevos;
	}

}