package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	//Dati per la ricorsione
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private String sequenza;
	double best = 1000000000000000000000000.0;
	
	List<String> listaCitta = new ArrayList<String>();
	
	//Costruttore
	public Model() {
		sequenza="";
		listaCitta.add("Torino");
		listaCitta.add("Genova");
		listaCitta.add("Milano");
	}

	public String getUmiditaMedia(int mese) {
		
		String risultato="";
		MeteoDAO dao = new MeteoDAO();

		List<String> listaCitta = new ArrayList<String>();
		List<Rilevamento> rilevamenti = dao.getAllRilevamenti();
		
		for (Rilevamento r : rilevamenti) {
			if (!listaCitta.contains(r.getLocalita())) listaCitta.add(r.getLocalita());
		}
		
		for (String citta : listaCitta) {
			risultato=risultato+citta+" - Umidità media: "+dao.getAvgRilevamentiLocalitaMese(mese, citta)+"\n";
		}
		
		if (risultato.length()>1) risultato=risultato.substring(0, risultato.length()-1);
		
		return risultato;
		
	}

	
	public String trovaSequenza(int mese) {

		MeteoDAO dao = new MeteoDAO();
		
		//Creo lista di parziale SimpleCity
		List<SimpleCity> parziale = new ArrayList<SimpleCity>();
		
		List<SimpleCity> valori = new ArrayList<SimpleCity>();
		for (String localita : listaCitta) {
			
		//Ottengo tutti i valori
		for (Rilevamento r : dao.getAllRilevamentiLocalitaMese(mese, localita) ) {
		    valori.add(new SimpleCity(r.getLocalita(),r.getUmidita()));
		    }
	    }
		//Avvio ricorsione
		cercaSequenza(parziale,0,mese,valori);
		
		//ritorno sequenza trovata alla fine della ricorsione
		return sequenza;
	}

	private void cercaSequenza(List<SimpleCity> parziale,int livello,int mese, List<SimpleCity> valori) {
		
		//CASO TERMINALE
		if (livello==15) {
			if (controllaParziale(parziale)==true) {
			
				if (punteggioSoluzione(parziale)<best) {
					
					for (SimpleCity city : parziale)
						sequenza=sequenza+city.getNome()+"-";
					    best = punteggioSoluzione(parziale);
					return;
				}
			}
		}
		
		//Controllo intermedio
		if (controllaParziale(parziale)==false) return;
		
		//RICORSIONE
		for (int i=0;i<valori.size();i++) {
		parziale.add(new SimpleCity(valori.get(i).getNome(),valori.get(i).getCosto()));
		cercaSequenza(parziale,livello+1,mese,valori);
		
		//backtracking
		parziale.remove(valori.get(i));	
		}
		
	}
	
	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		double contatore=0.0;
		
		for (SimpleCity c : soluzioneCandidata) 
			score=score+c.getCosto();
		
		for (int i=0;i<14;i++) {
			if (!soluzioneCandidata.get(i).equals(soluzioneCandidata.get(i+1)))
				contatore++;
		}
		return score+(contatore*100.0);
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		int torino=0;
		int milano=0;
		int genova=0;
		
		for (SimpleCity c : parziale) {
			if (c.getNome().compareTo("Torino")==0) torino++;
			if (c.getNome().compareTo("Genova")==0) genova++;
			if (c.getNome().compareTo("Milano")==0) milano++;
		}
		
		//Controllo sul MIN e MAX di Rilevamenti
		if (torino>0 && torino <7 && milano>0 && milano <7 && genova>0 && genova <7) return true;
		
		return false;
	}

}
