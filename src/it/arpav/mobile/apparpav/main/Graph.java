package it.arpav.mobile.apparpav.main;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint.Align;

import it.arpav.mobile.apparpav.utils.Global;


/**
 * Class for the chart
 * @author Giacom Lorigiola
 */
public class Graph {
	Context context= null; 
	private String type = null;			// specifics the type of data: LIVIDRO or PREC
	private String title = null;		// title of graph
	private String legend = null;		// legend of graph
	private String stationName = null;	// the name of the station
	private Date[] date = null;			// date
	private String unitMeasurement = null;
	private double[] value = null;		// value data
	
	
	/**
	 * create a graph
	 */
	public Graph(Context context){
		this.context=context;
	}
	
	
	/**
	 * return a graph activity intent 
	 */
	public Intent execute(Context context){
		this.context=context;
		
		// set the title of the graph
		if(date!=null)
			title= (stationName + "\ndati dal "+ date[0].getDate()+"/"+ date[0].getMonth()+ " al " + date[date.length-1].getDate() +"/"+ date[date.length-1].getMonth());
		
		// set the max and min value of the values to print in the chart
		//double maxValue= Double.MIN_VALUE;
		double maxValue= -100000;
		double minValue= Double.MAX_VALUE;
		
		CategorySeries series = new CategorySeries(legend);
		
		if( value!=null)
			for( int i=0; i < value.length; i++){
				series.add("Bar " + (i+1), value[i]);
				if(value[i]>maxValue)
					maxValue=value[i];
				if(value[i]<minValue)
					minValue=value[i];
			}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
				
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		
		setSetting(mRenderer, renderer, maxValue, minValue);
		setLabels(mRenderer);

		// set color
	    setGraphColor( mRenderer);
	    // set size
	    setSize(mRenderer, renderer);
		

		Intent intent = ChartFactory.getLineChartIntent( context, dataset, mRenderer);
	    
		return intent;		
	}
	
	
	private void setSetting(XYMultipleSeriesRenderer mRenderer, XYSeriesRenderer renderer, double maxValue, double minValue){
		// customization for the line
		renderer.setDisplayChartValues(true);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		
		
		// customization for the graph
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setChartTitle(title);
		mRenderer.setXTitle("ora");
		mRenderer.setYTitle(unitMeasurement);
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setApplyBackgroundColor(true);
	    mRenderer.setShowGrid(true);
	    mRenderer.setXLabelsAlign(Align.CENTER);
	    mRenderer.setYLabelsAlign(Align.RIGHT);
	    mRenderer.setXLabels(0);
		
		// LIVIDRO (idro) chart
		if( type.equals(Global.KEY_LIVIDRO) ){
			renderer.setColor(Color.RED);
			
			double max = maxValue + (maxValue-minValue)*7;
			double min = minValue - (maxValue-minValue)*7;
			
			mRenderer.setYAxisMax( max );
			mRenderer.setYAxisMin( min );
		}
		// PREC (pluvio) chart
		else{
			renderer.setColor(Color.CYAN);
			
			double max = maxValue + (maxValue-minValue)*2;
			
			if(max <1)
				max=4;
			mRenderer.setYAxisMax((int)max);
			mRenderer.setYAxisMin(-0.2);
		}
	}
	
	
	private void setLabels(XYMultipleSeriesRenderer mRenderer){
		// LIVIDRO (idro) chart
	    if(date!=null){
	    	
	    	if( type.equals(Global.KEY_LIVIDRO) ){
		    	for (int i = 0; i < date.length; i++) { 
		    		// jump if the hour has values "30" as minutes 
		    		if( Integer.toString( date[i].getMinutes()).equals("30")  ){
		    			mRenderer.addXTextLabel(i+1, "");
		    		}
		    		// print the first Y value of chart with the date
		    		else if(i==0 || i==1 ){
		    			mRenderer.addXTextLabel(i+1, date[i].getHours()+"\n"+ date[i].getDate()+"/"+ date[i].getMonth());
		    			i++;
		    			mRenderer.addXTextLabel(i+1, "");
		    		}
		    		// print the hour of midnight with the date
		    		else if( Integer.toString( date[i].getHours()).equals("0") ){
		    			mRenderer.addXTextLabel(i+1, "00" +"\n"+ date[i].getDate()+"/"+ date[i].getMonth());
		    			i++;
		    			mRenderer.addXTextLabel(i+1, "");
		    		}
		    		// print all the hour 3, 6, 9, 12, 15, 18, 21, 00
		    		else if( Integer.toString( date[i].getHours()).equals("3") ||  Integer.toString( date[i].getHours()).equals("6") || Integer.toString( date[i].getHours()).equals("9") || Integer.toString( date[i].getHours()).equals("12") || Integer.toString( date[i].getHours()).equals("15")|| Integer.toString( date[i].getHours()).equals("18") || Integer.toString( date[i].getHours()).equals("21")){
		    			mRenderer.addXTextLabel(i+1, Integer.toString(date[i].getHours()) );
		    			i++;
		    			mRenderer.addXTextLabel(i+1, "");
		    		}
		    		else
		    			mRenderer.addXTextLabel(i+1, "");
		    		
		    	}
	    	}
	    	else{
		    	for (int i = 0; i < date.length; i++) { 
		    		// print the first Y value of chart with the date
		    		if(i==0 ){
		    			mRenderer.addXTextLabel(i+1, date[i].getHours()+"\n"+ date[i].getDate()+"/"+ date[i].getMonth());
		    			i++;
		    			mRenderer.addXTextLabel(i+1, "");
		    		}
		    		// print the last Y value of chart or the midnight Y value
		    		else if( Integer.toString( date[i].getHours()).equals("0")  ){
		    			mRenderer.addXTextLabel(i+1, "00" + "\n"+ date[i].getDate()+"/"+ date[i].getMonth());
		    			i++;
		    			mRenderer.addXTextLabel(i+1, "");
		    		}
		    		// print all the hour 3, 6, 9, 12, 15, 18, 21, 00
		    		else if( Integer.toString( date[i].getHours()).equals("3") ||  Integer.toString( date[i].getHours()).equals("6") || Integer.toString( date[i].getHours()).equals("9") || Integer.toString( date[i].getHours()).equals("12") || Integer.toString( date[i].getHours()).equals("15")|| Integer.toString( date[i].getHours()).equals("18") || Integer.toString( date[i].getHours()).equals("21")){
		    			mRenderer.addXTextLabel(i+1, Integer.toString(date[i].getHours()) );
		    			i++;
		    			mRenderer.addXTextLabel(i+1, "");
		    		}
		    		else
		    			mRenderer.addXTextLabel(i+1, "");
		    		
		    	}
		    }
		}
			
	}
	
