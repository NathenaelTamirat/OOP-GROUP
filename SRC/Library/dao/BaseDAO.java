package library.dao;

import library.exception.DatabaseException;
import java.util.List;

public interface BaseDAO<T> {
    
    boolean save(T entity) throws DatabaseException;
    boolean update(T entity) throws DatabaseException;
    boolean delete(String id) throws DatabaseException;
    T findById(String id) throws DatabaseException;
    List<T> findAll() throws DatabaseException;
    
    default boolean exists(String id) throws DatabaseException {
        return findById(id) != null;
    }
    
    default int count() throws DatabaseException {
        return findAll().size();
    }
    
    default int saveAll(List<T> entities) throws DatabaseException {
        if (entities == null || entities.isEmpty()) return 0;
        int successCount = 0;
        for (T entity : entities) {
            if (save(entity)) successCount++;
        }
        return successCount;
    }
    
    default int updateAll(List<T> entities) throws DatabaseException {
        if (entities == null || entities.isEmpty()) return 0;
        int successCount = 0;
        for (T entity : entities) {
            if (update(entity)) successCount++;
        }
        return successCount;
    }
    
    default int deleteAll(List<String> ids) throws DatabaseException {
        if (ids == null || ids.isEmpty()) return 0;
        int successCount = 0;
        for (String id : ids) {
            if (delete(id)) successCount++;
        }
        return successCount;
    }
    
    default List<T> executeQuery(String query, Object... parameters) throws DatabaseException {
        throw new UnsupportedOperationException("Custom query execution not implemented");
    }
    
    default int executeUpdate(String query, Object... parameters) throws DatabaseException {
        throw new UnsupportedOperationException("Custom update execution not implemented");
    }
    
    default void beginTransaction() throws DatabaseException {
        throw new UnsupportedOperationException("Transaction support not implemented");
    }
    
    default void commitTransaction() throws DatabaseException {
        throw new UnsupportedOperationException("Transaction support not implemented");
    }
    
    default void rollbackTransaction() throws DatabaseException {
        throw new UnsupportedOperationException("Transaction support not implemented");
    }
}