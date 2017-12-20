import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int CategoryID;

    private String Name;

    @OneToMany
    private List<Product> products;

    public Category(String name) {
        Name = name;
        products = new ArrayList<>();
    }

    public Category() {
    }

    public String getName() {
        return Name;
    }

    public int getId() {
        return CategoryID;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }
}
