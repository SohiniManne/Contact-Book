import java.io.*;
import java.util.*;

public class ContactManager {
    private List<Contact> contacts;
    private final String DATA_FILE = "contacts.dat";
    
    public ContactManager() {
        contacts = new ArrayList<>();
        loadContacts();
    }
    
    public void addContact(Contact contact) {
        contacts.add(contact);
        saveContacts();
    }
    
    public void updateContact(int index, Contact contact) {
        if (index >= 0 && index < contacts.size()) {
            contacts.set(index, contact);
            saveContacts();
        }
    }
    
    public void deleteContact(int index) {
        if (index >= 0 && index < contacts.size()) {
            contacts.remove(index);
            saveContacts();
        }
    }
    
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }
    
    public List<Contact> searchContacts(String query) {
        List<Contact> results = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();
        
        for (Contact contact : contacts) {
            if (contact.getFirstName().toLowerCase().contains(lowercaseQuery) ||
                contact.getLastName().toLowerCase().contains(lowercaseQuery) ||
                contact.getPhoneNumber().contains(query) ||
                contact.getEmail().toLowerCase().contains(lowercaseQuery)) {
                results.add(contact);
            }
        }
        return results;
    }
    
    private void saveContacts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            System.err.println("Error saving contacts: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadContacts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            contacts = (List<Contact>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, start with empty list
            contacts = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading contacts: " + e.getMessage());
            contacts = new ArrayList<>();
        }
    }
}