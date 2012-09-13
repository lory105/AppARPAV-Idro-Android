package it.arpav.mobile.apparpav.main;

import java.text.DecimalFormat;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint.Align;
import android.util.Log;

import it.arpav.mobile.apparpav.utils.Global;


/**
 * Class for the graphs
 * @author Giacom Lorigiola
 */
public class Graph {
	Context context= null; 

	private String type = null;		// specifics the type of data: LIVIDRO or PREC
	private String title = null;	// title of graph
	private String legend = null;	// legend of graph
	private String stationName = null;	// the name of the station
	private String[] time = null;	// time data
	private String[] date = null;	// date
	private String unitMeasurement = null;
	private double[] value = null;	// value data
	
	
	/**
	 * create a graph
	 */
	public Graph(Context context){
		this.context=context;
	}
	
	/**
	 * return a graph activity intent 
	 */
	public Intent getIntent(Context context){
		this.context=context;
		
		// set the title of the graph
		if(date!=null)
			title=( stationName + "\ndati dal "+ date[0]+ " al " + date[date.length-1]);
		
		// set the max and min value of the values to print in the chart
		//double maxValue= Double.MIN_VALUE;
		double maxValue= -100000;
		double minValue= Double.MAX_VALUE;
		
		CategorySeries series = new CategorySeries(legend);
		
		if( value!=null)
			for( int i=0; i < value.length; i++){
				series.add("Bar " + (i+1), value[i]);
				Log.d(Integer.toString(i), Double.toString(value[i]));
				if(value[i]>maxValue)
					maxValue=value[i];
				if(value[i]<minValue)
					minValue=value[i];
			}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		
		
		// LIVIDRO chart
		if( type.equals(Global.KEY_LIVIDRO) ){
			renderer.setColor(context.getResources().getColor(R.color.graphLividro));
			mRenderer.setXAxisMax(30);
			double max = maxValue + (maxValue-minValue)*7;
			double min = minValue - (maxValue-minValue)*7;
			
			mRenderer.setYAxisMax( max );
			mRenderer.setYAxisMin( min );
		}
		// PREC chart
		else{
			renderer.setColor(context.getResources().getColor(R.color.graphPluvio));		
			mRenderer.setXAxisMax(27);

			//double max = maxValue + (maxValue-minValue)*5;
			double max = maxValue + (maxValue-minValue)*2;
			
			if(max <1)
				max=4;
			mRenderer.setYAxisMax((int)max);
			mRenderer.setYAxisMin(-0.2);
		}

		
	    if(time!=null){
	    	boolean addTime=true;
	    
	    	for (int i = 0; i < time.length; i++) { 
	    		if(i==0){
	    			mRenderer.addXTextLabel(i+1, time[i]+"\n"+ date[i].substring(0, 5) );
	    			addTime=false;
	    		}
	    		else if( time[i].equals("00:00") ||  i==time.length-1 ){
	    			mRenderer.addXTextLabel(i+1, time[i]+"\n"+ date[i].substring(0, 5) );
	    			mRenderer.addXTextLabel(i, "" );
	    			addTime=false;
	    		}
	    		else if( addTime){
	    			mRenderer.addXTextLabel(i+1, time[i]);
	    			addTime=false;
	    		}
	    		else{
	    			mRenderer.addXTextLabel(i+1, "");
	    			addTime=true;
	    		}
	    		
	    	}
	    }
		
		// customization for the bar
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesSpacing( 4);
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
	    mRenderer.setShowGridX(true);
	    mRenderer.setXLabelsAlign(Align.CENTER);
	    mRenderer.setXLabels(0);
//	    mRenderer.setLegendHeight(120);
	    mRenderer.setYLabelsAlign(Align.RIGHT);
		
		// set color
		mRenderer.setBackgroundColor(context.getResources().getColor(R.color.graphBackground));
	    mRenderer.setMarginsColor( context.getResources().getColor(R.color.graphBackground));
		mRenderer.setGridColor(context.getResources().getColor(R.color.greySoft));
		mRenderer.setAxesColor(context.getResources().getColor(R.color.black));
		mRenderer.setLabelsColor(context.getResources().getColor(R.color.black));
		mRenderer.setXLabelsColor(context.getResources().getColor(R.color.grey));
		mRenderer.setYLabelsColor(0, context.getResources().getColor(R.color.grey));
	    
		
		// detection of screen size (small, normal, large, xlarge)
		// if screen size != small
		if ((context.getResources().getConfiguration().screenLayout & 
				Configuration.SCREENLAYOUT_SIZE_MASK) != Configuration.SCREENLAYOUT_SIZE_SMALL) {
			
			Log.d("if", "normal");
			
			// text size
			mRenderer.setChartTitleTextSize(21);
			mRenderer.setAxisTitleTextSize(18);
			mRenderer.setLabelsTextSize(12);
			renderer.setChartValuesTextSize(15); // size of value displayed with setDisplayChartValues(true);
			mRenderer.setLegendTextSize(19);
			mRenderer.setPointSize(2f);
			
		}
		else{
			Log.d("screen", "small");
			
			mRenderer.setXAxisMax(17);
			// text size
			mRenderer.setChartTitleTextSize(11);
			mRenderer.setAxisTitleTextSize(10);
			mRenderer.setLabelsTextSize(8);
			//renderer.setChartValuesTextSize(12); // size of value displayed with setDisplayChartValues(true);
			//mRenderer.setLegendTextSize(12);
			mRenderer.setPointSize(2f);
			//mRenderer.setPanEnabled(true, false); // lock and unlock the x y axis movement
		}
	    
		
		Intent intent = ChartFactory.getLineChartIntent( context, dataset, mRenderer);
		return intent;		
	}
	
	/**
	 * set the value for the graph
	 */
	public void setValue( double[] value){
		this.value=value;
	}
	
	/**
	 * set the time for each values
	 */
	public void setTime(String[] time){
		this.time=time;
	}

	
	/**
	 * set the station name
	 */
	public void setStationName(String stationName){
		this.stationName=stationName;
	}
	
	/**
	 * set the date
	 */
	public void setDate(String[] date){
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
