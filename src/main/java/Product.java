import javax.persistence.*;
import java.util.Set;

@Entity
public class Product {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;

    private String ProductName;
    private Integer UnitsOnStock;
    private Integer CategoryID;

    @ManyToMany(mappedBy = "sales", cascade = CascadeType.PERSIST)
    private Set<Transactions> canBeSoldInTransaction;


    public Product() {
    }


    public Product(String productName, Integer unitsOnStock, int CategoryID) {
        ProductName = productName;
        UnitsOnStock = unitsOnStock;
        this.CategoryID = CategoryID;

    }

    public Integer getUnitsOnStock() {
        return UnitsOnStock;
    }

    public void addTransaction(Transactions transactions) {
        canBeSoldInTransaction.add(transactions);
    }

    public int getId() {
        return Id;
    }

    public Set<Transactions> getCanBeSoldInTransaction() {
        return canBeSoldInTransaction;
    }

    public Integer getCategoryID() {
        return this.CategoryID;
    }

    public String getProductName() {
        return ProductName;
    }

}
