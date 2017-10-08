package com.qut.routeOptimizerApplication.service;

import java.util.ArrayList;
import java.util.List;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.shapes.GHPoint;
import com.qut.routeOptimizerApplication.Bean.Address;
import com.qut.routeOptimizerApplication.Bean.AddressListBean;
import com.qut.routeOptimizerApplication.Bean.ResultsBean;
import com.qut.routeOptimizerApplication.properties.RouteOptimzerProperties;

public class DistanceService {
	public ResultsBean getDistance(Address source, Address destination) {
		GHPoint ghPointSource, ghPointDestination;
		GHResponse ghResponse = new GHResponse();
		ResultsBean resultbean = new ResultsBean();
		RouteOptimzerProperties routeOptimzerProperties = new RouteOptimzerProperties();
		ghPointSource = new GHPoint();
		ghPointSource.lat = Double.parseDouble(source.getLatitude());
		ghPointSource.lon = Double.parseDouble(source.getLongitude());
		resultbean.setSource(ghPointSource);
		ghPointDestination = new GHPoint();
		ghPointDestination.lat = Double.parseDouble(destination.getLatitude());
		ghPointDestination.lon = Double.parseDouble(destination.getLongitude());
		resultbean.setDestination(ghPointDestination);
		if (source == destination) {
			resultbean.setDistance(0);
			resultbean.setTime(0);
		} 
		else {
			GraphHopper graphHopper = new GraphHopper().setGraphHopperLocation(routeOptimzerProperties.hopperDirectory)
					.setEncodingManager(new EncodingManager("car")).setOSMFile(routeOptimzerProperties.osmFilePath);
			graphHopper.importOrLoad();
			GHRequest request = new GHRequest(ghPointSource, ghPointDestination);
			request.putHint("calcPoints", false);
			request.putHint("instructions", true);
			request.setVehicle("car");
			ghResponse = graphHopper.route(request);
			resultbean.setDistance(ghResponse.getDistance());
			resultbean.setTime(ghResponse.getMillis());
		}
		return resultbean;
	}

	public List<ResultsBean> calculateDistanceMatrix(AddressListBean uploadInvoiceBean) {
		int locationLength = uploadInvoiceBean.getLocationList().size();
		List<ResultsBean> resultsBeanList=new ArrayList<ResultsBean>();
		ResultsBean resultBean;
		for (int i = 0; i < locationLength; i++) {
			for (int j = 0; j < locationLength; j++) {
				resultBean =new ResultsBean();
				resultBean=getDistance( uploadInvoiceBean.getLocationList().get(i), uploadInvoiceBean.getLocationList().get(j));
				resultsBeanList.add(resultBean);
			}
		}
		
		return resultsBeanList;
	}
}