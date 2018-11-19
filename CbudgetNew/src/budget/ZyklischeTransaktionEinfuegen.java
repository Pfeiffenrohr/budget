package budget;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class ZyklischeTransaktionEinfuegen extends javax.servlet.http.HttpServlet {

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
				
				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				String akt_konto = (String)session.getAttribute("akt_konto");
				String name = request.getParameter("Name");
				String end_datum = request.getParameter("dob");
				String wiederholung =request.getParameter("repeat");
				if (wiederholung==null)
				{
					wiederholung="monatlich";
				}
				String transid=(String)session.getAttribute("transid");
				String noend =request.getParameter("endlos");
				String delta =request.getParameter("delta");
				String update =request.getParameter("kor_id");
				Integer int_vec_id=(Integer)session.getAttribute("int_vec_id");
				Vector trans=(Vector)session.getAttribute("transaktionen");
				Hashtable hash_trans;
				Calendar cal_begin= Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				if (update.equals("0"))
				{
				hash_trans=(Hashtable)trans.elementAt(int_vec_id);
				}
				else
				{
					System.out.println("update =" +update);
				hash_trans=db.getLastCycleTransaktion(formatter.format(cal_begin.getTime()),new Integer(update));
				db.deleteTransaktionExcept(new Integer(update),formatter.format(hash_trans.get("datum")));
				}
				if (noend==null)
				{
					noend="nein";
				}
				boolean Fehler=false;
				HeaderFooter hf = new HeaderFooter();
				boolean multicycle=true;
				Hashtable hash= new Hashtable();
				hash.put("name", name);
				hash.put("end_datum",end_datum);
				hash.put("wiederholung", wiederholung);
				hash.put("delta", delta);
				//hash.put("kor_id", kor_id);
				
				//Vor dem Einfügen muß das konto noch als String gecastet werden.
				
				
				//hash.put("transid",transid );
				hash.put("noend",noend );
				out.println("<html>");
				out.println("<body>");

				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				out.println("Transaktion wird erstellt...");
				out.println("<p>");
				//Integer korid= db.getKorId(transid);
				Integer korid=(Integer)hash_trans.get("kor_id");
				String cycle="2";
				//System.out.println("Gefundene Korid = "+korid);
				if (update.equals("0"))
				{
				if (korid.intValue()==0)
				{
					korid=db.getHighestId("transaktionen","kor_id");
					korid=korid+1;
					multicycle=false;
					cycle="1";
					hash_trans.put("cycle", 1);
					//db.setCycle(hash_trans.get("id").toString(),"1");
				}
				else
				{
					hash_trans.put("cycle", 2);
				}
				}
				else
				{
					cycle=((Integer)hash_trans.get("cycle")).toString();
					if (cycle.equals("1"))
					{
						multicycle=false;
					}
				}
				db.setCycle(hash_trans.get("id").toString(),cycle);
				//System.out.println("Neue Korid = "+korid);
				hash.put("korid",korid);
				
				if (! db.insertCycleTransaktion(hash,update))
				{
				out.println("<font color=\"red\">!!!Zyklische Transaktion konnt nicht eingefügt werden!!!</font>");
				out.println("</body>");
				out.println("</html>");
				out.close();
				
				return;
			   }
				
				
				cal_begin.setTime((Date)hash_trans.get("datum"));
				Calendar cal_kor=Calendar.getInstance();
				cal_kor=(Calendar)cal_begin.clone();
				Calendar cal_end= Calendar.getInstance();
				cal_end.setTime(formatter.parse(end_datum));
				int int_delta= new Integer(delta).intValue();
				if (wiederholung.equals("taeglich"))
				{
					cal_begin.add(Calendar.DATE,int_delta );
				}
				if (wiederholung.equals("monatlich"))
				{
					cal_begin.add(Calendar.MONTH, int_delta);
				}
				if (wiederholung.equals("jaehrlich"))
				{
					cal_begin.add(Calendar.YEAR, int_delta);
				}
				int ende=0;
				hash_trans.put("kor_id",korid);
				hash_trans.put("user","Wiederholung");
				db.setKorId(korid.toString(),hash_trans.get("id").toString());
				
				while (cal_begin.compareTo(cal_end)<0 && ende<20)
				{
					//System.out.println(formatter.format(cal_begin.getTime()));
					//System.out.println("wiederholung =" + wiederholung);
					hash_trans.put("datum",formatter.format(cal_begin.getTime()));
					//hash_trans.put("cycle", 1);
					db.insertTransaktionCycle(hash_trans,((Integer)hash_trans.get("kor_id")).intValue());
					if (wiederholung.equals("taeglich"))
					{
						cal_begin.add(Calendar.DATE, int_delta);
					}
					if (wiederholung.equals("monatlich"))
					{
						cal_begin.add(Calendar.MONTH, int_delta);
					}
					if (wiederholung.equals("jaehrlich"))
					{
						cal_begin.add(Calendar.YEAR, int_delta);
					}
					ende++;
				}
				if (multicycle)
				{
					System.err.println("kor_id = "+korid.toString());
					hash_trans=db.getKorKontoId(((Integer)hash_trans.get("konto")).toString(),korid.toString());
					System.err.println("hash = "+hash_trans);
					if (wiederholung.equals("taeglich"))
					{
						cal_kor.add(Calendar.DATE, int_delta);
					}
					if (wiederholung.equals("monatlich"))
					{
						cal_kor.add(Calendar.MONTH, int_delta);
					}
					if (wiederholung.equals("jaehrlich"))
					{
						cal_kor.add(Calendar.YEAR, int_delta);
					}
					ende=0;
					db.setKorId(korid.toString(),hash_trans.get("id").toString());
					db.setCycle(hash_trans.get("id").toString(),"2");
					while (cal_kor.compareTo(cal_end)<0 && ende<20)
					{
						System.out.println(formatter.format(cal_kor.getTime()));
						hash_trans.put("datum",formatter.format(cal_kor.getTime()));
						hash_trans.put("cycle", 2);
						hash_trans.put("user","Wiederholung");
						db.insertTransaktionCycle(hash_trans,((Integer)hash_trans.get("kor_id")).intValue());
						if (wiederholung.equals("taeglich"))
						{
							cal_kor.add(Calendar.DATE, int_delta);
						}
						if (wiederholung.equals("monatlich"))
						{
							cal_kor.add(Calendar.MONTH, int_delta);
						}
						if (wiederholung.equals("jaehrlich"))
						{
							cal_kor.add(Calendar.YEAR, int_delta);
						}
						ende++;
					}
					
				}//End multicycle
				out.println("Zyklischer Eintrag erfolgreich erstellt");
				out.println("</body>");
				out.println("</html>");
				out.close();
				
			}
				catch (Throwable theException) {
					theException.printStackTrace();
				}
				
			

		}
		
	}

				