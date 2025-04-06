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
		this.valores = nuevos;
	}
	
	public Instancia(String nuevos){
		String[] subcadenas = nuevos.split(",");
		ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
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
	
	public void estandarizar() {
		Vector aux = this.getVector();
		double media = 0.0;
		for(int i = 0; i < aux.size(); ++i) {
			media += aux.get(i);
		}
		media =  media/aux.size(); 
		double auxiliar = 0;
		for(int i = 0; i < aux.size(); ++i) {
			auxiliar += (aux.get(i) - media) * (aux.get(i) - media);
		}
		auxiliar /= this.valores.size();
		double desviacion = Math.sqrt(auxiliar);
		for (int i = 0; i < aux.size(); ++i) {
			aux.set(i, (aux.get(i)-media)/desviacion);
		}
		ArrayList<Object> arrayListObject = new ArrayList<>();
        for (Double d : aux.getValores()) {
            arrayListObject.add(d); // La conversión automática de tipos se encarga de convertir Double a Object
        }
		this.valores = arrayListObject;
	}
	
	public void deleteClase() {
		valores.remove(valores.size() - 1);
	}

}
