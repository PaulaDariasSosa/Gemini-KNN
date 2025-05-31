package procesamiento;

import java.util.List;

import datos.Atributo;
import datos.Cuantitativo;
import datos.Dataset;

/**
 * @file Estandarizacion.java
 * @brief Implementación de la interfaz {@link Preprocesado} para aplicar la estandarización Z-score.
 *
 * Esta clase procesa un {@link Dataset} estandarizando solo los atributos de tipo
 * {@link Cuantitativo} utilizando la fórmula (valor - media) / desviación_estándar.
 * Los atributos cualitativos no son modificados.
 *
 * @author [Tu Nombre/Nombre del Equipo]
 * @version 1.0
 * @since 2023-XX-XX
 * @see Preprocesado
 * @see datos.Cuantitativo#estandarizacion()
 */
public class Estandarizacion implements Preprocesado{

	/**
	 * @brief Procesa un {@link Dataset} aplicando la estandarización Z-score a sus atributos cuantitativos.
	 * <p>
	 * Itera sobre la lista de atributos del dataset. Si un atributo es de tipo {@link Cuantitativo},
	 * aplica el método {@link Cuantitativo#estandarizacion()} para transformar sus valores.
	 * Los atributos cualitativos no son afectados.
	 *
	 * @param datos El {@link Dataset} de entrada cuyos atributos serán estandarizados.
	 * @return Una {@link List} de {@link Atributo}s con los atributos cuantitativos estandarizados.
	 */
	public List<Atributo> procesar(Dataset datos) {
		List<Atributo> nuevos = datos.getAtributos();
		Cuantitativo ejemplo = new Cuantitativo(); // Se utiliza una instancia para comparar el tipo de clase
		for (int i = 0; i < nuevos.size(); i++) {
			// Compara si el atributo actual es de la misma clase que un Cuantitativo
			if (nuevos.get(i).getClass() == ejemplo.getClass()) {
				ejemplo = (Cuantitativo) nuevos.get(i); // Realiza el casting a Cuantitativo
				ejemplo.estandarizacion(); // Llama al método de estandarización del atributo cuantitativo
				nuevos.set(i,ejemplo); // Actualiza el atributo en la lista (aunque la referencia ya es la misma)
			}
		}
		return nuevos;
	}

}