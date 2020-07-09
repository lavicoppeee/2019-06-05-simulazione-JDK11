package it.polito.tdp.crimes.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.TypeEvent;

public class Simulator {

	//in 
	int n;
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	
	//mondo
    Graph<Integer,DefaultWeightedEdge> graph;
	Integer centrale;
	
	//mappa del distretto con n agenti liberi 
	Map<Integer,Integer> free;
	
	//out
	Integer bad;
    PriorityQueue<Evento> queue;

	
    public void ini(int n, Integer anno, Integer mese, Integer giorno, Graph<Integer, DefaultWeightedEdge> graph) {
    	this.bad=0;
    	
    	this.anno=anno;
    	this.mese=mese;
    	this.giorno=giorno;
    	
    	this.n=n;
    	this.graph=graph;
    	
    	this.free = new HashMap<Integer, Integer>();
		for(Integer d : this.graph.vertexSet()) {
			this.free.put(d,0);
		}
		
		//scelgo la centrale e ci aggiungo gli agenti
		EventsDao dao=new EventsDao();
		centrale=dao.getDistrettoMin(anno);
		free.put(centrale, n);
		
		//creo e inizializzo la coda
				this.queue = new PriorityQueue<Evento>();
				
				for(Event e : dao.listAllEventsByDate(anno, mese, giorno)) {
					queue.add(new Evento(TypeEvent.CRIMINE, e.getReported_date(),e));
				}
     }

    public void run() {
	Evento e;
	while((e = queue.poll())!=null) {
		switch (e.getTipo()) {
		case CRIMINE:
			Integer partenza = null;
			partenza = cercaAgente(e.getCrimine().getDistrict_id());
			
			if(partenza != null) {
				
				//levo l'agente
				this.free.put(partenza, this.free.get(partenza) - 1);
				//cerco di capire quanto ci metterà l'agente libero ad arrivare sul posto
				Double distanza;
				if(partenza.equals(e.getCrimine().getDistrict_id()))
					distanza = 0.0;
				else
					distanza = this.graph.getEdgeWeight(this.graph.getEdge(partenza, e.getCrimine().getDistrict_id()));
				
				Long seconds = (long) ((distanza * 1000)/(60/3.6));
				this.queue.add(new Evento(TypeEvent.ARRIVA_A, e.getData().plusSeconds(seconds), e.getCrimine()));
				
			} else {
				
				this.bad ++;
			}
			
			break;
		case ARRIVA_A:
			
			Long duration = getDurata(e.getCrimine().getOffense_category_id());
			this.queue.add(new Evento(TypeEvent.GESTITO,e.getData().plusSeconds(duration), e.getCrimine()));
			
			//controllare se il crimine è mal gestito dopo i 15 minuti dall'arrivo
			if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
				this.bad++;
			}
			break;
		case GESTITO:
			
			this.free.put(e.getCrimine().getDistrict_id(), this.free.get(e.getCrimine().getDistrict_id())+1);
			break;
			
		}
	}
	
	
	
	
     }



	private Long getDurata(String offense_category_id) {
		
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5)
				return Long.valueOf(2*60*60);
			else
				return Long.valueOf(1*60*60);
		} else {
			return Long.valueOf(2*60*60);
		}
	}

	private Integer cercaAgente(Integer d) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;
		
		for(Integer f : this.free.keySet()) {
			if(this.free.get(f) > 0) {
				if(d.equals(f)) {
					distanza = 0.0;
					distretto = d; 
				} else if(this.graph.getEdgeWeight(this.graph.getEdge(d, f)) < distanza) {
					distanza = this.graph.getEdgeWeight(this.graph.getEdge(d, f));
					distretto = f;
				}
			}
		}
		return distretto;
		
		
	}

	public int getnBad() {
		return bad;
	}

}
