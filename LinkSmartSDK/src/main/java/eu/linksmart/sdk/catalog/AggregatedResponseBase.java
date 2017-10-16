package eu.linksmart.sdk.catalog;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public abstract class AggregatedResponseBase {
    protected String description;
    protected int page, per_page, total;

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public int getPage() {
        return page;
    }

    
    public void setPage(int page) {
        this.page = page;
    }

    
    public int getPer_page() {
        return per_page;
    }

    
    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    
    public int getTotal() {
        return total;
    }

    
    public void setTotal(int total) {
        this.total = total;
    }
}
