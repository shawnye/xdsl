package unicom.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page<E> {
	private int totalItems;
	private int pageSize;//max rows
	private int pageNumber;
    private int pagesAvailable;
    private List<E> pageItems = new ArrayList<E>();
    
    private Map<String, Object> additionInfos = new HashMap<String, Object>();

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPagesAvailable(int pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }

    public void setPageItems(List<E> pageItems) {
        this.pageItems = pageItems;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPagesAvailable() {
        return pagesAvailable;
    }
    
    
    public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public List<E> getPageItems() {
        return pageItems;
    }
	
	public void addAdditionInfo(String key, Object value){
		this.additionInfos.put(key, value);
	}
	
	public Object getAdditionInfo(String key){
		return this.additionInfos.get(key);
	}
}
