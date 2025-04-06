package clasificacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import datos.*;
import vectores.Vector;

public class KNN {
  private int vecinos;
  
  public KNN (int k) {
	  this.vecinos = k;
  }
  
  public Vector getDistancias(Dataset datos, Instancia nueva){
	Vector aux = new Vector();
	// obtenemos el peso de los atributos 
	ArrayList<Atributo>  pesosString = new ArrayList<>(datos.getAtributos());
	ArrayList<Double> pesosDouble = new ArrayList<>();
	for (Atributo str : pesosString) {
        pesosDouble.add(str.getPeso());
    }
    for (int i = 0; i < datos.numeroCasos(); ++i) {
    	aux.add(this.getDistanciaEuclidea(datos.getInstance(i).getVector(), nueva.getVector(), pesosDouble));
    }
    return aux;
  }
  
  public String getClase (List<Instancia> candidatos) {
	  ArrayList<String> nombresClases = new ArrayList<>();
	  for (int i = 0; i < candidatos.size(); i++) {
		  if (!nombresClases.contains(candidatos.get(i).getClase())) nombresClases.add(candidatos.get(i).getClase());
	  }
	  ArrayList<Integer> numeroClases = new ArrayList<>();
	  for (int i = 0; i < nombresClases.size(); i++) {
		  int aux = 0;
		  for (int j = 0; j < candidatos.size();++j) {
			  if (candidatos.get(j).getClase().equals(nombresClases.get(i))) aux += 1;
		  }
		  numeroClases.add(aux);
	  }
	  return nombresClases.get(numeroClases.indexOf(Collections.max(numeroClases)));
			  
  }
  
  public double getDistanciaEuclidea(Vector vieja, Vector nueva) {
	  if (vieja.size() == nueva.size()) {
		  throw new IllegalArgumentException("Los vectores deben tener distinto tamaño para calcular la distancia euclídea.");
	  }
	double dist = 0.0;
	for(int i = 0; i < nueva.size(); i++) {
		dist += Math.pow((vieja.get(i) - nueva.get(i)), 2);
	}
	return Math.sqrt(dist);
  }
  
  public double getDistanciaEuclidea(Vector vieja, Vector nueva, List<Double> pesos) {
	  if (vieja.size() == nueva.size()) {
		  throw new IllegalArgumentException("Los vectores deben tener distinto tamaño para calcular la distancia euclídea.");
	  }
		double dist = 0.0;
		for(int i = 0; i < nueva.size(); i++) {
			dist += Math.pow((vieja.get(i) - nueva.get(i))*pesos.get(i), 2);
		}
		return Math.sqrt(dist);
	  }
  
  public String getVecino(List<Instancia> candidatos, Vector distancias){
	  Vector aux = new Vector();
	  ArrayList<Integer> indices = new ArrayList<>();
	  for (int i = 0; i < vecinos; i++) {
		  aux.add(distancias.get(i));
		  indices.add(i);
	  }
	  // metemos los k primeros elementos en un vector
	  for (int i = 0+vecinos-1; i < candidatos.size(); ++i) {
		// si el elemento mayor del vector tiene 
		if (aux.getMax() > distancias.get(i)) {
			// sacar el mayor y meter el nuevo
			aux.set(aux.getMaxInt(), distancias.get(i));
			indices.set(aux.getMaxInt(), i);
		}
	  }
	  ArrayList<Instancia> elegidos = new ArrayList<>();
	  for (int i = 0; i < indices.size(); i++) elegidos.add(candidatos.get(indices.get(i)));
	  return this.getClase(elegidos);
  }
  
  public String clasificar(Dataset datos, Instancia nueva) {
	  Vector aux = this.getDistancias(datos, nueva);
	  ArrayList<Instancia> elegidos = new ArrayList<>();
	  for (int i = 0; i < datos.numeroCasos(); ++i) {
	    elegidos.add(datos.getInstance(i));
	  }
	  return this.getVecino(elegidos, aux);
  }
}
