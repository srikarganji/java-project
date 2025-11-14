public class Customer {
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
