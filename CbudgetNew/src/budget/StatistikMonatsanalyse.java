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

public class StatistikMonatsanalyse extends javax.servlet.http.HttpServlet {

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
			HeaderFooter hf = new HeaderFooter();
			// String akt_konto=(String)session.getAttribute("akt_konto");

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String akt_datum = formatter.format(cal.getTime());
			cal.set(Calendar.DAY_OF_YEAR, 1);
			String start_datum = formatter.format(cal.getTime());
			DB db = (DB)session.getAttribute("db"); 
			String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
			{
				RequestDispatcher rd;
				rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
				rd.forward(request, response);
				return;
			}	
			Hashtable settings = (Hashtable) session.getAttribute("settings");
			String mode = request.getParameter("mode");
			if (mode == null) {
				mode = "";
			}

			String startdatum = request.getParameter("startdatum");
			if (startdatum==null)
				if (settings.containsKey("MonatStartdatum"))
				{
					startdatum=(String)settings.get("MonatStartdatum");
					//System.err.println("Stardatum = "+startdatum);
				}
				else
				{							
				startdatum=start_datum;
				
				}
			settings.put("MonatStartdatum",startdatum);
			db.updatesetting("MonatStartdatum",startdatum);
			String enddatum = request.getParameter("enddatum");
			if (enddatum==null)
				if (settings.containsKey("MonatEnddatum"))
				{
					enddatum=(String)settings.get("MonatEnddatum");
				}
				else
				{
					enddatum=akt_datum;
					
				}
			settings.put("MonatEnddatum",enddatum);
			db.updatesetting("MonatEnddatum",enddatum);
			String str_kategorie = request.getParameter("kategorie");
			if (str_kategorie==null)
			{
				if (settings.containsKey("MonatKategorie"))
				{
					str_kategorie=(String)settings.get("MonatKategorie");
				}
				else
				{
					str_kategorie="alleAusgaben";
					
				}
				
			}
			settings.put("MonatKategorie",str_kategorie);
			db.updatesetting("MonatKategorie",str_kategorie);
			String rule_id=request.getParameter("rule_id");
			if (rule_id==null)
			{
				if (settings.containsKey("MonatRuleId"))
				{
					rule_id=(String)settings.get("MonatRuleId");
				}
				else
				{
					rule_id="";
					
				}					
			}
			settings.put("MonatRuleId",rule_id);
			db.updatesetting("MonatRuleId",rule_id);
			String str_mode = request.getParameter("mode");
			if (str_mode == null) {
				// System.out.println("Setze Konto");
				str_mode = "";
			}
			String str_auswert = request.getParameter("auswert");
			if (str_auswert == null) {
				// System.out.println("Setze Konto");
				str_auswert = "";
			}

			Vector<?> kategorien = db.getAllKategorien();
			Vector rules=db.onlyValidRules(db.getAllRules());

			out.println("<html>");
			out.println("<head>");
			out.println(" <title>Statistik</title>");
			out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
			out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
			out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
			out.println("</head>");

