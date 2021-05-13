package services;

import models.ShoeProductModel;
import models.interfaces.ProductModel;
import services.interfaces.SearchService;
import utils.Compare;

import java.util.ArrayList;
import java.util.List;

/* Search service that can be used to find shoe products according to user-defined conditions */
public class ShoesSearchService implements SearchService {


    /* List of existing shoe products. Emulates a database in this example */
    private List<ShoeProductModel> shoes = new ArrayList<>();

    /* The constructor fills the database with dummy data. */
    public ShoesSearchService()
    {
        ShoeProductModel s1 = new ShoeProductModel(38, "Brown", "Nike");
        ShoeProductModel s2 = new ShoeProductModel(32, "Black", "Adidas");
        ShoeProductModel s3 = new ShoeProductModel(40, "White", "Adidas");
        ShoeProductModel s4 = new ShoeProductModel(45, "Brown", "Zara");
        ShoeProductModel s5 = new ShoeProductModel(39, "Pink", "Geox");
        ShoeProductModel s6 = new ShoeProductModel(36, "Orange", "CAT");
        ShoeProductModel s7 = new ShoeProductModel(34, "Brown", "Camper");

        shoes.add(s1);
        shoes.add(s2);
        shoes.add(s3);
        shoes.add(s4);
        shoes.add(s5);
        shoes.add(s6);
        shoes.add(s7);


    }

    /**
     * Searches for shoe products that match the non-null fields of the variable 'query'.
     * @param query ShoeProductModel that contains the search conditions as chosen by the user.
     * @return a list with the products that match the search, null if there is any problem.
     */
    @Override
    public List<Object> search(ProductModel query) {
        if (query instanceof ShoeProductModel)
        {
            ShoeProductModel queryShoe = (ShoeProductModel) query;

            List<Object> results = new ArrayList<Object>();

            for (ShoeProductModel shoe: shoes)
            {
                boolean condition1 = Compare.integers(queryShoe.getSize(), shoe.getSize());
                boolean condition2 = Compare.strings(queryShoe.getColor(), shoe.getColor());
                boolean condition3 = Compare.strings(queryShoe.getBrand(), shoe.getBrand());

                // Assume AND condition
                if (condition1 & condition2 & condition3)
                {
                    results.add(shoe);
                }
            }

            return results;

        }
        return null;
    }
}
