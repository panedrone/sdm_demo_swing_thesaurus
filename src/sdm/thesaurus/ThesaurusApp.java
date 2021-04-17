package sdm.thesaurus;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JTextArea;

import com.sqldalmaker.DataStoreManager;

public class ThesaurusApp {

    private JFrame frame;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    DataStoreManager.init();
//					try { // https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html
//						for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    // System.out.println(info.getName());
//							if ("Windows".equals(info.getName())) {
//								UIManager.setLookAndFeel(info.getClassName());
//								break;
//							}
//						}
//					} catch (Exception e) {
//					}
                    ThesaurusApp window = new ThesaurusApp();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    //e.printStackTrace();
                    InternalHelpers.showError(null, e);
                }
            }
        });
    }

    public ThesaurusApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Thesaurus Demo");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // place a window in the center of
        // the screen
        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo look : looks) {
            System.out.println(look.getClassName());
        }

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        final MainPanel mainPanel = new MainPanel();
        tabbedPane.addTab("Thesaurus", null, mainPanel, null);

        JScrollPane scrollPane_1 = new JScrollPane();
        tabbedPane.addTab("ERD", null, scrollPane_1, null);

        JLabel label = new JLabel("");
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setIcon(new ImageIcon(Objects.requireNonNull(ThesaurusApp.class.getResource("/sdm/thesaurus/erd.png"))));
        scrollPane_1.setViewportView(label);

        JScrollPane scrollPane_2 = new JScrollPane();
        tabbedPane.addTab("DTO", null, scrollPane_2, null);

        final JTextPane textPane_DTO = new JTextPane();
        scrollPane_2.setViewportView(textPane_DTO);

        JScrollPane scrollPane_3 = new JScrollPane();
        tabbedPane.addTab("DAO", null, scrollPane_3, null);

        final JTextArea textArea_DAO = new JTextArea();
        scrollPane_3.setViewportView(textArea_DAO);

        JScrollPane scrollPane = new JScrollPane();
        tabbedPane.addTab("About", null, scrollPane, null);

        final JTextPane textPane_About = new JTextPane();
        scrollPane.setViewportView(textPane_About);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                try {
                    String dto = InternalHelpers.readFromJARFile(InternalHelpers.getConfigFileName("dto.xml"));
                    textPane_DTO.setText(dto);
                    textPane_DTO.setEditable(false);
                    String dao = InternalHelpers.readFromJARFile(InternalHelpers.getConfigFileName("ThesaurusDao.xml"));
                    textArea_DAO.setText(dao);
                    textArea_DAO.setEditable(false);
                    String about = InternalHelpers.readFromJARFile("README_th_en_US_v2.txt");
                    textPane_About.setText(about);
                    textPane_About.setEditable(false);
                    mainPanel.updateWordsCount();
                } catch (Exception e1) {
                    // e1.printStackTrace();
                    InternalHelpers.showError(frame, e1);
                }
            }
        });
    }

}
