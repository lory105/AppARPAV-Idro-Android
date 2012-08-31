package it.arpav.mobile.apparpav.main;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

import it.arpav.mobile.apparpav.utils.Global;

public class Graph {
	Context context= null; 

	private String type = null;		// specifics the type of data: LIVIDRO or PREC
	private String title = null;	// title of graph
	private String legend = null;	// legend of graph
	
	private String[] time = null;	// time data
	private float[] value = null;	// value data
	
	
	public Graph(Context context){
		this.context=context;
	}
	
	public Intent getIntent(Context context){
		this.context=context;
		
		
		CategorySeries series = new CategorySeries(legend);
		
		if( value!=null)
			for( int i=0; i < value.length; i++){
				series.add("Bar " + (i+1), value[i]);
			}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		

		// customization for the bar
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesSpacing((float) 0.5);
		
		if( type.equals(Global.KEY_LIVIDRO) )
			renderer.setColor(Color.RED);
		else
			renderer.setColor(Color.BLUE);

		
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		

		// customization for the graph
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setChartTitle(title);
		mRenderer.setXTitle("orario");
		mRenderer.setYTitle("metri");
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);

		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
	    mRenderer.setMarginsColor(Color.BLACK);

		//mRenderer.
		mRenderer.setShowGrid(true);

	    mRenderer.setAxisTitleTextSize(14);
	    mRenderer.setChartTitleTextSize(17);
	    mRenderer.setLabelsTextSize(10);
	    mRenderer.setLegendTextSize(14);
	    mRenderer.setPointSize(5f);


	     if(time!=null)
	    	for (int i = 0; i < time.length; i++) { 
	    		mRenderer.addXTextLabel(i+1, time[i]);
	    	}
	    
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

	public void setTitle(String title){
		this.title=title;
		
	}

	public void setType(String type){
		this.type=type;
		if(type.equals( Global.KEY_LIVIDRO) )
			legend=context.getString(R.string.lividroLegend);
		else
			legend=context.getString(R.string.precLegend);
	}
	
	
}
