/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

/**
 *
 * @author marios
 */
public class NegativeNumericByteView extends NumericByteView {

    public NegativeNumericByteView(PropertyHandler handler, String title) {
        super(handler, title);
    }

    @Override
    protected void updateControls() {
        datalabel.setText(new Integer(data).toString());
    }
}
