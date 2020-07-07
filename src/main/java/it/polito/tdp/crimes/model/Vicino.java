package it.polito.tdp.crimes.model;

public class Vicino implements Comparable<Vicino>{
	
	Integer v;
	Integer distanza;
	
	public Vicino(Integer v, Integer distanza) {
		super();
		this.v = v;
		this.distanza = distanza;
	}
	
	
	public Integer getV() {
		return v;
	}
	public void setV(Integer v) {
		this.v = v;
	}
	public Integer getDistanza() {
		return distanza;
	}
	public void setDistanza(Integer distanza) {
		this.distanza = distanza;
	}


	@Override
	public int compareTo(Vicino o) {
		// TODO Auto-generated method stub
		return  this.distanza.compareTo(o.getDistanza());
	}
	
	
	

}
