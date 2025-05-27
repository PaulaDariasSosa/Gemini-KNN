package datos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vectores.Vector;

public class Instancia {
	private List<Object> valores;
	
	public Instancia(){
		this.valores = new ArrayList<Object>();
	}

	public Instancia(List<Object> nuevos){
		if (nuevos == null) {
			this.valores = new ArrayList<>(); // Or throw IllegalArgumentException, depending on desired behavior
		} else {
			this.valores = new ArrayList<>(nuevos); // THIS IS THE KEY CHANGE
		}
	}

	public Instancia(String nuevos){
		String[] subcadenas = nuevos.split(",");
		ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(subcadenas)); // This creates a new modifiable ArrayList
		this.valores = arrayList;
	}
	
	public List<Object> getValores() {
		return this.valores;
	}
	
	public String toString() {
		return valores.toString();
	}
	
	public Vector getVector() {
		Vector aux = new Vector();
		for (int i = 0; i < valores.size()-1; ++i) {
			 if (valores.get(i) instanceof Double) {
				 aux.add((Double) valores.get(i));
	         } else if (valores.get(i) instanceof Integer) {
	             aux.add((int) valores.get(i));
	         }
		}
		return aux;
	}
	
	public String getClase() {
		return (String) this.valores.get(valores.size()-1);
	}
	
	public void normalizar() {
		Vector aux = this.getVector();
		aux.normalize();
		ArrayList<Object> arrayListObject = new ArrayList<>();
        for (Double d : aux.getValores()) {
            arrayListObject.add(d); // La conversión automática de tipos se encarga de convertir Double a Object
        }
		this.valores = arrayListObject;
	}

	// Inside Instancia.java
	public void estandarizar() {
		Vector aux = this.getVector();
		if (aux.size() == 0) { // Handle empty vector case gracefully
			this.valores = new ArrayList<>(); // Clear values or keep as is? Current normalize clears.
			return;
		}

		double media = 0.0;
		for(int i = 0; i < aux.size(); ++i) {
			media += aux.get(i);
		}
		media =  media/aux.size();

		double auxiliar = 0; // sum of squared differences
		for(int i = 0; i < aux.size(); ++i) {
			auxiliar += (aux.get(i) - media) * (aux.get(i) - media);
		}

		// Corrected line: Divide by aux.size() for population variance
		double desviacion = 0.0;
		if (aux.size() > 0) { // Ensure no division by zero if aux.size() was 0 (already handled above but good for clarity)
			desviacion = Math.sqrt(auxiliar / aux.size());
		}


		// Handle division by zero for standard deviation
		if (desviacion == 0.0) {
			// If std dev is 0, all values are the same. Standardization makes them 0 (or original value if definition allows).
			// A common approach is to set them all to 0, or leave them as is if they are already 0.
			// Given normalization sets them to original value if range is 0, let's follow that.
			// Or more strictly, if std dev is 0, then x - mean is 0, so result is 0.
			ArrayList<Object> arrayListObject = new ArrayList<>();
			for (int i = 0; i < aux.size(); ++i) {
				arrayListObject.add(0.0); // All standardized values become 0
			}
			this.valores = arrayListObject;
			return;
		}

		for (int i = 0; i < aux.size(); ++i) {
			aux.set(i, (aux.get(i)-media)/desviacion);
		}
		ArrayList<Object> arrayListObject = new ArrayList<>();
		for (Double d : aux.getValores()) {
			arrayListObject.add(d);
		}
		this.valores = arrayListObject;
	}
	
	public void deleteClase() {
		valores.remove(valores.size() - 1);
	}

	public void addClase(String clase) {
		valores.add(clase);
	}

	public void set (int i, Object nuevo) {
		valores.set(i, nuevo);
	}

	public String getValoresString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < valores.size(); ++i) {
			sb.append(valores.get(i));
			if (i < valores.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
}
