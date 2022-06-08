package budget.statistik;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;
import cbudgetbase.DB;

public class RenditeUebersicht extends javax.servlet.http.HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, java.io.IOException {

        performTask(request, response);

    }

    public void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
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
            DB db = (DB) session.getAttribute("db");
            String auth = (String) session.getAttribute("auth");
            if (db == null || !auth.equals("ok")) {
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
            // System.err.println("Stardatum = "+startdatum);
            // System.err.println(settings);
            if (startdatum == null)
                if (settings.containsKey("renditeUebersichtStartdatum")) {
                    startdatum = (String) settings.get("renditeUebersichtStartdatum");
                    // System.err.println("Stardatum = "+startdatum);
                } else {
                    startdatum = start_datum;

                }
            settings.put("renditeUebersichtStartdatum", startdatum);
            db.updatesetting("renditeUebersichtStartdatum", startdatum);
            String enddatum = request.getParameter("enddatum");
            if (enddatum == null)
                if (settings.containsKey("renditeUebersichtEnddatum")) {
                    enddatum = (String) settings.get("renditeUebersichtEnddatum");
                } else {
                    enddatum = akt_datum;

                }
            settings.put("renditeUebersichtEnddatum", enddatum);
            db.updatesetting("renditeUebersichtEnddatum", enddatum);
            String anlagen_id = request.getParameter("anlagen_id");
            if (anlagen_id == null) {
                if (settings.containsKey("renditeUebersichtAnlagen")) {
                    anlagen_id = (String) settings.get("renditeUebersichtAnlagen");
                } else {
                    anlagen_id = "";

                }
            }
            settings.put("renditeUebersichtAnlagen", anlagen_id);
            db.updatesetting("renditeUebersichtAnlagen", anlagen_id);
            
            String einzeln = request.getParameter("einzeln");
            String exactSelected="";
            if (einzeln == null)
            {
                //System.out.println("SetExact Nein");
                einzeln="nein";
            }
            else
            {
                exactSelected = "checked";
            }

            session.setAttribute("settings", settings);
            Vector rulesAnlagen = db.getAllAnlagen();
            String rule_id=request.getParameter("rule_id");
            if (rule_id==null)
            {
                if (settings.containsKey("renditeRuleId"))
                {
                    rule_id=(String)settings.get("renditeRuleId");
                }
                else
                {
                    rule_id="";
                    
                }                   
            }
            settings.put("renditeRuleId",rule_id);
            db.updatesetting("renditeRuleId",rule_id);
            
            out.println("<html>");
            out.println("<head>");
            out.println(" <title>Rendite Uebersicht</title>");
            out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
            out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
            out.println("</head>");

            out.println("<body  bgcolor=\"#EEFFBB\">");
            // Vector vec=(Vector)session.getAttribute("kategorien");
            // Vector konten=(Vector)session.getAttribute("konten");
            hf.writeHeader(out, (String) ((Hashtable) session.getAttribute("settings")).get("instance"));
            hf.writeStatisticHeader(out);
            out.println("<h1>Rendite Übersicht</h1>");
            out.println("<table>");
            out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
            out.println("<form action=renditeoverview method=post>");
            /// 
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
            Vector rules=db.renditeRules(db.getAllRules());
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
            ///
            out.println("Anlage: <select name=\"anlagen_id\" size=\"1\">");
            // out.println("<option> </option>");
            select = "";
            if (anlagen_id.equals("-1")) {
                select = " selected";
            } else {
                select = "";
            }
            out.println("<option" + select + " value=\"-1\"> </option>");

            for (int i = 0; i < rulesAnlagen.size(); i++) {
                
                // System.out.println("RULE_ID:
                // "+((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString());
                // System.out.println("RULE_ID_: "+rule_id);
                if (((Integer) ((Hashtable) rulesAnlagen.elementAt(i)).get("id")).toString().equals(anlagen_id)) {
                    select = " selected";
                } else {
                    select = "";
                }
                out.println("<option" + select + " value=\"" + ((Hashtable) rulesAnlagen.elementAt(i)).get("id") + "\">"
                        + ((Hashtable) rulesAnlagen.elementAt(i)).get("name") + "</option>");
            }
            out.println("</select>");
            out.println("<p>");
            out.println("Start Datum:<br><input id=\"dob1\" name=\"startdatum\" value=\"" + startdatum
                    + "\" size=\"10\" maxlength=\"10\" type=\"text\" value=\"" + akt_datum
                    + "\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
            out.println(
                    "<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
            out.println("</div>");
            out.println("<p>");
            out.println("End Datum:<br><input id=\"dob2\" name=\"enddatum\"  value=\"" + enddatum
                    + "\" size=\"10\" maxlength=\"10\" type=\"text\" value=\"" + akt_datum
                    + "\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
            out.println(
                    "<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
            out.println("</div>");
            out.println("<p>");
            out.println("<input type=\"checkbox\" name=\"einzeln\" value=\"yes\" "+exactSelected+"> Durchschnitt der einzelnen Anlagen <br>");
            out.println("<p>");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"rendite\">");
            out.println("<input type=\"submit\" value=\"Absenden\";>");
            out.println("</form>");
            out.println("</td>");
            out.println("<td valign=top>");
            if (mode.equals("rendite")) {
                Vector chartvec = new Vector();
                Vector allAnlagen = db.getAllAnlagen();
                Map<String, Map<String, TupelAmount>> mapRendite = new HashMap<String, Map<String, TupelAmount>>();
               // List <TupelDatum> itemList = new ArrayList<TupelDatum>();
                for (int i = 0; i < allAnlagen.size(); i++) {
                    Hashtable hash = (Hashtable) allAnlagen.get(i);

                    if (!((Integer) ((Hashtable) rulesAnlagen.elementAt(i)).get("id")).toString().equals(anlagen_id) && ! anlagen_id.equals("-1") )
                    {
                      
                        continue;
                    }
                    if (hash.get("rendite").equals("Y")) {

                        Vector allKonten = db.getAllKonto((String) hash.get("name"));
                        String rule="";
                        for (int j = 0; j < allKonten.size(); j++) {
                            if (rule_id.equals("-1"))
                            {
                                //dummy
                                
                            rule="";
                            }
                            else
                            {
                            rule=" AND "+db.getRuleCommand(new Integer(rule_id));
                            }
                            Hashtable konto = (Hashtable) allKonten.get(j);
                            Hashtable hash_chart = new Hashtable();
                            Vector allRendite = db.getRenditeByKonto((Integer) konto.get("id"),
                                    startdatum.replaceAll("-", ""), enddatum.replaceAll("-", ""),
                                    rule);
                            Double sumKontostand=0.0;
                            for (int k = 0; k < allRendite.size(); k++) {
                                Hashtable rend = (Hashtable) allRendite.get(k);
                                
                                if (mapRendite.containsKey(formatter.format(rend.get("datum")))) {
                                    TupelAmount tupel = new TupelAmount();
                                    tupel.setValue((Double) rend.get("value"));
                                    tupel.setAmount((Double) rend.get("amount"));
                                    Map<String, TupelAmount> tmp = mapRendite.get(formatter.format(rend.get("datum")));
                                    tmp.put((String) konto.get("name"), tupel);
                                } else {
                                    Map<String,TupelAmount > tmp = new HashMap<String, TupelAmount>();
                                    TupelAmount tupel = new TupelAmount();
                                    tupel.setValue((Double) rend.get("value"));
                                    tupel.setAmount((Double) rend.get("amount"));
                                    tmp.put((String) konto.get("name"), tupel);
                                    
                                    mapRendite.put(formatter.format(rend.get("datum")), tmp);
                                }
                               // sumKontostand=sumKontostand+ (Double)rend.get("amount");
                            }

                         
                        }
                        if (einzeln.equals("yes")) {
                            Set<String> setKeys = mapRendite.keySet();
                            for (String key : setKeys) {
                                Map<String, TupelAmount> tmp = mapRendite.get(key);
                                // System.out.println("Key ist " + key);
                                Set<String> setAnlage = tmp.keySet();
                                Double sum=0.0;
                                Double sumKontostand=0.0;
                                int count=0;
                                Vector<String> toRemove = new Vector<String>();
                                for (String keyAnlage : setAnlage) { 
                                    if (keyAnlage.startsWith("Anlage_"))
                                    {
                                        continue;
                                    }
                                    //System.out.println("Einzel sumKontostand for Anlage "+keyAnlage+ " "+sumKontostand);
                                    sumKontostand=sumKontostand+tmp.get(keyAnlage).getAmount();
                                }
                                //System.out.println("Einzel sumKontostand = " +sumKontostand );
                                Double summeAmount = 0.0;
                               // Double summeKontostand = 0.0;
                                Double gesamtrendite= 0.0;
                                for (String keyAnlage : setAnlage) {
                                    if (keyAnlage.startsWith("Anlage_"))
                                    {
                                        continue;
                                    }
                                    TupelAmount tupel = tmp.get(keyAnlage);
                                   //System.out.println("Einzel keyAnlage = " +keyAnlage);
                                    //System.out.println("Einzel amount = " + tmp.get(keyAnlage).getAmount() );
                                    //System.out.println("Einzel sumKontostand = " +sumKontostand );
                                    Double gewicht = tmp.get(keyAnlage).getAmount() /sumKontostand;
                                    //System.out.println("Einzel Gewicht "+keyAnlage + " = " +gewicht);
                                    Double rendite= gewicht * tmp.get(keyAnlage).getValue();
                                    //System.out.println("Einzel Rendite "+keyAnlage + " = " +rendite);
                                    gesamtrendite = gesamtrendite + rendite;
                                    summeAmount=summeAmount+tmp.get(keyAnlage).getValue();
                                    //sum = sum + tupel.getValue();
                                    //sumKontostand = sumKontostand + tupel.getAmount();
                                    count ++;
                                    toRemove.add(keyAnlage);
                                    //setAnlage.remove(keyAnlage);
                                }
                               //Double avg = summeAmount/ setAnlage.size();
                                //Lösche die einzelnen Anlagen raus
                                for (int l=0; l<toRemove.size(); l++ )
                                {
                                    setAnlage.remove(toRemove.get(l));
                                }
                               
                                TupelAmount tupel = new TupelAmount();
                                tupel.setValue(gesamtrendite);
                                //System.out.println("Einzel AVG = " + gesamtrendite);
                                //System.out.println("Einzel sumKontostand = " +sumKontostand );
                                tupel.setAmount(sumKontostand);
                                //TODO hier muss noch der amount gesetzt wereden!!!
                               tmp.put("Anlage_"+hash.get("name"), tupel);
                            }
                        }
                            
                    }
                }
                Set<String> setKeys = mapRendite.keySet();

                for (String key : setKeys) {
                    Hashtable hash = new Hashtable();
                    Map<String, TupelAmount> tmp = mapRendite.get(key);
                    // System.out.println("Key ist " + key);
                    Set<String> setAnlage = tmp.keySet();
                    // Brerechne Gewichte
                    
                    Double summeAmount = 0.0;
                    Double summeKontostand = 0.0;
                    Double gesamtrendite= 0.0;
                    for (String keyAnlage : setAnlage) {                     
                        summeKontostand=summeKontostand+tmp.get(keyAnlage).getAmount();
                    }
                    for (String keyAnlage : setAnlage) {
                        Double gewicht = tmp.get(keyAnlage).getAmount() /summeKontostand;
                        //System.out.println("Gewicht "+keyAnlage + " = " +gewicht);
                        Double rendite= gewicht * tmp.get(keyAnlage).getValue();
                        //System.out.println("Rendite "+keyAnlage + " = " +rendite);
                        gesamtrendite = gesamtrendite + rendite;
                        summeAmount=summeAmount+tmp.get(keyAnlage).getValue();
                        hash.put(keyAnlage, tmp.get(keyAnlage).getValue());
                    }
                    //System.out.println("SummeAmount = " +summeAmount);
                    //System.out.println("SummeKontostand = " +summeKontostand);
                    //System.out.println("Gesamtrendite = " +gesamtrendite);
                   // Double avg = summeAmount/ setAnlage.size();
                    //System.out.println("Durchschnitt = " +avg);
                    hash.put("Mittelwert", gesamtrendite);
                    hash.put("datum", key);
                    chartvec.addElement(hash);
                }
               // System.out.println(chartvec);
                session.setAttribute("chart_vec", chartvec);
                out.println("<img src=chart?mode=rendite width'600' height='600'>");
            }
            out.println("<td></tr>");

            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        } catch (Throwable theException) {
            theException.printStackTrace();
        }

    }

    private Vector eliminateZeroValues(Vector vec) {
        System.out.println("Size before " + vec.size());
        Vector newvec = new Vector();
        Double sum = 0.0;
        for (int i = 0; i < vec.size(); i++) {
            Double value = (Double) vec.elementAt(i) + sum;
            sum = value;
            if ((value > 0.001) || (value < -0.001)) {
                newvec.addElement(vec.elementAt(i));
            }
        }
        System.out.println("Size after " + newvec.size());
        System.out.println(newvec);
        return newvec;
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

    private Double computeAvgPerDay(Vector vec) {
        Double summe = 0.0;
        for (int i = 0; i < vec.size(); i++) {
            summe += (Double) vec.get(i);
        }
        return summe / vec.size();
    }

}
