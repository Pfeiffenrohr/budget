	package budget;

	import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
	import java.util.Hashtable;
	import java.util.Vector;
	import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class Transaktionen extends javax.servlet.http.HttpServlet {

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
					if (mode==null)
						mode="";
					String startdatum=request.getParameter("startdatum");
					if (startdatum==null)
						if (settings.containsKey("transaktionStartdatum"))
						{
							startdatum=(String)settings.get("transaktionStartdatum");
							//System.err.println("Stardatum = "+startdatum);
						}
						else
						{							
						startdatum=start_datum;
						
						}
					settings.put("transaktionStartdatum",startdatum);
					db.updatesetting("transaktionStartdatum",startdatum);
					String enddatum=request.getParameter("enddatum");
					if (enddatum==null)
						if (settings.containsKey("transaktionEnddatum"))
						{
							enddatum=(String)settings.get("transaktionEnddatum");
						}
						else
						{
							enddatum=akt_datum;
							
						}
					settings.put("transaktionEnddatum",enddatum);
					db.updatesetting("transaktionEnddatum",enddatum);
					String kategorie=request.getParameter("kategorie");
					//System.err.println("Kategorie "+kategorie);
					if (kategorie==null)
					{
						if (settings.containsKey("transaktionKategorie"))
						{
							kategorie=(String)settings.get("transaktionKategorie");
						}
						else
						{
							kategorie="";
							
						}
						
					}
					settings.put("transaktionKategorie",kategorie);
					db.updatesetting("transaktionKategorie",kategorie);
					String konto=request.getParameter("konto");
					if (konto==null)
					{
						if (settings.containsKey("transaktionKonto"))
						{
							konto=(String)settings.get("transaktionKonto");
						}
						else
						{
							konto="alleAusgaben";
							
						}
						
					}
					settings.put("transaktionKonto",konto);
					db.updatesetting("transaktionKonto",konto);
					
					String name=request.getParameter("name");
					if (name==null)
					{
						if (settings.containsKey("transaktionName"))
						{
							name=(String)settings.get("transaktionName");
						}
						else
						{
							name="";
							
						}
						
					}
					settings.put("transaktionName",name);
					db.updatesetting("transaktionName",name);
					String rule_id=request.getParameter("rule_id");
					if (rule_id==null)
					{
						if (settings.containsKey("transaktionRuleId"))
						{
							rule_id=(String)settings.get("transaktionRuleId");
						}
						else
						{
							rule_id="";
							
						}					
					}
					settings.put("transaktionRuleId",rule_id);
					db.updatesetting("transaktionRuleId",rule_id);
					
					
					Vector kat=db.getAllActiveKategorien();
					Vector rules=db.onlyValidRules(db.getAllRules());
					
					out.println("<html>");
					out.println("<head>");
					out.println(" <title>Transaktionen</title>");
					out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
					out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
					out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
					out.println("</head>");

					out.println("<body  bgcolor=\"#EEFFBB\">");
					//Vector vec=(Vector)session.getAttribute("kategorien"); 
					
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Bitte Filterkriterien eingeben</h1>");
					out.println("<table>");
					out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
					out.println("<form action=transaktionen method=post>");
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
					out.println("Start Datum:<br><input id=\"dob1\" name=\"startdatum\" value=\""+startdatum+"\" size=\"10\" maxlength=\"10\" type=\"text\" ><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					out.println("<p>");
					out.println("End Datum:<br><input id=\"dob2\" name=\"enddatum\"  value=\""+enddatum+"\" size=\"10\" maxlength=\"10\" type=\"text\" ><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					out.println("<p>");
					//String checked="";
					out.println("<p>Name: <br><input name=\"name\" type=\"text\" size=\"40\" value=\""+name+"\"maxlength=\"50\"></p>");
					out.println("<p>");
					Vector kategorien=db.getAllActiveKategorien();
					out.println("Kategorie: <select name=\"kategorie\" size=\"7\">");
					//out.println("<option>   </option>");
					out.println("<option></option>");
					for (int i=0;i<kategorien.size();i++)
					{
						String sel;
						if (kategorie.equals(((Hashtable)kategorien.elementAt(i)).get("name")))
						{
							sel="selected";
						}
						else
						{
							sel="";
						}
						out.println("<option "+sel+">"+((Hashtable)kategorien.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("<p>");
				
					Vector konten=db.getAllKonto();
					out.println("Konto: <select name=\"konto\" size=\"7\">");
					//out.println("<option>   </option>");
					out.println("<option></option>");
					for (int i=0;i<konten.size();i++)
					{
						String sel;
						if (konto.equals(((Hashtable)konten.elementAt(i)).get("name")))
						{
							sel="selected";
						}
						else
						{
							sel="";
						}
						out.println("<option "+sel+">"+((Hashtable)konten.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("<input type=\"hidden\" name=\"mode\" value=\"auswertung\">");
					out.println("<p>");
					out.println("<input type=\"submit\" value=\"Absenden\" >");
					out.println("</form>");
					out.println("</td>");
					if (mode.equals("auswertung"))
					{
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
						//Wherestring zusammenbasteln
						String where="";
						boolean first=true;
						if (!startdatum.equals(""))
						{
							where=where+" datum >= to_date('"+startdatum+"','YYYY-MM-DD')";
							first=false;
					    }
						if (!enddatum.equals(""))
						{
							if (!first)
							{
								where=where +" and ";
							}
							where=where+" datum <= to_date('"+enddatum+"','YYYY-MM-DD')";
							first=false;
						}
						if (!name.equals(""))
						{
							if (!first)
							{
								where=where +" and ";
							}
							where=where+" name like '%"+name+"%' ";
							first=false;
						}
						if (!konto.equals(""))
						{
							if (!first)
							{
								where=where +" and ";
							}
							where=where+" konto_id=(select id from konten where kontoname='"+konto+"') ";
							first=false;
						}
						if (!kategorie.equals(""))
						{
							if (!first)
							{
								where=where +" and ";
							}
							where=where+" kategorie=(select id from kategorien where name='"+kategorie+"') ";
							first=false;
						}
						if (!first)
						{
							where=" where "+where;
						}
						where=where +rule+" order by datum";
						System.out.println(where);
						
						Vector trans= db.getAllTransaktionenWithWhere(where);
						session.setAttribute("konten", konten);
						session.setAttribute("kategorien",kategorien);
						//session.setAttribute("akt_konto",konto);
						session.setAttribute("transaktionen",trans);
						out.println("<td>");
						Double summe=0.0;
						String transzeile="";
						for (int i=0; i<trans.size();i++)
						{
						Double wert=(Double)((Hashtable)trans.elementAt(i)).get("wert");
						summe=summe+wert;
						transzeile=transzeile+"<tr>";
						String bgcolor=">";
						if (((String)((Hashtable)trans.elementAt(i)).get("planed")).equals("j"))
								{
							bgcolor=" bgcolor=\"#CCCCCC\">";
								}
						transzeile=transzeile+"<td"+bgcolor+"<input type=\"checkbox\" name=\"loeschen\" value=\""+new Integer(i).toString()+"\"></td>";
						transzeile=transzeile+"\n<td"+bgcolor+((Hashtable)trans.elementAt(i)).get("name")+"</td>";
						String color;
						if (ispositiv(wert))
							{
							color="green";
							}
						else
						{
							color="red";
						}
						transzeile=transzeile+"\n<td"+bgcolor+"<font color=\""+color+"\">"+wert.toString()+"</font></td>";
						
					//Kontostand
						transzeile=transzeile+"\n<td"+bgcolor+calcKonto((Integer)((Hashtable)trans.elementAt(i)).get("konto"),konten)+"</td>";
						transzeile=transzeile+"\n<td"+bgcolor+calcKategorie((Integer)((Hashtable)trans.elementAt(i)).get("kategorie"),kategorien)+"</td>";
						transzeile=transzeile+"\n<td"+bgcolor+"<a href=neueZyklischeTransaktion?vec_id="+i+"&trans="+(Integer)((Hashtable)trans.elementAt(i)).get("id")+"&kor_id="+(Integer)((Hashtable)trans.elementAt(i)).get("kor_id")+">"+chooseIcon((Integer)((Hashtable)trans.elementAt(i)).get("kor_id"),(Integer)((Hashtable)trans.elementAt(i)).get("cycle"))+"</a></td>";
						transzeile=transzeile+"\n<td"+bgcolor+formatDatum((Date)((Hashtable)trans.elementAt(i)).get("datum"))+"</td>";
						transzeile=transzeile+"</tr>";	
						}
						transzeile=transzeile+"<tr><td></td><td>Summe</td><td>"+formater(summe)+"</td><td>-</td><td>-</td><td>-</td><td>-</td></tr>"; 
						out.println("<table cellpadding=\"0\" cellspacing=\"4\" width=\"114\" border=\"0\">");
						out.println("<tr><td>");
						out.println("</td>");
						out.println("<td>");
						out.println("<form action=\"transaktionAendern\">");
						out.println("<input type=\"submit\" name=\"Text2\" value=\"Transaktion ändern/löschen\">");
						out.println("</td></tr>");
						out.println("</table>");
						
						out.println("</p>");
						
						out.println("<table border=\"1\"  bgcolor=\"#CCEECC\">");
						//out.println("<table border=\"1\">");
						out.println("<thead>");
						out.println("<tr>");
						out.println("<th></th>");
						out.println("<th>Name der ausgeführeten Transaktion</th>");
						out.println("<th>Buchungsbetrag</th>");
						out.println("<th>Konto</th>");
						out.println("<th>Kategorie</th>");
						out.println("<th>Typ</th>");
						out.println("<th>Datum</th>");
						out.println("</tr>");
						out.println("</thead>");
						out.println("<tbody>");
						
						out.println(transzeile);
						out.println("</tr>");	
						out.println("</tbody>");
						out.println("</table>");
						out.println("<font size=\"1\">Anzalhl gefundener Transaktionen: "+trans.size()+"</font>");
						out.println("</form>");
					}
					out.println("</td>");
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
			private boolean ispositiv(Double wert)
			{
				if (wert>0)
				{
					return true;
				}
				else
					return false;
			}
			private String calcKategorie(Integer id ,Vector allKat)
			{
			  for (int i=0;i<allKat.size();i++)
			  {
				  if (id.intValue()==((Integer)((Hashtable)allKat.elementAt(i)).get("id")).intValue())
				  {
					  return (String)((Hashtable)allKat.elementAt(i)).get("name");
				  }
			  }
			  return "keine Kategorie";
			}
			private String calcKonto(Integer id ,Vector allKat)
			{
			  for (int i=0;i<allKat.size();i++)
			  {
				  if (id.intValue()==((Integer)((Hashtable)allKat.elementAt(i)).get("id")).intValue())
				  {
					  return (String)((Hashtable)allKat.elementAt(i)).get("name");
				  }
			  }
			  return "kein Konto";
			}
			private String chooseIcon(Integer id,Integer cycle)
			{
				if (id.intValue()==0)
				{
					return "<img src=\"icons/finished_transaction.png\" width'20' height='20'>";
				}
				else
				{
					if (cycle.intValue()==0)
					{
					return "<img src=\"icons/finished_transferal_target_transaction.png\" width'20' height='20'>";
					}
					else
					{
						if (cycle.intValue()==1)
						{
							return "<img src=\"icons/planned_transferal_source_transaction.png\" width'20' height='20'>";
						}
						else
						{
							return "<img src=\"icons/planned_transferal_target_transaction.png\" width'20' height='20'>";
						}
					}
					}
			}
			private String formatDatum(Date datum)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("E dd.MM.yyyy");
				Calendar cal= Calendar.getInstance();
				cal.setTime(datum);
				System.out.println(" Cal = " + formatter.format(cal.getTime()));
				return formatter.format(cal.getTime());
			}
	}
