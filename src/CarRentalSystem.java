import java.util.ArrayList;
import java.util.List;

public class CarRentalSystem {
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
            if (r.getCar() == car) {
                found = r;
                break;
            }
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

    // helper: find car by id
    public Car findCarById(String id) {
        if (id == null) return null;
        for (Car c : cars) if (id.equals(c.getCarId())) return c;
        return null;
    }
}
