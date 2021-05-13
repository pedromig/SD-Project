package services;

import models.CarProductModel;
import models.interfaces.ProductModel;
import services.interfaces.SearchService;
import utils.Compare;

import java.util.ArrayList;
import java.util.List;

/* Search service that can be used to find car products according to user-defined conditions */
public class CarSearchService implements SearchService {

    /* List of existing car products. Emulates a database in this example */
    private List<CarProductModel> cars = new ArrayList<>();

    /* The constructor fills the database with dummy data. */
    public CarSearchService()
    {
        CarProductModel c1 = new CarProductModel("Swift", "Suzuki", 2010, 14000);
        CarProductModel c2 = new CarProductModel("Megane", "Renault", 2018, 19000);
        CarProductModel c3 = new CarProductModel("Model S", "Tesla", 2016, 110000);
        CarProductModel c4 = new CarProductModel("320i", "BMW", 1990, 5000);

        cars.add(c1);
        cars.add(c2);
        cars.add(c3);
        cars.add(c4);

    }

    /**
     * Searches for car products that match the non-null fields of the variable 'query'.
     * @param query CarProductModel that contains the search conditions as chosen by the user.
     * @return a list with the products that match the search, null if there is any problem.
     */
    @Override
    public List<Object> search(ProductModel query) {
        if (query instanceof CarProductModel)
        {
            CarProductModel queryCar = (CarProductModel) query;

            List<Object> results = new ArrayList<Object>();

            for (CarProductModel car: cars)
            {
                boolean condition1 = Compare.strings(queryCar.getCarModel(), car.getCarModel());
                boolean condition2 = Compare.strings(queryCar.getManufacturer(), car.getManufacturer());
                boolean condition3 = Compare.integers(queryCar.getYear(), car.getYear());
                boolean condition4 = Compare.integers(queryCar.getPrice(), car.getPrice());

                // Assume AND condition
                if (condition1 & condition2 & condition3 & condition4)
                {
                    results.add(car);
                }
            }

            return results;

        }
        return null;
    }

}
