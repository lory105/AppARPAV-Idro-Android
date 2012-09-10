package it.arpav.mobile.apparpav.main;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint.Align;

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
	private String[] time = null;	// time data
	private String unitMeasurement = null;
	private float[] value = null;	// value data
	
	
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
		
		CategorySeries series = new CategorySeries(legend);
		
		if( value!=null)
			for( int i=0; i < value.length; i++){
				series.add("Bar " + (i+1), value[i]);
			}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		
		// customization for the bar
		renderer.setDisplayChartValues(true);
		//renderer.setChartValuesSpacing((int) 1);
		
		if( type.equals(Global.KEY_LIVIDRO) ){
			renderer.setColor(context.getResources().getColor(R.color.graphLividro));
			mRenderer.setXAxisMax(30);
			mRenderer.setYAxisMax(10);
		}
		else{
			renderer.setColor(context.getResources().getColor(R.color.graphPluvio));
			mRenderer.setXAxisMax(27);
			mRenderer.setYAxisMax(25);
		}

		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		
		// customization for the graph
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setChartTitle(title);
		mRenderer.setXTitle("ora");
		mRenderer.setYTitle(unitMeasurement);
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(context.getResources().getColor(R.color.graphBackground));
	    mRenderer.setMarginsColor( context.getResources().getColor(R.color.graphBackground));
	    mRenderer.setShowGrid(true);
	    mRenderer.setShowGridX(true);
	    mRenderer.setAxisTitleTextSize(15);
	    mRenderer.setChartTitleTextSize(17);
	    mRenderer.setLabelsTextSize(11);
	    mRenderer.setLegendTextSize(17);
	    mRenderer.setPointSize(3f);
	    //mRenderer.setPanEnabled(true, false); // lock and unlock the x y axis movement
		//mRenderer.initAxesRange( 2);


	     if(time!=null)
	    	for (int i = 0; i < time.length; i++) { 
	    		mRenderer.addXTextLabel(i+1, time[i]);
	    	}
	    
	    //mRenderer.setYAxisAlign(Align.CENTER, 2);
	    mRenderer.setXLabelsAlign(Align.CENTER);
	    mRenderer.setXLabels(0);

		Intent intent = ChartFactory.getLineChartIntent( context, dataset, mRenderer);
		
		return intent;		
	}
	
	/**
	 * set the value for the graph
	 */
	public void setValue( float[] value){
		this.value=value;
	}
	
	/**
	 * set the time for each values
	 */
	public void setTime(String[] time){
		this.time=time;
	}

	/**
	 * set the time title of the graph
	 */
	public void setTitle(String title){
		this.title=title;
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
