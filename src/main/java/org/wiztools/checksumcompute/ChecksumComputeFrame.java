package org.wiztools.checksumcompute;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;

/**
 *
 * @author subhash
 */
public class ChecksumComputeFrame extends JFrame {
    private static final Logger logger = Logger.getLogger(ChecksumComputeFrame.class.getName());

    private static final String EXT_MD5 = "md5";
    private static final String EXT_SHA1 = "sha1";
    private static final String EXT_SHA256 = "sha256";

    private JFileChooser jfcOpen = new JFileChooser();
    private JFileChooser jfcSave = new JFileChooser();

    private final Icon goIcon = new ImageIcon(this.getClass().getClassLoader().getResource("go.png"));
    private final Icon stopIcon = new ImageIcon(this.getClass().getClassLoader().getResource("stop.png"));
    private final Icon copyIcon = new ImageIcon(this.getClass().getClassLoader().getResource("copy.png"));
    private final Icon saveIcon = new ImageIcon(this.getClass().getClassLoader().getResource("save.png"));

    private JTextField jtf_file = new JTextField(35);
    private JButton jb_browse = new JButton("Browse");
    private JButton jb_go = new JButton(goIcon);

    private JTextField jtf_md5 = new JTextField();
    private JTextField jtf_sha1 = new JTextField();
    private JTextField jtf_sha256 = new JTextField();

    private JButton jb_md5_copy = new JButton(copyIcon);
    private JButton jb_sha1_copy = new JButton(copyIcon);
    private JButton jb_sha256_copy = new JButton(copyIcon);

    private JButton jb_md5_save = new JButton(saveIcon);
    private JButton jb_sha1_save = new JButton(saveIcon);
    private JButton jb_sha256_save = new JButton(saveIcon);

    private final JProgressBar jpb = new JProgressBar(0, 100);

