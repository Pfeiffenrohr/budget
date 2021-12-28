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

import org.jfree.chart.ChartUtils;
import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.category.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimePeriod;


import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.text.DecimalFormat;
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
			//System.err.println("Create Chart");
			XYDataset dataset = createDataset(chartVec,false);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChart(dataset);
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("plan")) {
			//System.err.println("Create Chart");
			XYDataset dataset = createDataset(chartVec,true);
			double aktValue = getAktValue(chartVec);
			double initialValue = getinitialValue(chartVec);
			String kategorieName= (String) session.getAttribute("kategorieNamne");
			//System.err.println("Create Dataset");
			JFreeChart chart = createPlanChart(dataset,kategorieName,aktValue,initialValue);
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
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
			ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("art")) {
			//System.err.println("Create Chart Kontoarten");
			XYDataset dataset = createDatasetKontoart(chartVec);
			//TimeTableXYDataset dataset1 =  createDatasetKontoart1(chartVec);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChartKontoArt(dataset);
			//JFreeChart chart = createAreaChart(dataset1);
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("rendite")) {
            //System.err.println("Create Chart Kontoarten");
            XYDataset dataset = createDatasetKontoart(chartVec);
            //TimeTableXYDataset dataset1 =  createDatasetKontoart1(chartVec);
            //System.err.println("Create Dataset");
            //System.err.println(chartVec);
            JFreeChart chart = createChartRendite(dataset);
            //JFreeChart chart = createAreaChart(dataset1);
            //System.err.println("Chart fertig");
            int width = 500;
            int height = 350;
            ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
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
			ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
		}		
    	catch (Exception ex) {System.err.println("Exception "+ex); }
	}
		
	}
	private static XYDataset createDataset(Vector vec,boolean createInitalValue) {

        TimeSeries s1 = new TimeSeries("Verlauf");
        String myDatum ="--not calculated--";
        if (vec.size() >0 )
		{
			if (((Hashtable) vec.get(0)).get("initialDatum") != null) {
				myDatum = ((Hashtable) vec.get(0)).get("initialDatum").toString();
			}
		}
		TimeSeries initial = new TimeSeries("Initialwert, angelegt am " + myDatum);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < vec.size(); i++) {
			try {
				// System.err.println("Eintrag "+(Double)
				// ((Hashtable)vec.elementAt(i)).get("wert"));
			
				s1.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
						(Double) ((Hashtable) vec.elementAt(i)).get("wert"));
				if (createInitalValue) {
					initial.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
							(Double) ((Hashtable) vec.elementAt(i)).get("initial"));
				}
			} catch (Exception ex) {
				System.err.println("Exception " + ex);
			}

		}

		dataset.addSeries(s1);
		if (createInitalValue) {
			dataset.addSeries(initial);
		}
		return dataset;
	}

	private static XYDataset createDatasetKontoart(Vector vec) {


	    /*
	    TimeSeries s1 = new TimeSeries("Geldkonto");
        TimeSeries s2 = new TimeSeries("Geldanlage");
        TimeSeries s3 = new TimeSeries("Sachanlage");
        TimeSeries s4 = new TimeSeries("Verbindlichkeit");
        */
        Map <String,TimeSeries >timeSeries = new HashMap<String, TimeSeries>();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        Hashtable hashinit = (Hashtable)vec.get(0);
        
        Set<String> setKeys = hashinit.keySet();
        
        for (String key : setKeys) {
           
            if (key.equals("datum"))
            {
                continue;
            }
            TimeSeries s = new TimeSeries(key);
            timeSeries.put(key, s);
            
        }
        
        
        for (int i=0; i<vec.size();i++)
        {
            try{
           // Hashtable hash = (Hashtable)vec.get(i);
            
            
            Set<String> setOfKeys = timeSeries.keySet();
        
            for (String key : setOfKeys) {
            
                TimeSeries s =  timeSeries.get(key);
                double value= (Double) ((Hashtable) vec.elementAt(i)).get(key);
                s.addOrUpdate(new Day(new SimpleDateFormat("yyyy-MM-dd").parse ((String)((Hashtable) vec.elementAt(i)).get("datum"))),
                        (Double) ((Hashtable) vec.elementAt(i)).get(key)); 
              
             
            }
            
        	
        
			} catch (Exception ex) {
				System.err.println("Exception " + ex);
			}

		}
        Set<String> setmyKeys = timeSeries.keySet();
        for (String key : setmyKeys) {
            
            dataset.addSeries(timeSeries.get(key)); 
        }
        /*
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		dataset.addSeries(s3);
		dataset.addSeries(s4);
		*/
		return dataset;
	}
	
	
	private static XYDataset createDatasetRendite(Vector vec) {


        /*
        TimeSeries s1 = new TimeSeries("Geldkonto");
        TimeSeries s2 = new TimeSeries("Geldanlage");
        TimeSeries s3 = new TimeSeries("Sachanlage");
        TimeSeries s4 = new TimeSeries("Verbindlichkeit");
        */
        Map <String,TimeSeries >timeSeries = new HashMap<String, TimeSeries>();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        Hashtable hashinit = (Hashtable)vec.get(0);
        
        Set<String> setKeys = hashinit.keySet();
        
        for (String key : setKeys) {
           
            if (key.equals("datum"))
            {
                continue;
            }
            TimeSeries s = new TimeSeries(key);
            timeSeries.put(key, s);
            
        }
        
        
        for (int i=0; i<vec.size();i++)
        {
            try{
           // Hashtable hash = (Hashtable)vec.get(i);
            
            
            Set<String> setOfKeys = timeSeries.keySet();
        
            for (String key : setOfKeys) {
            
                TimeSeries s =  timeSeries.get(key);
                double value= (Double) ((Hashtable) vec.elementAt(i)).get(key);
                s.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
                        (Double) ((Hashtable) vec.elementAt(i)).get(key)); 
              
             
            }
            
            
        
            } catch (Exception ex) {
                System.err.println("Exception " + ex);
            }

        }
        Set<String> setmyKeys = timeSeries.keySet();
        for (String key : setmyKeys) {
            
            dataset.addSeries(timeSeries.get(key)); 
        }
        /*
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);
        dataset.addSeries(s4);
        */
        return dataset;
    }
	
	private static TimeTableXYDataset createDatasetKontoart1(Vector vec) {


        /*
        TimeSeries s1 = new TimeSeries("Geldkonto");
        TimeSeries s2 = new TimeSeries("Geldanlage");
        TimeSeries s3 = new TimeSeries("Sachanlage");
        TimeSeries s4 = new TimeSeries("Verbindlichkeit");
        */
        Map <String,TimeSeries >timeSeries = new HashMap<String, TimeSeries>();
        TimeTableXYDataset dataset = new TimeTableXYDataset();
        Hashtable hashinit = (Hashtable)vec.get(0);
        
        Set<String> setKeys = hashinit.keySet();
        
        for (String key : setKeys) {
           
            if (key.equals("datum"))
            {
                continue;
            }
            TimeSeries s = new TimeSeries(key);
            timeSeries.put(key, s);
            
        }
        
        
        for (int i=0; i<vec.size();i++)
        {
            try{
           // Hashtable hash = (Hashtable)vec.get(i);
            
            
            Set<String> setOfKeys = timeSeries.keySet();
        
            for (String key : setOfKeys) {
            
                TimeSeries s =  timeSeries.get(key);
                TimePeriod p = new Day();
                double value= (Double) ((Hashtable) vec.elementAt(i)).get(key);
                s.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
                        (Double) ((Hashtable) vec.elementAt(i)).get(key)); 
                
                dataset.add(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
                        (Double) ((Hashtable) vec.elementAt(i)).get(key),
                        key);
                
            }
            
            
        
            } catch (Exception ex) {
                System.err.println("Exception " + ex);
            }

        }
        /*
        Set<String> setmyKeys = timeSeries.keySet();
        for (String key : setmyKeys) {
            
            dataset.add(timeSeries.get(key)); 
        }*/
       
        /*
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);
        dataset.addSeries(s4);
        */
        return dataset;
    }
