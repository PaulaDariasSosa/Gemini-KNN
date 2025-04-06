package procesamiento;
import java.util.List;

import datos.Atributo;
import datos.Dataset;

public interface Preprocesado {
	
	public List<Atributo> procesar(Dataset datos);
}
