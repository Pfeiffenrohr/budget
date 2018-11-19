package budget;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

//Aufruf http://localhost:8080/filme/MainFrame

public class TransaktionenUpdaten extends javax.servlet.http.HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		performTask(request, response);

	}

	public void doPost(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {
		performTask(request, response);

	}

	public void performTask(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response) {
		try {
			// FileHandling fh = new FileHandling();
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			HttpSession session = request.getSession(true);
			String user=(String)((Hashtable)session.getAttribute("settings")).get("user");

			DB db = (DB) session.getAttribute("db");
			if (db == null) {
				RequestDispatcher rd;
				rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
				rd.forward(request, response);
				return;
			}
			HeaderFooter hf = new HeaderFooter();
			String loeschen = request.getParameter("loeschen");
			String name = request.getParameter("Name");
			String wert = request.getParameter("wert");
			String beschreibung = request.getParameter("beschreibung");
			String datum = request.getParameter("dob");
			String kategorie =request.getParameter("kategorie");
			if (kategorie==null)
			{
				kategorie="";
			}
			String partner = request.getParameter("partner");
			String korkonto =request.getParameter("korkonto");
			if (korkonto==null)
			{
				korkonto="nein";
			}
			String kor_konto = request.getParameter("kor_konto");
			if (kor_konto==null)
			{
				kor_konto="Girokonto";
			}
			String anwenden=request.getParameter("anwenden");
			if (anwenden==null)
			{
				anwenden="alle";
			}
			String planung=request.getParameter("planung");
			if (planung==null)
			{
				planung="n";
			}
			String konto = (String)session.getAttribute("akt_konto");
			Hashtable hash = (Hashtable)session.getAttribute("transaktionensatz");
			hash.put("name",name);
			hash.put("konto",konto);
			hash.put("wert",wert);
			hash.put("beschreibung",beschreibung);
			hash.put("partner",partner);
			hash.put("kategorie",kategorie);
			//hash.put("kor_konto",kor_konto);
			hash.put("datum",datum);
			hash.put("planed",planung);
			hash.put("user",user);
			
			
			out.println("<html>");
			out.println("<body>");
			hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));			
			if (loeschen != null) {
				out.println("<p>");
				out.println("Transaktion wird gelöscht...");
				out.println("<p>");
				if (((Integer) hash.get("kor_id")).intValue() == 0) {
					//Normale Transaktion
					if (db.deleteTransaktion((Integer) hash.get("id"))) {
						out.println("Transaktion erfolgreich gelöscht");
						out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
					} else {
						out.println("<font color=\"red\">!!!Transaktion konte nicht gelöscht werden!!!</font>");
						out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
					}
					
				} 
				else
				{
						//Transaktion mit Gegenbuchung	
						if (db.deleteTransaktion_kor_id((Integer) hash.get("kor_id"),anwenden,datum)) 
							{
							out.println("Transaktion und alle Gegenbuchungen erfolgreich gelöscht");
							out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} 
						else 
							{
							out.println("<font color=\"red\">!!!Transaktion konte nicht gelöscht werden!!!</font>");
							out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							}
						
				}
				out.println("</body>");
				out.println("</html>");
				out.close();
				return;
					
			}// Ende Löschen
			out.println("<p>");
			out.println("Transaktion wird updatet...");
			out.println("<p>");
			if (((Integer) hash.get("kor_id")).intValue() == 0) {
				//Normale Transaktion
				if (db.updateTransaktion(hash)) {
					out.println("Transaktion erfolgreich update");
					out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
				} else {
					out.println("<font color=\"red\">!!!Transaktion konte nicht updatet werden!!!</font>");
					out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
				}
			}
			else
			{
				if (((Integer) hash.get("cycle")).intValue() == 0) 
				   {
					//Transaktion mit Gegenbuchung (kein cycle)
					if (db.updateTransaktion(hash)) {
						out.println("Transaktion erfolgreich update");
					} else {
						out.println("<font color=\"red\">!!!Transaktion konte nicht updatet werden!!!</font>");
					}
					String bisherigeskonto=db.getKorKonto((String)hash.get("konto"),(Integer)hash.get("kor_id"));
					hash.put("konto", bisherigeskonto);
					hash.put("neues_konto", kor_konto);
					hash.put("wert", invert(wert));
					hash.put("user",user);
					if (db.updateTransaktionWithKorAndKontoid(hash)) {
						out.println("<p>");
						out.println("Transaktion auf Gegenkonto erfolgreich update");
						out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
					} else {
						out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
						out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
					}
				   }
				if (((Integer) hash.get("cycle")).intValue() == 1) 
					{//Transaktion mit Wiederholung (kein Gegenbuchung)
					//System.out.println("Anwenden  ..."+anwenden);
						if (anwenden.equals("alle"))
						{
							hash.put("neues_konto",hash.get("konto"));
							hash.put("neue_kor_id",hash.get("kor_id"));
							hash.put("user",user);
							if (db.updateTransaktionWithKorAndKontoid(hash)) {
								out.println("<p>");
								out.println("Transaktion alle Transaktionen erfolgreich update");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} else 
							{
								out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							}
						}
							if (anwenden.equals("folgend"))
							{
								
								//System.out.println("Folgende ...");
								Integer new_korid=db.getHighestId("transaktionen", "kor_id")+1;
								Hashtable hash_cycle=db.getCycleTransaktionen((Integer)hash.get("kor_id"));
								db.setNewEnddate((Integer)hash.get("kor_id"),(String)hash.get("datum"));
								db.setNoendNo((Integer)hash.get("kor_id"));	
								hash.put("neues_konto",hash.get("konto"));
								hash.put("neue_kor_id",new_korid);
								hash.put("user",user);
								if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),1)) {
									out.println("<p>");
									out.println("alle folgende Transaktionen erfolgreich updatet");
									out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
								} else 
								{
									out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
									out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
								}	
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								//System.err.println("Hash ="+ hash_cycle);
								//neues Enddatum für den alten cycle
													
								hash_cycle.put("korid", new_korid);
								hash_cycle.put("name",(String)hash_cycle.get("name")+new_korid);
								db.insertCycleTransaktion(hash_cycle,"0");
								
							}
							if (anwenden.equals("einzeln"))
							{
								
								//System.out.println("Folgende ...");
								Integer new_korid=db.getHighestId("transaktionen", "kor_id")+1;
								//Hashtable hash_cycle=db.getCycleTransaktionen((Integer)hash.get("kor_id"));
								//db.setNewEnddate((Integer)hash.get("kor_id"),(String)hash.get("datum"));
								//db.setNoendNo((Integer)hash.get("kor_id"));	
								hash.put("neues_konto",hash.get("konto"));
								hash.put("neue_kor_id",new_korid);
								/*if((Integer)hash.get("cycle")==1)
								{
									hash.put("kor_id",0);
								}*/
								hash.put("cycle",0);
								hash.put("user",user);
			
								if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),0)) {
									//cycle auf 0 setzen
									db.updateTransaktionSetKorId0(hash);
									out.println("<p>");
									out.println("Transaktion erfolgreich updatet");
									out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
								} else 
								{
									out.println("<font color=\"red\">!!!Transaktion konte nicht updatet werden!!!</font>");
									out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
								}	
								//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								//System.err.println("Hash ="+ hash_cycle);
								//neues Enddatum für den alten cycle
													
								//hash_cycle.put("korid", new_korid);
								//hash_cycle.put("name",(String)hash_cycle.get("name")+new_korid);
								//db.insertCycleTransaktion(hash_cycle);
								
							}
							if (anwenden.equals("zuvor"))
							{
								
								//System.out.println("Folgende ...");
								Integer new_korid=db.getHighestId("transaktionen", "kor_id")+1;
								Hashtable hash_cycle=db.getCycleTransaktionen((Integer)hash.get("kor_id"));
								hash.put("neues_konto",hash.get("konto"));
								hash.put("neue_kor_id",new_korid);
								hash.put("user",user);
								if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),2)) {
									out.println("<p>");
									out.println("alle folgende Transaktionen erfolgreich updatet");
									out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
								} else 
								{
									out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
								}	
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								//System.err.println("Hash ="+ hash_cycle);
								//neues Enddatum für den alten cycle
													
								hash_cycle.put("korid", new_korid);
								
								hash_cycle.put("name",(String)hash_cycle.get("name")+new_korid);
								db.insertCycleTransaktion(hash_cycle,"0");
								db.setNewEnddate((Integer)hash.get("neue_kor_id"),(String)hash.get("datum"));
								db.setNoendNo((Integer)hash.get("neue_kor_id"));	
								
							}
					
				}
				if (((Integer) hash.get("cycle")).intValue() == 2) 
				{//Transaktion mit Wiederholung und Gegenbuchung)
				//System.out.println("Anwenden  ..."+anwenden);
					if (anwenden.equals("alle"))
					{
						hash.put("neues_konto",hash.get("konto"));
						hash.put("neue_kor_id",hash.get("kor_id"));
						hash.put("user",user);
						if (db.updateTransaktionWithKorAndKontoid(hash)) {
							out.println("<p>");
							out.println("Transaktion alle Transaktionen erfolgreich update");
							out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
						} else 
						{
							out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
						}
						String bisherigeskonto=db.getKorKonto((String)hash.get("konto"),(Integer)hash.get("kor_id"));
						hash.put("konto", bisherigeskonto);
						hash.put("neues_konto", kor_konto);
						hash.put("wert", invert(wert));
						hash.put("user",user);
						if (db.updateTransaktionWithKorAndKontoid(hash)) {
							out.println("<p>");
							out.println("Transaktion auf Gegenkonto erfolgreich update");
							out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
						} else {
							out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
						}
					 }
						if (anwenden.equals("folgend"))
						{
							
							//System.out.println("Folgende ...");
							Integer new_korid=db.getHighestId("transaktionen", "kor_id")+1;
							Hashtable hash_cycle=db.getCycleTransaktionen((Integer)hash.get("kor_id"));
							db.setNewEnddate((Integer)hash.get("kor_id"),(String)hash.get("datum"));
							db.setNoendNo((Integer)hash.get("kor_id"));	
							hash.put("neues_konto",hash.get("konto"));
							hash.put("neue_kor_id",new_korid);
							hash.put("user",user);
							if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),1)) {
								out.println("<p>");
								out.println("alle folgende Transaktionen erfolgreich updatet");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} else 
							{
								out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
							}	
							String bisherigeskonto=db.getKorKonto((String)hash.get("konto"),(Integer)hash.get("kor_id"));
							hash.put("konto", bisherigeskonto);
							hash.put("neues_konto", kor_konto);
							hash.put("wert", invert(wert));
							hash.put("user",user);
							if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),1)) {
								out.println("<p>");
								out.println("Transaktion auf Gegenkonto erfolgreich update");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} else {
								out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
							}
							
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							//System.err.println("Hash ="+ hash_cycle);
							//neues Enddatum für den alten cycle
												
							hash_cycle.put("korid", new_korid);
							hash_cycle.put("name",(String)hash_cycle.get("name")+"_"+new_korid);
							db.insertCycleTransaktion(hash_cycle,"0");
							
						}
						if (anwenden.equals("einzeln"))
						{
							
							//System.out.println("Folgende ...");
							Integer new_korid=db.getHighestId("transaktionen", "kor_id")+1;
							//Hashtable hash_cycle=db.getCycleTransaktionen((Integer)hash.get("kor_id"));
							//db.setNewEnddate((Integer)hash.get("kor_id"),(String)hash.get("datum"));
							//db.setNoendNo((Integer)hash.get("kor_id"));	
							hash.put("neues_konto",hash.get("konto"));
							hash.put("neue_kor_id",new_korid);
							hash.put("cycle",0);
							hash.put("user",user);
							if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),0)) {
								out.println("<p>");
								out.println("alle folgende Transaktionen erfolgreich updatet");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} else 
							{
								out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
							}	
							String bisherigeskonto=db.getKorKonto((String)hash.get("konto"),(Integer)hash.get("kor_id"));
							hash.put("konto", bisherigeskonto);
							hash.put("neues_konto", kor_konto);
							hash.put("wert", invert(wert));
							hash.put("user",user);
							if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),0)) {
								out.println("<p>");
								out.println("Transaktion auf Gegenkonto erfolgreich update");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} else {
								out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
							}
							
							//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							//System.err.println("Hash ="+ hash_cycle);
							//neues Enddatum für den alten cycle
												
							//hash_cycle.put("korid", new_korid);
							//hash_cycle.put("name",(String)hash_cycle.get("name")+"_"+new_korid);
							//db.insertCycleTransaktion(hash_cycle);
							
						}
						if (anwenden.equals("zuvor"))
						{
							
							//System.out.println("Folgende ...");
							Integer new_korid=db.getHighestId("transaktionen", "kor_id")+1;
							Hashtable hash_cycle=db.getCycleTransaktionen((Integer)hash.get("kor_id"));
							hash.put("neues_konto",hash.get("konto"));
							hash.put("neue_kor_id",new_korid);
							hash.put("user",user);
							if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),2)) {
								out.println("<p>");
								out.println("alle folgende Transaktionen erfolgreich updatet");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} else 
							{
								out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
							}
							String bisherigeskonto=db.getKorKonto((String)hash.get("konto"),(Integer)hash.get("kor_id"));
							hash.put("konto", bisherigeskonto);
							hash.put("neues_konto", kor_konto);
							hash.put("wert", invert(wert));
							hash.put("user",user);
							if (db.updateTransaktionWithKorAndKontoidDatum(hash,(String)hash.get("datum"),2)) {
								out.println("<p>");
								out.println("Transaktion auf Gegenkonto erfolgreich update");
								out.println("<p><a href='transaktionsverwaltung?konto="+konto+"'>Zurück zur Transaktionsverwaltung</a>");
							} else {
								out.println("<font color=\"red\">!!!Transaktion auf Gegenkontokonte nicht updatet werden!!!</font>");
							}
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							//System.err.println("Hash ="+ hash_cycle);
							//neues Enddatum für den alten cycle
												
							hash_cycle.put("korid", new_korid);
							
							hash_cycle.put("name",(String)hash_cycle.get("name")+"_"+new_korid);
							db.insertCycleTransaktion(hash_cycle,"0");
							db.setNewEnddate((Integer)hash.get("neue_kor_id"),(String)hash.get("datum"));
							db.setNoendNo((Integer)hash.get("neue_kor_id"));	
							
						}
				}
			}
			
			out.println("</body>");
			out.println("</html>");
			out.close();

		} catch (Throwable theException) {
			theException.printStackTrace();
		}

	}
	private String invert(String wert)
	{
		try{
		Double db = new Double(wert);
		db=db*-1;
		return db.toString();
		}
		catch (NumberFormatException nfe )
		{
			System.err.println (nfe);
			return "0.0";
		}
	}
}
