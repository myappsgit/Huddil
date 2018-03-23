package myapps.solutions.huddil.model;

public class CustomerReview {

	private int id;
	private String comments;
	private double rating;
	private Integer parentId;

	public CustomerReview() {

	}

	public CustomerReview(int id, String comments, double rating, int parentId) {
		this.id = id;
		this.comments = comments;
		this.rating = rating;
		this.parentId = parentId;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	
	

}