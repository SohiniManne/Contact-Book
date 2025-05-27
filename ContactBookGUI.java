import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ContactBookGUI extends JFrame {
    private ContactManager contactManager;
    private JTable contactTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField firstNameField, lastNameField, phoneField, emailField, addressField;
    private JButton addButton, editButton, deleteButton, clearButton;
    private int selectedRow = -1;
    
    public ContactBookGUI() {
        contactManager = new ContactManager();
        initializeGUI();
        loadContactsToTable();
    }
    
    private void initializeGUI() {
        setTitle("Contact Book Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panels
        JPanel topPanel = createSearchPanel();
        JPanel centerPanel = createTablePanel();
        JPanel bottomPanel = createFormPanel();
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Contacts"));
        
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        
        searchButton.addActionListener(e -> searchContacts());
        showAllButton.addActionListener(e -> loadContactsToTable());
        
        panel.add(new JLabel("Search:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(showAllButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Contacts"));
        
        String[] columnNames = {"First Name", "Last Name", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        contactTable = new JTable(tableModel);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRow = contactTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadContactToForm();
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(contactTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Contact Details"));
        
        // Form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();
        
        fieldsPanel.add(new JLabel("First Name:"));
        fieldsPanel.add(firstNameField);
        fieldsPanel.add(new JLabel("Last Name:"));
        fieldsPanel.add(lastNameField);
        fieldsPanel.add(new JLabel("Phone:"));
        fieldsPanel.add(phoneField);
        fieldsPanel.add(new JLabel("Email:"));
        fieldsPanel.add(emailField);
        fieldsPanel.add(new JLabel("Address:"));
        fieldsPanel.add(addressField);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add Contact");
        editButton = new JButton("Update Contact");
        deleteButton = new JButton("Delete Contact");
        clearButton = new JButton("Clear Form");
        
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        addButton.addActionListener(e -> addContact());
        editButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadContactsToTable() {
        tableModel.setRowCount(0);
        List<Contact> contacts = contactManager.getAllContacts();
        
        for (Contact contact : contacts) {
            Object[] row = {
                contact.getFirstName(),
                contact.getLastName(),
                contact.getPhoneNumber(),
                contact.getEmail(),
                contact.getAddress()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchContacts() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadContactsToTable();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Contact> results = contactManager.searchContacts(query);
        
        for (Contact contact : results) {
            Object[] row = {
                contact.getFirstName(),
                contact.getLastName(),
                contact.getPhoneNumber(),
                contact.getEmail(),
                contact.getAddress()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addContact() {
        if (validateForm()) {
            Contact contact = new Contact(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressField.getText().trim()
            );
            
            contactManager.addContact(contact);
            loadContactsToTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Contact added successfully!");
        }
    }
    
    private void updateContact() {
        if (selectedRow >= 0 && validateForm()) {
            Contact contact = new Contact(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressField.getText().trim()
            );
            
            contactManager.updateContact(selectedRow, contact);
            loadContactsToTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Contact updated successfully!");
        }
    }
    
    private void deleteContact() {
        if (selectedRow >= 0) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this contact?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                contactManager.deleteContact(selectedRow);
                loadContactsToTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Contact deleted successfully!");
            }
        }
    }
    
    private void loadContactToForm() {
        if (selectedRow >= 0) {
            List<Contact> contacts = contactManager.getAllContacts();
            Contact contact = contacts.get(selectedRow);
            
            firstNameField.setText(contact.getFirstName());
            lastNameField.setText(contact.getLastName());
            phoneField.setText(contact.getPhoneNumber());
            emailField.setText(contact.getEmail());
            addressField.setText(contact.getAddress());
        }
    }
    
    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        contactTable.clearSelection();
        selectedRow = -1;
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty() || 
            lastNameField.getText().trim().isEmpty() || 
            phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in at least First Name, Last Name, and Phone Number.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ContactBookGUI().setVisible(true);
        });
    }
}