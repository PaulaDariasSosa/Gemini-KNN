package datos;

import vectores.Vector;

public class Cuantitativo extends Atributo{
	private Vector valores;
	
	public Cuantitativo() {
		this.nombre = "";
		this.valores = new Vector();
	}
	
	public Cuantitativo(String name) {
		this();
		this.nombre = name;
	}
	
	public Cuantitativo(String name, Double valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}
	
	public Cuantitativo(String name, Vector valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}

	public Cuantitativo(Cuantitativo otro) {
		this();
		this.nombre = otro.getNombre();
		this.peso = otro.getPeso();
		this.valores = new Vector();
	}

	public Vector getValores() {
		return this.valores;
	}
	
	public void setValores(Vector nuevos) {
		this.valores = nuevos;
	}
	
	public double minimo() {
		double minimo = this.valores.get(0);
		for(int i = 0; i < this.valores.size(); ++i) {
			if(minimo > this.valores.get(i)) minimo = this.valores.get(i);
		}
		return minimo;
	}
	
	public double maximo() {
		double maximo = this.valores.get(0);
		for(int i = 0; i < this.valores.size(); ++i) {
			if(maximo < this.valores.get(i)) maximo = this.valores.get(i);
		}
		return maximo;
	}
	
	public double media() {
		double media = this.valores.get(0);
		for(int i = 0; i < this.valores.size(); ++i) {
			media += this.valores.get(i);
		}
		return media/this.valores.size();
	}
	
	public double desviacion() {
		double media = this.media();
		double auxiliar = 0;
		for(int i = 0; i < this.valores.size(); ++i) {
			auxiliar += (this.valores.get(i) - media) * (this.valores.get(i) - media);
		}
		auxiliar /= this.valores.size();
		return Math.sqrt(auxiliar);
	}
	
	public int size() {
		return this.valores.size();
	}
	
	public void estandarizacion() {
		for (int i = 0; i < valores.size(); ++i) {
			valores.set(i, (valores.get(i)-this.media())/this.desviacion());
		}
	}

	@Override
	public void add(Object valor) {
		valores.add((double) valor);
		
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
