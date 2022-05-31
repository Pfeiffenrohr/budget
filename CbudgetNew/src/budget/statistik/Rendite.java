package budget.statistik;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;
import cbudgetbase.DB;

public class Rendite extends javax.servlet.http.HttpServlet {

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
                if (settings.containsKey("renditeStartdatum")) {
                    startdatum = (String) settings.get("renditeStartdatum");
                    // System.err.println("Stardatum = "+startdatum);
                } else {
                    startdatum = start_datum;

                }
            settings.put("renditeStartdatum", startdatum);
            db.updatesetting("renditeStartdatum", startdatum);
            String enddatum = request.getParameter("enddatum");
            if (enddatum == null)
                if (settings.containsKey("renditeEnddatum")) {
                    enddatum = (String) settings.get("renditeEnddatum");
                } else {
                    enddatum = akt_datum;

                }
            settings.put("renditeEnddatum", enddatum);
            db.updatesetting("renditeEnddatum", enddatum);
            String rule_id = request.getParameter("rule_id");
            if (rule_id == null) {
                if (settings.containsKey("renditeRuleId")) {
                    rule_id = (String) settings.get("renditeRuleId");
                } else {
                    rule_id = "";

                }
            }
            settings.put("renditeRuleId", rule_id);
            db.updatesetting("renditeRuleId", rule_id);

            session.setAttribute("settings", settings);
            Vector rules = db.onlyValidRules(db.getAllRules());

            out.println("<html>");
            out.println("<head>");
            out.println(" <title>Rendite</title>");
            out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
            out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
            out.println("</head>");

            out.println("<body  bgcolor=\"#EEFFBB\">");
            // Vector vec=(Vector)session.getAttribute("kategorien");
            // Vector konten=(Vector)session.getAttribute("konten");
            hf.writeHeader(out, (String) ((Hashtable) session.getAttribute("settings")).get("instance"));
            hf.writeStatisticHeader(out);
            out.println("<h1>Rendite</h1>");
            out.println("<table>");
            out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
            out.println("<form action=rendite method=post>");
            out.println("Regel: <select name=\"rule_id\" size=\"1\">");
            // out.println("<option> </option>");
            String select = "";
            if (rule_id.equals("-1")) {
                select = " selected";
            } else {
                select = "";
            }
            out.println("<option" + select + " value=\"-1\"> </option>");

