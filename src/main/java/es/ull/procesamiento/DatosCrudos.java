package procesamiento;

import java.util.ArrayList;
import java.util.List;

import datos.*;

public class DatosCrudos implements Preprocesado{

	public List<Atributo> procesar(Dataset datos) {
		return datos.getAtributos();
	}
}
