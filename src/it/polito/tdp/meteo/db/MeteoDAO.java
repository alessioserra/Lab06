package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		String sql="";
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>(); 
		
		if (mese < 10) {
			 sql = "SELECT Localita, Data, Umidita FROM situazione WHERE data LIKE ? AND localita=? ORDER BY data ASC";
			}
			else {
			 sql = "SELECT Localita, Data, Umidita FROM situazione WHERE data LIKE ? AND localita=? ORDER BY data ASC";
			}
			
			try {
				Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(sql);

				//Assegno parametri
				if (mese <10 ) st.setString(1, "2013-0"+mese+"%%");
				else st.setString(1, "2013-"+mese+"%%");
				
				st.setString(2, localita);
				
				ResultSet rs = st.executeQuery();
				
				while (rs.next()) {

					Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
					rilevamenti.add(r);
				}

				conn.close();
				
				//Calcolo media e restituisco
				return rilevamenti;

			} catch (SQLException e) {

				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	
	
	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		double valoreUmiditaMedio=0.0;
		
		final String sql = "SELECT localita,SUM(umidita) AS somma,COUNT(DATA) AS giorni FROM situazione WHERE data LIKE ? AND localita=? GROUP BY localita";
			
			try {
				Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement st = conn.prepareStatement(sql);

				//Assegno parametri
				if (mese <10 ) st.setString(1, "2013-0"+mese+"%%");
				else st.setString(1, "2013-"+mese+"%%");
				
				st.setString(2, localita);
				
				ResultSet rs = st.executeQuery();

				double somma=0.0;
				double giorni=0.0;
				
				while (rs.next()) {

					somma = rs.getInt("somma");
					giorni = rs.getInt("giorni");
					
				}
				conn.close();
				
				//Calcolo media e restituisco
				valoreUmiditaMedio =somma/giorni;
				return valoreUmiditaMedio;

			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
}
