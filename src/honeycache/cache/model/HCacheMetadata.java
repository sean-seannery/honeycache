package honeycache.cache.model;

import java.sql.Date;

public class HCacheMetadata {


	private String key;
    private String tableName;
	private Date dateAccessed;
	private int frequencyAccessed;
	private int size;

	public HCacheMetadata(String key_id, String table_name, Date date_accessed,	int frequency_accessed, int size) {
		this.key = key_id;
		this.tableName = table_name;
		this.dateAccessed = date_accessed;
		this.frequencyAccessed = frequency_accessed;
		this.size = size;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key_id) {
		this.key = key_id;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String table_name) {
		this.tableName = table_name;
	}
	public Date getDateAccessed() {
		return dateAccessed;
	}
	public void setDateAccessed(Date date_accessed) {
		this.dateAccessed = date_accessed;
	}
	public int getFrequencyAccessed() {
		return frequencyAccessed;
	}
	public void setFrequencyAccessed(int frequency_accessed) {
		this.frequencyAccessed = frequency_accessed;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