/*
	private static XYDataset createDatasetKontoart(Vector vec) {

        TimeSeries s1 = new TimeSeries("Geldkonto");
        TimeSeries s2 = new TimeSeries("Geldanlage");
        TimeSeries s3 = new TimeSeries("Sachanlage");
        TimeSeries s4 = new TimeSeries("Verbindlichkeit");
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i=0; i<vec.size();i++)
        {
            
            try{
            //System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("wert"));
                s1.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
                        (Double) ((Hashtable) vec.elementAt(i)).get("Geldkonto"));
                s2.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
                        (Double) ((Hashtable) vec.elementAt(i)).get("Geldanlage"));
                s3.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
                        (Double) ((Hashtable) vec.elementAt(i)).get("Sachanlage"));
                s4.addOrUpdate(new Day((Date) ((Hashtable) vec.elementAt(i)).get("datum")),
                        (Double) ((Hashtable) vec.elementAt(i)).get("Verbindlichkeit"));

                // s2.addOrUpdate(new
                // Day((Date)((Hashtable)vec.elementAt(i)).get("datum")),(Double)
                // ((Hashtable)vec.elementAt(i)).get("wert")-100.0);
                // System.out.println("Wert = "+(Double)
                // ((Hashtable)vec.elementAt(i)).get("Sachanlage")+" Datum " +
                // ((Hashtable)vec.elementAt(i)).get("datum"));
                // System.err.println("fertig Eintrag "+i);
            } catch (Exception ex) {
                System.err.println("Exception " + ex);
            }

        }
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);
        dataset.addSeries(s4);
        return dataset;
    }
*/
	private static JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(

				"Kontoverlauf", // title
				"Datum", // x-axis label
				"Wert", // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
		);
		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		// plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		Calendar calendar = Calendar.getInstance();
		double millis = calendar.getTimeInMillis();
		final Marker today = new ValueMarker(millis);
		today.setPaint(Color.blue);
		today.setLabel("Heute");
		plot.addDomainMarker(today);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			// renderer.setShapesVisible(true);
			// renderer.setShapesFilled(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

		return chart;

	}
	 
	 private static JFreeChart createPlanChart(XYDataset dataset, String kategorieName, double aktValue, double initialValue) {
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
		            "Planungsverlauf " + kategorieName,  // title
		            "Datum",             // x-axis label
		            "Wert",   // y-axis label
		            dataset,            // data
		            true,               // create legend?
		            true,               // generate tooltips?
		            false               // generate URLs?
		        );
			// XYItemRenderer render = new XYAreaRenderer();
			  chart.setBackgroundPaint(Color.white);

		      XYPlot plot = (XYPlot) chart.getPlot();
		      
		      plot.setDataset(0, dataset);
		     
		      plot.setBackgroundPaint(Color.lightGray);
		      plot.setDomainGridlinePaint(Color.white);
		      plot.setRangeGridlinePaint(Color.white);
		      //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		      plot.setDomainCrosshairVisible(true);
		      plot.setRangeCrosshairVisible(true);
		      
		      plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.blue);
		      plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(1, Color.green);
		      XYItemRenderer r = plot.getRenderer();
		      if (r instanceof XYLineAndShapeRenderer) {
		          XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		          //renderer.setShapesVisible(true);
		          //renderer.setShapesFilled(true);
		      }
		      
		      //
		      // Marker
		      //
		      Calendar calendar = Calendar.getInstance();
		      double millis = calendar.getTimeInMillis();
		        final Marker today = new ValueMarker(millis);
		        today.setPaint(Color.blue);
		        today.setLabel("Heute");
		        plot.addDomainMarker(today);
		        
		        final Marker start = new ValueMarker(aktValue);
		        start.setPaint(Color.blue);
		        start.setLabel("Aktueller Wert "+myformat(aktValue));
		        //start.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		        //start.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		        plot.addRangeMarker(start);
		        
		        final Marker initial = new ValueMarker(initialValue);
		        initial.setPaint(Color.green);
		        initial.setLabel("Initial Wert "+myformat(initialValue));
		        //start.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		        //start.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		        plot.addRangeMarker(initial);
		        
		        
		        final Marker hundert = new ValueMarker(100.0);
		        hundert.setPaint(Color.red);
               // start.setLabel("Aktueller Wert "+myformat(hundert));
                //start.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
                //start.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                plot.addRangeMarker(hundert);
		       //
		       // Marker end
		       //
		       // currentEnd.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		        //currentEnd.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		      DateAxis axis = (DateAxis) plot.getDomainAxis();
		      axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		      
		      return chart;

		  }
	 
	 private static JFreeChart stackedArea ( CategoryDataset dataset)
	         {
	     JFreeChart chart = ChartFactory.createStackedAreaChart(
	             
                 "Anlageverlauf",  // title
                 "Datum",             // x-axis label
                 "Wert",   // y-axis label
                 dataset,            // data
                 PlotOrientation.VERTICAL,
                 true,               // create legend?
                 true,               // generate tooltips?
                 false               // generate URLs?
             );
         // XYItemRenderer render = new XYAreaRenderer();
         
           chart.setBackgroundPaint(Color.white);

           XYPlot plot = (XYPlot) chart.getPlot();
           XYItemRenderer render = plot.getRenderer();
           plot.setRenderer(render);
           plot.setBackgroundPaint(Color.lightGray);
           plot.setDomainGridlinePaint(Color.white);
           plot.setRangeGridlinePaint(Color.white);
           //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
           plot.setDomainCrosshairVisible(true);
           plot.setRangeCrosshairVisible(true);
           Calendar calendar = Calendar.getInstance();
           double millis = calendar.getTimeInMillis();
             final Marker today = new ValueMarker(millis);
             today.setPaint(Color.blue);
             today.setLabel("Heute");
             plot.addDomainMarker(today);
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
	      
	 
	 private JFreeChart createAreaChart(final TimeTableXYDataset dataset) {
	        final JFreeChart chart = ChartFactory.createStackedXYAreaChart(
	                "Live Sentiment Chart", "Time", "Sentiments", dataset, PlotOrientation.VERTICAL, true, true, false);

	        final StackedXYAreaRenderer render = new StackedXYAreaRenderer();
	        render.setSeriesPaint(0, Color.RED);
	        render.setSeriesPaint(1, Color.GREEN);

	        DateAxis domainAxis = new DateAxis();
	        domainAxis.setAutoRange(true);
	        domainAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
	       

	        XYPlot plot = (XYPlot) chart.getPlot();
	        plot.setRenderer(render);
	        plot.setDomainAxis(domainAxis);
	        plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);
	        plot.setForegroundAlpha(0.5f);

	       // NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	       // rangeAxis.setNumberFormatOverride(new DecimalFormat("#,###.#"));
	       // rangeAxis.setAutoRange(true);

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
			// XYItemRenderer render = new XYAreaRenderer();
			
			  chart.setBackgroundPaint(Color.white);

		      XYPlot plot = (XYPlot) chart.getPlot();
		      XYItemRenderer render = plot.getRenderer();
		     // int seriesCount = plot.getSeriesCount();
		    //  plot.getRenderer().setSeriesStroke(3, new BasicStroke(4));
		      plot.setRenderer(render);
		      plot.setBackgroundPaint(Color.lightGray);
		      plot.setDomainGridlinePaint(Color.white);
		      plot.setRangeGridlinePaint(Color.white);
		      //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		      plot.setDomainCrosshairVisible(true);
		      plot.setRangeCrosshairVisible(true);
		      Calendar calendar = Calendar.getInstance();
		      double millis = calendar.getTimeInMillis();
		        final Marker today = new ValueMarker(millis);
		        today.setPaint(Color.blue);
		        today.setLabel("Heute");
		        plot.addDomainMarker(today);
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
	 
	 private static JFreeChart createChartRendite(XYDataset dataset) {
         JFreeChart chart = ChartFactory.createTimeSeriesChart(
    
                 "Rendite",  // title
                 "Datum",             // x-axis label
                 "Wert",   // y-axis label
                 dataset,            // data
                 true,               // create legend?
                 true,               // generate tooltips?
                 false               // generate URLs?
             );
         // XYItemRenderer render = new XYAreaRenderer();
         
           chart.setBackgroundPaint(Color.white);

           XYPlot plot = (XYPlot) chart.getPlot();
           XYItemRenderer render = plot.getRenderer();
           //int seriesCount = plot.getSeriesCount();
           //plot.getRenderer().setSeriesStroke(3, new BasicStroke(4));
           plot.setRenderer(render);
           plot.setBackgroundPaint(Color.lightGray);
           plot.setDomainGridlinePaint(Color.white);
           plot.setRangeGridlinePaint(Color.white);
           //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
           plot.setDomainCrosshairVisible(true);
           plot.setRangeCrosshairVisible(true);
           Calendar calendar = Calendar.getInstance();
           double millis = calendar.getTimeInMillis();
             final Marker today = new ValueMarker(millis);
             today.setPaint(Color.blue);
             today.setLabel("Heute");
             plot.addDomainMarker(today);
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
	 
	 private Double getAktValue(Vector chartVec)
	 {
		 Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 for (int i =0; i < chartVec.size(); i++)
		 {
			 try {
					
					Day myDay = new Day((Date) ((Hashtable) chartVec.elementAt(i)).get("datum"));
					Day today = new Day(cal.getTime());
					if (myDay.equals(today)){
						return (Double) ((Hashtable) chartVec.elementAt(i)).get("wert");
					}
		 }
			 catch (Exception ex) {
					System.err.println("Exception " + ex);
					return 0.0;
				}
	
		 }
		 return 0.0;
	 }
	 
	 
	 private Double getinitialValue(Vector chartVec)
	 {
		 Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 for (int i =0; i < chartVec.size(); i++)
		 {
			 try {
				
					Day myDay = new Day((Date) ((Hashtable) chartVec.elementAt(i)).get("datum"));
					Day today = new Day(cal.getTime());
					if (myDay.equals(today)){
					    if (((Hashtable) chartVec.elementAt(i)).get("initial")==null)
					    {
					        return 0.0;
					    }
					    else
					    {
						return (Double) ((Hashtable) chartVec.elementAt(i)).get("initial");
					    }
					}
		 }
			 catch (Exception ex) {
					System.err.println("Exception " + ex);
					return 0.0;
				}
	
		 }
		 return 0.0;
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
	 private static String  myformat(double i)
	 {
	 	DecimalFormat f = new DecimalFormat("#0.00");
	 	double toFormat = ((double)Math.round(i*100))/100;
	 	return f.format(toFormat);
	 }
}
