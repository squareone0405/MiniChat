package gui;

import java.awt.*;

import javax.swing.*;

import javafx.util.Pair;

public class ContactRenderer implements ListCellRenderer<Object> {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		@SuppressWarnings("unchecked")
		Pair<String, Boolean> contact = (Pair<String, Boolean>) value;
		return new ContactLabel(contact);
	}
}
