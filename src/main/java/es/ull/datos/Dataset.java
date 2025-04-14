package datos;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dataset {
	private List<Atributo> atributos;
	int preprocesado;
	
	public Dataset() {
		this.atributos = new ArrayList<Atributo>();
	}
	
	public Dataset(List<Atributo> nuevos) {
		this();
		this.atributos = nuevos;
	}
	
	public Dataset(String filename) throws IOException {
		this();
		this.read(filename);
	}
	
	public Dataset(Dataset datos) {
		this();
		this.atributos = new ArrayList<>(datos.atributos);
		this.preprocesado = datos.preprocesado;
	}

	public void cambiarPeso(List<String> nuevosPesos) {
		if (nuevosPesos.size() != atributos.size()) throw new IllegalArgumentException("El número de pesos para asignar debe ser igual al número de atributos");
		for (int i = 0; i < nuevosPesos.size(); i++) {
			Atributo aux = atributos.get(i);
			try {
				double peso = Double.parseDouble(nuevosPesos.get(i));
				if (peso < 0 || peso > 1) {
					throw new IllegalArgumentException("Los pesos deben estar entre 0 y 1.");
				}
				aux.setPeso(peso);
				this.atributos.set(i, aux);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("El peso '" + nuevosPesos.get(i) + "' no es un número válido.");
			}
		}
	}
	
	// Cambiar peso para uno
	public void cambiarPeso(int index, double peso) {
		Atributo aux = this.atributos.get(index);
		aux.setPeso(peso);
		this.atributos.set(index, aux);
	}

	public void cambiarPeso(double peso) {
	       for (int i = 0; i <  atributos.size(); i++) {
	        Atributo aux = atributos.get(i);
	        aux.setPeso(peso);
	        this.atributos.set(i, aux);
	       }
	}
	
	// Print
	public void print() {
		Logger logger = LoggerFactory.getLogger(Dataset.class.getName());
		if (logger.isInfoEnabled()) {
			logger.info(this.toString());
		}
	}
	
	// toString
	public String toString() {
		StringBuilder sb = new StringBuilder();
		List<String> valores = this.nombreAtributos();
		valores.addAll(this.getValores());
		int contador = 1;
		for (int i = 0; i < valores.size(); ++i) {
			sb.append(valores.get(i));
			if (contador == this.numeroAtributos()) {
				sb.append("\n");
				contador = 1;
			} else {
				sb.append(",");
				++contador;
			}
		}
	    return sb.toString();
	}
	
	// Modify (mezcla de add y delete)
	// Add instancia 
	public void add(Instancia nueva) {
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux =  atributos.get(i);
			aux.add(nueva.getValores().get(i));
			atributos.set(i, aux);
		}	
	}
	
	public void add(List<String> nueva) {
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux =  atributos.get(i);
			try {
				aux.add(Double.parseDouble(nueva.get(i)));
    		} catch (NumberFormatException e) {
    			aux.add(nueva.get(i));
    		}
			atributos.set(i, aux);
		}	
	}
	// Delete
	public void delete(int nueva) {
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux = atributos.get(i);
			aux.delete(nueva);
			atributos.set(i, aux);
		}
	}
	
	// Método para escribir el dataset en un archivo CSV
    public void write(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(this.toString());
        }
    }
	
	public void read(String filename) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Leer la primera línea para obtener los nombres de los atributos
			// llamar al constructor vacio
            String[] attributeNamesArray = reader.readLine().split(",");
            String line;
            if ((line = reader.readLine()) != null) {
            	String[] values = line.split(",");
            	for (int i = 0; i < attributeNamesArray.length ; ++i) {
            		try {
            			this.atributos.add(new Cuantitativo(attributeNamesArray[i], Double.parseDouble(values[i]))); // sino poner encima Double.parseDouble(values[i])
            		} catch (NumberFormatException e) {
            			this.atributos.add(new Cualitativo(attributeNamesArray[i], values[i]));
            		}
            	}
            }
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (int i = 0; i < attributeNamesArray.length ; ++i) {
                	Atributo nuevo = this.atributos.get(i);
            		try {
            			nuevo.add(Double.parseDouble(values[i]));
            		} catch (NumberFormatException e) {
            			nuevo.add(values[i]);
            		}
            		this.atributos.set(i, nuevo);
            	}
            }
        }
	}
	
	// numero atributos
	public int numeroAtributos() {
		return atributos.size();
	}
	
	// nombre atributos
	public List<String> nombreAtributos(){
		ArrayList<String> nombres = new ArrayList<>();
		for(int i = 0; i < atributos.size(); ++i) nombres.add(atributos.get(i).getNombre());
		return nombres;
	}
	
	public List<Atributo> getAtributos(){
		return atributos;
	}
	
	public List<Atributo> getAtributosEmpty() {
		ArrayList<Atributo> aux = new ArrayList<Atributo> (atributos.size());
		for (int i = 0; i < atributos.size(); ++i) {
			try {
				Cualitativo auxiliar = (Cualitativo) atributos.get(i);
				aux.add(new Cualitativo(auxiliar.getNombre()));
			} catch (ClassCastException e) {
				Cuantitativo auxiliar = (Cuantitativo) atributos.get(i);
				aux.add(new Cuantitativo(auxiliar.getNombre()));
			}
		}
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo prov = aux.get(i);
			prov.setPeso(atributos.get(i).getPeso());
			aux.set(i, prov);
		}
		return aux;
	}
	
	// numero casos
	public int numeroCasos() {
		return atributos.get(0).size();
	}

	public List<String> getValores(){
		ArrayList<String> valores = new ArrayList<String>();
		 for (int i = 0; i < atributos.get(0).size(); ++i) {
	        	for (int j = 0; j < atributos.size(); ++j) valores.add(String.valueOf(atributos.get(j).getValor(i)));
		}
		return valores;
	}
	
	public Atributo get(int index) {
		return atributos.get(index);
	}
	
	public Instancia getInstance(int index){
	 	ArrayList<Object> auxiliar = new ArrayList<>();
		for (int i = 0; i < atributos.size(); ++i) auxiliar.add(atributos.get(i).getValor(index));
		return new Instancia (auxiliar);
	}
	
	public List<String> getPesos() {
		ArrayList<String> valores = new ArrayList<String>();
		for (Atributo valor : this.atributos) valores.add(valor.get());
		return valores;
	}

	/**
	 * Obtiene los pesos de los atributos como una lista de Double.
	 * Si hay errores en la conversión, se loguea y se devuelve null.
	 *
	 * @return Una lista de Double con los pesos de los atributos, o null si hay un error.
	 */
	public List<Double> getPesosDouble() {
		List<Double> pesosDouble = new ArrayList<>();
		for (int i = 0; i < atributos.size() - 1; ++i) {
			try {
				pesosDouble.add(atributos.get(i).getPeso());
			} catch (NumberFormatException e) {
				Logger logger = LoggerFactory.getLogger(Dataset.class);
				if (logger.isErrorEnabled()) {
					logger.error("Error al convertir el peso del atributo");
				}
				// En lugar de retornar null, seguimos añadiendo los pesos válidos y logueamos el error.
				// Otra opción sería añadir un valor por defecto (e.g., 1.0) si la conversión falla,
				// dependiendo de cómo quieras manejar los pesos no válidos.
			}
		}
		return pesosDouble; // Retornamos la lista, que podría estar vacía o contener algunos pesos válidos.
	}
	
	public List<String> getClases() {
		return ((Cualitativo) this.atributos.get(atributos.size()-1)).clases();
	}
	
	public int getPreprocesado() {
		return preprocesado;
	}
	
	public void setPreprocesado(int opcion) {
		this.preprocesado = opcion;
	}
	
	public void setAtributos(List<Atributo> nuevos) {
		this.atributos = nuevos;
	}

}