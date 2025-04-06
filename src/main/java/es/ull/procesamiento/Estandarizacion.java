package procesamiento;

import java.util.List;

import datos.Atributo;
import datos.Cuantitativo;
import datos.Dataset;

public class Estandarizacion implements Preprocesado{
	
	public List<Atributo> procesar(Dataset datos) {
		List<Atributo> nuevos = datos.getAtributos();
		Cuantitativo ejemplo = new Cuantitativo();
		for (int i = 0; i < nuevos.size(); i++) {
			if (nuevos.get(i).getClass() == ejemplo.getClass()) {
				ejemplo = (Cuantitativo) nuevos.get(i);
				ejemplo.estandarizacion();
				nuevos.set(i,ejemplo);
			}
		}
		return nuevos;
	}
	
}