	/**
	 * set the sizes for the graph
	 */
	private void setSize(XYMultipleSeriesRenderer mRenderer,XYSeriesRenderer renderer ){
		// detection of screen size (small, normal, large, xlarge)
		// if screen size != small
		if ((context.getResources().getConfiguration().screenLayout & 
				Configuration.SCREENLAYOUT_SIZE_MASK) != Configuration.SCREENLAYOUT_SIZE_SMALL) {
			
			mRenderer.setMargins(new int[] { 20, 37, 20, 5 });
			mRenderer.setLegendHeight(120);
			// text size
			mRenderer.setChartTitleTextSize(21);
			mRenderer.setAxisTitleTextSize(19);
			mRenderer.setLabelsTextSize(16);
			renderer.setChartValuesTextSize(18); // size of value displayed with setDisplayChartValues(true);
			mRenderer.setLegendTextSize(20);
			mRenderer.setPointSize(2f);
			
		}
		else{
			mRenderer.setMargins(new int[] { 20, 25, 20, 5 });
			mRenderer.setLegendHeight(90);
			//mRenderer.setXAxisMax(17);
			// text size
			mRenderer.setChartTitleTextSize(11);
			mRenderer.setAxisTitleTextSize(10);
			mRenderer.setLabelsTextSize(8);
			//renderer.setChartValuesTextSize(12); // size of value displayed with setDisplayChartValues(true);
			//mRenderer.setLegendTextSize(12);
			mRenderer.setPointSize(2f);
		}
	}
	
	
	private void setGraphColor( XYMultipleSeriesRenderer mRenderer ){
		    mRenderer.setBackgroundColor(Color.BLACK);
		    mRenderer.setMarginsColor( Color.BLACK);
			mRenderer.setAxesColor(Color.LTGRAY);
			mRenderer.setLabelsColor(Color.WHITE);
	}
	
	
	/**
	 * set the value for the graph
	 */
	public void setValue( double[] value){
		this.value=value;
	}
	
	
	/**
	 * set the station name
	 */
	public void setStationName(String stationName){
		this.stationName=stationName;
		setTitle();
	}
	
	
	/**
	 * set the title of chart
	 */
	public void setTitle(){
		title= (stationName + "\ndati dal "+ date[0].getDate()+"/"+ date[0].getMonth()+ " al " + date[date.length-1].getDate() +"/"+ date[date.length-1].getMonth());
	}	
	
	
	/**
	 * set the date
	 */
	public void setDate(Date[] date){
		this.date=date;
	}
	
	
	public void setUnitMeasurement(String unitMeasurement){
		if(unitMeasurement.equals(Global.KEY_METER))
			this.unitMeasurement=Global.KEY_METER_WORD;
		else
			this.unitMeasurement=Global.KEY_MILLIMETER_WORD;
	}

	
	public void setType(String type){
		this.type=type;
		if(type.equals( Global.KEY_LIVIDRO) )
			legend=context.getString(R.string.lividroLegend);
		else
			legend=context.getString(R.string.precLegend);
	}
		
}
