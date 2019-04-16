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

	private List<Citta> best;
	private List<Citta> leCitta;

	public List<Citta> getBest() {
		return this.best;
	}
	
	List<String> listaCitta = new ArrayList<String>();
	
	//Costruttore
	public Model() {
	}

	//Metodo per avere tutte le citta' presenti nel DB (lo richiameremo sempre dal DAO)
	public List<Citta> getCitta(){
	     MeteoDAO dao = new MeteoDAO();
	     return dao.getCitta();
	}
	
	public String getUmiditaMedia(int mese) {
		
		String risultato="";
		MeteoDAO dao = new MeteoDAO();
		List<Citta> listaCitta = dao.getCitta();	
		
		for (Citta citta : listaCitta) {
			risultato=risultato+citta.getNome()+" - Umidità media: "+dao.getAvgRilevamentiLocalitaMese(mese, citta.getNome())+"\n";
		}
		
		if (risultato.length()>1) risultato=risultato.substring(0, risultato.length()-1);
		return risultato;
		
	}

	
	public List<Citta> trovaSequenza(int mese) {

		//Collego Model e Dao
		MeteoDAO dao = new MeteoDAO();
		
		//Creo soluzione parziale vuota
		List<Citta> parziale = new ArrayList<Citta>();
		//Azzero best
		this.best=null;
		//Aggiungo Citta dal DB
		leCitta = dao.getCitta();
		
		//Carico dentro ciascuna citta la lista dei rilevamenti del mese considerato
        for (Citta c : leCitta) {
        	c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
        }
        
		//Avvio ricorsione
		cercaSequenza(parziale,0);
		
		//ritorno la miglior sequenza trovata 
		return best;
	}

	private void cercaSequenza(List<Citta> parziale,int livello) {
		
		//CASO TERMINALE
		if (livello == NUMERO_GIORNI_TOTALI) {	    
			double costo = punteggioSoluzione(parziale);
		    //  Best vuoto  o  trovato best migliore
		    if (best==null || costo<punteggioSoluzione(best)) {
			    best = new ArrayList<>(parziale);
			    return;
			    }
		    }
		
		//CASO INTERMEDIO
		for (Citta prova : leCitta ) {
			if ( controllaParziale(parziale,prova)) {
				parziale.add(prova);
				cercaSequenza(parziale,livello+1);
				//backtracking
				parziale.remove(parziale.size()-1);
			}
		}	   
	}
	
	public Double punteggioSoluzione(List<Citta> parziale) {

		double costo = 0.0;

		// Sommatoria delle umidità in ciascuna città, considerando il rilevamento del giorno giusto
		// SOMMA parziale.get(giorno-1).getRilevamenti().get(giorno-1)
		
		for (int giorno = 1; giorno <= NUMERO_GIORNI_TOTALI; giorno++) {
			
			// Dove mi trovo?
			Citta c = parziale.get(giorno - 1);
			
			// Che umidità ho in quel giorno in quella città?
			double umidita = c.getRilevamenti().get(giorno - 1).getUmidita();
			
			//Aggiungo umidita al costo totale
			costo += umidita;
		}

		// A cui sommo 100 * numero di volte in cui cambio città
		for (int giorno = 2; giorno <= NUMERO_GIORNI_TOTALI; giorno++) {
			//Verifico giorno precedente e giorno successivi diversi
			if (!parziale.get(giorno - 1).equals(parziale.get(giorno - 2))) {
				//Se è vero aggiungo la costante
				costo += COST;
			}
		}
        //Alla fine ritorno il costo
		return costo;
	}

	private boolean controllaParziale(List<Citta> parziale,Citta prova) {

		//VERIFICO GIORNI MAX
		int conta = 0;
		
		//Verifico quante volte la citta in prova è stata già aggiunta
		for (Citta precedente : parziale)
			if (precedente.equals(prova)) conta ++;
		
		//Superato limite MAX dei giorni
		if (conta >= NUMERO_GIORNI_CITTA_MAX) return false;
		
		//VERIFICO GIORNO MIN
	    if (parziale.size() == 0) /* Primo giorno */ return true;
		
	    if (parziale.size() == 1 || parziale.size() == 2) //Secondo o Terzo giorno: Non posso cambiare citta'
		return parziale.get(parziale.size() - 1).equals(prova);
	    
		if (parziale.get(parziale.size() - 1).equals(prova)) // Giorni successivi, posso SEMPRE rimanere
			return true;
		
		// Quando cambio citta'
		if (parziale.get(parziale.size() - 1).equals(parziale.get(parziale.size() - 2)) && parziale.get(parziale.size() - 2).equals(parziale.get(parziale.size() - 3)))
		return true;

		//In tutti gli altri casi 
		return false;	
	    }
 }