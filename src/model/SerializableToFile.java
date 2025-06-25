package model;

public interface SerializableToFile<T extends Task> {
    String serrializeToString();
    T serializeFromString(String stringFromFile);
}
