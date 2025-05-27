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
		// Delegar en el método de Vector que ya maneja el caso vacío
		return this.valores.getMin();
	}

	public double maximo() {
		// Delegar en el método de Vector que ya maneja el caso vacío
		return this.valores.getMax();
	}

	public double media() {
		if (this.valores.size() == 0) { // Añadir esta validación
			throw new IllegalStateException("No se puede calcular la media de un vector vacío.");
		}
		double suma = 0.0; // Cambiar inicialización
		for(int i = 0; i < this.valores.size(); ++i) {
			suma += this.valores.get(i);
		}
		return suma / this.valores.size();
	}

	public double desviacion() {
		double media = this.media();
		double auxiliar = 0;
		for(int i = 0; i < this.valores.size(); ++i) {
			auxiliar += (this.valores.get(i) - media) * (this.valores.get(i) - media);
		}
		auxiliar /= this.valores.size(); // Tu calculas desviación poblacional. Eso está bien.
		return Math.sqrt(auxiliar);
	}
	
	public int size() {
		return this.valores.size();
	}

	public void estandarizacion() {
		if (this.valores.size() == 0) { // Añadir esta validación
			throw new IllegalStateException("No se puede estandarizar un vector vacío.");
		}
		double media = this.media();
		double desviacion = this.desviacion(); // Calcular una vez
		if (desviacion == 0.0) { // Manejar el caso de desviación cero
			for (int i = 0; i < valores.size(); ++i) {
				valores.set(i, 0.0); // Si todos los valores son iguales, su Z-score es 0.
			}
			return;
		}
		for (int i = 0; i < valores.size(); ++i) {
			valores.set(i, (valores.get(i) - media) / desviacion);
		}
	}

	@Override
	public void add(Object valor) {
		if (valor instanceof Number) { // Verifica si es un número (Integer, Double, etc.)
			valores.add(((Number) valor).doubleValue()); // Convierte a Double
		} else {
			throw new ClassCastException("El valor añadido debe ser un número convertible a Double.");
		}
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
