package mcjty.rftoolscontrol.logic.editors;

import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.rftoolscontrol.api.parameters.ParameterType;
import mcjty.rftoolscontrol.api.parameters.ParameterValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class IntegerEditor extends AbstractParameterEditor {

    private TextField field;
    private ToggleButton hexMode;

    @Override
    public void build(Minecraft mc, Gui gui, Panel panel, ParameterEditorCallback callback) {
        Panel constantPanel = new Panel(mc, gui).setLayout(new HorizontalLayout());
        field = new TextField(mc, gui)
                .addTextEvent((parent, newText) -> callback.valueChanged(readValue()))
                .addTextEnterEvent((parent, newText) -> closeWindow());
        constantPanel.addChild(field);
        hexMode = new ToggleButton(mc, gui)
                .addButtonEvent(widget -> updateHex())
                .setCheckMarker(true)
                .setText("Hex");
        constantPanel.addChild(hexMode);

        createEditorPanel(mc, gui, panel, callback, constantPanel, ParameterType.PAR_INTEGER);
    }

    private void updateHex() {
        String value = field.getText();
        if (hexMode.isPressed()) {
            if (!value.startsWith("$")) {
                try {
                    int i = Integer.parseInt(value);
                    value = "$" + Integer.toHexString(i);
                    field.setText(value);
                } catch (NumberFormatException e) {
                }
            }
        } else {
            if (value.startsWith("$")) {
                Integer i = parseIntSafe(value);
                if (i != null) {
                    field.setText(String.valueOf(i));
                }
            }
        }
    }

    @Override
    public void initialFocusInternal(Window window) {
        window.setTextFocus(field);
    }

    @Override
    protected ParameterValue readConstantValue() {
        return ParameterValue.constant(parseIntSafe(field.getText()));
    }

    @Override
    protected void writeConstantValue(ParameterValue value) {
        if (value == null || value.getValue() == null) {
            field.setText("");
        } else {
            try {
                field.setText(Integer.toString((Integer) value.getValue()));
            } catch (Exception e) {
                field.setText("");
            }
        }
        updateHex();
    }
}
