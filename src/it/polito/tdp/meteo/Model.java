package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	//Dati per la ricorsione
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	//Costruttore
	public Model() {
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
			risultato=risultato+citta+"- Umidità media: "+dao.getAvgRilevamentiLocalitaMese(mese, citta)+"\n";
		}
		
		if (risultato.length()>1) risultato=risultato.substring(0, risultato.length()-1);
		
		return risultato;
		
	}

	public String trovaSequenza(int mese) {

		return "TODO!";
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		return true;
	}

}
