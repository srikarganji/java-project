import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Single-file Car Rental GUI with full customer details.
 * Compile: javac CarRentalGUI.java
 * Run:     java CarRentalGUI
 */
public class CarRentalGUI extends JFrame {

    // --- Model classes as inner classes ---

    public static class Car {
        private String carId;
        private String brand;
        private String model;
        private double basePricePerDay;
        private boolean isAvailable;

        public Car(String carId, String brand, String model, double basePricePerDay) {
            this.carId = carId;
            this.brand = brand;
            this.model = model;
            this.basePricePerDay = basePricePerDay;
            this.isAvailable = true;
        }

        public String getCarId() { return carId; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public double getBasePricePerDay() { return basePricePerDay; }

        public double calculatePrice(int rentalDays) { return basePricePerDay * rentalDays; }

        public boolean isAvailable() { return isAvailable; }
        public void rent() { isAvailable = false; }
        public void returnCar() { isAvailable = true; }

        @Override
        public String toString() {
            return carId + " - " + brand + " " + model + (isAvailable ? " (Available)" : " (Rented)");
        }
    }

    public static class Customer {
        private String customerId;
        private String name;
        private int age;
        private String phone;
        private String gender;
        private String address;
        private String aadhar;

        public Customer(String customerId, String name, int age, String phone, String gender, String address, String aadhar) {
            this.customerId = customerId;
            this.name = name;
            this.age = age;
            this.phone = phone;
            this.gender = gender;
            this.address = address;
            this.aadhar = aadhar;
        }

        public String getCustomerId() { return customerId; }
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getPhone() { return phone; }
        public String getGender() { return gender; }
        public String getAddress() { return address; }
        public String getAadhar() { return aadhar; }

        @Override
        public String toString() {
            return customerId + " - " + name + " (" + phone + ")";
        }
    }

    public static class Rental {
        private Car car;
        private Customer customer;
        private int days;

        public Rental(Car car, Customer customer, int days) {
            this.car = car;
            this.customer = customer;
            this.days = days;
        }

        public Car getCar() { return car; }
        public Customer getCustomer() { return customer; }
        public int getDays() { return days; }

        @Override
        public String toString() {
            // Format: "C001 | John Doe | 3 day(s)"
            return car.getCarId() + " | " + customer.getName() + " | " + days + " day(s)";
        }
    }

    public static class CarRentalSystem {
        private List<Car> cars;
        private List<Customer> customers;
        private List<Rental> rentals;

        public CarRentalSystem() {
            cars = new ArrayList<>();
            customers = new ArrayList<>();
            rentals = new ArrayList<>();
        }

        public void addCar(Car car) { cars.add(car); }
        public void addCustomer(Customer customer) { customers.add(customer); }

        public boolean rentCar(Car car, Customer customer, int days) {
            if (car == null || !car.isAvailable()) return false;
            car.rent();
            rentals.add(new Rental(car, customer, days));
            if (!customers.contains(customer)) customers.add(customer);
            return true;
        }

        public boolean returnCar(Car car) {
            if (car == null || car.isAvailable()) return false;
            Rental found = null;
            for (Rental r : rentals) {
                if (r.getCar() == car) { found = r; break; }
            }
            if (found != null) {
                rentals.remove(found);
                car.returnCar();
                return true;
            }
            return false;
        }

        public List<Car> getCars() { return cars; }
        public List<Rental> getRentals() { return rentals; }
        public List<Customer> getCustomers() { return customers; }

        public Car findCarById(String id) {
            if (id == null) return null;
            for (Car c : cars) if (id.equals(c.getCarId())) return c;
            return null;
        }
    }

    // --- GUI fields ---
    private CarRentalSystem system;

    private DefaultListModel<String> availListModel;
    private JList<String> availList;
    private DefaultListModel<String> rentedListModel;
    private JList<String> rentedList;

    // Rent input fields (members so onRent can access them)
    private JTextField nameField;
    private JSpinner ageSpinner;
    private JTextField phoneField;
    private JComboBox<String> genderBox;
    private JTextField aadharField;
    private JTextField addressField;
    private JComboBox<String> carComboBox;
    private JSpinner daysSpinner;

    private JTextArea messages;

