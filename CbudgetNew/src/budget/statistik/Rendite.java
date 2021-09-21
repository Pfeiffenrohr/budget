package budget.statistik;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
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
            if (mode==null)
            {
                mode="";
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
            if (mode.equals("rendite"))
            {
            String[] anlage = { "P2p", "ETF", "Fonds" };
            for (int j = 0; j < anlage.length; j++) {
                Vector kontos = db.getAllKonto(anlage[j]);
                out.println("<h2>"+anlage[j]+"</h2>");
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

                for (int i = 0; i < kontos.size(); i++) {
                    Hashtable konto = (Hashtable) kontos.get(i);
                    
                    /*
                      if (! konto.get("name").equals("Consors Depot")) { continue; }
                     */
                    Calendar cal_begin = Calendar.getInstance();
                    cal_begin.setTime(formatter.parse(startdatum));
                    Calendar cal_end = Calendar.getInstance();
                    cal_end.setTime(formatter.parse(enddatum));
                    int count = 1;
                    int sumcount = 0;
                    String where = ""; // TODO Hier muss die Rule rein.
                    Double sum = 0.0;
                    while (cal_end.after(cal_begin)) {
                        Double kontostand = db.getAktuellerKontostand((String) konto.get("name"),
                                (String) formatter.format(cal_end.getTime()), where);
                        // System.out.println("Kontostand: "+ kontostand);
                        if (kontostand ==0.0 )
                        {
                            cal_end.add(Calendar.DATE, -1);
                            continue;
                        }

                        sum = sum + (kontostand * count);

                        sumcount = sumcount + count;
                        count++;
                        cal_end.add(Calendar.DATE, -1);
                    }

                    Double dayAvg = sum / sumcount;

                    where = "konto_id=" + konto.get("id") + " and name ='Ertrag'";
                    Double ertrag = db.getKategorienAlleSummeWhere(startdatum, enddatum, where);
                    // Ertrag hochrechnen auf Jahr
                    Double ertragProjahr = ertrag * (365.0 / count);
                    /*
                     * System.out.println("Ertrag " + ertrag);
                    System.out.println("Ertrag pro Jahr  " + ertragProjahr);
                    System.out.println("Durchschnitt Tag = " + dayAvg);
                    */
                    if (dayAvg != 0.0) {
                        Double rendite = (ertragProjahr * 100) / dayAvg;
                        //System.out.println((String) konto.get("name") + " " + rendite);
                        out.println("<tr>");
                        out.println("<td>" + i + "<td>" + konto.get("name") + "</td><td>" + formater(dayAvg)
                                + "</td><td>" + formater(ertrag) + "</td><td>" + formater(rendite) + "%</td>");
                        out.println("</tr>");
                    }
                }

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