    private final ProgressCallback progressCallback = new ProgressCallback() {
        private long totalLength;
        private long progressLength;

        @Override
        public void start(long length) {
            progressLength = 0;
            totalLength = length;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jb_go.setIcon(stopIcon);
                }
            });
        }

        @Override
        public void progress(int partLength) {
            progressLength += partLength;
            final int percentProgress = (int) ((progressLength*100L)/totalLength);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jpb.setValue(percentProgress);
                }
            });
        }

        @Override
        public void end(final Result result) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(result != null) {
                        jtf_md5.setText(result.getMD5Hex());
                        jtf_sha1.setText(result.getSHA1Hex());
                        jtf_sha256.setText(result.getSHA256Hex());
                    }
                }
            });
        }

        @Override
        public void stop() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jpb.setValue(0);
                    jb_go.setIcon(goIcon);
                }
            });
        }
        
    };

    private Compute compute;

    public ChecksumComputeFrame() {
        super("WizTools.org Checksum Compute");

        init();
        initMenuBar();

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        { // North
            JPanel jp = new JPanel(new BorderLayout());

            jp.add(jtf_file, BorderLayout.CENTER);

            JPanel jp_east = new JPanel(new BorderLayout());
            jp_east.add(jb_browse, BorderLayout.CENTER);
            jp_east.add(jb_go, BorderLayout.EAST);

            jp.add(jp_east, BorderLayout.EAST);

            c.add(jp, BorderLayout.NORTH);
        }

        { // Center
            JPanel jp = new JPanel(new BorderLayout());
            jp.setBorder(BorderFactory.createTitledBorder("Result"));

            JPanel jp_west = new JPanel(new GridLayout(3, 1));
            jp_west.add(new JLabel("MD5: "));
            jp_west.add(new JLabel("SHA-1: "));
            jp_west.add(new JLabel("SHA-256: "));
            jp.add(jp_west, BorderLayout.WEST);

            JPanel jp_center = new JPanel(new GridLayout(3, 1));
            jp_center.add(jtf_md5);
            jp_center.add(jtf_sha1);
            jp_center.add(jtf_sha256);

            jp.add(jp_center, BorderLayout.CENTER);

            JPanel jp_east = new JPanel(new GridLayout(3, 2));
            jp_east.add(jb_md5_copy);
            jp_east.add(jb_md5_save);
            
            jp_east.add(jb_sha1_copy);
            jp_east.add(jb_sha1_save);
            
            jp_east.add(jb_sha256_copy);
            jp_east.add(jb_sha256_save);

            jp.add(jp_east, BorderLayout.EAST);

            c.add(jp, BorderLayout.CENTER);
        }

        { // South
            JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            jp.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            jp.add(jpb);
            c.add(jp, BorderLayout.SOUTH);
        }

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
    }

    private void init() {
        jtf_md5.setEditable(false);
        jtf_sha1.setEditable(false);
        jtf_sha256.setEditable(false);

        // Tooltips
        jb_md5_copy.setToolTipText("Copy");
        jb_md5_save.setToolTipText("Save");
        jb_sha1_copy.setToolTipText("Copy");
        jb_sha1_save.setToolTipText("Save");
        jb_sha256_copy.setToolTipText("Copy");
        jb_sha256_save.setToolTipText("Save");

        // Copy button event handlers:
        jb_md5_copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardCopy(jtf_md5.getText());
            }
        });
        jb_sha1_copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardCopy(jtf_sha1.getText());
            }
        });
        jb_sha256_copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clipboardCopy(jtf_sha256.getText());
            }
        });

        // Save button event handlers:
        jb_md5_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDigest(jtf_md5.getText(), EXT_MD5);
            }
        });
        jb_sha1_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDigest(jtf_sha1.getText(), EXT_SHA1);
            }
        });
        jb_sha256_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDigest(jtf_sha256.getText(), EXT_SHA256);
            }
        });

        jb_browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDialog();
            }
        });

        jb_go.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        go();
                    }
                }).start();
            }
        });
    }

    private void initMenuBar() {
        JMenuBar jmb = new JMenuBar();

        { // File menu
            JMenu jmFile = new JMenu("File");
            jmFile.setMnemonic('f');

            JMenuItem jmiOpen = new JMenuItem("Open");
            jmiOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
            jmiOpen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openDialog();
                }
            });
            jmFile.add(jmiOpen);

            JMenuItem jmiClear = new JMenuItem("Clear Result");
            jmiClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
            jmiClear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearResult();
                }
            });
            jmFile.add(jmiClear);

            JMenuItem jmiExit = new JMenuItem("Exit");
            jmiExit.setMnemonic('x');
            jmiExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
            jmiExit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            jmFile.add(jmiExit);

            jmb.add(jmFile);
        }

        { // Help menu
            JMenu jmHelp = new JMenu("Help");
            jmHelp.setMnemonic('h');

            JMenuItem jmiAbout = new JMenuItem("About");
            jmiAbout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showAbout();
                }
            });
            jmHelp.add(jmiAbout);

            jmb.add(jmHelp);
        }

        this.setJMenuBar(jmb);
    }

    private void openDialog() {
        final int res = jfcOpen.showOpenDialog(this);
        switch(res) {
            case JFileChooser.APPROVE_OPTION:
                File f = jfcOpen.getSelectedFile();
                jtf_file.setText(f.getAbsolutePath());
                break;
        }
    }

    private void go() {
        if(compute != null && !compute.isFinished()) {
            stop();
            return;
        }
        File f = new File(jtf_file.getText());
        if(!f.exists()) {
            JOptionPane.showMessageDialog(this,
                    "File does not exist: " + f.getAbsolutePath(),
                    "File does not exist!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(f.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                    "Is not a file: " + f.getAbsolutePath(),
                    "Is not a file!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!f.canRead()) {
            JOptionPane.showMessageDialog(this,
                    "File does not have read permission: " + f.getAbsolutePath(),
                    "File does not have read permission!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try{
            compute = new Compute();
            compute.compute(f, progressCallback);
        }
        catch(IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void stop() {
        compute.interrupt();
    }

    private void clipboardCopy(final String str) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(str), null);
    }

    private void saveDigest(final String digest, final String extension) {
        if(digest.trim().equals("") || jtf_file.getText().trim().equals("")) {
            return;
        }
        File selectedFile = new File(jtf_file.getText());
        jfcSave.setSelectedFile(new File(selectedFile.getParentFile(),
                selectedFile.getName() + "." + extension));
        final int res = jfcSave.showSaveDialog(this);
        if(res == JFileChooser.APPROVE_OPTION) {
            File f = jfcSave.getSelectedFile();
            if(f.exists()) {
                final int owRes = JOptionPane.showConfirmDialog(this,
                        "File exists. Overwrite?",
                        "File exists",
                        JOptionPane.YES_NO_OPTION);
                if(owRes == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            try{
                FileUtil.writeString(f, digest, Charsets.US_ASCII);
            }
            catch(IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error writing", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearResult() {
        jtf_md5.setText("");
        jtf_sha1.setText("");
        jtf_sha256.setText("");
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "<html>&copy; WizTools.org<br>Apache 2.0 Licensed</html>",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