			out.println("<body  bgcolor=\"#EEFFBB\">");
			// Vector vec=(Vector)session.getAttribute("kategorien");
			hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
			hf.writeStatisticHeader(out);
			out.println("<h1>Monatsverlauf: Bitte Auswertekriterien eingeben</h1>");
			out.println("<table>");
			out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
			out.println("<form action=auswertungmonat?mode=monat method=post>");
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
			out.println("Start Datum:<br><input id=\"dob1\" name=\"startdatum\" value=\""
					+ startdatum
					+ "\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""
					+ akt_datum
					+ "\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
			out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
			out.println("</div>");
			out.println("<p>");
			out.println("End Datum:<br><input id=\"dob2\" name=\"enddatum\"  value=\""
					+ enddatum
					+ "\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""
					+ akt_datum
					+ "\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
			out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
			out.println("</div>");
			out.println("<p>");
			out.println("Kategorie: <select name=\"kategorie\" size=\"7\">");
			if (str_kategorie.equals("alleAusgaben")) {
				out.println("<option selected>alleAusgaben</option>");
			} else {
				out.println("<option>alleAusgaben</option>");
			}
			if (str_kategorie.equals("alleEinnahmen")) {
				out.println("<option selected>alleEinnahmen</option>");
			} else {
				out.println("<option>alleEinnahmen</option>");
			}
			select = "";
			for (int i = 0; i < kategorien.size(); i++) {
				// System.out.println("Select: "+((Hashtable)kat.elementAt(i)).get("name"));
				if (((Hashtable<?, ?>) kategorien.elementAt(i)).get("name").equals(
						str_kategorie)) {
					select = " selected";
				} else {
					select = "";
				}
				out.println("<option" + select + ">"
						+ ((Hashtable<?, ?>) kategorien.elementAt(i)).get("name")
						+ "</option>");
			}
			out.println("</select>");
			out.println("<p>");
			if (str_auswert.equals("monat")||str_auswert.equals(""))
			{
				select="checked";
			}
			else
			{
				select="";
			}
			out.println("<input type=\"radio\" name=\"auswert\" value=\"monat\" "+select+"> Monatlich auswerten <br>");
			if (str_auswert.equals("jahr"))
			{
				select="checked";
			}
			else
			{
				select="";
			}
			out.println("<input type=\"radio\" name=\"auswert\" value=\"jahr\" "+select+"> Jährlich auswerten <br>");
			if (str_auswert.equals("einausmonat"))
			{
				select="checked";
			}
			else
			{
				select="";
			}
			out.println("<input type=\"radio\" name=\"auswert\" value=\"einausmonat\" "+select+">Ein- Ausgabenvergleich monatlich <br>");
			if (str_auswert.equals("einausjahr"))
			{
				select="checked";
			}
			else
			{
				select="";
			}
			out.println("<input type=\"radio\" name=\"auswert\" value=\"einausjahr\" "+select+" >Ein- Ausgabenvergleich jährlich<br>");
		    out.println("<p>");
			out.println("<input type=\"hidden\" name=\"mode\" value=\"monat\">");
			out.println("<input type=\"submit\" value=\"Absenden\";>");
			out.println("</form>");
			out.println("</td><td>");

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
			
			if (str_auswert.equals("monat")) {
			//Monatsverlauf
				Calendar cal_begin = Calendar.getInstance();
				cal_begin.setTime(formatter.parse(startdatum));
				Calendar cal_end = Calendar.getInstance();
				cal_end.setTime(formatter.parse(enddatum));
				cal=(Calendar)cal_begin.clone();
				double anz=1.0;
				double gesamt=0.0;
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				Double summe=0.0;
				if (str_kategorie.equals("alleAusgaben"))
				{
					summe=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
				}
				else
				{ if(str_kategorie.equals("alleEinnahmen"))
				{
					summe=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
				}
				else
				{
				summe=db.getKategorienAlleRecursivSumme(str_kategorie, formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
				}
				}
				Vector<Hashtable<String, Comparable>> vec = new Vector<Hashtable<String, Comparable>>();
				Hashtable<String, Comparable> hash = new Hashtable<String, Comparable>();
				if (summe<0){summe=summe*-1;}
				hash.put("wert",summe);
				hash.put("mode","ausgabe");
				hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
				vec.addElement(hash);
				gesamt=+summe;
				//System.out.println("Bevor while");
				while (cal.before(cal_end))
				{
					anz+=1.0;
					//System.out.println("In while "+ formatter.format(cal.getTime()));
					cal_begin.add(Calendar.MONTH, 1);
					cal_begin.set(Calendar.DAY_OF_MONTH, 1);
					cal=(Calendar)cal_begin.clone();
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					if (str_kategorie.equals("alleAusgaben"))
					{
						summe=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					}
					else
					{ if(str_kategorie.equals("alleEinnahmen"))
					{
						summe=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					}
					else
					{
					summe=db.getKategorienAlleRecursivSumme(str_kategorie, formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					}
					}
					if (summe<0){summe=summe*-1;}
					hash = new Hashtable<String, Comparable>();
					hash.put("wert",summe);
					hash.put("mode","ausgabe");
					//System.out.println("Month"+cal.get(Calendar.MONTH));
					hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					gesamt+=summe;
					
				}
				// System.out.println(vec);
					session.setAttribute("chart_vec", vec);

				out.println("<img src=chart?mode=monat width'800' height='400'>");
				out.println("<p>Durchschnitt pro Monat: "+formater(gesamt/anz));

			}//str_auswert.equals("monat")
			if (str_auswert.equals("jahr")) {
				//Jahressverlauf
					Calendar cal_begin = Calendar.getInstance();
					cal_begin.setTime(formatter.parse(startdatum));
					Calendar cal_end = Calendar.getInstance();
					cal_end.setTime(formatter.parse(enddatum));
					cal=(Calendar)cal_begin.clone();
					cal.set(Calendar.MONTH,11);
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					Double summe=0.0;
					double anz=1.0;
					double gesamt=0.0;
					if (str_kategorie.equals("alleAusgaben"))
					{
						summe=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					}
					else
					{ if(str_kategorie.equals("alleEinnahmen"))
					{
						summe=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					}
					else
					{
					summe=db.getKategorienAlleRecursivSumme(str_kategorie, formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					}
					}
					Vector<Hashtable<String, Comparable>> vec = new Vector<Hashtable<String, Comparable>>();
					Hashtable<String, Comparable> hash = new Hashtable<String, Comparable>();
					if (summe<0){summe=summe*-1;}
					hash.put("wert",summe);
					hash.put("mode","ausgabe");
					hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					gesamt=+summe;
					//System.out.println("Bevor while");
					while (cal.before(cal_end))
					{
						anz+=1.0;
						//System.out.println("In while "+ formatter.format(cal.getTime()));
						cal_begin.add(Calendar.YEAR, 1);
						cal_begin.set(Calendar.DAY_OF_YEAR, 1);
						cal=(Calendar)cal_begin.clone();
						cal.set(Calendar.MONTH,11);
						cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
						if (str_kategorie.equals("alleAusgaben"))
						{
							summe=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
						}
						else
						{ if(str_kategorie.equals("alleEinnahmen"))
						{
							summe=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
						}
						else
						{
						summe=db.getKategorienAlleRecursivSumme(str_kategorie, formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
						}
						}
						if (summe<0){summe=summe*-1;}
						hash = new Hashtable<String, Comparable>();
						hash.put("wert",summe);
						hash.put("mode","ausgabe");
						//System.out.println("Month"+cal.get(Calendar.MONTH));
						hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
						vec.addElement(hash);
						gesamt+=summe;
						
					}
					// System.out.println(vec);
						session.setAttribute("chart_vec", vec);

					out.println("<img src=chart?mode=monat width'800' height='400'>");
					out.println("<p>Durchschnitt pro Jahr: "+formater(gesamt/anz));
				}//str_auswert.equals("jahr")
			if (str_auswert.equals("einausmonat")) {
				//Monatsverlauf
					Calendar cal_begin = Calendar.getInstance();
					cal_begin.setTime(formatter.parse(startdatum));
					Calendar cal_end = Calendar.getInstance();
					cal_end.setTime(formatter.parse(enddatum));
					cal=(Calendar)cal_begin.clone();
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					Double summe_aus=0.0;
					Double summe_ein=0.0;
					double anz=1.0;
					double gesamt=0.0;
						summe_aus=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					Vector<Hashtable<String, Comparable>> vec = new Vector<Hashtable<String, Comparable>>();
					Hashtable<String, Comparable> hash = new Hashtable<String, Comparable>();
					if (summe_aus<0){summe_aus=summe_aus*-1;}
					hash.put("wert",summe_aus);
					hash.put("mode","Ausgabe");
					hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					
					
					summe_ein=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					if (summe_ein<0){summe_ein=summe_ein*-1;}
					hash = new Hashtable<String, Comparable>();
					hash.put("wert",summe_ein);
					hash.put("mode","Einnahme");
					hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					hash = new Hashtable<String, Comparable>();
					hash.put("wert",summe_ein-summe_aus);
					hash.put("mode","Überschuss");
					hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					gesamt+=(summe_ein-summe_aus);
					//System.out.println("Bevor while");
					while (cal.before(cal_end))
					{
						anz+=1.0;
						//System.out.println("In while "+ formatter.format(cal.getTime()));
						cal_begin.add(Calendar.MONTH, 1);
						cal_begin.set(Calendar.DAY_OF_MONTH, 1);
						cal=(Calendar)cal_begin.clone();
						cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
						
							summe_aus=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
						if (summe_aus<0){summe_aus=summe_aus*-1;}
						hash = new Hashtable<String, Comparable>();
						hash.put("wert",summe_aus);
						hash.put("mode","Ausgabe");
						//System.out.println("Month"+cal.get(Calendar.MONTH));
						hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
						vec.addElement(hash);
						summe_ein=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
						hash = new Hashtable<String, Comparable>();
						hash.put("wert",summe_ein);
						hash.put("mode","Einnahme");
						//System.out.println("Month"+cal.get(Calendar.MONTH));
						hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
						vec.addElement(hash);
						hash = new Hashtable<String, Comparable>();
						hash.put("wert",summe_ein-summe_aus);
						hash.put("mode","Überschuss");
						//System.out.println("Month"+cal.get(Calendar.MONTH));
						hash.put("Monat",new Integer(cal_begin.get(Calendar.MONTH)+1).toString()+"/"+new Integer(cal_begin.get(Calendar.YEAR)).toString());
						vec.addElement(hash);
						gesamt+=(summe_ein-summe_aus);
					}
					// System.out.println(vec);
						session.setAttribute("chart_vec", vec);

					out.println("<img src=chart?mode=monat width'800' height='400'>");
					out.println("<p>Überschuss Durchschnitt pro Monat: "+formater(gesamt/anz));
				}//str_auswert.equals("einausmonat")
			if (str_auswert.equals("einausjahr")) {
				//Monatsverlauf
					Calendar cal_begin = Calendar.getInstance();
					cal_begin.setTime(formatter.parse(startdatum));
					Calendar cal_end = Calendar.getInstance();
					cal_end.setTime(formatter.parse(enddatum));
					cal=(Calendar)cal_begin.clone();
					cal.set(Calendar.MONTH,11);
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					Double summe_aus=0.0;
					Double summe_ein=0.0;
					double anz=1.0;
					double gesamt=0.0;
						summe_aus=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					Vector<Hashtable<String, Comparable>> vec = new Vector<Hashtable<String, Comparable>>();
					Hashtable<String, Comparable> hash = new Hashtable<String, Comparable>();
					if (summe_aus<0){summe_aus=summe_aus*-1;}
					hash.put("wert",summe_aus);
					hash.put("mode","Ausgabe");
					hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					summe_ein=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					if (summe_ein<0){summe_ein=summe_ein*-1;}
					hash = new Hashtable<String, Comparable>();
					hash.put("wert",summe_ein);
					hash.put("mode","Einnahme");
					hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					hash = new Hashtable<String, Comparable>();
					hash.put("wert",summe_ein-summe_aus);
					hash.put("mode","Überschuss");
					hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					gesamt+=(summe_ein-summe_aus);
					while (cal.before(cal_end))
					{
						anz+=1.0;
						//System.out.println("In while "+ formatter.format(cal.getTime()));
						cal_begin.add(Calendar.YEAR, 1);
						cal_begin.set(Calendar.DAY_OF_YEAR, 1);
						cal=(Calendar)cal_begin.clone();
						cal.set(Calendar.MONTH,11);
						cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
						
							summe_aus=getSumme(db,"ausgabe", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
						if (summe_aus<0){summe_aus=summe_aus*-1;}
						hash = new Hashtable<String, Comparable>();
						hash.put("wert",summe_aus);
						hash.put("mode","Ausgabe");
						//System.out.println("Month"+cal.get(Calendar.MONTH));
						hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
						vec.addElement(hash);

						summe_ein=getSumme(db,"einnahme", formatter.format(cal_begin.getTime()), formatter.format(cal.getTime()),rule);
					if (summe_ein<0){summe_ein=summe_ein*-1;}
					hash = new Hashtable<String, Comparable>();
					hash.put("wert",summe_ein);
					hash.put("mode","Einnahme");
					//System.out.println("Month"+cal.get(Calendar.MONTH));
					hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					hash = new Hashtable<String, Comparable>();
					hash.put("wert",summe_ein-summe_aus);
					hash.put("mode","Überschuss");
					//System.out.println("Month"+cal.get(Calendar.MONTH));
					hash.put("Monat",new Integer(cal_begin.get(Calendar.YEAR)).toString());
					vec.addElement(hash);
					gesamt+=(summe_ein-summe_aus);
						
					}
					// System.out.println(vec);
						session.setAttribute("chart_vec", vec);

					out.println("<img src=chart?mode=monat width'800' height='400'>");
					out.println("<p>Überschuss Durchschnitt pro Jahr: "+formater(gesamt/anz));
				}//str_auswert.equals("einausjahr")
			out.println("</td>");
			out.println("</tr>");
			out.println("</tbody>");
			out.println("</table>");
			out.println("</td></tr>");

			out.println("</table>");
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (Throwable theException) {
			theException.printStackTrace();
		}

	}

	private String formater(Double d) {
		String str = "";
		DecimalFormat f = new DecimalFormat("#0.00");
		if (d.doubleValue() < 0) {
			str = "<font color=\"red\">";
		} else {
			str = "<font color=\"green\">";
		}

		str = str + f.format(d);
		str = str + "</font>";
		return str;
	}

	private Vector<?> replaceIdWithName(Vector<?> katGroup, DB db) {
		for (int i = 0; i < katGroup.size(); i++) {
			Integer kat_id = (Integer) ((Hashtable<?, ?>) katGroup.elementAt(i))
					.get("kategorie");
			String name = db.getKategorieName(kat_id);

			((Hashtable<String, String>) katGroup.elementAt(i)).put("name", name);
		}
		return katGroup;
	}

	private double getSumme(DB db,String mode,String startdate, String enddate,String rule)
	{
		
		Vector	vec=db.getAllKategorien(mode);
		String where = "(";
		boolean first=true;
		for (int i=0;i<vec.size();i++){
			if (! first)
			{
				where=where+" or ";
			}
			else
			{
				first=false;
			}
			
			where =where+" kategorie= "+((Hashtable)vec.elementAt(i)).get("id");
		}
		where=where +")"+rule;
		return db.getKategorienAlleSummeWhere(startdate,enddate,where);
		
	}
}
