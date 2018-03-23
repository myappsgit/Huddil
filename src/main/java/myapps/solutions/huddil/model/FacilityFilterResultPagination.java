package myapps.solutions.huddil.model;

import java.util.List;

public class FacilityFilterResultPagination {
	
	private String count;
	private List<FacilityFilterResult> facilityFilterView;
	private int id;
	
	public FacilityFilterResultPagination(String id, List<FacilityFilterResult> facilityFilterView) {
		super();
		this.count = id;
		this.facilityFilterView = facilityFilterView;	
	}
	
	public FacilityFilterResultPagination(int id) {
		this.id = id;
	}

	public List<FacilityFilterResult> getFacilityFilterView() {
		return facilityFilterView;
	}
	
	public void setFacilityFilterView(List<FacilityFilterResult> facilityFilterView) {
		this.facilityFilterView = facilityFilterView;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}
