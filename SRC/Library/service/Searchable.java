package library.service;

import java.util.List;

public interface Searchable<T> {
    
    List<T> searchByTitle(String title);
    List<T> searchByAuthor(String author);
    List<T> searchByCategory(String category);
    List<T> searchByIsbn(String isbn);
    
    default List<T> searchByCriteria(String title, String author, String category) {
        throw new UnsupportedOperationException("Combined search not implemented");
    }
    
    default List<T> searchByKeyword(String searchTerm) {
        throw new UnsupportedOperationException("Keyword search not implemented");
    }
    
    default int getTotalCount() {
        throw new UnsupportedOperationException("Count functionality not implemented");
    }
    
    default List<T> searchWithPagination(String searchTerm, int page, int pageSize) {
        throw new UnsupportedOperationException("Paginated search not implemented");
    }
}