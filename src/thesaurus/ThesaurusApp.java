package thesaurus;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.JTextArea;

import com.sqldalmaker.DataStoreManager;

public class ThesaurusApp {

	private JFrame frame;
	private MainPanel mainPanel;
	private JTextPane textPane_DTO;
	private JTextArea textArea_DAO;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataStoreManager.init();
					try { // https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html
						for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
							// System.out.println(info.getName());
							if ("Windows".equals(info.getName())) {
								UIManager.setLookAndFeel(info.getClassName());
								break;
							}
						}
					} catch (Exception e) {
						// If Nimbus is not available, you can set the GUI to another look and feel.
					}
					ThesaurusApp window = new ThesaurusApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					InternalHelpers.showError(null, e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ThesaurusApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
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

		try {
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.Windows");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		mainPanel = new MainPanel();
		tabbedPane.addTab("Thesaurus", null, mainPanel, null);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("DB-diagram", null, scrollPane_1, null);

		JLabel label = new JLabel("");
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setIcon(new ImageIcon(ThesaurusApp.class.getResource("/thesaurus/db-diagram.png")));
		scrollPane_1.setViewportView(label);

		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPane.addTab("DTO XML", null, scrollPane_2, null);

		textPane_DTO = new JTextPane();
		scrollPane_2.setViewportView(textPane_DTO);

		JScrollPane scrollPane_3 = new JScrollPane();
		tabbedPane.addTab("DAO XML", null, scrollPane_3, null);

		textArea_DAO = new JTextArea();
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
					e1.printStackTrace();
					InternalHelpers.showError(frame, e1);
				}
			}
		});
	}

}