            for (int i = 0; i < rules.size(); i++) {
                // System.out.println("RULE_ID:
                // "+((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString());
                // System.out.println("RULE_ID_: "+rule_id);
                if (((Integer) ((Hashtable) rules.elementAt(i)).get("rule_id")).toString().equals(rule_id)) {
                    select = " selected";
                } else {
                    select = "";
                }
                out.println("<option" + select + " value=\"" + ((Hashtable) rules.elementAt(i)).get("rule_id") + "\">"
                        + ((Hashtable) rules.elementAt(i)).get("name") + "</option>");
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
            out.println("<input type=\"hidden\" name=\"mode\" value=\"rendite\">");
            out.println("<input type=\"submit\" value=\"Absenden\";>");
            out.println("</form>");
            out.println("</td>");
            out.println("<td valign=top>");
            if (mode.equals("rendite")) {
                Vector vecAnlagen = db.getAllAnlagen();

                // String[] anlage = { "P2p", "ETF", "Fonds" };

                for (int j = 0; j < vecAnlagen.size(); j++) {
                    Hashtable anlage = (Hashtable) vecAnlagen.get(j);
                    if (anlage.get("rendite").equals("N")) {
                        continue;
                    }

                    Vector kontos = db.getAllKonto((String) anlage.get("name"));
                    out.println("<h2>" + anlage.get("name") + "</h2>");
                    out.println("<table border=\"1\"  bgcolor=\"#CCEECC\">");
                    // out.println("<table border=\"1\">");
                    out.println("<thead>");
                    out.println("<tr>");
                    out.println("<th>Nr.</th>");
                    out.println("<th>Konto</th>");
                    out.println("<th>Wert pro Tag</th>");
                    out.println("<th>Ertrag</th>");
                    out.println("<th>Prozent</th>");
                    out.println("</tr>");
                    out.println("</thead>");
                    out.println("<tbody>");
                    Hashtable<Integer, Double> gesamtKontostandHash = new Hashtable<Integer, Double>();
                    Hashtable<Integer, Double> gesamtErtragHash = new Hashtable<Integer, Double>();
                    Double gesamtProzent = 0.0;
                    Double summeErtragProJahr = 0.0;
                    for (int i = 0; i < kontos.size(); i++) {
                        Hashtable konto = (Hashtable) kontos.get(i);

                        /*
                         * if (! konto.get("name").equals("Consors Depot")) { continue; }
                         */
                        Calendar cal_begin = Calendar.getInstance();
                        cal_begin.setTime(formatter.parse(startdatum));
                        Calendar cal_end = Calendar.getInstance();
                        cal_end.setTime(formatter.parse(enddatum));
                        int count = 0;
                        String rule;
                        if (rule_id.equals("-1")) {
                            // dummy

                            rule = "";
                        } else {
                            rule = " AND " + db.getRuleCommand(new Integer(rule_id));
                        }

                        String ruleErtrag = "";
                        if ((Integer) konto.get("rule_id") == null || (Integer) konto.get("rule_id") == -1
                                || (Integer) konto.get("rule_id") == 0) {
                            ruleErtrag = db.getRuleCommand((Integer) anlage.get("rule_id"));
                            // System.out.println("Rule_id von Anlage");
                            // System.out.println("Rule_id =" +anlage.get("rule_id"));

                        } else {
                            ruleErtrag = db.getRuleCommand((Integer) konto.get("rule_id"));

                            // System.out.println("Rule_id von Konto");
                            // System.out.println("Rule_id =" +konto.get("rule_id"));

                        }

                        String where = rule;
                        //System.out.println("where = " + where);
                        Double sum = 0.0;
                        Double sumProzent = 0.0;
                        Double sumErtrag = 0.0;
                        String whereErtrag = "";
                        Boolean toView=false;
                        if (!ruleErtrag.contains("konto_id")) {
                            whereErtrag = "konto_id=" + konto.get("id") + " AND " + ruleErtrag + where;
                        } else {
                            whereErtrag = ruleErtrag + where;
                        }
                        while (cal_end.after(cal_begin)) {
                            Double kontostand = db.getAktuellerKontostand((String) konto.get("name"),
                                    (String) formatter.format(cal_end.getTime()), where);

                            if (!gesamtKontostandHash.containsKey(count)) {
                                gesamtKontostandHash.put(count, kontostand);
                            } else {
                                gesamtKontostandHash.put(count, gesamtKontostandHash.get(count) + kontostand);
                            }
                            if (kontostand > -0.001 && kontostand < 0.001) {
                                cal_end.add(Calendar.DATE, -1);
                                count++;
                                continue;
                            }
                            toView=true;
                            // Berechne Ertrag

                            // System.out.println("Where = " +where);
                            Double ertrag = db.getKategorienAlleSummeWhere((String) formatter.format(cal_end.getTime()),
                                    (String) formatter.format(cal_end.getTime()), whereErtrag);
                            //System.out.println("Tag: " + formatter.format(cal_end.getTime()));
                            //System.out.println("Kontostand: " + kontostand);
                            //System.out.println("Ertrag = " + ertrag);
                            if (!gesamtErtragHash.containsKey(count)) {
                                gesamtErtragHash.put(count, ertrag);
                            } else {
                                gesamtErtragHash.put(count, gesamtErtragHash.get(count) + ertrag);
                            }
                            sumErtrag = sumErtrag + ertrag;
                            Double prozent = ertrag / kontostand;
                            //System.out.println("Prozent = " + prozent);
                            sumProzent = sumProzent + prozent;
                            count++;
                            cal_end.add(Calendar.DATE, -1);
                        }
                        //System.out.println("sumProzent = " + sumProzent);
                        //System.out.println("count = " + count);
                        sumProzent = (sumProzent / count) * 365 * 100;
                        if (toView) {
                        out.println("<tr>");
                        out.println("<td>" + i + "<td>" + konto.get("name") + "</td><td>" 
                                + formater(sumErtrag) + "</td><td>" + formater(sumProzent) + "%</td>");
                        out.println("</tr>");
                        }
                    } // for konto
                    Double sumProzent = 0.0;
                    Double sumErtrag = 0.0;

                    Enumeration allEntries = gesamtKontostandHash.keys();

                    while (allEntries.hasMoreElements()) {
                        Integer key = (Integer) allEntries.nextElement();
                        Double gesamtKontostand = gesamtKontostandHash.get(key);
                        //System.out.println("gesamt Kontostand =" + gesamtKontostand);
                        Double gesamtErtrag = gesamtErtragHash.get(key);
                        if (gesamtErtrag == null) {
                            gesamtErtrag = 0.0;
                        }
                        //System.out.println("gesamt Ertrag =" + gesamtErtrag);
                        Double prozent = 0.0;
                        if (gesamtKontostand > -0.001 && gesamtKontostand < 0.001) {
                            prozent = 0.0;
                        } else {
                            prozent = gesamtErtrag / gesamtKontostand;
                        }
                        //System.out.println("gesamt Prozent =" + prozent);
                        sumProzent = sumProzent + prozent;
                        sumErtrag = sumErtrag + gesamtErtrag;
                    }
                    //System.out.println("gesamt sumProzent =" + sumProzent);
                    sumProzent = (sumProzent / gesamtKontostandHash.size()) * 365 * 100;

                    out.println("<tr>");
                    out.println("<td><td><b>Durchschnitt:</b></td><td><b>" + formater(0.0) + "</td></td><td><b>"
                            + formater(sumErtrag) + "</td></td><td><b>" + formater(sumProzent) + "% </b></td>");
                    out.println("</tr>");
                    out.println("</tbody>");
                    out.println("</table>");
                    out.println("</td><td valign=top>");
                }
            }
            out.println("<td></tr>");

            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        } catch (

        Throwable theException) {
            theException.printStackTrace();
        }

    }

    private Vector eliminateZeroValues(Vector vec) {
        //System.out.println("Size before " + vec.size());
        Vector newvec = new Vector();
        Double sum = 0.0;
        for (int i = 0; i < vec.size(); i++) {
            Double value = (Double) vec.elementAt(i) + sum;
            sum = value;
            if ((value > 0.001) || (value < -0.001)) {
                newvec.addElement(vec.elementAt(i));
            }
        }
        //System.out.println("Size after " + newvec.size());
        //System.out.println(newvec);
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
