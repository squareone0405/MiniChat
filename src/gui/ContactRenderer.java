package gui;

import java.awt.*;

import javax.swing.*;

public class ContactRenderer implements ListCellRenderer<Object> {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		return (ContactLabel)value;
	}
}
