package honeycache.cache.model;

import java.sql.Date;

public class HCacheMetadata {


	private String key;
    private String cacheTableName;
	private Date dateAccessed;
	private int frequencyAccessed;
	private int size;
	private String originalTable;
	private String partitionData;

	public HCacheMetadata(String key_id, String table_name, Date date_accessed,	int frequency_accessed, int size, String originalTable, String partitionData) {
		this.key = key_id;
		this.cacheTableName = table_name;
		this.dateAccessed = date_accessed;
		this.frequencyAccessed = frequency_accessed;
		this.size = size;
		this.originalTable = originalTable;
		this.partitionData = partitionData;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key_id) {
		this.key = key_id;
	}
	public String getCacheTableName() {
		return cacheTableName;
	}
	public void setCacheTableName(String table_name) {
		this.cacheTableName = table_name;
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
	public String getOriginalTable() {
		return originalTable;
	}
	public void setOriginalTable(String originalTable) {
		this.originalTable = originalTable;
	}
	public String getPartitionData() {
		return partitionData;
	}
	public void setPartitionData(String partitionData) {
		this.partitionData = partitionData;
	}
	
}
