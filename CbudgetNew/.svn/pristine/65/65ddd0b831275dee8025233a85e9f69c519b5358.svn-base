	package budget;

	import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
	import java.util.Hashtable;
	import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class StatistikKategorien extends javax.servlet.http.HttpServlet {

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
					//String akt_konto=(String)session.getAttribute("akt_konto");
					
					Calendar cal= Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String akt_datum=formatter.format(cal.getTime());
					cal.set(Calendar.DAY_OF_YEAR,1);
					String start_datum=formatter.format(cal.getTime());					
					DB db = (DB)session.getAttribute("db"); 
					String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
					{
						RequestDispatcher rd;
						rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
						rd.forward(request, response);
						return;
					}			
					
					Hashtable settings = (Hashtable) session.getAttribute("settings");
					String mode=request.getParameter("mode");
					String startdatum=request.getParameter("startdatum");
					//System.err.println("Stardatum = "+startdatum);
					//System.err.println(settings);
					if (startdatum==null)
						if (settings.containsKey("kategorieStartdatum"))
						{
							startdatum=(String)settings.get("kategorieStartdatum");
							System.err.println("Stardatum = "+startdatum);
						}
						else
						{							
						startdatum=start_datum;
						
						}
					settings.put("kategorieStartdatum",startdatum);
					db.updatesetting("kategorieStartdatum",startdatum);
					String enddatum=request.getParameter("enddatum");
					if (enddatum==null)
						if (settings.containsKey("kategorieEnddatum"))
						{
							enddatum=(String)settings.get("kategorieEnddatum");
						}
						else
						{
							enddatum=akt_datum;
							
						}
					settings.put("kategorieEnddatum",enddatum);
					db.updatesetting("kategorieEnddatum",enddatum);
					String kategorie=request.getParameter("kategorie");
					if (kategorie==null)
					{
						if (settings.containsKey("kategorieKategorie"))
						{
							kategorie=(String)settings.get("kategorieKategorie");
						}
						else
						{
							kategorie="alleAusgaben";
							
						}
						
					}
					settings.put("kategorieKategorie",kategorie);
					db.updatesetting("kategorieKategorie",kategorie);
					String rule_id=request.getParameter("rule_id");
					if (rule_id==null)
					{
						if (settings.containsKey("kategorieRuleId"))
						{
							rule_id=(String)settings.get("kategorieRuleId");
						}
						else
						{
							rule_id="";
							
						}					
					}
					settings.put("kategorieRuleId",rule_id);
					db.updatesetting("kategorieRuleId",rule_id);
					String sub=request.getParameter("art");
					if (sub==null)
					{
						sub="haupt";
					}
					session.setAttribute("settings",settings);
					Vector kat=db.getAllKategorien();
					Vector rules=db.getAllRules();
					
					out.println("<html>");
					out.println("<head>");
					out.println(" <title>Statistik</title>");
					out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
					out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
					out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
					out.println("</head>");

					out.println("<body  bgcolor=\"#EEFFBB\">");
					//Vector vec=(Vector)session.getAttribute("kategorien"); 
					Vector konten=(Vector)session.getAttribute("konten");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					hf.writeStatisticHeader(out);
					out.println("<h1>Kategorien: Bitte Auswertekriterien eingeben</h1>");
					out.println("<table>");
					out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
					out.println("<form action=auswertung method=post>");
					out.println("Regel: <select name=\"rule_id\" size=\"1\">");
					//out.println("<option>   </option>");
					String select="";
					if (rule_id.equals("-1"))
					{
						select=" selected";
					}
					else
					{
						select="";
					}
					out.println("<option"+select+" value=\"-1\"> </option>");
					
					for (int i=0;i<rules.size();i++)
					{
						//System.out.println("RULE_ID: "+((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString());
						//System.out.println("RULE_ID_: "+rule_id);
						if (((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString().equals(rule_id))
						{
							select=" selected";
						}
						else
						{
							select="";
						}
						out.println("<option"+select+" value=\""+ ((Hashtable)rules.elementAt(i)).get("rule_id") +"\">"+((Hashtable)rules.elementAt(i)).get("name")+ "</option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("Start Datum:<br><input id=\"dob1\" name=\"startdatum\" value=\""+startdatum+"\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+akt_datum+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					out.println("<p>");
					out.println("End Datum:<br><input id=\"dob2\" name=\"enddatum\"  value=\""+enddatum+"\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+akt_datum+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					out.println("<p>");
					String checked="";
					if(sub.equals("haupt"))
					{
						checked="checked";
					}
					out.println("<input type=\"radio\" name=\"art\" value=\"haupt\""+checked+"> Nur Hauptkategorien <br>");
					checked="";
					if(sub.equals("sub"))
					{
						checked="checked";
					}
					out.println("<input type=\"radio\" name=\"art\" value=\"sub\""+checked+"> Alle Kategorien <br>");
					out.println("Kategorie: <select name=\"kategorie\" size=\"7\">");
					//out.println("<option>   </option>");
					if (kategorie.equals("alleAusgaben"))
					{
					out.println("<option selected>alleAusgaben</option>");
					}
					else
					{
						out.println("<option>alleAusgaben</option>");
					}
					if (kategorie.equals("alleEinnahmen"))
					{
					out.println("<option selected>alleEinnahmen</option>");
					}
					else
					{
						out.println("<option>alleEinnahmen</option>");
					}	
					select="";
					for (int i=0;i<kat.size();i++)
					{
						//System.out.println("Select: "+((Hashtable)kat.elementAt(i)).get("name"));
						if (((Hashtable)kat.elementAt(i)).get("name").equals(kategorie))
						{
							select=" selected";
						}
						else
						{
							select="";
						}
						out.println("<option"+select+">"+((Hashtable)kat.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<input type=\"hidden\" name=\"mode\" value=\"kategorie\">");
					out.println("<input type=\"submit\" value=\"Absenden\";>");
					out.println("</form>");
					out.println("</td><td>");
					Vector allkat=new Vector();
					Vector katGroup=new Vector();
					if (mode.equals("kategorie"))
					{
						String wherestring="";
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
						boolean issub=false;
						if (sub.equals("sub"))
						{
							issub=true;
						}
							
						
						if (kategorie.equals("alleAusgaben"))
						{
							
							allkat=db.getKategorienAlleAusgaben("ausgabe",issub);
						}
						else
							if (kategorie.equals("alleEinnahmen"))
							{
								allkat=db.getKategorienAlleAusgaben("einnahme",issub);
							}
							else
							{
								if (! issub)
								{
								allkat=db.getKategorienByName(kategorie);
								}
								else
								{
								db.getKategorienAlleRecursiv(kategorie,allkat);
								}
							}
						if (issub)
						{
							System.out.println("Nur haupt");
						boolean inhalt=false;
						for (int i=0; i<allkat.size();i++)
						{
							if (i==0)
							{
								wherestring=wherestring +" and (";
								inhalt=true;
							}
							else
							{
								wherestring=wherestring + " or ";
							}
							wherestring=wherestring + " kategorie = " + ((Hashtable)allkat.elementAt(i)).get("id") +' ';
						}
						wherestring=wherestring +")" +rule;
						
						
						katGroup=db.getKategorieGruppen(startdatum,enddatum,wherestring);
						katGroup=replaceIdWithName(katGroup,db);
						}
						else
						{
							for (int i=0;i<allkat.size();i++)
							{
							Hashtable hash = new Hashtable();
							hash.put("name",((Hashtable)allkat.elementAt(i)).get("name"));
							hash.put("wert",db.getKategorienAlleRecursivSumme((String)hash.get("name"),startdatum,enddatum,rule));
							
							katGroup.addElement(hash);
							}
						}
						//TODO !!!!!!!!!!!!!Besser machen
	                    					
						
						
						System.out.println(katGroup);
						
						session.setAttribute("chart_vec",katGroup);
						out.println("<img src=chart?mode=kat width'600' height='400'>");
					}
					out.println("</tr><tr>");
					out.println("<td></td>");
					out.println("<td>");
					out.println("<table border=\"1\"  bgcolor=\"#CCEECC\">");
					//out.println("<table border=\"1\">");
					out.println("<thead>");
					out.println("<tr>");
					out.println("<th>Nr.</th>");
					out.println("<th>Name der Kategorie</th>");
					out.println("<th>Wert</th>");
					out.println("<th>Prozent</th>");
					out.println("</tr>");
					out.println("</thead>");
					out.println("<tbody>");
					double summe=0.0;
					//Ermittle prozentualen Wert
					for (int i=0; i< katGroup.size();i++)
					{
						summe=summe+(Double)((Hashtable)katGroup.elementAt(i)).get("wert");
					}
					Vector sortedGroup = new Vector(); 
					//double min=-200000;
					for (int i=0; i< katGroup.size();i++)
					{
						Double prozent=((Double)((Hashtable)katGroup.elementAt(i)).get("wert")/summe)*100;
						((Hashtable)katGroup.elementAt(i)).put("prozent", prozent);
						int j=0;
						if ( i==0)
						{
							sortedGroup.addElement( (Hashtable)katGroup.elementAt(i));	
						}
						else
						{
						while (  j < sortedGroup.size() && (prozent < ((Double)((Hashtable)sortedGroup.elementAt(j)).get("prozent"))))
						{
							j++;
						}
						
						sortedGroup.insertElementAt(  (Hashtable)katGroup.elementAt(i)  , j);
						}
					}
					katGroup=sortedGroup;
					
					for (int i=0; i< katGroup.size();i++)
					{
					out.println("<tr>");
					out.println("<td>"+i+"<td>"+((Hashtable)katGroup.elementAt(i)).get("name")+"</td><td>"+formater((Double)((Hashtable)katGroup.elementAt(i)).get("wert"))+"</td><td>"+formater((Double)((Hashtable)katGroup.elementAt(i)).get("prozent"))+"%</td>") ;
					out.println("</tr>");
					}
					out.println("<tr>");
					out.println("<td></td><td><font size=\"5\">Summe:</font></td><td>"+formater(summe)+"</td><td>"+formater(100.0)+"%</td>");
					out.println("</tbody>");
					out.println("</table>");
					out.println("<td></tr>");
					
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
				if (d.doubleValue()<0)
				{
					str="<font color=\"red\">";
				}
				else
				{
					str="<font color=\"green\">";
				}
				
				str=str+f.format(d);
				str=str+"</font>";
				return str;
			}
			private Vector replaceIdWithName(Vector katGroup,DB db)
			{
				for (int i=0;i<katGroup.size();i++)
				{
					Integer kat_id= (Integer)((Hashtable)katGroup.elementAt(i)).get("kategorie");
					String name=db.getKategorieName(kat_id);
					
					((Hashtable)katGroup.elementAt(i)).put("name",name);
				}
				return katGroup;
			}
		
	}
