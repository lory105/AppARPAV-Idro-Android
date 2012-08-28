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

public class Graph {

	public Intent getIntent(Context context){
		
		//double[] x = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		int[] y = { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 };
		
		CategorySeries series = new CategorySeries("Demo Bar Graph");
		for( int i=0; i < y.length; i++){
			series.add("Bar " + (i+1), y[i]);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		

		// customization for the bar
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesSpacing((float) 0.5);
		//renderer.setColor(Color.WHITE);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		

		
		
		
		// customization for the graph
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setChartTitle("Mio Titolo");
		mRenderer.setXTitle("tempo");
		mRenderer.setYTitle("metri");
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);

		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
	    mRenderer.setMarginsColor(Color.BLACK);

		//mRenderer.
		mRenderer.setShowGrid(true);

	    mRenderer.setAxisTitleTextSize(14);
	    mRenderer.setChartTitleTextSize(14);
	    mRenderer.setLabelsTextSize(10);
	    mRenderer.setLegendTextSize(14);
	    mRenderer.setPointSize(5f);

		
		//Intent intent = ChartFactory.getBarChartIntent( context, dataset, mRenderer, Type.DEFAULT);
		Intent intent = ChartFactory.getLineChartIntent( context, dataset, mRenderer);
		
		return intent;
		
	}
	
}
