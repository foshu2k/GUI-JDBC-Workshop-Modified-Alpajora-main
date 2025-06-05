package pages;

import dal.students.StudentDAO;
import models.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StudentPage extends JFrame {
    private final StudentDAO studentDao = new StudentDAO();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField studentNumberField;
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField programField;
    private final JSpinner levelSpinner;
    private final JTextField searchField;

    public StudentPage() {
        setTitle("Student Data");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(230, 240, 255));

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBackground(new Color(230, 240, 255));

        // Left Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(230, 240, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Form"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        studentNumberField = createRoundedTextField();
        firstNameField = createRoundedTextField();
        lastNameField = createRoundedTextField();
        programField = createRoundedTextField();
        levelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Student Number:", studentNumberField);
        addFormRow(formPanel, gbc, row++, "First Name:", firstNameField);
        addFormRow(formPanel, gbc, row++, "Last Name:", lastNameField);
        addFormRow(formPanel, gbc, row++, "Program:", programField);
        addFormRow(formPanel, gbc, row++, "Level:", levelSpinner);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBackground(new Color(230, 240, 255));

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Search Field
        searchField = createRoundedTextField();
        searchField.setToolTipText("Search by student number");
        searchField.addActionListener(_ -> filterStudentsByNumber());

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Search (Student Number):"), gbc);

        gbc.gridx = 1;
        formPanel.add(searchField, gbc);

        // Right Panel - Table
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Student Number", "First Name", "Last Name", "Program", "Level"}, 0
        );
        table = new JTable(tableModel);
        table.setGridColor(Color.GRAY);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Records"));

        contentPanel.add(formPanel);
        contentPanel.add(scrollPane);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        loadStudents();

        addButton.addActionListener(_ -> addStudent());
        updateButton.addActionListener(_ -> updateStudent());
        deleteButton.addActionListener(_ -> deleteStudent());
        clearButton.addActionListener(_ -> clearFields());
        refreshButton.addActionListener(_ -> loadStudents());
        logoutButton.addActionListener(_ -> {
            dispose();
            new LoginPage();
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    studentNumberField.setText(tableModel.getValueAt(row, 1).toString());
                    firstNameField.setText(tableModel.getValueAt(row, 2).toString());
                    lastNameField.setText(tableModel.getValueAt(row, 3).toString());
                    programField.setText(tableModel.getValueAt(row, 4).toString());
                    levelSpinner.setValue(tableModel.getValueAt(row, 5));
                }
            }
        });

        setVisible(true);
    }

    private JTextField createRoundedTextField() {
        JTextField field = new JTextField(15);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> students = studentDao.getAllStudents();
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getId(),
                    student.getStudentNumber(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getProgram(),
                    student.getLevel()
            });
        }
    }

    private void filterStudentsByNumber() {
        String keyword = searchField.getText().toLowerCase();
        List<Student> filtered = studentDao.getAllStudents().stream()
                .filter(s -> s.getStudentNumber().toLowerCase().contains(keyword))
                .toList();

        tableModel.setRowCount(0);
        for (Student student : filtered) {
            tableModel.addRow(new Object[]{
                    student.getId(),
                    student.getStudentNumber(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getProgram(),
                    student.getLevel()
            });
        }
    }

    private void addStudent() {
        if (validateFields()) {
            studentDao.addStudent(getStudentFromFields(0));
            loadStudents();
            clearFields();
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row != -1 && validateFields()) {
            int id = (int) tableModel.getValueAt(row, 0);
            studentDao.updateStudent(getStudentFromFields(id));
            loadStudents();
            clearFields();
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            studentDao.deleteStudent(id);
            loadStudents();
            clearFields();
        }
    }

    private Student getStudentFromFields(int id) {
        return new Student(
                id,
                studentNumberField.getText(),
                firstNameField.getText(),
                lastNameField.getText(),
                programField.getText(),
                (int) levelSpinner.getValue()
        );
    }

    private boolean validateFields() {
        if (studentNumberField.getText().isEmpty() ||
                firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                programField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        studentNumberField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        programField.setText("");
        levelSpinner.setValue(1);
    }
}