    public CarRentalGUI(CarRentalSystem system) {
        this.system = system;
        setTitle("Car Rental System - GUI (with Customer Details)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
        initComponents();
        refreshAll();
    }

    private void initComponents() {
        JPanel left = new JPanel(new BorderLayout());
        JPanel right = new JPanel(new BorderLayout());
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(420);
        add(split);

        // --- Available cars list (left center) ---
        availListModel = new DefaultListModel<>();
        availList = new JList<>(availListModel);
        availList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        left.add(new JScrollPane(availList), BorderLayout.CENTER);

        // --- Rent panel (left bottom) ---
        JPanel rentPanel = new JPanel();
        rentPanel.setLayout(new GridBagLayout());
        rentPanel.setBorder(BorderFactory.createTitledBorder("Rent a Car (Enter Customer Details)"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        // Row 0 - Name
        c.gridx = 0; c.gridy = 0;
        rentPanel.add(new JLabel("Name:"), c);
        c.gridx = 1;
        nameField = new JTextField(14);
        rentPanel.add(nameField, c);

        // Row 1 - Age
        c.gridx = 0; c.gridy = 1;
        rentPanel.add(new JLabel("Age:"), c);
        c.gridx = 1;
        ageSpinner = new JSpinner(new SpinnerNumberModel(18, 18, 100, 1));
        rentPanel.add(ageSpinner, c);

        // Row 2 - Phone
        c.gridx = 0; c.gridy = 2;
        rentPanel.add(new JLabel("Phone:"), c);
        c.gridx = 1;
        phoneField = new JTextField(14);
        rentPanel.add(phoneField, c);

        // Row 3 - Gender
        c.gridx = 0; c.gridy = 3;
        rentPanel.add(new JLabel("Gender:"), c);
        c.gridx = 1;
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        rentPanel.add(genderBox, c);

        // Row 4 - Aadhar
        c.gridx = 0; c.gridy = 4;
        rentPanel.add(new JLabel("Aadhar ID:"), c);
        c.gridx = 1;
        aadharField = new JTextField(14);
        rentPanel.add(aadharField, c);

        // Row 5 - Address
        c.gridx = 0; c.gridy = 5;
        rentPanel.add(new JLabel("Address:"), c);
        c.gridx = 1;
        addressField = new JTextField(14);
        rentPanel.add(addressField, c);

        // Row 6 - Select Car
        c.gridx = 0; c.gridy = 6;
        rentPanel.add(new JLabel("Select Car:"), c);
        c.gridx = 1;
        carComboBox = new JComboBox<>();
        carComboBox.setPrototypeDisplayValue("CXXXX"); // width
        rentPanel.add(carComboBox, c);

        // Row 7 - Days
        c.gridx = 0; c.gridy = 7;
        rentPanel.add(new JLabel("Days:"), c);
        c.gridx = 1;
        daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        rentPanel.add(daysSpinner, c);

        // Row 8 - Rent button
        c.gridx = 0; c.gridy = 8; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        JButton rentBtn = new JButton("Rent Car");
        rentPanel.add(rentBtn, c);

        left.add(rentPanel, BorderLayout.SOUTH);

        // --- Right side: Rented list and controls ---
        rentedListModel = new DefaultListModel<>();
        rentedList = new JList<>(rentedListModel);
        rentedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        right.add(new JScrollPane(rentedList), BorderLayout.CENTER);

        JPanel returnPanel = new JPanel();
        returnPanel.setBorder(BorderFactory.createTitledBorder("Return / Info"));
        JButton returnBtn = new JButton("Return Selected");
        JButton viewCustBtn = new JButton("View Customer Details");
        returnPanel.add(returnBtn);
        returnPanel.add(viewCustBtn);
        right.add(returnPanel, BorderLayout.NORTH);

        messages = new JTextArea(6, 30);
        messages.setEditable(false);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        right.add(new JScrollPane(messages), BorderLayout.SOUTH);

        // --- Actions ---
        rentBtn.addActionListener(e -> onRent());
        returnBtn.addActionListener(e -> onReturn());
        viewCustBtn.addActionListener(e -> onViewCustomer());

        // Double-click an available car to auto-select in combo box
        availList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selected = availList.getSelectedValue();
                    if (selected != null) {
                        String id = selected.split(" ")[0]; // first token is ID
                        carComboBox.setSelectedItem(id);
                    }
                }
            }
        });
    }

