package de.sedico.generictableadapter;

import java.io.Serializable;
/**
 * Diese Klasse implementiert die Headers der Spalten, welche das Interface Serializable implementiert.
 * @author jens
 *
 */
public class TableColHeader implements Serializable {   

	private static final long serialVersionUID = 1L;
	private String propertyUri;
    private String label;
    private String unitLabel;
    private String unitUri;

    public TableColHeader (String label, String propertyUri) {
    	this.propertyUri = propertyUri;
    	this.label = label;
    }
    
	public String getPropertyUri() {
		return propertyUri;
	}
	public void setPropertyUri(String propertyUri) {
		this.propertyUri = propertyUri;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUnitLabel() {
		return unitLabel;
	}
	public void setUnitLabel(String unitLabel) {
		this.unitLabel = unitLabel;
	}
	public String getUnitUri() {
		return unitUri;
	}
	public void setUnitUri(String unitUri) {
		this.unitUri = unitUri;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result
				+ ((propertyUri == null) ? 0 : propertyUri.hashCode());
		result = prime * result
				+ ((unitLabel == null) ? 0 : unitLabel.hashCode());
		result = prime * result + ((unitUri == null) ? 0 : unitUri.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableColHeader other = (TableColHeader) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (propertyUri == null) {
			if (other.propertyUri != null)
				return false;
		} else if (!propertyUri.equals(other.propertyUri))
			return false;
		if (unitLabel == null) {
			if (other.unitLabel != null)
				return false;
		} else if (!unitLabel.equals(other.unitLabel))
			return false;
		if (unitUri == null) {
			if (other.unitUri != null)
				return false;
		} else if (!unitUri.equals(other.unitUri))
			return false;
		return true;
	}
    
    
}