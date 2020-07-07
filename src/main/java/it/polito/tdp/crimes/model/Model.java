package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao;
	List<Integer> vertici;
	private Graph<Integer,DefaultWeightedEdge> graph;

	
	
	public Model() {
		dao= new EventsDao();
	}
	
	//getAnno
	public List<Integer> getAnno(){
		return dao.getAnno();
	}
	
	//creaGrafo
	
	public void creaGrafo(int anno) {
		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		vertici=dao.getDistretti(anno);
		Graphs.addAllVertices(graph, vertici);
		
		for(Integer d1: this.graph.vertexSet()) {
			for(Integer d2: this.graph.vertexSet()) {
				if(!d1.equals(d2)) {
					Double lat1=dao.getLat(anno, d1);
					Double lat2=dao.getLat(anno, d2);
					
					Double lon1=dao.getLon(anno, d1);
					Double lon2=dao.getLon(anno, d2);
					
					Double peso= LatLngTool.distance(new LatLng(lat1,lon1), new LatLng(lat2,lon2), LengthUnit.KILOMETER);
							
					Graphs.addEdgeWithVertices(graph, d1, d2,peso);

				}
			}
		}
	}
	
	public List<Vicino> getVicini(Integer d){
		List<Vicino> result =new ArrayList<>();
		
		List<Integer> vicini=Graphs.neighborListOf(graph, d);
		
		for(Integer v: vicini) {
			Integer peso=(int) this.graph.getEdgeWeight(this.graph.getEdge(d, v));
			result.add(new Vicino(v,peso));
		}
		
		Collections.sort(result);
		return result;
	} 
	
	
	public int nVertici() {
		return this.graph.vertexSet().size();
	}

	public int nArchi() {
		return this.graph.edgeSet().size();
	}

	public List<Integer> getVertici() {
		// TODO Auto-generated method stub
		return vertici;
	}
	
	
}
