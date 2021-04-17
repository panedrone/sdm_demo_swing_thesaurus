package sdm.thesaurus;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.sqldalmaker.thesaurus.dto.RelatedWord;
import com.sqldalmaker.thesaurus.dto.Word;

@SuppressWarnings("rawtypes")
public class MainPanel extends JPanel {

    private final JTextField textField_SearchKey;
    private final JLabel lblWordsCount;
    private final JList list_Words;

    private final Timer timer = new Timer();
    private TimerTask task = null;

    private final MyTableModel tableModel;

    private List<RelatedWord> synonyms;

    private class MyTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        public void refresh() {
            // http://stackoverflow.com/questions/3179136/jtable-how-to-refresh-table-model-after-insert-delete-or-update-the-data
            super.fireTableDataChanged();
        }

        @Override
        public String getColumnName(int col) {
            if (col == 0) {
                return "Related";
            }
            return "Note";
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            if (synonyms == null) {
                return 0;
            }
            return synonyms.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (synonyms == null) {
                return null;
            }
            if (columnIndex == 0) {
                return synonyms.get(rowIndex).getRgwWord();
            }
            Object ps = synonyms.get(rowIndex).getRgPartOfSpeech();
            Object note = synonyms.get(rowIndex).getRgwNote();
            return String.format("%s %s", ps, note);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            // list.get(rowIndex)[columnIndex] = (String) aValue;
        }
    }

    private void show_related(int index) {
        ListModel dlm = list_Words.getModel();
        if (index > dlm.getSize() - 1) {
            index = dlm.getSize() - 1;
        }
        Word word = (Word) dlm.getElementAt(index);
        list_Words.ensureIndexIsVisible(index);
        reloadSynonymsTable(word);
    }

    public MainPanel() {
        super.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        super.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));

        lblWordsCount = new JLabel("Words, Total: 0");
        panel.add(lblWordsCount);

        JSplitPane splitPane = new JSplitPane();
        super.add(splitPane, BorderLayout.CENTER);

        JPanel panel_1 = new JPanel();
        panel_1.setPreferredSize(new Dimension(220, 220));
        splitPane.setLeftComponent(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_3 = new JPanel();
        panel_1.add(panel_3, BorderLayout.NORTH);
        panel_3.setLayout(new BorderLayout(0, 0));

        textField_SearchKey = new JTextField();
        panel_3.add(textField_SearchKey, BorderLayout.CENTER);
        textField_SearchKey.setColumns(20);

        JButton btnNewButton = new JButton("");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int index = list_Words.getSelectedIndex();
                if (index < 0) {
                    return;
                }
                show_related(index);
            }
        });
        btnNewButton.setIcon(new ImageIcon(Objects.requireNonNull(MainPanel.class.getResource("/img/131.png"))));
        panel_3.add(btnNewButton, BorderLayout.EAST);

        textField_SearchKey.getDocument().addDocumentListener(new DocumentListener() {
            private void updateFilter() {
                setFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        panel_1.add(scrollPane);

        list_Words = new JList();
        list_Words.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // http://www.rgagnon.com/javadetails/java-0219.html
                if (e.getClickCount() == 2) {
                    int index = list_Words.locationToIndex(e.getPoint());
                    if (index < 0) {
                        return;
                    }
                    show_related(index);
                }
            }
        });
        list_Words.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(list_Words);

        JPanel panel_2 = new JPanel();
        splitPane.setRightComponent(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));

        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.setColumns(10);
        panel_2.add(textField, BorderLayout.NORTH);

        JScrollPane scrollPane_1 = new JScrollPane();
        panel_2.add(scrollPane_1, BorderLayout.CENTER);

        JTable table = new JTable();
        scrollPane_1.setViewportView(table);
        table.setTableHeader(null);

        tableModel = new MyTableModel();
        table.setModel(tableModel);
        {
            TableColumn col = new TableColumn();
            col.setPreferredWidth(100);
        }
        {
            TableColumn col = new TableColumn();
            col.setPreferredWidth(100);
        }

    }

    private void reloadSynonymsTable(Word word) {
        try {
            synonyms = DataController.getRelatedWords(word);
        } catch (Exception tr) {
            InternalHelpers.showError(this, tr);
        } finally {
            tableModel.refresh(); // table.updateUI();
        }
    }

    protected void setFilter() {

        final Runnable doUpdate = new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                try {
                    final List<Word> list = DataController.getWordsByKey(textField_SearchKey.getText());
                    DefaultListCellRenderer cellRenderer = new DefaultListCellRenderer() {

                        private static final long serialVersionUID = 1L;

                        @Override
                        public Component getListCellRendererComponent(JList list, Object value, int index,
                                boolean isSelected, boolean cellHasFocus) {
                            Word w = (Word) value;
                            return super.getListCellRendererComponent(list, w.getWWord(), index, isSelected,
                                    cellHasFocus);
                        }
                    };
                    list_Words.setCellRenderer(cellRenderer);
                    AbstractListModel listModel = new AbstractListModel() {
                        private static final long serialVersionUID = 1L;
                        @Override
                        public Object getElementAt(int index) {
                            return list.get(index);
                        }
                        @Override
                        public int getSize() {
                            return list.size();
                        }
                    };
                    list_Words.setModel(listModel);
                } catch (Exception e) {
                    //e.printStackTrace();
                    InternalHelpers.showError(MainPanel.this, e);
                }
            }
        };

        if (task != null) {
            task.cancel();
        }

        task = new TimerTask() {
            @Override
            public void run() {
                // http://stackoverflow.com/questions/7411497/how-to-bind-a-jlist-to-a-bean-class-property
                SwingUtilities.invokeLater(doUpdate);
            }
        };

        timer.schedule(task, 500);
    }

    public void updateWordsCount() throws Exception {
        lblWordsCount.setText("Words count, total: " + DataController.getTotalWordsCount());
    }
}
