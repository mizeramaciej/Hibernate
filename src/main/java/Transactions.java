import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;

    private Integer Quantity;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Product> sales;

    public Transactions() {
    }

    public Transactions(Integer quantity) {
        Quantity = quantity;
        sales = new HashSet<>();
    }

    public void addToSales(Product product) {
        sales.add(product);
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public Set<Product> getSales() {
        return sales;
    }

    public int getID() {
        return ID;
    }
}
