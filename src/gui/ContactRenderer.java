package gui;

import java.awt.*;

import javax.swing.*;

public class ContactRenderer extends JLabel implements ListCellRenderer<Object> {
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		setEnabled(list.isEnabled());
		return (ContactLabel)value;
	}
}
