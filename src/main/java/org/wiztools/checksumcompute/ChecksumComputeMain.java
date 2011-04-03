package org.wiztools.checksumcompute;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author subhash
 */
public class ChecksumComputeMain {
    public static void main(String[] arg) {
        Font f = new Font(Font.DIALOG, Font.PLAIN, 12);
        ArrayList excludes = new ArrayList();
        Enumeration itr = UIManager.getDefaults().keys();
        while(itr.hasMoreElements()){
            Object key = itr.nextElement();
            Object value = UIManager.get (key);
            if ((value instanceof javax.swing.plaf.FontUIResource)
                    && (!excludes.contains(key))){
                UIManager.put (key, f);
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChecksumComputeFrame();
            }
        });
    }
}
