package library.service;

import java.util.List;


public interface Manageable<T> {
    

    boolean add(T item);
    

    boolean update(T item);
    

    boolean delete(String id);
    

    T findById(String id);
    

    List<T> findAll();
    

    default boolean exists(String id) {
        return findById(id) != null;
    }
    

    default int count() {
        return findAll().size();
    }
    

    default int addAll(List<T> items) {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (T item : items) {
            if (add(item)) {
                successCount++;
            }
        }
        return successCount;
    }
    

    default int deleteAll(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (String id : ids) {
            if (delete(id)) {
                successCount++;
            }
        }
        return successCount;
    }
    

    default boolean validate(T item) {
        return item != null;
    }
    

    default int clear() {
        throw new UnsupportedOperationException("Clear operation not supported");
    }
}