	package budget;

	import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
	import java.util.Hashtable;
	import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class PlanChart extends javax.servlet.http.HttpServlet {

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
					//FileHandling fh = new FileHandling();
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					HttpSession session = request.getSession(true);
					HeaderFooter hf = new HeaderFooter();
					Calendar cal_akt= Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String akt_datum=formatter.format(cal_akt.getTime());
					String plan_id =request.getParameter("plan_id");
					String kategorie_id=request.getParameter("kategorie");
					String batch=request.getParameter("batch");
					DB db = (DB)session.getAttribute("db"); 
					String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
					{
						RequestDispatcher rd;
						rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
						rd.forward(request, response);
						return;
					}
					//System.out.println("Planid="+plan_id);
					Hashtable hash_plan=db.getPlanungen(plan_id);
					//Alle Kategorien in dieser Planung
					//Vector kat_aus=db.getAllPlanungsKategorien(plan_id);
					String rule_id=((Integer)hash_plan.get("rule_id")).toString();
					String rule;
					if (rule_id.equals("-1"))
					{
						//dummy
						
					rule="";
					}
					else
					{
					rule=" AND "+db.getRuleCommand(new Integer(rule_id));
					}
					out.println("<html>");
					out.println("<body>");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					/*
					 Datum setzen
					 */
					Calendar cal_begin= Calendar.getInstance();
					Calendar cal_end= Calendar.getInstance();
					cal_begin.setTime((Date)hash_plan.get("startdatum"));
					cal_end.setTime((Date)hash_plan.get("enddatum"));
					//liegt ds jetzige Datum zwischenStart und Endzeit?
					if (cal_akt.before(cal_begin))
					{
						out.println("<p>Datum liegt nicht im Planungszeitraum!!<p>");
						out.println("</body>");
						out.println("</html>");
						out.close();
						return;
					}
					
					if (cal_akt.after(cal_end))
					{
						out.println("<h1><font color=\"red\">Ende des Planungszeitraums erreicht!!!!!</font></h1>");
						//cal_akt=(Calendar)cal_end.clone();
					}
					Vector daten=new Vector();
					if (batch.equals("ja"))
					{
						Hashtable hash = db.getAllPlanAktuell(plan_id,kategorie_id);
						SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
						if (! hash.containsKey("datum"))
						{
							out.println("<p>Für diese Planung ist der Cache noch nicht berechnet worden!<p>");
							out.println("</body>");
							out.println("</html>");
							out.close();
							return;
						}
						cal_begin.setTime((Date)hash.get("datum"));
						String datum=formatter.format(cal_begin.getTime());
						cal_begin.setTime((Date)hash.get("zeit"));
						String zeit=formatTime.format(cal_begin.getTime());
						
						daten=db.getAllPlanCache(plan_id, kategorie_id);
						out.println("<p>(Letzte Berechnung: "+datum +" "+zeit+")<p>");
		    			
					}
					else
					{
					cal_akt=(Calendar)cal_begin.clone();
					
					Long begin_zeit=cal_begin.getTimeInMillis();
					String kategorie=db.getKategorieName(new Integer(kategorie_id));
					Double wert_relativ=0.0;
					Double summe=0.0;
					Double prozent=0.0;
					String where="";
					if (kategorie_id.equals("-1"))
					{
					where=buildWhere(hf, db, "ausgabe", plan_id,rule);
					}
					if (kategorie_id.equals("-2"))
					{
					where=buildWhere(hf, db, "einnahme", plan_id,rule);
					}
					while (! cal_akt.after(cal_end))
							{
						Long akt_zeit= cal_akt.getTimeInMillis();
						
						long vergangene_tage=(akt_zeit - begin_zeit)/(3600000*24);
						Long end_zeit=cal_end.getTimeInMillis();
						long gesamte_tage=(end_zeit -begin_zeit)/(3600000*24);
					//double faktor=((double)vergangene_tage*100)/(double)gesamte_tage;
						double faktor=((double)vergangene_tage)/(double)gesamte_tage;
						if (faktor<0.05)
						{
							cal_akt.add(Calendar.DATE, 1);
							continue;
						}
					//out.println("<p>gesamt: "+gesamte_tage+" Vergangen: "+vergangene_tage+" Faktor: "+faktor+"<p>");	
				//System.out.println("Kategorie_id="+kategorie_id);
						if (kategorie_id.equals("-1")) {
					// Alle Ausgaben
					wert_relativ = db.getPlanungAllWhere(new Integer(plan_id),
							faktor, buildWhere(hf, db, "ausgabe", plan_id,""));
					//System.out.println("Plan: "+wert_relativ);
					summe = db.getKategorienAlleSummeWhere(
							formatter.format(hash_plan.get("startdatum")),
							formatter.format(cal_akt.getTime()), where);
					//System.out.println("Summe: "+summe);
				} else {
					if (kategorie_id.equals("-2")) {
						// Alle Einnahmenule
						wert_relativ = db.getPlanungAllWhere(new Integer(
								plan_id), faktor,
								buildWhere(hf, db, "einnahme", plan_id,""));
						summe = db.getKategorienAlleSummeWhere(
								formatter.format(hash_plan.get("startdatum")),
								formatter.format(cal_akt.getTime()),
								where);
					} else {
						// Kategorie
						wert_relativ = db.getKategorienAlleRecursivPlanung(
								kategorie, new Integer(plan_id), faktor);
						summe = db.getKategorienAlleRecursivSumme(kategorie,
								formatter.format(hash_plan.get("startdatum")),
								formatter.format(cal_akt.getTime()),rule);
					}
				}
						prozent=0.0;
						if ( wert_relativ ==0.0 )
						{
							prozent=0.0;
						}
						else
						{
						 prozent=(summe*100/wert_relativ);
						}
						Hashtable hash=new Hashtable();
						//hash.put("datum",formatter.format(cal_akt.getTime()));
						hash.put("datum",cal_akt.getTime());
						hash.put("wert",prozent);
						daten.add(hash);
						cal_akt.add(Calendar.DATE, 1);
						
							}
					}
					session.setAttribute("chart_vec", daten);
					//System.out.println(daten);
					out.println("<p><img src=chart?mode=plan width'600' height='400'>");
					out.println("</body>");
					out.println("</html>");
					out.close();
				}catch (Throwable theException) {
						theException.printStackTrace();
					}

			}
			private String formater(Double d)
			{
				String str="";
				DecimalFormat f = new DecimalFormat("#0.00");				
				str=str+f.format(d);
				return str;
			}
			private String formaterProzentAusgabe(Double d)
			{
				String str="";
				DecimalFormat f = new DecimalFormat("#0.00");
				if (d.doubleValue()>80)
				{
					if (d.doubleValue()>100)
					{
						str="<font color=\"red\">";
					}
					else
					{
					str="<font color=\"yellow\">";
					}
				}
				else
				{
						str="<font color=\"green\">";
				}							
				str=str+f.format(d);
				str=str+"</font>";
				return str;
			}
			private String formaterProzentEinnahme(Double d)
			{
				String str="";
				DecimalFormat f = new DecimalFormat("#0.00");
				if (d.doubleValue()>80)
				{
					if (d.doubleValue()>100)
					{
						str="<font color=\"green\">";
					}
					else
					{
					str="<font color=\"yellow\">";
					}
				}
				else
				{
						str="<font color=\"red\">";
				}							
				str=str+f.format(d);
				str=str+"</font>";
				return str;
			}
			String buildWhere(HeaderFooter hf,DB db,String mode,String plan_id,String rule)
			{
				Vector all = db.getAllKategorien(mode);
				//System.err.println("Alle Kategorien:"+all);
				Vector allIds = new Vector();
				for (int i=0;i<all.size();i++)
				{
					if(! db.getPlanWertNull(plan_id,(Integer)((Hashtable)all.elementAt(i)).get("id")))
					{
					allIds.add((Integer)((Hashtable)all.elementAt(i)).get("id"));
					hf.searchSub(allIds,(String)((Hashtable)all.elementAt(i)).get("name") , (Integer)((Hashtable)all.elementAt(i)).get("id"), all,0);
					}
					}
				//System.err.println("Alle Ids:"+allIds);
				String where="(";
				boolean first=true;
				for (int i=0;i<all.size();i++)
				{
					if(allIds.contains((Integer)((Hashtable)all.elementAt(i)).get("id")))
					{
					if (!first)
					{
						where=where+" or ";
					}
					else
					{
						first=false;
					}
					where=where+"kategorie= "+((Hashtable)all.elementAt(i)).get("id");
					}
				}
				where=where+")"+rule;
				return where;
			}
	}
