import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.get;


public class Main {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        String layout = "public/templates/layout.vtl";

        get("/", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/hello.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        getInitialize(layout);
    }

    private static void getInitialize(final String layout) {
        initCategory(layout);
        initProduct(layout);
        initSupplier(layout);
        initSupplier_Product(layout);
        initSuppierLists(layout);
        initTransactions(layout);
        initTransactionList(layout);
        initProductList(layout);
    }

    private static void initCategory(final String layout) {
        get("/addCategory", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/addCategory.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/addedCategory", (request, response) -> {
            HashMap model = new HashMap();
            String categoryName = request.queryParams("categoryName");

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            Category category = new Category(categoryName);
            session.save(category);
            tx.commit();
            session.close();

            model.put("categoryName", categoryName);
            model.put("template", "public/templates/addedCategory.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/allCategories", (request, response) -> {
            HashMap model = new HashMap();

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Category> categories = session.createNativeQuery(
                    "SELECT * FROM CATEGORY").addEntity(Category.class).list();
            for (Category p : categories) {
                System.out.println(p.getName());
            }
            tx.commit();
            session.close();

            model.put("categories", categories);
            model.put("template", "public/templates/allCategories.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }

    private static void initProduct(final String layout) {
        get("/addProduct", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/addProduct.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());


        get("/addedProduct", (request, response) -> {
            HashMap model = new HashMap();
            String productName = request.queryParams("productName");
            Integer units = Integer.parseInt(request.queryParams("units"));
            String categoryName = request.queryParams("categoryName");

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Category> categories = session.createNativeQuery(
                    "SELECT * FROM CATEGORY WHERE NAME = :name").setParameter("name", categoryName).addEntity(Category.class).list();
            if (categories.isEmpty()) {
                Category category = new Category(categoryName);
                session.save(category);
                categories.add(category);
            }
            System.out.println(categories.get(0).getId());
            Product product = new Product(productName, units, categories.get(0).getId());
            session.save(product);

            Category category = session.get(Category.class, categories.get(0).getId());
            category.addProduct(product);
            session.save(category);

            tx.commit();
            session.close();

            model.put("productName", productName);
            model.put("units", units);
            model.put("categoryName", categoryName);
            model.put("template", "public/templates/addedProduct.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());


        get("/allProducts", (request, response) -> {

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Product> products = session.createNativeQuery(
                    "SELECT * FROM PRODUCT").addEntity(Product.class).list();
            for (Product p : products) {
                System.out.println(p.getProductName());
            }
            tx.commit();
            session.close();

            HashMap model = new HashMap();
            model.put("products", products);
            model.put("template", "public/templates/allProducts.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }

    private static void initSupplier(final String layout) {
        get("/addSupplier", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/addSupplier.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/addedSupplier", (request, response) -> {
            HashMap model = new HashMap();
            String companyName = request.queryParams("companyName");
            String street = request.queryParams("street");
            String city = request.queryParams("city");

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            Supplier supplier = new Supplier(companyName, street, city);
            session.save(supplier);
            tx.commit();
            session.close();

            model.put("companyName", companyName);
            model.put("street", street);
            model.put("city", city);
            model.put("template", "public/templates/addedSupplier.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/allSuppliers", (request, response) -> {
            HashMap model = new HashMap();

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Supplier> suppliers = session.createNativeQuery(
                    "SELECT * FROM SUPPLIER").addEntity(Supplier.class).list();
            for (Supplier p : suppliers) {
                System.out.println(p.getCompanyName());
            }
            tx.commit();
            session.close();

            model.put("suppliers", suppliers);
            model.put("template", "public/templates/allSuppliers.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }

    private static void initSupplier_Product(final String layout) {
        get("/addProductOnSuppliersList", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/addProductOnSuppliersList.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
//GROPU BY SUPPIERLID

        get("/addedProductOnSuppliersList", (request, response) -> {
            HashMap model = new HashMap();
            String companyName = request.queryParams("companyName");
            String productName = request.queryParams("productName");

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Supplier> suppliers = session.createNativeQuery(
                    "SELECT * FROM SUPPLIER WHERE COMPANYNAME = :companyName").setParameter("companyName", companyName).addEntity(Supplier.class).list();
            List<Product> products = session.createNativeQuery(
                    "SELECT * FROM PRODUCT WHERE PRODUCTNAME = :productName").setParameter("productName", productName).addEntity(Product.class).list();
            if (suppliers.isEmpty() || products.isEmpty()) {
                tx.commit();
                session.close();
                model.put("template", "public/templates/wrongInput.vtl");
                return new ModelAndView(model, layout);
            }
            Supplier supplier = suppliers.get(0);
            supplier.addSuppliedProduct(products.get(0));
            session.save(supplier);

            tx.commit();
            session.close();

            model.put("companyName", companyName);
            model.put("productName", productName);
            model.put("template", "public/templates/addedProductOnSuppliersList.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }

    private static void initSuppierLists(final String layout) {

        get("/chooseSupplier", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/chooseSupplier.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/showSupplierList", (request, response) -> {
            HashMap model = new HashMap();
            String companyName = request.queryParams("companyName");
            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Supplier> suppliers = session.createNativeQuery(
                    "SELECT * FROM SUPPLIER WHERE COMPANYNAME = :companyName").setParameter("companyName", companyName).addEntity(Supplier.class).list();
            if (suppliers.isEmpty()) {
                tx.commit();
                session.close();
                model.put("template", "public/templates/wrongInput.vtl");
                return new ModelAndView(model, layout);
            }

            List<Product> products = session.createNativeQuery("SELECT * FROM PRODUCT p JOIN SUPPLIER_PRODUCT sp " +
                    "ON p.ID = sp.PRODUCTS_ID WHERE sp.SUPPLIER_ID = :supplierId")
                    .setParameter("supplierId", suppliers.get(0).getId())
                    .addEntity(Product.class).list();
            for (Product p : products) {
                System.out.println(p.getProductName());
            }

            tx.commit();
            session.close();

            model.put("companyName", companyName);
            model.put("products", products);
            model.put("template", "public/templates/showSupplierList.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }

    private static void initProductList(final String layout) {
        get("/chooseProduct", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/chooseProduct.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/showProductList", (request, response) -> {
            HashMap model = new HashMap();
            String productName = request.queryParams("productName");

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();

            List<Product> products = session.createNativeQuery("SELECT * FROM PRODUCT p " +
                    "WHERE p.PRODUCTNAME = :name").setParameter("name", productName).
                    addEntity(Product.class).list();
            if (products.isEmpty()) {
                tx.commit();
                session.close();
                model.put("template", "public/templates/wrongInput.vtl");
                return new ModelAndView(model, layout);
            }

            List<Supplier> suppliers = session.createNativeQuery("SELECT * FROM SUPPLIER s JOIN SUPPLIER_PRODUCT sp" +
                    " ON sp.SUPPLIER_ID = s.Id WHERE sp.PRODUCTS_ID = :prodId")
                    .setParameter("prodId", products.get(0).getId()).addEntity(Supplier.class).list();

            tx.commit();
            session.close();

            model.put("suppliers", suppliers);
            model.put("productName", productName);
            model.put("template", "public/templates/showProductList.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }

    private static void initTransactions(final String layout) {
        get("/addTransaction", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/addTransaction.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/addedTransaction", (request, response) -> {
            HashMap model = new HashMap();
            Integer quantity = Integer.parseInt(request.queryParams("quantity"));
            String[] products = request.queryParams("products").split(" ");

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Product> productList = new ArrayList<>();
            for (String name : products) {
                List<Product> validProduct = session.createNativeQuery(
                        "SELECT * FROM PRODUCT WHERE PRODUCT.PRODUCTNAME = :name")
                        .setParameter("name", name)
                        .addEntity(Product.class).list();
                if (validProduct.isEmpty()) {
                    tx.commit();
                    session.close();
                    model.put("template", "public/templates/wrongInput.vtl");
                    return new ModelAndView(model, layout);
                }
                productList.add(validProduct.get(0));
            }
            Transactions t = new Transactions(quantity);
            for (Product p : productList) {
                t.addToSales(p);
            }
            session.save(t);
            tx.commit();
            session.close();

            model.put("quantity", quantity);
            model.put("products", productList);
            model.put("template", "public/templates/addedTransaction.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/allTransactions", (request, response) -> {

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Transactions> transactions = session.createNativeQuery(
                    "SELECT * FROM TRANSACTIONS").addEntity(Transactions.class).list();
            tx.commit();
            session.close();
            HashMap model = new HashMap();
            model.put("transactions", transactions);
            model.put("template", "public/templates/allTransactions.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }

    private static void initTransactionList(final String layout) {
        get("/chooseTransaction", (request, response) -> {
            HashMap model = new HashMap();
            model.put("template", "public/templates/chooseTransaction.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        get("/showTransaction", (request, response) -> {
            HashMap model = new HashMap();
            Integer transactionId = Integer.parseInt(request.queryParams("transactionId"));

            Session session = ourSessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            List<Transactions> transactions = session.createNativeQuery(
                    "SELECT * FROM TRANSACTIONS t WHERE t.ID = :transID")
                    .setParameter("transID", transactionId).addEntity(Transactions.class).list();
            if (transactions.isEmpty()) {
                tx.commit();
                session.close();
                model.put("template", "public/templates/wrongInput.vtl");
                return new ModelAndView(model, layout);
            }

            List<Product> products = session.createNativeQuery("SELECT * FROM PRODUCT p JOIN TRANSACTIONS_PRODUCT tp " +
                    "ON p.ID = tp.SALES_ID WHERE tp.canBeSoldInTransaction_ID = :transID")
                    .setParameter("transID", transactions.get(0).getID())
                    .addEntity(Product.class).list();
            for (Product p : products) {
                System.out.println(p.getProductName());
            }

            tx.commit();
            session.close();

            model.put("transactionId", transactionId);
            model.put("products", products);
            model.put("template", "public/templates/showTransaction.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

    }
}