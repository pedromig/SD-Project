package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.interfaces.ProductModel;
import services.interfaces.SearchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs a search in the backend for a specific product and displays the result to the user
 */
public class SearchAction extends ActionSupport {

    private ProductModel inputObject;     /* Object holding the user's input */

    private SearchService searchService;

    /* Search results to be populated by this action */
    private List<Object> results;

    public SearchAction()
    {
        setResults(new ArrayList<Object>());
    }

    public String execute()
    {
        setResults(getSearchService().search(getInputObject()));
        return SUCCESS;
    }


    public ProductModel getInputObject() {
        return inputObject;
    }

    public void setInputObject(ProductModel inputObject) { this.inputObject = inputObject; }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<Object> getResults() {
        return results;
    }

    public void setResults(List<Object> results) {
        this.results = results;
    }

}
