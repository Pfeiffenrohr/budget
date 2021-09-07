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
                if (settings.containsKey("renditeStartdatum"))
                {
                    startdatum=(String)settings.get("renditeStartdatum");
                    //System.err.println("Stardatum = "+startdatum);
                }
                else
                {                           
                startdatum=start_datum;
                
                }
            settings.put("renditeStartdatum",startdatum);
            db.updatesetting("renditeStartdatum",startdatum);
            String enddatum=request.getParameter("enddatum");
            if (enddatum==null)
                if (settings.containsKey("renditeEnddatum"))
                {
                    enddatum=(String)settings.get("renditeEnddatum");
                }
                else
                {
                    enddatum=akt_datum;
                    
                }
            settings.put("renditeEnddatum",enddatum);
            db.updatesetting("renditeEnddatum",enddatum);
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
           
            session.setAttribute("settings",settings);
            Vector rules=db.onlyValidRules(db.getAllRules());
            Vector kontos = db.getAllKonto("P2p");
            out.println("<html>");
            out.println("<head>");
            out.println(" <title>Rendite</title>");
            out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
            out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
            out.println("</head>");

            out.println("<body  bgcolor=\"#EEFFBB\">");
            //Vector vec=(Vector)session.getAttribute("kategorien"); 
           // Vector konten=(Vector)session.getAttribute("konten");
            hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
            hf.writeStatisticHeader(out);
            out.println("<h1>Rendite</h1>");
            out.println("<table>");
            out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
            out.println("<form action=rendite method=post>");
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
            out.println("<input type=\"hidden\" name=\"mode\" value=\"kategorie\">");
            out.println("<input type=\"submit\" value=\"Absenden\";>");
            out.println("</form>");
            out.println("</td>");
            out.println("<td>");
            out.println("<table border=\"1\"  bgcolor=\"#CCEECC\">");
            //out.println("<table border=\"1\">");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Nr.</th>");
            out.println("<th>Konto</th>");
            out.println("<th>Wert pro Tag</th>");
            out.println("<th>Prozent</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
      
            for (int i=0; i < kontos.size();i++)
            {
                Hashtable konto = (Hashtable)kontos.get(i);
                String where = "konto_id = "+konto.get("id");
                Vector vec = db.getKategorienAlleSummeWhereAsVectorPerDay(startdatum, enddatum, where);
                Double summe = computeAvgPerDay(vec);
                where="konto_id="+konto.get("id")+ " and name ='Ertrag'";
                Double ertrag = db.getKategorienAlleSummeWhere(startdatum, enddatum,where);
                System.out.println("Ertrag "+ertrag);
                System.out.println("Summe "+summe);
                if (summe!=0.0)
                {
                Double rendite=(ertrag*100)/summe;
                System.out.println((String)konto.get("name") +" "+rendite);
                out.println("<tr>");
                out.println("<td>"+i+"<td>"+konto.get("name")+"</td><td>"+formater(summe)+"</td><td>"+formater(rendite)+"%</td>") ;
                out.println("</tr>");
                }
            }
            
            
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
  
     private Double computeAvgPerDay(Vector vec) {
         Double summe=0.0;
         for (int i=0; i< vec.size();i++)
         {
             summe +=(Double)vec.get(i);
         }
         return summe/365;
     }

}
