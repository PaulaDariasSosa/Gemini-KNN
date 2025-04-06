package datos;

import java.util.ArrayList;
import java.util.List;

public class Cualitativo extends Atributo{
	private List<String> valores;
	
	public Cualitativo() {
		this.nombre = "";
		this.valores = new ArrayList<String>();
	}
	
	public Cualitativo(String name) {
		this();
		this.nombre = name;
	}
	
	public Cualitativo(String name, String valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}
	
	public Cualitativo(String name, List<String> valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}

	public Cualitativo(Cualitativo otro) {
		this();
		this.nombre = otro.getNombre();
		this.peso = otro.getPeso();
		this.valores = new ArrayList<String>();
	}
	
	public List<String> getValores() {
		return this.valores;
	}
	
	public void setValores(List<String> nuevos) {
		this.valores = nuevos;
	}
	
	public List<String> clases() {
		ArrayList<String> clases = new ArrayList<>();
		for(int i = 0; i < this.valores.size(); ++i) {
			if(!clases.contains(this.valores.get(i))) clases.add(this.valores.get(i));
		}
		return clases;
	}
	
	public int nClases() {
		return this.clases().size();
	}
	
	public List<Double> frecuencia() {
		List<String> clases = this.clases();
		ArrayList<Double> frecuencias = new ArrayList<>();
		for (int j = 0; j < this.nClases(); ++j) {
			double auxiliar = 0;
			for(int i = 0; i < this.valores.size(); ++i) {
				if(clases.get(j).equals(this.valores.get(i))) auxiliar++;
			}
			frecuencias.add(auxiliar/this.valores.size());
		}
		return frecuencias;
	}
	
	public int size() {
		return this.valores.size();
	}
	
	@Override
	public void add(Object valor) {
		valores.add((String) valor);
	}
	
	@Override
	public Object getValor(int i) {
		return valores.get(i);
		
	}
	
	@Override
	public void delete(int index) {
		valores.remove(index);
	}
	
	@Override
	public String toString() {
		return valores.toString();
		
	}
	
	@Override
	public void clear() {
		valores.clear();
	}

}