    private void onRent() {
        String name = nameField.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        String phone = phoneField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String aadhar = aadharField.getText().trim();
        String address = addressField.getText().trim();
        String carId = (String) carComboBox.getSelectedItem();
        int days = (Integer) daysSpinner.getValue();

        // Basic validation
        if (name.isEmpty()) { showMessage("Enter customer name."); return; }
        if (phone.isEmpty()) { showMessage("Enter phone number."); return; }
        if (aadhar.isEmpty()) { showMessage("Enter Aadhar ID."); return; }
        if (carId == null) { showMessage("Select a car to rent."); return; }

        Car car = system.findCarById(carId);
        if (car == null) { showMessage("Selected car not found."); refreshAll(); return; }
        if (!car.isAvailable()) { showMessage("Selected car is not available."); refreshAll(); return; }

        String cid = "CUS" + (system.getCustomers().size() + 1);
        Customer cust = new Customer(cid, name, age, phone, gender, address, aadhar);

        boolean ok = system.rentCar(car, cust, days);
        if (ok) {
            double price = car.calculatePrice(days);
            showMessage(String.format("Rented %s to %s for %d day(s). Total: $%.2f",
                    car.getCarId(), cust.getName(), days, price));
            clearRentFields();
            refreshAll();
        } else {
            showMessage("Failed to rent car.");
        }
    }

    private void clearRentFields() {
        nameField.setText("");
        ageSpinner.setValue(18);
        phoneField.setText("");
        aadharField.setText("");
        addressField.setText("");
        genderBox.setSelectedIndex(0);
        daysSpinner.setValue(1);
    }

    private void onReturn() {
        String sel = rentedList.getSelectedValue();
        if (sel == null) { showMessage("Select a rented entry to return."); return; }
        // format: "C001 | John Doe | 3 day(s)"
        String carId = sel.split("\\|")[0].trim();
        Car car = system.findCarById(carId);
        if (car == null) { showMessage("Car not found."); return; }
        boolean ok = system.returnCar(car);
        if (ok) {
            showMessage("Car " + carId + " returned successfully.");
            refreshAll();
        } else {
            showMessage("Return failed.");
        }
    }

    private void onViewCustomer() {
        String sel = rentedList.getSelectedValue();
        if (sel == null) { showMessage("Select a rented entry to view customer details."); return; }
        String carId = sel.split("\\|")[0].trim();
        // find rental
        Rental found = null;
        for (Rental r : system.getRentals()) {
            if (r.getCar().getCarId().equals(carId)) { found = r; break; }
        }
        if (found == null) { showMessage("Rental info not found."); return; }
        Customer cust = found.getCustomer();
        StringBuilder sb = new StringBuilder();
        sb.append("Customer ID: ").append(cust.getCustomerId()).append("\n");
        sb.append("Name: ").append(cust.getName()).append("\n");
        sb.append("Age: ").append(cust.getAge()).append("\n");
        sb.append("Phone: ").append(cust.getPhone()).append("\n");
        sb.append("Gender: ").append(cust.getGender()).append("\n");
        sb.append("Aadhar: ").append(cust.getAadhar()).append("\n");
        sb.append("Address: ").append(cust.getAddress()).append("\n");
        sb.append("Car: ").append(found.getCar().getCarId()).append(" - ")
                .append(found.getCar().getBrand()).append(" ").append(found.getCar().getModel()).append("\n");
        sb.append("Days: ").append(found.getDays()).append("\n");
        sb.append("Total: $").append(String.format("%.2f", found.getCar().calculatePrice(found.getDays()))).append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Customer & Rental Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshAll() {
        // available cars & combo
        availListModel.clear();
        carComboBox.removeAllItems();
        for (Car c : system.getCars()) {
            if (c.isAvailable()) {
                availListModel.addElement(c.getCarId() + " - " + c.getBrand() + " " + c.getModel());
                carComboBox.addItem(c.getCarId());
            }
        }

        // rented list
        rentedListModel.clear();
        for (Rental r : system.getRentals()) {
            rentedListModel.addElement(r.toString());
        }
    }

    private void showMessage(String text) {
        messages.append(text + "\n");
        messages.setCaretPosition(messages.getDocument().getLength());
    }

    // --- main: sample data and start GUI ---
    public static void main(String[] args) {
        CarRentalSystem sys = new CarRentalSystem();
        sys.addCar(new Car("C001", "Toyota", "Camry", 60.0));
        sys.addCar(new Car("C002", "Honda", "Accord", 70.0));
        sys.addCar(new Car("C003", "Mahindra", "Thar", 150.0));
        sys.addCar(new Car("C004", "Hyundai", "i20", 40.0));
        sys.addCar(new Car("C005", "Ford", "Ecosport", 80.0));

        SwingUtilities.invokeLater(() -> {
            CarRentalGUI gui = new CarRentalGUI(sys);
            gui.setVisible(true);
        });
    }
}
