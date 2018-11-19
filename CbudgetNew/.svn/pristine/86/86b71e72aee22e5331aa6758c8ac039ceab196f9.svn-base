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

		public class ZeigePlanung extends javax.servlet.http.HttpServlet {

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
					
					String plan_id =request.getParameter("plan");
					String zeit =request.getParameter("zeit");
					DB db = (DB)session.getAttribute("db"); 
					String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
					{
						RequestDispatcher rd;
						rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
						rd.forward(request, response);
						return;
					}
					Hashtable hash_plan=db.getPlanungen(plan_id);
					String batch=(String)hash_plan.get("batch");
					//Alle Kategorien in dieser Planung
					//Vector kat_aus=db.getAllPlanungsKategorien(plan_id);
					Vector kat_aus=db.getAllKategorien("ausgabe");
					Vector kat_ein=db.getAllKategorien("einnahme");
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
						cal_akt=(Calendar)cal_end.clone();
						akt_datum=formatter.format(cal_akt.getTime());
					}
					if (zeit.equals("monat"))
					{
						cal_akt.set(Calendar.DAY_OF_MONTH, cal_akt.getActualMaximum(Calendar.DAY_OF_MONTH));
						akt_datum=formatter.format(cal_akt.getTime());
					}
					Long akt_zeit= cal_akt.getTimeInMillis();
					Long begin_zeit=cal_begin.getTimeInMillis();
					long vergangene_tage=(akt_zeit - begin_zeit)/(3600000*24);
					Long end_zeit=cal_end.getTimeInMillis();
					long gesamte_tage=(end_zeit -begin_zeit)/(3600000*24);
					//double faktor=((double)vergangene_tage*100)/(double)gesamte_tage;
					double faktor=((double)vergangene_tage)/(double)gesamte_tage;
					//out.println("<p>gesamt: "+gesamte_tage+" Vergangen: "+vergangene_tage+" Faktor: "+faktor+"<p>");
					out.println("<h2>Ausgaben</h2>");
					out.println("<table border=\"1\"  bgcolor=\"#CCEECC\">");
					//out.println("<table border=\"1\">");
					out.println("<thead>");
					out.println("<tr>");
					out.println("<th>Kategorie</th>");
					out.println("<th>Geplanter Wert</th>");
					out.println("<th>Ist Wert</th>");
					out.println("<th>Erreicht in %</th>");
					out.println("<th>Graph</th>");
					out.println("</tr>");
					out.println("</thead>");
					out.println("<tbody>");
					Double wert_relativ;
					Double summe;
					Double prozent;
					for (int i=0; i<kat_aus.size();i++)
					{
						String kategorie=(String)((Hashtable)kat_aus.elementAt(i)).get("name");
						 wert_relativ=db.getKategorienAlleRecursivPlanung(kategorie,new Integer(plan_id),faktor);
						 summe=db.getKategorienAlleRecursivSumme(kategorie,formatter.format(hash_plan.get("startdatum")),akt_datum,rule);
					    prozent=0.0;
						if ( wert_relativ ==0.0 )
						{
							prozent=0.0;
						}
						else
						{
						 prozent=(summe*100/wert_relativ);
						}
						if (wert_relativ !=0.0)
						{
						out.println("<tr>");
						out.println("<td>"+kategorie+"</td><td>"+formater(wert_relativ)+"</td><td>"+formater(summe)+"</td><td>"
								+formaterProzentAusgabe(prozent)
								+ "</td><td><a href='planChart?kategorie="
								+(Integer)((Hashtable)kat_aus.elementAt(i)).get("id")
								+"&plan_id="+plan_id+"&batch="+batch+"'><img src=\"icons/statistik.jpg\" width'20' height='20'></a></td>");
						out.println("</tr>");
						}
					}
					//alle Ausgaben
					wert_relativ=db.getPlanungAllWhere(new Integer(plan_id),faktor,buildWhere(hf,db,"ausgabe",plan_id,""));
					summe=db.getKategorienAlleSummeWhere(formatter.format(hash_plan.get("startdatum")),akt_datum,buildWhere(hf,db,"ausgabe",plan_id,rule));
					 prozent=0.0;
						if ( wert_relativ ==0.0 )
						{
							prozent=0.0;
						}
						else
						{
						 prozent=(summe*100/wert_relativ);
						}
						if (wert_relativ !=0.0)
						{
						out.println("<tr>");
						out.println("<td>Alle Ausgaben</td><td>"
								+formater(wert_relativ)+"</td><td>"
								+formater(summe)+"</td><td>"
								+formaterProzentAusgabe(prozent)
								+ "</td><td><a href='planChart?kategorie=-1&plan_id="+plan_id+"&batch="+batch+"'><img src=\"icons/statistik.jpg\" width'20' height='20'></a></td>");
						out.println("</tr>");
						}
					out.println("</tr>");	
					out.println("</tbody>");
					out.println("</table>");
				//Einnahmen ---------------------------------------------	
					out.println("<h2>Einnahmen</h2>");
					out.println("<table border=\"1\"  bgcolor=\"#CCEECC\">");
					//out.println("<table border=\"1\">");
					out.println("<thead>");
					out.println("<tr>");
					out.println("<th>Kategorie</th>");
					out.println("<th>Geplanter Wert</th>");
					out.println("<th>Ist Wert</th>");
					out.println("<th>Erreicht in %</th>");
					out.println("<th>Graph</th>");
					out.println("</tr>");
					out.println("</thead>");
					out.println("<tbody>");
					
					for (int i=0; i<kat_ein.size();i++)
					{
						String kategorie=(String)((Hashtable)kat_ein.elementAt(i)).get("name");
					    wert_relativ=db.getKategorienAlleRecursivPlanung(kategorie,new Integer(plan_id),faktor);
						summe=db.getKategorienAlleRecursivSumme(kategorie,formatter.format(hash_plan.get("startdatum")),akt_datum,rule);
						prozent=0.0;
						if ( wert_relativ ==0.0 )
						{
							prozent=0.0;
						}
						else
						{
						 prozent=(summe*100/wert_relativ);
						}
						if (wert_relativ !=0.0)
						{
						out.println("<tr>");
						out.println("<td>"+kategorie+"</td><td>"+formater(wert_relativ)+"</td><td>"
								+formater(summe)+"</td><td>"+formaterProzentEinnahme(prozent)
								+ "</td><td><a href='planChart?kategorie="+(Integer)((Hashtable)kat_ein.elementAt(i)).get("id")
								+"&plan_id="+plan_id+"&batch="+batch+"'><img src=\"icons/statistik.jpg\" width'20' height='20'></a></td>");
						out.println("</tr>");
						}
					}
					
					//alle Einnahmen
					wert_relativ=db.getPlanungAllWhere(new Integer(plan_id),faktor,buildWhere(hf,db,"einnahme",plan_id,""));
					summe=db.getKategorienAlleSummeWhere(formatter.format(hash_plan.get("startdatum")),akt_datum,buildWhere(hf,db,"einnahme",plan_id,rule));
					 prozent=0.0;
						if ( wert_relativ ==0.0 )
						{
							prozent=0.0;
						}
						else
						{
						 prozent=(summe*100/wert_relativ);
						}
						if (wert_relativ !=0.0)
						{
						out.println("<tr>");
						out.println("<td>Alle Einnahmen</td><td>"
								+formater(wert_relativ)+"</td><td>"
								+formater(summe)+"</td><td>"
								+formaterProzentEinnahme(prozent)
								+ "</td><td><a href='planChart?kategorie=-2&plan_id="+plan_id+"&batch="+batch+"'><img src=\"icons/statistik.jpg\" width'20' height='20'></a></td>");
						out.println("</tr>");
						}
					out.println("</tr>");	
					out.println("</tbody>");
					out.println("</table>");
					
					
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
				Vector allIds = new Vector();
				for (int i=0;i<all.size();i++)
				{
					if(! db.getPlanWertNull(plan_id,(Integer)((Hashtable)all.elementAt(i)).get("id")))
					{
					allIds.add((Integer)((Hashtable)all.elementAt(i)).get("id"));
					hf.searchSub(allIds,(String)((Hashtable)all.elementAt(i)).get("name") , (Integer)((Hashtable)all.elementAt(i)).get("id"), all,0);
					}
					}
				//System.err.println(allIds);
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
				if (where.equals("()"))
					where="(kategorie= -1)";
						
				return where;
			}
	}
