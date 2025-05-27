package datos;

import java.util.ArrayList;
import java.util.List;

/**
 * A concrete implementation of the abstract Atributo class for testing purposes.
 */
public class DummyAtributo extends Atributo {
    private List<Object> dummyValues;

    public DummyAtributo(String name, double weight) {
        this.nombre = name;
        this.peso = weight;
        this.dummyValues = new ArrayList<>();
    }

    public DummyAtributo(String name) {
        this(name, 1.0); // Default peso
    }

    public DummyAtributo() {
        this("default_name", 1.0);
    }

    @Override
    public Object getValores() {
        return new ArrayList<>(dummyValues); // Return a copy
    }

    @Override
    public int size() {
        return dummyValues.size();
    }

    @Override
    public void add(Object valor) {
        dummyValues.add(valor);
    }

    @Override
    public void delete(int indice) {
        if (indice < 0 || indice >= dummyValues.size()) {
            throw new IndexOutOfBoundsException("Index " + indice + " out of bounds for size " + dummyValues.size());
        }
        dummyValues.remove(indice);
    }

    @Override
    public Object getValor(int i) {
        if (i < 0 || i >= dummyValues.size()) {
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds for size " + dummyValues.size());
        }
        return dummyValues.get(i);
    }

    @Override
    public String toString() {
        return "DummyAtributo [name=" + nombre + ", peso=" + peso + ", values=" + dummyValues.toString() + "]";
    }

    @Override
    public void clear() {
        dummyValues.clear();
    }
}
