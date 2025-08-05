package library.gui;

import library.model.*;

import javax.swing.*;
import java.awt.*;

/**
 * Custom renderers for combo boxes to display user-friendly text.
 */
public class ComboBoxRenderer {
    
    /**
     * Custom renderer for User combo box.
     */
    public static class UserRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof User) {
                User user = (User) value;
                setText(user.getName() + " (" + user.getId() + ")");
            }
            
            return this;
        }
    }
    
    /**
     * Custom renderer for Book combo box.
     */
    public static class BookRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Book) {
                Book book = (Book) value;
                setText(book.getTitle() + " by " + book.getAuthor() + " (" + book.getId() + ")");
            }
            
            return this;
        }
    }
    
    /**
     * Custom renderer for Category combo box.
     */
    public static class CategoryRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Category) {
                Category category = (Category) value;
                setText(category.getName());
            } else if (value == null) {
                setText("No Category");
            }
            
            return this;
        }
    }
}