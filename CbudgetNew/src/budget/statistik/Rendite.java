package budget.statistik;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
            if (mode == null) {
                mode = "";
            }
            String imBatch = request.getParameter("imBatch");
            {
                if (imBatch == null) {
                    imBatch = "";
                }
            }
            String batchAuslesen = request.getParameter("batchAuslesen");
            {
                if (batchAuslesen == null) {
                    batchAuslesen = "";
                }
            }
            //System.out.println("ImBatch =" + imBatch);
            if (imBatch.equals("imBatch"))
            {
                mode = "imBatch";
                //insertInDatabase
            }
            if (batchAuslesen.equals("batchAuslesen"))
            {
                mode = "batchAuslesen";
                //insertInDatabase
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
            Hashtable orderrendite = db.getOrderRendite();
            /*
            Im Mode batchAuslesen soll start- und enddatum aus der ausgeeseene orderrendite gesetzt werden.
             */
            if ( mode.equals("batchAuslesen")) {
                startdatum = formatter.format((Date)orderrendite.get("startdate"));
                enddatum = formatter.format((Date)orderrendite.get("enddate"));
            }

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
            out.println("<input type=\"checkbox\" name=\"imBatch\" value=\"imBatch\"> Im Batch berechnen <br>");
            out.println("<p>");
            out.println("<input type=\"checkbox\" name=\"batchAuslesen\" value=\"batchAuslesen\">Batch Berechnung auslesen <br>");
            out.println("<p>");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"rendite\">");
            if (mode.equals("imBatch"))
            {
                if ((Integer) orderrendite.get("finished") == 0 )
                {
                    out.println("!!!Es wird schon ein Auftrag bearbeitet!!!");
                    out.println("<p>");
                }
                else {
                    out.println("Anfrage wurde entgegengenommen!");
                    out.println("<p>");
                    db.insertOrderRendite(startdatum, enddatum, rule_id);
                }
            }
            if (mode.equals("batchAuslesen"))
            {
                if ((Integer) orderrendite.get("finished") == 0 )
                {
                    out.println("!!!Der Batchauftrag ist noch nicht berechnet!!!!!!!!");
                    out.println("<p>");
                    mode="";
                }
            }
            out.println("<input type=\"submit\" value=\"Absenden\";>");
            out.println("</form>");
            out.println("</td>");
            out.println("<td valign=top>");
            if (mode.equals("rendite") || mode.equals("batchAuslesen")) {
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
                    Double summeErtrag = 0.0;
                    Double summedayAvg = 0.0;
                    Double summeErtragProJahr = 0.0;
                    for (int i = 0; i < kontos.size(); i++) {
                        Hashtable konto = (Hashtable) kontos.get(i);
                        Double ertragProjahr = 0.0;
                        Double ertrag;
                        Double dayAvg;
                        Double rendite =0.0;
                        int count = 1;
                        if (mode.equals("batchAuslesen")) {
                            Hashtable  renditeBatch = db.getRenditeBatch((Integer) konto.get("id"));
                            ertrag = (Double)renditeBatch.get("ertrag");
                            dayAvg=(Double)renditeBatch.get("wertProTag");
                            rendite = (Double)renditeBatch.get("ertragProzent");
                            count=365;
                        }
                        else {
                            /*
                             * if (! konto.get("name").equals("Consors Depot")) { continue; }
                             */
                            Calendar cal_begin = Calendar.getInstance();
                            cal_begin.setTime(formatter.parse(startdatum));
                            Calendar cal_end = Calendar.getInstance();
                            cal_end.setTime(formatter.parse(enddatum));

                            int sumcount = 0;
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

                                //  System.out.println("Rule_id von Konto");
                                //  System.out.println("Rule_id =" +konto.get("rule_id"));

                            }

                            String where = rule;
                            Double sum = 0.0;
                            Double kontostand = 0.0;
                            while (cal_end.after(cal_begin)) {
                                if (konto.get("name").equals("Haus Wiesengrund 17")) {
                                    kontostand = 260000.0;
                                } else {
                                    kontostand = db.getAktuellerKontostand((String) konto.get("name"),
                                            (String) formatter.format(cal_end.getTime()), where);
                                }
                                // System.out.println("Kontostand: "+ kontostand);
                                if (kontostand > -0.001 && kontostand < 0.001) {
                                    cal_end.add(Calendar.DATE, -1);
                                    continue;
                                }

                                sum = sum + (kontostand * count);

                                sumcount = sumcount + count;
                                count++;
                                cal_end.add(Calendar.DATE, -1);
                            }
                            if (count == 1) {
                                continue;
                            }
                            dayAvg = sum / sumcount;
                            if (!ruleErtrag.contains("konto_id")) {
                                where = "konto_id=" + konto.get("id") + " AND " + ruleErtrag + where;
                            } else {
                                where = ruleErtrag + where;
                            }
                            //System.out.println(where);
                            ertrag = db.getKategorienAlleSummeWhere(startdatum, enddatum, where);
                        }
                        // Ertrag hochrechnen auf Jahr

                        /*
                         * TODO Hack hardcoded. sollte besser gemacht werden!!!
                         */
                        if (anlage.get("name").equals("P2p")) {
                            ertragProjahr = ertrag * (365.0 / count);
                        } else {
                            ertragProjahr = ertrag;
                        }
                        /*
                         * System.out.println("Ertrag " + ertrag);
                         * System.out.println("Ertrag pro Jahr  " + ertragProjahr);
                         * System.out.println("Durchschnitt Tag = " + dayAvg);
                         * System.out.println("Count = " + count);
                         */
                        if (dayAvg != 0.0) {
                            rendite = (ertragProjahr * 100) / dayAvg;
                        }

                        summeErtrag += ertrag;
                        summedayAvg += dayAvg;
                        summeErtragProJahr += ertragProjahr;
                        if (dayAvg != 0.0) {

                            // System.out.println((String) konto.get("name") + " " + rendite);
                            out.println("<tr>");
                            out.println("<td>" + i + "<td>" + konto.get("name") + "</td><td>" + formater(dayAvg)
                                    + "</td><td>" + formater(ertrag) + "</td><td>" + formater(rendite) + "%</td>");
                            out.println("</tr>");
                        }
                    }
                    Double summeRendite = (summeErtragProJahr * 100) / summedayAvg;
                    out.println("<tr>");
                    out.println("<td><td><b>Durchschnitt:</b></td><td><b>" + formater(summedayAvg) + "</td></td><td><b>"
                            + formater(summeErtrag) + "</td></td><td><b>" + formater(summeRendite) + "% </b></td>");
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
        } catch (Throwable theException) {
            theException.printStackTrace();
        }

    }

    private Vector eliminateZeroValues(Vector vec) {
        // System.out.println("Size before " + vec.size());
        Vector newvec = new Vector();
        Double sum = 0.0;
        for (int i = 0; i < vec.size(); i++) {
            Double value = (Double) vec.elementAt(i) + sum;
            sum = value;
            if ((value > 0.001) || (value < -0.001)) {
                newvec.addElement(vec.elementAt(i));
            }
        }
        //  System.out.println("Size after " + newvec.size());
        //  System.out.println(newvec);
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
