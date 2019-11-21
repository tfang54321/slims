package ca.gc.dfo.slims.service;

import java.util.List;

abstract public class AbstractService<T> {

    abstract public List<T> getAll();
    abstract public List<T> getAll(String inputYear);
    abstract public List<T> getAllAfterYear(String inputYear);
    abstract public List<T> getAllBeforeYear(String inputYear);
}
