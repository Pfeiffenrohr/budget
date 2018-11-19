package budget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.category.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;


import java.util.Hashtable;
import java.util.Vector;
import java.util.Date;
import java.text.SimpleDateFormat;

public class GenerateChart extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/*response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("Hallo Welt");
		out.println("</body>");
		out.println("</html>");*/
		
		
		response.setContentType("image/png");
		HttpSession session = request.getSession(true);
		OutputStream outputStream = response.getOutputStream();
		Vector chartVec = (Vector)session.getAttribute("chart_vec");
		String mode=request.getParameter("mode");
		//createDataset(chartVec);

		if (mode.equals("verlauf")) {
			System.err.println("Create Chart");
			XYDataset dataset = createDataset(chartVec);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChart(dataset);
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("plan")) {
			//System.err.println("Create Chart");
			XYDataset dataset = createDataset(chartVec);
			//System.err.println("Create Dataset");
			JFreeChart chart = createPlanChart(dataset);
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("kat")) {
			//System.err.println("Create Chart");
			//XYDataset dataset = createDataset(chartVec);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = getPieChart(chartVec);
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("art")) {
			System.err.println("Create Chart Kontoarten");
			XYDataset dataset = createDatasetKontoart(chartVec);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChartKontoArt(dataset);
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		
		
		if(mode.equals("monat"))
				{
			try{
		  DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		 // System.out.println(chartVec);
		  for (int i=0; i<chartVec.size();i++)
		  {
			  dataset.setValue((Double)((Hashtable)chartVec.elementAt(i)).get("wert"),(String)((Hashtable)chartVec.elementAt(i)).get("mode"),(String)((Hashtable)chartVec.elementAt(i)).get("Monat"));
		  }
		  //dataset.setValue(6, "Mark", "9/2010");
		  JFreeChart chart = ChartFactory.createBarChart
		  ("Monatsübersicht","Monat", "Euro", dataset, 
		   PlotOrientation.VERTICAL, true,true, false);
		  //chart.setBackgroundPaint(Color.yellow);
		  chart.getTitle().setPaint(Color.black); 
		  CategoryPlot p = chart.getCategoryPlot(); 
		  p.setRangeGridlinePaint(Color.red); 
		  int width = 700;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}		
    	catch (Exception ex) {System.err.println("Exception "+ex); }
	}
		
	}
	private static XYDataset createDataset(Vector vec) {

        TimeSeries s1 = new TimeSeries("Kontodaten", Day.class);
        //TimeSeries s2 = new TimeSeries("Kontodate", Day.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i=0; i<vec.size();i++)
        {
        	try{
        	//System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("wert"));
        	s1.addOrUpdate(new Day((Date)((Hashtable)vec.elementAt(i)).get("datum")),(Double) ((Hashtable)vec.elementAt(i)).get("wert"));
        	//s2.addOrUpdate(new Day((Date)((Hashtable)vec.elementAt(i)).get("datum")),(Double) ((Hashtable)vec.elementAt(i)).get("wert")-100.0);
        	//System.out.println("Wert = "+(Double) ((Hashtable)vec.elementAt(i)).get("wert") );
        	//System.err.println("fertig Eintrag "+i);
        	}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	}
        	
        }
        dataset.addSeries(s1);
        //dataset.addSeries(s2);
        return dataset;
	}
	
	private static XYDataset createDatasetKontoart(Vector vec) {

        TimeSeries s1 = new TimeSeries("Geldkonto", Day.class);
        TimeSeries s2 = new TimeSeries("Geldanlage", Day.class);
        TimeSeries s3 = new TimeSeries("Sachanlage", Day.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i=0; i<vec.size();i++)
        {
        	try{
        	//System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("wert"));
        	s1.addOrUpdate(new Day((Date)((Hashtable)vec.elementAt(i)).get("datum")),(Double) ((Hashtable)vec.elementAt(i)).get("Geldkonto"));
        	s2.addOrUpdate(new Day((Date)((Hashtable)vec.elementAt(i)).get("datum")),(Double) ((Hashtable)vec.elementAt(i)).get("Geldanlage"));
        	s3.addOrUpdate(new Day((Date)((Hashtable)vec.elementAt(i)).get("datum")),(Double) ((Hashtable)vec.elementAt(i)).get("Sachanlage"));
        	
        	//s2.addOrUpdate(new Day((Date)((Hashtable)vec.elementAt(i)).get("datum")),(Double) ((Hashtable)vec.elementAt(i)).get("wert")-100.0);
        	//System.out.println("Wert = "+(Double) ((Hashtable)vec.elementAt(i)).get("Sachanlage")+" Datum " + ((Hashtable)vec.elementAt(i)).get("datum"));
        	//System.err.println("fertig Eintrag "+i);
        	}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	}
        	
        }
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);
        //dataset.addSeries(s2);
        return dataset;
	}
	
	
	
	
	 private static JFreeChart createChart(XYDataset dataset) {
	JFreeChart chart = ChartFactory.createTimeSeriesChart(
		
            "Kontoverlauf",  // title
            "Datum",             // x-axis label
            "Wert",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );
	  chart.setBackgroundPaint(Color.white);

      XYPlot plot = (XYPlot) chart.getPlot();
      plot.setBackgroundPaint(Color.lightGray);
      plot.setDomainGridlinePaint(Color.white);
      plot.setRangeGridlinePaint(Color.white);
      //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
      plot.setDomainCrosshairVisible(true);
      plot.setRangeCrosshairVisible(true);
      
      XYItemRenderer r = plot.getRenderer();
      if (r instanceof XYLineAndShapeRenderer) {
          XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
          //renderer.setShapesVisible(true);
          //renderer.setShapesFilled(true);
      }
      
      DateAxis axis = (DateAxis) plot.getDomainAxis();
      axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
      
      return chart;

  }
	 
	 private static JFreeChart createPlanChart(XYDataset dataset) {
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
		            "Planungsverlauf",  // title
		            "Datum",             // x-axis label
		            "Wert",   // y-axis label
		            dataset,            // data
		            true,               // create legend?
		            true,               // generate tooltips?
		            false               // generate URLs?
		        );
			  chart.setBackgroundPaint(Color.white);

		      XYPlot plot = (XYPlot) chart.getPlot();
		      plot.setBackgroundPaint(Color.lightGray);
		      plot.setDomainGridlinePaint(Color.white);
		      plot.setRangeGridlinePaint(Color.white);
		      //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		      plot.setDomainCrosshairVisible(true);
		      plot.setRangeCrosshairVisible(true);
		      
		      XYItemRenderer r = plot.getRenderer();
		      if (r instanceof XYLineAndShapeRenderer) {
		          XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		          //renderer.setShapesVisible(true);
		          //renderer.setShapesFilled(true);
		      }
		      
		      DateAxis axis = (DateAxis) plot.getDomainAxis();
		      axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		      
		      return chart;

		  }
	 
	 private static JFreeChart createChartKontoArt(XYDataset dataset) {
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
				
		            "Anlageverlauf",  // title
		            "Datum",             // x-axis label
		            "Wert",   // y-axis label
		            dataset,            // data
		            true,               // create legend?
		            true,               // generate tooltips?
		            false               // generate URLs?
		        );
			 XYItemRenderer render = new XYAreaRenderer();
			  chart.setBackgroundPaint(Color.white);

		      XYPlot plot = (XYPlot) chart.getPlot();
		      plot.setRenderer(render);
		      plot.setBackgroundPaint(Color.lightGray);
		      plot.setDomainGridlinePaint(Color.white);
		      plot.setRangeGridlinePaint(Color.white);
		      //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		      plot.setDomainCrosshairVisible(true);
		      plot.setRangeCrosshairVisible(true);
		      
		      XYItemRenderer r = plot.getRenderer();
		      if (r instanceof XYLineAndShapeRenderer) {
		          XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		          //renderer.setShapesVisible(true);
		          //renderer.setShapesFilled(true);
		      }
		      
		      DateAxis axis = (DateAxis) plot.getDomainAxis();
		      axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		      
		      return chart;

		  }
	 
	 public JFreeChart getPieChart(Vector kat) {
			try{
				
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (int i=0; i<kat.size();i++)
			{
				//System.out.println("Setze Wert "+(String)((Hashtable)kat.elementAt(i)).get("name") +(Double)setPositv((Double)((Hashtable)kat.elementAt(i)).get("wert")));
			dataset.setValue((String)((Hashtable)kat.elementAt(i)).get("name"), setPositiv((Double)((Hashtable)kat.elementAt(i)).get("wert")));
			}
			

			boolean legend = true;
			boolean tooltips = false;
			boolean urls = false;

			JFreeChart chart = ChartFactory.createPieChart("Kategorien", dataset, legend, tooltips, urls);

			//chart.setBorderPaint(Color.GREEN);
			chart.setBorderStroke(new BasicStroke(5.0f));
			//chart.setBorderVisible(true);

			return chart;
			}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	return null;
        	}
	

}
	 Double setPositiv(Double d)
	 {
		 if (d<0)
		 {
			 return d * -1;
		 }
		 else
		 {
			 return d;
		 }
	 }
}

