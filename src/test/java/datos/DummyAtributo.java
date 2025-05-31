package datos;

import java.util.ArrayList;
import java.util.List;

/**
 * /**
 * \brief A concrete implementation of the abstract Atributo class for testing purposes.
 *
 * This class extends the abstract `Atributo` class and provides a basic implementation
 * for its abstract methods. It is primarily intended for testing scenarios where
 * a simple attribute behavior is needed without the complexities of `Cuantitativo`
 * or `Cualitativo`.
 */
public class DummyAtributo extends Atributo {
    private List<Object> dummyValues;

    /**
     * \brief Constructs a new DummyAtributo with a specified name and weight.
     *
     * @param name The name of the attribute.
     * @param weight The weight of the attribute.
     */
    public DummyAtributo(String name, double weight) {
        this.nombre = name;
        this.peso = weight;
        this.dummyValues = new ArrayList<>();
    }

    /**
     * \brief Constructs a new DummyAtributo with a specified name and a default weight of 1.0.
     *
     * @param name The name of the attribute.
     */
    public DummyAtributo(String name) {
        this(name, 1.0); // Default peso
    }

    /**
     * \brief Constructs a new DummyAtributo with a default name "default_name" and a default weight of 1.0.
     */
    public DummyAtributo() {
        this("default_name", 1.0);
    }

    /**
     * \brief Returns a copy of the list of values associated with this attribute.
     *
     * @return A new `ArrayList` containing the attribute's values.
     */
    @Override
    public Object getValores() {
        return new ArrayList<>(dummyValues); // Return a copy
    }

    /**
     * \brief Returns the number of values stored in this attribute.
     *
     * @return The size of the `dummyValues` list.
     */
    @Override
    public int size() {
        return dummyValues.size();
    }

    /**
     * \brief Adds a new value to the attribute's list of values.
     *
     * @param valor The object to be added as a value.
     */
    @Override
    public void add(Object valor) {
        dummyValues.add(valor);
    }

    /**
     * \brief Deletes the value at the specified index from the attribute's list.
     *
     * @param indice The index of the value to be deleted.
     * @throws IndexOutOfBoundsException if the index is out of the bounds [0, size()-1].
     */
    @Override
    public void delete(int indice) {
        if (indice < 0 || indice >= dummyValues.size()) {
            throw new IndexOutOfBoundsException("Index " + indice + " out of bounds for size " + dummyValues.size());
        }
        dummyValues.remove(indice);
    }

    /**
     * \brief Returns the value at the specified index.
     *
     * @param i The index of the value to retrieve.
     * @return The object at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of the bounds [0, size()-1].
     */
    @Override
    public Object getValor(int i) {
        if (i < 0 || i >= dummyValues.size()) {
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds for size " + dummyValues.size());
        }
        return dummyValues.get(i);
    }

    /**
     * \brief Returns a string representation of the DummyAtributo, including its name, weight, and values.
     *
     * @return A string in the format "DummyAtributo [name=..., peso=..., values=...]".
     */
    @Override
    public String toString() {
        return "DummyAtributo [name=" + nombre + ", peso=" + peso + ", values=" + dummyValues.toString() + "]";
    }

    /**
     * \brief Clears all values from the attribute's list, making it empty.
     */
    @Override
    public void clear() {
        dummyValues.clear();
    }
}