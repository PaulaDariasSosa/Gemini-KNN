package datos;

public abstract class Atributo {
	protected double peso = 1;
	protected String nombre;
	
	public abstract Object getValores();
	
	public String getNombre() {
		return this.nombre;
	}
	
	public double getPeso() {
		return this.peso;
	}
	
	public void setNombre(String nuevo) {
		this.nombre = nuevo;
	}
	
	public void setPeso(double nuevo) {
		this.peso = nuevo;
	}
	
	public String get() {
		return (this.nombre + ": " + this.peso);
	}
	
	public abstract int size();
	
	public abstract void add(Object valor);
	
	public abstract void delete(int indice);
	
	public abstract Object getValor(int i);
	
	public abstract String toString();
	
	public abstract void clear();

}
