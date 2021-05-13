package services;

import models.LaptopProductModel;
import models.interfaces.ProductModel;
import services.interfaces.SearchService;
import utils.Compare;

import java.util.ArrayList;
import java.util.List;

/* Search service that can be used to find laptop products according to user-defined conditions */
public class LaptopSearchService implements SearchService {

    /* List of existing laptop products. Emulates a database in this example */
    private List<LaptopProductModel> laptops = new ArrayList<>();

    /* The constructor fills the database with dummy data. */
    public LaptopSearchService()
    {
        LaptopProductModel l1 = new LaptopProductModel("MacBook Pro", "Apple", 2000, 13);
        LaptopProductModel l2 = new LaptopProductModel("Envy", "HP", 600, 15);
        LaptopProductModel l3 = new LaptopProductModel("K53SJ", "Asus", 1000, 15);
        LaptopProductModel l4 = new LaptopProductModel("A515", "Acer", 751, 15);
        LaptopProductModel l5 = new LaptopProductModel("Thinkpad", "Lenovo", 900, 17);
        LaptopProductModel l6 = new LaptopProductModel("Vaio", "Sony", 1600, 13);

        laptops.add(l1);
        laptops.add(l2);
        laptops.add(l3);
        laptops.add(l4);
        laptops.add(l5);
        laptops.add(l6);

    }

    /**
     * Searches for laptop products that match the non-null fields of the variable 'query'.
     * @param query LaptopProductModel that contains the search conditions as chosen by the user.
     * @return a list with the products that match the search, null if there is any problem.
     */
    @Override
    public List<Object> search(ProductModel query) {
        if (query instanceof LaptopProductModel)
        {
            LaptopProductModel queryLaptop = (LaptopProductModel) query;

            List<Object> results = new ArrayList<Object>();

            for (LaptopProductModel laptop: laptops)
            {
                boolean condition1 = Compare.strings(queryLaptop.getModel(), laptop.getModel());
                boolean condition2 = Compare.strings(queryLaptop.getBrand(), laptop.getBrand());
                boolean condition3 = Compare.integers(queryLaptop.getPrice(), laptop.getPrice());
                boolean condition4 = Compare.integers(queryLaptop.getScreenSize(), laptop.getScreenSize());

                // Assume AND condition
                if (condition1 & condition2 & condition3 & condition4)
                {
                    results.add(laptop);
                }
            }

            return results;

        }
        return null;
    }
}
