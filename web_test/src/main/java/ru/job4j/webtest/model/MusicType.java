package ru.job4j.webtest.model;

/**
 * Music type.
 */
public class MusicType extends Model {
    /**
     * Music type.
     */
    private String type;

    /**
     * @param id   id model
     * @param type type Music type
     */
    public MusicType(int id, String type) {
        super(id);
        this.type = type;
    }

    /**
     * getter.
     *
     * @return Music type
     */
    public String getType() {
        return type;
    }

    /**
     * setter.
     *
     * @param type Music type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || ((o != null && getClass() == o.getClass()) && this.type.equals(((MusicType) o).type));
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
}